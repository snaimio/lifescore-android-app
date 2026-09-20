package com.lifescore.app.data.repository

import com.lifescore.app.data.local.dao.HydrationDao
import com.lifescore.app.data.local.entity.HydrationEntity
import com.lifescore.app.data.local.entity.HydrationGoalEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

data class HydrationStats(
    val todayTotalMl: Int,
    val dailyGoalMl: Int,
    val progressPercentage: Float,
    val glassesConsumed: Int,
    val glassesGoal: Int,
    val isGoalMet: Boolean,
    val streakDays: Int,
    val last7Days: List<DailyHydrationData>
)

data class DailyHydrationData(
    val date: String,
    val dayOfWeek: String,
    val totalMl: Int,
    val goalMl: Int
)

data class HydrationEntry(
    val id: Long,
    val timestamp: Long,
    val volumeMl: Int,
    val source: String,
    val formattedTime: String
)

interface HydrationRepository {
    fun getTodayEntries(userId: String = "user_default"): Flow<List<HydrationEntity>>
    fun getTodayTotal(userId: String = "user_default"): Flow<Int>
    fun getHydrationStats(userId: String = "user_default"): Flow<HydrationStats>
    fun getHydrationGoal(userId: String = "user_default"): Flow<HydrationGoalEntity?>
    suspend fun saveHydrationEntry(userId: String = "user_default", volumeMl: Int, source: String = "manual")
    suspend fun saveHydrationGoal(userId: String = "user_default", goalMl: Int, weightKg: Float? = null, activityLevel: String = "moderate")
    suspend fun deleteHydrationEntry(entry: HydrationEntity)
    suspend fun calculateStreak(userId: String = "user_default"): Int
    suspend fun getLast7Days(userId: String = "user_default"): List<DailyHydrationData>
}

class HydrationRepositoryImpl(
    private val dao: HydrationDao
) : HydrationRepository {

    private val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val dayFormatter = SimpleDateFormat("EEE", Locale.getDefault())
    private val dateFormatter = SimpleDateFormat("MMM d", Locale.getDefault())

    override fun getTodayEntries(userId: String): Flow<List<HydrationEntity>> {
        return dao.getTodayEntries(userId)
    }

    override fun getTodayTotal(userId: String): Flow<Int> {
        return dao.getTodayTotal(userId).map { it ?: 0 }
    }

    override fun getHydrationGoal(userId: String): Flow<HydrationGoalEntity?> {
        return dao.observeHydrationGoal(userId)
    }

    override fun getHydrationStats(userId: String): Flow<HydrationStats> {
        return combine(
            getTodayTotal(userId),
            getHydrationGoal(userId),
            dao.getHydrationEntries(userId)
        ) { todayTotal, goal, allEntries ->
            val goalMl = goal?.dailyGoalMl ?: 2500
            val glassesGoal = (goalMl / 250f).toInt().coerceAtLeast(1)
            val glassesConsumed = (todayTotal / 250f).toInt()
            val progress = (todayTotal.toFloat() / goalMl.toFloat()).coerceIn(0f, 1f)

            val last7 = computeLast7Days(allEntries, goalMl)
            val streak = computeStreak(allEntries, goalMl, todayTotal)

            HydrationStats(
                todayTotalMl = todayTotal,
                dailyGoalMl = goalMl,
                progressPercentage = progress,
                glassesConsumed = glassesConsumed,
                glassesGoal = glassesGoal,
                isGoalMet = todayTotal >= goalMl,
                streakDays = streak,
                last7Days = last7
            )
        }
    }

    override suspend fun saveHydrationEntry(userId: String, volumeMl: Int, source: String) {
        val entry = HydrationEntity(
            userId = userId,
            volumeMl = volumeMl,
            source = source,
            timestamp = System.currentTimeMillis()
        )
        dao.insertHydrationEntry(entry)
    }

    override suspend fun saveHydrationGoal(userId: String, goalMl: Int, weightKg: Float?, activityLevel: String) {
        val goal = HydrationGoalEntity(
            userId = userId,
            dailyGoalMl = goalMl,
            weightKg = weightKg,
            activityLevel = activityLevel,
            updatedAt = System.currentTimeMillis()
        )
        dao.saveHydrationGoal(goal)
    }

    override suspend fun deleteHydrationEntry(entry: HydrationEntity) {
        dao.deleteHydrationEntry(entry)
    }

    override suspend fun calculateStreak(userId: String): Int {
        val goal = dao.getHydrationGoal(userId)?.dailyGoalMl ?: 2500
        val allEntries = dao.getAllEntriesSync(userId)
        val todayTotal = dao.getTodayTotalMlSync(userId) ?: 0
        return computeStreak(allEntries, goal, todayTotal)
    }

    override suspend fun getLast7Days(userId: String): List<DailyHydrationData> {
        val goal = dao.getHydrationGoal(userId)?.dailyGoalMl ?: 2500
        val allEntries = dao.getAllEntriesSync(userId)
        return computeLast7Days(allEntries, goal)
    }

    private fun computeLast7Days(allEntries: List<HydrationEntity>, goalMl: Int): List<DailyHydrationData> {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val entriesByDay = allEntries.groupBy { sdf.format(Date(it.timestamp)) }

        val list = mutableListOf<DailyHydrationData>()
        val cal = Calendar.getInstance()

        for (i in 6 downTo 0) {
            val targetCal = Calendar.getInstance().apply {
                time = cal.time
                add(Calendar.DAY_OF_YEAR, -i)
            }
            val key = sdf.format(targetCal.time)
            val dayEntries = entriesByDay[key] ?: emptyList()
            val totalForDay = dayEntries.sumOf { it.volumeMl }

            list.add(
                DailyHydrationData(
                    date = dateFormatter.format(targetCal.time),
                    dayOfWeek = dayFormatter.format(targetCal.time),
                    totalMl = totalForDay,
                    goalMl = goalMl
                )
            )
        }
        return list
    }

    private fun computeStreak(allEntries: List<HydrationEntity>, goalMl: Int, todayTotal: Int): Int {
        if (allEntries.isEmpty()) return if (todayTotal >= goalMl) 1 else 0
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val entriesByDay = allEntries.groupBy { sdf.format(Date(it.timestamp)) }
        val dailyTotals = entriesByDay.mapValues { (_, entries) -> entries.sumOf { it.volumeMl } }

        val cal = Calendar.getInstance()
        var streak = 0
        val todayKey = sdf.format(cal.time)

        if ((dailyTotals[todayKey] ?: 0) < goalMl) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayKey = sdf.format(cal.time)
            if ((dailyTotals[yesterdayKey] ?: 0) < goalMl) {
                return 0
            }
        }

        while (true) {
            val key = sdf.format(cal.time)
            val total = dailyTotals[key] ?: 0
            if (total >= goalMl) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }
}
