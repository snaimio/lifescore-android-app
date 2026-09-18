package com.lifescore.app.presentation.ui.today

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import com.lifescore.app.core.engine.UserPhase
import com.lifescore.app.core.util.GettingStartedManager
import com.lifescore.app.core.util.ShareCardData
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.LifeTask
import com.lifescore.app.domain.model.UserProfile
import com.lifescore.app.presentation.navigation.Screen
import com.lifescore.app.presentation.ui.components.CharacterSheetDialog
import com.lifescore.app.presentation.ui.home.HomeViewModel
import com.lifescore.app.presentation.ui.home.components.GettingStartedCard
import com.lifescore.app.presentation.ui.share.ShareScoreCardDialog
import java.util.Calendar

enum class EnergyLevel(val title: String, val emoji: String, val tip: String) {
    LOW("Low", "🔋", "Prioritize light, low-friction habits today. Consistency beats intensity."),
    STEADY("Steady", "⚡", "Great steady energy. Aim for 2-3 focused habit completions."),
    HIGH("High", "🚀", "High momentum! Perfect time to tackle your primary focus task."),
    PEAK("Peak Flow", "🌟", "You're at peak performance! Great for deep work and breakthroughs.")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    navController: NavController,
    viewModel: HomeViewModel,
    onOpenPaywall: () -> Unit = {},
    onOpenDrawer: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var showCharacterSheet by remember { mutableStateOf(false) }
    var showShareCardDialog by remember { mutableStateOf(false) }
    var showAddHabitDialog by remember { mutableStateOf(false) }
    var newHabitTitle by remember { mutableStateOf("") }
    var newHabitDimension by remember { mutableStateOf(DimensionType.HEALTH) }

    var selectedEnergy by remember { mutableStateOf(EnergyLevel.HIGH) }
    var eveningReflectionText by remember { mutableStateOf("") }
    var isReflectionSaved by remember { mutableStateOf(false) }

    val gettingStartedManager = remember { GettingStartedManager(context) }
    var completedSteps by remember { mutableStateOf(gettingStartedManager.getCompletedStepCount()) }

    val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val isEvening = currentHour >= 18

    val maxVisibleQuests = when (uiState.userPhase) {
        UserPhase.NEW_USER -> 3
        UserPhase.EXPLORING -> 5
        UserPhase.ADVANCED, UserPhase.EXPERT -> 8
    }

    val visibleQuests = remember(uiState.todayTasks, maxVisibleQuests) {
        uiState.todayTasks.take(maxVisibleQuests)
    }

