package com.lifescore.app.data.repository

import com.lifescore.app.domain.model.atomichabits.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.*

data class AtomicHabitsOverview(
    val activeIdentitiesCount: Int,
    val totalVotesCast: Int,
    val totalScorecardHabits: Int,
    val goodHabitsCount: Int,
    val badHabitsCount: Int,
    val neutralHabitsCount: Int,
    val challengeCurrentDay: Int,
    val challengeCompleted: Boolean,
    val dailyTip: String
)

interface AtomicHabitsRepository {
    fun getOverview(): Flow<AtomicHabitsOverview>
    fun getIdentities(): Flow<List<HabitIdentity>>
    suspend fun saveIdentity(statement: String, targetVotes: Int = 30)
    suspend fun voteForIdentity(identityId: String, actionTaken: String = "Voted"): Int // Returns XP
    suspend fun deleteIdentity(identityId: String)

    fun getScorecardItems(): Flow<List<HabitScorecardItem>>
    suspend fun addScorecardItem(name: String, category: HabitCategory, notes: String = "")
    suspend fun updateScorecardCategory(id: String, category: HabitCategory)
    suspend fun deleteScorecardItem(id: String)

    fun getChallenge(): Flow<AtomicHabitsChallenge>
    suspend fun logChallengeDay(): Int // Returns XP
    suspend fun setChallengeHabit(habitName: String)

    fun getSystemJournalEntries(): Flow<List<SystemDesignEntry>>
    suspend fun saveSystemJournalEntry(entry: SystemDesignEntry)
}

