package com.lifescore.app.data.repository

import android.app.Activity
import android.content.Context
import com.lifescore.app.core.billing.BillingManager
import com.lifescore.app.data.remote.repository.FirebaseRepository
import com.lifescore.app.domain.model.SubscriptionTier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface BillingRepository {
    val isPremium: StateFlow<Boolean>
    val activeTier: StateFlow<SubscriptionTier?>
    fun startBillingConnection()
    fun launchPurchaseFlow(activity: Activity, tier: SubscriptionTier)
    fun restorePurchases(onComplete: (Boolean) -> Unit)
}

class BillingRepositoryImpl(
    private val context: Context,
    private val lifeScoreRepository: LifeScoreRepository,
    private val firebaseRepository: FirebaseRepository? = null,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : BillingRepository {

    private val _isPremium = MutableStateFlow(false)
    override val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _activeTier = MutableStateFlow<SubscriptionTier?>(null)
    override val activeTier: StateFlow<SubscriptionTier?> = _activeTier.asStateFlow()

    private val billingManager = BillingManager(
        context = context,
        coroutineScope = scope,
        onPurchaseSuccess = { tier ->
            applyPremiumEntitlement(tier)
        }
    )

    override fun startBillingConnection() {
        billingManager.startBillingConnection {
            scope.launch {
                lifeScoreRepository.getUserProfile().collect { profile ->
                    if (profile.isPremium) {
                        _isPremium.value = true
                        _activeTier.value = SubscriptionTier.ANNUAL
                    }
                }
            }
        }
    }

    override fun launchPurchaseFlow(activity: Activity, tier: SubscriptionTier) {
        billingManager.launchPurchaseFlow(activity, tier)
        applyPremiumEntitlement(tier)
    }

    override fun restorePurchases(onComplete: (Boolean) -> Unit) {
        billingManager.restorePurchases { success ->
            if (success) {
                applyPremiumEntitlement(SubscriptionTier.ANNUAL)
            }
            onComplete(success)
        }
    }

    private fun applyPremiumEntitlement(tier: SubscriptionTier) {
        _isPremium.value = true
        _activeTier.value = tier

        scope.launch {
            // 1. Update local SQLite database
            val currentProfile = lifeScoreRepository.getUserProfile()
            currentProfile.collect { profile ->
                val updated = profile.copy(isPremium = true)
                lifeScoreRepository.updateUserProfile(updated)
            }
        }
    }
}
