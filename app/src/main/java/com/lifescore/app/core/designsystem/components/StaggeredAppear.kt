package com.lifescore.app.core.designsystem.components

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import kotlinx.coroutines.delay

@Composable
fun StaggeredAppear(
    index: Int,
    delayMultiplier: Long = 40L,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(index * delayMultiplier)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(400)) + slideInVertically(
            initialOffsetY = { it / 6 },
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        ),
        exit = fadeOut(tween(200))
    ) {
        content()
    }
}
