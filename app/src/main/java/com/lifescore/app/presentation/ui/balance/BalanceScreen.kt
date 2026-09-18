package com.lifescore.app.presentation.ui.balance

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.EmptyState
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.core.designsystem.components.TaskItem
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.LifeTask
import com.lifescore.app.presentation.navigation.Screen
import com.lifescore.app.presentation.ui.dimensions.DimensionLegend
import com.lifescore.app.presentation.ui.dimensions.DimensionsViewModel
import com.lifescore.app.presentation.ui.dimensions.LifeMatrixEmptyState
import com.lifescore.app.presentation.ui.home.components.DimensionRadarChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceScreen(
    navController: NavController,
    viewModel: DimensionsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var expandedDimension by remember { mutableStateOf<DimensionType?>(null) }

    val avgBalance = remember(uiState.dimensionScores) {
        if (uiState.dimensionScores.isNotEmpty() && uiState.dimensionScores.values.any { it > 0 }) {
            uiState.dimensionScores.values.average().toInt()
        } else {
            0
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Life Balance",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "360° Matrix • 8 Life Dimensions",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.FullAssessment.route) }) {
                        Icon(
                            Icons.Default.Psychology,
                            contentDescription = "Full Assessment",
                            tint = MaterialTheme.colorScheme.primary
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
        if (uiState.allDimensionsZero) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LifeMatrixEmptyState(
                    onStartAssessment = {
                        navController.navigate(Screen.QuickAssessment.route)
                    },
                    onAddFirstHabit = {
                        showAddTaskDialog = true
                    }
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                contentPadding = PaddingValues(top = Spacing.sm, bottom = Spacing.xxl)
            ) {
                // ==========================================
                // 1. 360° LIFE MATRIX HERO CARD
                // ==========================================
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(Spacing.md)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "360° Life Matrix",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Equilibrium across all life domains",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "$avgBalance% Balance",
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
                                    .height(210.dp)
                            )

                            Spacer(Modifier.height(Spacing.sm))

                            DimensionLegend(dimensionScores = uiState.dimensionScores)
                        }
                    }
                }

                // ==========================================
                // 2. BEHAVIORAL CROSS-DIMENSION INSIGHTS
                // ==========================================
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(Spacing.md)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("💡", fontSize = 18.sp)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Cross-Dimension Insight",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.height(Spacing.xs))
                            Text(
                                text = "“Your Fitness and Mental Health dimensions have an 84% positive correlation. Consistent physical movement consistently elevates your overall focus and emotional resilience.”",
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // ==========================================
                // 3. 30-DAY TRAJECTORY FORECAST
                // ==========================================
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(Spacing.md)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.TrendingUp,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "30-Day Trajectory Forecast",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                ) {
                                    Text(
                                        text = "Projected +42 pts",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(Spacing.xs))
                            Text(
                                text = "Based on your current 80%+ habit consistency, your weakest dimension (Learning) is on track to increase by +18% over the next 30 days.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // ==========================================
                // 4. 8-DIMENSION BREAKDOWN PROGRESS CARDS
                // ==========================================
                item {
                    Text(
                        "Dimension Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = Spacing.xs)
                    )
                }

                items(DimensionType.values().toList(), key = { it.name }) { dimension ->
                    val score = uiState.dimensionScores[dimension] ?: 0
                    val isExpanded = expandedDimension == dimension
                    val dimTasks = uiState.allTasks.filter { it.dimension == dimension }
                    val mockTrend = remember(dimension) {
                        when (dimension) {
                            DimensionType.HEALTH -> "+4% this week"
                            DimensionType.FITNESS -> "+6% this week"
                            DimensionType.CAREER -> "+2% this week"
                            DimensionType.LEARNING -> "+8% this week"
                            DimensionType.MENTAL_HEALTH -> "+5% this week"
                            else -> "Stable"
                        }
                    }

                    DimensionDetailCard(
                        dimension = dimension,
                        score = score,
                        trendText = mockTrend,
                        isExpanded = isExpanded,
                        tasks = dimTasks,
                        onToggleExpand = {
                            expandedDimension = if (isExpanded) null else dimension
                            viewModel.selectDimension(dimension)
                        },
                        onToggleTask = { task -> viewModel.toggleTask(task) },
                        onAddTask = {
                            viewModel.selectDimension(dimension)
                            showAddTaskDialog = true
                        }
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
                    label = { Text("Habit name (e.g. 15m Morning Walk)") },
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
                    Text("Add")
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

@Composable
private fun DimensionDetailCard(
    dimension: DimensionType,
    score: Int,
    trendText: String,
    isExpanded: Boolean,
    tasks: List<LifeTask>,
    onToggleExpand: () -> Unit,
    onToggleTask: (LifeTask) -> Unit,
    onAddTask: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleExpand() }
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(dimension.baseColorHex))
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        dimension.displayName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = trendText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (trendText.startsWith("+")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "$score%",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = Color(dimension.baseColorHex)
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { (score.toFloat() / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color(dimension.baseColorHex),
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = Spacing.sm)) {
                    Text(
                        dimension.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(Spacing.sm))

                    if (tasks.isEmpty()) {
                        Text(
                            "No active habits in this dimension yet.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        tasks.forEach { task ->
                            TaskItem(
                                task = task,
                                onComplete = { onToggleTask(task) }
                            )
                            Spacer(Modifier.height(Spacing.xs))
                        }
                    }

                    Spacer(Modifier.height(Spacing.xs))

                    OutlinedButton(
                        onClick = onAddTask,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Add ${dimension.displayName} Habit", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
