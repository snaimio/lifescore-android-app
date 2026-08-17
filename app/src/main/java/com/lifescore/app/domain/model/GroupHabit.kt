package com.lifescore.app.domain.model

data class GroupHabit(
    val id: String,
    val title: String,
    val description: String,
    val dimension: DimensionType,
    val memberCount: Int = 1,
    val streakDays: Int = 0,
    val todayCompletedCount: Int = 0,
    val targetDailyCompletions: Int = 5,
    val xpReward: Int = 100,
    val isJoined: Boolean = false,
    val creatorName: String = "Achiever",
    val isCompletedToday: Boolean = false
) {
    val progressFraction: Float
        get() = if (memberCount > 0) (todayCompletedCount.toFloat() / memberCount.toFloat()).coerceIn(0f, 1f) else 0f
}

data class GroupMember(
    val id: String,
    val name: String,
    val title: String,
    val streakDays: Int,
    val hasCompletedToday: Boolean
)