    val pendingCount = remember(uiState.todayTasks) {
        uiState.todayTasks.count { !it.isCompleted }
    }

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
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("⚔️", fontSize = 16.sp)
                            }
                        }
                        Spacer(Modifier.width(Space.sm))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Today",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "${uiState.userTitle} • Lvl ${uiState.level}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.AICoach.route) }) {
                        Icon(
                            Icons.Default.Psychology,
                            contentDescription = "AI Coach",
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
                    .padding(horizontal = Space.screenH),
                verticalArrangement = Arrangement.spacedBy(Space.md)
            ) {
                Spacer(Modifier.height(Space.xs))
                SkeletonCard(height = 180.dp)
                SkeletonCard(height = 80.dp)
                SkeletonCard(height = 160.dp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(
                    start = Space.screenH,
                    end = Space.screenH,
                    top = Space.screenTop,
                    bottom = Space.xxxl
                ),
                verticalArrangement = Arrangement.spacedBy(Space.cardGap)
            ) {
                // 0. Getting Started Guide for New Users
                if (completedSteps < GettingStartedManager.TOTAL_STEPS) {
                    item {
                        GettingStartedCard(
                            completedSteps = completedSteps,
                            totalSteps = GettingStartedManager.TOTAL_STEPS,
                            onContinue = {
                                when (completedSteps) {
                                    0 -> navController.navigate(Screen.Tasks.route)
                                    1 -> {
                                        gettingStartedManager.markStepCompleted(GettingStartedManager.STEP_LIFE_MATRIX)
                                        completedSteps = gettingStartedManager.getCompletedStepCount()
                                        navController.navigate(Screen.Balance.route)
                                    }
                                    2 -> {
                                        gettingStartedManager.markStepCompleted(GettingStartedManager.STEP_AI_COACH)
                                        completedSteps = gettingStartedManager.getCompletedStepCount()
                                        navController.navigate(Screen.AICoach.route)
                                    }
                                    else -> navController.navigate(Screen.Explore.route)
                                }
                            }
                        )
                    }
                }

                // ==========================================
                // 1. SIGNATURE LIFESCORE HERO CARD
                // ==========================================
                item {
                    LifeScoreHero(
                        score = uiState.totalScore,
                        level = uiState.level,
                        currentXp = uiState.currentXp,
                        xpToNextLevel = 1000,
                        streak = uiState.streak,
                        userName = uiState.userName,
                        onClick = { showCharacterSheet = true },
                        onLeaderboard = { navController.navigate(Screen.LeagueTiers.route) },
                        onShare = { showShareCardDialog = true }
                    )
                }

                // ==========================================
                // 2. AI TODAY'S FOCUS CARD
                // ==========================================
                val topPendingTask = uiState.todayTasks.firstOrNull { !it.isCompleted } ?: uiState.todayTasks.firstOrNull()
                if (topPendingTask != null) {
                    item {
                        LifeCard(
                            variant = CardVariant.Cream,
                            onClick = {
                                viewModel.onToggleTask(topPendingTask)
                                gettingStartedManager.markStepCompleted(GettingStartedManager.STEP_FIRST_HABIT)
                                completedSteps = gettingStartedManager.getCompletedStepCount()
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🎯", fontSize = 16.sp)
                                    Spacer(Modifier.width(Space.xs))
                                    Text(
                                        "Today's Primary Focus",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Surface(
                                    shape = LifeScoreShapes.pill,
                                    color = Color(topPendingTask.dimension.baseColorHex).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = topPendingTask.dimension.displayName,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(topPendingTask.dimension.baseColorHex),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(Space.sm))

                            Text(
                                text = topPendingTask.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(Modifier.height(Space.xxs))

                            Text(
                                text = "Why it matters: Completing this builds compounding momentum for your ${topPendingTask.dimension.displayName} dimension and earns +${topPendingTask.pointsReward} XP.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(Modifier.height(Space.md))

                            Button(
                                onClick = {
                                    viewModel.onToggleTask(topPendingTask)
                                    gettingStartedManager.markStepCompleted(GettingStartedManager.STEP_FIRST_HABIT)
                                    completedSteps = gettingStartedManager.getCompletedStepCount()
                                },
                                shape = LifeScoreShapes.button,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (topPendingTask.isCompleted) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(
                                    text = if (topPendingTask.isCompleted) "Completed ✓" else "Mark Complete (+${topPendingTask.pointsReward} XP)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (topPendingTask.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // 3. ENERGY CHECK-IN (1-TAP SELECTOR)
                // ==========================================
                item {
                    LifeCard(variant = CardVariant.Default) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("⚡", fontSize = 16.sp)
                                Spacer(Modifier.width(Space.xs))
                                Text(
                                    "Energy Check-In",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "${selectedEnergy.emoji} ${selectedEnergy.title}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(Modifier.height(Space.xs))
                        Text(
                            text = selectedEnergy.tip,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(Space.md))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Space.xs)
                        ) {
                            EnergyLevel.values().forEach { level ->
                                val isSelected = selectedEnergy == level
                                Surface(
                                    shape = LifeScoreShapes.small,
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedEnergy = level }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = Space.sm),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(level.emoji, fontSize = 16.sp)
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            level.title,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 4. TODAY'S HABITS LIST
                // ==========================================
                item {
                    SectionHeader(
                        title = "Today's habits",
                        subtitle = if (pendingCount > 0) "$pendingCount remaining" else "All complete!",
                        action = {
                            TextButton(onClick = { showAddHabitDialog = true }) {
                                Text("+ Add Habit", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    )
                }

                if (visibleQuests.isEmpty()) {
                    item {
                        EmptyHabits(onAddHabit = { showAddHabitDialog = true })
                    }
                } else {
                    itemsIndexed(visibleQuests, key = { _, task -> task.id }) { index, task ->
                        StaggeredAppear(index = index) {
                            HabitRow(
                                task = task,
                                onComplete = {
                                    viewModel.onToggleTask(task)
                                    gettingStartedManager.markStepCompleted(GettingStartedManager.STEP_FIRST_HABIT)
                                    completedSteps = gettingStartedManager.getCompletedStepCount()
                                }
                            )
                        }
                    }
                }

                // ==========================================
                // 5. QUICK ACTIONS
                // ==========================================
                item {
                    SectionHeader(title = "Quick actions")
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Space.xs),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { navController.navigate(Screen.AICoach.route) },
                                leadingIcon = { Text("🤖") },
                                label = { Text("Ask Coach") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { navController.navigate(Screen.Hydration.route) },
                                leadingIcon = { Text("💧") },
                                label = { Text("Log Water") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { navController.navigate(Screen.FocusTimer.route) },
                                leadingIcon = { Text("⏱️") },
                                label = { Text("Focus Timer") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { navController.navigate(Screen.EnergySchedule.route) },
                                leadingIcon = { Text("⚡") },
                                label = { Text("Energy Curve") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { navController.navigate(Screen.MoodTracker.route) },
                                leadingIcon = { Text("🎭") },
                                label = { Text("Log Mood") }
                            )
                        }
                    }
                }

                // ==========================================
                // 6. EVENING REFLECTION
                // ==========================================
                if (isEvening || isReflectionSaved) {
                    item {
                        SectionHeader(
                            title = "Evening reflection",
                            subtitle = "Close your day with intentional gratitude"
                        )
                    }

                    item {
                        LifeCard(variant = CardVariant.Default) {
                            if (isReflectionSaved) {
                                Row(
                                    modifier = Modifier.padding(Space.sm),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("✨", fontSize = 24.sp)
                                    Spacer(Modifier.width(Space.sm))
                                    Column {
                                        Text(
                                            "Reflection saved for today",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Text(
                                            "Great job closing your day with intention and clarity.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = "What is one insight, win, or moment of gratitude from today?",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(Modifier.height(Space.sm))

                                OutlinedTextField(
                                    value = eveningReflectionText,
                                    onValueChange = { eveningReflectionText = it },
                                    placeholder = { Text("Log a quick reflection...", style = MaterialTheme.typography.bodySmall) },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 3,
                                    shape = LifeScoreShapes.input
                                )

                                Spacer(Modifier.height(Space.sm))

                                Button(
                                    onClick = {
                                        if (eveningReflectionText.isNotBlank()) {
                                            isReflectionSaved = true
                                            Toast.makeText(context, "✨ Daily reflection saved (+25 XP)", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = LifeScoreShapes.button
                                ) {
                                    Text("Save Reflection (+25 XP)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddHabitDialog) {
        AlertDialog(
            onDismissRequest = { showAddHabitDialog = false },
            title = {
                Text(
                    "Add Daily Habit",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Medium
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Space.sm)) {
                    OutlinedTextField(
                        value = newHabitTitle,
                        onValueChange = { newHabitTitle = it },
                        label = { Text("Habit name (e.g. 15m Morning Walk)") },
                        singleLine = true,
                        shape = LifeScoreShapes.input,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        "Dimension:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(Space.xxs)) {
                        items(DimensionType.values().size) { idx ->
                            val dim = DimensionType.values()[idx]
                            val isSel = newHabitDimension == dim
                            FilterChip(
                                selected = isSel,
                                onClick = { newHabitDimension = dim },
                                label = { Text(dim.displayName, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newHabitTitle.isNotBlank()) {
                            viewModel.onToggleTask(
                                LifeTask(
                                    title = newHabitTitle,
                                    dimension = newHabitDimension,
                                    pointsReward = 15
                                )
                            )
                            newHabitTitle = ""
                            showAddHabitDialog = false
                            Toast.makeText(context, "Habit added to your daily stack!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = LifeScoreShapes.button
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddHabitDialog = false }) {
                    Text("Cancel")
                }
            }
        )
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
