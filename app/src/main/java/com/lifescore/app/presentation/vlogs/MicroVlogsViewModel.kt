package com.lifescore.app.presentation.vlogs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.core.util.DailyVlogStitcher
import com.lifescore.app.core.util.MicroVlogManager
import com.lifescore.app.data.repository.LifeScoreRepository
import com.lifescore.app.domain.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MicroVlogsUiState(
    val weeklyClips: List<MicroVlog> = emptyList(),
    val weeklyMontage: WeeklyMontage = MicroVlogManager.createWeeklyMontage(emptyList()),
    val dailyStitchedVlog: DailyStitchedVlog = DailyVlogStitcher.stitchClipsIntoDailyVlog("u_me", "You", "2026-08-14", emptyList()),
    val logGroups: List<LogGroup> = DailyVlogStitcher.getDefaultMockGroups(),
    val selectedGroup: LogGroup? = DailyVlogStitcher.getDefaultMockGroups().firstOrNull(),
    val isReelPlayerOpen: Boolean = false,
    val activeReelIndex: Int = 0,
    val reelProgress: Float = 0f,
    val isReelPaused: Boolean = false,
    val isRecordingDialogOpen: Boolean = false,
    val selectedClipToRecord: MicroVlog? = null,
    val isCreateGroupDialogOpen: Boolean = false,
    val isJoinGroupDialogOpen: Boolean = false,
    val isCommentsDrawerOpen: Boolean = false,
    val userScore: Int = 740,
    val userStreak: Int = 7
)

