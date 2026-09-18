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
import com.lifescore.app.domain.model.UserProfile
import com.lifescore.app.presentation.navigation.Screen
import com.lifescore.app.presentation.ui.components.CharacterSheetDialog
import com.lifescore.app.presentation.ui.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeScreen(
    viewModel: ProfileViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCharacterSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Me",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Identity, Character & Evolution",
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
            // 1. PROFILE & ARCHETYPE CARD
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
                                modifier = Modifier.size(68.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    LifeIcon(
                                        icon = LifeIcons.Profile,
                                        size = 36.dp,
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }

                            Spacer(Modifier.height(Space.sm))
                            Text(
                                uiState.user.name.ifBlank { "Hero" },
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

                            // Level & XP Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Level ${uiState.user.currentLevel}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "${uiState.user.currentXp % 500} / 500 XP to Lvl ${uiState.user.currentLevel + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(Modifier.height(Space.xs))
                            LinearProgressIndicator(
                                progress = { ((uiState.user.currentXp % 500) / 500f).coerceIn(0f, 1f) },
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
            // 2. HERO STAT COUNTERS
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
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    LifeIcon(LifeIcons.Streak, size = 12.dp)
                                    Text("STREAK", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), fontWeight = FontWeight.Black, color = Color(0xFFFF5722))
                                }
                                Spacer(Modifier.height(Space.xxs))
                                Text("${uiState.user.currentStreakDays}d", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
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
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    LifeIcon(LifeIcons.Wealth, size = 12.dp)
                                    Text("GOLD", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), fontWeight = FontWeight.Black, color = Color(0xFFFFD700))
                                }
                                Spacer(Modifier.height(Space.xxs))
                                Text("1,250", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
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
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    LifeIcon(LifeIcons.Goal, size = 12.dp)
                                    Text("SHIELDS", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), fontWeight = FontWeight.Black, color = Color(0xFF6366F1))
                                }
                                Spacer(Modifier.height(Space.xxs))
                                Text("${uiState.streakShieldsAvailable}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
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
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    LifeIcon(LifeIcons.Trophy, size = 12.dp)
                                    Text("LEAGUE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                                }
                                Spacer(Modifier.height(Space.xxs))
                                Text("Diamond", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }

            // ==========================================
            // 3. HERO CHARACTER SHEET CARD
            // ==========================================
            item {
                StaggeredAppear(index = 2) {
                    LifeCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(Screen.CharacterStats.route) },
                        variant = CardVariant.Default
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = LifeScoreShapes.button,
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    LifeIcon(LifeIcons.Star, size = 22.dp, tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                            Spacer(Modifier.width(Space.md))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Hero Character Sheet", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("STR, INT, WIS, STA attribute points & class perks", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // ==========================================
            // 4. IDENTITY-BASED HABITS
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
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    LifeIcon(LifeIcons.Reading, size = 22.dp, tint = MaterialTheme.colorScheme.secondary)
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

            // ==========================================
            // 5. ACHIEVEMENTS & BADGES PREVIEW
            // ==========================================
            item {
                SectionHeader(
                    title = "Badges & Milestones",
                    subtitle = "Earned accolades from daily discipline",
                    actionLabel = "View All →",
                    onActionClick = { navController.navigate(Screen.CharacterStats.route) }
                )

                val badges = listOf(
                    Triple(LifeIcons.Streak, "7-Day Flame", true),
                    Triple(LifeIcons.Energy, "Deep Work", true),
                    Triple(LifeIcons.Meditation, "Circadian Zen", true),
                    Triple(LifeIcons.Trophy, "Outlier Legend", false)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Space.sm)
                ) {
                    badges.forEach { (icon, title, unlocked) ->
                        Surface(
                            shape = LifeScoreShapes.cardSmall,
                            color = if (unlocked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            border = if (unlocked) BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f)) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(Space.sm),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                LifeIcon(icon = icon, size = 22.dp)
                                Spacer(Modifier.height(Space.xxs))
                                Text(title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), fontWeight = FontWeight.Bold, maxLines = 1)
                            }
                        }
                    }
                }
            }

            // ==========================================
            // 6. CUSTOM GOLD REWARDS STORE PREVIEW
            // ==========================================
            item {
                LifeCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(Screen.CustomRewards.route) },
                    variant = CardVariant.Default
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = LifeScoreShapes.button,
                            color = Color(0xFFFFD700).copy(alpha = 0.2f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                LifeIcon(LifeIcons.Wealth, size = 22.dp)
                            }
                        }
                        Spacer(Modifier.width(Space.md))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Custom Gold Rewards Store", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("Spend earned gold on custom real-life treats & guilt-free rewards", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // ==========================================
            // 7. SUPPORTER & SUBSCRIPTION CARD
            // ==========================================
            item {
                LifeCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(Screen.SupporterSubscription.route) },
                    variant = CardVariant.Default
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = LifeScoreShapes.button,
                            color = Color(0xFF6366F1).copy(alpha = 0.2f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                LifeIcon(LifeIcons.Star, size = 22.dp, tint = Color(0xFF6366F1))
                            }
                        }
                        Spacer(Modifier.width(Space.md))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("LifeScore Supporter VIP", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("Unlock cloud sync, unlimited AI coaching, and supporter badge", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // ==========================================
            // 8. SETTINGS & PRIVACY
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

    if (showCharacterSheet) {
        CharacterSheetDialog(
            userProfile = uiState.user,
            onDismiss = { showCharacterSheet = false }
        )
    }
}
