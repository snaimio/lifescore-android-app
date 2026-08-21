package com.lifescore.app.presentation.ui.leaderboard

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.*
import com.lifescore.app.core.util.RankZone
import com.lifescore.app.domain.model.DimensionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    navController: NavController,
    viewModel: LeaderboardViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LifeScore Rankings", fontWeight = FontWeight.Black) },
                actions = {
                    IconButton(onClick = { viewModel.selectTab(uiState.selectedTab) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh Leaderboard")
                    }
                }
            )
        },
        bottomBar = {
            // Pinned Current User Live Standing Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("#${uiState.userRank}", fontWeight = FontWeight.Black, fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Your Standing", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                text = "${uiState.userScore} pts • ${uiState.currentLeague.displayName}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF4CAF50).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "▲ Promotion Zone",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Weekly League Header Card with Live Sunday Countdown
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(uiState.currentLeague.icon, fontSize = 28.sp)
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = uiState.currentLeague.displayName,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = "Top 3 promoted • Bottom 3 relegated",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }

                            // Sunday Reset Countdown Pill
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.HourglassEmpty, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(4.dp))
                                    Text(uiState.resetCountdown, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }

            // 2. Tab Switcher: Global | Weekly League | Friends
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        LeaderboardTab.values().forEach { tab ->
                            val isSelected = uiState.selectedTab == tab
                            Surface(
                                onClick = { viewModel.selectTab(tab) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = when(tab) {
                                            LeaderboardTab.GLOBAL -> "Global"
                                            LeaderboardTab.LEAGUE -> "League"
                                            LeaderboardTab.FRIENDS -> "Friends"
                                        },
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Dimension Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 2.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.selectedDimension == null,
                            onClick = { viewModel.selectDimension(null) },
                            label = { Text("All Dimensions", fontSize = 11.sp) }
                        )
                    }
                    items(DimensionType.values()) { dim ->
                        FilterChip(
                            selected = uiState.selectedDimension == dim,
                            onClick = { viewModel.selectDimension(dim) },
                            label = { Text(dim.displayName, fontSize = 11.sp) }
                        )
                    }
                }
            }

            // 4. Top 3 Visual Olympic-Style Podium
            if (uiState.entries.size >= 3 && uiState.selectedDimension == null) {
                item {
                    val top1 = uiState.entries.getOrNull(0)
                    val top2 = uiState.entries.getOrNull(1)
                    val top3 = uiState.entries.getOrNull(2)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Rank 2 (Silver - Left)
                        if (top2 != null) {
                            PodiumColumn(
                                rank = 2,
                                entry = top2,
                                height = 135.dp,
                                medalColor = Color(0xFFC0C0C0),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Rank 1 (Gold - Center)
                        if (top1 != null) {
                            PodiumColumn(
                                rank = 1,
                                entry = top1,
                                height = 165.dp,
                                medalColor = Color(0xFFFFD700),
                                modifier = Modifier.weight(1.15f)
                            )
                        }

                        // Rank 3 (Bronze - Right)
                        if (top3 != null) {
                            PodiumColumn(
                                rank = 3,
                                entry = top3,
                                height = 115.dp,
                                medalColor = Color(0xFFCD7F32),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Section Divider
            item {
                Text(
                    text = "Rankings (#${if (uiState.entries.size >= 3 && uiState.selectedDimension == null) 4 else 1} - #${uiState.entries.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // 5. Leaderboard List Items (#4 to #100)
            val listItems = if (uiState.entries.size >= 3 && uiState.selectedDimension == null) {
                uiState.entries.drop(3)
            } else {
                uiState.entries
            }

            items(listItems) { entry ->
                LeaderboardRowCard(entry = entry)
            }

            item {
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun PodiumColumn(
    rank: Int,
    entry: LeaderboardEntry,
    height: androidx.compose.ui.unit.Dp,
    medalColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Crown / Medal Icon
        Text(
            text = when (rank) {
                1 -> "👑"
                2 -> "🥈"
                else -> "🥉"
            },
            fontSize = if (rank == 1) 24.sp else 18.sp
        )

        Spacer(Modifier.height(4.dp))

        // Avatar Circle
        Surface(
            shape = CircleShape,
            color = medalColor.copy(alpha = 0.2f),
            border = BorderStroke(2.dp, medalColor),
            modifier = Modifier.size(if (rank == 1) 54.dp else 46.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = entry.name.take(1).uppercase(),
                    fontWeight = FontWeight.Black,
                    fontSize = if (rank == 1) 20.sp else 16.sp,
                    color = medalColor
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = entry.name.take(9),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text = "${entry.score} pts",
            fontWeight = FontWeight.Black,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(8.dp))

        // Step Pillar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            color = medalColor.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, medalColor.copy(alpha = 0.3f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "#$rank",
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    color = medalColor
                )
            }
        }
    }
}

@Composable
private fun LeaderboardRowCard(entry: LeaderboardEntry) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (entry.isCurrentUser) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        ),
        border = if (entry.isCurrentUser) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Number
            Text(
                text = "#${entry.rank}",
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.width(36.dp)
            )

            // User Info Column
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.name,
                        fontWeight = if (entry.isCurrentUser) FontWeight.Black else FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    if (entry.isCurrentUser) {
                        Spacer(Modifier.width(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = "YOU",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Lvl ${entry.level} ${entry.archetype} • 🔥 ${entry.streak}d",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Zone Pill
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(entry.zone.colorHex).copy(alpha = 0.12f)
            ) {
                Text(
                    text = entry.zone.indicator,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(entry.zone.colorHex),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }

            Spacer(Modifier.width(10.dp))

            // Score Points
            Text(
                text = "${entry.score}",
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
