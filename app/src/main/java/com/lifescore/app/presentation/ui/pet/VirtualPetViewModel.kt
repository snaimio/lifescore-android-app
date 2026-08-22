package com.lifescore.app.presentation.ui.pet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.local.entity.VirtualPetEntity
import com.lifescore.app.data.repository.VirtualPetRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class VirtualPetUiState(
    val pet: VirtualPetEntity? = null,
    val isPettingAnimation: Boolean = false,
    val toastMessage: String? = null
)

class VirtualPetViewModel(
    private val repository: VirtualPetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VirtualPetUiState())
    val uiState: StateFlow<VirtualPetUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getPet().collect { petData ->
                _uiState.update { it.copy(pet = petData) }
            }
        }
    }

    fun feedPet() {
        viewModelScope.launch {
            val boost = repository.feedPet()
            _uiState.update { it.copy(toastMessage = "Fed ${it.pet?.petName ?: "Pip"}! +$boost Happiness & +20 XP") }
        }
    }

    fun petAffection() {
        viewModelScope.launch {
            val boost = repository.petAffection()
            _uiState.update {
                it.copy(
                    isPettingAnimation = true,
                    toastMessage = "${it.pet?.petName ?: "Pip"} feels loved! +$boost Happiness & +15 XP"
                )
            }
        }
    }

    fun changeHat(newHat: String) {
        viewModelScope.launch {
            val current = _uiState.value.pet ?: return@launch
            repository.customizePet(newHat, current.equippedAccessory)
            _uiState.update { it.copy(toastMessage = "Equipped $newHat!") }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
