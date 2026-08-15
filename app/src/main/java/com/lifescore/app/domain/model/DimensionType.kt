package com.lifescore.app.domain.model

enum class DimensionType(
    val displayName: String,
    val iconName: String,
    val baseColorHex: Long,
    val description: String
) {
    HEALTH("Health", "favorite", 0xFF4CAF50, "Sleep, nutrition & physical wellness"),
    WEALTH("Wealth", "account_balance_wallet", 0xFFFFB300, "Budgeting, investing & savings"),
    RELATIONSHIPS("Relationships", "people", 0xFFE91E63, "Family, partner & deep bonds"),
    CAREER("Career", "work", 0xFF2196F3, "Projects, milestones & leadership"),
    LEARNING("Learning", "menu_book", 0xFF9C27B0, "Books, coding & new skills"),
    FITNESS("Fitness", "fitness_center", 0xFFFF5722, "Workouts, strength & endurance"),
    MENTAL_HEALTH("Mental Health", "self_improvement", 0xFF00BCD4, "Meditation, journaling & calm"),
    SOCIAL_LIFE("Social Life", "celebration", 0xFFFF4081, "Events, hobbies & friendships")
}
