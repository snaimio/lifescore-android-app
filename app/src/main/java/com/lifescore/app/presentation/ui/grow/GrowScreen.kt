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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lifescore.app.core.designsystem.components.CardVariant
import com.lifescore.app.core.designsystem.LifeScoreShapes
import com.lifescore.app.core.designsystem.Motion
import com.lifescore.app.core.designsystem.Space
import com.lifescore.app.core.designsystem.components.LifeCard
import com.lifescore.app.core.designsystem.components.LifeIcon
import com.lifescore.app.core.designsystem.components.LifeIcons
import com.lifescore.app.core.designsystem.components.LifeIllustration
import com.lifescore.app.core.designsystem.components.LifeIllustrations
import com.lifescore.app.core.designsystem.components.SectionHeader
import com.lifescore.app.core.designsystem.components.StaggeredAppear
import com.lifescore.app.presentation.navigation.Screen

data class GrowthToolItem(
    val title: String,
    val subtitle: String,
    val icon: LifeIcons,
    val route: String,
    val badge: String? = null
)

data class GrowthArea(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: LifeIcons,
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
                icon = LifeIcons.Run,
                color = Color(0xFFEF4444),
                tools = listOf(
                    GrowthToolItem("Mindful Focus Timer", "Pomodoro & deep work sessions", LifeIcons.Goal, Screen.FocusTimer.route, "Focus"),
                    GrowthToolItem("Daily Steps Tracker", "10,000 steps daily target", LifeIcons.Run, "tracker_detail/steps", "Tracker"),
                    GrowthToolItem("Workout & Cardio", "Log resistance & endurance training", LifeIcons.Health, "tracker_detail/workouts")
                )
            ),
            GrowthArea(
                id = "hydration",
                title = "Hydration & Body",
                subtitle = "Optimal daily water intake and cellular energy",
                icon = LifeIcons.Hydration,
                color = Color(0xFF3B82F6),
                tools = listOf(
                    GrowthToolItem("Hydration Tracker", "Log water and electrolytes", LifeIcons.Hydration, Screen.Hydration.route, "Daily"),
                    GrowthToolItem("Nutrition & Macros", "Fuel balance and calorie tracking", LifeIcons.Health, "tracker_detail/nutrition"),
                    GrowthToolItem("Vitals & Recovery", "Blood glucose, HR & HRV tracking", LifeIcons.Analytics, "tracker_detail/vitals")
                )
            ),
            GrowthArea(
                id = "rest",
                title = "Rest & Recovery",
                subtitle = "Deep REM sleep, circadian restoration, and nature soundscapes",
                icon = LifeIcons.Sleep,
                color = Color(0xFF8B5CF6),
                tools = listOf(
                    GrowthToolItem("Sleep Stories & Nature Sounds", "Pure acoustic nature sounds and bedtime wind-downs", LifeIcons.Sleep, Screen.SleepSoundscapes.route, "Calm"),
                    GrowthToolItem("Sleep Telemetry", "Track duration and sleep quality", LifeIcons.Analytics, "tracker_detail/sleep")
                )
            ),
            GrowthArea(
                id = "calm",
                title = "Calm & Mindfulness",
                subtitle = "Emotional regulation, breathing exercises, and mood",
                icon = LifeIcons.Meditation,
                color = Color(0xFF10B981),
                tools = listOf(
                    GrowthToolItem("Insight Meditation", "Guided breathwork and zen timers", LifeIcons.Meditation, Screen.MeditationLibrary.route, "Insight"),
                    GrowthToolItem("Mood & Telemetry", "Log emotions and friction triggers", LifeIcons.MoodHappy, Screen.MoodTracker.route, "Daily"),
                    GrowthToolItem("Thought Break (CBT)", "Cognitive reframing for stress & anxiety", LifeIcons.Mental, Screen.ThoughtBreak.route, "CBT")
                )
            ),
            GrowthArea(
                id = "learning",
                title = "Learning & Intellect",
                subtitle = "15-minute book summaries, mental models, and lessons",
                icon = LifeIcons.Reading,
                color = Color(0xFFF59E0B),
                tools = listOf(
                    GrowthToolItem("Book Summaries Library", "Key ideas from bestsellers in 15 mins", LifeIcons.Reading, Screen.BookLibrary.route, "Headway"),
                    GrowthToolItem("15-Min Daily Growth", "Curated daily micro-lessons", LifeIcons.Energy, Screen.DailyGrowth.route, "Daily"),
                    GrowthToolItem("Science Journeys", "Step-by-step habit science tracks", LifeIcons.Analytics, Screen.ScienceJourneys.route, "Science"),
                    GrowthToolItem("Book Flashcards & Action Plans", "Retain key takeaways with spaced repetition", LifeIcons.Reading, Screen.BookFlashcards.route)
                )
            ),
            GrowthArea(
                id = "energy",
                title = "Energy & Circadian",
                subtitle = "Plan deep work according to biological peak hours",
                icon = LifeIcons.Energy,
                color = Color(0xFFEC4899),
                tools = listOf(
                    GrowthToolItem("Circadian Energy Schedule", "Track ultradian rhythms and peak focus windows", LifeIcons.Energy, Screen.EnergySchedule.route, "Productivity"),
                    GrowthToolItem("Task Breakthrough", "AI guided breakdown of intimidating tasks", LifeIcons.Goal, Screen.TaskBreakthrough.route, "AI Focus")
                )
            ),
            GrowthArea(
                id = "reflection",
                title = "Reflection & Mastery",
                subtitle = "Deliberate practice, system design, and journaling",
                icon = LifeIcons.Mental,
                color = Color(0xFF14B8A6),
                tools = listOf(
                    GrowthToolItem("Cognitive Journal", "Daily reflections, wins, and gratitude", LifeIcons.Reading, Screen.Journal.route, "Journal"),
                    GrowthToolItem("10,000-Hour Skill Mastery", "Deliberate practice tracker for mastery", LifeIcons.Goal, Screen.SkillMastery.route, "10k Hours"),
                    GrowthToolItem("AI Action Plan", "Generate tailored life goals and strategy", LifeIcons.Goal, Screen.ActionPlan.route, "AI"),
                    GrowthToolItem("Atomic Habits OS", "Habit scorecards and environment design", LifeIcons.Energy, Screen.AtomicHabits.route, "System"),
                    GrowthToolItem("Goal Starter Stacks", "1-tap habit stacks for common goals", LifeIcons.Check, Screen.HabitTemplates.route, "Templates")
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
                            "Self-Improvement, Science & Habits",
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
                                    LifeIcon(
                                        icon = LifeIcons.Rocket,
                                        size = 24.dp,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
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
                            leadingIcon = {
                                LifeIcon(icon = area.icon, size = 16.dp, tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                            },
                            label = { Text(area.title.split(" ").first()) },
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
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val cardBg = if (isDark) MaterialTheme.colorScheme.surface else area.color.copy(alpha = 0.08f)
    val cardBorder = if (isDark) {
        BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    } else {
        BorderStroke(1.dp, area.color.copy(alpha = 0.28f))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(LifeScoreShapes.card)
            .clickable { onToggle() },
        shape = LifeScoreShapes.card,
        color = cardBg,
        border = cardBorder
    ) {
        Column(modifier = Modifier.padding(horizontal = Space.cardH, vertical = Space.cardV)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = LifeScoreShapes.button,
                        color = area.color.copy(alpha = if (isDark) 0.12f else 0.18f),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            LifeIcon(
                                icon = area.icon,
                                size = 20.dp,
                                tint = area.color
                            )
                        }
                    }
                    Spacer(Modifier.width(Space.sm))
                    Column {
                        Text(
                            area.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
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
                    color = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else area.color.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${area.tools.size} tools",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else area.color,
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
                        val toolBg = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f) else Color.White
                        val toolBorder = if (isDark) {
                            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        } else {
                            BorderStroke(1.dp, area.color.copy(alpha = 0.2f))
                        }
                        Surface(
                            shape = LifeScoreShapes.cardSmall,
                            color = toolBg,
                            border = toolBorder,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenTool(tool.route) }
                        ) {
                        Row(
                            modifier = Modifier.padding(horizontal = Space.md, vertical = Space.sm),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LifeIcon(
                                icon = tool.icon,
                                size = 18.dp,
                                tint = area.color
                            )
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
}
