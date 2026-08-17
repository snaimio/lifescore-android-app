package com.lifescore.app.data.local.dao

import androidx.room.*
import com.lifescore.app.data.local.entity.GroupHabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupHabitDao {
    @Query("SELECT * FROM group_habits ORDER BY memberCount DESC")
    fun getAllGroupHabits(): Flow<List<GroupHabitEntity>>

    @Query("SELECT * FROM group_habits WHERE isJoined = 1")
    fun getJoinedGroupHabits(): Flow<List<GroupHabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupHabits(habits: List<GroupHabitEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupHabit(habit: GroupHabitEntity)

    @Query("UPDATE group_habits SET isJoined = 1, memberCount = memberCount + 1 WHERE id = :id")
    suspend fun joinGroup(id: String)

    @Query("UPDATE group_habits SET isJoined = 0, memberCount = CASE WHEN memberCount > 1 THEN memberCount - 1 ELSE 1 END WHERE id = :id")
    suspend fun leaveGroup(id: String)

    @Query("UPDATE group_habits SET isCompletedToday = 1, todayCompletedCount = todayCompletedCount + 1 WHERE id = :id")
    suspend fun completeToday(id: String)
}
