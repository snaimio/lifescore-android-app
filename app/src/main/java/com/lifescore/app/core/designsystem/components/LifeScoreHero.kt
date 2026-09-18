package com.lifescore.app.core.designsystem.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.LifeScoreShapes
import com.lifescore.app.core.designsystem.Space
import java.util.Calendar

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
    val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greetingText = remember(currentHour, userName) {
        val prefix = when (currentHour) {
            in 5..11 -> "Good morning"
            in 12..17 -> "Good afternoon"
            in 18..21 -> "Good evening"
            else -> "Night owl mode"
        }
        "$prefix, $userName"
    }

    val animatedScore by animateIntAsState(
        targetValue = score,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(LifeScoreShapes.extraLarge)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF2A2750),      // Deep indigo
                        Color(0xFF3D3A8C),      // Primary
                        Color(0xFF6B4B8C)       // Warm violet
                    ),
                    start = Offset.Zero,
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .padding(Space.xl)
    ) {
        Column {
            // 1. Greeting + Level Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = greetingText,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = LifeScoreShapes.pill,
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Lvl $level",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(Space.lg))

            // 2. Score — The Hero Number
            Text(
                text = "LIFESCORE",
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Black,
                color = Color.White.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(Space.xxs))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = animatedScore.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Spacer(Modifier.width(Space.sm))
                Text(
                    text = "/ 1000",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            Spacer(Modifier.height(Space.md))

            // 3. XP Progress — Thin, Elegant
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { if (xpToNextLevel > 0) (currentXp.toFloat() / xpToNextLevel).coerceIn(0f, 1f) else 0f },
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(LifeScoreShapes.pill),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
                Spacer(Modifier.width(Space.md))
                Text(
                    text = "$currentXp / $xpToNextLevel XP",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }

            Spacer(Modifier.height(Space.md))

            // 4. Streak & Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LifeIcon(
                        icon = LifeIcons.Streak,
                        size = 16.dp
                    )
                    Spacer(Modifier.width(Space.xs))
                    Text(
                        text = "$streak day streak",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(Space.xs)) {
                    if (onLeaderboard != null) {
                        Surface(
                            shape = LifeScoreShapes.pill,
                            color = Color.White.copy(alpha = 0.12f),
                            modifier = Modifier.clickable { onLeaderboard() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Leaderboard,
                                    contentDescription = "Ranks",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "Leagues",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    if (onShare != null) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.12f),
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { onShare() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
