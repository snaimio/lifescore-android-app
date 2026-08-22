package com.lifescore.app.data.local.dao

import androidx.room.*
import com.lifescore.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookSummaryDao {
    @Query("SELECT * FROM book_summary_progress WHERE userId = :userId")
    fun getAllProgress(userId: String): Flow<List<BookSummaryProgressEntity>>

    @Query("SELECT * FROM book_summary_progress WHERE bookId = :bookId AND userId = :userId LIMIT 1")
    fun getProgressForBook(bookId: String, userId: String): Flow<BookSummaryProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: BookSummaryProgressEntity)

    @Query("SELECT COUNT(*) FROM book_summary_progress WHERE isCompleted = 1 AND userId = :userId")
    fun getCompletedBooksCount(userId: String): Flow<Int>
}

@Dao
interface DailyGrowthDao {
    @Query("SELECT * FROM daily_growth_progress WHERE userId = :userId")
    fun getAllProgress(userId: String): Flow<List<DailyGrowthProgressEntity>>

    @Query("SELECT * FROM daily_growth_progress WHERE sessionId = :sessionId AND userId = :userId LIMIT 1")
    fun getProgressForSession(sessionId: Int, userId: String): Flow<DailyGrowthProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: DailyGrowthProgressEntity)

    @Query("SELECT COUNT(*) FROM daily_growth_progress WHERE isCompleted = 1 AND userId = :userId")
    fun getCompletedSessionsCount(userId: String): Flow<Int>
}

@Dao
interface FocusDao {
    @Query("SELECT * FROM focus_sessions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllFocusSessions(userId: String): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions WHERE userId = :userId AND wasSuccessful = 1 ORDER BY timestamp DESC")
    fun getSuccessfulFocusSessions(userId: String): Flow<List<FocusSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSessionEntity): Long

    @Query("SELECT SUM(durationMinutes) FROM focus_sessions WHERE userId = :userId AND wasSuccessful = 1")
    fun getTotalFocusMinutes(userId: String): Flow<Int?>

    @Query("SELECT COUNT(*) FROM focus_sessions WHERE userId = :userId AND wasSuccessful = 1")
    fun getTotalTreesPlanted(userId: String): Flow<Int>
}

@Dao
interface MoodDao {
    @Query("SELECT * FROM mood_logs WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllMoodLogs(userId: String): Flow<List<MoodLogEntity>>

    @Query("SELECT * FROM mood_logs WHERE dateIso = :dateIso AND userId = :userId ORDER BY timestamp DESC")
    fun getMoodLogsForDate(dateIso: String, userId: String): Flow<List<MoodLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodLog(log: MoodLogEntity): Long

    @Query("DELETE FROM mood_logs WHERE id = :id")
    suspend fun deleteMoodLog(id: Long)
}
