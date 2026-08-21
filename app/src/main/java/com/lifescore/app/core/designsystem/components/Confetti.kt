package com.lifescore.app.core.designsystem.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val color: Color,
    val size: Float,
    val speedY: Float,
    val speedX: Float
)

@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier,
    particleCount: Int = 40
) {
    val colors = listOf(
        Color(0xFF6750A4),
        Color(0xFF00CEC9),
        Color(0xFFFFD700),
        Color(0xFFFF5722),
        Color(0xFFEC4899),
        Color(0xFF4CAF50)
    )

    val particles = remember {
        List(particleCount) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -0.5f,
                color = colors.random(),
                size = Random.nextFloat() * 12f + 6f,
                speedY = Random.nextFloat() * 0.008f + 0.004f,
                speedX = (Random.nextFloat() - 0.5f) * 0.006f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confettiProgress"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { p ->
            val curY = ((p.y + progress * (1f + p.speedY * 100)) % 1.2f) * size.height
            val curX = (p.x + progress * p.speedX * 50) * size.width

            if (curY in 0f..size.height) {
                drawRect(
                    color = p.color,
                    topLeft = Offset(curX, curY),
                    size = Size(p.size, p.size * 1.5f)
                )
            }
        }
    }
}
