package com.lifescore.app.presentation.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard

data class FirstRunStep(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val actionText: String
)

@Composable
fun FirstRunGuideScreen(
    onComplete: () -> Unit
) {
    val steps = remember {
        listOf(
            FirstRunStep(
                emoji = "👋",
                title = "Welcome to LifeScore!",
                subtitle = "Your Life Operating System",
                description = "LifeScore helps you measure balance, build consistent daily habits, and reach peak potential across 8 life dimensions.",
                actionText = "Next Step →"
            ),
            FirstRunStep(
                emoji = "✅",
                title = "Step 1: Daily Habits",
                subtitle = "Small actions, compounding results",
                description = "Complete 1 to 3 micro-habits each day to gain XP, unlock rewards, and level up your character sheet.",
                actionText = "Next Step →"
            ),
            FirstRunStep(
                emoji = "📊",
                title = "Step 2: 360° Life Matrix",
                subtitle = "Visual balance across 8 dimensions",
                description = "Watch your radar chart expand and balance as you make steady progress in Health, Wealth, Career, and more.",
                actionText = "Next Step →"
            ),
            FirstRunStep(
                emoji = "🤖",
                title = "Step 3: Meet Your AI Coach",
                subtitle = "Personalized strategies & daily guidance",
                description = "Your Gemini AI Coach analyzes your weakest dimensions and provides friction-busting tactics tailored to your personality.",
                actionText = "Enter LifeScore 🚀"
            )
        )
    }

    var currentStepIndex by remember { mutableStateOf(0) }
    val currentStep = steps[currentStepIndex]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(Spacing.lg)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header / Progress dots
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.md),
                horizontalArrangement = Arrangement.Center
            ) {
                steps.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(6.dp)
                            .width(if (index == currentStepIndex) 28.dp else 8.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (index == currentStepIndex)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                    )
                }
            }

            // Main Content Card
            AnimatedContent(
                targetState = currentStepIndex,
                transitionSpec = {
                    (fadeIn() + slideInHorizontally { width -> if (targetState > initialState) width else -width })
                        .togetherWith(fadeOut() + slideOutHorizontally { width -> if (targetState > initialState) -width else width })
                },
                label = "stepAnimation"
            ) { stepIdx ->
                val step = steps[stepIdx]
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.sm),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.size(100.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(step.emoji, fontSize = 48.sp)
                        }
                    }

                    Spacer(Modifier.height(Spacing.xl))

                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(Modifier.height(Spacing.xs))

                    Text(
                        text = step.subtitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(Spacing.md))

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = step.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(Spacing.lg)
                        )
                    }
                }
            }

            // Bottom Navigation Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        if (currentStepIndex < steps.size - 1) {
                            currentStepIndex++
                        } else {
                            onComplete()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = currentStep.actionText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                if (currentStepIndex < steps.size - 1) {
                    Spacer(Modifier.height(Spacing.xs))
                    TextButton(onClick = onComplete) {
                        Text(
                            "Skip to Dashboard",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
