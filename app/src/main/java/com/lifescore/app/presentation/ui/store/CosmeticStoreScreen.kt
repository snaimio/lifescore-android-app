package com.lifescore.app.presentation.ui.store

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.domain.model.CosmeticCategory
import com.lifescore.app.domain.model.CosmeticItem
import com.lifescore.app.domain.model.CosmeticStoreCatalog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CosmeticStoreScreen(
    onNavigateBack: () -> Unit,
    onOpenGemStore: () -> Unit,
    onOpenSupporter: () -> Unit
) {
    val context = LocalContext.current
    var gemBalance by remember { mutableIntStateOf(145) }
    var selectedCategory by remember { mutableStateOf<CosmeticCategory?>(null) }

    // State for owned and equipped item IDs
    val ownedItemIds = remember { mutableStateListOf("skin_warrior", "badge_supporter") }
    var equippedAvatarId by remember { mutableStateOf("skin_warrior") }
    var equippedThemeId by remember { mutableStateOf("theme_cosmic") }
    var equippedBadgeId by remember { mutableStateOf("badge_supporter") }

    val filteredItems = remember(selectedCategory) {
        if (selectedCategory == null) CosmeticStoreCatalog.items
        else CosmeticStoreCatalog.items.filter { it.category == selectedCategory }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text("🎨 Cosmetic Vault", fontWeight = FontWeight.Bold)
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        onClick = onOpenGemStore,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("💎", fontSize = 14.sp)
                            Spacer(Modifier.width(4.dp))
                            Text("$gemBalance", fontWeight = FontWeight.Black, fontSize = 13.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(Modifier.width(4.dp))
                            Text("+", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text("✨ All Items") }
                        )
                    }
                    items(CosmeticCategory.values()) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text("${category.iconEmoji} ${category.displayName}") }
                        )
                    }
                }
            }

            // Notice Header
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🛡️", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Cosmetics are 100% optional visual upgrades that support our independent development.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Cosmetic Items List
            items(filteredItems) { item ->
                val isOwned = ownedItemIds.contains(item.id)
                val isEquipped = when (item.category) {
                    CosmeticCategory.AVATAR -> equippedAvatarId == item.id
                    CosmeticCategory.THEME -> equippedThemeId == item.id
                    CosmeticCategory.BADGE -> equippedBadgeId == item.id
                    else -> false
                }

                CosmeticItemCard(
                    item = item,
                    isOwned = isOwned,
                    isEquipped = isEquipped,
                    onEquip = {
                        when (item.category) {
                            CosmeticCategory.AVATAR -> equippedAvatarId = item.id
                            CosmeticCategory.THEME -> equippedThemeId = item.id
                            CosmeticCategory.BADGE -> equippedBadgeId = item.id
                            else -> {}
                        }
                        Toast.makeText(context, "Equipped ${item.name}!", Toast.LENGTH_SHORT).show()
                    },
                    onPurchase = {
                        if (gemBalance >= item.gemCost) {
                            gemBalance -= item.gemCost
                            ownedItemIds.add(item.id)
                            Toast.makeText(context, "Unlocked ${item.name}! 💎", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Need ${item.gemCost - gemBalance} more Gems. Opening Gem Store...", Toast.LENGTH_SHORT).show()
                            onOpenGemStore()
                        }
                    },
                    onSupporterClick = onOpenSupporter
                )
            }

            item {
                Spacer(Modifier.height(Spacing.xl))
            }
        }
    }
}

@Composable
private fun CosmeticItemCard(
    item: CosmeticItem,
    isOwned: Boolean,
    isEquipped: Boolean,
    onEquip: () -> Unit,
    onPurchase: () -> Unit,
    onSupporterClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(
            1.dp,
            if (isEquipped) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(item.emoji, fontSize = 24.sp)
                    }
                }

                Spacer(Modifier.width(Spacing.md))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        if (item.isSupporterExclusive) {
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer
                            ) {
                                Text(
                                    "SUPPORTER",
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        item.season?.let { season ->
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    season,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Text(item.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.width(Spacing.sm))

            // Action Button
            when {
                isEquipped -> {
                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        enabled = false
                    ) {
                        Text("Active", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
                isOwned -> {
                    Button(
                        onClick = onEquip,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Equip", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
                item.isSupporterExclusive -> {
                    Button(
                        onClick = onSupporterClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text("Unlock 👑", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
                else -> {
                    Button(
                        onClick = onPurchase,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("${item.gemCost} 💎", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
