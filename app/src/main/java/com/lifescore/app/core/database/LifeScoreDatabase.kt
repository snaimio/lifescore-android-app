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
        MoodLogEntity::class,
        ScreenTimeEntry::class,
        ScreenTimeGoalEntity::class,
        ScreenTimeSession::class,
        ScreenTimeChallenge::class,
        ThoughtBreakLog::class,
        FlashcardEntity::class,
        LearningPlanEntity::class,
        EnergyPredictionEntity::class,
        SmartScheduledTaskEntity::class,
        VirtualPetEntity::class,
        MeditationTrackEntity::class,
        LiveEventEntity::class,
        PartyEntity::class,
        PartyMessageEntity::class,
        CoachEntity::class,
        CoachBookingEntity::class,
        ScienceJourneyEntity::class,
        HabitStackEntity::class,
        ReferralEntity::class,
        LeagueTierEntity::class,
        StreakInventoryEntity::class,
        CustomRewardEntity::class,
        FriendActivityEntity::class,
        AiMemoryEntity::class
    ],
    version = 10,
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
    abstract fun screenTimeDao(): ScreenTimeDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun energyDao(): EnergyDao
    abstract fun virtualPetDao(): VirtualPetDao
    abstract fun meditationDao(): MeditationDao
    abstract fun partyDao(): PartyDao
    abstract fun coachDao(): CoachDao
    abstract fun journeyDao(): JourneyDao
    abstract fun viralGrowthDao(): ViralGrowthDao

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

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS screen_time_entries (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL, date TEXT NOT NULL, totalMinutes INTEGER NOT NULL, socialMediaMinutes INTEGER NOT NULL DEFAULT 0, gamingMinutes INTEGER NOT NULL DEFAULT 0, videoMinutes INTEGER NOT NULL DEFAULT 0, shoppingMinutes INTEGER NOT NULL DEFAULT 0, pickups INTEGER NOT NULL DEFAULT 0, firstPickup INTEGER, lastPickup INTEGER, screenTimeGoalMet INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS screen_time_goals (userId TEXT PRIMARY KEY NOT NULL, dailyLimitMinutes INTEGER NOT NULL DEFAULT 120, appWhitelist TEXT NOT NULL DEFAULT '', isFocusModeEnabled INTEGER NOT NULL DEFAULT 0, intentionalDelaySeconds INTEGER NOT NULL DEFAULT 10, earnedBonusMinutes INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS screen_time_sessions (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL, sessionType TEXT NOT NULL, startTime INTEGER NOT NULL, endTime INTEGER, durationMinutes INTEGER NOT NULL DEFAULT 0, appsBlocked TEXT NOT NULL DEFAULT '', wasSuccessful INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS screen_time_challenges (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL, challengeType TEXT NOT NULL, title TEXT NOT NULL DEFAULT '7-Day Digital Detox', targetDays INTEGER NOT NULL DEFAULT 7, currentDay INTEGER NOT NULL DEFAULT 0, isActive INTEGER NOT NULL DEFAULT 1, xpReward INTEGER NOT NULL DEFAULT 100, completedAt INTEGER)")
                db.execSQL("CREATE TABLE IF NOT EXISTS thought_break_logs (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL, automaticThought TEXT NOT NULL, cognitiveDistortion TEXT NOT NULL, evidenceAgainst TEXT NOT NULL, reframedThought TEXT NOT NULL, emotionalReliefRating INTEGER NOT NULL DEFAULT 8, timestamp INTEGER NOT NULL)")
            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS flashcard_entities (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, bookId TEXT NOT NULL, bookTitle TEXT NOT NULL, question TEXT NOT NULL, answer TEXT NOT NULL, keyConcept TEXT NOT NULL, boxLevel INTEGER NOT NULL DEFAULT 1, nextReviewTimestamp INTEGER NOT NULL, repetitions INTEGER NOT NULL DEFAULT 0, lastReviewedAt INTEGER)")
                db.execSQL("CREATE TABLE IF NOT EXISTS learning_plans (planId TEXT PRIMARY KEY NOT NULL, userId TEXT NOT NULL DEFAULT 'default_user', title TEXT NOT NULL, targetDimension TEXT NOT NULL, totalDays INTEGER NOT NULL DEFAULT 30, currentDay INTEGER NOT NULL DEFAULT 1, bookIdsCsv TEXT NOT NULL, isCompleted INTEGER NOT NULL DEFAULT 0, createdAt INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS energy_predictions (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'default_user', dateIso TEXT NOT NULL, morningPeakHour INTEGER NOT NULL DEFAULT 9, afternoonDipHour INTEGER NOT NULL DEFAULT 14, eveningPeakHour INTEGER NOT NULL DEFAULT 18, predictedFocusScore INTEGER NOT NULL DEFAULT 85, sleepQualityScore INTEGER NOT NULL DEFAULT 88, restingHeartRate INTEGER NOT NULL DEFAULT 62, peakEnergyStartHour INTEGER NOT NULL DEFAULT 9, peakEnergyEndHour INTEGER NOT NULL DEFAULT 12)")
                db.execSQL("CREATE TABLE IF NOT EXISTS smart_scheduled_tasks (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'default_user', taskTitle TEXT NOT NULL, estimatedMinutes INTEGER NOT NULL DEFAULT 45, cognitiveDemand TEXT NOT NULL DEFAULT 'HIGH', scheduledTimeIso TEXT NOT NULL, energyMatchPercentage INTEGER NOT NULL DEFAULT 94, isCompleted INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS virtual_pets (userId TEXT PRIMARY KEY NOT NULL, petName TEXT NOT NULL DEFAULT 'Pip', petType TEXT NOT NULL DEFAULT 'Phoenix', happinessLevel INTEGER NOT NULL DEFAULT 80, energyLevel INTEGER NOT NULL DEFAULT 90, evolutionStage INTEGER NOT NULL DEFAULT 1, equippedHat TEXT NOT NULL DEFAULT 'Wizard Hat', equippedAccessory TEXT NOT NULL DEFAULT 'Golden Scarf', petMoodEmoji TEXT NOT NULL DEFAULT '🐥', lastFedTimestamp INTEGER NOT NULL, totalAffectionInteractions INTEGER NOT NULL DEFAULT 0, gentleStreakDays INTEGER NOT NULL DEFAULT 5)")
                db.execSQL("CREATE TABLE IF NOT EXISTS meditation_tracks (trackId TEXT PRIMARY KEY NOT NULL, title TEXT NOT NULL, teacherName TEXT NOT NULL, category TEXT NOT NULL, durationMinutes INTEGER NOT NULL, playsCount INTEGER NOT NULL DEFAULT 12400, rating REAL NOT NULL DEFAULT 4.9, audioUrl TEXT NOT NULL DEFAULT '', ambientSound TEXT NOT NULL DEFAULT 'Tibetan Singing Bowls', isBookmarked INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS live_meditation_events (eventId TEXT PRIMARY KEY NOT NULL, title TEXT NOT NULL, hostName TEXT NOT NULL, startTimeIso TEXT NOT NULL, registeredCount INTEGER NOT NULL DEFAULT 340, isRegistered INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS rpg_parties (partyId TEXT PRIMARY KEY NOT NULL, partyName TEXT NOT NULL, leaderName TEXT NOT NULL, memberCount INTEGER NOT NULL DEFAULT 4, activeQuestName TEXT NOT NULL, questBossMaxHp INTEGER NOT NULL DEFAULT 500, questBossCurrentHp INTEGER NOT NULL DEFAULT 230, teamXpMultiplier REAL NOT NULL DEFAULT 1.25, isCurrentUserLeader INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS party_messages (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, partyId TEXT NOT NULL, senderName TEXT NOT NULL, senderAvatar TEXT NOT NULL, messageText TEXT NOT NULL, timestamp INTEGER NOT NULL, questDamageDealt INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS coach_profiles (coachId TEXT PRIMARY KEY NOT NULL, name TEXT NOT NULL, title TEXT NOT NULL, specialty TEXT NOT NULL, avatarEmoji TEXT NOT NULL, rating REAL NOT NULL DEFAULT 4.95, reviewsCount INTEGER NOT NULL DEFAULT 128, hourlyRateUsd INTEGER NOT NULL DEFAULT 45, bio TEXT NOT NULL, isVerified INTEGER NOT NULL DEFAULT 1)")
                db.execSQL("CREATE TABLE IF NOT EXISTS coach_bookings (bookingId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'default_user', coachId TEXT NOT NULL, coachName TEXT NOT NULL, scheduledDateIso TEXT NOT NULL, sessionGoal TEXT NOT NULL, isConfirmed INTEGER NOT NULL DEFAULT 1, createdAt INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS science_journeys (journeyId TEXT PRIMARY KEY NOT NULL, title TEXT NOT NULL, subtitle TEXT NOT NULL, behavioralPrinciple TEXT NOT NULL, durationDays INTEGER NOT NULL DEFAULT 21, currentDay INTEGER NOT NULL DEFAULT 1, currentMilestone TEXT NOT NULL, isEnrolled INTEGER NOT NULL DEFAULT 0, completionPercentage INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS habit_stacks (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, triggerHabit TEXT NOT NULL, newActionHabit TEXT NOT NULL, rewardHabit TEXT NOT NULL, targetDimension TEXT NOT NULL, streakDays INTEGER NOT NULL DEFAULT 0, isCompletedToday INTEGER NOT NULL DEFAULT 0)")
            }
        }

        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS referrals (userId TEXT PRIMARY KEY NOT NULL, referralCode TEXT NOT NULL, invitedCount INTEGER NOT NULL DEFAULT 2, targetInviteCount INTEGER NOT NULL DEFAULT 3, isOneMonthPremiumUnlocked INTEGER NOT NULL DEFAULT 0, totalBonusXpEarned INTEGER NOT NULL DEFAULT 300, invitedFriendNamesCsv TEXT NOT NULL DEFAULT '')")
                db.execSQL("CREATE TABLE IF NOT EXISTS league_tiers (userId TEXT PRIMARY KEY NOT NULL, tierName TEXT NOT NULL DEFAULT 'Gold II', tierLevel INTEGER NOT NULL DEFAULT 5, currentWeeklyXp INTEGER NOT NULL DEFAULT 1450, userRankInLeague INTEGER NOT NULL DEFAULT 4, totalCompetitorsInPool INTEGER NOT NULL DEFAULT 30, promotionCutoffRank INTEGER NOT NULL DEFAULT 10, relegationCutoffRank INTEGER NOT NULL DEFAULT 25, seasonDaysRemaining INTEGER NOT NULL DEFAULT 3, tierBadgeEmoji TEXT NOT NULL DEFAULT '🥇')")
                db.execSQL("CREATE TABLE IF NOT EXISTS streak_inventory (userId TEXT PRIMARY KEY NOT NULL, streakFreezesAvailable INTEGER NOT NULL DEFAULT 2, isFreezeShieldArmed INTEGER NOT NULL DEFAULT 1, currentActiveStreakDays INTEGER NOT NULL DEFAULT 12, isResurrectionQuestActive INTEGER NOT NULL DEFAULT 0, resurrectionStreakDaysToRecover INTEGER NOT NULL DEFAULT 0, resurrectionDayProgress INTEGER NOT NULL DEFAULT 0, totalFreezesUsed INTEGER NOT NULL DEFAULT 1)")
                db.execSQL("CREATE TABLE IF NOT EXISTS custom_rewards (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'default_user', title TEXT NOT NULL, goldPrice INTEGER NOT NULL, iconEmoji TEXT NOT NULL, category TEXT NOT NULL, timesRedeemed INTEGER NOT NULL DEFAULT 0, isAvailable INTEGER NOT NULL DEFAULT 1)")
                db.execSQL("CREATE TABLE IF NOT EXISTS friend_feed_items (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, friendName TEXT NOT NULL, avatarEmoji TEXT NOT NULL, actionDescription TEXT NOT NULL, dimensionTag TEXT NOT NULL, streakDays INTEGER NOT NULL, timestamp INTEGER NOT NULL, isNudgedToday INTEGER NOT NULL DEFAULT 0, isGiftReceived INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS ai_long_term_memories (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId TEXT NOT NULL DEFAULT 'default_user', patternType TEXT NOT NULL, observedInsight TEXT NOT NULL, recommendedMicroIntervention TEXT NOT NULL, confidenceScore REAL NOT NULL DEFAULT 0.92, detectedAtTimestamp INTEGER NOT NULL)")
            }
        }

        fun getInstance(context: Context): LifeScoreDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeScoreDatabase::class.java,
                    "lifescore.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10)
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

