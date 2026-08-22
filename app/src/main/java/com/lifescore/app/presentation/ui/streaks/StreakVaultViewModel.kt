package com.lifescore.app.presentation.ui.streaks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.local.entity.StreakInventoryEntity
import com.lifescore.app.data.repository.ViralGrowthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class StreakVaultUiState(
    val inventory: StreakInventoryEntity? = null,
    val toastMessage: String? = null
)

class StreakVaultViewModel(
    private val repository: ViralGrowthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StreakVaultUiState())
    val uiState: StateFlow<StreakVaultUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getStreakInventory().collect { inv ->
                _uiState.update { it.copy(inventory = inv) }
            }
        }
    }

    fun toggleShield() {
        val current = _uiState.value.inventory ?: return
        viewModelScope.launch {
            repository.toggleFreezeShield(!current.isFreezeShieldArmed)
            _uiState.update {
                it.copy(
                    toastMessage = if (!current.isFreezeShieldArmed) "🛡️ Streak Freeze Shield Armed!" else "Shield Disarmed"
                )
            }
        }
    }

    fun startResurrectionQuest() {
        viewModelScope.launch {
            repository.startResurrectionQuest(lostStreakDays = 15)
            _uiState.update { it.copy(toastMessage = "⚔️ 3-Day Resurrection Quest Started! Recover your 15-day streak!") }
        }
    }

    fun advanceResurrectionDay() {
        viewModelScope.launch {
            val completed = repository.advanceResurrectionProgress()
            _uiState.update {
                it.copy(
                    toastMessage = if (completed) "🔥 STREAK RESURRECTED! Full 18-Day Streak Restored!" else "Day Completed! 1 more day to fully resurrect!"
                )
            }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
