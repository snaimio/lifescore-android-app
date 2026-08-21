package com.lifescore.app.presentation.ui.atomichabits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.core.designsystem.components.GradientButton
import com.lifescore.app.domain.model.atomichabits.AtomicHabitsChallenge

@Composable
fun AtomicChallengeContent(
    challenge: AtomicHabitsChallenge?,
    onLogDay: () -> Unit,
    onChangeHabit: (String) -> Unit
) {
    val currentDay = challenge?.currentDay ?: 1
    val habitName = challenge?.habitName ?: "Daily 2-Minute Meditation"
    val progress = (currentDay.toFloat() / 30f).coerceIn(0f, 1f)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        // Challenge Progress Card
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.md)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "🔥 30-Day Atomic Challenge",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Habit: $habitName",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF59E0B).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Day $currentDay of 30",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF59E0B),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(Spacing.md))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${(progress * 100).toInt()}% COMPLETED",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "${30 - currentDay} days remaining",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = Color(0xFFF59E0B),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(Modifier.height(Spacing.md))

                    GradientButton(
                        text = if (challenge?.isCompleted == true) "🎉 30-Day Challenge Mastered!" else "⚡ Complete Day $currentDay (+30 XP)",
                        onClick = onLogDay,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Daily Motivation Quote Card
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.md)
                ) {
                    Text(
                        "💡 Day $currentDay Wisdom",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "\"${getAtomicQuote(currentDay)}\"",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "— James Clear, Atomic Habits",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        // 30-Day Matrix Visualization Card
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.md)
                ) {
                    Text(
                        "📅 30-Day Habit Matrix",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(Spacing.sm))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (row in 0 until 5) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                for (col in 1..6) {
                                    val dayIndex = row * 6 + col
                                    val isDayCompleted = dayIndex <= currentDay
                                    val isCurrent = dayIndex == currentDay

                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when {
                                                    isCurrent -> Color(0xFFF59E0B)
                                                    isDayCompleted -> Color(0xFF10B981)
                                                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                                }
                                            )
                                    ) {
                                        Text(
                                            text = if (isDayCompleted && !isCurrent) "✓" else "$dayIndex",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isDayCompleted) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
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

private fun getAtomicQuote(day: Int): String {
    return when (day) {
        1 -> "Every action you take is a vote for the type of person you wish to become."
        2 -> "You do not rise to the level of your goals. You fall to the level of your systems."
        3 -> "Be the designer of your world and not merely the consumer of it."
        4 -> "Habits are the compound interest of self-improvement."
        5 -> "The most effective way to change your habits is to focus not on what you want to achieve, but on who you wish to become."
        7 -> "Environment is the invisible hand that shapes human behavior."
        10 -> "When you start a new habit, it should take less than two minutes to do."
        14 -> "Professionals stick to the schedule; amateurs let life get in the way."
        21 -> "Missing once is an accident. Missing twice is the start of a new bad habit."
        30 -> "You have cast 30 votes for your new identity. You have proven who you are! 🎉"
        else -> "Small habits don't add up. They compound. Stay disciplined and trust the system."
    }
}
