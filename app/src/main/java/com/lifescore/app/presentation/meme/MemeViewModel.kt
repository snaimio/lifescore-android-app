package com.lifescore.app.presentation.meme

import androidx.lifecycle.ViewModel
import com.lifescore.app.core.util.MemeGeneratorEngine
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.GeneratedMeme
import com.lifescore.app.domain.model.MemeTemplate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MemeStudioUiState(
    val templates: List<MemeTemplate> = MemeGeneratorEngine.getAllTemplates(),
    val selectedTemplate: MemeTemplate = MemeGeneratorEngine.getAllTemplates().first(),
    val topCaption: String = MemeGeneratorEngine.getAllTemplates().first().topTextDefault,
    val bottomCaption: String = MemeGeneratorEngine.getAllTemplates().first().bottomTextDefault,
    val currentMeme: GeneratedMeme = MemeGeneratorEngine.generateContextualMeme(),
    val userStreak: Int = 14,
    val userScore: Int = 780,
    val coinsEarnedToast: String? = null
)

class MemeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MemeStudioUiState())
    val uiState: StateFlow<MemeStudioUiState> = _uiState.asStateFlow()

    fun selectTemplate(template: MemeTemplate) {
        _uiState.value = _uiState.value.copy(
            selectedTemplate = template,
            topCaption = template.topTextDefault,
            bottomCaption = template.bottomTextDefault,
            currentMeme = _uiState.value.currentMeme.copy(
                templateId = template.id,
                topText = template.topTextDefault,
                bottomText = template.bottomTextDefault,
                emojiArt = template.emojiArt
            )
        )
    }

    fun updateTopCaption(text: String) {
        _uiState.value = _uiState.value.copy(
            topCaption = text,
            currentMeme = _uiState.value.currentMeme.copy(topText = text)
        )
    }

    fun updateBottomCaption(text: String) {
        _uiState.value = _uiState.value.copy(
            bottomCaption = text,
            currentMeme = _uiState.value.currentMeme.copy(bottomText = text)
        )
    }

    fun remixWithAi() {
        val template = _uiState.value.selectedTemplate
        val (remixedTop, remixedBottom) = MemeGeneratorEngine.remixCaptions(
            templateId = template.id,
            strongest = DimensionType.CAREER,
            weakest = DimensionType.HEALTH
        )
        _uiState.value = _uiState.value.copy(
            topCaption = remixedTop,
            bottomCaption = remixedBottom,
            currentMeme = _uiState.value.currentMeme.copy(
                topText = remixedTop,
                bottomText = remixedBottom
            )
        )
    }

    fun saveMemeToGallery() {
        _uiState.value = _uiState.value.copy(
            coinsEarnedToast = "Saved to Gallery! +50 LifeScore Coins Earned 🪙"
        )
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(coinsEarnedToast = null)
    }
}
