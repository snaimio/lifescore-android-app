package com.lifescore.app.presentation.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.LifeScoreShapes
import com.lifescore.app.core.designsystem.Space
import com.lifescore.app.core.designsystem.components.CardVariant
import com.lifescore.app.core.designsystem.components.LifeCard
import com.lifescore.app.core.designsystem.components.LifeIcon
import com.lifescore.app.core.designsystem.components.LifeIcons
import com.lifescore.app.core.designsystem.components.LifeIllustration
import com.lifescore.app.core.designsystem.components.LifeIllustrations
import com.lifescore.app.domain.model.DimensionType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FirstQuestScreen(
    questTitle: String,
    dimension: DimensionType,
    onCompleteQuest: () -> Unit,
    onSkip: () -> Unit
) {
    var isCompleted by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val checkScale by animateFloatAsState(
        targetValue = if (isCompleted) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "checkScale"
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Space.screenH, vertical = Space.lg)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(Modifier.height(Space.sm))

                Surface(
                    shape = CircleShape,
                    color = if (isCompleted) Color(0x206BA89C) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier
                        .size(96.dp)
                        .scale(checkScale)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isCompleted) {
                            LifeIllustration(LifeIllustrations.Celebration, size = 64.dp)
                        } else {
                            LifeIllustration(LifeIllustrations.EmptyHabits, size = 64.dp)
                        }
                    }
                }

                Spacer(Modifier.height(Space.md))

                Text(
                    if (isCompleted) "Nice. First Win Locked In." else "Your First Win",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(Space.xs))

                Text(
                    if (isCompleted) "You showed up. That's the hard part." else "Momentum starts with a single completed action.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(Space.lg))

                // Interactive First Habit Card
                LifeCard(
                    modifier = Modifier.fillMaxWidth(),
                    variant = if (isCompleted) CardVariant.Cream else CardVariant.Primary
                ) {
                    Column(
                        modifier = Modifier.padding(Space.md),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Space.xs)
                        ) {
                            LifeIcon(LifeIcons.forDimension(dimension), size = 16.dp)
                            Text(
                                text = "${dimension.displayName} • +50 XP",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(Modifier.height(Space.sm))

                        Text(
                            text = questTitle,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(Modifier.height(Space.sm))

                        Text(
                            text = if (isCompleted) "Day 1 streak activated. Ready to architect your life." else "Complete this simple micro-habit right now to bank your first 50 XP.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(Space.lg))

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Space.xs),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isCompleted) {
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            isCompleted = true
                            onCompleteQuest()
                            scope.launch {
                                delay(600)
                                onSkip()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = LifeScoreShapes.button,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        LifeIcon(LifeIcons.Check, size = 18.dp, tint = MaterialTheme.colorScheme.onPrimary)
                        Spacer(Modifier.width(Space.xs))
                        Text("Tap to Complete (+50 XP)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }

                    TextButton(onClick = onSkip) {
                        Text(
                            "Skip to Dashboard →",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Button(
                        onClick = onSkip,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = LifeScoreShapes.button
                    ) {
                        Text(
                            "Enter Day 1 Dashboard →",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
