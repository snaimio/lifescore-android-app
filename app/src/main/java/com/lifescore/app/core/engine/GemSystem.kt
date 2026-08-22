package com.lifescore.app.core.engine

import java.util.UUID

data class GemTransaction(
    val id: String = UUID.randomUUID().toString(),
    val amount: Int,
    val type: GemTransactionType,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class GemTransactionType {
    EARNED,    // From gameplay, quests, streaks
    PURCHASED, // Bought with real money via Play Store
    SPENT      // Used to unlock cosmetics / themes
}

data class GemPackage(
    val id: String,
    val name: String,
    val gemsCount: Int,
    val priceFormatted: String,
    val bonusGems: Int = 0,
    val iconEmoji: String = "💎",
    val isPopular: Boolean = false,
    val badgeLabel: String? = null
)

object GemSystem {

    val availablePackages: List<GemPackage> = listOf(
        GemPackage("gems_pouch", "Pouch of Gems", 50, "$1.99", iconEmoji = "💎"),
        GemPackage("gems_chest", "Chest of Gems", 150, "$4.99", bonusGems = 20, iconEmoji = "💎", isPopular = true, badgeLabel = "BEST VALUE"),
        GemPackage("gems_vault", "Vault of Gems", 500, "$14.99", bonusGems = 100, iconEmoji = "👑", badgeLabel = "+20% BONUS"),
        GemPackage("gems_trove", "Legendary Trove", 1200, "$29.99", bonusGems = 350, iconEmoji = "✨", badgeLabel = "MAX SAVINGS")
    )

    fun earnGemsForQuest(questCompleted: Int): Int {
        return when {
            questCompleted >= 30 -> 30
            questCompleted >= 10 -> 15
            questCompleted >= 5 -> 10
            questCompleted >= 1 -> 5
            else -> 0
        }
    }

    fun earnGemsForStreak(streakDays: Int): Int {
        return when {
            streakDays >= 30 -> 50
            streakDays >= 14 -> 30
            streakDays >= 7 -> 20
            streakDays >= 3 -> 10
            streakDays >= 1 -> 5
            else -> 0
        }
    }

    fun earnGemsForTaskCompletion(tasksCompleted: Int): Int {
        return tasksCompleted / 5 // 1 gem per 5 tasks completed
    }
}
