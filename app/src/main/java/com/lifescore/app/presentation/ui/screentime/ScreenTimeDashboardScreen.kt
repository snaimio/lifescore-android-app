package com.lifescore.app.presentation.ui.screentime

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lifescore.app.data.local.entity.ScreenTimeChallenge
import com.lifescore.app.data.repository.AppUsageItemModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenTimeDashboardScreen(
    viewModel: ScreenTimeViewModel,
    onBack: () -> Unit = {},
    onNavigateToMinimalist: () -> Unit = {},
    onNavigateToFocusTimer: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.userMessage) {
        state.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    if (state.showFrictionDialog) {
        IntentionalOpeningFrictionDialog(
            appName = state.targetAppOpening,
            countdownSeconds = state.frictionSecondsRemaining,
            onDismiss = { viewModel.dismissFrictionDialog(proceed = false) },
            onProceed = { viewModel.dismissFrictionDialog(proceed = true) }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "📱 Screen Time & Digital Wellness",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Reclaim focus • Opal & SweatPass OS",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToMinimalist) {
                        Text("🔲", fontSize = 20.sp)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Hero Card: Today's Usage & Effective Limit
            item {
                ScreenTimeHeroCard(
                    todayMinutes = state.todayMinutes,
                    dailyLimit = state.dailyLimitMinutes,
                    bonusMinutes = state.earnedBonusMinutes,
                    effectiveLimit = state.effectiveLimitMinutes,
                    progress = state.progress,
                    pickups = state.pickups,
                    onAdjustLimit = { viewModel.setDailyGoal(it) }
                )
            }

            // 2. Quick Stat Counters
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ScreenStatCard(
                        modifier = Modifier.weight(1f),
                        emoji = "📱",
                        value = "${state.pickups}",
                        label = "Pickups Today",
                        detail = "Avg 4m per check"
                    )
                    ScreenStatCard(
                        modifier = Modifier.weight(1f),
                        emoji = "💪",
                        value = "+${state.earnedBonusMinutes}m",
                        label = "Earned Movement",
                        detail = "SweatPass Active"
                    )
                    ScreenStatCard(
                        modifier = Modifier.weight(1f),
                        emoji = "🛡️",
                        value = if (state.isFocusModeEnabled) "ON" else "OFF",
                        label = "Deep Focus",
                        detail = "Opal Shield"
                    )
                }
            }

            // 3. Opal Deep Focus Shield & Mode Toggle
            item {
                OpalDeepFocusCard(
                    isFocusEnabled = state.isFocusModeEnabled,
                    onToggle = { viewModel.toggleFocusMode(it) },
                    onLaunchFocusSession = onNavigateToFocusTimer
                )
            }

            // 4. SweatPass: Earn Screen Time Through Movement
            item {
                SweatPassMovementCard(
                    onExerciseCompleted = { exercise, reps ->
                        viewModel.completeMovementExercise(exercise, reps)
                    }
                )
            }

            // 5. Intentional App Opening Simulator (Friction Screen)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "🛑 Intentional App Opening",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                                Text("Anti-Autopilot", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Adds a 10s mindful breathing pause before opening distracting apps.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.triggerIntentionalAppOpening("Instagram") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("📸 Test Instagram")
                            }
                            OutlinedButton(
                                onClick = { viewModel.triggerIntentionalAppOpening("TikTok") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("🎵 Test TikTok")
                            }
                        }
                    }
                }
            }

            // 6. Top App Usage Breakdown
            item {
                Text(
                    "📊 Top App Usage",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(state.topApps) { app ->
                AppUsageListItem(
                    app = app,
                    onAppClick = { viewModel.triggerIntentionalAppOpening(app.appName) }
                )
            }

            // 7. Minimalist Phone Launcher Mode Banner
            item {
                MinimalistLauncherBanner(onOpenMinimalist = onNavigateToMinimalist)
            }

            // 8. Active Digital Detox Challenges
            item {
                Text(
                    "🏆 Active Digital Detox Challenges",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(state.activeChallenges) { challenge ->
                ChallengeProgressCard(
                    challenge = challenge,
                    onAdvance = { viewModel.advanceChallenge(challenge) }
                )
            }

            item {
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ScreenTimeHeroCard(
    todayMinutes: Int,
    dailyLimit: Int,
    bonusMinutes: Int,
    effectiveLimit: Int,
    progress: Float,
    pickups: Int,
    onAdjustLimit: (Int) -> Unit
) {
    val isWarning = progress > 0.85f
    val bgGradient = if (isWarning) {
        Brush.linearGradient(listOf(Color(0xFFE53935), Color(0xFFD81B60)))
    } else {
        Brush.linearGradient(listOf(Color(0xFF4A148C), Color(0xFF1E88E5)))
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(bgGradient)
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "TODAY'S SCREEN TIME",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    val hours = todayMinutes / 60
                    val mins = todayMinutes % 60
                    Text(
                        text = "${hours}h ${mins}m",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Text(
                    "Daily Limit: ${effectiveLimit}m (${dailyLimit}m base + ${bonusMinutes}m bonus)",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(14.dp))

                // Progress Indicator
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = if (isWarning) Color(0xFFFFD54F) else Color(0xFF69F0AE),
                    trackColor = Color.White.copy(alpha = 0.25f)
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "📱 $pickups pickups today",
                        color = Color.White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        LimitPresetChip("90m", onClick = { onAdjustLimit(90) })
                        LimitPresetChip("120m", onClick = { onAdjustLimit(120) })
                        LimitPresetChip("180m", onClick = { onAdjustLimit(180) })
                    }
                }
            }
        }
    }
}

@Composable
fun LimitPresetChip(label: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.2f),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            label,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun ScreenStatCard(
    modifier: Modifier = Modifier,
    emoji: String,
    value: String,
    label: String,
    detail: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 22.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
            Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
            Text(
                detail,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 9.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun OpalDeepFocusCard(
    isFocusEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    onLaunchFocusSession: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFocusEnabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🛡️", fontSize = 24.sp)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            "Opal Deep Focus Shield",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            if (isFocusEnabled) "Distractions Blocked • Forest Growing" else "Strict Block Active during work hours",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(
                    checked = isFocusEnabled,
                    onCheckedChange = onToggle
                )
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onLaunchFocusSession,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Start 25-Min Forest Focus Block (+50 XP)")
            }
        }
    }
}

