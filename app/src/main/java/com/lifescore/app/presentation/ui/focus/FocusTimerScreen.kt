package com.lifescore.app.presentation.ui.focus

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.data.local.entity.TreeType
import com.lifescore.app.domain.model.DimensionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusTimerScreen(
    viewModel: FocusViewModel,
    navController: NavController,
    onBack: () -> Unit = { navController.popBackStack() }
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    val totalSec = state.selectedMinutes * 60
    val progress = if (totalSec > 0) {
        (totalSec - state.remainingSeconds).toFloat() / totalSec.toFloat()
    } else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(500), label = "FocusProgress")

    // Dynamic Tree Growth Emoji
    val currentTreeEmoji = when {
        !state.isRunning && progress == 0f -> state.selectedTree.emoji
        progress < 0.25f -> "🌱"
        progress < 0.60f -> "🌿"
        progress < 0.90f -> "🌳"
        else -> state.selectedTree.emoji
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌲", fontSize = 22.sp)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                "Mindful Forest Focus",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Gamified Deep Work (Forest Style)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        modifier = Modifier.padding(end = Spacing.sm)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🌳", fontSize = 12.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "${state.stats.totalTreesPlanted} Planted",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Central Circular Tree Growth Hero Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.size(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { 1f },
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                strokeWidth = 14.dp
                            )
                            CircularProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier.fillMaxSize(),
                                color = Color(0xFF10B981),
                                strokeWidth = 14.dp,
                                strokeCap = StrokeCap.Round
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = currentTreeEmoji,
                                    fontSize = 54.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                val minutes = state.remainingSeconds / 60
                                val seconds = state.remainingSeconds % 60
                                Text(
                                    text = String.format("%02d:%02d", minutes, seconds),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (state.isRunning) "Growing ${state.selectedTree.displayName}..." else "Ready to Focus",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(Modifier.height(Spacing.lg))

                        // Big Action Button
                        Button(
                            onClick = { viewModel.toggleTimer() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (state.isRunning) Color(0xFFEF4444) else Color(0xFF10B981)
                            )
                        ) {
                            Icon(
                                if (state.isRunning) Icons.Default.Close else Icons.Default.PlayArrow,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (state.isRunning) "Give Up & Wither Tree" else "Plant ${state.selectedTree.displayName} (${state.selectedMinutes}m)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 2. Duration Presets (Only when timer is stopped)
            if (!state.isRunning) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(Spacing.md)) {
                            Text(
                                text = "⏱️ Focus Duration",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(Spacing.xs))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                            ) {
                                listOf(15, 25, 45, 60, 90).forEach { mins ->
                                    val isSelected = state.selectedMinutes == mins
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.selectDuration(mins) }
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.padding(vertical = 10.dp)
                                        ) {
                                            Text(
                                                text = "${mins}m",
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Tree Species Selector
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(Spacing.md)) {
                            Text(
                                text = "🌲 Tree Species",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(Spacing.xs))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                                items(TreeType.values()) { tree ->
                                    val isSelected = state.selectedTree == tree
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) Color(0xFF10B981).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        border = if (isSelected) BorderStroke(1.5.dp, Color(0xFF10B981)) else null,
                                        modifier = Modifier.clickable { viewModel.selectTree(tree) }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(tree.emoji, fontSize = 20.sp)
                                            Spacer(Modifier.width(6.dp))
                                            Column {
                                                Text(tree.displayName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Text("${tree.requiredMinutes}m+", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Dimension Tag
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(Spacing.md)) {
                            Text(
                                text = "🎯 Tag Focus Dimension",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(Spacing.xs))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                                items(DimensionType.values()) { dim ->
                                    val isSelected = state.selectedDimension == dim
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.selectDimension(dim) },
                                        label = { Text(dim.displayName, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Mindful Forest Stats & History
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Text(
                            text = "🏞️ My Mindful Forest",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(Spacing.xs))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("⏳ Total Focus", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${state.stats.totalFocusMinutes} mins", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🌳 Trees Grown", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${state.stats.totalTreesPlanted}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("⚡ XP Claimed", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${(state.stats.totalFocusMinutes * 1.5).toInt()} XP", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Spacer(Modifier.height(Spacing.md))

                        // Visual Planted Trees Grid
                        val successfulSessions = state.stats.recentSessions.filter { it.wasSuccessful }
                        if (successfulSessions.isNotEmpty()) {
                            Text(
                                text = "Recent Trees Grown:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                successfulSessions.take(8).forEach { session ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                        modifier = Modifier.padding(2.dp)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(6.dp)
                                        ) {
                                            Text(session.treeType.emoji, fontSize = 20.sp)
                                            Text("${session.durationMinutes}m", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(Spacing.xl))
            }
        }
    }

    // Give Up Confirmation Dialog
    if (state.showGiveUpDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissGiveUp() },
            title = { Text("Wither Your Tree? 🥀") },
            text = {
                Text("If you give up now, your growing ${state.selectedTree.displayName} will wither and die. Are you sure you want to surrender your focus?")
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmGiveUp() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Give Up")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissGiveUp() }) {
                    Text("Keep Focusing! 💪")
                }
            }
        )
    }
}
