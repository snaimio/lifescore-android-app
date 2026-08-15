package com.lifescore.app.presentation.enterprise

import androidx.lifecycle.ViewModel
import com.lifescore.app.core.util.EnterpriseManager
import com.lifescore.app.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class EnterpriseTab(val title: String, val icon: String) {
    MEMBERS("👥 Team Roster", "👥"),
    QUESTS("🏆 Company Quests", "🏆"),
    ANALYTICS("📊 Admin Analytics", "📊"),
    BILLING("💳 B2B Billing", "💳")
}

data class EnterpriseUiState(
    val selectedTab: EnterpriseTab = EnterpriseTab.MEMBERS,
    val org: EnterpriseOrg = EnterpriseManager.getDefaultOrg(),
    val members: List<EnterpriseMember> = EnterpriseManager.getDefaultMembers(),
    val challenges: List<EnterpriseChallenge> = EnterpriseManager.getDefaultChallenges(),
    val isInviteDialogOpen: Boolean = false,
    val selectedPlanForBilling: B2BPlanTier = B2BPlanTier.GROWTH,
    val billingSeatsCount: Int = 100,
    val isAnnualBilling: Boolean = true,
    val recentSuccessMessage: String? = null
) {
    val totalSeatsUsed: Int get() = members.size
    val companyVitalityIndex: Float get() = EnterpriseManager.calculateCompanyVitalityIndex(members)
    val departmentLeaderboard: List<DepartmentLeaderboardItem> get() = EnterpriseManager.calculateDepartmentLeaderboard(members)
    val burnoutMetrics: List<BurnoutRiskMetric> get() = EnterpriseManager.calculateBurnoutMetrics(members)
    val billingTotalQuote: Double get() = EnterpriseManager.calculateBillingQuote(selectedPlanForBilling, billingSeatsCount, isAnnualBilling)
}

class EnterpriseViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EnterpriseUiState())
    val uiState: StateFlow<EnterpriseUiState> = _uiState.asStateFlow()

    fun selectTab(tab: EnterpriseTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun openInviteDialog() {
        _uiState.value = _uiState.value.copy(isInviteDialogOpen = true)
    }

    fun closeInviteDialog() {
        _uiState.value = _uiState.value.copy(isInviteDialogOpen = false)
    }

    fun inviteMember(name: String, email: String, department: DepartmentType, role: EnterpriseRole) {
        if (name.isBlank() || email.isBlank()) return
        val newMember = EnterpriseMember(
            displayName = name,
            email = email,
            role = role,
            department = department,
            lifeScore = 750,
            currentStreak = 1,
            weeklyQuestsCompleted = 5
        )

        _uiState.value = _uiState.value.copy(
            members = listOf(newMember) + _uiState.value.members,
            isInviteDialogOpen = false,
            recentSuccessMessage = "Invited $name ($email) to ${department.displayName}!"
        )
    }

    fun setBillingSeats(seats: Int) {
        _uiState.value = _uiState.value.copy(billingSeatsCount = seats.coerceIn(5, 1000))
    }

    fun toggleBillingPeriod() {
        _uiState.value = _uiState.value.copy(isAnnualBilling = !_uiState.value.isAnnualBilling)
    }

    fun selectPlanTier(plan: B2BPlanTier) {
        _uiState.value = _uiState.value.copy(selectedPlanForBilling = plan)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(recentSuccessMessage = null)
    }
}
