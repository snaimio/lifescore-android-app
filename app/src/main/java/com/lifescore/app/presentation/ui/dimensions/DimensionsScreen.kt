package com.lifescore.app.presentation.ui.dimensions

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.EmptyState
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.core.designsystem.components.TaskItem
import com.lifescore.app.core.util.ScoreEngine
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.LifeTask
import com.lifescore.app.presentation.navigation.Screen
import com.lifescore.app.presentation.ui.home.components.DimensionRadarChart
import com.lifescore.app.presentation.ui.home.components.SectionHeader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DimensionsUiState(
    val selectedDimension: DimensionType = DimensionType.HEALTH,
    val tasksForSelectedDimension: List<LifeTask> = emptyList(),
    val allTasks: List<LifeTask> = emptyList(),
    val dimensionScores: Map<DimensionType, Int> = emptyMap(),
    val isOverviewMode: Boolean = true
) {
    val allDimensionsZero: Boolean
        get() = dimensionScores.isEmpty()
}

class DimensionsViewModel(
    private val repository: LifeScoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DimensionsUiState())
    val uiState: StateFlow<DimensionsUiState> = _uiState.asStateFlow()

    init {
        loadAllData()
    }

    private fun loadAllData() {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
            repository.getAllTasks().collect { tasks ->
                val scores = DimensionType.values().associateWith { dim ->
                    val dimTasks = tasks.filter { it.dimension == dim }
                    val completed = dimTasks.count { it.isCompleted }
                    ScoreEngine.calculateDimensionScore(completed, dimTasks.size)
                }

                _uiState.value = _uiState.value.copy(
                    allTasks = tasks,
                    dimensionScores = scores,
                    tasksForSelectedDimension = tasks.filter { it.dimension == _uiState.value.selectedDimension }
                )
            }
        }
    }

    fun selectDimension(dimension: DimensionType) {
        _uiState.value = _uiState.value.copy(
            selectedDimension = dimension,
            isOverviewMode = false,
            tasksForSelectedDimension = _uiState.value.allTasks.filter { it.dimension == dimension }
        )
    }

    fun setOverviewMode(overview: Boolean) {
        _uiState.value = _uiState.value.copy(isOverviewMode = overview)
    }

    fun toggleTask(task: LifeTask) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
        }
    }

    fun addTask(title: String, points: Int) {
        viewModelScope.launch {
            repository.addTask(title, _uiState.value.selectedDimension, points)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DimensionsScreen(
    navController: NavController,
    viewModel: DimensionsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "360° Life Matrix",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "8-Dimension Balance Overview",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskDialog = true },
                containerColor = Color(uiState.selectedDimension.baseColorHex)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (uiState.allDimensionsZero) {
                // Friendly Zero-State for New Users
                LifeMatrixEmptyState(
                    onStartAssessment = {
                        navController.navigate(Screen.QuickAssessment.route)
                    },
                    onAddFirstHabit = {
                        showAddTaskDialog = true
                    }
                )
            } else {
                // Scrollable Dimension Tabs
                ScrollableTabRow(
                    selectedTabIndex = DimensionType.values().indexOf(uiState.selectedDimension),
                    edgePadding = Spacing.md,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    DimensionType.values().forEach { dim ->
                        val isSelected = uiState.selectedDimension == dim
                        val score = uiState.dimensionScores[dim] ?: 0
                        Tab(
                            selected = isSelected,
                            onClick = { viewModel.selectDimension(dim) },
                            text = {
                                Text(
                                    text = "${dim.displayName} (${score}%)",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected)
                                        Color(dim.baseColorHex)
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    contentPadding = PaddingValues(top = Spacing.sm, bottom = Spacing.xxl)
                ) {
                    // 360 Radar Chart Card
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(Spacing.md)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Real-Time Radar Matrix",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        val avgScore = if (uiState.dimensionScores.isNotEmpty())
                                            uiState.dimensionScores.values.average().toInt()
                                        else 0
                                        Text(
                                            text = "$avgScore% Balanced",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(Modifier.height(Spacing.sm))

                                DimensionRadarChart(
                                    dimensionScores = uiState.dimensionScores,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp)
                                )

                                Spacer(Modifier.height(Spacing.sm))

                                // Dimension Legend
                                DimensionLegend(dimensionScores = uiState.dimensionScores)
                            }
                        }
                    }

                    item {
                        // Dimension Header Card
                        GlassCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(Spacing.md)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = uiState.selectedDimension.displayName,
                                        fontWeight = FontWeight.Black,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color(uiState.selectedDimension.baseColorHex)
                                    )
                                    Text(
                                        text = "${uiState.dimensionScores[uiState.selectedDimension] ?: 0}% Score",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color(uiState.selectedDimension.baseColorHex)
                                    )
                                }
                                Spacer(Modifier.height(Spacing.xs))
                                Text(
                                    text = uiState.selectedDimension.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item {
                        SectionHeader("Active Habits in ${uiState.selectedDimension.displayName}")
                    }

                    if (uiState.tasksForSelectedDimension.isEmpty()) {
                        item {
                            EmptyState(
                                icon = "🌟",
                                title = "No habits created in ${uiState.selectedDimension.displayName}",
                                description = "Tap the + button below to create your first ${uiState.selectedDimension.displayName} habit and grow this score.",
                                actionButtonText = "Add Habit",
                                onActionClick = { showAddTaskDialog = true }
                            )
                        }
                    } else {
                        items(uiState.tasksForSelectedDimension, key = { it.id }) { task ->
                            TaskItem(
                                task = task,
                                onComplete = { viewModel.toggleTask(task) }
                            )
                        }
                    }
                }
            }
        }

        if (showAddTaskDialog) {
            AlertDialog(
                onDismissRequest = { showAddTaskDialog = false },
                title = { Text("Add ${uiState.selectedDimension.displayName} Habit") },
                text = {
                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        label = { Text("Habit name (e.g. 10m Meditation)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newTaskTitle.isNotBlank()) {
                                viewModel.addTask(newTaskTitle, 15)
                                newTaskTitle = ""
                                showAddTaskDialog = false
                            }
                        }
                    ) {
                        Text("Add Habit")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddTaskDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun LifeMatrixEmptyState(
    onStartAssessment: () -> Unit,
    onAddFirstHabit: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Spacer(Modifier.height(Spacing.lg))

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🌟", fontSize = 40.sp)
                }
            }

            Spacer(Modifier.height(Spacing.md))

            Text(
                text = "Welcome to Your Life Matrix",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(Spacing.xs))

            Text(
                text = "This 360° chart visualizes your balance across 8 life dimensions. Complete habits or take the assessment to fill in each area.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(Spacing.xl))

            // Primary CTA
            Button(
                onClick = onStartAssessment,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "🎯 Take Your First Assessment",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(Modifier.height(Spacing.sm))

            // Secondary CTA
            OutlinedButton(
                onClick = onAddFirstHabit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    "✅ Or Add Your First Habit",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(Modifier.height(Spacing.xl))

            Text(
                text = "Preview of what your chart will look like:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(Spacing.sm))

            // Faded Sample Chart
            FadedSampleChart()

            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

@Composable
fun FadedSampleChart() {
    val baselineScores = remember {
        DimensionType.values().associateWith { 0 }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .alpha(0.6f),
        contentAlignment = Alignment.Center
    ) {
        DimensionRadarChart(
            dimensionScores = baselineScores,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun DimensionLegend(dimensionScores: Map<DimensionType, Int>) {
    val dimensions = remember { DimensionType.values().toList() }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            dimensions.take(4).forEach { dim ->
                val score = dimensionScores[dim] ?: 50
                DimensionLegendItem(
                    dimension = dim,
                    score = score,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            dimensions.takeLast(4).forEach { dim ->
                val score = dimensionScores[dim] ?: 50
                DimensionLegendItem(
                    dimension = dim,
                    score = score,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DimensionLegendItem(
    dimension: DimensionType,
    score: Int,
    modifier: Modifier = Modifier
) {
    val cleanName = when (dimension) {
        DimensionType.HEALTH -> "Health"
        DimensionType.WEALTH -> "Wealth"
        DimensionType.RELATIONSHIPS -> "Relations"
        DimensionType.CAREER -> "Career"
        DimensionType.LEARNING -> "Learning"
        DimensionType.FITNESS -> "Fitness"
        DimensionType.MENTAL_HEALTH -> "Mind"
        DimensionType.SOCIAL_LIFE -> "Social"
    }

    Row(
        modifier = modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(Color(dimension.baseColorHex))
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = "$cleanName: $score%",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            softWrap = false
        )
    }
}
