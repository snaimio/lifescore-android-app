package com.lifescore.app.presentation.ui.share

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lifescore.app.core.designsystem.LifeGradients
import com.lifescore.app.core.designsystem.LifeScoreShapes
import com.lifescore.app.core.designsystem.Space
import com.lifescore.app.core.designsystem.components.LifeIcon
import com.lifescore.app.core.designsystem.components.LifeIcons
import com.lifescore.app.core.designsystem.components.LifeIllustration
import com.lifescore.app.core.designsystem.components.LifeIllustrations
import com.lifescore.app.domain.model.HeroArchetype

enum class StoryCardType {
    ARCHETYPE_REVEAL,
    WEEK_STREAK,
    MONTH_PROGRESS,
    ACHIEVEMENT_UNLOCK
}

data class StoryCardData(
    val cardType: StoryCardType = StoryCardType.ARCHETYPE_REVEAL,
    val userName: String = "Architect",
    val archetype: HeroArchetype = HeroArchetype.ARCHITECT,
    val score: Int = 720,
    val beforeScore: Int = 540,
    val streakDays: Int = 7,
    val milestoneTitle: String = "7-Day Flame",
    val primaryStrength: String = "Scalable Systems & Structural Order"
)

@Composable
fun ShareStoryCardDialog(
    data: StoryCardData,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(Space.md),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Space.md)
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "9:16 Instagram Story Preview",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                // 9:16 Story Card Container (Simulated Instagram Story Canvas)
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF0F0E0C),
                    border = BorderStroke(1.5.dp, Color(0x33D4A24C)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(9f / 15f)
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF262466),
                                        Color(0xFF0F0E0C),
                                        Color(0xFF1E1A17)
                                    )
                                )
                            )
                            .padding(Space.lg)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // 1. Top Brand Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Space.xs)
                                ) {
                                    LifeIcon(LifeIcons.Star, size = 18.dp, tint = Color(0xFFD4A24C))
                                    Text(
                                        "LIFESCORE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            letterSpacing = 2.sp,
                                            fontWeight = FontWeight.Black
                                        ),
                                        color = Color(0xFFFBF8F3)
                                    )
                                }

                                Surface(
                                    shape = LifeScoreShapes.tag,
                                    color = Color(0x22D4A24C)
                                ) {
                                    Text(
                                        "DAY ${data.streakDays}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD4A24C),
                                        modifier = Modifier.padding(horizontal = Space.xs, vertical = 2.dp)
                                    )
                                }
                            }

                            // 2. Central Story Payload
                            when (data.cardType) {
                                StoryCardType.ARCHETYPE_REVEAL -> {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(Space.sm)
                                    ) {
                                        LifeIllustration(
                                            illustration = data.archetype.getIllustration(),
                                            size = 110.dp
                                        )

                                        Text(
                                            "I'm ${data.archetype.displayName}",
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color(0xFFFBF8F3),
                                            textAlign = TextAlign.Center
                                        )

                                        Text(
                                            data.archetype.title,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color(0xFFD4A24C),
                                            fontWeight = FontWeight.Bold
                                        )

                                        Spacer(Modifier.height(Space.xs))

                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = Color(0x20FBF8F3),
                                            border = BorderStroke(1.dp, Color(0x30D4A24C))
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(horizontal = Space.lg, vertical = Space.sm),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    "ESTIMATED LIFESCORE",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                    color = Color(0xFFFBF8F3).copy(alpha = 0.7f),
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    "${data.score}",
                                                    style = MaterialTheme.typography.displaySmall.copy(
                                                        fontFamily = FontFamily.Serif,
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    color = Color(0xFFFBF8F3)
                                                )
                                                Text(
                                                    "Top Strength: ${data.primaryStrength}",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                                    color = Color(0xFFD4A24C),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }

                                StoryCardType.WEEK_STREAK -> {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(Space.sm)
                                    ) {
                                        LifeIcon(LifeIcons.Streak, size = 64.dp)
                                        Text(
                                            "7 Days. 8 Dimensions. 1 Life.",
                                            style = MaterialTheme.typography.headlineSmall.copy(
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color(0xFFFBF8F3),
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            "Compounding daily consistency unlocked.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFFFBF8F3).copy(alpha = 0.8f)
                                        )
                                    }
                                }

                                StoryCardType.MONTH_PROGRESS -> {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(Space.sm)
                                    ) {
                                        LifeIcon(LifeIcons.Analytics, size = 64.dp)
                                        Text(
                                            "30 Days of Evolution",
                                            style = MaterialTheme.typography.headlineSmall.copy(
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color(0xFFFBF8F3)
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(Space.md),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("${data.beforeScore}", style = MaterialTheme.typography.titleLarge, color = Color(0x88FBF8F3))
                                            Text("→", style = MaterialTheme.typography.titleLarge, color = Color(0xFFD4A24C))
                                            Text("${data.score} LifeScore", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFFFBF8F3))
                                        }
                                    }
                                }

                                StoryCardType.ACHIEVEMENT_UNLOCK -> {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(Space.sm)
                                    ) {
                                        LifeIllustration(LifeIllustrations.Celebration, size = 96.dp)
                                        Text(
                                            data.milestoneTitle,
                                            style = MaterialTheme.typography.headlineSmall.copy(
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color(0xFFFBF8F3)
                                        )
                                        Text(
                                            "Milestone accolade achieved.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFFD4A24C)
                                        )
                                    }
                                }
                            }

                            // 3. Bottom Soft CTA & Watermark
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    "What's your LifeScore?",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFFFBF8F3)
                                )
                                Text(
                                    "lifescore.app • 60-sec assessment",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = Color(0xFFFBF8F3).copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }

                // Share Button Action
                Button(
                    onClick = {
                        shareStoryToInstagram(context, data)
                    },
                    shape = LifeScoreShapes.button,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE08556),
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(Space.xs))
                    Text("Share to Story / Squad", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun shareStoryToInstagram(context: Context, data: StoryCardData) {
    val message = "I'm ${data.archetype.displayName} on LifeScore with a ${data.score} baseline! Discover your archetype in 60 seconds: https://lifescore.app"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "My LifeScore Archetype")
        putExtra(Intent.EXTRA_TEXT, message)
    }
    context.startActivity(Intent.createChooser(intent, "Share Your Archetype"))
}
