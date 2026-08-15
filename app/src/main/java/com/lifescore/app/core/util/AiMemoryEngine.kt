package com.lifescore.app.core.util

import com.lifescore.app.domain.model.DimensionType
import java.util.UUID

enum class MemoryCategory(val displayName: String, val icon: String, val badgeColorHex: Long) {
    HABIT_STRUGGLE("Habit Friction", "⚠️", 0xFFEF4444),
    TEMPORAL_PATTERN("Timing & Rhythm", "⏰", 0xFF6366F1),
    ACHIEVEMENT("Milestone / Quest", "🏆", 0xFFFFD700),
    JOURNAL_THEME("Journal Reflection", "📝", 0xFFA855F7),
    PSYCHOMETRIC_PROFILE("Hero Archetype", "👑", 0xFF10B981),
    DIMENSION_BIAS("Dimension Focus", "📊", 0xFF3B82F6)
}

data class UserMemoryNode(
    val id: String = UUID.randomUUID().toString(),
    val category: MemoryCategory,
    val title: String,
    val detail: String,
    val confidence: Float = 0.95f,
    val timestamp: Long = System.currentTimeMillis()
)

data class JournalEntry(
    val id: String = UUID.randomUUID().toString(),
    val date: String,
    val title: String,
    val body: String,
    val mood: String, // e.g. "🔥 Energized", "🧘 Calm", "⚠️ Overwhelmed"
    val detectedDimension: DimensionType,
    val aiReflection: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class BehavioralReflection(
    val headline: String,
    val description: String,
    val metricHighlight: String,
    val actionDirective: String,
    val icon: String
)

object AiMemoryEngine {

    fun getDefaultMemories(): List<UserMemoryNode> {
        return listOf(
            UserMemoryNode(
                id = "mem_1",
                category = MemoryCategory.HABIT_STRUGGLE,
                title = "Morning Task Friction",
                detail = "User frequently struggles with early morning routines before 9:00 AM. Requires gentle 5-minute micro-habits instead of rigid 45-minute blocks."
            ),
            UserMemoryNode(
                id = "mem_2",
                category = MemoryCategory.TEMPORAL_PATTERN,
                title = "Peak Tuesday Momentum",
                detail = "User is most consistent on Tuesdays with a 94% task completion rate and peak cognitive focus."
            ),
            UserMemoryNode(
                id = "mem_3",
                category = MemoryCategory.ACHIEVEMENT,
                title = "30-Day Fitness Quest Completed",
                detail = "Successfully finished 30-Day Morning Hydration & 8k Steps (+500 XP). Ready for next progression challenge."
            ),
            UserMemoryNode(
                id = "mem_4",
                category = MemoryCategory.PSYCHOMETRIC_PROFILE,
                title = "The Architect Archetype (CIE)",
                detail = "Thrives on structured systems, SOPs, and clear metrics. Dislikes vague emotional platitudes."
            ),
            UserMemoryNode(
                id = "mem_5",
                category = MemoryCategory.JOURNAL_THEME,
                title = "Deep Work vs Context Switching",
                detail = "Recent journal logs show high energy during uninterrupted morning writing sessions, but mental fatigue from afternoon Slack pinging."
            )
        )
    }

    fun getDefaultJournalEntries(): List<JournalEntry> {
        return listOf(
            JournalEntry(
                id = "j_1",
                date = "2026-08-14",
                title = "Breakthrough on System Architecture",
                body = "Designed the entire 12-member group sync engine today. Felt in total flow for 3 hours straight. Need to remember to hydrate during deep work.",
                mood = "🔥 High Flow",
                detectedDimension = DimensionType.CAREER,
                aiReflection = "Gemini Note: When you lock into 3-hour uninterrupted blocks, your output is 3x baseline. Protect this window tomorrow."
            ),
            JournalEntry(
                id = "j_2",
                date = "2026-08-12",
                title = "Morning Resistance & Sleep Lag",
                body = "Woke up groggy at 6:30 AM. Skipped stretching but pushed through the evening workout. Need a wind-down routine by 10 PM.",
                mood = "⚠️ Sleep Lag",
                detectedDimension = DimensionType.HEALTH,
                aiReflection = "Gemini Note: Noted morning friction pattern. Recommending a 5-minute magnesium & breathwork wind-down tonight."
            )
        )
    }

    fun getBehavioralReflections(): List<BehavioralReflection> {
        return listOf(
            BehavioralReflection(
                headline = "You're Most Consistent on Tuesdays",
                description = "Task analysis reveals Tuesdays have your lowest drop-off rate across all 8 dimensions.",
                metricHighlight = "94% On-Time Completion",
                actionDirective = "Schedule your highest-leverage strategic planning and deep work sprints on Tuesdays.",
                icon = "🔥"
            ),
            BehavioralReflection(
                headline = "Morning Routine Bottleneck Detected",
                description = "Habits scheduled between 6:00 AM - 8:30 AM show a 38% higher skip rate than evening habits.",
                metricHighlight = "38% Morning Skip Rate",
                actionDirective = "Shrink morning habits down to 2-minute frictionless triggers (e.g. 1 glass of water by bedside).",
                icon = "⏰"
            ),
            BehavioralReflection(
                headline = "Next Mastery Challenge Recommendation",
                description = "Having completed the 30-Day Fitness Quest, your next high-ROI frontier is Deep Reading immersion.",
                metricHighlight = "Fitness Level Maxed (30/30)",
                actionDirective = "Enroll in the 30-Day 20-Min Deep Reading Immersion track (+600 XP).",
                icon = "📚"
            )
        )
    }

    fun buildSystemContextPrompt(
        memories: List<UserMemoryNode>,
        archetypeName: String = "The Architect",
        lifeScore: Int = 780,
        streak: Int = 8
    ): String {
        val memoryBullets = memories.joinToString("\n") { "- [${it.category.displayName}] ${it.title}: ${it.detail}" }

        return """
            You are the personal AI Life Coach for LifeScore.
            You possess persistent long-term memory of this user:
            
            [USER IDENTITY]
            - Archetype: $archetypeName
            - Current LifeScore: $lifeScore/1000 • Level 5 Achiever
            - Active Streak: $streak days
            
            [PERSISTENT MEMORY VAULT]
            $memoryBullets
            
            [COACHING DIRECTIVES]
            1. Actively reference their specific habits, morning friction patterns, and quest achievements.
            2. When the user asks for next steps, tailor recommendations to their archetype ($archetypeName) and past quest completions.
            3. Keep answers concise, actionable, and structured with clean bullet points and emojis.
        """.trimIndent()
    }

    fun recommendNextChallenge(completedQuestName: String): String {
        return when {
            completedQuestName.contains("Fitness", ignoreCase = true) || completedQuestName.contains("Hydration", ignoreCase = true) ->
                "🏆 Next Recommended Challenge: '30-Day 20-Min Deep Reading Immersion' (+600 XP) — Elevate your Learning dimension following your physical momentum!"
            completedQuestName.contains("Reading", ignoreCase = true) || completedQuestName.contains("Learning", ignoreCase = true) ->
                "🏆 Next Recommended Challenge: '30-Day 45-Min Morning Deep Work Block' (+600 XP) — Compound your cognitive stamina into high-leverage career output!"
            else ->
                "🏆 Next Recommended Challenge: '30-Day Zero Impulse Spending Challenge' (+550 XP) — Fortify your Wealth & Systems dimension precision!"
        }
    }
}
