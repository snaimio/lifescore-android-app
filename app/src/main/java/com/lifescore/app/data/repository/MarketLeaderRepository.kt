package com.lifescore.app.data.repository

import com.lifescore.app.data.local.dao.*
import com.lifescore.app.data.local.entity.*
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.UserProfile
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

// ==========================================
// 1. HEADWAY / BITELY: FLASHCARDS & PLANS
// ==========================================
interface BookLearningRepository {
    fun getAllFlashcards(): Flow<List<FlashcardEntity>>
    fun getDueFlashcards(): Flow<List<FlashcardEntity>>
    suspend fun reviewFlashcard(cardId: Long, rating: String): Int // Easy, Good, Hard -> updates Leitner box
    fun getLearningPlans(userId: String = "default_user"): Flow<List<LearningPlanEntity>>
    suspend fun generate30DayPlan(targetDimension: DimensionType, userId: String = "default_user"): LearningPlanEntity
    suspend fun advancePlanDay(planId: String)
    suspend fun seedDefaultFlashcardsIfEmpty()
}

class BookLearningRepositoryImpl(
    private val flashcardDao: FlashcardDao,
    private val lifeScoreRepository: LifeScoreRepository
) : BookLearningRepository {

    private suspend fun awardXp(amount: Int) {
        try {
            val user = lifeScoreRepository.getUserProfile().firstOrNull()
            if (user != null) {
                lifeScoreRepository.updateUserProfile(user.copy(currentXp = user.currentXp + amount))
            }
        } catch (_: Exception) {}
    }

    override fun getAllFlashcards(): Flow<List<FlashcardEntity>> = flashcardDao.getAllFlashcards()

    override fun getDueFlashcards(): Flow<List<FlashcardEntity>> =
        flashcardDao.getDueFlashcards(System.currentTimeMillis() + 86400000L) // Include due within 24h

    override suspend fun reviewFlashcard(cardId: Long, rating: String): Int {
        val all = flashcardDao.getAllFlashcards().firstOrNull() ?: emptyList()
        val card = all.find { it.id == cardId } ?: return 0

        val (nextBox, intervalMultiplier) = when (rating.uppercase()) {
            "EASY" -> Pair((card.boxLevel + 1).coerceAtMost(5), 4)
            "GOOD" -> Pair((card.boxLevel + 1).coerceAtMost(5), 2)
            else -> Pair(1, 1) // HARD -> reset to Box 1
        }

        val nextReview = System.currentTimeMillis() + (intervalMultiplier * 24 * 60 * 60 * 1000L)
        val updated = card.copy(
            boxLevel = nextBox,
            nextReviewTimestamp = nextReview,
            repetitions = card.repetitions + 1,
            lastReviewedAt = System.currentTimeMillis()
        )
        flashcardDao.updateFlashcard(updated)

        val xp = if (rating.uppercase() == "EASY") 15 else 10
        awardXp(xp)
        return xp
    }

    override fun getLearningPlans(userId: String): Flow<List<LearningPlanEntity>> =
        flashcardDao.getLearningPlans(userId)

    override suspend fun generate30DayPlan(targetDimension: DimensionType, userId: String): LearningPlanEntity {
        val plan = LearningPlanEntity(
            planId = "plan_${targetDimension.name.lowercase()}_${System.currentTimeMillis()}",
            userId = userId,
            title = "30-Day ${targetDimension.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }} Mastery Plan",
            targetDimension = targetDimension,
            totalDays = 30,
            currentDay = 1,
            bookIdsCsv = "atomic_habits,deep_work,psychology_of_money,why_we_sleep"
        )
        flashcardDao.insertLearningPlan(plan)
        awardXp(50)
        return plan
    }

    override suspend fun advancePlanDay(planId: String) {
        val plans = flashcardDao.getLearningPlans().firstOrNull() ?: emptyList()
        val plan = plans.find { it.planId == planId } ?: return
        val nextDay = plan.currentDay + 1
        val completed = nextDay >= plan.totalDays
        flashcardDao.updateLearningPlan(plan.copy(currentDay = nextDay, isCompleted = completed))
        awardXp(25)
    }

    override suspend fun seedDefaultFlashcardsIfEmpty() {
        val cards = listOf(
            FlashcardEntity(
                bookId = "atomic_habits",
                bookTitle = "Atomic Habits",
                question = "What is the 2-Minute Rule for building new habits?",
                answer = "When you start a new habit, it should take less than two minutes to do, making starting frictionless.",
                keyConcept = "Friction Reduction & Habit Inception",
                boxLevel = 1
            ),
            FlashcardEntity(
                bookId = "atomic_habits",
                bookTitle = "Atomic Habits",
                question = "What is Habit Stacking formula?",
                answer = "'After [CURRENT HABIT], I will [NEW HABIT].'",
                keyConcept = "Implementation Intentions",
                boxLevel = 2
            ),
            FlashcardEntity(
                bookId = "deep_work",
                bookTitle = "Deep Work",
                question = "What is Attention Residue?",
                answer = "When you switch from Task A to Task B, your attention does not immediately follow; a residue remains stuck thinking about the previous task.",
                keyConcept = "Cognitive Focus Hygiene",
                boxLevel = 1
            ),
            FlashcardEntity(
                bookId = "psychology_of_money",
                bookTitle = "The Psychology of Money",
                question = "What is the highest form of wealth according to Morgan Housel?",
                answer = "The ability to wake up every morning and say, 'I can do whatever I want today.'",
                keyConcept = "Financial Autonomy & Sovereignty",
                boxLevel = 3
            )
        )
        flashcardDao.insertFlashcards(cards)
    }
}

