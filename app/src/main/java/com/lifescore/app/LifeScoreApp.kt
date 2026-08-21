package com.lifescore.app

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.work.Configuration
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.lifescore.app.core.database.LifeScoreDatabase
import com.lifescore.app.data.remote.repository.AuthRepository
import com.lifescore.app.data.remote.repository.AuthRepositoryImpl
import com.lifescore.app.data.remote.repository.FirebaseRepository
import com.lifescore.app.data.remote.repository.FirebaseRepositoryImpl
import com.lifescore.app.data.repository.BillingRepository
import com.lifescore.app.data.repository.BillingRepositoryImpl
import com.lifescore.app.data.repository.GeminiCoachRepository
import com.lifescore.app.data.repository.GeminiCoachRepositoryImpl
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.data.repository.LifeScoreRepositoryImpl
import com.lifescore.app.services.ReminderWorker
import com.lifescore.app.services.SyncWorker

class LifeScoreApp : Application(), Configuration.Provider {

    lateinit var database: LifeScoreDatabase
        private set

    lateinit var lifeScoreRepository: LifeScoreRepository
        private set

    lateinit var coachRepository: GeminiCoachRepository
        private set

    lateinit var billingRepository: BillingRepository
        private set

    lateinit var firebaseRepository: FirebaseRepository
        private set

    lateinit var container: com.lifescore.app.core.di.LifeScoreContainer
        private set

    lateinit var authRepository: AuthRepository
        private set

    val aiQuestRepository: com.lifescore.app.data.repository.AiQuestRepository
        get() = container.aiQuestRepository

    val characterStatsRepository: com.lifescore.app.data.repository.CharacterStatsRepository
        get() = container.characterStatsRepository

    val groupHabitRepository: com.lifescore.app.data.repository.GroupHabitRepository
        get() = container.groupHabitRepository

    val journalRepository: com.lifescore.app.data.repository.JournalRepository
        get() = container.journalRepository

    val combatRepository: com.lifescore.app.data.repository.CombatRepository
        get() = container.combatRepository

    val analyticsRepository: com.lifescore.app.data.repository.AnalyticsRepository
        get() = container.analyticsRepository

    val hydrationRepository: com.lifescore.app.data.repository.HydrationRepository
        get() = container.hydrationRepository

    val lifeTrackersRepository: com.lifescore.app.data.repository.LifeTrackersRepository
        get() = container.lifeTrackersRepository

    val atomicHabitsRepository: com.lifescore.app.data.repository.AtomicHabitsRepository
        get() = container.atomicHabitsRepository

    val recoveryRepository: com.lifescore.app.data.repository.RecoveryRepository
        get() = container.recoveryRepository

    private var firebaseAnalytics: FirebaseAnalytics? = null

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG_MODE) Log.DEBUG else Log.ERROR)
            .build()

    override fun onCreate() {
        super.onCreate()

        // 1. Initialize Firebase & Crashlytics / Analytics
        try {
            FirebaseApp.initializeApp(this)
            firebaseAnalytics = FirebaseAnalytics.getInstance(this)
            FirebaseCrashlytics.getInstance().apply {
                setCrashlyticsCollectionEnabled(true)
                setCustomKey("debug_mode", BuildConfig.DEBUG_MODE)
                setCustomKey("app_version", BuildConfig.VERSION_NAME)
            }

            val firestore = FirebaseFirestore.getInstance()
            val settings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(
                    com.google.firebase.firestore.PersistentCacheSettings.newBuilder()
                        .setSizeBytes(100L * 1024 * 1024) // 100 MB offline cache
                        .build()
                )
                .build()
            firestore.firestoreSettings = settings
        } catch (e: Exception) {
            Log.w("LifeScoreApp", "Firebase initialization in offline/fallback mode: ${e.localizedMessage}")
        }

        // 2. Initialize Container & Repositories
        container = com.lifescore.app.core.di.LifeScoreContainer(this)
        database = LifeScoreDatabase.getInstance(this)
        lifeScoreRepository = LifeScoreRepositoryImpl(database)
        coachRepository = GeminiCoachRepositoryImpl(apiKey = BuildConfig.GEMINI_API_KEY)
        billingRepository = BillingRepositoryImpl(this, lifeScoreRepository).apply {
            startBillingConnection()
        }

        firebaseRepository = FirebaseRepositoryImpl()
        authRepository = AuthRepositoryImpl(
            firebaseRepository = firebaseRepository,
            localAuthRepository = container.localAuthRepository,
            lifeScoreRepository = lifeScoreRepository
        )

        // 3. Schedule Background WorkManager Workers
        ReminderWorker.scheduleDailyReminder(this)
        SyncWorker.schedulePeriodicSync(this)
    }

    fun logAnalyticsEvent(eventName: String, params: Bundle? = null) {
        try {
            firebaseAnalytics?.logEvent(eventName, params)
        } catch (_: Exception) {}
    }
}
