package com.lifescore.app.presentation.ui.atomichabits

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard

enum class AtomicHabitsTab(val title: String, val emoji: String) {
    IDENTITY("Identity", "👤"),
    SCORECARD("Scorecard", "📋"),
    FOUR_LAWS("4 Laws", "⚡"),
    CHALLENGE("30d Challenge", "🔥"),
    SYSTEMS("Systems", "🔧")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtomicHabitsDashboardScreen(
    viewModel: AtomicHabitsViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(AtomicHabitsTab.IDENTITY) }

    LaunchedEffect(uiState.successToast) {
        uiState.successToast?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { err ->
            Toast.makeText(context, "Error: $err", Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "⚡ Atomic Habits OS",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                "James Clear Behavior Change Architecture",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF8B5CF6).copy(alpha = 0.15f),
                            modifier = Modifier.padding(end = Spacing.sm)
                        ) {
                            Text(
                                text = "🔥 Day ${uiState.challenge?.currentDay ?: 1}/30",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8B5CF6),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Scrollable Tab Row for 5 Atomic Pillars
                ScrollableTabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    edgePadding = Spacing.md,
                    divider = {},
                    containerColor = Color.Transparent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AtomicHabitsTab.values().forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            text = {
                                Text(
                                    text = "${tab.emoji} ${tab.title}",
                                    fontWeight = if (selectedTab == tab) FontWeight.Black else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }

                Spacer(Modifier.height(Spacing.sm))

                // Tab Content Rendering
                when (selectedTab) {
                    AtomicHabitsTab.IDENTITY -> {
                        HabitIdentityContent(
                            identities = uiState.identities,
                            onSaveIdentity = { viewModel.saveIdentity(it) },
                            onVote = { viewModel.voteForIdentity(it) },
                            onDelete = { viewModel.deleteIdentity(it) }
                        )
                    }
                    AtomicHabitsTab.SCORECARD -> {
                        HabitScorecardContent(
                            scorecards = uiState.scorecardItems,
                            onAddHabit = { name, cat -> viewModel.addScorecardItem(name, cat) },
                            onUpdateCategory = { id, cat -> viewModel.updateScorecardCategory(id, cat) },
                            onDeleteHabit = { viewModel.deleteScorecardItem(it) }
                        )
                    }
                    AtomicHabitsTab.FOUR_LAWS -> {
                        FourLawsContent()
                    }
                    AtomicHabitsTab.CHALLENGE -> {
                        AtomicChallengeContent(
                            challenge = uiState.challenge,
                            onLogDay = { viewModel.logChallengeToday() },
                            onChangeHabit = { viewModel.setChallengeHabit(it) }
                        )
                    }
                    AtomicHabitsTab.SYSTEMS -> {
                        SystemDesignJournalContent(
                            entries = uiState.journalEntries,
                            onSaveEntry = { t, h, e, s, m, r ->
                                viewModel.saveSystemJournalEntry(t, h, e, s, m, r)
                            }
                        )
                    }
                }
            }
        }
    }
}
