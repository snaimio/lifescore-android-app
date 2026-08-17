package com.lifescore.app.presentation.privacy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyDashboardScreen(
    viewModel: PrivacyViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showPurgeConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔒", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("Zero-Data Privacy Guard", fontWeight = FontWeight.Black)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
        ) {
            // Hero
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(14.dp, shape = RoundedCornerShape(22.dp))
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF212121), Color(0xFF424242), Color(0xFF616161))
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
                            Text(
                                "PRIVACY FIRST ARCHITECTURE",
                                style = MaterialTheme.typography.labelMedium,
                                letterSpacing = 1.8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF00E676).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    "GDPR & CCPA Compliant",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00E676),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Your Life Data Belongs To You",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            "LifeScore enforces strict client-side encryption. No advertising trackers, no selling of psychometric profiles.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Notification Banner
            if (uiState.bannerMessage != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                uiState.bannerMessage!!,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.dismissBanner() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Zero-Data Toggle
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Zero-Data Collection Mode", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    "When active, all scores, quests, and reflections are strictly stored in local Room DB on this device only.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = uiState.isZeroDataModeActive,
                                onCheckedChange = { viewModel.toggleZeroDataMode(it) }
                            )
                        }
                    }
                }
            }

            // Data Actions Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("Data Portability & Right to Erasure", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                        Button(
                            onClick = { viewModel.exportData() },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Export My Data (JSON)")
                        }

                        OutlinedButton(
                            onClick = { showPurgeConfirmation = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Purge & Delete All Local Data")
                        }
                    }
                }
            }

            // Exported Preview
            if (uiState.exportedJsonString != null) {
                item {
                    Text("Exported JSON Payload Preview", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = uiState.exportedJsonString!!,
                            color = Color(0xFF64FFDA),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }

    if (showPurgeConfirmation) {
        AlertDialog(
            onDismissRequest = { showPurgeConfirmation = false },
            title = { Text("⚠️ Confirm Permanent Purge") },
            text = { Text("Are you sure you want to delete all local tables, quests, stats, and journals? This action is irreversible.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.purgeAllData()
                        showPurgeConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Purge Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPurgeConfirmation = false }) { Text("Cancel") }
            }
        )
    }
}
