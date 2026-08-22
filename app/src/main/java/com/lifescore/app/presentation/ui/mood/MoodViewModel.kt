package com.lifescore.app.presentation.ui.mood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.local.entity.MoodType
import com.lifescore.app.data.repository.MoodAnalytics
import com.lifescore.app.data.repository.MoodRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class MoodTrackerUiState(
    val selectedMood: MoodType = MoodType.HAPPY,
    val energyLevel: Float = 7f,
    val stressLevel: Float = 3f,
    val selectedFactors: Set<String> = emptySet(),
    val note: String = "",
    val analytics: MoodAnalytics = MoodAnalytics(),
    val isLoggedToday: Boolean = false,
    val snackbarMessage: String? = null
)

class MoodViewModel(
    private val repository: MoodRepository,
    private val userId: String = "default_user"
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoodTrackerUiState())
    val uiState: StateFlow<MoodTrackerUiState> = _uiState.asStateFlow()

    val availableFactors = listOf(
        "Sleep 💤", "Exercise 💪", "Nutrition 🥗", "Deep Work 💼",
        "Social/Friends 👥", "Family 🏡", "Screen Time 📱", "Nature 🌲",
        "Meditation 🧘", "Reading 📚", "Stress ⚡", "Weather ☀️"
    )

    init {
        loadAnalytics()
    }

    private fun loadAnalytics() {
        viewModelScope.launch {
            repository.getMoodAnalytics(userId).collectLatest { analytics ->
                val todayIso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val loggedToday = analytics.logs.any { it.dateIso == todayIso }
                _uiState.update {
                    it.copy(
                        analytics = analytics,
                        isLoggedToday = loggedToday
                    )
                }
            }
        }
    }

    fun selectMood(mood: MoodType) {
        _uiState.update { it.copy(selectedMood = mood) }
    }

    fun setEnergyLevel(level: Float) {
        _uiState.update { it.copy(energyLevel = level) }
    }

    fun setStressLevel(level: Float) {
        _uiState.update { it.copy(stressLevel = level) }
    }

    fun toggleFactor(factor: String) {
        _uiState.update { state ->
            val set = state.selectedFactors.toMutableSet()
            if (set.contains(factor)) set.remove(factor) else set.add(factor)
            state.copy(selectedFactors = set)
        }
    }

    fun updateNote(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun logMood() {
        viewModelScope.launch {
            val state = _uiState.value
            val todayIso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            repository.logMood(
                mood = state.selectedMood,
                energyLevel = state.energyLevel.toInt(),
                stressLevel = state.stressLevel.toInt(),
                factors = state.selectedFactors.toList(),
                note = state.note,
                dateIso = todayIso,
                userId = userId
            )
            _uiState.update {
                it.copy(
                    isLoggedToday = true,
                    note = "",
                    snackbarMessage = "🎭 Mood Check-in Saved! (+20 XP)"
                )
            }
        }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
