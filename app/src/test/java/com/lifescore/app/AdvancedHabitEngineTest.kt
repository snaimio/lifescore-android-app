package com.lifescore.app

import com.lifescore.app.core.util.SmartHabitEngine
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.HabitType
import com.lifescore.app.domain.model.LifeTask
import com.lifescore.app.domain.model.SubTaskItem
import org.junit.Assert.*
import org.junit.Test

class AdvancedHabitEngineTest {

    @Test
    fun testDefaultAdvancedHabitsCompleteness() {
        val habits = SmartHabitEngine.getDefaultAdvancedHabits()
        assertTrue(habits.size >= 6)

        val types = habits.map { it.habitType }
        assertTrue(types.contains(HabitType.COUNTER))
        assertTrue(types.contains(HabitType.SUB_TASKS))
        assertTrue(types.contains(HabitType.BOOLEAN))
    }

    @Test
    fun testCounterHabitProgressionAndAutoCompletion() {
        val task = LifeTask(
            id = 1,
            title = "Hydrate 8 Glasses 💧",
            dimension = DimensionType.HEALTH,
            pointsReward = 20,
            habitType = HabitType.COUNTER,
            currentCount = 6,
            targetCount = 8,
            countUnit = "glasses",
            streakDays = 5
        )

        assertEquals(0.75f, task.counterProgress, 0.01f)
        assertFalse(task.isCompleted)

        // Increment to 7
        val step7 = SmartHabitEngine.incrementCounter(task, 1)
        assertEquals(7, step7.currentCount)
        assertFalse(step7.isCompleted)

        // Increment to 8 (Reaches target)
        val step8 = SmartHabitEngine.incrementCounter(step7, 1)
        assertEquals(8, step8.currentCount)
        assertTrue(step8.isCompleted)
        assertNotNull(step8.completedAt)
        assertEquals(6, step8.streakDays)

        // Decrement back to 7 (Unmarks completion)
        val decStep = SmartHabitEngine.decrementCounter(step8, 1)
        assertEquals(7, decStep.currentCount)
        assertFalse(decStep.isCompleted)
    }

    @Test
    fun testSubTasksRoutineProgressAndCompletion() {
        val task = LifeTask(
            id = 2,
            title = "🌙 3-Step Sleep Routine",
            dimension = DimensionType.HEALTH,
            pointsReward = 30,
            habitType = HabitType.SUB_TASKS,
            subTasks = listOf(
                SubTaskItem(id = "s1", title = "Magnesium", isCompleted = false),
                SubTaskItem(id = "s2", title = "Amber Glasses", isCompleted = false),
                SubTaskItem(id = "s3", title = "67F Room", isCompleted = false)
            )
        )

        assertEquals(0f, task.subTaskProgress, 0.01f)
        assertFalse(task.isCompleted)

        // Toggle s1
        val step1 = SmartHabitEngine.toggleSubTask(task, "s1")
        assertEquals(1f / 3f, step1.subTaskProgress, 0.01f)
        assertFalse(step1.isCompleted)

        // Toggle s2
        val step2 = SmartHabitEngine.toggleSubTask(step1, "s2")
        assertEquals(2f / 3f, step2.subTaskProgress, 0.01f)
        assertFalse(step2.isCompleted)

        // Toggle s3 (All done)
        val step3 = SmartHabitEngine.toggleSubTask(step2, "s3")
        assertEquals(1f, step3.subTaskProgress, 0.01f)
        assertTrue(step3.isCompleted)
        assertNotNull(step3.completedAt)
    }

    @Test
    fun testSmartHabitAutoSuggestionsForAllDimensions() {
        DimensionType.values().forEach { dim ->
            val suggested = SmartHabitEngine.getSmartSuggestionForDimension(dim)
            assertEquals(dim, suggested.dimension)
            assertTrue(suggested.isSmartSuggested)
            assertTrue(suggested.title.isNotBlank())
            assertTrue(suggested.pointsReward >= 15)
        }
    }
}
