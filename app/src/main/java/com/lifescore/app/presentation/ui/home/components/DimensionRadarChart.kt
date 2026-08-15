package com.lifescore.app.presentation.ui.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.domain.model.DimensionType
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DimensionRadarChart(
    dimensionScores: Map<DimensionType, Int>,
    modifier: Modifier = Modifier
) {
    val dimensions = remember { DimensionType.values().toList() }
    val animatedProgress = animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1000),
        label = "radarAnimation"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(240.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = (size.minDimension / 2) * 0.85f
                val numAxes = dimensions.size
                val angleStep = (2 * Math.PI / numAxes).toFloat()

                // 1. Draw Web/Grid Circles (25%, 50%, 75%, 100%)
                for (step in 1..4) {
                    val stepRadius = radius * (step / 4f)
                    val gridPath = Path()
                    for (i in 0 until numAxes) {
                        val angle = (i * angleStep - Math.PI / 2).toFloat()
                        val x = center.x + stepRadius * cos(angle)
                        val y = center.y + stepRadius * sin(angle)
                        if (i == 0) gridPath.moveTo(x, y) else gridPath.lineTo(x, y)
                    }
                    gridPath.close()
                    drawPath(path = gridPath, color = gridColor, style = Stroke(width = 1.dp.toPx()))
                }

                // 2. Draw Radial Axes
                for (i in 0 until numAxes) {
                    val angle = (i * angleStep - Math.PI / 2).toFloat()
                    val endX = center.x + radius * cos(angle)
                    val endY = center.y + radius * sin(angle)
                    drawLine(
                        color = gridColor,
                        start = center,
                        end = Offset(endX, endY),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // 3. Draw User Score Polygon
                val scorePath = Path()
                val points = mutableListOf<Offset>()
                for (i in 0 until numAxes) {
                    val dim = dimensions[i]
                    val score = (dimensionScores[dim] ?: 50) / 100f
                    val animatedScore = score * animatedProgress.value
                    val angle = (i * angleStep - Math.PI / 2).toFloat()
                    val pointRadius = radius * animatedScore
                    val x = center.x + pointRadius * cos(angle)
                    val y = center.y + pointRadius * sin(angle)
                    points.add(Offset(x, y))

                    if (i == 0) scorePath.moveTo(x, y) else scorePath.lineTo(x, y)
                }
                scorePath.close()

                // Fill with translucent primary color
                drawPath(path = scorePath, color = primaryColor.copy(alpha = 0.25f), style = Fill)
                // Stroke outline
                drawPath(path = scorePath, color = primaryColor, style = Stroke(width = 2.5.dp.toPx()))

                // Draw vertex points
                points.forEachIndexed { index, point ->
                    val dimColor = Color(dimensions[index].baseColorHex)
                    drawCircle(color = dimColor, radius = 4.dp.toPx(), center = point)
                }
            }
        }
    }
}
