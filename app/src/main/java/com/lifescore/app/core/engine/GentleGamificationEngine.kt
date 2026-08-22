package com.lifescore.app.core.engine

data class StreakShieldState(
    val shieldsAvailable: Int = 2,
    val isFreezeActiveToday: Boolean = false,
    val lastProtectedDateIso: String? = null,
    val totalFreezesUsed: Int = 1
)

data class CompassionReframe(
    val title: String,
    val emoji: String,
    val message: String,
    val resilienceXpBonus: Int = 15
)

object GentleGamificationEngine {

    val supportiveReframes: List<CompassionReframe> = listOf(
        CompassionReframe(
            title = "Rest Builds Compounding Strength",
            emoji = "🛡️",
            message = "One missed day doesn't erase weeks of consistency. Top athletes take deliberate recovery days.",
            resilienceXpBonus = 15
        ),
        CompassionReframe(
            title = "Identity Over Perfection",
            emoji = "🌱",
            message = "Missing once is an accident. Never miss twice. Your identity as an achiever remains unbroken.",
            resilienceXpBonus = 20
        ),
        CompassionReframe(
            title = "Zero Anxiety Philosophy",
            emoji = "🧘",
            message = "LifeScore never damages your avatar's health or punishes your life. We celebrate every step forward.",
            resilienceXpBonus = 15
        )
    )

    fun applyStreakFreeze(currentState: StreakShieldState): Pair<StreakShieldState, Boolean> {
        if (currentState.shieldsAvailable <= 0) {
            return Pair(currentState, false)
        }
        val newState = currentState.copy(
            shieldsAvailable = currentState.shieldsAvailable - 1,
            isFreezeActiveToday = true,
            totalFreezesUsed = currentState.totalFreezesUsed + 1
        )
        return Pair(newState, true)
    }

    fun getRandomReframe(): CompassionReframe {
        return supportiveReframes.random()
    }
}
