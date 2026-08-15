package com.lifescore.app

import com.lifescore.app.core.util.StreakShieldManager
import com.lifescore.app.data.remote.model.GuardianSponsorshipDocument
import com.lifescore.app.data.remote.model.UserDocument
import org.junit.Assert.*
import org.junit.Test

class StreakInsuranceAndGuardianTest {

    @Test
    fun testShieldPurchaseUpToMaxCap() {
        // Initial shields = 1
        val (success1, count1) = StreakShieldManager.purchaseShield(1)
        assertTrue(success1)
        assertEquals(2, count1)

        // Purchase another shield -> 3 (Max)
        val (success2, count2) = StreakShieldManager.purchaseShield(2)
        assertTrue(success2)
        assertEquals(3, count2)

        // Attempt to purchase beyond 3 -> should fail and remain 3
        val (success3, count3) = StreakShieldManager.purchaseShield(3)
        assertFalse(success3)
        assertEquals(3, count3)
    }

    @Test
    fun testShieldConsumption() {
        // Consume 1 shield from 2 available
        val (success1, count1) = StreakShieldManager.consumeShield(2)
        assertTrue(success1)
        assertEquals(1, count1)

        // Consume last shield
        val (success2, count2) = StreakShieldManager.consumeShield(1)
        assertTrue(success2)
        assertEquals(0, count2)

        // Attempt consume from 0
        val (success3, count3) = StreakShieldManager.consumeShield(0)
        assertFalse(success3)
        assertEquals(0, count3)
    }

    @Test
    fun testThankYouCaptionGeneration() {
        val caption = StreakShieldManager.generateThankYouCaption(
            recipientName = "Elena",
            sponsorName = "Marcus"
        )
        assertTrue(caption.contains("@Marcus"))
        assertTrue(caption.contains("#GuardianProgram"))
        assertTrue(caption.contains("https://lifescore.app"))
    }

    @Test
    fun testUserDocumentShieldAndGuardianFields() {
        val userDoc = UserDocument(
            uid = "sponsored_user_01",
            displayName = "Student Hero",
            shieldsRemaining = 2,
            guardianId = "guardian_marcus_99",
            isSponsored = true,
            shieldsUsedCount = 1
        )

        assertEquals(2, userDoc.shieldsRemaining)
        assertEquals("guardian_marcus_99", userDoc.guardianId)
        assertTrue(userDoc.isSponsored)
        assertEquals(1, userDoc.shieldsUsedCount)
    }

    @Test
    fun testGuardianSponsorshipDocumentCreation() {
        val sponsorship = GuardianSponsorshipDocument(
            id = "sp_100",
            sponsorUid = "u_marcus",
            sponsorName = "Marcus Vance",
            recipientEmail = "student@university.edu",
            message = "Believe in your potential!"
        )

        assertEquals("sp_100", sponsorship.id)
        assertEquals("Marcus Vance", sponsorship.sponsorName)
        assertEquals("student@university.edu", sponsorship.recipientEmail)
        assertTrue(sponsorship.sponsoredDate > 0)
    }
}
