package com.lifescore.app.core.config

import com.lifescore.app.BuildConfig

object FeatureFlags {
    // A/B testing and feature rollout
    const val ENABLE_AI_QUESTS = true
    const val ENABLE_COMBAT_SYSTEM = true
    const val ENABLE_GROUP_HABITS = true
    const val ENABLE_JOURNALING = true
    const val ENABLE_MICRO_VLOGS = true
    const val ENABLE_PAYWALL = true
    const val ENABLE_ENTERPRISE = true
    
    // Debug features
    val SHOW_DEBUG_LOGS = BuildConfig.DEBUG
    val ENABLE_MOCK_BILLING = BuildConfig.DEBUG
}
