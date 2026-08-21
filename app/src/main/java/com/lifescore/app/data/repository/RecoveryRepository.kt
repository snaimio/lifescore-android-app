package com.lifescore.app.data.repository

import com.lifescore.app.core.engine.RecoveryEngine
import com.lifescore.app.data.local.dao.RecoveryDao
import com.lifescore.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

interface RecoveryRepository {
    fun getActiveRecovery(userId: String, addictionType: AddictionType): Flow<RecoveryEntry?>
    fun getAllActiveRecoveries(userId: String): Flow<List<RecoveryEntry>>
    suspend fun saveRecoveryEntry(entry: RecoveryEntry): Long
    suspend fun resetSobriety(userId: String, addictionType: AddictionType): RecoveryEntry
    suspend fun recordSlip(userId: String, addictionType: AddictionType, trigger: String, lesson: String, plan: String)

    fun getRecentCravings(userId: String): Flow<List<CravingLog>>
    fun getCravingsForAddiction(userId: String, addictionType: AddictionType): Flow<List<CravingLog>>
    fun getSurvivedCravingsCount(userId: String): Flow<Int>
    suspend fun logCraving(craving: CravingLog): Long

    fun getRelapseLogs(userId: String): Flow<List<RelapseLog>>
    suspend fun logRelapse(relapse: RelapseLog): Long

    fun getMilestones(userId: String, addictionType: AddictionType): Flow<List<RecoveryMilestone>>
    suspend fun seedInitialMilestonesIfEmpty(userId: String, addictionType: AddictionType)
    suspend fun unlockMilestone(milestone: RecoveryMilestone)

    fun getActiveNotes(userId: String): Flow<List<MotivationalNote>>
    suspend fun saveNote(note: MotivationalNote): Long
    suspend fun deleteNote(noteId: Long)

    fun getTodayPledge(userId: String, dateIso: String, addictionType: AddictionType): Flow<RecoveryPledge?>
    suspend fun savePledge(pledge: RecoveryPledge): Long

    fun getSavingsGoals(userId: String): Flow<List<RecoverySavingsGoal>>
    suspend fun saveSavingsGoal(goal: RecoverySavingsGoal): Long
    suspend fun deleteSavingsGoal(goal: RecoverySavingsGoal)
}

