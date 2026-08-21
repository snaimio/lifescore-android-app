package com.lifescore.app.presentation.ui.recovery.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.data.local.entity.AddictionType
import com.lifescore.app.data.local.entity.CravingIntensity
import com.lifescore.app.data.local.entity.CravingLog
import com.lifescore.app.data.local.entity.RelapseType

@Composable
fun LogCravingDialog(
    addictionType: AddictionType,
    triggers: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (CravingLog) -> Unit
) {
    var selectedIntensity by remember { mutableStateOf(CravingIntensity.MODERATE) }
    var selectedTrigger by remember { mutableStateOf(triggers.firstOrNull() ?: "Stress") }
    var durationMinutes by remember { mutableIntStateOf(10) }
    var didResist by remember { mutableStateOf(true) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📝", fontSize = 20.sp)
                Spacer(Modifier.width(8.dp))
                Text("Log Craving / Urge", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Text(
                    text = "Logging cravings disarms them and reveals your subconscious trigger patterns.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Intensity Selection
                Text(
                    text = "Urge Intensity",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CravingIntensity.values().forEach { intensity ->
                        val isSelected = intensity == selectedIntensity
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedIntensity = intensity }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 6.dp)
                            ) {
                                Text(intensity.emoji, fontSize = 14.sp)
                                Text(
                                    text = intensity.name.take(4),
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Trigger Picker
                Text(
                    text = "Primary Trigger",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    triggers.take(5).forEach { trigger ->
                        val isSelected = trigger == selectedTrigger
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTrigger = trigger }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedTrigger = trigger }
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(trigger, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Did Resist Checkbox
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (didResist) Color(0xFF10B981).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { didResist = !didResist }
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = didResist,
                            onCheckedChange = { didResist = it }
                        )
                        Spacer(Modifier.width(4.dp))
                        Column {
                            Text(
                                text = if (didResist) "🏆 I resisted and surfed the urge! (+25 XP)" else "I slipped during this urge",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (didResist) Color(0xFF047857) else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Honesty with yourself is the highest virtue in recovery.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("What did you feel / what helped?") },
                    placeholder = { Text("e.g. Drank cold water and went for a walk...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        CravingLog(
                            addictionType = addictionType,
                            intensity = selectedIntensity,
                            trigger = selectedTrigger,
                            durationMinutes = durationMinutes,
                            survived = didResist,
                            notes = notes,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            ) {
                Text("Save Craving Log")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun LogSlipDialog(
    addictionType: AddictionType,
    onDismiss: () -> Unit,
    onConfirm: (RelapseType, String, String, String) -> Unit
) {
    var selectedType by remember { mutableStateOf(RelapseType.SLIP) }
    var triggerText by remember { mutableStateOf("") }
    var lessonText by remember { mutableStateOf("") }
    var planText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💛", fontSize = 20.sp)
                Spacer(Modifier.width(8.dp))
                Text("Compassionate Slip Journal", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF6366F1).copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🌱 \"A slip is a temporary stumble on a mountain path, not falling back to the bottom. Your previous days of sobriety never vanish.\" — Dr. Gabor Maté",
                        fontSize = 11.sp,
                        color = Color(0xFF4F46E5),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(Spacing.sm)
                    )
                }

                // Relapse Type Choice
                Text(
                    text = "How would you like to handle this?",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (selectedType == RelapseType.SLIP) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedType = RelapseType.SLIP }
                ) {
                    Column(modifier = Modifier.padding(Spacing.sm)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedType == RelapseType.SLIP,
                                onClick = { selectedType = RelapseType.SLIP }
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "🛡️ Slip: Protect Streak & Learn",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            "Keeps your streak count alive. Treats the event as valuable data to build stronger armor.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 32.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (selectedType == RelapseType.RELAPSE) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedType = RelapseType.RELAPSE }
                ) {
                    Column(modifier = Modifier.padding(Spacing.sm)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedType == RelapseType.RELAPSE,
                                onClick = { selectedType = RelapseType.RELAPSE }
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "🔄 Full Reset: Fresh Day 1",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            "Resets your clock to 0 days for a pure fresh chapter with renewed commitment.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 32.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = triggerText,
                    onValueChange = { triggerText = it },
                    label = { Text("What triggered the slip?") },
                    placeholder = { Text("e.g. Work stress + tiredness + seeing someone else use") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = lessonText,
                    onValueChange = { lessonText = it },
                    label = { Text("Lesson Learned") },
                    placeholder = { Text("e.g. I cannot skip meals when stressed") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = planText,
                    onValueChange = { planText = it },
                    label = { Text("Future Protective Plan") },
                    placeholder = { Text("e.g. Call my partner immediately when feeling that urge") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        selectedType,
                        triggerText.ifBlank { "Unspecified trigger" },
                        lessonText.ifBlank { "Stay mindful" },
                        planText.ifBlank { "Deep breathing & SOS" }
                    )
                }
            ) {
                Text("Confirm with Compassion")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
