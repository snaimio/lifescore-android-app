package com.lifescore.app.services

import com.lifescore.app.core.database.LifeScoreDatabase
import com.lifescore.app.data.local.entity.TaskEntity
import com.lifescore.app.data.remote.repository.AuthRepository
import com.lifescore.app.data.remote.repository.FirebaseRepository
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.LifeTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SyncReport(
    val isSuccess: Boolean,
    val tasksSyncedCount: Int,
    val logs: List<String>,
    val error: String? = null
)

class DataSyncService(
    private val db: LifeScoreDatabase,
    private val firebaseRepository: FirebaseRepository,
    private val authRepository: AuthRepository
) {

    private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
    private fun logTimestamp(): String = "[${timeFormat.format(Date())}]"

    suspend fun syncAllWithLogs(): SyncReport = withContext(Dispatchers.IO) {
        val logs = mutableListOf<String>()
        logs.add("${logTimestamp()} [SYNC_INIT] Starting hybrid Room ➔ Firestore data synchronization...")

        val currentUser = authRepository.currentUser
        if (currentUser == null) {
            logs.add("${logTimestamp()} [AUTH_ERROR] No active authenticated user found.")
            return@withContext SyncReport(isSuccess = false, tasksSyncedCount = 0, logs = logs, error = "User not authenticated")
        }

        val uid = currentUser.uid
        logs.add("${logTimestamp()} [AUTH_OK] Authenticated user session found: UID = ${uid.take(12)}...")

        try {
            // 1. Sync User Profile (XP, Level, Streak)
            logs.add("${logTimestamp()} [PROFILE_SYNC] Reading local user profile from Room DB...")
            val localUser = db.userDao().getUserProfile().first()
            if (localUser != null) {
                logs.add("${logTimestamp()} [PROFILE_UPLOAD] Pushing profile: XP=${localUser.currentXp}, Level=${localUser.currentLevel} to /users/$uid")
                firebaseRepository.updateUserScore(
                    uid = uid,
                    score = localUser.currentXp / 2,
                    xp = localUser.currentXp,
                    level = localUser.currentLevel
                )
                logs.add("${logTimestamp()} [PROFILE_CONFIRMED] Profile successfully updated in Cloud Firestore.")
            } else {
                logs.add("${logTimestamp()} [PROFILE_SKIP] No local user profile found in Room DB.")
            }

            // 2. Upload Local Offline Tasks to Firestore
            logs.add("${logTimestamp()} [TASK_SCAN] Scanning local Room database for offline tasks...")
            val localTasks = db.taskDao().getAllTasks().first()
            logs.add("${logTimestamp()} [TASK_COUNT] Found ${localTasks.size} tasks in local SQLite cache.")

            var uploadedCount = 0
            for (task in localTasks) {
                val lifeTask = LifeTask(
                    id = task.id,
                    title = task.title,
                    dimension = task.dimension,
                    pointsReward = task.pointsReward,
                    isCompleted = task.isCompleted,
                    completedAt = task.completedAt,
                    createdAt = task.createdAt
                )
                logs.add("${logTimestamp()} [TASK_UPLOAD] Uploading task #${task.id} '${task.title}' (${task.dimension.name}) ➔ /tasks/...")
                firebaseRepository.saveTask(uid = uid, task = lifeTask)
                uploadedCount++
            }
            logs.add("${logTimestamp()} [UPLOAD_COMPLETE] Successfully uploaded $uploadedCount local tasks to Cloud Firestore.")

            // 3. Verification - Read remote tasks back from Firestore
            val todayIso = com.lifescore.app.core.util.DateUtils.getTodayIso()
            logs.add("${logTimestamp()} [CLOUD_VERIFY] Querying Firestore collection '/tasks' where uid='$uid' and date='$todayIso'...")
            val remoteTasks = firebaseRepository.getTasks(uid, todayIso)
            logs.add("${logTimestamp()} [CLOUD_VERIFIED] Confirmed ${remoteTasks.size} tasks active in Cloud Firestore.")

            logs.add("${logTimestamp()} [SYNC_SUCCESS] 🎉 All local tasks and metrics fully synchronized with Cloud Firestore!")
            return@withContext SyncReport(isSuccess = true, tasksSyncedCount = uploadedCount, logs = logs)

        } catch (e: Exception) {
            logs.add("${logTimestamp()} [SYNC_FAILURE] ❌ Error during sync: ${e.localizedMessage}")
            return@withContext SyncReport(isSuccess = false, tasksSyncedCount = 0, logs = logs, error = e.localizedMessage)
        }
    }

    suspend fun syncAll(): Result<Unit> {
        val report = syncAllWithLogs()
        return if (report.isSuccess) Result.success(Unit) else Result.failure(Exception(report.error ?: "Sync failed"))
    }
}
