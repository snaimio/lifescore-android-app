package com.lifescore.app.presentation.ui.meditation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.core.util.TextToSpeechNarrator
import com.lifescore.app.data.local.entity.LiveEventEntity
import com.lifescore.app.data.local.entity.MeditationTrackEntity
import com.lifescore.app.data.repository.MeditationLibraryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MeditationUiState(
    val tracks: List<MeditationTrackEntity> = emptyList(),
    val liveEvents: List<LiveEventEntity> = emptyList(),
    val selectedCategory: String = "ALL",
    val customTimerMinutes: Int = 10,
    val selectedBell: String = "Tibetan Singing Bowl",
    val isTimerActive: Boolean = false,
    val isPlayingAudio: Boolean = false,
    val activeTrackId: String? = null,
    val timerRemainingSeconds: Int = 600,
    val toastMessage: String? = null
)

class MeditationViewModel(
    private val repository: MeditationLibraryRepository,
    context: Context? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(MeditationUiState())
    val uiState: StateFlow<MeditationUiState> = _uiState.asStateFlow()
    private val narrator: TextToSpeechNarrator? = context?.let { TextToSpeechNarrator(it) }

    init {
        viewModelScope.launch {
            repository.seedDefaultTracksIfEmpty()
        }

        viewModelScope.launch {
            combine(
                repository.getAllMeditations(),
                repository.getLiveEvents()
            ) { tracks, events ->
                Pair(tracks, events)
            }.collect { (tracks, events) ->
                _uiState.update { it.copy(tracks = tracks, liveEvents = events) }
            }
        }
    }

    fun setCategory(cat: String) {
        _uiState.update { it.copy(selectedCategory = cat) }
    }

    fun setCustomTimer(mins: Int) {
        _uiState.update { it.copy(customTimerMinutes = mins, timerRemainingSeconds = mins * 60) }
    }

    fun playTrackAudio(track: MeditationTrackEntity) {
        if (_uiState.value.isPlayingAudio && _uiState.value.activeTrackId == track.trackId) {
            stopAudio()
        } else {
            stopAudio()
            _uiState.update { it.copy(activeTrackId = track.trackId, isPlayingAudio = true) }
            val script = buildString {
                append("Welcome to your ").append(track.title).append(" meditation session with ").append(track.teacherName).append(". ")
                append("Ambient setting: ").append(track.ambientSound).append(". ")
                append("Duration: ").append(track.durationMinutes).append(" minutes. ")
                append("Close your eyes, inhale deeply through your nose, hold gently... and exhale all tension. ")
                append("Allow your awareness to settle calmly into the present moment.")
            }
            narrator?.speak(script) {
                _uiState.update { it.copy(isPlayingAudio = false, activeTrackId = null) }
                completeSession(track.durationMinutes)
            }
        }
    }

    fun startTimerWithBell(mins: Int) {
        if (_uiState.value.isTimerActive || _uiState.value.isPlayingAudio) {
            stopAudio()
        } else {
            _uiState.update { it.copy(isTimerActive = true, isPlayingAudio = true, activeTrackId = "timer") }
            val script = "Beginning your $mins-minute meditation timer with ${_uiState.value.selectedBell}. Find a comfortable posture, relax your shoulders, and bring your attention to the rhythm of your natural breath."
            narrator?.speak(script) {
                _uiState.update { it.copy(isPlayingAudio = false, activeTrackId = null) }
            }
            completeSession(mins)
        }
    }

    fun stopAudio() {
        narrator?.stop()
        _uiState.update { it.copy(isPlayingAudio = false, isTimerActive = false, activeTrackId = null) }
    }

    fun completeSession(mins: Int) {
        viewModelScope.launch {
            val xp = repository.recordMeditationCompleted(mins)
            _uiState.update { it.copy(toastMessage = "$mins-minute meditation completed! +$xp XP Mind/Spirit") }
        }
    }

    fun toggleBookmark(trackId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleBookmark(trackId, !currentStatus)
        }
    }

    fun toggleRegisterEvent(eventId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleRegistration(eventId, !currentStatus)
            _uiState.update { it.copy(toastMessage = if (!currentStatus) "Registered for Live Event!" else "Registration canceled") }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        stopAudio()
        narrator?.shutdown()
    }
}
