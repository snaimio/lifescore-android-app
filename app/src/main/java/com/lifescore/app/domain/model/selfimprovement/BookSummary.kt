@file:Suppress("SpellCheckingInspection")
package com.lifescore.app.domain.model.selfimprovement

import com.lifescore.app.domain.model.DimensionType

data class KeyTakeaway(
    val index: Int,
    val title: String,
    val summary: String,
    val actionStep: String
)

data class BookSummary(
    val id: String,
    val title: String,
    val author: String,
    val coverEmoji: String,
    val dimension: DimensionType,
    val readingTimeMinutes: Int = 12,
    val rating: Double = 4.9,
    val coreThesis: String,
    val summaryOverview: String,
    val keyTakeaways: List<KeyTakeaway>,
    val memorableQuotes: List<String>,
    val actionableLifeScoreQuest: String,
    val questXpReward: Int = 75
)

object BookSummariesCatalog {
    val books: List<BookSummary> = listOf(
        BookSummary(
            id = "atomic_habits",
            title = "Atomic Habits",
            author = "James Clear",
            coverEmoji = "⚡",
            dimension = DimensionType.HEALTH,
            readingTimeMinutes = 12,
            rating = 4.95,
            coreThesis = "Changes that seem small and unimportant at first will compound into remarkable results if you are willing to stick with them for years.",
            summaryOverview = "Atomic Habits is the definitive guide on how tiny changes can lead to remarkable results. James Clear reveals how real change comes from the compound effect of hundreds of small decisions—doing two push-ups a day, waking up five minutes earlier, or reading a single page.",
            keyTakeaways = listOf(
                KeyTakeaway(
                    1,
                    "The Aggregation of Marginal Gains",
                    "Improving by just 1% each day results in being 37 times better by the end of a single year.",
                    "Identify one habit today and make it 1% easier or more consistent."
                ),
                KeyTakeaway(
                    2,
                    "Identity-Based Habits",
                    "True behavior change is identity change. You start a habit because of motivation, but you stick with it because it becomes who you are.",
                    "Ask yourself: 'What would a healthy/productive person do right now?'"
                ),
                KeyTakeaway(
                    3,
                    "The Four Laws of Behavior Change",
                    "To build a habit, make it Obvious, Attractive, Easy, and Satisfying. To break one, invert the laws.",
                    "Use Habit Stacking: 'After I [CURRENT HABIT], I will [NEW HABIT].'"
                ),
                KeyTakeaway(
                    4,
                    "Never Miss Twice",
                    "Missing once is an accident. Missing twice is the start of a new, negative habit.",
                    "If you slip today, do a miniature 2-minute version tomorrow no matter what."
                )
            ),
            memorableQuotes = listOf(
                "You do not rise to the level of your goals. You fall to the level of your systems.",
                "Every action you take is a vote for the type of person you wish to become."
            ),
            actionableLifeScoreQuest = "Execute a 2-Minute Habit Stack right now and log it for +75 XP."
        ),
        BookSummary(
            id = "deep_work",
            title = "Deep Work",
            author = "Cal Newport",
            coverEmoji = "🧠",
            dimension = DimensionType.CAREER,
            readingTimeMinutes = 14,
            rating = 4.9,
            coreThesis = "The ability to perform deep work is becoming increasingly rare at exactly the same time it is becoming increasingly valuable in our economy.",
            summaryOverview = "Deep Work is the ability to focus without distraction on a cognitively demanding task. It creates new value, improves skill, and is hard to replicate. Cal Newport argues that multitasking and shallow work are eroding our capacity for meaningful achievement.",
            keyTakeaways = listOf(
                KeyTakeaway(
                    1,
                    "Deep Work vs Shallow Work",
                    "Shallow work (emails, chats, notifications) keeps you busy but doesn't create lasting value. Deep work moves the needle.",
                    "Schedule a protected 90-minute deep work block on your calendar tomorrow morning."
                ),
                KeyTakeaway(
                    2,
                    "Embrace Boredom",
                    "Constantly switching to your phone at every moment of boredom trains your brain to crave constant novelty.",
                    "Practice waiting in line without checking your smartphone."
                ),
                KeyTakeaway(
                    3,
                    "Quit Social Media / Digital Minimalism",
                    "Treat network tools with the craftsman approach: only use tools that offer positive substantial benefits.",
                    "Perform a digital audit and remove apps that fragment your attention span."
                ),
                KeyTakeaway(
                    4,
                    "Execute Like a Business (4DX)",
                    "Focus on the wildly important, act on lead measures, keep a compelling scoreboard, and create cadence of accountability.",
                    "Track deep work hours on a physical or digital LifeScore scoreboard."
                )
            ),
            memorableQuotes = listOf(
                "If you don't produce, you won't thrive—no matter how skilled or talented you are.",
                "Clarity about what matters provides clarity about what does not."
            ),
            actionableLifeScoreQuest = "Complete a 25-minute uninterrupted Deep Focus timer session (+75 XP)."
        ),
        BookSummary(
            id = "psychology_of_money",
            title = "The Psychology of Money",
            author = "Morgan Housel",
            coverEmoji = "💰",
            dimension = DimensionType.WEALTH,
            readingTimeMinutes = 11,
            rating = 4.92,
            coreThesis = "Doing well with money has a little to do with how smart you are and a lot to do with how you behave.",
            summaryOverview = "Financial success is not a hard science. It's a soft skill, where how you behave is more important than what you know. Morgan Housel shares 19 short stories exploring the strange ways people think about wealth, greed, risk, and happiness.",
            keyTakeaways = listOf(
                KeyTakeaway(
                    1,
                    "Wealth Is What You Don't See",
                    "Spending money to show people how much money you have is the fastest way to have less money.",
                    "Define your true financial freedom goal instead of outward status markers."
                ),
                KeyTakeaway(
                    2,
                    "The Power of Compounding & Time",
                    "More than 99% of Warren Buffett's wealth was accumulated after his 50th birthday because of endurance.",
                    "Automate your monthly index fund investment and never interrupt compounding unnecessarily."
                ),
                KeyTakeaway(
                    3,
                    "Freedom is the Highest Dividend",
                    "The ability to do what you want, when you want, with who you want, for as long as you want, is the highest ROI in life.",
                    "Build a 6-month peace-of-mind emergency cushion."
                ),
                KeyTakeaway(
                    4,
                    "Room for Error",
                    "You must plan on your plan not going according to plan. Margin of safety keeps you in the game.",
                    "Stress-test your budget with conservative return assumptions."
                )
            ),
            memorableQuotes = listOf(
                "Spending money to show people how much money you have is the fastest way to have less money.",
                "The highest form of wealth is the ability to wake up every morning and say, 'I can do whatever I want today.'"
            ),
            actionableLifeScoreQuest = "Log a financial audit and allocate money to your emergency fund (+75 XP)."
        ),
        BookSummary(
            id = "why_we_sleep",
            title = "Why We Sleep",
            author = "Matthew Walker, PhD",
            coverEmoji = "🛌",
            dimension = DimensionType.HEALTH,
            readingTimeMinutes = 13,
            rating = 4.88,
            coreThesis = "Sleep is the single most effective thing we can do to reset our brain and body health each day.",
            summaryOverview = "Neuroscientist Matthew Walker illuminates how sleep impacts every single biological system in human physiology. From memory consolidation and emotional regulation to cardiovascular health and immune resilience, sleep is our supreme biological superpower.",
            keyTakeaways = listOf(
                KeyTakeaway(
                    1,
                    "The 8-Hour Biological Requirement",
                    "Routinely sleeping less than six or seven hours a night demolishes your immune system and doubles cancer risk.",
                    "Set a non-negotiable 8-hour sleep opportunity window tonight."
                ),
                KeyTakeaway(
                    2,
                    "REM Sleep: Overnight Therapy",
                    "During REM sleep, the brain processes emotional memories and strips away the painful emotional charge.",
                    "Avoid late-night alcohol which blocks restorative REM sleep phases."
                ),
                KeyTakeaway(
                    3,
                    "Circadian Rhythm & Light Hygiene",
                    "Morning sunlight sets your biological master clock, while evening blue light delays melatonin secretion.",
                    "Get 10 minutes of morning sunlight outside within 30 minutes of waking."
                ),
                KeyTakeaway(
                    4,
                    "Temperature & Sleep Architecture",
                    "Your core body temperature must drop by 2-3°F to initiate and maintain deep restorative sleep.",
                    "Keep your bedroom cool (around 65-68°F / 18-20°C)."
                )
            ),
            memorableQuotes = listOf(
                "Sleep is the greatest legal performance-enhancing drug that most people are neglecting.",
                "The shorter your sleep, the shorter your lifespan."
            ),
            actionableLifeScoreQuest = "Commit to a consistent wind-down routine 1 hour before bed tonight (+75 XP)."
        ),
        BookSummary(
            id = "cant_hurt_me",
            title = "Can't Hurt Me",
            author = "David Goggins",
            coverEmoji = "🔥",
            dimension = DimensionType.MENTAL_HEALTH,
            readingTimeMinutes = 15,
            rating = 4.96,
            coreThesis = "Most people tap out when they have only reached 40% of their actual capabilities. The mind controls everything.",
            summaryOverview = "David Goggins shares his astonishing life story of overcoming severe childhood trauma, poverty, and obesity to become a Navy SEAL and ultra-endurance athlete. He outlines mental hardness principles that turn suffering into supreme mental armor.",
            keyTakeaways = listOf(
                KeyTakeaway(
                    1,
                    "The Accountability Mirror",
                    "Face your true self in the mirror with brutal honesty and write down your weaknesses and responsibilities.",
                    "Write down three uncomfortable truths you've been avoiding and face them."
                ),
                KeyTakeaway(
                    2,
                    "The 40% Rule",
                    "When your brain tells you that you are completely exhausted and done, you are only at 40% of your true reserve.",
                    "Push past your mental barrier when doing your next physical workout."
                ),
                KeyTakeaway(
                    3,
                    "The Cookie Jar Method",
                    "Store all your past victories, moments where you overcame odds, into a mental cookie jar to feed on during dark moments.",
                    "Recall one time you conquered immense difficulty and harness that pride."
                ),
                KeyTakeaway(
                    4,
                    "Callous Your Mind",
                    "Do things that make you uncomfortable every single day to build thick mental resilience.",
                    "Take a cold shower or tackle your most dreaded task first thing."
                )
            ),
            memorableQuotes = listOf(
                "You are in danger of living a life so comfortable and soft, that you will die without ever realizing your true potential.",
                "The only thing more contagious than a good attitude is a bad one."
            ),
            actionableLifeScoreQuest = "Step outside your comfort zone and complete one dreaded task today (+75 XP)."
        ),
        BookSummary(
            id = "seven_habits",
            title = "The 7 Habits of Highly Effective People",
            author = "Stephen R. Covey",
            coverEmoji = "🧭",
            dimension = DimensionType.RELATIONSHIPS,
            readingTimeMinutes = 14,
            rating = 4.91,
            coreThesis = "True character effectiveness comes from inside-out alignment with timeless universal human principles.",
            summaryOverview = "Covey’s timeless masterpiece guides individuals from dependence to independence (Private Victory) and ultimately to interdependence (Public Victory) through 7 foundational paradigms of character ethics.",
            keyTakeaways = listOf(
                KeyTakeaway(
                    1,
                    "Habit 1: Be Proactive",
                    "Focus your energy entirely on your Circle of Influence rather than wasting mental bandwidth on your Circle of Concern.",
                    "Catch yourself complaining and shift immediately to an actionable solution."
                ),
                KeyTakeaway(
                    2,
                    "Habit 2: Begin with the End in Mind",
                    "Envision your 80th birthday or funeral eulogy. What character legacy do you want to leave?",
                    "Draft your personal life mission statement across all 8 dimensions."
                ),
                KeyTakeaway(
                    3,
                    "Habit 3: Put First Things First",
                    "Organize your life around Quadrant II: Not Urgent but Important (deep relationships, health, planning, learning).",
                    "Block 1 hour this week dedicated exclusively to Quadrant II activities."
                ),
                KeyTakeaway(
                    4,
                    "Habit 5: Seek First to Understand, Then to Be Understood",
                    "Listen with the intent to genuinely understand the other person’s perspective rather than listening to reply.",
                    "Practice empathetic listening in your next conversation without interrupting."
                )
            ),
            memorableQuotes = listOf(
                "Most people do not listen with the intent to understand; they listen with the intent to reply.",
                "Sow a thought, reap an action; sow an action, reap a habit; sow a habit, reap a character; sow a character, reap a destiny."
            ),
            actionableLifeScoreQuest = "Have an empathetic listening conversation with a loved one today (+75 XP)."
        ),
        BookSummary(
            id = "mans_search_for_meaning",
            title = "Man's Search for Meaning",
            author = "Viktor E. Frankl",
            coverEmoji = "🕊️",
            dimension = DimensionType.MENTAL_HEALTH,
            readingTimeMinutes = 12,
            rating = 4.97,
            coreThesis = "Everything can be taken from a man but one thing: the last of the human freedoms—to choose one’s attitude in any given set of circumstances.",
            summaryOverview = "Psychiatrist Viktor Frankl chronicles his experiences in Auschwitz concentration camp and reveals his psychotherapeutic method of Logotherapy: identifying a profound purpose in life to survive even the deepest existential suffering.",
            keyTakeaways = listOf(
                KeyTakeaway(
                    1,
                    "The Ultimate Human Freedom",
                    "Between stimulus and response there is a space. In that space is our power to choose our response.",
                    "When triggered today, pause for 5 seconds before choosing your reaction."
                ),
                KeyTakeaway(
                    2,
                    "He Who Has a Why",
                    "Those who have a 'why' to live, can bear with almost any 'how'. Purpose transforms suffering into dignity.",
                    "Clarify your core personal purpose for the next 12 months."
                ),
                KeyTakeaway(
                    3,
                    "Three Sources of Meaning",
                    "We find meaning: (1) by creating a work or doing a deed, (2) by experiencing something or encountering someone (love), (3) by our attitude toward unavoidable suffering.",
                    "Dedicate an act of love or craft to someone you care about today."
                )
            ),
            memorableQuotes = listOf(
                "When we are no longer able to change a situation, we are challenged to change ourselves.",
                "Those who have a 'why' to live, can bear with almost any 'how'."
            ),
            actionableLifeScoreQuest = "Reflect on your life purpose in your journal and write down your core 'Why' (+75 XP)."
        ),
        BookSummary(
            id = "digital_minimalism",
            title = "Digital Minimalism",
            author = "Cal Newport",
            coverEmoji = "📱",
            dimension = DimensionType.MENTAL_HEALTH,
            readingTimeMinutes = 12,
            rating = 4.87,
            coreThesis = "A philosophy of technology use in which you focus your online time on a small number of carefully selected activities that strongly support things you value.",
            summaryOverview = "Digital Minimalism is the antidote to modern digital overwhelm. Newport explains how tech platforms engineer addictive loops and provides a practical framework for reclaiming your attention, relationships, and peace of mind.",
            keyTakeaways = listOf(
                KeyTakeaway(
                    1,
                    "The Attention Economy",
                    "Your time and attention are the product being sold. Constant connectivity degrades solitude and reflective thought.",
                    "Turn off all non-human notifications on your device."
                ),
                KeyTakeaway(
                    2,
                    "Reclaim Solitude",
                    "Solitude is a state of mind where your mind is free from the input of other minds. It is essential for clarity.",
                    "Take a 20-minute walk without phone, music, or podcasts."
                ),
                KeyTakeaway(
                    3,
                    "High-Quality Leisure",
                    "Replace passive algorithmic scrolling with high-quality physical or creative hobbies (crafting, sports, reading).",
                    "Pick up an analog hobby this weekend."
                )
            ),
            memorableQuotes = listOf(
                "Digital minimalists see new technologies as tools to be used to support things they deeply value—not as sources of value themselves.",
                "The sugar rush of clicks and likes is a poor substitute for real-world engagement."
            ),
            actionableLifeScoreQuest = "Do a 60-minute tech-free digital detox block this evening (+75 XP)."
        )
    )
}
