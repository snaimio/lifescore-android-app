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

enum class EnergyLevel(val title: String, val icon: LifeIcons, val tip: String) {
    LOW("Low", LifeIcons.Sleep, "Prioritize light, low-friction habits today. Consistency beats intensity."),
    STEADY("Steady", LifeIcons.Energy, "Great steady energy. Aim for 2-3 focused habit completions."),
    HIGH("High", LifeIcons.Rocket, "High momentum! Perfect time to tackle your primary focus task."),
    PEAK("Peak Flow", LifeIcons.Star, "You're at peak performance! Great for deep work and breakthroughs.")
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
                                LifeIcon(
                                    icon = LifeIcons.Profile,
                                    size = 18.dp,
                                    tint = MaterialTheme.colorScheme.primary
                                )
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
                                    LifeIcon(
                                        icon = LifeIcons.Goal,
                                        size = 16.dp
                                    )
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
                                text = "${uiState.userTitle.ifBlank { "Architect" }}, focusing on ${topPendingTask.dimension.displayName} builds compounding momentum today (+${topPendingTask.pointsReward} XP).",
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
                                    text = if (topPendingTask.isCompleted) "Completed" else "Mark Complete (+${topPendingTask.pointsReward} XP)",
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
                                LifeIcon(
                                    icon = LifeIcons.Energy,
                                    size = 16.dp
                                )
                                Spacer(Modifier.width(Space.xs))
                                Text(
                                    "Energy Check-In",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LifeIcon(
                                    icon = selectedEnergy.icon,
                                    size = 14.dp
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = selectedEnergy.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
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
                                        LifeIcon(
                                            icon = level.icon,
                                            size = 18.dp
                                        )
                                        Spacer(Modifier.height(4.dp))
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
                // 4. TODAY'S HABIT QUESTS (2x2 GRID)
                // ==========================================
                item {
                    SectionHeader(
                        title = "Daily Habit Quests",
                        subtitle = if (pendingCount > 0) "$pendingCount quests remaining today" else "All daily quests completed!",
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
                    item {
                        val chunkedQuests = visibleQuests.chunked(2)
                        Column(verticalArrangement = Arrangement.spacedBy(Space.sm)) {
                            chunkedQuests.forEach { rowTasks ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(Space.sm)
                                ) {
                                    rowTasks.forEach { task ->
                                        val isCompleted = task.isCompleted
                                        val dimColor = Color(task.dimension.baseColorHex)
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(LifeScoreShapes.card)
                                                .clickable {
                                                    viewModel.onToggleTask(task)
                                                    gettingStartedManager.markStepCompleted(GettingStartedManager.STEP_FIRST_HABIT)
                                                    completedSteps = gettingStartedManager.getCompletedStepCount()
                                                },
                                            shape = LifeScoreShapes.card,
                                            color = if (isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f) else MaterialTheme.colorScheme.surface,
                                            border = BorderStroke(
                                                width = 1.dp,
                                                color = if (isCompleted) dimColor.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(Space.md),
                                                verticalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                // Category Tag
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Surface(
                                                        shape = LifeScoreShapes.pill,
                                                        color = dimColor.copy(alpha = 0.15f)
                                                    ) {
                                                        Text(
                                                            text = task.dimension.displayName,
                                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                            fontWeight = FontWeight.Bold,
                                                            color = dimColor,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }

                                                Spacer(Modifier.height(Space.sm))

                                                // Task Title
                                                Text(
                                                    text = task.title,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 2,
                                                    color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                                                )

                                                Spacer(Modifier.height(Space.xs))

                                                // Subtitle status
                                                Text(
                                                    text = if (isCompleted) "Completed" else "In Progress",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                    color = if (isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                                                )

                                                Spacer(Modifier.height(Space.sm))

                                                // Bottom Row: Checkmark & XP Badge
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = if (isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.surfaceVariant,
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            if (isCompleted) {
                                                                Icon(
                                                                    Icons.Default.Check,
                                                                    contentDescription = null,
                                                                    tint = Color.White,
                                                                    modifier = Modifier.size(14.dp)
                                                                )
                                                            }
                                                        }
                                                    }

                                                    Surface(
                                                        shape = LifeScoreShapes.pill,
                                                        color = Color(0xFFF59E0B).copy(alpha = 0.15f)
                                                    ) {
                                                        Text(
                                                            text = "+${task.pointsReward} XP",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFFF59E0B),
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (rowTasks.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
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
                                leadingIcon = { LifeIcon(LifeIcons.Reading, size = 16.dp) },
                                label = { Text("Ask Coach") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { navController.navigate(Screen.Hydration.route) },
                                leadingIcon = { LifeIcon(LifeIcons.Hydration, size = 16.dp) },
                                label = { Text("Log Water") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { navController.navigate(Screen.FocusTimer.route) },
                                leadingIcon = { LifeIcon(LifeIcons.Goal, size = 16.dp) },
                                label = { Text("Focus Timer") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { navController.navigate(Screen.EnergySchedule.route) },
                                leadingIcon = { LifeIcon(LifeIcons.Energy, size = 16.dp) },
                                label = { Text("Energy Curve") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { navController.navigate(Screen.MoodTracker.route) },
                                leadingIcon = { LifeIcon(LifeIcons.MoodHappy, size = 16.dp) },
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
                            subtitle = "How did today feel? Close your day with intention."
                        )
                    }

                    item {
                        LifeCard(variant = CardVariant.Default) {
                            if (isReflectionSaved) {
                                Row(
                                    modifier = Modifier.padding(Space.sm),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    LifeIcon(
                                        icon = LifeIcons.Star,
                                        size = 24.dp
                                    )
                                    Spacer(Modifier.width(Space.sm))
                                    Column {
                                        Text(
                                            "Reflection saved for today",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Text(
                                            "You closed your day with intention and clarity.",
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
                                            Toast.makeText(context, "Daily reflection saved (+25 XP)", Toast.LENGTH_SHORT).show()
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
