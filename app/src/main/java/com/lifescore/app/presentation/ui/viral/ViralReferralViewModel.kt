package com.lifescore.app.presentation.ui.viral

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.local.entity.ReferralEntity
import com.lifescore.app.data.repository.ViralGrowthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ViralReferralUiState(
    val referral: ReferralEntity? = null,
    val simulatedFriendNameInput: String = "",
    val isShareSheetOpen: Boolean = false,
    val toastMessage: String? = null
)

class ViralReferralViewModel(
    private val repository: ViralGrowthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ViralReferralUiState())
    val uiState: StateFlow<ViralReferralUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getReferralInfo().collect { ref ->
                _uiState.update { it.copy(referral = ref) }
            }
        }
    }

    fun onFriendNameChange(name: String) {
        _uiState.update { it.copy(simulatedFriendNameInput = name) }
    }

    fun simulateFriendSignup() {
        val name = _uiState.value.simulatedFriendNameInput.trim().ifBlank { "Jordan Lee" }
        viewModelScope.launch {
            val unlocked = repository.simulateInviteFriend(name)
            _uiState.update {
                it.copy(
                    simulatedFriendNameInput = "",
                    toastMessage = if (unlocked) "🎉 3 Friends Joined! 1-Month Premium Unlocked!" else "Friend $name joined with your code! +150 XP"
                )
            }
        }
    }

    fun claimPremiumReward() {
        viewModelScope.launch {
            repository.claimReferralReward()
            _uiState.update { it.copy(toastMessage = "1-Month Free Premium Activated! +200 XP") }
        }
    }

    fun toggleShareModal(open: Boolean) {
        _uiState.update { it.copy(isShareSheetOpen = open) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
