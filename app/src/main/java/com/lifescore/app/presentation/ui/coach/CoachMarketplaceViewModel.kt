package com.lifescore.app.presentation.ui.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.local.entity.CoachBookingEntity
import com.lifescore.app.data.local.entity.CoachEntity
import com.lifescore.app.data.repository.CoachMarketplaceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CoachMarketplaceUiState(
    val coaches: List<CoachEntity> = emptyList(),
    val bookings: List<CoachBookingEntity> = emptyList(),
    val selectedSpecialty: String = "ALL",
    val selectedCoachForBooking: CoachEntity? = null,
    val bookingGoalInput: String = "",
    val toastMessage: String? = null
)

class CoachMarketplaceViewModel(
    private val repository: CoachMarketplaceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoachMarketplaceUiState())
    val uiState: StateFlow<CoachMarketplaceUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedDefaultCoachesIfEmpty()
        }

        viewModelScope.launch {
            combine(
                repository.getAllCoaches(),
                repository.getBookings()
            ) { coaches, bookings ->
                Pair(coaches, bookings)
            }.collect { (coaches, bookings) ->
                _uiState.update { it.copy(coaches = coaches, bookings = bookings) }
            }
        }
    }

    fun setSpecialty(specialty: String) {
        _uiState.update { it.copy(selectedSpecialty = specialty) }
    }

    fun selectCoachForBooking(coach: CoachEntity?) {
        _uiState.update { it.copy(selectedCoachForBooking = coach, bookingGoalInput = "") }
    }

    fun onGoalChange(goal: String) {
        _uiState.update { it.copy(bookingGoalInput = goal) }
    }

    fun confirmBooking() {
        val coach = _uiState.value.selectedCoachForBooking ?: return
        val goal = _uiState.value.bookingGoalInput.ifBlank { "Deep work accountability & habit mastery" }

        viewModelScope.launch {
            repository.bookCoachSession(
                coachId = coach.coachId,
                coachName = coach.name,
                dateIso = "Tomorrow, 10:00 AM EST",
                goal = goal
            )
            _uiState.update {
                it.copy(
                    selectedCoachForBooking = null,
                    toastMessage = "Session booked with ${coach.name}! Check confirmation email."
                )
            }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
