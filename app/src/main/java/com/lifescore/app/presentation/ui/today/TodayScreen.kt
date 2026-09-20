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
import androidx.compose.ui.graphics.luminance
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
import com.lifescore.app.presentation.navigation.Screen
import com.lifescore.app.presentation.ui.home.HomeViewModel
import com.lifescore.app.presentation.ui.home.components.GettingStartedCard
import com.lifescore.app.presentation.ui.share.ShareScoreCardDialog
import java.util.Calendar

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

    var showShareCardDialog by remember { mutableStateOf(false) }
    var showAddHabitDialog by remember { mutableStateOf(false) }
    var newHabitTitle by remember { mutableStateOf("") }
    var newHabitDimension by remember { mutableStateOf(DimensionType.HEALTH) }

    var eveningReflectionText by remember { mutableStateOf("") }
    var isReflectionSaved by remember { mutableStateOf(false) }

    val gettingStartedManager = remember { GettingStartedManager(context) }
    var completedSteps by remember { mutableStateOf(gettingStartedManager.getCompletedStepCount()) }

    val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val isEvening = currentHour >= 18

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val visibleTasks = remember(uiState.todayTasks) {
        val pending = uiState.todayTasks.filter { !it.isCompleted }
        val completed = uiState.todayTasks.filter { it.isCompleted }
        (pending + completed).take(4)
    }
    val pendingCount = uiState.todayTasks.count { !it.isCompleted }

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
                            .clickable { navController.navigate(Screen.Me.route) }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isDark) Color(0xFF1D1B2B) else Color(0xFFFFF3DB),
                            border = BorderStroke(1.dp, if (isDark) Color(0x50D4A24C) else Color(0xFFD4A24C)),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                LifeIcon(
                                    icon = LifeIcons.Profile,
                                    size = 18.dp,
                                    tint = Color(0xFFD4A24C)
                                )
                            }
                        }
                        Spacer(Modifier.width(Space.sm))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Today",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                uiState.userTitle.ifBlank { "Daily Habits & Life Index" },
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
                            tint = Color(0xFFD4A24C)
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
                // 1. LIFESCORE INDEX HERO CARD
                // ==========================================
                item {
                    LifeScoreHero(
                        score = uiState.totalScore,
                        level = uiState.level,
                        currentXp = uiState.currentXp,
                        xpToNextLevel = 1000,
                        streak = uiState.streak,
                        userName = uiState.userName,
                        onClick = { navController.navigate(Screen.Me.route) },
                        onShare = { showShareCardDialog = true }
                    )
                }

                // ==========================================
                // 2. TODAY'S PRIMARY FOCUS CARD
                // ==========================================
                val topPendingTask = uiState.todayTasks.firstOrNull { !it.isCompleted } ?: uiState.todayTasks.firstOrNull()
                if (topPendingTask != null) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(LifeScoreShapes.card)
                                .clickable {
                                    viewModel.onToggleTask(topPendingTask)
                                    gettingStartedManager.markStepCompleted(GettingStartedManager.STEP_FIRST_HABIT)
                                    completedSteps = gettingStartedManager.getCompletedStepCount()
                                },
                            shape = LifeScoreShapes.card,
                            color = if (isDark) Color(0xFF14131E) else DimensionPastels.backgroundFor(topPendingTask.dimension),
                            border = if (isDark) {
                                BorderStroke(
                                    1.dp,
                                    androidx.compose.ui.graphics.Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFD4A24C).copy(alpha = 0.4f),
                                            Color(0xFF2E2B3E).copy(alpha = 0.5f)
                                        )
                                    )
                                )
                            } else {
                                BorderStroke(1.dp, DimensionPastels.borderFor(topPendingTask.dimension))
                            }
                        ) {
                            Column(modifier = Modifier.padding(Space.cardH)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        LifeIcon(
                                            icon = LifeIcons.Goal,
                                            size = 16.dp,
                                            tint = Color(0xFFD4A24C)
                                        )
                                        Spacer(Modifier.width(Space.xs))
                                        Text(
                                            "Today's Keystone Focus",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFD4A24C),
                                            letterSpacing = 0.4.sp
                                        )
                                    }
                                    Surface(
                                        shape = LifeScoreShapes.pill,
                                        color = Color(topPendingTask.dimension.baseColorHex).copy(alpha = 0.18f),
                                        border = BorderStroke(0.5.dp, Color(topPendingTask.dimension.baseColorHex).copy(alpha = 0.4f))
                                    ) {
                                        Text(
                                            text = topPendingTask.dimension.displayName,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
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
                                    color = if (isDark) Color(0xFFFBF8F3) else Color(0xFF19181F)
                                )

                                Spacer(Modifier.height(Space.xxs))

                                Text(
                                    text = "Focusing on ${topPendingTask.dimension.displayName} builds compounding momentum today.",
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
                                        containerColor = if (topPendingTask.isCompleted) {
                                            if (isDark) Color(0xFF1D1B2B) else Color(0xFFEBE6DD)
                                        } else Color(0xFFD4A24C)
                                    )
                                ) {
                                    Text(
                                        text = if (topPendingTask.isCompleted) "Completed" else "Mark Complete",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (topPendingTask.isCompleted) {
                                            if (isDark) Color(0xFF9E9AA8) else Color(0xFF6B6678)
                                        } else Color(0xFF1B1408)
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 4. DAILY HABITS (CLEAN 2x2 CARD SHOWCASE)
                // ==========================================
                item {
                    SectionHeader(
                        title = "Daily Habits",
                        subtitle = if (pendingCount > 0) "$pendingCount habits remaining today" else "All daily habits completed!",
                        action = {
                            TextButton(onClick = { showAddHabitDialog = true }) {
                                Text("+ Add Habit", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFD4A24C))
                            }
                        }
                    )
                }

                if (visibleTasks.isEmpty()) {
                    item {
                        EmptyHabits(onAddHabit = { showAddHabitDialog = true })
                    }
                } else {
                    item {
                        val chunkedTasks = visibleTasks.chunked(2)
                        Column(verticalArrangement = Arrangement.spacedBy(Space.sm)) {
                            chunkedTasks.forEach { rowTasks ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(Space.sm)
                                ) {
                                    rowTasks.forEach { task ->
                                        val isCompleted = task.isCompleted
                                        val dimColor = Color(task.dimension.baseColorHex)
                                        val cardBg = if (isDark) {
                                            if (isCompleted) Color(0xFF12111A) else Color(0xFF151422)
                                        } else {
                                            if (isCompleted) Color(0xFFF0FDF4) else DimensionPastels.backgroundFor(task.dimension)
                                        }
                                        val cardBorder = if (isDark) {
                                            if (isCompleted) Color(0x3010B981) else Color(0x20D4A24C)
                                        } else {
                                            if (isCompleted) Color(0xFFBBF7D0) else DimensionPastels.borderFor(task.dimension)
                                        }

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
                                            color = cardBg,
                                            border = BorderStroke(
                                                width = 1.dp,
                                                color = cardBorder
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
                                                    color = if (isCompleted) {
                                                        if (isDark) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else Color(0xFF8C8696)
                                                    } else {
                                                        if (isDark) Color(0xFFFBF8F3) else Color(0xFF19181F)
                                                    }
                                                )

                                                Spacer(Modifier.height(Space.xs))

                                                // Subtitle status
                                                Text(
                                                    text = if (isCompleted) "Completed" else "In Progress",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                    color = if (isCompleted) {
                                                        if (isDark) Color(0xFF10B981) else Color(0xFF16A34A)
                                                    } else {
                                                        if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else dimColor
                                                    }
                                                )

                                                Spacer(Modifier.height(Space.sm))

                                                // Bottom Row: Checkmark
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = if (isCompleted) {
                                                            if (isDark) Color(0xFF10B981) else Color(0xFF16A34A)
                                                        } else {
                                                            if (isDark) Color(0xFF1D1B2B) else Color(0xFFFFFFFF)
                                                        },
                                                        border = if (!isCompleted) {
                                                            BorderStroke(1.dp, if (isDark) Color(0x30D4A24C) else dimColor.copy(alpha = 0.5f))
                                                        } else null,
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
                                onClick = { navController.navigate(Screen.MeditationLibrary.route) },
                                leadingIcon = { LifeIcon(LifeIcons.Meditation, size = 16.dp) },
                                label = { Text("Meditation") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { navController.navigate(Screen.DailyGrowth.route) },
                                leadingIcon = { LifeIcon(LifeIcons.Energy, size = 16.dp) },
                                label = { Text("Daily Growth") }
                            )
                        }
                    }
                }

                // ==========================================
                // 6. EVENING REFLECTION
                // 6. EVENING REFLECTION
                // ==========================================
                val hasSavedReflection = !uiState.todayReflection.isNullOrBlank() || isReflectionSaved
                if (isEvening || hasSavedReflection) {
                    item {
                        SectionHeader(
                            title = "Evening reflection",
                            subtitle = "How did today feel? Close your day with intention."
                        )
                    }

                    item {
                        LifeCard(variant = CardVariant.Default) {
                            if (hasSavedReflection) {
                                val savedText = uiState.todayReflection ?: eveningReflectionText
                                Column(modifier = Modifier.padding(Space.sm)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                                    if (savedText.isNotBlank()) {
                                        Spacer(Modifier.height(Space.xs))
                                        Text(
                                            text = "\"$savedText\"",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.primary
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
                                            viewModel.saveEveningReflection(eveningReflectionText)
                                            isReflectionSaved = true
                                            Toast.makeText(context, "Daily reflection saved to your journal!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = LifeScoreShapes.button
                                ) {
                                    Text("Save Reflection", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
}
