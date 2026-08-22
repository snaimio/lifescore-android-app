package com.lifescore.app.data.repository

import com.lifescore.app.data.local.dao.*
import com.lifescore.app.data.local.entity.*
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.selfimprovement.*
import kotlinx.coroutines.flow.*

// ==========================================
// 1. BOOK SUMMARY REPOSITORY
// ==========================================
interface BookSummaryRepository {
    fun getAllBooksWithProgress(userId: String = "default_user"): Flow<List<Pair<BookSummary, BookSummaryProgressEntity?>>>
    fun getBookDetailWithProgress(bookId: String, userId: String = "default_user"): Flow<Pair<BookSummary?, BookSummaryProgressEntity?>>
    suspend fun toggleBookmark(bookId: String, userId: String = "default_user")
    suspend fun markBookCompleted(bookId: String, userId: String = "default_user"): Int // Returns XP
    suspend fun completeAppliedQuest(bookId: String, userId: String = "default_user"): Int // Returns XP
    suspend fun saveChapterProgress(bookId: String, chapterIndex: Int, userId: String = "default_user")
}

class BookSummaryRepositoryImpl(
    private val bookSummaryDao: BookSummaryDao,
    private val lifeScoreRepository: LifeScoreRepository
) : BookSummaryRepository {

    private suspend fun awardXp(amount: Int) {
        try {
            val user = lifeScoreRepository.getUserProfile().firstOrNull()
            if (user != null) {
                lifeScoreRepository.updateUserProfile(user.copy(currentXp = user.currentXp + amount))
            }
        } catch (_: Exception) {}
    }

    override fun getAllBooksWithProgress(userId: String): Flow<List<Pair<BookSummary, BookSummaryProgressEntity?>>> {
        return bookSummaryDao.getAllProgress(userId).map { progressList ->
            val map = progressList.associateBy { it.bookId }
            BookSummariesCatalog.books.map { book ->
                Pair(book, map[book.id])
            }
        }
    }

    override fun getBookDetailWithProgress(bookId: String, userId: String): Flow<Pair<BookSummary?, BookSummaryProgressEntity?>> {
        val book = BookSummariesCatalog.books.find { it.id == bookId }
        return bookSummaryDao.getProgressForBook(bookId, userId).map { progress ->
            Pair(book, progress)
        }
    }

    override suspend fun toggleBookmark(bookId: String, userId: String) {
        val current = bookSummaryDao.getProgressForBook(bookId, userId).firstOrNull()
            ?: BookSummaryProgressEntity(bookId = bookId, userId = userId)
        bookSummaryDao.insertOrUpdate(current.copy(isBookmarked = !current.isBookmarked))
    }

    override suspend fun markBookCompleted(bookId: String, userId: String): Int {
        val current = bookSummaryDao.getProgressForBook(bookId, userId).firstOrNull()
            ?: BookSummaryProgressEntity(bookId = bookId, userId = userId)
        if (!current.isCompleted) {
            bookSummaryDao.insertOrUpdate(current.copy(isCompleted = true, lastReadTimestamp = System.currentTimeMillis()))
            awardXp(75)
            return 75
        }
        return 0
    }

    override suspend fun completeAppliedQuest(bookId: String, userId: String): Int {
        val current = bookSummaryDao.getProgressForBook(bookId, userId).firstOrNull()
            ?: BookSummaryProgressEntity(bookId = bookId, userId = userId)
        if (!current.appliedQuestCompleted) {
            bookSummaryDao.insertOrUpdate(current.copy(appliedQuestCompleted = true))
            awardXp(75)
            return 75
        }
        return 0
    }

    override suspend fun saveChapterProgress(bookId: String, chapterIndex: Int, userId: String) {
        val current = bookSummaryDao.getProgressForBook(bookId, userId).firstOrNull()
            ?: BookSummaryProgressEntity(bookId = bookId, userId = userId)
        bookSummaryDao.insertOrUpdate(current.copy(lastReadChapterIndex = chapterIndex, lastReadTimestamp = System.currentTimeMillis()))
    }
}

