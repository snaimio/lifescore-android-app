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

    suspend fun createUser(email: String, password: String = "", displayName: String): UserEntity {
        val user = UserEntity(
            id = 1L,
            name = displayName.ifBlank { email.substringBefore("@").replaceFirstChar { it.uppercase() } },
            email = email,
            currentXp = 0,
            currentLevel = 1,
            currentStreakDays = 0,
            isPremium = false,
            title = "Novice Seeker",
            lastActive = System.currentTimeMillis(),
            isLocal = true
        )
        database.userDao().insertUser(user)
        return user
    }

    suspend fun getUser(email: String): UserEntity? {
        return database.userDao().getUserByEmail(email)
    }

    suspend fun getCurrentUser(): UserEntity? {
        return database.userDao().getLastActiveUser()
    }

    suspend fun updateUser(user: UserEntity) {
        database.userDao().insertOrUpdateUser(user)
    }

    suspend fun updateUserProfile(userProfile: UserProfile) {
        val entity = UserEntity(
            id = userProfile.id,
            name = userProfile.name,
            currentXp = userProfile.currentXp,
            currentLevel = userProfile.currentLevel,
            currentStreakDays = userProfile.currentStreakDays,
            isPremium = userProfile.isPremium,
            title = userProfile.title,
            lastActive = System.currentTimeMillis(),
            isLocal = true
        )
        database.userDao().insertOrUpdateUser(entity)
    }

    suspend fun createProfile(email: String, displayName: String): UserProfile {
        val user = createUser(email, "", displayName)
        return user.toUserProfile()
    }
}

fun UserEntity.toUserProfile(): UserProfile = UserProfile(
    id = id,
    name = name,
    currentXp = currentXp,
    currentLevel = currentLevel,
    currentStreakDays = currentStreakDays,
    isPremium = isPremium,
    title = title
)
