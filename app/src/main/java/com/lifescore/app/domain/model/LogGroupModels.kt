package com.lifescore.app.domain.model

data class LogGroup(
    val id: String,
    val name: String,
    val description: String,
    val inviteCode: String,           // e.g. "LOGS-88X"
    val maxMembers: Int = 12,
    val members: List<LogGroupMember>,
    val adminUid: String,
    val themeColorHex: Long = 0xFF6366F1,
    val createdAt: Long = System.currentTimeMillis()
) {
    val isFull: Boolean get() = members.size >= maxMembers
    val availableSlots: Int get() = (maxMembers - members.size).coerceAtLeast(0)
}

data class LogGroupMember(
    val uid: String,
    val displayName: String,
    val avatarEmoji: String = "⚡",
    val streakDays: Int = 0,
    val todayClipsCount: Int = 0,
    val hasRecordedToday: Boolean = false,
    val lastRecordedAt: Long? = null
)

data class VlogComment(
    val id: String,
    val vlogId: String,
    val authorUid: String,
    val authorName: String,
    val authorEmoji: String = "⚡",
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class DailyStitchedVlog(
    val id: String,
    val date: String,                  // YYYY-MM-DD
    val ownerUid: String,
    val ownerName: String,
    val clips: List<MicroVlog>,        // Up to 30 clips @ 2s each
    val totalDurationSeconds: Double,  // e.g. 60.0
    val dominantDimension: DimensionType,
    val reactionCounts: Map<String, Int> = mapOf("🔥" to 0, "👏" to 0, "🚀" to 0, "❤️" to 0, "🤯" to 0, "⚡" to 0),
    val userReactions: Set<String> = emptySet(),
    val comments: List<VlogComment> = emptyList(),
    val xpEarned: Int = 150
)
