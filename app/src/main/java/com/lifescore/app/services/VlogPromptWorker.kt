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
        showVlogPromptNotification()
        return Result.success()
    }

    private fun showVlogPromptNotification() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ensureChannelExists(notificationManager)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("OPEN_MICRO_VLOGS", true)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            2001,
            intent,
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
            .setContentIntent(pendingIntent)
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

        fun schedulePeriodicPrompts(context: Context, intervalHours: Long = 2) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<VlogPromptWorker>(intervalHours, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_VLOG_PROMPT,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
