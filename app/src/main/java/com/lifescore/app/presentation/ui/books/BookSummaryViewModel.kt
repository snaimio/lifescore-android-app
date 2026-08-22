package com.lifescore.app.presentation.ui.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val userId: String = "default_user"
) : ViewModel() {

    private val _libraryState = MutableStateFlow(BookLibraryUiState())
    val libraryState: StateFlow<BookLibraryUiState> = _libraryState.asStateFlow()

    private val _detailState = MutableStateFlow(BookDetailUiState())
    val detailState: StateFlow<BookDetailUiState> = _detailState.asStateFlow()

    private var audioJob: Job? = null

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
            audioJob?.cancel()
            _detailState.update { it.copy(isPlayingAudio = false) }
        } else {
            _detailState.update { it.copy(isPlayingAudio = true) }
            startAudioSimulation()
        }
    }

    private fun startAudioSimulation() {
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
                    _detailState.update { it.copy(isPlayingAudio = false) }
                    _detailState.value.book?.let { book ->
                        markBookCompleted(book.id)
                    }
                    break
                }
            }
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _detailState.update { it.copy(playbackSpeed = speed) }
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
    }
}
