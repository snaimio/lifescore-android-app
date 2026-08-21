package com.lifescore.app.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.core.util.LevelCalculator
import com.lifescore.app.core.util.ScoreEngine
import com.lifescore.app.data.remote.repository.AuthRepository
import com.lifescore.app.data.remote.repository.FirebaseRepository
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.LifeTask
import com.lifescore.app.domain.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val isSyncing: Boolean = false,
    val userName: String = "Achiever",
    val totalScore: Int = 500,
    val level: Int = 1,
    val levelProgress: Float = 0.0f,
    val currentXp: Int = 0,
    val streak: Int = 0,
    val userTitle: String = "Novice Seeker",
    val dailyProgress: Float = 0.0f,
    val tasksCompleted: Int = 0,
    val totalTasks: Int = 0,
    val dimensions: List<DimensionType> = DimensionType.values().toList(),
    val dimensionScores: Map<DimensionType, Int> = DimensionType.values().associateWith { 50 },
    val todayTasks: List<LifeTask> = emptyList(),
    val userPhase: com.lifescore.app.core.engine.UserPhase = com.lifescore.app.core.engine.UserPhase.NEW_USER,
    val unlockedFeatures: List<String> = emptyList(),
    val milestoneMessage: String? = null,
    val cloudSyncStatus: String = "Synced with Firestore"
)

class HomeViewModel(
    private val repository: LifeScoreRepository,
    private val firebaseRepository: FirebaseRepository? = null,
    private val authRepository: AuthRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.seedInitialDataIfEmpty()

            // 1. Fetch remote user profile from Firestore if authenticated
            authRepository?.currentUser?.uid?.let { uid ->
                try {
                    val remoteProfile = firebaseRepository?.getUser(uid)
                    if (remoteProfile != null) {
                        repository.updateUserProfile(remoteProfile)
                    }
                } catch (_: Exception) {
                    // Fallback to local cache
                }
            }

            // 2. Combine Room Flow for reactive 0ms updates
            combine(
                repository.getAllTasks(),
                repository.getUserProfile()
            ) { tasks, user ->
                val scores = DimensionType.values().associateWith { dim ->
                    val dimTasks = tasks.filter { it.dimension == dim }
                    val completed = dimTasks.count { it.isCompleted }
                    ScoreEngine.calculateDimensionScore(completed, dimTasks.size)
                }

                val overall = ScoreEngine.calculateOverallLifeScore(scores)
                val level = LevelCalculator.calculateLevel(user.currentXp)
                val progress = LevelCalculator.calculateLevelProgress(user.currentXp)
                val completedCount = tasks.count { it.isCompleted }
                val taskProgress = if (tasks.isNotEmpty()) completedCount.toFloat() / tasks.size else 0f

                val userProg = com.lifescore.app.core.engine.UserProgress(
                    daysActive = user.currentStreakDays,
                    completedQuests = completedCount,
                    level = level,
                    lifeScore = overall,
                    isPremium = false
                )
                val phase = com.lifescore.app.core.engine.UserProgressTracker.determinePhase(userProg)
                val questLimit = com.lifescore.app.core.engine.UserProgressTracker.getDailyQuestLimit(phase)
                val visibleTasks = tasks.take(questLimit)

                HomeUiState(
                    isLoading = false,
                    isSyncing = false,
                    userName = user.name.ifBlank { "Achiever" },
                    totalScore = overall,
                    level = level,
                    levelProgress = progress,
                    currentXp = user.currentXp,
                    streak = user.currentStreakDays,
                    userTitle = LevelCalculator.getTitleForLevel(level),
                    dailyProgress = taskProgress,
                    tasksCompleted = completedCount,
                    totalTasks = visibleTasks.size,
                    dimensions = DimensionType.values().toList(),
                    dimensionScores = scores,
                    todayTasks = visibleTasks,
                    userPhase = phase,
                    unlockedFeatures = com.lifescore.app.core.engine.UserProgressTracker.getUnlockedFeatures(phase),
                    milestoneMessage = com.lifescore.app.core.engine.FeatureUnlockNotification.getUnlockMessage(phase),
                    cloudSyncStatus = "Live Cloud Sync Active"
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    /**
     * Toggles task completion locally (0ms UI latency) and triggers auto-sync to Cloud Firestore.
     */
    fun onToggleTask(task: LifeTask) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true, cloudSyncStatus = "Syncing to Firestore...")

            // 1. Local update
            repository.toggleTaskCompletion(task)

            // 2. Background Cloud Firestore Auto-Sync
            val uid = authRepository?.currentUser?.uid
            if (uid != null && firebaseRepository != null) {
                try {
                    val updatedTask = task.copy(isCompleted = !task.isCompleted)
                    firebaseRepository.saveTask(uid, updatedTask)
                    
                    // Update user score in Firestore
                    val currentXp = _uiState.value.currentXp + (if (!task.isCompleted) task.pointsReward else -task.pointsReward)
                    firebaseRepository.updateUserScore(
                        uid = uid,
                        score = _uiState.value.totalScore,
                        xp = currentXp.coerceAtLeast(0),
                        level = _uiState.value.level
                    )
                    _uiState.value = _uiState.value.copy(isSyncing = false, cloudSyncStatus = "Synced with Firestore")
                } catch (_: Exception) {
                    _uiState.value = _uiState.value.copy(isSyncing = false, cloudSyncStatus = "Saved locally (Offline)")
                }
            } else {
                _uiState.value = _uiState.value.copy(isSyncing = false, cloudSyncStatus = "Saved locally")
            }
        }
    }

    fun triggerManualSync() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isSyncing = true, cloudSyncStatus = "Syncing...")
            val uid = authRepository?.currentUser?.uid
            if (uid != null && firebaseRepository != null) {
                try {
                    _uiState.value.todayTasks.forEach { task ->
                        firebaseRepository.saveTask(uid, task)
                    }
                    firebaseRepository.updateUserScore(
                        uid = uid,
                        score = _uiState.value.totalScore,
                        xp = _uiState.value.currentXp,
                        level = _uiState.value.level
                    )
                    _uiState.value = _uiState.value.copy(isSyncing = false, cloudSyncStatus = "Cloud Sync Up to Date")
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(isSyncing = false, cloudSyncStatus = "Sync queued (Offline)")
                }
            } else {
                _uiState.value = _uiState.value.copy(isSyncing = false, cloudSyncStatus = "Guest Mode (Local)")
            }
        }
    }
}
