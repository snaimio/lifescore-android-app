package com.lifescore.app.presentation.ui.leagues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.local.entity.LeagueTierEntity
import com.lifescore.app.data.repository.ViralGrowthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LeagueCompetitor(
    val rank: Int,
    val name: String,
    val avatarEmoji: String,
    val weeklyXp: Int,
    val isCurrentUser: Boolean = false,
    val isPromoted: Boolean = false,
    val isRelegated: Boolean = false
)

data class LeagueTiersUiState(
    val leagueTier: LeagueTierEntity? = null,
    val competitors: List<LeagueCompetitor> = emptyList(),
    val toastMessage: String? = null
)

class LeagueTiersViewModel(
    private val repository: ViralGrowthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeagueTiersUiState())
    val uiState: StateFlow<LeagueTiersUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getLeagueTier().collect { tier ->
                val list = generateCompetitors(tier.currentWeeklyXp, tier.userRankInLeague)
                _uiState.update { it.copy(leagueTier = tier, competitors = list) }
            }
        }
    }

    private fun generateCompetitors(userXp: Int, userRank: Int): List<LeagueCompetitor> {
        val names = listOf(
            "Alex Vance", "Elena Rostova", "Kavita Sharma", "You", "Marcus Flint",
            "Sophia Lin", "Liam Walker", "Chloe Dubois", "Noah Bennett", "Amara Okafor"
        )
        val avatars = listOf("🧙‍♂️", "🏹", "🧘‍♀️", "⚔️", "🛡️", "🔬", "🚀", "🎨", "🧗", "🏃")

        return names.mapIndexed { idx, name ->
            val rank = idx + 1
            val xp = if (name == "You") userXp else (1800 - idx * 75)
            LeagueCompetitor(
                rank = rank,
                name = name,
                avatarEmoji = avatars[idx % avatars.size],
                weeklyXp = xp,
                isCurrentUser = name == "You",
                isPromoted = rank <= 3,
                isRelegated = rank >= 9
            )
        }.sortedBy { it.rank }
    }

    fun completeWeeklyQuestXpBoost() {
        viewModelScope.launch {
            repository.contributeLeagueXp(120)
            _uiState.update { it.copy(toastMessage = "Completed League Quest! +120 Weekly XP • Rank Climbing!") }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
