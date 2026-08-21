package com.lifescore.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hydration_entries")
data class HydrationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "user_default",
    val timestamp: Long = System.currentTimeMillis(),
    val volumeMl: Int, // Amount in milliliters (e.g. 250, 500)
    val source: String = "manual" // manual, quick_add, health_connect
)

@Entity(tableName = "hydration_goals")
data class HydrationGoalEntity(
    @PrimaryKey val userId: String = "user_default",
    val dailyGoalMl: Int = 2500, // Default: 2.5L
    val weightKg: Float? = null,
    val activityLevel: String = "moderate", // sedentary, moderate, active
    val updatedAt: Long = System.currentTimeMillis()
)
