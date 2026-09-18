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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.navigation.NavController
import com.lifescore.app.core.designsystem.components.CardVariant
import com.lifescore.app.core.designsystem.LifeScoreShapes
import com.lifescore.app.core.designsystem.Space
import com.lifescore.app.core.designsystem.components.LifeCard
import com.lifescore.app.core.designsystem.components.SectionHeader
import com.lifescore.app.core.designsystem.components.StaggeredAppear
import com.lifescore.app.core.engine.FeatureCategory
import com.lifescore.app.core.engine.FeatureItem
import com.lifescore.app.core.engine.FeatureUnlockManager
import com.lifescore.app.core.engine.UserPhase
import com.lifescore.app.core.util.ShareCardData
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.presentation.navigation.Screen
import com.lifescore.app.presentation.ui.share.ShareScoreCardDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    currentPhase: UserPhase = UserPhase.EXPERT,
    navController: NavController
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<FeatureCategory?>(null) }
    var showShareCardDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val features = remember(searchQuery, selectedCategoryFilter) {
        var list = FeatureUnlockManager.allFeatures
        if (searchQuery.isNotBlank()) {
            list = list.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true) ||
                it.category.displayName.contains(searchQuery, ignoreCase = true)
            }
        }
        selectedCategoryFilter?.let { cat ->
            list = list.filter { it.category == cat }
        }
        list
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Explore",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Community, Quests & 40+ Features",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showShareCardDialog = true }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share LifeScore",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Space.screenH),
            verticalArrangement = Arrangement.spacedBy(Space.cardGap),
            contentPadding = PaddingValues(top = Space.xs, bottom = Space.xxxl)
        ) {
            // ==========================================
            // 1. COMMUNITY & FRIENDS FEED PREVIEW
            // ==========================================
            item {
                StaggeredAppear(index = 0) {
                    LifeCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(Screen.FriendsFeed.route) },
                        variant = CardVariant.Default
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🔥", fontSize = 20.sp)
                                Spacer(Modifier.width(Space.sm))
                                Text(
                                    "Friends & Social Feed",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Surface(
                                shape = LifeScoreShapes.tag,
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            ) {
                                Text(
                                    text = "3 Live Nudges",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = Space.sm, vertical = Space.xxs)
                                )
                            }
                        }

                        Spacer(Modifier.height(Space.xs))
                        Text(
                            text = "Alex just completed a 14-day meditation streak! Send a high-five or nudge your squad.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ==========================================
            // 2. VIRAL REFERRALS CARD
            // ==========================================
            item {
                StaggeredAppear(index = 1) {
                    LifeCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(Screen.ViralReferrals.route) },
                        variant = CardVariant.Cream
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🎁", fontSize = 22.sp)
                                }
                            }
                            Spacer(Modifier.width(Space.md))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Invite Friends, Get Free Pro", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("Share your invite link. Both you & your friend receive 1 month of LifeScore Pro.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }

            // ==========================================
            // 3. HIDDEN GEMS & RPG HIGHLIGHTS
            // ==========================================
            item {
                SectionHeader(
                    title = "🌟 Featured Highlights",
                    subtitle = "Specialized mini-apps and multiplayer features"
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Space.sm)
                ) {
                    Surface(
                        shape = LifeScoreShapes.cardSmall,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { navController.navigate(Screen.Combat.route) }
                    ) {
                        Column(modifier = Modifier.padding(Space.sm)) {
                            Text("⚔️", fontSize = 24.sp)
                            Spacer(Modifier.height(Space.xs))
                            Text("Boss Raids", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("Team up to beat bosses", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Surface(
                        shape = LifeScoreShapes.cardSmall,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { navController.navigate(Screen.VirtualPet.route) }
                    ) {
                        Column(modifier = Modifier.padding(Space.sm)) {
                            Text("🐥", fontSize = 24.sp)
                            Spacer(Modifier.height(Space.xs))
                            Text("Virtual Pet", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("Evolves with habits", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Surface(
                        shape = LifeScoreShapes.cardSmall,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { navController.navigate(Screen.LeagueTiers.route) }
                    ) {
                        Column(modifier = Modifier.padding(Space.sm)) {
                            Text("🏆", fontSize = 24.sp)
                            Spacer(Modifier.height(Space.xs))
                            Text("10-Tier Leagues", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("Weekly leaderboards", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // ==========================================
            // 4. HOLOGRAPHIC SHARE CARD PREVIEW
            // ==========================================
            item {
                LifeCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showShareCardDialog = true },
                    variant = CardVariant.Default
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = LifeScoreShapes.button,
                            color = Color(0xFF6366F1).copy(alpha = 0.15f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("✨", fontSize = 22.sp)
                            }
                        }
                        Spacer(Modifier.width(Space.md))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Generate Holographic Share Card", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("Share your 360° Life Matrix & level milestones on Instagram or Twitter", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // ==========================================
            // 5. DIRECTORY SEARCH & CATEGORY FILTERS
            // ==========================================
            item {
                Column(verticalArrangement = Arrangement.spacedBy(Space.xs)) {
                    SectionHeader(
                        title = "🔍 All Features Directory",
                        subtitle = "Access every specialized module and tracker"
                    )
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search 40+ features...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = LifeScoreShapes.input,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Space.xs),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategoryFilter == null,
                            onClick = { selectedCategoryFilter = null },
                            label = { Text("All (${FeatureUnlockManager.allFeatures.size})") },
                            shape = LifeScoreShapes.chip
                        )
                    }
                    items(FeatureCategory.values()) { cat ->
                        val isSelected = selectedCategoryFilter == cat
                        val count = FeatureUnlockManager.getFeaturesByCategory(cat).size
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategoryFilter = if (isSelected) null else cat },
                            label = { Text("${cat.iconEmoji} ${cat.displayName} ($count)") },
                            shape = LifeScoreShapes.chip
                        )
                    }
                }
            }

            // List of Features
            items(features, key = { it.id }) { feature ->
                LifeCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(feature.route) },
                    variant = CardVariant.Default
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = LifeScoreShapes.button,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(feature.iconEmoji, fontSize = 20.sp)
                            }
                        }
                        Spacer(Modifier.width(Space.md))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    feature.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.width(Space.xs))
                                Surface(
                                    shape = LifeScoreShapes.tag,
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ) {
                                    Text(
                                        feature.category.displayName,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = Space.xxs, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                feature.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    if (showShareCardDialog) {
        ShareScoreCardDialog(
            data = ShareCardData(
                userName = "Alex",
                score = 824,
                level = 12,
                streak = 14,
                title = "The Architect",
                dimensionScores = mapOf(
                    DimensionType.HEALTH to 88,
                    DimensionType.FITNESS to 76,
                    DimensionType.CAREER to 84,
                    DimensionType.LEARNING to 92,
                    DimensionType.MENTAL_HEALTH to 80,
                    DimensionType.WEALTH to 75,
                    DimensionType.RELATIONSHIPS to 85,
                    DimensionType.SOCIAL_LIFE to 70
                )
            ),
            onDismiss = { showShareCardDialog = false }
        )
    }
}
