package com.lifescore.app.presentation.ui.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lifescore.app.core.util.StreakShieldManager
import com.lifescore.app.domain.model.GuardianSponsor
import com.lifescore.app.domain.model.ReferralStatus
import com.lifescore.app.utils.ShareHelper

enum class GuardianTab(val title: String) {
    SPONSOR("Sponsor"),
    WALL("Guardian Wall"),
    THANK_YOU("Thank-You Card")
}

@Composable
fun GuardianSponsorshipDialog(
    referralStatus: ReferralStatus = ReferralStatus(),
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(GuardianTab.SPONSOR) }
    var recipientEmail by remember { mutableStateOf("") }
    var giftMessage by remember { mutableStateOf("") }
    var sponsorSuccess by remember { mutableStateOf(false) }

    val sampleGuardians = remember {
        listOf(
            GuardianSponsor("g1", "Marcus Vance", 5, "Empowering 5 university students with life balance tools.", "Platinum Guardian"),
            GuardianSponsor("g2", "Sarah Chen", 3, "Dedicated to high-school habit builders.", "Gold Benefactor"),
            GuardianSponsor("g3", "Alex Morgan", 2, "Building future leaders one quest at a time.", "Silver Sponsor"),
            GuardianSponsor("g4", "David Kim", 2, "Pay it forward always.", "Silver Sponsor")
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE91E63).copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("💖", fontSize = 18.sp)
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Text("Guardian Program", fontWeight = FontWeight.Black, fontSize = 17.sp)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Tab Switcher
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        GuardianTab.values().forEach { tab ->
                            val isSelected = selectedTab == tab
                            Surface(
                                onClick = { selectedTab = tab },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = tab.title,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                when (selectedTab) {
                    GuardianTab.SPONSOR -> {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text("Sponsor a Free Pro Subscription", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                "Gift a 1-year LifeScore Pro membership to a student or seeker in need.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.outline
                            )

                            Spacer(Modifier.height(12.dp))

                            OutlinedTextField(
                                value = recipientEmail,
                                onValueChange = { recipientEmail = it },
                                label = { Text("Recipient Email") },
                                placeholder = { Text("friend@domain.com") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            Spacer(Modifier.height(10.dp))

                            OutlinedTextField(
                                value = giftMessage,
                                onValueChange = { giftMessage = it },
                                label = { Text("Dedication Note") },
                                placeholder = { Text("Keep leveling up! You got this 🚀") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                maxLines = 3
                            )

                            Spacer(Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    sponsorSuccess = true
                                    Toast.makeText(context, "Sponsorship sent! You are now listed on the Guardian Wall 💖", Toast.LENGTH_LONG).show()
                                },
                                enabled = recipientEmail.isNotBlank(),
                                modifier = Modifier.fillMaxWidth().height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                            ) {
                                Icon(Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Sponsor with Pro ($49.99/yr)", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    GuardianTab.WALL -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                Text(
                                    text = "🏛️ Public Guardian Honor Wall",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Thank you to the benefactors empowering our community.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Spacer(Modifier.height(4.dp))
                            }

                            items(sampleGuardians) { guardian ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFFFFD700).copy(alpha = 0.2f),
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("👑", fontSize = 16.sp)
                                            }
                                        }
                                        Spacer(Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(guardian.sponsorName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Spacer(Modifier.width(6.dp))
                                                Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                                                    Text(guardian.tier, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                                }
                                            }
                                            Text(guardian.recentNote, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                        }
                                        Text("${guardian.recipientsCount} Sponsored", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFFE91E63))
                                    }
                                }
                            }
                        }
                    }

                    GuardianTab.THANK_YOU -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFE91E63).copy(alpha = 0.15f),
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("💌", fontSize = 26.sp)
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            Text("Send Thank-You Story Card", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                "Express gratitude to your sponsor with a personalized LifeScore card.",
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            Spacer(Modifier.height(16.dp))

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Sample Appreciation Note", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "\"💖 A huge thank you to my LifeScore Guardian @Marcus for sponsoring my Pro journey! Leveling up daily habits with balance and accountability.\"",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                    )
                                }
                            }

                            Spacer(Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    val caption = StreakShieldManager.generateThankYouCaption("Achiever", "Marcus")
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, caption)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Thank-You Card"))
                                },
                                modifier = Modifier.fillMaxWidth().height(44.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Share Thank-You Story Card", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
