package com.lifescore.app.data.repository

import com.lifescore.app.data.local.dao.ViralGrowthDao
import com.lifescore.app.data.local.entity.*
import com.lifescore.app.domain.model.UserProfile
import kotlinx.coroutines.flow.*

interface ViralGrowthRepository {
    // 1. Referrals
    fun getReferralInfo(userId: String = "default_user"): Flow<ReferralEntity>
    suspend fun simulateInviteFriend(friendName: String, userId: String = "default_user"): Boolean
    suspend fun claimReferralReward(userId: String = "default_user")

    // 2. 10-Tier Leagues
    fun getLeagueTier(userId: String = "default_user"): Flow<LeagueTierEntity>
    suspend fun contributeLeagueXp(xp: Int, userId: String = "default_user")

    // 3. Streak Freezes & Resurrection
    fun getStreakInventory(userId: String = "default_user"): Flow<StreakInventoryEntity>
    suspend fun toggleFreezeShield(isArmed: Boolean, userId: String = "default_user")
    suspend fun startResurrectionQuest(lostStreakDays: Int, userId: String = "default_user")
    suspend fun advanceResurrectionProgress(userId: String = "default_user"): Boolean // true if completed

    // 4. Custom Rewards & Gold
    fun getCustomRewards(userId: String = "default_user"): Flow<List<CustomRewardEntity>>
    suspend fun createCustomReward(title: String, goldPrice: Int, emoji: String, category: String, userId: String = "default_user"): Long
    suspend fun redeemCustomReward(rewardId: Long, userId: String = "default_user"): Boolean

    // 5. Social Feed & Nudges
    fun getFriendFeed(): Flow<List<FriendActivityEntity>>
    suspend fun nudgeFriend(activityId: Long): Boolean
    suspend fun giftStreakFreeze(friendName: String, userId: String = "default_user"): Boolean

    // 6. AI Motivational Memory
    fun getAiMemories(userId: String = "default_user"): Flow<List<AiMemoryEntity>>
    suspend fun recordAiObservation(patternType: String, insight: String, intervention: String, userId: String = "default_user")
    suspend fun seedInitialViralDataIfEmpty()
}

