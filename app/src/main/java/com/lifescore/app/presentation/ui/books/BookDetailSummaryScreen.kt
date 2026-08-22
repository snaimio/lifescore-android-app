package com.lifescore.app.presentation.ui.books

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailSummaryScreen(
    bookId: String,
    viewModel: BookSummaryViewModel,
    navController: NavController,
    onBack: () -> Unit = { navController.popBackStack() }
) {
    val state by viewModel.detailState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(bookId) {
        viewModel.loadBookDetail(bookId)
    }

    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    val book = state.book

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(book?.title ?: "Book Summary", maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    book?.let {
                        IconButton(onClick = { viewModel.toggleBookmark(it.id) }) {
                            Icon(
                                if (state.progress?.isBookmarked == true) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (book == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                // 1. Hero Book Header & Audio Player Bar
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.md),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                modifier = Modifier.size(80.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(book.coverEmoji, fontSize = 44.sp)
                                }
                            }

                            Spacer(Modifier.height(Spacing.sm))

                            Text(
                                text = book.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "By ${book.author}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(Modifier.height(Spacing.xs))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🎯 ${book.dimension.displayName}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.width(12.dp))
                                Text("⏱️ ${book.readingTimeMinutes} min", fontSize = 12.sp)
                                Spacer(Modifier.width(12.dp))
                                Text("⭐ ${book.rating}", fontSize = 12.sp, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                            }

                            Spacer(Modifier.height(Spacing.md))

                            // Audio Simulation Player Card
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(Spacing.sm)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            FilledIconButton(
                                                onClick = { viewModel.toggleAudioPlayback() },
                                                shape = CircleShape,
                                                colors = IconButtonDefaults.filledIconButtonColors(
                                                    containerColor = MaterialTheme.colorScheme.primary
                                                )
                                            ) {
                                                Icon(
                                                    if (state.isPlayingAudio) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                    contentDescription = "Play/Pause Audio"
                                                )
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = if (state.isPlayingAudio) "Playing Audio Summary..." else "Listen (Audio Mode)",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                val progressMin = state.audioProgressSeconds / 60
                                                val progressSec = state.audioProgressSeconds % 60
                                                val totalMin = state.audioTotalSeconds / 60
                                                Text(
                                                    text = String.format("%02d:%02d / %02d:00", progressMin, progressSec, totalMin),
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        // Speed button
                                        TextButton(
                                            onClick = {
                                                val nextSpeed = when (state.playbackSpeed) {
                                                    1.0f -> 1.25f
                                                    1.25f -> 1.5f
                                                    1.5f -> 2.0f
                                                    else -> 1.0f
                                                }
                                                viewModel.setPlaybackSpeed(nextSpeed)
                                            }
                                        ) {
                                            Text("${state.playbackSpeed}x", fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    LinearProgressIndicator(
                                        progress = {
                                            (state.audioProgressSeconds.toFloat() / state.audioTotalSeconds.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Tab Navigation
                item {
                    TabRow(
                        selectedTabIndex = state.activeTab,
                        containerColor = Color.Transparent
                    ) {
                        Tab(
                            selected = state.activeTab == 0,
                            onClick = { viewModel.setActiveTab(0) },
                            text = { Text("Overview", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = state.activeTab == 1,
                            onClick = { viewModel.setActiveTab(1) },
                            text = { Text("Key Insights", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = state.activeTab == 2,
                            onClick = { viewModel.setActiveTab(2) },
                            text = { Text("Life Quest", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = state.activeTab == 3,
                            onClick = { viewModel.setActiveTab(3) },
                            text = { Text("Quotes", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }

                // 3. Tab Content
                when (state.activeTab) {
                    0 -> { // Overview
                        item {
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(Spacing.md)) {
                                    Text(
                                        text = "Core Thesis",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "\"${book.coreThesis}\"",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontStyle = FontStyle.Italic,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Spacer(Modifier.height(Spacing.md))

                                    Text(
                                        text = "Executive Summary",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = book.summaryOverview,
                                        style = MaterialTheme.typography.bodyMedium,
                                        lineHeight = 22.sp
                                    )
                                }
                            }
                        }
                    }
                    1 -> { // Key Insights
                        items(book.keyTakeaways) { takeaway ->
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(Spacing.md)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "${takeaway.index}",
                                                    color = Color.White,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = takeaway.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = takeaway.summary,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Spacer(Modifier.height(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("💡", fontSize = 14.sp)
                                            Spacer(Modifier.width(6.dp))
                                            Text(
                                                text = "Action: ${takeaway.actionStep}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    2 -> { // LifeScore Dimension Quest
                        item {
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(Spacing.md)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "⚔️ Actionable Dimension Quest",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFF10B981).copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "+${book.questXpReward} XP",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF10B981),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(Spacing.sm))

                                    Text(
                                        text = book.actionableLifeScoreQuest,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Spacer(Modifier.height(Spacing.md))

                                    val isApplied = state.progress?.appliedQuestCompleted == true

                                    Button(
                                        onClick = { viewModel.applyQuest(book.id) },
                                        enabled = !isApplied,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isApplied) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text(
                                            text = if (isApplied) "✅ Quest Completed (+75 XP)" else "Complete Quest & Claim +75 XP",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                    3 -> { // Memorable Quotes
                        items(book.memorableQuotes) { quote ->
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(Spacing.md)) {
                                    Text(
                                        text = "“$quote”",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontStyle = FontStyle.Italic,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = "— ${book.author}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Mark as Read Action Button
                item {
                    val isDone = state.progress?.isCompleted == true
                    Button(
                        onClick = { viewModel.markBookCompleted(book.id) },
                        enabled = !isDone,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Spacing.sm),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDone) Color(0xFF10B981) else MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(
                            text = if (isDone) "✅ Book Summary Completed (+75 XP)" else "Mark Summary Read (+75 XP)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(Spacing.xl))
                }
            }
        }
    }
}
