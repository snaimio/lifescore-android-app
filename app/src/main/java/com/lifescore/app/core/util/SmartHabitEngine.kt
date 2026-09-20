package com.lifescore.app.core.util

import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.HabitType
import com.lifescore.app.domain.model.LifeTask
import com.lifescore.app.domain.model.SubTaskItem

object SmartHabitEngine {

    fun getDefaultAdvancedHabits(): List<LifeTask> {
        return listOf(
            // 1. Counter Habit: 8/8 glasses hydration
            LifeTask(
                id = 101,
                title = "Hydrate 8 Glasses of Mineral Water 💧",
                dimension = DimensionType.HEALTH,
                pointsReward = 20,
                habitType = HabitType.COUNTER,
                currentCount = 5,
                targetCount = 8,
                countUnit = "glasses",
                streakDays = 8
            ),
            // 2. Counter Habit: 10,000 Steps
            LifeTask(
                id = 102,
                title = "Daily 10,000 Steps Baseline 🏃",
                dimension = DimensionType.FITNESS,
                pointsReward = 25,
                habitType = HabitType.COUNTER,
                currentCount = 7500,
                targetCount = 10000,
                countUnit = "steps",
                streakDays = 14
            ),
            // 3. Sub-Tasks Routine: 4-Step Circadian Sleep Routine
            LifeTask(
                id = 103,
                title = "🌙 4-Step Circadian Sleep Wind-Down",
                dimension = DimensionType.HEALTH,
                pointsReward = 30,
                habitType = HabitType.SUB_TASKS,
                streakDays = 6,
                subTasks = listOf(
                    SubTaskItem(id = "st_1", title = "Take 400mg Magnesium Glycinate & L-Theanine", isCompleted = true),
                    SubTaskItem(id = "st_2", title = "Put on 100% amber blue-blocking glasses at 9 PM", isCompleted = true),
                    SubTaskItem(id = "st_3", title = "Set bedroom temperature to 67°F (19°C) & pitch dark", isCompleted = false),
                    SubTaskItem(id = "st_4", title = "10-minute NSDR / physiological breathwork in bed", isCompleted = false)
                )
            ),
            // 4. Sub-Tasks Routine: 3 Most Important Tasks (MITs)
            LifeTask(
                id = 104,
                title = "🎯 3 Most Important Tasks (MITs) Execution",
                dimension = DimensionType.CAREER,
                pointsReward = 35,
                habitType = HabitType.SUB_TASKS,
                streakDays = 11,
                subTasks = listOf(
                    SubTaskItem(id = "st_5", title = "Define #1 single highest-leverage priority before opening email", isCompleted = true),
                    SubTaskItem(id = "st_6", title = "Complete 90-minute uninterrupted deep work sprint", isCompleted = true),
                    SubTaskItem(id = "st_7", title = "Log progress & shutdown communication by 6 PM", isCompleted = false)
                )
            ),
            // 5. Boolean Habit: Morning Cold Splash
            LifeTask(
                id = 105,
                title = "60s Cold Shower Finish & Sunlight ☀️",
                dimension = DimensionType.HEALTH,
                pointsReward = 15,
                habitType = HabitType.BOOLEAN,
                isCompleted = true,
                streakDays = 7
            ),
            // 6. Boolean Habit: Zero Impulse Spending
            LifeTask(
                id = 106,
                title = "Zero Impulse Spending Day 💰",
                dimension = DimensionType.WEALTH,
                pointsReward = 20,
                habitType = HabitType.BOOLEAN,
                isCompleted = false,
                streakDays = 15
            )
        )
    }

    fun incrementCounter(task: LifeTask, amount: Int = 1): LifeTask {
        val newCount = (task.currentCount + amount).coerceAtMost(task.targetCount * 2)
        val isNowCompleted = newCount >= task.targetCount
        val completedTime = if (isNowCompleted && !task.isCompleted) System.currentTimeMillis() else task.completedAt

        return task.copy(
            currentCount = newCount,
            isCompleted = isNowCompleted,
            completedAt = completedTime,
            streakDays = if (isNowCompleted && !task.isCompleted) task.streakDays + 1 else task.streakDays
        )
    }

