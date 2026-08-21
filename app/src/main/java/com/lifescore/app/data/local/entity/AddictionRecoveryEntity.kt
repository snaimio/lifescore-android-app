package com.lifescore.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AddictionType(val displayName: String, val emoji: String, val unitName: String, val defaultDailyCost: Double) {
    SMOKING("Smoking", "🚬", "Cigarettes", 12.0),
    ALCOHOL("Alcohol", "🍺", "Drinks", 15.0),
    NICOTINE_VAPING("Nicotine & Vaping", "💨", "Puffs/Pods", 10.0),
    DRUGS("Substances", "💊", "Doses", 20.0),
    SUGAR_ADDICTION("Sugar & Junk Food", "🍩", "Sweets", 8.0),
    SOCIAL_MEDIA_ADDICTION("Doomscrolling & Social Media", "📱", "Hours", 0.0),
    GAMBLING("Gambling", "🎰", "Bets", 25.0),
    PORN_ADDICTION("Pornography", "🔞", "Sessions", 0.0),
    OTHER("Custom Recovery", "🎯", "Occurrences", 10.0)
}

enum class CravingIntensity(val level: Int, val label: String, val emoji: String) {
    MILD(1, "Mild - Noticeable urge", "🟢"),
    MODERATE(2, "Moderate - Distracting", "🟡"),
    STRONG(3, "Strong - Intense push", "🟠"),
    OVERWHELMING(4, "Severe - Peak urge", "🔴")
}

enum class RelapseType(val label: String, val resetStreak: Boolean) {
    SLIP("Minor Slip (Streak Protected - Learning Moment)", false),
    RELAPSE("Full Relapse (Reset Clock - Fresh Start)", true),
    LAPSE("Mental Urge Surfed (No Action Taken)", false)
}

@Entity(tableName = "recovery_entries")
data class RecoveryEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "default_user",
    val addictionType: AddictionType = AddictionType.SMOKING,
    val sobrietyStartDate: Long = System.currentTimeMillis(),
    val dailyCost: Double = 12.0,
    val dailyMinutesConsumed: Int = 45,
    val dailyItemsConsumed: Int = 15,
    val currentStreakDays: Int = 0,
    val longestStreakDays: Int = 0,
    val totalSobrietyDays: Int = 0,
    val totalSlipsCount: Int = 0,
    val moneySaved: Double = 0.0,
    val timeSavedHours: Double = 0.0,
    val lastUpdated: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

@Entity(tableName = "craving_logs")
data class CravingLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "default_user",
    val addictionType: AddictionType = AddictionType.SMOKING,
    val intensity: CravingIntensity = CravingIntensity.MODERATE,
    val trigger: String = "Stress",
    val durationMinutes: Int = 10,
    val survived: Boolean = true,
    val notes: String = "",
    val distractionUsed: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "relapse_logs")
data class RelapseLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "default_user",
    val addictionType: AddictionType = AddictionType.SMOKING,
    val relapseType: RelapseType = RelapseType.SLIP,
    val trigger: String = "Social pressure",
    val lessonsLearned: String = "",
    val actionPlan: String = "",
    val streakBeforeSetback: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "recovery_milestones")
data class RecoveryMilestone(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "default_user",
    val addictionType: AddictionType = AddictionType.SMOKING,
    val milestoneDays: Int = 1,
    val title: String = "",
    val description: String = "",
    val healthBenefit: String = "",
    val medallionEmoji: String = "🥉",
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)

@Entity(tableName = "motivational_notes")
data class MotivationalNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "default_user",
    val note: String = "",
    val reason: String = "For my health and family",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recovery_pledges")
data class RecoveryPledge(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "default_user",
    val dateIso: String = "",
    val addictionType: AddictionType = AddictionType.SMOKING,
    val pledgeText: String = "I pledge to stay sober and honor my growth today.",
    val isEveningReflected: Boolean = false,
    val eveningReflection: String = "",
    val isKept: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "recovery_savings_goals")
data class RecoverySavingsGoal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "default_user",
    val title: String = "New Sneakers",
    val targetAmount: Double = 120.0,
    val iconEmoji: String = "👟",
    val isReached: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
