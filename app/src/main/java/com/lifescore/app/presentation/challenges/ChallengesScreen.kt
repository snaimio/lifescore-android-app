package com.lifescore.app.presentation.challenges

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lifescore.app.core.util.CardGenerator
import com.lifescore.app.core.util.DuelManager
import com.lifescore.app.core.util.ExpertChallengeManager
import com.lifescore.app.domain.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengesScreen(
    viewModel: ChallengesViewModel,
    onOpenPaywall: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Show toast for success messages
    LaunchedEffect(uiState.recentSuccessMessage) {
        uiState.recentSuccessMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Life Duels & Masterclasses", fontWeight = FontWeight.Black) },
                actions = {
                    if (uiState.selectedTab == ChallengeTab.MASTERCLASSES) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("🎓", fontSize = 12.sp)
                                Spacer(Modifier.width(4.dp))
                                Text("14-Day Tracks", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    } else {
                        FilledTonalButton(
                            onClick = { viewModel.showCreateDialog(true) },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("New Duel", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Row
            ScrollableTabRow(
                selectedTabIndex = uiState.selectedTab.ordinal,
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                ChallengeTab.values().forEach { tab ->
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        text = { Text(tab.title, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                }
            }

            when (uiState.selectedTab) {
                ChallengeTab.MASTERCLASSES -> {
                    MasterclassesView(
                        uiState = uiState,
                        onSelectMasterclass = { viewModel.selectMasterclass(it) },
                        onSelectDay = { viewModel.selectMasterclassDay(it) },
                        onToggleAudio = { viewModel.toggleAudioPlayback() },
                        onCheckInDay = { mcId, day -> viewModel.checkInMasterclassDay(mcId, day) },
                        onUnlock = { mcId -> viewModel.unlockMasterclass(mcId) },
                        onOpenPaywall = onOpenPaywall
                    )
                }
                else -> {
                    DuelsAndSprintsView(
                        uiState = uiState,
                        viewModel = viewModel,
                        context = context
                    )
                }
            }
        }
    }

    // Graduation Certificate Modal
    if (uiState.showGraduationModal && uiState.activeCertificate != null) {
        val cert = uiState.activeCertificate!!
        Dialog(onDismissRequest = { viewModel.closeGraduationModal() }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFD700).copy(alpha = 0.2f),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🎓", fontSize = 32.sp)
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text("MASTERCLASS GRADUATE", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFFFFD700))
                    Text(cert.masterclassTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(vertical = 4.dp))
                    Text("Instructor: ${cert.coachName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)

                    Spacer(Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Credential ID: ${cert.certificateId}", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text("Issued: ${cert.completionDate}", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                            Text("Total XP Earned: +${cert.xpEarnedTotal} XP", fontWeight = FontWeight.Black, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val caption = ExpertChallengeManager.generateCertificateShareCaption(cert)
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, caption)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Masterclass Certificate"))
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Share Official Certificate", fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { viewModel.closeGraduationModal() }) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@Composable
fun MasterclassesView(
    uiState: ChallengesUiState,
    onSelectMasterclass: (ExpertMasterclass) -> Unit,
    onSelectDay: (MasterclassDayModule) -> Unit,
    onToggleAudio: () -> Unit,
    onCheckInDay: (String, Int) -> Unit,
    onUnlock: (String) -> Unit,
    onOpenPaywall: () -> Unit
) {
    val activeMasterclass = uiState.selectedMasterclass ?: uiState.masterclasses.firstOrNull()
    val activeDay = uiState.selectedMasterclassDay ?: activeMasterclass?.days?.firstOrNull()
    var showTranscript by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Masterclasses Selector Carousel
        item {
            Text("Elite 14-Day Coach Tracks", fontWeight = FontWeight.Black, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(uiState.masterclasses) { mc ->
                    val isSelected = mc.id == activeMasterclass?.id
                    Card(
                        onClick = { onSelectMasterclass(mc) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier.width(220.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(mc.coachAvatarEmoji, fontSize = 24.sp)
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (mc.isUnlocked) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFFFD700).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (mc.isUnlocked) "UNLOCKED" else "$9.99 / PRO",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (mc.isUnlocked) Color(0xFF10B981) else Color(0xFFFFD700),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(mc.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 2)
                            Spacer(Modifier.height(4.dp))
                            Text(mc.coachName, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                            Spacer(Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { mc.progressPercentage },
                                modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape)
                            )
                        }
                    }
                }
            }
        }

        if (activeMasterclass != null) {
            // Coach Header Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(activeMasterclass.coachAvatarEmoji, fontSize = 24.sp)
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(activeMasterclass.coachName, fontWeight = FontWeight.Black, fontSize = 15.sp)
                                Text(activeMasterclass.coachTitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                Text(activeMasterclass.coachCredentials, fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(activeMasterclass.subtitle, fontSize = 12.sp, lineHeight = 16.sp)
                    }
                }
            }

            // 14-Day Node Slider
            item {
                Text("Curriculum Modules (14 Days)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(activeMasterclass.days) { day ->
                        val isDaySelected = day.dayNumber == activeDay?.dayNumber
                        Surface(
                            onClick = { onSelectDay(day) },
                            shape = CircleShape,
                            color = when {
                                day.isCompleted -> Color(0xFF10B981)
                                isDaySelected -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (day.isCompleted) {
                                    Text("✓", color = Color.White, fontWeight = FontWeight.Black)
                                } else {
                                    Text("D${day.dayNumber}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isDaySelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            // Active Day Lesson & Player Card
            if (activeDay != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("DAY ${activeDay.dayNumber} OF 14", fontWeight = FontWeight.Black, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                                ) {
                                    Text("🎧 5:00 Audio Lesson", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }

                            Spacer(Modifier.height(6.dp))
                            Text(activeDay.title, fontWeight = FontWeight.Black, fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(Modifier.height(4.dp))
                            Text(activeDay.summary, fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))

                            Spacer(Modifier.height(14.dp))

                            // 🎧 Audio Player Bar
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = onToggleAudio,
                                        modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape).size(36.dp)
                                    ) {
                                        Icon(
                                            if (uiState.isPlayingAudio) Icons.Default.Close else Icons.Default.PlayArrow,
                                            contentDescription = "Play",
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        LinearProgressIndicator(
                                            progress = { if (uiState.isPlayingAudio) 0.65f else uiState.audioProgress },
                                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape)
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(if (uiState.isPlayingAudio) "Playing lesson..." else "Paused", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                            Text("05:00", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                        }
                                    }
                                }
                            }

                            // Transcript Dropdown
                            Spacer(Modifier.height(8.dp))
                            TextButton(
                                onClick = { showTranscript = !showTranscript },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text(if (showTranscript) "Hide Key Takeaways" else "Read Key Takeaways ▾", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            if (showTranscript) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(activeDay.transcriptSummary, fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.padding(12.dp))
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            // ⚡ Actionable Daily Task
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("⚡ Daily Actionable Quest", fontWeight = FontWeight.Black, fontSize = 12.sp)
                                        Text("+${activeDay.dailyTaskPoints} XP", fontWeight = FontWeight.Black, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(activeDay.dailyTaskTitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                    Spacer(Modifier.height(12.dp))

                                    if (!activeMasterclass.isUnlocked) {
                                        Button(
                                            onClick = onOpenPaywall,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                                            modifier = Modifier.fillMaxWidth().height(42.dp),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("🔒 Unlock Masterclass ($9.99 / Pro)", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                                        }
                                    } else {
                                        Button(
                                            onClick = { onCheckInDay(activeMasterclass.id, activeDay.dayNumber) },
                                            enabled = !activeDay.isCompleted,
                                            colors = if (activeDay.isCompleted) ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)) else ButtonDefaults.buttonColors(),
                                            modifier = Modifier.fillMaxWidth().height(42.dp),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(if (activeDay.isCompleted) "Day ${activeDay.dayNumber} Complete ✓" else "Complete Day ${activeDay.dayNumber} Task (+50 XP)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(30.dp)) }
    }
}

@Composable
fun DuelsAndSprintsView(
    uiState: ChallengesUiState,
    viewModel: ChallengesViewModel,
    context: android.content.Context
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Social Duels Hero Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("⚔️", fontSize = 20.sp)
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("7-Day Head-to-Head Duels", fontWeight = FontWeight.Black, fontSize = 16.sp)
                                Text("Challenge friends • Daily check-ins • Winner takes XP", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("⚡ Active Sprints:", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                Spacer(Modifier.width(4.dp))
                                Text("${uiState.activeCount}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("🏆 XP Pool Won:", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                Spacer(Modifier.width(4.dp))
                                Text("+${uiState.totalXpEarned} XP", fontSize = 12.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }

        // Active Challenges List
        items(uiState.challenges) { challenge ->
            Card(
                onClick = { viewModel.selectChallengeDetail(challenge) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(challenge.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("+${challenge.xpReward} XP", fontWeight = FontWeight.Black, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(challenge.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)

                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Day ${challenge.currentDay}/${challenge.durationDays}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Button(
                            onClick = { viewModel.checkInToday(challenge.id) },
                            enabled = challenge.isJoined && !challenge.isCompleted,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(if (challenge.isCompleted) "Completed ✓" else "Check-In", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(30.dp)) }
    }
}
