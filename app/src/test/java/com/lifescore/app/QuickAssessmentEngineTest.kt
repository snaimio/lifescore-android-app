package com.lifescore.app

import com.lifescore.app.core.util.QuickAssessmentEngine
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.HeroArchetype
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QuickAssessmentEngineTest {

    @Test
    fun testQuickAssessmentHas10Questions() {
        assertEquals(10, QuickAssessmentEngine.questions.size)
    }

    @Test
    fun testEvaluateProducesValidArchetypeAndScore() {
        // High scores on questions 2 and 6 (Creator affinity)
        val answers = mapOf(
            1 to 4,
            2 to 5,
            3 to 3,
            4 to 3,
            5 to 4,
            6 to 5,
            7 to 3,
            8 to 3,
            9 to 4,
            10 to 3
        )

        val result = QuickAssessmentEngine.evaluate(answers)

        assertNotNull(result)
        assertEquals(HeroArchetype.CREATOR, result.archetype)
        assertTrue(result.startingLifeScore in 300..950)
        assertEquals(8, result.dimensionScores.size)
        assertTrue(result.primaryStrength.isNotEmpty())
        assertTrue(result.firstQuestTitle.isNotEmpty())
    }

    @Test
    fun testEvaluateExplorerOrWarriorForHighPhysicalAndSleep() {
        val answers = mapOf(
            1 to 5,
            2 to 1,
            3 to 1,
            4 to 5,
            5 to 2,
            6 to 1,
            7 to 2,
            8 to 4,
            9 to 2,
            10 to 2
        )

        val result = QuickAssessmentEngine.evaluate(answers)

        assertNotNull(result)
        assertTrue(result.archetype == HeroArchetype.EXPLORER || result.archetype == HeroArchetype.WARRIOR)
        assertEquals(DimensionType.HEALTH, result.dimensionScores.keys.first { it == DimensionType.HEALTH })
    }
}
