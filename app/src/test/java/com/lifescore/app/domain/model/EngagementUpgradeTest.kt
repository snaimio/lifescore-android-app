package com.lifescore.app.domain.model

import com.lifescore.app.core.engine.*
import com.lifescore.app.presentation.ui.templates.HabitTemplatesCatalog
import org.junit.Assert.*
import org.junit.Test

class EngagementUpgradeTest {

    @Test
    fun seasonalEvents_calculatesBossDamageAndMultiplierCorrectly() {
        val event = SeasonalEventsEngine.activeEvent
        assertNotNull(event)
        assertEquals("Solar Solstice Awakening", event.title)
        assertEquals(2.0f, event.xpMultiplier, 0.01f)
        assertEquals(4, event.eventQuests.size)

        val damage = SeasonalEventsEngine.calculateBossDamage(100)
        assertEquals(150, damage)
    }

    @Test
    fun habitTemplates_providesWellFormedStarterStacks() {
        val stacks = HabitTemplatesCatalog.stacks
        assertTrue("Should have multiple pre-configured stacks", stacks.size >= 4)

        val morningStack = stacks.find { it.id == "stack_morning_miracle" }
        assertNotNull(morningStack)
        assertEquals(3, morningStack?.habits?.size)
        assertTrue(morningStack?.habits?.any { it.dimension == DimensionType.HEALTH } == true)
    }

    @Test
    fun taskVerificationEngine_validatesTimerAndPhotoIntegrity() {
        // Timer validation: at least 80%
        assertFalse(TaskVerificationEngine.validateTimerCompletion(targetMinutes = 25, elapsedMinutes = 15))
        assertTrue(TaskVerificationEngine.validateTimerCompletion(targetMinutes = 25, elapsedMinutes = 20))
        assertTrue(TaskVerificationEngine.validateTimerCompletion(targetMinutes = 25, elapsedMinutes = 25))

        // Reflection validation: at least 3 words
        assertFalse(TaskVerificationEngine.validateReflection("Done"))
        assertTrue(TaskVerificationEngine.validateReflection("Completed deep work session"))

        // Bonus XP calculation
        assertEquals(35, TaskVerificationEngine.calculateBonusIntegrityXp(VerificationType.TIMER, 100))
        assertEquals(25, TaskVerificationEngine.calculateBonusIntegrityXp(VerificationType.PHOTO, 100))
        assertEquals(20, TaskVerificationEngine.calculateBonusIntegrityXp(VerificationType.REFLECTION, 100))
        assertEquals(0, TaskVerificationEngine.calculateBonusIntegrityXp(VerificationType.NONE, 100))
    }

    @Test
    fun gentleGamification_handlesStreakFreezeCompassionately() {
        val initial = StreakShieldState(shieldsAvailable = 2, isFreezeActiveToday = false, totalFreezesUsed = 0)
        val (afterFreeze, success) = GentleGamificationEngine.applyStreakFreeze(initial)

        assertTrue(success)
        assertEquals(1, afterFreeze.shieldsAvailable)
        assertTrue(afterFreeze.isFreezeActiveToday)
        assertEquals(1, afterFreeze.totalFreezesUsed)

        val emptyState = StreakShieldState(shieldsAvailable = 0)
        val (_, failedFreeze) = GentleGamificationEngine.applyStreakFreeze(emptyState)
        assertFalse(failedFreeze)

        val reframe = GentleGamificationEngine.getRandomReframe()
        assertNotNull(reframe)
        assertTrue(reframe.resilienceXpBonus > 0)
    }

    @Test
    fun identityHabitEngine_computesIdentityLevelsAndMilestones() {
        val (lvl1, title1) = IdentityHabitEngine.calculateIdentityLevel(5)
        assertEquals(1, lvl1)
        assertEquals("Curious Beginner", title1)

        val (lvl2, title2) = IdentityHabitEngine.calculateIdentityLevel(15)
        assertEquals(2, lvl2)
        assertEquals("Dedicated Apprentice", title2)

        val (lvl3, title3) = IdentityHabitEngine.calculateIdentityLevel(30)
        assertEquals(3, lvl3)
        assertEquals("Consistent Achiever", title3)

        val (lvl5, title5) = IdentityHabitEngine.calculateIdentityLevel(120)
        assertEquals(5, lvl5)
        assertEquals("Master of Identity", title5)
    }
}
