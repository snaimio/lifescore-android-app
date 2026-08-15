package com.lifescore.app.data.repository

import com.lifescore.app.core.database.LifeScoreDatabase
import com.lifescore.app.data.local.entity.TaskEntity
import com.lifescore.app.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface LifeScoreRepository {
    fun getAllTasks(): Flow<List<LifeTask>>
    fun getTasksByDimension(dimension: DimensionType): Flow<List<LifeTask>>
    suspend fun addTask(title: String, dimension: DimensionType, points: Int): Long
    suspend fun toggleTaskCompletion(task: LifeTask)
    suspend fun deleteTask(task: LifeTask)
    fun getUserProfile(): Flow<UserProfile>
    suspend fun updateUserProfile(user: UserProfile)
    suspend fun seedInitialDataIfEmpty()
}

class LifeScoreRepositoryImpl(
    private val db: LifeScoreDatabase
) : LifeScoreRepository {

    override fun getAllTasks(): Flow<List<LifeTask>> {
        return db.taskDao().getAllTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTasksByDimension(dimension: DimensionType): Flow<List<LifeTask>> {
        return db.taskDao().getTasksByDimension(dimension).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getUserProfile(): Flow<UserProfile> {
        return db.userDao().getUserProfile().map { entity ->
            entity?.let {
                UserProfile(
                    id = it.id,
                    name = it.name,
                    currentXp = it.currentXp,
                    currentLevel = it.currentLevel,
                    currentStreakDays = it.currentStreakDays,
                    isPremium = it.isPremium,
                    title = it.title
                )
            } ?: UserProfile()
        }
    }

    override suspend fun updateUserProfile(user: UserProfile) {
        db.userDao().insertOrUpdateUser(
            com.lifescore.app.data.local.entity.UserEntity(
                id = 1L,
                name = user.name,
                currentXp = user.currentXp,
                currentLevel = user.currentLevel,
                currentStreakDays = user.currentStreakDays,
                isPremium = user.isPremium,
                title = user.title
            )
        )
    }

    override suspend fun addTask(title: String, dimension: DimensionType, points: Int): Long {
        val entity = TaskEntity(
            title = title,
            dimension = dimension,
            pointsReward = points
        )
        return db.taskDao().insertTask(entity)
    }

    override suspend fun toggleTaskCompletion(task: LifeTask) {
        val newStatus = !task.isCompleted
        val completedAt = if (newStatus) System.currentTimeMillis() else null
        db.taskDao().updateTaskStatus(task.id, newStatus, completedAt)

        if (newStatus) {
            db.userDao().addXp(task.pointsReward)
        } else {
            db.userDao().addXp(-task.pointsReward)
        }
    }

    override suspend fun deleteTask(task: LifeTask) {
        val entity = TaskEntity(
            id = task.id,
            title = task.title,
            dimension = task.dimension,
            pointsReward = task.pointsReward,
            isCompleted = task.isCompleted,
            streakDays = task.streakDays
        )
        db.taskDao().deleteTask(entity)
    }

    override suspend fun seedInitialDataIfEmpty() {
        // Initial setup for starter tasks
        val starterTasks = listOf(
            TaskEntity(title = "Morning 10-minute meditation", dimension = DimensionType.MENTAL_HEALTH, pointsReward = 15),
            TaskEntity(title = "Drink 2L of water & stretch", dimension = DimensionType.HEALTH, pointsReward = 10),
            TaskEntity(title = "Review monthly budget & investments", dimension = DimensionType.WEALTH, pointsReward = 20),
            TaskEntity(title = "Send thoughtful message to a close friend", dimension = DimensionType.RELATIONSHIPS, pointsReward = 15),
            TaskEntity(title = "Read 15 pages of non-fiction book", dimension = DimensionType.LEARNING, pointsReward = 20),
            TaskEntity(title = "Complete 30 min cardio or strength session", dimension = DimensionType.FITNESS, pointsReward = 25),
            TaskEntity(title = "Outline top 3 deep-work priorities for tomorrow", dimension = DimensionType.CAREER, pointsReward = 15),
            TaskEntity(title = "Plan weekend outing or community meetup", dimension = DimensionType.SOCIAL_LIFE, pointsReward = 10)
        )
        starterTasks.forEach { task ->
            db.taskDao().insertTask(task)
        }
    }

    private fun TaskEntity.toDomain() = LifeTask(
        id = id,
        title = title,
        dimension = dimension,
        pointsReward = pointsReward,
        isCompleted = isCompleted,
        streakDays = streakDays,
        recurringInterval = recurringInterval,
        completedAt = completedAt,
        createdAt = createdAt
    )
}
