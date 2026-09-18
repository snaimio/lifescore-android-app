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
    val topApps: List<AppUsageItemModel>,
    val hasUsagePermission: Boolean = true
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
    private val lifeScoreRepository: LifeScoreRepository,
    private val context: android.content.Context? = null
) : ScreenTimeRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private fun getTodayDate(): String = dateFormat.format(Date())

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
        return combine(
            screenTimeDao.getGoal(userId),
            screenTimeDao.getEntryForDate(userId, today)
        ) { goal, entry ->
            val dailyLimit = goal?.dailyLimitMinutes ?: 120
            val bonusMinutes = goal?.earnedBonusMinutes ?: 0
            val effectiveLimit = dailyLimit + bonusMinutes

            val realReport = context?.let { com.lifescore.app.core.util.RealUsageStatsHelper.getTodayUsageReport(it) }

            if (realReport != null && realReport.hasPermission) {
                ScreenTimeUsageSummary(
                    date = today,
                    totalMinutes = realReport.totalMinutes,
                    dailyLimitMinutes = dailyLimit,
                    bonusMinutesEarned = bonusMinutes,
                    effectiveLimitMinutes = effectiveLimit,
                    pickups = realReport.pickups,
                    isGoalMet = realReport.totalMinutes <= effectiveLimit,
                    socialMinutes = realReport.socialMinutes,
                    gamingMinutes = realReport.gamingMinutes,
                    videoMinutes = realReport.videoMinutes,
                    shoppingMinutes = realReport.shoppingMinutes,
                    topApps = realReport.topApps,
                    hasUsagePermission = true
                )
            } else if (entry != null) {
                ScreenTimeUsageSummary(
                    date = today,
                    totalMinutes = entry.totalMinutes,
                    dailyLimitMinutes = dailyLimit,
                    bonusMinutesEarned = bonusMinutes,
                    effectiveLimitMinutes = effectiveLimit,
                    pickups = entry.pickups,
                    isGoalMet = entry.totalMinutes <= effectiveLimit,
                    socialMinutes = entry.socialMediaMinutes,
                    gamingMinutes = entry.gamingMinutes,
                    videoMinutes = entry.videoMinutes,
                    shoppingMinutes = entry.shoppingMinutes,
                    topApps = emptyList(),
                    hasUsagePermission = false
                )
            } else {
                ScreenTimeUsageSummary(
                    date = today,
                    totalMinutes = 0,
                    dailyLimitMinutes = dailyLimit,
                    bonusMinutesEarned = bonusMinutes,
                    effectiveLimitMinutes = effectiveLimit,
                    pickups = 0,
                    isGoalMet = true,
                    socialMinutes = 0,
                    gamingMinutes = 0,
                    videoMinutes = 0,
                    shoppingMinutes = 0,
                    topApps = emptyList(),
                    hasUsagePermission = false
                )
            }
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
        val existing = screenTimeDao.getEntryForDate(userId, today).firstOrNull()
        val entry = existing?.copy(
            pickups = existing.pickups + 1,
            lastPickup = System.currentTimeMillis()
        ) ?: ScreenTimeEntry(
            userId = userId,
            date = today,
            totalMinutes = 0,
            pickups = 1,
            firstPickup = System.currentTimeMillis(),
            lastPickup = System.currentTimeMillis()
        )
        screenTimeDao.insertOrUpdateEntry(entry)
    }

    override suspend fun logAppUsage(userId: String, category: ScreenTimeActionType, minutes: Int) {
        val today = getTodayDate()
        val existing = screenTimeDao.getEntryForDate(userId, today).firstOrNull()
        val entry = existing?.copy(
            totalMinutes = existing.totalMinutes + minutes,
            socialMediaMinutes = if (category == ScreenTimeActionType.SOCIAL_MEDIA) existing.socialMediaMinutes + minutes else existing.socialMediaMinutes,
            gamingMinutes = if (category == ScreenTimeActionType.GAMING) existing.gamingMinutes + minutes else existing.gamingMinutes,
            videoMinutes = if (category == ScreenTimeActionType.VIDEO_STREAMING) existing.videoMinutes + minutes else existing.videoMinutes,
            shoppingMinutes = if (category == ScreenTimeActionType.SHOPPING) existing.shoppingMinutes + minutes else existing.shoppingMinutes
        ) ?: ScreenTimeEntry(
            userId = userId,
            date = today,
            totalMinutes = minutes,
            socialMediaMinutes = if (category == ScreenTimeActionType.SOCIAL_MEDIA) minutes else 0,
            gamingMinutes = if (category == ScreenTimeActionType.GAMING) minutes else 0,
            videoMinutes = if (category == ScreenTimeActionType.VIDEO_STREAMING) minutes else 0,
            shoppingMinutes = if (category == ScreenTimeActionType.SHOPPING) minutes else 0
        )
        screenTimeDao.insertOrUpdateEntry(entry)
    }

    override suspend fun grantMovementExercise(userId: String, exerciseName: String, reps: Int): Int {
        // SweatPass rule: 1 bonus minute per 5 reps / 15 seconds plank
        val earnedMinutes = (reps / 5).coerceAtLeast(1)
        screenTimeDao.addBonusMinutes(userId, earnedMinutes)
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
    }
}
