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
        ChallengeEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LifeScoreDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun dailyScoreDao(): DailyScoreDao
    abstract fun userDao(): UserDao
    abstract fun challengeDao(): ChallengeDao

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
