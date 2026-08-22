package com.lifescore.app.data.repository

import com.lifescore.app.data.local.dao.ScreenTimeDao
import com.lifescore.app.data.local.entity.*
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.UserProfile
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

data class AppUsageItemModel(
    val appName: String,
    val minutes: Int,
    val category: ScreenTimeActionType,
    val iconEmoji: String,
    val isBlocked: Boolean = false
)

data class ScreenTimeUsageSummary(
    val date: String,
    val totalMinutes: Int,
    val dailyLimitMinutes: Int,
    val bonusMinutesEarned: Int,
    val effectiveLimitMinutes: Int,
    val pickups: Int,
    val isGoalMet: Boolean,
    val socialMinutes: Int,
    val gamingMinutes: Int,
    val videoMinutes: Int,
    val shoppingMinutes: Int,
    val topApps: List<AppUsageItemModel>
)

interface ScreenTimeRepository {
    fun getTodayUsage(userId: String): Flow<ScreenTimeUsageSummary>
    fun getGoal(userId: String): Flow<ScreenTimeGoalEntity>
    fun getRecentHistory(userId: String): Flow<List<ScreenTimeEntry>>
    fun getActiveChallenges(userId: String): Flow<List<ScreenTimeChallenge>>
    fun getThoughtBreakLogs(userId: String): Flow<List<ThoughtBreakLog>>
    
    suspend fun updateDailyLimit(userId: String, limitMinutes: Int)
    suspend fun toggleFocusMode(userId: String, isEnabled: Boolean)
    suspend fun updateIntentionalDelay(userId: String, delaySeconds: Int)
    suspend fun recordPickup(userId: String)
    suspend fun logAppUsage(userId: String, category: ScreenTimeActionType, minutes: Int)
    suspend fun grantMovementExercise(userId: String, exerciseName: String, reps: Int): Int
    suspend fun startSession(userId: String, sessionType: String, durationMinutes: Int): Long
    suspend fun completeSession(sessionId: Long, userId: String, durationMinutes: Int, wasSuccessful: Boolean)
    suspend fun joinChallenge(userId: String, challengeType: String, title: String, targetDays: Int, xpReward: Int)
    suspend fun advanceChallengeProgress(challenge: ScreenTimeChallenge)
    suspend fun saveThoughtBreak(
        userId: String,
        automaticThought: String,
        cognitiveDistortion: String,
        evidenceAgainst: String,
        reframedThought: String,
        reliefRating: Int
    )
}

