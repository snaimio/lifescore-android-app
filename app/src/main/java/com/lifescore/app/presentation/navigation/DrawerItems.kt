package com.lifescore.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class DrawerItem(
    val label: String,
    val icon: ImageVector,
    val route: String,
    val badge: String? = null,
    val emoji: String? = null
)

object DrawerNavigationConfig {
    // 1. Core Navigation
    val mainItems = listOf(
        DrawerItem("Today Hub", Icons.Default.Home, Screen.Today.route, emoji = "🌅"),
        DrawerItem("Life Balance (360°)", Icons.Default.PieChart, Screen.Balance.route, emoji = "🌐"),
        DrawerItem("Growth Hub", Icons.AutoMirrored.Filled.TrendingUp, Screen.Grow.route, emoji = "🌱"),
        DrawerItem("Me (Profile & Stats)", Icons.Default.Person, Screen.Me.route, emoji = "👤"),
        DrawerItem("Explore Directory", Icons.Default.Explore, Screen.Explore.route, badge = "Tools", emoji = "🌟")
    )

    // 2. Growth & Reflection
    val growthItems = listOf(
        DrawerItem("Gemini AI Coach", Icons.Default.Psychology, Screen.AICoach.route, badge = "AI", emoji = "🤖"),
        DrawerItem("Cognitive Journal", Icons.Default.Book, Screen.Journal.route, emoji = "📝"),
        DrawerItem("Book Summaries", Icons.AutoMirrored.Filled.MenuBook, Screen.BookLibrary.route, badge = "15-Min", emoji = "📚"),
        DrawerItem("15-Min Daily Growth", Icons.Default.Lightbulb, Screen.DailyGrowth.route, emoji = "⚡"),
        DrawerItem("Science Journeys", Icons.Default.Biotech, Screen.ScienceJourneys.route, emoji = "🧬"),
        DrawerItem("Atomic Habits OS", Icons.Default.Bolt, Screen.AtomicHabits.route, badge = "Systems", emoji = "⚡"),
        DrawerItem("AI Action Plan", Icons.Default.AutoAwesome, Screen.ActionPlan.route, emoji = "🎯"),
        DrawerItem("Goal Starter Stacks", Icons.Default.ContentPaste, Screen.HabitTemplates.route, emoji = "📋"),
        DrawerItem("Identity-Based Habits", Icons.Default.HowToVote, Screen.IdentityHabits.route, emoji = "🗳️"),
        DrawerItem("14-Day Masterclasses", Icons.Default.EmojiEvents, Screen.Challenges.route, emoji = "🎓")
    )

    // 3. Trackers & Wellness
    val trackerMiniApps = listOf(
        DrawerItem("15 Life Trackers Hub", Icons.Default.DashboardCustomize, Screen.TrackerHub.route, badge = "Hub", emoji = "📊"),
        DrawerItem("Hydration Tracker", Icons.Default.WaterDrop, Screen.Hydration.route, emoji = "💧"),
        DrawerItem("Daily Steps", Icons.Default.DirectionsRun, "tracker_detail/steps", emoji = "🏃"),
        DrawerItem("Sleep & Recovery", Icons.Default.Bedtime, Screen.SleepSoundscapes.route, emoji = "🌙"),
        DrawerItem("Mindful Focus Timer", Icons.Default.Timer, Screen.FocusTimer.route, emoji = "⏱️"),
        DrawerItem("Insight Meditation", Icons.Default.SelfImprovement, Screen.MeditationLibrary.route, emoji = "🧘"),
        DrawerItem("Mood & Telemetry", Icons.Default.Mood, Screen.MoodTracker.route, emoji = "🎭"),
        DrawerItem("Circadian Energy Curve", Icons.Default.ElectricBolt, Screen.EnergySchedule.route, emoji = "⚡"),
        DrawerItem("Workout & Cardio", Icons.Default.FitnessCenter, "tracker_detail/workouts", emoji = "💪"),
        DrawerItem("Nutrition & Macros", Icons.Default.Restaurant, "tracker_detail/nutrition", emoji = "🥗")
    )

    // 4. Info & Settings
    val infoItems = listOf(
        DrawerItem("App Settings", Icons.Default.Settings, Screen.Settings.route, emoji = "⚙️"),
        DrawerItem("Privacy & Data Control", Icons.Default.Lock, Screen.Privacy.route, emoji = "🔒"),
        DrawerItem("LifeScore Assessment", Icons.Default.Psychology, Screen.FullAssessment.route, emoji = "🎯")
    )
}
