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
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.*
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
                title = { Text("Profile & Analytics", fontWeight = FontWeight.Bold) },
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
            // 1. Identity Banner
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
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(10.dp))
                        Text(uiState.user.name, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(uiState.user.title.ifBlank { "Dedicated Practitioner" }, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(12.dp))

                        // Consistency Progress
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Weekly Habit Consistency", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("${uiState.consistencyPercentage}% on track", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Spacer(Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (uiState.consistencyPercentage / 100f).coerceIn(0f, 1f) },
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
                            Text("🔥 STREAK", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF5722))
                            Text("${uiState.user.currentStreakDays}d", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                    Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("✓ HABITS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                            Text("${uiState.totalTasksCompleted}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                    Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⏳ FOCUS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6366F1))
                            Text("${String.format(java.util.Locale.US, "%.1f", uiState.focusHours)}h", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                    Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📈 RATE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                            Text("${uiState.consistencyPercentage}%", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                                Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Behavioral Archetype Profile", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Habit Tendencies • Focus & Execution Style", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }

            // 4. Milestones & Consistency Badges
            item {
                Text("🎖️ Milestones & Consistency", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            item {
                val badges = listOf(
                    Triple("🔥 7-Day Flame", "Maintained 7d streak", true),
                    Triple("⏱️ Deep Work", "50+ focus hours", true),
                    Triple("🧘 Circadian Habit", "14d sleep routine", true),
                    Triple("📊 Master Index", "Reached 900 LifeScore", false)
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

            // 5. Quick Links
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⚡ Quick Hubs & Directory", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    TextButton(onClick = { navController.navigate(Screen.Explore.route) }) {
                        Text("View All →", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Screen.Explore.route) }
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Explore, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Explore Directory", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Discover all habit modules & analytics tools", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Screen.SkillMastery.route) }
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null)
                            Spacer(Modifier.width(12.dp))
                            Text("10,000-Hour Skill Mastery Tracker", fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(30.dp)) }
        }
    }
}
