package com.lifescore.app.data.local.dao

import androidx.room.*
import com.lifescore.app.data.local.entity.AiQuestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AiQuestDao {
    @Query("SELECT * FROM ai_quests ORDER BY createdAt DESC")
    fun getAllQuests(): Flow<List<AiQuestEntity>>

    @Query("SELECT * FROM ai_quests WHERE isAccepted = 1 AND isCompleted = 0")
    fun getActiveQuests(): Flow<List<AiQuestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuests(quests: List<AiQuestEntity>)

    @Query("UPDATE ai_quests SET isAccepted = 1 WHERE id = :questId")
    suspend fun acceptQuest(questId: String)

    @Query("UPDATE ai_quests SET isCompleted = 1 WHERE id = :questId")
    suspend fun completeQuest(questId: String)

    @Query("DELETE FROM ai_quests WHERE isAccepted = 0")
    suspend fun clearUnacceptedQuests()
}