class ViralGrowthRepositoryImpl(
    private val viralDao: ViralGrowthDao,
    private val lifeScoreRepository: LifeScoreRepository
) : ViralGrowthRepository {

    private suspend fun awardXp(amount: Int) {
        try {
            val user = lifeScoreRepository.getUserProfile().firstOrNull()
            if (user != null) {
                lifeScoreRepository.updateUserProfile(user.copy(currentXp = user.currentXp + amount))
            }
        } catch (_: Exception) {}
    }

    override fun getReferralInfo(userId: String): Flow<ReferralEntity> {
        return viralDao.getReferralInfo(userId).map { ref ->
            ref ?: ReferralEntity(
                userId = userId,
                referralCode = "HERO-7782",
                invitedCount = 2,
                targetInviteCount = 3,
                isOneMonthPremiumUnlocked = false,
                totalBonusXpEarned = 300,
                invitedFriendNamesCsv = "Sarah Jenkins,Marcus Vance"
            )
        }
    }

    override suspend fun simulateInviteFriend(friendName: String, userId: String): Boolean {
        val current = getReferralInfo(userId).first()
        val newCount = current.invitedCount + 1
        val unlocked = newCount >= current.targetInviteCount
        val updatedNames = if (current.invitedFriendNamesCsv.isEmpty()) friendName else "${current.invitedFriendNamesCsv},$friendName"

        viralDao.insertOrUpdateReferral(
            current.copy(
                invitedCount = newCount,
                isOneMonthPremiumUnlocked = unlocked || current.isOneMonthPremiumUnlocked,
                totalBonusXpEarned = current.totalBonusXpEarned + 150,
                invitedFriendNamesCsv = updatedNames
            )
        )
        awardXp(150)
        return unlocked
    }

    override suspend fun claimReferralReward(userId: String) {
        viralDao.unlockPremium(userId)
        awardXp(200)
    }

    override fun getLeagueTier(userId: String): Flow<LeagueTierEntity> {
        return viralDao.getLeagueTier(userId).map { tier ->
            tier ?: LeagueTierEntity(
                userId = userId,
                tierName = "Gold II",
                tierLevel = 5,
                currentWeeklyXp = 1450,
                userRankInLeague = 4,
                totalCompetitorsInPool = 30,
                promotionCutoffRank = 10,
                relegationCutoffRank = 25,
                seasonDaysRemaining = 3,
                tierBadgeEmoji = "🥇"
            )
        }
    }

    override suspend fun contributeLeagueXp(xp: Int, userId: String) {
        viralDao.addLeagueXp(userId, xp)
    }

    override fun getStreakInventory(userId: String): Flow<StreakInventoryEntity> {
        return viralDao.getStreakInventory(userId).map { inv ->
            inv ?: StreakInventoryEntity(
                userId = userId,
                streakFreezesAvailable = 2,
                isFreezeShieldArmed = true,
                currentActiveStreakDays = 12,
                isResurrectionQuestActive = false,
                resurrectionStreakDaysToRecover = 0,
                resurrectionDayProgress = 0,
                totalFreezesUsed = 1
            )
        }
    }

    override suspend fun toggleFreezeShield(isArmed: Boolean, userId: String) {
        viralDao.toggleFreezeShield(userId, isArmed)
    }

    override suspend fun startResurrectionQuest(lostStreakDays: Int, userId: String) {
        viralDao.startResurrectionQuest(userId, lostStreakDays)
    }

    override suspend fun advanceResurrectionProgress(userId: String): Boolean {
        val inv = getStreakInventory(userId).first()
        val nextProgress = inv.resurrectionDayProgress + 1
        if (nextProgress >= 3) {
            // Resurrection complete!
            viralDao.insertOrUpdateStreakInventory(
                inv.copy(
                    isResurrectionQuestActive = false,
                    currentActiveStreakDays = inv.resurrectionStreakDaysToRecover + 3,
                    resurrectionStreakDaysToRecover = 0,
                    resurrectionDayProgress = 0
                )
            )
            awardXp(100)
            return true
        } else {
            viralDao.advanceResurrectionProgress(userId)
            awardXp(30)
            return false
        }
    }

    override fun getCustomRewards(userId: String): Flow<List<CustomRewardEntity>> {
        return viralDao.getCustomRewards(userId).map { list ->
            if (list.isEmpty()) {
                listOf(
                    CustomRewardEntity(1, userId, "1 Hour Guilt-Free Gaming", 75, "🎮", "Leisure", 2),
                    CustomRewardEntity(2, userId, "Specialty Artisan Coffee & Pastry", 50, "☕", "Food & Drink", 4),
                    CustomRewardEntity(3, userId, "Full Spa & Sauna Recovery Day", 300, "🧖", "Wellness", 1),
                    CustomRewardEntity(4, userId, "Weekend Road Trip Outing", 500, "🚗", "Travel", 0)
                )
            } else list
        }
    }

    override suspend fun createCustomReward(
        title: String,
        goldPrice: Int,
        emoji: String,
        category: String,
        userId: String
    ): Long {
        return viralDao.insertCustomReward(
            CustomRewardEntity(
                userId = userId,
                title = title,
                goldPrice = goldPrice,
                iconEmoji = emoji,
                category = category
            )
        )
    }

    override suspend fun redeemCustomReward(rewardId: Long, userId: String): Boolean {
        viralDao.redeemCustomReward(rewardId)
        awardXp(20)
        return true
    }

    override fun getFriendFeed(): Flow<List<FriendActivityEntity>> {
        return viralDao.getFriendFeed().map { list ->
            if (list.isEmpty()) {
                listOf(
                    FriendActivityEntity(1, "Elena Rostova", "🏹", "Completed 45m Deep Work Sprint", "CAREER", 14, System.currentTimeMillis() - 1800000),
                    FriendActivityEntity(2, "Marcus Vance", "🛡️", "Crushed 10k steps & 2L hydration", "HEALTH", 9, System.currentTimeMillis() - 3600000),
                    FriendActivityEntity(3, "Dr. Tara Brach", "🧘", "Finished 20m Breathwork & Yoga Nidra", "MENTAL_HEALTH", 21, System.currentTimeMillis() - 7200000),
                    FriendActivityEntity(4, "Alex Mercer", "🧙‍♂️", "Advanced to Diamond League Tier I", "ACHIEVEMENT", 30, System.currentTimeMillis() - 14400000)
                )
            } else list
        }
    }

    override suspend fun nudgeFriend(activityId: Long): Boolean {
        viralDao.nudgeFriend(activityId)
        awardXp(10)
        return true
    }

    override suspend fun giftStreakFreeze(friendName: String, userId: String): Boolean {
        val inv = getStreakInventory(userId).first()
        if (inv.streakFreezesAvailable > 0) {
            viralDao.consumeStreakFreeze(userId)
            awardXp(30)
            return true
        }
        return false
    }

    override fun getAiMemories(userId: String): Flow<List<AiMemoryEntity>> {
        return viralDao.getAiMemories(userId).map { list ->
            if (list.isEmpty()) {
                listOf(
                    AiMemoryEntity(
                        id = 1,
                        userId = userId,
                        patternType = "TEMPORAL_STRESS",
                        observedInsight = "Stress levels elevate on Thursday afternoons following back-to-back roadmap meetings.",
                        recommendedMicroIntervention = "Prompt user with a 3-minute 4-7-8 Breathing Pacer at 1:45 PM Thursdays."
                    ),
                    AiMemoryEntity(
                        id = 2,
                        userId = userId,
                        patternType = "MOTIVATIONAL_ANCHOR",
                        observedInsight = "Primary intrinsic drive is building autonomous health sovereignty and executive clarity.",
                        recommendedMicroIntervention = "Frame daily habits as identity votes toward Sovereignty rather than external obligations."
                    ),
                    AiMemoryEntity(
                        id = 3,
                        userId = userId,
                        patternType = "ENERGY_DIP",
                        observedInsight = "Cognitive endurance dips by 24% when daily hydration drops below 1.5L before 2 PM.",
                        recommendedMicroIntervention = "Trigger a 500ml hydration reminder at 11:30 AM."
                    )
                )
            } else list
        }
    }

    override suspend fun recordAiObservation(
        patternType: String,
        insight: String,
        intervention: String,
        userId: String
    ) {
        viralDao.insertAiMemory(
            AiMemoryEntity(
                userId = userId,
                patternType = patternType,
                observedInsight = insight,
                recommendedMicroIntervention = intervention
            )
        )
    }

    override suspend fun seedInitialViralDataIfEmpty() {
        val friends = listOf(
            FriendActivityEntity(1, "Elena Rostova", "🏹", "Completed 45m Deep Work Sprint", "CAREER", 14, System.currentTimeMillis() - 1800000),
            FriendActivityEntity(2, "Marcus Vance", "🛡️", "Crushed 10k steps & 2L hydration", "HEALTH", 9, System.currentTimeMillis() - 3600000)
        )
        viralDao.insertFriendActivities(friends)
    }
}
