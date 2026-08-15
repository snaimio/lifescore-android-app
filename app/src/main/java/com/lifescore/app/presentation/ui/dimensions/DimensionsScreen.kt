package com.lifescore.app.presentation.ui.dimensions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.LifeTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DimensionsUiState(
    val selectedDimension: DimensionType = DimensionType.HEALTH,
    val tasksForSelectedDimension: List<LifeTask> = emptyList(),
    val dimensionScores: Map<DimensionType, Int> = emptyMap()
)

class DimensionsViewModel(
    private val repository: LifeScoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DimensionsUiState())
    val uiState: StateFlow<DimensionsUiState> = _uiState.asStateFlow()

    init {
        loadTasksForDimension(DimensionType.HEALTH)
    }

    fun selectDimension(dimension: DimensionType) {
        _uiState.value = _uiState.value.copy(selectedDimension = dimension)
        loadTasksForDimension(dimension)
    }

    private fun loadTasksForDimension(dimension: DimensionType) {
        viewModelScope.launch {
            repository.getTasksByDimension(dimension).collect { tasks ->
                _uiState.value = _uiState.value.copy(tasksForSelectedDimension = tasks)
            }
        }
    }

    fun toggleTask(task: LifeTask) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
        }
    }

    fun addTask(title: String, points: Int) {
        viewModelScope.launch {
            repository.addTask(title, _uiState.value.selectedDimension, points)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DimensionsScreen(
    navController: NavController,
    viewModel: DimensionsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Life Dimensions", fontWeight = FontWeight.Bold) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskDialog = true },
                containerColor = Color(uiState.selectedDimension.baseColorHex)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Scrollable Dimension Tabs
            ScrollableTabRow(
                selectedTabIndex = DimensionType.values().indexOf(uiState.selectedDimension),
                edgePadding = 16.dp
            ) {
                DimensionType.values().forEach { dim ->
                    Tab(
                        selected = uiState.selectedDimension == dim,
                        onClick = { viewModel.selectDimension(dim) },
                        text = {
                            Text(
                                dim.displayName,
                                fontWeight = if (uiState.selectedDimension == dim) FontWeight.Bold else FontWeight.Normal,
                                color = if (uiState.selectedDimension == dim) Color(dim.baseColorHex) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // Dimension Header Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(uiState.selectedDimension.baseColorHex).copy(alpha = 0.15f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = uiState.selectedDimension.displayName,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp,
                                color = Color(uiState.selectedDimension.baseColorHex)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = uiState.selectedDimension.description,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Active Habits & Quests",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                if (uiState.tasksForSelectedDimension.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No habits yet. Tap + to add one!", color = MaterialTheme.colorScheme.outline)
                        }
                    }
                } else {
                    items(uiState.tasksForSelectedDimension) { task ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = task.isCompleted,
                                    onCheckedChange = { viewModel.toggleTask(task) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(uiState.selectedDimension.baseColorHex)
                                    )
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = task.title,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f),
                                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "+${task.pointsReward} XP",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(uiState.selectedDimension.baseColorHex)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showAddTaskDialog) {
            AlertDialog(
                onDismissRequest = { showAddTaskDialog = false },
                title = { Text("Add ${uiState.selectedDimension.displayName} Habit") },
                text = {
                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        label = { Text("Habit name (e.g. 10m Meditation)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newTaskTitle.isNotBlank()) {
                                viewModel.addTask(newTaskTitle, 15)
                                newTaskTitle = ""
                                showAddTaskDialog = false
                            }
                        }
                    ) {
                        Text("Add Habit")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddTaskDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
