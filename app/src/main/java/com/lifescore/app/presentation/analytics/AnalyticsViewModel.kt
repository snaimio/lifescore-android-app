package com.lifescore.app.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.repository.AnalyticsRepository
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.domain.model.DimensionCorrelation
import com.lifescore.app.domain.model.HeatmapDay
import com.lifescore.app.domain.model.LifeScoreForecast
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AnalyticsUiState(
    val currentLifeScore: Int = 780,
    val heatmapDays: List<HeatmapDay> = emptyList(),
    val correlations: List<DimensionCorrelation> = emptyList(),
    val forecast: LifeScoreForecast? = null
)

class AnalyticsViewModel(
    private val analyticsRepository: AnalyticsRepository,
    private val lifeScoreRepository: LifeScoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val heatmap = analyticsRepository.getHeatmapData()
        val correlations = analyticsRepository.getDimensionCorrelations()
        val forecast = analyticsRepository.getLifeScoreForecast(780)

        _uiState.update {
            it.copy(
                heatmapDays = heatmap,
                correlations = correlations,
                forecast = forecast
            )
        }

        viewModelScope.launch {
            lifeScoreRepository.getUserProfile().collect { profile ->
                val score = if (profile.currentXp > 0) profile.currentXp.coerceIn(50, 950) else 780
                _uiState.update {
                    it.copy(
                        currentLifeScore = score,
                        forecast = analyticsRepository.getLifeScoreForecast(score)
                    )
                }
            }
        }
    }
}
