package com.lifescore.app.presentation.ui.explore

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.lifescore.app.core.engine.FeatureCategory
import com.lifescore.app.core.engine.FeatureItem
import com.lifescore.app.core.engine.FeatureUnlockManager
import com.lifescore.app.core.engine.UserPhase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreSectionScreen(
    currentPhase: UserPhase,
    onNavigateToRoute: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedCategoryFilter by remember { mutableStateOf<FeatureCategory?>(null) }
    var showOnlyUnlocked by remember { mutableStateOf(false) }
    var powerUserUnlockAll by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val effectivePhase = if (powerUserUnlockAll) UserPhase.EXPERT else currentPhase

    val features = remember(selectedCategoryFilter, showOnlyUnlocked, effectivePhase) {
        var list = FeatureUnlockManager.allFeatures
        selectedCategoryFilter?.let { cat ->
            list = list.filter { it.category == cat }
        }
        if (showOnlyUnlocked) {
            list = list.filter { effectivePhase.ordinal >= it.minPhase.ordinal }
        }
        list
    }

    val unlockedCount = FeatureUnlockManager.getUnlockedFeatures(effectivePhase).size
    val totalCount = FeatureUnlockManager.allFeatures.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Explore LifeScore Directory", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("$unlockedCount of $totalCount Features Available • ${effectivePhase.title}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            item {
                Spacer(Modifier.height(Spacing.xs))

                // Phase Progress Header
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(effectivePhase.badgeEmoji, fontSize = 24.sp)
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(effectivePhase.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(effectivePhase.subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            ) {
                                Text(
                                    text = "${((unlockedCount.toFloat() / totalCount.toFloat()) * 100).toInt()}% Unlocked",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(Spacing.sm))

                        LinearProgressIndicator(
                            progress = { unlockedCount.toFloat() / totalCount.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )

                        Spacer(Modifier.height(Spacing.sm))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Power User Mode (Unlock All)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Switch(
                                checked = powerUserUnlockAll,
                                onCheckedChange = {
                                    powerUserUnlockAll = it
                                    if (it) {
                                        Toast.makeText(context, "🔓 All 88+ features and 15 trackers unlocked!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.height(24.dp)
                            )
                        }
                    }
                }
            }

            // Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategoryFilter == null && !showOnlyUnlocked,
                            onClick = {
                                selectedCategoryFilter = null
                                showOnlyUnlocked = false
                            },
                            label = { Text("All ($totalCount)") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = showOnlyUnlocked,
                            onClick = {
                                showOnlyUnlocked = !showOnlyUnlocked
                            },
                            label = { Text("🔓 Unlocked ($unlockedCount)") }
                        )
                    }
                    items(FeatureCategory.values()) { cat ->
                        val isSelected = selectedCategoryFilter == cat
                        val count = FeatureUnlockManager.getFeaturesByCategory(cat).size
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedCategoryFilter = if (isSelected) null else cat
                            },
                            label = { Text("${cat.iconEmoji} ${cat.displayName} ($count)") }
                        )
                    }
                }
            }

            // Features List
            items(features, key = { it.id }) { feature ->
                val isUnlocked = effectivePhase.ordinal >= feature.minPhase.ordinal
                FeatureDirectoryCard(
                    feature = feature,
                    isUnlocked = isUnlocked,
                    onOpen = {
                        if (isUnlocked) {
                            onNavigateToRoute(feature.route)
                        } else {
                            val requirement = FeatureUnlockManager.getPhaseRequirementDescription(feature.minPhase)
                            Toast.makeText(context, "🔒 $requirement. Enable Power User Mode to open now!", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }

            item {
                Spacer(Modifier.height(Spacing.xl))
            }
        }
    }
}

@Composable
private fun FeatureDirectoryCard(
    feature: FeatureItem,
    isUnlocked: Boolean,
    onOpen: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
    ) {
        Row(
            modifier = Modifier.padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isUnlocked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(feature.iconEmoji, fontSize = 22.sp)
                }
            }

            Spacer(Modifier.width(Spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = feature.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(6.dp))
                    if (!isUnlocked) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = "LOCKED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = if (isUnlocked) feature.description else FeatureUnlockManager.getPhaseRequirementDescription(feature.minPhase),
                    fontSize = 12.sp,
                    color = if (isUnlocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            IconButton(onClick = onOpen) {
                Icon(
                    if (isUnlocked) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Lock,
                    contentDescription = if (isUnlocked) "Open" else "Locked",
                    tint = if (isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
