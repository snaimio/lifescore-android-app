package com.lifescore.app.services

import com.lifescore.app.core.database.LifeScoreDatabase
import com.lifescore.app.data.local.entity.TaskEntity
import com.lifescore.app.data.remote.repository.AuthRepository
import com.lifescore.app.data.remote.repository.FirebaseRepository
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.LifeTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SyncProgress(
    val totalSteps: Int,
    val currentStep: Int,
    val progressPercentage: Float,
    val currentStage: String
)

data class SyncReport(
    val isSuccess: Boolean,
    val tasksSyncedCount: Int,
    val conflictsResolvedCount: Int = 0,
    val logs: List<String>,
    val error: String? = null
)

class DataSyncService(
    private val db: LifeScoreDatabase,
    private val firebaseRepository: FirebaseRepository,
    private val authRepository: AuthRepository
) {

    private val _syncProgress = MutableStateFlow<SyncProgress?>(null)
    val syncProgress: StateFlow<SyncProgress?> = _syncProgress.asStateFlow()

    private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
    private fun logTimestamp(): String = "[${timeFormat.format(Date())}]"

    /**
     * Conflict resolution helper: server wins on equal timestamp; otherwise latest update wins.
     */
    fun resolveConflict(local: TaskEntity, remote: TaskEntity): TaskEntity {
        val localTime = local.completedAt ?: local.createdAt
        val remoteTime = remote.completedAt ?: remote.createdAt
        return if (remoteTime >= localTime) remote else local
    }

    fun resolveConflict(local: LifeTask, remote: LifeTask): LifeTask {
        val localTime = local.completedAt ?: local.createdAt
        val remoteTime = remote.completedAt ?: remote.createdAt
        return if (remoteTime >= localTime) remote else local
    }

    /**
     * Executes a block with exponential backoff retry for transient network/Firestore failures.
     */
    private suspend fun <T> retryWithBackoff(
        maxRetries: Int = 3,
        initialDelayMs: Long = 500L,
        factor: Double = 2.0,
        operationName: String = "Operation",
        logs: MutableList<String>? = null,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelayMs
        var lastException: Exception? = null

        repeat(maxRetries) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                lastException = e
                val nextAttempt = attempt + 1
                logs?.add("${logTimestamp()} [RETRY] $operationName failed (attempt $nextAttempt/$maxRetries): ${e.localizedMessage}. Retrying in ${currentDelay}ms...")
                if (nextAttempt < maxRetries) {
                    delay(currentDelay)
                    currentDelay = (currentDelay * factor).toLong()
                }
            }
        }
        throw lastException ?: Exception("$operationName failed after $maxRetries retries")
    }

    suspend fun syncAllWithLogs(onProgress: ((SyncProgress) -> Unit)? = null): SyncReport = withContext(Dispatchers.IO) {
        val logs = mutableListOf<String>()
        logs.add("${logTimestamp()} [SYNC_INIT] Starting hybrid Room ➔ Firestore data synchronization...")

        fun updateProgress(current: Int, total: Int, stage: String) {
            val percentage = if (total > 0) (current.toFloat() / total.toFloat()).coerceIn(0f, 1f) else 0f
            val progress = SyncProgress(totalSteps = total, currentStep = current, progressPercentage = percentage, currentStage = stage)
            _syncProgress.value = progress
            onProgress?.invoke(progress)
        }

        updateProgress(0, 4, "Authenticating user session...")

        val currentUser = authRepository.currentUser
        if (currentUser == null) {
            logs.add("${logTimestamp()} [AUTH_ERROR] No active authenticated user found.")
            _syncProgress.value = null
            return@withContext SyncReport(isSuccess = false, tasksSyncedCount = 0, logs = logs, error = "User not authenticated")
        }

        val uid = currentUser.uid
        logs.add("${logTimestamp()} [AUTH_OK] Authenticated user session found: UID = ${uid.take(12)}...")

        var uploadedCount = 0
        var conflictsResolved = 0

        try {
            // ──────────────────────────────────────────────
            // 1. Sync User Profile (XP, Level, Streak)
            // ──────────────────────────────────────────────
            updateProgress(1, 4, "Synchronizing user profile...")
            logs.add("${logTimestamp()} [PROFILE_SYNC] Reading local user profile from Room DB...")
            val localUser = db.userDao().getUserProfile().first()
            if (localUser != null) {
                retryWithBackoff(operationName = "Profile Sync", logs = logs) {
                    val remoteUser = firebaseRepository.getUser(uid)
                    if (remoteUser != null) {
                        // Conflict Resolution: If server has higher XP or different level, reconcile
                        if (remoteUser.currentXp > localUser.currentXp) {
                            logs.add("${logTimestamp()} [PROFILE_CONFLICT] Remote server XP (${remoteUser.currentXp}) > local XP (${localUser.currentXp}). Reconciling local DB.")
                            db.userDao().insertOrUpdateUser(
                                localUser.copy(
                                    currentXp = remoteUser.currentXp,
                                    currentLevel = remoteUser.currentLevel,
                                    currentStreakDays = remoteUser.currentStreakDays
                                )
                            )
                            conflictsResolved++
                        } else {
                            logs.add("${logTimestamp()} [PROFILE_UPLOAD] Pushing profile: XP=${localUser.currentXp}, Level=${localUser.currentLevel} to /users/$uid")
                            firebaseRepository.updateUserScore(
                                uid = uid,
                                score = localUser.currentXp / 2,
                                xp = localUser.currentXp,
                                level = localUser.currentLevel
                            )
                        }
                    } else {
                        firebaseRepository.updateUserScore(
                            uid = uid,
                            score = localUser.currentXp / 2,
                            xp = localUser.currentXp,
                            level = localUser.currentLevel
                        )
                    }
                }
                logs.add("${logTimestamp()} [PROFILE_CONFIRMED] Profile successfully synchronized.")
            } else {
                logs.add("${logTimestamp()} [PROFILE_SKIP] No local user profile found in Room DB.")
            }

            // ──────────────────────────────────────────────
            // 2. Fetch Remote Tasks for Conflict Resolution
            // ──────────────────────────────────────────────
            updateProgress(2, 4, "Fetching remote tasks & checking conflicts...")
            val todayIso = com.lifescore.app.core.util.DateUtils.getTodayIso()
            val remoteTasks = retryWithBackoff(operationName = "Fetch Remote Tasks", logs = logs) {
                firebaseRepository.getTasks(uid, todayIso)
            }
            val remoteTaskMap = remoteTasks.associateBy { it.id }

            // ──────────────────────────────────────────────
            // 3. Scan & Sync Local Tasks with Timestamp Check
            // ──────────────────────────────────────────────
            updateProgress(3, 4, "Resolving task conflicts & pushing updates...")
            logs.add("${logTimestamp()} [TASK_SCAN] Scanning local Room database for offline tasks...")
            val localTasks = db.taskDao().getAllTasks().first()
            logs.add("${logTimestamp()} [TASK_COUNT] Found ${localTasks.size} tasks in local SQLite cache.")

            for (task in localTasks) {
                val remoteMatch = remoteTaskMap[task.id]

                if (remoteMatch != null) {
                    // Conflict Resolution: Check timestamps
                    val localCompletedAt = task.completedAt ?: 0L
                    val remoteCompletedAt = remoteMatch.completedAt ?: 0L

                    if (remoteMatch.isCompleted != task.isCompleted) {
                        if (remoteCompletedAt >= localCompletedAt) {
                            // Server wins if remote timestamp is newer or equal
                            logs.add("${logTimestamp()} [CONFLICT_SERVER_WIN] Task #${task.id} '${task.title}': Remote state (isCompleted=${remoteMatch.isCompleted}) is newer. Updating local Room DB.")
                            db.taskDao().updateTaskStatus(
                                taskId = task.id,
                                isCompleted = remoteMatch.isCompleted,
                                completedAt = remoteMatch.completedAt
                            )
                            conflictsResolved++
                        } else {
                            // Local wins if local completion timestamp is strictly newer
                            logs.add("${logTimestamp()} [CONFLICT_LOCAL_WIN] Task #${task.id} '${task.title}': Local state (isCompleted=${task.isCompleted}) is newer. Uploading to Firestore.")
                            retryWithBackoff(operationName = "Upload Task #${task.id}", logs = logs) {
                                firebaseRepository.saveTask(
                                    uid = uid,
                                    task = LifeTask(
                                        id = task.id,
                                        title = task.title,
                                        dimension = task.dimension,
                                        pointsReward = task.pointsReward,
                                        isCompleted = task.isCompleted,
                                        completedAt = task.completedAt,
                                        createdAt = task.createdAt
                                    )
                                )
                            }
                            uploadedCount++
                        }
                    }
                } else {
                    // Local only - Upload to Firestore
                    logs.add("${logTimestamp()} [TASK_UPLOAD] Uploading new task #${task.id} '${task.title}' (${task.dimension.name}) ➔ /tasks/...")
                    retryWithBackoff(operationName = "Upload New Task #${task.id}", logs = logs) {
                        firebaseRepository.saveTask(
                            uid = uid,
                            task = LifeTask(
                                id = task.id,
                                title = task.title,
                                dimension = task.dimension,
                                pointsReward = task.pointsReward,
                                isCompleted = task.isCompleted,
                                completedAt = task.completedAt,
                                createdAt = task.createdAt
                            )
                        )
                    }
                    uploadedCount++
                }
            }

            // ──────────────────────────────────────────────
            // 4. Verify & Finalize
            // ──────────────────────────────────────────────
            updateProgress(4, 4, "Sync completed successfully!")
            logs.add("${logTimestamp()} [UPLOAD_COMPLETE] Successfully uploaded $uploadedCount local tasks ($conflictsResolved conflicts resolved).")
            logs.add("${logTimestamp()} [SYNC_SUCCESS] 🎉 Hybrid offline-online synchronization completed successfully!")

            _syncProgress.value = null
            return@withContext SyncReport(
                isSuccess = true,
                tasksSyncedCount = uploadedCount,
                conflictsResolvedCount = conflictsResolved,
                logs = logs
            )

        } catch (e: Exception) {
            logs.add("${logTimestamp()} [SYNC_FAILURE] ❌ Error during sync: ${e.localizedMessage}")
            _syncProgress.value = null
            return@withContext SyncReport(
                isSuccess = false,
                tasksSyncedCount = uploadedCount,
                conflictsResolvedCount = conflictsResolved,
                logs = logs,
                error = e.localizedMessage
            )
        }
    }

    suspend fun syncAll(): Result<Unit> {
        val report = syncAllWithLogs()
        return if (report.isSuccess) Result.success(Unit) else Result.failure(Exception(report.error ?: "Sync failed"))
    }
}
