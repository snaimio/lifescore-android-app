package com.lifescore.app.core.engine

import com.lifescore.app.domain.model.ArchetypeTribe

object CommunityTribeSystem {

    val archetypeTribes: List<ArchetypeTribe> by lazy {
        listOf(
            ArchetypeTribe(
                archetypeId = "architect",
                tribeName = "The Architects Syndicate",
                emoji = "🏛️",
                memberCount = 1420,
                collectiveScore = 892400L,
                weeklyGoal = "10,000 deep-work sprints logged",
                rank = 1,
                topTraits = listOf("System Design", "Optimization", "High Agency")
            ),
            ArchetypeTribe(
                archetypeId = "warrior",
                tribeName = "The Iron Vanguard",
                emoji = "⚔️",
                memberCount = 1290,
                collectiveScore = 845200L,
                weeklyGoal = "2,500 physical workouts & athletic challenges",
                rank = 2,
                topTraits = listOf("Relentless Drive", "Grit", "Endurance")
            ),
            ArchetypeTribe(
                archetypeId = "visionary",
                tribeName = "The Horizon Pioneers",
                emoji = "🚀",
                memberCount = 1150,
                collectiveScore = 789100L,
                weeklyGoal = "1,800 creative prototypes drafted",
                rank = 3,
                topTraits = listOf("Big Picture", "Disruption", "Innovation")
            ),
            ArchetypeTribe(
                archetypeId = "strategist",
                tribeName = "The Grand Tacticians",
                emoji = "♟️",
                memberCount = 980,
                collectiveScore = 712300L,
                weeklyGoal = "950 financial audits & portfolio balance reviews",
                rank = 4,
                topTraits = listOf("Precision", "Wealth Strategy", "Compounding")
            ),
            ArchetypeTribe(
                archetypeId = "healer",
                tribeName = "The Empathy Circle",
                emoji = "🌿",
                memberCount = 910,
                collectiveScore = 678400L,
                weeklyGoal = "3,200 active listening sessions & gratitude logs",
                rank = 5,
                topTraits = listOf("Emotional Intelligence", "Social Harmony", "Mental Calm")
            ),
            ArchetypeTribe(
                archetypeId = "scholar",
                tribeName = "The Alexandria Athenaeum",
                emoji = "📚",
                memberCount = 870,
                collectiveScore = 654100L,
                weeklyGoal = "1,500 research synthesis papers read",
                rank = 6,
                topTraits = listOf("Deep Study", "First Principles", "Intellect")
            )
        )
    }

    fun getTribeForArchetype(archetypeId: String): ArchetypeTribe {
        return archetypeTribes.find { it.archetypeId.equals(archetypeId, ignoreCase = true) }
            ?: archetypeTribes.first()
    }
}
