package com.lifescore.app.presentation.ui.trackers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.core.trackers.TrackerStatus
import com.lifescore.app.core.trackers.TrackerType
import com.lifescore.app.data.repository.DailyTrackerData
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.data.repository.LifeTrackersRepository
import com.lifescore.app.data.repository.TrackerLogEntry
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DedicatedTrackerUiState(
    val trackerType: TrackerType,
    val status: TrackerStatus? = null,
    val weeklyData: List<DailyTrackerData> = emptyList(),
    val historyLogs: List<TrackerLogEntry> = emptyList(),
    val isLoading: Boolean = false,
    val successToast: String? = null,
    val error: String? = null
)

class DedicatedTrackerViewModel(
    val trackerType: TrackerType,
    private val lifeTrackersRepository: LifeTrackersRepository,
    private val lifeScoreRepository: LifeScoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DedicatedTrackerUiState(trackerType = trackerType))
    val uiState: StateFlow<DedicatedTrackerUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            launch {
                lifeTrackersRepository.getTrackerStatus(trackerType).collect { st ->
                    _uiState.update { it.copy(status = st, isLoading = false) }
                }
            }

            launch {
                lifeTrackersRepository.getWeeklySummary(trackerType).collect { summary ->
                    _uiState.update { it.copy(weeklyData = summary.dailyValues) }
                }
            }

            launch {
                lifeTrackersRepository.getTrackerHistory(trackerType).collect { logs ->
                    _uiState.update { it.copy(historyLogs = logs) }
                }
            }
        }
    }

    fun logValue(value: Float, note: String = "Manual entry") {
        viewModelScope.launch {
            try {
                val xp = lifeTrackersRepository.logTrackerValue(trackerType, value, note)
                val profile = lifeScoreRepository.getUserProfile().first()
                val updatedXp = profile.currentXp + xp
                val newLevel = (updatedXp / 1000) + 1
                lifeScoreRepository.updateUserProfile(profile.copy(currentXp = updatedXp, currentLevel = newLevel))

                val formatted = if (value % 1.0f == 0f) "${value.toInt()}" else "$value"
                _uiState.update {
                    it.copy(successToast = "Added +$formatted ${trackerType.unit}! (+$xp XP) 🚀")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun updateGoal(newGoal: Float) {
        viewModelScope.launch {
            try {
                lifeTrackersRepository.setTrackerGoal(trackerType, newGoal)
                _uiState.update {
                    it.copy(successToast = "Target updated to ${newGoal.toInt()} ${trackerType.unit}! 🎯")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun deleteLog(entryId: String) {
        viewModelScope.launch {
            try {
                lifeTrackersRepository.deleteTrackerLog(entryId, trackerType)
                _uiState.update { it.copy(successToast = "Entry removed") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successToast = null, error = null) }
    }
}
