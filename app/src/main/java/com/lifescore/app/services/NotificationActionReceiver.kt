package com.lifescore.app.services

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.lifescore.app.LifeScoreApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", -1)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        if (notificationId != -1) {
            notificationManager?.cancel(notificationId)
        }

        when (action) {
            ACTION_COMPLETE_TASK -> {
                val app = context.applicationContext as? LifeScoreApp ?: return
                CoroutineScope(Dispatchers.IO).launch {
                    val pendingTasks = app.database.taskDao().getAllTasks().first().filter { !it.isCompleted }
                    val taskToComplete = pendingTasks.firstOrNull()
                    if (taskToComplete != null) {
                        app.database.taskDao().updateTaskStatus(
                            taskId = taskToComplete.id,
                            isCompleted = true,
                            completedAt = System.currentTimeMillis()
                        )
                        app.database.userDao().addXp(taskToComplete.pointsReward)
                    }
                }
            }

            ACTION_SNOOZE_VLOG_PROMPT -> {
                val snoozeRequest = OneTimeWorkRequestBuilder<VlogPromptWorker>()
                    .setInitialDelay(1, TimeUnit.HOURS)
                    .build()
                WorkManager.getInstance(context).enqueue(snoozeRequest)
            }
        }
    }

    companion object {
        const val ACTION_COMPLETE_TASK = "com.lifescore.app.ACTION_COMPLETE_TASK"
        const val ACTION_SNOOZE_VLOG_PROMPT = "com.lifescore.app.ACTION_SNOOZE_VLOG_PROMPT"
    }
}