// ==========================================
// 2. DAILY GROWTH REPOSITORY (15-MIN SESSIONS)
// ==========================================
interface DailyGrowthRepository {
    fun getSessionForDay(dayNumber: Int, userId: String = "default_user"): Flow<Pair<DailyGrowthSession, DailyGrowthProgressEntity?>>
    fun getAllProgress(userId: String = "default_user"): Flow<List<DailyGrowthProgressEntity>>
    suspend fun completeGrowthSession(sessionId: Int, journalReflection: String, dateIso: String, userId: String = "default_user"): Int // Returns XP
    suspend fun completeActionChallenge(sessionId: Int, userId: String = "default_user"): Int // Returns XP
}

class DailyGrowthRepositoryImpl(
    private val dailyGrowthDao: DailyGrowthDao,
    private val lifeScoreRepository: LifeScoreRepository
) : DailyGrowthRepository {

    private suspend fun awardXp(amount: Int) {
        try {
            val user = lifeScoreRepository.getUserProfile().firstOrNull()
            if (user != null) {
                lifeScoreRepository.updateUserProfile(user.copy(currentXp = user.currentXp + amount))
            }
        } catch (_: Exception) {}
    }

    override fun getSessionForDay(dayNumber: Int, userId: String): Flow<Pair<DailyGrowthSession, DailyGrowthProgressEntity?>> {
        val session = DailyGrowthCurriculum.getSessionForDay(dayNumber)
        return dailyGrowthDao.getProgressForSession(session.dayNumber, userId).map { progress ->
            Pair(session, progress)
        }
    }

    override fun getAllProgress(userId: String): Flow<List<DailyGrowthProgressEntity>> {
        return dailyGrowthDao.getAllProgress(userId)
    }

    override suspend fun completeGrowthSession(sessionId: Int, journalReflection: String, dateIso: String, userId: String): Int {
        val current = dailyGrowthDao.getProgressForSession(sessionId, userId).firstOrNull()
            ?: DailyGrowthProgressEntity(sessionId = sessionId, userId = userId, dateIso = dateIso)
        val updated = current.copy(
            isCompleted = true,
            journalReflection = journalReflection,
            completedAt = System.currentTimeMillis()
        )
        dailyGrowthDao.insertOrUpdate(updated)
        awardXp(50)
        return 50
    }

    override suspend fun completeActionChallenge(sessionId: Int, userId: String): Int {
        val current = dailyGrowthDao.getProgressForSession(sessionId, userId).firstOrNull() ?: return 0
        if (!current.actionItemCompleted) {
            dailyGrowthDao.insertOrUpdate(current.copy(actionItemCompleted = true))
            awardXp(50)
            return 50
        }
        return 0
    }
}

// ==========================================
// 3. GAMIFIED FOCUS REPOSITORY (FOREST STYLE)
// ==========================================
data class FocusStats(
    val totalFocusMinutes: Int = 0,
    val totalTreesPlanted: Int = 0,
    val recentSessions: List<FocusSessionEntity> = emptyList()
)

interface FocusRepository {
    fun getFocusStats(userId: String = "default_user"): Flow<FocusStats>
    suspend fun recordCompletedSession(
        durationMinutes: Int,
        dimensionTag: DimensionType,
        treeType: TreeType,
        notes: String = "",
        userId: String = "default_user"
    ): Int // Returns XP

    suspend fun recordFailedSession(
        durationMinutes: Int,
        dimensionTag: DimensionType,
        treeType: TreeType,
        userId: String = "default_user"
    )
}