// ==========================================
// 2. LIFESTACK: ENERGY PREDICTION & SMART SCHEDULE
// ==========================================
interface EnergyScheduleRepository {
    fun getLatestPrediction(userId: String = "default_user"): Flow<EnergyPredictionEntity>
    fun getScheduledTasks(userId: String = "default_user"): Flow<List<SmartScheduledTaskEntity>>
    suspend fun toggleTaskCompleted(taskId: Long, completed: Boolean)
    suspend fun autoScheduleTasks(tasks: List<SmartScheduledTaskEntity>)
    suspend fun refreshCircadianPrediction(userId: String = "default_user"): EnergyPredictionEntity
}

class EnergyScheduleRepositoryImpl(
    private val energyDao: EnergyDao,
    private val lifeScoreRepository: LifeScoreRepository
) : EnergyScheduleRepository {

    private suspend fun awardXp(amount: Int) {
        try {
            val user = lifeScoreRepository.getUserProfile().firstOrNull()
            if (user != null) {
                lifeScoreRepository.updateUserProfile(user.copy(currentXp = user.currentXp + amount))
            }
        } catch (_: Exception) {}
    }

    override fun getLatestPrediction(userId: String): Flow<EnergyPredictionEntity> {
        return energyDao.getLatestEnergyPrediction(userId).map { pred ->
            pred ?: EnergyPredictionEntity(
                dateIso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                morningPeakHour = 9,
                afternoonDipHour = 14,
                eveningPeakHour = 18,
                predictedFocusScore = 88,
                sleepQualityScore = 91,
                restingHeartRate = 60,
                peakEnergyStartHour = 9,
                peakEnergyEndHour = 12
            )
        }
    }

    override fun getScheduledTasks(userId: String): Flow<List<SmartScheduledTaskEntity>> {
        return energyDao.getScheduledTasks(userId).map { list ->
            if (list.isEmpty()) {
                listOf(
                    SmartScheduledTaskEntity(
                        id = 1,
                        taskTitle = "Architect System Core & Performance Tuning",
                        estimatedMinutes = 60,
                        cognitiveDemand = "HIGH",
                        scheduledTimeIso = "09:30 AM",
                        energyMatchPercentage = 98,
                        isCompleted = false
                    ),
                    SmartScheduledTaskEntity(
                        id = 2,
                        taskTitle = "Draft High-Impact Strategic Roadmap",
                        estimatedMinutes = 45,
                        cognitiveDemand = "HIGH",
                        scheduledTimeIso = "11:00 AM",
                        energyMatchPercentage = 94,
                        isCompleted = false
                    ),
                    SmartScheduledTaskEntity(
                        id = 3,
                        taskTitle = "Routine Code Review & Minor Fixes",
                        estimatedMinutes = 30,
                        cognitiveDemand = "LOW",
                        scheduledTimeIso = "02:30 PM",
                        energyMatchPercentage = 88,
                        isCompleted = false
                    ),
                    SmartScheduledTaskEntity(
                        id = 4,
                        taskTitle = "Creative UI Polish & Animation Design",
                        estimatedMinutes = 45,
                        cognitiveDemand = "MEDIUM",
                        scheduledTimeIso = "05:30 PM",
                        energyMatchPercentage = 92,
                        isCompleted = false
                    )
                )
            } else list
        }
    }

    override suspend fun toggleTaskCompleted(taskId: Long, completed: Boolean) {
        energyDao.toggleTaskComplete(taskId, completed)
        if (completed) awardXp(35)
    }

    override suspend fun autoScheduleTasks(tasks: List<SmartScheduledTaskEntity>) {
        tasks.forEach { energyDao.insertScheduledTask(it) }
    }

    override suspend fun refreshCircadianPrediction(userId: String): EnergyPredictionEntity {
        val pred = EnergyPredictionEntity(
            dateIso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            morningPeakHour = 9,
            afternoonDipHour = 14,
            eveningPeakHour = 18,
            predictedFocusScore = (80..98).random(),
            sleepQualityScore = (85..95).random(),
            restingHeartRate = (58..64).random(),
            peakEnergyStartHour = 9,
            peakEnergyEndHour = 12
        )
        energyDao.insertEnergyPrediction(pred)
        return pred
    }
}

