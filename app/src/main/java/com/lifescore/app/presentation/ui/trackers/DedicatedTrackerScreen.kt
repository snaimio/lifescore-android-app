package com.lifescore.app.presentation.ui.trackers

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.lifescore.app.core.trackers.TrackerType
import com.lifescore.app.presentation.ui.trackers.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DedicatedTrackerScreen(
    viewModel: DedicatedTrackerViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val type = viewModel.trackerType
    var showGoalDialog by remember { mutableStateOf(false) }
    var showCustomLogDialog by remember { mutableStateOf(false) }

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

    val status = uiState.status
    val currVal = status?.currentValue ?: 0f
    val goalVal = status?.targetGoal ?: type.defaultGoal
    val progress = status?.progressPercentage ?: 0f
    val isGoalMet = status?.todayCompleted ?: false

    val accentColor = when (type.dimension) {
        com.lifescore.app.domain.model.DimensionType.HEALTH -> Color(0xFF0284C7)
        com.lifescore.app.domain.model.DimensionType.FITNESS -> Color(0xFF10B981)
        com.lifescore.app.domain.model.DimensionType.LEARNING -> Color(0xFF8B5CF6)
        com.lifescore.app.domain.model.DimensionType.CAREER -> Color(0xFFF59E0B)
        com.lifescore.app.domain.model.DimensionType.WEALTH -> Color(0xFFEAB308)
        com.lifescore.app.domain.model.DimensionType.RELATIONSHIPS -> Color(0xFFEC4899)
        com.lifescore.app.domain.model.DimensionType.MENTAL_HEALTH -> Color(0xFF06B6D4)
        else -> MaterialTheme.colorScheme.primary
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
                                text = "${type.emoji} ${type.title}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "${type.dimension.displayName} • Dedicated Mini-App",
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
                        IconButton(onClick = { showGoalDialog = true }) {
                            Icon(Icons.Default.Tune, contentDescription = "Adjust Goal")
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
                // 1. Master Progress Card
                item {
                    MiniAppProgressCard(
                        emoji = type.emoji,
                        title = type.title,
                        currentValue = currVal,
                        targetGoal = goalVal,
                        unit = type.unit,
                        isGoalMet = isGoalMet,
                        progress = progress,
                        onQuickAdd = { showCustomLogDialog = true },
                        onAdjustGoal = { showGoalDialog = true },
                        accentColor = accentColor
                    )
                }

                // 2. Streak Card
                item {
                    MiniAppStreakCard(
                        streakDays = status?.streakDays ?: 3,
                        dimensionName = type.dimension.displayName
                    )
                }

                // 3. Quick Log Presets Row
                item {
                    Text(
                        text = "⚡ Quick Log Presets",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(Spacing.xs))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        type.quickAddValues.forEach { quickVal ->
                            val quickLabel = if (quickVal % 1.0f == 0f) "+${quickVal.toInt()}" else "+$quickVal"
                            OutlinedButton(
                                onClick = { viewModel.logValue(quickVal, "Quick log") },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "$quickLabel ${type.unit}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 4. Weekly 7-Day Performance Chart
                item {
                    MiniAppWeeklyChart(
                        weeklyData = uiState.weeklyData,
                        targetGoal = goalVal,
                        unit = type.unit,
                        accentColor = accentColor
                    )
                }

                // 5. Activity Log History
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📋 Activity Log (${uiState.historyLogs.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Total Today: ${if (currVal % 1.0f == 0f) currVal.toInt() else "%.1f".format(currVal)} ${type.unit}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
                }

                if (uiState.historyLogs.isEmpty()) {
                    item {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Spacing.lg),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(type.emoji, fontSize = 32.sp)
                                Spacer(Modifier.height(Spacing.xs))
                                Text(
                                    "No entries logged yet today",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Tap any quick button above to record your first session!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(uiState.historyLogs, key = { it.id }) { log ->
                        MiniAppHistoryItem(
                            entry = log,
                            unit = type.unit,
                            onDelete = { viewModel.deleteLog(log.id) }
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(Spacing.xl))
                }
            }
        }
    }

    if (showCustomLogDialog) {
        CustomMiniAppLogDialog(
            type = type,
            onDismiss = { showCustomLogDialog = false },
            onLog = { valAmount, note ->
                viewModel.logValue(valAmount, note)
                showCustomLogDialog = false
            }
        )
    }

    if (showGoalDialog) {
        AdjustGoalDialog(
            type = type,
            currentGoal = goalVal,
            onDismiss = { showGoalDialog = false },
            onSave = { newG ->
                viewModel.updateGoal(newG)
                showGoalDialog = false
            }
        )
    }
}

@Composable
fun CustomMiniAppLogDialog(
    type: TrackerType,
    onDismiss: () -> Unit,
    onLog: (Float, String) -> Unit
) {
    var textValue by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(type.emoji, fontSize = 24.sp)
                    Spacer(Modifier.width(Spacing.xs))
                    Text(
                        text = "Log ${type.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                }

                Text(
                    text = type.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    label = { Text("Amount (${type.unit})") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Session Note (optional)") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
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
                            onLog(parsed, note)
                        },
                        shape = RoundedCornerShape(10.dp),
                        enabled = textValue.isNotBlank()
                    ) {
                        Text("Save Entry (+${type.xpReward} XP)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AdjustGoalDialog(
    type: TrackerType,
    currentGoal: Float,
    onDismiss: () -> Unit,
    onSave: (Float) -> Unit
) {
    var goalText by remember { mutableStateOf(if (currentGoal % 1.0f == 0f) "${currentGoal.toInt()}" else "$currentGoal") }

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
                Text(
                    text = "🎯 Adjust Daily Target",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "Configure your personalized daily target for ${type.title}.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = goalText,
                    onValueChange = { goalText = it },
                    label = { Text("Daily Target (${type.unit})") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(10.dp),
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
                            val parsed = goalText.toFloatOrNull() ?: type.defaultGoal
                            onSave(parsed)
                        },
                        shape = RoundedCornerShape(10.dp),
                        enabled = goalText.isNotBlank()
                    ) {
                        Text("Update Target", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
