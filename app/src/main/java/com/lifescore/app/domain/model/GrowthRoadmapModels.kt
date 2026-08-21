package com.lifescore.app.domain.model

import com.lifescore.app.core.util.PsychometricArchetype
import com.lifescore.app.core.util.PsychometricDimension

data class DimensionFocusCampaign(
    val id: String,
    val title: String,
    val subtitle: String,
    val psychometricDimension: PsychometricDimension,
    val targetLifeDimension: DimensionType,
    val currentScore: Int,
    val targetScore: Int = 200,
    val durationDays: Int = 30,
    val dailyAction: String,
    val xpReward: Int = 600,
    val isJoined: Boolean = true
)

data class AssessmentGrowthRoadmap(
    val archetype: PsychometricArchetype,
    val overallScore: Int,
    val primaryFocusCampaign: DimensionFocusCampaign,
    val secondaryFocusCampaign: DimensionFocusCampaign?,
    val recommendedCareerQuests: List<CareerQuest>,
    val personalizedDailyQuests: List<AiQuest>,
    val executiveSummary: String
)

data class ChallengePack(
    val id: String,
    val title: String,
    val dimension: DimensionType,
    val tier: ChallengeTier,
    val durationDays: Int,
    val description: String,
    val dailyTasks: List<String>,
    val totalXpReward: Int,
    val isUnlocked: Boolean = true,
    val isJoined: Boolean = false,
    val completedDays: Int = 0
)

enum class ChallengeTier(val title: String, val badge: String) {
    STARTER("7-Day Starter Foundation", "🌱"),
    PROGRESSION("14-Day Accelerator", "🚀"),
    MASTERY("30-Day Mastery Forge", "👑")
}

data class ArchetypeTribe(
    val archetypeId: String,
    val tribeName: String,
    val emoji: String,
    val memberCount: Int,
    val collectiveScore: Long,
    val weeklyGoal: String,
    val rank: Int,
    val topTraits: List<String>
)