// ==========================================
// 3. FINCH: GENTLE VIRTUAL PET COMPANION
// ==========================================
interface VirtualPetRepository {
    fun getPet(userId: String = "default_user"): Flow<VirtualPetEntity>
    suspend fun feedPet(userId: String = "default_user"): Int // returns happiness boost
    suspend fun petAffection(userId: String = "default_user"): Int // returns happiness boost
    suspend fun customizePet(hat: String, accessory: String, userId: String = "default_user")
}

class VirtualPetRepositoryImpl(
    private val virtualPetDao: VirtualPetDao,
    private val lifeScoreRepository: LifeScoreRepository
) : VirtualPetRepository {

    private suspend fun awardXp(amount: Int) {
        try {
            val user = lifeScoreRepository.getUserProfile().firstOrNull()
            if (user != null) {
                lifeScoreRepository.updateUserProfile(user.copy(currentXp = user.currentXp + amount))
            }
        } catch (_: Exception) {}
    }

    override fun getPet(userId: String): Flow<VirtualPetEntity> {
        return virtualPetDao.getPet(userId).map { pet ->
            pet ?: VirtualPetEntity(
                userId = userId,
                petName = "Pip",
                petType = "Phoenix",
                happinessLevel = 85,
                energyLevel = 90,
                evolutionStage = 2,
                equippedHat = "Crown of Focus",
                equippedAccessory = "Golden Scarf",
                petMoodEmoji = "🐥",
                gentleStreakDays = 7
            )
        }
    }

    override suspend fun feedPet(userId: String): Int {
        val current = getPet(userId).first()
        val newHappiness = (current.happinessLevel + 15).coerceAtMost(100)
        val newEnergy = (current.energyLevel + 10).coerceAtMost(100)
        virtualPetDao.feedAndPet(userId, newHappiness, newEnergy, System.currentTimeMillis())
        awardXp(20)
        return 15
    }

    override suspend fun petAffection(userId: String): Int {
        val current = getPet(userId).first()
        val newHappiness = (current.happinessLevel + 10).coerceAtMost(100)
        virtualPetDao.feedAndPet(userId, newHappiness, current.energyLevel, System.currentTimeMillis())
        awardXp(15)
        return 10
    }

    override suspend fun customizePet(hat: String, accessory: String, userId: String) {
        virtualPetDao.updateCustomization(userId, hat, accessory)
    }
}

// ==========================================
// 4. INSIGHT TIMER: MEDITATION & TIMERS
// ==========================================
interface MeditationLibraryRepository {
    fun getAllMeditations(): Flow<List<MeditationTrackEntity>>
    fun getMeditationsByCategory(category: String): Flow<List<MeditationTrackEntity>>
    fun getLiveEvents(): Flow<List<LiveEventEntity>>
    suspend fun toggleBookmark(trackId: String, isBookmarked: Boolean)
    suspend fun toggleRegistration(eventId: String, isRegistered: Boolean)
    suspend fun recordMeditationCompleted(durationMinutes: Int): Int // awards XP
    suspend fun seedDefaultTracksIfEmpty()
}

