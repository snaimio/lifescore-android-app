package com.lifescore.app.presentation.ui.trackers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.core.trackers.TrackerStatus
import com.lifescore.app.core.trackers.TrackerType
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.data.repository.LifeTrackersRepository
import com.lifescore.app.domain.model.DimensionType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TrackerHubUiState(
    val isLoading: Boolean = false,
    val trackers: List<TrackerStatus> = emptyList(),
    val selectedDimension: DimensionType? = null,
    val totalTrackersCount: Int = 15,
    val completedTrackersCount: Int = 0,
    val totalXpAvailableToday: Int = 400,
    val successToast: String? = null,
    val error: String? = null
)

class TrackerHubViewModel(
    private val lifeTrackersRepository: LifeTrackersRepository,
    private val lifeScoreRepository: LifeScoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackerHubUiState())
    val uiState: StateFlow<TrackerHubUiState> = _uiState.asStateFlow()

    init {
        loadTrackers()
    }

    private fun loadTrackers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            lifeTrackersRepository.getAllTrackerStatuses().collect { list ->
                val completed = list.count { it.todayCompleted }
                _uiState.update {
                    it.copy(
                        trackers = list,
                        completedTrackersCount = completed,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun filterDimension(dimension: DimensionType?) {
        _uiState.update { it.copy(selectedDimension = dimension) }
    }

    fun quickLog(type: TrackerType, value: Float) {
        viewModelScope.launch {
            try {
                val xp = lifeTrackersRepository.logTrackerValue(type, value, "Quick log")

                // Update user profile XP
                val currentProfile = lifeScoreRepository.getUserProfile().first()
                val updatedXp = currentProfile.currentXp + xp
                val newLevel = (updatedXp / 1000) + 1
                lifeScoreRepository.updateUserProfile(currentProfile.copy(currentXp = updatedXp, currentLevel = newLevel))

                val formattedVal = if (value % 1.0f == 0f) "${value.toInt()}" else "$value"
                _uiState.update {
                    it.copy(successToast = "Logged +$formattedVal ${type.unit} to ${type.title}! (+$xp XP) 🚀")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun setGoal(type: TrackerType, newGoal: Float) {
        viewModelScope.launch {
            try {
                lifeTrackersRepository.setTrackerGoal(type, newGoal)
                _uiState.update {
                    it.copy(successToast = "Updated ${type.title} target to $newGoal ${type.unit}! 🎯")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successToast = null, error = null) }
    }
}
