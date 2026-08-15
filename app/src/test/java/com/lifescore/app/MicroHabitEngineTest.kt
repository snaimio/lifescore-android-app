package com.lifescore.app

import com.lifescore.app.core.util.MicroHabitManager
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.LifeTask
import org.junit.Assert.*
import org.junit.Test

class MicroHabitEngineTest {

    @Test
    fun testGenerate30DayChainMatrix() {
        val chain = MicroHabitManager.generate30DayChain(currentStreak = 5)
        assertEquals(30, chain.size)

        // Verify there is exactly 1 'today' node
        val todayNodes = chain.filter { it.isToday }
        assertEquals(1, todayNodes.size)

        // Verify active streak nodes before today are marked completed
        val todayIndex = chain.indexOfFirst { it.isToday }
        for (i in (todayIndex - 5) until todayIndex) {
            assertTrue("Day at index $i should be completed", chain[i].isCompleted)
        }
    }

    @Test
    fun testDefault30DayChallenges() {
        val challenges = MicroHabitManager.getDefault30DayChallenges()
        assertTrue(challenges.size >= 5)

        val fitnessChallenge = challenges.find { it.dimension == DimensionType.FITNESS }
        assertNotNull(fitnessChallenge)
        assertEquals(30, fitnessChallenge!!.totalDays)
        assertTrue(fitnessChallenge.xpReward >= 400)
    }

    @Test
    fun testChallengeProgressIncrement() {
        val challenges = MicroHabitManager.getDefault30DayChallenges().toMutableList()
        val ch = challenges[0]
        val currentDay = ch.currentDay
        val updated = ch.copy(currentDay = currentDay + 1)

        assertEquals(currentDay + 1, updated.currentDay)
        assertFalse(updated.isCompleted)

        val finished = ch.copy(currentDay = 30, isCompleted = true)
        assertTrue(finished.isCompleted)
    }

    @Test
    fun testMicroHabitTaskPointsAndDimensionAttribution() {
        val task = LifeTask(
            id = 10,
            title = "Drink 500ml Water",
            dimension = DimensionType.HEALTH,
            pointsReward = 15,
            isCompleted = false
        )

        assertEquals(DimensionType.HEALTH, task.dimension)
        assertEquals(15, task.pointsReward)
        assertFalse(task.isCompleted)
    }
}
