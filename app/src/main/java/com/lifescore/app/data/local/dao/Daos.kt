package com.lifescore.app.data.local.dao

import androidx.room.*
import com.lifescore.app.data.local.entity.*
import com.lifescore.app.domain.model.DimensionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, id DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE dimension = :dimension ORDER BY isCompleted ASC, id DESC")
    fun getTasksByDimension(dimension: DimensionType): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("UPDATE tasks SET isCompleted = :isCompleted, completedAt = :completedAt WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: Long, isCompleted: Boolean, completedAt: Long?)
}

@Dao
interface DailyScoreDao {
    @Query("SELECT * FROM daily_scores ORDER BY dateIso DESC LIMIT 30")
    fun getRecentScores(): Flow<List<DailyScoreEntity>>

    @Query("SELECT * FROM daily_scores WHERE dateIso = :dateIso LIMIT 1")
    suspend fun getScoreByDate(dateIso: String): DailyScoreEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateScore(score: DailyScoreEntity)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserEntity)

    @Query("UPDATE user_profile SET currentXp = currentXp + :xpDelta WHERE id = 1")
    suspend fun addXp(xpDelta: Int)

    @Query("UPDATE user_profile SET isPremium = :isPremium WHERE id = 1")
    suspend fun updatePremiumStatus(isPremium: Boolean)
}

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM challenges")
    fun getAllChallenges(): Flow<List<ChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<ChallengeEntity>)

    @Update
    suspend fun updateChallenge(challenge: ChallengeEntity)
}
