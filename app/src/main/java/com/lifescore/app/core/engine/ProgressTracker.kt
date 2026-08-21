package com.lifescore.app.core.engine

data class ProgressData(
    val scoreProgress: Float,
    val habitCompletion: Float,
    val estimatedDaysToTarget: Int,
    val nextMilestone: String
)

object ProgressTracker {

    fun calculateDimensionProgress(
        currentScore: Int,
        targetScore: Int,
        habitsCompleted: Int,
        totalHabits: Int
    ): ProgressData {
        val scoreProgress = if (targetScore > 0) (currentScore.toFloat() / targetScore.toFloat()) * 100f else 0f
        val habitComp = if (totalHabits > 0) (habitsCompleted.toFloat() / totalHabits.toFloat()) * 100f else 0f
        val remainingPoints = (targetScore - currentScore).coerceAtLeast(0)
        val estimatedDays = ((remainingPoints / 5).coerceIn(1, 90))

        val milestone = when {
            currentScore < 50 -> "Bronze Foundation (50 pts)"
            currentScore < 100 -> "Silver Accelerator (100 pts)"
            currentScore < 150 -> "Gold Mastery (150 pts)"
            else -> "Platinum Equilibrium (200 pts)"
        }

        return ProgressData(
            scoreProgress = scoreProgress.coerceIn(0f, 100f),
            habitCompletion = habitComp.coerceIn(0f, 100f),
            estimatedDaysToTarget = estimatedDays,
            nextMilestone = milestone
        )
    }

    fun getMotivationalMessage(progress: Float): String {
        return when {
            progress < 25f -> "🚀 You're building the neuromuscular baseline! Every micro-win counts."
            progress < 50f -> "💪 Great momentum! Your daily habits are beginning to compound."
            progress < 75f -> "🌟 Incredible consistency! You're past the halfway mark to total mastery."
            progress < 95f -> "🔥 In the zone! Protect your streak for the final promotion push."
            else -> "🏆 Milestone Achieved! You have unlocked top-tier equilibrium."
        }
    }
}
