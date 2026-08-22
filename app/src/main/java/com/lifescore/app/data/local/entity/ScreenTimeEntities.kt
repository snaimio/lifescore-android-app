package com.lifescore.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ScreenTimeActionType {
    SOCIAL_MEDIA,
    GAMING,
    VIDEO_STREAMING,
    SHOPPING,
    NEWS,
    PRODUCTIVITY,
    OTHER
}

@Entity(tableName = "screen_time_entries")
data class ScreenTimeEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val date: String, // YYYY-MM-DD
    val totalMinutes: Int,
    val socialMediaMinutes: Int = 0,
    val gamingMinutes: Int = 0,
    val videoMinutes: Int = 0,
    val shoppingMinutes: Int = 0,
    val pickups: Int = 0,
    val firstPickup: Long? = null,
    val lastPickup: Long? = null,
    val screenTimeGoalMet: Boolean = false
)

@Entity(tableName = "screen_time_goals")
data class ScreenTimeGoalEntity(
    @PrimaryKey val userId: String,
    val dailyLimitMinutes: Int = 120, // 2 hours default
    val appWhitelist: String = "", // JSON of allowed app IDs
    val isFocusModeEnabled: Boolean = false,
    val intentionalDelaySeconds: Int = 10, // Friction delay before opening
    val earnedBonusMinutes: Int = 0 // Minutes unlocked via SweatPass movement
)

@Entity(tableName = "screen_time_sessions")
data class ScreenTimeSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val sessionType: String, // "FOCUS", "BREAK", "DETOX"
    val startTime: Long,
    val endTime: Long? = null,
    val durationMinutes: Int = 0,
    val appsBlocked: String = "", // JSON array
    val wasSuccessful: Boolean = false
)

@Entity(tableName = "screen_time_challenges")
data class ScreenTimeChallenge(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val challengeType: String, // "DETOX", "PICKUP_REDUCTION", "MINDFULNESS"
    val title: String = "7-Day Digital Detox",
    val targetDays: Int = 7,
    val currentDay: Int = 0,
    val isActive: Boolean = true,
    val xpReward: Int = 100,
    val completedAt: Long? = null
)

@Entity(tableName = "thought_break_logs")
data class ThoughtBreakLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val automaticThought: String,
    val cognitiveDistortion: String, // "Catastrophizing", "All-or-Nothing", "Mind Reading", etc.
    val evidenceAgainst: String,
    val reframedThought: String,
    val emotionalReliefRating: Int = 8, // 1 - 10
    val timestamp: Long = System.currentTimeMillis()
)
