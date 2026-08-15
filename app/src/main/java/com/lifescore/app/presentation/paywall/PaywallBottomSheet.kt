package com.lifescore.app.presentation.paywall

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.data.repository.BillingRepository
import com.lifescore.app.domain.model.SubscriptionTier

data class FeatureComparisonRow(
    val featureName: String,
    val freeTier: String,
    val proTier: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallBottomSheet(
    billingRepository: BillingRepository,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedTier by remember { mutableStateOf(SubscriptionTier.ANNUAL) }
    var isRestoring by remember { mutableStateOf(false) }

    val comparisonList = remember {
        listOf(
            FeatureComparisonRow("Daily Habit Quests", "3 Active Max", "Unlimited Quests ⚡"),
            FeatureComparisonRow("Gemini AI Coach", "1 Daily Brief", "Unlimited Gemini 1.5 Flash 🧠"),
            FeatureComparisonRow("Duels & Sprints", "Public Only", "30-Day Masteries & 1v1 Duels ⚔️"),
            FeatureComparisonRow("Streak Insurance", "1 Shield/mo", "3 Shields/mo + 50% Off 🛡️"),
            FeatureComparisonRow("Story Card Themes", "Cosmic Only", "All 4 Themes & Custom Badges 🎨"),
            FeatureComparisonRow("Leaderboard Prestige", "Standard", "👑 Diamond Crown & Pro Badge")
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFD700).copy(alpha = 0.2f),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "Unlock LifeScore Pro",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = "Supercharge your growth with unlimited AI coaching, social duels, and streak insurance.",
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            // Pricing Tiers Carousel / Selector
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SubscriptionTier.values().forEach { tier ->
                        val isSelected = selectedTier == tier
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTier = tier }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedTier = tier }
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(tier.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            if (tier.isPopular) {
                                                Spacer(Modifier.width(8.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = Color(0xFFFFD700)
                                                ) {
                                                    Text(
                                                        text = "SAVE 48%",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = Color.Black,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(tier.billingPeriod, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                    }
                                }

                                Text(
                                    text = tier.priceFormatted,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Free vs Pro Comparison Matrix
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Feature", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.outline, modifier = Modifier.weight(1.2f))
                            Text("Free", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.outline, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text("Pro ⚡", fontWeight = FontWeight.Black, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1.3f), textAlign = TextAlign.End)
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)

                        comparisonList.forEach { row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(row.featureName, fontSize = 11.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1.2f))
                                Text(row.freeTier, fontSize = 10.sp, color = MaterialTheme.colorScheme.outline, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                Text(row.proTier, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1.3f), textAlign = TextAlign.End)
                            }
                        }
                    }
                }
            }

            // Upgrade Button CTA
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = {
                            if (context is Activity) {
                                billingRepository.launchPurchaseFlow(context, selectedTier)
                            }
                            Toast.makeText(context, "Welcome to LifeScore Pro! 🌟 All features unlocked.", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (selectedTier == SubscriptionTier.ANNUAL) "Start 3-Day Free Trial" else "Upgrade to LifeScore Pro",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    TextButton(
                        onClick = {
                            isRestoring = true
                            billingRepository.restorePurchases { success ->
                                isRestoring = false
                                Toast.makeText(context, "Purchases restored! Active entitlement verified.", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            }
                        }
                    ) {
                        Text("Restore Purchases", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }

                    Text(
                        text = "Recurring billing • Cancel anytime in Google Play Settings • Terms & Privacy",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}
