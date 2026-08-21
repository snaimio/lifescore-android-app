package com.lifescore.app.data.local.dao

import androidx.room.*
import com.lifescore.app.data.local.entity.HydrationEntity
import com.lifescore.app.data.local.entity.HydrationGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HydrationDao {

    @Query("SELECT * FROM hydration_entries WHERE userId = :userId ORDER BY timestamp DESC")
    fun getHydrationEntries(userId: String): Flow<List<HydrationEntity>>

    @Query("SELECT * FROM hydration_entries WHERE userId = :userId AND date(timestamp / 1000, 'unixepoch') = date('now', 'localtime') ORDER BY timestamp DESC")
    fun getTodayEntries(userId: String): Flow<List<HydrationEntity>>

    @Query("SELECT SUM(volumeMl) FROM hydration_entries WHERE userId = :userId AND date(timestamp / 1000, 'unixepoch') = date('now', 'localtime')")
    fun getTodayTotal(userId: String): Flow<Int?>

    @Query("SELECT * FROM hydration_entries WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastEntry(userId: String): HydrationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHydrationEntry(entry: HydrationEntity)

    @Delete
    suspend fun deleteHydrationEntry(entry: HydrationEntity)

    @Query("DELETE FROM hydration_entries WHERE userId = :userId AND timestamp < :beforeTimestamp")
    suspend fun deleteOldEntries(userId: String, beforeTimestamp: Long)

    @Query("SELECT * FROM hydration_goals WHERE userId = :userId LIMIT 1")
    suspend fun getHydrationGoal(userId: String): HydrationGoalEntity?

    @Query("SELECT * FROM hydration_goals WHERE userId = :userId LIMIT 1")
    fun observeHydrationGoal(userId: String): Flow<HydrationGoalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveHydrationGoal(goal: HydrationGoalEntity)

    @Query("UPDATE hydration_goals SET dailyGoalMl = :goalMl, updatedAt = :updatedAt WHERE userId = :userId")
    suspend fun updateDailyGoal(userId: String, goalMl: Int, updatedAt: Long = System.currentTimeMillis())
}
