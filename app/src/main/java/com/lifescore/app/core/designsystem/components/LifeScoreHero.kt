package com.lifescore.app.core.designsystem.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.LifeScoreShapes
import com.lifescore.app.core.designsystem.Space

@Composable
fun LifeScoreHero(
    score: Int,
    level: Int,
    currentXp: Int,
    xpToNextLevel: Int = 1000,
    streak: Int,
    userName: String,
    onClick: () -> Unit = {},
    onShare: (() -> Unit)? = null,
    onLeaderboard: (() -> Unit)? = null
) {
    val animatedScore by animateIntAsState(
        targetValue = score,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "heroScoreAnimation"
    )

    val progressFraction = (score.toFloat() / 1000f).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "heroProgressAnimation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF151922)
        ),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFFD4A24C).copy(alpha = 0.35f),
                    Color(0xFF2A334A).copy(alpha = 0.2f)
                )
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF2C2416).copy(alpha = 0.55f),
                            Color(0xFF131720)
                        ),
                        center = Offset(180f, 180f),
                        radius = 450f
                    )
                )
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Circular Arc LifeScore Gauge
                Box(
                    modifier = Modifier
                        .size(175.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                        val strokeWidth = 9.dp.toPx()
                        val arcSize = Size(size.width, size.height)
                        val startAngle = 150f
                        val totalSweep = 240f

                        // Background Arc Track
                        drawArc(
                            color = Color(0xFF2E2922),
                            startAngle = startAngle,
                            sweepAngle = totalSweep,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                            size = arcSize
                        )

                        // Progress Arc
                        drawArc(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFD97706),
                                    Color(0xFFF59E0B),
                                    Color(0xFFFCD34D)
                                )
                            ),
                            startAngle = startAngle,
                            sweepAngle = totalSweep * animatedProgress,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                            size = arcSize
                        )
                    }

                    // Inside Circular Arc
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.offset(y = (-4).dp)
                    ) {
                        Text(
                            text = "LifeScore",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF94A3B8)
                        )

                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.padding(top = 1.dp, bottom = 2.dp)
                        ) {
                            Text(
                                text = animatedScore.toString(),
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "/1000",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                            )
                        }

                        Text(
                            text = "Current Score\n$animatedScore",
                            fontSize = 9.sp,
                            color = Color(0xFF94A3B8),
                            textAlign = TextAlign.Center,
                            lineHeight = 11.sp
                        )

                        Spacer(Modifier.height(4.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF232838),
                            border = BorderStroke(0.5.dp, Color(0xFFF59E0B).copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "Level $level",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFCD34D),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Right: Streak Pill & My Stats Action
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f).padding(start = 12.dp)
                ) {
                    // Gold 14-Day Streak Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Transparent,
                        modifier = Modifier
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFF59E0B),
                                        Color(0xFFE5A83B),
                                        Color(0xFFD97706)
                                    )
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFF1F1206),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "$streak-Day\nStreak 🔥",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1F1206),
                                lineHeight = 13.sp
                            )
                        }
                    }

                    // My Stats Action Button
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF242C3D),
                        border = BorderStroke(1.dp, Color(0xFF374151)),
                        modifier = Modifier.clickable { onClick() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "My Stats",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    if (onLeaderboard != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1A202C).copy(alpha = 0.8f),
                            modifier = Modifier.clickable { onLeaderboard() }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Leaderboard,
                                    contentDescription = null,
                                    tint = Color(0xFFD4A24C),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "Leagues",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEDE7DF)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
