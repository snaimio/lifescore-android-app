package com.lifescore.app.presentation.ui.tasks

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.lifescore.app.core.util.ChainNode
import com.lifescore.app.core.util.MicroHabitChallenge
import com.lifescore.app.core.util.MicroHabitManager
import com.lifescore.app.core.util.SmartHabitEngine
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.*

enum class HabitTab(val title: String) {
    HABITS("⚡ Habits"),
    CHAIN("🔗 Chains"),
    CHALLENGES("🏆 30-Day")
}

data class TasksUiState(
    val selectedTab: HabitTab = HabitTab.HABITS,
    val tasks: List<LifeTask> = SmartHabitEngine.getDefaultAdvancedHabits(),
    val filterDimension: DimensionType? = null,
    val chainNodes: List<ChainNode> = MicroHabitManager.generate30DayChain(7),
    val challenges: List<MicroHabitChallenge> = MicroHabitManager.getDefault30DayChallenges(),
    val currentStreak: Int = 7,
    val smartSuggestedHabit: LifeTask = SmartHabitEngine.getSmartSuggestionForDimension(DimensionType.HEALTH),
    val recentSuccessMessage: String? = null
)

class TasksViewModel(
    private val repository: LifeScoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            repository.getAllTasks().collect { allTasks ->
                if (allTasks.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(tasks = allTasks)
                }
            }
        }
        viewModelScope.launch {
            repository.getUserProfile().collect { profile ->
                _uiState.value = _uiState.value.copy(
                    currentStreak = profile.currentStreakDays,
                    chainNodes = MicroHabitManager.generate30DayChain(profile.currentStreakDays)
                )
            }
        }
    }

    fun selectTab(tab: HabitTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun toggleTask(task: LifeTask) {
        val updated = task.copy(
            isCompleted = !task.isCompleted,
            streakDays = if (!task.isCompleted) task.streakDays + 1 else task.streakDays
        )
        val list = _uiState.value.tasks.map { if (it.id == task.id) updated else it }
        _uiState.value = _uiState.value.copy(
            tasks = list,
            recentSuccessMessage = if (updated.isCompleted) "Completed '${task.title}'! +${task.pointsReward} XP" else null
        )
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
        }
    }

    fun incrementCounter(task: LifeTask, amount: Int = 1) {
        val updated = SmartHabitEngine.incrementCounter(task, amount)
        val list = _uiState.value.tasks.map { if (it.id == task.id) updated else it }
        _uiState.value = _uiState.value.copy(
            tasks = list,
            recentSuccessMessage = if (updated.isCompleted && !task.isCompleted) "🎉 Target Reached! +${task.pointsReward} XP" else null
        )
    }

    fun decrementCounter(task: LifeTask, amount: Int = 1) {
        val updated = SmartHabitEngine.decrementCounter(task, amount)
        val list = _uiState.value.tasks.map { if (it.id == task.id) updated else it }
        _uiState.value = _uiState.value.copy(tasks = list)
    }

    fun toggleSubTask(task: LifeTask, subTaskId: String) {
        val updated = SmartHabitEngine.toggleSubTask(task, subTaskId)
        val list = _uiState.value.tasks.map { if (it.id == task.id) updated else it }
        _uiState.value = _uiState.value.copy(
            tasks = list,
            recentSuccessMessage = if (updated.isCompleted && !task.isCompleted) "🏆 Routine Finished! +${task.pointsReward} XP" else null
        )
    }

    fun addSmartSuggestedHabit(suggested: LifeTask) {
        val newTask = suggested.copy(id = System.currentTimeMillis())
        _uiState.value = _uiState.value.copy(
            tasks = listOf(newTask) + _uiState.value.tasks,
            smartSuggestedHabit = SmartHabitEngine.getSmartSuggestionForDimension(DimensionType.LEARNING),
            recentSuccessMessage = "Added Smart Habit: '${suggested.title}'!"
        )
    }

    fun addAdvancedHabit(
        title: String,
        dimension: DimensionType,
        points: Int,
        habitType: HabitType,
        targetCount: Int = 1,
        countUnit: String = "",
        subTaskTitles: List<String> = emptyList()
    ) {
        if (title.isBlank()) return
        val subItems = subTaskTitles.filter { it.isNotBlank() }.map { SubTaskItem(title = it) }
        val newTask = LifeTask(
            id = System.currentTimeMillis(),
            title = title,
            dimension = dimension,
            pointsReward = points,
            habitType = habitType,
            targetCount = if (habitType == HabitType.COUNTER) targetCount.coerceAtLeast(1) else 1,
            countUnit = countUnit,
            subTasks = subItems
        )

        _uiState.value = _uiState.value.copy(
            tasks = listOf(newTask) + _uiState.value.tasks,
            recentSuccessMessage = "Created new ${habitType.displayName}: '$title'!"
        )
        viewModelScope.launch {
            repository.addTask(title, dimension, points)
        }
    }

    fun deleteTask(task: LifeTask) {
        val list = _uiState.value.tasks.filter { it.id != task.id }
        _uiState.value = _uiState.value.copy(tasks = list)
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun setFilter(dimension: DimensionType?) {
        _uiState.value = _uiState.value.copy(filterDimension = dimension)
    }

    fun toggleChallengeJoin(challengeId: String) {
        val updated = _uiState.value.challenges.map { ch ->
            if (ch.id == challengeId) ch.copy(isJoined = !ch.isJoined) else ch
        }
        _uiState.value = _uiState.value.copy(challenges = updated)
    }

    fun incrementChallengeDay(challengeId: String) {
        val updated = _uiState.value.challenges.map { ch ->
            if (ch.id == challengeId) {
                val nextDay = (ch.currentDay + 1).coerceAtMost(ch.totalDays)
                ch.copy(currentDay = nextDay, isCompleted = nextDay >= ch.totalDays)
            } else ch
        }
        _uiState.value = _uiState.value.copy(challenges = updated)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(recentSuccessMessage = null)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    navController: NavController,
    viewModel: TasksViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.recentSuccessMessage) {
        uiState.recentSuccessMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quests & Micro-Habits", fontWeight = FontWeight.Black) },
                actions = {
                    FilledTonalButton(
                        onClick = { showCreateDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("New Habit", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
            // 3 Tabs
            TabRow(
                selectedTabIndex = uiState.selectedTab.ordinal,
                modifier = Modifier.fillMaxWidth()
            ) {
                HabitTab.values().forEach { tab ->
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        text = { Text(tab.title, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                }
            }

            when (uiState.selectedTab) {
                HabitTab.HABITS -> {
                    HabitsListView(
                        uiState = uiState,
                        onToggleTask = { viewModel.toggleTask(it) },
                        onIncrementCounter = { task, amt -> viewModel.incrementCounter(task, amt) },
                        onDecrementCounter = { task, amt -> viewModel.decrementCounter(task, amt) },
                        onToggleSubTask = { task, stId -> viewModel.toggleSubTask(task, stId) },
                        onDeleteTask = { viewModel.deleteTask(it) },
                        onFilter = { viewModel.setFilter(it) },
                        onAddSmartHabit = { viewModel.addSmartSuggestedHabit(it) }
                    )
                }
                HabitTab.CHAIN -> {
                    DontBreakTheChainView(uiState = uiState)
                }
                HabitTab.CHALLENGES -> {
                    Challenges30DayView(
                        challenges = uiState.challenges,
                        onToggleJoin = { viewModel.toggleChallengeJoin(it) },
                        onIncrementDay = { viewModel.incrementChallengeDay(it) }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateAdvancedHabitDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { title, dim, pts, type, target, unit, subTasks ->
                viewModel.addAdvancedHabit(title, dim, pts, type, target, unit, subTasks)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun HabitsListView(
    uiState: TasksUiState,
    onToggleTask: (LifeTask) -> Unit,
    onIncrementCounter: (LifeTask, Int) -> Unit,
    onDecrementCounter: (LifeTask, Int) -> Unit,
    onToggleSubTask: (LifeTask, String) -> Unit,
    onDeleteTask: (LifeTask) -> Unit,
    onFilter: (DimensionType?) -> Unit,
    onAddSmartHabit: (LifeTask) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val filtered = if (uiState.filterDimension == null) {
        uiState.tasks
    } else {
        uiState.tasks.filter { it.dimension == uiState.filterDimension }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Smart Auto-Suggestion Hero Banner
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("✨", fontSize = 18.sp)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("SMART HABIT RECOMMENDATION", fontSize = 10.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                        Text(uiState.smartSuggestedHabit.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text("+${uiState.smartSuggestedHabit.pointsReward} XP • ${uiState.smartSuggestedHabit.dimension.displayName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onAddSmartHabit(uiState.smartSuggestedHabit) },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Add +", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Dimension Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = uiState.filterDimension == null,
                        onClick = { onFilter(null) },
                        label = { Text("All", fontSize = 11.sp) }
                    )
                }
                items(DimensionType.values()) { dim ->
                    FilterChip(
                        selected = uiState.filterDimension == dim,
                        onClick = { onFilter(dim) },
                        label = { Text(dim.displayName, fontSize = 11.sp) }
                    )
                }
            }
        }

        // Habit Cards
        items(filtered) { task ->
            when (task.habitType) {
                HabitType.COUNTER -> {
                    CounterHabitCard(
                        task = task,
                        onIncrement = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onIncrementCounter(task, 1)
                        },
                        onDecrement = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onDecrementCounter(task, 1)
                        },
                        onDelete = { onDeleteTask(task) }
                    )
                }
                HabitType.SUB_TASKS -> {
                    SubTasksHabitCard(
                        task = task,
                        onToggleSubTask = { stId ->
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onToggleSubTask(task, stId)
                        },
                        onDelete = { onDeleteTask(task) }
                    )
                }
                else -> {
                    BooleanHabitCard(
                        task = task,
                        onToggle = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onToggleTask(task)
                        },
                        onDelete = { onDeleteTask(task) }
                    )
                }
            }
        }

        item { Spacer(Modifier.height(30.dp)) }
    }
}

@Composable
fun CounterHabitCard(
    task: LifeTask,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onDelete: () -> Unit
) {
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
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(task.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Delete", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${task.dimension.displayName} • +${task.pointsReward} XP • 🔥 ${task.streakDays}d streak",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "${task.currentCount}/${task.targetCount} ${task.countUnit}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = if (task.isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { task.counterProgress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color = if (task.isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (task.isCompleted) {
                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF10B981).copy(alpha = 0.2f)) {
                        Text("✓ Target Reached! (+${task.pointsReward} XP)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                } else {
                    Text("${String.format("%.0f", task.counterProgress * 100)}% complete", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onDecrement,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("-1", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = onIncrement,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("+1 ${task.countUnit.take(5)}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SubTasksHabitCard(
    task: LifeTask,
    onToggleSubTask: (String) -> Unit,
    onDelete: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }
    val completedSteps = task.subTasks.count { it.isCompleted }

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
                Column(modifier = Modifier.weight(1f)) {
                    Text(task.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("${task.dimension.displayName} • Routine • +${task.pointsReward} XP", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (task.isCompleted) Color(0xFF10B981).copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "$completedSteps/${task.subTasks.size} Steps",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (task.isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    IconButton(onClick = { isExpanded = !isExpanded }, modifier = Modifier.size(28.dp)) {
                        Icon(if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = "Expand")
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Delete", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { task.subTaskProgress },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                color = if (task.isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
            )

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    task.subTasks.forEach { sub ->
                        Surface(
                            onClick = { onToggleSubTask(sub.id) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (sub.isCompleted) MaterialTheme.colorScheme.surface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = sub.isCompleted,
                                    onCheckedChange = { onToggleSubTask(sub.id) },
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = sub.title,
                                    fontSize = 12.sp,
                                    color = if (sub.isCompleted) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BooleanHabitCard(
    task: LifeTask,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() },
                modifier = Modifier.size(28.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${task.dimension.displayName} • +${task.pointsReward} XP • 🔥 ${task.streakDays}d streak",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Delete", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun DontBreakTheChainView(uiState: TasksUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("DON'T BREAK THE CHAIN", fontSize = 11.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    Text("🔥 ${uiState.currentStreak}-Day Active Momentum", fontSize = 18.sp, fontWeight = FontWeight.Black)
                    Text("Jerry Seinfeld's rule: Mark an X for each day of habit completion. Don't break the chain!", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                }
            }
        }

        item {
            Text("30-Day Consistency Matrix", fontWeight = FontWeight.Black, fontSize = 15.sp)
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                modifier = Modifier.height(280.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.chainNodes) { node ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = when {
                            node.isCompleted -> Color(0xFF10B981)
                            node.isToday -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        modifier = Modifier.height(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (node.isCompleted) {
                                Text("✓", color = Color.White, fontWeight = FontWeight.Black)
                            } else {
                                Text("D${node.dayNumber}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (node.isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Challenges30DayView(
    challenges: List<MicroHabitChallenge>,
    onToggleJoin: (String) -> Unit,
    onIncrementDay: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
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
                        Text("+${ch.xpReward} XP", fontWeight = FontWeight.Black, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(ch.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)

                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Day ${ch.currentDay}/${ch.totalDays}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Button(
                            onClick = { if (ch.isJoined) onIncrementDay(ch.id) else onToggleJoin(ch.id) },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (ch.isJoined) "Check-In D${ch.currentDay + 1}" else "Join Challenge", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAdvancedHabitDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, DimensionType, Int, HabitType, Int, String, List<String>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedDim by remember { mutableStateOf(DimensionType.HEALTH) }
    var selectedType by remember { mutableStateOf(HabitType.BOOLEAN) }
    var points by remember { mutableStateOf(20) }
    var targetCount by remember { mutableStateOf("8") }
    var countUnit by remember { mutableStateOf("glasses") }
    var subStep1 by remember { mutableStateOf("") }
    var subStep2 by remember { mutableStateOf("") }
    var subStep3 by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Create Advanced Habit", fontWeight = FontWeight.Black, fontSize = 18.sp)
                Spacer(Modifier.height(12.dp))

                // Habit Type Chips
                Text("Habit Type:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(listOf(HabitType.BOOLEAN, HabitType.COUNTER, HabitType.SUB_TASKS)) { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text("${type.icon} ${type.displayName}", fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Habit Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Counter specifics
                if (selectedType == HabitType.COUNTER) {
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = targetCount,
                            onValueChange = { targetCount = it },
                            label = { Text("Target Goal") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = countUnit,
                            onValueChange = { countUnit = it },
                            label = { Text("Unit (e.g. glasses)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Sub-Tasks specifics
                if (selectedType == HabitType.SUB_TASKS) {
                    Spacer(Modifier.height(8.dp))
                    Text("Sub-Tasks / Routine Steps:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(value = subStep1, onValueChange = { subStep1 = it }, label = { Text("Step 1") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(value = subStep2, onValueChange = { subStep2 = it }, label = { Text("Step 2") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(value = subStep3, onValueChange = { subStep3 = it }, label = { Text("Step 3") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }

                Spacer(Modifier.height(10.dp))
                Text("Dimension:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(DimensionType.values()) { dim ->
                        FilterChip(
                            selected = selectedDim == dim,
                            onClick = { selectedDim = dim },
                            label = { Text(dim.displayName, fontSize = 10.sp) }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        val target = targetCount.toIntOrNull() ?: 8
                        val steps = listOf(subStep1, subStep2, subStep3)
                        onConfirm(title, selectedDim, points, selectedType, target, countUnit, steps)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Create Habit", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
