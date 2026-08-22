package com.lifescore.app.domain.model

import com.lifescore.app.core.engine.GemSystem
import com.lifescore.app.core.engine.GemTransaction
import com.lifescore.app.core.engine.GemTransactionType
import com.lifescore.app.core.engine.GoldToGemsConverter
import org.junit.Assert.*
import org.junit.Test

class MonetizationSystemTest {

    @Test
    fun gemSystem_earnsGemsForQuestsCorrectly() {
        assertEquals(0, GemSystem.earnGemsForQuest(0))
        assertEquals(5, GemSystem.earnGemsForQuest(1))
        assertEquals(10, GemSystem.earnGemsForQuest(5))
        assertEquals(15, GemSystem.earnGemsForQuest(10))
        assertEquals(30, GemSystem.earnGemsForQuest(35))
    }

    @Test
    fun gemSystem_earnsGemsForStreaksCorrectly() {
        assertEquals(0, GemSystem.earnGemsForStreak(0))
        assertEquals(5, GemSystem.earnGemsForStreak(1))
        assertEquals(10, GemSystem.earnGemsForStreak(3))
        assertEquals(20, GemSystem.earnGemsForStreak(7))
        assertEquals(30, GemSystem.earnGemsForStreak(14))
        assertEquals(50, GemSystem.earnGemsForStreak(30))
    }

    @Test
    fun gemSystem_earnsGemsForTasksCompleted() {
        assertEquals(0, GemSystem.earnGemsForTaskCompletion(4))
        assertEquals(1, GemSystem.earnGemsForTaskCompletion(5))
        assertEquals(4, GemSystem.earnGemsForTaskCompletion(23))
    }

    @Test
    fun gemSystem_packagesConfiguredProperly() {
        val packages = GemSystem.availablePackages
        assertTrue("At least 4 gem packages should exist", packages.size >= 4)
        val bestValue = packages.find { it.isPopular }
        assertNotNull("Best value package should be tagged", bestValue)
        assertEquals("gems_chest", bestValue?.id)
    }

    @Test
    fun cosmeticStore_catalogContainsRequiredCategories() {
        val items = CosmeticStoreCatalog.items
        assertTrue("Cosmetic catalog should have items", items.isNotEmpty())

        val avatars = items.filter { it.category == CosmeticCategory.AVATAR }
        val themes = items.filter { it.category == CosmeticCategory.THEME }
        val badges = items.filter { it.category == CosmeticCategory.BADGE }

        assertTrue("Should have avatar skins", avatars.size >= 3)
        assertTrue("Should have themes", themes.size >= 3)
        assertTrue("Should have badges", badges.size >= 2)
    }

    @Test
    fun goldToGemsConverter_convertsAtExactRate() {
        assertEquals(100, GoldToGemsConverter.CONVERSION_RATE)
        assertFalse(GoldToGemsConverter.canConvertGold(99))
        assertTrue(GoldToGemsConverter.canConvertGold(100))
        assertTrue(GoldToGemsConverter.canConvertGold(850))

        assertEquals(0, GoldToGemsConverter.convertGoldToGems(50))
        assertEquals(1, GoldToGemsConverter.convertGoldToGems(100))
        assertEquals(8, GoldToGemsConverter.convertGoldToGems(850))

        val remainingGold = GoldToGemsConverter.calculateRemainingGold(gold = 850, gemsToObtain = 5)
        assertEquals(350, remainingGold)
    }

    @Test
    fun subscriptionManager_benefitsHierarchyPreserved() {
        val freeBenefits = SubscriptionManager.getSubscriptionBenefits(SupporterTier.FREE)
        val supporterBenefits = SubscriptionManager.getSubscriptionBenefits(SupporterTier.SUPPORTER)
        val premiumBenefits = SubscriptionManager.getSubscriptionBenefits(SupporterTier.PREMIUM)

        assertTrue(freeBenefits.any { it.contains("100% Free Core Experience") })
        assertTrue(supporterBenefits.any { it.contains("Convert In-Game Gold to Gems") })
        assertTrue(premiumBenefits.any { it.contains("Stanford AI") })
    }
}
