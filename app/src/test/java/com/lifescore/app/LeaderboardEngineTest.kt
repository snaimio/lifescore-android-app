package com.lifescore.app

import com.lifescore.app.core.util.LeagueManager
import com.lifescore.app.core.util.LeagueTier
import com.lifescore.app.core.util.RankZone
import org.junit.Assert.*
import org.junit.Test

class LeaderboardEngineTest {

    @Test
    fun testScoreToLeagueTierMapping() {
        assertEquals(LeagueTier.BRONZE, LeagueManager.getLeagueForScore(150))
        assertEquals(LeagueTier.BRONZE, LeagueManager.getLeagueForScore(399))
        assertEquals(LeagueTier.SILVER, LeagueManager.getLeagueForScore(400))
        assertEquals(LeagueTier.SILVER, LeagueManager.getLeagueForScore(599))
        assertEquals(LeagueTier.GOLD, LeagueManager.getLeagueForScore(600))
        assertEquals(LeagueTier.GOLD, LeagueManager.getLeagueForScore(749))
        assertEquals(LeagueTier.PLATINUM, LeagueManager.getLeagueForScore(750))
        assertEquals(LeagueTier.PLATINUM, LeagueManager.getLeagueForScore(899))
        assertEquals(LeagueTier.DIAMOND, LeagueManager.getLeagueForScore(900))
        assertEquals(LeagueTier.DIAMOND, LeagueManager.getLeagueForScore(1000))
    }

    @Test
    fun testRankZonePromotionAndRelegation() {
        val totalPlayers = 20

        // Top 3 should always be in Promotion Zone
        assertEquals(RankZone.PROMOTION, LeagueManager.getRankZone(1, totalPlayers))
        assertEquals(RankZone.PROMOTION, LeagueManager.getRankZone(2, totalPlayers))
        assertEquals(RankZone.PROMOTION, LeagueManager.getRankZone(3, totalPlayers))

        // Middle players should be in Safe Zone
        assertEquals(RankZone.SAFE, LeagueManager.getRankZone(4, totalPlayers))
        assertEquals(RankZone.SAFE, LeagueManager.getRankZone(10, totalPlayers))
        assertEquals(RankZone.SAFE, LeagueManager.getRankZone(15, totalPlayers))

        // Bottom 2 players in a group of 20 should be in Relegation Zone
        assertEquals(RankZone.RELEGATION, LeagueManager.getRankZone(19, totalPlayers))
        assertEquals(RankZone.RELEGATION, LeagueManager.getRankZone(20, totalPlayers))
    }

    @Test
    fun testSundayResetCountdownString() {
        val countdown = LeagueManager.getCountdownToSunday()
        assertNotNull(countdown)
        assertTrue(countdown.contains("left"))
        assertTrue(countdown.contains("h") || countdown.contains("d"))
    }
}
