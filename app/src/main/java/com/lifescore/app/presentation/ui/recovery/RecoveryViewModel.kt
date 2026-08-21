package com.lifescore.app.presentation.ui.recovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.core.engine.CbtLesson
import com.lifescore.app.core.engine.RecoveryEngine
import com.lifescore.app.core.engine.RecoveryStats
import com.lifescore.app.data.local.entity.*
import com.lifescore.app.data.repository.RecoveryRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class RecoveryUiState(
    val selectedAddiction: AddictionType = AddictionType.SMOKING,
    val activeRecovery: RecoveryEntry? = null,
    val stats: RecoveryStats = RecoveryStats(0, 0, 0, 0, 0L, 0.0),
    val moneySaved: Double = 0.0,
    val timeSavedHours: Double = 0.0,
    val itemsAvoided: Int = 0,
    val survivedCravingsCount: Int = 0,
    val milestones: List<RecoveryMilestone> = emptyList(),
    val todayPledge: RecoveryPledge? = null,
    val savingsGoals: List<RecoverySavingsGoal> = emptyList(),
    val cbtLessons: List<CbtLesson> = emptyList(),
    val triggers: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val snackbarMessage: String? = null
)

class RecoveryViewModel(
    private val repository: RecoveryRepository,
    private val engine: RecoveryEngine = RecoveryEngine(),
    private val userId: String = "default_user"
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        RecoveryUiState(
            cbtLessons = engine.getCbtLessons(),
            triggers = engine.getCravingTriggers()
        )
    )
    val uiState: StateFlow<RecoveryUiState> = _uiState.asStateFlow()

    private var tickerJob: Job? = null

    init {
        loadAddictionData(_uiState.value.selectedAddiction)
        startSobrietyLiveTicker()
    }

    fun selectAddiction(addictionType: AddictionType) {
        if (_uiState.value.selectedAddiction == addictionType) return
        _uiState.update { it.copy(selectedAddiction = addictionType, isLoading = true) }
        loadAddictionData(addictionType)
    }

    private fun loadAddictionData(addictionType: AddictionType) {
        viewModelScope.launch {
            // 1. Seed milestones if first time
            repository.seedInitialMilestonesIfEmpty(userId, addictionType)

            // 2. Observe Recovery Entry
            repository.getActiveRecovery(userId, addictionType).collectLatest { recovery ->
                val entry = recovery ?: RecoveryEntry(
                    userId = userId,
                    addictionType = addictionType,
                    sobrietyStartDate = System.currentTimeMillis() - (12 * 3600 * 1000L), // default 12h headstart
                    dailyCost = addictionType.defaultDailyCost
                ).also {
                    repository.saveRecoveryEntry(it)
                }

                updateCalculatedStats(entry)
            }
        }

        // 3. Observe Milestones
        viewModelScope.launch {
            repository.getMilestones(userId, addictionType).collectLatest { milestonesList ->
                _uiState.update { it.copy(milestones = milestonesList) }
            }
        }

        // 4. Observe Today's Pledge
        val todayIso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        viewModelScope.launch {
            repository.getTodayPledge(userId, todayIso, addictionType).collectLatest { pledge ->
                _uiState.update { it.copy(todayPledge = pledge) }
            }
        }

        // 5. Observe Savings Goals
        viewModelScope.launch {
            repository.getSavingsGoals(userId).collectLatest { goals ->
                _uiState.update { it.copy(savingsGoals = goals) }
            }
        }

        // 6. Observe Survived Cravings Count
        viewModelScope.launch {
            repository.getSurvivedCravingsCount(userId).collectLatest { count ->
                _uiState.update { it.copy(survivedCravingsCount = count, isLoading = false) }
            }
        }
    }

    private fun startSobrietyLiveTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (true) {
                _uiState.value.activeRecovery?.let { entry ->
                    updateCalculatedStats(entry)
                }
                delay(1000L) // tick every second for real-time seconds counter
            }
        }
    }

    private fun updateCalculatedStats(entry: RecoveryEntry) {
        val stats = engine.calculateSobrietyStats(entry.sobrietyStartDate)
        val money = engine.calculateMoneySaved(stats.totalFractionalDays, entry.dailyCost)
        val timeSaved = engine.calculateTimeSavedHours(stats.totalFractionalDays, entry.dailyMinutesConsumed)
        val items = engine.calculateItemsAvoided(stats.totalFractionalDays, entry.dailyItemsConsumed)

        _uiState.update { state ->
            state.copy(
                activeRecovery = entry,
                stats = stats,
                moneySaved = money,
                timeSavedHours = timeSaved,
                itemsAvoided = items
            )
        }
    }

    fun signDailyPledge(pledgeText: String = "I commit to honor my mind, body, and loved ones by staying free today.") {
        viewModelScope.launch {
            val todayIso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val pledge = RecoveryPledge(
                userId = userId,
                dateIso = todayIso,
                addictionType = _uiState.value.selectedAddiction,
                pledgeText = pledgeText,
                isEveningReflected = false,
                isKept = true,
                timestamp = System.currentTimeMillis()
            )
            repository.savePledge(pledge)
            _uiState.update { it.copy(snackbarMessage = "📜 Freedom Pledge Signed! (+50 XP)") }
        }
    }

    fun saveEveningReflection(reflection: String) {
        val currentPledge = _uiState.value.todayPledge ?: return
        viewModelScope.launch {
            val updated = currentPledge.copy(
                isEveningReflected = true,
                eveningReflection = reflection
            )
            repository.savePledge(updated)
            _uiState.update { it.copy(snackbarMessage = "🌙 Nightly Reflection Saved! Stay proud.") }
        }
    }

    fun logCraving(craving: CravingLog) {
        viewModelScope.launch {
            repository.logCraving(craving.copy(userId = userId))
            val msg = if (craving.survived) "🏆 Victory! Urge surfed and logged (+25 XP)" else "Craving logged. Take it one breath at a time."
            _uiState.update { it.copy(snackbarMessage = msg) }
        }
    }

    fun logSlipOrRelapse(
        relapseType: RelapseType,
        trigger: String,
        lesson: String,
        plan: String
    ) {
        viewModelScope.launch {
            val addiction = _uiState.value.selectedAddiction
            if (relapseType == RelapseType.SLIP) {
                repository.recordSlip(userId, addiction, trigger, lesson, plan)
                _uiState.update { it.copy(snackbarMessage = "💛 Slip logged. Streak protected—progress is not lost.") }
            } else {
                repository.logRelapse(
                    RelapseLog(
                        userId = userId,
                        addictionType = addiction,
                        relapseType = RelapseType.RELAPSE,
                        trigger = trigger,
                        lessonsLearned = lesson,
                        actionPlan = plan,
                        streakBeforeSetback = _uiState.value.stats.totalDays,
                        timestamp = System.currentTimeMillis()
                    )
                )
                _uiState.update { it.copy(snackbarMessage = "🔄 Reset clock with fresh determination. Day 1 starts now!") }
            }
        }
    }

    fun resetSobrietyClock() {
        viewModelScope.launch {
            val addiction = _uiState.value.selectedAddiction
            val newEntry = repository.resetSobriety(userId, addiction)
            updateCalculatedStats(newEntry)
            _uiState.update { it.copy(snackbarMessage = "Clock reset to 0d 0h. You've got this!") }
        }
    }

    fun addSavingsGoal(title: String, targetAmount: Double, emoji: String) {
        viewModelScope.launch {
            repository.saveSavingsGoal(
                RecoverySavingsGoal(
                    userId = userId,
                    title = title,
                    targetAmount = targetAmount,
                    iconEmoji = emoji
                )
            )
            _uiState.update { it.copy(snackbarMessage = "🎯 Reward Goal '$title' Added!") }
        }
    }

    fun deleteSavingsGoal(goal: RecoverySavingsGoal) {
        viewModelScope.launch {
            repository.deleteSavingsGoal(goal)
        }
    }

    fun unlockMilestone(milestone: RecoveryMilestone) {
        viewModelScope.launch {
            repository.unlockMilestone(milestone)
            _uiState.update { it.copy(snackbarMessage = "🏅 Milestone Unlocked: ${milestone.title} (+100 XP)") }
        }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        tickerJob?.cancel()
    }
}
