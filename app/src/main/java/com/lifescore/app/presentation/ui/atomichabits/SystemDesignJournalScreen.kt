package com.lifescore.app.presentation.ui.atomichabits

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.lifescore.app.domain.model.atomichabits.SystemDesignEntry

@Composable
fun SystemDesignJournalContent(
    entries: List<SystemDesignEntry>,
    onSaveEntry: (title: String, habitTarget: String, environment: String, stack: String, twoMin: String, reward: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var habitTarget by remember { mutableStateOf("") }
    var environment by remember { mutableStateOf("") }
    var habitStack by remember { mutableStateOf("") }
    var twoMin by remember { mutableStateOf("") }
    var reward by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        // Design Philosophy Header
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
                    Text(
                        "🔧 System Design Journal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "\"Every time you fail, it is not a personal failure—it is a SYSTEM failure.\" Use this journal to design high-friction bad habit barriers and low-friction good habit launchpads.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // New System Architecture Form
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Text(
                        "🛠️ Architect New Habit System",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("System Name (e.g. Morning Focus Ritual)") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = habitTarget,
                        onValueChange = { habitTarget = it },
                        label = { Text("Desired Outcome (e.g. 45 min Deep Work)") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = environment,
                        onValueChange = { environment = it },
                        label = { Text("Law 1: Environment Cue (e.g. Noise canceling headphones on desk)") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = habitStack,
                        onValueChange = { habitStack = it },
                        label = { Text("Habit Stack: \"After [X], I will [Y]\"") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = twoMin,
                        onValueChange = { twoMin = it },
                        label = { Text("Law 3: 2-Minute Starter Step") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = reward,
                        onValueChange = { reward = it },
                        label = { Text("Law 4: Immediate Reward Plan") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    GradientButton(
                        text = "Save Habit Architecture (+35 XP)",
                        onClick = {
                            if (title.isNotBlank() && habitTarget.isNotBlank()) {
                                onSaveEntry(title, habitTarget, environment, habitStack, twoMin, reward)
                                title = ""
                                habitTarget = ""
                                environment = ""
                                habitStack = ""
                                twoMin = ""
                                reward = ""
                            }
                        },
                        enabled = title.isNotBlank() && habitTarget.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // System Entries List
        item {
            Text(
                text = "Architected Systems (${entries.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        items(entries, key = { it.id }) { entry ->
            SystemEntryCard(entry)
        }

        item {
            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

@Composable
fun SystemEntryCard(entry: SystemDesignEntry) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "Active System",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = "🎯 Target: ${entry.habitTarget}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (entry.environmentChanges.isNotBlank()) {
                Text(
                    text = "🏡 Environment: ${entry.environmentChanges}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (entry.habitStack.isNotBlank()) {
                Text(
                    text = "⚡ Stack: ${entry.habitStack}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (entry.twoMinuteStep.isNotBlank()) {
                Text(
                    text = "⏱️ 2-Min Rule: ${entry.twoMinuteStep}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (entry.rewardPlan.isNotBlank()) {
                Text(
                    text = "🏆 Reward: ${entry.rewardPlan}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
