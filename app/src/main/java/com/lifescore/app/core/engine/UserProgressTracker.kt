package com.lifescore.app.core.engine

enum class UserPhase(val title: String, val subtitle: String, val badgeEmoji: String) {
    NEW_USER("New Seeker", "Day 1-3 • Focused Foundations", "🌱"),
    EXPLORING("Explorer", "Week 2 • Expanding Horizons", "🧭"),
    ADVANCED("Achiever", "Week 3-4 • Holistic Growth", "⚡"),
    EXPERT("Life Master", "Month 1+ • Unlocked Complete OS", "👑")
}

data class UserProgress(
    val daysActive: Int,
    val completedQuests: Int,
    val level: Int,
    val lifeScore: Int,
    val isPremium: Boolean = false,
    val totalLogins: Int = 1
)

object UserProgressTracker {

    fun determinePhase(progress: UserProgress): UserPhase {
        return when {
            progress.daysActive >= 30 || progress.level >= 8 -> UserPhase.EXPERT
            progress.daysActive >= 14 || progress.level >= 4 -> UserPhase.ADVANCED
            progress.daysActive >= 4 || progress.completedQuests >= 3 || progress.level >= 2 -> UserPhase.EXPLORING
            else -> UserPhase.NEW_USER
        }
    }

    fun getDailyQuestLimit(phase: UserPhase): Int {
        return when (phase) {
            UserPhase.NEW_USER -> 3
            UserPhase.EXPLORING -> 5
            UserPhase.ADVANCED, UserPhase.EXPERT -> 8
        }
    }

    fun isFeatureUnlocked(featureKey: String, phase: UserPhase): Boolean {
        return when (featureKey) {
            "HOME_BASIC", "QUESTS_BASIC", "SCORE_BASIC", "AI_COACH_BASIC", "PROFILE_BASIC" -> true
            "ALL_DIMENSIONS_DETAILED", "30_DAY_CHALLENGES", "LEADERBOARD", "JOURNAL", "SHARE_CARDS" ->
                phase != UserPhase.NEW_USER
            "MASTERCLASSES", "SOCIAL_DUELS", "REWARD_STORE", "SKILL_MASTERY", "MICRO_VLOGS", "GROUP_HABITS", "WEEKLY_AUDITS" ->
                phase == UserPhase.ADVANCED || phase == UserPhase.EXPERT
            "ENTERPRISE_HUB", "COMBAT_BOSS", "ANALYTICS_DASHBOARD", "PRIVACY_GUARD", "CAREER_QUESTS" ->
                phase == UserPhase.EXPERT || phase == UserPhase.ADVANCED
            else -> true
        }
    }

    fun getUnlockedFeatures(phase: UserPhase): List<String> {
        return when (phase) {
            UserPhase.NEW_USER -> listOf(
                "3 Daily Quests", "LifeScore Index", "Basic Stats", "AI Coach Prompt", "Streak Counter"
            )
            UserPhase.EXPLORING -> listOf(
                "5 Daily Quests", "All 8 Dimensions", "30-Day Challenges", "Global Leaderboard", "Cognitive Journal"
            )
            UserPhase.ADVANCED -> listOf(
                "8 Daily Quests", "Expert Masterclasses", "Social Duels", "XP Reward Store", "Skill Mastery", "Micro-Vlogs", "Weekly Audits"
            )
            UserPhase.EXPERT -> listOf(
                "Complete Life OS", "Enterprise Hub", "Dimension Boss Raids", "Predictive Analytics", "Zero-Data Privacy", "Career Quests"
            )
        }
    }
}
