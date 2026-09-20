package com.lifescore.app.data.repository

import com.lifescore.app.core.trackers.TrackerStatus
import com.lifescore.app.core.trackers.TrackerType
import com.lifescore.app.domain.model.DimensionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.*

data class TrackerLogEntry(
    val id: String = UUID.randomUUID().toString(),
    val trackerType: TrackerType,
    val value: Float,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)

data class TrackerWeeklySummary(
    val trackerType: TrackerType,
    val dailyValues: List<DailyTrackerData>,
    val averageValue: Float,
    val completionRate: Float
)

data class DailyTrackerData(
    val dayOfWeek: String,
    val value: Float,
    val goal: Float
)

interface LifeTrackersRepository {
    fun getAllTrackerStatuses(): Flow<List<TrackerStatus>>
    fun getTrackerStatus(type: TrackerType): Flow<TrackerStatus>
    fun getTrackerHistory(type: TrackerType): Flow<List<TrackerLogEntry>>
    fun getWeeklySummary(type: TrackerType): Flow<TrackerWeeklySummary>
    suspend fun logTrackerValue(type: TrackerType, valueToAdd: Float, note: String = ""): Int // Returns XP earned
    suspend fun setTrackerGoal(type: TrackerType, newGoal: Float)
    suspend fun deleteTrackerLog(entryId: String, type: TrackerType)
}

