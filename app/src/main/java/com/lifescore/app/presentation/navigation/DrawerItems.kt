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
    // 1. Core / Main
    val mainItems = listOf(
        DrawerItem("Home Hub", Icons.Default.Home, Screen.Home.route, emoji = "🏠"),
        DrawerItem("15 Life Trackers Hub", Icons.Default.DashboardCustomize, Screen.TrackerHub.route, badge = "15 Mini-Apps", emoji = "📊"),
        DrawerItem("Dimensions OS", Icons.Default.PieChart, Screen.Dimensions.route, emoji = "🌐"),
        DrawerItem("Daily Quests", Icons.Default.TaskAlt, Screen.Tasks.route, badge = "Daily", emoji = "⚔️"),
        DrawerItem("Hero Profile", Icons.Default.Person, Screen.Profile.route, emoji = "👤")
    )

    // 2. 15 Dedicated Mini-Apps
    val trackerMiniApps = listOf(
        DrawerItem("Hydration Tracker", Icons.Default.WaterDrop, "tracker_detail/hydration", emoji = "💧"),
        DrawerItem("Nutrition & Macros", Icons.Default.Restaurant, "tracker_detail/nutrition", emoji = "🥗"),
        DrawerItem("Sleep & Recovery", Icons.Default.Bedtime, "tracker_detail/sleep", emoji = "🛌"),
        DrawerItem("Vitals & Glucose", Icons.Default.Favorite, "tracker_detail/vitals", emoji = "🩸"),
        DrawerItem("Daily Steps", Icons.Default.DirectionsRun, "tracker_detail/steps", emoji = "🏃"),
        DrawerItem("Workout & Cardio", Icons.Default.FitnessCenter, "tracker_detail/workouts", emoji = "💪"),
        DrawerItem("Weight & Comp", Icons.Default.MonitorWeight, "tracker_detail/weight", emoji = "⚖️"),
        DrawerItem("Daily Reading", Icons.AutoMirrored.Filled.MenuBook, "tracker_detail/reading", emoji = "📚"),
        DrawerItem("10k Skill Mastery", Icons.Default.School, "tracker_detail/skills", emoji = "🎯"),
        DrawerItem("Cognitive Journal", Icons.Default.EditNote, "tracker_detail/journal", emoji = "📝"),
        DrawerItem("Goals & OKRs", Icons.Default.TrackChanges, "tracker_detail/goals", emoji = "🎯"),
        DrawerItem("Habits & Routines", Icons.Default.Checklist, "tracker_detail/routines", emoji = "📋"),
        DrawerItem("Social Relationships", Icons.Default.People, "tracker_detail/relationships", emoji = "💞"),
        DrawerItem("Financial & Wealth", Icons.Default.AccountBalance, "tracker_detail/wealth", emoji = "💰"),
        DrawerItem("Mindfulness & Zen", Icons.Default.SelfImprovement, "tracker_detail/mindfulness", emoji = "🧠")
    )

    // 3. Growth & Science Systems
    val growthItems = listOf(
        DrawerItem("Atomic Habits OS", Icons.AutoMirrored.Filled.TrendingUp, Screen.AtomicHabits.route, badge = "James Clear", emoji = "⚡"),
        DrawerItem("AI Action Plan", Icons.Default.AutoAwesome, Screen.ActionPlan.route, badge = "AI", emoji = "🎯"),
        DrawerItem("Gemini AI Coach", Icons.Default.Psychology, Screen.AICoach.route, emoji = "🤖"),
        DrawerItem("30-Day Challenges", Icons.Default.EmojiEvents, Screen.Challenges.route, emoji = "🏆"),
        DrawerItem("Habit Library (100)", Icons.Default.Checklist, Screen.HabitLibrary.route, emoji = "📋"),
        DrawerItem("Habit Micro-Vlogs", Icons.Default.Videocam, Screen.MicroVlogs.route, emoji = "🎬")
    )

    // 4. Community & Settings
    val communityItems = listOf(
        DrawerItem("Global Leaderboard", Icons.Default.Leaderboard, Screen.Leaderboard.route, emoji = "🏆"),
        DrawerItem("Boss Raids & Combat", Icons.Default.FlashOn, Screen.Combat.route, emoji = "⚔️"),
        DrawerItem("Squads & Circles", Icons.Default.Group, Screen.GroupHabits.route, emoji = "👥"),
        DrawerItem("App Settings", Icons.Default.Settings, Screen.Settings.route, emoji = "⚙️")
    )
}
