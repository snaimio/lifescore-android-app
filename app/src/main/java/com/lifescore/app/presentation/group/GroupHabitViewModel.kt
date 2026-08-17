package com.lifescore.app.presentation.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.repository.GroupHabitRepository
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.GroupHabit
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class GroupHabitUiState(
    val allHabits: List<GroupHabit> = emptyList(),
    val joinedHabits: List<GroupHabit> = emptyList(),
    val selectedTab: Int = 0, // 0 = Explore, 1 = My Squads
    val bannerMessage: String? = null
)

class GroupHabitViewModel(
    private val repository: GroupHabitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupHabitUiState())
    val uiState: StateFlow<GroupHabitUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getAllGroupHabits().collect { list ->
                _uiState.update {
                    it.copy(
                        allHabits = list,
                        joinedHabits = list.filter { h -> h.isJoined }
                    )
                }
            }
        }
    }

    fun selectTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    fun joinGroup(habit: GroupHabit) {
        viewModelScope.launch {
            repository.joinGroup(habit.id)
            _uiState.update { it.copy(bannerMessage = "Joined ${habit.title}! Shared streak activated.") }
        }
    }

    fun leaveGroup(habit: GroupHabit) {
        viewModelScope.launch {
            repository.leaveGroup(habit.id)
            _uiState.update { it.copy(bannerMessage = "Left squad.") }
        }
    }

    fun completeToday(habit: GroupHabit) {
        viewModelScope.launch {
            repository.completeToday(habit.id)
            _uiState.update { it.copy(bannerMessage = "⚡ Daily completion logged for ${habit.title}! +${habit.xpReward} XP") }
        }
    }

    fun createSquad(title: String, description: String, dimension: DimensionType) {
        viewModelScope.launch {
            val created = repository.createGroupHabit(title, description, dimension)
            _uiState.update { it.copy(bannerMessage = "Squad '${created.title}' created! Invite link generated.") }
        }
    }

    fun dismissBanner() {
        _uiState.update { it.copy(bannerMessage = null) }
    }
}
