package com.lifescore.app.presentation.challenges

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.core.util.DuelManager
import com.lifescore.app.data.remote.model.ChallengeDocument
import com.lifescore.app.data.remote.repository.AuthRepository
import com.lifescore.app.data.remote.repository.FirebaseRepository
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.domain.model.Challenge
import com.lifescore.app.domain.model.ChallengeParticipant
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.core.util.ExpertChallengeManager
import com.lifescore.app.domain.model.ExpertMasterclass
import com.lifescore.app.domain.model.MasterclassCertificate
import com.lifescore.app.domain.model.MasterclassDayModule
import com.lifescore.app.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ChallengeTab(val title: String) {
    ACTIVE("Active Sprints"),
    DISCOVER("Discover Public"),
    DUELS("1v1 Duels"),
    MASTERCLASSES("🎓 Masterclasses (14-Day)")
}

data class ChallengesUiState(
    val selectedTab: ChallengeTab = ChallengeTab.ACTIVE,
    val selectedDimension: DimensionType? = null,
    val challenges: List<Challenge> = emptyList(),
    val masterclasses: List<ExpertMasterclass> = ExpertChallengeManager.masterclasses,
    val selectedMasterclass: ExpertMasterclass? = ExpertChallengeManager.masterclasses.firstOrNull(),
    val selectedMasterclassDay: MasterclassDayModule? = ExpertChallengeManager.masterclasses.firstOrNull()?.days?.firstOrNull(),
    val isPlayingAudio: Boolean = false,
    val audioProgress: Float = 0.35f,
    val showGraduationModal: Boolean = false,
    val activeCertificate: MasterclassCertificate? = null,
    val activeCount: Int = 0,
    val totalXpEarned: Int = 0,
    val userProfile: UserProfile = UserProfile(),
    val showCreateDialog: Boolean = false,
    val selectedChallengeDetail: Challenge? = null,
    val recentSuccessMessage: String? = null
)