class MicroVlogsViewModel(
    private val repository: LifeScoreRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(MicroVlogsUiState())
    val uiState: StateFlow<MicroVlogsUiState> = _uiState.asStateFlow()

    private var playbackJob: Job? = null

    init {
        loadWeeklyData()
    }

    private fun loadWeeklyData() {
        val templateClips = MicroVlogManager.generateCurrentWeekTemplate()
        val montage = MicroVlogManager.createWeeklyMontage(templateClips)
        val dailyVlog = DailyVlogStitcher.stitchClipsIntoDailyVlog("u_me", "You", "2026-08-14", templateClips)

        _uiState.value = _uiState.value.copy(
            weeklyClips = templateClips,
            weeklyMontage = montage,
            dailyStitchedVlog = dailyVlog
        )

        viewModelScope.launch {
            repository?.getUserProfile()?.collect { profile ->
                _uiState.value = _uiState.value.copy(
                    userScore = 740,
                    userStreak = profile.currentStreakDays
                )
            }
        }
    }

    fun openRecordDialog(clip: MicroVlog) {
        _uiState.value = _uiState.value.copy(
            isRecordingDialogOpen = true,
            selectedClipToRecord = clip
        )
    }

    fun closeRecordDialog() {
        _uiState.value = _uiState.value.copy(
            isRecordingDialogOpen = false,
            selectedClipToRecord = null
        )
    }

    fun saveRecordedClip(dayOfWeek: String, dimension: DimensionType, caption: String) {
        val updated = _uiState.value.weeklyClips.map { clip ->
            if (clip.dayOfWeek == dayOfWeek) {
                clip.copy(
                    isRecorded = true,
                    dimension = dimension,
                    caption = caption,
                    thumbnailColorHex = dimension.baseColorHex,
                    timestamp = System.currentTimeMillis()
                )
            } else clip
        }
        val montage = MicroVlogManager.createWeeklyMontage(updated, streak = _uiState.value.userStreak)
        val dailyVlog = DailyVlogStitcher.stitchClipsIntoDailyVlog("u_me", "You", "2026-08-14", updated)

        _uiState.value = _uiState.value.copy(
            weeklyClips = updated,
            weeklyMontage = montage,
            dailyStitchedVlog = dailyVlog,
            isRecordingDialogOpen = false,
            selectedClipToRecord = null
        )
    }

    fun toggleReaction(emoji: String) {
        val updated = DailyVlogStitcher.toggleReaction(_uiState.value.dailyStitchedVlog, emoji)
        _uiState.value = _uiState.value.copy(dailyStitchedVlog = updated)
    }

    fun addComment(text: String) {
        if (text.isBlank()) return
        val updated = DailyVlogStitcher.addComment(_uiState.value.dailyStitchedVlog, "u_me", "You", text)
        _uiState.value = _uiState.value.copy(dailyStitchedVlog = updated)
    }

    fun selectGroup(group: LogGroup) {
        _uiState.value = _uiState.value.copy(selectedGroup = group)
    }

    fun createLogGroup(name: String, description: String) {
        if (name.isBlank()) return
        val newGroup = DailyVlogStitcher.createLogGroup(name, description, "u_me", "You")
        _uiState.value = _uiState.value.copy(
            logGroups = listOf(newGroup) + _uiState.value.logGroups,
            selectedGroup = newGroup,
            isCreateGroupDialogOpen = false
        )
    }

    fun joinLogGroup(inviteCode: String): Boolean {
        val target = _uiState.value.logGroups.find { it.inviteCode.equals(inviteCode.trim(), ignoreCase = true) }
            ?: DailyVlogStitcher.getDefaultMockGroups().first()

        val myMember = LogGroupMember("u_me", "You", "👑", _uiState.value.userStreak, 3, true)
        val (updated, err) = DailyVlogStitcher.joinLogGroup(target, myMember)
        if (updated != null) {
            val list = _uiState.value.logGroups.map { if (it.id == updated.id) updated else it }
            _uiState.value = _uiState.value.copy(
                logGroups = list,
                selectedGroup = updated,
                isJoinGroupDialogOpen = false
            )
            return true
        }
        return false
    }

    fun setCreateGroupDialogOpen(isOpen: Boolean) {
        _uiState.value = _uiState.value.copy(isCreateGroupDialogOpen = isOpen)
    }

    fun setJoinGroupDialogOpen(isOpen: Boolean) {
        _uiState.value = _uiState.value.copy(isJoinGroupDialogOpen = isOpen)
    }

    fun setCommentsDrawerOpen(isOpen: Boolean) {
        _uiState.value = _uiState.value.copy(isCommentsDrawerOpen = isOpen)
    }

    fun openReelPlayer(startIndex: Int = 0) {
        val recordedClips = _uiState.value.weeklyClips.filter { it.isRecorded }
        if (recordedClips.isEmpty()) return

        _uiState.value = _uiState.value.copy(
            isReelPlayerOpen = true,
            activeReelIndex = startIndex.coerceIn(0, recordedClips.size - 1),
            reelProgress = 0f,
            isReelPaused = false
        )
        startPlaybackLoop()
    }

    fun closeReelPlayer() {
        playbackJob?.cancel()
        _uiState.value = _uiState.value.copy(isReelPlayerOpen = false)
    }

    fun nextReelClip() {
        val recordedClips = _uiState.value.weeklyClips.filter { it.isRecorded }
        if (_uiState.value.activeReelIndex < recordedClips.size - 1) {
            _uiState.value = _uiState.value.copy(
                activeReelIndex = _uiState.value.activeReelIndex + 1,
                reelProgress = 0f
            )
            startPlaybackLoop()
        } else {
            closeReelPlayer()
        }
    }

    fun previousReelClip() {
        if (_uiState.value.activeReelIndex > 0) {
            _uiState.value = _uiState.value.copy(
                activeReelIndex = _uiState.value.activeReelIndex - 1,
                reelProgress = 0f
            )
            startPlaybackLoop()
        }
    }

    fun toggleReelPause() {
        val isPaused = !_uiState.value.isReelPaused
        _uiState.value = _uiState.value.copy(isReelPaused = isPaused)
        if (isPaused) {
            playbackJob?.cancel()
        } else {
            startPlaybackLoop()
        }
    }

    private fun startPlaybackLoop() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val clipDurationMs = 2000L
            val intervalMs = 20L
            val steps = clipDurationMs / intervalMs

            for (i in 0..steps) {
                if (_uiState.value.isReelPaused) break
                _uiState.value = _uiState.value.copy(reelProgress = i.toFloat() / steps.toFloat())
                delay(intervalMs)
            }
            if (!_uiState.value.isReelPaused) {
                nextReelClip()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playbackJob?.cancel()
    }
}
