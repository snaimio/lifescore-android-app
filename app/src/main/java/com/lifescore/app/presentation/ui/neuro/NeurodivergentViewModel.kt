package com.lifescore.app.presentation.ui.neuro

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class NeurodivergentUiState(
    val isDopamineMenuEnabled: Boolean = true,
    val isBionicReadingEnabled: Boolean = false,
    val selectedColorFilter: String = "WARM_AMBER", // NONE, WARM_AMBER, SAGE_GREEN, OCEAN_BLUE, ROSE_TINT
    val isVisualTimerEnabled: Boolean = true,
    val anonymousPodMembersCount: Int = 4,
    val isPodCheckinDone: Boolean = false,
    val toastMessage: String? = null
)

class NeurodivergentViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NeurodivergentUiState())
    val uiState: StateFlow<NeurodivergentUiState> = _uiState.asStateFlow()

    fun setColorFilter(filter: String) {
        _uiState.update { it.copy(selectedColorFilter = filter, toastMessage = "Color filter set to $filter") }
    }

    fun toggleDopamineMenu() {
        _uiState.update { it.copy(isDopamineMenuEnabled = !it.isDopamineMenuEnabled) }
    }

    fun toggleBionicReading() {
        _uiState.update { it.copy(isBionicReadingEnabled = !it.isBionicReadingEnabled) }
    }

    fun completePodCheckin() {
        _uiState.update {
            it.copy(
                isPodCheckinDone = true,
                toastMessage = "Checked in with Anonymous Pod #204! +30 XP Focus"
            )
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
