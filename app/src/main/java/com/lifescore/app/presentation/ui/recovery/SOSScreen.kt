package com.lifescore.app.presentation.ui.recovery

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.core.designsystem.components.GradientButton
import com.lifescore.app.core.designsystem.components.GradientCard
import com.lifescore.app.core.engine.CrisisContact
import com.lifescore.app.core.engine.RecoveryEngine
import kotlinx.coroutines.delay

enum class BreathingPhase(val label: String, val instruction: String, val durationSecs: Int, val targetScale: Float, val color: Color) {
    INHALE("Inhale (4s)", "Breathe in deeply through your nose...", 4, 1.4f, Color(0xFF06B6D4)),
    HOLD("Hold (7s)", "Hold your breath gently. Relax your shoulders...", 7, 1.4f, Color(0xFFF59E0B)),
    EXHALE("Exhale (8s)", "Exhale completely through your mouth...", 8, 1.0f, Color(0xFF10B981))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SOSScreen(
    engine: RecoveryEngine = remember { RecoveryEngine() },
    onBack: () -> Unit,
    onOpenAICoach: () -> Unit
) {
    val context = LocalContext.current
    val distractions = remember { engine.getDistractionActivities() }
    var currentDistractionIndex by remember { mutableIntStateOf(0) }

    // 15-Minute Countdown Timer State
    var timerRunning by remember { mutableStateOf(false) }
    var secondsRemaining by remember { mutableIntStateOf(15 * 60) }

    LaunchedEffect(timerRunning) {
        while (timerRunning && secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining -= 1
        }
        if (secondsRemaining <= 0) {
            timerRunning = false
        }
    }

    // 4-7-8 Breathing State
    var isBreathingActive by remember { mutableStateOf(false) }
    var currentPhase by remember { mutableStateOf(BreathingPhase.INHALE) }
    var phaseSecondsRemaining by remember { mutableIntStateOf(BreathingPhase.INHALE.durationSecs) }
    var completedBreathingCycles by remember { mutableIntStateOf(0) }

    LaunchedEffect(isBreathingActive, currentPhase) {
        if (!isBreathingActive) return@LaunchedEffect
        phaseSecondsRemaining = currentPhase.durationSecs
        while (phaseSecondsRemaining > 0 && isBreathingActive) {
            delay(1000L)
            phaseSecondsRemaining -= 1
        }
        if (isBreathingActive) {
            when (currentPhase) {
                BreathingPhase.INHALE -> currentPhase = BreathingPhase.HOLD
                BreathingPhase.HOLD -> currentPhase = BreathingPhase.EXHALE
                BreathingPhase.EXHALE -> {
                    completedBreathingCycles += 1
                    currentPhase = BreathingPhase.INHALE
                }
            }
        }
    }

    val animatedBreathingScale by animateFloatAsState(
        targetValue = if (isBreathingActive) currentPhase.targetScale else 1.0f,
        animationSpec = tween(
            durationMillis = if (isBreathingActive) currentPhase.durationSecs * 1000 else 400,
            easing = LinearEasing
        ),
        label = "breathing_scale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🆘", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("Emergency Craving Support", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            // 1. Empathy & Urge Surfing Card
            item {
                GradientCard(
                    gradient = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFEF4444),
                            Color(0xFFDC2626),
                            Color(0xFF991B1B)
                        )
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "You Are Stronger Than This Urge",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(Spacing.xs))
                        Text(
                            text = "Cravings are intense chemical waves that peak at 10-15 minutes and naturally subside. You do not have to fight it—just surf it minute by minute.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(Spacing.md))

                        // 15-Minute Countdown Display
                        val mins = secondsRemaining / 60
                        val secs = secondsRemaining % 60

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Black.copy(alpha = 0.25f)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.sm)
                            ) {
                                Text(
                                    text = String.format("%02d:%02d", mins, secs),
                                    fontSize = 44.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = if (timerRunning) "Surfing the wave..." else "15-Minute Craving Delay Timer",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Spacer(Modifier.height(Spacing.md))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    if (secondsRemaining <= 0) secondsRemaining = 15 * 60
                                    timerRunning = !timerRunning
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color(0xFFDC2626)
                                )
                            ) {
                                Text(if (timerRunning) "⏸️ Pause Timer" else "⏱️ Start 15-Min Urge Surfer", fontWeight = FontWeight.Bold)
                            }

                            if (timerRunning || secondsRemaining < 15 * 60) {
                                TextButton(
                                    onClick = {
                                        timerRunning = false
                                        secondsRemaining = 15 * 60
                                    },
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                                ) {
                                    Text("Reset")
                                }
                            }
                        }
                    }
                }
            }

            // 2. Interactive 4-7-8 Breathing Pacer
            item {
                GlassCard {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🌬️", fontSize = 20.sp)
                                Spacer(Modifier.width(Spacing.xs))
                                Column {
                                    Text(
                                        text = "4-7-8 Grounding Breathing",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Calms autonomic nervous system instantly",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (completedBreathingCycles > 0) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF10B981).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "$completedBreathingCycles cycles done",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(Spacing.lg))

                        // Visual Breathing Animated Circle
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(170.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .scale(animatedBreathingScale)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                currentPhase.color.copy(alpha = 0.85f),
                                                currentPhase.color.copy(alpha = 0.35f)
                                            )
                                        )
                                    )
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isBreathingActive) "$phaseSecondsRemaining" else "4-7-8",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isBreathingActive) currentPhase.label else "Ready",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(Modifier.height(Spacing.sm))

                        Text(
                            text = if (isBreathingActive) currentPhase.instruction else "Tap below to begin synchronized 4-7-8 breathwork.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(Spacing.md))

                        Button(
                            onClick = { isBreathingActive = !isBreathingActive },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBreathingActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isBreathingActive) "⏹️ Stop Breathing Guide" else "▶️ Start Guided 4-7-8 Breathing")
                        }
                    }
                }
            }

            // 3. Instant Distraction Engine
            item {
                GlassCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🎯", fontSize = 20.sp)
                                Spacer(Modifier.width(Spacing.xs))
                                Text(
                                    text = "Instant Craving Distraction",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(
                                onClick = {
                                    currentDistractionIndex = (currentDistractionIndex + 1) % distractions.size
                                }
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Shuffle", tint = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Spacer(Modifier.height(Spacing.xs))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(Spacing.md),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = distractions[currentDistractionIndex],
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(Modifier.height(Spacing.sm))

                        OutlinedButton(
                            onClick = {
                                currentDistractionIndex = (currentDistractionIndex + 1) % distractions.size
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🎲 Give Me Another Distraction")
                        }
                    }
                }
            }

            // 4. Gemini AI Recovery Coach Shortcut
            item {
                GlassCard(
                    modifier = Modifier.clickable { onOpenAICoach() }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🤖", fontSize = 22.sp)
                            }
                        }
                        Spacer(Modifier.width(Spacing.sm))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Talk to Gemini Recovery Coach",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Compassionate, non-judgmental support available 24/7",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // 5. 24/7 Crisis Helplines & Contacts
            item {
                Text(
                    text = "📞 24/7 Free & Confidential Helplines",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = Spacing.xs)
                )
            }

            items(engine.getCrisisResources().size) { index ->
                val contact = engine.getCrisisResources()[index]
                CrisisContactCard(
                    contact = contact,
                    onCall = { phone ->
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                        context.startActivity(intent)
                    },
                    onText = { number, body ->
                        val uri = Uri.parse("smsto:$number")
                        val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                            if (body != null) putExtra("sms_body", body)
                        }
                        context.startActivity(intent)
                    },
                    onOpenWeb = { url ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }
                )
            }

            item {
                Spacer(Modifier.height(Spacing.lg))
            }
        }
    }
}

@Composable
fun CrisisContactCard(
    contact: CrisisContact,
    onCall: (String) -> Unit,
    onText: (String, String?) -> Unit,
    onOpenWeb: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(Spacing.sm)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = contact.title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = contact.availability,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = contact.description,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(Spacing.xs))
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                contact.phone?.let { phone ->
                    FilledTonalButton(
                        onClick = { onCall(phone) },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Call $phone", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                contact.textNumber?.let { textNum ->
                    OutlinedButton(
                        onClick = { onText(textNum, contact.textBody) },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(if (contact.textBody != null) "Text '${contact.textBody}' to $textNum" else "Text $textNum", fontSize = 11.sp)
                    }
                }
                contact.website?.let { url ->
                    TextButton(
                        onClick = { onOpenWeb(url) },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Website ↗", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
