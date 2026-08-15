package com.lifescore.app.core.util

import com.lifescore.app.domain.model.*
import java.text.SimpleDateFormat
import java.util.*

object RewardStoreManager {

    fun getDefaultCustomRewards(): List<CustomUserReward> {
        return listOf(
            CustomUserReward(
                id = "rew_netflix",
                title = "Watch 1 Episode of Netflix / HBO",
                description = "Guilt-free entertainment break after crushing your daily MITs.",
                emoji = "🎬",
                coinCost = 150,
                unlockCondition = "Complete 5 daily quests (Earn 150 LifeCoins)",
                redemptionCount = 4
            ),
            CustomUserReward(
                id = "rew_sushi",
                title = "High-End Omakase Sushi Dinner",
                description = "Celebrate a 7-day perfect streak milestone with top-tier dinner.",
                emoji = "🍣",
                coinCost = 600,
                unlockCondition = "Maintain 7-day streak (Earn 600 LifeCoins)",
                redemptionCount = 1
            ),
            CustomUserReward(
                id = "rew_gaming",
                title = "90-Minute Uninterrupted Gaming Sprint",
                description = "Immerse in your favorite video game with zero guilt.",
                emoji = "🎮",
                coinCost = 300,
                unlockCondition = "Earn 300 LifeCoins",
                redemptionCount = 3
            ),
            CustomUserReward(
                id = "rew_coffee",
                title = "Artisan Flat White & Almond Croissant",
                description = "Morning cafe treat for finishing your morning mobility routine.",
                emoji = "☕",
                coinCost = 100,
                unlockCondition = "Earn 100 LifeCoins",
                redemptionCount = 6
            )
        )
    }

    fun getDefaultStoreProducts(): List<StoreProductItem> {
        return listOf(
            // BOOSTERS
            StoreProductItem(
                id = "boost_2x_multiplier",
                title = "2x XP & Coin Multiplier (24 Hours)",
                description = "Double all XP and LifeCoins earned from habits and masterclasses for the next 24 hours.",
                emoji = "🚀",
                category = StoreCategory.BOOSTER,
                coinCost = 400,
                durationHours = 24,
                badgeLabel = "HOT"
            ),
            StoreProductItem(
                id = "boost_streak_shield",
                title = "Emergency Streak Freeze Shield",
                description = "Automatically preserves your streak if you miss a day due to travel or emergencies.",
                emoji = "🛡️",
                category = StoreCategory.BOOSTER,
                coinCost = 300,
                badgeLabel = "POPULAR"
            ),
            StoreProductItem(
                id = "boost_skip_pass",
                title = "Instant Habit Skip Pass",
                description = "Forgives 1 incomplete habit without breaking daily 100% completion quota.",
                emoji = "⚡",
                category = StoreCategory.BOOSTER,
                coinCost = 250
            ),
            // THEMES
            StoreProductItem(
                id = "theme_cosmic_night",
                title = "Cosmic Night Deep Space Theme",
                description = "Obsidian black and nebula violet holographic styling for your cards & share studio.",
                emoji = "🌌",
                category = StoreCategory.THEME,
                coinCost = 500
            ),
            StoreProductItem(
                id = "theme_cyber_neon",
                title = "Cyberpunk High-Tech Neon Theme",
                description = "Electric cyan & neon magenta HUD accents inspired by high-performance interfaces.",
                emoji = "⚡",
                category = StoreCategory.THEME,
                coinCost = 600
            ),
            StoreProductItem(
                id = "theme_royal_gold",
                title = "Royal Gold & Ivory Sovereign Theme",
                description = "Prestigious 24k gold filigree and ivory textures for top 1% achievers.",
                emoji = "👑",
                category = StoreCategory.THEME,
                coinCost = 800
            ),
            // AVATARS / PERSONAS
            StoreProductItem(
                id = "avatar_chronomancer",
                title = "The Chronomancer Title & Persona",
                description = "Exclusive profile title and cosmic avatar frame for master time-blockers.",
                emoji = "🧙",
                category = StoreCategory.AVATAR,
                coinCost = 750
            ),
            StoreProductItem(
                id = "avatar_titan",
                title = "Titan of Unyielding Discipline",
                description = "Mythic badge displaying golden armor and unbreakable fortitude aura.",
                emoji = "⚔️",
                category = StoreCategory.AVATAR,
                coinCost = 1000
            )
        )
    }

    fun getDefaultTransactions(): List<RewardTransaction> {
        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.US)

        return listOf(
            RewardTransaction(
                id = "tx_1",
                itemTitle = "Completed 7-Day Social Duel Victory",
                category = StoreCategory.BOOSTER,
                coinsAmount = 250,
                timestamp = now - 3600000 * 2,
                formattedDate = dateFormat.format(Date(now - 3600000 * 2)),
                notes = "Reward for defeating opponent in Fitness Duel"
            ),
            RewardTransaction(
                id = "tx_2",
                itemTitle = "Redeemed: Watch 1 Episode of Netflix / HBO",
                category = StoreCategory.CUSTOM_REWARD,
                coinsAmount = -150,
                timestamp = now - 3600000 * 18,
                formattedDate = dateFormat.format(Date(now - 3600000 * 18)),
                notes = "Custom Reward Redeemed"
            ),
            RewardTransaction(
                id = "tx_3",
                itemTitle = "Purchased: 2x XP Multiplier (24h)",
                category = StoreCategory.BOOSTER,
                coinsAmount = -400,
                timestamp = now - 3600000 * 48,
                formattedDate = dateFormat.format(Date(now - 3600000 * 48)),
                notes = "Active booster applied"
            ),
            RewardTransaction(
                id = "tx_4",
                itemTitle = "7-Day Streak Milestone Bonus",
                category = StoreCategory.CUSTOM_REWARD,
                coinsAmount = 100,
                timestamp = now - 3600000 * 72,
                formattedDate = dateFormat.format(Date(now - 3600000 * 72)),
                notes = "Consistent execution bonus"
            )
        )
    }

    fun redeemCustomReward(
        user: UserProfile,
        reward: CustomUserReward
    ): Pair<UserProfile, RewardTransaction?> {
        if (user.coinBalance < reward.coinCost) {
            return Pair(user, null) // Insufficient funds
        }

        val updatedUser = user.copy(coinBalance = user.coinBalance - reward.coinCost)
        val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.US)
        val tx = RewardTransaction(
            itemTitle = "Redeemed: ${reward.title}",
            category = StoreCategory.CUSTOM_REWARD,
            coinsAmount = -reward.coinCost,
            formattedDate = dateFormat.format(Date()),
            notes = "Enjoy your guilt-free reward!"
        )

        return Pair(updatedUser, tx)
    }

    fun buyStoreProduct(
        user: UserProfile,
        product: StoreProductItem
    ): Pair<UserProfile, RewardTransaction?> {
        if (user.coinBalance < product.coinCost) {
            return Pair(user, null)
        }

        val updatedUser = user.copy(coinBalance = user.coinBalance - product.coinCost)
        val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.US)
        val tx = RewardTransaction(
            itemTitle = "Purchased: ${product.title}",
            category = product.category,
            coinsAmount = -product.coinCost,
            formattedDate = dateFormat.format(Date()),
            notes = "${product.category.displayName} Unlocked"
        )

        return Pair(updatedUser, tx)
    }
}
