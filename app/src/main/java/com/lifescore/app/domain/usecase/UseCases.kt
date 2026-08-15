package com.lifescore.app.domain.usecase

import com.lifescore.app.core.util.ScoreEngine
import com.lifescore.app.data.repository.GeminiCoachRepository
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.data.repository.WeeklyAuditResult
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.LifeTask
import com.lifescore.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Calculates normalized LifeScore from 8 dimension values.
 */
class CalculateLifeScoreUseCase {
    operator fun invoke(dimensionScores: Map<DimensionType, Int>): Int {
        return ScoreEngine.calculateOverallLifeScore(dimensionScores)
    }
}

/**
 * Retrieves daily habit quests and tasks from the repository.
 */
class GetDailyTasksUseCase(
    private val repository: LifeScoreRepository
) {
    operator fun invoke(): Flow<List<LifeTask>> {
        return repository.getAllTasks()
    }
}

/**
 * Toggles completion for a task, applying XP and streak logic.
 */
class CompleteTaskUseCase(
    private val repository: LifeScoreRepository
) {
    suspend operator fun invoke(task: LifeTask) {
        repository.toggleTaskCompletion(task)
    }
}

/**
 * Creates a new custom task or habit under a specific dimension.
 */
class AddTaskUseCase(
    private val repository: LifeScoreRepository
) {
    suspend operator fun invoke(title: String, dimension: DimensionType, points: Int = 15): Long {
        return repository.addTask(title, dimension, points)
    }
}

/**
 * Deletes a task from the database.
 */
class DeleteTaskUseCase(
    private val repository: LifeScoreRepository
) {
    suspend operator fun invoke(task: LifeTask) {
        repository.deleteTask(task)
    }
}

/**
 * Streams the active user profile (XP, Level, Streak, Coins).
 */
class GetUserProfileUseCase(
    private val repository: LifeScoreRepository
) {
    operator fun invoke(): Flow<UserProfile> {
        return repository.getUserProfile()
    }
}

/**
 * Updates user profile stats.
 */
class UpdateUserProfileUseCase(
    private val repository: LifeScoreRepository
) {
    suspend operator fun invoke(user: UserProfile) {
        repository.updateUserProfile(user)
    }
}

/**
 * Queries Gemini AI for personalized coaching and daily directives.
 */
class GetAIRecommendationUseCase(
    private val coachRepository: GeminiCoachRepository
) {
    suspend operator fun invoke(lowestDimension: DimensionType, lowestScore: Int, totalScore: Int): String {
        return coachRepository.getDailyExecutiveBrief(lowestDimension, lowestScore, totalScore)
    }
}

/**
 * Generates an executive weekly performance audit.
 */
class GenerateWeeklyAuditUseCase(
    private val coachRepository: GeminiCoachRepository
) {
    suspend operator fun invoke(
        scores: Map<DimensionType, Int>,
        tasksCompleted: Int,
        totalScore: Int,
        streak: Int
    ): WeeklyAuditResult {
        return coachRepository.generateWeeklyAudit(scores, tasksCompleted, totalScore, streak)
    }
}
