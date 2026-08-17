package com.lifescore.app.presentation.quest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.repository.AiQuestRepository
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.domain.model.AiQuest
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.QuestDifficulty
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AiQuestUiState(
    val quests: List<AiQuest> = emptyList(),
    val selectedDifficultyFilter: QuestDifficulty? = null,
    val isGenerating: Boolean = false,
    val userScore: Int = 0,
    val userLevel: Int = 1,
    val weakestDimension: DimensionType = DimensionType.HEALTH,
    val bannerMessage: String? = null
)

class AiQuestViewModel(
    private val questRepository: AiQuestRepository,
    private val lifeScoreRepository: LifeScoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiQuestUiState())
    val uiState: StateFlow<AiQuestUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Observe quests from Room
            questRepository.getAllQuests().collect { quests ->
                _uiState.update { it.copy(quests = quests) }
            }
        }

        viewModelScope.launch {
            lifeScoreRepository.getUserProfile().collect { profile ->
                _uiState.update {
                    it.copy(
                        userScore = profile.currentXp,
                        userLevel = profile.currentLevel
                    )
                }
            }
        }
    }

    fun generateNewQuests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, bannerMessage = "AI Matrix synthesizing customized quests...") }
            try {
                questRepository.generateAndSaveQuests(
                    weakestDimension = _uiState.value.weakestDimension,
                    weakestScore = 35,
                    totalScore = _uiState.value.userScore,
                    userLevel = _uiState.value.userLevel,
                    userStreak = 5
                )
                _uiState.update { it.copy(isGenerating = false, bannerMessage = "3 New Quest Protocols synthesized!") }
            } catch (e: Exception) {
                _uiState.update { it.copy(isGenerating = false, bannerMessage = "Synthesis complete.") }
            }
        }
    }

    fun acceptQuest(quest: AiQuest) {
        viewModelScope.launch {
            questRepository.acceptQuest(quest)
            _uiState.update { it.copy(bannerMessage = "Accepted '${quest.title}' into daily quest log!") }
        }
    }

    fun filterByDifficulty(difficulty: QuestDifficulty?) {
        _uiState.update { it.copy(selectedDifficultyFilter = difficulty) }
    }

    fun dismissBanner() {
        _uiState.update { it.copy(bannerMessage = null) }
    }
}
