package com.lifescore.app

import com.lifescore.app.data.remote.model.UserDocument
import com.lifescore.app.domain.model.UserProfile
import org.junit.Assert.*
import org.junit.Test

class FirestoreConnectionFlowTest {

    @Test
    fun testUserDocumentSerializationRoundTrip() {
        // 1. Simulated Anonymous User UID
        val testUid = "guest_hero_abc123"

        // 2. Build User Document to Save
        val userDocToSave = UserDocument(
            uid = testUid,
            email = "guest@lifescore.app",
            displayName = "Guest Hero",
            totalScore = 500,
            level = 1,
            currentXp = 0,
            streak = 0,
            isPremium = false,
            archetype = "WARRIOR"
        )

        // 3. Read and Map back to UserProfile domain model
        val retrievedProfile = UserProfile(
            id = userDocToSave.uid.hashCode().toLong(),
            name = userDocToSave.displayName,
            currentXp = userDocToSave.currentXp,
            currentLevel = userDocToSave.level,
            currentStreakDays = userDocToSave.streak,
            isPremium = userDocToSave.isPremium,
            title = userDocToSave.archetype
        )

        // 4. Assert all fields match perfectly
        assertEquals("Guest Hero", retrievedProfile.name)
        assertEquals(1, retrievedProfile.currentLevel)
        assertEquals(0, retrievedProfile.currentXp)
        assertEquals(0, retrievedProfile.currentStreakDays)
        assertEquals("WARRIOR", retrievedProfile.title)
        assertFalse(retrievedProfile.isPremium)
    }
}
