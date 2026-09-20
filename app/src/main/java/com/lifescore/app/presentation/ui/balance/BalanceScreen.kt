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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lifescore.app.core.designsystem.components.CardVariant
import com.lifescore.app.core.designsystem.DimensionColors
import com.lifescore.app.core.designsystem.DimensionPastels
import com.lifescore.app.core.designsystem.LifeScoreShapes
import com.lifescore.app.core.designsystem.Motion
import com.lifescore.app.core.designsystem.Neutrals
import com.lifescore.app.core.designsystem.Space
import com.lifescore.app.core.designsystem.components.AnimatedNumber
import com.lifescore.app.core.designsystem.components.LifeCard
import com.lifescore.app.core.designsystem.components.SectionHeader
import com.lifescore.app.core.designsystem.components.StaggeredAppear
import com.lifescore.app.core.designsystem.components.TaskItem
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.LifeTask
import com.lifescore.app.core.designsystem.components.LifeIcon
import com.lifescore.app.core.designsystem.components.LifeIcons
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
    var expandedDimension by remember { mutableStateOf<DimensionType?>(DimensionType.HEALTH) }

    val avgBalance = remember(uiState.dimensionScores) {
        if (uiState.dimensionScores.isNotEmpty()) {
            uiState.dimensionScores.values.average().toInt()
        } else {
            50
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
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
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
                containerColor = DimensionColors.forDimension(uiState.selectedDimension),
                shape = LifeScoreShapes.fab
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit", tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Space.screenH),
            verticalArrangement = Arrangement.spacedBy(Space.cardGap),
            contentPadding = PaddingValues(top = Space.xs, bottom = Space.xxxl)
        ) {
                // ==========================================
                // 1. 360° LIFE MATRIX HERO CARD
                // ==========================================
                item {
                    StaggeredAppear(index = 0) {
                        LifeCard(
                            modifier = Modifier.fillMaxWidth(),
                            variant = CardVariant.Default
                        ) {
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
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = LifeScoreShapes.tag,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = Space.sm, vertical = Space.xxs),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AnimatedNumber(
                                            value = avgBalance,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "% Balance",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(Space.md))

                            DimensionRadarChart(
                                dimensionScores = uiState.dimensionScores,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = Space.xs)
                            )

                            Spacer(Modifier.height(Space.sm))

                            DimensionLegend(dimensionScores = uiState.dimensionScores)
                        }
                    }
                }

                // ==========================================
                // 2. BEHAVIORAL CROSS-DIMENSION INSIGHTS
                // ==========================================
                item {
                    StaggeredAppear(index = 1) {
                        LifeCard(
                            modifier = Modifier.fillMaxWidth(),
                            variant = CardVariant.Cream
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LifeIcon(
                                    icon = LifeIcons.Analytics,
                                    size = 18.dp
                                )
                                Spacer(Modifier.width(Space.sm))
                                Text(
                                    "Cross-Dimension Insight",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.height(Space.xs))
                            Text(
                                text = "“Your Fitness and Mental Health dimensions have an 84% positive correlation. Consistent physical movement elevates your overall focus and emotional resilience.”",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // ==========================================
                // 3. 30-DAY TRAJECTORY FORECAST
                // ==========================================
                item {
                    StaggeredAppear(index = 2) {
                        LifeCard(
                            modifier = Modifier.fillMaxWidth(),
                            variant = CardVariant.Default
                        ) {
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
                                    Spacer(Modifier.width(Space.sm))
                                    Text(
                                        "30-Day Trajectory Forecast",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Surface(
                                    shape = LifeScoreShapes.tag,
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                ) {
                                    Text(
                                        text = "Projected +18% Growth",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = Space.sm, vertical = Space.xxs)
                                    )
                                }
                            }
                            Spacer(Modifier.height(Space.xs))
                            Text(
                                text = "Based on your current 80%+ habit consistency, your weakest dimension (Learning) is on track to increase by +18% over the next 30 days.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // ==========================================
                // 4. 8-DIMENSION BREAKDOWN PROGRESS CARDS (2x4 GRID)
                // ==========================================
                item {
                    SectionHeader(
                        title = "Dimension Breakdown",
                        subtitle = "Detailed balance metrics for each life domain"
                    )
                }

                item {
                    val allDims = DimensionType.values().toList()
                    val chunkedDims = allDims.chunked(2)
                    Column(verticalArrangement = Arrangement.spacedBy(Space.sm)) {
                        chunkedDims.forEach { rowDims ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Space.sm)
                            ) {
                                rowDims.forEach { dimension ->
                                    val score = uiState.dimensionScores[dimension] ?: 0
                                    val isExpanded = expandedDimension == dimension
                                    val dimTasks = uiState.allTasks.filter { it.dimension == dimension }
                                    val dimColor = DimensionColors.forDimension(dimension)
                                    val trendBadge = when (dimension) {
                                        DimensionType.HEALTH -> "+3%"
                                        DimensionType.WEALTH -> "+1%"
                                        DimensionType.RELATIONSHIPS -> "+5%"
                                        DimensionType.CAREER -> "+2%"
                                        DimensionType.LEARNING -> "+4%"
                                        DimensionType.FITNESS -> "+6%"
                                        DimensionType.MENTAL_HEALTH -> "+2%"
                                        DimensionType.SOCIAL_LIFE -> "+3%"
                                    }

                                    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
                                    val cardBg = if (isDark) MaterialTheme.colorScheme.surface else DimensionPastels.backgroundFor(dimension)
                                    val cardBorder = if (isDark) {
                                        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                    } else {
                                        BorderStroke(1.dp, DimensionPastels.borderFor(dimension))
                                    }

                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(LifeScoreShapes.card)
                                            .clickable {
                                                expandedDimension = if (isExpanded) null else dimension
                                                viewModel.selectDimension(dimension)
                                            },
                                        shape = LifeScoreShapes.card,
                                        color = cardBg,
                                        border = cardBorder
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(Space.md),
                                            verticalArrangement = Arrangement.spacedBy(Space.xs)
                                        ) {
                                            // Top Row: Icon + Score
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = dimColor.copy(alpha = 0.18f),
                                                    modifier = Modifier.size(34.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        LifeIcon(
                                                            icon = LifeIcons.forDimension(dimension),
                                                            size = 18.dp,
                                                            tint = dimColor
                                                        )
                                                    }
                                                }

                                                Surface(
                                                    shape = LifeScoreShapes.pill,
                                                    color = Color(0xFF10B981).copy(alpha = 0.15f)
                                                ) {
                                                    Text(
                                                        text = trendBadge,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF10B981),
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }

                                            Spacer(Modifier.height(2.dp))

                                            // Dimension Name
                                            Text(
                                                text = dimension.displayName,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            // Score Percentage Text
                                            Text(
                                                text = "$score%",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Black,
                                                color = dimColor
                                            )

                                            // Progress Bar
                                            LinearProgressIndicator(
                                                progress = { (score.toFloat() / 100f).coerceIn(0f, 1f) },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(4.dp)
                                                    .clip(RoundedCornerShape(2.dp)),
                                                color = dimColor,
                                                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                            )
                                        }
                                    }
                                }
                                if (rowDims.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 5. SELECTED DIMENSION DETAIL & HABITS
                // ==========================================
                if (expandedDimension != null) {
                    val activeDim = expandedDimension!!
                    val score = uiState.dimensionScores[activeDim] ?: 50
                    val dimTasks = uiState.allTasks.filter { it.dimension == activeDim }
                    val dimColor = DimensionColors.forDimension(activeDim)

                    item {
                        SectionHeader(
                            title = "${activeDim.displayName} Habits & Focus",
                            subtitle = activeDim.description
                        )
                    }

                    item {
                        LifeCard(
                            modifier = Modifier.fillMaxWidth(),
                            variant = CardVariant.Default
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = dimColor.copy(alpha = 0.18f),
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            LifeIcon(
                                                icon = LifeIcons.forDimension(activeDim),
                                                size = 18.dp,
                                                tint = dimColor
                                            )
                                        }
                                    }
                                    Spacer(Modifier.width(Space.sm))
                                    Column {
                                        Text(
                                            activeDim.displayName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "${dimTasks.count { it.isCompleted }} of ${dimTasks.size} completed",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    shape = LifeScoreShapes.tag,
                                    color = dimColor.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "$score%",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Black,
                                        color = dimColor,
                                        modifier = Modifier.padding(horizontal = Space.sm, vertical = Space.xxs)
                                    )
                                }
                            }

                            Spacer(Modifier.height(Space.sm))

                            LinearProgressIndicator(
                                progress = { (score.toFloat() / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = dimColor,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )

                            Spacer(Modifier.height(Space.md))

                            if (dimTasks.isEmpty()) {
                                Text(
                                    "No habits active in ${activeDim.displayName} yet.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                dimTasks.forEach { task ->
                                    TaskItem(
                                        task = task,
                                        onComplete = { viewModel.toggleTask(task) }
                                    )
                                    Spacer(Modifier.height(Space.xs))
                                }
                            }

                            Spacer(Modifier.height(Space.xs))

                            OutlinedButton(
                                onClick = {
                                    viewModel.selectDimension(activeDim)
                                    showAddTaskDialog = true
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = LifeScoreShapes.button
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(Space.xs))
                                Text("Add ${activeDim.displayName} Habit", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }

    if (showAddTaskDialog) {
        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = {
                Text(
                    "Add ${uiState.selectedDimension.displayName} Habit",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = newTaskTitle,
                    onValueChange = { newTaskTitle = it },
                    label = { Text("Habit name (e.g. 15m Morning Walk)") },
                    singleLine = true,
                    shape = LifeScoreShapes.input,
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
                    },
                    shape = LifeScoreShapes.button
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = LifeScoreShapes.modal
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
    val dimColor = DimensionColors.forDimension(dimension)

    LifeCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleExpand() },
        variant = CardVariant.Default
    ) {
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
                        .background(dimColor)
                )
                Spacer(Modifier.width(Space.sm))
                Text(
                    dimension.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = LifeScoreShapes.tag,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = trendText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (trendText.startsWith("+")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = Space.xs, vertical = Space.xxs)
                    )
                }
                Spacer(Modifier.width(Space.sm))
                Text(
                    "$score%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    color = dimColor
                )
            }
        }

        Spacer(Modifier.height(Space.xs))

        LinearProgressIndicator(
            progress = { (score.toFloat() / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = dimColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(Motion.Gentle) + expandVertically(),
            exit = fadeOut(Motion.Snappy) + shrinkVertically()
        ) {
            Column(modifier = Modifier.padding(top = Space.sm)) {
                Text(
                    dimension.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(Space.sm))

                if (tasks.isEmpty()) {
                    Text(
                        "No active habits in this dimension yet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    tasks.forEach { task ->
                        TaskItem(
                            task = task,
                            onComplete = { onToggleTask(task) }
                        )
                        Spacer(Modifier.height(Space.xs))
                    }
                }

                Spacer(Modifier.height(Space.xs))

                OutlinedButton(
                    onClick = onAddTask,
                    modifier = Modifier.fillMaxWidth(),
                    shape = LifeScoreShapes.button
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(Space.xs))
                    Text("Add ${dimension.displayName} Habit", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
