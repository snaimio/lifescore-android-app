package com.lifescore.app.core.engine

import com.lifescore.app.presentation.navigation.Screen

data class FeatureItem(
    val id: String,
    val title: String,
    val category: FeatureCategory,
    val description: String,
    val iconEmoji: String,
    val minPhase: UserPhase,
    val route: String
)

enum class FeatureCategory(val displayName: String, val iconEmoji: String) {
    CORE("Core Essentials", "🌟"),
    TRACKERS("15 Life Trackers", "📊"),
    SELF_IMPROVEMENT("Self-Improvement", "📚"),
    WELLNESS("Digital Wellness & Screen Time", "⏳"),
    RECOVERY("Addiction Recovery OS", "🛡️"),
    MARKET_LEADERS("Market Leader Super-Engines", "🚀"),
    SOCIAL_GROWTH("Social, Leagues & Duels", "⚔️"),
    ENTERPRISE("Enterprise & AI", "💼")
}

object FeatureUnlockManager {

    val allFeatures: List<FeatureItem> = listOf(
        // Core Essentials (Week 1 / Phase NEW)
        FeatureItem("home", "LifeScore Command Center", FeatureCategory.CORE, "5-card streamlined daily dashboard", "🏠", UserPhase.NEW_USER, Screen.Home.route),
        FeatureItem("quests", "Daily Micro-Quests", FeatureCategory.CORE, "Focused 3-task daily execution engine", "📋", UserPhase.NEW_USER, Screen.Tasks.route),
        FeatureItem("quick_stats", "Core Dimension Stats", FeatureCategory.CORE, "Visual balance across health & life goals", "📊", UserPhase.NEW_USER, Screen.Dimensions.route),
        FeatureItem("ai_coach", "Stanford AI Coach", FeatureCategory.CORE, "OARS motivational intelligence & guidance", "🤖", UserPhase.NEW_USER, Screen.AICoach.route),
        FeatureItem("profile", "Hero Character Profile", FeatureCategory.CORE, "Archetype attributes, level & streaks", "👤", UserPhase.NEW_USER, Screen.Profile.route),

        // Week 2 (Phase EXPLORING)
        FeatureItem("all_dimensions", "8-Dimension Radar", FeatureCategory.CORE, "Comprehensive 360-degree life analytics", "🧭", UserPhase.EXPLORING, Screen.Dimensions.route),
        FeatureItem("journal", "Cognitive Thought Journal", FeatureCategory.SELF_IMPROVEMENT, "CBT cognitive reframing & gratitude", "📓", UserPhase.EXPLORING, Screen.Journal.route),
        FeatureItem("leaderboards", "10-Tier League Ladder", FeatureCategory.SOCIAL_GROWTH, "Weekly promotion zones from Bronze to Diamond", "🏆", UserPhase.EXPLORING, Screen.LeagueTiers.route),
        FeatureItem("mood", "Mood & Emotional Tracker", FeatureCategory.TRACKERS, "Emotional telemetry and triggers", "🎭", UserPhase.EXPLORING, Screen.MoodTracker.route),
        FeatureItem("daily_growth", "Daily Growth Audio", FeatureCategory.SELF_IMPROVEMENT, "15-minute compounding audio lessons", "🌱", UserPhase.EXPLORING, Screen.DailyGrowth.route),
        FeatureItem("share_cards", "Holographic Scorecards", FeatureCategory.SOCIAL_GROWTH, "Custom shareable social progress cards", "✨", UserPhase.EXPLORING, Screen.Home.route),

        // Week 3 (Phase ADVANCED)
        FeatureItem("hydration", "Hydration Tracker", FeatureCategory.TRACKERS, "Smart water intake & hourly reminders", "💧", UserPhase.ADVANCED, Screen.Hydration.route),
        FeatureItem("sleep", "Sleep & Wind-Down Studio", FeatureCategory.TRACKERS, "Circadian tracking & relaxing sleep stories", "🛌", UserPhase.ADVANCED, Screen.SleepSoundscapes.route),
        FeatureItem("steps", "Steps & Daily Movement", FeatureCategory.TRACKERS, "Step goals, distance & calorie telemetry", "👟", UserPhase.ADVANCED, Screen.TrackerHub.route),
        FeatureItem("nutrition", "Nutrition & Macro Tracker", FeatureCategory.TRACKERS, "Meal logging and healthy food scoring", "🥗", UserPhase.ADVANCED, Screen.TrackerHub.route),
        FeatureItem("reading", "Book Mastery & Flashcards", FeatureCategory.SELF_IMPROVEMENT, "Headway/Blinkist 15-min book summaries", "📖", UserPhase.ADVANCED, Screen.BookLibrary.route),
        FeatureItem("challenges", "30-Day Life Challenges", FeatureCategory.SELF_IMPROVEMENT, "Structured habit transformation programs", "🔥", UserPhase.ADVANCED, Screen.Challenges.route),
        FeatureItem("rewards", "Custom Gold Rewards Store", FeatureCategory.CORE, "Price real-world treats with earned gold", "🎁", UserPhase.ADVANCED, Screen.CustomRewards.route),
        FeatureItem("screentime", "Screen Time & Friction Shield", FeatureCategory.WELLNESS, "Opal app blocker & SweatPass unlocking", "⏱️", UserPhase.ADVANCED, Screen.ScreenTime.route),
        FeatureItem("streak_vault", "Streak Vault & Resurrection", FeatureCategory.CORE, "Streak freezes & 3-day recovery quests", "🛡️", UserPhase.ADVANCED, Screen.StreakVault.route),

        // Week 4+ (Phase EXPERT) - ALL 15 Trackers & Super Engines
        FeatureItem("vitals", "Vitals & Glucose Tracker", FeatureCategory.TRACKERS, "Heart rate, HRV and blood glucose", "💓", UserPhase.EXPERT, Screen.TrackerHub.route),
        FeatureItem("workouts", "Workout & Strength Logger", FeatureCategory.TRACKERS, "Gym routines, progressive overload & PRs", "🏋️", UserPhase.EXPERT, Screen.TrackerHub.route),
        FeatureItem("weight", "Weight & Body Comp", FeatureCategory.TRACKERS, "Weight milestones and body fat trendlines", "⚖️", UserPhase.EXPERT, Screen.TrackerHub.route),
        FeatureItem("skill_mastery", "10,000-Hour Skill Engine", FeatureCategory.TRACKERS, "Deliberate practice and milestone mastery", "🎯", UserPhase.EXPERT, Screen.SkillMastery.route),
        FeatureItem("goals_okrs", "Goals & OKR Architecture", FeatureCategory.TRACKERS, "Quarterly objectives and key result tracking", "🚩", UserPhase.EXPERT, Screen.ActionPlan.route),
        FeatureItem("habits_routines", "Advanced Habit Stacking", FeatureCategory.TRACKERS, "Fabulous behavior loops and triggers", "🔄", UserPhase.EXPERT, Screen.HabitLibrary.route),
        FeatureItem("relationships", "Relationship CRM", FeatureCategory.TRACKERS, "Keep-in-touch cadence and meaningful bonds", "💞", UserPhase.EXPERT, Screen.GroupHabits.route),
        FeatureItem("wealth", "Wealth & Net Worth Engine", FeatureCategory.TRACKERS, "Asset allocation and debt payoff snowballs", "💰", UserPhase.EXPERT, Screen.RewardStore.route),
        FeatureItem("mindfulness", "Insight Meditation Sanctuary", FeatureCategory.TRACKERS, "Ambient bell timers and guided stillness", "🧘", UserPhase.EXPERT, Screen.MeditationLibrary.route),

        // Addiction Recovery OS
        FeatureItem("recovery", "Addiction Recovery OS", FeatureCategory.RECOVERY, "Emergency urge SOS, streak shields & pledges", "🛑", UserPhase.EXPERT, Screen.Recovery.route),
        FeatureItem("recovery_sos", "Urge Surge SOS Station", FeatureCategory.RECOVERY, "90-second physiological reset & emergency contacts", "🚨", UserPhase.EXPERT, Screen.RecoverySos.route),

        // Market Leaders
        FeatureItem("party", "Habitica RPG Boss Party", FeatureCategory.MARKET_LEADERS, "Multiplayer dungeon raids & shared quests", "⚔️", UserPhase.EXPERT, Screen.PartySystem.route),
        FeatureItem("virtual_pet", "Finch Virtual Companion", FeatureCategory.MARKET_LEADERS, "Gentle pet that grows with self-care", "🐣", UserPhase.EXPERT, Screen.VirtualPet.route),
        FeatureItem("circadian_energy", "Lifestack Energy Curve", FeatureCategory.MARKET_LEADERS, "Circadian rhythm smart task scheduling", "⚡", UserPhase.EXPERT, Screen.EnergySchedule.route),
        FeatureItem("coach_marketplace", "Coach.me Mentorship", FeatureCategory.MARKET_LEADERS, "1-on-1 certified accountability coaches", "🎓", UserPhase.EXPERT, Screen.CoachMarketplace.route),
        FeatureItem("science_journeys", "Fabulous Science Journeys", FeatureCategory.MARKET_LEADERS, "Behavioral transformation science journeys", "🔬", UserPhase.EXPERT, Screen.ScienceJourneys.route),
        FeatureItem("neurodivergent", "Soft Focus ADHD Hub", FeatureCategory.MARKET_LEADERS, "Sensory filters, micro-pacing & body doubles", "🧠", UserPhase.EXPERT, Screen.NeurodivergentHub.route),

        // Social, Growth & Enterprise
        FeatureItem("viral_referrals", "Referral & Free Premium Loop", FeatureCategory.SOCIAL_GROWTH, "Invite 3 friends for 1 month free Premium", "🎁", UserPhase.EXPERT, Screen.ViralReferrals.route),
        FeatureItem("friends_feed", "Live Friends Activity Feed", FeatureCategory.SOCIAL_GROWTH, "1-tap flame nudges and freeze gifting", "🔥", UserPhase.EXPERT, Screen.FriendsFeed.route),
        FeatureItem("social_duels", "1-on-1 Habit Duels", FeatureCategory.SOCIAL_GROWTH, "Wager XP stakes against friends", "🤺", UserPhase.EXPERT, Screen.Combat.route),
        FeatureItem("micro_vlogs", "2-Second Micro-Vlogs", FeatureCategory.SOCIAL_GROWTH, "Visual proof of daily habit execution", "🎥", UserPhase.EXPERT, Screen.MicroVlogs.route),
        FeatureItem("ai_memory", "Stanford AI Memory Inspector", FeatureCategory.ENTERPRISE, "OARS behavioral patterns and personality memory", "🧠", UserPhase.EXPERT, Screen.AiMemoryInspector.route),
        FeatureItem("enterprise", "Enterprise Team Wellbeing", FeatureCategory.ENTERPRISE, "Corporate B2B burnout prevention & analytics", "🏢", UserPhase.EXPERT, Screen.Enterprise.route)
    )

    fun isFeatureUnlocked(featureId: String, currentPhase: UserPhase): Boolean {
        val feature = allFeatures.find { it.id == featureId } ?: return true
        return currentPhase.ordinal >= feature.minPhase.ordinal
    }

    fun getUnlockedFeatures(phase: UserPhase): List<FeatureItem> {
        return allFeatures.filter { phase.ordinal >= it.minPhase.ordinal }
    }

    fun getLockedFeatures(phase: UserPhase): List<FeatureItem> {
        return allFeatures.filter { phase.ordinal < it.minPhase.ordinal }
    }

    fun getFeaturesByCategory(category: FeatureCategory): List<FeatureItem> {
        return allFeatures.filter { it.category == category }
    }

    fun getPhaseRequirementDescription(minPhase: UserPhase): String {
        return when (minPhase) {
            UserPhase.NEW_USER -> "Unlocked on Day 1"
            UserPhase.EXPLORING -> "Unlocks in Week 2 (Day 4+ or Level 2)"
            UserPhase.ADVANCED -> "Unlocks in Week 3 (Day 14+ or Level 4)"
            UserPhase.EXPERT -> "Unlocks in Month 1 (Day 30+ or Level 8)"
        }
    }
}
