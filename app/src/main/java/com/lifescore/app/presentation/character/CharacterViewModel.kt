package com.lifescore.app.presentation.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.repository.CharacterStatsRepository
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.domain.model.CharacterStats
import com.lifescore.app.domain.model.CharacterTitle
import com.lifescore.app.domain.model.TitleCatalog
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CharacterUiState(
    val stats: CharacterStats = CharacterStats(),
    val titles: List<CharacterTitle> = TitleCatalog.allTitles,
    val level: Int = 1,
    val currentXp: Int = 0,
    val xpToNextLevel: Int = 1000,
    val bannerMessage: String? = null
)

class CharacterViewModel(
    private val characterRepository: CharacterStatsRepository,
    private val lifeScoreRepository: LifeScoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterUiState())
    val uiState: StateFlow<CharacterUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            characterRepository.getCharacterStatsFlow().collect { stats ->
                _uiState.update { it.copy(stats = stats) }
            }
        }

        viewModelScope.launch {
            lifeScoreRepository.getUserProfile().collect { profile ->
                _uiState.update {
                    it.copy(
                        level = profile.currentLevel,
                        currentXp = profile.currentXp
                    )
                }
            }
        }
    }

    fun allocateStrength() {
        viewModelScope.launch {
            if (characterRepository.addStrength(1)) {
                _uiState.update { it.copy(bannerMessage = "Strength increased! (+STR)") }
            }
        }
    }

    fun allocateVitality() {
        viewModelScope.launch {
            if (characterRepository.addVitality(1)) {
                _uiState.update { it.copy(bannerMessage = "Vitality increased! (+VIT)") }
            }
        }
    }

    fun allocateAgility() {
        viewModelScope.launch {
            if (characterRepository.addAgility(1)) {
                _uiState.update { it.copy(bannerMessage = "Agility increased! (+AGI)") }
            }
        }
    }

    fun allocateIntelligence() {
        viewModelScope.launch {
            if (characterRepository.addIntelligence(1)) {
                _uiState.update { it.copy(bannerMessage = "Intelligence increased! (+INT)") }
            }
        }
    }

    fun allocatePerception() {
        viewModelScope.launch {
            if (characterRepository.addPerception(1)) {
                _uiState.update { it.copy(bannerMessage = "Perception increased! (+PER)") }
            }
        }
    }

    fun equipTitle(title: CharacterTitle) {
        viewModelScope.launch {
            characterRepository.equipTitle(title.name, title.statBonus)
            _uiState.update { state ->
                state.copy(
                    titles = state.titles.map { it.copy(isEquipped = it.id == title.id) },
                    bannerMessage = "Equipped Title: ${title.name} (${title.statBonus})"
                )
            }
        }
    }

    fun dismissBanner() {
        _uiState.update { it.copy(bannerMessage = null) }
    }
}
