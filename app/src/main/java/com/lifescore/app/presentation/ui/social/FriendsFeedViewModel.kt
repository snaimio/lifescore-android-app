package com.lifescore.app.presentation.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.local.entity.FriendActivityEntity
import com.lifescore.app.data.repository.ViralGrowthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class FriendsFeedUiState(
    val activities: List<FriendActivityEntity> = emptyList(),
    val toastMessage: String? = null
)

class FriendsFeedViewModel(
    private val repository: ViralGrowthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendsFeedUiState())
    val uiState: StateFlow<FriendsFeedUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getFriendFeed().collect { list ->
                _uiState.update { it.copy(activities = list) }
            }
        }
    }

    fun nudgeFriend(activity: FriendActivityEntity) {
        viewModelScope.launch {
            repository.nudgeFriend(activity.id)
            _uiState.update {
                it.copy(
                    toastMessage = "🔥 Sent Streak Encouragement Nudge to ${activity.friendName}! (+10 XP)"
                )
            }
        }
    }

    fun giftStreakFreeze(activity: FriendActivityEntity) {
        viewModelScope.launch {
            val success = repository.giftStreakFreeze(activity.friendName)
            _uiState.update {
                it.copy(
                    toastMessage = if (success) "🎁 Gifted 1 Streak Freeze to ${activity.friendName}! (+30 XP)" else "No streak freezes available in vault to gift."
                )
            }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
