package com.lifescore.app.presentation.ui.viral

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.LifeGradients
import com.lifescore.app.core.designsystem.LifeScoreShapes
import com.lifescore.app.core.designsystem.Space
import com.lifescore.app.core.designsystem.components.CardVariant
import com.lifescore.app.core.designsystem.components.LifeCard
import com.lifescore.app.core.designsystem.components.LifeIcon
import com.lifescore.app.core.designsystem.components.LifeIcons
import com.lifescore.app.presentation.ui.share.ShareStoryCardDialog
import com.lifescore.app.presentation.ui.share.StoryCardData
import com.lifescore.app.presentation.ui.share.StoryCardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViralReferralScreen(
    viewModel: ViralReferralViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val ref = uiState.referral
    var showStoryDialog by remember { mutableStateOf(false) }

    val formattedCode = remember(ref?.referralCode) {
        val raw = ref?.referralCode ?: "7821"
        if (raw.startsWith("LIFE-")) raw else "LIFE-ARCH-${raw.takeLast(4)}"
    }

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
                            "Invite Friends & Earn Pro",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "1 Month Free for You and a Friend",
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
            verticalArrangement = Arrangement.spacedBy(Space.md),
            contentPadding = PaddingValues(top = Space.xs, bottom = Space.xxxl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Referral Hero Banner
            item {
                LifeCard(
                    modifier = Modifier.fillMaxWidth(),
                    variant = CardVariant.Primary
                ) {
                    Column(
                        modifier = Modifier.padding(Space.sm),
                        horizontalAlignment = Alignment.CenterHorizontally
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
                                LifeIcon(LifeIcons.Rocket, size = 14.dp, tint = MaterialTheme.colorScheme.primary)
                                Text(
                                    "Referral Reward Program",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Space.md))

                        Text(
                            "Invite 3 Friends → Unlock 1-Month Free Pro",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(Space.md))

                        // Progress Steps (1, 2, 3)
                        val invited = ref?.invitedCount ?: 1
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            (1..3).forEach { step ->
                                val isFilled = step <= invited
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(if (isFilled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isFilled) {
                                            LifeIcon(LifeIcons.Check, size = 18.dp, tint = MaterialTheme.colorScheme.onPrimary)
                                        } else {
                                            Text(
                                                "$step",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.titleSmall
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(Space.xxs))
                                    Text(
                                        "Friend $step",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(Space.md))

                        if (invited >= 3 || (ref?.isOneMonthPremiumUnlocked == true)) {
                            Button(
                                onClick = { viewModel.claimPremiumReward() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = LifeScoreShapes.button
                            ) {
                                LifeIcon(LifeIcons.Star, size = 18.dp, tint = MaterialTheme.colorScheme.onPrimary)
                                Spacer(modifier = Modifier.width(Space.xs))
                                Text("Claim 1-Month Free Pro (+200 XP)", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = LifeScoreShapes.cardSmall
                            ) {
                                Text(
                                    "Only ${3 - invited} more friend needed to unlock Free Pro",
                                    modifier = Modifier.padding(horizontal = Space.sm, vertical = Space.xs),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Unique Referral Code Card
            item {
                LifeCard(
                    modifier = Modifier.fillMaxWidth(),
                    variant = CardVariant.Default
                ) {
                    Column(
                        modifier = Modifier.padding(Space.sm),
                        verticalArrangement = Arrangement.spacedBy(Space.xs)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LifeIcon(LifeIcons.Goal, size = 18.dp)
                            Spacer(Modifier.width(Space.xs))
                            Text(
                                "Your Unique Invite Code",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            "Share your unique code. Both you and your friend get 1 month of LifeScore Pro.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(Space.sm))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    LifeScoreShapes.cardSmall
                                )
                                .padding(horizontal = Space.md, vertical = Space.sm),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                formattedCode,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                letterSpacing = 1.sp,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(
                                        ClipData.newPlainText(
                                            "LifeScore Referral",
                                            "Join me on LifeScore! Use my invite code: $formattedCode https://lifescore.app/invite/$formattedCode"
                                        )
                                    )
                                    Toast.makeText(context, "Invite link copied to clipboard", Toast.LENGTH_SHORT).show()
                                },
                                shape = LifeScoreShapes.button
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(Space.xs))
                                Text("Copy Link")
                            }
                        }
                    }
                }
            }

            // Shareable Story Card Trigger
            item {
                LifeCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showStoryDialog = true },
                    variant = CardVariant.Cream
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Space.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = LifeScoreShapes.button,
                            color = Color(0x20D4A24C),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                LifeIcon(LifeIcons.Star, size = 22.dp, tint = Color(0xFFD4A24C))
                            }
                        }
                        Spacer(Modifier.width(Space.md))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Generate 9:16 Instagram Story",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Share your archetype portrait, baseline score, and invite link to your story",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }

    if (showStoryDialog) {
        ShareStoryCardDialog(
            data = StoryCardData(
                cardType = StoryCardType.ARCHETYPE_REVEAL,
                score = 720,
                primaryStrength = "Scalable Systems & Structural Order"
            ),
            onDismiss = { showStoryDialog = false }
        )
    }
}
