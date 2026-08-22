package com.lifescore.app.domain.model

enum class SupporterTier(val title: String, val badgeEmoji: String) {
    FREE("Adventurer", "🌱"),
    SUPPORTER("Supporter Patron", "💎"),
    PREMIUM("LifeScore Pro", "👑")
}

data class SupporterSubscription(
    val userId: String = "local_user",
    val tier: SupporterTier = SupporterTier.FREE,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val isActive: Boolean = true,
    val monthlyPrice: String = "$4.99/month",
    val annualPrice: String = "$47.99/year ($3.99/mo)"
)

object SubscriptionManager {

    fun getSubscriptionBenefits(tier: SupporterTier): List<String> {
        return when (tier) {
            SupporterTier.FREE -> listOf(
                "✅ 100% Free Core Experience",
                "✅ All 88+ Features & Modules",
                "✅ All 15 Dedicated Trackers",
                "✅ 8 Life Dimensions & Radar",
                "✅ Daily Micro-Quests & XP",
                "✅ Stanford AI Coach (Basic)",
                "✅ RPG Boss Raids & Party System",
                "✅ 10-Tier Leagues & Duels",
                "✅ Earn Gems from Gameplay"
            )
            SupporterTier.SUPPORTER -> listOf(
                "💎 All FREE Core Benefits",
                "💎 Convert In-Game Gold to Gems (100:1)",
                "💎 Exclusive Supporter Avatar & Themes",
                "💎 Supporter Patron Profile Badge",
                "💎 Early Access to New Feature Modules",
                "💎 100% Ad-Free Support of Development",
                "💎 Keep LifeScore Free for Everyone"
            )
            SupporterTier.PREMIUM -> listOf(
                "👑 All SUPPORTER Benefits",
                "👑 Unlimited Stanford AI Memory & Long-Term Coaching",
                "👑 Advanced Multi-Year Predictive Analytics",
                "👑 Custom Habit Loops & Sub-Routines",
                "👑 Priority Feature Requests & Discord VIP"
            )
        }
    }

    fun getSupporterPrice(): String = "$4.99/month or $47.99/year"
}
