package com.lifescore.app.presentation.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.repository.JournalRepository
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.JournalEntry
import com.lifescore.app.domain.model.JournalMood
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class JournalUiState(
    val entries: List<JournalEntry> = emptyList(),
    val isGeneratingAi: Boolean = false,
    val selectedMood: JournalMood = JournalMood.HAPPY,
    val selectedDimension: DimensionType = DimensionType.MENTAL_HEALTH,
    val currentText: String = "",
    val bannerMessage: String? = null
)

class JournalViewModel(
    private val repository: JournalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    init {
        loadEntries()
    }

    private fun loadEntries() {
        viewModelScope.launch {
            repository.getAllEntries().collect { list ->
                _uiState.update { it.copy(entries = list) }
            }
        }
    }

    fun setMood(mood: JournalMood) {
        _uiState.update { it.copy(selectedMood = mood) }
    }

    fun setDimension(dimension: DimensionType) {
        _uiState.update { it.copy(selectedDimension = dimension) }
    }

    fun setText(text: String) {
        _uiState.update { it.copy(currentText = text) }
    }

    fun saveEntry() {
        val text = _uiState.value.currentText.trim()
        if (text.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingAi = true) }
            val entry = repository.saveEntry(
                mood = _uiState.value.selectedMood,
                text = text,
                dimension = _uiState.value.selectedDimension
            )
            _uiState.update {
                it.copy(
                    isGeneratingAi = false,
                    currentText = "",
                    bannerMessage = "✨ Journal entry saved with AI Cognitive Reflection!"
                )
            }
        }
    }

    fun deleteEntry(entry: JournalEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }

    fun dismissBanner() {
        _uiState.update { it.copy(bannerMessage = null) }
    }
}
