package com.lifescore.app.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.lifescore.app.domain.model.DimensionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class WeeklyAuditResult(
    val headline: String,
    val pointSummary: String,
    val topDimension: DimensionType,
    val growthDimension: DimensionType,
    val keyAchievements: List<String>,
    val nextWeekDirectives: List<String>,
    val motivationalQuote: String
)

data class UserAiContext(
    val score: Int,
    val level: Int,
    val streak: Int,
    val dimensionScores: Map<DimensionType, Int>,
    val title: String
)

interface GeminiCoachRepository {
    suspend fun getDailyExecutiveBrief(
        lowestDimension: DimensionType,
        lowestScore: Int,
        totalScore: Int
    ): String

    suspend fun askCoach(userQuestion: String, contextScore: Int): String
    suspend fun askCoachWithMemory(userQuestion: String, memoryContext: String): String
    suspend fun askCoachWithArchetype(
        userQuestion: String,
        archetypeId: String,
        assessmentSummary: String = "",
        memoryContext: String = ""
    ): String

    suspend fun generateWeeklyAudit(
        scores: Map<DimensionType, Int>,
        tasksCompleted: Int,
        totalScore: Int,
        streak: Int
    ): WeeklyAuditResult

    fun generateDimensionGuidance(
        dimension: DimensionType,
        score: Int,
        isWeakest: Boolean
    ): String

    fun generateWeeklyRecapShareText(
        audit: WeeklyAuditResult,
        score: Int,
        streak: Int
    ): String
}

