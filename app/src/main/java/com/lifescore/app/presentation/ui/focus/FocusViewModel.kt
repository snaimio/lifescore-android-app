package com.lifescore.app.presentation.ui.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.local.entity.TreeType
import com.lifescore.app.data.repository.FocusRepository
import com.lifescore.app.data.repository.FocusStats
import com.lifescore.app.domain.model.DimensionType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class FocusUiState(
    val selectedMinutes: Int = 25,
    val remainingSeconds: Int = 25 * 60,
    val isRunning: Boolean = false,
    val selectedDimension: DimensionType = DimensionType.CAREER,
    val selectedTree: TreeType = TreeType.OAK,
    val stats: FocusStats = FocusStats(),
    val isWhiteNoisePlaying: Boolean = false,
    val showGiveUpDialog: Boolean = false,
    val snackbarMessage: String? = null
)

class FocusViewModel(
    private val repository: FocusRepository,
    private val userId: String = "default_user"
) : ViewModel() {

    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            repository.getFocusStats(userId).collectLatest { stats ->
                _uiState.update { it.copy(stats = stats) }
            }
        }
    }

    fun selectDuration(minutes: Int) {
        if (_uiState.value.isRunning) return
        _uiState.update {
            it.copy(
                selectedMinutes = minutes,
                remainingSeconds = minutes * 60
            )
        }
    }

    fun selectDimension(dim: DimensionType) {
        _uiState.update { it.copy(selectedDimension = dim) }
    }

    fun selectTree(tree: TreeType) {
        _uiState.update { it.copy(selectedTree = tree) }
    }

    fun toggleTimer() {
        if (_uiState.value.isRunning) {
            _uiState.update { it.copy(showGiveUpDialog = true) }
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _uiState.update { it.copy(isRunning = true) }
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.isRunning && _uiState.value.remainingSeconds > 0) {
                delay(1000L)
                val next = _uiState.value.remainingSeconds - 1
                _uiState.update { it.copy(remainingSeconds = next) }

                if (next <= 0) {
                    completeFocusSession()
                    break
                }
            }
        }
    }

    private fun completeFocusSession() {
        timerJob?.cancel()
        viewModelScope.launch {
            val state = _uiState.value
            val xp = repository.recordCompletedSession(
                durationMinutes = state.selectedMinutes,
                dimensionTag = state.selectedDimension,
                treeType = state.selectedTree,
                userId = userId
            )
            _uiState.update {
                it.copy(
                    isRunning = false,
                    remainingSeconds = it.selectedMinutes * 60,
                    snackbarMessage = "🌳 Tree Planted in Mindful Forest! (+${xp} XP)"
                )
            }
        }
    }

    fun confirmGiveUp() {
        timerJob?.cancel()
        viewModelScope.launch {
            val state = _uiState.value
            val elapsed = (state.selectedMinutes * 60 - state.remainingSeconds) / 60
            if (elapsed >= 1) {
                repository.recordFailedSession(
                    durationMinutes = elapsed,
                    dimensionTag = state.selectedDimension,
                    treeType = state.selectedTree,
                    userId = userId
                )
            }
            _uiState.update {
                it.copy(
                    isRunning = false,
                    remainingSeconds = it.selectedMinutes * 60,
                    showGiveUpDialog = false,
                    snackbarMessage = "🥀 Session cancelled. Your tree withered."
                )
            }
        }
    }

    fun dismissGiveUp() {
        _uiState.update { it.copy(showGiveUpDialog = false) }
    }

    fun toggleWhiteNoise() {
        _uiState.update { it.copy(isWhiteNoisePlaying = !it.isWhiteNoisePlaying) }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
