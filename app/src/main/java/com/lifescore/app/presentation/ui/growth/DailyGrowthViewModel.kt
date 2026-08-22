package com.lifescore.app.presentation.ui.growth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.local.entity.DailyGrowthProgressEntity
import com.lifescore.app.data.repository.DailyGrowthRepository
import com.lifescore.app.domain.model.selfimprovement.DailyGrowthCurriculum
import com.lifescore.app.domain.model.selfimprovement.DailyGrowthSession
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class DailyGrowthUiState(
    val selectedDay: Int = 1,
    val session: DailyGrowthSession = DailyGrowthCurriculum.sessions.first(),
    val progress: DailyGrowthProgressEntity? = null,
    val isAudioPlaying: Boolean = false,
    val audioSeconds: Int = 0,
    val audioTotalSeconds: Int = 15 * 60,
    val journalText: String = "",
    val completedDaysCount: Int = 0,
    val totalCurriculumDays: Int = DailyGrowthCurriculum.sessions.size,
    val snackbarMessage: String? = null
)

class DailyGrowthViewModel(
    private val repository: DailyGrowthRepository,
    private val userId: String = "default_user"
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyGrowthUiState())
    val uiState: StateFlow<DailyGrowthUiState> = _uiState.asStateFlow()

    private var audioJob: Job? = null

    init {
        selectDay(1)
        loadOverallProgress()
    }

    fun selectDay(dayNumber: Int) {
        val session = DailyGrowthCurriculum.getSessionForDay(dayNumber)
        _uiState.update {
            it.copy(
                selectedDay = dayNumber,
                session = session,
                isAudioPlaying = false,
                audioSeconds = 0,
                audioTotalSeconds = session.durationMinutes * 60
            )
        }
        audioJob?.cancel()

        viewModelScope.launch {
            repository.getSessionForDay(dayNumber, userId).collectLatest { pair ->
                _uiState.update {
                    it.copy(
                        session = pair.first,
                        progress = pair.second,
                        journalText = pair.second?.journalReflection ?: it.journalText
                    )
                }
            }
        }
    }

    private fun loadOverallProgress() {
        viewModelScope.launch {
            repository.getAllProgress(userId).collectLatest { list ->
                val completed = list.count { it.isCompleted }
                _uiState.update { it.copy(completedDaysCount = completed) }
            }
        }
    }

    fun updateJournalText(text: String) {
        _uiState.update { it.copy(journalText = text) }
    }

    fun toggleAudio() {
        if (_uiState.value.isAudioPlaying) {
            audioJob?.cancel()
            _uiState.update { it.copy(isAudioPlaying = false) }
        } else {
            _uiState.update { it.copy(isAudioPlaying = true) }
            audioJob?.cancel()
            audioJob = viewModelScope.launch {
                while (_uiState.value.isAudioPlaying) {
                    delay(1000L)
                    val next = _uiState.value.audioSeconds + 1
                    _uiState.update { it.copy(audioSeconds = next) }
                    if (next >= _uiState.value.audioTotalSeconds) {
                        _uiState.update { it.copy(isAudioPlaying = false) }
                        break
                    }
                }
            }
        }
    }

    fun completeSession() {
        viewModelScope.launch {
            val todayIso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val xp = repository.completeGrowthSession(
                sessionId = _uiState.value.selectedDay,
                journalReflection = _uiState.value.journalText,
                dateIso = todayIso,
                userId = userId
            )
            _uiState.update { it.copy(snackbarMessage = "🌟 15-Min Daily Growth Completed! (+${xp} XP)") }
        }
    }

    fun completeActionChallenge() {
        viewModelScope.launch {
            val xp = repository.completeActionChallenge(_uiState.value.selectedDay, userId)
            if (xp > 0) {
                _uiState.update { it.copy(snackbarMessage = "⚔️ Daily Action Challenge Completed! (+50 XP)") }
            }
        }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        audioJob?.cancel()
    }
}
