package com.lifescore.app.core.engine

import com.lifescore.app.domain.model.ChallengePack
import com.lifescore.app.domain.model.ChallengeTier
import com.lifescore.app.domain.model.DimensionType

object ChallengePackSystem {

    /**
     * Generates a comprehensive 3-tier Challenge Pack matrix (Starter, Progression, Mastery)
     * for all 8 life dimensions.
     */
    fun getPacksForDimension(dimension: DimensionType): List<ChallengePack> {
        val (starterTasks, progTasks, masteryTasks) = getTasksForDimension(dimension)

        return listOf(
            ChallengePack(
                id = "pack_${dimension.name.lowercase()}_starter",
                title = "7-Day ${dimension.displayName} Foundation",
                dimension = dimension,
                tier = ChallengeTier.STARTER,
                durationDays = 7,
                description = "Build the baseline neuromuscular habit and eliminate initial friction.",
                dailyTasks = starterTasks,
                totalXpReward = 175
            ),
            ChallengePack(
                id = "pack_${dimension.name.lowercase()}_progression",
                title = "14-Day ${dimension.displayName} Accelerator",
                dimension = dimension,
                tier = ChallengeTier.PROGRESSION,
                durationDays = 14,
                description = "Scale consistency, track compounding metrics, and elevate execution velocity.",
                dailyTasks = progTasks,
                totalXpReward = 400
            ),
            ChallengePack(
                id = "pack_${dimension.name.lowercase()}_mastery",
                title = "30-Day ${dimension.displayName} Mastery Forge",
                dimension = dimension,
                tier = ChallengeTier.MASTERY,
                durationDays = 30,
                description = "Cement irreversible life transformation and achieve elite percentile performance.",
                dailyTasks = masteryTasks,
                totalXpReward = 900
            )
        )
    }

    fun getAllPacks(): List<ChallengePack> {
        return DimensionType.values().flatMap { getPacksForDimension(it) }
    }

    private fun getTasksForDimension(dimension: DimensionType): Triple<List<String>, List<String>, List<String>> {
        return when (dimension) {
            DimensionType.HEALTH -> Triple(
                listOf("Drink 2.5L water", "Sleep 7.5+ hours", "No screens 30m before bed", "Morning 10m sunlight", "Whole food lunch", "Zero refined sugar after 7pm", "Evening stretch 10m"),
                List(14) { "Consistent sleep window + 25m cardiovascular stimulus (Day ${it + 1})" },
                List(30) { "Comprehensive biomarker optimization protocol (Day ${it + 1})" }
            )
            DimensionType.FITNESS -> Triple(
                listOf("10,000 steps", "20 bodyweight squats + pushups", "15m mobility flow", "Zone 2 brisk walk 30m", "High protein intake", "Active recovery stretch", "Benchmark fitness test"),
                List(14) { "Progressive overload resistance session + hydration metric (Day ${it + 1})" },
                List(30) { "Hypertrophy & VO2 max athletic progression (Day ${it + 1})" }
            )
            DimensionType.WEALTH -> Triple(
                listOf("Log every expense", "Cancel 1 unused subscription", "Review 3-month bank statement", "Automate savings transfer", "Read 1 financial primer chapter", "Calculate net worth baseline", "Set weekly budget cap"),
                List(14) { "Investment asset allocation review & expense optimization (Day ${it + 1})" },
                List(30) { "Cashflow compounding and income diversification engine (Day ${it + 1})" }
            )
            DimensionType.CAREER -> Triple(
                listOf("Identify top daily priority", "Execute 60m deep work sprint", "Organize project board", "Send 1 high-value follow-up", "Document a system process", "Learn 1 high-leverage tool shortcut", "Conduct weekly career retrospective"),
                List(14) { "High-impact deliverable milestone sprint (Day ${it + 1})" },
                List(30) { "Leadership influence & portfolio milestone delivery (Day ${it + 1})" }
            )
            DimensionType.LEARNING -> Triple(
                listOf("Read 20 pages non-fiction", "Take structured notes on 1 insight", "Listen to an educational lecture", "Practice a technical skill for 25m", "Teach an idea to someone else", "Solve 1 complex domain challenge", "Review weekly synthesis notes"),
                List(14) { "Accelerated skill acquisition deep study session (Day ${it + 1})" },
                List(30) { "Polymath mastery curriculum synthesis (Day ${it + 1})" }
            )
            DimensionType.RELATIONSHIPS -> Triple(
                listOf("Send 1 thoughtful appreciation note", "Engage in 15m undistracted conversation", "Call a family member", "Perform a random act of kindness", "Practice empathetic listening", "Plan a meaningful shared activity", "Express gratitude to a mentor"),
                List(14) { "Deep connection ritual and active presence practice (Day ${it + 1})" },
                List(30) { "Social bond strengthening & community reciprocity (Day ${it + 1})" }
            )
            DimensionType.MENTAL_HEALTH -> Triple(
                listOf("10m mindfulness meditation", "Write 3 gratitude points", "Take a mindful nature walk", "Perform 4-7-8 breathing exercises", "Digital detox for 2 hours", "Identify and reframe 1 negative thought", "Evening cognitive journal reflection"),
                List(14) { "Emotional resilience & stress regulation practice (Day ${it + 1})" },
                List(30) { "Cognitive behavioral reframing & inner equilibrium forge (Day ${it + 1})" }
            )
            DimensionType.SOCIAL_LIFE -> Triple(
                listOf("Reach out to an old friend", "Join a community discussion", "Attend a local meetup or group event", "Host a casual gathering or coffee", "Compliment a colleague or stranger", "Engage in a shared hobby group", "Plan a weekend social outing"),
                List(14) { "Social circle expansion and collaborative group engagement (Day ${it + 1})" },
                List(30) { "High-trust network compounding & charisma mastery (Day ${it + 1})" }
            )
        }
    }
}
