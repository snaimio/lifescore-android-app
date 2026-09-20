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
    suspend fun saveEveningReflection(text: String, dateIso: String) {}
    suspend fun getEveningReflection(dateIso: String): String? = null
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
        // Starts with clean zero state. User creates their genuine daily habits and tracks real data.
    }

    override suspend fun saveEveningReflection(text: String, dateIso: String) {
        val entry = com.lifescore.app.data.local.entity.JournalEntity(
            id = java.util.UUID.randomUUID().toString(),
            dateIso = dateIso,
            mood = JournalMood.HAPPY,
            textContent = text,
            dimensionTag = DimensionType.MENTAL_HEALTH,
            createdAt = System.currentTimeMillis()
        )
        db.journalDao().insertJournalEntry(entry)
    }

    override suspend fun getEveningReflection(dateIso: String): String? {
        return db.journalDao().getEntryByDate(dateIso)?.textContent
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
