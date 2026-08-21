package com.lifescore.app.presentation.enterprise

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.*
import com.lifescore.app.domain.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterpriseDashboardScreen(
    viewModel: EnterpriseViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.recentSuccessMessage) {
        uiState.recentSuccessMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LifeScore Enterprise Hub", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.selectedTab == EnterpriseTab.MEMBERS) {
                        FilledTonalButton(
                            onClick = { viewModel.openInviteDialog() },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Invite Colleague", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
            // 1. Hero Organization Header Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("ENTERPRISE WORKSPACE", fontSize = 10.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            Text(uiState.org.companyName, fontSize = 20.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text("Domain: @${uiState.org.domain} • Admin: ${uiState.org.adminEmail}", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF6366F1),
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Text(uiState.org.planTier.badgeLabel, fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Active Seats", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                Text("${uiState.totalSeatsUsed} / ${uiState.org.totalSeats}", fontWeight = FontWeight.Black, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), modifier = Modifier.weight(1.2f)) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Company Vitality Index", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                Text(String.format("%.1f / 100", uiState.companyVitalityIndex), fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color(0xFF10B981))
                            }
                        }
                    }
                }
            }

            // 2. Tab Navigation
            TabRow(
                selectedTabIndex = uiState.selectedTab.ordinal,
                modifier = Modifier.fillMaxWidth()
            ) {
                EnterpriseTab.values().forEach { tab ->
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        text = { Text(tab.title, fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                    )
                }
            }

            // 3. Tab Views
            when (uiState.selectedTab) {
                EnterpriseTab.MEMBERS -> {
                    TeamRosterView(members = uiState.members)
                }
                EnterpriseTab.QUESTS -> {
                    CompanyQuestsView(
                        challenges = uiState.challenges,
                        leaderboard = uiState.departmentLeaderboard
                    )
                }
                EnterpriseTab.ANALYTICS -> {
                    AdminAnalyticsView(
                        uiState = uiState,
                        onExportCsv = { Toast.makeText(context, "Exported Acme_Q3_Vitality_Report.csv! 📥", Toast.LENGTH_SHORT).show() }
                    )
                }
                EnterpriseTab.BILLING -> {
                    B2BBillingView(
                        uiState = uiState,
                        onSeatsChange = { viewModel.setBillingSeats(it) },
                        onToggleAnnual = { viewModel.toggleBillingPeriod() },
                        onSelectPlan = { viewModel.selectPlanTier(it) },
                        onDownloadInvoice = { Toast.makeText(context, "Invoice INV-2026-ACME downloaded! 📄", Toast.LENGTH_SHORT).show() }
                    )
                }
            }
        }
    }

    // Invite Colleague Modal
    if (uiState.isInviteDialogOpen) {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var dept by remember { mutableStateOf(DepartmentType.ENGINEERING) }
        var role by remember { mutableStateOf(EnterpriseRole.MEMBER) }

        Dialog(onDismissRequest = { viewModel.closeInviteDialog() }) {
            Card(
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Invite Colleague to Acme Workspace", fontWeight = FontWeight.Black, fontSize = 16.sp)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Work Email (@acme.com)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))

                    Text("Department:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(DepartmentType.values()) { d ->
                            FilterChip(
                                selected = dept == d,
                                onClick = { dept = d },
                                label = { Text("${d.iconEmoji} ${d.displayName.take(12)}", fontSize = 10.sp) }
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.inviteMember(name, email, dept, role) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Send Workspace Invite", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TeamRosterView(members: List<EnterpriseMember>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(members) { member ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(member.avatarEmoji, fontSize = 22.sp)
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(member.displayName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (member.role == EnterpriseRole.ADMIN) Color(0xFFFFD700).copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Text("${member.role.badgeEmoji} ${member.role.displayName}", fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                        Text("${member.email} • ${member.department.displayName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Score: ${member.lifeScore}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            Text("🔥 ${member.currentStreak}d streak", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                            Text("✅ ${member.weeklyQuestsCompleted} quests/wk", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                    }

                    if (member.isBurnoutRisk) {
                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFEF4444).copy(alpha = 0.2f)) {
                            Text("⚠️ Burnout Alert", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444), modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(30.dp)) }
    }
}

@Composable
fun CompanyQuestsView(
    challenges: List<EnterpriseChallenge>,
    leaderboard: List<DepartmentLeaderboardItem>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("🏢 Active Company-Wide Challenges", fontWeight = FontWeight.Black, fontSize = 16.sp)
        }

        items(challenges) { ch ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(ch.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF10B981).copy(alpha = 0.2f)) {
                            Text("+${ch.rewardXpPerMember} XP/Seat", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(ch.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline, lineHeight = 15.sp)

                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Progress: ${ch.currentProgress} / ${ch.targetGoal} ${ch.unit}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("${String.format("%.0f", ch.progressFraction * 100)}%", fontSize = 11.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { ch.progressFraction },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                        color = Color(0xFF10B981)
                    )
                }
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            Text("🏆 Department Rankings & LifeScore Derby", fontWeight = FontWeight.Black, fontSize = 16.sp)
        }

        items(leaderboard) { dept ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = when (dept.rank) {
                            1 -> Color(0xFFFFD700)
                            2 -> Color(0xFFC0C0C0)
                            3 -> Color(0xFFCD7F32)
                            else -> MaterialTheme.colorScheme.surface
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("#${dept.rank}", fontWeight = FontWeight.Black, fontSize = 13.sp, color = if (dept.rank <= 3) Color.Black else MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text("${dept.department.iconEmoji} ${dept.department.displayName}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${dept.memberCount} Team Members • ${dept.totalQuestsCompleted} Quests Completed", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(String.format("%.0f avg", dept.averageLifeScore), fontWeight = FontWeight.Black, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                        Text("🔥 ${String.format("%.0f", dept.averageStreak)}d streak", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }

        item { Spacer(Modifier.height(30.dp)) }
    }
}

@Composable
fun AdminAnalyticsView(
    uiState: EnterpriseUiState,
    onExportCsv: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("EXECUTIVE HEALTH OVERVIEW", fontSize = 10.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    Text("Organizational Burnout Risk & Vitality", fontWeight = FontWeight.Black, fontSize = 16.sp)
                    Text("Algorithmic detection of overwork, missed sleep habits, and sustained high workloads.", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    Spacer(Modifier.height(14.dp))

                    Button(
                        onClick = onExportCsv,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Export Q3 Enterprise Wellness Audit (CSV)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Text("Department Burnout Risk Analysis", fontWeight = FontWeight.Black, fontSize = 15.sp)
        }

        items(uiState.burnoutMetrics) { metric ->
            Card(
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
                        Text("${metric.department.iconEmoji} ${metric.department.displayName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (metric.riskLevel) {
                                "HIGH" -> Color(0xFFEF4444).copy(alpha = 0.2f)
                                "MODERATE" -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                                else -> Color(0xFF10B981).copy(alpha = 0.2f)
                            }
                        ) {
                            Text(
                                text = "${metric.riskLevel} RISK (${metric.riskScorePercent}%)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = when (metric.riskLevel) {
                                    "HIGH" -> Color(0xFFEF4444)
                                    "MODERATE" -> Color(0xFFF59E0B)
                                    else -> Color(0xFF10B981)
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(metric.recommendations, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                }
            }
        }

        item { Spacer(Modifier.height(30.dp)) }
    }
}

@Composable
fun B2BBillingView(
    uiState: EnterpriseUiState,
    onSeatsChange: (Int) -> Unit,
    onToggleAnnual: () -> Unit,
    onSelectPlan: (B2BPlanTier) -> Unit,
    onDownloadInvoice: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("B2B SUBSCRIPTION & QUOTE CALCULATOR", fontSize = 10.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    Text("Estimated Annual Cost: $${String.format("%.2f", uiState.billingTotalQuote)}", fontWeight = FontWeight.Black, fontSize = 20.sp)
                    Text("Includes all ${uiState.billingSeatsCount} seats on ${uiState.selectedPlanForBilling.title}", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Billing Cycle:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        FilterChip(
                            selected = uiState.isAnnualBilling,
                            onClick = onToggleAnnual,
                            label = { Text(if (uiState.isAnnualBilling) "Annual (Save 20% 🎉)" else "Monthly", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    Text("Seat Capacity: ${uiState.billingSeatsCount} Seats", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Slider(
                        value = uiState.billingSeatsCount.toFloat(),
                        onValueChange = { onSeatsChange(it.toInt()) },
                        valueRange = 10f..250f,
                        steps = 24
                    )
                }
            }
        }

        item {
            Text("Available Enterprise Tiers", fontWeight = FontWeight.Black, fontSize = 15.sp)
        }

        items(B2BPlanTier.values()) { tier ->
            val isSelected = uiState.selectedPlanForBilling == tier
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ),
                border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                modifier = Modifier.fillMaxWidth().clickable { onSelectPlan(tier) }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(tier.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(Modifier.width(6.dp))
                            Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.primary) {
                                Text(tier.badgeLabel, fontSize = 8.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                            }
                        }
                        Text("$${tier.annualPerSeatCost}/seat/mo (billed annually)", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    }

                    RadioButton(selected = isSelected, onClick = { onSelectPlan(tier) })
                }
            }
        }

        item {
            Button(
                onClick = onDownloadInvoice,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.ReceiptLong, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Generate Official B2B Invoice PDF", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(30.dp))
        }
    }
}
