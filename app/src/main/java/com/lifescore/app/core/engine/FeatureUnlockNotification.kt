package com.lifescore.app.core.engine

object FeatureUnlockNotification {

    fun getUnlockMessage(phase: UserPhase): String {
        return when (phase) {
            UserPhase.NEW_USER -> "🌱 Welcome! Start simple with 3 micro-habits today."
            UserPhase.EXPLORING -> "🎉 Level Up! Unlocked: 30-Day Challenges, Global Leaderboard, and Cognitive Journal!"
            UserPhase.ADVANCED -> "👑 Unlocked: Expert Masterclasses, Social Duels, XP Reward Store & Micro-Vlogs!"
            UserPhase.EXPERT -> "⚡ Full Life OS Unlocked! Dimension Boss Raids, Enterprise Hub & Predictive Analytics active."
        }
    }
}
