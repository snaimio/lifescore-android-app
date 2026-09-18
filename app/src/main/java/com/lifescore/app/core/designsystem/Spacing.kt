package com.lifescore.app.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ============================================
// LIFESCORE — Strict 4dp Spacing Grid
// ============================================

object Space {
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp
    val xxxl = 48.dp
    val huge = 64.dp

    // Screen paddings
    val screenH = 20.dp
    val screenV = 24.dp
    val screenTop = 16.dp

    // Card paddings
    val cardH = 20.dp
    val cardV = 20.dp
    val cardGap = 12.dp

    // Section spacing
    val sectionGap = 32.dp
}

object Spacing {
    val xxs: Dp = Space.xxs
    val xs: Dp = Space.xs
    val sm: Dp = Space.sm
    val md: Dp = Space.lg
    val lg: Dp = Space.xl
    val xl: Dp = Space.xxl
    val xxl: Dp = Space.xxxl
    val xxxl: Dp = Space.huge

    val defaultPadding: Dp = Space.screenH
    val cardPadding: Dp = Space.cardH
    val screenPadding: Dp = Space.screenH
    val itemSpacing: Dp = Space.cardGap
    val sectionSpacing: Dp = Space.sectionGap

    @Composable
    fun responsiveHorizontalPadding(): Dp {
        val screenWidthDp = LocalConfiguration.current.screenWidthDp
        return when {
            screenWidthDp <= 340 -> 12.dp
            screenWidthDp <= 400 -> 16.dp
            else -> 20.dp
        }
    }

    @Composable
    fun responsiveCardPadding(): Dp {
        val screenWidthDp = LocalConfiguration.current.screenWidthDp
        return if (screenWidthDp <= 340) 12.dp else 20.dp
    }
}

object Elevation {
    val none: Dp = 0.dp
    val low: Dp = 1.dp
    val medium: Dp = 3.dp
    val high: Dp = 6.dp
    val extraHigh: Dp = 12.dp
}
