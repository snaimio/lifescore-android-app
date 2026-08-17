package com.lifescore.app.core.designsystem.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

// 🎯 Animated Score Counter
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedScoreCounter(
    score: Int,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.displayLarge
) {
    AnimatedContent(
        targetState = score,
        transitionSpec = {
            if (targetState > initialState) {
                slideInVertically { height -> height } + fadeIn() togetherWith
                slideOutVertically { height -> -height } + fadeOut()
            } else {
                slideInVertically { height -> -height } + fadeIn() togetherWith
                slideOutVertically { height -> height } + fadeOut()
            }.using(
                SizeTransform(clip = false)
            )
        },
        modifier = modifier,
        label = "score_counter"
    ) { targetScore ->
        Text(
            text = targetScore.toString(),
            style = textStyle
        )
    }
}

// 🔄 Animated Progress Ring
@Composable
fun AnimatedProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Float = 8f,
    size: Int = 80
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ring_transition")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "ring_rotation"
    )
    
    Box(modifier = modifier.size(size.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.dp.toPx() / 2
            val centerY = size.dp.toPx() / 2
            val radius = size.dp.toPx() / 2 - strokeWidth.dp.toPx() / 2
            
            // Background ring
            drawArc(
                color = color.copy(alpha = 0.2f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth.dp.toPx())
            )
            
            // Progress ring
            val sweepAngle = progress * 360f
            drawArc(
                color = color,
                startAngle = -90f + rotation,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(
                    width = strokeWidth.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }
    }
}

// 🌟 Animated Gradient Card
@Composable
fun AnimatedGradientCard(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "grad_transition")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing)
        ),
        label = "grad_offset"
    )
    
    val brush = Brush.horizontalGradient(
        colors = colors,
        startX = offset * 100f,
        endX = offset * 100f + 200f
    )
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush)
                .padding(16.dp)
        ) {
            content()
        }
    }
}

// 💫 Staggered Animation for Lists
@Composable
fun <T> StaggeredList(
    items: List<T>,
    key: (T) -> Any,
    content: @Composable (T) -> Unit
) {
    Column {
        items.forEachIndexed { index, item ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(
                        durationMillis = 500,
                        delayMillis = index * 50
                    )
                )
            ) {
                content(item)
            }
        }
    }
}

// 🔔 Haptic Feedback Button
@Composable
fun HapticButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    Button(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier,
        enabled = enabled
    ) {
        content()
    }
}
