package com.lifescore.app.data.repository

import com.lifescore.app.data.local.dao.CharacterStatsDao
import com.lifescore.app.data.local.entity.CharacterStatsEntity
import com.lifescore.app.domain.model.CharacterStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

interface CharacterStatsRepository {
    fun getCharacterStatsFlow(): Flow<CharacterStats>
    suspend fun getCharacterStats(): CharacterStats
    suspend fun addStrength(points: Int = 1): Boolean
    suspend fun addVitality(points: Int = 1): Boolean
    suspend fun addAgility(points: Int = 1): Boolean
    suspend fun addIntelligence(points: Int = 1): Boolean
    suspend fun addPerception(points: Int = 1): Boolean
    suspend fun grantAvailablePoints(points: Int)
    suspend fun equipTitle(title: String, bonusDescription: String)
}

class CharacterStatsRepositoryImpl(
    private val dao: CharacterStatsDao
) : CharacterStatsRepository {

    override fun getCharacterStatsFlow(): Flow<CharacterStats> {
        return dao.getCharacterStatsFlow()
            .onStart {
                ensureInitialized()
            }
            .map { entity ->
                entity?.toDomain() ?: CharacterStats()
            }
    }

    private suspend fun ensureInitialized() {
        if (dao.getCharacterStats() == null) {
            dao.insertOrUpdate(CharacterStatsEntity())
        }
    }

    override suspend fun getCharacterStats(): CharacterStats {
        ensureInitialized()
        return (dao.getCharacterStats() ?: CharacterStatsEntity()).toDomain()
    }

    override suspend fun addStrength(points: Int): Boolean {
        ensureInitialized()
        return dao.addStrength(points) > 0
    }

    override suspend fun addVitality(points: Int): Boolean {
        ensureInitialized()
        return dao.addVitality(points) > 0
    }

    override suspend fun addAgility(points: Int): Boolean {
        ensureInitialized()
        return dao.addAgility(points) > 0
    }

    override suspend fun addIntelligence(points: Int): Boolean {
        ensureInitialized()
        return dao.addIntelligence(points) > 0
    }

    override suspend fun addPerception(points: Int): Boolean {
        ensureInitialized()
        return dao.addPerception(points) > 0
    }

    override suspend fun grantAvailablePoints(points: Int) {
        ensureInitialized()
        dao.grantAvailablePoints(points)
    }

    override suspend fun equipTitle(title: String, bonusDescription: String) {
        ensureInitialized()
        dao.equipTitle(title, bonusDescription)
    }

    private fun CharacterStatsEntity.toDomain(): CharacterStats {
        return CharacterStats(
            id = id,
            strength = strength,
            vitality = vitality,
            agility = agility,
            intelligence = intelligence,
            perception = perception,
            availablePoints = availablePoints,
            title = equippedTitle,
            titleBonusDescription = titleBonusDescription
        )
    }
}