class RecoveryRepositoryImpl(
    private val recoveryDao: RecoveryDao,
    private val lifeScoreRepository: LifeScoreRepository,
    private val engine: RecoveryEngine = RecoveryEngine()
) : RecoveryRepository {

    private suspend fun awardXp(amount: Int) {
        try {
            val user = lifeScoreRepository.getUserProfile().firstOrNull()
            if (user != null) {
                lifeScoreRepository.updateUserProfile(user.copy(currentXp = user.currentXp + amount))
            }
        } catch (_: Exception) {}
    }

    override fun getActiveRecovery(userId: String, addictionType: AddictionType): Flow<RecoveryEntry?> {
        return recoveryDao.getActiveRecovery(userId, addictionType)
    }

    override fun getAllActiveRecoveries(userId: String): Flow<List<RecoveryEntry>> {
        return recoveryDao.getAllActiveRecoveries(userId)
    }

    override suspend fun saveRecoveryEntry(entry: RecoveryEntry): Long {
        return recoveryDao.insertRecovery(entry)
    }

    override suspend fun resetSobriety(userId: String, addictionType: AddictionType): RecoveryEntry {
        val existing = recoveryDao.getActiveRecovery(userId, addictionType).firstOrNull()
        val longestStreak = existing?.let {
            maxOf(it.longestStreakDays, it.currentStreakDays)
        } ?: 0

        val newEntry = (existing ?: RecoveryEntry(userId = userId, addictionType = addictionType)).copy(
            sobrietyStartDate = System.currentTimeMillis(),
            currentStreakDays = 0,
            longestStreakDays = longestStreak,
            lastUpdated = System.currentTimeMillis(),
            isActive = true
        )
        val id = recoveryDao.insertRecovery(newEntry)
        return newEntry.copy(id = id)
    }

    override suspend fun recordSlip(
        userId: String,
        addictionType: AddictionType,
        trigger: String,
        lesson: String,
        plan: String
    ) {
        val existing = recoveryDao.getActiveRecovery(userId, addictionType).firstOrNull()
        if (existing != null) {
            val updated = existing.copy(
                totalSlipsCount = existing.totalSlipsCount + 1,
                lastUpdated = System.currentTimeMillis()
            )
            recoveryDao.updateRecovery(updated)
        }

        recoveryDao.insertRelapse(
            RelapseLog(
                userId = userId,
                addictionType = addictionType,
                relapseType = RelapseType.SLIP,
                trigger = trigger,
                lessonsLearned = lesson,
                actionPlan = plan,
                streakBeforeSetback = existing?.currentStreakDays ?: 0,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    override fun getRecentCravings(userId: String): Flow<List<CravingLog>> {
        return recoveryDao.getRecentCravings(userId)
    }

    override fun getCravingsForAddiction(userId: String, addictionType: AddictionType): Flow<List<CravingLog>> {
        return recoveryDao.getCravingsForAddiction(userId, addictionType)
    }

    override fun getSurvivedCravingsCount(userId: String): Flow<Int> {
        return recoveryDao.getSurvivedCravingsCount(userId)
    }

    override suspend fun logCraving(craving: CravingLog): Long {
        val id = recoveryDao.insertCraving(craving)
        if (craving.survived) {
            awardXp(25)
        }
        return id
    }

    override fun getRelapseLogs(userId: String): Flow<List<RelapseLog>> {
        return recoveryDao.getRelapseLogs(userId)
    }

    override suspend fun logRelapse(relapse: RelapseLog): Long {
        if (relapse.relapseType == RelapseType.RELAPSE) {
            resetSobriety(relapse.userId, relapse.addictionType)
        }
        return recoveryDao.insertRelapse(relapse)
    }

    override fun getMilestones(userId: String, addictionType: AddictionType): Flow<List<RecoveryMilestone>> {
        return recoveryDao.getMilestones(userId, addictionType)
    }

    override suspend fun seedInitialMilestonesIfEmpty(userId: String, addictionType: AddictionType) {
        val existing = recoveryDao.getMilestones(userId, addictionType).firstOrNull()
        if (existing.isNullOrEmpty()) {
            val defaults = engine.getHealthMilestones(addictionType).map {
                it.copy(userId = userId, addictionType = addictionType)
            }
            recoveryDao.insertMilestones(defaults)
        }
    }

    override suspend fun unlockMilestone(milestone: RecoveryMilestone) {
        val updated = milestone.copy(
            isUnlocked = true,
            unlockedAt = System.currentTimeMillis()
        )
        recoveryDao.updateMilestone(updated)
        awardXp(100)
    }

    override fun getActiveNotes(userId: String): Flow<List<MotivationalNote>> {
        return recoveryDao.getActiveNotes(userId)
    }

    override suspend fun saveNote(note: MotivationalNote): Long {
        return recoveryDao.insertNote(note)
    }

    override suspend fun deleteNote(noteId: Long) {
        recoveryDao.deleteNote(noteId)
    }

    override fun getTodayPledge(userId: String, dateIso: String, addictionType: AddictionType): Flow<RecoveryPledge?> {
        return recoveryDao.getTodayPledge(userId, dateIso, addictionType)
    }

    override suspend fun savePledge(pledge: RecoveryPledge): Long {
        val id = recoveryDao.insertPledge(pledge)
        awardXp(50)
        return id
    }

    override fun getSavingsGoals(userId: String): Flow<List<RecoverySavingsGoal>> {
        return recoveryDao.getSavingsGoals(userId)
    }

    override suspend fun saveSavingsGoal(goal: RecoverySavingsGoal): Long {
        return recoveryDao.insertSavingsGoal(goal)
    }

    override suspend fun deleteSavingsGoal(goal: RecoverySavingsGoal) {
        recoveryDao.deleteSavingsGoal(goal)
    }
}
