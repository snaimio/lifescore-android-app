package com.lifescore.app.domain.model

data class HeatmapDay(
    val dateIso: String,
    val dayOfWeek: Int, // 0-6 (Sun-Sat)
    val weekOfYear: Int,
    val completionCount: Int,
    val intensityLevel: Int // 0 (none) to 4 (peak)
)

data class DimensionCorrelation(
    val dimensionA: DimensionType,
    val dimensionB: DimensionType,
    val correlationPercentage: Int, // e.g. 85%
    val insightText: String
)

data class LifeScoreForecast(
    val currentScore: Int,
    val projected30Days: Int,
    val projected60Days: Int,
    val projected90Days: Int,
    val growthRateDaily: Float,
    val topGrowthDriver: DimensionType
)
