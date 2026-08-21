package com.lifescore.app.presentation.ui.atomichabits

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.core.engine.FourLawsSystem

@Composable
fun FourLawsContent() {
    val system = remember { FourLawsSystem() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
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
                    Text(
                        "⚡ The 4 Laws of Behavior Change",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "To build a good habit: 1) Make it obvious, 2) Make it attractive, 3) Make it easy, 4) Make it satisfying. To break a bad habit, invert these rules.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Law 1: Make It Obvious (Habit Stacking)
        item {
            LawCard(
                number = "1",
                lawTitle = "Make It Obvious",
                emoji = "👁️",
                concept = "Habit Stacking & Implementation Intentions",
                formula = "\"After [CURRENT HABIT], I will [NEW HABIT].\"",
                examples = system.getHabitStackExamples(),
                accentColor = Color(0xFF3B82F6)
            )
        }

        // Law 2: Make It Attractive (Temptation Bundling)
        item {
            LawCard(
                number = "2",
                lawTitle = "Make It Attractive",
                emoji = "🎯",
                concept = "Temptation Bundling & Dopamine Pairing",
                formula = "\"Only while I [GUILTY PLEASURE], will I [DESIRED HABIT].\"",
                examples = system.getTemptationBundlingExamples(),
                accentColor = Color(0xFFEC4899)
            )
        }

        // Law 3: Make It Easy (The 2-Minute Rule)
        item {
            LawCard(
                number = "3",
                lawTitle = "Make It Easy",
                emoji = "🌟",
                concept = "The 2-Minute Rule & Friction Reduction",
                formula = "\"Scale down the habit until it takes under 2 minutes to start.\"",
                examples = system.getTwoMinuteExamples().map { "${it.first} → ${it.second}" },
                accentColor = Color(0xFFF59E0B)
            )
        }

        // Law 4: Make It Satisfying (Immediate Rewards)
        item {
            LawCard(
                number = "4",
                lawTitle = "Make It Satisfying",
                emoji = "🏆",
                concept = "Immediate Reinforcement & Visual Streaks",
                formula = "\"What is immediately rewarded is repeated. What is punished is avoided.\"",
                examples = system.getImmediateRewards(),
                accentColor = Color(0xFF10B981)
            )
        }

        item {
            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

@Composable
fun LawCard(
    number: String,
    lawTitle: String,
    emoji: String,
    concept: String,
    formula: String,
    examples: List<String>,
    accentColor: Color
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = accentColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "$emoji Law $number",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(Modifier.width(Spacing.xs))
                    Text(
                        text = lawTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(Spacing.xs))

            Text(
                text = concept,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(Spacing.xs))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = formula,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(Spacing.sm)
                )
            }

            Spacer(Modifier.height(Spacing.sm))

            Text(
                text = "Practical Examples:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(Modifier.height(4.dp))

            examples.forEach { ex ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("• ", color = accentColor, fontWeight = FontWeight.Black)
                    Text(
                        text = ex,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
