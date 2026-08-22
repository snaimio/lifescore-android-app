package com.lifescore.app

import com.lifescore.app.core.engine.FeatureCategory
import com.lifescore.app.core.engine.FeatureUnlockManager
import com.lifescore.app.core.engine.UserPhase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureUnlockManagerTest {

    @Test
    fun testAllFeaturesCountContainsAtLeast25Entries() {
        assertTrue(FeatureUnlockManager.allFeatures.size >= 25)
    }

    @Test
    fun testNewUserHasBasicFeaturesUnlocked() {
        val newFeatures = FeatureUnlockManager.getUnlockedFeatures(UserPhase.NEW_USER)

        assertTrue(newFeatures.any { it.id == "home" })
        assertTrue(newFeatures.any { it.id == "quests" })
        assertTrue(newFeatures.any { it.id == "quick_stats" })
        assertTrue(newFeatures.any { it.id == "ai_coach" })

        // Advanced features should be locked in NEW phase
        assertFalse(FeatureUnlockManager.isFeatureUnlocked("hydration", UserPhase.NEW_USER))
        assertFalse(FeatureUnlockManager.isFeatureUnlocked("enterprise", UserPhase.NEW_USER))
    }

    @Test
    fun testExploringUserUnlocks8DimensionsAndLeaderboard() {
        assertTrue(FeatureUnlockManager.isFeatureUnlocked("all_dimensions", UserPhase.EXPLORING))
        assertTrue(FeatureUnlockManager.isFeatureUnlocked("leaderboards", UserPhase.EXPLORING))
        assertTrue(FeatureUnlockManager.isFeatureUnlocked("journal", UserPhase.EXPLORING))
    }

    @Test
    fun testAdvancedUserUnlocksTrackersAndChallenges() {
        assertTrue(FeatureUnlockManager.isFeatureUnlocked("hydration", UserPhase.ADVANCED))
        assertTrue(FeatureUnlockManager.isFeatureUnlocked("sleep", UserPhase.ADVANCED))
        assertTrue(FeatureUnlockManager.isFeatureUnlocked("challenges", UserPhase.ADVANCED))
        assertTrue(FeatureUnlockManager.isFeatureUnlocked("rewards", UserPhase.ADVANCED))
    }

    @Test
    fun testExpertUserUnlocksAllFeatures() {
        val lockedForExpert = FeatureUnlockManager.getLockedFeatures(UserPhase.EXPERT)
        assertEquals(0, lockedForExpert.size)

        val unlockedForExpert = FeatureUnlockManager.getUnlockedFeatures(UserPhase.EXPERT)
        assertEquals(FeatureUnlockManager.allFeatures.size, unlockedForExpert.size)
    }

    @Test
    fun testCategoriesFiltering() {
        val trackers = FeatureUnlockManager.getFeaturesByCategory(FeatureCategory.TRACKERS)
        assertTrue(trackers.isNotEmpty())
        assertTrue(trackers.any { it.id == "hydration" })

        val recovery = FeatureUnlockManager.getFeaturesByCategory(FeatureCategory.RECOVERY)
        assertTrue(recovery.any { it.id == "recovery" })
    }
}
