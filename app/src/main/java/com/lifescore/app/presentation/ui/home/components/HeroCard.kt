package com.lifescore.app.presentation.ui.home.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.LifeScoreColors
import com.lifescore.app.core.designsystem.components.AnimatedScoreCounter

@Composable
fun HeroCard(
    score: Int,
    level: Int,
    currentXp: Int,
    xpToNextLevel: Int,
    streak: Int,
    userName: String,
    onShare: () -> Unit,
    onLeaderboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = LifeScoreColors.Primary.copy(alpha = 0.35f),
                spotColor = LifeScoreColors.PrimaryDark.copy(alpha = 0.40f)
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            LifeScoreColors.Primary,
                            LifeScoreColors.PrimaryDark
                        )
                    )
                )
                .clip(RoundedCornerShape(22.dp))
        ) {
            // Decorative background radial shapes
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(200.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.12f),
                                Color.Transparent
                            ),
                            radius = 200f
                        )
                    )
            )
            
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(150.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.06f),
                                Color.Transparent
                            ),
                            radius = 150f
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                // Top row: Greeting + Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hello, $userName 👋",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.95f)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = Color(0xFF66FFF0)
                            )
                            Text(
                                text = "Level $level",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = onLeaderboard,
                            modifier = Modifier
                                .size(38.dp)
                                .background(
                                    Color.White.copy(alpha = 0.2f),
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Leaderboard,
                                contentDescription = "Leaderboard",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = onShare,
                            modifier = Modifier
                                .size(38.dp)
                                .background(
                                    Color.White.copy(alpha = 0.2f),
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Score + Streak
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "YOUR LIFESCORE",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                        AnimatedScoreCounter(
                            score = score,
                            textStyle = MaterialTheme.typography.displayLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 56.sp
                            )
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.Black.copy(alpha = 0.25f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("🔥", fontSize = 15.sp)
                                Text(
                                    text = "$streak days",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Color(0xFFFFD54F),
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Active Streak",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // XP Progress
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "XP Progress to Next Level",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "$currentXp / ${if (xpToNextLevel > 0) xpToNextLevel else 1000} XP",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    val progressFraction = if (xpToNextLevel > 0) (currentXp.toFloat() / xpToNextLevel).coerceIn(0f, 1f) else 0.5f
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF03DAC6),
                        trackColor = Color.White.copy(alpha = 0.25f)
                    )
                }
            }
        }
    }
}
