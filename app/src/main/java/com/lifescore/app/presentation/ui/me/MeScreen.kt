package com.lifescore.app.presentation.ui.me

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.lifescore.app.core.designsystem.components.CardVariant
import com.lifescore.app.core.designsystem.LifeScoreShapes
import com.lifescore.app.core.designsystem.Space
import com.lifescore.app.core.designsystem.components.AnimatedNumber
import com.lifescore.app.core.designsystem.components.LifeCard
import com.lifescore.app.core.designsystem.components.SectionHeader
import com.lifescore.app.core.designsystem.components.StaggeredAppear
import com.lifescore.app.core.designsystem.components.LifeIcon
import com.lifescore.app.core.designsystem.components.LifeIcons
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.presentation.navigation.Screen
import com.lifescore.app.presentation.ui.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeScreen(
    viewModel: ProfileViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Profile & Stats",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Consistency, Habits & Growth",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Space.screenH),
            verticalArrangement = Arrangement.spacedBy(Space.cardGap),
            contentPadding = PaddingValues(top = Space.xs, bottom = Space.xxxl)
        ) {
            // ==========================================
            // 1. PROFILE & IDENTITY CARD
            // ==========================================
            item {
                StaggeredAppear(index = 0) {
                    LifeCard(
                        modifier = Modifier.fillMaxWidth(),
                        variant = CardVariant.Primary
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    LifeIcon(
                                        icon = LifeIcons.Profile,
                                        size = 32.dp,
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }

                            Spacer(Modifier.height(Space.sm))
                            Text(
                                uiState.user.name.ifBlank { "User Profile" },
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                uiState.user.title.ifBlank { "The Architect" },
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(Modifier.height(Space.md))

                            // Overall Consistency Progress
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Weekly Habit Consistency",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "85% on track",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(Modifier.height(Space.xs))
                            LinearProgressIndicator(
                                progress = { 0.85f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            // ==========================================
            // 2. CONSISTENCY & DISCIPLINE METRICS (4 CARDS)
            // ==========================================
            item {
                StaggeredAppear(index = 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Space.sm)
                    ) {
                        Surface(
                            shape = LifeScoreShapes.cardSmall,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(Space.sm),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    LifeIcon(LifeIcons.Streak, size = 14.dp)
                                    Text("STREAK", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), fontWeight = FontWeight.Bold, color = Color(0xFFFF5722))
                                }
                                Spacer(Modifier.height(Space.xxs))
                                Text("${uiState.user.currentStreakDays} Days", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                        Surface(
                            shape = LifeScoreShapes.cardSmall,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(Space.sm),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    LifeIcon(LifeIcons.Check, size = 14.dp)
                                    Text("COMPLETED", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                                }
                                Spacer(Modifier.height(Space.xxs))
                                Text("48 Habits", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                        Surface(
                            shape = LifeScoreShapes.cardSmall,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(Space.sm),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    LifeIcon(LifeIcons.Goal, size = 14.dp)
                                    Text("FOCUS", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), fontWeight = FontWeight.Bold, color = Color(0xFF6366F1))
                                }
                                Spacer(Modifier.height(Space.xxs))
                                Text("12.5 hrs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                        Surface(
                            shape = LifeScoreShapes.cardSmall,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(Space.sm),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    LifeIcon(LifeIcons.Analytics, size = 14.dp)
                                    Text("RATE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                                }
                                Spacer(Modifier.height(Space.xxs))
                                Text("92%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // ==========================================
            // 3. 360° DIMENSIONS BALANCE SUMMARY CARD
            // ==========================================
            item {
                StaggeredAppear(index = 2) {
                    LifeCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(Screen.Balance.route) },
                        variant = CardVariant.Default
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = LifeScoreShapes.button,
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        LifeIcon(LifeIcons.Analytics, size = 20.dp, tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                Spacer(Modifier.width(Space.sm))
                                Column {
                                    Text("360° Life Matrix", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text("8 dimensions balanced", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // ==========================================
            // 4. IDENTITY & HABIT SYSTEMS CARDS
            // ==========================================
            item {
                StaggeredAppear(index = 3) {
                    LifeCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(Screen.IdentityHabits.route) },
                        variant = CardVariant.Cream
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = LifeScoreShapes.button,
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    LifeIcon(LifeIcons.Reading, size = 20.dp, tint = MaterialTheme.colorScheme.secondary)
                                }
                            }
                            Spacer(Modifier.width(Space.md))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Identity-Based Habits", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("“Every action is a vote for the type of person you wish to become.”", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            item {
                StaggeredAppear(index = 4) {
                    LifeCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(Screen.AtomicHabits.route) },
                        variant = CardVariant.Default
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = LifeScoreShapes.button,
                                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    LifeIcon(LifeIcons.Energy, size = 20.dp, tint = MaterialTheme.colorScheme.tertiary)
                                }
                            }
                            Spacer(Modifier.width(Space.md))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Atomic Habits OS", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("Habit loops, cues, craving, response, and environment design", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // ==========================================
            // 5. SETTINGS & PRIVACY
            // ==========================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Space.sm)
                ) {
                    OutlinedButton(
                        onClick = { navController.navigate(Screen.Settings.route) },
                        modifier = Modifier.weight(1f),
                        shape = LifeScoreShapes.button
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(Space.xs))
                        Text("Settings", style = MaterialTheme.typography.labelMedium)
                    }
                    OutlinedButton(
                        onClick = { navController.navigate(Screen.Privacy.route) },
                        modifier = Modifier.weight(1f),
                        shape = LifeScoreShapes.button
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(Space.xs))
                        Text("Privacy", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

