package com.lifescore.app.presentation.ui.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.local.entity.AiMemoryEntity
import com.lifescore.app.data.repository.ViralGrowthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AiMemoryInspectorUiState(
    val memories: List<AiMemoryEntity> = emptyList(),
    val motivationalInterviewingDialogue: List<Pair<String, String>> = listOf(
        "AI Coach (Motivational Interviewing)" to "I noticed you're aiming to complete the 30-Day Fitness Quest. On a scale of 1-10, how important is reclaiming this energy level for your daily focus?",
        "You" to "Probably an 8. I feel tired by 3 PM and want sustained clarity.",
        "AI Coach (Reflective Listening)" to "That makes complete sense. Sustained afternoon focus is directly linked to metabolic momentum. What is one small friction point you encountered last time you tried building this workout habit?",
        "You" to "I tried doing 45-minute heavy workouts right away and burned out in week 2.",
        "AI Coach (Empathetic Reframe)" to "You identified the exact root cause: over-intensity upfront. How would it feel if we locked in a non-negotiable 10-minute micro-session instead for the first 14 days?"
    ),
    val toastMessage: String? = null
)

class AiMemoryInspectorViewModel(
    private val repository: ViralGrowthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiMemoryInspectorUiState())
    val uiState: StateFlow<AiMemoryInspectorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAiMemories().collect { list ->
                _uiState.update { it.copy(memories = list) }
            }
        }
    }

    fun recordNewMemory(patternType: String, insight: String, intervention: String) {
        viewModelScope.launch {
            repository.recordAiObservation(patternType, insight, intervention)
            _uiState.update { it.copy(toastMessage = "Recorded new AI Behavioral Pattern memory!") }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
