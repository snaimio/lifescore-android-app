package com.lifescore.app.core.util

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import com.lifescore.app.data.local.entity.ScreenTimeActionType
import com.lifescore.app.data.repository.AppUsageItemModel
import java.util.Calendar

data class RealUsageReport(
    val totalMinutes: Int,
    val socialMinutes: Int,
    val gamingMinutes: Int,
    val videoMinutes: Int,
    val shoppingMinutes: Int,
    val pickups: Int,
    val topApps: List<AppUsageItemModel>,
    val hasPermission: Boolean
)

object RealUsageStatsHelper {

    fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager ?: return false
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        } else {
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun createUsageAccessIntent(): Intent {
        return Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    fun getTodayUsageReport(context: Context): RealUsageReport {
        if (!hasUsageStatsPermission(context)) {
            return RealUsageReport(
                totalMinutes = 0,
                socialMinutes = 0,
                gamingMinutes = 0,
                videoMinutes = 0,
                shoppingMinutes = 0,
                pickups = 0,
                topApps = emptyList(),
                hasPermission = false
            )
        }

        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            ?: return RealUsageReport(0, 0, 0, 0, 0, 0, emptyList(), false)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val statsList: List<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        ) ?: emptyList()

        val packageManager = context.packageManager
        val aggregatedMap = mutableMapOf<String, Long>()

        for (stat in statsList) {
            val totalTime = stat.totalTimeInForeground
            if (totalTime > 0) {
                aggregatedMap[stat.packageName] = (aggregatedMap[stat.packageName] ?: 0L) + totalTime
            }
        }

        var totalMillis = 0L
        var socialMillis = 0L
        var gamingMillis = 0L
        var videoMillis = 0L
        var shoppingMillis = 0L

        val appItems = mutableListOf<AppUsageItemModel>()

        for ((pkg, durationMillis) in aggregatedMap) {
            val minutes = (durationMillis / (1000 * 60)).toInt()
            if (minutes <= 0) continue

            val appName = try {
                val appInfo = packageManager.getApplicationInfo(pkg, 0)
                packageManager.getApplicationLabel(appInfo).toString()
            } catch (_: Exception) {
                pkg.substringAfterLast('.')
            }

            // Determine category and emoji
            val (category, emoji) = categorizePackage(pkg, appName)

            totalMillis += durationMillis
            when (category) {
                ScreenTimeActionType.SOCIAL_MEDIA -> socialMillis += durationMillis
                ScreenTimeActionType.GAMING -> gamingMillis += durationMillis
                ScreenTimeActionType.VIDEO_STREAMING -> videoMillis += durationMillis
                ScreenTimeActionType.SHOPPING -> shoppingMillis += durationMillis
                else -> {}
            }

            appItems.add(
                AppUsageItemModel(
                    appName = appName,
                    minutes = minutes,
                    category = category,
                    iconEmoji = emoji
                )
            )
        }

        // Calculate device pickups / unlocks from usage events
        var pickupsCount = 0
        try {
            val events = usageStatsManager.queryEvents(startTime, endTime)
            val event = UsageEvents.Event()
            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.KEYGUARD_HIDDEN ||
                    event.eventType == UsageEvents.Event.SCREEN_INTERACTIVE
                ) {
                    pickupsCount++
                }
            }
        } catch (_: Exception) {}

        // Sort top apps by minutes descending
        val sortedTopApps = appItems.sortedByDescending { it.minutes }.take(10)

        return RealUsageReport(
            totalMinutes = (totalMillis / (1000 * 60)).toInt(),
            socialMinutes = (socialMillis / (1000 * 60)).toInt(),
            gamingMinutes = (gamingMillis / (1000 * 60)).toInt(),
            videoMinutes = (videoMillis / (1000 * 60)).toInt(),
            shoppingMinutes = (shoppingMillis / (1000 * 60)).toInt(),
            pickups = pickupsCount.coerceAtLeast(sortedTopApps.size),
            topApps = sortedTopApps,
            hasPermission = true
        )
    }

    private fun categorizePackage(pkg: String, appName: String): Pair<ScreenTimeActionType, String> {
        val lowerPkg = pkg.lowercase()
        val lowerName = appName.lowercase()

        return when {
            lowerPkg.contains("instagram") || lowerPkg.contains("facebook") ||
            lowerPkg.contains("twitter") || lowerPkg.contains("tiktok") ||
            lowerPkg.contains("snapchat") || lowerPkg.contains("reddit") ||
            lowerPkg.contains("threads") || lowerPkg.contains("whatsapp") ||
            lowerPkg.contains("telegram") || lowerPkg.contains("discord") ||
            lowerName.contains("instagram") || lowerName.contains("tiktok") ||
            lowerName.contains("twitter") || lowerName.contains("reddit") -> {
                Pair(ScreenTimeActionType.SOCIAL_MEDIA, "💬")
            }

            lowerPkg.contains("youtube") || lowerPkg.contains("netflix") ||
            lowerPkg.contains("disney") || lowerPkg.contains("hulu") ||
            lowerPkg.contains("twitch") || lowerPkg.contains("primevideo") ||
            lowerName.contains("youtube") || lowerName.contains("netflix") -> {
                Pair(ScreenTimeActionType.VIDEO_STREAMING, "▶️")
            }

            lowerPkg.contains("game") || lowerPkg.contains("supercell") ||
            lowerPkg.contains("roblox") || lowerPkg.contains("minecraft") ||
            lowerPkg.contains("unity") || lowerName.contains("game") ||
            lowerName.contains("clash") -> {
                Pair(ScreenTimeActionType.GAMING, "🎮")
            }

            lowerPkg.contains("amazon") || lowerPkg.contains("ebay") ||
            lowerPkg.contains("shopping") || lowerPkg.contains("walmart") ||
            lowerPkg.contains("target") || lowerPkg.contains("shein") ||
            lowerPkg.contains("temu") || lowerName.contains("amazon") ||
            lowerName.contains("shop") -> {
                Pair(ScreenTimeActionType.SHOPPING, "🛍️")
            }

            else -> Pair(ScreenTimeActionType.PRODUCTIVITY, "📱")
        }
    }
}
