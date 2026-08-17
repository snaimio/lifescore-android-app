package com.lifescore.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.JournalMood
import com.lifescore.app.domain.model.QuestDifficulty

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

@Entity(tableName = "ai_quests")
data class AiQuestEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val dimension: DimensionType,
    val difficulty: QuestDifficulty = QuestDifficulty.C,
    val pointsReward: Int = 30,
    val statRewardPoints: Int = 2,
    val estimatedMinutes: Int = 15,
    val subObjectivesJson: String = "[]",
    val isAccepted: Boolean = false,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "character_stats")
data class CharacterStatsEntity(
    @PrimaryKey val id: Long = 1,
    val strength: Int = 10,
    val vitality: Int = 10,
    val agility: Int = 10,
    val intelligence: Int = 10,
    val perception: Int = 10,
    val availablePoints: Int = 5,
    val equippedTitle: String = "Novice Seeker",
    val titleBonusDescription: String = "+5% XP from all Quests"
)

@Entity(tableName = "group_habits")
data class GroupHabitEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val dimension: DimensionType,
    val memberCount: Int = 1,
    val streakDays: Int = 0,
    val todayCompletedCount: Int = 0,
    val targetDailyCompletions: Int = 5,
    val xpReward: Int = 100,
    val isJoined: Boolean = false,
    val creatorName: String = "Achiever",
    val isCompletedToday: Boolean = false
)

@Entity(tableName = "journal_entries")
data class JournalEntity(
    @PrimaryKey val id: String,
    val dateIso: String,
    val mood: JournalMood,
    val textContent: String,
    val dimensionTag: DimensionType,
    val aiReflection: String? = null,
    val audioDurationSeconds: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "combat_bosses")
data class BossEntity(
    @PrimaryKey val id: String,
    val name: String,
    val title: String,
    val dimension: DimensionType,
    val maxHp: Int,
    val currentHp: Int,
    val attackPower: Int,
    val avatarEmoji: String,
    val rewardXp: Int,
    val rewardStatPoints: Int,
    val isDefeated: Boolean = false
)
