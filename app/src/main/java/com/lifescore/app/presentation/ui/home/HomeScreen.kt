package com.lifescore.app.presentation.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lifescore.app.core.designsystem.*
import com.lifescore.app.core.designsystem.components.*
import com.lifescore.app.core.engine.UserPhase
import com.lifescore.app.core.util.ShareCardData
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.HeroArchetype
import com.lifescore.app.domain.model.LifeTask
import com.lifescore.app.domain.model.UserProfile
import com.lifescore.app.presentation.navigation.Screen
import com.lifescore.app.presentation.ui.components.CharacterSheetDialog
import com.lifescore.app.presentation.ui.components.GuardianSponsorshipDialog
import com.lifescore.app.presentation.ui.components.HardModeSheet
import com.lifescore.app.presentation.ui.home.components.DimensionRadarChart
import com.lifescore.app.presentation.ui.home.components.LifeScoreProgressionCard
import com.lifescore.app.presentation.ui.home.components.QuickActionsRow
import com.lifescore.app.presentation.ui.home.components.SectionHeader
import com.lifescore.app.presentation.ui.share.ShareScoreCardDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel,
    onOpenPaywall: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var showCharacterSheet by remember { mutableStateOf(false) }
    var showGuardianDialog by remember { mutableStateOf(false) }
    var showHardModeSheet by remember { mutableStateOf(false) }
    var showShareCardDialog by remember { mutableStateOf(false) }
    var isHardModeEnabled by remember { mutableStateOf(false) }

    // Sync rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "sync_rotate")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCharacterSheet = true }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("⚔️", fontSize = 16.sp)
                            }
                        }
                        Spacer(Modifier.width(Spacing.sm))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "LifeScore",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "${uiState.userTitle} • Lvl ${uiState.level}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.GroupHabits.route) }) {
                        Icon(
                            Icons.Default.GroupAdd,
                            contentDescription = "Squads",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { showShareCardDialog = true }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share LifeScore",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Spacer(Modifier.height(Spacing.xs))
                LoadingSkeleton(height = 140)
                LoadingSkeleton(height = 60)
                LoadingSkeleton(height = 200)
                LoadingSkeleton(height = 100)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                // 1. Cloud Sync Banner
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.triggerManualSync() }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (uiState.isSyncing) {
                                Icon(
                                    Icons.Default.Sync,
                                    contentDescription = "Syncing",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .rotate(rotationAngle)
                                )
                            } else {
                                Icon(
                                    Icons.Default.CloudDone,
                                    contentDescription = "Cloud Synced",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(Modifier.width(Spacing.xs))
                            Text(
                                text = if (uiState.isSyncing) "Syncing with Firestore..." else "Live Cloud Sync Active",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Auto-Sync ON",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // 1.5. Progressive User Phase Milestone Banner
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = uiState.userPhase.badgeEmoji,
                                    fontSize = 24.sp,
                                    modifier = Modifier.padding(Spacing.sm)
                                )
                            }
                            Spacer(Modifier.width(Spacing.sm))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${uiState.userPhase.title} • ${uiState.userPhase.subtitle}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = uiState.milestoneMessage ?: "Start simple and build compounding momentum daily.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // 2. Hero Card
                item {
                    HeroCard(
                        score = uiState.totalScore,
                        level = uiState.level,
                        currentXp = uiState.currentXp,
                        xpToNextLevel = 1000,
                        streak = uiState.streak,
                        userName = uiState.userName,
                        onShare = { showShareCardDialog = true },
                        onLeaderboard = { navController.navigate(Screen.Leaderboard.route) }
                    )
                }

                // 3. Quick Actions Row
                item {
                    QuickActionsRow(
                        onTasks = { navController.navigate(Screen.Tasks.route) },
                        onAI = { navController.navigate(Screen.AICoach.route) },
                        onLeaderboard = { navController.navigate(Screen.Leaderboard.route) }
                    )
                }

                // 4. Feature Pills (Progressively Unlocked)
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { navController.navigate(Screen.TrackerHub.route) },
                                label = { Text("⚡ 15 Trackers", style = MaterialTheme.typography.labelMedium) }
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { navController.navigate(Screen.ActionPlan.route) },
                                label = { Text("🎯 Action Plan", style = MaterialTheme.typography.labelMedium) }
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { navController.navigate(Screen.Hydration.route) },
                                label = { Text("💧 Water", style = MaterialTheme.typography.labelMedium) }
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { navController.navigate(Screen.HabitLibrary.route) },
                                label = { Text("📚 100 Habits", style = MaterialTheme.typography.labelMedium) }
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { navController.navigate(Screen.AiQuests.route) },
                                label = { Text("🤖 AI Quests", style = MaterialTheme.typography.labelMedium) }
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { navController.navigate(Screen.Journal.route) },
                                label = { Text("📖 Journal", style = MaterialTheme.typography.labelMedium) }
                            )
                        }
                        if (uiState.userPhase != UserPhase.NEW_USER) {
                            item {
                                FilterChip(
                                    selected = false,
                                    onClick = { navController.navigate(Screen.GroupHabits.route) },
                                    label = { Text("👥 Squads", style = MaterialTheme.typography.labelMedium) }
                                )
                            }
                        }
                        if (uiState.userPhase == UserPhase.ADVANCED || uiState.userPhase == UserPhase.EXPERT) {
                            item {
                                FilterChip(
                                    selected = false,
                                    onClick = { navController.navigate(Screen.CharacterStats.route) },
                                    label = { Text("🛡️ Hunter Stats", style = MaterialTheme.typography.labelMedium) }
                                )
                            }
                            item {
                                FilterChip(
                                    selected = false,
                                    onClick = { navController.navigate(Screen.Combat.route) },
                                    label = { Text("⚔️ Boss Fight", style = MaterialTheme.typography.labelMedium) }
                                )
                            }
                        }
                    }
                }

                // 5. Daily Goal Progress
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.md)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🎯", fontSize = 16.sp)
                                    Spacer(Modifier.width(Spacing.xs))
                                    Text(
                                        "Daily Goal Progress",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(Spacing.xs)
                                ) {
                                    Text(
                                        "${uiState.tasksCompleted} / ${uiState.totalTasks} Done",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs)
                                    )
                                }
                            }
                            Spacer(Modifier.height(Spacing.sm))
                            LinearProgressIndicator(
                                progress = { uiState.dailyProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(Spacing.xs)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }

                // 6. 8-Dimension Spider Chart Balance
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.md)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🕸️", fontSize = 16.sp)
                                    Spacer(Modifier.width(Spacing.xs))
                                    Text(
                                        "8-Dimension Balance",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                TextButton(
                                    onClick = { navController.navigate(Screen.Dimensions.route) },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        "Breakdown",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            DimensionRadarChart(
                                dimensionScores = uiState.dimensionScores,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(vertical = Spacing.xs)
                            )
                        }
                    }
                }

                // 7. LifeScore Trajectory Progression Card
                item {
                    LifeScoreProgressionCard(
                        currentScore = uiState.totalScore,
                        targetScore = 1000
                    )
                }

                // 8. Action Cards Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showHardModeSheet = true },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isHardModeEnabled)
                                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                                else
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(Spacing.md),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("💀", fontSize = 20.sp)
                                Spacer(Modifier.width(Spacing.sm))
                                Column {
                                    Text(
                                        "Hard Mode",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        if (isHardModeEnabled) "Active" else "Tap to Enable",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showGuardianDialog = true },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(Spacing.md),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🎁", fontSize = 20.sp)
                                Spacer(Modifier.width(Spacing.sm))
                                Column {
                                    Text(
                                        "Guardian Gift",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        "Sponsor a Hero",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // 8. Quests Section
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SectionHeader("⚡ Today's Dimension Quests")
                        TextButton(
                            onClick = { navController.navigate(Screen.Tasks.route) },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                "Manage All",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                if (uiState.todayTasks.isEmpty()) {
                    item {
                        EmptyState(
                            icon = "🎯",
                            title = "All Caught Up!",
                            description = "No pending dimension quests for today. Add new habits or generate quests with AI Coach!",
                            actionButtonText = "Generate AI Quests",
                            onActionClick = { navController.navigate(Screen.AiQuests.route) }
                        )
                    }
                } else {
                    items(uiState.todayTasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onComplete = { viewModel.onToggleTask(task) }
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(Spacing.xl))
                }
            }
        }
    }

    // Dialogs
    if (showCharacterSheet) {
        val userProfile = UserProfile(
            currentXp = uiState.currentXp,
            currentLevel = uiState.level,
            currentStreakDays = uiState.streak,
            title = uiState.userTitle
        )
        CharacterSheetDialog(
            userProfile = userProfile,
            archetype = HeroArchetype.fromLevel(uiState.level),
            onDismiss = { showCharacterSheet = false }
        )
    }

    if (showGuardianDialog) {
        GuardianSponsorshipDialog(onDismiss = { showGuardianDialog = false })
    }

    if (showHardModeSheet) {
        HardModeSheet(
            isHardMode = isHardModeEnabled,
            streakInsuranceCount = 1,
            onToggleHardMode = { isHardModeEnabled = it },
            onBuyInsurance = { /* Buy streak insurance */ },
            onDismiss = { showHardModeSheet = false }
        )
    }

    if (showShareCardDialog) {
        val shareData = ShareCardData(
            userName = uiState.userName,
            score = uiState.totalScore,
            level = uiState.level,
            streak = uiState.streak,
            title = uiState.userTitle,
            dimensionScores = uiState.dimensionScores
        )
        ShareScoreCardDialog(
            data = shareData,
            onDismiss = { showShareCardDialog = false }
        )
    }
}
