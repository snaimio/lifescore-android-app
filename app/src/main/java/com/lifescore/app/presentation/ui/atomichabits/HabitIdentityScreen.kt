package com.lifescore.app.presentation.ui.atomichabits

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.core.designsystem.components.GradientButton
import com.lifescore.app.domain.model.atomichabits.HabitIdentity

@Composable
fun HabitIdentityContent(
    identities: List<HabitIdentity>,
    onSaveIdentity: (String) -> Unit,
    onVote: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    var statementText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        // Identity Definition Card
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.md)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("👤", fontSize = 22.sp)
                        Spacer(Modifier.width(Spacing.xs))
                        Text(
                            "Identity-Based Habits",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "The goal is not to read a book, the goal is to BECOME a reader. Every action you take is a vote for who you wish to become.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(Spacing.md))
                    OutlinedTextField(
                        value = statementText,
                        onValueChange = { statementText = it },
                        placeholder = { Text("e.g. I am a focused software architect...") },
                        label = { Text("Define Identity Statement") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 3
                    )
                    Spacer(Modifier.height(Spacing.md))
                    GradientButton(
                        text = "✨ Establish Identity (+25 XP)",
                        onClick = {
                            if (statementText.isNotBlank()) {
                                onSaveIdentity(statementText.trim())
                                statementText = ""
                            }
                        },
                        enabled = statementText.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Active Identities Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🗳️ Your Identity Ballots (${identities.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tap ✓ to cast daily vote",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Identity Cards
        items(identities, key = { it.id }) { identity ->
            IdentityVoteCard(
                identity = identity,
                onVote = { onVote(identity.id) },
                onDelete = { onDelete(identity.id) }
            )
        }

        item {
            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

@Composable
fun IdentityVoteCard(
    identity: HabitIdentity,
    onVote: () -> Unit,
    onDelete: () -> Unit
) {
    val progress = (identity.dailyVotes.toFloat() / identity.targetVotes.toFloat()).coerceIn(0f, 1f)

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "\"${identity.identityStatement}\"",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(Spacing.xs))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF8B5CF6).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "🗳️ ${identity.dailyVotes} / ${identity.targetVotes} Votes",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8B5CF6),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(Modifier.width(Spacing.xs))
                        Text(
                            text = "${(progress * 100).toInt()}% Proven",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilledIconButton(
                        onClick = onVote,
                        shape = RoundedCornerShape(10.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color(0xFF10B981)
                        ),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Cast vote",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete identity",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(Spacing.sm))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = Color(0xFF8B5CF6),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}
