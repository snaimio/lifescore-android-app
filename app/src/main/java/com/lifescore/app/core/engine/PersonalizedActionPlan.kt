package com.lifescore.app.core.engine

import com.lifescore.app.core.util.AssessmentResult
import com.lifescore.app.core.util.PsychometricDimension
import com.lifescore.app.data.Habit
import com.lifescore.app.data.HabitData
import com.lifescore.app.domain.model.DimensionType

data class ActionPlan(
    val userName: String,
    val archetype: String,
    val archetypeTitle: String,
    val overallScore: Int,
    val level: Int = 1,
    val strengths: List<DimensionStrength>,
    val weaknesses: List<DimensionWeakness>,
    val dailyHabits: List<DailyHabit>,
    val weeklyGoals: List<WeeklyGoal>,
    val monthlyMilestones: List<MonthlyMilestone>,
    val recommendedChallenges: List<RecommendedChallenge>,
    val aiCoachBriefing: String
)

data class DimensionStrength(
    val dimension: DimensionType,
    val score: Int,
    val whyStrong: String,
    val leverageStrategy: String
)

data class DimensionWeakness(
    val dimension: DimensionType,
    val score: Int,
    val whyWeak: String,
    val improvementPlan: String,
    val starterHabits: List<Habit>
)

data class DailyHabit(
    val id: Int,
    val title: String,
    val description: String,
    val dimension: DimensionType,
    val xpReward: Int,
    val estimatedMinutes: Int,
    val timeOfDay: String, // Morning, Afternoon, Evening
    val reminderText: String,
    val whyThisMatters: String
)

data class WeeklyGoal(
    val title: String,
    val description: String,
    val dimension: DimensionType,
    val targetDays: Int, // e.g. 5 out of 7 days
    val xpReward: Int
)

data class MonthlyMilestone(
    val title: String,
    val description: String,
    val dimension: DimensionType,
    val currentScore: Int,
    val targetScore: Int,
    val xpReward: Int,
    val celebrationText: String
)

data class RecommendedChallenge(
    val title: String,
    val description: String,
    val duration: Int, // Days
    val dimension: DimensionType,
    val xpReward: Int,
    val whyThisChallenge: String
)

object ActionPlanGenerator {

    fun generateActionPlan(result: AssessmentResult, userName: String = "Achiever"): ActionPlan {
        // Map psychometric dimensions to Life Dimensions & scores
        val mappedScores = result.dimensionScores.entries.map { (psyDim, score) ->
            mapPsychometricToLife(psyDim) to score
        }

        val sorted = mappedScores.sortedByDescending { it.second }

        val strengths = sorted.take(2).map { (dim, score) ->
            DimensionStrength(
                dimension = dim,
                score = score,
                whyStrong = getStrengthReason(dim),
                leverageStrategy = getLeverageStrategy(dim)
            )
        }

        val weaknesses = sorted.takeLast(3).map { (dim, score) ->
            DimensionWeakness(
                dimension = dim,
                score = score,
                whyWeak = getWeaknessReason(dim),
                improvementPlan = getImprovementPlan(dim),
                starterHabits = getStarterHabits(dim)
            )
        }

        // Generate 5 daily starter habits (focus on growth opportunities)
        val dailyHabits = weaknesses.flatMap { weakness ->
            weakness.starterHabits.take(2).map { habit ->
                DailyHabit(
                    id = habit.id,
                    title = habit.title,
                    description = habit.description,
                    dimension = habit.dimension,
                    xpReward = habit.xpReward,
                    estimatedMinutes = habit.estimatedMinutes,
                    timeOfDay = recommendTimeOfDay(habit.dimension),
                    reminderText = "⏰ Time for: ${habit.title} (${habit.estimatedMinutes} min) - ${habit.description}",
                    whyThisMatters = "Completing this habit builds compounding consistency in ${habit.dimension.displayName} (+${habit.xpReward} XP)!"
                )
            }
        }.distinctBy { it.id }.take(5)

        return ActionPlan(
            userName = userName,
            archetype = result.archetype.name,
            archetypeTitle = result.archetype.title,
            overallScore = result.overallScore,
            level = (result.overallScore / 100).coerceAtLeast(1),
            strengths = strengths,
            weaknesses = weaknesses,
            dailyHabits = dailyHabits,
            weeklyGoals = generateWeeklyGoals(weaknesses),
            monthlyMilestones = generateMonthlyMilestones(weaknesses),
            recommendedChallenges = generateRecommendedChallenges(weaknesses),
            aiCoachBriefing = generateAICoachBriefing(result)
        )
    }

