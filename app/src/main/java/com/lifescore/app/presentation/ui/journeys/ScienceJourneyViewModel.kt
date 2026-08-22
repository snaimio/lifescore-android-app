package com.lifescore.app.presentation.ui.journeys

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.local.entity.HabitStackEntity
import com.lifescore.app.data.local.entity.ScienceJourneyEntity
import com.lifescore.app.data.repository.ScienceJourneyRepository
import com.lifescore.app.domain.model.DimensionType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ScienceJourneyUiState(
    val journeys: List<ScienceJourneyEntity> = emptyList(),
    val habitStacks: List<HabitStackEntity> = emptyList(),
    val triggerInput: String = "",
    val actionInput: String = "",
    val rewardInput: String = "",
    val isCreatingStack: Boolean = false,
    val toastMessage: String? = null
)

class ScienceJourneyViewModel(
    private val repository: ScienceJourneyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScienceJourneyUiState())
    val uiState: StateFlow<ScienceJourneyUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedDefaultJourneysIfEmpty()
        }

        viewModelScope.launch {
            combine(
                repository.getAllJourneys(),
                repository.getHabitStacks()
            ) { journeys, stacks ->
                Pair(journeys, stacks)
            }.collect { (journeys, stacks) ->
                _uiState.update { it.copy(journeys = journeys, habitStacks = stacks) }
            }
        }
    }

    fun onTriggerChange(text: String) { _uiState.update { it.copy(triggerInput = text) } }
    fun onActionChange(text: String) { _uiState.update { it.copy(actionInput = text) } }
    fun onRewardChange(text: String) { _uiState.update { it.copy(rewardInput = text) } }
    fun toggleCreateStackDialog(open: Boolean) { _uiState.update { it.copy(isCreatingStack = open) } }

    fun saveHabitStack() {
        val trigger = _uiState.value.triggerInput.trim().ifBlank { "After I pour my morning water" }
        val action = _uiState.value.actionInput.trim().ifBlank { "I will stretch for 2 minutes" }
        val reward = _uiState.value.rewardInput.trim().ifBlank { "Then I will take a deep breath of gratitude" }

        viewModelScope.launch {
            repository.createHabitStack(trigger, action, reward, DimensionType.HEALTH)
            _uiState.update {
                it.copy(
                    isCreatingStack = false,
                    triggerInput = "",
                    actionInput = "",
                    rewardInput = "",
                    toastMessage = "Habit Stack Created! +30 XP"
                )
            }
        }
    }

    fun toggleStackComplete(id: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleHabitStackCompleted(id, !currentStatus)
            if (!currentStatus) {
                _uiState.update { it.copy(toastMessage = "Habit stack anchored! +25 XP") }
            }
        }
    }

    fun advanceJourney(journeyId: String) {
        viewModelScope.launch {
            repository.advanceJourneyDay(journeyId)
            _uiState.update { it.copy(toastMessage = "Journey Milestone Completed! +40 XP") }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
