package com.lifescore.app.presentation.store

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.*
import com.lifescore.app.domain.model.CustomUserReward
import com.lifescore.app.domain.model.StoreCategory
import com.lifescore.app.domain.model.StoreProductItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardStoreScreen(
    viewModel: RewardStoreViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.recentSuccessMessage, uiState.errorMessage) {
        uiState.recentSuccessMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
        uiState.errorMessage?.let { err ->
            Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LifeScore Reward Store", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.selectedTab == StoreTab.CUSTOM) {
                        FilledTonalButton(
                            onClick = { viewModel.openCreateDialog() },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("New Reward", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 1. Hero Coin Wallet Banner
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = Color(0xFFFFD700), modifier = Modifier.size(44.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🪙", fontSize = 22.sp)
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("LIFECOIN BALANCE", fontSize = 10.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                Text("${uiState.userProfile.coinBalance} Coins", fontSize = 22.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }

                        if (uiState.boosterState.isDoubleXpActive) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, Color(0xFF10B981))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🚀", fontSize = 12.sp)
                                    Spacer(Modifier.width(4.dp))
                                    Text("2x Multiplier ACTIVE", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("🛡️ ${uiState.boosterState.streakShieldsAvailable} Streak Shields", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        Text("⚡ ${uiState.boosterState.instantSkipPassesAvailable} Skip Passes", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        Text("🏆 ${uiState.totalCoinsEarnedLifetime} Lifetime", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // 2. Tab Navigation
            TabRow(
                selectedTabIndex = uiState.selectedTab.ordinal,
                modifier = Modifier.fillMaxWidth()
            ) {
                StoreTab.values().forEach { tab ->
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        text = { Text(tab.title, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                }
            }

            // 3. Tab Views
            when (uiState.selectedTab) {
                StoreTab.CUSTOM -> {
                    CustomRewardsView(
                        rewards = uiState.customRewards,
                        userCoins = uiState.userProfile.coinBalance,
                        onRedeem = { viewModel.redeemReward(it) }
                    )
                }
                StoreTab.PREMIUM -> {
                    PremiumStoreView(
                        products = uiState.storeProducts,
                        userCoins = uiState.userProfile.coinBalance,
                        onBuy = { viewModel.buyProduct(it) }
                    )
                }
                StoreTab.HISTORY -> {
                    HistoryLedgerView(uiState = uiState)
                }
            }
        }
    }

    // Create Custom Reward Dialog
    if (uiState.isCreateCustomDialogOpen) {
        var title by remember { mutableStateOf("") }
        var emoji by remember { mutableStateOf("🎁") }
        var cost by remember { mutableStateOf("150") }
        var description by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { viewModel.closeCreateDialog() }) {
            Card(
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Create Custom Reward", fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Reward Name (e.g. Netflix, Sushi)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = emoji,
                            onValueChange = { emoji = it },
                            label = { Text("Emoji") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = cost,
                            onValueChange = { cost = it },
                            label = { Text("Coin Cost") },
                            singleLine = true,
                            modifier = Modifier.weight(1.5f)
                        )
                    }
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Motivation / Rules (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val c = cost.toIntOrNull() ?: 150
                            viewModel.createCustomReward(title, emoji, c, description)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Add to My Rewards", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomRewardsView(
    rewards: List<CustomUserReward>,
    userCoins: Int,
    onRedeem: (CustomUserReward) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(rewards) { rew ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(rew.emoji, fontSize = 22.sp)
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(rew.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        if (rew.description.isNotBlank()) {
                            Text(rew.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFFD700).copy(alpha = 0.2f)
                            ) {
                                Text("🪙 ${rew.coinCost} Coins", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                            Spacer(Modifier.width(8.dp))
                            Text("Redeemed ${rew.redemptionCount}x", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                        }
                    }

                    Spacer(Modifier.width(8.dp))

                    Button(
                        onClick = { onRedeem(rew) },
                        enabled = userCoins >= rew.coinCost,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Redeem 🎁", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item { Spacer(Modifier.height(30.dp)) }
    }
}

@Composable
fun PremiumStoreView(
    products: List<StoreProductItem>,
    userCoins: Int,
    onBuy: (StoreProductItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        StoreCategory.values().filter { it != StoreCategory.CUSTOM_REWARD }.forEach { category ->
            val catProducts = products.filter { it.category == category }
            if (catProducts.isNotEmpty()) {
                item {
                    Text("${category.icon} ${category.displayName}s", fontWeight = FontWeight.Black, fontSize = 16.sp)
                }

                items(catProducts) { product ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(product.emoji, fontSize = 22.sp)
                                }
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(product.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    product.badgeLabel?.let { badge ->
                                        Spacer(Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFFEF4444)
                                        ) {
                                            Text(badge, fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                        }
                                    }
                                }
                                Text(product.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline, lineHeight = 15.sp)
                                Spacer(Modifier.height(4.dp))
                                Text("🪙 ${product.coinCost} LifeCoins", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFD700))
                            }

                            Spacer(Modifier.width(8.dp))

                            if (product.isPurchased) {
                                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF10B981).copy(alpha = 0.2f)) {
                                    Text("OWNED ✓", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            } else {
                                Button(
                                    onClick = { onBuy(product) },
                                    enabled = userCoins >= product.coinCost,
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Buy 🛒", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(30.dp)) }
    }
}

@Composable
fun HistoryLedgerView(uiState: RewardStoreUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Lifetime Earned", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        Text("+${uiState.totalCoinsEarnedLifetime}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFF10B981))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total Spent", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        Text("-${uiState.totalCoinsSpent}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFFEF4444))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Current Balance", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        Text("${uiState.userProfile.coinBalance}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFFFFD700))
                    }
                }
            }
        }

        item {
            Text("Ledger & Transaction History", fontWeight = FontWeight.Black, fontSize = 15.sp, modifier = Modifier.padding(top = 8.dp))
        }

        items(uiState.transactions) { tx ->
            val isEarn = tx.coinsAmount > 0
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(tx.itemTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${tx.formattedDate} • ${tx.category.displayName}", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                    }

                    Text(
                        text = if (isEarn) "+${tx.coinsAmount} Coins" else "${tx.coinsAmount} Coins",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = if (isEarn) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                }
            }
        }

        item { Spacer(Modifier.height(30.dp)) }
    }
}
