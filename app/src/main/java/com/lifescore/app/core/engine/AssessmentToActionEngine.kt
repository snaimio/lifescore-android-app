package com.lifescore.app.core.engine

import com.lifescore.app.core.util.AssessmentResult
import com.lifescore.app.core.util.PsychometricDimension
import com.lifescore.app.domain.model.*

object AssessmentToActionEngine {

    /**
     * Bridges the psychometric assessment results directly into actionable, gamified growth.
     * Identifies the weakest dimensions and generates tailored 30-day focus campaigns.
     */
    fun generateFocusCampaigns(result: AssessmentResult): List<DimensionFocusCampaign> {
        val sortedDimensions = result.dimensionScores.entries
            .sortedBy { it.value } // Ascending order (lowest scores first)
            .take(2)

        return sortedDimensions.map { (dimension, score) ->
            val lifeDim = mapPsychometricToLifeDimension(dimension)
            val dailyHabit = getDailyActionForDimension(dimension)
            DimensionFocusCampaign(
                id = "focus_campaign_${dimension.id}_30d",
                title = "30-Day ${dimension.displayName} Accelerator",
                subtitle = "Turn your lowest assessment dimension (${score}/200) into your compounding superpower.",
                psychometricDimension = dimension,
                targetLifeDimension = lifeDim,
                currentScore = score,
                targetScore = 200,
                durationDays = 30,
                dailyAction = dailyHabit,
                xpReward = 600,
                isJoined = true
            )
        }
    }

    /**
     * Generates personalized daily AI quests specifically targeting the user's growth gaps.
     */
    fun generatePersonalizedQuests(result: AssessmentResult): List<AiQuest> {
        val weakest = result.dimensionScores.minByOrNull { it.value }?.key ?: PsychometricDimension.INTELLECTUAL
        val targetLifeDim = mapPsychometricToLifeDimension(weakest)

        val quests = mutableListOf<AiQuest>()

        // 1. Weakest dimension primary quest
        quests.add(
            AiQuest(
                id = "ai_quest_assessment_focus_${System.currentTimeMillis()}_1",
                title = "Assessment Growth Protocol: ${weakest.displayName}",
                description = "Target your primary growth area identified in your psychometric test.",
                dimension = targetLifeDim,
                difficulty = QuestDifficulty.B,
                pointsReward = 50,
                statRewardPoints = 5,
                estimatedMinutes = 20,
                subObjectives = listOf(
                    "Review archetype growth recommendation: ${result.archetype.growthArea}",
                    getDailyActionForDimension(weakest),
                    "Log 1 key learning or obstacle in your Cognitive Journal"
                ),
                isAccepted = true
            )
        )

        // 2. Archetype superpower calibration quest
        quests.add(
            AiQuest(
                id = "ai_quest_archetype_superpower_${System.currentTimeMillis()}_2",
                title = "${result.archetype.name} Superpower Alignment",
                description = "Leverage your core strength (${result.archetype.superpower}) to create maximum leverage today.",
                dimension = DimensionType.CAREER,
                difficulty = QuestDifficulty.A,
                pointsReward = 45,
                statRewardPoints = 4,
                estimatedMinutes = 25,
                subObjectives = listOf(
                    "Identify your highest-leverage task for today",
                    "Execute with 45 minutes of uninterrupted deep work",
                    "Record completion metric"
                ),
                isAccepted = true
            )
        )

        return quests
    }

    /**
     * Synthesizes a complete growth roadmap from assessment results.
     */
    fun generateRoadmap(result: AssessmentResult): AssessmentGrowthRoadmap {
        val campaigns = generateFocusCampaigns(result)
        val careerQuests = CareerQuestSystem.generateCareerQuestCatalog(result.topCareers.take(3))
        val dailyQuests = generatePersonalizedQuests(result)

        val summary = """
            As a ${result.archetype.name} (${result.archetype.title}), your cognitive profile scores ${result.overallScore}/1200 across 6 dimensions.
            Your superpower is ${result.archetype.superpower}. Your biggest compounding leverage point is strengthening ${campaigns.firstOrNull()?.psychometricDimension?.displayName ?: "Execution"}.
        """.trimIndent()

        return AssessmentGrowthRoadmap(
            archetype = result.archetype,
            overallScore = result.overallScore,
            primaryFocusCampaign = campaigns.first(),
            secondaryFocusCampaign = campaigns.getOrNull(1),
            recommendedCareerQuests = careerQuests,
            personalizedDailyQuests = dailyQuests,
            executiveSummary = summary
        )
    }

    fun mapPsychometricToLifeDimension(dimension: PsychometricDimension): DimensionType {
        return when (dimension) {
            PsychometricDimension.INTELLECTUAL -> DimensionType.LEARNING
            PsychometricDimension.EXECUTION -> DimensionType.FITNESS
            PsychometricDimension.CREATIVE -> DimensionType.CAREER
            PsychometricDimension.EMPATHY -> DimensionType.RELATIONSHIPS
            PsychometricDimension.STRATEGY -> DimensionType.CAREER
            PsychometricDimension.SYSTEMS_ORDER -> DimensionType.WEALTH
        }
    }

    private fun getDailyActionForDimension(dimension: PsychometricDimension): String {
        return when (dimension) {
            PsychometricDimension.INTELLECTUAL -> "Complete 20 minutes of active deep reading or technical study."
            PsychometricDimension.EXECUTION -> "Execute 30 minutes of rigorous physical output or decisive task completion."
            PsychometricDimension.CREATIVE -> "Produce 1 original creative artifact, design draft, or synthesis note."
            PsychometricDimension.EMPATHY -> "Engage in 1 proactive, active-listening conversation with zero interruptions."
            PsychometricDimension.STRATEGY -> "Conduct a 15-minute weekly resource audit and long-term priority review."
            PsychometricDimension.SYSTEMS_ORDER -> "Review daily financials, eliminate 1 system friction, and organize workspace."
        }
    }
}
