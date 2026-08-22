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
        )
    )

    fun getSessionForDay(day: Int): DailyGrowthSession {
        val index = (day - 1).coerceAtLeast(0) % sessions.size
        return sessions[index]
    }
}
