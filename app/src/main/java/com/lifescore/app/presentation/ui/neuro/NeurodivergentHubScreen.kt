package com.lifescore.app.presentation.ui.neuro

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeurodivergentHubScreen(
    viewModel: NeurodivergentViewModel,
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

    val overlayColor = when (uiState.selectedColorFilter) {
        "WARM_AMBER" -> Color(0x15FFB300)
        "SAGE_GREEN" -> Color(0x1566BB6A)
        "OCEAN_BLUE" -> Color(0x1542A5F5)
        "ROSE_TINT" -> Color(0x15EC407A)
        else -> Color.Transparent
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Soft Focus & ADHD Hub", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Neurodivergent Sensory Comfort & Micro-Pacing", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Calming Color Overlays Selector
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🎨 Calming Sensory Color Overlays", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Reduces visual glare and sensory fatigue for ADHD/ASD reading.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ColorFilterButton("Off", "NONE", uiState.selectedColorFilter == "NONE") { viewModel.setColorFilter("NONE") }
                                ColorFilterButton("Amber 🌅", "WARM_AMBER", uiState.selectedColorFilter == "WARM_AMBER") { viewModel.setColorFilter("WARM_AMBER") }
                                ColorFilterButton("Sage 🌿", "SAGE_GREEN", uiState.selectedColorFilter == "SAGE_GREEN") { viewModel.setColorFilter("SAGE_GREEN") }
                                ColorFilterButton("Ocean 🌊", "OCEAN_BLUE", uiState.selectedColorFilter == "OCEAN_BLUE") { viewModel.setColorFilter("OCEAN_BLUE") }
                            }
                        }
                    }
                }

                // Dopamine Menu (Side-car for ADHD Paralysis)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("🍽️ Dopamine Menu (Quick Starters)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("Low-friction micro-actions to beat task paralysis.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = uiState.isDopamineMenuEnabled,
                                    onCheckedChange = { viewModel.toggleDopamineMenu() }
                                )
                            }

                            if (uiState.isDopamineMenuEnabled) {
                                Spacer(modifier = Modifier.height(12.dp))
                                DopamineMenuItem("Appetizer (2m)", "Put on upbeat song + clear desk workspace")
                                DopamineMenuItem("Entrée (15m)", "One Pomodoro block of single-task flow")
                                DopamineMenuItem("Dessert (5m)", "Stretching in sunlight or cold water splash")
                            }
                        }
                    }
                }

                // Anonymous Focus Pod (Body Doubling)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("👥 Anonymous Support Pod #204", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Surface(
                                    color = Color(0xFFE8F5E9),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "🟢 4 Live in Deep Work",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Silent body doubling with anonymous peers worldwide.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { viewModel.completePodCheckin() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !uiState.isPodCheckinDone
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (uiState.isPodCheckinDone) "Checked In (+30 XP)" else "Tap to Check In with Pod (+30 XP)")
                            }
                        }
                    }
                }
            }
        }

        // Color overlay layer
        if (overlayColor != Color.Transparent) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(overlayColor)
            )
        }
    }
}

@Composable
fun RowScope.ColorFilterButton(label: String, filter: String, isSelected: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(10.dp),
        colors = if (isSelected) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.outlinedButtonColors()
    ) {
        Text(label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, maxLines = 1)
    }
}

@Composable
fun DopamineMenuItem(category: String, action: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(category, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(action, fontSize = 12.sp)
        }
    }
}
