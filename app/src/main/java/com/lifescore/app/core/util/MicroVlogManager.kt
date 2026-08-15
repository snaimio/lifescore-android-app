package com.lifescore.app.core.util

import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.MicroVlog
import com.lifescore.app.domain.model.WeeklyMontage

object MicroVlogManager {

    const val CLIP_DURATION_SECONDS = 2.0
    const val DAYS_IN_WEEK = 7

    fun generateCurrentWeekTemplate(): List<MicroVlog> {
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val sampleDimensions = listOf(
            DimensionType.FITNESS,
            DimensionType.LEARNING,
            DimensionType.CAREER,
            DimensionType.HEALTH,
            DimensionType.MENTAL_HEALTH,
            DimensionType.RELATIONSHIPS,
            DimensionType.WEALTH
        )
        val sampleCaptures = listOf(
            "Morning 5km run & breathwork 🏃",
            "Read 2 chapters on system architecture 📖",
            "Shipped new API sprint features 💻",
            "Healthy meal prep & 3L hydration 🥗",
            "10-min meditation in the park 🧘",
            "Dinner & catchup with old friends ☕",
            "Weekly budget review & savings check 📊"
        )

        return days.mapIndexed { index, day ->
            MicroVlog(
                id = "vlog_day_${index + 1}",
                date = "2026-08-${10 + index}",
                dayOfWeek = day,
                dimension = sampleDimensions[index],
                caption = sampleCaptures[index],
                thumbnailColorHex = sampleDimensions[index].baseColorHex,
                durationSeconds = CLIP_DURATION_SECONDS,
                isRecorded = index < 5 // First 5 recorded, weekend pending
            )
        }
    }

    fun calculateTotalDuration(clips: List<MicroVlog>): Double {
        val recordedClips = clips.filter { it.isRecorded }
        return recordedClips.size * CLIP_DURATION_SECONDS
    }

    fun findDominantDimension(clips: List<MicroVlog>): DimensionType {
        val recorded = clips.filter { it.isRecorded }
        if (recorded.isEmpty()) return DimensionType.FITNESS
        val counts = recorded.groupingBy { it.dimension }.eachCount()
        return counts.maxByOrNull { it.value }?.key ?: DimensionType.FITNESS
    }

    fun createWeeklyMontage(clips: List<MicroVlog>, scoreGain: Int = 45, streak: Int = 7): WeeklyMontage {
        val recorded = clips.filter { it.isRecorded }
        val dominant = findDominantDimension(recorded)
        val duration = calculateTotalDuration(recorded)

        return WeeklyMontage(
            weekRange = "Aug 10 - Aug 16",
            clips = recorded,
            totalDurationSeconds = duration,
            dominantDimension = dominant,
            lifeScoreGain = scoreGain,
            streakMaintained = streak
        )
    }

    fun generateReelCaption(montage: WeeklyMontage, score: Int, streak: Int): String {
        val durationStr = String.format("%.0fs", montage.totalDurationSeconds)
        return "🎬 My LifeScore 7-Day Reel: $durationStr of daily discipline & compounding momentum! 🔥 $streak-day streak • $score LifeScore. Dominating in ${montage.dominantDimension.displayName}! Create your 2s daily micro-vlogs: https://lifescore.app/reels #LifeScoreReel #Setlog #DailyVlog #HabitTracker"
    }
}
