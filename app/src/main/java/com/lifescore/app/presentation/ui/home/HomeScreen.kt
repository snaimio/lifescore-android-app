package com.lifescore.app.presentation.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lifescore.app.core.designsystem.*
import com.lifescore.app.core.designsystem.components.*
import com.lifescore.app.core.engine.FeatureUnlockManager
import com.lifescore.app.core.engine.UserPhase
import com.lifescore.app.core.util.ShareCardData
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.HeroArchetype
import com.lifescore.app.domain.model.LifeTask
import com.lifescore.app.domain.model.UserProfile
import com.lifescore.app.presentation.navigation.Screen
import com.lifescore.app.presentation.ui.components.CharacterSheetDialog
import com.lifescore.app.presentation.ui.components.FeatureUnlockBanner
import com.lifescore.app.presentation.ui.home.components.DimensionRadarChart
import com.lifescore.app.presentation.ui.share.ShareScoreCardDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel,
    onOpenPaywall: () -> Unit,
    onOpenDrawer: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var showCharacterSheet by remember { mutableStateOf(false) }
    var showShareCardDialog by remember { mutableStateOf(false) }
    var isStatsExpanded by remember { mutableStateOf(false) }

    val maxVisibleQuests = when (uiState.userPhase) {
        UserPhase.NEW_USER -> 3
        UserPhase.EXPLORING -> 5
        UserPhase.ADVANCED, UserPhase.EXPERT -> 8
    }

    val visibleQuests = remember(uiState.todayTasks, maxVisibleQuests) {
        uiState.todayTasks.take(maxVisibleQuests)
    }

    val unlockedCount = remember(uiState.userPhase) {
        FeatureUnlockManager.getUnlockedFeatures(uiState.userPhase).size
    }
    val totalFeaturesCount = FeatureUnlockManager.allFeatures.size

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Open Navigation Menu",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
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
                    IconButton(onClick = { navController.navigate(Screen.Explore.route) }) {
                        Icon(
                            Icons.Default.Explore,
                            contentDescription = "Explore Directory",
                            tint = MaterialTheme.colorScheme.primary
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
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                // Tier Unlock Banner (Progressive Milestone)
                item {
                    FeatureUnlockBanner(
                        phase = uiState.userPhase,
                        onExploreClick = { navController.navigate(Screen.Explore.route) }
                    )
                }

                // ==========================================
                // 1. GREETING & LIFESCORE HERO CARD
                // ==========================================
                item {
                    HeroCard(
                        score = uiState.totalScore,
                        level = uiState.level,
                        currentXp = uiState.currentXp,
                        xpToNextLevel = 1000,
                        streak = uiState.streak,
                        userName = uiState.userName,
                        onShare = { showShareCardDialog = true },
                        onLeaderboard = { navController.navigate(Screen.LeagueTiers.route) }
                    )
                }

                // ==========================================
                // 2. TODAY'S QUESTS (MAX 3 FOR NEW USERS)
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
                                    Text("📋", fontSize = 18.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Today's Quests",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "${uiState.tasksCompleted} of ${uiState.todayTasks.size} Done",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(Spacing.sm))

                            if (visibleQuests.isEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(Spacing.md),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("🎉", fontSize = 28.sp)
                                        Spacer(Modifier.height(4.dp))
                                        Text("All daily quests completed!", fontWeight = FontWeight.Bold)
                                        Text("You've built compounding momentum today.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            } else {
                                visibleQuests.forEach { task ->
                                    DailyQuestRow(
                                        task = task,
                                        onToggle = { viewModel.onToggleTask(task) }
                                    )
                                    Spacer(Modifier.height(Spacing.xs))
                                }
                            }

                            Spacer(Modifier.height(Spacing.xs))

                            TextButton(
                                onClick = { navController.navigate(Screen.Tasks.route) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    if (uiState.todayTasks.size > visibleQuests.size) "View All Quests (${uiState.todayTasks.size}) →" else "Manage Quests →",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // 3. QUICK STATS (COLLAPSIBLE / EXPANDABLE)
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
                                    Text("📊", fontSize = 18.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Life Dimensions",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                TextButton(
                                    onClick = { isStatsExpanded = !isStatsExpanded }
                                ) {
                                    Text(
                                        if (isStatsExpanded) "Collapse" else "Tap to see all 8",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(Modifier.height(Spacing.xs))

                            // Show top 2 dimensions by default
                            val dimensionEntries = uiState.dimensionScores.entries.toList()
                            val displayedEntries = if (isStatsExpanded) dimensionEntries else dimensionEntries.take(2)

                            displayedEntries.forEach { (dim, score) ->
                                DimensionScoreRow(dimension = dim, score = score)
                                Spacer(Modifier.height(Spacing.xs))
                            }

                            // Expandable Radar Chart if fully expanded
                            AnimatedVisibility(
                                visible = isStatsExpanded,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column {
                                    Spacer(Modifier.height(Spacing.sm))
                                    DimensionRadarChart(
                                        dimensionScores = uiState.dimensionScores,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                    )
                                    Spacer(Modifier.height(Spacing.xs))
                                    TextButton(
                                        onClick = { navController.navigate(Screen.Dimensions.route) },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Open Full 360° Analytics →", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 4. AI COACH GUIDANCE CARD
                // ==========================================
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(Screen.AICoach.route) }
                    ) {
                        Row(
                            modifier = Modifier.padding(Spacing.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🤖", fontSize = 24.sp)
                                }
                            }
                            Spacer(Modifier.width(Spacing.md))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Need guidance? Ask AI Coach",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Get tailored daily strategy & habit friction advice.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Chat",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // ==========================================
                // 5. EXPLORE DIRECTORY & QUICK ACTIONS
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
                                    Text("🌟", fontSize = 18.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Explore LifeScore OS",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                ) {
                                    Text(
                                        text = "$unlockedCount / $totalFeaturesCount Unlocked",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(Spacing.sm))

                            // Quick context chips
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                item {
                                    FilterChip(
                                        selected = false,
                                        onClick = { navController.navigate(Screen.Hydration.route) },
                                        label = { Text("💧 Water") }
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = false,
                                        onClick = { navController.navigate(Screen.BookLibrary.route) },
                                        label = { Text("📖 Books") }
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = false,
                                        onClick = { navController.navigate(Screen.ScreenTime.route) },
                                        label = { Text("⏳ Screen Time") }
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = false,
                                        onClick = { navController.navigate(Screen.MoodTracker.route) },
                                        label = { Text("🎭 Mood") }
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = false,
                                        onClick = { navController.navigate(Screen.StreakVault.route) },
                                        label = { Text("🛡️ Freeze Vault") }
                                    )
                                }
                            }

                            Spacer(Modifier.height(Spacing.xs))

                            Button(
                                onClick = { navController.navigate(Screen.Explore.route) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Open Feature Directory ($totalFeaturesCount Features) →", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(Spacing.xl))
                }
            }
        }
    }

    if (showShareCardDialog) {
        val shareData = remember(uiState) {
            ShareCardData(
                userName = uiState.userName,
                score = uiState.totalScore,
                level = uiState.level,
                streak = uiState.streak,
                title = uiState.userTitle,
                dimensionScores = uiState.dimensionScores
            )
        }
        ShareScoreCardDialog(
            data = shareData,
            onDismiss = { showShareCardDialog = false }
        )
    }

    if (showCharacterSheet) {
        CharacterSheetDialog(
            userProfile = UserProfile(
                name = uiState.userName,
                currentLevel = uiState.level,
                currentXp = uiState.currentXp,
                currentStreakDays = uiState.streak,
                title = uiState.userTitle
            ),
            onDismiss = { showCharacterSheet = false }
        )
    }
}

@Composable
private fun DailyQuestRow(
    task: LifeTask,
    onToggle: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (task.isCompleted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(
            1.dp,
            if (task.isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() }
            )
            Spacer(Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${task.dimension.displayName} • +${task.pointsReward} XP",
                    fontSize = 11.sp,
                    color = Color(task.dimension.baseColorHex)
                )
            }
            if (task.isCompleted) {
                Text("✅", fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun DimensionScoreRow(
    dimension: DimensionType,
    score: Int
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(dimension.displayName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text("$score%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(dimension.baseColorHex))
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { score.toFloat() / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = Color(dimension.baseColorHex),
            trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    }
}
