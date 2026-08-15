package com.lifescore.app.presentation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.core.util.ArchetypeManager
import com.lifescore.app.core.util.LeagueManager
import com.lifescore.app.core.util.LeagueTier
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.domain.model.HeroArchetype
import com.lifescore.app.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: UserProfile = UserProfile(),
    val archetype: HeroArchetype = HeroArchetype.WARRIOR,
    val leagueTier: LeagueTier = LeagueTier.DIAMOND,
    val totalQuestsCompleted: Int = 142,
    val lifetimePoints: Int = 4320,
    val streakShieldsAvailable: Int = 3,
    val coinBalance: Int = 1250,
    val isReferralModalOpen: Boolean = false
)

class ProfileViewModel(
    private val repository: LifeScoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            repository.getUserProfile().collect { profile ->
                val tier = LeagueManager.getLeagueForScore(profile.currentXp)
                _uiState.value = _uiState.value.copy(
                    user = profile,
                    leagueTier = tier,
                    lifetimePoints = profile.currentXp + 2500
                )
            }
        }
    }

    fun openReferralModal() {
        _uiState.value = _uiState.value.copy(isReferralModalOpen = true)
    }

    fun closeReferralModal() {
        _uiState.value = _uiState.value.copy(isReferralModalOpen = false)
    }
}
