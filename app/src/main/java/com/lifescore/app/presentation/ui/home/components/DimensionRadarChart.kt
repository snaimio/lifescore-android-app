package com.lifescore.app.presentation.ui.home.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.*
import com.lifescore.app.domain.model.DimensionType
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DimensionRadarChart(
    dimensionScores: Map<DimensionType, Int>,
    modifier: Modifier = Modifier
) {
    val dimensions = remember { DimensionType.values().toList() }

    // Spring animation for organic expansion feel
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "radarAnimation"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    // Map dimension types to their colors
    val dimensionColors = remember {
        mapOf(
            DimensionType.HEALTH to ColorHealth,
            DimensionType.WEALTH to ColorWealth,
            DimensionType.RELATIONSHIPS to ColorRelationships,
            DimensionType.CAREER to ColorCareer,
            DimensionType.LEARNING to ColorLearning,
            DimensionType.FITNESS to ColorFitness,
            DimensionType.MENTAL_HEALTH to ColorMentalHealth,
            DimensionType.SOCIAL_LIFE to ColorSocialLife
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = (size.minDimension / 2) * 0.68f
                val numAxes = dimensions.size
                val angleStep = (2 * Math.PI / numAxes).toFloat()

                // 1. Draw concentric web rings (25%, 50%, 75%, 100%)
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
                    drawPath(
                        path = gridPath,
                        color = gridColor.copy(alpha = 0.15f + (step * 0.08f)),
                        style = Stroke(
                            width = if (step == 4) 1.5.dp.toPx() else 0.8.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }

                // 2. Draw radial axes with rounded caps
                for (i in 0 until numAxes) {
                    val angle = (i * angleStep - Math.PI / 2).toFloat()
                    val endX = center.x + radius * cos(angle)
                    val endY = center.y + radius * sin(angle)
                    drawLine(
                        color = gridColor.copy(alpha = 0.25f),
                        start = center,
                        end = Offset(endX, endY),
                        strokeWidth = 0.8.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // 3. Draw user score polygon with gradient fill
                val scorePath = Path()
                val points = mutableListOf<Offset>()
                for (i in 0 until numAxes) {
                    val dim = dimensions[i]
                    val score = (dimensionScores[dim] ?: 50) / 100f
                    val animatedScore = score * animatedProgress
                    val angle = (i * angleStep - Math.PI / 2).toFloat()
                    val pointRadius = radius * animatedScore
                    val x = center.x + pointRadius * cos(angle)
                    val y = center.y + pointRadius * sin(angle)
                    points.add(Offset(x, y))
                    if (i == 0) scorePath.moveTo(x, y) else scorePath.lineTo(x, y)
                }
                scorePath.close()

                // Radial gradient fill from primary center to transparent edge
                drawPath(
                    path = scorePath,
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.30f),
                            primaryColor.copy(alpha = 0.08f)
                        ),
                        center = center,
                        radius = radius
                    ),
                    style = Fill
                )

                // Anti-aliased stroke outline
                drawPath(
                    path = scorePath,
                    color = primaryColor.copy(alpha = 0.85f),
                    style = Stroke(
                        width = 2.5.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // 4. Draw vertex dots with dimension colors
                points.forEachIndexed { index, point ->
                    val dimColor = dimensionColors[dimensions[index]] ?: primaryColor
                    // Outer glow ring
                    drawCircle(
                        color = dimColor.copy(alpha = 0.25f),
                        radius = 7.dp.toPx(),
                        center = point
                    )
                    // Solid dot
                    drawCircle(
                        color = dimColor,
                        radius = 4.dp.toPx(),
                        center = point
                    )
                    // White center highlight
                    drawCircle(
                        color = Color.White.copy(alpha = 0.6f),
                        radius = 1.5.dp.toPx(),
                        center = point
                    )
                }
            }

            // Dimension labels positioned around the chart
            dimensions.forEachIndexed { index, dimension ->
                val numAxes = dimensions.size
                val angleStep = (2 * Math.PI / numAxes).toFloat()
                val angle = (index * angleStep - Math.PI / 2).toFloat()
                val labelRadius = 0.88f
                val score = dimensionScores[dimension] ?: 50
                val dimColor = dimensionColors[dimension] ?: primaryColor

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val offsetX = (cos(angle) * labelRadius)
                    val offsetY = (sin(angle) * labelRadius)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(
                                align = Alignment.Center
                            )
                            .offset(
                                x = (offsetX * 110).dp,
                                y = (offsetY * 110).dp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = dimension.displayName.take(6),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = dimColor,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                        Text(
                            text = "${score}%",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = labelColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
