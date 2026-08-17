package com.lifescore.app.domain.model

enum class QuestDifficulty(
    val rankLetter: String,
    val title: String,
    val xpMultiplier: Float,
    val statRewardPoints: Int,
    val badgeColorHex: Long
) {
    E("E", "Novice", 1.0f, 1, 0xFF8BC34A),
    D("D", "Apprentice", 1.5f, 2, 0xFF4CAF50),
    C("C", "Adept", 2.0f, 3, 0xFF03DAC6),
    B("B", "Elite", 3.0f, 5, 0xFF2196F3),
    A("A", "Master", 5.0f, 8, 0xFF9C27B0),
    S("S", "Monarch", 10.0f, 15, 0xFFFF9800);

    companion object {
        fun fromRankLetter(letter: String): QuestDifficulty {
            return values().find { it.rankLetter.equals(letter, ignoreCase = true) } ?: C
        }
    }
}
