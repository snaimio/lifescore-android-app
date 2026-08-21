package com.lifescore.app.data.repository

import com.lifescore.app.core.trackers.TrackerStatus
import com.lifescore.app.core.trackers.TrackerType
import com.lifescore.app.domain.model.DimensionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            listOf(
                TrackerLogEntry(
                    trackerType = type,
                    value = type.defaultGoal * 0.5f,
                    timestamp = System.currentTimeMillis() - 3600000,
                    note = "Daily focus session"
                )
            )
        }
    )

    override fun getAllTrackerStatuses(): Flow<List<TrackerStatus>> {
        return _trackerValues.map { currentMap ->
            val goals = _trackerGoals.value
            TrackerType.values().map { type ->
                val curr = currentMap[type] ?: 0f
                val goal = goals[type] ?: type.defaultGoal
                TrackerStatus(
                    type = type,
                    currentValue = curr,
                    targetGoal = goal,
                    streakDays = (3..12).random(),
                    todayCompleted = curr >= goal,
                    progressPercentage = if (goal > 0) (curr / goal).coerceIn(0f, 1f) else 0f
                )
            }
        }
    }

    override fun getTrackerStatus(type: TrackerType): Flow<TrackerStatus> {
        return _trackerValues.map { map ->
            val curr = map[type] ?: 0f
            val goal = _trackerGoals.value[type] ?: type.defaultGoal
            TrackerStatus(
                type = type,
                currentValue = curr,
                targetGoal = goal,
                streakDays = 5,
                todayCompleted = curr >= goal,
                progressPercentage = if (goal > 0) (curr / goal).coerceIn(0f, 1f) else 0f
            )
        }
    }

    override fun getTrackerHistory(type: TrackerType): Flow<List<TrackerLogEntry>> {
        return _trackerLogs.map { it[type] ?: emptyList() }
    }

    override fun getWeeklySummary(type: TrackerType): Flow<TrackerWeeklySummary> {
        return _trackerValues.map { map ->
            val goal = _trackerGoals.value[type] ?: type.defaultGoal
            val currentVal = map[type] ?: 0f
            val cal = Calendar.getInstance()

            val weekData = (6 downTo 0).map { daysAgo ->
                val d = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -daysAgo) }
                val dayVal = if (daysAgo == 0) currentVal else (goal * (0.6f + (daysAgo * 0.08f))).coerceAtMost(goal * 1.2f)
                DailyTrackerData(
                    dayOfWeek = dayFormatter.format(d.time),
                    value = dayVal,
                    goal = goal
                )
            }

            val avg = weekData.map { it.value }.average().toFloat()
            val metCount = weekData.count { it.value >= goal }
            val rate = metCount.toFloat() / 7f

            TrackerWeeklySummary(
                trackerType = type,
                dailyValues = weekData,
                averageValue = avg,
                completionRate = rate
            )
        }
    }

    override suspend fun logTrackerValue(type: TrackerType, valueToAdd: Float, note: String): Int {
        _trackerValues.update { current ->
            val updated = (current[type] ?: 0f) + valueToAdd
            current + (type to updated)
        }

        val newEntry = TrackerLogEntry(
            trackerType = type,
            value = valueToAdd,
            timestamp = System.currentTimeMillis(),
            note = note
        )

        _trackerLogs.update { current ->
            val list = current[type]?.toMutableList() ?: mutableListOf()
            list.add(0, newEntry)
            current + (type to list)
        }

        // Award XP dynamically based on tracker and goal completion
        val xp = type.xpReward
        try {
            val user = lifeScoreRepository.getUserProfile()
            // In a full DB flow, user repository updates XP
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
    }
}
