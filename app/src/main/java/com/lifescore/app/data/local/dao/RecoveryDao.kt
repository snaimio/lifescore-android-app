package com.lifescore.app.data.local.dao

import androidx.room.*
import com.lifescore.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecoveryDao {

    // 1. Recovery Profile Entries
    @Query("SELECT * FROM recovery_entries WHERE userId = :userId AND addictionType = :addictionType AND isActive = 1 LIMIT 1")
    fun getActiveRecovery(userId: String, addictionType: AddictionType): Flow<RecoveryEntry?>

    @Query("SELECT * FROM recovery_entries WHERE userId = :userId AND isActive = 1")
    fun getAllActiveRecoveries(userId: String): Flow<List<RecoveryEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecovery(entry: RecoveryEntry): Long

    @Update
    suspend fun updateRecovery(entry: RecoveryEntry)

    @Query("DELETE FROM recovery_entries WHERE id = :id")
    suspend fun deleteRecovery(id: Long)

    // 2. Craving Logs
    @Query("SELECT * FROM craving_logs WHERE userId = :userId ORDER BY timestamp DESC LIMIT 50")
    fun getRecentCravings(userId: String): Flow<List<CravingLog>>

    @Query("SELECT * FROM craving_logs WHERE userId = :userId AND addictionType = :addictionType ORDER BY timestamp DESC")
    fun getCravingsForAddiction(userId: String, addictionType: AddictionType): Flow<List<CravingLog>>

    @Query("SELECT COUNT(*) FROM craving_logs WHERE userId = :userId AND survived = 1")
    fun getSurvivedCravingsCount(userId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCraving(log: CravingLog): Long

    @Delete
    suspend fun deleteCraving(log: CravingLog)

    // 3. Relapse Logs
    @Query("SELECT * FROM relapse_logs WHERE userId = :userId ORDER BY timestamp DESC")
    fun getRelapseLogs(userId: String): Flow<List<RelapseLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelapse(log: RelapseLog): Long

    // 4. Milestones
    @Query("SELECT * FROM recovery_milestones WHERE userId = :userId AND addictionType = :addictionType ORDER BY milestoneDays ASC")
    fun getMilestones(userId: String, addictionType: AddictionType): Flow<List<RecoveryMilestone>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(milestones: List<RecoveryMilestone>)

    @Update
    suspend fun updateMilestone(milestone: RecoveryMilestone)

    // 5. Motivational Notes & Reasons
    @Query("SELECT * FROM motivational_notes WHERE userId = :userId AND isActive = 1 ORDER BY createdAt DESC")
    fun getActiveNotes(userId: String): Flow<List<MotivationalNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: MotivationalNote): Long

    @Query("UPDATE motivational_notes SET isActive = 0 WHERE id = :noteId")
    suspend fun deleteNote(noteId: Long)

    // 6. Daily Recovery Pledges
    @Query("SELECT * FROM recovery_pledges WHERE userId = :userId AND dateIso = :dateIso AND addictionType = :addictionType LIMIT 1")
    fun getTodayPledge(userId: String, dateIso: String, addictionType: AddictionType): Flow<RecoveryPledge?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPledge(pledge: RecoveryPledge): Long

    @Update
    suspend fun updatePledge(pledge: RecoveryPledge)

    // 7. Savings Reward Goals
    @Query("SELECT * FROM recovery_savings_goals WHERE userId = :userId ORDER BY targetAmount ASC")
    fun getSavingsGoals(userId: String): Flow<List<RecoverySavingsGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsGoal(goal: RecoverySavingsGoal): Long

    @Update
    suspend fun updateSavingsGoal(goal: RecoverySavingsGoal)

    @Delete
    suspend fun deleteSavingsGoal(goal: RecoverySavingsGoal)
}
