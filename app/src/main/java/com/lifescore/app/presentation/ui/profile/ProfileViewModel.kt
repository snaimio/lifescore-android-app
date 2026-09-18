package com.lifescore.app.presentation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.core.util.LeagueManager
import com.lifescore.app.core.util.LeagueTier
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.domain.model.HeroArchetype
import com.lifescore.app.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: UserProfile = UserProfile(),
    val archetype: HeroArchetype = HeroArchetype.WARRIOR,
    val leagueTier: LeagueTier = LeagueTier.BRONZE,
    val totalTasksCompleted: Int = 0,
    val totalTasksCount: Int = 0,
    val consistencyPercentage: Int = 0,
    val focusHours: Float = 0.0f,
    val lifetimePoints: Int = 0,
    val streakShieldsAvailable: Int = 0,
    val coinBalance: Int = 0,
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
            combine(
                repository.getUserProfile(),
                repository.getAllTasks()
            ) { profile, tasks ->
                val completed = tasks.count { it.isCompleted }
                val total = tasks.size
                val consistency = if (total > 0) ((completed.toFloat() / total.toFloat()) * 100).toInt() else 0
                val focusHours = completed * 0.5f
                val tier = LeagueManager.getLeagueForScore(profile.currentXp)

                ProfileUiState(
                    user = profile,
                    leagueTier = tier,
                    totalTasksCompleted = completed,
                    totalTasksCount = total,
                    consistencyPercentage = consistency,
                    focusHours = focusHours,
                    lifetimePoints = profile.currentXp,
                    streakShieldsAvailable = profile.shieldsRemaining,
                    coinBalance = profile.coinBalance
                )
            }.collect { state ->
                _uiState.value = state
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
