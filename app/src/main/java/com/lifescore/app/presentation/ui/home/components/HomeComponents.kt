package com.lifescore.app.presentation.ui.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.*
import com.lifescore.app.domain.model.DimensionType

fun getDimensionEmoji(dimension: DimensionType): String {
    return when (dimension) {
        DimensionType.HEALTH -> "🏃"
        DimensionType.WEALTH -> "💰"
        DimensionType.RELATIONSHIPS -> "💖"
        DimensionType.CAREER -> "💼"
        DimensionType.LEARNING -> "🧠"
        DimensionType.FITNESS -> "⚡"
        DimensionType.MENTAL_HEALTH -> "🧘"
        DimensionType.SOCIAL_LIFE -> "🤝"
    }
}

@Composable
fun LifeScoreCard(
    score: Int,
    level: Int,
    streak: Int,
    levelProgress: Float,
    title: String
) {
    val animatedProgress by animateFloatAsState(
        targetValue = levelProgress,
        animationSpec = tween(1000),
        label = "levelProgress"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color(0xFF6C63FF).copy(alpha = 0.35f),
                spotColor = Color(0xFF03DAC6).copy(alpha = 0.25f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(heroCardGradient)
            .padding(22.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "OVERALL LIFESCORE INDEX",
                    style = MaterialTheme.typography.labelMedium,
                    letterSpacing = 1.8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.25f)
                ) {
                    Text(
                        text = "🔥 ${streak}d Streak",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = Color(0xFFFFD54F),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "$score",
                fontSize = 62.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = (-1.5).sp
            )

            Text(
                text = "Level $level • $title",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(Modifier.height(16.dp))

            // Level Progress with glowing bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Level $level Progress",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                    Text(
                        text = "${(levelProgress * 100).toInt()}% to Level ${level + 1}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape),
                    color = Color(0xFF03DAC6),
                    trackColor = Color.White.copy(alpha = 0.25f)
                )
            }
        }
    }
}

@Composable
fun DailyProgressBar(
    progress: Float,
    tasksCompleted: Int,
    totalTasks: Int
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800),
        label = "dailyProgress"
    )

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎯", fontSize = 16.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Daily Goal Progress",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                ) {
                    Text(
                        "$tasksCompleted / $totalTasks Done",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
        }
    }
}

@Composable
fun DimensionCard(
    dimension: DimensionType,
    score: Int,
    onClick: () -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = score / 100f,
        animationSpec = tween(900),
        label = "dimProgress"
    )
    val baseColor = Color(dimension.baseColorHex)
    val emoji = getDimensionEmoji(dimension)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = baseColor.copy(alpha = 0.07f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = baseColor.copy(alpha = 0.16f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = emoji, fontSize = 20.sp)
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dimension.displayName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = dimension.description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1
                )
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    color = baseColor,
                    trackColor = baseColor.copy(alpha = 0.18f),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(6.dp)
                        .clip(CircleShape)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$score%",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = baseColor
                )
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = baseColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun TaskCard(
    task: com.lifescore.app.domain.model.LifeTask,
    onToggle: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val baseColor = Color(task.dimension.baseColorHex)
    val cardBg by animateColorAsState(
        targetValue = if (task.isCompleted)
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        else
            MaterialTheme.colorScheme.surface,
        label = "taskBg"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onToggle()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (task.isCompleted) 0.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Dimension Accent Bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(baseColor)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Interactive Checkbox
                Surface(
                    shape = CircleShape,
                    color = if (task.isCompleted) baseColor else baseColor.copy(alpha = 0.12f),
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onToggle()
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (task.isCompleted) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        style = if (task.isCompleted)
                            MaterialTheme.typography.bodyMedium.copy(
                                textDecoration = TextDecoration.LineThrough,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        else
                            MaterialTheme.typography.bodyMedium
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = getDimensionEmoji(task.dimension),
                            fontSize = 11.sp
                        )
                        Text(
                            text = task.dimension.displayName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = baseColor
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "+${task.pointsReward} XP",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
