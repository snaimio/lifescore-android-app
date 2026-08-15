package com.lifescore.app

import com.lifescore.app.core.util.PsychometricAssessmentEngine
import com.lifescore.app.core.util.PsychometricDimension
import com.lifescore.app.core.util.RiasecType
import com.lifescore.app.data.remote.model.AssessmentResultDocument
import org.junit.Assert.*
import org.junit.Test

class PsychometricAssessmentEngineTest {

    @Test
    fun test130QuestionBankCompleteness() {
        val questions = PsychometricAssessmentEngine.questions
        assertEquals(130, questions.size)

        // Verify each dimension has >= 20 questions
        PsychometricDimension.values().forEach { dim ->
            val count = questions.count { it.dimension == dim }
            assertTrue("Dimension ${dim.displayName} has $count questions, expected >= 20", count >= 20)
        }
    }

    @Test
    fun testPsychometricScoreNormalization0To200() {
        // Test max possible answers (all 5s)
        val maxAnswers = (1..130).associateWith { 5 }
        val maxScores = PsychometricAssessmentEngine.calculatePsychometricScores(maxAnswers)

        maxScores.forEach { (dim, score) ->
            assertEquals("Dimension ${dim.displayName} should equal 200 for all 5s", 200, score)
        }

        // Test min possible answers (all 1s)
        val minAnswers = (1..130).associateWith { 1 }
        val minScores = PsychometricAssessmentEngine.calculatePsychometricScores(minAnswers)

        minScores.forEach { (dim, score) ->
            assertEquals("Dimension ${dim.displayName} should equal 40 for all 1s", 40, score)
        }
    }

    @Test
    fun test10ArchetypesResolution() {
        val archetypes = PsychometricAssessmentEngine.archetypes
        assertEquals(10, archetypes.size)

        val names = archetypes.map { it.name }
        assertTrue(names.contains("The Architect"))
        assertTrue(names.contains("The Sage"))
        assertTrue(names.contains("The Warrior"))
        assertTrue(names.contains("The Healer"))
        assertTrue(names.contains("The Visionary"))
        assertTrue(names.contains("The Alchemist"))
        assertTrue(names.contains("The Strategist"))
        assertTrue(names.contains("The Guardian"))
        assertTrue(names.contains("The Catalyst"))
        assertTrue(names.contains("The Explorer"))
    }

    @Test
    fun test48RiasecCareerMatches() {
        val careers = PsychometricAssessmentEngine.careersDatabase
        assertEquals(48, careers.size)

        // Test matching for code "IEC"
        val topMatches = PsychometricAssessmentEngine.findTopCareerMatches("IEC", limit = 10)
        assertEquals(10, topMatches.size)
        assertTrue(topMatches.first().matchPercentage >= 90)
    }

    @Test
    fun testFullAssessmentEvaluation() {
        val answers = (1..130).associateWith { 4 } // Agree across the board
        val result = PsychometricAssessmentEngine.evaluateAssessment(answers)

        assertNotNull(result)
        assertEquals(6, result.dimensionScores.size)
        assertTrue(result.overallScore in 800..1100)
        assertNotNull(result.archetype)
        assertEquals(3, result.topRiasecCode.length)
        assertTrue(result.topCareers.isNotEmpty())
    }

    @Test
    fun testAssessmentResultDocumentCreation() {
        val doc = AssessmentResultDocument(
            id = "res_100",
            uid = "user_hero_999",
            archetypeId = "architect",
            archetypeName = "The Architect",
            archetypeTitle = "Master of Structural Systems & Order",
            overallScore = 1080,
            topRiasecCode = "CIE",
            intellectualScore = 185,
            executionScore = 160,
            creativeScore = 140,
            empathyScore = 130,
            strategyScore = 175,
            systemsScore = 190,
            topCareerTitles = listOf("AI Systems Architect", "Quantitative Strategist")
        )

        assertEquals("res_100", doc.id)
        assertEquals("The Architect", doc.archetypeName)
        assertEquals(1080, doc.overallScore)
        assertEquals(190, doc.systemsScore)
    }
}
