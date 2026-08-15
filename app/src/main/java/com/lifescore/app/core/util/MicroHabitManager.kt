package com.lifescore.app.core.util

import com.lifescore.app.domain.model.DimensionType

data class ChainNode(
    val dayNumber: Int,
    val isCompleted: Boolean,
    val isToday: Boolean,
    val isMissed: Boolean
)

data class MicroHabitChallenge(
    val id: String,
    val title: String,
    val description: String,
    val dimension: DimensionType,
    val currentDay: Int,
    val totalDays: Int = 30,
    val xpReward: Int = 500,
    val isJoined: Boolean = false,
    val isCompleted: Boolean = false
)

object MicroHabitManager {

    fun generate30DayChain(currentStreak: Int): List<ChainNode> {
        val totalDays = 30
        val todayIndex = 14 // middle of current 30-day cycle for visualization

        return (1..totalDays).map { day ->
            val isToday = day == todayIndex
            val isCompleted = when {
                day < todayIndex && day >= (todayIndex - currentStreak) -> true
                day < (todayIndex - currentStreak) -> day % 4 != 0 // occasional completed historical
                day == todayIndex -> false // Pending for today until completed
                else -> false // Future days
            }
            val isMissed = day < (todayIndex - currentStreak) && !isCompleted

            ChainNode(
                dayNumber = day,
                isCompleted = isCompleted,
                isToday = isToday,
                isMissed = isMissed
            )
        }
    }

    fun getDefault30DayChallenges(): List<MicroHabitChallenge> {
        return listOf(
            MicroHabitChallenge(
                id = "ch_hydr_30",
                title = "30-Day Morning Hydration & 8k Steps",
                description = "Drink 500ml of water right upon waking and hit 8,000 daily steps.",
                dimension = DimensionType.FITNESS,
                currentDay = 12,
                totalDays = 30,
                xpReward = 500,
                isJoined = true
            ),
            MicroHabitChallenge(
                id = "ch_mind_30",
                title = "30-Day Mindful Breathwork & RAM Dump",
                description = "Practice 4-7-8 box breathing for 5 minutes before checking morning notifications.",
                dimension = DimensionType.MENTAL_HEALTH,
                currentDay = 8,
                totalDays = 30,
                xpReward = 450,
                isJoined = true
            ),
            MicroHabitChallenge(
                id = "ch_learn_30",
                title = "30-Day 20-Min Deep Reading Immersion",
                description = "Read 1 chapter of a non-fiction or skill-building book every evening.",
                dimension = DimensionType.LEARNING,
                currentDay = 19,
                totalDays = 30,
                xpReward = 600,
                isJoined = true
            ),
            MicroHabitChallenge(
                id = "ch_wealth_30",
                title = "30-Day Zero Impulse Spending Challenge",
                description = "Log every expense and wait 48 hours before any non-essential purchase.",
                dimension = DimensionType.WEALTH,
                currentDay = 0,
                totalDays = 30,
                xpReward = 550,
                isJoined = false
            ),
            MicroHabitChallenge(
                id = "ch_career_30",
                title = "30-Day 45-Min Morning Deep Work Block",
                description = "Complete your #1 most critical career task before opening your inbox.",
                dimension = DimensionType.CAREER,
                currentDay = 0,
                totalDays = 30,
                xpReward = 600,
                isJoined = false
            )
        )
    }
}
