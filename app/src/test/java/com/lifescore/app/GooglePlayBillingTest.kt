package com.lifescore.app

import com.lifescore.app.data.remote.model.UserDocument
import com.lifescore.app.domain.model.SubscriptionTier
import org.junit.Assert.*
import org.junit.Test

class GooglePlayBillingTest {

    @Test
    fun testSubscriptionTierSkuIntegrity() {
        val tiers = SubscriptionTier.values()
        assertEquals(3, tiers.size)

        val annual = SubscriptionTier.ANNUAL
        assertEquals("lifescore_annual_4999", annual.skuId)
        assertTrue(annual.isPopular)
        assertEquals("$49.99", annual.priceFormatted)

        val monthly = SubscriptionTier.MONTHLY
        assertEquals("lifescore_monthly_799", monthly.skuId)
        assertEquals("$7.99", monthly.priceFormatted)

        val lifetime = SubscriptionTier.LIFETIME
        assertEquals("lifescore_lifetime_119", lifetime.skuId)
        assertEquals("$119.99", lifetime.priceFormatted)
    }

    @Test
    fun testUserDocumentSubscriptionFields() {
        val proUserDoc = UserDocument(
            uid = "user_pro_777",
            displayName = "Champion Pro",
            isPremium = true
        )

        assertTrue(proUserDoc.isPremium)
        assertEquals("Champion Pro", proUserDoc.displayName)
    }

    @Test
    fun testAnnualSavingsCalculation() {
        // 12 months at $7.99 = $95.88 vs Annual $49.99 -> savings > 45%
        val monthlyYearCost = 7.99 * 12
        val annualCost = 49.99
        val savingsPercent = ((monthlyYearCost - annualCost) / monthlyYearCost) * 100

        assertTrue(savingsPercent >= 45.0)
    }
}
