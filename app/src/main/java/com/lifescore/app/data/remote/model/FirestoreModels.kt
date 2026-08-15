package com.lifescore.app.data.remote.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@IgnoreExtraProperties
data class UserDocument(
    @DocumentId val uid: String = "",
    val email: String = "",
    val displayName: String = "Achiever",
    val totalScore: Int = 500,
    val level: Int = 1,
    val currentXp: Int = 0,
    val streak: Int = 0,
    val isPremium: Boolean = false,
    val archetype: String = "WARRIOR",
    val shieldsRemaining: Int = 1,
    val guardianId: String? = null,
    val isSponsored: Boolean = false,
    val shieldsUsedCount: Int = 0,
    val coinBalance: Int = 1250,
    val lifetimeCoinsEarned: Int = 3400,
    @ServerTimestamp val createdAt: Date? = null,
    @ServerTimestamp val lastActive: Date? = null
)

@IgnoreExtraProperties
data class DimensionDocument(
    @DocumentId val id: String = "",
    val uid: String = "",
    val dimensionId: String = "",
    val name: String = "",
    val score: Float = 50f,
    val progress: Float = 0.5f,
    val tasksCompleted: Int = 0,
    val totalTasks: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)

@IgnoreExtraProperties
data class TaskDocument(
    @DocumentId val taskId: String = "",
    val uid: String = "",
    val dimensionId: String = "",
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val points: Int = 15,
    val date: String = "",
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@IgnoreExtraProperties
data class StreakDocument(
    @DocumentId val uid: String = "",
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActiveDate: String = "",
    val streakHistory: List<Boolean> = emptyList(),
    val streakInsuranceCount: Int = 1,
    val lastShieldUsedDate: String = ""
)

@IgnoreExtraProperties
data class GuardianSponsorshipDocument(
    @DocumentId val id: String = "",
    val sponsorUid: String = "",
    val sponsorName: String = "",
    val recipientEmail: String = "",
    val recipientUid: String = "",
    val message: String = "",
    val sponsoredDate: Long = System.currentTimeMillis(),
    val tier: String = "LifeScore Pro",
    val thankYouNote: String = ""
)

@IgnoreExtraProperties
data class ChallengeDocument(
    @DocumentId val challengeId: String = "",
    val title: String = "",
    val description: String = "",
    val duration: Int = 30,
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val progress: Float = 0f,
    val isActive: Boolean = true,
    val xpReward: Int = 500
)

@IgnoreExtraProperties
data class UserChallengeDocument(
    @DocumentId val id: String = "",
    val uid: String = "",
    val challengeId: String = "",
    val progress: Float = 0f,
    val currentDay: Int = 0,
    val isCompleted: Boolean = false,
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

@IgnoreExtraProperties
data class AssessmentResultDocument(
    @DocumentId val id: String = "",
    val uid: String = "",
    val archetypeId: String = "",
    val archetypeName: String = "",
    val archetypeTitle: String = "",
    val overallScore: Int = 0,
    val topRiasecCode: String = "",
    val intellectualScore: Int = 0,
    val executionScore: Int = 0,
    val creativeScore: Int = 0,
    val empathyScore: Int = 0,
    val strategyScore: Int = 0,
    val systemsScore: Int = 0,
    val topCareerTitles: List<String> = emptyList(),
    val completedAt: Long = System.currentTimeMillis()
)
