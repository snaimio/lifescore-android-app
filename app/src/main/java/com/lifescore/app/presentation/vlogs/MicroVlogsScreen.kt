package com.lifescore.app.presentation.vlogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.ui.window.Dialog
import com.lifescore.app.core.util.DailyVlogStitcher
import com.lifescore.app.core.util.MicroVlogManager
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.MicroVlog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MicroVlogsScreen(
    viewModel: MicroVlogsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) } // 0: Daily 60s Vlog, 1: 12-Member Log Groups

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Micro-Vlog Studio", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val dummyClip = MicroVlog(
                                id = "clip_quick",
                                date = "2026-08-14",
                                dayOfWeek = "Today",
                                dimension = DimensionType.FITNESS,
                                caption = ""
                            )
                            viewModel.openRecordDialog(dummyClip)
                        }
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = "Quick Record 2s", tint = MaterialTheme.colorScheme.primary)
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
            // Tab Selector (Daily 60s Vlog vs 12-Member Log Groups)
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("🎬 Daily 60s Vlog", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("👥 Log Groups (${DailyVlogStitcher.MAX_GROUP_MEMBERS})", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
            }

            when (selectedTab) {
                // TAB 0: DAILY 60S VLOG STUDIO
                0 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 60-Second Stitched Reel Hero Card
                        item {
                            Card(
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color(0xFF6366F1).copy(alpha = 0.4f),
                                                    Color(0xFF0F172A)
                                                )
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
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color.Red.copy(alpha = 0.9f)
                                            ) {
                                                Text("60s AUTO-STITCHED", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color(0xFFFFD700).copy(alpha = 0.2f)
                                            ) {
                                                Text("+150 XP", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                            }
                                        }

                                        Spacer(Modifier.height(14.dp))

                                        Text(
                                            text = "Today's 60-Second Daily Reel",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 20.sp,
                                            color = Color.White
                                        )

                                        Text(
                                            text = "${uiState.weeklyClips.filter { it.isRecorded }.size} clips compiled • ${uiState.dailyStitchedVlog.totalDurationSeconds}s total • Dominant: ${uiState.dailyStitchedVlog.dominantDimension.displayName}",
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )

                                        Spacer(Modifier.height(18.dp))

                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Button(
                                                onClick = { viewModel.openReelPlayer() },
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.weight(1f).height(44.dp)
                                            ) {
                                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                                                Spacer(Modifier.width(6.dp))
                                                Text("Play 60s Reel", fontWeight = FontWeight.Bold)
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    val caption = DailyVlogStitcher.generateVlogShareCaption(uiState.dailyStitchedVlog)
                                                    val sendIntent = Intent().apply {
                                                        action = Intent.ACTION_SEND
                                                        putExtra(Intent.EXTRA_TEXT, caption)
                                                        type = "text/plain"
                                                    }
                                                    context.startActivity(Intent.createChooser(sendIntent, "Share Daily Vlog"))
                                                },
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                                                modifier = Modifier.height(44.dp)
                                            ) {
                                                Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 7-Day Habit Story Timeline
                        item {
                            Text("🗓️ Weekly Story Timeline (2s / day)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        items(uiState.weeklyClips) { clip ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.openRecordDialog(clip) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(clip.thumbnailColorHex),
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            if (clip.isRecorded) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                                            } else {
                                                Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White.copy(alpha = 0.6f))
                                            }
                                        }
                                    }

                                    Spacer(Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(clip.dayOfWeek, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Spacer(Modifier.width(8.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(clip.dimension.baseColorHex).copy(alpha = 0.2f)
                                            ) {
                                                Text(clip.dimension.displayName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(clip.dimension.baseColorHex), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }

                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = if (clip.isRecorded) clip.caption else "Tap to record 2.0s quick clip",
                                            fontSize = 12.sp,
                                            color = if (clip.isRecorded) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline
                                        )
                                    }

                                    Icon(
                                        if (clip.isRecorded) Icons.Default.PlayCircle else Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        tint = if (clip.isRecorded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }

                        item { Spacer(Modifier.height(20.dp)) }
                    }
                }

                // TAB 1: 12-MEMBER LOG GROUPS
                1 -> {
                    val group = uiState.selectedGroup
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Group Switcher Chips & Actions
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LazyRow(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(uiState.logGroups) { grp ->
                                        val isSelected = grp.id == group?.id
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { viewModel.selectGroup(grp) },
                                            label = { Text(grp.name, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) }
                                        )
                                    }
                                }

                                Row {
                                    IconButton(onClick = { viewModel.setCreateGroupDialogOpen(true) }) {
                                        Icon(Icons.Default.AddCircle, contentDescription = "Create Group", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { viewModel.setJoinGroupDialogOpen(true) }) {
                                        Icon(Icons.Default.GroupAdd, contentDescription = "Join Group", tint = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                            }
                        }

                        if (group != null) {
                            // Active Group Banner
                            item {
                                Card(
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(group.name, fontWeight = FontWeight.Black, fontSize = 18.sp)
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer
                                            ) {
                                                Text(
                                                    "${group.members.size}/${group.maxMembers} Members",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }

                                        Spacer(Modifier.height(4.dp))
                                        Text(group.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)

                                        Spacer(Modifier.height(12.dp))

                                        // Invite Code Pill with 1-Tap Copy
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                                                .clickable {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    clipboard.setPrimaryClip(ClipData.newPlainText("Log Group Invite", group.inviteCode))
                                                    Toast.makeText(context, "Invite Code ${group.inviteCode} copied! 📋", Toast.LENGTH_SHORT).show()
                                                }
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Invite Code: ${group.inviteCode}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }

                            // 12 Members Daily Progress Grid
                            item {
                                Text("🔥 Squad Daily Snaps (${group.members.count { it.hasRecordedToday }}/${group.members.size} Done Today)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            items(group.members) { member ->
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = if (member.hasRecordedToday) Color(0xFF4CAF50).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f),
                                            border = if (member.hasRecordedToday) BorderStroke(1.5.dp, Color(0xFF4CAF50)) else null,
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(member.avatarEmoji, fontSize = 18.sp)
                                            }
                                        }

                                        Spacer(Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(member.displayName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("${member.streakDays} Day Streak • ${member.todayClipsCount} snaps today", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (member.hasRecordedToday) Color(0xFF4CAF50) else Color.Gray.copy(alpha = 0.3f)
                                        ) {
                                            Text(
                                                text = if (member.hasRecordedToday) "SNAPPED" else "PENDING",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Emoji Reactions & Comments Header
                            item {
                                Card(
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text("⚡ React with Emojis", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Spacer(Modifier.height(8.dp))

                                        // 6 Instant Emoji Reaction Bubbles
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            listOf("🔥", "👏", "🚀", "❤️", "🤯", "⚡").forEach { emoji ->
                                                val count = uiState.dailyStitchedVlog.reactionCounts[emoji] ?: 0
                                                val isReacted = uiState.dailyStitchedVlog.userReactions.contains(emoji)

                                                Surface(
                                                    onClick = { viewModel.toggleReaction(emoji) },
                                                    shape = RoundedCornerShape(10.dp),
                                                    color = if (isReacted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                                    border = if (isReacted) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                                                    modifier = Modifier.height(38.dp)
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.padding(horizontal = 8.dp)
                                                    ) {
                                                        Text(emoji, fontSize = 14.sp)
                                                        Spacer(Modifier.width(4.dp))
                                                        Text("$count", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(Modifier.height(14.dp))
                                        Divider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                        Spacer(Modifier.height(10.dp))

                                        // Comments Thread Preview
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("💬 Comments (${uiState.dailyStitchedVlog.comments.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }

                                        Spacer(Modifier.height(6.dp))

                                        uiState.dailyStitchedVlog.comments.take(2).forEach { comment ->
                                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                                Text("${comment.authorEmoji} ${comment.authorName}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                                Text(comment.text, fontSize = 12.sp)
                                            }
                                        }

                                        Spacer(Modifier.height(8.dp))

                                        // Quick Add Comment Input
                                        var newCommentText by remember { mutableStateOf("") }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            OutlinedTextField(
                                                value = newCommentText,
                                                onValueChange = { newCommentText = it },
                                                placeholder = { Text("Cheer on your squad...", fontSize = 11.sp) },
                                                modifier = Modifier.weight(1f),
                                                singleLine = true,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            IconButton(
                                                onClick = {
                                                    viewModel.addComment(newCommentText)
                                                    newCommentText = ""
                                                }
                                            ) {
                                                Icon(Icons.Default.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item { Spacer(Modifier.height(30.dp)) }
                    }
                }
            }
        }
    }

    // CameraX 2-Second Quick Recording Dialog
    if (uiState.isRecordingDialogOpen && uiState.selectedClipToRecord != null) {
        CameraXCaptureDialog(
            clip = uiState.selectedClipToRecord!!,
            onDismiss = { viewModel.closeRecordDialog() },
            onSaveClip = { day, dim, cap ->
                viewModel.saveRecordedClip(day, dim, cap)
                Toast.makeText(context, "2s Vlog Snap Saved! 🎬 +25 XP", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Create Group Dialog
    if (uiState.isCreateGroupDialogOpen) {
        var groupName by remember { mutableStateOf("") }
        var groupDesc by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { viewModel.setCreateGroupDialogOpen(false) }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Create 12-Member Log Group", fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        label = { Text("Group Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = groupDesc,
                        onValueChange = { groupDesc = it },
                        label = { Text("Habit Focus / Goal") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.createLogGroup(groupName, groupDesc) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Create Squad (Max 12)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Join Group Dialog
    if (uiState.isJoinGroupDialogOpen) {
        var inviteCodeInput by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { viewModel.setJoinGroupDialogOpen(false) }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Join Log Group", fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Text("Enter the 6-character invite code (e.g. LOGS-77X)", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = inviteCodeInput,
                        onValueChange = { inviteCodeInput = it },
                        label = { Text("Invite Code") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val success = viewModel.joinLogGroup(inviteCodeInput)
                            if (success) {
                                Toast.makeText(context, "Joined Squad! 🎉", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Invalid Code or Squad Full!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Join Group", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
