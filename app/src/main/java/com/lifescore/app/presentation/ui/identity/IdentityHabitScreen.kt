package com.lifescore.app.presentation.ui.identity

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.core.engine.CoreIdentity
import com.lifescore.app.core.engine.IdentityHabitEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdentityHabitScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val identities = remember { mutableStateListOf(*IdentityHabitEngine.defaultIdentities.toTypedArray()) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text("🗳️ Identity-Based Habits", fontWeight = FontWeight.Bold)
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Identity", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // 1. Philosophy Manifesto Card
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Text("✨ James Clear Identity Law", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "\"Every action you take is a vote for the type of person you wish to become.\"",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Stop chasing empty dopamine checkmarks. Cast daily votes to reshape who you are at your core.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // 2. Identities List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Your Core Identities",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${identities.sumOf { it.totalVotesCast }} Total Votes",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            items(identities) { identity ->
                val (level, rankTitle) = IdentityHabitEngine.calculateIdentityLevel(identity.totalVotesCast)

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(identity.archetypeEmoji, fontSize = 26.sp)
                                Spacer(Modifier.width(Spacing.sm))
                                Column {
                                    Text(identity.statement, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Lvl $level $rankTitle", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    "${identity.totalVotesCast} Votes",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(Spacing.sm))

                        // Progress Bar toward next Identity Milestone
                        val progress = (identity.totalVotesCast % 25) / 25f
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(Modifier.height(Spacing.xs))
                        Text(
                            "Latest Proof: \"${identity.recentProofAction}\"",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(Spacing.sm))

                        Button(
                            onClick = {
                                val idx = identities.indexOf(identity)
                                if (idx != -1) {
                                    identities[idx] = identity.copy(
                                        totalVotesCast = identity.totalVotesCast + 1,
                                        recentProofAction = "Cast today's identity affirmation vote"
                                    )
                                    Toast.makeText(context, "Vote cast for ${identity.statement}! 🗳️", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("🗳️ Cast Daily Vote (+1)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
