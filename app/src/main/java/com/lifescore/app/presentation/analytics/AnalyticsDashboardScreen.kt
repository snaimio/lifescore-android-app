package com.lifescore.app.presentation.analytics

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.*
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.domain.model.DimensionCorrelation
import com.lifescore.app.domain.model.HeatmapDay
import com.lifescore.app.presentation.ui.home.components.getDimensionEmoji

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsDashboardScreen(
    viewModel: AnalyticsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📊", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("Predictive Analytics", fontWeight = FontWeight.Black)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
        ) {
            // Hero Analytics Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(14.dp, shape = RoundedCornerShape(22.dp))
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF004D40), Color(0xFF00796B), Color(0xFF00BFA5))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "INTELLIGENCE ENGINE",
                                style = MaterialTheme.typography.labelMedium,
                                letterSpacing = 1.8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.Black.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    "Compounding +2.8pts/day",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64FFDA),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Growth Trajectory & Heatmaps",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            "Multi-variate correlation models predicting your 30/60/90 day holistic LifeScore expansion.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // GitHub-style Heatmap Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "🔥 140-Day Consistency Heatmap",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Past 20 Weeks",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        // Scrollable Heatmap Grid
                        val scrollState = rememberScrollState()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(scrollState),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Group into columns of 7 days (weeks)
                            val weeks = uiState.heatmapDays.chunked(7)
                            weeks.forEach { weekDays ->
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    weekDays.forEach { day ->
                                        val color = when (day.intensityLevel) {
                                            0 -> MaterialTheme.colorScheme.surfaceVariant
                                            1 -> Color(0xFF00E676).copy(alpha = 0.35f)
                                            2 -> Color(0xFF00E676).copy(alpha = 0.60f)
                                            3 -> Color(0xFF00E676).copy(alpha = 0.85f)
                                            else -> Color(0xFF00E676)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(color)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Legend
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Less", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                            Spacer(Modifier.width(4.dp))
                            listOf(0.2f, 0.45f, 0.7f, 1.0f).forEach { alpha ->
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Color(0xFF00E676).copy(alpha = alpha))
                                )
                                Spacer(Modifier.width(3.dp))
                            }
                            Spacer(Modifier.width(2.dp))
                            Text("More", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }

            // Predictive Growth Curve Forecast Card
            item {
                val forecast = uiState.forecast
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            "📈 90-Day Predictive Trajectory",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Based on current task velocity and streak stability",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )

                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ForecastPill(label = "Now", score = forecast?.currentScore ?: 780, color = MaterialTheme.colorScheme.primary)
                            ForecastPill(label = "+30 Days", score = forecast?.projected30Days ?: 864, color = Color(0xFF00ACC1))
                            ForecastPill(label = "+60 Days", score = forecast?.projected60Days ?: 948, color = Color(0xFF00E676))
                            ForecastPill(label = "+90 Days", score = forecast?.projected90Days ?: 990, color = Color(0xFFFFD54F))
                        }
                    }
                }
            }

            // Dimension Correlation Analysis
            item {
                Text(
                    "🧠 Cross-Dimension Correlations",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(uiState.correlations) { correlation ->
                CorrelationCard(correlation = correlation)
            }
        }
    }
}

@Composable
fun ForecastPill(label: String, score: Int, color: Color) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            Spacer(Modifier.height(2.dp))
            Text("$score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = color)
        }
    }
}

@Composable
fun CorrelationCard(correlation: DimensionCorrelation) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${getDimensionEmoji(correlation.dimensionA)} ${correlation.dimensionA.displayName} ⟷ ${getDimensionEmoji(correlation.dimensionB)} ${correlation.dimensionB.displayName}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF00E676).copy(alpha = 0.15f)
                ) {
                    Text(
                        "${correlation.correlationPercentage}% Sync",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = correlation.insightText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
