package com.lifescore.app.presentation.ui.atomichabits

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.lifescore.app.domain.model.atomichabits.HabitCategory
import com.lifescore.app.domain.model.atomichabits.HabitScorecardItem

@Composable
fun HabitScorecardContent(
    scorecards: List<HabitScorecardItem>,
    onAddHabit: (String, HabitCategory) -> Unit,
    onUpdateCategory: (String, HabitCategory) -> Unit,
    onDeleteHabit: (String) -> Unit
) {
    var habitNameInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(HabitCategory.GOOD) }

    val goodCount = scorecards.count { it.category == HabitCategory.GOOD }
    val badCount = scorecards.count { it.category == HabitCategory.BAD }
    val neutralCount = scorecards.count { it.category == HabitCategory.NEUTRAL }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        // Awareness Overview Summary Card
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
                        "📋 Habit Scorecard (Awareness Engine)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Until you make the unconscious conscious, it will direct your life and you will call it fate. Classify daily behaviors as (+), (-), or (=).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(Spacing.md))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.15f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(Spacing.sm)
                            ) {
                                Text("✅ Good (+)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                                Text("$goodCount", fontSize = 18.sp, fontWeight = FontWeight.Black)
                            }
                        }
                        Spacer(Modifier.width(Spacing.xs))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFEF4444).copy(alpha = 0.15f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(Spacing.sm)
                            ) {
                                Text("❌ Bad (-)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                                Text("$badCount", fontSize = 18.sp, fontWeight = FontWeight.Black)
                            }
                        }
                        Spacer(Modifier.width(Spacing.xs))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(Spacing.sm)
                            ) {
                                Text("➖ Neutral (=)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$neutralCount", fontSize = 18.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }

        // Add Habit Input Card
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
                        "Add Behavior to Audit",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = habitNameInput,
                        onValueChange = { habitNameInput = it },
                        placeholder = { Text("e.g. Scrolling phone before breakfast...") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        HabitCategory.values().forEach { cat ->
                            val isSelected = selectedCategory == cat
                            OutlinedButton(
                                onClick = { selectedCategory = cat },
                                shape = RoundedCornerShape(8.dp),
                                border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "${cat.symbol} ${cat.displayName.substringBefore(" ")}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    GradientButton(
                        text = "Add to Scorecard (+15 XP)",
                        onClick = {
                            if (habitNameInput.isNotBlank()) {
                                onAddHabit(habitNameInput.trim(), selectedCategory)
                                habitNameInput = ""
                            }
                        },
                        enabled = habitNameInput.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Habits List Header
        item {
            Text(
                text = "Audit List (${scorecards.size} habits)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        items(scorecards, key = { it.id }) { item ->
            ScorecardRow(
                item = item,
                onUpdate = { onUpdateCategory(item.id, it) },
                onDelete = { onDeleteHabit(item.id) }
            )
        }

        item {
            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

@Composable
fun ScorecardRow(
    item: HabitScorecardItem,
    onUpdate: (HabitCategory) -> Unit,
    onDelete: () -> Unit
) {
    val categoryColor = when (item.category) {
        HabitCategory.GOOD -> Color(0xFF10B981)
        HabitCategory.BAD -> Color(0xFFEF4444)
        HabitCategory.NEUTRAL -> MaterialTheme.colorScheme.outline
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = categoryColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = item.category.symbol,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = categoryColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(Modifier.width(Spacing.sm))
                Text(
                    text = item.habitName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                HabitCategory.values().forEach { cat ->
                    IconButton(
                        onClick = { onUpdate(cat) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Text(
                            text = cat.symbol,
                            fontSize = 13.sp,
                            fontWeight = if (item.category == cat) FontWeight.Black else FontWeight.Normal,
                            color = if (item.category == cat) categoryColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    }
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
