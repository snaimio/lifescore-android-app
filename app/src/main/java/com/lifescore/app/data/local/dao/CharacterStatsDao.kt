package com.lifescore.app.data.local.dao

import androidx.room.*
import com.lifescore.app.data.local.entity.CharacterStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterStatsDao {
    @Query("SELECT * FROM character_stats WHERE id = 1 LIMIT 1")
    fun getCharacterStatsFlow(): Flow<CharacterStatsEntity?>

    @Query("SELECT * FROM character_stats WHERE id = 1 LIMIT 1")
    suspend fun getCharacterStats(): CharacterStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stats: CharacterStatsEntity)

    @Query("UPDATE character_stats SET strength = strength + :points, availablePoints = availablePoints - :points WHERE id = 1 AND availablePoints >= :points")
    suspend fun addStrength(points: Int = 1): Int

    @Query("UPDATE character_stats SET vitality = vitality + :points, availablePoints = availablePoints - :points WHERE id = 1 AND availablePoints >= :points")
    suspend fun addVitality(points: Int = 1): Int

    @Query("UPDATE character_stats SET agility = agility + :points, availablePoints = availablePoints - :points WHERE id = 1 AND availablePoints >= :points")
    suspend fun addAgility(points: Int = 1): Int

    @Query("UPDATE character_stats SET intelligence = intelligence + :points, availablePoints = availablePoints - :points WHERE id = 1 AND availablePoints >= :points")
    suspend fun addIntelligence(points: Int = 1): Int

    @Query("UPDATE character_stats SET perception = perception + :points, availablePoints = availablePoints - :points WHERE id = 1 AND availablePoints >= :points")
    suspend fun addPerception(points: Int = 1): Int

    @Query("UPDATE character_stats SET availablePoints = availablePoints + :points WHERE id = 1")
    suspend fun grantAvailablePoints(points: Int)

    @Query("UPDATE character_stats SET equippedTitle = :title, titleBonusDescription = :bonus WHERE id = 1")
    suspend fun equipTitle(title: String, bonus: String)
}
