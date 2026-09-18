package com.lifescore.app.presentation.ui.social

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.LifeScoreShapes
import com.lifescore.app.core.designsystem.Space
import com.lifescore.app.core.designsystem.components.CardVariant
import com.lifescore.app.core.designsystem.components.LifeCard
import com.lifescore.app.core.designsystem.components.LifeIcon
import com.lifescore.app.core.designsystem.components.LifeIcons
import com.lifescore.app.core.designsystem.components.SectionHeader
import com.lifescore.app.data.local.entity.FriendActivityEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsFeedScreen(
    viewModel: FriendsFeedViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Friends & Squad Feed",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Social Accountability & Mutual Cheering",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Space.screenH),
            verticalArrangement = Arrangement.spacedBy(Space.cardGap),
            contentPadding = PaddingValues(top = Space.xs, bottom = Space.xxxl)
        ) {
            // Accountability Hero Card
            item {
                LifeCard(
                    modifier = Modifier.fillMaxWidth(),
                    variant = CardVariant.Primary
                ) {
                    Column(modifier = Modifier.padding(Space.sm)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = LifeScoreShapes.tag
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = Space.sm, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Space.xs)
                                ) {
                                    LifeIcon(LifeIcons.Social, size = 14.dp, tint = MaterialTheme.colorScheme.primary)
                                    Text(
                                        "Social Accountability",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                LifeIcon(LifeIcons.Streak, size = 14.dp)
                                Text(
                                    "4 Friends Active",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Space.md))

                        Text(
                            "Celebrate Wins & Nudge Your Squad",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(Space.xxs))
                        Text(
                            "Sharing consistency makes you 2.8x more likely to maintain daily compounding habits.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Live Feed List Header
            item {
                SectionHeader(
                    title = "Live Squad Activity Stream",
                    subtitle = "Real-time updates from your accountability circle"
                )
            }

            items(uiState.activities) { act ->
                FriendActivityCard(
                    activity = act,
                    onNudge = { viewModel.nudgeFriend(act) },
                    onGift = { viewModel.giftStreakFreeze(act) }
                )
            }
        }
    }
}

@Composable
fun FriendActivityCard(
    activity: FriendActivityEntity,
    onNudge: () -> Unit,
    onGift: () -> Unit
) {
    LifeCard(
        modifier = Modifier.fillMaxWidth(),
        variant = CardVariant.Default
    ) {
        Column(modifier = Modifier.padding(Space.sm)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        LifeIcon(LifeIcons.Profile, size = 20.dp, tint = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(modifier = Modifier.width(Space.sm))
                Column(modifier = Modifier.weight(1f)) {
                    Text(activity.friendName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        LifeIcon(LifeIcons.Streak, size = 12.dp)
                        Text(
                            "${activity.streakDays}d streak • ${activity.dimensionTag}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Space.sm))

            Text(
                activity.actionDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(Space.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Space.sm)
            ) {
                OutlinedButton(
                    onClick = onNudge,
                    modifier = Modifier.weight(1f),
                    shape = LifeScoreShapes.button
                ) {
                    LifeIcon(LifeIcons.Streak, size = 14.dp)
                    Spacer(Modifier.width(Space.xs))
                    Text(if (activity.isNudgedToday) "Nudged" else "Send Nudge", style = MaterialTheme.typography.labelMedium)
                }

                Button(
                    onClick = onGift,
                    modifier = Modifier.weight(1f),
                    shape = LifeScoreShapes.button,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    LifeIcon(LifeIcons.Goal, size = 14.dp, tint = MaterialTheme.colorScheme.onSecondary)
                    Spacer(Modifier.width(Space.xs))
                    Text("Gift Freeze", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
