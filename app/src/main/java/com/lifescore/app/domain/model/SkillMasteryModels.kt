package com.lifescore.app.domain.model

import java.util.UUID

enum class MasteryTier(
    val title: String,
    val icon: String,
    val minHours: Int,
    val maxHours: Int,
    val colorHex: Long
) {
    NOVICE("Novice", "🌱", 0, 100, 0xFF9CA3AF),
    APPRENTICE("Apprentice", "⚡", 100, 500, 0xFF3B82F6),
    JOURNEYMAN("Journeyman", "🛠️", 500, 2000, 0xFF10B981),
    EXPERT("Expert", "🎖️", 2000, 5000, 0xFFA855F7),
    MASTER("Master", "👑", 5000, 9999, 0xFFFF8C00),
    OUTLIER_LEGEND("10K Outlier Legend", "🏆", 10000, Int.MAX_VALUE, 0xFFFFD700);

    companion object {
        fun fromHours(hours: Float): MasteryTier {
            val h = hours.toInt()
            return values().findLast { h >= it.minHours } ?: NOVICE
        }
    }
}

data class SkillMastery(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val emoji: String,
    val dimension: DimensionType,
    val targetHours: Int = 10000,
    val accumulatedMinutes: Int = 0,
    val streakDays: Int = 0,
    val lastPracticedDate: String? = null,
    val sessionsCount: Int = 0,
    val totalXpEarned: Int = 0
) {
    val totalHours: Float get() = accumulatedMinutes / 60f
    val currentTier: MasteryTier get() = MasteryTier.fromHours(totalHours)
    val progressPercentage: Float get() = (totalHours / targetHours.toFloat()).coerceIn(0f, 1f)
    val tenKProgressPercentage: Float get() = (totalHours / 10000f).coerceIn(0f, 1f)
}

data class SkillLogSession(
    val id: String = UUID.randomUUID().toString(),
    val skillId: String,
    val minutes: Int,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val xpGranted: Int = (minutes * 25) / 60
)