class MeditationLibraryRepositoryImpl(
    private val meditationDao: MeditationDao,
    private val lifeScoreRepository: LifeScoreRepository
) : MeditationLibraryRepository {

    private suspend fun awardXp(amount: Int) {
        try {
            val user = lifeScoreRepository.getUserProfile().firstOrNull()
            if (user != null) {
                lifeScoreRepository.updateUserProfile(user.copy(currentXp = user.currentXp + amount))
            }
        } catch (_: Exception) {}
    }

    override fun getAllMeditations(): Flow<List<MeditationTrackEntity>> = meditationDao.getAllMeditations()

    override fun getMeditationsByCategory(category: String): Flow<List<MeditationTrackEntity>> =
        meditationDao.getMeditationsByCategory(category)

    override fun getLiveEvents(): Flow<List<LiveEventEntity>> = meditationDao.getLiveEvents()

    override suspend fun toggleBookmark(trackId: String, isBookmarked: Boolean) {
        meditationDao.toggleBookmark(trackId, isBookmarked)
    }

    override suspend fun toggleRegistration(eventId: String, isRegistered: Boolean) {
        meditationDao.toggleRegistration(eventId, isRegistered)
    }

    override suspend fun recordMeditationCompleted(durationMinutes: Int): Int {
        val xp = (durationMinutes * 2).coerceAtLeast(20)
        awardXp(xp)
        return xp
    }

    override suspend fun seedDefaultTracksIfEmpty() {
        val tracks = listOf(
            MeditationTrackEntity(
                trackId = "med_1",
                title = "Deep Nervous System Reset & Vagus Nerve Calm",
                teacherName = "Dr. Tara Brach",
                category = "Mindfulness",
                durationMinutes = 12,
                playsCount = 48500,
                rating = 4.98,
                ambientSound = "Gentle Tibetan Bells"
            ),
            MeditationTrackEntity(
                trackId = "med_2",
                title = "4-7-8 Breathwork for Instant De-Stressing",
                teacherName = "Andrew Huberman",
                category = "Breathwork",
                durationMinutes = 8,
                playsCount = 72100,
                rating = 4.95,
                ambientSound = "Forest Rain & Chimes"
            ),
            MeditationTrackEntity(
                trackId = "med_3",
                title = "Delta Wave Sleep Yoga Nidra",
                teacherName = "Jennifer Piercy",
                category = "Sleep",
                durationMinutes = 20,
                playsCount = 104000,
                rating = 4.99,
                ambientSound = "Deep Ocean Waves"
            ),
            MeditationTrackEntity(
                trackId = "med_4",
                title = "Executive Focus & High-Performance Clarity",
                teacherName = "Michael Sealey",
                category = "Focus",
                durationMinutes = 15,
                playsCount = 31200,
                rating = 4.92,
                ambientSound = "Alpha Binaural Waves"
            )
        )
        meditationDao.insertMeditations(tracks)

        val liveEvents = listOf(
            LiveEventEntity("event_1", "Global Solstice Mindfulness Circle", "Jack Kornfield", "Tomorrow at 8:00 AM", 840),
            LiveEventEntity("event_2", "Sound Bath & Healing Frequency Workshop", "Sarah Blondin", "Saturday at 6:00 PM", 1240)
        )
        meditationDao.insertLiveEvents(liveEvents)
    }
}

// ==========================================
// 5. HABITICA: RPG PARTY SYSTEM & GUILDS
// ==========================================
interface PartySystemRepository {
    fun getCurrentParty(): Flow<PartyEntity>
    fun getPartyMessages(partyId: String = "alpha_squad"): Flow<List<PartyMessageEntity>>
    suspend fun dealQuestDamage(partyId: String = "alpha_squad", damage: Int): Boolean // true if boss defeated
    suspend fun sendPartyMessage(partyId: String = "alpha_squad", text: String, senderName: String = "Hero")
}

