package com.lifescore.app.presentation.ui.books

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.core.util.TextToSpeechNarrator
import com.lifescore.app.data.local.entity.BookSummaryProgressEntity
import com.lifescore.app.data.repository.BookSummaryRepository
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.selfimprovement.BookSummariesCatalog
import com.lifescore.app.domain.model.selfimprovement.BookSummary
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class BookLibraryUiState(
    val books: List<Pair<BookSummary, BookSummaryProgressEntity?>> = emptyList(),
    val selectedDimension: DimensionType? = null,
    val searchQuery: String = "",
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val isLoading: Boolean = true,
    val snackbarMessage: String? = null
)

data class BookDetailUiState(
    val book: BookSummary? = null,
    val progress: BookSummaryProgressEntity? = null,
    val isPlayingAudio: Boolean = false,
    val audioProgressSeconds: Int = 0,
    val audioTotalSeconds: Int = 12 * 60,
    val playbackSpeed: Float = 1.0f,
    val activeTab: Int = 0, // 0: Overview, 1: Key Insights, 2: LifeScore Quest, 3: Quotes
    val snackbarMessage: String? = null
)

class BookSummaryViewModel(
    private val repository: BookSummaryRepository,
    private val userId: String = "default_user",
    context: Context? = null
) : ViewModel() {

    private val _libraryState = MutableStateFlow(BookLibraryUiState())
    val libraryState: StateFlow<BookLibraryUiState> = _libraryState.asStateFlow()

    private val _detailState = MutableStateFlow(BookDetailUiState())
    val detailState: StateFlow<BookDetailUiState> = _detailState.asStateFlow()

    private var audioJob: Job? = null
    private val narrator: TextToSpeechNarrator? = context?.let { TextToSpeechNarrator(it) }

    init {
        loadLibrary()
    }

    private fun loadLibrary() {
        viewModelScope.launch {
            repository.getAllBooksWithProgress(userId).collectLatest { list ->
                val completed = list.count { it.second?.isCompleted == true }
                _libraryState.update {
                    it.copy(
                        books = list,
                        completedCount = completed,
                        totalCount = list.size,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun filterDimension(dimension: DimensionType?) {
        _libraryState.update { it.copy(selectedDimension = dimension) }
    }

    fun updateSearchQuery(query: String) {
        _libraryState.update { it.copy(searchQuery = query) }
    }

    fun loadBookDetail(bookId: String) {
        narrator?.stop()
        audioJob?.cancel()
        val book = BookSummariesCatalog.books.find { it.id == bookId }
        _detailState.update {
            it.copy(
                book = book,
                audioTotalSeconds = (book?.readingTimeMinutes ?: 12) * 60,
                audioProgressSeconds = 0,
                isPlayingAudio = false
            )
        }
        viewModelScope.launch {
            repository.getBookDetailWithProgress(bookId, userId).collectLatest { pair ->
                _detailState.update { it.copy(book = pair.first, progress = pair.second) }
            }
        }
    }

    fun toggleBookmark(bookId: String) {
        viewModelScope.launch {
            repository.toggleBookmark(bookId, userId)
        }
    }

    fun toggleAudioPlayback() {
        val currentlyPlaying = _detailState.value.isPlayingAudio
        if (currentlyPlaying) {
            pauseAudio()
        } else {
            val book = _detailState.value.book ?: return
            _detailState.update { it.copy(isPlayingAudio = true) }
            val speechText = buildNarrationScript(book)
            narrator?.speak(speechText, _detailState.value.playbackSpeed) {
                viewModelScope.launch {
                    _detailState.update { it.copy(isPlayingAudio = false, audioProgressSeconds = it.audioTotalSeconds) }
                    markBookCompleted(book.id)
                }
            }
            startAudioProgressTimer()
        }
    }

    fun pauseAudio() {
        audioJob?.cancel()
        narrator?.stop()
        _detailState.update { it.copy(isPlayingAudio = false) }
    }

    fun stopAudio() {
        audioJob?.cancel()
        narrator?.stop()
        _detailState.update { it.copy(isPlayingAudio = false, audioProgressSeconds = 0) }
    }

    private fun buildNarrationScript(book: BookSummary): String {
        return buildString {
            append("Audio summary of ").append(book.title).append(", by ").append(book.author).append(". ")
            append("Core Thesis: ").append(book.coreThesis).append(". ")
            append("Summary Overview: ").append(book.summaryOverview).append(". ")
            if (book.keyTakeaways.isNotEmpty()) {
                append("Key Takeaways. ")
                book.keyTakeaways.forEach { takeaway ->
                    append("Takeaway ").append(takeaway.index).append(": ").append(takeaway.title).append(". ")
                    append(takeaway.summary).append(". ")
                    append("Action Step: ").append(takeaway.actionStep).append(". ")
                }
            }
            if (book.memorableQuotes.isNotEmpty()) {
                append("Memorable Quotes: ")
                book.memorableQuotes.forEach { quote ->
                    append("\"").append(quote).append("\". ")
                }
            }
            append("LifeScore Applied Quest: ").append(book.actionableLifeScoreQuest).append(".")
        }
    }

    private fun startAudioProgressTimer() {
        audioJob?.cancel()
        audioJob = viewModelScope.launch {
            while (_detailState.value.isPlayingAudio) {
                delay(1000L)
                val current = _detailState.value.audioProgressSeconds
                val total = _detailState.value.audioTotalSeconds
                val speed = _detailState.value.playbackSpeed
                val next = (current + (1 * speed).toInt()).coerceAtMost(total)

                _detailState.update { it.copy(audioProgressSeconds = next) }

                if (next >= total) {
                    break
                }
            }
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _detailState.update { it.copy(playbackSpeed = speed) }
        narrator?.setSpeed(speed)
    }

    fun setActiveTab(tab: Int) {
        _detailState.update { it.copy(activeTab = tab) }
    }

    fun markBookCompleted(bookId: String) {
        viewModelScope.launch {
            val xp = repository.markBookCompleted(bookId, userId)
            if (xp > 0) {
                _detailState.update { it.copy(snackbarMessage = "🎉 Summary Completed! (+75 XP)") }
            }
        }
    }

    fun applyQuest(bookId: String) {
        viewModelScope.launch {
            val xp = repository.completeAppliedQuest(bookId, userId)
            if (xp > 0) {
                _detailState.update { it.copy(snackbarMessage = "⚔️ LifeScore Quest Completed! (+75 XP)") }
            }
        }
    }

    fun clearSnackbar() {
        _detailState.update { it.copy(snackbarMessage = null) }
        _libraryState.update { it.copy(snackbarMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        audioJob?.cancel()
        narrator?.shutdown()
    }
}
