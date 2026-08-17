package com.lifescore.app.data.local.dao

import androidx.room.*
import com.lifescore.app.data.local.entity.JournalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC")
    fun getAllJournalEntries(): Flow<List<JournalEntity>>

    @Query("SELECT * FROM journal_entries WHERE dateIso = :dateIso LIMIT 1")
    suspend fun getEntryByDate(dateIso: String): JournalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntity)

    @Delete
    suspend fun deleteJournalEntry(entry: JournalEntity)

    @Query("UPDATE journal_entries SET aiReflection = :reflection WHERE id = :id")
    suspend fun updateAiReflection(id: String, reflection: String)
}
