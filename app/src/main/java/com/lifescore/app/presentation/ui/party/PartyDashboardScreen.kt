package com.lifescore.app.presentation.ui.party

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.lifescore.app.data.local.entity.PartyMessageEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyDashboardScreen(
    viewModel: PartyViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val party = uiState.party
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
                        Text(party?.partyName ?: "RPG Party", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Habitica Social Accountability & Boss Raids", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                // Boss Raid Hero Card
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
                                            Color(0xFFB71C1C),
                                            Color(0xFF880E4F),
                                            Color(0xFF4A148C)
                                        )
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = Color.White.copy(alpha = 0.2f),
                                        shape = CircleShape
                                    ) {
                                        Text(
                                            "🐉 Active Party Quest",
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text("Squad XP: +${((party?.teamXpMultiplier ?: 1.3) * 100 - 100).toInt()}%", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    party?.activeQuestName ?: "The Dread Procrastinator",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                val maxHp = party?.questBossMaxHp ?: 500
                                val curHp = party?.questBossCurrentHp ?: 210
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Boss Health", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                    Text("$curHp / $maxHp HP", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { curHp.toFloat() / maxHp.toFloat() },
                                    modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                                    color = Color(0xFFFF5252),
                                    trackColor = Color.White.copy(alpha = 0.2f)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { viewModel.strikeBossQuest(35) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F), contentColor = Color(0xFF3E2723))
                                ) {
                                    Text("⚔️ Deal 35 DMG from Completed Daily Habits (+70 XP)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // Party Members
                item {
                    Text("🛡️ Party Members (${party?.memberCount ?: 4})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PartyMemberChip("Alex (Ldr)", "🧙‍♂️", "100%", true)
                        PartyMemberChip("Elena", "🏹", "85%", false)
                        PartyMemberChip("Marcus", "🛡️", "90%", false)
                        PartyMemberChip("You", "⚔️", "95%", false)
                    }
                }

                // Party Chat & Activity Feed
                item {
                    Text("💬 Party Chat & Quest Log", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                items(uiState.messages) { msg ->
                    PartyMessageBubble(msg)
                }
            }

            // Message Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.chatInput,
                        onValueChange = { viewModel.onChatChange(it) },
                        placeholder = { Text("Send encouragement to party...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { viewModel.sendMessage() },
                        colors = IconButtonDefaults.filledIconButtonColors()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.PartyMemberChip(name: String, avatar: String, completion: String, isLeader: Boolean) {
    Surface(
        modifier = Modifier.weight(1f),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(avatar, fontSize = 20.sp)
            Text(name, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(completion, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun PartyMessageBubble(msg: PartyMessageEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (msg.questDamageDealt > 0) Color(0xFFFFF3E0) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(msg.senderAvatar, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(msg.senderName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(msg.messageText, fontSize = 13.sp)
            }
            if (msg.questDamageDealt > 0) {
                Surface(
                    color = Color(0xFFFF5252),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "-${msg.questDamageDealt} HP",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
