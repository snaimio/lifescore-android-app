package com.lifescore.app.domain.model.selfimprovement

import com.lifescore.app.domain.model.DimensionType

data class DailyGrowthSession(
    val dayNumber: Int,
    val title: String,
    val subtitle: String,
    val dimension: DimensionType,
    val durationMinutes: Int = 15,
    val iconEmoji: String,
    val coreConcept: String,
    val lessonBody: String,
    val keyTakeaways: List<String>,
    val dailyActionChallenge: String,
    val reflectionPrompt: String,
    val xpReward: Int = 50
)

object DailyGrowthCurriculum {
    val sessions: List<DailyGrowthSession> = listOf(
        DailyGrowthSession(
            dayNumber = 1,
            title = "The Architecture of Compounding",
            subtitle = "Why 1% Daily Improvements Outpace All Radical Changes",
            dimension = DimensionType.MENTAL_HEALTH,
            durationMinutes = 15,
            iconEmoji = "🌱",
            coreConcept = "True life transformation is mathematical compounding. Small daily actions create massive long-term divergence.",
            lessonBody = "Most people overestimate what they can accomplish in a single day and vastly underestimate what they can achieve in a year of consistent, non-negotiable daily habits. When you improve 1% each day, by day 365 you are 37.78 times better. The secret is removing friction and executing your baseline habit on your worst days.",
            keyTakeaways = listOf(
                "Focus on the trajectory of your habits, not your current score.",
                "Make your minimum daily baseline so easy you cannot say no.",
                "Compounding is quiet at first, then exponential."
            ),
            dailyActionChallenge = "Define your 'Minimum Non-Negotiable' for your top habit (e.g. 5 pushups, 2 pages read) and complete it today.",
            reflectionPrompt = "If your daily habits for the last 30 days were repeated for 5 years, where would your life end up?"
        ),
        DailyGrowthSession(
            dayNumber = 2,
            title = "Dopamine Detox & Focus Clarity",
            subtitle = "Reclaiming Your Brain from Algorithmic Overstimulation",
            dimension = DimensionType.MENTAL_HEALTH,
            durationMinutes = 15,
            iconEmoji = "⚡",
            coreConcept = "Your ability to sustain deep focus on hard problems is your most lucrative and peaceful superpower.",
            lessonBody = "Modern smartphone apps are engineered by behavioral psychologists to exploit intermittent variable rewards. When your dopamine receptors are continuously flooded with micro-hits, hard tasks like studying, deep work, or reading feel unbearable. A dopamine reset restores the sensitivity of your neural reward circuits.",
            keyTakeaways = listOf(
                "Boredom is the catalyst for genuine creativity and deep focus.",
                "Every notification is an interruption to your cognitive flow state.",
                "High baseline dopamine creates calm, steady executive drive."
            ),
            dailyActionChallenge = "Spend the next 60 minutes with your phone in a completely separate room during your main work block.",
            reflectionPrompt = "Which digital habit currently consumes the most mental energy without offering meaningful joy or growth?"
        ),
        DailyGrowthSession(
            dayNumber = 3,
            title = "The High-Energy Biological Engine",
            subtitle = "Optimizing Hydration, Sunlight, and Cellular Vitality",
            dimension = DimensionType.HEALTH,
            durationMinutes = 15,
            iconEmoji = "☀️",
            coreConcept = "Mental energy is directly downstream of your physical cellular physiology.",
            lessonBody = "Your brain consumes 20% of your body's glucose and oxygen despite representing only 2% of your mass. Mild dehydration of just 1-2% degrades cognitive performance and increases fatigue. Early morning natural photons hitting your retinal ganglion cells trigger cortisol awakening response and synchronize your circadian rhythm for all-day stamina.",
            keyTakeaways = listOf(
                "Drink 500ml of water with a pinch of electrolytes immediately upon waking.",
                "Step outside into natural morning sunlight for 10-15 minutes.",
                "Energy is generated through motion, not passive waiting."
            ),
            dailyActionChallenge = "Drink a large glass of water and take a 10-minute outdoor walk right now.",
            reflectionPrompt = "What physical choice (sleep, water, movement) has the highest leverage on your mood today?"
        ),
        DailyGrowthSession(
            dayNumber = 4,
            title = "Financial Armor & Asymmetric Wealth",
            subtitle = "The Psychology of Automated Freedom vs Status Traps",
            dimension = DimensionType.WEALTH,
            durationMinutes = 15,
            iconEmoji = "💰",
            coreConcept = "Wealth is not what you spend to impress strangers; wealth is the autonomy to own your time.",
            lessonBody = "Financial peace of mind is created through the gap between your ego and your income. The wealthy prioritize assets that buy back their freedom, while the middle class often buys liabilities disguised as luxury. Automating your investments and maintaining a fortress emergency fund turns money into an engine of tranquility.",
            keyTakeaways = listOf(
                "Spend money to buy time, not to signal status.",
                "Automate savings before you have the chance to spend them.",
                "A 6-month cash reserve provides unmatched emotional calm."
            ),
            dailyActionChallenge = "Audit your monthly subscriptions and cancel at least one unused recurring fee.",
            reflectionPrompt = "What does true financial freedom look like to you in daily hours and peace of mind?"
        ),
        DailyGrowthSession(
            dayNumber = 5,
            title = "Relational Mastery & Radical Empathy",
            subtitle = "Deepening Connection Through Active Presence",
            dimension = DimensionType.RELATIONSHIPS,
            durationMinutes = 15,
            iconEmoji = "💞",
            coreConcept = "The quality of your life is the quality of your relationships.",
            lessonBody = "In a world of constant digital distraction, giving another human being your undivided, unhurried attention is the rarest and most generous gift you can offer. Relationships compound like investments: small deposits of genuine appreciation and active listening build emotional bank accounts that weather any storm.",
            keyTakeaways = listOf(
                "Put your phone completely face down when conversing with someone.",
                "Express specific, unprompted gratitude to one person daily.",
                "Seek first to understand their world before seeking validation."
            ),
            dailyActionChallenge = "Send a thoughtful, sincere appreciation message to a friend or mentor today with zero expectations of return.",
            reflectionPrompt = "Who in your life deserves your full presence and appreciation today?"
        ),
        DailyGrowthSession(
            dayNumber = 6,
            title = "The 90-Minute Deep Work Protocol",
            subtitle = "Eliminating Cognitive Fragmentation for High-Value Output",
            dimension = DimensionType.CAREER,
            durationMinutes = 15,
            iconEmoji = "🧠",
            coreConcept = "Attention residue destroys cognitive capacity. Monotasking in protected blocks produces exponentially higher output.",
            lessonBody = "When you switch from a complex project to quickly check an email or text message, a portion of your cognitive bandwidth remains stuck on the secondary task. This attention residue lowers your IQ and drains your executive stamina. By structuring your day around 90-minute ultradian rhythm sprints with zero context switching, you accomplish 8 hours of shallow work in a single morning session.",
            keyTakeaways = listOf(
                "Attention residue from a 10-second notification can impair deep focus for up to 20 minutes.",
                "Align deep work with your natural morning circadian cognitive peak.",
                "Create a shutdown ritual at the end of the day to completely close open cognitive loops."
            ),
            dailyActionChallenge = "Schedule one 90-minute uninterrupted focus sprint on your most important project today with all notifications disabled.",
            reflectionPrompt = "What single high-value deliverable would move your career or craft forward most if you gave it 90 minutes of pure focus today?"
        ),
        DailyGrowthSession(
            dayNumber = 7,
            title = "Stoic Cognitive Control & Emotional Reframing",
            subtitle = "Mastering the Dichotomy of Control in High-Pressure Moments",
            dimension = DimensionType.MENTAL_HEALTH,
            durationMinutes = 15,
            iconEmoji = "🗿",
            coreConcept = "You cannot control external events, but you maintain 100% sovereign ownership of your internal response.",
            lessonBody = "The Stoic philosopher Epictetus established that humans are not disturbed by things, but by the view they take of them. Between stimulus and response, there is a microsecond pause where your conscious mind assigns meaning. When you separate what is strictly within your control (your actions, effort, attitude) from what is outside it (other people's reactions, outcomes, market conditions), anxiety evaporates into focused efficacy.",
            keyTakeaways = listOf(
                "Draw a strict boundary between your Circle of Control and your Circle of Concern.",
                "Adversity is neutral data; your interpretation determines whether it is a catastrophe or a catalyst.",
                "Practice voluntary discomfort to inoculate yourself against fear of loss."
            ),
            dailyActionChallenge = "When a frustrating obstacle occurs today, pause and verbally state: 'This is out of my control, but my response is 100% within my control.'",
            reflectionPrompt = "What unresolved concern is currently draining your mental energy that is outside of your direct control?"
        ),
        DailyGrowthSession(
            dayNumber = 8,
            title = "Metabolic Flexibility & Zone 2 Conditioning",
            subtitle = "Building an Unstoppable Mitochondrial Engine for Longevity",
            dimension = DimensionType.HEALTH,
            durationMinutes = 15,
            iconEmoji = "🏃",
            coreConcept = "Your cellular mitochondrial health dictates your daily cognitive stamina, insulin sensitivity, and longevity.",
            lessonBody = "Zone 2 aerobic exercise—training at a conversational pace where you can speak in full sentences but cannot sing—trains your slow-twitch muscle fibers to maximize fat oxidation and clear metabolic lactate. Groundbreaking longevity research demonstrates that building a robust Zone 2 aerobic foundation increases mitochondrial density, dramatically lowers resting heart rate, and protects against metabolic dysfunction.",
            keyTakeaways = listOf(
                "Zone 2 training (60-70% max heart rate) builds the aerobic base that supports all other physical and mental endeavors.",
                "Consistent Zone 2 activity drastically improves cellular insulin sensitivity and glucose clearance.",
                "Aim for 150-180 minutes of Zone 2 work per week, distributed in 30-45 minute steady-state sessions."
            ),
            dailyActionChallenge = "Complete a 30-minute Zone 2 brisk walk, jog, or cycle where you maintain a steady conversational breathing pace.",
            reflectionPrompt = "How do your physical movement and cardiovascular fitness levels directly affect your focus and emotional resilience during demanding days?"
        ),
        DailyGrowthSession(
            dayNumber = 9,
            title = "Circadian Sleep Architecture & Thermal Regulation",
            subtitle = "Mastering REM, Deep Sleep, and Adenosine Clearance",
            dimension = DimensionType.HEALTH,
            durationMinutes = 15,
            iconEmoji = "🛌",
            coreConcept = "Sleep is not passive downtime; it is the active biological synthesis of memory, hormone balance, and brain detoxification.",
            lessonBody = "During slow-wave deep sleep, the brain's glymphatic system pumps cerebrospinal fluid through neural tissues to wash away metabolic waste including beta-amyloid plaques. During REM sleep, neural connections are restructured and emotional trauma is metabolized. To unlock maximum sleep architecture efficiency, you must drop your core body temperature by 2-3°F and align light exposure with the solar clock.",
            keyTakeaways = listOf(
                "A cool sleeping environment (65-68°F / 18-20°C) is essential for entering restorative deep sleep stages.",
                "Adenosine accumulation builds sleep pressure; caffeine blocks adenosine receptors without eliminating biological fatigue.",
                "Consistent wake times anchor your circadian clock more reliably than fluctuating bedtimes."
            ),
            dailyActionChallenge = "Lower your bedroom temperature, eliminate all ambient LEDs, and stop screen exposure 60 minutes before sleep tonight.",
            reflectionPrompt = "What evening routine habits are currently sabotaging the restorative quality of your sleep?"
        ),
        DailyGrowthSession(
            dayNumber = 10,
            title = "Mental Models & First-Principles Inversion",
            subtitle = "Deconstructing Complex Problems to Atomic Truths",
            dimension = DimensionType.LEARNING,
            durationMinutes = 15,
            iconEmoji = "🔬",
            coreConcept = "Reasoning from first principles strips away analogy and reveals unconventional, high-leverage solutions.",
            lessonBody = "Most thinking is by analogy: copying what others do with minor iterative tweaks. First-principles thinking boils things down to their fundamental, indisputable truths and reasons upward from there. When paired with Carl Jacobi's mathematical principle of Inversion—'Invert, always invert'—you solve hard problems not by figuring out how to succeed, but by systematically mapping and avoiding every single way to fail.",
            keyTakeaways = listOf(
                "Invert problems: ask 'What actions would guarantee failure?' and ruthlessly eliminate them.",
                "Question foundational assumptions in your industry or habits: 'Why is this done this way?'",
                "Synthesize a latticework of mental models across physics, biology, and economics."
            ),
            dailyActionChallenge = "Take your most challenging current project and write down 5 ways you could completely ruin it, then verify you are doing none of them.",
            reflectionPrompt = "What belief or habit are you holding onto simply because 'that's how everyone else does it'?"
        ),
        DailyGrowthSession(
            dayNumber = 11,
            title = "Nonviolent Communication & Radical De-escalation",
            subtitle = "Transforming Conflict into High-Trust Collaboration",
            dimension = DimensionType.RELATIONSHIPS,
            durationMinutes = 15,
            iconEmoji = "🤝",
            coreConcept = "All human conflict is the tragic, unskillful expression of unmet universal psychological needs.",
            lessonBody = "Dr. Marshall Rosenberg's Nonviolent Communication (NVC) provides a 4-step framework for resolving any interpersonal tension without defense or hostility: (1) Concrete Observation without judgment, (2) Expressing specific Feelings without blame, (3) Identifying underlying unmet Needs, and (4) Formulating clear, actionable Requests. When you listen for the unmet need behind someone's anger, hostility dissolves into authentic connection.",
            keyTakeaways = listOf(
                "Separate objective observations (facts) from moralistic judgments (evaluations).",
                "Emotions are biological signals pointing directly to met or unmet universal human needs.",
                "Make positive, concrete requests ('Would you be willing to...') rather than vague demands."
            ),
            dailyActionChallenge = "In your next disagreement or conversation, reframe an emotional thought into the 4-part NVC format: Observation, Feeling, Need, Request.",
            reflectionPrompt = "What recurring argument or tension in your personal or professional life is rooted in an unexpressed core need?"
        ),
        DailyGrowthSession(
            dayNumber = 12,
            title = "Autotelic Flow States & Craft Mastery",
            subtitle = "Balancing High Challenge with High Skill",
            dimension = DimensionType.CAREER,
            durationMinutes = 15,
            iconEmoji = "🌊",
            coreConcept = "Optimal human happiness and peak performance occur when challenge seamlessly matches maximum skill.",
            lessonBody = "Psychologist Mihaly Csikszentmihalyi discovered that when an individual engages in an autotelic activity—one that is inherently rewarding for its own sake—they enter the Flow State. In Flow, self-consciousness vanishes, time perception dilates, and cognitive throughput skyrockets. Flow requires three conditions: clear immediate goals, unambiguous real-time feedback, and a challenge level calibrated just 4% above current skill.",
            keyTakeaways = listOf(
                "Flow occurs at the sweet spot between boredom (low challenge) and anxiety (overwhelming challenge).",
                "Immediate feedback loops accelerate skill acquisition and maintain deep engagement.",
                "Treat your daily work as a craft to be perfected rather than a chore to be endured."
            ),
            dailyActionChallenge = "Identify a difficult task you've been avoiding, calibrate its difficulty to be slightly outside your comfort zone, and immerse yourself for 30 minutes.",
            reflectionPrompt = "During what activities in the past month have you completely lost track of time and felt fully alive?"
        ),
        DailyGrowthSession(
            dayNumber = 13,
            title = "Antifragility & Asymmetric Upside Systems",
            subtitle = "Designing Life Architecture That Benefits from Chaos",
            dimension = DimensionType.WEALTH,
            durationMinutes = 15,
            iconEmoji = "🛡️",
            coreConcept = "The resilient resists shocks and stays the same; the antifragile gets better and stronger from volatility and stressors.",
            lessonBody = "Nassim Nicholas Taleb categorized all systems into fragile, robust, or antifragile. Fragile systems hate volatility and break under stress. Antifragile systems—like human muscles, immune systems, and well-designed careers—grow stronger when exposed to manageable stressors. To build an antifragile life, adopt the Barbell Strategy: place 90% of your resources in hyper-safe, impenetrable foundations and 10% in high-upside asymmetric bets with zero ruin risk.",
            keyTakeaways = listOf(
                "Never expose yourself to risk of total ruin, regardless of the potential reward.",
                "Look for asymmetric opportunities with strictly capped downside and unbounded upside.",
                "Embrace small, frequent stressors (exercise, learning curves, cold exposure) to build systemic anti-fragility."
            ),
            dailyActionChallenge = "Identify one area of your financial, health, or career setup that is fragile to sudden shocks and put a protective buffer in place today.",
            reflectionPrompt = "If an unexpected crisis occurred in your industry tomorrow, is your skill set fragile, robust, or antifragile?"
        ),
        DailyGrowthSession(
            dayNumber = 14,
            title = "Digital Minimalism & The Attention Sanctum",
            subtitle = "Reclaiming Autonomy from Algorithmic Compulsion",
            dimension = DimensionType.MENTAL_HEALTH,
            durationMinutes = 15,
            iconEmoji = "📵",
            coreConcept = "Your attention is your life. What you pay attention to becomes your lived experience and character.",
            lessonBody = "Modern technology platforms operate on business models engineered to capture and monetize your cognitive awareness. When you allow algorithms to dictate your inputs, your attention fragments into shallow reactivity. Digital minimalism is the intentional philosophy where you aggressively eliminate low-grade digital noise, reclaiming protected spaces for deep analog reflection, face-to-face community, and profound solitude.",
            keyTakeaways = listOf(
                "Every app on your phone is competing against your life goals for your finite daily attention.",
                "Solitude deprivation—having zero moments free from external inputs—leads to anxiety and loss of original thought.",
                "Adopt high-quality analog leisure activities to replace passive screen consumption."
            ),
            dailyActionChallenge = "Delete or hide your two most compulsive non-essential apps for the next 24 hours.",
            reflectionPrompt = "How much of your daily screen time is an active choice versus an automated unconscious reflex?"
        ),
        DailyGrowthSession(
            dayNumber = 15,
            title = "Deliberate Practice & The Feedback Velocity Loop",
            subtitle = "How 10,000 Focused Hours Create World-Class Expertise",
            dimension = DimensionType.LEARNING,
            durationMinutes = 15,
            iconEmoji = "🎯",
            coreConcept = "Mere repetition does not produce excellence; deliberate practice with rapid corrective feedback creates mastery.",
            lessonBody = "Dr. Anders Ericsson's landmark research on world-class violinists, chess grandmasters, and athletes revealed that experience alone does not lead to improvement. Most people reach acceptable proficiency and plateau. World-class masters engage in Deliberate Practice: breaking a complex craft down into micro-skills, pushing just past the edge of current capability, focusing on error correction, and incorporating immediate high-fidelity feedback.",
            keyTakeaways = listOf(
                "Targeted struggle at the edge of your ability stimulates myelin development around neural circuits.",
                "Rapid feedback loops compress years of passive practice into weeks of accelerated learning.",
                "Track objective metrics of execution rather than subjective feelings of effort."
            ),
            dailyActionChallenge = "Pick one specific micro-skill in your craft or workout, isolate it, and practice it with intense focus for 20 minutes while correcting errors in real time.",
            reflectionPrompt = "In what area of your life are you currently on autopilot where deliberate practice could unlock your next breakthrough?"
        )
    )

    fun getSessionForDay(day: Int): DailyGrowthSession {
        val index = (day - 1).coerceAtLeast(0) % sessions.size
        return sessions[index]
    }
}
