package com.lifescore.app.core.util

import android.content.Context
import android.content.SharedPreferences

class GettingStartedManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("lifescore_getting_started", Context.MODE_PRIVATE)

    companion object {
        const val STEP_FIRST_HABIT = "step_first_habit"
        const val STEP_LIFE_MATRIX = "step_life_matrix"
        const val STEP_AI_COACH = "step_ai_coach"
        const val TOTAL_STEPS = 3
    }

    fun isStepCompleted(stepKey: String): Boolean {
        return prefs.getBoolean(stepKey, false)
    }

    fun markStepCompleted(stepKey: String) {
        prefs.edit().putBoolean(stepKey, true).apply()
    }

    fun getCompletedStepCount(): Int {
        var count = 0
        if (isStepCompleted(STEP_FIRST_HABIT)) count++
        if (isStepCompleted(STEP_LIFE_MATRIX)) count++
        if (isStepCompleted(STEP_AI_COACH)) count++
        return count
    }

    fun isAllCompleted(): Boolean = getCompletedStepCount() >= TOTAL_STEPS

    fun reset() {
        prefs.edit().clear().apply()
    }
}
