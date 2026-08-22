package com.lifescore.app.presentation.ui.meditation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val timerRemainingSeconds: Int = 600,
    val toastMessage: String? = null
)

class MeditationViewModel(
    private val repository: MeditationLibraryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MeditationUiState())
    val uiState: StateFlow<MeditationUiState> = _uiState.asStateFlow()

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
}