class PartySystemRepositoryImpl(
    private val partyDao: PartyDao,
    private val lifeScoreRepository: LifeScoreRepository
) : PartySystemRepository {

    private suspend fun awardXp(amount: Int) {
        try {
            val user = lifeScoreRepository.getUserProfile().firstOrNull()
            if (user != null) {
                lifeScoreRepository.updateUserProfile(user.copy(currentXp = user.currentXp + amount))
            }
        } catch (_: Exception) {}
    }

    override fun getCurrentParty(): Flow<PartyEntity> {
        return partyDao.getCurrentParty().map { party ->
            party ?: PartyEntity(
                partyId = "alpha_squad",
                partyName = "Ascendant Guild: Alpha Vanguard",
                leaderName = "Alex Mercer",
                memberCount = 4,
                activeQuestName = "The Dread Procrastinator Dragon",
                questBossMaxHp = 500,
                questBossCurrentHp = 210,
                teamXpMultiplier = 1.30,
                isCurrentUserLeader = false
            )
        }
    }

    override fun getPartyMessages(partyId: String): Flow<List<PartyMessageEntity>> {
        return partyDao.getPartyMessages(partyId).map { list ->
            if (list.isEmpty()) {
                listOf(
                    PartyMessageEntity(1, partyId, "Alex Mercer", "🧙‍♂️", "Let's crush the daily quests today team! Boss is almost down.", System.currentTimeMillis() - 3600000, 0),
                    PartyMessageEntity(2, partyId, "Elena Rostova", "🏹", "Completed 45m deep work! Hit the boss for 45 damage 💥", System.currentTimeMillis() - 1800000, 45),
                    PartyMessageEntity(3, partyId, "Marcus Vance", "🛡️", "Morning workout completed! Streak protected.", System.currentTimeMillis() - 600000, 30)
                )
            } else list
        }
    }

    override suspend fun dealQuestDamage(partyId: String, damage: Int): Boolean {
        partyDao.dealBossDamage(partyId, damage)
        partyDao.insertMessage(
            PartyMessageEntity(
                partyId = partyId,
                senderName = "You",
                senderAvatar = "⚔️",
                messageText = "Completed daily dimension quest and struck the boss for $damage damage!",
                questDamageDealt = damage
            )
        )
        awardXp(damage * 2)
        val current = getCurrentParty().first()
        return current.questBossCurrentHp <= damage
    }

    override suspend fun sendPartyMessage(partyId: String, text: String, senderName: String) {
        partyDao.insertMessage(
            PartyMessageEntity(
                partyId = partyId,
                senderName = senderName,
                senderAvatar = "👤",
                messageText = text
            )
        )
    }
}

// ==========================================
// 6. COACH.ME: HUMAN COACH MARKETPLACE
// ==========================================
interface CoachMarketplaceRepository {
    fun getAllCoaches(): Flow<List<CoachEntity>>
    fun getBookings(userId: String = "default_user"): Flow<List<CoachBookingEntity>>
    suspend fun bookCoachSession(coachId: String, coachName: String, dateIso: String, goal: String, userId: String = "default_user"): Long
    suspend fun seedDefaultCoachesIfEmpty()
}

class CoachMarketplaceRepositoryImpl(
    private val coachDao: CoachDao
) : CoachMarketplaceRepository {

    override fun getAllCoaches(): Flow<List<CoachEntity>> = coachDao.getAllCoaches()

    override fun getBookings(userId: String): Flow<List<CoachBookingEntity>> = coachDao.getBookings(userId)

    override suspend fun bookCoachSession(
        coachId: String,
        coachName: String,
        dateIso: String,
        goal: String,
        userId: String
    ): Long {
        return coachDao.insertBooking(
            CoachBookingEntity(
                userId = userId,
                coachId = coachId,
                coachName = coachName,
                scheduledDateIso = dateIso,
                sessionGoal = goal,
                isConfirmed = true
            )
        )
    }

    override suspend fun seedDefaultCoachesIfEmpty() {
        val coaches = listOf(
            CoachEntity(
                coachId = "coach_1",
                name = "Dr. Michael Chen",
                title = "Executive Performance & Flow Coach",
                specialty = "Executive Productivity",
                avatarEmoji = "👨‍💼",
                rating = 4.98,
                reviewsCount = 214,
                hourlyRateUsd = 65,
                bio = "Former Stanford neuroscience researcher specializing in ultra-high output deep work systems and burnout recovery."
            ),
            CoachEntity(
                coachId = "coach_2",
                name = "Elena Rostova, CSCS",
                title = "Strength & Metabolic Optimization Coach",
                specialty = "Fitness & Nutrition",
                avatarEmoji = "👩‍🔬",
                rating = 4.96,
                reviewsCount = 189,
                hourlyRateUsd = 50,
                bio = "Olympic lifting coach and nutritionist helping professionals build effortless daily training routines."
            ),
            CoachEntity(
                coachId = "coach_3",
                name = "Kavita Sharma",
                title = "Mindfulness & Emotional Agility Mentor",
                specialty = "Mental Health",
                avatarEmoji = "🧘‍♀️",
                rating = 4.99,
                reviewsCount = 310,
                hourlyRateUsd = 40,
                bio = "Certified MBSR teacher guiding founders and creatives through stress reframing and daily stillness practices."
            )
        )
        coachDao.insertCoaches(coaches)
    }
}

