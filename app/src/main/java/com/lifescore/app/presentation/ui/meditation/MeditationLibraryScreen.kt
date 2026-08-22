package com.lifescore.app.presentation.ui.meditation

import android.widget.Toast
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.data.local.entity.MeditationTrackEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationLibraryScreen(
    viewModel: MeditationViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Insight Meditation Library", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Insight Timer Guided Sanctuary & Ambient Bell Timers", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Ambient Bell Timer Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF00695C),
                                        Color(0xFF004D40),
                                        Color(0xFF00796B)
                                    )
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
                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = CircleShape
                                ) {
                                    Text(
                                        "🔔 Customizable Singing Bowl Timer",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text("Tibetan Bell", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                "Duration: ${uiState.customTimerMinutes} Minutes",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(5, 10, 15, 20).forEach { mins ->
                                    val isSelected = uiState.customTimerMinutes == mins
                                    Button(
                                        onClick = { viewModel.setCustomTimer(mins) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSelected) Color(0xFF80CBC4) else Color.White.copy(alpha = 0.2f),
                                            contentColor = if (isSelected) Color(0xFF004D40) else Color.White
                                        )
                                    ) {
                                        Text("${mins}m", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { viewModel.completeSession(uiState.customTimerMinutes) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF80CBC4), contentColor = Color(0xFF004D40))
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Begin Meditation (${uiState.customTimerMinutes * 2} XP)", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Category Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val categories = listOf("ALL", "Mindfulness", "Breathwork", "Sleep", "Focus")
                    items(categories) { cat ->
                        val isSelected = uiState.selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setCategory(cat) },
                            label = { Text(cat, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }
            }

            // Guided Tracks
            val filtered = if (uiState.selectedCategory == "ALL") {
                uiState.tracks
            } else {
                uiState.tracks.filter { it.category.equals(uiState.selectedCategory, ignoreCase = true) }
            }

            items(filtered) { track ->
                MeditationTrackCard(
                    track = track,
                    onPlay = { viewModel.completeSession(track.durationMinutes) },
                    onBookmark = { viewModel.toggleBookmark(track.trackId, track.isBookmarked) }
                )
            }

            // Live Events Section
            item {
                Text(
                    "🔴 Community Live Workshops",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            items(uiState.liveEvents) { event ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(event.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Host: ${event.hostName} • ${event.startTimeIso}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("👥 ${event.registeredCount} meditators registered", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        }

                        Button(
                            onClick = { viewModel.toggleRegisterEvent(event.eventId, event.isRegistered) },
                            shape = RoundedCornerShape(10.dp),
                            colors = if (event.isRegistered) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) else ButtonDefaults.buttonColors()
                        ) {
                            Text(if (event.isRegistered) "Joined" else "RSVP", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MeditationTrackCard(
    track: MeditationTrackEntity,
    onPlay: () -> Unit,
    onBookmark: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { onPlay() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(track.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                Text("${track.teacherName} • ${track.durationMinutes} mins", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⭐ ${track.rating} (${track.playsCount / 1000}k listens)", fontSize = 11.sp, color = Color(0xFFFFA000), fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("• ${track.ambientSound}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            IconButton(onClick = onBookmark) {
                Icon(
                    if (track.isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (track.isBookmarked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
