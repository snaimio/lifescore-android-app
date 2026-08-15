package com.lifescore.app.core.util

import java.util.Calendar
import java.util.TimeZone

enum class LeagueTier(
    val displayName: String,
    val minScore: Int,
    val maxScore: Int,
    val icon: String,
    val badgeColorHex: Long,
    val rewardTitle: String
) {
    DIAMOND("Diamond League", 900, 1000, "👑", 0xFF00E5FF, "Diamond Crown Badge"),
    PLATINUM("Platinum League", 750, 899, "💎", 0xFFC7D2FE, "Platinum Shield Badge"),
    GOLD("Gold League", 600, 749, "🥇", 0xFFFFD700, "Gold Medalist Badge"),
    SILVER("Silver League", 400, 599, "🥈", 0xFFE2E8F0, "Silver Crest Badge"),
    BRONZE("Bronze League", 0, 399, "🥉", 0xFFD97706, "Bronze Initiate Badge")
}

enum class RankZone(
    val label: String,
    val colorHex: Long,
    val indicator: String
) {
    PROMOTION("Promotion Zone", 0xFF4CAF50, "▲ Promoted"),
    SAFE("Safe Zone", 0xFF9E9E9E, "• Safe"),
    RELEGATION("Relegation Zone", 0xFFEF4444, "▼ Demoted")
}

object LeagueManager {

    fun getLeagueForScore(score: Int): LeagueTier {
        val clamped = score.coerceIn(0, 1000)
        return when {
            clamped >= 900 -> LeagueTier.DIAMOND
            clamped >= 750 -> LeagueTier.PLATINUM
            clamped >= 600 -> LeagueTier.GOLD
            clamped >= 400 -> LeagueTier.SILVER
            else -> LeagueTier.BRONZE
        }
    }

    fun getRankZone(rank: Int, totalEntries: Int): RankZone {
        return when {
            rank in 1..3 -> RankZone.PROMOTION
            totalEntries > 5 && rank > (totalEntries - 3) -> RankZone.RELEGATION
            else -> RankZone.SAFE
        }
    }

    fun getCountdownToSunday(): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // Sunday = 1, Saturday = 7
        val daysUntilSunday = (Calendar.SUNDAY - currentDayOfWeek + 7) % 7
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val remainingHours = 23 - currentHour
        val remainingMinutes = 59 - currentMinute

        return if (daysUntilSunday == 0) {
            "${remainingHours}h ${remainingMinutes}m left"
        } else {
            "${daysUntilSunday}d ${remainingHours}h left"
        }
    }
}