class ScreenTimeRepositoryImpl(
    private val screenTimeDao: ScreenTimeDao,
    private val lifeScoreRepository: LifeScoreRepository
) : ScreenTimeRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private fun getTodayDate(): String = dateFormat.format(Date())

    private suspend fun awardXp(amount: Int) {
        try {
            val user = lifeScoreRepository.getUserProfile().firstOrNull()
            if (user != null) {
                lifeScoreRepository.updateUserProfile(user.copy(currentXp = user.currentXp + amount))
            }
        } catch (_: Exception) {}
    }

    override fun getGoal(userId: String): Flow<ScreenTimeGoalEntity> {
        return screenTimeDao.getGoal(userId).map { goal ->
            goal ?: ScreenTimeGoalEntity(
                userId = userId,
                dailyLimitMinutes = 120,
                isFocusModeEnabled = false,
                intentionalDelaySeconds = 10,
                earnedBonusMinutes = 0
            )
        }
    }

    override fun getTodayUsage(userId: String): Flow<ScreenTimeUsageSummary> {
        val today = getTodayDate()
        return screenTimeDao.getEntryForDate(userId, today).map { entry ->
            val todayEntry = entry ?: ScreenTimeEntry(
                userId = userId,
                date = today,
                totalMinutes = 85,
                socialMediaMinutes = 45,
                gamingMinutes = 20,
                videoMinutes = 15,
                shoppingMinutes = 5,
                pickups = 34,
                firstPickup = System.currentTimeMillis() - 8 * 3600 * 1000L,
                lastPickup = System.currentTimeMillis(),
                screenTimeGoalMet = true
            )

            val sampleTopApps = listOf(
                AppUsageItemModel("Instagram", todayEntry.socialMediaMinutes.coerceAtLeast(30), ScreenTimeActionType.SOCIAL_MEDIA, "📸"),
                AppUsageItemModel("YouTube", todayEntry.videoMinutes.coerceAtLeast(25), ScreenTimeActionType.VIDEO_STREAMING, "▶️"),
                AppUsageItemModel("TikTok", 15, ScreenTimeActionType.SOCIAL_MEDIA, "🎵"),
                AppUsageItemModel("Clash Royale", todayEntry.gamingMinutes.coerceAtLeast(10), ScreenTimeActionType.GAMING, "⚔️"),
                AppUsageItemModel("Amazon", todayEntry.shoppingMinutes.coerceAtLeast(5), ScreenTimeActionType.SHOPPING, "🛒")
            )

            ScreenTimeUsageSummary(
                date = today,
                totalMinutes = todayEntry.totalMinutes,
                dailyLimitMinutes = 120,
                bonusMinutesEarned = 15,
                effectiveLimitMinutes = 135,
                pickups = todayEntry.pickups,
                isGoalMet = todayEntry.totalMinutes <= 135,
                socialMinutes = todayEntry.socialMediaMinutes,
                gamingMinutes = todayEntry.gamingMinutes,
                videoMinutes = todayEntry.videoMinutes,
                shoppingMinutes = todayEntry.shoppingMinutes,
                topApps = sampleTopApps
            )
        }
    }

    override fun getRecentHistory(userId: String): Flow<List<ScreenTimeEntry>> {
        return screenTimeDao.getRecentEntries(userId)
    }

    override fun getActiveChallenges(userId: String): Flow<List<ScreenTimeChallenge>> {
        return screenTimeDao.getActiveChallenges(userId).map { list ->
            if (list.isEmpty()) {
                listOf(
                    ScreenTimeChallenge(
                        id = 1,
                        userId = userId,
                        challengeType = "DETOX",
                        title = "7-Day Digital Sunset (No screens after 9 PM)",
                        targetDays = 7,
                        currentDay = 3,
                        isActive = true,
                        xpReward = 150
                    ),
                    ScreenTimeChallenge(
                        id = 2,
                        userId = userId,
                        challengeType = "PICKUP_REDUCTION",
                        title = "Under 40 Pickups Challenge",
                        targetDays = 5,
                        currentDay = 2,
                        isActive = true,
                        xpReward = 100
                    )
                )
            } else {
                list
            }
        }
    }

    override fun getThoughtBreakLogs(userId: String): Flow<List<ThoughtBreakLog>> {
        return screenTimeDao.getThoughtBreakLogs(userId)
    }

    override suspend fun updateDailyLimit(userId: String, limitMinutes: Int) {
        val currentGoal = ScreenTimeGoalEntity(
            userId = userId,
            dailyLimitMinutes = limitMinutes
        )
        screenTimeDao.setGoal(currentGoal)
    }

    override suspend fun toggleFocusMode(userId: String, isEnabled: Boolean) {
        val currentGoal = ScreenTimeGoalEntity(
            userId = userId,
            isFocusModeEnabled = isEnabled
        )
        screenTimeDao.setGoal(currentGoal)
    }

    override suspend fun updateIntentionalDelay(userId: String, delaySeconds: Int) {
        val currentGoal = ScreenTimeGoalEntity(
            userId = userId,
            intentionalDelaySeconds = delaySeconds
        )
        screenTimeDao.setGoal(currentGoal)
    }

    override suspend fun recordPickup(userId: String) {
        val today = getTodayDate()
        val entry = ScreenTimeEntry(
            userId = userId,
            date = today,
            totalMinutes = 90,
            pickups = 35,
            lastPickup = System.currentTimeMillis()
        )
        screenTimeDao.insertOrUpdateEntry(entry)
    }

    override suspend fun logAppUsage(userId: String, category: ScreenTimeActionType, minutes: Int) {
        val today = getTodayDate()
        val entry = ScreenTimeEntry(
            userId = userId,
            date = today,
            totalMinutes = minutes,
            socialMediaMinutes = if (category == ScreenTimeActionType.SOCIAL_MEDIA) minutes else 0
        )
        screenTimeDao.insertOrUpdateEntry(entry)
    }

    override suspend fun grantMovementExercise(userId: String, exerciseName: String, reps: Int): Int {
        // SweatPass rule: 1 bonus minute per 5 reps / 15 seconds plank
        val earnedMinutes = (reps / 5).coerceAtLeast(1)
        screenTimeDao.addBonusMinutes(userId, earnedMinutes)

        // Award Fitness & Mental Health XP
        val xpBonus = earnedMinutes * 15
        awardXp(xpBonus)

        return earnedMinutes
    }

    override suspend fun startSession(userId: String, sessionType: String, durationMinutes: Int): Long {
        val session = ScreenTimeSession(
            userId = userId,
            sessionType = sessionType,
            startTime = System.currentTimeMillis(),
            durationMinutes = durationMinutes
        )
        return screenTimeDao.insertSession(session)
    }

    override suspend fun completeSession(sessionId: Long, userId: String, durationMinutes: Int, wasSuccessful: Boolean) {
        val session = ScreenTimeSession(
            id = sessionId,
            userId = userId,
            sessionType = "FOCUS",
            startTime = System.currentTimeMillis() - durationMinutes * 60 * 1000L,
            endTime = System.currentTimeMillis(),
            durationMinutes = durationMinutes,
            wasSuccessful = wasSuccessful
        )
        screenTimeDao.updateSession(session)

        if (wasSuccessful) {
            val xpEarned = durationMinutes * 2 // e.g. 25 min focus = 50 XP
            awardXp(xpEarned)
        }
    }

    override suspend fun joinChallenge(
        userId: String,
        challengeType: String,
        title: String,
        targetDays: Int,
        xpReward: Int
    ) {
        val challenge = ScreenTimeChallenge(
            userId = userId,
            challengeType = challengeType,
            title = title,
            targetDays = targetDays,
            currentDay = 1,
            isActive = true,
            xpReward = xpReward
        )
        screenTimeDao.insertChallenge(challenge)
    }

    override suspend fun advanceChallengeProgress(challenge: ScreenTimeChallenge) {
        val nextDay = challenge.currentDay + 1
        val isCompleted = nextDay >= challenge.targetDays
        val updated = challenge.copy(
            currentDay = nextDay,
            isActive = !isCompleted,
            completedAt = if (isCompleted) System.currentTimeMillis() else null
        )
        screenTimeDao.updateChallenge(updated)

        if (isCompleted) {
            awardXp(challenge.xpReward)
        }
    }

    override suspend fun saveThoughtBreak(
        userId: String,
        automaticThought: String,
        cognitiveDistortion: String,
        evidenceAgainst: String,
        reframedThought: String,
        reliefRating: Int
    ) {
        val log = ThoughtBreakLog(
            userId = userId,
            automaticThought = automaticThought,
            cognitiveDistortion = cognitiveDistortion,
            evidenceAgainst = evidenceAgainst,
            reframedThought = reframedThought,
            emotionalReliefRating = reliefRating
        )
        screenTimeDao.insertThoughtBreakLog(log)
        // Award Mental Health XP
        awardXp(40)
    }
}
