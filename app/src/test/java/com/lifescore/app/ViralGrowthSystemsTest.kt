package com.lifescore.app

import com.lifescore.app.data.local.dao.ViralGrowthDao
import com.lifescore.app.data.local.entity.*
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.data.repository.ViralGrowthRepositoryImpl
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.LifeTask
import com.lifescore.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ViralGrowthSystemsTest {

    private lateinit var mockLifeScoreRepo: LifeScoreRepository
    private var testUserXp = 500

    @Before
    fun setup() {
        mockLifeScoreRepo = object : LifeScoreRepository {
            override fun getUserProfile(): Flow<UserProfile> = flowOf(
                UserProfile(id = 1L, name = "Hero", currentXp = testUserXp, currentLevel = 3)
            )
            override suspend fun updateUserProfile(user: UserProfile) {
                testUserXp = user.currentXp
            }
            override fun getAllTasks(): Flow<List<LifeTask>> = flowOf(emptyList())
            override fun getTasksByDimension(dimension: DimensionType): Flow<List<LifeTask>> = flowOf(emptyList())
            override suspend fun addTask(title: String, dimension: DimensionType, points: Int): Long = 1L
            override suspend fun toggleTaskCompletion(task: LifeTask) {}
            override suspend fun deleteTask(task: LifeTask) {}
            override suspend fun seedInitialDataIfEmpty() {}
        }
    }

    @Test
    fun testReferralInviteLoopUnlocksPremiumOnThirdFriend() = runBlocking {
        var currentRef = ReferralEntity(
            userId = "default_user",
            referralCode = "HERO-7782",
            invitedCount = 2,
            targetInviteCount = 3,
            isOneMonthPremiumUnlocked = false,
            totalBonusXpEarned = 300,
            invitedFriendNamesCsv = "Sarah Jenkins,Marcus Vance"
        )

        val mockDao = object : ViralGrowthDao {
            override fun getReferralInfo(userId: String): Flow<ReferralEntity?> = flowOf(currentRef)
            override suspend fun insertOrUpdateReferral(referral: ReferralEntity) {
                currentRef = referral
            }
            override suspend fun incrementReferral(userId: String) {}
            override suspend fun unlockPremium(userId: String) {
                currentRef = currentRef.copy(isOneMonthPremiumUnlocked = true)
            }
            override fun getLeagueTier(userId: String): Flow<LeagueTierEntity?> = flowOf(null)
            override suspend fun insertOrUpdateLeague(league: LeagueTierEntity) {}
            override suspend fun addLeagueXp(userId: String, xp: Int) {}
            override fun getStreakInventory(userId: String): Flow<StreakInventoryEntity?> = flowOf(null)
            override suspend fun insertOrUpdateStreakInventory(inventory: StreakInventoryEntity) {}
            override suspend fun consumeStreakFreeze(userId: String) {}
            override suspend fun toggleFreezeShield(userId: String, isArmed: Boolean) {}
            override suspend fun startResurrectionQuest(userId: String, days: Int) {}
            override suspend fun advanceResurrectionProgress(userId: String) {}
            override fun getCustomRewards(userId: String): Flow<List<CustomRewardEntity>> = flowOf(emptyList())
            override suspend fun insertCustomReward(reward: CustomRewardEntity): Long = 1L
            override suspend fun redeemCustomReward(id: Long) {}
            override fun getFriendFeed(): Flow<List<FriendActivityEntity>> = flowOf(emptyList())
            override suspend fun insertFriendActivity(activity: FriendActivityEntity): Long = 1L
            override suspend fun insertFriendActivities(activities: List<FriendActivityEntity>) {}
            override suspend fun nudgeFriend(id: Long) {}
            override fun getAiMemories(userId: String): Flow<List<AiMemoryEntity>> = flowOf(emptyList())
            override suspend fun insertAiMemory(memory: AiMemoryEntity): Long = 1L
            override suspend fun insertAiMemories(memories: List<AiMemoryEntity>) {}
        }

        val repo = ViralGrowthRepositoryImpl(mockDao, mockLifeScoreRepo)
        val unlocked = repo.simulateInviteFriend("Jordan Lee")

        assertTrue(unlocked)
        assertEquals(3, currentRef.invitedCount)
        assertTrue(currentRef.isOneMonthPremiumUnlocked)
        assertTrue(currentRef.invitedFriendNamesCsv.contains("Jordan Lee"))
        assertEquals(650, testUserXp)
    }

    @Test
    fun testStreakVaultResurrectionQuestProgression() = runBlocking {
        var currentInv = StreakInventoryEntity(
            userId = "default_user",
            streakFreezesAvailable = 2,
            isFreezeShieldArmed = true,
            currentActiveStreakDays = 12,
            isResurrectionQuestActive = true,
            resurrectionStreakDaysToRecover = 15,
            resurrectionDayProgress = 2
        )

        val mockDao = object : ViralGrowthDao {
            override fun getReferralInfo(userId: String): Flow<ReferralEntity?> = flowOf(null)
            override suspend fun insertOrUpdateReferral(referral: ReferralEntity) {}
            override suspend fun incrementReferral(userId: String) {}
            override suspend fun unlockPremium(userId: String) {}
            override fun getLeagueTier(userId: String): Flow<LeagueTierEntity?> = flowOf(null)
            override suspend fun insertOrUpdateLeague(league: LeagueTierEntity) {}
            override suspend fun addLeagueXp(userId: String, xp: Int) {}
            override fun getStreakInventory(userId: String): Flow<StreakInventoryEntity?> = flowOf(currentInv)
            override suspend fun insertOrUpdateStreakInventory(inventory: StreakInventoryEntity) {
                currentInv = inventory
            }
            override suspend fun consumeStreakFreeze(userId: String) {}
            override suspend fun toggleFreezeShield(userId: String, isArmed: Boolean) {}
            override suspend fun startResurrectionQuest(userId: String, days: Int) {}
            override suspend fun advanceResurrectionProgress(userId: String) {
                currentInv = currentInv.copy(resurrectionDayProgress = currentInv.resurrectionDayProgress + 1)
            }
            override fun getCustomRewards(userId: String): Flow<List<CustomRewardEntity>> = flowOf(emptyList())
            override suspend fun insertCustomReward(reward: CustomRewardEntity): Long = 1L
            override suspend fun redeemCustomReward(id: Long) {}
            override fun getFriendFeed(): Flow<List<FriendActivityEntity>> = flowOf(emptyList())
            override suspend fun insertFriendActivity(activity: FriendActivityEntity): Long = 1L
            override suspend fun insertFriendActivities(activities: List<FriendActivityEntity>) {}
            override suspend fun nudgeFriend(id: Long) {}
            override fun getAiMemories(userId: String): Flow<List<AiMemoryEntity>> = flowOf(emptyList())
            override suspend fun insertAiMemory(memory: AiMemoryEntity): Long = 1L
            override suspend fun insertAiMemories(memories: List<AiMemoryEntity>) {}
        }

        val repo = ViralGrowthRepositoryImpl(mockDao, mockLifeScoreRepo)
        val completed = repo.advanceResurrectionProgress("default_user")

        assertTrue(completed)
        assertFalse(currentInv.isResurrectionQuestActive)
        assertEquals(18, currentInv.currentActiveStreakDays)
        assertEquals(600, testUserXp)
    }

    @Test
    fun testCustomRealWorldGoldRewardRedemption() = runBlocking {
        var redeemedCount = 0
        val mockDao = object : ViralGrowthDao {
            override fun getReferralInfo(userId: String): Flow<ReferralEntity?> = flowOf(null)
            override suspend fun insertOrUpdateReferral(referral: ReferralEntity) {}
            override suspend fun incrementReferral(userId: String) {}
            override suspend fun unlockPremium(userId: String) {}
            override fun getLeagueTier(userId: String): Flow<LeagueTierEntity?> = flowOf(null)
            override suspend fun insertOrUpdateLeague(league: LeagueTierEntity) {}
            override suspend fun addLeagueXp(userId: String, xp: Int) {}
            override fun getStreakInventory(userId: String): Flow<StreakInventoryEntity?> = flowOf(null)
            override suspend fun insertOrUpdateStreakInventory(inventory: StreakInventoryEntity) {}
            override suspend fun consumeStreakFreeze(userId: String) {}
            override suspend fun toggleFreezeShield(userId: String, isArmed: Boolean) {}
            override suspend fun startResurrectionQuest(userId: String, days: Int) {}
            override suspend fun advanceResurrectionProgress(userId: String) {}
            override fun getCustomRewards(userId: String): Flow<List<CustomRewardEntity>> = flowOf(emptyList())
            override suspend fun insertCustomReward(reward: CustomRewardEntity): Long = 1L
            override suspend fun redeemCustomReward(id: Long) {
                redeemedCount++
            }
            override fun getFriendFeed(): Flow<List<FriendActivityEntity>> = flowOf(emptyList())
            override suspend fun insertFriendActivity(activity: FriendActivityEntity): Long = 1L
            override suspend fun insertFriendActivities(activities: List<FriendActivityEntity>) {}
            override suspend fun nudgeFriend(id: Long) {}
            override fun getAiMemories(userId: String): Flow<List<AiMemoryEntity>> = flowOf(emptyList())
            override suspend fun insertAiMemory(memory: AiMemoryEntity): Long = 1L
            override suspend fun insertAiMemories(memories: List<AiMemoryEntity>) {}
        }

        val repo = ViralGrowthRepositoryImpl(mockDao, mockLifeScoreRepo)
        val success = repo.redeemCustomReward(1L, "default_user")

        assertTrue(success)
        assertEquals(1, redeemedCount)
        assertEquals(520, testUserXp)
    }
}
