package com.lifescore.app.presentation.ui.hydration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.local.entity.HydrationEntity
import com.lifescore.app.data.repository.DailyHydrationData
import com.lifescore.app.data.repository.HydrationRepository
import com.lifescore.app.data.repository.HydrationStats
import com.lifescore.app.data.repository.LifeScoreRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HydrationUiState(
    val isLoading: Boolean = false,
    val stats: HydrationStats? = null,
    val todayEntries: List<HydrationEntity> = emptyList(),
    val weeklyData: List<DailyHydrationData> = emptyList(),
    val currentGoalMl: Int = 2500,
    val successMessage: String? = null,
    val error: String? = null
)

class HydrationViewModel(
    private val hydrationRepository: HydrationRepository,
    private val lifeScoreRepository: LifeScoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HydrationUiState())
    val uiState: StateFlow<HydrationUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Observe today's entries
            launch {
                hydrationRepository.getTodayEntries().collect { entries ->
                    _uiState.update { it.copy(todayEntries = entries) }
                }
            }

            // Observe hydration stats
            launch {
                hydrationRepository.getHydrationStats().collect { stats ->
                    _uiState.update {
                        it.copy(
                            stats = stats,
                            weeklyData = stats.last7Days,
                            currentGoalMl = stats.dailyGoalMl,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun addWater(volumeMl: Int, source: String = "manual") {
        viewModelScope.launch {
            try {
                hydrationRepository.saveHydrationEntry(volumeMl = volumeMl, source = source)

                // Award XP and Health boost
                val xpEarned = when {
                    volumeMl >= 500 -> 15
                    volumeMl >= 250 -> 10
                    else -> 5
                }
                val currentProfile = lifeScoreRepository.getUserProfile().first()
                val updatedXp = currentProfile.currentXp + xpEarned
                val newLevel = (updatedXp / 1000) + 1
                lifeScoreRepository.updateUserProfile(currentProfile.copy(currentXp = updatedXp, currentLevel = newLevel))

                val stats = _uiState.value.stats
                val newTotal = (stats?.todayTotalMl ?: 0) + volumeMl
                val goal = stats?.dailyGoalMl ?: 2500

                val msg = if (newTotal >= goal && (stats?.todayTotalMl ?: 0) < goal) {
                    "🎉 Goal Met! +25 Bonus XP & Health Dimension Boost!"
                } else {
                    "+${volumeMl}ml logged! (+$xpEarned XP)"
                }

                _uiState.update { it.copy(successMessage = msg) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun deleteEntry(entry: HydrationEntity) {
        viewModelScope.launch {
            try {
                hydrationRepository.deleteHydrationEntry(entry)
                _uiState.update { it.copy(successMessage = "Entry removed") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun updateGoal(newGoalMl: Int, weightKg: Float? = null, activityLevel: String = "moderate") {
        viewModelScope.launch {
            try {
                hydrationRepository.saveHydrationGoal(
                    goalMl = newGoalMl,
                    weightKg = weightKg,
                    activityLevel = activityLevel
                )
                _uiState.update { it.copy(currentGoalMl = newGoalMl, successMessage = "Daily goal set to ${newGoalMl}ml! 🎯") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, error = null) }
    }
}
