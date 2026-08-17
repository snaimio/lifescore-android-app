package com.lifescore.app.presentation.combat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.repository.CharacterStatsRepository
import com.lifescore.app.data.repository.CombatRepository
import com.lifescore.app.domain.model.BattleLogEntry
import com.lifescore.app.domain.model.CharacterStats
import com.lifescore.app.domain.model.CombatBoss
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class CombatUiState(
    val bosses: List<CombatBoss> = emptyList(),
    val selectedBoss: CombatBoss? = null,
    val characterStats: CharacterStats = CharacterStats(),
    val isAttacking: Boolean = false,
    val battleLogs: List<BattleLogEntry> = emptyList(),
    val bannerMessage: String? = null
)

class CombatViewModel(
    private val combatRepository: CombatRepository,
    private val statsRepository: CharacterStatsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CombatUiState())
    val uiState: StateFlow<CombatUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            statsRepository.getCharacterStatsFlow().collect { stats ->
                _uiState.update { it.copy(characterStats = stats) }
            }
        }

        viewModelScope.launch {
            combatRepository.getAllBosses().collect { list ->
                _uiState.update {
                    val currentSelected = it.selectedBoss
                    val updatedSelected = if (currentSelected != null) {
                        list.find { b -> b.id == currentSelected.id } ?: list.firstOrNull()
                    } else {
                        list.firstOrNull()
                    }
                    it.copy(bosses = list, selectedBoss = updatedSelected)
                }
            }
        }
    }

    fun selectBoss(boss: CombatBoss) {
        _uiState.update { it.copy(selectedBoss = boss) }
    }

    fun attackBoss() {
        val boss = _uiState.value.selectedBoss ?: return
        if (boss.isDefeated) return

        viewModelScope.launch {
            _uiState.update { it.copy(isAttacking = true) }
            val result = combatRepository.executeAttack(boss.id, _uiState.value.characterStats.combatPower)

            val newLog = BattleLogEntry(
                id = UUID.randomUUID().toString(),
                message = result.battleLog,
                isCritical = result.isCritical,
                damageDealt = result.damageDealt
            )

            _uiState.update {
                it.copy(
                    isAttacking = false,
                    battleLogs = listOf(newLog) + it.battleLogs.take(15),
                    bannerMessage = if (result.isDefeated) "🏆 BOSS DEFEATED! Rewards distributed to character profile!" else null
                )
            }
        }
    }

    fun dismissBanner() {
        _uiState.update { it.copy(bannerMessage = null) }
    }
}
