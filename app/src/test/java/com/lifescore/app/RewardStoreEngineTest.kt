package com.lifescore.app

import com.lifescore.app.core.util.RewardStoreManager
import com.lifescore.app.data.remote.model.UserDocument
import com.lifescore.app.domain.model.*
import org.junit.Assert.*
import org.junit.Test

class RewardStoreEngineTest {

    @Test
    fun testDefaultStoreCatalogsIntegrity() {
        val customRewards = RewardStoreManager.getDefaultCustomRewards()
        assertTrue(customRewards.isNotEmpty())
        assertTrue(customRewards.any { it.title.contains("Netflix") })
        assertTrue(customRewards.any { it.title.contains("Sushi") })

        val products = RewardStoreManager.getDefaultStoreProducts()
        assertTrue(products.size >= 8)

        val categories = products.map { it.category }
        assertTrue(categories.contains(StoreCategory.BOOSTER))
        assertTrue(categories.contains(StoreCategory.THEME))
        assertTrue(categories.contains(StoreCategory.AVATAR))
    }

    @Test
    fun testRedeemCustomRewardSuccessAndDeduction() {
        val user = UserProfile(coinBalance = 500, lifetimeCoinsEarned = 1200)
        val reward = CustomUserReward(
            id = "test_rew",
            title = "Watch 1 Episode Netflix",
            coinCost = 150
        )

        val (updatedUser, tx) = RewardStoreManager.redeemCustomReward(user, reward)

        assertNotNull(tx)
        assertEquals(350, updatedUser.coinBalance)
        assertEquals(-150, tx!!.coinsAmount)
        assertEquals(StoreCategory.CUSTOM_REWARD, tx.category)
        assertTrue(tx.itemTitle.contains("Netflix"))
    }

    @Test
    fun testRedeemCustomRewardInsufficientCoins() {
        val user = UserProfile(coinBalance = 50)
        val reward = CustomUserReward(
            id = "test_rew_expensive",
            title = "Omakase Sushi Dinner",
            coinCost = 600
        )

        val (updatedUser, tx) = RewardStoreManager.redeemCustomReward(user, reward)

        assertNull(tx) // Transaction should be null when insufficient funds
        assertEquals(50, updatedUser.coinBalance) // Balance unmodified
    }

    @Test
    fun testBuyStoreProductSuccess() {
        val user = UserProfile(coinBalance = 1000)
        val product = StoreProductItem(
            id = "boost_2x_multiplier",
            title = "2x XP Multiplier (24 Hours)",
            description = "Double all XP for 24h",
            emoji = "🚀",
            category = StoreCategory.BOOSTER,
            coinCost = 400
        )

        val (updatedUser, tx) = RewardStoreManager.buyStoreProduct(user, product)

        assertNotNull(tx)
        assertEquals(600, updatedUser.coinBalance)
        assertEquals(-400, tx!!.coinsAmount)
        assertEquals(StoreCategory.BOOSTER, tx.category)
    }

    @Test
    fun testUserDocumentFirestoreCoinFields() {
        val userDoc = UserDocument(
            uid = "user_coin_test_101",
            displayName = "Disciplined Monk",
            coinBalance = 2400,
            lifetimeCoinsEarned = 5800
        )

        assertEquals("user_coin_test_101", userDoc.uid)
        assertEquals(2400, userDoc.coinBalance)
        assertEquals(5800, userDoc.lifetimeCoinsEarned)
    }

    @Test
    fun testTransactionsLedgerCalculations() {
        val txs = RewardStoreManager.getDefaultTransactions()
        assertTrue(txs.isNotEmpty())

        val totalEarned = txs.filter { it.coinsAmount > 0 }.sumOf { it.coinsAmount }
        val totalSpent = txs.filter { it.coinsAmount < 0 }.sumOf { -it.coinsAmount }

        assertTrue(totalEarned > 0)
        assertTrue(totalSpent > 0)
    }
}
