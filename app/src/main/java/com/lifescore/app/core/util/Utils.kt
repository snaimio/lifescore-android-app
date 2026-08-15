package com.lifescore.app.core.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun getTodayIso(): String {
        return isoFormat.format(Date())
    }

    fun formatDisplayDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

object LevelCalculator {
    private const val BASE_XP = 100
    private const val MULTIPLIER = 1.25

    fun calculateLevel(totalXp: Int): Int {
        var level = 1
        var requiredXp = BASE_XP
        var accumulatedXp = 0

        while (totalXp >= accumulatedXp + requiredXp) {
            accumulatedXp += requiredXp
            level++
            requiredXp = (requiredXp * MULTIPLIER).toInt()
        }
        return level
    }

    fun calculateLevelProgress(totalXp: Int): Float {
        var requiredXp = BASE_XP
        var accumulatedXp = 0

        while (totalXp >= accumulatedXp + requiredXp) {
            accumulatedXp += requiredXp
            requiredXp = (requiredXp * MULTIPLIER).toInt()
        }
        val currentLevelXp = totalXp - accumulatedXp
        return (currentLevelXp.toFloat() / requiredXp.toFloat()).coerceIn(0f, 1f)
    }

    fun getTitleForLevel(level: Int): String {
        return when {
            level < 3 -> "Novice Seeker"
            level < 6 -> "Disciplined Apprentice"
            level < 10 -> "Harmonized Striver"
            level < 15 -> "Master of Equilibrium"
            level < 25 -> "Grand Ascendant"
            else -> "Enlightened Sovereign"
        }
    }
}

object ScoreEngine {
    fun calculateDimensionScore(completedTasks: Int, totalTasks: Int): Int {
        if (totalTasks == 0) return 50 // baseline neutral score
        val percentage = (completedTasks.toFloat() / totalTasks.toFloat())
        return (percentage * 100).toInt().coerceIn(0, 100)
    }

    fun calculateOverallLifeScore(dimensionScores: Map<com.lifescore.app.domain.model.DimensionType, Int>): Int {
        if (dimensionScores.isEmpty()) return 500
        val sum = dimensionScores.values.sum()
        val average = sum.toFloat() / dimensionScores.size
        return (average * 10).toInt().coerceIn(0, 1000)
    }
}
