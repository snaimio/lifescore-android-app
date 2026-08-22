package com.lifescore.app.presentation.ui.templates

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.domain.model.DimensionType

data class HabitTemplateItem(
    val title: String,
    val dimension: DimensionType,
    val points: Int,
    val cue: String
)

data class GoalStarterStack(
    val id: String,
    val name: String,
    val emoji: String,
    val subtitle: String,
    val description: String,
    val difficulty: String,
    val habits: List<HabitTemplateItem>
)

object HabitTemplatesCatalog {
    val stacks: List<GoalStarterStack> = listOf(
        GoalStarterStack(
            id = "stack_morning_miracle",
            name = "Morning Momentum Stack",
            emoji = "🌅",
            subtitle = "Win the morning, win the day",
            description = "Scientifically sequenced routine based on Huberman morning protocols.",
            difficulty = "Beginner Friendly",
            habits = listOf(
                HabitTemplateItem("Drink 500ml water with electrolytes", DimensionType.HEALTH, 15, "Immediately upon waking"),
                HabitTemplateItem("10 mins natural sunlight view", DimensionType.HEALTH, 20, "Within 1 hour of waking"),
                HabitTemplateItem("Outline Top 3 high-leverage priorities", DimensionType.CAREER, 25, "Before checking email")
            )
        ),
        GoalStarterStack(
            id = "stack_deep_work",
            name = "Deep Work Software Craftsman",
            emoji = "💻",
            subtitle = "Cal Newport Focus Architecture",
            description = "Eliminate context switching and produce elite technical work.",
            difficulty = "Intermediate",
            habits = listOf(
                HabitTemplateItem("90-min uninterrupted deep-work block", DimensionType.CAREER, 40, "At 9:00 AM sharp"),
                HabitTemplateItem("Review code changes & clean git commits", DimensionType.CAREER, 20, "After midday standup"),
                HabitTemplateItem("Complete shutdown ritual & close IDE", DimensionType.MENTAL_HEALTH, 15, "At 6:00 PM")
            )
        ),
        GoalStarterStack(
            id = "stack_athlete_longevity",
            name = "Athletic Longevity & Energy",
            emoji = "🏃",
            subtitle = "Peter Attia Longevity Blueprint",
            description = "Cardiovascular capacity, joint mobility, and restorative sleep hygiene.",
            difficulty = "Advanced",
            habits = listOf(
                HabitTemplateItem("10-min morning joint mobility routine", DimensionType.HEALTH, 15, "Post morning coffee"),
                HabitTemplateItem("Hit 8,000 active daily steps", DimensionType.HEALTH, 30, "Throughout work day"),
                HabitTemplateItem("No screens 60 mins before bed", DimensionType.HEALTH, 25, "At 10:00 PM")
            )
        ),
        GoalStarterStack(
            id = "stack_mindful_stoic",
            name = "Mindful Stoic & Inner Peace",
            emoji = "🧠",
            subtitle = "Marcus Aurelius Inner Citadel",
            description = "Emotional regulation, cognitive journaling, and stress resilience.",
            difficulty = "Beginner Friendly",
            habits = listOf(
                HabitTemplateItem("5-min morning premeditatio malorum", DimensionType.MENTAL_HEALTH, 15, "During morning tea"),
                HabitTemplateItem("Evening gratitude & cognitive reflection", DimensionType.MENTAL_HEALTH, 20, "Before sleep"),
                HabitTemplateItem("Random act of appreciation for a loved one", DimensionType.RELATIONSHIPS, 25, "Mid-afternoon")
            )
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitTemplatesHubScreen(
    onNavigateBack: () -> Unit,
    onAdoptStack: (GoalStarterStack) -> Unit
) {
    val context = LocalContext.current
    val adoptedStackIds = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text("📋 Goal Starter Stacks", fontWeight = FontWeight.Bold)
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Header Guide
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Text("🚀 Zero Setup Friction", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Don't waste 30 minutes configuring habits from scratch. Tap any proven starter stack below to instantly load it into your daily quest log.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            items(HabitTemplatesCatalog.stacks) { stack ->
                val isAdopted = adoptedStackIds.contains(stack.id)

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = if (isAdopted) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, if (isAdopted) Color(0xFF81C784) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(stack.emoji, fontSize = 28.sp)
                                Spacer(Modifier.width(Spacing.sm))
                                Column {
                                    Text(stack.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(stack.subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    stack.difficulty,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(Spacing.xs))
                        Text(stack.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(Modifier.height(Spacing.sm))

                        // Included Habits Preview
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            stack.habits.forEach { habit ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("•", color = Color(habit.dimension.baseColorHex), fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        "${habit.title} (+${habit.points} XP)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(Spacing.md))

                        Button(
                            onClick = {
                                if (!isAdopted) {
                                    adoptedStackIds.add(stack.id)
                                    onAdoptStack(stack)
                                    Toast.makeText(context, "Added ${stack.habits.size} habits from ${stack.name}! 🚀", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAdopted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                            ),
                            enabled = !isAdopted
                        ) {
                            Text(if (isAdopted) "Adopted into Quests ✅" else "⚡ Adopt Entire Stack", fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
