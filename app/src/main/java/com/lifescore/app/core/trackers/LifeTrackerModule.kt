package com.lifescore.app.core.trackers

import com.lifescore.app.domain.model.DimensionType

enum class TrackerType(
    val id: String,
    val title: String,
    val dimension: DimensionType,
    val emoji: String,
    val unit: String,
    val defaultGoal: Float,
    val quickAddValues: List<Float>,
    val xpReward: Int,
    val description: String
) {
    HYDRATION(
        id = "hydration",
        title = "Hydration Tracker",
        dimension = DimensionType.HEALTH,
        emoji = "💧",
        unit = "ml",
        defaultGoal = 2500f,
        quickAddValues = listOf(250f, 500f, 750f),
        xpReward = 20,
        description = "Log daily water intake, hydration intervals, and liquid balance."
    ),
    NUTRITION(
        id = "nutrition",
        title = "Nutrition & Macros",
        dimension = DimensionType.HEALTH,
        emoji = "🥗",
        unit = "kcal",
        defaultGoal = 2200f,
        quickAddValues = listOf(350f, 600f, 850f),
        xpReward = 25,
        description = "Track balanced daily caloric intake, clean fuel, and macronutrient targets."
    ),
    SLEEP(
        id = "sleep",
        title = "Sleep & Recovery",
        dimension = DimensionType.HEALTH,
        emoji = "🌙",
        unit = "hrs",
        defaultGoal = 8.0f,
        quickAddValues = listOf(6.5f, 7.5f, 8.5f),
        xpReward = 25,
        description = "Log nocturnal restorative sleep duration, REM quality, and sleep consistency."
    ),
    VITALS(
        id = "vitals",
        title = "Blood Pressure & Vitals",
        dimension = DimensionType.HEALTH,
        emoji = "🩺",
        unit = "bpm",
        defaultGoal = 72f,
        quickAddValues = listOf(65f, 72f, 80f),
        xpReward = 20,
        description = "Track resting heart rate, blood pressure equilibrium, and metabolic stability."
    ),
    STEPS(
        id = "steps",
        title = "Steps & Movement",
        dimension = DimensionType.FITNESS,
        emoji = "👟",
        unit = "steps",
        defaultGoal = 10000f,
        quickAddValues = listOf(1500f, 3000f, 5000f),
        xpReward = 30,
        description = "Daily ambulation, active mileage, and NEAT non-exercise activity thermogenesis."
    ),
    WORKOUTS(
        id = "workouts",
        title = "Workouts & Training",
        dimension = DimensionType.FITNESS,
        emoji = "🏋️",
        unit = "mins",
        defaultGoal = 45f,
        quickAddValues = listOf(15f, 30f, 45f, 60f),
        xpReward = 35,
        description = "Log strength lifting, cardiovascular endurance, mobility, and HIIT."
    ),
    WEIGHT(
        id = "weight",
        title = "Weight & Body Comp",
        dimension = DimensionType.FITNESS,
        emoji = "⚖️",
        unit = "kg",
        defaultGoal = 75f,
        quickAddValues = listOf(0.1f, 0.5f, 1.0f),
        xpReward = 20,
        description = "Monitor body composition trajectory, muscle mass, and metabolic trendlines."
    ),
    READING(
        id = "reading",
        title = "Daily Reading Log",
        dimension = DimensionType.LEARNING,
        emoji = "📚",
        unit = "pages",
        defaultGoal = 20f,
        quickAddValues = listOf(5f, 10f, 20f),
        xpReward = 25,
        description = "Track daily non-fiction book immersion, wisdom synthesis, and literature."
    ),
    SKILL_MASTERY(
        id = "skill_mastery",
        title = "10,000-Hour Mastery",
        dimension = DimensionType.LEARNING,
        emoji = "⏳",
        unit = "hrs",
        defaultGoal = 2.0f,
        quickAddValues = listOf(0.5f, 1.0f, 2.0f),
        xpReward = 40,
        description = "Log deliberate deep-practice hours toward world-class domain mastery."
    ),
    JOURNAL(
        id = "journal",
        title = "Journal & Reflections",
        dimension = DimensionType.LEARNING,
        emoji = "📖",
        unit = "entries",
        defaultGoal = 1.0f,
        quickAddValues = listOf(1.0f),
        xpReward = 20,
        description = "Daily cognitive processing, evening retrospectives, and mental clarity logs."
    ),
    OKRS(
        id = "okrs",
        title = "Goals & OKR Tracker",
        dimension = DimensionType.CAREER,
        emoji = "🎯",
        unit = "%",
        defaultGoal = 100f,
        quickAddValues = listOf(10f, 25f, 50f),
        xpReward = 35,
        description = "Manage Objectives and Key Results, quarterly milestones, and career deliverables."
    ),
    ROUTINES(
        id = "routines",
        title = "Routines & Productivity",
        dimension = DimensionType.CAREER,
        emoji = "⚡",
        unit = "rituals",
        defaultGoal = 3f,
        quickAddValues = listOf(1f, 2f, 3f),
        xpReward = 25,
        description = "Maintain morning activation, focus pomodoros, and evening shutdown rituals."
    ),
    SOCIAL(
        id = "social",
        title = "Relationships & Tribe",
        dimension = DimensionType.RELATIONSHIPS,
        emoji = "💞",
        unit = "connections",
        defaultGoal = 2f,
        quickAddValues = listOf(1f, 2f),
        xpReward = 25,
        description = "Track meaningful calls, deep conversations, family check-ins, and squad syncs."
    ),
    FINANCE(
        id = "finance",
        title = "Financial & Wealth Log",
        dimension = DimensionType.WEALTH,
        emoji = "💰",
        unit = "$",
        defaultGoal = 50f,
        quickAddValues = listOf(10f, 25f, 50f, 100f),
        xpReward = 30,
        description = "Daily savings automation, budget discipline, investment audits, and net worth."
    ),
    MINDFULNESS(
        id = "mindfulness",
        title = "Mindfulness & Zen",
        dimension = DimensionType.MENTAL_HEALTH,
        emoji = "🧘",
        unit = "mins",
        defaultGoal = 15f,
        quickAddValues = listOf(5f, 10f, 15f, 20f),
        xpReward = 25,
        description = "Log meditation stillness, breathwork sessions, stress alleviation, and peace."
    );

    companion object {
        fun getAllTrackers(): List<TrackerType> = values().toList()
        fun getTrackersByDimension(dimension: DimensionType): List<TrackerType> =
            values().filter { it.dimension == dimension }
        fun fromId(id: String): TrackerType? = values().firstOrNull { it.id.equals(id, ignoreCase = true) }
    }
}

data class TrackerStatus(
    val type: TrackerType,
    val currentValue: Float,
    val targetGoal: Float,
    val streakDays: Int = 1,
    val todayCompleted: Boolean = false,
    val progressPercentage: Float = (currentValue / targetGoal).coerceIn(0f, 1f),
    val lastUpdatedIso: String = "Today"
)
