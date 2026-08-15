package com.lifescore.app.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.lifescore.app.utils.Constants
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val isMorning = inputData.getBoolean("IS_MORNING", true)
        if (isMorning) {
            showMorningKickstartNotification()
        } else {
            showEveningStreakNotification()
        }
        return Result.success()
    }

    private fun showMorningKickstartNotification() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ensureChannelExists(notificationManager)

        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_ID_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("☀️ Morning Kickstart!")
            .setContentText("Complete your 3 morning micro-habits and start your daily LifeScore momentum.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }

    private fun showEveningStreakNotification() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ensureChannelExists(notificationManager)

        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_ID_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🌙 Don't Break The Chain!")
            .setContentText("Protect your active streak! Complete your pending habits before midnight.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1002, notification)
    }

    private fun ensureChannelExists(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID_REMINDERS,
                Constants.CHANNEL_NAME_REMINDERS,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Smart morning & evening reminders for habits and streak protection"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        fun scheduleDailyReminder(context: Context) {
            scheduleMorningReminder(context)
            scheduleEveningReminder(context)
        }

        fun scheduleMorningReminder(context: Context) {
            val data = workDataOf("IS_MORNING" to true)
            val morningRequest = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
                .setInputData(data)
                .setInitialDelay(calculateInitialDelayHours(8), TimeUnit.HOURS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "WORK_MORNING_KICKSTART",
                ExistingPeriodicWorkPolicy.KEEP,
                morningRequest
            )
        }

        fun scheduleEveningReminder(context: Context) {
            val data = workDataOf("IS_MORNING" to false)
            val eveningRequest = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
                .setInputData(data)
                .setInitialDelay(calculateInitialDelayHours(20), TimeUnit.HOURS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "WORK_EVENING_STREAK",
                ExistingPeriodicWorkPolicy.KEEP,
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
