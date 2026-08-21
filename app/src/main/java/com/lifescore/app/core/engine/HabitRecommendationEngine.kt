package com.lifescore.app.core.engine

import com.lifescore.app.data.Habit
import com.lifescore.app.data.HabitData
import com.lifescore.app.domain.model.DimensionType

object HabitRecommendationEngine {

    /**
     * Recommends habits for a specific dimension based on the user's current score.
     */
    fun recommendHabitsForDimension(dimension: DimensionType, score: Int): List<Habit> {
        val habits = HabitData.getHabitsByDimension(dimension)
        return when {
            score < 40 -> habits.filter { it.difficulty == "Easy" }.ifEmpty { habits }.take(5)
            score < 75 -> habits.filter { it.difficulty in listOf("Easy", "Medium") }.ifEmpty { habits }.take(5)
            else -> habits.take(5)
        }
    }

    /**
     * Generates a tailored daily quest list from the 100-habit catalog:
     * - Weighted toward the weakest dimensions
     * - Respects the user's current level & streak
     */
    fun generateDailyQuests(
        dimensionScores: Map<DimensionType, Int>,
        userLevel: Int = 1,
        userStreak: Int = 0,
        questLimit: Int = 5
    ): List<Habit> {
        val weakest = dimensionScores.entries.sortedBy { it.value }.take(2)
        val habits = mutableListOf<Habit>()

        weakest.forEach { (dimension, score) ->
            habits.addAll(recommendHabitsForDimension(dimension, score).take(2))
        }

        val otherDimensions = dimensionScores.keys.filter { dim ->
            dim !in weakest.map { it.key }
        }

        otherDimensions.shuffled().forEach { dimension ->
            habits.addAll(HabitData.getHabitsByDimension(dimension).shuffled().take(1))
        }

        return habits.distinctBy { it.id }.take(questLimit)
    }

    /**
     * Formats an AI Coach recommendation string leveraging the 100-habit bank.
     */
    fun getHabitRecommendationText(dimension: DimensionType, score: Int): String {
        val recommended = recommendHabitsForDimension(dimension, score).take(3)
        return "🎯 **Recommended ${dimension.displayName} Habits (from 100-Habit Bank):**\n" +
                recommended.joinToString("\n") { "• **${it.title}** (${it.estimatedMinutes}m, +${it.xpReward} XP) — ${it.description}" }
    }
}
