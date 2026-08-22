package com.lifescore.app.presentation.ui.rewards

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.data.local.entity.CustomRewardEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomRewardsScreen(
    viewModel: CustomRewardsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Custom Gold Rewards Store", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Price Your Own Real-World Treats", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.toggleCreateSheet(true) },
                containerColor = Color(0xFFFFD54F),
                contentColor = Color(0xFF5D4037)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Custom Reward")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Gold Balance Hero Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFFFFB300),
                                        Color(0xFFFFA000),
                                        Color(0xFFFF6F00)
                                    )
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = Color.White.copy(alpha = 0.25f),
                                    shape = CircleShape
                                ) {
                                    Text(
                                        "💰 Real-World Motivation",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text("Habits = Real Treats ✨", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                "${uiState.goldBalance} Gold Available",
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                "Earn gold by completing habits. Spend gold on guilt-free treats YOU choose.",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Rewards List Header
            item {
                Text("🎁 Your Custom Treat Menu", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            items(uiState.rewards) { reward ->
                CustomRewardCard(reward = reward, onRedeem = { viewModel.redeemReward(reward) })
            }
        }

        if (uiState.isCreatingReward) {
            AlertDialog(
                onDismissRequest = { viewModel.toggleCreateSheet(false) },
                title = { Text("Create Real-World Treat", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = uiState.newTitle,
                            onValueChange = { viewModel.onTitleChange(it) },
                            label = { Text("Treat Title (e.g. 1h Gaming)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = uiState.newGoldCost,
                            onValueChange = { viewModel.onGoldCostChange(it) },
                            label = { Text("Gold Price (e.g. 75)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = uiState.newEmoji,
                            onValueChange = { viewModel.onEmojiChange(it) },
                            label = { Text("Emoji (e.g. 🎮)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { viewModel.createCustomReward() }) {
                        Text("Add Treat")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.toggleCreateSheet(false) }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun CustomRewardCard(
    reward: CustomRewardEntity,
    onRedeem: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(reward.iconEmoji, fontSize = 28.sp)

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(reward.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    "${reward.category} • Redeemed ${reward.timesRedeemed} times",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onRedeem,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD54F),
                    contentColor = Color(0xFF5D4037)
                )
            ) {
                Text("${reward.goldPrice} 🪙", fontWeight = FontWeight.Bold)
            }
        }
    }
}
