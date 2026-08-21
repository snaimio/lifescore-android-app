package com.lifescore.app.presentation.ui.atomichabits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.repository.AtomicHabitsOverview
import com.lifescore.app.data.repository.AtomicHabitsRepository
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.domain.model.atomichabits.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AtomicHabitsUiState(
    val isLoading: Boolean = false,
    val overview: AtomicHabitsOverview? = null,
    val identities: List<HabitIdentity> = emptyList(),
    val scorecardItems: List<HabitScorecardItem> = emptyList(),
    val challenge: AtomicHabitsChallenge? = null,
    val journalEntries: List<SystemDesignEntry> = emptyList(),
    val successToast: String? = null,
    val error: String? = null
)

class AtomicHabitsViewModel(
    private val repository: AtomicHabitsRepository,
    private val lifeScoreRepository: LifeScoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AtomicHabitsUiState())
    val uiState: StateFlow<AtomicHabitsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            launch {
                repository.getOverview().collect { ov ->
                    _uiState.update { it.copy(overview = ov, isLoading = false) }
                }
            }

            launch {
                repository.getIdentities().collect { ids ->
                    _uiState.update { it.copy(identities = ids) }
                }
            }

            launch {
                repository.getScorecardItems().collect { items ->
                    _uiState.update { it.copy(scorecardItems = items) }
                }
            }

            launch {
                repository.getChallenge().collect { ch ->
                    _uiState.update { it.copy(challenge = ch) }
                }
            }

            launch {
                repository.getSystemJournalEntries().collect { entries ->
                    _uiState.update { it.copy(journalEntries = entries) }
                }
            }
        }
    }

    fun saveIdentity(statement: String, targetVotes: Int = 30) {
        viewModelScope.launch {
            try {
                repository.saveIdentity(statement, targetVotes)
                awardXp(25)
                _uiState.update { it.copy(successToast = "✨ Identity created: \"$statement\" (+25 XP)") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun voteForIdentity(identityId: String) {
        viewModelScope.launch {
            try {
                val xp = repository.voteForIdentity(identityId)
                awardXp(xp)
                _uiState.update { it.copy(successToast = "🗳️ Cast 1 Vote for your identity! (+$xp XP)") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun deleteIdentity(identityId: String) {
        viewModelScope.launch {
            try {
                repository.deleteIdentity(identityId)
                _uiState.update { it.copy(successToast = "Identity removed") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun addScorecardItem(name: String, category: HabitCategory, notes: String = "") {
        viewModelScope.launch {
            try {
                repository.addScorecardItem(name, category, notes)
                awardXp(15)
                _uiState.update { it.copy(successToast = "📋 Added to Habit Scorecard (+15 XP)") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun updateScorecardCategory(id: String, category: HabitCategory) {
        viewModelScope.launch {
            try {
                repository.updateScorecardCategory(id, category)
                _uiState.update { it.copy(successToast = "Scorecard updated to ${category.displayName}") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun deleteScorecardItem(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteScorecardItem(id)
                _uiState.update { it.copy(successToast = "Habit removed from scorecard") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun logChallengeToday() {
        viewModelScope.launch {
            try {
                val xp = repository.logChallengeDay()
                awardXp(xp)
                val day = _uiState.value.challenge?.currentDay ?: 1
                _uiState.update { it.copy(successToast = "🔥 Day $day of 30-Day Challenge Completed! (+$xp XP)") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun setChallengeHabit(habitName: String) {
        viewModelScope.launch {
            try {
                repository.setChallengeHabit(habitName)
                _uiState.update { it.copy(successToast = "30-Day Challenge set to \"$habitName\"") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun saveSystemJournalEntry(
        title: String,
        habitTarget: String,
        environmentChanges: String,
        habitStack: String,
        twoMinuteStep: String,
        rewardPlan: String
    ) {
        viewModelScope.launch {
            try {
                val entry = SystemDesignEntry(
                    title = title,
                    habitTarget = habitTarget,
                    environmentChanges = environmentChanges,
                    habitStack = habitStack,
                    twoMinuteStep = twoMinuteStep,
                    rewardPlan = rewardPlan
                )
                repository.saveSystemJournalEntry(entry)
                awardXp(35)
                _uiState.update { it.copy(successToast = "🛠️ System Design Journal entry saved! (+35 XP)") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    private suspend fun awardXp(xp: Int) {
        try {
            val profile = lifeScoreRepository.getUserProfile().first()
            val updatedXp = profile.currentXp + xp
            val newLevel = (updatedXp / 1000) + 1
            lifeScoreRepository.updateUserProfile(profile.copy(currentXp = updatedXp, currentLevel = newLevel))
        } catch (_: Exception) {}
    }

    fun clearMessages() {
        _uiState.update { it.copy(successToast = null, error = null) }
    }
}
