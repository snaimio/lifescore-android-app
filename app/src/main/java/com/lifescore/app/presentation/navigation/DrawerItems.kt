package com.lifescore.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class DrawerItem(
    val label: String,
    val icon: ImageVector,
    val route: String,
    val badge: String? = null
)

object DrawerNavigationConfig {
    // 1. Quick Access & Essential Tools (Complements bottom tabs without duplicating them)
    val quickTools = listOf(
        DrawerItem(
            label = "AI Life Coach",
            icon = Icons.Default.Psychology,
            route = Screen.AICoach.route,
            badge = "AI"
        ),
        DrawerItem(
            label = "Book Summaries (15-Min)",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            route = Screen.BookLibrary.route,
            badge = "Audio"
        ),
        DrawerItem(
            label = "Mindful Focus Timer",
            icon = Icons.Default.Timer,
            route = Screen.FocusTimer.route
        ),
        DrawerItem(
            label = "Cognitive Journal",
            icon = Icons.Default.Book,
            route = Screen.Journal.route
        ),
        DrawerItem(
            label = "15 Life Trackers Hub",
            icon = Icons.Default.DashboardCustomize,
            route = Screen.TrackerHub.route,
            badge = "Hub"
        )
    )

    // 2. Wellness & Lifestyle Trackers
    val wellnessTrackers = listOf(
        DrawerItem(
            label = "Hydration Tracker",
            icon = Icons.Default.WaterDrop,
            route = Screen.Hydration.route
        ),
        DrawerItem(
            label = "Sleep & Soundscapes",
            icon = Icons.Default.Bedtime,
            route = Screen.SleepSoundscapes.route,
            badge = "Audio"
        ),
        DrawerItem(
            label = "Mood Tracker",
            icon = Icons.Default.Mood,
            route = Screen.MoodTracker.route
        ),
        DrawerItem(
            label = "Screen Time & Detox",
            icon = Icons.Default.PhoneAndroid,
            route = Screen.ScreenTime.route
        ),
        DrawerItem(
            label = "Atomic Habits OS",
            icon = Icons.Default.Bolt,
            route = Screen.AtomicHabits.route
        )
    )

    // 3. System & Account
    val systemPreferences = listOf(
        DrawerItem(
            label = "Profile & Identity",
            icon = Icons.Default.AccountCircle,
            route = Screen.Profile.route
        ),
        DrawerItem(
            label = "Settings & Notifications",
            icon = Icons.Default.Settings,
            route = Screen.Settings.route
        ),
        DrawerItem(
            label = "Privacy & Data Security",
            icon = Icons.Default.Lock,
            route = Screen.Privacy.route
        )
    )
}
