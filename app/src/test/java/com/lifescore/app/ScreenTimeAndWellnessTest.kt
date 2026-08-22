package com.lifescore.app

import com.lifescore.app.data.local.entity.*
import com.lifescore.app.data.repository.AppUsageItemModel
import com.lifescore.app.data.repository.ScreenTimeUsageSummary
import org.junit.Assert.*
import org.junit.Test

class ScreenTimeAndWellnessTest {

    @Test
    fun testSweatPassMovementConversionCalculation() {
        // SweatPass rule: 1 bonus minute per 5 reps
        val reps15 = 15
        val bonusMins15 = (reps15 / 5).coerceAtLeast(1)
        assertEquals(3, bonusMins15)

        val reps2 = 2
        val bonusMins2 = (reps2 / 5).coerceAtLeast(1)
        assertEquals(1, bonusMins2)

        val xpAward = bonusMins15 * 15
        assertEquals(45, xpAward)
    }

    @Test
    fun testScreenTimeUsageSummaryGoalCalculation() {
        val summary = ScreenTimeUsageSummary(
            date = "2026-08-21",
            totalMinutes = 85,
            dailyLimitMinutes = 120,
            bonusMinutesEarned = 15,
            effectiveLimitMinutes = 135,
            pickups = 34,
            isGoalMet = true,
            socialMinutes = 45,
            gamingMinutes = 20,
            videoMinutes = 15,
            shoppingMinutes = 5,
            topApps = listOf(
                AppUsageItemModel("Instagram", 45, ScreenTimeActionType.SOCIAL_MEDIA, "📸"),
                AppUsageItemModel("YouTube", 25, ScreenTimeActionType.VIDEO_STREAMING, "▶️")
            )
        )

        assertTrue(summary.isGoalMet)
        assertEquals(135, summary.effectiveLimitMinutes)
        assertEquals(2, summary.topApps.size)
        assertTrue(summary.totalMinutes <= summary.effectiveLimitMinutes)
    }

    @Test
    fun testScreenTimeChallengeAdvancement() {
        val challenge = ScreenTimeChallenge(
            id = 1L,
            userId = "user_1",
            challengeType = "DETOX",
            title = "7-Day Digital Detox",
            targetDays = 7,
            currentDay = 6,
            isActive = true,
            xpReward = 150
        )

        val nextDay = challenge.currentDay + 1
        val isCompleted = nextDay >= challenge.targetDays
        val updated = challenge.copy(
            currentDay = nextDay,
            isActive = !isCompleted,
            completedAt = if (isCompleted) System.currentTimeMillis() else null
        )

        assertEquals(7, updated.currentDay)
        assertFalse(updated.isActive)
        assertNotNull(updated.completedAt)
    }

    @Test
    fun testThoughtBreakLogDataIntegrity() {
        val log = ThoughtBreakLog(
            userId = "user_1",
            automaticThought = "I have too much to do, it's impossible",
            cognitiveDistortion = "Catastrophizing",
            evidenceAgainst = "I can break it down into 3 priority blocks",
            reframedThought = "I will complete priority 1 in 25 mins",
            emotionalReliefRating = 9
        )

        assertEquals("Catastrophizing", log.cognitiveDistortion)
        assertEquals(9, log.emotionalReliefRating)
        assertTrue(log.timestamp > 0)
    }
}