    private fun mapPsychometricToLife(dim: PsychometricDimension): DimensionType {
        return when (dim) {
            PsychometricDimension.INTELLECTUAL -> DimensionType.LEARNING
            PsychometricDimension.EXECUTION -> DimensionType.FITNESS
            PsychometricDimension.CREATIVE -> DimensionType.CAREER
            PsychometricDimension.EMPATHY -> DimensionType.RELATIONSHIPS
            PsychometricDimension.STRATEGY -> DimensionType.WEALTH
            PsychometricDimension.SYSTEMS_ORDER -> DimensionType.HEALTH
        }
    }

    private fun getStrengthReason(dim: DimensionType): String {
        return when (dim) {
            DimensionType.HEALTH -> "You prioritize physical recovery, restorative sleep, and biological vitality."
            DimensionType.LEARNING -> "You possess an innate intellectual curiosity and rapid synthesis capability."
            DimensionType.CAREER -> "You have exceptional execution velocity and strategic career ambition."
            DimensionType.WEALTH -> "You practice financial foresight, asset discipline, and budget control."
            DimensionType.RELATIONSHIPS -> "You exhibit high emotional intelligence and invest in deep social bonds."
            DimensionType.FITNESS -> "You sustain consistent athletic output and neuromuscular discipline."
            DimensionType.MENTAL_HEALTH -> "You maintain high mindfulness, composure, and emotional equilibrium."
            DimensionType.SOCIAL_LIFE -> "You naturally energize communities and build charismatic connections."
        }
    }

    private fun getLeverageStrategy(dim: DimensionType): String {
        return when (dim) {
            DimensionType.HEALTH -> "Anchor your hardest deep-work sprint directly after your peak morning energy window."
            DimensionType.LEARNING -> "Document your domain learnings in the Cognitive Journal to synthesize career leverage."
            DimensionType.CAREER -> "Delegate or eliminate 20% of low-leverage tasks to protect 90-minute focus blocks."
            DimensionType.WEALTH -> "Automate weekly investments to compound your baseline financial freedom."
            DimensionType.RELATIONSHIPS -> "Mentor and collaborate with squad peers to scale community impact."
            DimensionType.FITNESS -> "Channel athletic discipline into challenging multi-day habit streaks."
            DimensionType.MENTAL_HEALTH -> "Use your mental clarity to make decisive, stress-free strategic choices."
            DimensionType.SOCIAL_LIFE -> "Lead an archetype tribe accountability group to elevate peer performance."
        }
    }

    private fun getWeaknessReason(dim: DimensionType): String {
        return when (dim) {
            DimensionType.HEALTH -> "Daily sleep windows and hydration routines experience friction during high-stress periods."
            DimensionType.LEARNING -> "Reading and deliberate skill acquisition are often crowded out by reactive tasks."
            DimensionType.CAREER -> "Prioritization clarity and deep-work boundaries need structured enforcement."
            DimensionType.WEALTH -> "Spending tracking and automated savings require systematic review."
            DimensionType.RELATIONSHIPS -> "Consistent outreach to close circles can be neglected during busy cycles."
            DimensionType.FITNESS -> "Sedentary work blocks cause physical stiffness and inconsistent movement."
            DimensionType.MENTAL_HEALTH -> "Emotional processing and mindful stillness need regular intentional pauses."
            DimensionType.SOCIAL_LIFE -> "Social community engagement is occasional rather than rhythmic."
        }
    }

