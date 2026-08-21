package com.lifescore.app.domain.model

enum class DimensionType(
    val id: Int,
    val displayName: String,
    val iconName: String,
    val baseColorHex: Long,
    val description: String
) {
    HEALTH(1, "Health", "favorite", 0xFF4CAF50, "Sleep, nutrition & physical wellness"),
    WEALTH(2, "Wealth", "account_balance_wallet", 0xFFFFB300, "Budgeting, investing & savings"),
    RELATIONSHIPS(3, "Relationships", "people", 0xFFE91E63, "Family, partner & deep bonds"),
    CAREER(4, "Career", "work", 0xFF2196F3, "Projects, milestones & leadership"),
    LEARNING(5, "Learning", "menu_book", 0xFF9C27B0, "Books, coding & new skills"),
    FITNESS(6, "Fitness", "fitness_center", 0xFFFF5722, "Workouts, strength & endurance"),
    MENTAL_HEALTH(7, "Mental Health", "self_improvement", 0xFF00BCD4, "Meditation, journaling & calm"),
    SOCIAL_LIFE(8, "Social Life", "celebration", 0xFFFF4081, "Events, hobbies & friendships");

    companion object {
        fun fromId(id: Int): DimensionType {
            return values().find { it.id == id } ?: HEALTH
        }
    }
}
