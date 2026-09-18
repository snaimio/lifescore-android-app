package com.lifescore.app.core.util

import android.content.Context
import android.content.SharedPreferences

class CoachMarkManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("lifescore_coach_marks", Context.MODE_PRIVATE)

    fun hasSeen(featureId: String): Boolean {
        return prefs.getBoolean("seen_$featureId", false)
    }

    fun markSeen(featureId: String) {
        prefs.edit().putBoolean("seen_$featureId", true).apply()
    }

    fun resetAll() {
        prefs.edit().clear().apply()
    }
}
