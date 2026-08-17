package com.lifescore.app.domain.model

data class CharacterStats(
    val id: Long = 1,
    val strength: Int = 10,       // Physical output & Fitness
    val vitality: Int = 10,       // Health & Streak resilience
    val agility: Int = 10,        // Speed & Task efficiency
    val intelligence: Int = 10,   // Learning & Career mastery
    val perception: Int = 10,     // Mental Health & Awareness
    val availablePoints: Int = 5,
    val title: String = "Novice Seeker",
    val titleBonusDescription: String = "+5% XP from all Quests"
) {
    val totalStats: Int
        get() = strength + vitality + agility + intelligence + perception

    val combatPower: Int
        get() = (strength * 2.5 + vitality * 2.0 + agility * 2.2 + intelligence * 2.8 + perception * 2.1).toInt()
}

data class CharacterTitle(
    val id: String,
    val name: String,
    val requirement: String,
    val statBonus: String,
    val isUnlocked: Boolean = false,
    val isEquipped: Boolean = false
)

object TitleCatalog {
    val allTitles = listOf(
        CharacterTitle("novice", "Novice Seeker", "Default starter title", "+5% XP from all Quests", isUnlocked = true, isEquipped = true),
        CharacterTitle("iron_will", "Iron Vanguard", "Reach a 7-day streak", "+5 Vitality in Battles", isUnlocked = false),
        CharacterTitle("shadow_hunter", "Apex Hunter", "Complete 25 Daily Quests", "+10% Critical Quest XP", isUnlocked = false),
        CharacterTitle("grand_master", "Grand Strategist", "Reach Level 5 in Career", "+8 Intelligence bonus", isUnlocked = false),
        CharacterTitle("monarch", "Shadow Sovereign", "Achieve 800+ Overall LifeScore", "+20% All Rewards", isUnlocked = false)
    )
}
