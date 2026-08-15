package com.lifescore.app.presentation.ui.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.core.util.LeagueManager
import com.lifescore.app.core.util.LeagueTier
import com.lifescore.app.core.util.RankZone
import com.lifescore.app.data.remote.model.UserDocument
import com.lifescore.app.data.remote.repository.AuthRepository
import com.lifescore.app.data.remote.repository.FirebaseRepository
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

enum class LeaderboardTab(val title: String) {
    GLOBAL("Global Top 100"),
    LEAGUE("Weekly League"),
    FRIENDS("Friends & Rivals")
}

data class LeaderboardEntry(
    val rank: Int,
    val uid: String,
    val name: String,
    val score: Int,
    val level: Int,
    val streak: Int,
    val archetype: String = "Warrior",
    val isCurrentUser: Boolean = false,
    val zone: RankZone = RankZone.SAFE
)

data class LeaderboardUiState(
    val selectedTab: LeaderboardTab = LeaderboardTab.GLOBAL,
    val selectedDimension: DimensionType? = null,
    val currentLeague: LeagueTier = LeagueTier.DIAMOND,
    val userRank: Int = 1,
    val totalParticipants: Int = 100,
    val userScore: Int = 780,
    val resetCountdown: String = "2d 14h left",
    val entries: List<LeaderboardEntry> = emptyList(),
    val isLoading: Boolean = false
)

class LeaderboardViewModel(
    private val repository: LifeScoreRepository,
    private val firebaseRepository: FirebaseRepository? = null,
    private val authRepository: AuthRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    private var currentLocalUser: UserProfile = UserProfile()

    init {
        observeData()
    }

    fun selectTab(tab: LeaderboardTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
        refreshRankings()
    }

    fun selectDimension(dim: DimensionType?) {
        _uiState.value = _uiState.value.copy(selectedDimension = dim)
        refreshRankings()
    }

    private fun observeData() {
        viewModelScope.launch {
            repository.getUserProfile().collect { profile ->
                currentLocalUser = profile
                val league = LeagueManager.getLeagueForScore(profile.currentXp)
                val countdown = LeagueManager.getCountdownToSunday()
                _uiState.value = _uiState.value.copy(
                    currentLeague = league,
                    userScore = profile.currentXp,
                    resetCountdown = countdown
                )
                refreshRankings()
            }
        }

        // Real-time Firestore Global Leaderboard Observation
        viewModelScope.launch {
            firebaseRepository?.observeGlobalLeaderboard(100)?.collect { remoteUsers ->
                processRemoteUsers(remoteUsers)
            }
        }
    }

    private fun processRemoteUsers(remoteUsers: List<UserDocument>) {
        val currentUid = authRepository?.currentUser?.uid ?: "current_user_local"
        val baseList = if (remoteUsers.isNotEmpty()) {
            remoteUsers.mapIndexed { index, doc ->
                val isSelf = doc.uid == currentUid || doc.displayName == currentLocalUser.name
                LeaderboardEntry(
                    rank = index + 1,
                    uid = doc.uid,
                    name = if (isSelf) "${doc.displayName} (You)" else doc.displayName,
                    score = doc.totalScore,
                    level = doc.level,
                    streak = doc.streak,
                    archetype = doc.archetype,
                    isCurrentUser = isSelf,
                    zone = LeagueManager.getRankZone(index + 1, remoteUsers.size)
                )
            }
        } else {
            generateCompetitiveRoster(currentLocalUser)
        }

        val myRank = baseList.find { it.isCurrentUser }?.rank ?: 4

        _uiState.value = _uiState.value.copy(
            entries = baseList,
            userRank = myRank,
            totalParticipants = baseList.size,
            isLoading = false
        )
    }

    private fun refreshRankings() {
        // Generates / filters rankings according to selected tab and dimension
        val baseList = generateCompetitiveRoster(currentLocalUser)
        val filtered = when (_uiState.value.selectedTab) {
            LeaderboardTab.GLOBAL -> baseList
            LeaderboardTab.LEAGUE -> {
                val league = _uiState.value.currentLeague
                baseList.filter { it.score in league.minScore..league.maxScore }
                    .ifEmpty { baseList.take(20) }
            }
            LeaderboardTab.FRIENDS -> {
                baseList.take(8)
            }
        }.mapIndexed { idx, entry ->
            entry.copy(rank = idx + 1, zone = LeagueManager.getRankZone(idx + 1, baseList.size))
        }

        val myRank = filtered.find { it.isCurrentUser }?.rank ?: 1

        _uiState.value = _uiState.value.copy(
            entries = filtered,
            userRank = myRank,
            totalParticipants = filtered.size
        )
    }

    private fun generateCompetitiveRoster(user: UserProfile): List<LeaderboardEntry> {
        val rawRoster = listOf(
            LeaderboardEntry(1, "u_01", "Alex Morgan", 940, 18, 42, "Warrior"),
            LeaderboardEntry(2, "u_02", "Sarah Chen", 895, 16, 28, "Sage"),
            LeaderboardEntry(3, "u_03", "Marcus Vance", 860, 15, 31, "Architect"),
            LeaderboardEntry(4, "u_self", "${user.name} (You)", 840, user.currentLevel, user.currentStreakDays, "Warrior", isCurrentUser = true),
            LeaderboardEntry(5, "u_05", "Elena Rostova", 810, 13, 14, "Explorer"),
            LeaderboardEntry(6, "u_06", "David Kim", 780, 11, 9, "Monk"),
            LeaderboardEntry(7, "u_07", "Chloe Martin", 740, 10, 12, "Strategist"),
            LeaderboardEntry(8, "u_08", "Liam O'Connor", 710, 9, 8, "Vanguard"),
            LeaderboardEntry(9, "u_09", "Aria Thorne", 680, 8, 15, "Sage"),
            LeaderboardEntry(10, "u_10", "Lucas Silva", 650, 7, 6, "Warrior"),
            LeaderboardEntry(11, "u_11", "Maya Patel", 620, 6, 5, "Architect"),
            LeaderboardEntry(12, "u_12", "Noah Becker", 590, 5, 4, "Explorer")
        )

        val multiplier = _uiState.value.selectedDimension?.let { 0.95f } ?: 1.0f

        return rawRoster.sortedByDescending { (it.score * multiplier).toInt() }
            .mapIndexed { index, item ->
                item.copy(
                    rank = index + 1,
                    zone = LeagueManager.getRankZone(index + 1, rawRoster.size)
                )
            }
    }
}
