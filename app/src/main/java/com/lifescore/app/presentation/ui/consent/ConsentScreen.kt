package com.lifescore.app.presentation.ui.consent

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.util.ConsentManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsentScreen(
    onConsentResolved: () -> Unit
) {
    val context = LocalContext.current

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(20.dp))
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🛡️", fontSize = 36.sp)
                    }
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "Your Privacy & Data Ownership",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "LifeScore is built on a transparent, privacy-first architecture.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(10.dp))
            }

            // 1. Cloud Sync & Backup
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Text("☁️", fontSize = 24.sp)
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text("Encrypted Cloud Synchronization", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                "Your habit streaks, 8-dimension scores, and coin balances are encrypted and securely synced with Google Cloud Firestore.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // 2. Gemini AI Coaching
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Text("🧠", fontSize = 24.sp)
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text("Context-Aware AI Coaching", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                "Anonymized dimension stats and habit milestones are processed via Google Gemini AI to deliver personalized directives.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // 3. Local CameraX Video Storage
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Text("📹", fontSize = 24.sp)
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text("Local-Only Micro-Vlog Storage", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                "Video clips captured for daily montages remain strictly on your device storage.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Action Buttons
            item {
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = {
                        ConsentManager.setConsent(context, true)
                        onConsentResolved()
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Accept & Enable Full Experience 🚀", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        ConsentManager.setConsent(context, false)
                        onConsentResolved()
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Decline & Use Local-Only Mode", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                }

                Spacer(Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "By continuing, you agree to our ",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Terms of Service",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://lifescore-app.web.app/terms"))
                            try { context.startActivity(intent) } catch (e: Exception) {}
                        }
                    )
                    Text(
                        text = " and ",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Privacy Policy",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://lifescore-app.web.app/privacy"))
                            try { context.startActivity(intent) } catch (e: Exception) {}
                        }
                    )
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}
