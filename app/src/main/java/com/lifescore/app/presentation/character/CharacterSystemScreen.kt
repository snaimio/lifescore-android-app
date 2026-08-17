package com.lifescore.app.presentation.character

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.*
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.domain.model.CharacterTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterSystemScreen(
    viewModel: CharacterViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    val animatedCombatPower by animateIntAsState(
        targetValue = uiState.stats.combatPower,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "combatPowerAnim"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🛡️", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("Hunter Status Window", fontWeight = FontWeight.Black)
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
            contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
        ) {
            // Solo Leveling Status Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp, shape = RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF4338CA))
                            )
                        )
                        .padding(22.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "HUNTER PROFILE",
                                    style = MaterialTheme.typography.labelSmall,
                                    letterSpacing = 2.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF818CF8)
                                )
                                Text(
                                    "Level ${uiState.level}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFFD700).copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("👑", fontSize = 14.sp)
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        uiState.stats.title,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = Color(0xFFFFD700)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Combat Power Metric Card
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Black.copy(alpha = 0.35f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("COMBAT POWER (CP)", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                                    Text(
                                        "$animatedCombatPower",
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF00E5FF)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Stat Points Available", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                                    Text(
                                        "${uiState.stats.availablePoints} PTS",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (uiState.stats.availablePoints > 0) Color(0xFFFF9800) else Color.White.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "Active Bonus: ${uiState.stats.titleBonusDescription}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF67E8F9)
                        )
                    }
                }
            }

            // Notification Banner
            if (uiState.bannerMessage != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                uiState.bannerMessage!!,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            IconButton(
                                onClick = { viewModel.dismissBanner() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Stat Distribution Section
            item {
                Text(
                    "Core Attributes (5 Primary Stats)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // STR
            item {
                StatAllocationRow(
                    name = "Strength (STR)",
                    value = uiState.stats.strength,
                    description = "Boosts Physical Output, Fitness XP, and Boss Damage",
                    emoji = "💪",
                    accentColor = Color(0xFFEF4444),
                    canAllocate = uiState.stats.availablePoints > 0,
                    onAllocate = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.allocateStrength()
                    }
                )
            }

            // VIT
            item {
                StatAllocationRow(
                    name = "Vitality (VIT)",
                    value = uiState.stats.vitality,
                    description = "Boosts Health, Streak Resilience, and Shields",
                    emoji = "❤️",
                    accentColor = Color(0xFF10B981),
                    canAllocate = uiState.stats.availablePoints > 0,
                    onAllocate = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.allocateVitality()
                    }
                )
            }

            // AGI
            item {
                StatAllocationRow(
                    name = "Agility (AGI)",
                    value = uiState.stats.agility,
                    description = "Boosts Speed, Habit Efficiency, and Time Multipliers",
                    emoji = "⚡",
                    accentColor = Color(0xFFF59E0B),
                    canAllocate = uiState.stats.availablePoints > 0,
                    onAllocate = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.allocateAgility()
                    }
                )
            }

            // INT
            item {
                StatAllocationRow(
                    name = "Intelligence (INT)",
                    value = uiState.stats.intelligence,
                    description = "Boosts Learning, Career Milestones, and AI Insights",
                    emoji = "🧠",
                    accentColor = Color(0xFF8B5CF6),
                    canAllocate = uiState.stats.availablePoints > 0,
                    onAllocate = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.allocateIntelligence()
                    }
                )
            }

            // PER
            item {
                StatAllocationRow(
                    name = "Perception (PER)",
                    value = uiState.stats.perception,
                    description = "Boosts Mental Health, Mindfulness, and Mood Clarity",
                    emoji = "👁️",
                    accentColor = Color(0xFF06B6D4),
                    canAllocate = uiState.stats.availablePoints > 0,
                    onAllocate = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.allocatePerception()
                    }
                )
            }

            // Title System Section
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Hunter Titles & Passive Buffs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(uiState.titles) { title ->
                TitleItemCard(
                    title = title,
                    onEquip = { viewModel.equipTitle(title) }
                )
            }
        }
    }
}

@Composable
fun StatAllocationRow(
    name: String,
    value: Int,
    description: String,
    emoji: String,
    accentColor: Color,
    canAllocate: Boolean,
    onAllocate: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Surface(
                    shape = CircleShape,
                    color = accentColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(emoji, fontSize = 20.sp)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), maxLines = 1)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$value",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = accentColor
                )
                Spacer(Modifier.width(10.dp))
                FilledIconButton(
                    onClick = onAllocate,
                    enabled = canAllocate,
                    shape = RoundedCornerShape(10.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = accentColor,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Point", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun TitleItemCard(
    title: CharacterTitle,
    onEquip: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (title.isEquipped)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            else
                MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    if (title.isEquipped) {
                        Spacer(Modifier.width(8.dp))
                        Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.primary) {
                            Text(
                                "EQUIPPED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text("Bonus: ${title.statBonus}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                Text(title.requirement, fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
            }

            if (!title.isEquipped) {
                OutlinedButton(
                    onClick = onEquip,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("Equip", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
