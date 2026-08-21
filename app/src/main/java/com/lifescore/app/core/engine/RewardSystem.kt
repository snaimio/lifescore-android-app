package com.lifescore.app.core.engine

data class WeeklyReward(
    val xpEarned: Int,
    val coinsEarned: Int,
    val badgeEarned: String?,
    val message: String
)

object RewardSystem {

    fun calculateWeeklyReward(
        habitsCompleted: Int,
        totalHabits: Int,
        streakDays: Int
    ): WeeklyReward {
        val completionRate = if (totalHabits > 0) habitsCompleted.toFloat() / totalHabits.toFloat() else 0f
        val streakBonus = if (streakDays >= 7) 100 else 25

        val badge = when {
            streakDays >= 30 -> "👑 30-Day Unstoppable Streak"
            streakDays >= 14 -> "🚀 14-Day Consistency Master"
            streakDays >= 7 -> "🔥 7-Day Habit Champion"
            habitsCompleted >= 20 -> "💪 20 Micro-Habits Forge"
            else -> "🌱 Active Seeker"
        }

        val msg = when {
            completionRate >= 0.9f -> "🏆 Elite Consistency! You've dominated your transformation goals this week."
            completionRate >= 0.7f -> "🌟 Outstanding Progress! Your compounding daily habits are paying off."
            completionRate >= 0.5f -> "💪 Solid Execution! Maintain your rhythm into next week."
            else -> "📈 Small steps lead to massive life transformations. Keep showing up daily!"
        }

        return WeeklyReward(
            xpEarned = (habitsCompleted * 20) + streakBonus,
            coinsEarned = (habitsCompleted * 3) + (streakDays * 2),
            badgeEarned = badge,
            message = msg
        )
    }
}
