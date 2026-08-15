package com.lifescore.app

import com.lifescore.app.core.util.DateUtils
import com.lifescore.app.core.util.LevelCalculator
import com.lifescore.app.core.util.ScoreEngine
import com.lifescore.app.data.repository.GeminiCoachRepositoryImpl
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.LifeTask
import com.lifescore.app.domain.model.UserProfile
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class OfflineFunctionalityTest {

    @Test
    fun testOfflineTaskCompletionAndXpCalculation() {
        // Simulates completing tasks offline
        val initialUser = UserProfile(currentXp = 80, currentLevel = 1)
        val task = LifeTask(
            id = 1,
            title = "Drink 2L Water",
            dimension = DimensionType.HEALTH,
            pointsReward = 25,
            isCompleted = false
        )

        // Completing the task offline gives +25 XP
        val newXp = initialUser.currentXp + task.pointsReward
        val newLevel = LevelCalculator.calculateLevel(newXp)

        assertEquals(105, newXp)
        assertEquals(2, newLevel) // Level 2 reached at 100 XP
    }

    @Test
    fun testOfflineAiCoachFallback(): Unit = runBlocking {
        // Without network or API key, GeminiCoachRepository falls back gracefully to offline briefs
        val offlineCoach = GeminiCoachRepositoryImpl(apiKey = null)
        val brief = offlineCoach.getDailyExecutiveBrief(
            lowestDimension = DimensionType.HEALTH,
            lowestScore = 40,
            totalScore = 650
        )

        assertNotNull(brief)
        assertTrue(brief.contains("Hydration") || brief.contains("water") || brief.contains("HEALTH"))
    }

    @Test
    fun testOfflineLifeScoreIndexIntegrity() {
        // Verify 8-dimension scoring works without network connection
        val dimensionScores = mapOf(
            DimensionType.HEALTH to 80,
            DimensionType.WEALTH to 70,
            DimensionType.RELATIONSHIPS to 90,
            DimensionType.CAREER to 85,
            DimensionType.LEARNING to 60,
            DimensionType.FITNESS to 100,
            DimensionType.MENTAL_HEALTH to 75,
            DimensionType.SOCIAL_LIFE to 80
        )

        val lifeScoreIndex = ScoreEngine.calculateOverallLifeScore(dimensionScores)
        assertEquals(800, lifeScoreIndex)
    }
}
