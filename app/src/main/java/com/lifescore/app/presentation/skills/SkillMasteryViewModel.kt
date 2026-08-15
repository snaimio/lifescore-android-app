package com.lifescore.app.presentation.skills

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.core.util.SkillMasteryManager
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.SkillLogSession
import com.lifescore.app.domain.model.SkillMastery
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SkillMasteryUiState(
    val skills: List<SkillMastery> = SkillMasteryManager.getDefaultSkills(),
    val selectedSkill: SkillMastery? = null,
    val selectedDimensionFilter: DimensionType? = null,
    val isStopwatchRunning: Boolean = false,
    val stopwatchSeconds: Long = 0,
    val activeStopwatchSkillId: String? = null,
    val isLogDialogOpen: Boolean = false,
    val isAddSkillDialogOpen: Boolean = false,
    val recentSuccessMessage: String? = null
) {
    val totalPracticeHours: Float get() = SkillMasteryManager.calculateTotalPracticeHours(skills)
    val global10kProgress: Float get() = SkillMasteryManager.calculate10kGlobalProgress(skills)
    val globalMasteryTier get() = SkillMasteryManager.getGlobalMasteryTier(totalPracticeHours)
    val filteredSkills: List<SkillMastery>
        get() = if (selectedDimensionFilter == null) skills else skills.filter { it.dimension == selectedDimensionFilter }
}

class SkillMasteryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SkillMasteryUiState())
    val uiState: StateFlow<SkillMasteryUiState> = _uiState.asStateFlow()

    private var stopwatchJob: Job? = null

    fun selectDimensionFilter(dim: DimensionType?) {
        _uiState.value = _uiState.value.copy(selectedDimensionFilter = dim)
    }

    fun openLogDialog(skill: SkillMastery) {
        _uiState.value = _uiState.value.copy(selectedSkill = skill, isLogDialogOpen = true)
    }

    fun closeLogDialog() {
        _uiState.value = _uiState.value.copy(isLogDialogOpen = false, selectedSkill = null)
    }

    fun openAddSkillDialog() {
        _uiState.value = _uiState.value.copy(isAddSkillDialogOpen = true)
    }

    fun closeAddSkillDialog() {
        _uiState.value = _uiState.value.copy(isAddSkillDialogOpen = false)
    }

    fun logMinutes(skillId: String, minutes: Int, notes: String = "") {
        val target = _uiState.value.skills.find { it.id == skillId } ?: return
        val (updatedSkill, session) = SkillMasteryManager.logPracticeSession(target, minutes, notes)

        val updatedList = _uiState.value.skills.map { if (it.id == skillId) updatedSkill else it }
        _uiState.value = _uiState.value.copy(
            skills = updatedList,
            isLogDialogOpen = false,
            selectedSkill = null,
            recentSuccessMessage = "Logged ${minutes}m for ${updatedSkill.title}! +${session.xpGranted} XP (${updatedSkill.dimension.displayName})"
        )
    }

    fun addNewSkill(title: String, emoji: String, dimension: DimensionType, targetHours: Int) {
        if (title.isBlank()) return
        val newSkill = SkillMastery(
            title = title,
            emoji = emoji.ifBlank { "⚡" },
            dimension = dimension,
            targetHours = targetHours
        )
        _uiState.value = _uiState.value.copy(
            skills = listOf(newSkill) + _uiState.value.skills,
            isAddSkillDialogOpen = false,
            recentSuccessMessage = "Added new skill: $title ($targetHours hrs target)!"
        )
    }

    fun startStopwatch(skillId: String) {
        if (_uiState.value.isStopwatchRunning && _uiState.value.activeStopwatchSkillId == skillId) return
        stopwatchJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isStopwatchRunning = true,
            activeStopwatchSkillId = skillId,
            stopwatchSeconds = 0
        )

        stopwatchJob = viewModelScope.launch {
            while (_uiState.value.isStopwatchRunning) {
                delay(1000)
                _uiState.value = _uiState.value.copy(stopwatchSeconds = _uiState.value.stopwatchSeconds + 1)
            }
        }
    }

    fun stopAndSaveStopwatch() {
        val skillId = _uiState.value.activeStopwatchSkillId ?: return
        val seconds = _uiState.value.stopwatchSeconds
        stopwatchJob?.cancel()

        val minutes = (seconds / 60).toInt().coerceAtLeast(1)
        logMinutes(skillId, minutes, "Live deliberate practice timer session")

        _uiState.value = _uiState.value.copy(
            isStopwatchRunning = false,
            activeStopwatchSkillId = null,
            stopwatchSeconds = 0
        )
    }

    fun cancelStopwatch() {
        stopwatchJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isStopwatchRunning = false,
            activeStopwatchSkillId = null,
            stopwatchSeconds = 0
        )
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(recentSuccessMessage = null)
    }
}
