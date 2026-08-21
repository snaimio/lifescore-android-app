package com.lifescore.app.core.di

import android.content.Context
import com.lifescore.app.core.database.LifeScoreDatabase
import com.lifescore.app.data.remote.repository.FirebaseRepository
import com.lifescore.app.data.repository.*
import com.lifescore.app.domain.usecase.*

/**
 * Clean Architecture Dependency Injection Container for LifeScore.
 * Provides singleton dependencies, repositories, and use case providers.
 */
class LifeScoreContainer(private val context: Context) {

    val database: LifeScoreDatabase by lazy {
        LifeScoreDatabase.getInstance(context)
    }

    val firebaseRepository: FirebaseRepository by lazy {
        com.lifescore.app.data.remote.repository.FirebaseRepositoryImpl()
    }

    val lifeScoreRepository: LifeScoreRepository by lazy {
        LifeScoreRepositoryImpl(database)
    }

    val geminiCoachRepository: GeminiCoachRepository by lazy {
        GeminiCoachRepositoryImpl()
    }

    val localAuthRepository: com.lifescore.app.data.local.repository.LocalAuthRepository by lazy {
        com.lifescore.app.data.local.repository.LocalAuthRepository(database)
    }

    val authRepository: com.lifescore.app.data.remote.repository.AuthRepository by lazy {
        com.lifescore.app.data.remote.repository.AuthRepositoryImpl(
            firebaseRepository = firebaseRepository,
            localAuthRepository = localAuthRepository,
            lifeScoreRepository = lifeScoreRepository
        )
    }

    val billingRepository: BillingRepository by lazy {
        BillingRepositoryImpl(
            context = context,
            lifeScoreRepository = lifeScoreRepository,
            firebaseRepository = firebaseRepository
        )
    }

    val aiQuestGeneratorService by lazy {
        com.lifescore.app.data.ai.AiQuestGeneratorService()
    }

    val aiQuestRepository: AiQuestRepository by lazy {
        AiQuestRepositoryImpl(
            aiQuestDao = database.aiQuestDao(),
            taskDao = database.taskDao(),
            aiService = aiQuestGeneratorService
        )
    }

    val characterStatsRepository: CharacterStatsRepository by lazy {
        CharacterStatsRepositoryImpl(
            dao = database.characterStatsDao()
        )
    }

    val groupHabitRepository: GroupHabitRepository by lazy {
        GroupHabitRepositoryImpl(
            dao = database.groupHabitDao()
        )
    }

    val journalRepository: JournalRepository by lazy {
        JournalRepositoryImpl(
            dao = database.journalDao()
        )
    }

    val combatRepository: CombatRepository by lazy {
        CombatRepositoryImpl(
            bossDao = database.bossDao(),
            statsDao = database.characterStatsDao()
        )
    }

    val analyticsRepository: AnalyticsRepository by lazy {
        AnalyticsRepositoryImpl(
            scoreDao = database.dailyScoreDao()
        )
    }

    val hydrationRepository: HydrationRepository by lazy {
        HydrationRepositoryImpl(
            dao = database.hydrationDao()
        )
    }

    val lifeTrackersRepository: LifeTrackersRepository by lazy {
        LifeTrackersRepositoryImpl(
            lifeScoreRepository = lifeScoreRepository
        )
    }

    val atomicHabitsRepository: AtomicHabitsRepository by lazy {
        AtomicHabitsRepositoryImpl(
            lifeScoreRepository = lifeScoreRepository
        )
    }

    val recoveryRepository: RecoveryRepository by lazy {
        RecoveryRepositoryImpl(
            recoveryDao = database.recoveryDao(),
            lifeScoreRepository = lifeScoreRepository
        )
    }

    // Domain Use Cases
    val calculateLifeScoreUseCase by lazy { CalculateLifeScoreUseCase() }
    val getDailyTasksUseCase by lazy { GetDailyTasksUseCase(lifeScoreRepository) }
    val completeTaskUseCase by lazy { CompleteTaskUseCase(lifeScoreRepository) }
    val addTaskUseCase by lazy { AddTaskUseCase(lifeScoreRepository) }
    val deleteTaskUseCase by lazy { DeleteTaskUseCase(lifeScoreRepository) }
    val getUserProfileUseCase by lazy { GetUserProfileUseCase(lifeScoreRepository) }
    val updateUserProfileUseCase by lazy { UpdateUserProfileUseCase(lifeScoreRepository) }
    val getAIRecommendationUseCase by lazy { GetAIRecommendationUseCase(geminiCoachRepository) }
    val generateWeeklyAuditUseCase by lazy { GenerateWeeklyAuditUseCase(geminiCoachRepository) }
}
