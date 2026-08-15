package com.lifescore.app

import com.lifescore.app.core.util.DuelManager
import com.lifescore.app.domain.model.ChallengeParticipant
import com.lifescore.app.domain.model.DimensionType
import org.junit.Assert.*
import org.junit.Test

class SocialDuelEngineTest {

    @Test
    fun testDailyCheckInStatusGeneration() {
        val checkIns = DuelManager.getDailyCheckInStatus(completedDays = 4, totalDays = 7)
        assertEquals(7, checkIns.size)
        assertTrue(checkIns[0])
        assertTrue(checkIns[1])
        assertTrue(checkIns[2])
        assertTrue(checkIns[3])
        assertFalse(checkIns[4])
        assertFalse(checkIns[5])
        assertFalse(checkIns[6])
    }

    @Test
    fun testDuelWinnerCalculation() {
        val p1 = ChallengeParticipant("u1", "Sarah Chen", level = 4, completedDays = 6, streak = 14)
        val p2 = ChallengeParticipant("u2", "Marcus Vance", level = 5, completedDays = 7, streak = 21)
        val p3 = ChallengeParticipant("u3", "Alex Morgan", level = 3, completedDays = 5, streak = 7)

        val winner = DuelManager.calculateDuelWinner(listOf(p1, p2, p3), durationDays = 7)

        assertNotNull(winner)
        assertEquals("u2", winner?.uid)
        assertEquals(7, winner?.completedDays)
    }

    @Test
    fun testDuelTieBreakingByStreak() {
        val p1 = ChallengeParticipant("u1", "Elena", level = 5, completedDays = 7, streak = 12)
        val p2 = ChallengeParticipant("u2", "David", level = 5, completedDays = 7, streak = 18)

        val winner = DuelManager.calculateDuelWinner(listOf(p1, p2), durationDays = 7)

        assertEquals("u2", winner?.uid)
        assertEquals(18, winner?.streak)
    }

    @Test
    fun testInviteLinkAndCaptionGeneration() {
        val inviteLink = DuelManager.generateInviteLink("c_fit_07", "Champion Hero")
        assertTrue(inviteLink.startsWith("https://lifescore.app/challenge/c_fit_07?invitedBy=Champion_Hero"))

        val caption = DuelManager.generateDuelCaption(
            challengeTitle = "7-Day Cardio Blitz",
            dimension = DimensionType.FITNESS,
            inviteUrl = inviteLink
        )

        assertTrue(caption.contains("7-Day Cardio Blitz"))
        assertTrue(caption.contains("Fitness"))
        assertTrue(caption.contains(inviteLink))
        assertTrue(caption.contains("#LifeScoreDuel"))
    }
}
