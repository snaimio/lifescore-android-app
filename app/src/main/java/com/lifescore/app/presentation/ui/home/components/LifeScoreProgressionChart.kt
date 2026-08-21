package com.lifescore.app.presentation.ui.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard

@Composable
fun LifeScoreProgressionCard(
    currentScore: Int,
    targetScore: Int = 1000,
    historicalScores: List<Int> = listOf(520, 580, 610, 670, 710, 750, 780),
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📈", fontSize = 18.sp)
                        Spacer(Modifier.width(Spacing.xs))
                        Text(
                            "LifeScore Trajectory",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        "30-Day Growth & Psychometric Projection",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "+260 pts (+50%)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(Spacing.md))

            // Progression Canvas Chart
            val primaryColor = MaterialTheme.colorScheme.primary
            val tertiaryColor = MaterialTheme.colorScheme.tertiary

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val padding = 20f

                    val scores = if (historicalScores.size >= 2) historicalScores else listOf(500, 600, 700, 780)
                    val minVal = (scores.minOrNull() ?: 500) - 50
                    val maxVal = (scores.maxOrNull() ?: 800) + 50
                    val range = (maxVal - minVal).coerceAtLeast(1)

                    val stepX = (width - padding * 2) / (scores.size - 1)

                    val points = scores.mapIndexed { index, score ->
                        val x = padding + index * stepX
                        val normalizedY = (score - minVal).toFloat() / range.toFloat()
                        val y = height - padding - (normalizedY * (height - padding * 2))
                        Offset(x, y)
                    }

                    // Draw gradient fill under curve
                    val fillPath = Path().apply {
                        moveTo(points.first().x, height)
                        points.forEach { lineTo(it.x, it.y) }
                        lineTo(points.last().x, height)
                        close()
                    }

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.35f),
                                primaryColor.copy(alpha = 0.02f)
                            )
                        )
                    )

                    // Draw progression curve line
                    val linePath = Path().apply {
                        moveTo(points.first().x, points.first().y)
                        for (i in 0 until points.size - 1) {
                            val p0 = points[i]
                            val p1 = points[i + 1]
                            val midX = (p0.x + p1.x) / 2f
                            cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
                        }
                    }

                    drawPath(
                        path = linePath,
                        brush = Brush.horizontalGradient(
                            colors = listOf(primaryColor, Color(0xFF00CEC9), tertiaryColor)
                        ),
                        style = Stroke(width = 6f, cap = StrokeCap.Round)
                    )

                    // Draw data nodes
                    points.forEachIndexed { idx, point ->
                        val isLast = idx == points.size - 1
                        drawCircle(
                            color = if (isLast) tertiaryColor else primaryColor,
                            radius = if (isLast) 8f else 5f,
                            center = point
                        )
                        if (isLast) {
                            drawCircle(
                                color = Color.White,
                                radius = 4f,
                                center = point
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(Spacing.xs))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Day 1 (Baseline)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Text("Day 15", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Text("Current: $currentScore", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("Target: $targetScore", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
            }
        }
    }
}
