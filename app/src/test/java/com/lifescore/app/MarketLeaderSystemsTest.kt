package com.lifescore.app

import com.lifescore.app.data.local.dao.*
import com.lifescore.app.data.local.entity.*
import com.lifescore.app.data.repository.*
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.LifeTask
import com.lifescore.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MarketLeaderSystemsTest {

    private lateinit var mockLifeScoreRepo: LifeScoreRepository
    private var testUserXp = 500

    @Before
    fun setup() {
        mockLifeScoreRepo = object : LifeScoreRepository {
            override fun getUserProfile(): Flow<UserProfile> = flowOf(
                UserProfile(id = 1L, name = "Hero", currentXp = testUserXp, currentLevel = 3)
            )
            override suspend fun updateUserProfile(user: UserProfile) {
                testUserXp = user.currentXp
            }
            override fun getAllTasks(): Flow<List<LifeTask>> = flowOf(emptyList())
            override fun getTasksByDimension(dimension: DimensionType): Flow<List<LifeTask>> = flowOf(emptyList())
            override suspend fun addTask(title: String, dimension: DimensionType, points: Int): Long = 1L
            override suspend fun toggleTaskCompletion(task: LifeTask) {}
            override suspend fun deleteTask(task: LifeTask) {}
            override suspend fun seedInitialDataIfEmpty() {}
        }
    }

    // 1. Headway Flashcards Leitner System Test
    @Test
    fun testFlashcardLeitnerSpacedRepetitionProgression() = runBlocking {
        val cardsList = mutableListOf(
            FlashcardEntity(
                id = 10,
                bookId = "atomic_habits",
                bookTitle = "Atomic Habits",
                question = "What is the 2-minute rule?",
                answer = "Make habits take <2 mins to start.",
                keyConcept = "Friction Reduction",
                boxLevel = 1
            )
        )

        val mockDao = object : FlashcardDao {
            override fun getAllFlashcards(): Flow<List<FlashcardEntity>> = flowOf(cardsList)
            override fun getFlashcardsForBook(bookId: String): Flow<List<FlashcardEntity>> = flowOf(cardsList)
            override fun getDueFlashcards(currentTimestamp: Long): Flow<List<FlashcardEntity>> = flowOf(cardsList)
            override suspend fun insertFlashcard(flashcard: FlashcardEntity): Long = 1L
            override suspend fun insertFlashcards(flashcards: List<FlashcardEntity>) {}
            override suspend fun updateFlashcard(flashcard: FlashcardEntity) {
                cardsList[0] = flashcard
            }
            override fun getLearningPlans(userId: String): Flow<List<LearningPlanEntity>> = flowOf(emptyList())
            override suspend fun insertLearningPlan(plan: LearningPlanEntity) {}
            override suspend fun updateLearningPlan(plan: LearningPlanEntity) {}
        }

        val repo = BookLearningRepositoryImpl(mockDao, mockLifeScoreRepo)

        // Rating EASY should bump boxLevel to 2 and award 15 XP
        val xpAwarded = repo.reviewFlashcard(10L, "EASY")
        assertEquals(15, xpAwarded)
        assertEquals(2, cardsList[0].boxLevel)
        assertEquals(1, cardsList[0].repetitions)
        assertEquals(515, testUserXp)
    }

    // 2. Lifestack Circadian Energy Prediction Test
    @Test
    fun testCircadianEnergyPredictionAndTaskCompletion() = runBlocking {
        var lastPrediction: EnergyPredictionEntity? = null
        val mockDao = object : EnergyDao {
            override fun getLatestEnergyPrediction(userId: String): Flow<EnergyPredictionEntity?> = flowOf(lastPrediction)
            override suspend fun insertEnergyPrediction(prediction: EnergyPredictionEntity): Long {
                lastPrediction = prediction
                return 1L
            }
            override fun getScheduledTasks(userId: String): Flow<List<SmartScheduledTaskEntity>> = flowOf(emptyList())
            override suspend fun insertScheduledTask(task: SmartScheduledTaskEntity): Long = 1L
            override suspend fun updateScheduledTask(task: SmartScheduledTaskEntity) {}
            override suspend fun toggleTaskComplete(taskId: Long, isCompleted: Boolean) {}
        }

        val repo = EnergyScheduleRepositoryImpl(mockDao, mockLifeScoreRepo)
        val pred = repo.refreshCircadianPrediction()

        assertTrue(pred.predictedFocusScore in 80..98)
        assertEquals(9, pred.morningPeakHour)
        assertEquals(14, pred.afternoonDipHour)
    }

    // 3. Finch Gentle Virtual Pet Happiness Test
    @Test
    fun testVirtualPetCareAndHappinessGrowth() = runBlocking {
        var currentPet = VirtualPetEntity(
            userId = "default_user",
            petName = "Pip",
            happinessLevel = 70,
            energyLevel = 80,
            evolutionStage = 1
        )

        val mockDao = object : VirtualPetDao {
            override fun getPet(userId: String): Flow<VirtualPetEntity?> = flowOf(currentPet)
            override suspend fun insertOrUpdatePet(pet: VirtualPetEntity) { currentPet = pet }
            override suspend fun feedAndPet(userId: String, happiness: Int, energy: Int, timestamp: Long) {
                currentPet = currentPet.copy(happinessLevel = happiness, energyLevel = energy)
            }
            override suspend fun updateCustomization(userId: String, hat: String, accessory: String) {
                currentPet = currentPet.copy(equippedHat = hat, equippedAccessory = accessory)
            }
        }

        val repo = VirtualPetRepositoryImpl(mockDao, mockLifeScoreRepo)

        val boost = repo.feedPet("default_user")
        assertEquals(15, boost)
        assertEquals(85, currentPet.happinessLevel)
        assertEquals(90, currentPet.energyLevel)
    }

    // 4. Habitica RPG Party Boss Raid Damage Test
    @Test
    fun testPartyBossQuestDamageAndLog() = runBlocking {
        var currentParty = PartyEntity(
            partyId = "alpha_squad",
            partyName = "Alpha Vanguard",
            leaderName = "Alex",
            memberCount = 4,
            activeQuestName = "Procrastination Dragon",
            questBossMaxHp = 500,
            questBossCurrentHp = 100
        )
        val messageList = mutableListOf<PartyMessageEntity>()

        val mockDao = object : PartyDao {
            override fun getCurrentParty(): Flow<PartyEntity?> = flowOf(currentParty)
            override suspend fun insertParty(party: PartyEntity) { currentParty = party }
            override suspend fun dealBossDamage(partyId: String, damage: Int) {
                currentParty = currentParty.copy(questBossCurrentHp = (currentParty.questBossCurrentHp - damage).coerceAtLeast(0))
            }
            override fun getPartyMessages(partyId: String): Flow<List<PartyMessageEntity>> = flowOf(messageList)
            override suspend fun insertMessage(message: PartyMessageEntity): Long {
                messageList.add(message)
                return messageList.size.toLong()
            }
        }

        val repo = PartySystemRepositoryImpl(mockDao, mockLifeScoreRepo)
        val bossDefeated = repo.dealQuestDamage("alpha_squad", 100)

        assertTrue(bossDefeated)
        assertEquals(0, currentParty.questBossCurrentHp)
        assertEquals(1, messageList.size)
        assertEquals(100, messageList[0].questDamageDealt)
    }

    // 5. Fabulous Habit Stacking Intentions Test
    @Test
    fun testHabitStackingCreationAndCompletion() = runBlocking {
        val stacks = mutableListOf<HabitStackEntity>()

        val mockDao = object : JourneyDao {
            override fun getAllJourneys(): Flow<List<ScienceJourneyEntity>> = flowOf(emptyList())
            override suspend fun insertJourneys(journeys: List<ScienceJourneyEntity>) {}
            override suspend fun toggleEnrollment(journeyId: String, isEnrolled: Boolean) {}
            override suspend fun advanceJourneyDay(journeyId: String) {}
            override fun getHabitStacks(): Flow<List<HabitStackEntity>> = flowOf(stacks)
            override suspend fun insertHabitStack(stack: HabitStackEntity): Long {
                stacks.add(stack.copy(id = 1))
                return 1L
            }
            override suspend fun toggleStackCompleted(id: Long, completed: Boolean) {
                stacks[0] = stacks[0].copy(isCompletedToday = completed, streakDays = stacks[0].streakDays + 1)
            }
        }

        val repo = ScienceJourneyRepositoryImpl(mockDao, mockLifeScoreRepo)
        val id = repo.createHabitStack(
            trigger = "After I brew tea",
            action = "I will meditate for 3 minutes",
            reward = "Then I will drink the first sip warmly",
            dimension = DimensionType.HEALTH
        )

        assertEquals(1L, id)
        assertEquals(1, stacks.size)
        repo.toggleHabitStackCompleted(1L, true)
        assertEquals(1, stacks[0].streakDays)
        assertTrue(stacks[0].isCompletedToday)
    }
}