class FocusRepositoryImpl(
    private val focusDao: FocusDao,
    private val lifeScoreRepository: LifeScoreRepository
) : FocusRepository {

    private suspend fun awardXp(amount: Int) {
        try {
            val user = lifeScoreRepository.getUserProfile().firstOrNull()
            if (user != null) {
                lifeScoreRepository.updateUserProfile(user.copy(currentXp = user.currentXp + amount))
            }
        } catch (_: Exception) {}
    }

    override fun getFocusStats(userId: String): Flow<FocusStats> {
        return combine(
            focusDao.getTotalFocusMinutes(userId),
            focusDao.getTotalTreesPlanted(userId),
            focusDao.getAllFocusSessions(userId)
        ) { minutes, trees, sessions ->
            FocusStats(
                totalFocusMinutes = minutes ?: 0,
                totalTreesPlanted = trees,
                recentSessions = sessions
            )
        }
    }

    override suspend fun recordCompletedSession(
        durationMinutes: Int,
        dimensionTag: DimensionType,
        treeType: TreeType,
        notes: String,
        userId: String
    ): Int {
        focusDao.insertSession(
            FocusSessionEntity(
                userId = userId,
                durationMinutes = durationMinutes,
                dimensionTag = dimensionTag,
                treeType = treeType,
                wasSuccessful = true,
                notes = notes,
                timestamp = System.currentTimeMillis()
            )
        )
        val xp = (durationMinutes * 1.5).toInt().coerceAtLeast(20)
        awardXp(xp)
        return xp
    }

    override suspend fun recordFailedSession(
        durationMinutes: Int,
        dimensionTag: DimensionType,
        treeType: TreeType,
        userId: String
    ) {
        focusDao.insertSession(
            FocusSessionEntity(
                userId = userId,
                durationMinutes = durationMinutes,
                dimensionTag = dimensionTag,
                treeType = treeType,
                wasSuccessful = false,
                notes = "Tree withered - session ended early",
                timestamp = System.currentTimeMillis()
            )
        )
    }
}

// ==========================================
// 4. MOOD & EMOTIONAL REPOSITORY (REFRAME STYLE)
// ==========================================
data class MoodAnalytics(
    val dominantMood: MoodType = MoodType.CALM,
    val averageEnergy: Double = 7.0,
    val averageStress: Double = 3.5,
    val totalCheckIns: Int = 0,
    val topFactors: List<String> = emptyList(),
    val logs: List<MoodLogEntity> = emptyList()
)

interface MoodRepository {
    fun getAllMoodLogs(userId: String = "default_user"): Flow<List<MoodLogEntity>>
    fun getMoodAnalytics(userId: String = "default_user"): Flow<MoodAnalytics>
    suspend fun logMood(
        mood: MoodType,
        energyLevel: Int,
        stressLevel: Int,
        factors: List<String>,
        note: String = "",
        dateIso: String,
        userId: String = "default_user"
    ): Long
}

class MoodRepositoryImpl(
    private val moodDao: MoodDao,
    private val lifeScoreRepository: LifeScoreRepository
) : MoodRepository {

    private suspend fun awardXp(amount: Int) {
        try {
            val user = lifeScoreRepository.getUserProfile().firstOrNull()
            if (user != null) {
                lifeScoreRepository.updateUserProfile(user.copy(currentXp = user.currentXp + amount))
            }
        } catch (_: Exception) {}
    }

    override fun getAllMoodLogs(userId: String): Flow<List<MoodLogEntity>> {
        return moodDao.getAllMoodLogs(userId)
    }

    override fun getMoodAnalytics(userId: String): Flow<MoodAnalytics> {
        return moodDao.getAllMoodLogs(userId).map { logs ->
            if (logs.isEmpty()) {
                MoodAnalytics()
            } else {
                val dominant = logs.groupBy { it.mood }.maxByOrNull { it.value.size }?.key ?: MoodType.CALM
                val avgEnergy = logs.map { it.energyLevel }.average()
                val avgStress = logs.map { it.stressLevel }.average()
                val factorCounts = logs.flatMap { it.factorTags.split(",") }
                    .filter { it.isNotBlank() }
                    .groupingBy { it.trim() }
                    .eachCount()
                    .toList()
                    .sortedByDescending { it.second }
                    .take(5)
                    .map { it.first }

                MoodAnalytics(
                    dominantMood = dominant,
                    averageEnergy = avgEnergy,
                    averageStress = avgStress,
                    totalCheckIns = logs.size,
                    topFactors = factorCounts,
                    logs = logs
                )
            }
        }
    }

    override suspend fun logMood(
        mood: MoodType,
        energyLevel: Int,
        stressLevel: Int,
        factors: List<String>,
        note: String,
        dateIso: String,
        userId: String
    ): Long {
        val id = moodDao.insertMoodLog(
            MoodLogEntity(
                userId = userId,
                mood = mood,
                energyLevel = energyLevel,
                stressLevel = stressLevel,
                factorTags = factors.joinToString(","),
                note = note,
                dateIso = dateIso,
                timestamp = System.currentTimeMillis()
            )
        )
        awardXp(20)
        return id
    }
}
