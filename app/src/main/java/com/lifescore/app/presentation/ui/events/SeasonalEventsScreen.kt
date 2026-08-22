package com.lifescore.app.presentation.ui.events

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.core.engine.SeasonalEventsEngine
import com.lifescore.app.core.engine.SeasonalQuest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonalEventsScreen(
    onNavigateBack: () -> Unit,
    onOpenBossRaid: () -> Unit
) {
    val context = LocalContext.current
    val event = remember { SeasonalEventsEngine.activeEvent }
    val completedQuests = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text("☀️ Seasonal Live Event", fontWeight = FontWeight.Bold)
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // 1. Hero Event Banner
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(event.themeColorHex).copy(alpha = 0.15f),
                    border = BorderStroke(1.5.dp, Color(event.themeColorHex).copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(event.themeColorHex)
                            ) {
                                Text(
                                    "⏳ ${event.daysRemaining} DAYS REMAINING",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    "🔥 2X XP ACTIVE",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(Spacing.sm))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(event.bannerEmoji, fontSize = 36.sp)
                            Spacer(Modifier.width(Spacing.sm))
                            Column {
                                Text(event.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                                Text(event.subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Spacer(Modifier.height(Spacing.xs))
                        Text(event.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            // 2. Community Boss Health Bar
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("GLOBAL COMMUNITY RAID", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(event.bossName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = onOpenBossRaid,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Fight Boss ⚔️", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(Modifier.height(Spacing.sm))

                        val healthPercentage = event.bossCurrentHp.toFloat() / event.bossMaxHp.toFloat()
                        LinearProgressIndicator(
                            progress = { healthPercentage },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = Color(0xFFE53935),
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${event.bossCurrentHp} / ${event.bossMaxHp} HP Remaining", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${(healthPercentage * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE53935))
                        }
                    }
                }
            }

            // 3. Event Exclusive Quests
            item {
                Text(
                    "Event Quests (2x Rewards)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(event.eventQuests) { quest ->
                val isDone = completedQuests.contains(quest.id)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isDone) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, if (isDone) Color(0xFF81C784) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.md),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(quest.dimension.baseColorHex).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        "${quest.dimension.displayName} • +${quest.xpReward} XP • +${quest.gemReward} 💎",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(quest.dimension.baseColorHex),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(quest.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(quest.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Spacer(Modifier.width(Spacing.sm))

                        Button(
                            onClick = {
                                if (!isDone) {
                                    completedQuests.add(quest.id)
                                    Toast.makeText(context, "Completed! +${quest.xpReward} XP & +${quest.gemReward} Gems! ☀️", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDone) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                            ),
                            enabled = !isDone
                        ) {
                            Text(if (isDone) "Done ✅" else "Claim", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 4. Exclusive Event Reward Drop
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(Spacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(event.exclusiveRewardEmoji, fontSize = 32.sp)
                        Spacer(Modifier.width(Spacing.md))
                        Column {
                            Text("SEASON REWARD UNLOCK", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(event.exclusiveRewardName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Complete all 4 event quests to unlock before the season ends.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(Spacing.xl))
            }
        }
    }
}
