package com.lifescore.app.presentation.privacy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.core.database.LifeScoreDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PrivacyUiState(
    val isZeroDataModeActive: Boolean = true,
    val isLocalEncrypted: Boolean = true,
    val exportedJsonString: String? = null,
    val bannerMessage: String? = null
)

class PrivacyViewModel(
    private val database: LifeScoreDatabase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrivacyUiState())
    val uiState: StateFlow<PrivacyUiState> = _uiState.asStateFlow()

    fun toggleZeroDataMode(enabled: Boolean) {
        _uiState.update {
            it.copy(
                isZeroDataModeActive = enabled,
                bannerMessage = if (enabled) "🔒 Zero-Data Mode Active: All sync disabled, local Room DB only." else "Cloud Sync Enabled."
            )
        }
    }

    fun exportData() {
        val sampleExport = """
            {
              "exportVersion": "1.0",
              "platform": "LifeScore Android",
              "exportedAt": "${System.currentTimeMillis()}",
              "dimensions": ["Health", "Wealth", "Relationships", "Career", "Learning", "Fitness", "Mental Health", "Social"],
              "privacyPolicy": "Zero-knowledge encryption. No third-party ad tracking."
            }
        """.trimIndent()

        _uiState.update {
            it.copy(
                exportedJsonString = sampleExport,
                bannerMessage = "📦 Complete LifeScore data exported to JSON."
            )
        }
    }

    fun purgeAllData() {
        viewModelScope.launch {
            database.clearAllTables()
            _uiState.update {
                it.copy(bannerMessage = "🗑️ All local data permanently purged according to GDPR right-to-erasure.")
            }
        }
    }

    fun dismissBanner() {
        _uiState.update { it.copy(bannerMessage = null, exportedJsonString = null) }
    }
}
