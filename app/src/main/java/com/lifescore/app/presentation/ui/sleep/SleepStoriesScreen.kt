package com.lifescore.app.presentation.ui.sleep

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SleepStoriesScreen(
    viewModel: SleepViewModel,
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌙", fontSize = 22.sp)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                "Sleep Stories & Soundscapes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Restorative Sleep & Calming Ambiance",
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
                    if (state.isTimerActive) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF6366F1).copy(alpha = 0.15f),
                            modifier = Modifier.padding(end = Spacing.sm)
                        ) {
                            val min = state.timerRemainingSeconds / 60
                            val sec = state.timerRemainingSeconds % 60
                            Text(
                                text = String.format("⏳ %02d:%02d", min, sec),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // 1. Ambient Soundscape Mixer Card
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🎧 Ambient Sound Mixer",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (state.activeTrackVolumes.isNotEmpty()) {
                                Text(
                                    text = "${state.activeTrackVolumes.size} Active",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }

                        Spacer(Modifier.height(Spacing.sm))

                        // Grid of sound chips
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            state.ambientTracks.forEach { track ->
                                val isActive = state.activeTrackVolumes.containsKey(track.id)
                                FilterChip(
                                    selected = isActive,
                                    onClick = { viewModel.toggleTrack(track.id) },
                                    label = { Text("${track.iconEmoji} ${track.name.split(" ").take(2).joinToString(" ")}") }
                                )
                            }
                        }

                        // Volume sliders for active tracks
                        if (state.activeTrackVolumes.isNotEmpty()) {
                            Spacer(Modifier.height(Spacing.sm))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            Spacer(Modifier.height(Spacing.xs))

                            state.activeTrackVolumes.forEach { (trackId, volume) ->
                                val track = state.ambientTracks.find { it.id == trackId } ?: return@forEach
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${track.iconEmoji} ${track.name}", fontSize = 12.sp, modifier = Modifier.width(130.dp), maxLines = 1)
                                    Slider(
                                        value = volume,
                                        onValueChange = { viewModel.setTrackVolume(trackId, it) },
                                        valueRange = 0f..1f,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Sleep Stories Library
            item {
                Text(
                    text = "📖 Relaxing Sleep Stories",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(state.sleepStories) { story ->
                val isSelected = state.selectedStory?.id == story.id
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectStory(story) }
                ) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                modifier = Modifier.size(50.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(story.coverEmoji, fontSize = 28.sp)
                                }
                            }
                            Spacer(Modifier.width(Spacing.md))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(story.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("🎙️ ${story.narrator} • ⏱️ ${story.durationMinutes} min", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            FilledIconButton(
                                onClick = {
                                    viewModel.selectStory(story)
                                    viewModel.toggleStoryPlayback()
                                },
                                shape = CircleShape
                            ) {
                                Icon(
                                    if (isSelected && state.isStoryPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play"
                                )
                            }
                        }

                        if (isSelected) {
                            Spacer(Modifier.height(Spacing.sm))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(Spacing.sm)) {
                                    val currentParagraph = story.storyScript.getOrNull(state.storyProgressParagraph)
                                        ?: story.storyScript.first()
                                    Text(
                                        text = "\"$currentParagraph\"",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontStyle = FontStyle.Italic,
                                        lineHeight = 22.sp
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = {
                                            (state.storyProgressParagraph + 1).toFloat() / story.storyScript.size.toFloat()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(4.dp))
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Sleep Timer Presets
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Text(
                            text = "⏱️ Auto-Off Sleep Timer",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(Spacing.xs))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            listOf(0, 15, 30, 45, 60).forEach { mins ->
                                val isSelected = state.sleepTimerMinutes == mins
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setSleepTimer(mins) }
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = if (mins == 0) "Off" else "${mins}m",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Bedtime Wind-Down Checklist
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Text(
                            text = "🛌 Bedtime Wind-Down Ritual",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(Spacing.xs))
                        state.windDownItems.forEach { (item, isChecked) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.toggleWindDownItem(item) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { viewModel.toggleWindDownItem(item) }
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = item,
                                    fontSize = 13.sp,
                                    color = if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
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
}
