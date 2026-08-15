package com.lifescore.app.presentation.ui.profile

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.navigation.NavController
import com.lifescore.app.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hero Profile & Stats", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Hero Identity Banner
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("⚔️", fontSize = 36.sp)
                            }
                        }

                        Spacer(Modifier.height(10.dp))
                        Text(uiState.user.name, fontWeight = FontWeight.Black, fontSize = 22.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(uiState.user.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))

                        // Level & XP Progress
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Level ${uiState.user.currentLevel}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("${uiState.user.currentXp % 500} / 500 XP to Lvl ${uiState.user.currentLevel + 1}", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Spacer(Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { ((uiState.user.currentXp % 500) / 500f).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // 2. 4 Stat Counters
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔥 STREAK", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF5722))
                            Text("${uiState.user.currentStreakDays}d", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                    }
                    Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🪙 COINS", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFD700))
                            Text("1,250", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                    }
                    Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🛡️ SHIELDS", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFF6366F1))
                            Text("${uiState.streakShieldsAvailable}", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                    }
                    Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🏆 LEAGUE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                            Text("Diamond", fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }
                    }
                }
            }

            // 3. Archetype Showcase Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth().clickable {
                        navController.navigate(Screen.ArchetypeProfile.route)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFF5722).copy(alpha = 0.2f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("⚔️", fontSize = 24.sp)
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Hero Archetype: The Warrior", fontWeight = FontWeight.Black, fontSize = 15.sp)
                            Text("Unyielding Force • Fitness & Tactical Execution", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }

            // 4. Lifetime Achievements & Badges
            item {
                Text("🎖️ Badges & Milestones", fontWeight = FontWeight.Black, fontSize = 16.sp)
            }

            item {
                val badges = listOf(
                    Triple("🔥 7-Day Flame", "Maintained 7d streak", true),
                    Triple("⚔️ Deep Work Knight", "50+ focus hours", true),
                    Triple("🧘 Circadian Zen", "14d sleep routine", true),
                    Triple("👑 Outlier Legend", "Reached 900 LifeScore", false)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    badges.forEach { (title, subtitle, unlocked) ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (unlocked) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = if (unlocked) BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f)) else null,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(title.take(2), fontSize = 20.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(title.drop(3), fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            }
                        }
                    }
                }
            }

            // 5. Quick Links (Referral, Enterprise, Skill Mastery)
            item {
                Text("⚡ Quick Hubs", fontWeight = FontWeight.Black, fontSize = 16.sp)
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Screen.SkillMastery.route) }
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("⏱️", fontSize = 20.sp)
                            Spacer(Modifier.width(12.dp))
                            Text("10,000-Hour Skill Mastery Tracker", fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Screen.RewardStore.route) }
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("🎁", fontSize = 20.sp)
                            Spacer(Modifier.width(12.dp))
                            Text("LifeScore Reward Store & Coins", fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Screen.Enterprise.route) }
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("🏢", fontSize = 20.sp)
                            Spacer(Modifier.width(12.dp))
                            Text("LifeScore Enterprise & Team Hub", fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Screen.MemeStudio.route) }
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("🎭", fontSize = 20.sp)
                            Spacer(Modifier.width(12.dp))
                            Text("AI Meme Studio & Viral Content", fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(30.dp)) }
        }
    }
}
