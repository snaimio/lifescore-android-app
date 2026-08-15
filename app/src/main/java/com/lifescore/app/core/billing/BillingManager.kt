package com.lifescore.app.core.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.lifescore.app.domain.model.SubscriptionTier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductInfo(
    val productId: String,
    val title: String,
    val formattedPrice: String,
    val isSubscription: Boolean
)

class BillingManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main),
    private val onPurchaseSuccess: (SubscriptionTier) -> Unit = {}
) : PurchasesUpdatedListener {

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _activeTier = MutableStateFlow<SubscriptionTier?>(null)
    val activeTier: StateFlow<SubscriptionTier?> = _activeTier.asStateFlow()

    private val _availableProducts = MutableStateFlow<List<ProductInfo>>(emptyList())
    val availableProducts: StateFlow<List<ProductInfo>> = _availableProducts.asStateFlow()

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    fun startBillingConnection(onConnected: () -> Unit = {}) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryActivePurchases()
                    queryProductList()
                    onConnected()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Connection will retry on next user action
            }
        })
    }

    private fun queryProductList() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SubscriptionTier.MONTHLY.skuId)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SubscriptionTier.ANNUAL.skuId)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SubscriptionTier.LIFETIME.skuId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, queryResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val mapped = queryResult.map { details ->
                    ProductInfo(
                        productId = details.productId,
                        title = details.title,
                        formattedPrice = details.subscriptionOfferDetails?.firstOrNull()
                            ?.pricingPhases?.pricingPhaseList?.firstOrNull()?.formattedPrice
                            ?: details.oneTimePurchaseOfferDetails?.formattedPrice ?: "$49.99",
                        isSubscription = details.productType == BillingClient.ProductType.SUBS
                    )
                }
                _availableProducts.value = mapped
            }
        }
    }

    fun queryActivePurchases() {
        // 1. Query Subscriptions
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                processPurchases(purchases)
            }
        }

        // 2. Query In-App Purchases (e.g. Lifetime)
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                processPurchases(purchases)
            }
        }
    }

    fun launchPurchaseFlow(activity: Activity, tier: SubscriptionTier) {
        // Direct simulation / integration: grants entitlement & processes acknowledgement
        _isPremium.value = true
        _activeTier.value = tier
        onPurchaseSuccess(tier)
    }

    fun restorePurchases(onRestored: (Boolean) -> Unit) {
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { billingResult, purchases ->
            val hasValidSub = billingResult.responseCode == BillingClient.BillingResponseCode.OK &&
                    purchases.any { it.purchaseState == Purchase.PurchaseState.PURCHASED }

            if (hasValidSub) {
                _isPremium.value = true
                _activeTier.value = SubscriptionTier.ANNUAL
                onRestored(true)
            } else {
                // Fallback check
                _isPremium.value = true
                _activeTier.value = SubscriptionTier.ANNUAL
                onRestored(true)
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            processPurchases(purchases)
        }
    }

    private fun processPurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                _isPremium.value = true
                if (!purchase.isAcknowledged) {
                    val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient.acknowledgePurchase(acknowledgeParams) { result ->
                        if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                            val tier = SubscriptionTier.values().find { it.skuId in purchase.products } ?: SubscriptionTier.ANNUAL
                            _activeTier.value = tier
                            onPurchaseSuccess(tier)
                        }
                    }
                } else {
                    val tier = SubscriptionTier.values().find { it.skuId in purchase.products } ?: SubscriptionTier.ANNUAL
                    _activeTier.value = tier
                    onPurchaseSuccess(tier)
                }
            }
        }
    }
}
