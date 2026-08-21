package com.lifescore.app.domain.model.atomichabits

import java.util.UUID

// 1. Habit Identity Models
data class HabitIdentity(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "user_default",
    val identityStatement: String, // "I am a reader", "I am a healthy athlete"
    val dailyVotes: Int = 0, // Number of days "voted" for this identity
    val targetVotes: Int = 30, // 30-day goal
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

data class IdentityVote(
    val id: String = UUID.randomUUID().toString(),
    val identityId: String,
    val date: String, // YYYY-MM-DD
    val actionTaken: String, // What they did to "vote"
    val timestamp: Long = System.currentTimeMillis()
)

// 2. Habit Scorecard Models
enum class HabitCategory(val symbol: String, val displayName: String) {
    GOOD("+", "Good Habit"),
    BAD("-", "Bad Habit"),
    NEUTRAL("=", "Neutral Habit")
}

data class HabitScorecardItem(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "user_default",
    val habitName: String,
    val category: HabitCategory,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// 3. 30-Day Atomic Challenge Models
data class AtomicHabitsChallenge(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "user_default",
    val habitName: String = "Morning 2-Minute Meditation",
    val startDate: Long = System.currentTimeMillis(),
    val currentDay: Int = 1,
    val totalDays: Int = 30,
    val dailyLogs: List<DailyHabitLog> = emptyList(),
    val isCompleted: Boolean = false
)

data class DailyHabitLog(
    val day: Int,
    val date: String,
    val isCompleted: Boolean,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

// 4. System Design Journal Models
data class SystemDesignEntry(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val habitTarget: String,
    val environmentChanges: String,
    val habitStack: String,
    val twoMinuteStep: String,
    val rewardPlan: String,
    val timestamp: Long = System.currentTimeMillis()
)
