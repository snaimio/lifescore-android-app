package com.lifescore.app

import com.lifescore.app.data.repository.GeminiCoachRepositoryImpl
import com.lifescore.app.domain.model.DimensionType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AiCoachServiceTest {

    private lateinit var coachRepository: GeminiCoachRepositoryImpl

    @Before
    fun setUp() {
        coachRepository = GeminiCoachRepositoryImpl(apiKey = null) // Tests offline resilient engine
    }

    @Test
    fun testDimensionGuidanceFormatting() {
        val weakGuidance = coachRepository.generateDimensionGuidance(
            dimension = DimensionType.HEALTH,
            score = 42,
            isWeakest = true
        )
        assertTrue(weakGuidance.contains("Health"))
        assertTrue(weakGuidance.contains("primary growth bottleneck"))
        assertTrue(weakGuidance.contains("42%"))

        val strongGuidance = coachRepository.generateDimensionGuidance(
            dimension = DimensionType.FITNESS,
            score = 90,
            isWeakest = false
        )
        assertTrue(strongGuidance.contains("Fitness"))
        assertTrue(strongGuidance.contains("exceptional"))
        assertTrue(strongGuidance.contains("90%"))
    }

    @Test
    fun testWeeklyAuditGeneration() = runBlocking {
        val scores = mapOf(
            DimensionType.HEALTH to 50,
            DimensionType.CAREER to 90,
            DimensionType.WEALTH to 65,
            DimensionType.FITNESS to 85,
            DimensionType.LEARNING to 75,
            DimensionType.RELATIONSHIPS to 70,
            DimensionType.MENTAL_HEALTH to 60,
            DimensionType.SOCIAL_LIFE to 80
        )

        val audit = coachRepository.generateWeeklyAudit(
            scores = scores,
            tasksCompleted = 18,
            totalScore = 780,
            streak = 14
        )

        assertNotNull(audit)
        assertEquals(DimensionType.CAREER, audit.topDimension)
        assertEquals(DimensionType.HEALTH, audit.growthDimension)
        assertTrue(audit.keyAchievements.isNotEmpty())
        assertTrue(audit.nextWeekDirectives.isNotEmpty())
        assertTrue(audit.motivationalQuote.isNotBlank())
    }

    @Test
    fun testWeeklyRecapShareTextGeneration() = runBlocking {
        val scores = DimensionType.values().associateWith { 80 }
        val audit = coachRepository.generateWeeklyAudit(
            scores = scores,
            tasksCompleted = 20,
            totalScore = 800,
            streak = 10
        )

        val shareText = coachRepository.generateWeeklyRecapShareText(
            audit = audit,
            score = 800,
            streak = 10
        )

        assertTrue(shareText.contains("800/1000"))
        assertTrue(shareText.contains("10-day streak"))
        assertTrue(shareText.contains("#WeeklyAudit"))
        assertTrue(shareText.contains("https://lifescore.app/audit"))
    }

    @Test
    fun testDeterministicCoachReplies() = runBlocking {
        val healthReply = coachRepository.askCoach("How do I fix my sleep and health?", 700)
        assertTrue(healthReply.contains("Health Architecture"))

        val careerReply = coachRepository.askCoach("How can I stop procrastinating on my career goals?", 700)
        assertTrue(careerReply.contains("Deep Work Protocol"))

        val wealthReply = coachRepository.askCoach("Give me tips for wealth and money", 700)
        assertTrue(wealthReply.contains("Financial Discipline"))
    }
}
