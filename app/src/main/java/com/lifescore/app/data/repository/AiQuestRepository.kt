package com.lifescore.app.data.repository

import com.lifescore.app.data.ai.AiQuestGeneratorService
import com.lifescore.app.data.local.dao.AiQuestDao
import com.lifescore.app.data.local.dao.TaskDao
import com.lifescore.app.data.local.entity.AiQuestEntity
import com.lifescore.app.data.local.entity.TaskEntity
import com.lifescore.app.domain.model.AiQuest
import com.lifescore.app.domain.model.DimensionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray

interface AiQuestRepository {
    fun getAllQuests(): Flow<List<AiQuest>>
    fun getActiveQuests(): Flow<List<AiQuest>>
    suspend fun generateAndSaveQuests(
        weakestDimension: DimensionType,
        weakestScore: Int,
        totalScore: Int,
        userLevel: Int,
        userStreak: Int
    ): List<AiQuest>
    suspend fun acceptQuest(quest: AiQuest)
    suspend fun completeQuest(questId: String)
}

class AiQuestRepositoryImpl(
    private val aiQuestDao: AiQuestDao,
    private val taskDao: TaskDao,
    private val aiService: AiQuestGeneratorService
) : AiQuestRepository {

    override fun getAllQuests(): Flow<List<AiQuest>> {
        return aiQuestDao.getAllQuests().map { list -> list.map { it.toDomain() } }
    }

    override fun getActiveQuests(): Flow<List<AiQuest>> {
        return aiQuestDao.getActiveQuests().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun generateAndSaveQuests(
        weakestDimension: DimensionType,
        weakestScore: Int,
        totalScore: Int,
        userLevel: Int,
        userStreak: Int
    ): List<AiQuest> {
        val quests = aiService.generatePersonalizedQuests(
            weakestDimension = weakestDimension,
            weakestScore = weakestScore,
            totalScore = totalScore,
            userLevel = userLevel,
            userStreak = userStreak
        )
        aiQuestDao.insertQuests(quests.map { it.toEntity() })
        return quests
    }

    override suspend fun acceptQuest(quest: AiQuest) {
        aiQuestDao.acceptQuest(quest.id)
        // Also insert into daily tasks so it's tracked in main dashboard quests
        taskDao.insertTask(
            TaskEntity(
                title = "[${quest.difficulty.rankLetter}-Rank] ${quest.title}",
                dimension = quest.dimension,
                pointsReward = quest.pointsReward,
                isCompleted = false
            )
        )
    }

    override suspend fun completeQuest(questId: String) {
        aiQuestDao.completeQuest(questId)
    }

    private fun AiQuestEntity.toDomain(): AiQuest {
        val subList = mutableListOf<String>()
        try {
            val arr = JSONArray(subObjectivesJson)
            for (i in 0 until arr.length()) subList.add(arr.getString(i))
        } catch (_: Exception) {}

        return AiQuest(
            id = id,
            title = title,
            description = description,
            dimension = dimension,
            difficulty = difficulty,
            pointsReward = pointsReward,
            statRewardPoints = statRewardPoints,
            estimatedMinutes = estimatedMinutes,
            subObjectives = subList,
            isAccepted = isAccepted,
            isCompleted = isCompleted,
            createdAt = createdAt
        )
    }

    private fun AiQuest.toEntity(): AiQuestEntity {
        val arr = JSONArray()
        subObjectives.forEach { arr.put(it) }
        return AiQuestEntity(
            id = id,
            title = title,
            description = description,
            dimension = dimension,
            difficulty = difficulty,
            pointsReward = pointsReward,
            statRewardPoints = statRewardPoints,
            estimatedMinutes = estimatedMinutes,
            subObjectivesJson = arr.toString(),
            isAccepted = isAccepted,
            isCompleted = isCompleted,
            createdAt = createdAt
        )
    }
}
