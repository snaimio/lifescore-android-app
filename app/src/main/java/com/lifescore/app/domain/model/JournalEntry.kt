package com.lifescore.app.domain.model

enum class JournalMood(
    val emoji: String,
    val title: String,
    val colorHex: Long
) {
    EXCITED("🔥", "Unstoppable", 0xFFFF5722),
    HAPPY("😊", "Grateful & High Energy", 0xFF4CAF50),
    NEUTRAL("😐", "Focused & Steady", 0xFF00ACC1),
    STRESSED("😔", "Friction / Challenged", 0xFFFF9800),
    TIRED("😴", "Depleted / Need Rest", 0xFF9E9E9E)
}

data class JournalEntry(
    val id: String,
    val dateIso: String,
    val mood: JournalMood,
    val textContent: String,
    val dimensionTag: DimensionType,
    val aiReflection: String? = null,
    val audioDurationSeconds: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
