package com.lifescore.app.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lifescore.app.data.local.dao.*
import com.lifescore.app.data.local.entity.*

@Database(
    entities = [
        TaskEntity::class,
        DailyScoreEntity::class,
        UserEntity::class,
        ChallengeEntity::class,
        AiQuestEntity::class,
        CharacterStatsEntity::class,
        GroupHabitEntity::class,
        JournalEntity::class,
        BossEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LifeScoreDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun dailyScoreDao(): DailyScoreDao
    abstract fun userDao(): UserDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun aiQuestDao(): AiQuestDao
    abstract fun characterStatsDao(): CharacterStatsDao
    abstract fun groupHabitDao(): GroupHabitDao
    abstract fun journalDao(): JournalDao
    abstract fun bossDao(): BossDao

    companion object {
        @Volatile
        private var INSTANCE: LifeScoreDatabase? = null

        fun getInstance(context: Context): LifeScoreDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeScoreDatabase::class.java,
                    "lifescore.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
