package com.lifescore.app.domain.model

import java.util.UUID

enum class HabitType(val displayName: String, val icon: String) {
    BOOLEAN("Simple Check", "✓"),
    COUNTER("Counter Goal", "🔢"),
    SUB_TASKS("Multi-Step Routine", "📋"),
    SMART_DIMENSION("Smart Auto-Suggest", "✨")
}

data class SubTaskItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val isCompleted: Boolean = false
)

data class LifeTask(
    val id: Long = 0,
    val title: String,
    val dimension: DimensionType,
    val pointsReward: Int = 15,
    val isCompleted: Boolean = false,
    val streakDays: Int = 0,
    val recurringInterval: String = "DAILY",
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val habitType: HabitType = HabitType.BOOLEAN,
    val currentCount: Int = 0,
    val targetCount: Int = 1,
    val countUnit: String = "",
    val subTasks: List<SubTaskItem> = emptyList(),
    val isSmartSuggested: Boolean = false
) {
    val subTaskProgress: Float
        get() = if (subTasks.isEmpty()) 0f else subTasks.count { it.isCompleted }.toFloat() / subTasks.size
    val counterProgress: Float
        get() = if (targetCount <= 0) 0f else (currentCount.toFloat() / targetCount).coerceIn(0f, 1f)
}

data class DailyScore(
    val dateIso: String,
    val totalScore: Int,
    val dimensionScores: Map<DimensionType, Int>,
    val completedTasksCount: Int
)

data class UserProfile(
    val id: Long = 1,
    val name: String = "Achiever",
    val currentXp: Int = 1450,
    val currentLevel: Int = 5,
    val currentStreakDays: Int = 7,
    val isPremium: Boolean = false,
    val title: String = "Ascendant Practitioner",
    val shieldsRemaining: Int = 1,
    val guardianId: String? = null,
    val isSponsored: Boolean = false,
    val sponsorName: String? = null,
    val coinBalance: Int = 1250,
    val lifetimeCoinsEarned: Int = 3400
)

data class GuardianSponsor(
    val id: String,
    val sponsorName: String,
    val recipientsCount: Int,
    val recentNote: String,
    val tier: String = "LifeScore Pro Benefactor"
)

data class ChallengeParticipant(
    val uid: String,
    val name: String,
    val level: Int = 1,
    val completedDays: Int = 0,
    val streak: Int = 0,
    val isWinner: Boolean = false,
    val isCurrentUser: Boolean = false
)

data class Challenge(
    val id: String,
    val title: String,
    val description: String,
    val dimension: DimensionType,
    val durationDays: Int = 7,
    val currentDay: Int = 0,
    val xpReward: Int = 500,
    val isJoined: Boolean = false,
    val isCompleted: Boolean = false,
    val isDuel: Boolean = false,
    val creatorName: String = "LifeScore",
    val inviteCode: String = "",
    val participantsCount: Int = 1,
    val participants: List<ChallengeParticipant> = emptyList(),
    val dailyCheckIns: List<Boolean> = emptyList()
)

enum class SubscriptionTier(
    val skuId: String,
    val title: String,
    val priceFormatted: String,
    val billingPeriod: String,
    val isPopular: Boolean = false
) {
    MONTHLY("lifescore_monthly_799", "LifeScore+", "$7.99", "per month"),
    ANNUAL("lifescore_annual_4999", "LifeScore Pro", "$49.99", "per year ($4.16/mo)", isPopular = true),
    LIFETIME("lifescore_lifetime_119", "Founder Edition", "$119.99", "one-time payment")
}
