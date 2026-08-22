package com.lifescore.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lifescore.app.domain.model.DimensionType

enum class MoodType(val emoji: String, val label: String, val scoreValue: Int) {
    ECSTATIC("🤩", "Ecstatic & Energized", 5),
    HAPPY("😊", "Happy & Positive", 4),
    CALM("😌", "Peaceful & Balanced", 3),
    ANXIOUS("😰", "Anxious & Stressed", 2),
    LOW("😔", "Exhausted & Down", 1)
}

enum class TreeType(val emoji: String, val displayName: String, val requiredMinutes: Int) {
    BONSAI("🪴", "Zen Bonsai", 15),
    OAK("🌳", "Majestic Oak", 25),
    PINE("🌲", "Highland Pine", 45),
    REDWOOD("🌴", "Ancient Redwood", 60),
    GOLDEN_TREE("✨", "Golden Bodhi", 90)
}

@Entity(tableName = "book_summary_progress")
data class BookSummaryProgressEntity(
    @PrimaryKey val bookId: String,
    val userId: String = "default_user",
    val isCompleted: Boolean = false,
    val isBookmarked: Boolean = false,
    val completedKeyTakeawaysCount: Int = 0,
    val appliedQuestCompleted: Boolean = false,
    val lastReadChapterIndex: Int = 0,
    val lastReadTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_growth_progress")
data class DailyGrowthProgressEntity(
    @PrimaryKey val sessionId: Int,
    val userId: String = "default_user",
    val dateIso: String,
    val isCompleted: Boolean = false,
    val journalReflection: String = "",
    val actionItemCompleted: Boolean = false,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "default_user",
    val durationMinutes: Int,
    val dimensionTag: DimensionType = DimensionType.CAREER,
    val treeType: TreeType = TreeType.OAK,
    val wasSuccessful: Boolean = true,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "mood_logs")
data class MoodLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "default_user",
    val mood: MoodType,
    val energyLevel: Int, // 1 to 10
    val stressLevel: Int, // 1 to 10
    val factorTags: String, // comma-separated e.g. "Sleep,Exercise,Work"
    val note: String = "",
    val dateIso: String,
    val timestamp: Long = System.currentTimeMillis()
)
