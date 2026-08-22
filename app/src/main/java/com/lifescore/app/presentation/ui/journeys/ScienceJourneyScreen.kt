package com.lifescore.app.presentation.ui.journeys

import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.data.local.entity.HabitStackEntity
import com.lifescore.app.data.local.entity.ScienceJourneyEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScienceJourneyScreen(
    viewModel: ScienceJourneyViewModel,
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
                        Text("Science Journeys & Habit Stacking", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Fabulous Behavioral Science Programs & Flow Rituals", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.toggleCreateStackDialog(true) },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Habit Stack") },
                containerColor = MaterialTheme.colorScheme.primary
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
            // Habit Stacking Section
            item {
                Text("🔗 Your Active Habit Stacks (Implementation Intentions)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            items(uiState.habitStacks) { stack ->
                HabitStackCard(
                    stack = stack,
                    onToggle = { viewModel.toggleStackComplete(stack.id, stack.isCompletedToday) }
                )
            }

            // Behavioral Science Journeys
            item {
                Text("🧬 21-Day Behavioral Change Journeys", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            items(uiState.journeys) { journey ->
                ScienceJourneyCard(
                    journey = journey,
                    onAdvance = { viewModel.advanceJourney(journey.journeyId) }
                )
            }

            item { Spacer(modifier = Modifier.height(60.dp)) } // FAB padding
        }
    }

    // Create Stack Dialog
    if (uiState.isCreatingStack) {
        AlertDialog(
            onDismissRequest = { viewModel.toggleCreateStackDialog(false) },
            title = { Text("Build a Habit Stack 🔗") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("The BJ Fogg / Atomic Habits Anchor Formula:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    OutlinedTextField(
                        value = uiState.triggerInput,
                        onValueChange = { viewModel.onTriggerChange(it) },
                        label = { Text("1. Trigger: 'After I [CURRENT HABIT]'") },
                        placeholder = { Text("After I pour my morning water") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.actionInput,
                        onValueChange = { viewModel.onActionChange(it) },
                        label = { Text("2. New Action: 'I will [NEW HABIT]'") },
                        placeholder = { Text("I will do 2 minutes of stretching") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.rewardInput,
                        onValueChange = { viewModel.onRewardChange(it) },
                        label = { Text("3. Immediate Celebration / Reward:") },
                        placeholder = { Text("Then I will smile and feel energized") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.saveHabitStack() }) {
                    Text("Save & Anchor (+30 XP)")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.toggleCreateStackDialog(false) }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun HabitStackCard(
    stack: HabitStackEntity,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (stack.isCompletedToday) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (stack.isCompletedToday) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = stack.isCompletedToday,
                onCheckedChange = { onToggle() }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(stack.triggerHabit, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text(stack.newActionHabit, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(stack.rewardHabit, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Surface(
                color = Color(0xFFFFECB3),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "🔥 ${stack.streakDays}d",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100)
                )
            }
        }
    }
}

@Composable
fun ScienceJourneyCard(
    journey: ScienceJourneyEntity,
    onAdvance: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(journey.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Day ${journey.currentDay}/${journey.durationDays}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Text(journey.subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("💡 Science Anchor: ${journey.behavioralPrinciple}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("🎯 Today's Goal: ${journey.currentMilestone}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onAdvance,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Complete Day ${journey.currentDay} Milestone (+40 XP)")
            }
        }
    }
}
