package com.lifescore.app.core.util

import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.HeroArchetype

data class QuickQuestion(
    val id: Int,
    val text: String,
    val dimension: DimensionType,
    val archetypeAffinity: HeroArchetype,
    val lowLabel: String = "Rarely",
    val highLabel: String = "Always"
)

data class QuickAssessmentResult(
    val archetype: HeroArchetype,
    val startingLifeScore: Int,
    val dimensionScores: Map<DimensionType, Float>,
    val primaryStrength: String,
    val growthArea: String,
    val firstQuestTitle: String,
    val firstQuestDimension: DimensionType
)

object QuickAssessmentEngine {

    val questions: List<QuickQuestion> = listOf(
        QuickQuestion(
            id = 1,
            text = "I prioritize 7-8 hours of restful sleep and hydrate consistently throughout the day.",
            dimension = DimensionType.HEALTH,
            archetypeAffinity = HeroArchetype.EXPLORER,
            lowLabel = "Struggling",
            highLabel = "Locked In"
        ),
        QuickQuestion(
            id = 2,
            text = "I enjoy planning structured routines, organizing tasks, and executing step-by-step systems.",
            dimension = DimensionType.CAREER,
            archetypeAffinity = HeroArchetype.CREATOR,
            lowLabel = "Chaotic",
            highLabel = "Highly Structured"
        ),
        QuickQuestion(
            id = 3,
            text = "I actively seek out books, podcasts, and deep learning to master new skills.",
            dimension = DimensionType.LEARNING,
            archetypeAffinity = HeroArchetype.SAGE,
            lowLabel = "Passive",
            highLabel = "Voracious"
        ),
        QuickQuestion(
            id = 4,
            text = "I push my physical limits through regular workouts, walking, or endurance training.",
            dimension = DimensionType.FITNESS,
            archetypeAffinity = HeroArchetype.WARRIOR,
            lowLabel = "Sedentary",
            highLabel = "Athletic"
        ),
        QuickQuestion(
            id = 5,
            text = "I stay calm under pressure, practicing mindfulness, deep breathing, or journaling.",
            dimension = DimensionType.MENTAL_HEALTH,
            archetypeAffinity = HeroArchetype.HEALER,
            lowLabel = "Easily Stressed",
            highLabel = "Zen & Grounded"
        ),
        QuickQuestion(
            id = 6,
            text = "I manage my budget wisely, track expenses, and focus on automated investments.",
            dimension = DimensionType.WEALTH,
            archetypeAffinity = HeroArchetype.CREATOR,
            lowLabel = "Impulsive",
            highLabel = "Disciplined"
        ),
        QuickQuestion(
            id = 7,
            text = "I make intentional time to nurture deep, meaningful connections with friends and family.",
            dimension = DimensionType.RELATIONSHIPS,
            archetypeAffinity = HeroArchetype.HEALER,
            lowLabel = "Isolated",
            highLabel = "Deeply Connected"
        ),
        QuickQuestion(
            id = 8,
            text = "I bring positive energy, organize social gatherings, and inspire others to grow.",
            dimension = DimensionType.SOCIAL_LIFE,
            archetypeAffinity = HeroArchetype.EXPLORER,
            lowLabel = "Reserved",
            highLabel = "Inspiring Catalyst"
        ),
        QuickQuestion(
            id = 9,
            text = "When I face obstacles, I treat them as valuable data and adjust my strategy quickly.",
            dimension = DimensionType.CAREER,
            archetypeAffinity = HeroArchetype.WARRIOR,
            lowLabel = "Get Discouraged",
            highLabel = "Relentless Focus"
        ),
        QuickQuestion(
            id = 10,
            text = "I am committed to replacing mindless screen scrolling with intentional life building.",
            dimension = DimensionType.MENTAL_HEALTH,
            archetypeAffinity = HeroArchetype.SAGE,
            lowLabel = "Distracted",
            highLabel = "Laser Focused"
        )
    )

    fun evaluate(answers: Map<Int, Int>): QuickAssessmentResult {
        val dimScores = mutableMapOf<DimensionType, Float>()
        val archetypeVotes = mutableMapOf<HeroArchetype, Int>()

        DimensionType.values().forEach { dim ->
            val dimQuestions = questions.filter { it.dimension == dim }
            val avg = if (dimQuestions.isNotEmpty()) {
                val sum = dimQuestions.map { answers[it.id] ?: 3 }.sum()
                (sum.toFloat() / (dimQuestions.size * 5)) * 100f
            } else 65f
            dimScores[dim] = avg.coerceIn(20f, 100f)
        }

        questions.forEach { q ->
            val score = answers[q.id] ?: 3
            val current = archetypeVotes.getOrDefault(q.archetypeAffinity, 0)
            archetypeVotes[q.archetypeAffinity] = current + score
        }

        val topArchetype = archetypeVotes.maxByOrNull { it.value }?.key ?: HeroArchetype.CREATOR
        val overallAverage = dimScores.values.average().toFloat()
        val calculatedLifeScore = ((overallAverage / 100f) * 600f + 250f).toInt().coerceIn(300, 950)

        val (strength, growthArea) = when (topArchetype) {
            HeroArchetype.CREATOR -> "Visionary Building & Structured Systems" to "Mindful Downtime"
            HeroArchetype.WARRIOR -> "Relentless Grit & Fitness Drive" to "Emotional Recovery"
            HeroArchetype.SAGE -> "Curiosity & Continuous Learning" to "Immediate Action"
            HeroArchetype.EXPLORER -> "Vitality, Energy & Health Habits" to "Detailed Planning"
            HeroArchetype.HEALER -> "Inner Harmony & Relationship Bonds" to "Personal Boundaries"
        }

        val (firstQuestTitle, firstQuestDim) = when (topArchetype) {
            HeroArchetype.CREATOR -> "Plan your top 3 priorities for tomorrow" to DimensionType.CAREER
            HeroArchetype.WARRIOR -> "Complete a 10-minute energizing walk" to DimensionType.FITNESS
            HeroArchetype.SAGE -> "Read 5 pages of an insightful book" to DimensionType.LEARNING
            HeroArchetype.EXPLORER -> "Drink 500ml of water with mindful breaths" to DimensionType.HEALTH
            HeroArchetype.HEALER -> "Practice 3 minutes of box breathing" to DimensionType.MENTAL_HEALTH
        }

        return QuickAssessmentResult(
            archetype = topArchetype,
            startingLifeScore = calculatedLifeScore,
            dimensionScores = dimScores,
            primaryStrength = strength,
            growthArea = growthArea,
            firstQuestTitle = firstQuestTitle,
            firstQuestDimension = firstQuestDim
        )
    }
}
