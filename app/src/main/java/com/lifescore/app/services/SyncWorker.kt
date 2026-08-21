package com.lifescore.app.services

import android.content.Context
import androidx.work.*
import com.lifescore.app.LifeScoreApp
import java.util.concurrent.TimeUnit

class SyncWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val app = context.applicationContext as? LifeScoreApp ?: return Result.failure()
        val syncService = DataSyncService(
            db = app.database,
            firebaseRepository = app.firebaseRepository,
            authRepository = app.authRepository
        )

        val result = syncService.syncAll()
        return if (result.isSuccess) Result.success() else Result.retry()
    }

    companion object {
        private const val SYNC_WORK_NAME = "lifescore_periodic_sync"
        private const val PREFS_NAME = "lifescore_sync_prefs"
        const val PREF_SYNC_ONLY_CHARGING = "sync_only_when_charging"

        fun schedulePeriodicSync(context: Context, onlyWhenCharging: Boolean = false) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val requireCharging = if (onlyWhenCharging) true else prefs.getBoolean(PREF_SYNC_ONLY_CHARGING, false)

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .setRequiresCharging(requireCharging)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(6, TimeUnit.HOURS)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                syncRequest
            )
        }

        fun updateChargingConstraint(context: Context, onlyWhenCharging: Boolean) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(PREF_SYNC_ONLY_CHARGING, onlyWhenCharging).apply()
            schedulePeriodicSync(context, onlyWhenCharging)
        }
    }
}
