package com.lifescore.app.presentation.ui.home.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.domain.model.DimensionType

@Composable
fun DimensionCard(
    dimension: DimensionType,
    score: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensionColors = getDimensionColors(dimension)
    val emoji = getDimensionEmoji(dimension)
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = dimensionColors.first().copy(alpha = 0.2f),
                spotColor = dimensionColors.last().copy(alpha = 0.25f)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = dimensionColors
                    )
                )
                .clip(RoundedCornerShape(18.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(emoji, fontSize = 20.sp)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = dimension.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = dimension.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$score%",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (score / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .width(60.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.35f)
                    )
                }
            }
        }
    }
}

@Composable
fun DimensionChipCard(
    dimension: DimensionType,
    score: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensionColors = getDimensionColors(dimension)
    val baseColor = dimensionColors.first()
    val emoji = getDimensionEmoji(dimension)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = baseColor.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .width(135.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(emoji, fontSize = 13.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        dimension.displayName.take(7),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Text(
                    "$score%",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = baseColor
                )
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (score / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = baseColor,
                trackColor = baseColor.copy(alpha = 0.2f)
            )
        }
    }
}

// 🎨 Dimension Gradient Colors
fun getDimensionColors(dimension: DimensionType): List<Color> {
    return when (dimension) {
        DimensionType.HEALTH -> listOf(Color(0xFF43A047), Color(0xFF66BB6A))
        DimensionType.WEALTH -> listOf(Color(0xFFFB8C00), Color(0xFFFFA726))
        DimensionType.RELATIONSHIPS -> listOf(Color(0xFFE91E63), Color(0xFFF06292))
        DimensionType.CAREER -> listOf(Color(0xFF1E88E5), Color(0xFF42A5F5))
        DimensionType.LEARNING -> listOf(Color(0xFF7E57C2), Color(0xFF9575CD))
        DimensionType.FITNESS -> listOf(Color(0xFFF4511E), Color(0xFFFF7043))
        DimensionType.MENTAL_HEALTH -> listOf(Color(0xFF00ACC1), Color(0xFF26C6DA))
        DimensionType.SOCIAL_LIFE -> listOf(Color(0xFF00897B), Color(0xFF26A69A))
    }
}
