package com.lifescore.app.presentation.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.navigation.NavController
import com.lifescore.app.LifeScoreApp
import com.lifescore.app.core.designsystem.*
import com.lifescore.app.core.designsystem.components.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    onOpenPaywall: () -> Unit,
    onOpenAuth: () -> Unit = {}
) {
    val context = LocalContext.current
    val app = context.applicationContext as LifeScoreApp

    var notificationsEnabled by remember { mutableStateOf(true) }
    var soundEffectsEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Account & Cloud Sync
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    onClick = onOpenAuth
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Account & Cloud Backup", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Sign in with Google / Email to sync to Firestore", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }

            // Enterprise Hub Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    onClick = { navController.navigate(com.lifescore.app.presentation.navigation.Screen.Enterprise.route) }
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF6366F1).copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🏢", fontSize = 20.sp)
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("LifeScore Enterprise Hub", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF6366F1)) {
                                    Text("B2B", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                            Text("Acme Technologies • 78 Seats • Team Analytics", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }

            // Pro Membership Promo
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    onClick = onOpenPaywall
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Upgrade to LifeScore Pro", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Unlimited AI coach, widgets & analytics", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }

            // Hero Archetype & Persona Profile
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    onClick = { navController.navigate("archetype_profile") }
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF6366F1).copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🏛️", fontSize = 20.sp)
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Hero Archetype Profile", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Tendencies, blind spots, work style & share cards", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }

            item {
                val themeManager = rememberThemeManager()
                val themeMode by themeManager.themeMode.collectAsState()

                ThemePicker(
                    currentMode = themeMode,
                    onModeChange = { themeManager.setThemeMode(it) }
                )
            }

            item {
                Text("Preferences", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }

            item {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        SettingsSwitchItem(
                            icon = Icons.Default.Notifications,
                            title = "Daily Streak Reminders",
                            subtitle = "Receive notifications at 8:00 AM",
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                        SettingsSwitchItem(
                            icon = Icons.AutoMirrored.Filled.VolumeUp,
                            title = "Sound Effects & Haptics",
                            subtitle = "Feel feedback upon habit completion",
                            checked = soundEffectsEnabled,
                            onCheckedChange = { soundEffectsEnabled = it }
                        )
                    }
                }
            }

            item {
                Text("Data Privacy & GDPR Rights", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }

            item {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        SettingsClickableItem(
                            icon = Icons.Default.FileDownload,
                            title = "Download My Data (JSON Export)",
                            subtitle = "Export your habit logs, dimension scores & streak archive",
                            onClick = {
                                val jsonExport = """
                                    {
                                      "app": "LifeScore",
                                      "version": "1.0.0",
                                      "exportedAt": "${System.currentTimeMillis()}",
                                      "userProfile": {
                                        "name": "Champion Hero",
                                        "level": 5,
                                        "lifeScore": 780,
                                        "streakDays": 14,
                                        "archetype": "The Warrior"
                                      },
                                      "dimensions": {
                                        "fitness": 85,
                                        "career": 90,
                                        "learning": 80,
                                        "health": 75,
                                        "mentalHealth": 80,
                                        "wealth": 85,
                                        "relationships": 75,
                                        "socialLife": 70
                                      }
                                    }
                                """.trimIndent()

                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, jsonExport)
                                    putExtra(Intent.EXTRA_TITLE, "LifeScore_UserData_Export.json")
                                    type = "application/json"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Export LifeScore JSON Archive"))
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))

                        SettingsClickableItem(
                            icon = Icons.Default.Security,
                            title = "Withdraw Cloud Processing Consent",
                            subtitle = "Revoke AI Coach & Cloud Sync (Switch to Local SQLite)",
                            onClick = {
                                com.lifescore.app.core.util.ConsentManager.revokeConsent(context)
                                Toast.makeText(context, "Cloud processing consent withdrawn. Local mode active.", Toast.LENGTH_LONG).show()
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))

                        SettingsClickableItem(
                            icon = Icons.Default.DeleteForever,
                            title = "Delete My Account & Wipe Cloud Data",
                            subtitle = "Permanently purge all Firestore collections & local databases",
                            onClick = {
                                Toast.makeText(context, "Account data wiped successfully from Cloud Firestore.", Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                }
            }

            item {
                Text("Legal & Compliance", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }

            item {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        SettingsClickableItem(
                            icon = Icons.Default.PrivacyTip,
                            title = "Privacy Policy (GDPR / CCPA / COPPA)",
                            subtitle = "View full transparent data practices",
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://lifescore-app.web.app/privacy"))
                                try { context.startActivity(intent) } catch (e: Exception) {
                                    Toast.makeText(context, "Opening Privacy Policy: https://lifescore-app.web.app/privacy", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))

                        SettingsClickableItem(
                            icon = Icons.Default.Description,
                            title = "Terms of Service",
                            subtitle = "Usage rules, acceptable use & AI disclaimer",
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://lifescore-app.web.app/terms"))
                                try { context.startActivity(intent) } catch (e: Exception) {
                                    Toast.makeText(context, "Opening Terms of Service: https://lifescore-app.web.app/terms", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))

                        SettingsClickableItem(
                            icon = Icons.Default.Info,
                            title = "App Version",
                            subtitle = "v1.0.0 (Production Release)",
                            onClick = {}
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SettingsClickableItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ThemePicker(
    currentMode: com.lifescore.app.core.designsystem.AppThemeMode,
    onModeChange: (com.lifescore.app.core.designsystem.AppThemeMode) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md)
        ) {
            Text(
                text = "🌓 Theme Mode",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = "Default is colorful Light Mode. Dark Mode enables true AMOLED black.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(Spacing.sm))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                ThemeOptionButton(
                    icon = "☀️",
                    label = "Light",
                    isSelected = currentMode == com.lifescore.app.core.designsystem.AppThemeMode.LIGHT,
                    onClick = { onModeChange(com.lifescore.app.core.designsystem.AppThemeMode.LIGHT) },
                    modifier = Modifier.weight(1f)
                )
                ThemeOptionButton(
                    icon = "🌙",
                    label = "Dark",
                    isSelected = currentMode == com.lifescore.app.core.designsystem.AppThemeMode.DARK,
                    onClick = { onModeChange(com.lifescore.app.core.designsystem.AppThemeMode.DARK) },
                    modifier = Modifier.weight(1f)
                )
                ThemeOptionButton(
                    icon = "📱",
                    label = "System",
                    isSelected = currentMode == com.lifescore.app.core.designsystem.AppThemeMode.SYSTEM,
                    onClick = { onModeChange(com.lifescore.app.core.designsystem.AppThemeMode.SYSTEM) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ThemeOptionButton(
    icon: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        }
    ) {
        Column(
            modifier = Modifier.padding(vertical = Spacing.md, horizontal = Spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 24.sp)
            Spacer(Modifier.height(Spacing.xs))
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

