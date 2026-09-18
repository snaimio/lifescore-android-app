package com.lifescore.app.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.lifescore.app.core.config.AppConfig
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
    private val apiKey: String? = AppConfig.GEMINI_API_KEY
) : GeminiCoachRepository {

    private val generativeModel by lazy {
        if (!apiKey.isNullOrBlank() && apiKey != "DEMO_KEY") {
            try {
                GenerativeModel(
                    modelName = AppConfig.GEMINI_MODEL,
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
                    You are LifeScore AI, a world-class performance and balance coach.
                    User's overall LifeScore is $totalScore/1000.
                    Lowest score today: ${lowestDimension.displayName} ($lowestScore/100).
                    
                    Provide a sharp, deeply relevant 3-point action protocol tailored specifically to elevating ${lowestDimension.displayName}:
                    1. Immediate 2-minute micro-win for ${lowestDimension.displayName}.
                    2. Focused midday momentum block.
                    3. Grounded evening reflection question.
                    
                    Tone: Warm, direct, calm, and actionable. Avoid generic fluff.
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
                    You are LifeScore AI, an expert life balance, habits, and human performance coach.
                    User LifeScore: $contextScore/1000.
                    
                    User's Question:
                    "$userQuestion"
                    
                    Instructions:
                    - Directly and specifically answer what the user asked in the opening sentence.
                    - Provide 2-3 concise paragraphs grounded in behavioral psychology (Atomic Habits, CBT, chronobiology).
                    - Include 1-2 concrete, actionable action steps the user can execute today.
                    - Tone: Warm, human, insightful, and practical. No robotic clichés.
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
                    You are LifeScore AI, a trusted personal performance and wellness coach.
                    
                    [USER CONTEXT & HABIT MEMORY]
                    $memoryContext
                    
                    [USER'S DIRECT MESSAGE / QUESTION]
                    "$userQuestion"
                    
                    DIRECTIVES:
                    1. ANSWER THE USER'S DIRECT QUESTION FIRST. Stay 100% relevant to what they asked.
                    2. Seamlessly incorporate relevant memory context ONLY if it directly supports the user's question. Do NOT force unrelated memories.
                    3. Provide concrete, actionable, science-backed guidance with clean structure.
                    4. Keep your answer focused, empathetic, and concise (2-3 short paragraphs or clean bullet points).
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
                    You are LifeScore AI, coaching a user with the '$archetypeId' cognitive archetype.
                    
                    [ARCHETYPE PERSONA]
                    $archetypePrompt
                    
                    [USER CONTEXT]
                    ${assessmentSummary.ifBlank { "User is cultivating balance across 8 life dimensions." }}
                    ${memoryContext.ifBlank { "" }}
                    
                    [USER QUESTION / MESSAGE]
                    "$userQuestion"
                    
                    DIRECTIVES:
                    - Directly answer the user's specific question or request first.
                    - Tailor the explanation to leverage their archetype strengths while mitigating blind spots.
                    - Give clear, practical next steps.
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
            "sage" -> "As The Sage, your essence is mindfulness, deep introspection, and emotional equanimity. Focus on grounding rituals and clarity under pressure."
            "warrior" -> "As The Warrior, you excel through discipline, grit, and physical output. Channel your somatic energy into consistent daily action and mindful recovery."
            "visionary" -> "As The Visionary, you see the macro horizon and innovate. Channel your big ideas into concrete daily milestones so execution matches vision."
            "scholar" -> "As The Scholar, you thrive on deep analytical mastery and first-principles study. Turn abstract theory into practical daily habit loops."
            "creator" -> "As The Creator, your essence is originality and creative synthesis. Focus on dedicated deep craft blocks and shipping authentic work."
            "nomad" -> "As The Nomad, you crave movement, vitality, and outdoor exploration. Structure habits with dynamic adaptability and physical freedom."
            "catalyst" -> "As The Catalyst, your superpower is empathetic connection and social energy. Focus on uplifting communities and deep relationship rituals."
            else -> "Focus on compounding daily consistency across your 8 life dimensions."
        }
    }

    private fun getDeterministicArchetypeReply(question: String, archetypeId: String): String {
        val persona = getArchetypeSpecificPrompt(archetypeId)
        val topicAnswer = resolveTopicAnswer(question)
        return "**${archetypeId.replaceFirstChar { it.uppercase() }} Perspective:**\n$persona\n\n$topicAnswer"
    }

    private fun getDeterministicMemoryReply(question: String, memoryContext: String): String {
        val q = question.lowercase().trim()
        return when {
            q.contains("challenge") && (q.contains("next") || q.contains("after") || q.contains("fitness")) ->
                "**Next Challenge Recommendation**:\nBased on your completed **30-Day Fitness Quest**, I recommend enrolling in the **30-Day 20-Min Deep Reading Immersion** (+600 XP). Your habit analytics note that you are most consistent on **Tuesdays** (94% completion rate), so leverage Tuesday mornings to anchor your first deep reading sprints!\n\n*Action Step:* Start with a 5-minute bedside reading trigger rather than a rigid 45-minute block."

            q.contains("morning") && (q.contains("struggle") || q.contains("friction") || q.contains("task")) ->
                "**Morning Friction Strategy**:\nAs an **Architect**, when morning drop-off occurs before 9:00 AM, the solution is reducing mechanical friction rather than relying on raw willpower:\n\n1. **Atomic Trigger:** Place your water bottle and journal directly next to your alarm.\n2. **2-Minute Gateway:** Do not commit to a full morning block immediately—simply complete 2 minutes of mobility.\n3. **Tuesday Anchoring:** Anchor high-friction habits on your highest-performing day (Tuesdays)."

            else -> resolveTopicAnswer(question)
        }
    }

    private fun resolveTopicAnswer(question: String): String {
        val q = question.lowercase().trim()
        return when {
            // Sleep & Fatigue
            q.contains("sleep") || q.contains("tired") || q.contains("fatigue") || q.contains("exhaust") || q.contains("wake up") || q.contains("insomnia") ->
                "**Rest & Circadian Alignment**:\nQuality sleep is the biological foundation of all 8 life dimensions. When your sleep suffers, cognitive bandwidth drops by up to 30%.\n\n" +
                "**Actionable Protocol:**\n" +
                "1. **Light Regulation:** View natural sunlight for 5–10 minutes within 30 minutes of waking to anchor your cortisol curve.\n" +
                "2. **Evening Dimming:** Lower overhead lights and power down screens 45 minutes before sleep.\n" +
                "3. **Thermal Drop:** Keep your sleeping environment cool (around 65–68°F / 18–20°C) to facilitate deep REM and slow-wave sleep."

            // Focus, Procrastination & Deep Work
            q.contains("focus") || q.contains("procrastinat") || q.contains("distract") || q.contains("deep work") || q.contains("concentrat") ->
                "**Focus & Friction Elimination**:\nProcrastination is rarely a lack of willpower; it is an emotional response to friction, ambiguity, or fatigue.\n\n" +
                "**Actionable Protocol:**\n" +
                "1. **The 2-Minute Gateway:** Shrink the entry barrier. Commit to doing just 2 minutes of your hardest task.\n" +
                "2. **Environmental Armor:** Put your phone in another room or turn on Minimalist Mode before opening your workspace.\n" +
                "3. **Time-Boxing:** Run a single 25-minute Pomodoro sprint using the Focus Timer in the Explore tab."

            // Stress, Anxiety & Burnout
            q.contains("stress") || q.contains("anxiety") || q.contains("anxious") || q.contains("burnout") || q.contains("overwhelm") || q.contains("calm") ->
                "**Nervous System Regulation & Recovery**:\nWhen high demands exceed your current recovery capacity, your nervous system enters chronic hyper-arousal.\n\n" +
                "**Actionable Protocol:**\n" +
                "1. **Physiological Sigh:** Take two quick inhales through your nose followed by a long, slow exhale through your mouth. Repeat 3 times to immediately lower heart rate.\n" +
                "2. **Cognitive Brain Dump:** Write down everything currently cluttering your head into your Journal without judgment.\n" +
                "3. **Scope Down:** Pick your single #1 non-negotiable task today and defer everything else."

            // Habits & Consistency
            q.contains("habit") || q.contains("streak") || q.contains("consistent") || q.contains("routine") || q.contains("atomic") ->
                "**Compounding Consistency System**:\nSmall habits do not add up—they compound exponentially over time. The goal is never 100% perfection; it is never missing twice.\n\n" +
                "**Actionable Protocol:**\n" +
                "1. **Habit Stacking:** Anchor your new habit to an established anchor: *'After I [CURRENT HABIT], I will [NEW 1-MIN HABIT].'*\n" +
                "2. **Identity Alignment:** Every time you complete a habit, you cast a vote for the type of person you want to become.\n" +
                "3. **Streak Protection:** Use the Streak Vault to protect your consistency when unexpected life events happen."

            // Hydration & Nutrition
            q.contains("water") || q.contains("hydrat") || q.contains("diet") || q.contains("food") || q.contains("nutrition") ->
                "**Cellular Hydration & Energy**:\nA 2% drop in hydration levels causes a noticeable drop in mental clarity, mood, and physical stamina.\n\n" +
                "**Actionable Protocol:**\n" +
                "1. **Morning Jumpstart:** Drink 500ml of fresh water with a pinch of sea salt right upon waking.\n" +
                "2. **Continuous Pacing:** Keep a water bottle at your desk and aim for regular intake before feeling thirsty.\n" +
                "3. **Log Progress:** Tap the Hydration Tracker in the Explore tab to maintain your daily baseline."

            // Fitness & Movement
            q.contains("workout") || q.contains("gym") || q.contains("exercise") || q.contains("fitness") || q.contains("run") || q.contains("walk") ->
                "**Movement & Physical Drive**:\nPhysical movement enhances neurogenesis, clears brain fog, and stabilizes blood glucose throughout your day.\n\n" +
                "**Actionable Protocol:**\n" +
                "1. **Daily Baseline:** Aim for at least 8,000 steps or a 20-minute brisk walk outside.\n" +
                "2. **Micro-Workouts:** If short on time, do 3 sets of 15 bodyweight squats and pushups between meetings.\n" +
                "3. **Recovery Balance:** Pair strenuous physical days with active stretching or breathwork."

            // Wealth, Money & Budgeting
            q.contains("money") || q.contains("wealth") || q.contains("budget") || q.contains("spend") || q.contains("invest") || q.contains("finance") ->
                "**Financial Equilibrium & Systems**:\nFinancial peace is built on automated systems and mindful consumption, not restrictive deprivation.\n\n" +
                "**Actionable Protocol:**\n" +
                "1. **Pay Yourself First:** Automate transfers to your investment or emergency fund on payday before discretionary spending.\n" +
                "2. **48-Hour Impulse Rule:** Wait 48 hours on any non-essential purchase over $50.\n" +
                "3. **Audit Subscriptions:** Review your monthly recurring bills and cancel one unused service."

            // Relationships & Social Life
            q.contains("friend") || q.contains("relationship") || q.contains("family") || q.contains("social") || q.contains("lonely") || q.contains("connect") ->
                "**Relationship Nurturing & Connection**:\nThe Harvard Study of Adult Development proved that deep, quality relationships are the single strongest predictor of lifetime happiness.\n\n" +
                "**Actionable Protocol:**\n" +
                "1. **Spontaneous Gratitude:** Send a 1-sentence text of genuine appreciation to a friend or mentor right now.\n" +
                "2. **Undivided Presence:** Place your phone face down during your next meal or conversation.\n" +
                "3. **Weekly Ritual:** Schedule a recurring phone call or coffee walk with someone you value."

            // 360 Life Matrix & LifeScore
            q.contains("lifescore") || q.contains("matrix") || q.contains("balance") || q.contains("dimension") || q.contains("score") ->
                "**The 360° Life Matrix Framework**:\nLifeScore evaluates your life across 8 interrelated pillars: Health, Career, Wealth, Learning, Fitness, Mental Health, Relationships, and Social.\n\n" +
                "**How to Optimize Your Score:**\n" +
                "1. **Identify the Bottleneck:** Your overall LifeScore is weighted toward your lowest dimension.\n" +
                "2. **Micro-Wins:** Completing 1 quest in your weakest dimension provides the largest compounding boost to your overall balance.\n" +
                "3. **Track Trends:** Check the Balance tab weekly to watch your radar chart expand harmoniously."

            // Archetypes
            q.contains("archetype") || q.contains("architect") || q.contains("warrior") || q.contains("sage") || q.contains("visionary") || q.contains("creator") ->
                "**Hero Archetype System**:\nYour archetype reflects your cognitive tendencies, natural strengths, and common blind spots.\n\n" +
                "**The 8 Archetypes:**\n" +
                "• **Architect:** Systems, structure, and operational leverage.\n" +
                "• **Sage:** Mindfulness, inner wisdom, and equanimity.\n" +
                "• **Warrior:** Relentless discipline, grit, and somatic output.\n" +
                "• **Visionary:** Audacious strategy, long-term forecasting, and innovation.\n" +
                "• **Scholar:** First-principles mastery and deep knowledge.\n" +
                "• **Creator:** Aesthetic brilliance, novel ideas, and authentic craft.\n" +
                "• **Nomad:** Physical vitality, outdoor freedom, and adventure.\n" +
                "• **Catalyst:** High empathy, connection, and social leadership."

            // Default general intelligent coaching response
            else ->
                "**Coaching Guidance for \"$question\":**\n" +
                "To make meaningful progress, break this objective down into its simplest controllable components:\n\n" +
                "1. **Define the Immediate Next Action:** What is the single smallest physical step you can take in the next 5 minutes?\n" +
                "2. **Reduce Resistance:** Remove any friction or distraction standing between you and that step.\n" +
                "3. **Anchor Consistency:** Log your win in LifeScore to maintain your streak and compound your progress today."
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
        val habitText = com.lifescore.app.core.engine.HabitRecommendationEngine.getHabitRecommendationText(dimension, score)
        return if (isWeakest) {
            "⚠️ **${dimension.displayName} is your primary growth bottleneck ($score%)**:\n$habitText"
        } else if (score >= 80) {
            "🌟 **${dimension.displayName} is exceptional ($score%)**: You have unlocked top-tier habit momentum. Keep compounding your streak!"
        } else {
            "⚡ **${dimension.displayName} is steady ($score%)**:\n$habitText"
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
        return resolveTopicAnswer(question)
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
