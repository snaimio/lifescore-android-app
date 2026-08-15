package com.lifescore.app

import com.lifescore.app.core.util.MicroVlogManager
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.MicroVlog
import org.junit.Assert.*
import org.junit.Test

class MicroVlogEngineTest {

    @Test
    fun testGenerateWeeklyTemplateIntegrity() {
        val template = MicroVlogManager.generateCurrentWeekTemplate()
        assertEquals(7, template.size)

        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        template.forEachIndexed { index, vlog ->
            assertEquals(days[index], vlog.dayOfWeek)
            assertEquals(2.0, vlog.durationSeconds, 0.001)
            assertTrue(vlog.caption.isNotBlank())
        }
    }

    @Test
    fun testCalculateTotalDuration() {
        val clips = listOf(
            MicroVlog("1", "2026-08-10", "Mon", DimensionType.FITNESS, "Run", durationSeconds = 2.0, isRecorded = true),
            MicroVlog("2", "2026-08-11", "Tue", DimensionType.CAREER, "Work", durationSeconds = 2.0, isRecorded = true),
            MicroVlog("3", "2026-08-12", "Wed", DimensionType.HEALTH, "Meal", durationSeconds = 2.0, isRecorded = true),
            MicroVlog("4", "2026-08-13", "Thu", DimensionType.LEARNING, "Read", durationSeconds = 2.0, isRecorded = false)
        )

        val totalDuration = MicroVlogManager.calculateTotalDuration(clips)
        assertEquals(6.0, totalDuration, 0.001) // 3 recorded clips * 2s = 6s
    }

    @Test
    fun testFindDominantDimension() {
        val clips = listOf(
            MicroVlog("1", "2026-08-10", "Mon", DimensionType.FITNESS, "Run", isRecorded = true),
            MicroVlog("2", "2026-08-11", "Tue", DimensionType.FITNESS, "Gym", isRecorded = true),
            MicroVlog("3", "2026-08-12", "Wed", DimensionType.HEALTH, "Meal", isRecorded = true),
            MicroVlog("4", "2026-08-13", "Thu", DimensionType.LEARNING, "Read", isRecorded = true)
        )

        val dominant = MicroVlogManager.findDominantDimension(clips)
        assertEquals(DimensionType.FITNESS, dominant)
    }

    @Test
    fun testWeeklyMontageCreation() {
        val template = MicroVlogManager.generateCurrentWeekTemplate()
        val montage = MicroVlogManager.createWeeklyMontage(template, scoreGain = 60, streak = 10)

        assertNotNull(montage)
        assertEquals("Aug 10 - Aug 16", montage.weekRange)
        assertEquals(10.0, montage.totalDurationSeconds, 0.001) // 5 recorded clips * 2s = 10s
        assertEquals(60, montage.lifeScoreGain)
        assertEquals(10, montage.streakMaintained)
    }

    @Test
    fun testViralReelShareCaptionFormat() {
        val template = MicroVlogManager.generateCurrentWeekTemplate()
        val montage = MicroVlogManager.createWeeklyMontage(template, streak = 7)
        val caption = MicroVlogManager.generateReelCaption(montage, score = 840, streak = 7)

        assertTrue(caption.contains("10s"))
        assertTrue(caption.contains("840 LifeScore"))
        assertTrue(caption.contains("7-day streak"))
        assertTrue(caption.contains("#LifeScoreReel"))
        assertTrue(caption.contains("https://lifescore.app/reels"))
    }
}
