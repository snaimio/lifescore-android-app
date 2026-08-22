package com.lifescore.app.data.local.dao

import androidx.room.*
import com.lifescore.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

// 1. Flashcard & Learning Plan DAO
@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcard_entities ORDER BY nextReviewTimestamp ASC")
    fun getAllFlashcards(): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcard_entities WHERE bookId = :bookId")
    fun getFlashcardsForBook(bookId: String): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcard_entities WHERE nextReviewTimestamp <= :currentTimestamp ORDER BY boxLevel ASC")
    fun getDueFlashcards(currentTimestamp: Long): Flow<List<FlashcardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: FlashcardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcards(flashcards: List<FlashcardEntity>)

    @Update
    suspend fun updateFlashcard(flashcard: FlashcardEntity)

    @Query("SELECT * FROM learning_plans WHERE userId = :userId")
    fun getLearningPlans(userId: String = "default_user"): Flow<List<LearningPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLearningPlan(plan: LearningPlanEntity)

    @Update
    suspend fun updateLearningPlan(plan: LearningPlanEntity)
}

// 2. Energy & Smart Schedule DAO
@Dao
interface EnergyDao {
    @Query("SELECT * FROM energy_predictions WHERE userId = :userId ORDER BY id DESC LIMIT 1")
    fun getLatestEnergyPrediction(userId: String = "default_user"): Flow<EnergyPredictionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnergyPrediction(prediction: EnergyPredictionEntity): Long

    @Query("SELECT * FROM smart_scheduled_tasks WHERE userId = :userId ORDER BY id ASC")
    fun getScheduledTasks(userId: String = "default_user"): Flow<List<SmartScheduledTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduledTask(task: SmartScheduledTaskEntity): Long

    @Update
    suspend fun updateScheduledTask(task: SmartScheduledTaskEntity)

    @Query("UPDATE smart_scheduled_tasks SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun toggleTaskComplete(taskId: Long, isCompleted: Boolean)
}

// 3. Virtual Pet DAO
@Dao
interface VirtualPetDao {
    @Query("SELECT * FROM virtual_pets WHERE userId = :userId")
    fun getPet(userId: String = "default_user"): Flow<VirtualPetEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePet(pet: VirtualPetEntity)

    @Query("UPDATE virtual_pets SET happinessLevel = :happiness, energyLevel = :energy, lastFedTimestamp = :timestamp, totalAffectionInteractions = totalAffectionInteractions + 1 WHERE userId = :userId")
    suspend fun feedAndPet(userId: String, happiness: Int, energy: Int, timestamp: Long)

    @Query("UPDATE virtual_pets SET equippedHat = :hat, equippedAccessory = :accessory WHERE userId = :userId")
    suspend fun updateCustomization(userId: String, hat: String, accessory: String)
}

// 4. Meditation DAO
@Dao
interface MeditationDao {
    @Query("SELECT * FROM meditation_tracks ORDER BY rating DESC")
    fun getAllMeditations(): Flow<List<MeditationTrackEntity>>

    @Query("SELECT * FROM meditation_tracks WHERE category = :category")
    fun getMeditationsByCategory(category: String): Flow<List<MeditationTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeditations(meditations: List<MeditationTrackEntity>)

    @Query("UPDATE meditation_tracks SET isBookmarked = :isBookmarked WHERE trackId = :trackId")
    suspend fun toggleBookmark(trackId: String, isBookmarked: Boolean)

    @Query("SELECT * FROM live_meditation_events ORDER BY startTimeIso ASC")
    fun getLiveEvents(): Flow<List<LiveEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiveEvents(events: List<LiveEventEntity>)

    @Query("UPDATE live_meditation_events SET isRegistered = :isRegistered WHERE eventId = :eventId")
    suspend fun toggleRegistration(eventId: String, isRegistered: Boolean)
}

// 5. Party DAO
@Dao
interface PartyDao {
    @Query("SELECT * FROM rpg_parties LIMIT 1")
    fun getCurrentParty(): Flow<PartyEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParty(party: PartyEntity)

    @Query("UPDATE rpg_parties SET questBossCurrentHp = MAX(0, questBossCurrentHp - :damage) WHERE partyId = :partyId")
    suspend fun dealBossDamage(partyId: String, damage: Int)

    @Query("SELECT * FROM party_messages WHERE partyId = :partyId ORDER BY timestamp ASC")
    fun getPartyMessages(partyId: String): Flow<List<PartyMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: PartyMessageEntity): Long
}

// 6. Coach DAO
@Dao
interface CoachDao {
    @Query("SELECT * FROM coach_profiles ORDER BY rating DESC")
    fun getAllCoaches(): Flow<List<CoachEntity>>

    @Query("SELECT * FROM coach_profiles WHERE specialty = :specialty")
    fun getCoachesBySpecialty(specialty: String): Flow<List<CoachEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoaches(coaches: List<CoachEntity>)

    @Query("SELECT * FROM coach_bookings WHERE userId = :userId ORDER BY createdAt DESC")
    fun getBookings(userId: String = "default_user"): Flow<List<CoachBookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: CoachBookingEntity): Long
}

// 7. Science Journey & Habit Stacks DAO
@Dao
interface JourneyDao {
    @Query("SELECT * FROM science_journeys ORDER BY durationDays ASC")
    fun getAllJourneys(): Flow<List<ScienceJourneyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJourneys(journeys: List<ScienceJourneyEntity>)

    @Query("UPDATE science_journeys SET isEnrolled = :isEnrolled WHERE journeyId = :journeyId")
    suspend fun toggleEnrollment(journeyId: String, isEnrolled: Boolean)

    @Query("UPDATE science_journeys SET currentDay = currentDay + 1, completionPercentage = (currentDay + 1) * 100 / durationDays WHERE journeyId = :journeyId")
    suspend fun advanceJourneyDay(journeyId: String)

    @Query("SELECT * FROM habit_stacks ORDER BY streakDays DESC")
    fun getHabitStacks(): Flow<List<HabitStackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitStack(stack: HabitStackEntity): Long

    @Query("UPDATE habit_stacks SET isCompletedToday = :completed, streakDays = streakDays + (CASE WHEN :completed = 1 THEN 1 ELSE 0 END) WHERE id = :id")
    suspend fun toggleStackCompleted(id: Long, completed: Boolean)
}
