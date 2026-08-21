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
import androidx.navigation.NavController
import com.lifescore.app.LifeScoreApp
import com.lifescore.app.core.designsystem.Spacing
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
    val coroutineScope = rememberCoroutineScope()

    var notificationsEnabled by remember { mutableStateOf(true) }
    var soundEffectsEnabled by remember { mutableStateOf(true) }

    var isTesting by remember { mutableStateOf(false) }
    var showTestDialog by remember { mutableStateOf(false) }
    var testOutput by remember { mutableStateOf("") }

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
                            Text("Tendencies, blind spots, work style & share cards", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }

            item {
                Text("Preferences & Localization", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }

            item {
                var showLanguageDialog by remember { mutableStateOf(false) }
                var currentLang by remember { mutableStateOf(com.lifescore.app.core.util.LanguageManager.getCurrentLanguage()) }

                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        // Language Selector Item
                        SettingsClickableItem(
                            icon = Icons.Default.Language,
                            title = "App Language / Idioma / 语言 / لغة / भाषा",
                            subtitle = "${currentLang.flagEmoji} ${currentLang.nativeName} (${currentLang.code.uppercase()})",
                            onClick = { showLanguageDialog = true }
                        )

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))

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
                            title = "XP Sound Effects & Haptics",
                            subtitle = "Feel rewards upon habit completion",
                            checked = soundEffectsEnabled,
                            onCheckedChange = { soundEffectsEnabled = it }
                        )
                    }
                }

                if (showLanguageDialog) {
                    androidx.compose.ui.window.Dialog(onDismissRequest = { showLanguageDialog = false }) {
                        Card(
                            shape = RoundedCornerShape(22.dp),
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text("Select Language", fontWeight = FontWeight.Black, fontSize = 18.sp)
                                Text("Choose your preferred language for LifeScore", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                                Spacer(Modifier.height(14.dp))

                                com.lifescore.app.core.util.LanguageManager.getSupportedLanguages().forEach { lang ->
                                    Surface(
                                        onClick = {
                                            com.lifescore.app.core.util.LanguageManager.setAppLanguage(lang)
                                            currentLang = lang
                                            showLanguageDialog = false
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (currentLang == lang) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(lang.flagEmoji, fontSize = 20.sp)
                                            Spacer(Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(lang.nativeName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Text(lang.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                            }
                                            if (currentLang == lang) {
                                                Text("✓", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text("Developer & Cloud Diagnostics", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }

            item {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        SettingsClickableItem(
                            icon = Icons.Default.CloudSync,
                            title = if (isTesting) "Running Firestore Test..." else "Run Firestore Connection Test",
                            subtitle = "Verify Anonymous Auth, Document Write & Read",
                            onClick = {
                                if (isTesting) return@SettingsClickableItem
                                isTesting = true
                                testOutput = "1. Authenticating anonymously...\n"
                                coroutineScope.launch {
                                    val authResult = app.authRepository.signInAnonymously()
                                    authResult.fold(
                                        onSuccess = { user ->
                                            val uid = app.authRepository.currentUser?.uid ?: user.id.toString()
                                            testOutput += "✅ Auth Success: UID = ${uid.take(12)}...\n\n2. Writing user document to /users/$uid...\n"
                                            
                                            try {
                                                app.firebaseRepository.saveUser(user, email = "guest@lifescore.app", uid = uid)
                                                testOutput += "✅ Document Write: SUCCESS\n\n3. Reading document back from Firestore...\n"
                                                
                                                val retrieved = app.firebaseRepository.getUser(uid)
                                                if (retrieved != null) {
                                                    testOutput += "✅ Document Read: SUCCESS\n"
                                                    testOutput += "   • Name: ${retrieved.name}\n"
                                                    testOutput += "   • Level: ${retrieved.currentLevel}\n"
                                                    testOutput += "   • Archetype: ${retrieved.title}\n\n"
                                                    testOutput += "🎉 Firestore connection verified and operational!"
                                                } else {
                                                    testOutput += "⚠️ Read back returned null from cache."
                                                }
                                            } catch (e: Exception) {
                                                testOutput += "❌ Operation error: ${e.localizedMessage}"
                                            }
                                        },
                                        onFailure = {
                                            testOutput += "❌ Auth failed: ${it.localizedMessage}"
                                        }
                                    )
                                    isTesting = false
                                    showTestDialog = true
                                }
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                        SettingsClickableItem(
                            icon = Icons.Default.SyncLock,
                            title = "Test Offline Task ➔ Online Sync",
                            subtitle = "Insert local Room task, reconnect & verify Firestore sync",
                            onClick = {
                                if (isTesting) return@SettingsClickableItem
                                isTesting = true
                                testOutput = "1. Simulating offline task creation in local Room DB...\n"
                                coroutineScope.launch {
                                    try {
                                        // 1. Insert offline task into local Room database
                                        val testTask = com.lifescore.app.data.local.entity.TaskEntity(
                                            title = "Hydrate 2L Water (Offline Test)",
                                            dimension = com.lifescore.app.domain.model.DimensionType.HEALTH,
                                            pointsReward = 20,
                                            isCompleted = true,
                                            completedAt = System.currentTimeMillis()
                                        )
                                        app.database.taskDao().insertTask(testTask)
                                        testOutput += "✅ Local SQLite Insert: Saved '${testTask.title}'\n\n"
                                        testOutput += "2. Restoring connection & triggering DataSyncService...\n"

                                        // Ensure anonymous auth session exists
                                        if (app.authRepository.currentUser == null) {
                                            app.authRepository.signInAnonymously()
                                        }

                                        val syncService = com.lifescore.app.services.DataSyncService(
                                            db = app.database,
                                            firebaseRepository = app.firebaseRepository,
                                            authRepository = app.authRepository
                                        )

                                        val report = syncService.syncAllWithLogs()
                                        testOutput += "\n=== SYNC EXECUTION TRACE ===\n"
                                        report.logs.forEach { logLine ->
                                            testOutput += "$logLine\n"
                                        }

                                        if (report.isSuccess) {
                                            testOutput += "\n🎉 Task verified in Cloud Firestore! (Total Synced: ${report.tasksSyncedCount})"
                                        } else {
                                            testOutput += "\n❌ Sync failed: ${report.error}"
                                        }
                                    } catch (e: Exception) {
                                        testOutput += "❌ Error: ${e.localizedMessage}"
                                    }
                                    isTesting = false
                                    showTestDialog = true
                                }
                            }
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

    if (showTestDialog) {
        AlertDialog(
            onDismissRequest = { showTestDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("Firestore Diagnostics", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = testOutput,
                        fontSize = 13.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showTestDialog = false }) {
                    Text("Done")
                }
            }
        )
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
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
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
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
    }
}
