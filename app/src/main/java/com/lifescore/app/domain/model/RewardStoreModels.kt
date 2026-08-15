package com.lifescore.app.domain.model

import java.util.UUID

enum class StoreCategory(val displayName: String, val icon: String) {
    CUSTOM_REWARD("Custom Reward", "🎁"),
    THEME("Visual Theme", "🎨"),
    AVATAR("Hero Persona", "👑"),
    BOOSTER("Power Booster", "⚡")
}

data class CustomUserReward(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val emoji: String = "🎁",
    val coinCost: Int = 150,
    val unlockCondition: String = "Earn 150 LifeCoins",
    val redemptionCount: Int = 0,
    val isRedeemed: Boolean = false,
    val lastRedeemedAt: Long? = null
)

data class StoreProductItem(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val category: StoreCategory,
    val coinCost: Int,
    val isPurchased: Boolean = false,
    val durationHours: Int? = null, // For time-limited boosters
    val badgeLabel: String? = null
)

data class RewardTransaction(
    val id: String = UUID.randomUUID().toString(),
    val itemTitle: String,
    val category: StoreCategory,
    val coinsAmount: Int, // Negative for purchases, positive for earns
    val timestamp: Long = System.currentTimeMillis(),
    val formattedDate: String = "",
    val notes: String = ""
)

data class BoosterState(
    val isDoubleXpActive: Boolean = false,
    val doubleXpExpiresAt: Long = 0,
    val streakShieldsAvailable: Int = 2,
    val instantSkipPassesAvailable: Int = 1
)