class LifeTrackersRepositoryImpl(
    private val lifeScoreRepository: LifeScoreRepository
) : LifeTrackersRepository {

    private val dayFormatter = SimpleDateFormat("EEE", Locale.getDefault())
    private val dateKeyFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // In-memory reactive state initialized with default baseline for all 15 trackers
    private val _trackerValues = MutableStateFlow<Map<TrackerType, Float>>(
        mapOf(
            TrackerType.HYDRATION to 1250f,
            TrackerType.NUTRITION to 1400f,
            TrackerType.SLEEP to 7.5f,
            TrackerType.VITALS to 68f,
            TrackerType.STEPS to 6420f,
            TrackerType.WORKOUTS to 30f,
            TrackerType.WEIGHT to 74.5f,
            TrackerType.READING to 15f,
            TrackerType.SKILL_MASTERY to 1.5f,
            TrackerType.JOURNAL to 1f,
            TrackerType.OKRS to 45f,
            TrackerType.ROUTINES to 2f,
            TrackerType.SOCIAL to 1f,
            TrackerType.FINANCE to 35f,
            TrackerType.MINDFULNESS to 10f
        )
    )

    private val _trackerGoals = MutableStateFlow<Map<TrackerType, Float>>(
        TrackerType.values().associateWith { it.defaultGoal }
    )

    private val _trackerLogs = MutableStateFlow<Map<TrackerType, List<TrackerLogEntry>>>(
        TrackerType.values().associateWith { type ->
            val now = System.currentTimeMillis()
            listOf(
                TrackerLogEntry(
                    trackerType = type,
                    value = type.defaultGoal * 0.5f,
                    timestamp = now - (24 * 3600000L),
                    note = "Previous day logged entry"
                ),
                TrackerLogEntry(
                    trackerType = type,
                    value = type.defaultGoal * 0.5f,
                    timestamp = now - 3600000L,
                    note = "Today focus session"
                )
            )
        }
    )

    private fun calculateStreak(logs: List<TrackerLogEntry>): Int {
        if (logs.isEmpty()) return 0
        val loggedDates = logs.map { dateKeyFormatter.format(Date(it.timestamp)) }.toSet()
        
        val cal = Calendar.getInstance()
        var streak = 0
        
        val todayStr = dateKeyFormatter.format(cal.time)
        if (!loggedDates.contains(todayStr)) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = dateKeyFormatter.format(cal.time)
            if (!loggedDates.contains(yesterdayStr)) {
                return 0
            }
        }
        
        while (true) {
            val dateStr = dateKeyFormatter.format(cal.time)
            if (loggedDates.contains(dateStr)) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }

    override fun getAllTrackerStatuses(): Flow<List<TrackerStatus>> {
        return combine(_trackerValues, _trackerGoals, _trackerLogs) { currentMap, goals, logsMap ->
            TrackerType.values().map { type ->
                val curr = currentMap[type] ?: 0f
                val goal = goals[type] ?: type.defaultGoal
                val logs = logsMap[type] ?: emptyList()
                val streak = calculateStreak(logs)
                TrackerStatus(
                    type = type,
                    currentValue = curr,
                    targetGoal = goal,
                    streakDays = streak.coerceAtLeast(if (curr > 0f) 1 else 0),
                    todayCompleted = curr >= goal,
                    progressPercentage = if (goal > 0) (curr / goal).coerceIn(0f, 1f) else 0f
                )
            }
        }
    }

    override fun getTrackerStatus(type: TrackerType): Flow<TrackerStatus> {
        return combine(_trackerValues, _trackerGoals, _trackerLogs) { map, goals, logsMap ->
            val curr = map[type] ?: 0f
            val goal = goals[type] ?: type.defaultGoal
            val logs = logsMap[type] ?: emptyList()
            val streak = calculateStreak(logs)
            TrackerStatus(
                type = type,
                currentValue = curr,
                targetGoal = goal,
                streakDays = streak.coerceAtLeast(if (curr > 0f) 1 else 0),
                todayCompleted = curr >= goal,
                progressPercentage = if (goal > 0) (curr / goal).coerceIn(0f, 1f) else 0f
            )
        }
    }

    override fun getTrackerHistory(type: TrackerType): Flow<List<TrackerLogEntry>> {
        return _trackerLogs.map { it[type] ?: emptyList() }
    }

    override fun getWeeklySummary(type: TrackerType): Flow<TrackerWeeklySummary> {
        return combine(_trackerLogs, _trackerGoals) { logsMap, goalsMap ->
            val logs = logsMap[type] ?: emptyList()
            val goal = goalsMap[type] ?: type.defaultGoal
            val logsByDay = logs.groupBy { dateKeyFormatter.format(Date(it.timestamp)) }

            val weekData = (6 downTo 0).map { daysAgo ->
                val d = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -daysAgo) }
                val dayKey = dateKeyFormatter.format(d.time)
                val dayLogs = logsByDay[dayKey] ?: emptyList()
                val daySum = dayLogs.sumOf { it.value.toDouble() }.toFloat()
                DailyTrackerData(
                    dayOfWeek = dayFormatter.format(d.time),
                    value = daySum,
                    goal = goal
                )
            }

            val avg = if (weekData.isNotEmpty()) weekData.map { it.value }.average().toFloat() else 0f
            val metCount = weekData.count { it.value >= goal }
            val rate = if (weekData.isNotEmpty()) metCount.toFloat() / weekData.size else 0f

            TrackerWeeklySummary(
                trackerType = type,
                dailyValues = weekData,
                averageValue = avg,
                completionRate = rate
            )
        }
    }

    override suspend fun logTrackerValue(type: TrackerType, valueToAdd: Float, note: String): Int {
        val now = System.currentTimeMillis()
        val newEntry = TrackerLogEntry(
            trackerType = type,
            value = valueToAdd,
            timestamp = now,
            note = note.ifBlank { "Logged entry" }
        )

        _trackerLogs.update { current ->
            val list = current[type]?.toMutableList() ?: mutableListOf()
            list.add(0, newEntry)
            current + (type to list)
        }

        val todayKey = dateKeyFormatter.format(Date(now))
        val currentLogs = _trackerLogs.value[type] ?: emptyList()
        val todayTotal = currentLogs
            .filter { dateKeyFormatter.format(Date(it.timestamp)) == todayKey }
            .sumOf { it.value.toDouble() }
            .toFloat()

        _trackerValues.update { current ->
            current + (type to todayTotal)
        }

        val xp = type.xpReward
        try {
            val user = lifeScoreRepository.getUserProfile()
        } catch (_: Exception) {}

        return xp
    }

    override suspend fun setTrackerGoal(type: TrackerType, newGoal: Float) {
        _trackerGoals.update { it + (type to newGoal) }
    }

    override suspend fun deleteTrackerLog(entryId: String, type: TrackerType) {
        _trackerLogs.update { current ->
            val list = current[type]?.filterNot { it.id == entryId } ?: emptyList()
            current + (type to list)
        }
        val todayKey = dateKeyFormatter.format(Date())
        val currentLogs = _trackerLogs.value[type] ?: emptyList()
        val todayTotal = currentLogs
            .filter { dateKeyFormatter.format(Date(it.timestamp)) == todayKey }
            .sumOf { it.value.toDouble() }
            .toFloat()
        _trackerValues.update { current ->
            current + (type to todayTotal)
        }
    }
}

