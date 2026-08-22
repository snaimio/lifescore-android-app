package com.lifescore.app.presentation.ui.energy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.local.entity.EnergyPredictionEntity
import com.lifescore.app.data.local.entity.SmartScheduledTaskEntity
import com.lifescore.app.data.repository.EnergyScheduleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class EnergyScheduleUiState(
    val prediction: EnergyPredictionEntity? = null,
    val scheduledTasks: List<SmartScheduledTaskEntity> = emptyList(),
    val isAutoOptimizing: Boolean = false,
    val toastMessage: String? = null
)

class EnergyScheduleViewModel(
    private val repository: EnergyScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EnergyScheduleUiState())
    val uiState: StateFlow<EnergyScheduleUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getLatestPrediction(),
                repository.getScheduledTasks()
            ) { pred, tasks ->
                Pair(pred, tasks)
            }.collect { (pred, tasks) ->
                _uiState.update { it.copy(prediction = pred, scheduledTasks = tasks) }
            }
        }
    }

    fun toggleTask(taskId: Long, completed: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskCompleted(taskId, completed)
            if (completed) {
                _uiState.update { it.copy(toastMessage = "Peak energy task finished! +35 XP") }
            }
        }
    }

    fun recalculateCircadianCurve() {
        viewModelScope.launch {
            _uiState.update { it.copy(isAutoOptimizing = true) }
            val newPred = repository.refreshCircadianPrediction()
            _uiState.update {
                it.copy(
                    prediction = newPred,
                    isAutoOptimizing = false,
                    toastMessage = "Circadian curve refreshed! Focus capacity: ${newPred.predictedFocusScore}%"
                )
            }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
