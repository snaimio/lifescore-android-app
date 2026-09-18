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
            archetypeAffinity = HeroArchetype.NOMAD,
            lowLabel = "Struggling",
            highLabel = "Locked In"
        ),
        QuickQuestion(
            id = 2,
            text = "I enjoy planning structured routines, organizing tasks, and executing step-by-step systems.",
            dimension = DimensionType.CAREER,
            archetypeAffinity = HeroArchetype.ARCHITECT,
            lowLabel = "Chaotic",
            highLabel = "Highly Structured"
        ),
        QuickQuestion(
            id = 3,
            text = "I actively seek out books, podcasts, and deep learning to master new skills.",
            dimension = DimensionType.LEARNING,
            archetypeAffinity = HeroArchetype.SCHOLAR,
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
            archetypeAffinity = HeroArchetype.SAGE,
            lowLabel = "Easily Stressed",
            highLabel = "Zen & Grounded"
        ),
        QuickQuestion(
            id = 6,
            text = "I manage my budget wisely, track expenses, and focus on automated investments.",
            dimension = DimensionType.WEALTH,
            archetypeAffinity = HeroArchetype.ARCHITECT,
            lowLabel = "Impulsive",
            highLabel = "Disciplined"
        ),
        QuickQuestion(
            id = 7,
            text = "I make intentional time to nurture deep, meaningful connections with friends and family.",
            dimension = DimensionType.RELATIONSHIPS,
            archetypeAffinity = HeroArchetype.CATALYST,
            lowLabel = "Isolated",
            highLabel = "Deeply Connected"
        ),
        QuickQuestion(
            id = 8,
            text = "I bring positive energy, organize social gatherings, and inspire others to grow.",
            dimension = DimensionType.SOCIAL_LIFE,
            archetypeAffinity = HeroArchetype.VISIONARY,
            lowLabel = "Reserved",
            highLabel = "Inspiring Catalyst"
        ),
        QuickQuestion(
            id = 9,
            text = "When I face obstacles, I treat them as creative puzzles and experiment with novel ideas.",
            dimension = DimensionType.CAREER,
            archetypeAffinity = HeroArchetype.CREATOR,
            lowLabel = "Get Discouraged",
            highLabel = "Highly Inventive"
        ),
        QuickQuestion(
            id = 10,
            text = "I am committed to replacing mindless screen scrolling with intentional life building.",
            dimension = DimensionType.MENTAL_HEALTH,
            archetypeAffinity = HeroArchetype.ARCHITECT,
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

        val topArchetype = archetypeVotes.maxByOrNull { it.value }?.key ?: HeroArchetype.ARCHITECT
        val overallAverage = dimScores.values.average().toFloat()
        val calculatedLifeScore = ((overallAverage / 100f) * 600f + 250f).toInt().coerceIn(300, 950)

        val (strength, growthArea) = when (topArchetype) {
            HeroArchetype.ARCHITECT -> "Scalable Systems & Structural Order" to "Spontaneous Flexibility"
            HeroArchetype.SAGE -> "Deep Contemplation & Wisdom" to "Tangible Execution"
            HeroArchetype.WARRIOR -> "Relentless Grit & High Somatic Drive" to "Mindful Recovery"
            HeroArchetype.VISIONARY -> "Audacious Strategy & 10-Year Foresight" to "Granular Follow-Through"
            HeroArchetype.SCHOLAR -> "First-Principles Synthesis & Mastery" to "Somatic Balance"
            HeroArchetype.CREATOR -> "Aesthetic Innovation & Novel Synthesis" to "Procedural Routine"
            HeroArchetype.NOMAD, HeroArchetype.EXPLORER -> "Vitality, Nature & Movement" to "Detailed Planning"
            HeroArchetype.CATALYST, HeroArchetype.HEALER -> "Empathetic Connection & Psychological Safety" to "Personal Boundaries"
        }

        val (firstQuestTitle, firstQuestDim) = when (topArchetype) {
            HeroArchetype.ARCHITECT -> "Drink 1 glass of fresh water & set day's intention" to DimensionType.HEALTH
            HeroArchetype.SAGE -> "Take 3 deep grounding breaths before starting" to DimensionType.MENTAL_HEALTH
            HeroArchetype.WARRIOR -> "Complete 10 intentional bodyweight squats" to DimensionType.FITNESS
            HeroArchetype.VISIONARY -> "Define your single #1 high-leverage goal for today" to DimensionType.CAREER
            HeroArchetype.SCHOLAR -> "Read 2 pages of a transformative book" to DimensionType.LEARNING
            HeroArchetype.CREATOR -> "Capture 1 creative thought or insight in your journal" to DimensionType.CAREER
            HeroArchetype.NOMAD, HeroArchetype.EXPLORER -> "Drink 1 tall glass of water with mindful focus" to DimensionType.HEALTH
            HeroArchetype.CATALYST, HeroArchetype.HEALER -> "Send a 1-sentence gratitude text to a friend" to DimensionType.RELATIONSHIPS
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
