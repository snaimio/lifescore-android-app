package com.lifescore.app

import com.lifescore.app.core.util.ExpertChallengeManager
import com.lifescore.app.domain.model.DimensionType
import org.junit.Assert.*
import org.junit.Test

class ExpertChallengeEngineTest {

    @Test
    fun testMasterclassesCurriculumCompleteness() {
        val masterclasses = ExpertChallengeManager.masterclasses
        assertEquals(5, masterclasses.size)

        masterclasses.forEach { mc ->
            assertEquals(14, mc.durationDays)
            assertEquals(14, mc.days.size)
            assertTrue(mc.coachName.isNotBlank())
            assertTrue(mc.coachCredentials.isNotBlank())
            assertTrue(mc.priceUsd > 0.0)

            // Check that all 14 days have distinct day numbers and tasks
            val dayNumbers = mc.days.map { it.dayNumber }
            assertEquals((1..14).toList(), dayNumbers)
            mc.days.forEach { day ->
                assertTrue(day.dailyTaskPoints >= 50)
                assertTrue(day.audioDurationSeconds >= 240) // at least 4-5 mins
                assertTrue(day.transcriptSummary.isNotBlank())
            }
        }
    }

    @Test
    fun testDayCheckInProgression() {
        val original = ExpertChallengeManager.masterclasses.first()
        assertEquals(0, original.completedDaysCount)
        assertEquals(0f, original.progressPercentage, 0.01f)
        assertFalse(original.isCompleted)

        // Check in Day 1
        val afterDay1 = ExpertChallengeManager.checkInDay(original, 1)
        assertEquals(1, afterDay1.completedDaysCount)
        assertTrue(afterDay1.days[0].isCompleted)
        assertEquals(2, afterDay1.currentDay)
        assertFalse(afterDay1.isCompleted)

        // Complete all 14 days
        var current = original
        for (day in 1..14) {
            current = ExpertChallengeManager.checkInDay(current, day)
        }
        assertEquals(14, current.completedDaysCount)
        assertEquals(1f, current.progressPercentage, 0.01f)
        assertTrue(current.isCompleted)
        assertNotNull(current.graduationDate)
    }

    @Test
    fun testCertificateGenerationAndVerificationHash() {
        val masterclass = ExpertChallengeManager.masterclasses.first()
        val certificate = ExpertChallengeManager.generateCertificate(masterclass, "Elena Rostova")

        assertEquals("Elena Rostova", certificate.userName)
        assertEquals(masterclass.title, certificate.masterclassTitle)
        assertEquals(masterclass.coachName, certificate.coachName)
        assertEquals(masterclass.dimension, certificate.dimension)
        assertTrue(certificate.certificateId.startsWith("CERT-"))
        assertEquals(16, certificate.verificationHash.length)
        assertEquals(1200, certificate.xpEarnedTotal) // (14 * 50) + 500 = 1200
    }

    @Test
    fun testCertificateShareCaptionFormatting() {
        val masterclass = ExpertChallengeManager.masterclasses.first()
        val cert = ExpertChallengeManager.generateCertificate(masterclass, "Elena Rostova")
        val caption = ExpertChallengeManager.generateCertificateShareCaption(cert)

        assertTrue(caption.contains("Certified in"))
        assertTrue(caption.contains(cert.certificateId))
        assertTrue(caption.contains("LifeScore Masterclass Graduate"))
        assertTrue(caption.contains("https://lifescore.app/masterclasses"))
    }
}
