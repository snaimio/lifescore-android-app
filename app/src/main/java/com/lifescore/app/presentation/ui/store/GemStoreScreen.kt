package com.lifescore.app.presentation.ui.store

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.core.engine.GemPackage
import com.lifescore.app.core.engine.GemSystem
import com.lifescore.app.core.engine.GoldToGemsConverter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GemStoreScreen(
    onNavigateBack: () -> Unit,
    onOpenSupporter: () -> Unit,
    onOpenCosmetics: () -> Unit
) {
    val context = LocalContext.current
    var gemBalance by remember { mutableIntStateOf(145) }
    var goldBalance by remember { mutableIntStateOf(850) }
    val isSupporter by remember { mutableStateOf(true) }

    var conversionGems by remember { mutableFloatStateOf(5f) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text("💎 Gem Store", fontWeight = FontWeight.Bold)
                },
                actions = {
                    IconButton(onClick = onOpenCosmetics) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = "Cosmetics Store", tint = MaterialTheme.colorScheme.primary)
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
            // 1. Current Balance & Cosmetics Shortcut
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("YOUR GEM BALANCE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("💎", fontSize = 26.sp)
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        "$gemBalance",
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            Button(
                                onClick = onOpenCosmetics,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Text("🎨 Spend Gems", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        Spacer(Modifier.height(Spacing.xs))
                        Text(
                            "Gems are used exclusively for cosmetic skins, themes, and badges. Core features are always 100% free.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 2. Supporter Gold-to-Gems Converter
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🪙", fontSize = 20.sp)
                                Spacer(Modifier.width(6.dp))
                                Text("Gold to Gems Converter", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            if (isSupporter) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFE8F5E9)
                                ) {
                                    Text("SUPPORTER PERK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }

                        Spacer(Modifier.height(Spacing.xs))
                        Text("Rate: 100 Gold = 1 Gem • Available Gold: $goldBalance 🪙", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(Modifier.height(Spacing.sm))

                        val maxConvertibleGems = GoldToGemsConverter.convertGoldToGems(goldBalance).coerceAtLeast(1)
                        Slider(
                            value = conversionGems,
                            onValueChange = { conversionGems = it },
                            valueRange = 1f..maxConvertibleGems.toFloat().coerceAtLeast(1f),
                            steps = (maxConvertibleGems - 2).coerceAtLeast(0)
                        )

                        val gemsToReceive = conversionGems.toInt()
                        val goldRequired = gemsToReceive * 100

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Convert $goldRequired 🪙 ➔ +$gemsToReceive 💎", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                            Button(
                                onClick = {
                                    if (goldBalance >= goldRequired) {
                                        goldBalance -= goldRequired
                                        gemBalance += gemsToReceive
                                        Toast.makeText(context, "Converted $goldRequired Gold into $gemsToReceive Gems! 💎", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Not enough Gold", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                enabled = goldBalance >= goldRequired
                            ) {
                                Text("Convert", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 3. Purchase Gem Packages
            item {
                Text(
                    "Purchase Gems",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(GemSystem.availablePackages) { pkg ->
                GemPackageCard(
                    pkg = pkg,
                    onPurchase = {
                        gemBalance += (pkg.gemsCount + pkg.bonusGems)
                        Toast.makeText(context, "Purchased ${pkg.name}! +${pkg.gemsCount + pkg.bonusGems} Gems 💎", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // 4. Free Gameplay Ways to Earn Gems
            item {
                Text(
                    "Earn Free Gems in Gameplay",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(Spacing.md), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        EarnMethodRow("⚔️ Complete 5 Quests", "+10 Gems", "Regular daily quest completions")
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        EarnMethodRow("🔥 7-Day Unbroken Streak", "+20 Gems", "Maintain compounding habits")
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        EarnMethodRow("🏆 30-Day Milestone", "+50 Gems", "Long-term consistency mastery")
                    }
                }
            }

            // 5. Supporter Banner
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("👑", fontSize = 32.sp)
                        Spacer(Modifier.width(Spacing.md))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Become a LifeScore Supporter", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Convert gold to gems, get exclusive skins & keep the app free.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                        }
                        Button(
                            onClick = onOpenSupporter,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("View", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(Spacing.xl))
            }
        }
    }
}

@Composable
private fun GemPackageCard(
    pkg: GemPackage,
    onPurchase: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(
            1.dp,
            if (pkg.isPopular) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(pkg.iconEmoji, fontSize = 22.sp)
                    }
                }
                Spacer(Modifier.width(Spacing.md))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(pkg.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        pkg.badgeLabel?.let { badge ->
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    badge,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        "${pkg.gemsCount} Gems" + if (pkg.bonusGems > 0) " (+${pkg.bonusGems} Bonus)" else "",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Button(
                onClick = onPurchase,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(pkg.priceFormatted, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun EarnMethodRow(
    title: String,
    reward: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                reward,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
        }
    }
}
