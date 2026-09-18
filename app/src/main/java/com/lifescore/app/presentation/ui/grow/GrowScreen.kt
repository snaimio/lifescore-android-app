package com.lifescore.app.presentation.ui.grow

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lifescore.app.core.designsystem.components.CardVariant
import com.lifescore.app.core.designsystem.LifeScoreShapes
import com.lifescore.app.core.designsystem.Motion
import com.lifescore.app.core.designsystem.Space
import com.lifescore.app.core.designsystem.components.LifeCard
import com.lifescore.app.core.designsystem.components.SectionHeader
import com.lifescore.app.core.designsystem.components.StaggeredAppear
import com.lifescore.app.presentation.navigation.Screen

data class GrowthToolItem(
    val title: String,
    val subtitle: String,
    val emoji: String,
    val route: String,
    val badge: String? = null
)

data class GrowthArea(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val color: Color,
    val tools: List<GrowthToolItem>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrowScreen(
    navController: NavController
) {
    var expandedAreaId by remember { mutableStateOf<String?>("learning") }
    var selectedFilter by remember { mutableStateOf<String?>(null) }

    val growthAreas = remember {
        listOf(
            GrowthArea(
                id = "movement",
                title = "Movement & Physicality",
                subtitle = "Active workouts, step counts, and physical vitality",
                emoji = "🏃",
                color = Color(0xFFEF4444),
                tools = listOf(
                    GrowthToolItem("Mindful Focus Timer", "Pomodoro & deep work sessions", "⏱️", Screen.FocusTimer.route, "Focus"),
                    GrowthToolItem("Daily Steps Tracker", "10,000 steps daily target", "👟", "tracker_detail/steps", "Tracker"),
                    GrowthToolItem("Workout & Cardio", "Log resistance & endurance training", "💪", "tracker_detail/workouts")
                )
            ),
            GrowthArea(
                id = "hydration",
                title = "Hydration & Body",
                subtitle = "Optimal daily water intake and cellular energy",
                emoji = "💧",
                color = Color(0xFF3B82F6),
                tools = listOf(
                    GrowthToolItem("Hydration Tracker", "Log water and electrolytes", "💧", Screen.Hydration.route, "Daily"),
                    GrowthToolItem("Nutrition & Macros", "Fuel balance and calorie tracking", "🥗", "tracker_detail/nutrition"),
                    GrowthToolItem("Vitals & Recovery", "Blood glucose, HR & HRV tracking", "🩸", "tracker_detail/vitals")
                )
            ),
            GrowthArea(
                id = "rest",
                title = "Rest & Recovery",
                subtitle = "Deep REM sleep, circadian restoration, and soundscapes",
                emoji = "🌙",
                color = Color(0xFF8B5CF6),
                tools = listOf(
                    GrowthToolItem("Sleep Stories & Soundscapes", "Ambient soundscapes and bedtime wind-downs", "🌙", Screen.SleepSoundscapes.route, "Calm"),
                    GrowthToolItem("Sleep Telemetry", "Track duration and sleep quality", "🛌", "tracker_detail/sleep")
                )
            ),
            GrowthArea(
                id = "calm",
                title = "Calm & Mindfulness",
                subtitle = "Emotional regulation, breathing exercises, and mood",
                emoji = "🧘",
                color = Color(0xFF10B981),
                tools = listOf(
                    GrowthToolItem("Insight Meditation", "Guided breathwork and zen timers", "🧘", Screen.MeditationLibrary.route, "Insight"),
                    GrowthToolItem("Mood & Telemetry", "Log emotions and friction triggers", "🎭", Screen.MoodTracker.route, "Daily"),
                    GrowthToolItem("Thought Break (CBT)", "Cognitive reframing for stress & anxiety", "🧠", Screen.ThoughtBreak.route, "CBT")
                )
            ),
            GrowthArea(
                id = "learning",
                title = "Learning & Intellect",
                subtitle = "15-minute book summaries, mental models, and lessons",
                emoji = "📚",
                color = Color(0xFFF59E0B),
                tools = listOf(
                    GrowthToolItem("Book Summaries Library", "Key ideas from bestsellers in 15 mins", "📚", Screen.BookLibrary.route, "Headway"),
                    GrowthToolItem("15-Min Daily Growth", "Curated daily micro-lessons", "⚡", Screen.DailyGrowth.route, "Daily"),
                    GrowthToolItem("Science Journeys", "Step-by-step habit science tracks", "🧬", Screen.ScienceJourneys.route, "Science"),
                    GrowthToolItem("Book Flashcards & Action Plans", "Retain key takeaways with spaced repetition", "📇", Screen.BookFlashcards.route)
                )
            ),
            GrowthArea(
                id = "rpg",
                title = "Progress & RPG Systems",
                subtitle = "Level up your hero, battle raid bosses, and earn badges",
                emoji = "🎮",
                color = Color(0xFF6366F1),
                tools = listOf(
                    GrowthToolItem("Hero Character Sheet", "Attributes, Level, and XP progression", "🛡️", Screen.CharacterStats.route, "Hero"),
                    GrowthToolItem("Boss Battles & Raids", "Co-op combat powered by real habits", "⚔️", Screen.Combat.route, "Raid"),
                    GrowthToolItem("Virtual Pet Companion", "Nurture your companion through daily quests", "🐥", Screen.VirtualPet.route, "Companion"),
                    GrowthToolItem("10-Tier Leagues", "Climb from Bronze to Outlier Grandmaster", "🏆", Screen.LeagueTiers.route, "League"),
                    GrowthToolItem("Streak Vault & Shields", "Protect your momentum with streak shields", "🛡️", Screen.StreakVault.route, "Protection")
                )
            ),
            GrowthArea(
                id = "energy",
                title = "Energy & Circadian",
                subtitle = "Plan deep work according to biological peak hours",
                emoji = "⚡",
                color = Color(0xFFEC4899),
                tools = listOf(
                    GrowthToolItem("Circadian Energy Schedule", "Track ultradian rhythms and peak focus windows", "⚡", Screen.EnergySchedule.route, "Productivity"),
                    GrowthToolItem("Task Breakthrough", "AI guided breakdown of intimidating tasks", "🎯", Screen.TaskBreakthrough.route, "AI Focus")
                )
            ),
            GrowthArea(
                id = "reflection",
                title = "Reflection & Mastery",
                subtitle = "Deliberate practice, system design, and journaling",
                emoji = "🧠",
                color = Color(0xFF14B8A6),
                tools = listOf(
                    GrowthToolItem("Cognitive Journal", "Daily reflections, wins, and gratitude", "📝", Screen.Journal.route, "Journal"),
                    GrowthToolItem("10,000-Hour Skill Mastery", "Deliberate practice tracker for mastery", "⏱️", Screen.SkillMastery.route, "10k Hours"),
                    GrowthToolItem("AI Action Plan", "Generate tailored life goals and strategy", "🎯", Screen.ActionPlan.route, "AI"),
                    GrowthToolItem("Atomic Habits OS", "Habit scorecards and environment design", "⚡", Screen.AtomicHabits.route, "System"),
                    GrowthToolItem("Goal Starter Stacks", "1-tap habit stacks for common goals", "📋", Screen.HabitTemplates.route, "Templates")
                )
            )
        )
    }

    val filteredAreas = remember(selectedFilter, growthAreas) {
        if (selectedFilter == null) growthAreas
        else growthAreas.filter { it.id == selectedFilter }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Growth Hub",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "8 Growth Areas • Science, RPG & Habits",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Space.screenH),
            verticalArrangement = Arrangement.spacedBy(Space.cardGap),
            contentPadding = PaddingValues(top = Space.xs, bottom = Space.xxxl)
        ) {
            // Header Banner
            item {
                StaggeredAppear(index = 0) {
                    LifeCard(
                        modifier = Modifier.fillMaxWidth(),
                        variant = CardVariant.Cream
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🌱", fontSize = 24.sp)
                                }
                            }
                            Spacer(Modifier.width(Space.md))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "What are you working on?",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Explore specialized tools to develop any domain of your life.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Quick Category Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Space.xs),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedFilter == null,
                            onClick = { selectedFilter = null },
                            label = { Text("All 8 Areas") },
                            shape = LifeScoreShapes.chip
                        )
                    }
                    items(growthAreas) { area ->
                        val isSelected = selectedFilter == area.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = if (isSelected) null else area.id },
                            label = { Text("${area.emoji} ${area.title.split(" ").first()}") },
                            shape = LifeScoreShapes.chip
                        )
                    }
                }
            }

            // 8 Growth Area Expandable Cards
            items(filteredAreas, key = { it.id }) { area ->
                val isExpanded = expandedAreaId == area.id
                GrowthAreaCard(
                    area = area,
                    isExpanded = isExpanded,
                    onToggle = {
                        expandedAreaId = if (isExpanded) null else area.id
                    },
                    onOpenTool = { route -> navController.navigate(route) }
                )
            }
        }
    }
}

