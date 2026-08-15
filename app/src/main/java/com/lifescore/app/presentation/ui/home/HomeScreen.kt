package com.lifescore.app.presentation.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.HeroArchetype
import com.lifescore.app.domain.model.LifeTask
import com.lifescore.app.domain.model.UserProfile
import com.lifescore.app.core.util.ShareCardData
import com.lifescore.app.presentation.navigation.Screen
import com.lifescore.app.presentation.ui.components.CharacterSheetDialog
import com.lifescore.app.presentation.ui.components.GuardianSponsorshipDialog
import com.lifescore.app.presentation.ui.components.HardModeSheet
import com.lifescore.app.presentation.ui.home.components.*
import com.lifescore.app.presentation.ui.share.ShareScoreCardDialog
import com.lifescore.app.utils.ShareHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel,
    onOpenPaywall: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var showCharacterSheet by remember { mutableStateOf(false) }
    var showGuardianDialog by remember { mutableStateOf(false) }
    var showHardModeSheet by remember { mutableStateOf(false) }
    var showShareCardDialog by remember { mutableStateOf(false) }
    var isHardModeEnabled by remember { mutableStateOf(false) }

    // Sync rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "sync_rotate")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showCharacterSheet = true }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("⚔️", fontSize = 18.sp)
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("LifeScore", fontWeight = FontWeight.Black, fontSize = 18.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(uiState.userTitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(6.dp))
                                Text("•", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                Spacer(Modifier.width(6.dp))
                                Text("Lvl ${uiState.level}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                },
                actions = {
                    // Sync Status / Trigger Button
                    IconButton(onClick = { viewModel.triggerManualSync() }) {
                        Icon(
                            Icons.Default.CloudSync,
                            contentDescription = "Sync Cloud",
                            tint = if (uiState.isSyncing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = if (uiState.isSyncing) Modifier.rotate(rotationAngle) else Modifier
                        )
                    }

                    // Share Progress Button (Opens 9:16 Story Card Generator)
                    IconButton(
                        onClick = { showShareCardDialog = true }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share LifeScore Card")
                    }

                    // Coin Wallet Store Chip
                    AssistChip(
                        onClick = { navController.navigate(Screen.RewardStore.route) },
                        label = { Text("1,250", fontWeight = FontWeight.Bold) },
                        leadingIcon = {
                            Text("🪙", fontSize = 14.sp)
                        }
                    )

                    // PRO Upgrade Chip
                    AssistChip(
                        onClick = onOpenPaywall,
                        label = { Text("PRO", fontWeight = FontWeight.Bold) },
                        leadingIcon = {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(18.dp))
                        }
                    )

                    // Settings
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            // Material 3 Loading State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, strokeWidth = 3.dp)
                    Spacer(Modifier.height(16.dp))
                    Text("Loading your LifeScore from Firestore...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cloud Sync Status Pill
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (uiState.isSyncing) Icons.Default.Sync else Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = if (uiState.isSyncing) MaterialTheme.colorScheme.primary else Color(0xFF10B981),
                                modifier = Modifier
                                    .size(16.dp)
                                    .let { if (uiState.isSyncing) it.rotate(rotationAngle) else it }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = uiState.cloudSyncStatus,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.weight(1f))
                            Text(
                                text = "Auto-Sync ON",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Hero LifeScore Index Card
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("OVERALL LIFESCORE INDEX", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${uiState.totalScore}",
                                    fontSize = 54.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "/1000",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            // Level & XP Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Level ${uiState.level} Progress", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                Text("${(uiState.levelProgress * 100).toInt()}% to Level ${uiState.level + 1}", style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { uiState.levelProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(CircleShape),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surface
                            )

                            Spacer(Modifier.height(16.dp))

                            // Streak & Today Completion Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)) {
                                    Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text("🔥", fontSize = 18.sp)
                                        Spacer(Modifier.width(6.dp))
                                        Text("${uiState.streak} Day Streak", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                                Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)) {
                                    Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text("✅", fontSize = 16.sp)
                                        Spacer(Modifier.width(6.dp))
                                        Text("${uiState.tasksCompleted}/${uiState.totalTasks} Quests", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            // Share My LifeScore 9:16 Card CTA Button
                            Button(
                                onClick = { showShareCardDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("📸 Share My LifeScore Card", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            Spacer(Modifier.height(8.dp))

                            // 2s Micro-Vlogs & 14s Weekly Reel CTA
                            OutlinedButton(
                                onClick = { navController.navigate(Screen.MicroVlogs.route) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("🎬 2s Daily Micro-Vlogs & 14s Reel", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            }

                            Spacer(Modifier.height(8.dp))

                            // 10,000-Hour Skill Mastery Tracker CTA
                            OutlinedButton(
                                onClick = { navController.navigate(Screen.SkillMastery.route) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f))
                            ) {
                                Text("⏱️", fontSize = 16.sp)
                                Spacer(Modifier.width(8.dp))
                                Text("10,000-Hour Skill Mastery Tracker", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFFFD700))
                            }

                            Spacer(Modifier.height(8.dp))

                            // LifeScore Reward Store CTA
                            OutlinedButton(
                                onClick = { navController.navigate(Screen.RewardStore.route) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                            ) {
                                Text("🎁", fontSize = 16.sp)
                                Spacer(Modifier.width(8.dp))
                                Text("LifeScore Reward Store & Custom Rewards", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            }

                            Spacer(Modifier.height(8.dp))

                            // Enterprise Hub CTA
                            OutlinedButton(
                                onClick = { navController.navigate(Screen.Enterprise.route) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.5f))
                            ) {
                                Text("🏢", fontSize = 16.sp)
                                Spacer(Modifier.width(8.dp))
                                Text("LifeScore Enterprise & Team Hub", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF6366F1))
                            }

                            Spacer(Modifier.height(8.dp))

                            // AI Meme Studio CTA
                            OutlinedButton(
                                onClick = { navController.navigate(Screen.MemeStudio.route) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, Color(0xFFEC4899).copy(alpha = 0.5f))
                            ) {
                                Text("🎭", fontSize = 16.sp)
                                Spacer(Modifier.width(8.dp))
                                Text("AI Meme Studio & Viral Content", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFEC4899))
                            }
                        }
                    }
                }

                // 8-Dimension Spider Radar Wheel
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("8-Dimension Life Balance Wheel", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                TextButton(onClick = { navController.navigate(Screen.Dimensions.route) }) {
                                    Text("Details", fontSize = 12.sp)
                                }
                            }
                            DimensionRadarChart(
                                dimensionScores = uiState.dimensionScores,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp)
                            )
                        }
                    }
                }

                // 8-Dimension Quick Grid / Scroll
                item {
                    Text("All 8 Life Dimensions", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 4.dp)
                    ) {
                        items(uiState.dimensions) { dimension ->
                            val score = uiState.dimensionScores[dimension] ?: 50
                            DimensionChipCard(
                                dimension = dimension,
                                score = score,
                                onClick = { navController.navigate(Screen.Dimensions.route) }
                            )
                        }
                    }
                }

                // Viral Retentive Cards: Hard Mode & Finch Guardian
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Life Reset Hard Mode Card
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isHardModeEnabled) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showHardModeSheet = true }
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("💀", fontSize = 20.sp)
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("Hard Mode", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(if (isHardModeEnabled) "Active (-50 XP)" else "Tap to Enable", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                }
                            }
                        }

                        // Finch Guardian Gifting Card
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showGuardianDialog = true }
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("🎁", fontSize = 20.sp)
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("Guardian Gift", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Sponsor a Hero", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f))
                                }
                            }
                        }
                    }
                }

                // Today's Quests & Habits (Auto-syncs on click)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Today's Dimension Quests", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = { navController.navigate(Screen.Tasks.route) }) {
                            Text("Manage All", fontSize = 12.sp)
                        }
                    }
                }

                items(uiState.todayTasks) { task ->
                    TaskCard(
                        task = task,
                        onToggle = { viewModel.onToggleTask(task) }
                    )
                }

                item {
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }

    // Dialogs
    if (showCharacterSheet) {
        CharacterSheetDialog(
            userProfile = UserProfile(
                currentXp = uiState.currentXp,
                currentLevel = uiState.level,
                currentStreakDays = uiState.streak,
                title = uiState.userTitle
            ),
            archetype = HeroArchetype.WARRIOR,
            onDismiss = { showCharacterSheet = false }
        )
    }

    if (showGuardianDialog) {
        GuardianSponsorshipDialog(onDismiss = { showGuardianDialog = false })
    }

    if (showHardModeSheet) {
        HardModeSheet(
            isHardMode = isHardModeEnabled,
            streakInsuranceCount = 1,
            onToggleHardMode = { isHardModeEnabled = it },
            onBuyInsurance = { /* Buy streak insurance */ },
            onDismiss = { showHardModeSheet = false }
        )
    }

    if (showShareCardDialog) {
        ShareScoreCardDialog(
            data = ShareCardData(
                userName = uiState.userName,
                score = uiState.totalScore,
                level = uiState.level,
                streak = uiState.streak,
                title = uiState.userTitle,
                dimensionScores = uiState.dimensionScores
            ),
            onDismiss = { showShareCardDialog = false }
        )
    }
}

@Composable
fun DimensionChipCard(
    dimension: DimensionType,
    score: Int,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .width(130.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(dimension.displayName.take(8), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("$score%", fontWeight = FontWeight.Black, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { score / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}
