package com.lifescore.app

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
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

class LifeScoreApp : Application() {

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

    override fun onCreate() {
        super.onCreate()

        // 1. Initialize Firebase & Firestore Offline Persistence
        try {
            FirebaseApp.initializeApp(this)
            val firestore = FirebaseFirestore.getInstance()
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(100 * 1024 * 1024) // 100 MB offline cache
                .build()
            firestore.firestoreSettings = settings
        } catch (e: Exception) {
            Log.w("LifeScoreApp", "Firebase initialization in offline/fallback mode: ${e.localizedMessage}")
        }

        // 2. Initialize Container & Repositories
        container = com.lifescore.app.core.di.LifeScoreContainer(this)
        database = LifeScoreDatabase.getInstance(this)
        lifeScoreRepository = LifeScoreRepositoryImpl(database)
        coachRepository = GeminiCoachRepositoryImpl()
        billingRepository = BillingRepositoryImpl(this, lifeScoreRepository).apply {
            startBillingConnection()
        }

        firebaseRepository = FirebaseRepositoryImpl()
        authRepository = AuthRepositoryImpl(firebaseRepository = firebaseRepository)

        // 3. Schedule Background WorkManager Workers
        ReminderWorker.scheduleDailyReminder(this)
        SyncWorker.schedulePeriodicSync(this)
    }
}
