package com.lifescore.app.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    elevation: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val shape = RoundedCornerShape(cornerRadius)
    
    Box(
        modifier = modifier
            .shadow(elevation, shape = shape)
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isDark) {
                        listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color.White.copy(alpha = 0.02f)
                        )
                    } else {
                        listOf(
                            Color.White.copy(alpha = 0.85f),
                            Color.White.copy(alpha = 0.65f)
                        )
                    }
                )
            )
            .border(
                width = 1.dp,
                color = if (isDark) {
                    Color.White.copy(alpha = 0.12f)
                } else {
                    Color.White.copy(alpha = 0.40f)
                },
                shape = shape
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            content()
        }
    }
}
