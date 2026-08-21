package com.lifescore.app.presentation.skills

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.*
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.MasteryTier
import com.lifescore.app.domain.model.SkillMastery

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillMasteryScreen(
    viewModel: SkillMasteryViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.recentSuccessMessage) {
        uiState.recentSuccessMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("10,000-Hour Skill Mastery", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = { viewModel.openAddSkillDialog() },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Add Skill", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. 10,000-Hour Hero Card
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("GLOBAL MASTERY ENGINE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                Text("${String.format("%.1f", uiState.totalPracticeHours)} / 10,000 Hours", fontSize = 20.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(uiState.globalMasteryTier.colorHex).copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(uiState.globalMasteryTier.icon, fontSize = 14.sp)
                                    Spacer(Modifier.width(4.dp))
                                    Text(uiState.globalMasteryTier.title, fontWeight = FontWeight.Black, fontSize = 11.sp, color = Color(uiState.globalMasteryTier.colorHex))
                                }
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        LinearProgressIndicator(
                            progress = { uiState.global10kProgress },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape)
                        )

                        Spacer(Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${String.format("%.2f", uiState.global10kProgress * 100)}% Toward 10k Outlier Mastery", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                            Text("${uiState.skills.size} Active Skills", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // 2. Active Stopwatch Banner if running
            if (uiState.isStopwatchRunning) {
                val runningSkill = uiState.skills.find { it.id == uiState.activeStopwatchSkillId }
                val minutes = uiState.stopwatchSeconds / 60
                val secs = uiState.stopwatchSeconds % 60

                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981).copy(alpha = 0.15f)),
                        border = BorderStroke(2.dp, Color(0xFF10B981)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = CircleShape, color = Color(0xFF10B981), modifier = Modifier.size(38.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("⏱️", fontSize = 18.sp)
                                    }
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text("Deliberate Practice Live", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF10B981))
                                    Text("${runningSkill?.title}", fontWeight = FontWeight.Black, fontSize = 14.sp)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    String.format("%02d:%02d", minutes, secs),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF10B981)
                                )
                                Spacer(Modifier.width(10.dp))
                                Button(
                                    onClick = { viewModel.stopAndSaveStopwatch() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Done ✓", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 3. Dimension Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = uiState.selectedDimensionFilter == null,
                            onClick = { viewModel.selectDimensionFilter(null) },
                            label = { Text("All Dimensions", fontSize = 11.sp) }
                        )
                    }
                    items(DimensionType.values()) { dim ->
                        FilterChip(
                            selected = uiState.selectedDimensionFilter == dim,
                            onClick = { viewModel.selectDimensionFilter(dim) },
                            label = { Text(dim.displayName, fontSize = 11.sp) }
                        )
                    }
                }
            }

            // 4. Skills List
            items(uiState.filteredSkills) { skill ->
                SkillMasteryCard(
                    skill = skill,
                    onLogClick = { viewModel.openLogDialog(skill) },
                    onStartTimer = { viewModel.startStopwatch(skill.id) },
                    onQuickLog = { mins -> viewModel.logMinutes(skill.id, mins) }
                )
            }

            item { Spacer(Modifier.height(30.dp)) }
        }
    }

    // Log Session Dialog
    if (uiState.isLogDialogOpen && uiState.selectedSkill != null) {
        val skill = uiState.selectedSkill!!
        var customMins by remember { mutableStateOf("30") }
        var notes by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { viewModel.closeLogDialog() }) {
            Card(
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Log Deliberate Practice", fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("${skill.emoji} ${skill.title} (${skill.dimension.displayName})", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

                    Spacer(Modifier.height(14.dp))

                    Text("Quick Select Duration:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                        listOf(15, 30, 45, 60, 120).forEach { mins ->
                            SuggestionChip(
                                onClick = { customMins = mins.toString() },
                                label = { Text("${mins}m", fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customMins,
                        onValueChange = { customMins = it },
                        label = { Text("Minutes Practiced") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Session Notes / Focus Area (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val mins = customMins.toIntOrNull() ?: 30
                            viewModel.logMinutes(skill.id, mins, notes)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Confirm & Award Dimension XP", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Add Custom Skill Dialog
    if (uiState.isAddSkillDialogOpen) {
        var title by remember { mutableStateOf("") }
        var emoji by remember { mutableStateOf("⚡") }
        var selectedDim by remember { mutableStateOf(DimensionType.LEARNING) }
        var targetHours by remember { mutableStateOf(10000) }

        Dialog(onDismissRequest = { viewModel.closeAddSkillDialog() }) {
            Card(
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Add Skill Mastery Track", fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Skill Name (e.g. Japanese, Chess)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = emoji,
                        onValueChange = { emoji = it },
                        label = { Text("Emoji Icon") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))
                    Text("Target Mastery Milestone:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf(100, 500, 2000, 5000, 10000)) { hrs ->
                            FilterChip(
                                selected = targetHours == hrs,
                                onClick = { targetHours = hrs },
                                label = { Text("${hrs}h", fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Text("Connected LifeScore Dimension:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(DimensionType.values()) { dim ->
                            FilterChip(
                                selected = selectedDim == dim,
                                onClick = { selectedDim = dim },
                                label = { Text(dim.displayName, fontSize = 10.sp) }
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.addNewSkill(title, emoji, selectedDim, targetHours) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Create Skill Track", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SkillMasteryCard(
    skill: SkillMastery,
    onLogClick: () -> Unit,
    onStartTimer: () -> Unit,
    onQuickLog: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(skill.emoji, fontSize = 24.sp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(skill.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = skill.dimension.displayName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(skill.dimension.baseColorHex)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text("•", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                            Spacer(Modifier.width(6.dp))
                            Text("🔥 ${skill.streakDays}d streak", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(skill.currentTier.colorHex).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "${skill.currentTier.icon} ${skill.currentTier.title}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(skill.currentTier.colorHex),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${String.format("%.1f", skill.totalHours)} / ${skill.targetHours} Hours", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("${String.format("%.1f", skill.progressPercentage * 100)}%", fontWeight = FontWeight.Black, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { skill.progressPercentage },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
            )

            Spacer(Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = { onQuickLog(30) },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.weight(1f).height(36.dp)
                ) {
                    Text("+30m", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { onQuickLog(60) },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.weight(1f).height(36.dp)
                ) {
                    Text("+1h", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                FilledTonalButton(
                    onClick = onStartTimer,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.weight(1.1f).height(36.dp)
                ) {
                    Text("⏱️ Timer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onLogClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.weight(1.2f).height(36.dp)
                ) {
                    Text("Log 📝", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
