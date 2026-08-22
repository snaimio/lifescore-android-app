package com.lifescore.app.data.local.dao

import androidx.room.*
import com.lifescore.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ViralGrowthDao {
    // 1. Referral & Growth Loops
    @Query("SELECT * FROM referrals WHERE userId = :userId")
    fun getReferralInfo(userId: String = "default_user"): Flow<ReferralEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateReferral(referral: ReferralEntity)

    @Query("UPDATE referrals SET invitedCount = invitedCount + 1, totalBonusXpEarned = totalBonusXpEarned + 150 WHERE userId = :userId")
    suspend fun incrementReferral(userId: String = "default_user")

    @Query("UPDATE referrals SET isOneMonthPremiumUnlocked = 1 WHERE userId = :userId")
    suspend fun unlockPremium(userId: String = "default_user")

    // 2. League Tiers
    @Query("SELECT * FROM league_tiers WHERE userId = :userId")
    fun getLeagueTier(userId: String = "default_user"): Flow<LeagueTierEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLeague(league: LeagueTierEntity)

    @Query("UPDATE league_tiers SET currentWeeklyXp = currentWeeklyXp + :xp, userRankInLeague = MAX(1, userRankInLeague - 1) WHERE userId = :userId")
    suspend fun addLeagueXp(userId: String = "default_user", xp: Int)

    // 3. Streak Freezes & Resurrection
    @Query("SELECT * FROM streak_inventory WHERE userId = :userId")
    fun getStreakInventory(userId: String = "default_user"): Flow<StreakInventoryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStreakInventory(inventory: StreakInventoryEntity)

    @Query("UPDATE streak_inventory SET streakFreezesAvailable = streakFreezesAvailable - 1, totalFreezesUsed = totalFreezesUsed + 1 WHERE userId = :userId AND streakFreezesAvailable > 0")
    suspend fun consumeStreakFreeze(userId: String = "default_user")

    @Query("UPDATE streak_inventory SET isFreezeShieldArmed = :isArmed WHERE userId = :userId")
    suspend fun toggleFreezeShield(userId: String = "default_user", isArmed: Boolean)

    @Query("UPDATE streak_inventory SET isResurrectionQuestActive = 1, resurrectionStreakDaysToRecover = :days, resurrectionDayProgress = 1 WHERE userId = :userId")
    suspend fun startResurrectionQuest(userId: String = "default_user", days: Int)

    @Query("UPDATE streak_inventory SET resurrectionDayProgress = resurrectionDayProgress + 1 WHERE userId = :userId")
    suspend fun advanceResurrectionProgress(userId: String = "default_user")

    // 4. Custom Real-World Gold Store
    @Query("SELECT * FROM custom_rewards WHERE userId = :userId ORDER BY goldPrice ASC")
    fun getCustomRewards(userId: String = "default_user"): Flow<List<CustomRewardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomReward(reward: CustomRewardEntity): Long

    @Query("UPDATE custom_rewards SET timesRedeemed = timesRedeemed + 1 WHERE id = :id")
    suspend fun redeemCustomReward(id: Long)

    // 5. Social Feed & Accountability
    @Query("SELECT * FROM friend_feed_items ORDER BY timestamp DESC")
    fun getFriendFeed(): Flow<List<FriendActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendActivity(activity: FriendActivityEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendActivities(activities: List<FriendActivityEntity>)

    @Query("UPDATE friend_feed_items SET isNudgedToday = 1 WHERE id = :id")
    suspend fun nudgeFriend(id: Long)

    // 6. AI Motivational Interviewing Long-Term Memory
    @Query("SELECT * FROM ai_long_term_memories WHERE userId = :userId ORDER BY detectedAtTimestamp DESC")
    fun getAiMemories(userId: String = "default_user"): Flow<List<AiMemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiMemory(memory: AiMemoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiMemories(memories: List<AiMemoryEntity>)
}
