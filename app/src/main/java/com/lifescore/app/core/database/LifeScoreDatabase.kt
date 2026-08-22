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
        BossEntity::class,
        HydrationEntity::class,
        HydrationGoalEntity::class,
        RecoveryEntry::class,
        CravingLog::class,
        RelapseLog::class,
        RecoveryMilestone::class,
        MotivationalNote::class,
        RecoveryPledge::class,
        RecoverySavingsGoal::class,
        BookSummaryProgressEntity::class,
        DailyGrowthProgressEntity::class,
        FocusSessionEntity::class,
        MoodLogEntity::class
    ],
    version = 7,
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
    abstract fun hydrationDao(): HydrationDao
    abstract fun recoveryDao(): RecoveryDao
    abstract fun bookSummaryDao(): BookSummaryDao
    abstract fun dailyGrowthDao(): DailyGrowthDao
    abstract fun focusDao(): FocusDao
    abstract fun moodDao(): MoodDao

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

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS hydration_entries (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL, timestamp INTEGER NOT NULL, volumeMl INTEGER NOT NULL, source TEXT NOT NULL DEFAULT 'manual')")
                db.execSQL("CREATE TABLE IF NOT EXISTS hydration_goals (userId TEXT PRIMARY KEY NOT NULL, dailyGoalMl INTEGER NOT NULL DEFAULT 2500, weightKg REAL, activityLevel TEXT NOT NULL DEFAULT 'moderate', updatedAt INTEGER NOT NULL)")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS recovery_entries (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL, addictionType TEXT NOT NULL, sobrietyStartDate INTEGER NOT NULL, dailyCost REAL NOT NULL DEFAULT 12.0, dailyMinutesConsumed INTEGER NOT NULL DEFAULT 45, dailyItemsConsumed INTEGER NOT NULL DEFAULT 15, currentStreakDays INTEGER NOT NULL DEFAULT 0, longestStreakDays INTEGER NOT NULL DEFAULT 0, totalSobrietyDays INTEGER NOT NULL DEFAULT 0, totalSlipsCount INTEGER NOT NULL DEFAULT 0, moneySaved REAL NOT NULL DEFAULT 0.0, timeSavedHours REAL NOT NULL DEFAULT 0.0, lastUpdated INTEGER NOT NULL, isActive INTEGER NOT NULL DEFAULT 1)")
                db.execSQL("CREATE TABLE IF NOT EXISTS craving_logs (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL, addictionType TEXT NOT NULL, intensity TEXT NOT NULL, trigger TEXT NOT NULL, durationMinutes INTEGER NOT NULL, survived INTEGER NOT NULL, notes TEXT NOT NULL, distractionUsed TEXT, timestamp INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS relapse_logs (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL, addictionType TEXT NOT NULL, relapseType TEXT NOT NULL, trigger TEXT NOT NULL, lessonsLearned TEXT NOT NULL, actionPlan TEXT NOT NULL, streakBeforeSetback INTEGER NOT NULL, timestamp INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS recovery_milestones (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL, addictionType TEXT NOT NULL, milestoneDays INTEGER NOT NULL, title TEXT NOT NULL, description TEXT NOT NULL, healthBenefit TEXT NOT NULL, medallionEmoji TEXT NOT NULL, isUnlocked INTEGER NOT NULL, unlockedAt INTEGER)")
                db.execSQL("CREATE TABLE IF NOT EXISTS motivational_notes (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL, note TEXT NOT NULL, reason TEXT NOT NULL, isActive INTEGER NOT NULL, createdAt INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS recovery_pledges (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL, dateIso TEXT NOT NULL, addictionType TEXT NOT NULL, pledgeText TEXT NOT NULL, isEveningReflected INTEGER NOT NULL, eveningReflection TEXT NOT NULL, isKept INTEGER NOT NULL, timestamp INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS recovery_savings_goals (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL, title TEXT NOT NULL, targetAmount REAL NOT NULL, iconEmoji TEXT NOT NULL, isReached INTEGER NOT NULL, createdAt INTEGER NOT NULL)")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS book_summary_progress (bookId TEXT PRIMARY KEY NOT NULL, userId TEXT NOT NULL, isCompleted INTEGER NOT NULL, isBookmarked INTEGER NOT NULL, completedKeyTakeawaysCount INTEGER NOT NULL, appliedQuestCompleted INTEGER NOT NULL, lastReadChapterIndex INTEGER NOT NULL, lastReadTimestamp INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS daily_growth_progress (sessionId INTEGER PRIMARY KEY NOT NULL, userId TEXT NOT NULL, dateIso TEXT NOT NULL, isCompleted INTEGER NOT NULL, journalReflection TEXT NOT NULL, actionItemCompleted INTEGER NOT NULL, completedAt INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS focus_sessions (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL, durationMinutes INTEGER NOT NULL, dimensionTag TEXT NOT NULL, treeType TEXT NOT NULL, wasSuccessful INTEGER NOT NULL, notes TEXT NOT NULL, timestamp INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS mood_logs (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL, mood TEXT NOT NULL, energyLevel INTEGER NOT NULL, stressLevel INTEGER NOT NULL, factorTags TEXT NOT NULL, note TEXT NOT NULL, dateIso TEXT NOT NULL, timestamp INTEGER NOT NULL)")
            }
        }

        fun getInstance(context: Context): LifeScoreDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeScoreDatabase::class.java,
                    "lifescore.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
