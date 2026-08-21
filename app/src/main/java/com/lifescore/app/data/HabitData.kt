package com.lifescore.app.data

import com.lifescore.app.domain.model.DimensionType

data class Habit(
    val id: Int,
    val title: String,
    val description: String,
    val dimension: DimensionType,
    val xpReward: Int = 15,
    val difficulty: String = "Easy", // Easy, Medium, Hard
    val estimatedMinutes: Int = 5,
    val isAdvanced: Boolean = false
)

object HabitData {
    val allHabits: List<Habit> = listOf(
        // ==========================================
        // DIMENSION 1: MENTAL HEALTH (1 - 19)
        // ==========================================
        Habit(1, "Practice Daily Gratitude", "Write down one thing you're thankful for each morning or night.", DimensionType.MENTAL_HEALTH, 20, "Easy", 3),
        Habit(2, "Reframe Negative Self-Talk", "Replace harsh self-criticism with a softer, more encouraging thought.", DimensionType.MENTAL_HEALTH, 25, "Medium", 5),
        Habit(3, "Forgive Yourself for a Mistake", "Acknowledge one slip-up today with self-compassion instead of guilt.", DimensionType.MENTAL_HEALTH, 20, "Easy", 2),
        Habit(4, "Morning Positive Affirmation", "Declare a grounding affirmation out loud before beginning your routine.", DimensionType.MENTAL_HEALTH, 15, "Easy", 1),
        Habit(5, "Accept What You Cannot Change", "Identify one uncontrollable situation and mentally release resistance to it.", DimensionType.MENTAL_HEALTH, 30, "Medium", 5),
        Habit(6, "Log Daily Key Lesson", "Write down one actionable insight or lesson discovered during the day.", DimensionType.MENTAL_HEALTH, 25, "Easy", 4),
        Habit(7, "Feel Feelings Without Judgment", "Sit with current emotions for 3 minutes without labeling them good or bad.", DimensionType.MENTAL_HEALTH, 25, "Medium", 5),
        Habit(8, "Ask for Help When Needed", "Reach out proactively when a task or emotional burden feels heavy.", DimensionType.MENTAL_HEALTH, 35, "Hard", 10),
        Habit(9, "Pause Before Responding When Upset", "Take three deep belly breaths before replying during high-friction moments.", DimensionType.MENTAL_HEALTH, 30, "Medium", 2),
        Habit(10, "Practice Patience in Difficulties", "Choose calm composure in a delayed or frustrating circumstance.", DimensionType.MENTAL_HEALTH, 25, "Medium", 3),
        Habit(11, "Celebrate Small Wins", "Acknowledge and praise yourself for completing a micro-task.", DimensionType.MENTAL_HEALTH, 15, "Easy", 1),
        Habit(12, "5 Minutes of Pure Stillness", "Sit quietly with no screens, audio, or distractions for 5 minutes.", DimensionType.MENTAL_HEALTH, 20, "Easy", 5),
        Habit(13, "Listen to Uplifting Music", "Play a calming or inspiring soundtrack to reset your nervous system.", DimensionType.MENTAL_HEALTH, 15, "Easy", 10),
        Habit(14, "One Joyful Daily Activity", "Dedicate time to an activity purely because it brings you happiness.", DimensionType.MENTAL_HEALTH, 25, "Easy", 15),
        Habit(15, "Laugh at Yourself Daily", "Find humor in a minor awkwardness or mistake without self-deprecation.", DimensionType.MENTAL_HEALTH, 15, "Easy", 1),
        Habit(16, "Cognitive Journal Brain Dump", "Write down what is bothering you on paper, then close the notebook.", DimensionType.MENTAL_HEALTH, 30, "Medium", 10),
        Habit(17, "Allow Emotional Release", "Give yourself full permission to cry and release stored physical tension.", DimensionType.MENTAL_HEALTH, 25, "Medium", 5),
        Habit(18, "Learn to Say 'No' Cleanly", "Politely decline a non-essential request that drains your core energy.", DimensionType.MENTAL_HEALTH, 35, "Hard", 3),
        Habit(19, "Curate Uplifting Company", "Spend intentional time interacting with positive, supportive peers.", DimensionType.MENTAL_HEALTH, 30, "Medium", 20),

        // ==========================================
        // DIMENSION 2: HEALTH & FITNESS (20 - 32)
        // ==========================================
        Habit(20, "Morning Hydration First", "Drink a 500ml glass of water immediately upon waking.", DimensionType.HEALTH, 15, "Easy", 1),
        Habit(21, "Greens or Fruits in Every Meal", "Add fresh produce or raw vegetables to your plate.", DimensionType.HEALTH, 20, "Easy", 5),
        Habit(22, "15-Minute Daily Walk", "Take a brisk walk outside to stimulate blood flow and circadian rhythm.", DimensionType.FITNESS, 25, "Easy", 15),
        Habit(23, "Morning or Evening Mobility Stretch", "Perform 5 minutes of spine and hamstring stretching.", DimensionType.FITNESS, 20, "Easy", 5),
        Habit(24, "Consistent Sleep Window", "Go to bed within the same 30-minute window every evening.", DimensionType.HEALTH, 30, "Medium", 1),
        Habit(25, "Avoid Late Night Heavy Meals", "Stop heavy eating at least 3 hours before going to sleep.", DimensionType.HEALTH, 25, "Medium", 1),
        Habit(26, "Mindful Slow Eating", "Chew thoroughly and avoid screens while eating your meal.", DimensionType.HEALTH, 20, "Easy", 15),
        Habit(27, "Move Body Every Hour", "Stand up and walk for 60 seconds after sitting for an hour.", DimensionType.FITNESS, 20, "Easy", 2),
        Habit(28, "Honest Rest When Fatigued", "Take a 15-minute power nap or non-sleep deep rest when exhausted.", DimensionType.HEALTH, 20, "Easy", 15),
        Habit(29, "One Unplugged Rest Day", "Designate a restful day free from intense work obligations.", DimensionType.HEALTH, 40, "Hard", 60),
        Habit(30, "Sneakers-On Activation Rule", "Put on workout shoes immediately when feeling low motivation.", DimensionType.FITNESS, 20, "Easy", 2),
        Habit(31, "7-9 Hours Quality Sleep", "Protect your sleep sanctuary for optimal cognitive recovery.", DimensionType.HEALTH, 35, "Medium", 480),
        Habit(32, "Screen Curfew (1st & Last Hour)", "Keep screens off during the first 30m and last 30m of the day.", DimensionType.HEALTH, 35, "Hard", 60),

        // ==========================================
        // DIMENSION 3: CAREER (33 - 48)
        // ==========================================
        Habit(33, "Consistent Wake-Up Alarm", "Rise at the same time every morning to set circadian anchor.", DimensionType.CAREER, 25, "Medium", 1),
        Habit(34, "Morning Gratitude Anchor", "Note one professional or life blessing before opening email.", DimensionType.CAREER, 15, "Easy", 2),
        Habit(35, "Zero Phone for First 30 Mins", "Keep phone in airplane mode for 30 minutes after waking.", DimensionType.CAREER, 30, "Medium", 30),
        Habit(36, "Read 1 Page Every Day", "Read at least one page of a domain-relevant book.", DimensionType.CAREER, 15, "Easy", 3),
        Habit(37, "Journal Strategic Thoughts", "Capture working ideas and insights in your professional journal.", DimensionType.CAREER, 20, "Easy", 5),
        Habit(38, "Night-Before Task Prep", "Write down tomorrow's core priorities before ending your day.", DimensionType.CAREER, 20, "Easy", 4),
        Habit(39, "Deconstruct Big Projects", "Break massive intimidating deliverables into 15-minute chunks.", DimensionType.CAREER, 25, "Medium", 10),
        Habit(40, "Pomodoro Sprint & Rest", "Work in 25-minute focused blocks followed by 5-minute recovery.", DimensionType.CAREER, 25, "Medium", 30),
        Habit(41, "Single-Tasking Discipline", "Focus entirely on one browser tab and one deliverable at a time.", DimensionType.CAREER, 30, "Medium", 25),
        Habit(42, "Clean Workspace Daily", "Clear physical desk and organize desktop files before shutdown.", DimensionType.CAREER, 20, "Easy", 5),
        Habit(43, "Top 3 Priorities Rule", "Identify the 3 most impactful tasks every morning.", DimensionType.CAREER, 25, "Easy", 3),
        Habit(44, "Stopwatch Deep Focus", "Set a countdown timer to eliminate task procrastination.", DimensionType.CAREER, 20, "Easy", 20),
        Habit(45, "Zero Social Media in Work Hours", "Block distracting feeds during designated core output hours.", DimensionType.CAREER, 35, "Hard", 120),
        Habit(46, "Complete Before Switching", "Finish one task completely before opening another thread.", DimensionType.CAREER, 30, "Medium", 20),
        Habit(47, "Daily Retrospective Review", "Review what worked and plan adjustments for tomorrow.", DimensionType.CAREER, 25, "Easy", 5),
        Habit(48, "Professional Skill Investment", "Spend 20 minutes learning an advanced industry capability.", DimensionType.CAREER, 35, "Medium", 20),

        // ==========================================
        // DIMENSION 4: WEALTH (49 - 58)
        // ==========================================
        Habit(49, "Weekly Spending Audit", "Review every financial transaction logged in the past 7 days.", DimensionType.WEALTH, 30, "Medium", 15),
        Habit(50, "Automate Savings First", "Set auto-transfer to savings on paycheck arrival before spending.", DimensionType.WEALTH, 35, "Medium", 5),
        Habit(51, "48-Hour Purchase Cooldown", "Wait 48 hours before buying any non-essential item.", DimensionType.WEALTH, 25, "Medium", 2),
        Habit(52, "Cancel Unused Subscriptions", "Audit recurring bank charges and cancel 1 unused service.", DimensionType.WEALTH, 30, "Easy", 10),
        Habit(53, "Track Monthly Net Worth", "Calculate assets minus liabilities at the end of each month.", DimensionType.WEALTH, 35, "Medium", 15),
        Habit(54, "Follow Spending Budget Cap", "Stay strictly within pre-allocated category budget envelopes.", DimensionType.WEALTH, 30, "Medium", 5),
        Habit(55, "Personal Finance Study", "Read 1 financial primer article or listen to an economics podcast.", DimensionType.WEALTH, 25, "Easy", 15),
        Habit(56, "Resist Lifestyle Inflation", "Keep baseline cost of living stable when income increases.", DimensionType.WEALTH, 40, "Hard", 10),
        Habit(57, "Build 3-Month Emergency Fund", "Direct percentage of savings into guaranteed liquid reserves.", DimensionType.WEALTH, 40, "Hard", 10),
        Habit(58, "Pay High-Interest Debt First", "Direct surplus cashflow to highest APR debts.", DimensionType.WEALTH, 45, "Hard", 10),

        // ==========================================
        // DIMENSION 5: RELATIONSHIPS & SOCIAL (59 - 80)
        // ==========================================
        Habit(59, "Honest but Kind Communication", "Speak truth with gentle consideration for the other person.", DimensionType.RELATIONSHIPS, 25, "Medium", 5),
        Habit(60, "Respect Others When in Power", "Treat junior members and service staff with utmost dignity.", DimensionType.RELATIONSHIPS, 20, "Easy", 5),
        Habit(61, "Generous Daily Acts", "Give your time, presence, or assistance without expecting return.", DimensionType.RELATIONSHIPS, 25, "Easy", 10),
        Habit(62, "Spot the Good in People", "Actively look for and acknowledge strengths in others.", DimensionType.RELATIONSHIPS, 15, "Easy", 2),
        Habit(63, "Sincere Mistake Apology", "Apologize quickly and sincerely without defensive excuses.", DimensionType.RELATIONSHIPS, 35, "Hard", 5),
        Habit(64, "100% Word Integrity", "Follow through on all small and large verbal promises.", DimensionType.RELATIONSHIPS, 30, "Medium", 5),
        Habit(65, "Zero Gossip Policy", "Refrain from speaking negatively about people who are not present.", DimensionType.RELATIONSHIPS, 30, "Medium", 5),
        Habit(66, "Punctual Time Respect", "Arrive 2 minutes early for scheduled meetings and calls.", DimensionType.SOCIAL_LIFE, 20, "Easy", 2),
        Habit(67, "Respect Personal Boundaries", "Honor others' physical, emotional, and schedule limits.", DimensionType.RELATIONSHIPS, 25, "Medium", 5),
        Habit(68, "Empathetic Perspective Taking", "Consider how the situation feels from the other person's shoes.", DimensionType.RELATIONSHIPS, 25, "Medium", 5),
        Habit(69, "Support in Micro-Ways", "Send a brief check-in text or offer practical assistance.", DimensionType.RELATIONSHIPS, 20, "Easy", 3),
        Habit(70, "Suspend Harsh Judgments", "Give people the benefit of the doubt during misunderstandings.", DimensionType.RELATIONSHIPS, 25, "Medium", 3),
        Habit(71, "Uplift and Encourage Others", "Send a word of encouragement to someone facing a hurdle.", DimensionType.SOCIAL_LIFE, 20, "Easy", 3),
        Habit(72, "Listen Without Interrupting", "Let the other person finish completely before responding.", DimensionType.RELATIONSHIPS, 30, "Medium", 10),
        Habit(73, "Weekly Family or Friend Call", "Call a loved one for a genuine catch-up conversation.", DimensionType.RELATIONSHIPS, 25, "Easy", 15),
        Habit(74, "Ask About Feelings, Not Just Day", "Ask deeper questions like 'How are you feeling about everything?'", DimensionType.RELATIONSHIPS, 25, "Medium", 5),
        Habit(75, "First to Greet and Smile", "Make warm eye contact and greet colleagues or neighbors first.", DimensionType.SOCIAL_LIFE, 15, "Easy", 1),
        Habit(76, "Send Sincere Thank-You Notes", "Write a brief appreciation message to a colleague or friend.", DimensionType.SOCIAL_LIFE, 20, "Easy", 4),
        Habit(77, "Embrace Universal Acceptance", "Welcome diverse viewpoints and personalities with an open mind.", DimensionType.RELATIONSHIPS, 25, "Medium", 5),
        Habit(78, "Avoid Spoilers in Conversation", "Let others share their stories without finishing their sentences.", DimensionType.SOCIAL_LIFE, 15, "Easy", 3),
        Habit(79, "Say 'Thank You' Generously", "Express genuine gratitude for everyday acts of service.", DimensionType.SOCIAL_LIFE, 15, "Easy", 1),
        Habit(80, "Make Space for Quiet Voices", "Invite quiet team or family members to voice their perspectives.", DimensionType.RELATIONSHIPS, 25, "Medium", 5),

        // ==========================================
        // DIMENSION 6: PURPOSE & MINDFULNESS (81 - 92)
        // ==========================================
        Habit(81, "Identify Personal North Star", "Reflect on your life purpose and unifying guiding vision.", DimensionType.MENTAL_HEALTH, 35, "Medium", 10),
        Habit(82, "5 Minutes Daily Prayer/Meditation", "Practice quiet prayer or mindfulness meditation.", DimensionType.MENTAL_HEALTH, 20, "Easy", 5),
        Habit(83, "Embody One Core Value Daily", "Pick one character virtue (e.g., courage) to live out today.", DimensionType.MENTAL_HEALTH, 20, "Easy", 2),
        Habit(84, "Read Soul-Uplifting Wisdom", "Read 1 page of timeless philosophy, poetry, or sacred text.", DimensionType.LEARNING, 20, "Easy", 5),
        Habit(85, "Evening Gratitude Closing", "Record 3 specific blessings before turning off the lights.", DimensionType.MENTAL_HEALTH, 20, "Easy", 4),
        Habit(86, "Visualize Ideal Self", "Spend 2 minutes imagining the character you are growing into.", DimensionType.MENTAL_HEALTH, 20, "Easy", 3),
        Habit(87, "Release Internal Grudges", "Mentally unburden yourself from past resentments.", DimensionType.MENTAL_HEALTH, 35, "Hard", 5),
        Habit(88, "Speak Softly in Conflict", "Lower your tone and pace when conversations become tense.", DimensionType.MENTAL_HEALTH, 30, "Medium", 3),
        Habit(89, "Community Contribution", "Give time or energy to support a local cause or group.", DimensionType.SOCIAL_LIFE, 35, "Medium", 30),
        Habit(90, "Declutter & Donate Surplus", "Gather unused clothes or items to donate to charity.", DimensionType.SOCIAL_LIFE, 30, "Easy", 20),
        Habit(91, "Appreciate Current Abundance", "Pause to recognize the sufficiency of what you have right now.", DimensionType.MENTAL_HEALTH, 20, "Easy", 3),
        Habit(92, "Mindfulness in Mundane Tasks", "Be 100% present while washing dishes, walking, or showering.", DimensionType.MENTAL_HEALTH, 20, "Easy", 5),

        // ==========================================
        // DIMENSION 7: LEARNING & WISDOM (93 - 100)
        // ==========================================
        Habit(93, "Learn Something New Every Day", "Acquire 1 new fact, concept, or technical skill.", DimensionType.LEARNING, 20, "Easy", 10),
        Habit(94, "Embrace the Present Moment", "Anchor your attention completely into the current second.", DimensionType.LEARNING, 20, "Easy", 3),
        Habit(95, "Release Historical Hurts", "Choose not to re-live past pains in today's mental space.", DimensionType.LEARNING, 35, "Hard", 5),
        Habit(96, "Never Give Up on Trust", "Maintain an open, discerning, yet hopeful heart with others.", DimensionType.LEARNING, 30, "Medium", 5),
        Habit(97, "Normalize Feelings of Overwhelm", "Recognize that feeling overwhelmed is normal and temporary.", DimensionType.LEARNING, 25, "Medium", 3),
        Habit(98, "Walk Away from Toxic Friction", "Step away calmly from unproductive, draining arguments.", DimensionType.LEARNING, 30, "Medium", 2),
        Habit(99, "Embrace Constructive Idleness", "Allow your brain unstructured daydreaming time without guilt.", DimensionType.LEARNING, 20, "Easy", 10),
        Habit(100, "Remember: Your Own Grass is Green", "Focus joy on watering your own garden instead of comparison.", DimensionType.LEARNING, 25, "Easy", 3)
    )

    fun getHabitsByDimension(dimension: DimensionType): List<Habit> {
        return allHabits.filter { it.dimension == dimension }
    }

    fun getHabitsByDifficulty(difficulty: String): List<Habit> {
        return allHabits.filter { it.difficulty.equals(difficulty, ignoreCase = true) }
    }

    fun getHabitById(id: Int): Habit? {
        return allHabits.find { it.id == id }
    }

    fun getDailyQuestHabits(dimension: DimensionType? = null, count: Int = 5): List<Habit> {
        val pool = if (dimension != null) getHabitsByDimension(dimension) else allHabits
        return pool.shuffled().take(count)
    }
}
