package com.lifescore.app.presentation.ui.books

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.selfimprovement.BookSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSummaryLibraryScreen(
    viewModel: BookSummaryViewModel,
    navController: NavController,
    onBack: () -> Unit = { navController.popBackStack() }
) {
    val state by viewModel.libraryState.collectAsState()

    val filteredBooks = state.books.filter { pair ->
        val book = pair.first
        val matchesDim = state.selectedDimension == null || book.dimension == state.selectedDimension
        val matchesQuery = state.searchQuery.isBlank() ||
                book.title.contains(state.searchQuery, ignoreCase = true) ||
                book.author.contains(state.searchQuery, ignoreCase = true)
        matchesDim && matchesQuery
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📚", fontSize = 22.sp)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                "Book Summaries Library",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "10-15 Min Core Insights (Headway Style)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(end = Spacing.sm)
                    ) {
                        Text(
                            text = "${state.completedCount}/${state.totalCount} Read",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // 1. Search Bar
            item {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Search books, authors, concepts...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
            }

            // 2. Dimension Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = state.selectedDimension == null,
                            onClick = { viewModel.filterDimension(null) },
                            label = { Text("All (${state.books.size})", style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                    items(DimensionType.values()) { dim ->
                        val count = state.books.count { it.first.dimension == dim }
                        FilterChip(
                            selected = state.selectedDimension == dim,
                            onClick = { viewModel.filterDimension(if (state.selectedDimension == dim) null else dim) },
                            label = { Text("${dim.displayName} ($count)", style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }

            // 3. Book List
            items(filteredBooks, key = { it.first.id }) { pair ->
                val book = pair.first
                val progress = pair.second
                val isCompleted = progress?.isCompleted == true
                val isBookmarked = progress?.isBookmarked == true

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("book_detail/${book.id}")
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Emoji Book Cover
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            modifier = Modifier.size(60.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(book.coverEmoji, fontSize = 32.sp)
                            }
                        }

                        Spacer(Modifier.width(Spacing.md))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = book.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                if (isCompleted) {
                                    Spacer(Modifier.width(6.dp))
                                    Text("✅", fontSize = 14.sp)
                                }
                            }
                            Text(
                                text = "By ${book.author}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF6366F1).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = book.dimension.displayName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(Modifier.width(8.dp))

                                Text(
                                    text = "⏱️ ${book.readingTimeMinutes} min",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(Modifier.width(8.dp))

                                Text(
                                    text = "⭐ ${book.rating}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF59E0B)
                                )
                            }
                        }

                        IconButton(onClick = { viewModel.toggleBookmark(book.id) }) {
                            Icon(
                                if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(Spacing.xl))
            }
        }
    }
}
