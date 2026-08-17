package com.lifescore.app.domain.model

import org.junit.Assert.*
import org.junit.Test

class AiQuestAndCharacterTest {

    @Test
    fun testQuestDifficultyScaling() {
        assertEquals("E", QuestDifficulty.E.rankLetter)
        assertEquals(1.0f, QuestDifficulty.E.xpMultiplier)

        assertEquals("S", QuestDifficulty.S.rankLetter)
        assertEquals(10.0f, QuestDifficulty.S.xpMultiplier)
        assertEquals(15, QuestDifficulty.S.statRewardPoints)

        assertEquals(QuestDifficulty.S, QuestDifficulty.fromRankLetter("S"))
        assertEquals(QuestDifficulty.A, QuestDifficulty.fromRankLetter("a"))
        assertEquals(QuestDifficulty.C, QuestDifficulty.fromRankLetter("unknown"))
    }

    @Test
    fun testCharacterCombatPowerCalculation() {
        val baseStats = CharacterStats(
            strength = 10,
            vitality = 10,
            agility = 10,
            intelligence = 10,
            perception = 10,
            availablePoints = 5
        )

        assertEquals(50, baseStats.totalStats)
        // 10*2.5 + 10*2.0 + 10*2.2 + 10*2.8 + 10*2.1 = 25 + 20 + 22 + 28 + 21 = 116
        assertEquals(116, baseStats.combatPower)

        val upgradedStats = baseStats.copy(
            strength = 15,
            intelligence = 20
        )
        assertTrue(upgradedStats.combatPower > baseStats.combatPower)
    }

    @Test
    fun testTitleCatalog() {
        val allTitles = TitleCatalog.allTitles
        assertTrue(allTitles.isNotEmpty())
        val defaultTitle = allTitles.first()
        assertTrue(defaultTitle.isUnlocked)
        assertTrue(defaultTitle.isEquipped)
    }
}
