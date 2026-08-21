package com.lifescore.app.presentation.ui.dimensions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.EmptyState
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.core.designsystem.components.TaskItem
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.LifeTask
import com.lifescore.app.presentation.ui.home.components.SectionHeader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DimensionsUiState(
    val selectedDimension: DimensionType = DimensionType.HEALTH,
    val tasksForSelectedDimension: List<LifeTask> = emptyList(),
    val dimensionScores: Map<DimensionType, Int> = emptyMap()
)

class DimensionsViewModel(
    private val repository: LifeScoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DimensionsUiState())
    val uiState: StateFlow<DimensionsUiState> = _uiState.asStateFlow()

    init {
        loadTasksForDimension(DimensionType.HEALTH)
    }

    fun selectDimension(dimension: DimensionType) {
        _uiState.value = _uiState.value.copy(selectedDimension = dimension)
        loadTasksForDimension(dimension)
    }

    private fun loadTasksForDimension(dimension: DimensionType) {
        viewModelScope.launch {
            repository.getTasksByDimension(dimension).collect { tasks ->
                _uiState.value = _uiState.value.copy(tasksForSelectedDimension = tasks)
            }
        }
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
                    Text(
                        "Life Dimensions",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
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
            // Scrollable Dimension Tabs
            ScrollableTabRow(
                selectedTabIndex = DimensionType.values().indexOf(uiState.selectedDimension),
                edgePadding = Spacing.md,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                DimensionType.values().forEach { dim ->
                    Tab(
                        selected = uiState.selectedDimension == dim,
                        onClick = { viewModel.selectDimension(dim) },
                        text = {
                            Text(
                                dim.displayName,
                                fontWeight = if (uiState.selectedDimension == dim) FontWeight.Bold else FontWeight.Medium,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (uiState.selectedDimension == dim)
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
                item {
                    // Dimension Header Card
                    GlassCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = uiState.selectedDimension.displayName,
                                fontWeight = FontWeight.Black,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(uiState.selectedDimension.baseColorHex)
                            )
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
                    SectionHeader("Active Habits & Quests")
                }

                if (uiState.tasksForSelectedDimension.isEmpty()) {
                    item {
                        EmptyState(
                            icon = "🌟",
                            title = "No habits created yet",
                            description = "Tap the + button below to create your first ${uiState.selectedDimension.displayName} habit.",
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
