package com.lifescore.app

import com.lifescore.app.core.util.SkillMasteryManager
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.MasteryTier
import com.lifescore.app.domain.model.SkillMastery
import org.junit.Assert.*
import org.junit.Test

class SkillMasteryEngineTest {

    @Test
    fun testMasteryTierBoundaries() {
        assertEquals(MasteryTier.NOVICE, MasteryTier.fromHours(0f))
        assertEquals(MasteryTier.NOVICE, MasteryTier.fromHours(99.5f))

        assertEquals(MasteryTier.APPRENTICE, MasteryTier.fromHours(100f))
        assertEquals(MasteryTier.APPRENTICE, MasteryTier.fromHours(499f))

        assertEquals(MasteryTier.JOURNEYMAN, MasteryTier.fromHours(500f))
        assertEquals(MasteryTier.JOURNEYMAN, MasteryTier.fromHours(1999f))

        assertEquals(MasteryTier.EXPERT, MasteryTier.fromHours(2000f))
        assertEquals(MasteryTier.EXPERT, MasteryTier.fromHours(4999f))

        assertEquals(MasteryTier.MASTER, MasteryTier.fromHours(5000f))
        assertEquals(MasteryTier.MASTER, MasteryTier.fromHours(9999f))

        assertEquals(MasteryTier.OUTLIER_LEGEND, MasteryTier.fromHours(10000f))
        assertEquals(MasteryTier.OUTLIER_LEGEND, MasteryTier.fromHours(15000f))
    }

    @Test
    fun testDefaultSkillsDataIntegrity() {
        val defaultSkills = SkillMasteryManager.getDefaultSkills()
        assertTrue(defaultSkills.size >= 5)

        val japanese = defaultSkills.find { it.title.contains("Japanese") }
        assertNotNull(japanese)
        assertEquals(DimensionType.LEARNING, japanese!!.dimension)
        assertEquals(240f, japanese.totalHours, 0.1f)
        assertEquals(MasteryTier.APPRENTICE, japanese.currentTier)

        val systems = defaultSkills.find { it.title.contains("Distributed Systems") }
        assertNotNull(systems)
        assertEquals(DimensionType.CAREER, systems!!.dimension)
        assertEquals(1200f, systems.totalHours, 0.1f)
        assertEquals(MasteryTier.JOURNEYMAN, systems.currentTier)
    }

    @Test
    fun testLogPracticeSessionMathAndXp() {
        val skill = SkillMastery(
            title = "Chess Tactics & Opening Theory",
            emoji = "♟️",
            dimension = DimensionType.LEARNING,
            targetHours = 1000,
            accumulatedMinutes = 60, // 1.0 hour
            streakDays = 1,
            lastPracticedDate = "2026-08-10",
            sessionsCount = 1,
            totalXpEarned = 25
        )

        // Log a 60-minute session
        val (updated, session) = SkillMasteryManager.logPracticeSession(skill, 60, "Studied Sicilian Defense")

        assertEquals(120, updated.accumulatedMinutes)
        assertEquals(2.0f, updated.totalHours, 0.01f)
        assertEquals(2, updated.sessionsCount)
        assertEquals(25, session.xpGranted) // 60 mins * 25 / 60 = 25 XP
        assertEquals(50, updated.totalXpEarned)
        assertEquals(2, updated.streakDays)
    }

    @Test
    fun testCalculate10kGlobalProgress() {
        val skills = listOf(
            SkillMastery(title = "A", emoji = "A", dimension = DimensionType.CAREER, accumulatedMinutes = 60000), // 1,000h
            SkillMastery(title = "B", emoji = "B", dimension = DimensionType.LEARNING, accumulatedMinutes = 90000) // 1,500h
        )

        val totalHours = SkillMasteryManager.calculateTotalPracticeHours(skills)
        assertEquals(2500f, totalHours, 0.1f)

        val progress = SkillMasteryManager.calculate10kGlobalProgress(skills)
        assertEquals(0.25f, progress, 0.001f) // 2500 / 10000 = 25%

        val tier = SkillMasteryManager.getGlobalMasteryTier(totalHours)
        assertEquals(MasteryTier.EXPERT, tier)
    }
}
