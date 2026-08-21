package com.lifescore.app.presentation.combat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.*
import com.lifescore.app.core.designsystem.components.*
import com.lifescore.app.domain.model.CombatBoss
import com.lifescore.app.presentation.ui.home.components.getDimensionEmoji

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CombatScreen(
    viewModel: CombatViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val boss = uiState.selectedBoss

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⚔️", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("Dimension Boss Raids", fontWeight = FontWeight.Black)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
        ) {
            // Hero Status Bar
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(14.dp, shape = RoundedCornerShape(22.dp))
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFFB71C1C), Color(0xFFC2185B), Color(0xFFFF5252))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "COMBAT POWER DEPLOYMENT",
                                style = MaterialTheme.typography.labelMedium,
                                letterSpacing = 1.8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.Black.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    "Your CP: ${uiState.characterStats.combatPower}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Overcome Life Obstacles",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            "Channel your daily task discipline and RPG character stats to slay procrastination, burnout, and mental friction.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Boss Selector Carousel
            item {
                Text("Select Target Dungeon Boss", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(uiState.bosses, key = { it.id }) { b ->
                        val isSelected = b.id == boss?.id
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            ),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier
                                .width(180.dp)
                                .clickable { viewModel.selectBoss(b) }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(b.avatarEmoji, fontSize = 28.sp)
                                    if (b.isDefeated) {
                                        Text("🏆 Defeated", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                                    } else {
                                        Text("${(b.hpPercentage * 100).toInt()}% HP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF5252))
                                    }
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(b.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                                Text(b.title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, maxLines = 1)
                            }
                        }
                    }
                }
            }

            // Active Battle Stage
            if (boss != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(boss.avatarEmoji, fontSize = 56.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(boss.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                            Text("${boss.title} • ${getDimensionEmoji(boss.dimension)} ${boss.dimension.displayName}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)

                            Spacer(Modifier.height(16.dp))

                            // HP Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Boss Health (HP)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                Text("${boss.currentHp} / ${boss.maxHp} HP", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFFFF5252))
                            }

                            Spacer(Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { boss.hpPercentage },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(CircleShape),
                                color = if (boss.hpPercentage > 0.5f) Color(0xFFFF5252) else Color(0xFFFF1744),
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            Spacer(Modifier.height(20.dp))

                            // Rewards Preview
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                                    Text("🎁 +${boss.rewardXp} XP", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                                }
                                Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                                    Text("⭐ +${boss.rewardStatPoints} Stat Points", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                                }
                            }

                            Spacer(Modifier.height(20.dp))

                            // Attack Action Button
                            Button(
                                onClick = { viewModel.attackBoss() },
                                enabled = !boss.isDefeated && !uiState.isAttacking,
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                if (boss.isDefeated) {
                                    Text("🏆 Boss Overcome & Slayed", fontWeight = FontWeight.Bold)
                                } else if (uiState.isAttacking) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Striking Boss...")
                                } else {
                                    Icon(Icons.Default.Bolt, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Execute CP Attack Strike", fontWeight = FontWeight.Black, fontSize = 15.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Battle Logs
            if (uiState.battleLogs.isNotEmpty()) {
                item {
                    Text("Battle Combat Log", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }

                items(uiState.battleLogs, key = { it.id }) { log ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (log.isCritical) Color(0xFFFFEB3B).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = log.message,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (log.isCritical) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}
