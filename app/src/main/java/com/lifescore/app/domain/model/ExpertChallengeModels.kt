package com.lifescore.app.domain.model

data class MasterclassDayModule(
    val dayNumber: Int,                // 1 to 14
    val title: String,
    val summary: String,
    val audioDurationSeconds: Int = 300, // 5:00 minutes
    val transcriptSummary: String,
    val dailyTaskTitle: String,
    val dailyTaskPoints: Int = 50,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null
)

data class ExpertMasterclass(
    val id: String,
    val title: String,
    val subtitle: String,
    val coachName: String,
    val coachTitle: String,
    val coachAvatarEmoji: String,
    val coachCredentials: String,
    val dimension: DimensionType,
    val durationDays: Int = 14,
    val priceUsd: Double = 9.99,
    val isPremiumIncluded: Boolean = true,
    val isUnlocked: Boolean = false,
    val currentDay: Int = 1,
    val isCompleted: Boolean = false,
    val graduationDate: Long? = null,
    val days: List<MasterclassDayModule>,
    val graduationXpBonus: Int = 500
) {
    val completedDaysCount: Int get() = days.count { it.isCompleted }
    val progressPercentage: Float get() = (completedDaysCount.toFloat() / durationDays.toFloat()).coerceIn(0f, 1f)
}

data class MasterclassCertificate(
    val certificateId: String,
    val userName: String,
    val masterclassTitle: String,
    val coachName: String,
    val coachTitle: String,
    val dimension: DimensionType,
    val completionDate: String,
    val verificationHash: String,
    val xpEarnedTotal: Int = 1200
)
