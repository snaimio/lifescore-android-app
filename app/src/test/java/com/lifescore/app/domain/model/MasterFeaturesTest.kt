package com.lifescore.app.domain.model

import org.junit.Assert.*
import org.junit.Test

class MasterFeaturesTest {

    @Test
    fun groupHabit_progressFractionCalculation_isAccurate() {
        val habit = GroupHabit(
            id = "test_squad",
            title = "Test Squad",
            description = "Description",
            dimension = DimensionType.HEALTH,
            memberCount = 10,
            todayCompletedCount = 7
        )
        assertEquals(0.7f, habit.progressFraction, 0.001f)
    }

    @Test
    fun combatBoss_hpPercentageAndDefeatState_calculatedCorrectly() {
        val boss = CombatBoss(
            id = "boss_test",
            name = "Test Demon",
            title = "Lord of Tests",
            dimension = DimensionType.CAREER,
            maxHp = 500,
            currentHp = 250,
            attackPower = 30,
            avatarEmoji = "👹",
            rewardXp = 500,
            rewardStatPoints = 3
        )
        assertEquals(0.5f, boss.hpPercentage, 0.001f)
        assertFalse(boss.isDefeated)
    }

    @Test
    fun journalMood_titlesAndColors_areConfigured() {
        assertEquals("🔥", JournalMood.EXCITED.emoji)
        assertEquals("😊", JournalMood.HAPPY.emoji)
        assertEquals("😐", JournalMood.NEUTRAL.emoji)
        assertEquals("😔", JournalMood.STRESSED.emoji)
        assertEquals("😴", JournalMood.TIRED.emoji)
    }
}
