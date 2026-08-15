package com.lifescore.app.core.util

import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.MasteryTier
import com.lifescore.app.domain.model.SkillLogSession
import com.lifescore.app.domain.model.SkillMastery
import java.text.SimpleDateFormat
import java.util.*

object SkillMasteryManager {

    fun getDefaultSkills(): List<SkillMastery> {
        return listOf(
            SkillMastery(
                id = "skill_japanese",
                title = "Japanese & Mandarin Fluency",
                emoji = "🗣️",
                dimension = DimensionType.LEARNING,
                targetHours = 2000,
                accumulatedMinutes = 14400, // 240 hours -> Apprentice
                streakDays = 12,
                lastPracticedDate = "2026-08-14",
                sessionsCount = 180,
                totalXpEarned = 6000
            ),
            SkillMastery(
                id = "skill_systems_arch",
                title = "Distributed Systems & Cloud Architecture",
                emoji = "💻",
                dimension = DimensionType.CAREER,
                targetHours = 10000,
                accumulatedMinutes = 72000, // 1,200 hours -> Journeyman
                streakDays = 24,
                lastPracticedDate = "2026-08-14",
                sessionsCount = 450,
                totalXpEarned = 30000
            ),
            SkillMastery(
                id = "skill_piano",
                title = "Classical Piano & Jazz Improvisation",
                emoji = "🎹",
                dimension = DimensionType.MENTAL_HEALTH,
                targetHours = 5000,
                accumulatedMinutes = 18000, // 300 hours -> Apprentice
                streakDays = 5,
                lastPracticedDate = "2026-08-13",
                sessionsCount = 120,
                totalXpEarned = 7500
            ),
            SkillMastery(
                id = "skill_bjj",
                title = "Brazilian Jiu-Jitsu & Somatics",
                emoji = "🥋",
                dimension = DimensionType.FITNESS,
                targetHours = 2000,
                accumulatedMinutes = 36000, // 600 hours -> Journeyman
                streakDays = 8,
                lastPracticedDate = "2026-08-14",
                sessionsCount = 200,
                totalXpEarned = 15000
            ),
            SkillMastery(
                id = "skill_investing",
                title = "Asymmetric Value Investing & Macro Analysis",
                emoji = "📈",
                dimension = DimensionType.WEALTH,
                targetHours = 5000,
                accumulatedMinutes = 27000, // 450 hours -> Apprentice
                streakDays = 14,
                lastPracticedDate = "2026-08-14",
                sessionsCount = 190,
                totalXpEarned = 11250
            )
        )
    }

    fun logPracticeSession(
        skill: SkillMastery,
        minutes: Int,
        notes: String = ""
    ): Pair<SkillMastery, SkillLogSession> {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val newAccumulated = skill.accumulatedMinutes + minutes
        val earnedXp = (minutes * 25) / 60
        val isNewDay = skill.lastPracticedDate != todayStr
        val newStreak = if (isNewDay) skill.streakDays + 1 else skill.streakDays

        val updatedSkill = skill.copy(
            accumulatedMinutes = newAccumulated,
            streakDays = newStreak,
            lastPracticedDate = todayStr,
            sessionsCount = skill.sessionsCount + 1,
            totalXpEarned = skill.totalXpEarned + earnedXp
        )

        val session = SkillLogSession(
            skillId = skill.id,
            minutes = minutes,
            notes = notes,
            xpGranted = earnedXp
        )

        return Pair(updatedSkill, session)
    }

    fun calculateTotalPracticeHours(skills: List<SkillMastery>): Float {
        val totalMinutes = skills.sumOf { it.accumulatedMinutes }
        return totalMinutes / 60f
    }

    fun calculate10kGlobalProgress(skills: List<SkillMastery>): Float {
        val totalHours = calculateTotalPracticeHours(skills)
        return (totalHours / 10000f).coerceIn(0f, 1f)
    }

    fun getGlobalMasteryTier(totalHours: Float): MasteryTier {
        return MasteryTier.fromHours(totalHours)
    }
}
