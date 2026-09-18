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
    // 1. Core / Main 5 Tabs
    val mainItems = listOf(
        DrawerItem("Today Hub", Icons.Default.Home, Screen.Today.route, emoji = "🌅"),
        DrawerItem("Life Balance (360°)", Icons.Default.PieChart, Screen.Balance.route, emoji = "🌐"),
        DrawerItem("Growth Hub", Icons.AutoMirrored.Filled.TrendingUp, Screen.Grow.route, emoji = "🌱"),
        DrawerItem("Me (Profile & Character)", Icons.Default.Person, Screen.Me.route, emoji = "👤"),
        DrawerItem("Explore Directory", Icons.Default.Explore, Screen.Explore.route, badge = "40+ Tools", emoji = "🌟")
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
        DrawerItem("Identity-Based Habits", Icons.Default.HowToVote, Screen.IdentityHabits.route, emoji = "🗳️")
    )

    // 3. Progress & RPG Mechanics
    val progressItems = listOf(
        DrawerItem("Hero Character Sheet", Icons.Default.Shield, Screen.CharacterStats.route, badge = "Hero", emoji = "🛡️"),
        DrawerItem("10-Tier Leagues", Icons.Default.EmojiEvents, Screen.LeagueTiers.route, badge = "Ranks", emoji = "🏆"),
        DrawerItem("Boss Battles & Raids", Icons.Default.FlashOn, Screen.Combat.route, badge = "Raid", emoji = "⚔️"),
        DrawerItem("Virtual Pet Companion", Icons.Default.Pets, Screen.VirtualPet.route, badge = "Companion", emoji = "🐥"),
        DrawerItem("Streak Vault & Shield", Icons.Default.Shield, Screen.StreakVault.route, badge = "Vault", emoji = "🛡️"),
        DrawerItem("10k Skill Mastery", Icons.Default.Timer, Screen.SkillMastery.route, emoji = "⏱️"),
        DrawerItem("30-Day Challenges", Icons.Default.EmojiEvents, Screen.Challenges.route, emoji = "🏆")
    )

    // 4. Trackers (15 Modular Trackers)
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

    // 5. Community & Social
    val communityItems = listOf(
        DrawerItem("Friends Social Feed", Icons.Default.Group, Screen.FriendsFeed.route, badge = "Social", emoji = "🔥"),
        DrawerItem("Invite Friends (Free Pro)", Icons.Default.CardGiftcard, Screen.ViralReferrals.route, badge = "Gift", emoji = "🎁"),
        DrawerItem("Seasonal Live Events", Icons.Default.WbSunny, Screen.SeasonalEvents.route, badge = "Live", emoji = "☀️"),
        DrawerItem("Squads & Group Habits", Icons.Default.Group, Screen.GroupHabits.route, emoji = "👥")
    )

    // 6. Rewards & Store
    val storeItems = listOf(
        DrawerItem("Custom Treats Store", Icons.Default.MonetizationOn, Screen.CustomRewards.route, badge = "Gold", emoji = "💰"),
        DrawerItem("LifeScore Rewards Vault", Icons.Default.ShoppingBag, Screen.RewardStore.route, emoji = "🎁"),
        DrawerItem("Gem Store", Icons.Default.Diamond, Screen.GemStore.route, emoji = "💎"),
        DrawerItem("Cosmetic Vault", Icons.Default.ShoppingBag, Screen.CosmeticStore.route, emoji = "🎨"),
        DrawerItem("Supporter VIP Program", Icons.Default.MilitaryTech, Screen.SupporterSubscription.route, badge = "VIP", emoji = "👑")
    )

    // 7. Info & Settings
    val infoItems = listOf(
        DrawerItem("App Settings", Icons.Default.Settings, Screen.Settings.route, emoji = "⚙️"),
        DrawerItem("Privacy & Data Control", Icons.Default.Lock, Screen.Privacy.route, emoji = "🔒"),
        DrawerItem("Full Assessment", Icons.Default.Psychology, Screen.FullAssessment.route, emoji = "🎯")
    )
}
