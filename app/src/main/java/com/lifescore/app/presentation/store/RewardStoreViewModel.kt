package com.lifescore.app.presentation.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.core.util.RewardStoreManager
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class StoreTab(val title: String, val icon: String) {
    CUSTOM("🎁 Custom Rewards", "🎁"),
    PREMIUM("💎 Premium Store", "💎"),
    HISTORY("📊 History & Ledger", "📊")
}

data class RewardStoreUiState(
    val selectedTab: StoreTab = StoreTab.CUSTOM,
    val userProfile: UserProfile = UserProfile(),
    val customRewards: List<CustomUserReward> = RewardStoreManager.getDefaultCustomRewards(),
    val storeProducts: List<StoreProductItem> = RewardStoreManager.getDefaultStoreProducts(),
    val transactions: List<RewardTransaction> = RewardStoreManager.getDefaultTransactions(),
    val boosterState: BoosterState = BoosterState(
        isDoubleXpActive = true,
        doubleXpExpiresAt = System.currentTimeMillis() + 3600000 * 14,
        streakShieldsAvailable = 2,
        instantSkipPassesAvailable = 1
    ),
    val isCreateCustomDialogOpen: Boolean = false,
    val selectedProductForPurchase: StoreProductItem? = null,
    val recentSuccessMessage: String? = null,
    val errorMessage: String? = null
) {
    val totalCoinsEarnedLifetime: Int get() = userProfile.lifetimeCoinsEarned
    val totalCoinsSpent: Int get() = transactions.filter { it.coinsAmount < 0 }.sumOf { -it.coinsAmount }
}

class RewardStoreViewModel(
    private val repository: LifeScoreRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(RewardStoreUiState())
    val uiState: StateFlow<RewardStoreUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            repository?.getUserProfile()?.collect { profile ->
                _uiState.value = _uiState.value.copy(userProfile = profile)
            }
        }
    }

    fun selectTab(tab: StoreTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun redeemReward(reward: CustomUserReward) {
        val user = _uiState.value.userProfile
        val (updatedUser, tx) = RewardStoreManager.redeemCustomReward(user, reward)

        if (tx == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Insufficient LifeCoins! Need ${reward.coinCost} coins (You have ${user.coinBalance})."
            )
            return
        }

        val updatedRewards = _uiState.value.customRewards.map {
            if (it.id == reward.id) it.copy(
                redemptionCount = it.redemptionCount + 1,
                lastRedeemedAt = System.currentTimeMillis(),
                isRedeemed = true
            ) else it
        }

        _uiState.value = _uiState.value.copy(
            userProfile = updatedUser,
            customRewards = updatedRewards,
            transactions = listOf(tx) + _uiState.value.transactions,
            recentSuccessMessage = "🎉 Redeemed '${reward.title}'! -${reward.coinCost} Coins"
        )
    }

    fun buyProduct(product: StoreProductItem) {
        val user = _uiState.value.userProfile
        val (updatedUser, tx) = RewardStoreManager.buyStoreProduct(user, product)

        if (tx == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Insufficient LifeCoins! Need ${product.coinCost} coins (You have ${user.coinBalance})."
            )
            return
        }

        val updatedProducts = _uiState.value.storeProducts.map {
            if (it.id == product.id) it.copy(isPurchased = true) else it
        }

        // Update booster state if applicable
        val updatedBooster = when (product.id) {
            "boost_2x_multiplier" -> _uiState.value.boosterState.copy(
                isDoubleXpActive = true,
                doubleXpExpiresAt = System.currentTimeMillis() + 86400000
            )
            "boost_streak_shield" -> _uiState.value.boosterState.copy(
                streakShieldsAvailable = _uiState.value.boosterState.streakShieldsAvailable + 1
            )
            "boost_skip_pass" -> _uiState.value.boosterState.copy(
                instantSkipPassesAvailable = _uiState.value.boosterState.instantSkipPassesAvailable + 1
            )
            else -> _uiState.value.boosterState
        }

        _uiState.value = _uiState.value.copy(
            userProfile = updatedUser,
            storeProducts = updatedProducts,
            boosterState = updatedBooster,
            transactions = listOf(tx) + _uiState.value.transactions,
            recentSuccessMessage = "✨ Purchased '${product.title}'! -${product.coinCost} Coins"
        )
    }

    fun createCustomReward(title: String, emoji: String, coinCost: Int, description: String) {
        if (title.isBlank()) return
        val newReward = CustomUserReward(
            title = title,
            emoji = emoji.ifBlank { "🎁" },
            coinCost = coinCost.coerceAtLeast(10),
            description = description,
            unlockCondition = "Earn $coinCost LifeCoins"
        )

        _uiState.value = _uiState.value.copy(
            customRewards = listOf(newReward) + _uiState.value.customRewards,
            isCreateCustomDialogOpen = false,
            recentSuccessMessage = "Created custom reward: '$title' (${coinCost} coins)!"
        )
    }

    fun openCreateDialog() {
        _uiState.value = _uiState.value.copy(isCreateCustomDialogOpen = true)
    }

    fun closeCreateDialog() {
        _uiState.value = _uiState.value.copy(isCreateCustomDialogOpen = false)
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(recentSuccessMessage = null, errorMessage = null)
    }
}
