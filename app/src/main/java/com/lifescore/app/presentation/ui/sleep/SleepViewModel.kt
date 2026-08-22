package com.lifescore.app.presentation.ui.sleep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.domain.model.selfimprovement.AmbientSoundTrack
import com.lifescore.app.domain.model.selfimprovement.SleepStory
import com.lifescore.app.domain.model.selfimprovement.SoundscapeCatalog
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SleepUiState(
    val ambientTracks: List<AmbientSoundTrack> = SoundscapeCatalog.ambientTracks,
    val activeTrackVolumes: Map<String, Float> = emptyMap(),
    val sleepStories: List<SleepStory> = SoundscapeCatalog.sleepStories,
    val selectedStory: SleepStory? = null,
    val isStoryPlaying: Boolean = false,
    val storyProgressParagraph: Int = 0,
    val sleepTimerMinutes: Int = 30,
    val isTimerActive: Boolean = false,
    val timerRemainingSeconds: Int = 30 * 60,
    val windDownItems: Map<String, Boolean> = mapOf(
        "No screens 1 hour before bed" to false,
        "Bedroom temperature cool (68°F/20°C)" to false,
        "Read 10 pages or listen to sleep story" to false,
        "Gratitude reflection logged" to false
    ),
    val snackbarMessage: String? = null
)

class SleepViewModel(
    private val lifeScoreRepository: LifeScoreRepository,
    private val userId: String = "default_user"
) : ViewModel() {

    private val _uiState = MutableStateFlow(SleepUiState())
    val uiState: StateFlow<SleepUiState> = _uiState.asStateFlow()

    private var sleepTimerJob: Job? = null
    private var storyNarrationJob: Job? = null

    fun toggleTrack(trackId: String) {
        _uiState.update { state ->
            val volumes = state.activeTrackVolumes.toMutableMap()
            if (volumes.containsKey(trackId)) {
                volumes.remove(trackId)
            } else {
                val track = state.ambientTracks.find { it.id == trackId }
                volumes[trackId] = track?.defaultVolume ?: 0.7f
            }
            state.copy(activeTrackVolumes = volumes)
        }
    }

    fun setTrackVolume(trackId: String, volume: Float) {
        _uiState.update { state ->
            val volumes = state.activeTrackVolumes.toMutableMap()
            volumes[trackId] = volume
            state.copy(activeTrackVolumes = volumes)
        }
    }

    fun selectStory(story: SleepStory) {
        _uiState.update {
            it.copy(
                selectedStory = story,
                storyProgressParagraph = 0,
                isStoryPlaying = false
            )
        }
        storyNarrationJob?.cancel()
    }

    fun toggleStoryPlayback() {
        if (_uiState.value.isStoryPlaying) {
            storyNarrationJob?.cancel()
            _uiState.update { it.copy(isStoryPlaying = false) }
        } else {
            _uiState.update { it.copy(isStoryPlaying = true) }
            startStoryNarration()
        }
    }

    private fun startStoryNarration() {
        storyNarrationJob?.cancel()
        storyNarrationJob = viewModelScope.launch {
            val story = _uiState.value.selectedStory ?: return@launch
            while (_uiState.value.isStoryPlaying) {
                delay(4000L) // advance story paragraph every 4 seconds
                val next = _uiState.value.storyProgressParagraph + 1
                if (next < story.storyScript.size) {
                    _uiState.update { it.copy(storyProgressParagraph = next) }
                } else {
                    _uiState.update { it.copy(isStoryPlaying = false) }
                    awardBedtimeXp()
                    break
                }
            }
        }
    }

    fun setSleepTimer(minutes: Int) {
        _uiState.update {
            it.copy(
                sleepTimerMinutes = minutes,
                timerRemainingSeconds = minutes * 60,
                isTimerActive = minutes > 0
            )
        }

        sleepTimerJob?.cancel()
        if (minutes > 0) {
            sleepTimerJob = viewModelScope.launch {
                while (_uiState.value.isTimerActive && _uiState.value.timerRemainingSeconds > 0) {
                    delay(1000L)
                    val next = _uiState.value.timerRemainingSeconds - 1
                    _uiState.update { it.copy(timerRemainingSeconds = next) }
                    if (next <= 0) {
                        // Turn off all sounds
                        _uiState.update {
                            it.copy(
                                isTimerActive = false,
                                activeTrackVolumes = emptyMap(),
                                isStoryPlaying = false
                            )
                        }
                        break
                    }
                }
            }
        }
    }

    fun toggleWindDownItem(item: String) {
        _uiState.update { state ->
            val updated = state.windDownItems.toMutableMap()
            updated[item] = !(updated[item] ?: false)
            state.copy(windDownItems = updated)
        }
    }

    private fun awardBedtimeXp() {
        viewModelScope.launch {
            try {
                val user = lifeScoreRepository.getUserProfile().firstOrNull()
                if (user != null) {
                    lifeScoreRepository.updateUserProfile(user.copy(currentXp = user.currentXp + 25))
                }
            } catch (_: Exception) {}
            _uiState.update { it.copy(snackbarMessage = "🌙 Sleep Story Complete! Sweet dreams (+25 XP)") }
        }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        sleepTimerJob?.cancel()
        storyNarrationJob?.cancel()
    }
}
