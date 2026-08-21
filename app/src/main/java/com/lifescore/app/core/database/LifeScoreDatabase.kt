package com.lifescore.app.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS ai_quests (id TEXT PRIMARY KEY NOT NULL, title TEXT NOT NULL, description TEXT NOT NULL, dimension TEXT NOT NULL, difficulty TEXT NOT NULL DEFAULT 'C', pointsReward INTEGER NOT NULL DEFAULT 30, statRewardPoints INTEGER NOT NULL DEFAULT 2, estimatedMinutes INTEGER NOT NULL DEFAULT 15, subObjectivesJson TEXT NOT NULL DEFAULT '[]', isAccepted INTEGER NOT NULL DEFAULT 0, isCompleted INTEGER NOT NULL DEFAULT 0, createdAt INTEGER NOT NULL DEFAULT 0)")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS group_habits (id TEXT PRIMARY KEY NOT NULL, title TEXT NOT NULL, description TEXT NOT NULL, dimension TEXT NOT NULL, memberCount INTEGER NOT NULL DEFAULT 1, streakDays INTEGER NOT NULL DEFAULT 0, todayCompletedCount INTEGER NOT NULL DEFAULT 0, targetDailyCompletions INTEGER NOT NULL DEFAULT 5, xpReward INTEGER NOT NULL DEFAULT 100, isJoined INTEGER NOT NULL DEFAULT 0, creatorName TEXT NOT NULL DEFAULT 'Achiever', isCompletedToday INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS journal_entries (id TEXT PRIMARY KEY NOT NULL, dateIso TEXT NOT NULL, mood TEXT NOT NULL DEFAULT 'NEUTRAL', textContent TEXT NOT NULL, dimensionTag TEXT NOT NULL, aiReflection TEXT, audioDurationSeconds INTEGER NOT NULL DEFAULT 0, createdAt INTEGER NOT NULL DEFAULT 0)")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS combat_bosses (id TEXT PRIMARY KEY NOT NULL, name TEXT NOT NULL, title TEXT NOT NULL, dimension TEXT NOT NULL, maxHp INTEGER NOT NULL, currentHp INTEGER NOT NULL, attackPower INTEGER NOT NULL, avatarEmoji TEXT NOT NULL, rewardXp INTEGER NOT NULL, rewardStatPoints INTEGER NOT NULL, isDefeated INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS character_stats (id INTEGER PRIMARY KEY NOT NULL, strength INTEGER NOT NULL DEFAULT 10, vitality INTEGER NOT NULL DEFAULT 10, agility INTEGER NOT NULL DEFAULT 10, intelligence INTEGER NOT NULL DEFAULT 10, perception INTEGER NOT NULL DEFAULT 10, availablePoints INTEGER NOT NULL DEFAULT 5, equippedTitle TEXT NOT NULL DEFAULT 'Novice Seeker', titleBonusDescription TEXT NOT NULL DEFAULT '+5% XP from all Quests')")
            }
        }

        fun getInstance(context: Context): LifeScoreDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeScoreDatabase::class.java,
                    "lifescore.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
