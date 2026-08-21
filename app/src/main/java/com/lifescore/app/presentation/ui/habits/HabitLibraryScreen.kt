package com.lifescore.app.presentation.ui.habits

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.lifescore.app.core.designsystem.components.GradientButton
import com.lifescore.app.data.Habit
import com.lifescore.app.data.HabitData
import com.lifescore.app.domain.model.DimensionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitLibraryScreen(
    navController: NavController
) {
    var selectedDimension by remember { mutableStateOf<DimensionType?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val filteredHabits = remember(selectedDimension, searchQuery, selectedDifficulty) {
        HabitData.allHabits.filter { habit ->
            val matchesDim = selectedDimension == null || habit.dimension == selectedDimension
            val matchesQuery = searchQuery.isBlank() ||
                    habit.title.contains(searchQuery, ignoreCase = true) ||
                    habit.description.contains(searchQuery, ignoreCase = true)
            val matchesDiff = selectedDifficulty == null || habit.difficulty.equals(selectedDifficulty, ignoreCase = true)
            matchesDim && matchesQuery && matchesDiff
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
                                "100 Habit Catalog",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                "8 Life Dimensions • Science-Backed Micro-Habits",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.padding(end = Spacing.sm)
                        ) {
                            Text(
                                text = "${filteredHabits.size} / 100",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                // Search Bar
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Spacing.xs),
                        placeholder = { Text("Search habits (e.g. gratitude, walk, savings)...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Dimension Filter Chips
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedDimension == null,
                                onClick = { selectedDimension = null },
                                label = { Text("All (100)", style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                        items(DimensionType.values()) { dim ->
                            FilterChip(
                                selected = selectedDimension == dim,
                                onClick = { selectedDimension = if (selectedDimension == dim) null else dim },
                                label = { Text(dim.displayName, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }

                // Difficulty Filter Row
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Easy", "Medium", "Hard").forEach { diff ->
                            FilterChip(
                                selected = selectedDifficulty == diff,
                                onClick = { selectedDifficulty = if (selectedDifficulty == diff) null else diff },
                                label = { Text(diff, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }

                // Habit Cards
                items(filteredHabits, key = { it.id }) { habit ->
                    HabitCardItem(
                        habit = habit,
                        onAddQuest = {
                            Toast.makeText(context, "Added '${habit.title}' to Today's Quests! 🎯", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                item {
                    Spacer(Modifier.height(Spacing.xl))
                }
            }
        }
    }
}

@Composable
fun HabitCardItem(
    habit: Habit,
    onAddQuest: () -> Unit
) {
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "#${habit.id}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(Modifier.width(Spacing.xs))
                    Text(
                        text = habit.dimension.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (habit.difficulty) {
                            "Easy" -> Color(0xFF10B981).copy(alpha = 0.15f)
                            "Medium" -> Color(0xFFF59E0B).copy(alpha = 0.15f)
                            else -> Color(0xFFEF4444).copy(alpha = 0.15f)
                        }
                    ) {
                        Text(
                            text = habit.difficulty,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (habit.difficulty) {
                                "Easy" -> Color(0xFF10B981)
                                "Medium" -> Color(0xFFF59E0B)
                                else -> Color(0xFFEF4444)
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = "+${habit.xpReward} XP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(Spacing.xs))

            Text(
                text = habit.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = habit.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(Spacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⏱️ ~${habit.estimatedMinutes} min",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                OutlinedButton(
                    onClick = onAddQuest,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add Quest", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
