package com.lifescore.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// ==========================================
// 1. VIRAL GROWTH & REFERRAL ENTITIES
// ==========================================
@Entity(tableName = "referrals")
data class ReferralEntity(
    @PrimaryKey
    val userId: String = "default_user",
    val referralCode: String = "HERO-7782",
    val invitedCount: Int = 2,
    val targetInviteCount: Int = 3,
    val isOneMonthPremiumUnlocked: Boolean = false,
    val totalBonusXpEarned: Int = 300,
    val invitedFriendNamesCsv: String = "Sarah Jenkins,Marcus Vance"
)

// ==========================================
// 2. 10-TIER COMPETITIVE LEAGUES
// ==========================================
@Entity(tableName = "league_tiers")
data class LeagueTierEntity(
    @PrimaryKey
    val userId: String = "default_user",
    val tierName: String = "Gold II", // Bronze I-III, Silver I-III, Gold I-III, Platinum I-III, Diamond
    val tierLevel: Int = 5, // 1 to 10
    val currentWeeklyXp: Int = 1450,
    val userRankInLeague: Int = 4,
    val totalCompetitorsInPool: Int = 30,
    val promotionCutoffRank: Int = 10,
    val relegationCutoffRank: Int = 25,
    val seasonDaysRemaining: Int = 3,
    val tierBadgeEmoji: String = "🥇"
)

// ==========================================
// 3. STREAK FREEZES & 3-DAY RESURRECTION
// ==========================================
@Entity(tableName = "streak_inventory")
data class StreakInventoryEntity(
    @PrimaryKey
    val userId: String = "default_user",
    val streakFreezesAvailable: Int = 2,
    val isFreezeShieldArmed: Boolean = true,
    val currentActiveStreakDays: Int = 12,
    val isResurrectionQuestActive: Boolean = false,
    val resurrectionStreakDaysToRecover: Int = 0,
    val resurrectionDayProgress: Int = 0, // 0 to 3
    val totalFreezesUsed: Int = 1
)

// ==========================================
// 4. GOLD ECONOMY & CUSTOM REAL-WORLD REWARDS
// ==========================================
@Entity(tableName = "custom_rewards")
data class CustomRewardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "default_user",
    val title: String,
    val goldPrice: Int,
    val iconEmoji: String,
    val category: String, // Leisure, Food & Drink, Travel, Wellness
    val timesRedeemed: Int = 0,
    val isAvailable: Boolean = true
)

// ==========================================
// 5. SOCIAL ACCOUNTABILITY & FRIEND FEED
// ==========================================
@Entity(tableName = "friend_feed_items")
data class FriendActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val friendName: String,
    val avatarEmoji: String,
    val actionDescription: String,
    val dimensionTag: String,
    val streakDays: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val isNudgedToday: Boolean = false,
    val isGiftReceived: Boolean = false
)

// ==========================================
// 6. MOTIVATIONAL INTERVIEWING AI LONG-TERM MEMORY
// ==========================================
@Entity(tableName = "ai_long_term_memories")
data class AiMemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "default_user",
    val patternType: String, // TEMPORAL_STRESS, MOTIVATIONAL_ANCHOR, ENERGY_DIP, GOAL_COMMITMENT
    val observedInsight: String,
    val recommendedMicroIntervention: String,
    val confidenceScore: Double = 0.92,
    val detectedAtTimestamp: Long = System.currentTimeMillis()
)
