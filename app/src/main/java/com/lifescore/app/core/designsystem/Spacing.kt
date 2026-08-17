package com.lifescore.app.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 8dp Material Design Spacing System
 * Provides standardized spacing tokens and responsive dimension adapters.
 */
object Spacing {
    val xxs: Dp = 2.dp
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 16.dp
    val lg: Dp = 24.dp
    val xl: Dp = 32.dp
    val xxl: Dp = 48.dp
    val xxxl: Dp = 64.dp

    // Standard Semantic Tokens
    val defaultPadding: Dp = 16.dp
    val cardPadding: Dp = 16.dp
    val screenPadding: Dp = 16.dp
    val itemSpacing: Dp = 12.dp
    val sectionSpacing: Dp = 20.dp

    /**
     * Responsive horizontal screen padding that scales down gracefully on narrow
     * viewports (like Samsung S24 at 540dpi = 320dp width).
     */
    @Composable
    fun responsiveHorizontalPadding(): Dp {
        val screenWidthDp = LocalConfiguration.current.screenWidthDp
        return when {
            screenWidthDp <= 340 -> 12.dp
            screenWidthDp <= 400 -> 16.dp
            else -> 20.dp
        }
    }

    /**
     * Responsive card padding for compact viewports.
     */
    @Composable
    fun responsiveCardPadding(): Dp {
        val screenWidthDp = LocalConfiguration.current.screenWidthDp
        return if (screenWidthDp <= 340) 12.dp else 16.dp
    }
}
