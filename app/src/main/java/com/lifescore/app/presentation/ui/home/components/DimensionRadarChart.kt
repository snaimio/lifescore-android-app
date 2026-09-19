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
import com.lifescore.app.domain.model.DimensionType
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DimensionRadarChart(
    dimensionScores: Map<DimensionType, Int>,
    modifier: Modifier = Modifier
) {
    // Exact 8-Dimension Ordering matching Screenshot 2
    val dimensions = remember {
        listOf(
            DimensionType.HEALTH,
            DimensionType.WEALTH,
            DimensionType.RELATIONSHIPS,
            DimensionType.CAREER,
            DimensionType.LEARNING,
            DimensionType.FITNESS,
            DimensionType.MENTAL_HEALTH,
            DimensionType.SOCIAL_LIFE
        )
    }

    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "radarAnimation"
    )

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val radarPurple = Color(0xFFA855F7)
    val radarGlowPurple = Color(0xFFC084FC)
    val gridLineColor = if (isDark) Color(0xFF475569).copy(alpha = 0.4f) else Color(0xFFCBD5E1)
    val labelTextColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF19181F)
    val scoreTextColor = if (isDark) Color(0xFFCBD5E1) else Color(0xFF64748B)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.02f)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            val chartRadius = (maxWidth.coerceAtMost(maxHeight) / 2) * 0.62f
            val labelRadiusDist = (maxWidth.coerceAtMost(maxHeight) / 2) * 0.84f

            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = chartRadius.toPx()
                val numAxes = dimensions.size
                val angleStep = (2 * Math.PI / numAxes).toFloat()

                // 1. Draw 5 concentric web rings (20%, 40%, 60%, 80%, 100%)
                for (step in 1..5) {
                    val stepRadius = radius * (step / 5f)
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
                        color = if (step == 5) gridLineColor.copy(alpha = 0.6f) else gridLineColor.copy(alpha = 0.25f),
                        style = Stroke(
                            width = if (step == 5) 1.2.dp.toPx() else 0.8.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }

                // 2. Draw 8 radial spokes
                for (i in 0 until numAxes) {
                    val angle = (i * angleStep - Math.PI / 2).toFloat()
                    val endX = center.x + radius * cos(angle)
                    val endY = center.y + radius * sin(angle)
                    drawLine(
                        color = gridLineColor.copy(alpha = 0.35f),
                        start = center,
                        end = Offset(endX, endY),
                        strokeWidth = 0.8.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // 3. Draw User Score Radar Polygon with glowing purple gradient
                val scorePath = Path()
                val points = mutableListOf<Offset>()
                for (i in 0 until numAxes) {
                    val dim = dimensions[i]
                    val score = (dimensionScores[dim] ?: 80).coerceIn(10, 100) / 100f
                    val animatedScore = score * animatedProgress
                    val angle = (i * angleStep - Math.PI / 2).toFloat()
                    val pointRadius = radius * animatedScore
                    val x = center.x + pointRadius * cos(angle)
                    val y = center.y + pointRadius * sin(angle)
                    points.add(Offset(x, y))
                    if (i == 0) scorePath.moveTo(x, y) else scorePath.lineTo(x, y)
                }
                scorePath.close()

                // Radial Fill
                drawPath(
                    path = scorePath,
                    brush = Brush.radialGradient(
                        colors = listOf(
                            radarGlowPurple.copy(alpha = 0.45f),
                            radarPurple.copy(alpha = 0.25f),
                            Color(0xFF581C87).copy(alpha = 0.08f)
                        ),
                        center = center,
                        radius = radius
                    ),
                    style = Fill
                )

                // Neon Stroke Outline
                drawPath(
                    path = scorePath,
                    color = if (isDark) Color(0xFFE9D5FF) else Color(0xFF9333EA),
                    style = Stroke(
                        width = 2.2.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // 4. Draw glowing vertex dots
                points.forEach { point ->
                    drawCircle(
                        color = radarGlowPurple.copy(alpha = 0.4f),
                        radius = 6.5.dp.toPx(),
                        center = point
                    )
                    drawCircle(
                        color = if (isDark) Color.White else Color(0xFF7E22CE),
                        radius = 3.5.dp.toPx(),
                        center = point
                    )
                }
            }

            // Dimension labels & percentages positioned around chart
            dimensions.forEachIndexed { index, dimension ->
                val numAxes = dimensions.size
                val angleStep = (2 * Math.PI / numAxes).toFloat()
                val angle = (index * angleStep - Math.PI / 2).toFloat()
                val score = dimensionScores[dimension] ?: 80

                val offsetX = (cos(angle) * labelRadiusDist.value).dp
                val offsetY = (sin(angle) * labelRadiusDist.value).dp

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .wrapContentSize(align = Alignment.Center)
                            .offset(x = offsetX, y = offsetY),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = dimension.displayName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = labelTextColor,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            softWrap = false
                        )
                        Text(
                            text = "${score}%",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = scoreTextColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
