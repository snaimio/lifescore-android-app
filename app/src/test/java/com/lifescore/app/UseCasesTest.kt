package com.lifescore.app

import com.lifescore.app.data.repository.GeminiCoachRepository
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.data.repository.WeeklyAuditResult
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.LifeTask
import com.lifescore.app.domain.model.UserProfile
import com.lifescore.app.domain.usecase.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class UseCasesTest {

    private class FakeLifeScoreRepository : LifeScoreRepository {
        val tasks = mutableListOf<LifeTask>(
            LifeTask(id = 1, title = "Drink 8 glasses of water", dimension = DimensionType.HEALTH, pointsReward = 15, isCompleted = false),
            LifeTask(id = 2, title = "20-minute HIIT workout", dimension = DimensionType.FITNESS, pointsReward = 25, isCompleted = true)
        )
        var userProfile = UserProfile(id = 1, name = "Test Champion", currentXp = 450, currentLevel = 3, currentStreakDays = 7)

        override fun getAllTasks() = flowOf(tasks)
        override fun getTasksByDimension(dimension: DimensionType) = flowOf(tasks.filter { it.dimension == dimension })
        override suspend fun addTask(title: String, dimension: DimensionType, points: Int): Long {
            val newId = (tasks.size + 1).toLong()
            tasks.add(LifeTask(id = newId, title = title, dimension = dimension, pointsReward = points))
            return newId
        }
        override suspend fun toggleTaskCompletion(task: LifeTask) {
            val index = tasks.indexOfFirst { it.id == task.id }
            if (index != -1) {
                tasks[index] = tasks[index].copy(isCompleted = !tasks[index].isCompleted)
            }
        }
        override suspend fun deleteTask(task: LifeTask) {
            tasks.removeAll { it.id == task.id }
        }
        override fun getUserProfile() = flowOf(userProfile)
        override suspend fun updateUserProfile(user: UserProfile) {
            userProfile = user
        }
        override suspend fun seedInitialDataIfEmpty() {}
    }

    private class FakeGeminiCoachRepository : GeminiCoachRepository {
        override suspend fun getDailyExecutiveBrief(lowestDimension: DimensionType, lowestScore: Int, totalScore: Int): String {
            return "Focus on ${lowestDimension.displayName} to elevate your overall LifeScore."
        }
        override suspend fun askCoach(userQuestion: String, contextScore: Int): String {
            return "Actionable coaching response for $userQuestion"
        }
        override suspend fun askCoachWithMemory(userQuestion: String, memoryContext: String): String {
            return "Contextual coaching with memory: $memoryContext"
        }
        override suspend fun generateWeeklyAudit(scores: Map<DimensionType, Int>, tasksCompleted: Int, totalScore: Int, streak: Int): WeeklyAuditResult {
            return WeeklyAuditResult(
                headline = "Great consistency!",
                pointSummary = "Earned +450 XP",
                topDimension = DimensionType.FITNESS,
                growthDimension = DimensionType.MENTAL_HEALTH,
                keyAchievements = listOf("7-day streak maintained"),
                nextWeekDirectives = listOf("Prioritize evening wind-down"),
                motivationalQuote = "Discipline equals freedom."
            )
        }
        override fun generateDimensionGuidance(dimension: DimensionType, score: Int, isWeakest: Boolean): String {
            return "Guidance for ${dimension.displayName} at score $score"
        }
        override fun generateWeeklyRecapShareText(audit: WeeklyAuditResult, score: Int, streak: Int): String {
            return "Weekly Recap Share Text"
        }
    }

    @Test
    fun testCalculateLifeScoreUseCase() {
        val useCase = CalculateLifeScoreUseCase()
        val scores: Map<DimensionType, Int> = mapOf(
            DimensionType.FITNESS to 80,
            DimensionType.CAREER to 90,
            DimensionType.LEARNING to 85,
            DimensionType.HEALTH to 70,
            DimensionType.MENTAL_HEALTH to 75,
            DimensionType.RELATIONSHIPS to 80,
            DimensionType.WEALTH to 85,
            DimensionType.SOCIAL_LIFE to 75
        )
        val overallScore = useCase(scores)
        assertTrue(overallScore in 700..900)
    }

    @Test
    fun testGetDailyTasksAndCompleteTaskUseCases() = runBlocking {
        val fakeRepo = FakeLifeScoreRepository()
        val getTasksUseCase = GetDailyTasksUseCase(fakeRepo)
        val completeTaskUseCase = CompleteTaskUseCase(fakeRepo)

        var loadedTasks: List<LifeTask>? = null
        getTasksUseCase().collect { loadedTasks = it }

        assertNotNull(loadedTasks)
        assertEquals(2, loadedTasks!!.size)

        val task1 = loadedTasks!![0]
        assertFalse(task1.isCompleted)

        completeTaskUseCase(task1)
        assertTrue(fakeRepo.tasks[0].isCompleted)
    }

    @Test
    fun testAddTaskAndDeleteTaskUseCases() = runBlocking {
        val fakeRepo = FakeLifeScoreRepository()
        val addTaskUseCase = AddTaskUseCase(fakeRepo)
        val deleteTaskUseCase = DeleteTaskUseCase(fakeRepo)

        val newId = addTaskUseCase("Read 1 chapter of Atomic Habits", DimensionType.LEARNING, 20)
        assertEquals(3L, newId)
        assertEquals(3, fakeRepo.tasks.size)

        val addedTask = fakeRepo.tasks.first { it.id == 3L }
        assertEquals("Read 1 chapter of Atomic Habits", addedTask.title)

        deleteTaskUseCase(addedTask)
        assertEquals(2, fakeRepo.tasks.size)
    }

    @Test
    fun testUserProfileUseCases() = runBlocking {
        val fakeRepo = FakeLifeScoreRepository()
        val getUserProfileUseCase = GetUserProfileUseCase(fakeRepo)
        val updateUserProfileUseCase = UpdateUserProfileUseCase(fakeRepo)

        var profile: UserProfile? = null
        getUserProfileUseCase().collect { profile = it }

        assertNotNull(profile)
        assertEquals("Test Champion", profile!!.name)
        assertEquals(7, profile!!.currentStreakDays)

        val updated = profile!!.copy(currentStreakDays = 8, currentXp = 500)
        updateUserProfileUseCase(updated)
        assertEquals(8, fakeRepo.userProfile.currentStreakDays)
    }

    @Test
    fun testAiCoachUseCases() = runBlocking {
        val fakeCoachRepo = FakeGeminiCoachRepository()
        val getAiRecommendationUseCase = GetAIRecommendationUseCase(fakeCoachRepo)
        val generateWeeklyAuditUseCase = GenerateWeeklyAuditUseCase(fakeCoachRepo)

        val recommendation = getAiRecommendationUseCase(DimensionType.HEALTH, 65, 780)
        assertTrue(recommendation.contains("Health"))

        val audit = generateWeeklyAuditUseCase(emptyMap(), 14, 780, 7)
        assertEquals("Great consistency!", audit.headline)
        assertEquals(DimensionType.FITNESS, audit.topDimension)
    }
}
