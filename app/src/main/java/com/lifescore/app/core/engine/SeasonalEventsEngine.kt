package com.lifescore.app.core.engine

import com.lifescore.app.domain.model.DimensionType

data class SeasonalQuest(
    val id: String,
    val title: String,
    val description: String,
    val dimension: DimensionType,
    val xpReward: Int,
    val gemReward: Int,
    val isCompleted: Boolean = false
)

data class SeasonalEvent(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val bannerEmoji: String,
    val themeColorHex: Long,
    val seasonName: String,
    val daysRemaining: Int,
    val bossName: String,
    val bossMaxHp: Int,
    val bossCurrentHp: Int,
    val xpMultiplier: Float = 2.0f,
    val exclusiveRewardName: String,
    val exclusiveRewardEmoji: String,
    val eventQuests: List<SeasonalQuest>
)

object SeasonalEventsEngine {

    val activeEvent: SeasonalEvent = SeasonalEvent(
        id = "event_summer_solstice_2026",
        title = "Solar Solstice Awakening",
        subtitle = "Limited 14-Day Global Community Event",
        description = "Harness the power of the longest days. Earn 2x XP on all physical and mindfulness habits, defeat the Shadow of Procrastination with community raids, and unlock the Solar Solstice Halo!",
        bannerEmoji = "☀️",
        themeColorHex = 0xFFFF9800,
        seasonName = "Summer Season 2026",
        daysRemaining = 11,
        bossName = "Ignis: The Sunfire Titan",
        bossMaxHp = 100000,
        bossCurrentHp = 42500,
        xpMultiplier = 2.0f,
        exclusiveRewardName = "Solar Solstice Aura & Title",
        exclusiveRewardEmoji = "✨",
        eventQuests = listOf(
            SeasonalQuest("sq_1", "Morning Sunrise Walk (15 mins)", "Absorb natural sunlight within 1 hour of waking", DimensionType.HEALTH, 100, 10),
            SeasonalQuest("sq_2", "Hydration Sprint (2.5L)", "Fuel your body through summer heat", DimensionType.HEALTH, 80, 5),
            SeasonalQuest("sq_3", "Deep Work at Golden Hour", "Complete 45 mins of distraction-free focus", DimensionType.CAREER, 120, 15),
            SeasonalQuest("sq_4", "Evening Gratitude Journaling", "Reflect on 3 high points under the summer night sky", DimensionType.MENTAL_HEALTH, 90, 8)
        )
    )

    fun calculateBossDamage(questXp: Int): Int {
        return (questXp * 1.5).toInt()
    }
}
