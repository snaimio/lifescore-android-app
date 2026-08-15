package com.lifescore.app.core.util

object StreakShieldManager {

    const val MAX_SHIELDS_PER_MONTH = 3
    const val SHIELD_PRICE_FORMATTED = "$2.99"

    fun purchaseShield(currentShields: Int): Pair<Boolean, Int> {
        return if (currentShields < MAX_SHIELDS_PER_MONTH) {
            Pair(true, currentShields + 1)
        } else {
            Pair(false, currentShields)
        }
    }

    fun consumeShield(currentShields: Int): Pair<Boolean, Int> {
        return if (currentShields > 0) {
            Pair(true, currentShields - 1)
        } else {
            Pair(false, 0)
        }
    }

    fun generateThankYouCaption(recipientName: String, sponsorName: String): String {
        return "💖 A huge thank you to my LifeScore Guardian @$sponsorName for sponsoring my Pro journey! Leveling up daily habits with balance and accountability: https://lifescore.app #GuardianProgram #LifeScore #LifeBalance"
    }

    fun getShieldStatusText(shieldsRemaining: Int): String {
        return "$shieldsRemaining/$MAX_SHIELDS_PER_MONTH Shields Active • Streak Protected 🔥"
    }
}
