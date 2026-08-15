package com.lifescore.app.presentation.coach

import android.content.Intent
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.core.util.*
import com.lifescore.app.data.repository.GeminiCoachRepository
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.data.repository.WeeklyAuditResult
import com.lifescore.app.domain.model.DimensionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

enum class CoachTab(val title: String) {
    CHAT("💬 Daily Coach"),
    AUDIT("📊 Weekly Audit"),
    MEMORY("🧠 Memory Vault")
}

data class ChatMessage(
    val sender: String, // "AI" or "USER"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class CoachUiState(
    val selectedTab: CoachTab = CoachTab.CHAT,
    val totalScore: Int = 780,
    val streak: Int = 8,
    val lowestDimension: DimensionType = DimensionType.HEALTH,
    val lowestScore: Int = 45,
    val highestDimension: DimensionType = DimensionType.CAREER,
    val highestScore: Int = 88,
    val diagnosticGuidance: String = "",
    val weeklyAudit: WeeklyAuditResult? = null,
    val chatMessages: List<ChatMessage> = emptyList(),
    val memories: List<UserMemoryNode> = AiMemoryEngine.getDefaultMemories(),
    val journalEntries: List<JournalEntry> = AiMemoryEngine.getDefaultJournalEntries(),
    val behavioralReflections: List<BehavioralReflection> = AiMemoryEngine.getBehavioralReflections(),
    val isAddMemoryDialogOpen: Boolean = false,
    val isGenerating: Boolean = false
)

