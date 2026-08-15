package com.lifescore.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lifescore.app.domain.model.DimensionType

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val dimension: DimensionType,
    val pointsReward: Int = 15,
    val isCompleted: Boolean = false,
    val streakDays: Int = 0,
    val recurringInterval: String = "DAILY",
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_scores")
data class DailyScoreEntity(
    @PrimaryKey val dateIso: String,
    val totalScore: Int,
    val healthScore: Int,
    val wealthScore: Int,
    val relationshipsScore: Int,
    val careerScore: Int,
    val learningScore: Int,
    val fitnessScore: Int,
    val mentalHealthScore: Int,
    val socialScore: Int,
    val completedTasksCount: Int
)

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val id: Long = 1,
    val name: String = "Achiever",
    val email: String? = null,
    val currentXp: Int = 0,
    val currentLevel: Int = 1,
    val currentStreakDays: Int = 0,
    val isPremium: Boolean = false,
    val title: String = "Novice Seeker",
    val lastActive: Long = System.currentTimeMillis(),
    val isLocal: Boolean = false
)

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val dimension: DimensionType,
    val durationDays: Int = 30,
    val currentDay: Int = 0,
    val xpReward: Int = 500,
    val isJoined: Boolean = false,
    val isCompleted: Boolean = false
)