@Composable
fun SweatPassMovementCard(
    onExerciseCompleted: (String, Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💪", fontSize = 24.sp)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            "SweatPass Movement Converter",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            "Earn 1 min screen time per 5 reps",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Badge(containerColor = MaterialTheme.colorScheme.primary) {
                    Text("+XP Loop", color = Color.White)
                }
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExerciseActionChip(
                    modifier = Modifier.weight(1f),
                    emoji = "🏋️",
                    name = "15 Squats",
                    bonus = "+3 mins",
                    onClick = { onExerciseCompleted("Squats", 15) }
                )
                ExerciseActionChip(
                    modifier = Modifier.weight(1f),
                    emoji = "🤸",
                    name = "10 Pushups",
                    bonus = "+2 mins",
                    onClick = { onExerciseCompleted("Pushups", 10) }
                )
                ExerciseActionChip(
                    modifier = Modifier.weight(1f),
                    emoji = "🦘",
                    name = "25 Jacks",
                    bonus = "+5 mins",
                    onClick = { onExerciseCompleted("Jumping Jacks", 25) }
                )
            }
        }
    }
}

@Composable
fun ExerciseActionChip(
    modifier: Modifier = Modifier,
    emoji: String,
    name: String,
    bonus: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 20.sp)
            Spacer(Modifier.height(2.dp))
            Text(name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, fontSize = 11.sp)
            Text(bonus, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black, fontSize = 10.sp)
        }
    }
}

@Composable
fun AppUsageListItem(
    app: AppUsageItemModel,
    onAppClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onAppClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(app.iconEmoji, fontSize = 24.sp)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(app.appName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        app.category.name.replace("_", " "),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${app.minutes}m",
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (app.minutes > 30) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Friction Pacer",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun MinimalistLauncherBanner(onOpenMinimalist: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenMinimalist)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔲", fontSize = 28.sp)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "Minimalist Launcher Mode",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        "Text-only home screen • Eliminates autopilot taps",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
fun ChallengeProgressCard(
    challenge: ScreenTimeChallenge,
    onAdvance: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    challenge.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
                Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                    Text("+${challenge.xpReward} XP", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(8.dp))

            val prog = (challenge.currentDay.toFloat() / challenge.targetDays.toFloat()).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { prog },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Day ${challenge.currentDay} of ${challenge.targetDays}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FilledTonalButton(
                    onClick = onAdvance,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("Check-In Day", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun IntentionalOpeningFrictionDialog(
    appName: String,
    countdownSeconds: Int,
    onDismiss: () -> Unit,
    onProceed: () -> Unit
) {
    var secondsLeft by remember { mutableStateOf(countdownSeconds) }
    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🧘", fontSize = 48.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    "Pause & Breathe",
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Are you opening $appName with clear intention, or on autopilot?",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(20.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        if (secondsLeft > 0) "$secondsLeft" else "✓",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("I'll do something meaningful instead ✨")
                }

                if (secondsLeft == 0) {
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = onProceed) {
                        Text("Continue to $appName", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
