package com.lifescore.app.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Reusable Glassmorphism card with translucent background, border highlight, and soft shadow.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    elevation: Dp = 6.dp,
    borderWidth: Dp = 1.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val fillBrush = if (isDark) GlassFillDark else GlassFillLight
    val borderColor = if (isDark) GlassBorderDark else GlassBorderLight

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = if (isDark) Color.Black.copy(alpha = 0.5f) else Color(0xFF6C63FF).copy(alpha = 0.08f),
                spotColor = if (isDark) Color.Black.copy(alpha = 0.6f) else Color(0xFF6C63FF).copy(alpha = 0.12f)
            )
            .clip(shape)
            .background(fillBrush)
            .border(width = borderWidth, color = borderColor, shape = shape)
            .padding(16.dp),
        content = content
    )
}

/**
 * Reusable Gradient container for hero metrics and highlight banners.
 */
@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    gradient: Brush = heroCardGradient,
    shape: Shape = RoundedCornerShape(24.dp),
    elevation: Dp = 8.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = Color(0xFF6C63FF).copy(alpha = 0.25f),
                spotColor = Color(0xFF03DAC6).copy(alpha = 0.20f)
            )
            .clip(shape)
            .background(gradient)
            .padding(20.dp),
        content = content
    )
}
