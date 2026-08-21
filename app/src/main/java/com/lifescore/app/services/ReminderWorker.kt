package com.lifescore.app.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.lifescore.app.MainActivity
import com.lifescore.app.core.database.LifeScoreDatabase
import com.lifescore.app.utils.Constants
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar
import java.util.concurrent.TimeUnit

data class ReminderConfig(
    val morningHour: Int = 8,
    val eveningHour: Int = 20,
    val morningEnabled: Boolean = true,
    val eveningEnabled: Boolean = true
)

class ReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val config = getReminderConfig(context)
        val isMorning = inputData.getBoolean("IS_MORNING", true)

        if (isMorning && !config.morningEnabled) return Result.success()
        if (!isMorning && !config.eveningEnabled) return Result.success()

        // Fetch current streak from Room DB
        val streak = try {
            val db = LifeScoreDatabase.getInstance(context)
            val user = db.userDao().getUserProfile().firstOrNull()
            user?.currentStreakDays ?: 0
        } catch (e: Exception) {
            0
        }

        if (isMorning) {
            showMorningKickstartNotification(streak)
        } else {
            showEveningStreakNotification(streak)
        }
        return Result.success()
    }

    private fun showMorningKickstartNotification(streak: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ensureChannelExists(notificationManager)

        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("OPEN_TASKS", true)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            1001,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Notification Action: Complete Task
        val completeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_COMPLETE_TASK
            putExtra("NOTIFICATION_ID", 1001)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            1003,
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val streakText = if (streak > 0) " Keep your $streak-day streak active!" else ""

        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_ID_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("☀️ Morning Kickstart!")
            .setContentText("Complete your 3 morning micro-habits and start your daily momentum.$streakText")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentPendingIntent)
            .addAction(
                android.R.drawable.checkbox_on_background,
                "✓ Complete Quick Habit",
                completePendingIntent
            )
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }

    private fun showEveningStreakNotification(streak: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ensureChannelExists(notificationManager)

        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("OPEN_TASKS", true)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            1002,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Notification Action: Complete Task
        val completeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_COMPLETE_TASK
            putExtra("NOTIFICATION_ID", 1002)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            1004,
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (streak > 0) "🔥 $streak-Day Streak at Risk!" else "🌙 Don't Break The Chain!"
        val body = if (streak > 0) {
            "Protect your active $streak-day streak! Complete your pending habits before midnight."
        } else {
            "Build momentum! Complete your daily habits before midnight."
        }

        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_ID_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentPendingIntent)
            .addAction(
                android.R.drawable.checkbox_on_background,
                "✓ Complete Pending Task",
                completePendingIntent
            )
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1002, notification)
    }

    private fun ensureChannelExists(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID_REMINDERS,
                Constants.CHANNEL_NAME_REMINDERS,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Smart morning & evening reminders for habits and streak protection"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val PREFS_NAME = "lifescore_reminder_prefs"
        const val PREF_MORNING_HOUR = "morning_reminder_hour"
        const val PREF_EVENING_HOUR = "evening_reminder_hour"
        const val PREF_MORNING_ENABLED = "morning_reminder_enabled"
        const val PREF_EVENING_ENABLED = "evening_reminder_enabled"

        fun getReminderConfig(context: Context): ReminderConfig {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return ReminderConfig(
                morningHour = prefs.getInt(PREF_MORNING_HOUR, 8),
                eveningHour = prefs.getInt(PREF_EVENING_HOUR, 20),
                morningEnabled = prefs.getBoolean(PREF_MORNING_ENABLED, true),
                eveningEnabled = prefs.getBoolean(PREF_EVENING_ENABLED, true)
            )
        }

        fun saveReminderConfig(context: Context, config: ReminderConfig) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putInt(PREF_MORNING_HOUR, config.morningHour)
                .putInt(PREF_EVENING_HOUR, config.eveningHour)
                .putBoolean(PREF_MORNING_ENABLED, config.morningEnabled)
                .putBoolean(PREF_EVENING_ENABLED, config.eveningEnabled)
                .apply()

            scheduleDailyReminder(context)
        }

        fun scheduleDailyReminder(context: Context) {
            val config = getReminderConfig(context)
            if (config.morningEnabled) {
                scheduleMorningReminder(context, config.morningHour)
            } else {
                WorkManager.getInstance(context).cancelUniqueWork("WORK_MORNING_KICKSTART")
            }

            if (config.eveningEnabled) {
                scheduleEveningReminder(context, config.eveningHour)
            } else {
                WorkManager.getInstance(context).cancelUniqueWork("WORK_EVENING_STREAK")
            }
        }

        fun scheduleMorningReminder(context: Context, targetHour: Int = 8) {
            val data = workDataOf("IS_MORNING" to true)
            val morningRequest = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
                .setInputData(data)
                .setInitialDelay(calculateInitialDelayHours(targetHour), TimeUnit.HOURS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "WORK_MORNING_KICKSTART",
                ExistingPeriodicWorkPolicy.UPDATE,
                morningRequest
            )
        }

        fun scheduleEveningReminder(context: Context, targetHour: Int = 20) {
            val data = workDataOf("IS_MORNING" to false)
            val eveningRequest = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
                .setInputData(data)
                .setInitialDelay(calculateInitialDelayHours(targetHour), TimeUnit.HOURS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "WORK_EVENING_STREAK",
                ExistingPeriodicWorkPolicy.UPDATE,
                eveningRequest
            )
        }

        private fun calculateInitialDelayHours(targetHour: Int): Long {
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            return if (targetHour > currentHour) {
                (targetHour - currentHour).toLong()
            } else {
                (24 - currentHour + targetHour).toLong()
            }
        }
    }
}
