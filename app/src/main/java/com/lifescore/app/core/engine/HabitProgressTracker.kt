package com.lifescore.app.core.engine

import com.lifescore.app.data.HabitData
import com.lifescore.app.domain.model.DimensionType

data class DimensionMastery(
    val dimension: DimensionType,
    val totalHabits: Int,
    val completedHabitsCount: Int,
    val masteryPercentage: Float,
    val unlockedAdvancedHabits: Boolean
)

object HabitProgressTracker {

    fun calculateMastery(completedHabitIds: Set<Int>): Map<DimensionType, DimensionMastery> {
        return DimensionType.values().associateWith { dimension ->
            val dimensionHabits = HabitData.getHabitsByDimension(dimension)
            val completedCount = dimensionHabits.count { it.id in completedHabitIds }
            val percentage = if (dimensionHabits.isNotEmpty()) {
                (completedCount.toFloat() / dimensionHabits.size.toFloat()).coerceIn(0f, 1f)
            } else 0f

            DimensionMastery(
                dimension = dimension,
                totalHabits = dimensionHabits.size,
                completedHabitsCount = completedCount,
                masteryPercentage = percentage,
                unlockedAdvancedHabits = percentage >= 0.5f
            )
        }
    }
}
