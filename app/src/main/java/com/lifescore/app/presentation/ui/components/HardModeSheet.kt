package com.lifescore.app.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.util.StreakShieldManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HardModeSheet(
    isHardMode: Boolean,
    streakInsuranceCount: Int,
    onToggleHardMode: (Boolean) -> Unit,
    onBuyInsurance: () -> Unit,
    onDismiss: () -> Unit
) {
    var hardModeEnabled by remember { mutableStateOf(isHardMode) }
    var currentShields by remember { mutableStateOf(streakInsuranceCount) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFFF5722).copy(alpha = 0.2f),
                modifier = Modifier.size(60.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Dangerous, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(34.dp))
                }
            }

            Spacer(Modifier.height(10.dp))

            Text("Streak Insurance & Hard Mode", fontSize = 20.sp, fontWeight = FontWeight.Black)
            Text(
                "Protect your momentum or turn on extreme stakes for 2x XP multipliers.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(Modifier.height(16.dp))

            // 1. Streak Insurance Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("Streak Insurance", fontWeight = FontWeight.Black, fontSize = 16.sp)
                                Text("Auto-protects 1 missed day", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                            }
                        }

                        // 3-Shield Meter Indicator
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            (1..StreakShieldManager.MAX_SHIELDS_PER_MONTH).forEach { index ->
                                val isActive = index <= currentShields
                                Surface(
                                    shape = CircleShape,
                                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = if (isActive) "🛡️" else "○",
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = StreakShieldManager.getShieldStatusText(currentShields),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val (success, newCount) = StreakShieldManager.purchaseShield(currentShields)
                            if (success) {
                                currentShields = newCount
                                onBuyInsurance()
                            }
                        },
                        enabled = currentShields < StreakShieldManager.MAX_SHIELDS_PER_MONTH,
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (currentShields >= StreakShieldManager.MAX_SHIELDS_PER_MONTH) {
                                "Max Shields Active (3/3)"
                            } else {
                                "Buy 1 Shield (${StreakShieldManager.SHIELD_PRICE_FORMATTED})"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // 2. Hard Mode Toggle Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF5722))
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Enable Hard Mode", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Missed daily quests penalize -50 XP at midnight • 2x XP boost", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    }
                    Switch(
                        checked = hardModeEnabled,
                        onCheckedChange = {
                            hardModeEnabled = it
                            onToggleHardMode(it)
                        }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
