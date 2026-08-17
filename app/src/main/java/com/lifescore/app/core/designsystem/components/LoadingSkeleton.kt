package com.lifescore.app.core.designsystem.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LoadingSkeleton(
    modifier: Modifier = Modifier,
    width: Int = 200,
    height: Int = 20,
    shape: RoundedCornerShape = RoundedCornerShape(4.dp)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton_transition")
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = LinearEasing
            )
        ),
        label = "skeleton_offset"
    )
    
    Box(
        modifier = modifier
            .width(width.dp)
            .height(height.dp)
            .clip(shape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Gray.copy(alpha = 0.2f),
                        Color.Gray.copy(alpha = 0.08f),
                        Color.Gray.copy(alpha = 0.2f)
                    ),
                    startX = offsetX,
                    endX = offsetX + 200f
                )
            )
    )
}
