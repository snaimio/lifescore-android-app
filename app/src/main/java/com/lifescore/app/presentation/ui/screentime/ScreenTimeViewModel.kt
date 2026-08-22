package com.lifescore.app.presentation.ui.screentime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.local.entity.*
import com.lifescore.app.data.repository.AppUsageItemModel
import com.lifescore.app.data.repository.ScreenTimeRepository
import com.lifescore.app.data.repository.ScreenTimeUsageSummary
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ScreenTimeUiState(
    val isLoading: Boolean = false,
    val todayMinutes: Int = 85,
    val dailyLimitMinutes: Int = 120,
    val earnedBonusMinutes: Int = 15,
    val effectiveLimitMinutes: Int = 135,
    val progress: Float = 85f / 135f,
    val pickups: Int = 34,
    val isFocusModeEnabled: Boolean = false,
    val intentionalDelaySeconds: Int = 10,
    val topApps: List<AppUsageItemModel> = emptyList(),
    val activeChallenges: List<ScreenTimeChallenge> = emptyList(),
    val thoughtLogs: List<ThoughtBreakLog> = emptyList(),
    val showFrictionDialog: Boolean = false,
    val targetAppOpening: String = "Instagram",
    val frictionSecondsRemaining: Int = 10,
    val movementBonusAwarded: Int = 0,
    val selectedTab: Int = 0, // 0 = Dashboard, 1 = SweatPass Movement, 2 = Minimalist Mode, 3 = Thought Break
    val userMessage: String? = null
)

class ScreenTimeViewModel(
    private val repository: ScreenTimeRepository,
    private val userId: String = "hero_user"
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScreenTimeUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                repository.getTodayUsage(userId),
                repository.getGoal(userId),
                repository.getActiveChallenges(userId),
                repository.getThoughtBreakLogs(userId)
            ) { usage, goal, challenges, logs ->
                val effLimit = (goal.dailyLimitMinutes + goal.earnedBonusMinutes).coerceAtLeast(30)
                val prog = (usage.totalMinutes.toFloat() / effLimit.toFloat()).coerceIn(0f, 1.5f)
                
                ScreenTimeUiState(
                    isLoading = false,
                    todayMinutes = usage.totalMinutes,
                    dailyLimitMinutes = goal.dailyLimitMinutes,
                    earnedBonusMinutes = goal.earnedBonusMinutes,
                    effectiveLimitMinutes = effLimit,
                    progress = prog,
                    pickups = usage.pickups,
                    isFocusModeEnabled = goal.isFocusModeEnabled,
                    intentionalDelaySeconds = goal.intentionalDelaySeconds,
                    topApps = usage.topApps,
                    activeChallenges = challenges,
                    thoughtLogs = logs
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun setDailyGoal(minutes: Int) {
        viewModelScope.launch {
            repository.updateDailyLimit(userId, minutes)
            _uiState.update { it.copy(userMessage = "Daily limit updated to $minutes mins") }
        }
    }

    fun toggleFocusMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleFocusMode(userId, enabled)
            _uiState.update {
                it.copy(
                    isFocusModeEnabled = enabled,
                    userMessage = if (enabled) "🔒 Deep Focus Active — Distracting apps locked" else "Focus mode turned off"
                )
            }
        }
    }

    fun completeMovementExercise(exerciseName: String, reps: Int) {
        viewModelScope.launch {
            val bonusMins = repository.grantMovementExercise(userId, exerciseName, reps)
            _uiState.update {
                it.copy(
                    movementBonusAwarded = bonusMins,
                    userMessage = "💪 Awesome! +$bonusMins mins screen time unlocked (+${bonusMins * 15} XP)"
                )
            }
        }
    }

    fun triggerIntentionalAppOpening(appName: String) {
        _uiState.update {
            it.copy(
                showFrictionDialog = true,
                targetAppOpening = appName,
                frictionSecondsRemaining = it.intentionalDelaySeconds
            )
        }
    }

    fun dismissFrictionDialog(proceed: Boolean) {
        _uiState.update {
            it.copy(
                showFrictionDialog = false,
                userMessage = if (proceed) "Opening ${it.targetAppOpening} mindfully" else "🎉 Saved 15+ mins of mindless scrolling! (+20 XP)"
            )
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun advanceChallenge(challenge: ScreenTimeChallenge) {
        viewModelScope.launch {
            repository.advanceChallengeProgress(challenge)
            _uiState.update { it.copy(userMessage = "Day ${challenge.currentDay + 1} completed! Keep going!") }
        }
    }

    fun submitThoughtBreak(
        automaticThought: String,
        distortion: String,
        evidence: String,
        reframed: String,
        relief: Int
    ) {
        viewModelScope.launch {
            repository.saveThoughtBreak(userId, automaticThought, distortion, evidence, reframed, relief)
            _uiState.update { it.copy(userMessage = "🧠 Thought reframed! +40 XP Mental Health boost") }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
