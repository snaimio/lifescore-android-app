package com.lifescore.app.data.local.dao

import androidx.room.*
import com.lifescore.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScreenTimeDao {

    @Query("SELECT * FROM screen_time_entries WHERE userId = :userId AND date = :date LIMIT 1")
    fun getEntryForDate(userId: String, date: String): Flow<ScreenTimeEntry?>

    @Query("SELECT * FROM screen_time_entries WHERE userId = :userId ORDER BY date DESC LIMIT 7")
    fun getRecentEntries(userId: String): Flow<List<ScreenTimeEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateEntry(entry: ScreenTimeEntry)

    @Query("SELECT * FROM screen_time_goals WHERE userId = :userId LIMIT 1")
    fun getGoal(userId: String): Flow<ScreenTimeGoalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setGoal(goal: ScreenTimeGoalEntity)

    @Query("UPDATE screen_time_goals SET earnedBonusMinutes = earnedBonusMinutes + :bonusMinutes WHERE userId = :userId")
    suspend fun addBonusMinutes(userId: String, bonusMinutes: Int)

    @Query("SELECT * FROM screen_time_sessions WHERE userId = :userId ORDER BY startTime DESC LIMIT 20")
    fun getSessions(userId: String): Flow<List<ScreenTimeSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ScreenTimeSession): Long

    @Update
    suspend fun updateSession(session: ScreenTimeSession)

    @Query("SELECT * FROM screen_time_challenges WHERE userId = :userId AND isActive = 1")
    fun getActiveChallenges(userId: String): Flow<List<ScreenTimeChallenge>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: ScreenTimeChallenge)

    @Update
    suspend fun updateChallenge(challenge: ScreenTimeChallenge)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThoughtBreakLog(log: ThoughtBreakLog)

    @Query("SELECT * FROM thought_break_logs WHERE userId = :userId ORDER BY timestamp DESC LIMIT 20")
    fun getThoughtBreakLogs(userId: String): Flow<List<ThoughtBreakLog>>
}
