package com.lifescore.app.core.config

/**
 * Application build and environment configuration.
 * Provides source-level constant definitions for app metadata,
 * debug toggles, and AI parameters to eliminate AGP-generated
 * BuildConfig IDE resolution issues.
 */
object AppConfig {
    const val APPLICATION_ID: String = "com.lifescore.app"
    const val VERSION_NAME: String = "1.0.0"
    const val VERSION_CODE: Int = 1
    const val BUILD_TYPE: String = "debug"
    const val DEBUG: Boolean = true
    const val DEBUG_MODE: Boolean = true

    // AI Engine Configuration
    const val GEMINI_API_KEY: String = "DEMO_KEY"
    const val GEMINI_MODEL: String = "gemini-1.5-flash"
}
