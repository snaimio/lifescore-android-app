package com.lifescore.app.presentation.ui.recovery

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.presentation.navigation.Screen
import com.lifescore.app.presentation.ui.recovery.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoveryDashboardScreen(
    viewModel: RecoveryViewModel,
    navController: NavController,
    onBack: () -> Unit = { navController.popBackStack() },
    onOpenSOS: () -> Unit = { navController.navigate("recovery_sos") }
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showLogCravingDialog by remember { mutableStateOf(false) }
    var showLogSlipDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(state.selectedAddiction.emoji, fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Addiction Recovery OS",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Science-Backed Behavioral Freedom",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEF4444).copy(alpha = 0.15f),
                        modifier = Modifier.padding(end = Spacing.xs)
                    ) {
                        IconButton(onClick = onOpenSOS) {
                            Text("🆘", fontSize = 18.sp)
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onOpenSOS,
                containerColor = Color(0xFFEF4444),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("🆘", fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Text("Craving SOS", fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                // 1. Sobriety Live Countdown Hero Card
                item {
                    SobrietyLiveHeroCard(
                        stats = state.stats,
                        selectedAddiction = state.selectedAddiction,
                        onSelectAddiction = { viewModel.selectAddiction(it) },
                        totalSlips = state.activeRecovery?.totalSlipsCount ?: 0,
                        onLogSlip = { showLogSlipDialog = true },
                        onResetSobriety = { showResetConfirmDialog = true },
                        onOpenSOS = onOpenSOS
                    )
                }

                // 2. Quick Action Grid
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showLogCravingDialog = true }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = Spacing.sm, horizontal = 4.dp)
                            ) {
                                Text("📝", fontSize = 20.sp)
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    "Log Craving",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onOpenSOS() }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = Spacing.sm, horizontal = 4.dp)
                            ) {
                                Text("🌬️", fontSize = 20.sp)
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    "4-7-8 Breath",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { navController.navigate(Screen.AICoach.route) }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = Spacing.sm, horizontal = 4.dp)
                            ) {
                                Text("🤖", fontSize = 20.sp)
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    "AI Coach",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // 3. Reclaimed Life Telemetry (Money, Time, Items, Cravings Resisted)
                item {
                    RecoveryStatsSummaryRow(
                        moneySaved = state.moneySaved,
                        timeSavedHours = state.timeSavedHours,
                        itemsAvoided = state.itemsAvoided,
                        unitName = state.selectedAddiction.unitName,
                        survivedCravingsCount = state.survivedCravingsCount
                    )
                }

                // 4. Daily Freedom Pledge
                item {
                    DailyPledgeCard(
                        pledge = state.todayPledge,
                        onMakePledge = { viewModel.signDailyPledge() },
                        onEveningReflection = { viewModel.saveEveningReflection(it) }
                    )
                }

                // 5. Biological Health Milestones Timeline
                item {
                    HealthMilestonesCard(
                        milestones = state.milestones,
                        currentDays = state.stats.totalDays,
                        onUnlockMilestone = { viewModel.unlockMilestone(it) }
                    )
                }

                // 6. Savings Reward Goals (Fund real rewards with saved cash)
                item {
                    SavingsGoalsSection(
                        savingsGoals = state.savingsGoals,
                        moneySaved = state.moneySaved,
                        onAddGoal = { title, amount, emoji ->
                            viewModel.addSavingsGoal(title, amount, emoji)
                        },
                        onDeleteGoal = { viewModel.deleteSavingsGoal(it) }
                    )
                }

                // 7. CBT & Scientific Thought Tools (Urge Surfing, HALT check, etc.)
                item {
                    CbtThoughtToolsCard(lessons = state.cbtLessons)
                }

                item {
                    Spacer(Modifier.height(80.dp)) // Padding for FAB
                }
            }
        }
    }

    // Dialogs
    if (showLogCravingDialog) {
        LogCravingDialog(
            addictionType = state.selectedAddiction,
            triggers = state.triggers,
            onDismiss = { showLogCravingDialog = false },
            onConfirm = {
                viewModel.logCraving(it)
                showLogCravingDialog = false
            }
        )
    }

    if (showLogSlipDialog) {
        LogSlipDialog(
            addictionType = state.selectedAddiction,
            onDismiss = { showLogSlipDialog = false },
            onConfirm = { type, trigger, lesson, plan ->
                viewModel.logSlipOrRelapse(type, trigger, lesson, plan)
                showLogSlipDialog = false
            }
        )
    }

    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Reset Sobriety Clock?") },
            text = {
                Text("Are you sure you want to reset the counter to 0? If this was a minor slip, you can instead choose 'Log Slip' to protect your streak while recording the lesson learned.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetSobrietyClock()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Reset Clock")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) { Text("Cancel") }
            }
        )
    }
}