@Composable
private fun GrowthAreaCard(
    area: GrowthArea,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onOpenTool: (String) -> Unit
) {
    LifeCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        variant = CardVariant.Default
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Surface(
                    shape = LifeScoreShapes.button,
                    color = area.color.copy(alpha = 0.12f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(area.emoji, fontSize = 20.sp)
                    }
                }
                Spacer(Modifier.width(Space.sm))
                Column {
                    Text(
                        area.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        area.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Surface(
                shape = LifeScoreShapes.tag,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
                Text(
                    text = "${area.tools.size} tools",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = Space.xs, vertical = Space.xxs)
                )
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(Motion.Gentle) + expandVertically(),
            exit = fadeOut(Motion.Snappy) + shrinkVertically()
        ) {
            Column(
                modifier = Modifier.padding(top = Space.md),
                verticalArrangement = Arrangement.spacedBy(Space.xs)
            ) {
                area.tools.forEach { tool ->
                    Surface(
                        shape = LifeScoreShapes.cardSmall,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenTool(tool.route) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = Space.md, vertical = Space.sm),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(tool.emoji, fontSize = 18.sp)
                            Spacer(Modifier.width(Space.sm))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        tool.title,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    if (tool.badge != null) {
                                        Spacer(Modifier.width(Space.xs))
                                        Surface(
                                            shape = LifeScoreShapes.tag,
                                            color = area.color.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                tool.badge,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                fontWeight = FontWeight.Bold,
                                                color = area.color,
                                                modifier = Modifier.padding(horizontal = Space.xxs, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    tool.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Open",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
