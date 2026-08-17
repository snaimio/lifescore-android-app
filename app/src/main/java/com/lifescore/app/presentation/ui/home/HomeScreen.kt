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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lifescore.app.core.designsystem.*
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.core.designsystem.components.LoadingSkeleton
import com.lifescore.app.core.util.ShareCardData
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.HeroArchetype
import com.lifescore.app.domain.model.LifeTask
import com.lifescore.app.domain.model.UserProfile
import com.lifescore.app.presentation.navigation.Screen
import com.lifescore.app.presentation.ui.components.CharacterSheetDialog
import com.lifescore.app.presentation.ui.components.GuardianSponsorshipDialog
import com.lifescore.app.presentation.ui.components.HardModeSheet
import com.lifescore.app.presentation.ui.home.components.*
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
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showCharacterSheet = true }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("⚔️", fontSize = 18.sp)
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                "LifeScore",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    uiState.userTitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("•", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "Lvl ${uiState.level}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.triggerManualSync() }) {
                        Icon(
                            Icons.Default.CloudSync,
                            contentDescription = "Sync Cloud",
                            tint = if (uiState.isSyncing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = if (uiState.isSyncing) Modifier.rotate(rotationAngle) else Modifier
                        )
                    }

                    IconButton(onClick = { showShareCardDialog = true }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share LifeScore Card",
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
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoadingSkeleton(modifier = Modifier.fillMaxWidth(), height = 240, shape = RoundedCornerShape(22.dp))
                LoadingSkeleton(modifier = Modifier.fillMaxWidth(), height = 60, shape = RoundedCornerShape(16.dp))
                LoadingSkeleton(modifier = Modifier.fillMaxWidth(), height = 180, shape = RoundedCornerShape(20.dp))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp)
            ) {
                // Cloud Sync Status Pill
                item {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (uiState.isSyncing) Icons.Default.Sync else Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = if (uiState.isSyncing) MaterialTheme.colorScheme.primary else Color(0xFF43A047),
                                modifier = Modifier
                                    .size(15.dp)
                                    .let { if (uiState.isSyncing) it.rotate(rotationAngle) else it }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = uiState.cloudSyncStatus,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.weight(1f))
                            Text(
                                text = "Auto-Sync ON",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Senior Hero Card with Animated Counter & Progression
                item {
                    HeroCard(
                        score = uiState.totalScore,
                        level = uiState.level,
                        currentXp = uiState.currentXp,
                        xpToNextLevel = 1000,
                        streak = uiState.streak,
                        userName = uiState.userName.ifEmpty { "Hero" },
                        onShare = { showShareCardDialog = true },
                        onLeaderboard = { navController.navigate(Screen.Leaderboard.route) }
                    )
                }

                // Quick Action Frosted Chips
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            AssistChip(
                                onClick = { navController.navigate(Screen.AiQuests.route) },
                                label = { Text("AI Quests", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                leadingIcon = { Text("🤖", fontSize = 13.sp) },
                                shape = CircleShape,
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                )
                            )
                        }
                        item {
                            AssistChip(
                                onClick = { navController.navigate(Screen.CharacterStats.route) },
                                label = { Text("Hunter Stats", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                leadingIcon = { Text("🛡️", fontSize = 13.sp) },
                                shape = CircleShape
                            )
                        }
                        item {
                            AssistChip(
                                onClick = { navController.navigate(Screen.GroupHabits.route) },
                                label = { Text("Squads", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                leadingIcon = { Text("👥", fontSize = 13.sp) },
                                shape = CircleShape
                            )
                        }
                        item {
                            AssistChip(
                                onClick = { navController.navigate(Screen.Journal.route) },
                                label = { Text("Journal", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                leadingIcon = { Text("📖", fontSize = 13.sp) },
                                shape = CircleShape
                            )
                        }
                        item {
                            AssistChip(
                                onClick = { navController.navigate(Screen.Combat.route) },
                                label = { Text("Boss Raids", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                leadingIcon = { Text("⚔️", fontSize = 13.sp) },
                                shape = CircleShape
                            )
                        }
                        item {
                            AssistChip(
                                onClick = { navController.navigate(Screen.Analytics.route) },
                                label = { Text("Heatmap", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                leadingIcon = { Text("📊", fontSize = 13.sp) },
                                shape = CircleShape
                            )
                        }
                        item {
                            AssistChip(
                                onClick = { navController.navigate(Screen.Privacy.route) },
                                label = { Text("Privacy", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                leadingIcon = { Text("🔒", fontSize = 13.sp) },
                                shape = CircleShape
                            )
                        }
                        item {
                            AssistChip(
                                onClick = { navController.navigate(Screen.RewardStore.route) },
                                label = { Text("Coins", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                leadingIcon = { Text("🪙", fontSize = 13.sp) },
                                shape = CircleShape
                            )
                        }
                        item {
                            AssistChip(
                                onClick = onOpenPaywall,
                                label = { Text("PRO", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.WorkspacePremium,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                shape = CircleShape,
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = Color(0xFFFFD700).copy(alpha = 0.12f)
                                )
                            )
                        }
                        item {
                            AssistChip(
                                onClick = { showShareCardDialog = true },
                                label = { Text("Share", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                leadingIcon = { Text("📸", fontSize = 13.sp) },
                                shape = CircleShape
                            )
                        }
                        item {
                            AssistChip(
                                onClick = { navController.navigate(Screen.MicroVlogs.route) },
                                label = { Text("Vlogs", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                leadingIcon = { Text("🎬", fontSize = 13.sp) },
                                shape = CircleShape
                            )
                        }
                        item {
                            AssistChip(
                                onClick = { navController.navigate(Screen.SkillMastery.route) },
                                label = { Text("Skills", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                leadingIcon = { Text("⏱️", fontSize = 13.sp) },
                                shape = CircleShape
                            )
                        }
                        item {
                            AssistChip(
                                onClick = { navController.navigate(Screen.Enterprise.route) },
                                label = { Text("Teams", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                leadingIcon = { Text("🏢", fontSize = 13.sp) },
                                shape = CircleShape
                            )
                        }
                        item {
                            AssistChip(
                                onClick = { navController.navigate(Screen.MemeStudio.route) },
                                label = { Text("Memes", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                leadingIcon = { Text("🎭", fontSize = 13.sp) },
                                shape = CircleShape
                            )
                        }
                    }
                }

                // Daily Progress Bar (Glassmorphic)
                item {
                    DailyProgressBar(
                        progress = if (uiState.totalTasks > 0) uiState.tasksCompleted.toFloat() / uiState.totalTasks else 0f,
                        tasksCompleted = uiState.tasksCompleted,
                        totalTasks = uiState.totalTasks
                    )
                }

                // 8-Dimension Spider Radar Wheel
                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🕸️", fontSize = 16.sp)
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        "8-Dimension Balance Wheel",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                TextButton(onClick = { navController.navigate(Screen.Dimensions.route) }) {
                                    Text("Breakdown", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            DimensionRadarChart(
                                dimensionScores = uiState.dimensionScores,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                            )
                        }
                    }
                }

                // 8 Dimensions Horizontal Chips
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Life Dimensions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { navController.navigate(Screen.Dimensions.route) }) {
                            Text("See All", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 4.dp)
                    ) {
                        items(uiState.dimensions) { dimension ->
                            val score = uiState.dimensionScores[dimension] ?: 50
                            DimensionChipCard(
                                dimension = dimension,
                                score = score,
                                onClick = { navController.navigate(Screen.Dimensions.route) }
                            )
                        }
                    }
                }

                // Retentive Cards: Hard Mode & Finch Guardian
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isHardModeEnabled) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showHardModeSheet = true }
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("💀", fontSize = 22.sp)
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text("Hard Mode", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(
                                        if (isHardModeEnabled) "Active (-50 XP)" else "Tap to Enable",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showGuardianDialog = true }
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("🎁", fontSize = 22.sp)
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text("Guardian Gift", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(
                                        "Sponsor a Hero",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Today's Quests & Habits
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⚡", fontSize = 16.sp)
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Today's Dimension Quests",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        TextButton(onClick = { navController.navigate(Screen.Tasks.route) }) {
                            Text("Manage All", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                items(uiState.todayTasks) { task ->
                    TaskCard(
                        task = task,
                        onToggle = { viewModel.onToggleTask(task) }
                    )
                }

                item {
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }

    // Dialogs
    if (showCharacterSheet) {
        CharacterSheetDialog(
            userProfile = UserProfile(
                currentXp = uiState.currentXp,
                currentLevel = uiState.level,
                currentStreakDays = uiState.streak,
                title = uiState.userTitle
            ),
            archetype = HeroArchetype.WARRIOR,
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
        ShareScoreCardDialog(
            data = ShareCardData(
                userName = uiState.userName,
                score = uiState.totalScore,
                level = uiState.level,
                streak = uiState.streak,
                title = uiState.userTitle,
                dimensionScores = uiState.dimensionScores
            ),
            onDismiss = { showShareCardDialog = false }
        )
    }
}
