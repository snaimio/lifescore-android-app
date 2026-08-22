package com.lifescore.app.presentation.ui.store

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.domain.model.SubscriptionManager
import com.lifescore.app.domain.model.SupporterTier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    onNavigateBack: () -> Unit,
    onOpenGemStore: () -> Unit
) {
    val context = LocalContext.current
    var selectedTier by remember { mutableStateOf(SupporterTier.SUPPORTER) }
    var isSubscribed by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text("👑 Supporter Program", fontWeight = FontWeight.Bold)
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
            // Hero Manifesto Header
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🛡️", fontSize = 40.sp)
                        Spacer(Modifier.height(Spacing.xs))
                        Text(
                            "100% Free Core Experience",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "LifeScore will never paywall habit tracking, quests, or 8 life dimensions. Subscriptions are optional patronages that fund our mission.",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Tier Selector
            item {
                Text(
                    "Choose Your Journey",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Free Adventurer Tier
            item {
                TierComparisonCard(
                    tier = SupporterTier.FREE,
                    priceTag = "$0 / Forever Free",
                    benefits = SubscriptionManager.getSubscriptionBenefits(SupporterTier.FREE),
                    isSelected = selectedTier == SupporterTier.FREE,
                    onSelect = { selectedTier = SupporterTier.FREE }
                )
            }

            // Supporter Patron Tier
            item {
                TierComparisonCard(
                    tier = SupporterTier.SUPPORTER,
                    priceTag = "$4.99/mo or $47.99/yr ($3.99/mo)",
                    benefits = SubscriptionManager.getSubscriptionBenefits(SupporterTier.SUPPORTER),
                    isSelected = selectedTier == SupporterTier.SUPPORTER,
                    badgeLabel = "RECOMMENDED PATRON",
                    onSelect = { selectedTier = SupporterTier.SUPPORTER }
                )
            }

            // What It Means to Be a Supporter
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Text(
                            "❤️ Why We Chose the Habitica Model",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(Spacing.xs))
                        Text(
                            "When you subscribe, you're not just buying cosmetic perks—you are directly helping keep high-leverage self-improvement and AI coaching accessible to millions worldwide.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Subscribe Button
            item {
                Button(
                    onClick = {
                        isSubscribed = true
                        Toast.makeText(context, "Welcome to the Supporter Patron Circle! 👑💎", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTier == SupporterTier.SUPPORTER) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        if (selectedTier == SupporterTier.FREE) "Current Plan: Free Adventurer" else "👑 Become a Supporter ($4.99/mo)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            // Frequently Asked Questions
            item {
                Text(
                    "❓ Frequently Asked Questions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                FAQAccordionCard(
                    question = "Can I cancel anytime?",
                    answer = "Yes! You can manage or cancel your subscription at any time with 1 tap via Google Play Subscriptions."
                )
            }

            item {
                FAQAccordionCard(
                    question = "What happens if I cancel?",
                    answer = "You retain all supporter benefits until the end of your billing cycle. After that, your account simply transitions to the 100% Free Adventurer tier without losing any habit history, XP, or quests."
                )
            }

            item {
                FAQAccordionCard(
                    question = "Can I earn gems without paying?",
                    answer = "Yes! You earn gems naturally through daily quest completions, maintaining unbroken streaks, and leveling up your character."
                )
            }

            item {
                Spacer(Modifier.height(Spacing.xl))
            }
        }
    }
}

@Composable
private fun TierComparisonCard(
    tier: SupporterTier,
    priceTag: String,
    benefits: List<String>,
    isSelected: Boolean,
    badgeLabel: String? = null,
    onSelect: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(tier.badgeEmoji, fontSize = 22.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(tier.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                badgeLabel?.let { label ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            label,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(priceTag, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)

            Spacer(Modifier.height(Spacing.sm))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                benefits.forEach { benefit ->
                    Text(benefit, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
private fun FAQAccordionCard(
    question: String,
    answer: String
) {
    var expanded by remember { mutableStateOf(false) }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(question, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Expand FAQ",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(Modifier.height(Spacing.xs))
                    Text(answer, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
