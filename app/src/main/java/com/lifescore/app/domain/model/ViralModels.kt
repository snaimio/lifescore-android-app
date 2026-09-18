package com.lifescore.app.domain.model

import com.lifescore.app.core.designsystem.components.LifeIllustrations

enum class HeroArchetype(
    val displayName: String,
    val title: String,
    val description: String,
    val primaryDimension: DimensionType,
    val iconEmoji: String,
    val baseColorHex: Long
) {
    ARCHITECT(
        displayName = "The Architect",
        title = "Master of Systems & Order",
        description = "Turns chaos into scalable workflows, structured systems, and compounding habits.",
        primaryDimension = DimensionType.CAREER,
        iconEmoji = "🏛️",
        baseColorHex = 0xFF5B7BA8
    ),
    SAGE(
        displayName = "The Sage",
        title = "Seeker of Wisdom",
        description = "Cultivates deep contemplation, mental clarity, and foundational truths.",
        primaryDimension = DimensionType.LEARNING,
        iconEmoji = "📜",
        baseColorHex = 0xFF7B6BA8
    ),
    WARRIOR(
        displayName = "The Warrior",
        title = "Relentless Tactical Force",
        description = "Leads through physical discipline, unwavering grit, and high somatic endurance.",
        primaryDimension = DimensionType.FITNESS,
        iconEmoji = "⚔️",
        baseColorHex = 0xFFE85D5D
    ),
    VISIONARY(
        displayName = "The Visionary",
        title = "Strategic Futurist",
        description = "Sees 10 years ahead, rallies world-class ambition, and launches 0-to-1 breakthroughs.",
        primaryDimension = DimensionType.CAREER,
        iconEmoji = "🚀",
        baseColorHex = 0xFFD4A24C
    ),
    SCHOLAR(
        displayName = "The Scholar",
        title = "Guardian of Knowledge",
        description = "Synthesizes dense literature and deliberate practice to build intellectual mastery.",
        primaryDimension = DimensionType.LEARNING,
        iconEmoji = "📖",
        baseColorHex = 0xFF5B7BA8
    ),
    CREATOR(
        displayName = "The Creator",
        title = "Aesthetic Pioneer",
        description = "Transmutes raw inspiration into captivating design, culture, and novel output.",
        primaryDimension = DimensionType.CAREER,
        iconEmoji = "⚡",
        baseColorHex = 0xFFE08556
    ),
    NOMAD(
        displayName = "The Nomad",
        title = "Vital Pathfinder",
        description = "Thrives in nature, somatic vitality, outdoor exploration, and physical health.",
        primaryDimension = DimensionType.HEALTH,
        iconEmoji = "🧭",
        baseColorHex = 0xFF6BA89C
    ),
    CATALYST(
        displayName = "The Catalyst",
        title = "Beacon of Empathy",
        description = "Elevates collective potential, builds deep trust, and nurtures psychological safety.",
        primaryDimension = DimensionType.RELATIONSHIPS,
        iconEmoji = "🌿",
        baseColorHex = 0xFFE85D5D
    ),
    EXPLORER(
        displayName = "The Nomad",
        title = "Vital Pathfinder",
        description = "Thrives in nature, somatic vitality, outdoor exploration, and physical health.",
        primaryDimension = DimensionType.HEALTH,
        iconEmoji = "🧭",
        baseColorHex = 0xFF6BA89C
    ),
    HEALER(
        displayName = "The Catalyst",
        title = "Beacon of Empathy",
        description = "Elevates collective potential, builds deep trust, and nurtures psychological safety.",
        primaryDimension = DimensionType.RELATIONSHIPS,
        iconEmoji = "🌿",
        baseColorHex = 0xFFE85D5D
    );

    fun getIllustration(): LifeIllustrations = when (this) {
        ARCHITECT -> LifeIllustrations.ArchetypeArchitect
        SAGE -> LifeIllustrations.ArchetypeSage
        WARRIOR -> LifeIllustrations.ArchetypeWarrior
        VISIONARY -> LifeIllustrations.ArchetypeVisionary
        SCHOLAR -> LifeIllustrations.ArchetypeScholar
        CREATOR -> LifeIllustrations.ArchetypeCreator
        NOMAD, EXPLORER -> LifeIllustrations.ArchetypeNomad
        CATALYST, HEALER -> LifeIllustrations.ArchetypeCatalyst
    }

    companion object {
        fun fromLevel(level: Int): HeroArchetype {
            return when {
                level >= 35 -> VISIONARY
                level >= 30 -> ARCHITECT
                level >= 25 -> SCHOLAR
                level >= 20 -> CREATOR
                level >= 15 -> SAGE
                level >= 10 -> CATALYST
                level >= 5 -> NOMAD
                else -> WARRIOR
            }
        }
    }
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
