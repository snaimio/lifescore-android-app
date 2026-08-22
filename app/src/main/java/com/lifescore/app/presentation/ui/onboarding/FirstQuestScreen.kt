package com.lifescore.app.presentation.ui.onboarding

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.domain.model.DimensionType

@Composable
fun FirstQuestScreen(
    questTitle: String,
    dimension: DimensionType,
    onCompleteQuest: () -> Unit,
    onSkip: () -> Unit
) {
    var isCompleted by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.lg, vertical = Spacing.md)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(Spacing.md))

                Surface(
                    shape = CircleShape,
                    color = if (isCompleted) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(96.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(if (isCompleted) "🎉" else "🌱", fontSize = 48.sp)
                    }
                }

                Spacer(Modifier.height(Spacing.md))

                Text(
                    if (isCompleted) "Quest Mastered!" else "Your First Quest",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(Spacing.xs))

                Text(
                    "Small wins create compounding daily momentum.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(Spacing.lg))

                // Interactive First Quest Card
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(Spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(dimension.baseColorHex).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${dimension.displayName} • +50 XP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(dimension.baseColorHex),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }

                        Spacer(Modifier.height(Spacing.md))

                        Text(
                            text = questTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(Spacing.sm))

                        Text(
                            text = if (isCompleted) "Awesome job! You earned +50 XP and unlocked your Day 1 Streak." else "Tap the button below once you've completed this first action to claim +50 XP.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(Modifier.height(Spacing.lg))

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isCompleted) {
                    Button(
                        onClick = {
                            isCompleted = true
                            onCompleteQuest()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00E676),
                            contentColor = Color(0xFF1B5E20)
                        )
                    ) {
                        Text("✅ I Did It! (+50 XP)", fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }

                    TextButton(onClick = onSkip) {
                        Text("Skip to Dashboard →", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    }
                } else {
                    Button(
                        onClick = onSkip,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Enter Command Center 🏠 →", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(Modifier.height(Spacing.sm))
        }
    }
}
