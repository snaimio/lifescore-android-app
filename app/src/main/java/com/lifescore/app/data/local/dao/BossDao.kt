package com.lifescore.app.data.local.dao

import androidx.room.*
import com.lifescore.app.data.local.entity.BossEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BossDao {
    @Query("SELECT * FROM combat_bosses")
    fun getAllBosses(): Flow<List<BossEntity>>

    @Query("SELECT * FROM combat_bosses WHERE id = :id LIMIT 1")
    suspend fun getBossById(id: String): BossEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBosses(bosses: List<BossEntity>)

    @Query("UPDATE combat_bosses SET currentHp = :newHp, isDefeated = CASE WHEN :newHp <= 0 THEN 1 ELSE 0 END WHERE id = :id")
    suspend fun updateBossHp(id: String, newHp: Int)
}
