package com.lifescore.app.presentation.ui.party

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.local.entity.PartyEntity
import com.lifescore.app.data.local.entity.PartyMessageEntity
import com.lifescore.app.data.repository.PartySystemRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PartyUiState(
    val party: PartyEntity? = null,
    val messages: List<PartyMessageEntity> = emptyList(),
    val chatInput: String = "",
    val toastMessage: String? = null
)

class PartyViewModel(
    private val repository: PartySystemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PartyUiState())
    val uiState: StateFlow<PartyUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getCurrentParty(),
                repository.getPartyMessages()
            ) { party, msgs ->
                Pair(party, msgs)
            }.collect { (party, msgs) ->
                _uiState.update { it.copy(party = party, messages = msgs) }
            }
        }
    }

    fun onChatChange(text: String) {
        _uiState.update { it.copy(chatInput = text) }
    }

    fun sendMessage() {
        val msg = _uiState.value.chatInput.trim()
        if (msg.isEmpty()) return
        viewModelScope.launch {
            repository.sendPartyMessage(text = msg)
            _uiState.update { it.copy(chatInput = "") }
        }
    }

    fun strikeBossQuest(damage: Int) {
        viewModelScope.launch {
            val defeated = repository.dealQuestDamage(damage = damage)
            _uiState.update {
                it.copy(
                    toastMessage = if (defeated) "🏆 BOSS DEFEATED! Party earned +200 bonus XP!" else "⚔️ Struck boss for $damage DMG! +${damage * 2} XP"
                )
            }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
