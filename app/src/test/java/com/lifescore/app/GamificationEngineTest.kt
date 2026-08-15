package com.lifescore.app

import com.lifescore.app.core.util.LevelCalculator
import com.lifescore.app.core.util.ScoreEngine
import com.lifescore.app.domain.model.DimensionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GamificationEngineTest {

    @Test
    fun testLevelProgression() {
        // Level 1 starts at 0 XP
        assertEquals(1, LevelCalculator.calculateLevel(0))
        assertEquals(1, LevelCalculator.calculateLevel(99))

        // Level 2 reached at 100 XP
        assertEquals(2, LevelCalculator.calculateLevel(100))

        // Level 3+ reaches with 1.25x multiplier
        assertTrue(LevelCalculator.calculateLevel(500) >= 3)
    }

    @Test
    fun testDimensionScoreCalculation() {
        // 0 completed out of 4 tasks = 0%
        assertEquals(0, ScoreEngine.calculateDimensionScore(0, 4))

        // 2 completed out of 4 tasks = 50%
        assertEquals(50, ScoreEngine.calculateDimensionScore(2, 4))

        // 4 completed out of 4 tasks = 100%
        assertEquals(100, ScoreEngine.calculateDimensionScore(4, 4))

        // Empty tasks = neutral baseline 50
        assertEquals(50, ScoreEngine.calculateDimensionScore(0, 0))
    }

    @Test
    fun testOverallLifeScoreCalculation() {
        val scores = DimensionType.values().associateWith { 80 }
        val overall = ScoreEngine.calculateOverallLifeScore(scores)

        // 80% across all 8 dimensions = 800/1000 index
        assertEquals(800, overall)
    }
}
