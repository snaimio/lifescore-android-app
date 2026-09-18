package com.lifescore.app.core.engine

import com.lifescore.app.presentation.navigation.Screen

data class FeatureItem(
    val id: String,
    val title: String,
    val category: FeatureCategory,
    val description: String,
    val minPhase: UserPhase,
    val route: String
)

enum class FeatureCategory(val displayName: String) {
    CORE("Core Essentials"),
    TRACKERS("15 Life Trackers"),
    SELF_IMPROVEMENT("Self-Improvement"),
    WELLNESS("Digital Wellness & Screen Time"),
    RECOVERY("Addiction Recovery OS"),
    HABIT_SYSTEMS("Habit Systems & Science"),
    SOCIAL_GROWTH("Community & Growth"),
    ENTERPRISE("Enterprise & AI")
}

object FeatureUnlockManager {

    val allFeatures: List<FeatureItem> = listOf(
        // Core Essentials (Week 1 / Phase NEW)
        FeatureItem("home", "LifeScore Command Center", FeatureCategory.CORE, "Streamlined daily habits and balance dashboard", UserPhase.NEW_USER, Screen.Home.route),
        FeatureItem("quests", "Daily Focus Habits", FeatureCategory.CORE, "Focused execution engine for daily habits", UserPhase.NEW_USER, Screen.Tasks.route),
        FeatureItem("quick_stats", "Core Dimension Stats", FeatureCategory.CORE, "Visual balance across health & life goals", UserPhase.NEW_USER, Screen.Dimensions.route),
        FeatureItem("ai_coach", "Stanford AI Coach", FeatureCategory.CORE, "OARS motivational intelligence & guidance", UserPhase.NEW_USER, Screen.AICoach.route),
        FeatureItem("profile", "Profile & Stats Dashboard", FeatureCategory.CORE, "Consistency metrics, streak history & balance", UserPhase.NEW_USER, Screen.Profile.route),

        // Week 2 (Phase EXPLORING)
        FeatureItem("all_dimensions", "8-Dimension Radar", FeatureCategory.CORE, "Comprehensive 360-degree life analytics", UserPhase.EXPLORING, Screen.Dimensions.route),
        FeatureItem("journal", "Cognitive Thought Journal", FeatureCategory.SELF_IMPROVEMENT, "CBT cognitive reframing & gratitude", UserPhase.EXPLORING, Screen.Journal.route),
        FeatureItem("mood", "Mood & Emotional Tracker", FeatureCategory.TRACKERS, "Emotional telemetry and triggers", UserPhase.EXPLORING, Screen.MoodTracker.route),
        FeatureItem("daily_growth", "Daily Growth Audio", FeatureCategory.SELF_IMPROVEMENT, "15-minute compounding audio lessons", UserPhase.EXPLORING, Screen.DailyGrowth.route),
        FeatureItem("share_cards", "Scorecard Sharing", FeatureCategory.SOCIAL_GROWTH, "Custom shareable balance progress cards", UserPhase.EXPLORING, Screen.Home.route),

        // Week 3 (Phase ADVANCED)
        FeatureItem("hydration", "Hydration Tracker", FeatureCategory.TRACKERS, "Smart water intake & hourly reminders", UserPhase.ADVANCED, Screen.Hydration.route),
        FeatureItem("sleep", "Sleep & Wind-Down Studio", FeatureCategory.TRACKERS, "Circadian tracking & relaxing sleep stories", UserPhase.ADVANCED, Screen.SleepSoundscapes.route),
        FeatureItem("steps", "Steps & Daily Movement", FeatureCategory.TRACKERS, "Step goals, distance & calorie telemetry", UserPhase.ADVANCED, Screen.TrackerHub.route),
        FeatureItem("nutrition", "Nutrition & Macro Tracker", FeatureCategory.TRACKERS, "Meal logging and healthy food scoring", UserPhase.ADVANCED, Screen.TrackerHub.route),
        FeatureItem("reading", "Book Mastery & Flashcards", FeatureCategory.SELF_IMPROVEMENT, "15-min book summaries and key insights", UserPhase.ADVANCED, Screen.BookLibrary.route),
        FeatureItem("challenges", "14-Day Coach Masterclasses", FeatureCategory.SELF_IMPROVEMENT, "Structured habit transformation programs", UserPhase.ADVANCED, Screen.Challenges.route),
        FeatureItem("screentime", "Screen Time & Friction Shield", FeatureCategory.WELLNESS, "Mindful app blocker & friction pauses", UserPhase.ADVANCED, Screen.ScreenTime.route),
        FeatureItem("streak_vault", "Streak Vault & Consistency", FeatureCategory.CORE, "Streak shields & reflection resets", UserPhase.ADVANCED, Screen.StreakVault.route),

        // Week 4+ (Phase EXPERT) - ALL 15 Trackers & Systems
        FeatureItem("vitals", "Vitals & Glucose Tracker", FeatureCategory.TRACKERS, "Heart rate, HRV and blood glucose", UserPhase.EXPERT, Screen.TrackerHub.route),
        FeatureItem("workouts", "Workout & Strength Logger", FeatureCategory.TRACKERS, "Gym routines, progressive overload & tracking", UserPhase.EXPERT, Screen.TrackerHub.route),
        FeatureItem("weight", "Weight & Body Comp", FeatureCategory.TRACKERS, "Weight milestones and body fat trendlines", UserPhase.EXPERT, Screen.TrackerHub.route),
        FeatureItem("skill_mastery", "10,000-Hour Skill Engine", FeatureCategory.TRACKERS, "Deliberate practice and milestone mastery", UserPhase.EXPERT, Screen.SkillMastery.route),
        FeatureItem("goals_okrs", "Goals & OKR Architecture", FeatureCategory.TRACKERS, "Quarterly objectives and key result tracking", UserPhase.EXPERT, Screen.ActionPlan.route),
        FeatureItem("habits_routines", "Advanced Habit Stacking", FeatureCategory.TRACKERS, "Behavior loops and habit triggers", UserPhase.EXPERT, Screen.HabitLibrary.route),
        FeatureItem("relationships", "Relationship CRM", FeatureCategory.TRACKERS, "Keep-in-touch cadence and meaningful bonds", UserPhase.EXPERT, Screen.GroupHabits.route),
        FeatureItem("wealth", "Wealth & Net Worth Engine", FeatureCategory.TRACKERS, "Asset allocation and savings targets", UserPhase.EXPERT, Screen.RewardStore.route),
        FeatureItem("mindfulness", "Insight Meditation Sanctuary", FeatureCategory.TRACKERS, "Ambient bell timers and guided stillness", UserPhase.EXPERT, Screen.MeditationLibrary.route),

        // Addiction Recovery OS
        FeatureItem("recovery", "Addiction Recovery OS", FeatureCategory.RECOVERY, "Emergency urge SOS, streak shields & pledges", UserPhase.EXPERT, Screen.Recovery.route),
        FeatureItem("recovery_sos", "Urge Surge SOS Station", FeatureCategory.RECOVERY, "90-second physiological reset & emergency contacts", UserPhase.EXPERT, Screen.RecoverySos.route),

        // Science & Growth
        FeatureItem("circadian_energy", "Circadian Energy Curve", FeatureCategory.HABIT_SYSTEMS, "Circadian rhythm smart task scheduling", UserPhase.EXPERT, Screen.EnergySchedule.route),
        FeatureItem("coach_marketplace", "Coach Mentorship", FeatureCategory.HABIT_SYSTEMS, "1-on-1 certified accountability coaches", UserPhase.EXPERT, Screen.CoachMarketplace.route),
        FeatureItem("science_journeys", "Habit Science Journeys", FeatureCategory.HABIT_SYSTEMS, "Behavioral transformation science journeys", UserPhase.EXPERT, Screen.ScienceJourneys.route),
        FeatureItem("neurodivergent", "Soft Focus ADHD Hub", FeatureCategory.HABIT_SYSTEMS, "Sensory filters, micro-pacing & focus timers", UserPhase.EXPERT, Screen.NeurodivergentHub.route),

        // Social, Growth & Enterprise
        FeatureItem("viral_referrals", "Referral Program", FeatureCategory.SOCIAL_GROWTH, "Invite friends for free Pro access", UserPhase.EXPERT, Screen.ViralReferrals.route),
        FeatureItem("friends_feed", "Friends Activity Feed", FeatureCategory.SOCIAL_GROWTH, "Accountability nudges and encouragement", UserPhase.EXPERT, Screen.FriendsFeed.route),
        FeatureItem("micro_vlogs", "Micro-Vlogs Proof", FeatureCategory.SOCIAL_GROWTH, "Visual proof of daily habit execution", UserPhase.EXPERT, Screen.MicroVlogs.route),
        FeatureItem("ai_memory", "AI Memory Inspector", FeatureCategory.ENTERPRISE, "OARS behavioral patterns and habit memory", UserPhase.EXPERT, Screen.AiMemoryInspector.route),
        FeatureItem("enterprise", "Enterprise Team Wellbeing", FeatureCategory.ENTERPRISE, "Corporate team wellbeing and burnout analytics", UserPhase.EXPERT, Screen.Enterprise.route)
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
            UserPhase.EXPLORING -> "Unlocks in Week 2"
            UserPhase.ADVANCED -> "Unlocks in Week 3"
            UserPhase.EXPERT -> "Unlocks in Month 1"
        }
    }
}

