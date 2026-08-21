package com.lifescore.app.presentation.ui.hydration

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.core.designsystem.components.GradientButton
import com.lifescore.app.presentation.ui.hydration.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HydrationScreen(
    viewModel: HydrationViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showGoalDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { msg ->
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
                                "💧 Hydration Tracker",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                "Health Dimension • Biological Vitality",
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
                // 1. Progress Card
                item {
                    HydrationProgressCard(
                        stats = uiState.stats,
                        onAddGlass = { viewModel.addWater(250) },
                        onAddBottle = { viewModel.addWater(500) },
                        onAdjustGoal = { showGoalDialog = true }
                    )
                }

                // 2. Streak Card
                item {
                    HydrationStreakCard(streak = uiState.stats?.streakDays ?: 1)
                }

                // 3. Quick Add Row
                item {
                    Text(
                        text = "⚡ Quick Intake Log",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(Spacing.xs))
                    QuickAddRow(
                        onAddSmall = { viewModel.addWater(150) },
                        onAddMedium = { viewModel.addWater(250) },
                        onAddLarge = { viewModel.addWater(500) }
                    )
                }

                // 4. Weekly History Chart
                item {
                    WeeklyHydrationChart(
                        data = uiState.weeklyData,
                        goal = uiState.currentGoalMl
                    )
                }

                // 5. Today's Entries
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📋 Today's Entries (${uiState.todayEntries.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Total: ${uiState.stats?.todayTotalMl ?: 0} ml",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (uiState.todayEntries.isEmpty()) {
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
                                Text("🥤", fontSize = 32.sp)
                                Spacer(Modifier.height(Spacing.xs))
                                Text(
                                    "No water logged yet today",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Tap any quick button above to log your first glass!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(uiState.todayEntries, key = { it.id }) { entry ->
                        HydrationEntryItem(
                            entry = entry,
                            onDelete = { viewModel.deleteEntry(it) }
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(Spacing.xl))
                }
            }
        }
    }

    if (showGoalDialog) {
        HydrationGoalDialog(
            currentGoalMl = uiState.currentGoalMl,
            onDismiss = { showGoalDialog = false },
            onSave = { newGoal ->
                viewModel.updateGoal(newGoal)
                showGoalDialog = false
            }
        )
    }
}

@Composable
fun HydrationGoalDialog(
    currentGoalMl: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var selectedGoal by remember { mutableStateOf(currentGoalMl) }

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
                    text = "🎯 Set Daily Hydration Goal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "Recommended daily intake based on clinical baseline is 2,000ml – 3,500ml.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                listOf(2000, 2500, 3000, 3500).forEach { goal ->
                    Surface(
                        onClick = { selectedGoal = goal },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedGoal == goal) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = if (selectedGoal == goal) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${goal} ml (${goal / 250} glasses)",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (selectedGoal == goal) {
                                Text("✓ Selected", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black, fontSize = 12.sp)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(Spacing.xs))
                    Button(
                        onClick = { onSave(selectedGoal) },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save Goal", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
