package com.lifescore.app.presentation.ui.growth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.core.util.TextToSpeechNarrator
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
    private val userId: String = "default_user",
    context: Context? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyGrowthUiState())
    val uiState: StateFlow<DailyGrowthUiState> = _uiState.asStateFlow()

    private var audioJob: Job? = null
    private val narrator: TextToSpeechNarrator? = context?.let { TextToSpeechNarrator(it) }

    init {
        selectDay(1)
        loadOverallProgress()
    }

    fun selectDay(dayNumber: Int) {
        stopAudio()
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
            pauseAudio()
        } else {
            val session = _uiState.value.session
            _uiState.update { it.copy(isAudioPlaying = true) }
            val script = buildString {
                append("Daily Growth Lesson. Day ").append(session.dayNumber).append(": ").append(session.title).append(". ")
                append(session.subtitle).append(". ")
                append("Core Concept: ").append(session.coreConcept).append(". ")
                append("Micro Lesson: ").append(session.lessonBody).append(". ")
                append("Key Takeaways: ").append(session.keyTakeaways.joinToString(". ")).append(". ")
                append("Daily Action Protocol: ").append(session.dailyActionChallenge).append(".")
            }
            narrator?.speak(script) {
                _uiState.update { it.copy(isAudioPlaying = false, audioSeconds = it.audioTotalSeconds) }
            }
            startAudioTimer()
        }
    }

    fun pauseAudio() {
        audioJob?.cancel()
        narrator?.stop()
        _uiState.update { it.copy(isAudioPlaying = false) }
    }

    fun stopAudio() {
        audioJob?.cancel()
        narrator?.stop()
        _uiState.update { it.copy(isAudioPlaying = false, audioSeconds = 0) }
    }

    private fun startAudioTimer() {
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
        stopAudio()
        narrator?.shutdown()
    }
}
