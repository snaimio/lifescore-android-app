package com.lifescore.app.core.designsystem

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// ⚡ Animation Constants
object AnimationConstants {
    const val FAST = 200
    const val MEDIUM = 400
    const val SLOW = 600
    const val VERY_SLOW = 800

    const val Fast = 200
    const val Medium = 400
    const val Slow = 600
    const val VerySlow = 800
    
    val SpringSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
    
    val SnapSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )
}

// 📦 Animated Visibility with Stagger
@Composable
fun AnimatedItem(
    delay: Int = 0,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 400,
                delayMillis = delay
            )
        ) + slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(
                durationMillis = 400,
                delayMillis = delay
            )
        ),
        exit = fadeOut() + slideOutVertically(),
        modifier = modifier
    ) {
        content()
    }
}
