package com.lifescore.app.domain.model

enum class HeroArchetype(
    val displayName: String,
    val title: String,
    val description: String,
    val primaryDimension: DimensionType,
    val iconEmoji: String,
    val baseColorHex: Long
) {
    WARRIOR(
        displayName = "The Warrior",
        title = "Unyielding Force",
        description = "Masters fitness, physical endurance, and discipline.",
        primaryDimension = DimensionType.FITNESS,
        iconEmoji = "⚔️",
        baseColorHex = 0xFFFF5722
    ),
    SAGE(
        displayName = "The Sage",
        title = "Seeker of Truth",
        description = "Cultivates deep learning, mental clarity, and wisdom.",
        primaryDimension = DimensionType.LEARNING,
        iconEmoji = "📜",
        baseColorHex = 0xFF9C27B0
    ),
    EXPLORER(
        displayName = "The Explorer",
        title = "Pathfinder",
        description = "Thrives on health, outdoor adventure, and vitality.",
        primaryDimension = DimensionType.HEALTH,
        iconEmoji = "🧭",
        baseColorHex = 0xFF4CAF50
    ),
    CREATOR(
        displayName = "The Creator",
        title = "Visionary Builder",
        description = "Focuses on career breakthroughs, wealth, and mastery.",
        primaryDimension = DimensionType.CAREER,
        iconEmoji = "⚡",
        baseColorHex = 0xFF2196F3
    ),
    HEALER(
        displayName = "The Healer",
        title = "Beacon of Harmony",
        description = "Nurtures deep relationships, mental peace, and empathy.",
        primaryDimension = DimensionType.MENTAL_HEALTH,
        iconEmoji = "🌿",
        baseColorHex = 0xFF00BCD4
    )
}

data class CollectibleCard(
    val id: String,
    val title: String,
    val category: String, // "Strength", "Discipline", "Wisdom", "Focus"
    val quote: String,
    val unlockedAtLevel: Int,
    val isUnlocked: Boolean = false,
    val colorHex: Long = 0xFFFFD700
)

data class GuardianSponsorship(
    val sponsorId: String,
    val sponsorName: String,
    val recipientEmail: String,
    val monthsGifted: Int,
    val message: String
)

data class ReferralStatus(
    val referralCode: String = "LIFESCORE-HERO-77",
    val friendsInvited: Int = 2,
    val requiredForFreeMonth: Int = 3,
    val freeMonthsEarned: Int = 0
)
