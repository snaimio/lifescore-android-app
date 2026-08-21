package com.lifescore.app.presentation.ui.trackers

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.core.designsystem.components.GradientButton
import com.lifescore.app.core.trackers.TrackerStatus
import com.lifescore.app.core.trackers.TrackerType
import com.lifescore.app.domain.model.DimensionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerHubScreen(
    viewModel: TrackerHubViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedTrackerForCustomLog by remember { mutableStateOf<TrackerType?>(null) }

    LaunchedEffect(uiState.successToast) {
        uiState.successToast?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { err ->
            Toast.makeText(context, "Error: $err", Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    val filteredTrackers = remember(uiState.trackers, uiState.selectedDimension) {
        if (uiState.selectedDimension == null) {
            uiState.trackers
        } else {
            uiState.trackers.filter { it.type.dimension == uiState.selectedDimension }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "⚡ 15 Life Trackers",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                "8 Life Dimensions • Complete Life OS Engine",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.padding(end = Spacing.sm)
                        ) {
                            Text(
                                text = "${uiState.completedTrackersCount} / 15 Active",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                // 1. Master Overview Card
                item {
                    MasterOverviewCard(
                        completedCount = uiState.completedTrackersCount,
                        totalCount = uiState.totalTrackersCount
                    )
                }

                // 2. Dimension Filter Chips
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = uiState.selectedDimension == null,
                                onClick = { viewModel.filterDimension(null) },
                                label = { Text("All (15)", style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                        items(DimensionType.values()) { dim ->
                            FilterChip(
                                selected = uiState.selectedDimension == dim,
                                onClick = { viewModel.filterDimension(if (uiState.selectedDimension == dim) null else dim) },
                                label = { Text("${dim.displayName}", style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }

                // 3. 15 Tracker Cards List
                items(filteredTrackers, key = { it.type.id }) { tracker ->
                    TrackerModuleCard(
                        tracker = tracker,
                        onOpenTracker = { navController.navigate("tracker_detail/${tracker.type.id}") },
                        onQuickLog = { amount -> viewModel.quickLog(tracker.type, amount) },
                        onCustomLog = { selectedTrackerForCustomLog = tracker.type }
                    )
                }

                item {
                    Spacer(Modifier.height(Spacing.xl))
                }
            }
        }
    }

    selectedTrackerForCustomLog?.let { trackerType ->
        CustomLogDialog(
            trackerType = trackerType,
            onDismiss = { selectedTrackerForCustomLog = null },
            onLog = { value ->
                viewModel.quickLog(trackerType, value)
                selectedTrackerForCustomLog = null
            }
        )
    }
}

@Composable
fun MasterOverviewCard(
    completedCount: Int,
    totalCount: Int
) {
    val progress = (completedCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(700),
        label = "progress"
    )

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🌐", fontSize = 20.sp)
                    Spacer(Modifier.width(Spacing.xs))
                    Text(
                        "Life OS Equilibrium",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "15 modular telemetry feeds compounding daily habit momentum",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
                Spacer(Modifier.height(Spacing.xs))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "⚡ Up to +400 XP Daily",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(Modifier.width(Spacing.md))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(76.dp)
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 8.dp,
                    strokeCap = StrokeCap.Round
                )
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF8B5CF6),
                    strokeWidth = 8.dp,
                    strokeCap = StrokeCap.Round
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$completedCount/$totalCount",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "MET",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
fun TrackerModuleCard(
    tracker: TrackerStatus,
    onOpenTracker: () -> Unit,
    onQuickLog: (Float) -> Unit,
    onCustomLog: () -> Unit
) {
    val progress = tracker.progressPercentage
    val type = tracker.type
    val isCompleted = tracker.todayCompleted

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md)
        ) {
            // Header Row: Emoji + Title + Dimension + Streak
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onOpenTracker,
                    color = Color.Transparent,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ) {
                        Text(
                            text = type.emoji,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                    Spacer(Modifier.width(Spacing.sm))
                    Column {
                        Text(
                            text = type.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = type.dimension.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF59E0B).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "🔥 ${tracker.streakDays}d",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF59E0B),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isCompleted) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = if (isCompleted) "✓ DONE" else "+${type.xpReward} XP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(Spacing.sm))

            // Progress bar and value text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentFormatted = if (tracker.currentValue % 1.0f == 0f) "${tracker.currentValue.toInt()}" else "%.1f".format(tracker.currentValue)
                val targetFormatted = if (tracker.targetGoal % 1.0f == 0f) "${tracker.targetGoal.toInt()}" else "%.1f".format(tracker.targetGoal)

                Text(
                    text = "$currentFormatted / $targetFormatted ${type.unit}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = if (isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(Modifier.height(Spacing.sm))

            // Quick Add Pills + Custom Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                type.quickAddValues.forEach { quickVal ->
                    val quickLabel = if (quickVal % 1.0f == 0f) "+${quickVal.toInt()}" else "+$quickVal"
                    OutlinedButton(
                        onClick = { onQuickLog(quickVal) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp)
                    ) {
                        Text(
                            text = "$quickLabel",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                OutlinedIconButton(
                    onClick = onCustomLog,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Custom log",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CustomLogDialog(
    trackerType: TrackerType,
    onDismiss: () -> Unit,
    onLog: (Float) -> Unit
) {
    var textValue by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(trackerType.emoji, fontSize = 24.sp)
                        Spacer(Modifier.width(Spacing.xs))
                        Text(
                            text = "Log ${trackerType.title}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = trackerType.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    label = { Text("Amount in ${trackerType.unit}") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(Spacing.xs))
                    Button(
                        onClick = {
                            val parsed = textValue.toFloatOrNull() ?: 1f
                            onLog(parsed)
                        },
                        shape = RoundedCornerShape(10.dp),
                        enabled = textValue.isNotBlank()
                    ) {
                        Text("Log Entry (+${trackerType.xpReward} XP)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
