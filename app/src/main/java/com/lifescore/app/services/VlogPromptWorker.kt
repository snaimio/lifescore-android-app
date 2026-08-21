package com.lifescore.app.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.lifescore.app.MainActivity
import com.lifescore.app.utils.Constants
import java.util.concurrent.TimeUnit

class VlogPromptWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        // 1. User Opt-Out Check
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isEnabled = prefs.getBoolean(PREF_VLOG_PROMPTS_ENABLED, true)
        if (!isEnabled) {
            return Result.success()
        }

        // 2. Do Not Disturb (DND) Check
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val filter = notificationManager.currentInterruptionFilter
            if (filter == NotificationManager.INTERRUPTION_FILTER_NONE ||
                filter == NotificationManager.INTERRUPTION_FILTER_ALARMS
            ) {
                // Respect DND mode: skip displaying prompt during quiet hours
                return Result.success()
            }
        }

        showVlogPromptNotification(notificationManager)
        return Result.success()
    }

    private fun showVlogPromptNotification(notificationManager: NotificationManager) {
        ensureChannelExists(notificationManager)

        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("OPEN_MICRO_VLOGS", true)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            2001,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Notification Action: Snooze (1h)
        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_SNOOZE_VLOG_PROMPT
            putExtra("NOTIFICATION_ID", 2001)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            2002,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val prompts = listOf(
            "🎬 2-Second Habit Snap: Capture what you are building right now!",
            "⚡ Snap your momentum! Record 2 seconds for today's 60s montage.",
            "📸 Quick 2s check-in! Share your progress with your Log Group.",
            "🔥 Log your habit proof! 2 seconds to keep your group inspired."
        )
        val randomPrompt = prompts.random()

        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_ID_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setContentTitle("🎬 Time for Your 2s Vlog Snap!")
            .setContentText(randomPrompt)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentPendingIntent)
            .addAction(
                android.R.drawable.ic_lock_idle_alarm,
                "⏰ Snooze (1h)",
                snoozePendingIntent
            )
            .setAutoCancel(true)
            .build()

        notificationManager.notify(2001, notification)
    }

    private fun ensureChannelExists(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID_REMINDERS,
                "Habit & Vlog Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Smart reminders for micro-habits and 2-second vlog snaps"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val WORK_VLOG_PROMPT = "work_vlog_prompt_periodic"
        const val PREFS_NAME = "lifescore_vlog_prefs"
        const val PREF_VLOG_PROMPTS_ENABLED = "vlog_prompts_enabled"

        fun setVlogPromptsEnabled(context: Context, enabled: Boolean) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(PREF_VLOG_PROMPTS_ENABLED, enabled).apply()
            if (enabled) {
                schedulePeriodicPrompts(context)
            } else {
                WorkManager.getInstance(context).cancelUniqueWork(WORK_VLOG_PROMPT)
            }
        }

        fun schedulePeriodicPrompts(
            context: Context,
            intervalHours: Long = 3,
            enabled: Boolean = true
        ) {
            if (!enabled) return

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<VlogPromptWorker>(intervalHours, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_VLOG_PROMPT,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }
    }
}
