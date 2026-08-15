package com.lifescore.app.domain.model

import java.util.UUID

enum class MemeCategory(val title: String, val icon: String) {
    STREAK_VICTORY("Streak Victory", "🔥"),
    DIMENSION_STRUGGLE("Dimension Gap", "📉"),
    EXPECTATION_VS_REALITY("Expectation vs Reality", "🤡"),
    DEEP_WORK_GRIND("Deep Work Grind", "⚡"),
    MEDITATION_ZEN("Zen Ascendance", "🧘")
}

data class MemeTemplate(
    val id: String,
    val title: String,
    val category: MemeCategory,
    val topTextDefault: String,
    val bottomTextDefault: String,
    val emojiArt: String,
    val viralTagline: String,
    val backgroundGradientColors: List<Long>
)

data class GeneratedMeme(
    val id: String = UUID.randomUUID().toString(),
    val templateId: String,
    val topText: String,
    val bottomText: String,
    val emojiArt: String,
    val userScore: Int = 780,
    val userStreak: Int = 14,
    val targetDimension: DimensionType = DimensionType.FITNESS,
    val createdAt: Long = System.currentTimeMillis()
)