class ChallengesViewModel(
    private val repository: LifeScoreRepository,
    private val firebaseRepository: FirebaseRepository? = null,
    private val authRepository: AuthRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChallengesUiState())
    val uiState: StateFlow<ChallengesUiState> = _uiState.asStateFlow()

    private var localUser: UserProfile = UserProfile()

    init {
        loadData()
    }

    fun selectTab(tab: ChallengeTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun selectDimension(dim: DimensionType?) {
        _uiState.value = _uiState.value.copy(selectedDimension = dim)
    }

    fun showCreateDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showCreateDialog = show)
    }

    fun selectChallengeDetail(challenge: Challenge?) {
        _uiState.value = _uiState.value.copy(selectedChallengeDetail = challenge)
    }

    fun selectMasterclass(masterclass: ExpertMasterclass) {
        _uiState.value = _uiState.value.copy(
            selectedMasterclass = masterclass,
            selectedMasterclassDay = masterclass.days.getOrNull(masterclass.currentDay - 1) ?: masterclass.days.firstOrNull()
        )
    }

    fun selectMasterclassDay(day: MasterclassDayModule) {
        _uiState.value = _uiState.value.copy(selectedMasterclassDay = day)
    }

    fun toggleAudioPlayback() {
        _uiState.value = _uiState.value.copy(isPlayingAudio = !_uiState.value.isPlayingAudio)
    }

    fun unlockMasterclass(masterclassId: String) {
        val updated = _uiState.value.masterclasses.map { mc ->
            if (mc.id == masterclassId) mc.copy(isUnlocked = true) else mc
        }
        val currentSel = updated.find { it.id == masterclassId }
        _uiState.value = _uiState.value.copy(
            masterclasses = updated,
            selectedMasterclass = currentSel,
            recentSuccessMessage = "Masterclass Unlocked! 🎉 Access to all 14 days granted."
        )
    }

    fun checkInMasterclassDay(masterclassId: String, dayNumber: Int) {
        val target = _uiState.value.masterclasses.find { it.id == masterclassId } ?: return
        val updated = ExpertChallengeManager.checkInDay(target, dayNumber)

        val list = _uiState.value.masterclasses.map { if (it.id == masterclassId) updated else it }
        val cert = if (updated.isCompleted) {
            ExpertChallengeManager.generateCertificate(updated, _uiState.value.userProfile.name.ifBlank { "Champion Hero" })
        } else null

        _uiState.value = _uiState.value.copy(
            masterclasses = list,
            selectedMasterclass = updated,
            selectedMasterclassDay = updated.days.getOrNull(updated.currentDay - 1),
            activeCertificate = cert,
            showGraduationModal = updated.isCompleted,
            recentSuccessMessage = if (updated.isCompleted) "🏆 Congratulations! 14-Day Masterclass Graduated!" else "Day $dayNumber Completed! +50 XP"
        )
    }

    fun closeGraduationModal() {
        _uiState.value = _uiState.value.copy(showGraduationModal = false)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(recentSuccessMessage = null)
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getUserProfile().collect { profile ->
                localUser = profile
                _uiState.value = _uiState.value.copy(userProfile = profile)
            }
        }

        val initialChallenges = getInitialChallengesList()
        _uiState.value = _uiState.value.copy(
            challenges = initialChallenges,
            activeCount = initialChallenges.count { it.isJoined && !it.isCompleted }
        )
    }

    fun joinChallenge(challengeId: String) {
        viewModelScope.launch {
            val updated = _uiState.value.challenges.map { item ->
                if (item.id == challengeId) {
                    val myParticipant = ChallengeParticipant(
                        uid = authRepository?.currentUser?.uid ?: "self",
                        name = "${localUser.name} (You)",
                        level = localUser.currentLevel,
                        completedDays = 0,
                        streak = localUser.currentStreakDays,
                        isCurrentUser = true
                    )
                    item.copy(
                        isJoined = true,
                        currentDay = 1,
                        participantsCount = item.participantsCount + 1,
                        participants = listOf(myParticipant) + item.participants,
                        dailyCheckIns = DuelManager.getDailyCheckInStatus(0, item.durationDays)
                    )
                } else item
            }
            _uiState.value = _uiState.value.copy(
                challenges = updated,
                activeCount = updated.count { it.isJoined && !it.isCompleted },
                recentSuccessMessage = "Joined Challenge! Let the sprint begin 🚀"
            )

            // Sync to Firestore
            authRepository?.currentUser?.uid?.let { uid ->
                try {
                    firebaseRepository?.joinChallenge(uid, challengeId)
                } catch (_: Exception) {}
            }
        }
    }

    fun checkInToday(challengeId: String) {
        viewModelScope.launch {
            var bonusXp = 50
            var completedBonus = 0

            val updated = _uiState.value.challenges.map { item ->
                if (item.id == challengeId && item.isJoined && !item.isCompleted) {
                    val nextDay = item.currentDay + 1
                    val isNowCompleted = nextDay >= item.durationDays
                    if (isNowCompleted) {
                        completedBonus = item.xpReward
                    }

                    val updatedParticipants = item.participants.map { p ->
                        if (p.isCurrentUser) {
                            p.copy(completedDays = nextDay, streak = p.streak + 1)
                        } else p
                    }

                    item.copy(
                        currentDay = nextDay,
                        isCompleted = isNowCompleted,
                        dailyCheckIns = DuelManager.getDailyCheckInStatus(nextDay, item.durationDays),
                        participants = updatedParticipants
                    )
                } else item
            }

            val totalEarned = bonusXp + completedBonus
            val updatedXp = localUser.currentXp + totalEarned
            val updatedProfile = localUser.copy(
                currentXp = updatedXp,
                currentStreakDays = localUser.currentStreakDays + 1
            )
            repository.updateUserProfile(updatedProfile)

            val msg = if (completedBonus > 0) {
                "🎉 Challenge Completed! +${totalEarned} XP Earned & Champion Badge Unlocked!"
            } else {
                "✅ Day Check-In Logged! +${bonusXp} XP & Streak Continued 🔥"
            }

            _uiState.value = _uiState.value.copy(
                challenges = updated,
                activeCount = updated.count { it.isJoined && !it.isCompleted },
                totalXpEarned = _uiState.value.totalXpEarned + totalEarned,
                recentSuccessMessage = msg
            )
        }
    }

    fun createChallenge(
        title: String,
        description: String,
        dimension: DimensionType,
        durationDays: Int,
        xpReward: Int,
        isDuel: Boolean
    ) {
        viewModelScope.launch {
            val newId = "c_custom_${System.currentTimeMillis()}"
            val inviteCode = "DUEL_${(1000..9999).random()}"
            val myParticipant = ChallengeParticipant(
                uid = authRepository?.currentUser?.uid ?: "self",
                name = "${localUser.name} (You)",
                level = localUser.currentLevel,
                completedDays = 0,
                streak = localUser.currentStreakDays,
                isCurrentUser = true
            )

            val newChallenge = Challenge(
                id = newId,
                title = title,
                description = description,
                dimension = dimension,
                durationDays = durationDays,
                currentDay = 1,
                xpReward = xpReward,
                isJoined = true,
                isCompleted = false,
                isDuel = isDuel,
                creatorName = localUser.name,
                inviteCode = inviteCode,
                participantsCount = 1,
                participants = listOf(myParticipant),
                dailyCheckIns = DuelManager.getDailyCheckInStatus(0, durationDays)
            )

            val updated = listOf(newChallenge) + _uiState.value.challenges
            _uiState.value = _uiState.value.copy(
                challenges = updated,
                activeCount = updated.count { it.isJoined && !it.isCompleted },
                showCreateDialog = false,
                recentSuccessMessage = if (isDuel) "1v1 Duel Created! Share the invite link to start ⚔️" else "New Challenge Created!"
            )

            // Firestore sync
            try {
                firebaseRepository?.saveChallenge(
                    ChallengeDocument(
                        challengeId = newId,
                        title = title,
                        description = description,
                        duration = durationDays,
                        startDate = System.currentTimeMillis(),
                        xpReward = xpReward
                    )
                )
            } catch (_: Exception) {}
        }
    }

    fun inviteFriend(context: Context, challenge: Challenge) {
        val inviteLink = DuelManager.generateInviteLink(challenge.id, localUser.name)
        val caption = DuelManager.generateDuelCaption(challenge.title, challenge.dimension, inviteLink)

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, caption)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Challenge a Friend")
        context.startActivity(shareIntent)
    }

    private fun getInitialChallengesList(): List<Challenge> {
        return listOf(
            Challenge(
                id = "c_duel_01",
                title = "7-Day 100 Push-Ups & Cardio Sprint",
                description = "Complete 100 push-ups and 20 min cardio every single day for 7 days. Head-to-head duel!",
                dimension = DimensionType.FITNESS,
                durationDays = 7,
                currentDay = 3,
                xpReward = 750,
                isJoined = true,
                isDuel = true,
                creatorName = "Sarah Chen",
                inviteCode = "DUEL_FIT_7",
                participantsCount = 2,
                participants = listOf(
                    ChallengeParticipant("self", "You", 5, 3, 7, isCurrentUser = true),
                    ChallengeParticipant("u2", "Sarah Chen", 6, 3, 14, isCurrentUser = false)
                ),
                dailyCheckIns = listOf(true, true, true, false, false, false, false)
            ),
            Challenge(
                id = "c_duel_02",
                title = "Deep Work 4-Hour Daily Block",
                description = "4 hours of pure zero-distraction deep work. High-intensity productivity duel.",
                dimension = DimensionType.CAREER,
                durationDays = 7,
                currentDay = 1,
                xpReward = 800,
                isJoined = true,
                isDuel = true,
                creatorName = "Marcus Vance",
                inviteCode = "DUEL_WORK_4",
                participantsCount = 2,
                participants = listOf(
                    ChallengeParticipant("self", "You", 5, 1, 7, isCurrentUser = true),
                    ChallengeParticipant("u3", "Marcus Vance", 7, 2, 21, isCurrentUser = false)
                ),
                dailyCheckIns = listOf(true, false, false, false, false, false, false)
            ),
            Challenge(
                id = "c_pub_01",
                title = "30-Day CEO Morning Routine",
                description = "Wake up at 6 AM, drink 500ml water, 10 min journaling, no social media for 1 hour.",
                dimension = DimensionType.CAREER,
                durationDays = 30,
                currentDay = 12,
                xpReward = 1000,
                isJoined = true,
                isDuel = false,
                creatorName = "LifeScore Global",
                participantsCount = 428,
                dailyCheckIns = (1..30).map { it <= 12 }
            ),
            Challenge(
                id = "c_pub_02",
                title = "Mindfulness & Zen Master",
                description = "Daily 15-minute mindfulness meditation + evening gratitude journal.",
                dimension = DimensionType.MENTAL_HEALTH,
                durationDays = 21,
                currentDay = 0,
                xpReward = 750,
                isJoined = false,
                isDuel = false,
                creatorName = "LifeScore Zen",
                participantsCount = 189
            ),
            Challenge(
                id = "c_pub_03",
                title = "Zero Non-Essential Spending",
                description = "Strict no-spend challenge on non-essential items for 14 days.",
                dimension = DimensionType.WEALTH,
                durationDays = 14,
                currentDay = 0,
                xpReward = 600,
                isJoined = false,
                isDuel = false,
                creatorName = "Wealth Builders",
                participantsCount = 214
            ),
            Challenge(
                id = "c_pub_04",
                title = "7-Day Fast-Track Coding Sprint",
                description = "Build and ship 1 mini-project or solve 3 LeetCode problems every day.",
                dimension = DimensionType.LEARNING,
                durationDays = 7,
                currentDay = 0,
                xpReward = 550,
                isJoined = false,
                isDuel = true,
                creatorName = "David Kim",
                participantsCount = 67
            )
        )
    }
}
