package com.lifescore.app.domain.model

data class MicroVlog(
    val id: String,
    val date: String,            // YYYY-MM-DD
    val dayOfWeek: String,       // Mon, Tue, Wed, Thu, Fri, Sat, Sun
    val dimension: DimensionType,
    val caption: String,
    val videoUri: String? = null,
    val thumbnailColorHex: Long = 0xFF1E293B,
    val durationSeconds: Double = 2.0,
    val isRecorded: Boolean = true,
    val isUploaded: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class WeeklyMontage(
    val weekRange: String,       // e.g. "Aug 10 - Aug 16"
    val clips: List<MicroVlog>,
    val totalDurationSeconds: Double,
    val dominantDimension: DimensionType,
    val lifeScoreGain: Int = 45,
    val streakMaintained: Int = 7
)
