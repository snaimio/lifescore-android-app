package com.lifescore.app.presentation.ui.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.local.entity.FlashcardEntity
import com.lifescore.app.data.local.entity.LearningPlanEntity
import com.lifescore.app.data.repository.BookLearningRepository
import com.lifescore.app.domain.model.DimensionType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class BookLearningUiState(
    val flashcards: List<FlashcardEntity> = emptyList(),
    val dueFlashcards: List<FlashcardEntity> = emptyList(),
    val activePlans: List<LearningPlanEntity> = emptyList(),
    val currentCardIndex: Int = 0,
    val isAnswerRevealed: Boolean = false,
    val toastMessage: String? = null,
    val selectedTab: Int = 0 // 0: Flashcards, 1: 30-Day Curriculum
)

class BookLearningViewModel(
    private val repository: BookLearningRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookLearningUiState())
    val uiState: StateFlow<BookLearningUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedDefaultFlashcardsIfEmpty()
        }

        viewModelScope.launch {
            combine(
                repository.getAllFlashcards(),
                repository.getDueFlashcards(),
                repository.getLearningPlans()
            ) { allCards, dueCards, plans ->
                Triple(allCards, dueCards, plans)
            }.collect { (allCards, dueCards, plans) ->
                _uiState.update { it.copy(
                    flashcards = allCards,
                    dueFlashcards = if (dueCards.isNotEmpty()) dueCards else allCards,
                    activePlans = plans
                ) }
            }
        }
    }

    fun setTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun revealAnswer() {
        _uiState.update { it.copy(isAnswerRevealed = true) }
    }

    fun rateFlashcard(rating: String) {
        val currentCards = _uiState.value.dueFlashcards
        if (currentCards.isEmpty()) return
        val currentCard = currentCards.getOrNull(_uiState.value.currentCardIndex) ?: return

        viewModelScope.launch {
            val xp = repository.reviewFlashcard(currentCard.id, rating)
            val nextIndex = if (_uiState.value.currentCardIndex + 1 < currentCards.size) {
                _uiState.value.currentCardIndex + 1
            } else 0

            _uiState.update {
                it.copy(
                    currentCardIndex = nextIndex,
                    isAnswerRevealed = false,
                    toastMessage = "Card reviewed! +$xp XP awarded"
                )
            }
        }
    }

    fun generateLearningPlan(dimension: DimensionType) {
        viewModelScope.launch {
            repository.generate30DayPlan(dimension)
            _uiState.update { it.copy(toastMessage = "30-Day ${dimension.name} Plan Generated! +50 XP") }
        }
    }

    fun advancePlan(planId: String) {
        viewModelScope.launch {
            repository.advancePlanDay(planId)
            _uiState.update { it.copy(toastMessage = "Day completed! +25 XP") }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
