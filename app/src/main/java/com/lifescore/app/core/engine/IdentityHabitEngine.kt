package com.lifescore.app.core.engine

import com.lifescore.app.domain.model.DimensionType

data class CoreIdentity(
    val id: String,
    val statement: String, // e.g. "I am a Disciplined Deep Worker"
    val archetypeEmoji: String,
    val dimension: DimensionType,
    val totalVotesCast: Int = 12,
    val identityLevel: Int = 2,
    val milestoneTitle: String = "Apprentice",
    val recentProofAction: String = "Completed 90-min coding sprint"
)

object IdentityHabitEngine {

    val defaultIdentities: List<CoreIdentity> = listOf(
        CoreIdentity("id_worker", "I am a Disciplined Deep Worker", "💻", DimensionType.CAREER, totalVotesCast = 28, identityLevel = 3, milestoneTitle = "Focused Craftsman", recentProofAction = "Shipped release PR"),
        CoreIdentity("id_athlete", "I am an Athletic High-Performer", "🏃", DimensionType.HEALTH, totalVotesCast = 42, identityLevel = 4, milestoneTitle = "Endurance Athlete", recentProofAction = "10k morning steps"),
        CoreIdentity("id_reader", "I am a Lifelong Scholar & Reader", "📚", DimensionType.LEARNING, totalVotesCast = 19, identityLevel = 2, milestoneTitle = "Avid Reader", recentProofAction = "Read 20 pages of Atomic Habits"),
        CoreIdentity("id_stoic", "I am a Calm, Mindful Stoic", "🧠", DimensionType.MENTAL_HEALTH, totalVotesCast = 31, identityLevel = 3, milestoneTitle = "Citadel of Calm", recentProofAction = "10-min box breathing")
    )

    fun calculateIdentityLevel(votes: Int): Pair<Int, String> {
        return when {
            votes >= 100 -> Pair(5, "Master of Identity")
            votes >= 50 -> Pair(4, "Established Practitioner")
            votes >= 25 -> Pair(3, "Consistent Achiever")
            votes >= 10 -> Pair(2, "Dedicated Apprentice")
            else -> Pair(1, "Curious Beginner")
        }
    }
}