// ==========================================
// 7. FABULOUS: SCIENCE JOURNEYS & HABIT STACKING
// ==========================================
interface ScienceJourneyRepository {
    fun getAllJourneys(): Flow<List<ScienceJourneyEntity>>
    fun getHabitStacks(): Flow<List<HabitStackEntity>>
    suspend fun createHabitStack(trigger: String, action: String, reward: String, dimension: DimensionType): Long
    suspend fun toggleHabitStackCompleted(id: Long, completed: Boolean)
    suspend fun toggleJourneyEnrollment(journeyId: String, enrolled: Boolean)
    suspend fun advanceJourneyDay(journeyId: String)
    suspend fun seedDefaultJourneysIfEmpty()
}

class ScienceJourneyRepositoryImpl(
    private val journeyDao: JourneyDao,
    private val lifeScoreRepository: LifeScoreRepository
) : ScienceJourneyRepository {

    private suspend fun awardXp(amount: Int) {
        try {
            val user = lifeScoreRepository.getUserProfile().firstOrNull()
            if (user != null) {
                lifeScoreRepository.updateUserProfile(user.copy(currentXp = user.currentXp + amount))
            }
        } catch (_: Exception) {}
    }

    override fun getAllJourneys(): Flow<List<ScienceJourneyEntity>> = journeyDao.getAllJourneys()

    override fun getHabitStacks(): Flow<List<HabitStackEntity>> = journeyDao.getHabitStacks()

    override suspend fun createHabitStack(trigger: String, action: String, reward: String, dimension: DimensionType): Long {
        val stack = HabitStackEntity(
            triggerHabit = trigger,
            newActionHabit = action,
            rewardHabit = reward,
            targetDimension = dimension
        )
        val id = journeyDao.insertHabitStack(stack)
        awardXp(30)
        return id
    }

    override suspend fun toggleHabitStackCompleted(id: Long, completed: Boolean) {
        journeyDao.toggleStackCompleted(id, completed)
        if (completed) awardXp(25)
    }

    override suspend fun toggleJourneyEnrollment(journeyId: String, enrolled: Boolean) {
        journeyDao.toggleEnrollment(journeyId, enrolled)
    }

    override suspend fun advanceJourneyDay(journeyId: String) {
        journeyDao.advanceJourneyDay(journeyId)
        awardXp(40)
    }

    override suspend fun seedDefaultJourneysIfEmpty() {
        val journeys = listOf(
            ScienceJourneyEntity(
                journeyId = "journey_morning",
                title = "Morning Energy & Peak Momentum",
                subtitle = "Ignite alertness, hydration & dopamine baseline",
                behavioralPrinciple = "Circadian Light + Hydration Stacking",
                durationDays = 21,
                currentDay = 4,
                currentMilestone = "Day 4: 10-Minute Morning Sunlight Anchor",
                isEnrolled = true,
                completionPercentage = 19
            ),
            ScienceJourneyEntity(
                journeyId = "journey_deep_work",
                title = "Unbreakable Deep Work Architecture",
                subtitle = "Eliminate context switching and hyper-focus",
                behavioralPrinciple = "Time-Boxing & Attention Residue Protocol",
                durationDays = 14,
                currentDay = 1,
                currentMilestone = "Day 1: Zero-Tab Clean Workstation Ritual",
                isEnrolled = false,
                completionPercentage = 0
            ),
            ScienceJourneyEntity(
                journeyId = "journey_evening_winddown",
                title = "Restorative Sleep & Digital Sunset",
                subtitle = "Lower cortisol and enhance slow-wave recovery",
                behavioralPrinciple = "Melatonin Protection & Temperature Drop",
                durationDays = 21,
                currentDay = 1,
                currentMilestone = "Day 1: 9 PM Screen Blackout & Herbal Tea",
                isEnrolled = false,
                completionPercentage = 0
            )
        )
        journeyDao.insertJourneys(journeys)

        val stacks = listOf(
            HabitStackEntity(
                id = 1,
                triggerHabit = "After I pour my morning coffee ☕",
                newActionHabit = "I will write my top 3 non-negotiable priorities 🎯",
                rewardHabit = "Then I will enjoy the first warm sip with full presence ✨",
                targetDimension = DimensionType.CAREER,
                streakDays = 6,
                isCompletedToday = true
            ),
            HabitStackEntity(
                id = 2,
                triggerHabit = "After I close my laptop at 6 PM 💻",
                newActionHabit = "I will do 20 bodyweight squats & drink 500ml water 💧",
                rewardHabit = "Then I will step outside for 5 mins of fresh air 🌿",
                targetDimension = DimensionType.HEALTH,
                streakDays = 4,
                isCompletedToday = false
            )
        )
        stacks.forEach { journeyDao.insertHabitStack(it) }
    }
}
