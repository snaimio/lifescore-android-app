package com.lifescore.app.core.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.lifescore.app.core.config.FeatureFlags
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
    val isSubscription: Boolean,
    val productDetails: ProductDetails? = null
)

class BillingManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main),
    private val onPurchaseSuccess: (SubscriptionTier) -> Unit = {}
) : PurchasesUpdatedListener {

    private val tag = "BillingManager"

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _activeTier = MutableStateFlow<SubscriptionTier?>(null)
    val activeTier: StateFlow<SubscriptionTier?> = _activeTier.asStateFlow()

    private val _availableProducts = MutableStateFlow<List<ProductInfo>>(emptyList())
    val availableProducts: StateFlow<List<ProductInfo>> = _availableProducts.asStateFlow()

    private val cachedProductDetails = mutableMapOf<String, ProductDetails>()

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    fun startBillingConnection(onConnected: () -> Unit = {}) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(tag, "Billing client connected successfully.")
                    queryActivePurchases()
                    queryProductList()
                    onConnected()
                } else {
                    Log.w(tag, "Billing setup returned code: ${billingResult.responseCode} - ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.d(tag, "Billing service disconnected. Will reconnect on next user request.")
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
                for (details in queryResult) {
                    cachedProductDetails[details.productId] = details
                }

                val mapped = queryResult.map { details ->
                    ProductInfo(
                        productId = details.productId,
                        title = details.title,
                        formattedPrice = details.subscriptionOfferDetails?.firstOrNull()
                            ?.pricingPhases?.pricingPhaseList?.firstOrNull()?.formattedPrice
                            ?: details.oneTimePurchaseOfferDetails?.formattedPrice ?: "$49.99",
                        isSubscription = details.productType == BillingClient.ProductType.SUBS,
                        productDetails = details
                    )
                }
                _availableProducts.value = mapped
            } else {
                Log.e(tag, "Product query failed: ${billingResult.debugMessage}")
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
        val productDetails = cachedProductDetails[tier.skuId]

        if (productDetails != null) {
            val productDetailsParamsList = when (productDetails.productType) {
                BillingClient.ProductType.SUBS -> {
                    val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: ""
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .setOfferToken(offerToken)
                            .build()
                    )
                }
                else -> {
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .build()
                    )
                }
            }

            val flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

            val result = billingClient.launchBillingFlow(activity, flowParams)
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                Log.e(tag, "Failed to launch billing flow: ${result.debugMessage}")
                handleFallbackForDebug(tier)
            }
        } else {
            // Query dynamically and launch
            val isSub = tier != SubscriptionTier.LIFETIME
            val productType = if (isSub) BillingClient.ProductType.SUBS else BillingClient.ProductType.INAPP
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(
                    listOf(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(tier.skuId)
                            .setProductType(productType)
                            .build()
                    )
                )
                .build()

            billingClient.queryProductDetailsAsync(params) { billingResult, queryResult ->
                val details = queryResult.firstOrNull()
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && details != null) {
                    cachedProductDetails[tier.skuId] = details
                    coroutineScope.launch {
                        launchPurchaseFlow(activity, tier)
                    }
                } else {
                    Log.w(tag, "Product details not found for ${tier.skuId}: ${billingResult.debugMessage}")
                    handleFallbackForDebug(tier)
                }
            }
        }
    }

    private fun handleFallbackForDebug(tier: SubscriptionTier) {
        if (FeatureFlags.ENABLE_MOCK_BILLING) {
            Log.d(tag, "Debug mock billing activated for tier: $tier")
            _isPremium.value = true
            _activeTier.value = tier
            onPurchaseSuccess(tier)
        }
    }

    fun restorePurchases(onRestored: (Boolean) -> Unit) {
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val hasValidSub = purchases.any { it.purchaseState == Purchase.PurchaseState.PURCHASED }
                if (hasValidSub) {
                    processPurchases(purchases)
                    onRestored(true)
                } else {
                    // Check INAPP
                    billingClient.queryPurchasesAsync(
                        QueryPurchasesParams.newBuilder()
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
                    ) { inappResult, inappPurchases ->
                        val hasInapp = inappResult.responseCode == BillingClient.BillingResponseCode.OK &&
                                inappPurchases.any { it.purchaseState == Purchase.PurchaseState.PURCHASED }
                        if (hasInapp) {
                            processPurchases(inappPurchases)
                            onRestored(true)
                        } else {
                            _isPremium.value = false
                            _activeTier.value = null
                            onRestored(false)
                        }
                    }
                }
            } else {
                _isPremium.value = false
                _activeTier.value = null
                onRestored(false)
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            processPurchases(purchases)
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d(tag, "User cancelled purchase flow.")
        } else {
            Log.e(tag, "Purchase update error: ${billingResult.debugMessage}")
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
