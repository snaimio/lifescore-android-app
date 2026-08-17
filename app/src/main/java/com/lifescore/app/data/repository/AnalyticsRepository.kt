package com.lifescore.app.data.repository

import com.lifescore.app.data.local.dao.DailyScoreDao
import com.lifescore.app.domain.model.DimensionCorrelation
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.HeatmapDay
import com.lifescore.app.domain.model.LifeScoreForecast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

interface AnalyticsRepository {
    fun getHeatmapData(): List<HeatmapDay>
    fun getDimensionCorrelations(): List<DimensionCorrelation>
    fun getLifeScoreForecast(currentScore: Int): LifeScoreForecast
}

class AnalyticsRepositoryImpl(
    private val scoreDao: DailyScoreDao
) : AnalyticsRepository {

    override fun getHeatmapData(): List<HeatmapDay> {
        val days = mutableListOf<HeatmapDay>()
        val cal = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Generate past 140 days (20 weeks x 7 days)
        cal.add(Calendar.DAY_OF_YEAR, -140)
        for (i in 0 until 140) {
            val dateStr = dateFormat.format(cal.time)
            val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sun, 6=Sat
            val weekOfYear = cal.get(Calendar.WEEK_OF_YEAR)

            // Calculate mock completions based on weekend vs weekday
            val isWeekend = dayOfWeek == 0 || dayOfWeek == 6
            val completions = if (isWeekend) Random.nextInt(2, 6) else Random.nextInt(3, 8)
            val intensity = when {
                completions == 0 -> 0
                completions in 1..2 -> 1
                completions in 3..4 -> 2
                completions in 5..6 -> 3
                else -> 4
            }

            days.add(
                HeatmapDay(
                    dateIso = dateStr,
                    dayOfWeek = dayOfWeek,
                    weekOfYear = weekOfYear,
                    completionCount = completions,
                    intensityLevel = intensity
                )
            )
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return days
    }

    override fun getDimensionCorrelations(): List<DimensionCorrelation> {
        return listOf(
            DimensionCorrelation(
                dimensionA = DimensionType.HEALTH,
                dimensionB = DimensionType.CAREER,
                correlationPercentage = 88,
                insightText = "When your Health score exceeds 75%, your Career task velocity increases by 2.4x."
            ),
            DimensionCorrelation(
                dimensionA = DimensionType.FITNESS,
                dimensionB = DimensionType.MENTAL_HEALTH,
                correlationPercentage = 92,
                insightText = "Daily morning physical output reduces cognitive stress markers by 65%."
            ),
            DimensionCorrelation(
                dimensionA = DimensionType.LEARNING,
                dimensionB = DimensionType.WEALTH,
                correlationPercentage = 79,
                insightText = "Consistent daily 15-minute skill mastery directly correlates with high-yield financial habit completion."
            )
        )
    }

    override fun getLifeScoreForecast(currentScore: Int): LifeScoreForecast {
        val dailyGrowth = 2.8f
        val proj30 = (currentScore + (dailyGrowth * 30)).toInt().coerceIn(0, 1000)
        val proj60 = (currentScore + (dailyGrowth * 60)).toInt().coerceIn(0, 1000)
        val proj90 = (currentScore + (dailyGrowth * 90)).toInt().coerceIn(0, 1000)

        return LifeScoreForecast(
            currentScore = currentScore,
            projected30Days = proj30,
            projected60Days = proj60,
            projected90Days = proj90,
            growthRateDaily = dailyGrowth,
            topGrowthDriver = DimensionType.CAREER
        )
    }
}
