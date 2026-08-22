package com.lifescore.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lifescore.app.domain.model.DimensionType

// ==========================================
// 1. HEADWAY / BITELY: FLASHCARDS & PLANS
// ==========================================
@Entity(tableName = "flashcard_entities")
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: String,
    val bookTitle: String,
    val question: String,
    val answer: String,
    val keyConcept: String,
    val boxLevel: Int = 1, // 1 to 5 (Leitner Box spaced repetition)
    val nextReviewTimestamp: Long = System.currentTimeMillis(),
    val repetitions: Int = 0,
    val lastReviewedAt: Long? = null
)

@Entity(tableName = "learning_plans")
data class LearningPlanEntity(
    @PrimaryKey
    val planId: String,
    val userId: String = "default_user",
    val title: String,
    val targetDimension: DimensionType,
    val totalDays: Int = 30,
    val currentDay: Int = 1,
    val bookIdsCsv: String, // comma separated book IDs in order
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

// ==========================================
// 2. LIFESTACK: ENERGY PREDICTION & SMART SCHEDULE
// ==========================================
@Entity(tableName = "energy_predictions")
data class EnergyPredictionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "default_user",
    val dateIso: String,
    val morningPeakHour: Int = 9,
    val afternoonDipHour: Int = 14,
    val eveningPeakHour: Int = 18,
    val predictedFocusScore: Int = 85,
    val sleepQualityScore: Int = 88,
    val restingHeartRate: Int = 62,
    val peakEnergyStartHour: Int = 9,
    val peakEnergyEndHour: Int = 12
)

@Entity(tableName = "smart_scheduled_tasks")
data class SmartScheduledTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "default_user",
    val taskTitle: String,
    val estimatedMinutes: Int = 45,
    val cognitiveDemand: String = "HIGH", // HIGH, MEDIUM, LOW
    val scheduledTimeIso: String, // e.g. "09:30 AM"
    val energyMatchPercentage: Int = 94,
    val isCompleted: Boolean = false
)

// ==========================================
// 3. FINCH: GENTLE VIRTUAL PET COMPANION
// ==========================================
@Entity(tableName = "virtual_pets")
data class VirtualPetEntity(
    @PrimaryKey
    val userId: String = "default_user",
    val petName: String = "Pip",
    val petType: String = "Phoenix", // Phoenix, Owl, Otter, Dragon
    val happinessLevel: Int = 80, // 0 - 100
    val energyLevel: Int = 90, // 0 - 100
    val evolutionStage: Int = 1, // 1 (Baby), 2 (Adolescent), 3 (Mythic Guardian)
    val equippedHat: String = "Wizard Hat",
    val equippedAccessory: String = "Golden Scarf",
    val petMoodEmoji: String = "🐥",
    val lastFedTimestamp: Long = System.currentTimeMillis(),
    val totalAffectionInteractions: Int = 0,
    val gentleStreakDays: Int = 5 // No penalties on missed days
)

// ==========================================
// 4. INSIGHT TIMER: MEDITATIONS & TIMERS
// ==========================================
@Entity(tableName = "meditation_tracks")
data class MeditationTrackEntity(
    @PrimaryKey
    val trackId: String,
    val title: String,
    val teacherName: String,
    val category: String, // Breathwork, Mindfulness, Sleep, Deep Relaxation, Anxiety Relief
    val durationMinutes: Int,
    val playsCount: Int = 12400,
    val rating: Double = 4.9,
    val audioUrl: String = "",
    val ambientSound: String = "Tibetan Singing Bowls",
    val isBookmarked: Boolean = false
)

@Entity(tableName = "live_meditation_events")
data class LiveEventEntity(
    @PrimaryKey
    val eventId: String,
    val title: String,
    val hostName: String,
    val startTimeIso: String,
    val registeredCount: Int = 340,
    val isRegistered: Boolean = false
)

// ==========================================
// 5. HABITICA: RPG PARTY SYSTEM & GUILDS
// ==========================================
@Entity(tableName = "rpg_parties")
data class PartyEntity(
    @PrimaryKey
    val partyId: String,
    val partyName: String,
    val leaderName: String,
    val memberCount: Int = 4,
    val activeQuestName: String = "The Dread Procrastinator Dragon",
    val questBossMaxHp: Int = 500,
    val questBossCurrentHp: Int = 230,
    val teamXpMultiplier: Double = 1.25,
    val isCurrentUserLeader: Boolean = false
)

@Entity(tableName = "party_messages")
data class PartyMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val partyId: String,
    val senderName: String,
    val senderAvatar: String,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val questDamageDealt: Int = 0
)

// ==========================================
// 6. COACH.ME: HUMAN COACH MARKETPLACE
// ==========================================
@Entity(tableName = "coach_profiles")
data class CoachEntity(
    @PrimaryKey
    val coachId: String,
    val name: String,
    val title: String,
    val specialty: String, // Habit Mastery, Executive Productivity, Fitness & Weight Loss, Mindfulness
    val avatarEmoji: String,
    val rating: Double = 4.95,
    val reviewsCount: Int = 128,
    val hourlyRateUsd: Int = 45,
    val bio: String,
    val isVerified: Boolean = true
)

@Entity(tableName = "coach_bookings")
data class CoachBookingEntity(
    @PrimaryKey(autoGenerate = true)
    val bookingId: Long = 0,
    val userId: String = "default_user",
    val coachId: String,
    val coachName: String,
    val scheduledDateIso: String,
    val sessionGoal: String,
    val isConfirmed: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

// ==========================================
// 7. FABULOUS: SCIENCE JOURNEYS & HABIT STACKING
// ==========================================
@Entity(tableName = "science_journeys")
data class ScienceJourneyEntity(
    @PrimaryKey
    val journeyId: String,
    val title: String,
    val subtitle: String,
    val behavioralPrinciple: String,
    val durationDays: Int = 21,
    val currentDay: Int = 1,
    val currentMilestone: String,
    val isEnrolled: Boolean = false,
    val completionPercentage: Int = 0
)

@Entity(tableName = "habit_stacks")
data class HabitStackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val triggerHabit: String, // e.g., "After I pour my morning coffee"
    val newActionHabit: String, // e.g., "I will write my top 3 daily priorities"
    val rewardHabit: String, // e.g., "Then I will enjoy the first warm sip with pride"
    val targetDimension: DimensionType,
    val streakDays: Int = 0,
    val isCompletedToday: Boolean = false
)
