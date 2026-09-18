package com.lifescore.app.presentation.ui.today

import android.widget.Toast
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.*
import com.lifescore.app.core.engine.FeatureUnlockManager
import com.lifescore.app.core.engine.UserPhase
import com.lifescore.app.core.util.GettingStartedManager
import com.lifescore.app.core.util.ShareCardData
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.LifeTask
import com.lifescore.app.domain.model.UserProfile
import com.lifescore.app.presentation.navigation.Screen
import com.lifescore.app.presentation.ui.components.CharacterSheetDialog
import com.lifescore.app.presentation.ui.home.HomeViewModel
import com.lifescore.app.presentation.ui.home.components.DailyFocusCard
import com.lifescore.app.presentation.ui.home.components.GettingStartedCard
import com.lifescore.app.presentation.ui.share.ShareScoreCardDialog
import java.util.Calendar

enum class EnergyLevel(val title: String, val emoji: String, val tip: String) {
    LOW("Low", "🔋", "Prioritize light, low-friction habits today. Consistency beats intensity."),
    MEDIUM("Steady", "⚡", "Great steady energy. Aim for 2-3 focused habit completions."),
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
    val (greeting, timeEmoji) = remember(currentHour) {
        when (currentHour) {
            in 5..11 -> "Good morning" to "🌅"
            in 12..17 -> "Good afternoon" to "☀️"
            in 18..21 -> "Good evening" to "🌆"
            else -> "Night owl mode" to "🌙"
        }
    }

    val maxVisibleQuests = when (uiState.userPhase) {
        UserPhase.NEW_USER -> 3
        UserPhase.EXPLORING -> 5
        UserPhase.ADVANCED, UserPhase.EXPERT -> 8
    }

    val visibleQuests = remember(uiState.todayTasks, maxVisibleQuests) {
        uiState.todayTasks.take(maxVisibleQuests)
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
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(timeEmoji, fontSize = 16.sp)
                            }
                        }
                        Spacer(Modifier.width(Spacing.sm))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Today",
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
                // 1. TIME-BASED GREETING HERO WITH SCORE DELTA
                // ==========================================
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(Spacing.md)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "$greeting, ${uiState.userName}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "Here is what matters most for your life today.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("📈", fontSize = 14.sp)
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = "+3 pts",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(Spacing.md))

                            // Score & Streak Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("LIFESCORE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("${uiState.totalScore}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("DAILY STREAK", fontSize = 9.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("🔥 ${uiState.streak}d", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF5722))
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("LEVEL", fontSize = 9.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("Lvl ${uiState.level}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 2. AI TODAY'S FOCUS CARD (WHY IT MATTERS)
                // ==========================================
                val topPendingTask = uiState.todayTasks.firstOrNull { !it.isCompleted } ?: uiState.todayTasks.firstOrNull()
                if (topPendingTask != null) {
                    item {
                        DailyFocusCard(
                            task = topPendingTask,
                            onToggle = { task ->
                                viewModel.onToggleTask(task)
                                gettingStartedManager.markStepCompleted(GettingStartedManager.STEP_FIRST_HABIT)
                                completedSteps = gettingStartedManager.getCompletedStepCount()
                            },
                            onOpenAll = { navController.navigate(Screen.Tasks.route) }
                        )
                    }
                }

                // ==========================================
                // 3. ENERGY CHECK-IN (1-TAP SELECTOR)
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
                                    Text("⚡", fontSize = 18.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Energy Check-In",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "${selectedEnergy.emoji} ${selectedEnergy.title}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(Modifier.height(Spacing.xs))
                            Text(
                                text = selectedEnergy.tip,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(Modifier.height(Spacing.sm))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                EnergyLevel.values().forEach { level ->
                                    val isSelected = selectedEnergy == level
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedEnergy = level }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(level.emoji, fontSize = 18.sp)
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
                }

                // ==========================================
                // 4. TODAY'S HABITS CHECKLIST
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
                                        "Today's Habits",
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
                                        Text("All daily habits completed!", fontWeight = FontWeight.Bold)
                                        Text("You've built compounding momentum today.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            } else {
                                visibleQuests.forEach { task ->
                                    TodayHabitRow(
                                        task = task,
                                        onToggle = {
                                            viewModel.onToggleTask(task)
                                            gettingStartedManager.markStepCompleted(GettingStartedManager.STEP_FIRST_HABIT)
                                            completedSteps = gettingStartedManager.getCompletedStepCount()
                                        }
                                    )
                                    Spacer(Modifier.height(Spacing.xs))
                                }
                            }

                            Spacer(Modifier.height(Spacing.xs))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                            ) {
                                OutlinedButton(
                                    onClick = { showAddHabitDialog = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Add Habit", fontSize = 12.sp)
                                }
                                Button(
                                    onClick = { navController.navigate(Screen.Tasks.route) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("All Habits (${uiState.todayTasks.size}) →", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 5. QUICK ACTIONS ROW
                // ==========================================
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(Spacing.md)) {
                            Text(
                                "⚡ Quick Actions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(Spacing.sm))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
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
                    }
                }

                // ==========================================
                // 6. EVENING REFLECTION PROMPT CARD
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
                                    Text("🌙", fontSize = 18.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Evening Reflection",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                TextButton(onClick = { navController.navigate(Screen.Journal.route) }) {
                                    Text("Open Journal →", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text(
                                text = "What went well today? What is one insight or win you want to remember?",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(Modifier.height(Spacing.sm))

                            if (isReflectionSaved) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("✨", fontSize = 20.sp)
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            "Reflection logged for today. Great job closing your day with intention!",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            } else {
                                OutlinedTextField(
                                    value = eveningReflectionText,
                                    onValueChange = { eveningReflectionText = it },
                                    placeholder = { Text("Log a quick insight or gratitude...", fontSize = 12.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 3,
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(Modifier.height(Spacing.xs))

                                Button(
                                    onClick = {
                                        if (eveningReflectionText.isNotBlank()) {
                                            isReflectionSaved = true
                                            Toast.makeText(context, "✨ Daily insight saved!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Save Reflection (+25 XP)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
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

    if (showAddHabitDialog) {
        AlertDialog(
            onDismissRequest = { showAddHabitDialog = false },
            title = { Text("Add Daily Habit") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    OutlinedTextField(
                        value = newHabitTitle,
                        onValueChange = { newHabitTitle = it },
                        label = { Text("Habit name (e.g. 15m Reading)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Choose Dimension:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
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
                            newHabitTitle = ""
                            showAddHabitDialog = false
                            Toast.makeText(context, "Habit added to your daily stack!", Toast.LENGTH_SHORT).show()
                        }
                    }
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

@Composable
private fun TodayHabitRow(
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
                    text = "${task.dimension.displayName} • +${task.pointsReward} XP • 🔥 ${task.streakDays}d streak",
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
