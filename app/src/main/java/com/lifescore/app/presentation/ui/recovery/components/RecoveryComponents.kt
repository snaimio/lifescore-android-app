package com.lifescore.app.presentation.ui.recovery.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.core.designsystem.components.GradientButton
import com.lifescore.app.core.designsystem.components.GradientCard
import com.lifescore.app.core.engine.CbtLesson
import com.lifescore.app.core.engine.RecoveryStats
import com.lifescore.app.data.local.entity.*

@Composable
fun SobrietyLiveHeroCard(
    stats: RecoveryStats,
    selectedAddiction: AddictionType,
    onSelectAddiction: (AddictionType) -> Unit,
    totalSlips: Int,
    onLogSlip: () -> Unit,
    onResetSobriety: () -> Unit,
    onOpenSOS: () -> Unit
) {
    GradientCard(
        gradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFF6366F1), // Indigo
                Color(0xFF8B5CF6), // Purple
                Color(0xFF06B6D4)  // Cyan
            )
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Addiction Type Selector Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                AddictionType.values().forEach { type ->
                    val isSelected = type == selectedAddiction
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.clickable { onSelectAddiction(type) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(type.emoji, fontSize = 14.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = type.displayName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF4F46E5) else Color.White
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(Spacing.md))

            // Subtitle
            Text(
                text = "${selectedAddiction.displayName} Freedom Clock",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.9f),
                letterSpacing = 1.sp
            )

            Spacer(Modifier.height(Spacing.xs))

            // Massive Days Counter
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${stats.totalDays}",
                    fontSize = 58.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (stats.totalDays == 1) "day" else "days",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            // Real-time ticking Clock: Hours, Minutes, Seconds
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.2f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ClockPill(value = stats.hours, unit = "hours")
                    Text(":", color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                    ClockPill(value = stats.minutes, unit = "mins")
                    Text(":", color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                    ClockPill(value = stats.seconds, unit = "secs")
                }
            }

            Spacer(Modifier.height(Spacing.sm))

            // Compassionate streak protection tag
            if (totalSlips > 0) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.25f)
                ) {
                    Text(
                        text = "🛡️ Streak Protected • $totalSlips ${if (totalSlips == 1) "slip" else "slips"} handled mindfully",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Spacer(Modifier.height(Spacing.xs))
            }

            Text(
                text = "Every second is a courageous act of reclaiming your life. ✨",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(Spacing.md))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onLogSlip,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.9f))
                ) {
                    Text("💛 Log Slip", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFEF4444),
                    modifier = Modifier.clickable { onOpenSOS() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🆘", fontSize = 12.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Panic / SOS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                TextButton(
                    onClick = onResetSobriety,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.7f))
                ) {
                    Text("🔄 Reset Clock", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ClockPill(value: Int, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = String.format("%02d", value),
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
        Text(
            text = unit,
            fontSize = 9.sp,
            color = Color.White.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun RecoveryStatsSummaryRow(
    moneySaved: Double,
    timeSavedHours: Double,
    itemsAvoided: Int,
    unitName: String,
    survivedCravingsCount: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            StatBox(
                title = "💰 Money Saved",
                value = String.format("$%.2f", moneySaved),
                subtitle = "Reclaimed wealth",
                color = Color(0xFF10B981),
                modifier = Modifier.weight(1f)
            )
            StatBox(
                title = "⏳ Time Reclaimed",
                value = String.format("%.1fh", timeSavedHours),
                subtitle = "Free hours gained",
                color = Color(0xFF3B82F6),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            StatBox(
                title = "🛡️ $unitName Avoided",
                value = "$itemsAvoided",
                subtitle = "Toxic doses avoided",
                color = Color(0xFF8B5CF6),
                modifier = Modifier.weight(1f)
            )
            StatBox(
                title = "🧘 Cravings Resisted",
                value = "$survivedCravingsCount",
                subtitle = "Victories in battle",
                color = Color(0xFFF59E0B),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatBox(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DailyPledgeCard(
    pledge: RecoveryPledge?,
    onMakePledge: () -> Unit,
    onEveningReflection: (String) -> Unit
) {
    var reflectionText by remember { mutableStateOf("") }
    var showReflectionDialog by remember { mutableStateOf(false) }

    GlassCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📜", fontSize = 20.sp)
                    Spacer(Modifier.width(Spacing.xs))
                    Column {
                        Text(
                            text = "Daily Freedom Pledge",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Daily morning commitment & evening reflection",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "+50 XP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(Spacing.sm))

            if (pledge == null) {
                Text(
                    text = "\"I commit to honoring my health, my mind, and my future by staying free from vice today.\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(Spacing.sm))
                GradientButton(
                    text = "✍️ Sign Today's Freedom Pledge (+50 XP)",
                    onClick = onMakePledge,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("✅", fontSize = 16.sp)
                        Spacer(Modifier.width(Spacing.xs))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Today's Pledge Signed & Active",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857)
                            )
                            Text(
                                text = pledge.pledgeText,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(Modifier.height(Spacing.xs))

                if (pledge.isEveningReflected) {
                    Text(
                        text = "🌙 Evening Reflection: \"${pledge.eveningReflection}\"",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    OutlinedButton(
                        onClick = { showReflectionDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🌙 Add Nightly Reflection")
                    }
                }
            }
        }
    }

    if (showReflectionDialog) {
        AlertDialog(
            onDismissRequest = { showReflectionDialog = false },
            title = { Text("Nightly Recovery Reflection") },
            text = {
                Column {
                    Text(
                        "How did you feel today? What gave you strength to stay clean?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(Spacing.sm))
                    OutlinedTextField(
                        value = reflectionText,
                        onValueChange = { reflectionText = it },
                        placeholder = { Text("e.g. Felt a craving after work but surfed it with deep breathing...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reflectionText.isNotBlank()) {
                            onEveningReflection(reflectionText)
                            showReflectionDialog = false
                        }
                    }
                ) {
                    Text("Save Reflection")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReflectionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun HealthMilestonesCard(
    milestones: List<RecoveryMilestone>,
    currentDays: Int,
    onUnlockMilestone: (RecoveryMilestone) -> Unit
) {
    GlassCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🏥", fontSize = 20.sp)
                    Spacer(Modifier.width(Spacing.xs))
                    Text(
                        text = "Biological Recovery Milestones",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${milestones.count { it.milestoneDays <= currentDays }}/${milestones.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(Spacing.sm))

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                milestones.forEach { milestone ->
                    val isReached = currentDays >= milestone.milestoneDays
                    val isUnlocked = milestone.isUnlocked || isReached

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isReached) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.sm),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isReached) milestone.medallionEmoji else "🔒",
                                fontSize = 24.sp
                            )
                            Spacer(Modifier.width(Spacing.sm))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = milestone.title,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isReached) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Day ${milestone.milestoneDays}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = milestone.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = milestone.healthBenefit,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SavingsGoalsSection(
    savingsGoals: List<RecoverySavingsGoal>,
    moneySaved: Double,
    onAddGoal: (String, Double, String) -> Unit,
    onDeleteGoal: (RecoverySavingsGoal) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var goalTitle by remember { mutableStateOf("") }
    var goalAmount by remember { mutableStateOf("") }
    var goalEmoji by remember { mutableStateOf("🎁") }

    GlassCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎯", fontSize = 20.sp)
                    Spacer(Modifier.width(Spacing.xs))
                    Column {
                        Text(
                            text = "Recovery Reward Goals",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Reward yourself with money saved from quitting",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add Goal", tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(Modifier.height(Spacing.sm))

            if (savingsGoals.isEmpty()) {
                Text(
                    text = "No reward goals added yet. Set a goal like 'New Running Shoes ($120)' to celebrate your savings!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    savingsGoals.forEach { goal ->
                        val progress = (moneySaved / goal.targetAmount).toFloat().coerceIn(0f, 1f)
                        val isAchieved = moneySaved >= goal.targetAmount

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isAchieved) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(Spacing.sm)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(goal.iconEmoji, fontSize = 18.sp)
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = goal.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = String.format("$%.0f / $%.0f", moneySaved.coerceAtMost(goal.targetAmount), goal.targetAmount),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isAchieved) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = if (isAchieved) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                                if (isAchieved) {
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = "🎉 Goal Funded! You earned this reward!",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Reward Goal") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    OutlinedTextField(
                        value = goalTitle,
                        onValueChange = { goalTitle = it },
                        label = { Text("Reward Title") },
                        placeholder = { Text("e.g. New Running Shoes, Spa Day") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = goalAmount,
                        onValueChange = { goalAmount = it },
                        label = { Text("Target Cost ($)") },
                        placeholder = { Text("120") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = goalEmoji,
                        onValueChange = { goalEmoji = it },
                        label = { Text("Icon Emoji") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = goalAmount.toDoubleOrNull() ?: 50.0
                        if (goalTitle.isNotBlank()) {
                            onAddGoal(goalTitle, amount, goalEmoji.ifBlank { "🎁" })
                            showAddDialog = false
                            goalTitle = ""
                            goalAmount = ""
                        }
                    }
                ) {
                    Text("Add Goal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun CbtThoughtToolsCard(
    lessons: List<CbtLesson>
) {
    var expandedLessonId by remember { mutableStateOf<String?>(null) }

    GlassCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🧠", fontSize = 20.sp)
                Spacer(Modifier.width(Spacing.xs))
                Column {
                    Text(
                        text = "CBT & Scientific Recovery Tools",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Evidence-based frameworks to disarm urges",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(Spacing.sm))

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                lessons.forEach { lesson ->
                    val isExpanded = expandedLessonId == lesson.id

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandedLessonId = if (isExpanded) null else lesson.id
                            }
                    ) {
                        Column(modifier = Modifier.padding(Spacing.sm)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(lesson.emoji, fontSize = 20.sp)
                                    Spacer(Modifier.width(Spacing.xs))
                                    Column {
                                        Text(
                                            text = lesson.title,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = lesson.subtitle,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Icon(
                                    if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (isExpanded) {
                                Spacer(Modifier.height(Spacing.xs))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                Spacer(Modifier.height(Spacing.xs))
                                Text(
                                    text = lesson.coreConcept,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                ) {
                                    Text(
                                        text = "⚡ Action: ${lesson.actionableExercise}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
