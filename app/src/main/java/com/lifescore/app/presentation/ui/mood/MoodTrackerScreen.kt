package com.lifescore.app.presentation.ui.mood

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.data.local.entity.MoodType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MoodTrackerScreen(
    viewModel: MoodViewModel,
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
                        Text("🎭", fontSize = 22.sp)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                "Mood & Well-Being Tracker",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Emotional Telemetry & LifeScore Analytics",
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
                        color = Color(0xFF6366F1).copy(alpha = 0.15f),
                        modifier = Modifier.padding(end = Spacing.sm)
                    ) {
                        Text(
                            text = "${state.analytics.totalCheckIns} Check-ins",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
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
            // 1. Mood Selection Selector Card
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "How are you feeling right now?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(Spacing.sm))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            MoodType.values().forEach { mood ->
                                val isSelected = state.selectedMood == mood
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { viewModel.selectMood(mood) }
                                        .padding(4.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                                        modifier = Modifier.size(54.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(mood.emoji, fontSize = 28.sp)
                                        }
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = mood.label.split(" ").first(),
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Energy & Stress Sliders
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("⚡ Energy Level", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("${state.energyLevel.toInt()} / 10", fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                        }
                        Slider(
                            value = state.energyLevel,
                            onValueChange = { viewModel.setEnergyLevel(it) },
                            valueRange = 1f..10f,
                            steps = 8
                        )

                        Spacer(Modifier.height(Spacing.sm))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("🛑 Stress / Anxiety Level", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("${state.stressLevel.toInt()} / 10", fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                        }
                        Slider(
                            value = state.stressLevel,
                            onValueChange = { viewModel.setStressLevel(it) },
                            valueRange = 1f..10f,
                            steps = 8
                        )
                    }
                }
            }

            // 3. Influencing Factors Tags
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Text(
                            text = "What is impacting your mood?",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(Spacing.sm))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            viewModel.availableFactors.forEach { factor ->
                                val isSelected = state.selectedFactors.contains(factor)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.toggleFactor(factor) },
                                    label = { Text(factor, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        Spacer(Modifier.height(Spacing.md))

                        OutlinedTextField(
                            value = state.note,
                            onValueChange = { viewModel.updateNote(it) },
                            placeholder = { Text("Add any personal thoughts or context (optional)...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 3
                        )

                        Spacer(Modifier.height(Spacing.md))

                        Button(
                            onClick = { viewModel.logMood() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Save Mood Check-in (+20 XP)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 4. Mood Correlation Analytics Card
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Text(
                            text = "📊 Emotional Insights & Habit Correlation",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(Spacing.xs))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Dominant State", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(state.analytics.dominantMood.emoji, fontSize = 18.sp)
                                    Spacer(Modifier.width(4.dp))
                                    Text(state.analytics.dominantMood.name, fontWeight = FontWeight.Bold)
                                }
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Avg Energy", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(String.format("%.1f/10", state.analytics.averageEnergy), fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Avg Stress", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(String.format("%.1f/10", state.analytics.averageStress), fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                            }
                        }

                        Spacer(Modifier.height(Spacing.md))

                        // AI Correlation Insight Box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(Spacing.sm),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🤖", fontSize = 24.sp)
                                Spacer(Modifier.width(Spacing.sm))
                                Column {
                                    Text(
                                        text = "AI Dimension Correlation",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "On days you complete Deep Work and log 8+ glasses of water, your Positive Emotion score increases by 38%.",
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Recent Logs Timeline
            item {
                Text(
                    text = "Recent Mood Timeline",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            items(state.analytics.logs.take(5)) { log ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(log.mood.emoji, fontSize = 20.sp)
                            }
                        }
                        Spacer(Modifier.width(Spacing.sm))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(log.mood.label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(Modifier.width(8.dp))
                                Text(log.dateIso, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (log.factorTags.isNotEmpty()) {
                                Text(
                                    text = log.factorTags.replace(",", " • "),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            if (log.note.isNotEmpty()) {
                                Text(
                                    text = "\"${log.note}\"",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
