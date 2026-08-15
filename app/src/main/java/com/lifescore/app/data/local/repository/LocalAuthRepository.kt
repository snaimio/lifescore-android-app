package com.lifescore.app.data.local.repository

import com.lifescore.app.core.database.LifeScoreDatabase
import com.lifescore.app.data.local.entity.UserEntity
import com.lifescore.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalAuthRepository(
    private val database: LifeScoreDatabase
) {
    fun getUserProfile(): Flow<UserProfile> {
        return database.userDao().getUserProfile().map { entity ->
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

    suspend fun createUser(email: String, displayName: String): UserProfile {
        val user = UserEntity(
            id = 1L,
            name = displayName.ifBlank { email.substringBefore("@").replaceFirstChar { it.uppercase() } },
            currentXp = 0,
            currentLevel = 1,
            currentStreakDays = 0,
            isPremium = false,
            title = "Novice Seeker"
        )
        database.userDao().insertOrUpdateUser(user)
        return UserProfile(
            id = user.id,
            name = user.name,
            currentXp = user.currentXp,
            currentLevel = user.currentLevel,
            currentStreakDays = user.currentStreakDays,
            isPremium = user.isPremium,
            title = user.title
        )
    }

    suspend fun updateUser(userProfile: UserProfile) {
        val entity = UserEntity(
            id = userProfile.id,
            name = userProfile.name,
            currentXp = userProfile.currentXp,
            currentLevel = userProfile.currentLevel,
            currentStreakDays = userProfile.currentStreakDays,
            isPremium = userProfile.isPremium,
            title = userProfile.title
        )
        database.userDao().insertOrUpdateUser(entity)
    }
}
