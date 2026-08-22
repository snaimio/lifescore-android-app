package com.lifescore.app.core.engine

object GoldToGemsConverter {

    const val CONVERSION_RATE = 100 // 100 Gold = 1 Gem

    fun convertGoldToGems(gold: Int): Int {
        if (gold < CONVERSION_RATE) return 0
        return gold / CONVERSION_RATE
    }

    fun canConvertGold(gold: Int): Boolean {
        return gold >= CONVERSION_RATE
    }

    fun getConversionRate(): Int {
        return CONVERSION_RATE
    }

    fun getMinimumGold(): Int {
        return CONVERSION_RATE
    }

    fun calculateRemainingGold(gold: Int, gemsToObtain: Int): Int {
        val requiredGold = gemsToObtain * CONVERSION_RATE
        return (gold - requiredGold).coerceAtLeast(0)
    }
}