class AiCoachViewModel(
    private val coachRepository: GeminiCoachRepository,
    private val lifeScoreRepository: LifeScoreRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoachUiState())
    val uiState: StateFlow<CoachUiState> = _uiState.asStateFlow()

    init {
        observeUserData()
    }

    fun selectTab(tab: CoachTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
        if (tab == CoachTab.AUDIT && _uiState.value.weeklyAudit == null) {
            generateWeeklyAudit()
        }
    }

    private fun observeUserData() {
        viewModelScope.launch {
            if (lifeScoreRepository != null) {
                combine(
                    lifeScoreRepository.getAllTasks(),
                    lifeScoreRepository.getUserProfile()
                ) { tasks, user ->
                    val scores = DimensionType.values().associateWith { dim ->
                        val dimTasks = tasks.filter { it.dimension == dim }
                        val completed = dimTasks.count { it.isCompleted }
                        ScoreEngine.calculateDimensionScore(completed, dimTasks.size)
                    }
                    val overall = ScoreEngine.calculateOverallLifeScore(scores)
                    val sorted = scores.entries.sortedBy { it.value }
                    val lowest = sorted.firstOrNull()?.key ?: DimensionType.HEALTH
                    val highest = sorted.lastOrNull()?.key ?: DimensionType.CAREER

                    val guidance = coachRepository.generateDimensionGuidance(lowest, scores[lowest] ?: 45, isWeakest = true)

                    _uiState.value = _uiState.value.copy(
                        totalScore = overall,
                        streak = user.currentStreakDays,
                        lowestDimension = lowest,
                        lowestScore = scores[lowest] ?: 45,
                        highestDimension = highest,
                        highestScore = scores[highest] ?: 88,
                        diagnosticGuidance = guidance
                    )

                    if (_uiState.value.chatMessages.isEmpty()) {
                        loadInitialBrief(lowest, scores[lowest] ?: 45, overall)
                    }
                }.collect {}
            } else {
                loadInitialBrief(DimensionType.HEALTH, 45, 780)
            }
        }
    }

    private fun loadInitialBrief(dim: DimensionType, score: Int, total: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGenerating = true)
            val brief = coachRepository.getDailyExecutiveBrief(dim, score, total)
            _uiState.value = _uiState.value.copy(
                isGenerating = false,
                chatMessages = listOf(
                    ChatMessage(
                        sender = "AI",
                        message = "👋 **Welcome back, Achiever!** I've synchronized your persistent memory & 8 dimensions.\n\n$brief"
                    )
                )
            )
        }
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return
        val currentList = _uiState.value.chatMessages.toMutableList()
        currentList.add(ChatMessage(sender = "USER", message = userText))
        _uiState.value = _uiState.value.copy(chatMessages = currentList, isGenerating = true)

        viewModelScope.launch {
            val memoryContext = AiMemoryEngine.buildSystemContextPrompt(
                memories = _uiState.value.memories,
                archetypeName = "The Architect",
                lifeScore = _uiState.value.totalScore,
                streak = _uiState.value.streak
            )
            val reply = coachRepository.askCoachWithMemory(userText, memoryContext)
            val updatedList = _uiState.value.chatMessages.toMutableList()
            updatedList.add(ChatMessage(sender = "AI", message = reply))
            _uiState.value = _uiState.value.copy(chatMessages = updatedList, isGenerating = false)
        }
    }

    fun deleteMemory(id: String) {
        val filtered = _uiState.value.memories.filter { it.id != id }
        _uiState.value = _uiState.value.copy(memories = filtered)
    }

    fun addMemory(category: MemoryCategory, title: String, detail: String) {
        if (title.isBlank()) return
        val newNode = UserMemoryNode(category = category, title = title, detail = detail)
        _uiState.value = _uiState.value.copy(
            memories = listOf(newNode) + _uiState.value.memories,
            isAddMemoryDialogOpen = false
        )
    }

    fun setAddMemoryDialogOpen(isOpen: Boolean) {
        _uiState.value = _uiState.value.copy(isAddMemoryDialogOpen = isOpen)
    }

    private fun generateWeeklyAudit() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGenerating = true)
            val mockScores = DimensionType.values().associateWith { 75 }
            val audit = coachRepository.generateWeeklyAudit(
                scores = mockScores,
                tasksCompleted = 24,
                totalScore = _uiState.value.totalScore,
                streak = _uiState.value.streak
            )
            _uiState.value = _uiState.value.copy(
                weeklyAudit = audit,
                isGenerating = false
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiCoachScreen(
    viewModel: AiCoachViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var inputQuery by remember { mutableStateOf("") }

    val quickPrompts = listOf(
        "What challenge should I do next after completing my 30-day fitness quest?",
        "How do I overcome my morning task friction?",
        "Why am I most consistent on Tuesdays?",
        "How do I boost my ${uiState.lowestDimension.displayName} score?"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LifeScore AI Coach", fontWeight = FontWeight.Black) },
                actions = {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("🧠", fontSize = 12.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Gemini 1.5 + Memory",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
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
            // 3 Tabs: Daily Coach, Weekly Audit, Memory Vault
            TabRow(
                selectedTabIndex = uiState.selectedTab.ordinal,
                modifier = Modifier.fillMaxWidth()
            ) {
                CoachTab.values().forEach { tab ->
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        text = { Text(tab.title, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                }
            }

            when (uiState.selectedTab) {
                // TAB 1: DAILY COACH CONVERSATION
                CoachTab.CHAT -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Diagnostic Banner
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(uiState.lowestDimension.baseColorHex).copy(alpha = 0.2f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("⚠️", fontSize = 16.sp)
                                    }
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Priority Frontier: ${uiState.lowestDimension.displayName} (${uiState.lowestScore}%)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(uiState.lowestDimension.baseColorHex)
                                    )
                                    Text(
                                        text = uiState.diagnosticGuidance,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // Quick Prompt Chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(quickPrompts) { prompt ->
                                Surface(
                                    onClick = { viewModel.sendMessage(prompt) },
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                ) {
                                    Text(
                                        text = prompt,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // Message Stream
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(uiState.chatMessages) { chat ->
                                val isAi = chat.sender == "AI"
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End
                                ) {
                                    Card(
                                        shape = RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isAi) 2.dp else 16.dp,
                                            bottomEnd = if (isAi) 16.dp else 2.dp
                                        ),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isAi) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary
                                        ),
                                        modifier = Modifier.widthIn(max = 300.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            if (isAi) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text("🧠", fontSize = 12.sp)
                                                    Spacer(Modifier.width(4.dp))
                                                    Text("AI Coach (Memory-Aware)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                                }
                                                Spacer(Modifier.height(4.dp))
                                            }
                                            Text(
                                                text = chat.message,
                                                fontSize = 13.sp,
                                                lineHeight = 19.sp,
                                                color = if (isAi) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                                            )
                                        }
                                    }
                                }
                            }

                            if (uiState.isGenerating) {
                                item {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                        Spacer(Modifier.width(8.dp))
                                        Text("AI Coach is consulting memory vault...", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                    }
                                }
                            }
                        }

                        // Input Box
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = inputQuery,
                                onValueChange = { inputQuery = it },
                                placeholder = { Text("Ask your AI coach anything...", fontSize = 12.sp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(24.dp),
                                maxLines = 3
                            )
                            Spacer(Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    viewModel.sendMessage(inputQuery)
                                    inputQuery = ""
                                },
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                                    .size(46.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }

                // TAB 2: WEEKLY AUDIT
                CoachTab.AUDIT -> {
                    val audit = uiState.weeklyAudit
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        if (audit != null) {
                            item {
                                Card(
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(18.dp)) {
                                        Text(audit.headline, fontWeight = FontWeight.Black, fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                        Spacer(Modifier.height(6.dp))
                                        Text(audit.pointSummary, fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                    }
                                }
                            }

                            item {
                                Card(
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("🏆 Key Achievements", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Spacer(Modifier.height(8.dp))
                                        audit.keyAchievements.forEach { ach ->
                                            Row(modifier = Modifier.padding(vertical = 3.dp)) {
                                                Text("✓", fontWeight = FontWeight.Black, color = Color(0xFF4CAF50))
                                                Spacer(Modifier.width(8.dp))
                                                Text(ach, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                Card(
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("🎯 Next Week Directives", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Spacer(Modifier.height(8.dp))
                                        audit.nextWeekDirectives.forEach { dir ->
                                            Row(modifier = Modifier.padding(vertical = 3.dp)) {
                                                Text("➔", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                                Spacer(Modifier.width(8.dp))
                                                Text(dir, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                Button(
                                    onClick = {
                                        val shareText = "📊 LifeScore Weekly Executive Audit:\n${audit.headline}\n${audit.pointSummary}\n\nTop Dimension: ${audit.topDimension.displayName}\nTrack your growth with LifeScore: https://lifescore.app"
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Share Weekly Audit"))
                                    },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Share Weekly Audit", fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.height(20.dp))
                            }
                        } else {
                            item {
                                Box(modifier = Modifier.fillMaxSize().padding(40.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }

                // TAB 3: 🧠 MEMORY VAULT
                CoachTab.MEMORY -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Behavioral Reflections Header
                        item {
                            Text("⚡ Discovered Behavioral Patterns", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }

                        items(uiState.behavioralReflections) { ref ->
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
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(ref.icon, fontSize = 18.sp)
                                            Spacer(Modifier.width(8.dp))
                                            Text(ref.headline, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        ) {
                                            Text(ref.metricHighlight, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }

                                    Spacer(Modifier.height(6.dp))
                                    Text(ref.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(Modifier.height(8.dp))
                                    Text("🎯 Directive: ${ref.actionDirective}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        // Active AI Memory Nodes
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🗂️ Active AI Memory Nodes (${uiState.memories.size})", fontWeight = FontWeight.Black, fontSize = 16.sp)
                                IconButton(onClick = { viewModel.setAddMemoryDialogOpen(true) }) {
                                    Icon(Icons.Default.AddCircle, contentDescription = "Add Memory", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        items(uiState.memories) { mem ->
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
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(mem.category.badgeColorHex).copy(alpha = 0.2f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(mem.category.icon, fontSize = 16.sp)
                                        }
                                    }

                                    Spacer(Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(mem.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Spacer(Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(mem.category.badgeColorHex).copy(alpha = 0.15f)
                                            ) {
                                                Text(mem.category.displayName, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(mem.category.badgeColorHex), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                            }
                                        }
                                        Spacer(Modifier.height(3.dp))
                                        Text(mem.detail, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                    }

                                    IconButton(onClick = { viewModel.deleteMemory(mem.id) }) {
                                        Icon(Icons.Default.Close, contentDescription = "Forget", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }

                        // Recent Journal Entries
                        item {
                            Text("📝 Recent Journal Reflections", fontWeight = FontWeight.Black, fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp))
                        }

                        items(uiState.journalEntries) { journal ->
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
                                        Text(journal.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(journal.mood, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(journal.body, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)

                                    Spacer(Modifier.height(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = journal.aiReflection,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(8.dp)
                                        )
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

    // Add Memory Node Dialog
    if (uiState.isAddMemoryDialogOpen) {
        var memTitle by remember { mutableStateOf("") }
        var memDetail by remember { mutableStateOf("") }
        var memCategory by remember { mutableStateOf(MemoryCategory.HABIT_STRUGGLE) }

        Dialog(onDismissRequest = { viewModel.setAddMemoryDialogOpen(false) }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Add AI Memory Node", fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Spacer(Modifier.height(12.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(MemoryCategory.values()) { cat ->
                            val isSel = memCategory == cat
                            FilterChip(
                                selected = isSel,
                                onClick = { memCategory = cat },
                                label = { Text("${cat.icon} ${cat.displayName}", fontSize = 10.sp) }
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = memTitle,
                        onValueChange = { memTitle = it },
                        label = { Text("Memory Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = memDetail,
                        onValueChange = { memDetail = it },
                        label = { Text("Observation / Detail") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.addMemory(memCategory, memTitle, memDetail) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save to Memory Vault", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
