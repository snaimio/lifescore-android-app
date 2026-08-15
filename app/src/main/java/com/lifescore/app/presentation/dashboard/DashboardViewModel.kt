package com.lifescore.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.core.util.LevelCalculator
import com.lifescore.app.core.util.ScoreEngine
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.LifeTask
import com.lifescore.app.domain.model.UserProfile
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardUiState(
    val userProfile: UserProfile = UserProfile(),
    val overallScore: Int = 750,
    val levelProgress: Float = 0.5f,
    val dimensionScores: Map<DimensionType, Int> = DimensionType.values().associateWith { 75 },
    val todayTasks: List<LifeTask> = emptyList(),
    val lowestDimension: DimensionType = DimensionType.HEALTH,
    val isLoading: Boolean = false
)

class DashboardViewModel(
    private val repository: LifeScoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
            
            combine(
                repository.getAllTasks(),
                repository.getUserProfile()
            ) { tasks, user ->
                // Calculate dimension scores based on task completion
                val scores = DimensionType.values().associateWith { dim ->
                    val dimTasks = tasks.filter { it.dimension == dim }
                    val completed = dimTasks.count { it.isCompleted }
                    ScoreEngine.calculateDimensionScore(completed, dimTasks.size)
                }

                val overall = ScoreEngine.calculateOverallLifeScore(scores)
                val level = LevelCalculator.calculateLevel(user.currentXp)
                val progress = LevelCalculator.calculateLevelProgress(user.currentXp)
                val lowestDim = scores.minByOrNull { it.value }?.key ?: DimensionType.HEALTH

                DashboardUiState(
                    userProfile = user.copy(
                        currentLevel = level,
                        title = LevelCalculator.getTitleForLevel(level)
                    ),
                    overallScore = overall,
                    levelProgress = progress,
                    dimensionScores = scores,
                    todayTasks = tasks,
                    lowestDimension = lowestDim,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun onToggleTask(task: LifeTask) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
        }
    }
}