    private fun getImprovementPlan(dim: DimensionType): String {
        return when (dim) {
            DimensionType.HEALTH -> "Lock in a fixed 30-minute sleep sanctuary and drink 500ml water right upon waking."
            DimensionType.LEARNING -> "Commit to 15 minutes of non-fiction deep reading or technical study daily."
            DimensionType.CAREER -> "Set tomorrow's top 3 priorities the night before and protect a 25-minute Pomodoro."
            DimensionType.WEALTH -> "Conduct a 10-minute weekly expense audit and set a 48-hour cooldown on purchases."
            DimensionType.RELATIONSHIPS -> "Send 1 thoughtful appreciation message and practice active, uninterrupted listening."
            DimensionType.FITNESS -> "Perform 15 minutes of brisk walking and a 5-minute morning mobility stretch."
            DimensionType.MENTAL_HEALTH -> "Practice daily gratitude journaling and 5 minutes of mindful silence."
            DimensionType.SOCIAL_LIFE -> "Reach out to one friend or colleague weekly and greet neighbors warmly."
        }
    }

    private fun getStarterHabits(dim: DimensionType): List<Habit> {
        val pool = HabitData.getHabitsByDimension(dim)
        return if (pool.isNotEmpty()) pool.take(3) else listOf(
            Habit(101, "Daily ${dim.displayName} Action", "Complete 5 minutes of focused effort.", dim, 20, "Easy", 5)
        )
    }

    private fun recommendTimeOfDay(dim: DimensionType): String {
        return when (dim) {
            DimensionType.HEALTH, DimensionType.FITNESS, DimensionType.CAREER, DimensionType.MENTAL_HEALTH -> "Morning"
            DimensionType.RELATIONSHIPS, DimensionType.SOCIAL_LIFE -> "Afternoon"
            DimensionType.WEALTH, DimensionType.LEARNING -> "Evening"
        }
    }

    private fun generateWeeklyGoals(weaknesses: List<DimensionWeakness>): List<WeeklyGoal> {
        return weaknesses.map { weakness ->
            WeeklyGoal(
                title = "5-Day ${weakness.dimension.displayName} Consistency",
                description = "Complete at least 1 starter habit in ${weakness.dimension.displayName} on 5 distinct days this week.",
                dimension = weakness.dimension,
                targetDays = 5,
                xpReward = 150
            )
        }
    }

    private fun generateMonthlyMilestones(weaknesses: List<DimensionWeakness>): List<MonthlyMilestone> {
        return weaknesses.map { weakness ->
            val target = (weakness.score + 40).coerceAtMost(200)
            MonthlyMilestone(
                title = "Elevate ${weakness.dimension.displayName} to $target/200",
                description = "Systematically raise your baseline from ${weakness.score}/200 to $target/200 through daily compounding micro-habits.",
                dimension = weakness.dimension,
                currentScore = weakness.score,
                targetScore = target,
                xpReward = 400,
                celebrationText = "🎉 You've unlocked the ${weakness.dimension.displayName} Mastery Badge and elevated your LifeScore equilibrium!"
            )
        }
    }

    private fun generateRecommendedChallenges(weaknesses: List<DimensionWeakness>): List<RecommendedChallenge> {
        return weaknesses.map { weakness ->
            RecommendedChallenge(
                title = "30-Day ${weakness.dimension.displayName} Transformation Forge",
                description = "Commit to daily micro-wins in ${weakness.dimension.displayName} to permanently eliminate this growth bottleneck.",
                duration = 30,
                dimension = weakness.dimension,
                xpReward = 600,
                whyThisChallenge = "Targeting ${weakness.dimension.displayName} generates the highest compounding leverage for your overall LifeScore."
            )
        }
    }

    private fun generateAICoachBriefing(result: AssessmentResult): String {
        val weakest = result.dimensionScores.minByOrNull { it.value }?.key?.displayName ?: "Execution"
        return """
            Welcome to your LifeScore Transformation Engine!
            
            As **${result.archetype.name}** (${result.archetype.title}), your baseline score is **${result.overallScore}/1200**.
            Your core superpower is **${result.archetype.superpower}**.
            
            Your single highest-leverage growth area is **$weakest**. Rather than trying to change everything at once, follow your 5 daily starter habits below. Compounding 15 minutes of daily focus here will dramatically elevate your life balance within 30 days!
        """.trimIndent()
    }
}
