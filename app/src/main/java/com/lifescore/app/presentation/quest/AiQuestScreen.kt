package com.lifescore.app.presentation.quest

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.*
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.core.designsystem.components.GradientButton
import com.lifescore.app.domain.model.AiQuest
import com.lifescore.app.domain.model.QuestDifficulty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiQuestScreen(
    viewModel: AiQuestViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val filteredQuests = remember(uiState.quests, uiState.selectedDifficultyFilter) {
        if (uiState.selectedDifficultyFilter == null) {
            uiState.quests
        } else {
            uiState.quests.filter { it.difficulty == uiState.selectedDifficultyFilter }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🤖", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("AI Quest Matrix", fontWeight = FontWeight.Black)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.generateNewQuests() }) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Synthesize Quests", tint = MaterialTheme.colorScheme.primary)
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
            // Header Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, shape = RoundedCornerShape(22.dp))
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF4A148C), Color(0xFF6C63FF), Color(0xFF03DAC6))
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
                                "SOLO LEVELING QUEST HUB",
                                style = MaterialTheme.typography.labelMedium,
                                letterSpacing = 1.8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.Black.copy(alpha = 0.3f)
                            ) {
                                Text(
                                    "Gemini AI Engine",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF66FFF0),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        Text(
                            "Synthesize Personalized Quests",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            "Tailored real-world directives calibrated to level up your weakest pillars and boost combat power.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )

                        Spacer(Modifier.height(16.dp))

                        GradientButton(
                            onClick = { viewModel.generateNewQuests() },
                            text = if (uiState.isGenerating) "Synthesizing Matrix..." else "✨ Generate New Quests",
                            enabled = !uiState.isGenerating,
                            colors = listOf(Color(0xFF00E676), Color(0xFF00B0FF))
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
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text("⚡", fontSize = 16.sp)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    uiState.bannerMessage!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
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

            // Rank Filter Chips
            item {
                Column {
                    Text(
                        "Filter by Difficulty Rank",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = uiState.selectedDifficultyFilter == null,
                                onClick = { viewModel.filterByDifficulty(null) },
                                label = { Text("All Ranks", fontWeight = FontWeight.Bold) }
                            )
                        }
                        items(QuestDifficulty.values()) { rank ->
                            val isSelected = uiState.selectedDifficultyFilter == rank
                            val badgeColor = Color(rank.badgeColorHex)
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.filterByDifficulty(rank) },
                                label = { Text("${rank.rankLetter}-Rank", fontWeight = FontWeight.Bold) },
                                leadingIcon = {
                                    Surface(
                                        shape = CircleShape,
                                        color = badgeColor,
                                        modifier = Modifier.size(10.dp)
                                    ) {}
                                }
                            )
                        }
                    }
                }
            }

            // Quests List
            if (filteredQuests.isEmpty() && !uiState.isGenerating) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📜", fontSize = 40.sp)
                            Spacer(Modifier.height(10.dp))
                            Text(
                                "No active quest protocols in this rank",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Tap 'Generate New Quests' to synthesize custom missions.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            } else {
                items(filteredQuests, key = { it.id }) { quest ->
                    AiQuestCard(
                        quest = quest,
                        onAccept = { viewModel.acceptQuest(quest) }
                    )
                }
            }
        }
    }
}

@Composable
fun AiQuestCard(
    quest: AiQuest,
    onAccept: () -> Unit
) {
    val rankColor = Color(quest.difficulty.badgeColorHex)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = rankColor.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(shape = CircleShape, color = rankColor, modifier = Modifier.size(8.dp)) {}
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "${quest.difficulty.rankLetter}-Rank • ${quest.difficulty.title}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = rankColor
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(Modifier.width(4.dp))
                    Text("${quest.estimatedMinutes}m", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = quest.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = quest.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (quest.subObjectives.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Objectives:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    quest.subObjectives.forEach { obj ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("•", fontWeight = FontWeight.Bold, color = rankColor)
                            Spacer(Modifier.width(6.dp))
                            Text(obj, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Footer Row: Rewards & Accept Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                        Text(
                            "+${quest.pointsReward} XP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFF9800).copy(alpha = 0.15f)) {
                        Text(
                            "+${quest.statRewardPoints} Stat Points",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF57C00),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                if (quest.isAccepted) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF4CAF50).copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Accepted", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF4CAF50))
                        }
                    }
                } else {
                    Button(
                        onClick = onAccept,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Accept Quest", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