    fun decrementCounter(task: LifeTask, amount: Int = 1): LifeTask {
        val newCount = (task.currentCount - amount).coerceAtLeast(0)
        val isStillCompleted = newCount >= task.targetCount

        return task.copy(
            currentCount = newCount,
            isCompleted = isStillCompleted,
            completedAt = if (isStillCompleted) task.completedAt else null
        )
    }

    fun toggleSubTask(task: LifeTask, subTaskId: String): LifeTask {
        val updatedSubTasks = task.subTasks.map { item ->
            if (item.id == subTaskId) item.copy(isCompleted = !item.isCompleted) else item
        }
        val isAllCompleted = updatedSubTasks.isNotEmpty() && updatedSubTasks.all { it.isCompleted }
        val completedTime = if (isAllCompleted && !task.isCompleted) System.currentTimeMillis() else if (isAllCompleted) task.completedAt else null

        return task.copy(
            subTasks = updatedSubTasks,
            isCompleted = isAllCompleted,
            completedAt = completedTime,
            streakDays = if (isAllCompleted && !task.isCompleted) task.streakDays + 1 else task.streakDays
        )
    }

    fun getSmartSuggestionForDimension(dimension: DimensionType): LifeTask {
        return when (dimension) {
            DimensionType.HEALTH -> LifeTask(
                title = "💧 Daily 8-Glass Hydration & Electrolytes",
                dimension = DimensionType.HEALTH,
                pointsReward = 20,
                habitType = HabitType.COUNTER,
                currentCount = 0,
                targetCount = 8,
                countUnit = "glasses",
                isSmartSuggested = true
            )
            DimensionType.LEARNING -> LifeTask(
                title = "📚 20 Pages Deep Reading Immersion",
                dimension = DimensionType.LEARNING,
                pointsReward = 25,
                habitType = HabitType.COUNTER,
                currentCount = 0,
                targetCount = 20,
                countUnit = "pages",
                isSmartSuggested = true
            )
            DimensionType.FITNESS -> LifeTask(
                title = "⚡ 5-Step Morning Mobility Routine",
                dimension = DimensionType.FITNESS,
                pointsReward = 30,
                habitType = HabitType.SUB_TASKS,
                subTasks = listOf(
                    SubTaskItem(title = "10 Cat-Cow pelvic tilts"),
                    SubTaskItem(title = "60s World's Greatest Stretch"),
                    SubTaskItem(title = "15 Deep bodyweight squats"),
                    SubTaskItem(title = "30s Hanging bar decompress"),
                    SubTaskItem(title = "5 Deep diaphragmatic breaths")
                ),
                isSmartSuggested = true
            )
            DimensionType.CAREER -> LifeTask(
                title = "🎯 3 Most Important Tasks (MITs)",
                dimension = DimensionType.CAREER,
                pointsReward = 35,
                habitType = HabitType.SUB_TASKS,
                subTasks = listOf(
                    SubTaskItem(title = "Lock in single highest ROI task"),
                    SubTaskItem(title = "90-minute focus block with phone in DND"),
                    SubTaskItem(title = "Zero Slack/Email during morning block")
                ),
                isSmartSuggested = true
            )
            DimensionType.WEALTH -> LifeTask(
                title = "💰 Zero Impulse Spend & Daily Audit",
                dimension = DimensionType.WEALTH,
                pointsReward = 25,
                habitType = HabitType.BOOLEAN,
                isSmartSuggested = true
            )
            DimensionType.RELATIONSHIPS -> LifeTask(
                title = "💌 1 Meaningful Gratitude Text",
                dimension = DimensionType.RELATIONSHIPS,
                pointsReward = 20,
                habitType = HabitType.BOOLEAN,
                isSmartSuggested = true
            )
            DimensionType.MENTAL_HEALTH -> LifeTask(
                title = "🫁 5 Physiological Sigh Micro-Resets",
                dimension = DimensionType.MENTAL_HEALTH,
                pointsReward = 20,
                habitType = HabitType.COUNTER,
                currentCount = 0,
                targetCount = 5,
                countUnit = "resets",
                isSmartSuggested = true
            )
            DimensionType.SOCIAL_LIFE -> LifeTask(
                title = "☕ Weekly Social Catch-up or Meetup",
                dimension = DimensionType.SOCIAL_LIFE,
                pointsReward = 25,
                habitType = HabitType.BOOLEAN,
                isSmartSuggested = true
            )
        }
    }
}
