package com.lifescore.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Dimensions : Screen("dimensions", "Dimensions", Icons.Default.PieChart)
    object Tasks : Screen("tasks", "Quests", Icons.Default.Checklist)
    object Challenges : Screen("challenges", "Challenges", Icons.Default.EmojiEvents)
    object AICoach : Screen("ai_coach", "AI Coach", Icons.Default.Psychology)
    object Leaderboard : Screen("leaderboard", "Ranks", Icons.Default.Leaderboard)
    object MicroVlogs : Screen("micro_vlogs", "Reels", Icons.Default.Videocam)
    object ArchetypeProfile : Screen("archetype_profile", "Archetype", Icons.Default.Shield)
    object SkillMastery : Screen("skill_mastery", "Mastery", Icons.Default.Timer)
    object RewardStore : Screen("reward_store", "Store", Icons.Default.ShoppingBag)
    object Enterprise : Screen("enterprise", "Enterprise", Icons.Default.Business)
    object Profile : Screen("profile", "Profile", Icons.Default.AccountCircle)
    object MemeStudio : Screen("meme_studio", "Memes", Icons.Default.AutoAwesomeMotion)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object Consent : Screen("consent", "Consent", Icons.Default.Security)
    object Onboarding : Screen("onboarding", "Assessment", Icons.Default.AutoAwesome)
    object Login : Screen("login", "Login", Icons.Default.Lock)
    object AiQuests : Screen("ai_quests", "AI Quests", Icons.Default.AutoAwesome)
    object CharacterStats : Screen("character_stats", "Stats", Icons.Default.Shield)
    object GroupHabits : Screen("group_habits", "Squads", Icons.Default.Group)
    object Journal : Screen("journal", "Journal", Icons.Default.Book)
    object Combat : Screen("combat", "Raids", Icons.Default.FlashOn)
    object Analytics : Screen("analytics", "Analytics", Icons.Default.BarChart)
    object Privacy : Screen("privacy", "Privacy", Icons.Default.Lock)
    object HabitLibrary : Screen("habit_library", "100 Habits", Icons.Default.ListAlt)
    object ActionPlan : Screen("action_plan", "Action Plan", Icons.Default.TrackChanges)
    object Hydration : Screen("hydration", "Hydration", Icons.Default.WaterDrop)
}