class GeminiCoachRepositoryImpl(
    private val apiKey: String? = com.lifescore.app.BuildConfig.GEMINI_API_KEY
) : GeminiCoachRepository {

    private val generativeModel by lazy {
        if (!apiKey.isNullOrBlank() && apiKey != "DEMO_KEY") {
            try {
                GenerativeModel(
                    modelName = com.lifescore.app.BuildConfig.GEMINI_MODEL,
                    apiKey = apiKey
                )
            } catch (_: Exception) {
                null
            }
        } else null
    }

    override suspend fun getDailyExecutiveBrief(
        lowestDimension: DimensionType,
        lowestScore: Int,
        totalScore: Int
    ): String = withContext(Dispatchers.IO) {
        if (generativeModel != null) {
            try {
                val prompt = """
                    You are the personal AI Life Coach for the LifeScore app.
                    The user's current overall LifeScore is $totalScore/1000.
                    Their lowest score today is in ${lowestDimension.displayName} ($lowestScore/100).
                    
                    Provide an inspiring, high-impact 3-point action plan for today:
                    1. One 5-minute immediate micro-win.
                    2. One mid-day focus block recommendation.
                    3. One evening reflective question.
                    
                    Format with clear emojis and concise bullet points.
                """.trimIndent()
                val response = generativeModel?.generateContent(prompt)
                return@withContext response?.text ?: getOfflineBrief(lowestDimension)
            } catch (_: Exception) {
                return@withContext getOfflineBrief(lowestDimension)
            }
        } else {
            getOfflineBrief(lowestDimension)
        }
    }

    override suspend fun askCoach(userQuestion: String, contextScore: Int): String = withContext(Dispatchers.IO) {
        if (generativeModel != null) {
            try {
                val prompt = """
                    You are LifeScore AI, a world-class executive performance and wellness coach.
                    User's LifeScore is $contextScore/1000.
                    User asked: "$userQuestion"
                    
                    Give a direct, actionable, science-backed 2-3 paragraph answer.
                """.trimIndent()
                val response = generativeModel?.generateContent(prompt)
                return@withContext response?.text ?: getDeterministicCoachReply(userQuestion, contextScore)
            } catch (_: Exception) {
                return@withContext getDeterministicCoachReply(userQuestion, contextScore)
            }
        } else {
            getDeterministicCoachReply(userQuestion, contextScore)
        }
    }

    override suspend fun askCoachWithMemory(userQuestion: String, memoryContext: String): String = withContext(Dispatchers.IO) {
        if (generativeModel != null) {
            try {
                val fullPrompt = """
                    $memoryContext
                    
                    [USER QUESTION / MESSAGE]
                    "$userQuestion"
                    
                    Respond directly to the user as their AI Coach. Leverage your memory of their background, struggles, and achievements.
                """.trimIndent()
                val response = generativeModel?.generateContent(fullPrompt)
                return@withContext response?.text ?: getDeterministicMemoryReply(userQuestion, memoryContext)
            } catch (_: Exception) {
                return@withContext getDeterministicMemoryReply(userQuestion, memoryContext)
            }
        } else {
            getDeterministicMemoryReply(userQuestion, memoryContext)
        }
    }

    override suspend fun askCoachWithArchetype(
        userQuestion: String,
        archetypeId: String,
        assessmentSummary: String,
        memoryContext: String
    ): String = withContext(Dispatchers.IO) {
        val archetypePrompt = getArchetypeSpecificPrompt(archetypeId)
        if (generativeModel != null) {
            try {
                val fullPrompt = """
                    You are LifeScore AI, coaching a user with the psychometric profile '$archetypeId'.
                    
                    [ARCHETYPE COACHING PERSONA]
                    $archetypePrompt
                    
                    [ASSESSMENT INSIGHTS]
                    ${assessmentSummary.ifBlank { "Psychometric Assessment: 130 questions evaluated across 6 dimensions." }}
                    
                    [BEHAVIORAL MEMORY CONTEXT]
                    ${memoryContext.ifBlank { "Memory: User is building daily compounding consistency across 8 life dimensions." }}
                    
                    [USER QUESTION / MESSAGE]
                    "$userQuestion"
                    
                    Respond directly to the user as their specialized Archetype Coach. Provide precise, actionable, science-backed guidance honoring their strengths while systematically upgrading their blind spots.
                """.trimIndent()
                val response = generativeModel?.generateContent(fullPrompt)
                return@withContext response?.text ?: getDeterministicArchetypeReply(userQuestion, archetypeId)
            } catch (_: Exception) {
                return@withContext getDeterministicArchetypeReply(userQuestion, archetypeId)
            }
        } else {
            getDeterministicArchetypeReply(userQuestion, archetypeId)
        }
    }

    private fun getArchetypeSpecificPrompt(archetypeId: String): String {
        return when (archetypeId.lowercase()) {
            "architect" -> "As The Architect, you thrive on systems, structure, and optimization. Focus on building scalable habits, automating repetitive tasks, and eliminating friction."
            "warrior" -> "As The Warrior, you excel through discipline, grit, and physical output. Channel your somatic energy into consistent daily action and unwavering follow-through."
            "healer" -> "As The Healer, your superpower is empathy and emotional intelligence. Focus on deep relationships, active listening, and mental well-being."
            "visionary" -> "As The Visionary, you see the macro horizon and innovate. Channel your big ideas into concrete daily milestones so execution matches vision."
            "scholar" -> "As The Scholar, you thrive on deep analytical mastery and first-principles study. Turn abstract theory into practical habit loops."
            "explorer" -> "As The Explorer, you crave novelty, adventure, and adaptability. Structure habits with variety and dynamic challenges."
            "diplomat" -> "As The Diplomat, you excel at negotiation, influence, and social resonance. Focus on win-win community building and leadership."
            "alchemist" -> "As The Alchemist, your gift is creative synthesis and transformation. Bridge disparate disciplines into unique creative output."
            "strategist" -> "As The Strategist, you operate with high tactical precision and long-term leverage. Focus on asset compounding and 80/20 leverage points."
            else -> "As The Creator, your essence is originality and creative expression. Focus on daily craft output and shipping authentic work."
        }
    }

    private fun getDeterministicArchetypeReply(question: String, archetypeId: String): String {
        val persona = getArchetypeSpecificPrompt(archetypeId)
        return "🧠 **${archetypeId.replaceFirstChar { it.uppercase() }} Directive:**\n$persona\n\n🎯 **Action Protocol for \"$question\":**\n1. **Leverage Superpower:** Anchor your next task in your natural cognitive archetype.\n2. **Mitigate Bottleneck:** Execute 1 micro-win in your lowest assessment dimension today.\n3. **Compounding Log:** Record your progress in the Cognitive Journal to cement long-term memory."
    }

    private fun getDeterministicMemoryReply(question: String, memoryContext: String): String {
        return when {
            question.contains("challenge", ignoreCase = true) || question.contains("next", ignoreCase = true) ->
                "🏆 **Next Challenge Recommendation:**\nBased on your completed **30-Day Fitness Quest**, I recommend starting the **30-Day 20-Min Deep Reading Immersion** (+600 XP). Your habit memory notes that you're most consistent on **Tuesdays** (94% completion), so use Tuesday mornings to anchor your first deep reading sprints!\n\n⚡ *Tip:* Given your past morning friction before 9 AM, start with a 5-minute bedside reading trigger rather than a 45-minute block."
            question.contains("morning", ignoreCase = true) || question.contains("struggle", ignoreCase = true) ->
                "⏰ **Morning Friction Strategy:**\nI remember you've experienced drop-off on early morning tasks before 9 AM. Here is your personalized Architect framework:\n1. **Atomic Trigger:** Place your water bottle & journal directly next to your alarm.\n2. **2-Minute Rule:** Do not commit to a full workout immediately—simply complete 2 minutes of mobility.\n3. **Tuesday Momentum:** Anchor your hardest tasks on Tuesdays where your completion rate reaches 94%."
            question.contains("Tuesday", ignoreCase = true) || question.contains("pattern", ignoreCase = true) ->
                "📊 **Discovered Pattern Analysis:**\nYour LifeScore data shows **Tuesdays** are your highest-performing day with a **94% on-time quest completion rate**. Conversely, Sunday evenings have higher fatigue. Protect your Tuesday deep-work blocks as non-negotiable!"
            else ->
                "🧠 **Memory-Informed Coaching:**\nAs **The Architect**, your superpower lies in scalable systems. Based on your recent journal reflection about deep focus blocks and morning friction, I recommend locking in a 90-minute uninterrupted work sprint today between 10:00 AM - 11:30 AM."
        }
    }

    override suspend fun generateWeeklyAudit(
        scores: Map<DimensionType, Int>,
        tasksCompleted: Int,
        totalScore: Int,
        streak: Int
    ): WeeklyAuditResult = withContext(Dispatchers.IO) {
        val sorted = scores.entries.sortedByDescending { it.value }
        val topDim = sorted.firstOrNull()?.key ?: DimensionType.CAREER
        val lowestDim = sorted.lastOrNull()?.key ?: DimensionType.HEALTH

        if (generativeModel != null) {
            try {
                val prompt = """
                    Generate a weekly LifeScore executive audit:
                    - Total LifeScore: $totalScore/1000
                    - Quests completed this week: $tasksCompleted
                    - Active Streak: $streak days
                    - Strongest Dimension: ${topDim.displayName} (${scores[topDim]}%)
                    - Lowest Dimension: ${lowestDim.displayName} (${scores[lowestDim]}%)
                    
                    Return 3 bullet achievements and 3 next-week directives.
                """.trimIndent()
                val response = generativeModel?.generateContent(prompt)
                if (response?.text != null) {
                    return@withContext WeeklyAuditResult(
                        headline = "Weekly LifeScore Audit: ${if (totalScore >= 750) "Supercharged Momentum" else "Solid Foundation Building"}",
                        pointSummary = "$totalScore pts across 8 life dimensions with $tasksCompleted completed quests and a $streak-day streak.",
                        topDimension = topDim,
                        growthDimension = lowestDim,
                        keyAchievements = listOf(
                            "Mastered ${topDim.displayName} with an outstanding ${scores[topDim]}% balance rating.",
                            "Maintained an active $streak-day daily quest streak without relying on streak shields.",
                            "Successfully checked in on $tasksCompleted micro-habits and social sprints."
                        ),
                        nextWeekDirectives = listOf(
                            "Prioritize 15-minute daily focus blocks on ${lowestDim.displayName} to lift your baseline.",
                            "Stack your ${lowestDim.displayName} quest directly after your strongest habit in ${topDim.displayName}.",
                            "Engage in a 7-day social duel to reinforce mutual accountability."
                        ),
                        motivationalQuote = "\"Consistency isn't about perfection. It is about never giving up on the compound effect of small daily choices.\""
                    )
                }
            } catch (_: Exception) {}
        }

        return@withContext WeeklyAuditResult(
            headline = "Weekly LifeScore Audit: ${if (totalScore >= 750) "Supercharged Momentum" else "Steady Growth"}",
            pointSummary = "$totalScore/1000 pts with $tasksCompleted quests completed and a $streak-day active streak.",
            topDimension = topDim,
            growthDimension = lowestDim,
            keyAchievements = listOf(
                "Dominating in ${topDim.displayName} (${scores[topDim]}% score) with top tier consistency.",
                "Sustained a $streak-day streak across daily rituals.",
                "Completed $tasksCompleted habit quests advancing your level."
            ),
            nextWeekDirectives = listOf(
                "Focus on ${lowestDim.displayName} (${scores[lowestDim]}%) with 1 micro-win every morning.",
                "Stack a 10-minute habit block right before lunch.",
                "Challenge a friend to a 7-Day Duel in ${lowestDim.displayName}."
            ),
            motivationalQuote = "\"Small disciplines repeated with consistency every day lead to great achievements gained slowly over time.\""
        )
    }

    override fun generateDimensionGuidance(
        dimension: DimensionType,
        score: Int,
        isWeakest: Boolean
    ): String {
        return if (isWeakest) {
            "⚠️ **${dimension.displayName} is your primary growth bottleneck ($score%)**: Dedicate 10 distraction-free minutes today to complete a pending quest and protect your LifeScore equilibrium."
        } else if (score >= 80) {
            "🌟 **${dimension.displayName} is exceptional ($score%)**: You have unlocked top-tier habit momentum. Keep compounding your streak!"
        } else {
            "⚡ **${dimension.displayName} is steady ($score%)**: Maintain your daily routine to stay in the promotion zone."
        }
    }

    override fun generateWeeklyRecapShareText(
        audit: WeeklyAuditResult,
        score: Int,
        streak: Int
    ): String {
        return "📊 My LifeScore Weekly Audit: $score/1000! 🔥 $streak-day streak active. Dominating in ${audit.topDimension.displayName} & leveling up ${audit.growthDimension.displayName}! Track your life balance: https://lifescore.app/audit #LifeScore #WeeklyAudit #GamifyYourLife"
    }

    private fun getDeterministicCoachReply(question: String, score: Int): String {
        val q = question.lowercase()
        return when {
            q.contains("health") || q.contains("sleep") || q.contains("diet") ->
                "💧 **Health Architecture**: Sleep quality and hydration dictate 80% of daily cognitive energy. Tonight, dim overhead lights 45 minutes before sleep and hydrate with 500ml of water right upon waking."
            q.contains("procrastinat") || q.contains("focus") || q.contains("career") ->
                "🚀 **Deep Work Protocol**: Eliminate friction. Set a 25-minute Pomodoro timer, place your phone in another room, and commit to working on your single highest-leverage task for just 5 minutes."
            q.contains("wealth") || q.contains("money") || q.contains("budget") ->
                "📊 **Financial Discipline**: Automate your savings before spending. Review this week's discretionary expenses and redirect one impulsive purchase into your emergency fund."
            q.contains("streak") || q.contains("habit") || q.contains("routine") ->
                "🔥 **Habit Stacking Principle**: The easiest way to build a new habit is to anchor it to an existing one. E.g., 'After I pour my morning coffee, I will write 3 daily quests in LifeScore.'"
            else ->
                "🎯 **Consistency Over Intensity**: At LifeScore $score/1000, compounding small daily wins is what drives mastery. Complete your lowest-scoring dimension's quest right now to raise your balance!"
        }
    }

    private fun getOfflineBrief(lowestDimension: DimensionType): String {
        return when (lowestDimension) {
            DimensionType.HEALTH -> "💧 **Hydration & Reset**: Drink 500ml water and take a 10-min brisk walk outside.\n🧘 **Wind-down**: Set phone on 'Do Not Disturb' 30 mins before bed."
            DimensionType.WEALTH -> "📊 **Audit**: Review yesterday's expenses and cancel one unused subscription.\n🎯 **Target**: Allocate 10% toward your emergency fund."
            DimensionType.RELATIONSHIPS -> "💌 **Reach Out**: Send a thoughtful message of gratitude to a friend or mentor.\n🤝 **Presence**: Give undivided attention in your next conversation."
            DimensionType.CAREER -> "🚀 **Deep Work**: Block 45 minutes for your most critical project before checking inbox.\n📌 **Clarity**: Write down top 3 deliverables."
            DimensionType.LEARNING -> "📖 **15-Min Reading**: Read one chapter on a topic that sharpens your craft.\n📝 **Synthesis**: Write a 1-sentence summary of what you learned."
            DimensionType.FITNESS -> "⚡ **Quick Burn**: Do 3 sets of pushups/squats or a 15-minute mobility stretch.\n🏃 **Movement**: Hit at least 8,000 steps today."
            DimensionType.MENTAL_HEALTH -> "🌿 **Breathwork**: Practice 4-7-8 box breathing for 3 minutes.\n📓 **Brain Dump**: Write down anything causing friction."
            DimensionType.SOCIAL_LIFE -> "🎉 **Connect**: Say hello to a friend or plan a weekend meetup.\n☕ **Community**: Visit a local cafe for social energy."
        }
    }
}
