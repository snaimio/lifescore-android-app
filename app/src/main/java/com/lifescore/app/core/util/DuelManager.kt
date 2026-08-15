package com.lifescore.app.core.util

import com.lifescore.app.domain.model.ChallengeParticipant
import com.lifescore.app.domain.model.DimensionType

object DuelManager {

    fun generateInviteLink(challengeId: String, inviterName: String): String {
        val sanitizedInviter = inviterName.replace(" ", "_")
        return "https://lifescore.app/challenge/$challengeId?invitedBy=$sanitizedInviter"
    }

    fun generateDuelCaption(challengeTitle: String, dimension: DimensionType, inviteUrl: String): String {
        return "⚔️ I challenge you to the '$challengeTitle' 7-day duel in ${dimension.displayName}! Can you beat my streak? Accept my duel here: $inviteUrl #LifeScoreDuel #LifeBalance"
    }

    fun calculateDuelWinner(participants: List<ChallengeParticipant>, durationDays: Int): ChallengeParticipant? {
        if (participants.isEmpty()) return null
        return participants.maxWithOrNull(
            compareBy<ChallengeParticipant> { it.completedDays }
                .thenBy { it.streak }
                .thenBy { it.level }
        )
    }

    fun getDailyCheckInStatus(completedDays: Int, totalDays: Int): List<Boolean> {
        val count = totalDays.coerceAtLeast(1)
        val completed = completedDays.coerceIn(0, count)
        return (1..count).map { day -> day <= completed }
    }
}
