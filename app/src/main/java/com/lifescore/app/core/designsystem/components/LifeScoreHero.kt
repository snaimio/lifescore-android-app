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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LifeScoreHero(
    score: Int,
    level: Int = 1,
    currentXp: Int = 0,
    xpToNextLevel: Int = 1000,
    streak: Int,
    userName: String = "",
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

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

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
            containerColor = if (isDark) Color(0xFF13121D) else Color(0xFFFFFFFF)
        ),
        border = if (isDark) {
            BorderStroke(
                1.dp,
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFD4A24C).copy(alpha = 0.45f),
                        Color(0xFF2E2B3E).copy(alpha = 0.6f),
                        Color(0xFFD4A24C).copy(alpha = 0.2f)
                    )
                )
            )
        } else {
            BorderStroke(1.dp, Color(0xFFE8D5B5))
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = if (isDark) {
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF261F13).copy(alpha = 0.7f),
                                Color(0xFF100F18)
                            ),
                            center = Offset(180f, 180f),
                            radius = 450f
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFF9EE),
                                Color(0xFFFDF1EC),
                                Color(0xFFF3F1FA)
                            )
                        )
                    }
                )
                .padding(horizontal = 18.dp, vertical = 22.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Circular Arc LifeScore Gauge
                Box(
                    modifier = Modifier
                        .size(170.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                        val strokeWidth = 8.5.dp.toPx()
                        val arcSize = Size(size.width, size.height)
                        val startAngle = 150f
                        val totalSweep = 240f

                        // Background Arc Track
                        drawArc(
                            color = if (isDark) Color(0xFF221F2C) else Color(0xFFEADBCE),
                            startAngle = startAngle,
                            sweepAngle = totalSweep,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                            size = arcSize
                        )

                        // Progress Arc
                        drawArc(
                            brush = Brush.horizontalGradient(
                                colors = if (isDark) {
                                    listOf(
                                        Color(0xFFB38230),
                                        Color(0xFFD4A24C),
                                        Color(0xFFFDE68A)
                                    )
                                } else {
                                    listOf(
                                        Color(0xFFE08556),
                                        Color(0xFFD4A24C),
                                        Color(0xFF6BA89C)
                                    )
                                }
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
                            text = "LIFESCORE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD4A24C),
                            letterSpacing = 1.2.sp
                        )

                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.padding(top = 1.dp, bottom = 2.dp)
                        ) {
                            Text(
                                text = animatedScore.toString(),
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isDark) Color(0xFFFBF8F3) else Color(0xFF19181F)
                            )
                            Text(
                                text = "/1000",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFF9E9AA8) else Color(0xFF6B6678),
                                modifier = Modifier.padding(bottom = 5.dp, start = 2.dp)
                            )
                        }

                        Text(
                            text = "Daily Index",
                            fontSize = 10.sp,
                            color = if (isDark) Color(0xFF9E9AA8) else Color(0xFF6B6678),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Right: Streak Pill & View Stats Action
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    // Gold Streak Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Transparent,
                        modifier = Modifier
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFD4A24C),
                                        Color(0xFFE5B869)
                                    )
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFF1B1408),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                text = "$streak-Day Streak",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B1408),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    // View Stats Action Button
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isDark) Color(0xFF1D1B2B) else Color(0xFFFFFFFF),
                        border = BorderStroke(1.dp, if (isDark) Color(0x35D4A24C) else Color(0x50D4A24C)),
                        modifier = Modifier.clickable { onClick() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "View Stats",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFFBF8F3) else Color(0xFF19181F),
                                maxLines = 1,
                                softWrap = false
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color(0xFFD4A24C),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

