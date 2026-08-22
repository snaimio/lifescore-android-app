package com.lifescore.app.presentation.ui.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.local.entity.CustomRewardEntity
import com.lifescore.app.data.repository.ViralGrowthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CustomRewardsUiState(
    val goldBalance: Int = 420,
    val rewards: List<CustomRewardEntity> = emptyList(),
    val newTitle: String = "",
    val newGoldCost: String = "75",
    val newEmoji: String = "🎁",
    val newCategory: String = "Leisure",
    val isCreatingReward: Boolean = false,
    val toastMessage: String? = null
)

class CustomRewardsViewModel(
    private val repository: ViralGrowthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomRewardsUiState())
    val uiState: StateFlow<CustomRewardsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getCustomRewards().collect { list ->
                _uiState.update { it.copy(rewards = list) }
            }
        }
    }

    fun onTitleChange(t: String) = _uiState.update { it.copy(newTitle = t) }
    fun onGoldCostChange(g: String) = _uiState.update { it.copy(newGoldCost = g) }
    fun onEmojiChange(e: String) = _uiState.update { it.copy(newEmoji = e) }
    fun onCategoryChange(c: String) = _uiState.update { it.copy(newCategory = c) }
    fun toggleCreateSheet(open: Boolean) = _uiState.update { it.copy(isCreatingReward = open) }

    fun createCustomReward() {
        val title = _uiState.value.newTitle.trim().ifBlank { return }
        val cost = _uiState.value.newGoldCost.toIntOrNull() ?: 75
        val emoji = _uiState.value.newEmoji.trim().ifBlank { "🎁" }
        val category = _uiState.value.newCategory

        viewModelScope.launch {
            repository.createCustomReward(title, cost, emoji, category)
            _uiState.update {
                it.copy(
                    newTitle = "",
                    newGoldCost = "75",
                    isCreatingReward = false,
                    toastMessage = "Added Custom Reward '$title'!"
                )
            }
        }
    }

    fun redeemReward(reward: CustomRewardEntity) {
        val currentGold = _uiState.value.goldBalance
        if (currentGold < reward.goldPrice) {
            _uiState.update { it.copy(toastMessage = "Not enough Gold! Complete more habits to earn +${reward.goldPrice - currentGold} Gold.") }
            return
        }

        viewModelScope.launch {
            repository.redeemCustomReward(reward.id)
            _uiState.update {
                it.copy(
                    goldBalance = currentGold - reward.goldPrice,
                    toastMessage = "🎉 Redeemed '${reward.title}'! Enjoy your guilt-free reward!"
                )
            }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
