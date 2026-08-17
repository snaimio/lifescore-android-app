package com.lifescore.app.data.repository

import com.lifescore.app.data.local.dao.GroupHabitDao
import com.lifescore.app.data.local.entity.GroupHabitEntity
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.GroupHabit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.util.UUID

interface GroupHabitRepository {
    fun getAllGroupHabits(): Flow<List<GroupHabit>>
    fun getJoinedGroupHabits(): Flow<List<GroupHabit>>
    suspend fun createGroupHabit(title: String, description: String, dimension: DimensionType): GroupHabit
    suspend fun joinGroup(id: String)
    suspend fun leaveGroup(id: String)
    suspend fun completeToday(id: String)
}

class GroupHabitRepositoryImpl(
    private val dao: GroupHabitDao
) : GroupHabitRepository {

    override fun getAllGroupHabits(): Flow<List<GroupHabit>> {
        return dao.getAllGroupHabits()
            .onStart { seedDefaultsIfEmpty() }
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getJoinedGroupHabits(): Flow<List<GroupHabit>> {
        return dao.getJoinedGroupHabits().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun createGroupHabit(
        title: String,
        description: String,
        dimension: DimensionType
    ): GroupHabit {
        val habit = GroupHabit(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            dimension = dimension,
            memberCount = 1,
            streakDays = 1,
            todayCompletedCount = 1,
            targetDailyCompletions = 5,
            xpReward = 150,
            isJoined = true,
            creatorName = "You",
            isCompletedToday = true
        )
        dao.insertGroupHabit(habit.toEntity())
        return habit
    }

    override suspend fun joinGroup(id: String) {
        dao.joinGroup(id)
    }

    override suspend fun leaveGroup(id: String) {
        dao.leaveGroup(id)
    }

    override suspend fun completeToday(id: String) {
        dao.completeToday(id)
    }

    private suspend fun seedDefaultsIfEmpty() {
        val defaults = listOf(
            GroupHabitEntity(
                id = "group_5am_club",
                title = "⚡ 5:00 AM Dawn Vanguard",
                description = "Wake up at 5:00 AM, drink 500ml water, and complete 20 min morning sunlight walk.",
                dimension = DimensionType.HEALTH,
                memberCount = 428,
                streakDays = 14,
                todayCompletedCount = 312,
                targetDailyCompletions = 400,
                xpReward = 200,
                isJoined = true,
                creatorName = "Grand Master Kai",
                isCompletedToday = false
            ),
            GroupHabitEntity(
                id = "group_deep_work",
                title = "🧠 90-Minute Zero-Distraction Sprint",
                description = "Phone on airplane mode. 90 minutes of continuous high-leverage deep work.",
                dimension = DimensionType.CAREER,
                memberCount = 894,
                streakDays = 21,
                todayCompletedCount = 640,
                targetDailyCompletions = 800,
                xpReward = 250,
                isJoined = false,
                creatorName = "Elena Vance",
                isCompletedToday = false
            ),
            GroupHabitEntity(
                id = "group_cold_plunge",
                title = "❄️ Daily Bio-Stamina Cold Surge",
                description = "2 minutes cold shower or ice plunge to supercharge dopamine & immune defense.",
                dimension = DimensionType.FITNESS,
                memberCount = 215,
                streakDays = 9,
                todayCompletedCount = 145,
                targetDailyCompletions = 200,
                xpReward = 175,
                isJoined = false,
                creatorName = "Coach Marcus",
                isCompletedToday = false
            )
        )
        dao.insertGroupHabits(defaults)
    }

    private fun GroupHabitEntity.toDomain() = GroupHabit(
        id = id,
        title = title,
        description = description,
        dimension = dimension,
        memberCount = memberCount,
        streakDays = streakDays,
        todayCompletedCount = todayCompletedCount,
        targetDailyCompletions = targetDailyCompletions,
        xpReward = xpReward,
        isJoined = isJoined,
        creatorName = creatorName,
        isCompletedToday = isCompletedToday
    )

    private fun GroupHabit.toEntity() = GroupHabitEntity(
        id = id,
        title = title,
        description = description,
        dimension = dimension,
        memberCount = memberCount,
        streakDays = streakDays,
        todayCompletedCount = todayCompletedCount,
        targetDailyCompletions = targetDailyCompletions,
        xpReward = xpReward,
        isJoined = isJoined,
        creatorName = creatorName,
        isCompletedToday = isCompletedToday
    )
}
