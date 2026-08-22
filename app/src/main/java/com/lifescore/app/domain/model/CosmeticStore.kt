package com.lifescore.app.domain.model

enum class CosmeticCategory(val displayName: String, val iconEmoji: String) {
    AVATAR("Avatar Skins", "👤"),
    THEME("App Themes", "🎨"),
    BADGE("Profile Badges", "💎"),
    ACCESSORY("Accessories", "👑"),
    SPECIAL("Seasonal & Limited", "✨")
}

data class CosmeticItem(
    val id: String,
    val name: String,
    val description: String,
    val emoji: String,
    val gemCost: Int,
    val category: CosmeticCategory,
    val isLimited: Boolean = false,
    val season: String? = null,
    val isSupporterExclusive: Boolean = false,
    val isOwned: Boolean = false,
    val isEquipped: Boolean = false
)

object CosmeticStoreCatalog {

    val items: List<CosmeticItem> = listOf(
        // Avatar Skins
        CosmeticItem("skin_warrior", "Titan Warrior", "Battle-hardened armored hero", "⚔️", 100, CosmeticCategory.AVATAR),
        CosmeticItem("skin_wizard", "Archmage Sage", "Mystic scholar of high intellect", "🧙‍♂️", 150, CosmeticCategory.AVATAR),
        CosmeticItem("skin_rogue", "Shadow Rogue", "Stealthy assassin of habits", "🗡️", 120, CosmeticCategory.AVATAR),
        CosmeticItem("skin_cyber", "Cybernetic Titan", "Futuristic cyber-enhanced executor", "🤖", 200, CosmeticCategory.AVATAR),
        CosmeticItem("skin_sovereign", "Golden Sovereign", "Regal monarch of self-mastery", "👑", 250, CosmeticCategory.AVATAR, isSupporterExclusive = true),

        // Themes
        CosmeticItem("theme_cosmic", "Cosmic Night", "Deep slate indigo & golden celestial glow", "🌌", 200, CosmeticCategory.THEME),
        CosmeticItem("theme_forest", "Emerald Zen Forest", "Calming deep pine & mint green tranquility", "🌿", 150, CosmeticCategory.THEME),
        CosmeticItem("theme_neon", "Cyberpunk Neon", "High-voltage electric teal & obsidian", "⚡", 180, CosmeticCategory.THEME),
        CosmeticItem("theme_gold", "Royal Gold", "Warm amber luxury and prestige accents", "🏆", 220, CosmeticCategory.THEME, isSupporterExclusive = true),

        // Badges
        CosmeticItem("badge_supporter", "Supporter Patron", "Exclusive badge for LifeScore community patrons", "💎", 0, CosmeticCategory.BADGE, isSupporterExclusive = true),
        CosmeticItem("badge_legend", "Habit Legend", "Recognizes supreme consistency and grit", "🌟", 50, CosmeticCategory.BADGE),
        CosmeticItem("badge_centurion", "100-Day Centurion", "For heroic 100-day unbroken streaks", "🛡️", 100, CosmeticCategory.BADGE),

        // Accessories & Pets
        CosmeticItem("acc_phoenix", "Phoenix Companion", "A flame-feathered companion that roosts on your profile", "🔥", 180, CosmeticCategory.ACCESSORY),
        CosmeticItem("acc_halo", "Aura of Focus", "A radiant halo of pure deep-work concentration", "💫", 120, CosmeticCategory.ACCESSORY),

        // Special Items
        CosmeticItem("special_summer", "Solar Solstice Pack", "Limited-edition summer solstice aura and badges", "☀️", 300, CosmeticCategory.SPECIAL, isLimited = true, season = "Summer 2026")
    )
}