class AtomicHabitsRepositoryImpl(
    private val lifeScoreRepository: LifeScoreRepository
) : AtomicHabitsRepository {

    private val _identities = MutableStateFlow<List<HabitIdentity>>(
        listOf(
            HabitIdentity(
                identityStatement = "I am a disciplined athlete who honors physical vitality",
                dailyVotes = 14,
                targetVotes = 30
            ),
            HabitIdentity(
                identityStatement = "I am a lifelong learner and deep thinker",
                dailyVotes = 9,
                targetVotes = 30
            ),
            HabitIdentity(
                identityStatement = "I am an intentional builder who executes daily",
                dailyVotes = 21,
                targetVotes = 30
            )
        )
    )

    private val _scorecardItems = MutableStateFlow<List<HabitScorecardItem>>(
        listOf(
            HabitScorecardItem(habitName = "Wake up at 6:30 AM", category = HabitCategory.GOOD),
            HabitScorecardItem(habitName = "Check social media in bed", category = HabitCategory.BAD),
            HabitScorecardItem(habitName = "Drink 500ml water", category = HabitCategory.GOOD),
            HabitScorecardItem(habitName = "Brush teeth & shower", category = HabitCategory.NEUTRAL),
            HabitScorecardItem(habitName = "10 minutes meditation", category = HabitCategory.GOOD),
            HabitScorecardItem(habitName = "Unconscious snacking while working", category = HabitCategory.BAD),
            HabitScorecardItem(habitName = "Read 10 pages before sleep", category = HabitCategory.GOOD)
        )
    )

    private val _challenge = MutableStateFlow(
        AtomicHabitsChallenge(
            habitName = "Daily 2-Minute Evening Reflection",
            currentDay = 12,
            totalDays = 30,
            dailyLogs = (1..12).map { day ->
                DailyHabitLog(
                    day = day,
                    date = "Day $day",
                    isCompleted = true,
                    notes = "Cast a vote for my identity"
                )
            }
        )
    )

    private val _journalEntries = MutableStateFlow<List<SystemDesignEntry>>(
        listOf(
            SystemDesignEntry(
                title = "Deep Work Reading Environment",
                habitTarget = "Read 20 pages every night",
                environmentChanges = "Placed book on my pillow, phone charger across the bedroom.",
                habitStack = "After I get into bed, I will open my book immediately.",
                twoMinuteStep = "Read just one paragraph.",
                rewardPlan = "Log +25 XP and watch the reading streak counter glow."
            )
        )
    )

    override fun getOverview(): Flow<AtomicHabitsOverview> {
        return _identities.map { ids ->
            val scorecards = _scorecardItems.value
            val chal = _challenge.value
            val good = scorecards.count { it.category == HabitCategory.GOOD }
            val bad = scorecards.count { it.category == HabitCategory.BAD }
            val neutral = scorecards.count { it.category == HabitCategory.NEUTRAL }
            val votes = ids.sumOf { it.dailyVotes }

            AtomicHabitsOverview(
                activeIdentitiesCount = ids.size,
                totalVotesCast = votes,
                totalScorecardHabits = scorecards.size,
                goodHabitsCount = good,
                badHabitsCount = bad,
                neutralHabitsCount = neutral,
                challengeCurrentDay = chal.currentDay,
                challengeCompleted = chal.isCompleted,
                dailyTip = getDailyAtomicTip(chal.currentDay)
            )
        }
    }

    override fun getIdentities(): Flow<List<HabitIdentity>> = _identities.asStateFlow()

    override suspend fun saveIdentity(statement: String, targetVotes: Int) {
        val newIdentity = HabitIdentity(
            identityStatement = statement,
            targetVotes = targetVotes
        )
        _identities.update { listOf(newIdentity) + it }
    }

    override suspend fun voteForIdentity(identityId: String, actionTaken: String): Int {
        _identities.update { list ->
            list.map { id ->
                if (id.id == identityId) {
                    id.copy(
                        dailyVotes = id.dailyVotes + 1,
                        updatedAt = System.currentTimeMillis()
                    )
                } else id
            }
        }
        return 20 // +20 XP per identity vote
    }

    override suspend fun deleteIdentity(identityId: String) {
        _identities.update { list -> list.filterNot { it.id == identityId } }
    }

    override fun getScorecardItems(): Flow<List<HabitScorecardItem>> = _scorecardItems.asStateFlow()

    override suspend fun addScorecardItem(name: String, category: HabitCategory, notes: String) {
        val item = HabitScorecardItem(
            habitName = name,
            category = category,
            notes = notes
        )
        _scorecardItems.update { it + item }
    }

    override suspend fun updateScorecardCategory(id: String, category: HabitCategory) {
        _scorecardItems.update { list ->
            list.map { if (it.id == id) it.copy(category = category) else it }
        }
    }

    override suspend fun deleteScorecardItem(id: String) {
        _scorecardItems.update { list -> list.filterNot { it.id == id } }
    }

    override fun getChallenge(): Flow<AtomicHabitsChallenge> = _challenge.asStateFlow()

    override suspend fun logChallengeDay(): Int {
        val curr = _challenge.value
        val nextDay = (curr.currentDay + 1).coerceAtMost(30)
        val isDone = nextDay >= 30

        val newLog = DailyHabitLog(
            day = curr.currentDay,
            date = "Day ${curr.currentDay}",
            isCompleted = true,
            notes = "Completed atomic ritual"
        )

        _challenge.update {
            it.copy(
                currentDay = nextDay,
                isCompleted = isDone,
                dailyLogs = it.dailyLogs + newLog
            )
        }
        return if (isDone) 100 else 30
    }

    override suspend fun setChallengeHabit(habitName: String) {
        _challenge.update {
            it.copy(
                habitName = habitName,
                currentDay = 1,
                dailyLogs = emptyList(),
                isCompleted = false
            )
        }
    }

    override fun getSystemJournalEntries(): Flow<List<SystemDesignEntry>> = _journalEntries.asStateFlow()

    override suspend fun saveSystemJournalEntry(entry: SystemDesignEntry) {
        _journalEntries.update { listOf(entry) + it }
    }

    private fun getDailyAtomicTip(day: Int): String {
        return when (day % 7) {
            0 -> "Every action you take is a vote for the type of person you wish to become. No single instance will transform your beliefs, but as the votes build up, so does the evidence of your new identity."
            1 -> "You do not rise to the level of your goals. You fall to the level of your systems. Your goal is your desired outcome; your system is the collection of daily habits that will get you there."
            2 -> "Habits are the compound interest of self-improvement. Getting 1 percent better every day counts for a lot in the long-run: 1% better for 365 days makes you 37x better."
            3 -> "The 1st Law (Make it Obvious): Use Habit Stacking. Pair a new habit with a current habit by saying: 'After [CURRENT HABIT], I will [NEW HABIT].'"
            4 -> "The 2nd Law (Make it Attractive): Use Temptation Bundling. Link an action you WANT to do with an action you NEED to do."
            5 -> "The 3rd Law (Make it Easy): The 2-Minute Rule. When you start a new habit, it should take less than two minutes to do. Optimize for starting, not finishing."
            else -> "The 4th Law (Make it Satisfying): What is immediately rewarded is repeated. What is immediately punished is avoided. Use visual habit tracking to feel instant gratification."
        }
    }
}
