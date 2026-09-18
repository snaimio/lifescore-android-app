package com.lifescore.app.presentation.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.R
import com.lifescore.app.core.designsystem.DimensionColors
import com.lifescore.app.core.designsystem.LifeGradients
import com.lifescore.app.core.designsystem.LifeScoreShapes
import com.lifescore.app.core.designsystem.Space
import com.lifescore.app.core.designsystem.components.CardVariant
import com.lifescore.app.core.designsystem.components.LifeCard
import kotlinx.coroutines.launch

data class OnboardingFocusOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    onCreateAccount: (focusArea: String) -> Unit,
    onSignIn: () -> Unit,
    onContinueAsGuest: (focusArea: String) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    var selectedFocusIndex by remember { mutableIntStateOf(0) }

    val focusOptions = remember {
        listOf(
            OnboardingFocusOption(
                id = "high_performance",
                title = "High Performance",
                subtitle = "Career, wealth & execution",
                icon = Icons.Default.Bolt,
                accentColor = Color(0xFFD4A24C)
            ),
            OnboardingFocusOption(
                id = "vitality",
                title = "Vitality & Energy",
                subtitle = "Health, fitness & daily power",
                icon = Icons.Default.Favorite,
                accentColor = DimensionColors.Health
            ),
            OnboardingFocusOption(
                id = "mindfulness",
                title = "Mindful Clarity",
                subtitle = "Inner peace, calm & focus",
                icon = Icons.Default.Spa,
                accentColor = DimensionColors.MentalHealth
            ),
            OnboardingFocusOption(
                id = "growth",
                title = "Growth & Mastery",
                subtitle = "Learning, habits & wisdom",
                icon = Icons.Default.AutoAwesome,
                accentColor = DimensionColors.Learning
            )
        )
    }

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val backgroundColor = if (isDark) Color(0xFF0F0E14) else MaterialTheme.colorScheme.background
    val textPrimary = if (isDark) Color(0xFFFBF8F3) else Color(0xFF19181F)
    val textSecondary = if (isDark) Color(0xFF9E958B) else Color(0xFF6B6357)
    val cardBackground = if (isDark) Color(0xFF161522) else Color(0xFFFFFFFF)
    val cardBorder = if (isDark) Color(0x1FD4A24C) else Color(0x33D4A24C)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = Space.screenH)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(Space.sm))

            // Top Header: Brand Mark + Guest Skip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Space.xs)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0x22D4A24C),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(R.drawable.ic_launcher_foreground),
                                contentDescription = "LifeScore Brand Mark",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Text(
                        text = "LIFESCORE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = textPrimary
                    )
                }

                TextButton(
                    onClick = {
                        val chosenFocus = focusOptions[selectedFocusIndex].title
                        onContinueAsGuest(chosenFocus)
                    }
                ) {
                    Text(
                        text = "Guest Access →",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFD4A24C),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(Space.md))

            // Carousel Showcase of Core Pillars
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(270.dp)
            ) { page ->
                when (page) {
                    0 -> ShowcaseSlide(
                        badge = "360° LIFE MATRIX",
                        badgeColor = Color(0xFFD4A24C),
                        title = "Harmonize All 8 Dimensions",
                        description = "Track health, mindset, wealth, career, relationships, environment, growth, and purpose in one balanced view.",
                        icon = Icons.Default.PieChart,
                        tags = listOf("Health", "Mindset", "Wealth", "Career", "Growth")
                    )
                    1 -> ShowcaseSlide(
                        badge = "COMPOUNDING MOMENTUM",
                        badgeColor = DimensionColors.Fitness,
                        title = "Effortless Daily Habits",
                        description = "Turn intentions into automatic daily streaks with zero cognitive overload, hydration tracking, and focus intervals.",
                        icon = Icons.Default.CheckCircle,
                        tags = listOf("Atomic Habits", "Daily Streaks", "Focus Timer", "Hydration")
                    )
                    2 -> ShowcaseSlide(
                        badge = "INTELLIGENT COACH",
                        badgeColor = DimensionColors.MentalHealth,
                        title = "Daily Clarity & Reflection",
                        description = "Receive personalized morning insights, mindful summaries, and clear action plans tailored to your life goals.",
                        icon = Icons.Default.Psychology,
                        tags = listOf("Daily Briefing", "AI Reflections", "Audio Summaries")
                    )
                }
            }

            Spacer(Modifier.height(Space.xs))

            // Carousel Page Indicator Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val isSelected = pagerState.currentPage == index
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 6.dp,
                        animationSpec = tween(durationMillis = 300),
                        label = "indicatorWidth"
                    )
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(width)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (isSelected) Color(0xFFD4A24C) else if (isDark) Color(0x33FBF8F3) else Color(0x2219181F)
                            )
                            .clickable {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                    )
                }
            }

            Spacer(Modifier.height(Space.lg))

            // 1-Tap Quick Setup Card (No boring questionnaire!)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = cardBackground,
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Space.md),
                    verticalArrangement = Arrangement.spacedBy(Space.sm)
                ) {
                    Text(
                        text = "Tailor Your Experience",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Text(
                        text = "Select your primary focus to personalize your dashboard:",
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecondary
                    )

                    Spacer(Modifier.height(Space.xxs))

                    // 4 Grid Choices
                    Column(verticalArrangement = Arrangement.spacedBy(Space.xs)) {
                        focusOptions.forEachIndexed { index, option ->
                            val isSelected = selectedFocusIndex == index
                            val optionBg = if (isSelected) {
                                option.accentColor.copy(alpha = if (isDark) 0.18f else 0.12f)
                            } else {
                                if (isDark) Color(0xFF1F1E2E) else Color(0xFFF6F4EF)
                            }
                            val optionBorder = if (isSelected) {
                                option.accentColor
                            } else {
                                if (isDark) Color(0x1FFFFFFF) else Color(0x1A000000)
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = optionBg,
                                border = BorderStroke(1.dp, optionBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedFocusIndex = index }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = Space.md, vertical = Space.sm),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Space.md)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = option.accentColor.copy(alpha = if (isDark) 0.22f else 0.16f),
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = option.icon,
                                                contentDescription = option.title,
                                                tint = option.accentColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = option.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) option.accentColor else textPrimary
                                        )
                                        Text(
                                            text = option.subtitle,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isSelected) {
                                                if (isDark) Color(0xFFFBF8F3).copy(alpha = 0.85f) else Color(0xFF19181F).copy(alpha = 0.85f)
                                            } else textSecondary
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = option.accentColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                }
            }

            Spacer(Modifier.height(Space.lg))

            // Bottom Actions: Create Account, Sign In & Guest Mode
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Space.sm)
            ) {
                // Primary: Create Account
                Button(
                    onClick = {
                        val chosenFocus = focusOptions[selectedFocusIndex].title
                        onCreateAccount(chosenFocus)
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD4A24C),
                        contentColor = Color(0xFF0C0B12)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Space.xs)
                    ) {
                        Text(
                            "Create Account",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Secondary: Sign In
                OutlinedButton(
                    onClick = onSignIn,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    border = BorderStroke(1.dp, if (isDark) Color(0x33D4A24C) else Color(0x66D4A24C)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = textPrimary
                    )
                ) {
                    Text(
                        "Sign In with Existing Account",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Tertiary: Continue as Guest
                TextButton(
                    onClick = {
                        val chosenFocus = focusOptions[selectedFocusIndex].title
                        onContinueAsGuest(chosenFocus)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Continue as Guest",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF9E958B)
                    )
                }

                Spacer(Modifier.height(Space.xs))
            }
        }
    }
}

@Composable
private fun ShowcaseSlide(
    badge: String,
    badgeColor: Color,
    title: String,
    description: String,
    icon: ImageVector,
    tags: List<String>
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val slideBg = if (isDark) Color(0xFF161522) else Color(0xFFFFFFFF)
    val slideBorder = if (isDark) Color(0x1FD4A24C) else Color(0x33D4A24C)
    val textTitle = if (isDark) Color(0xFFFBF8F3) else Color(0xFF19181F)
    val textDesc = if (isDark) Color(0xFFFBF8F3).copy(alpha = 0.75f) else Color(0xFF4A453E)
    val tagBg = if (isDark) Color(0x0DFBF8F3) else Color(0xFFF4F0EB)
    val tagBorder = if (isDark) Color(0x14FBF8F3) else Color(0x1A000000)
    val tagText = if (isDark) Color(0xFFFBF8F3).copy(alpha = 0.8f) else Color(0xFF575249)

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = slideBg,
        border = BorderStroke(1.dp, slideBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Space.xs)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Space.md),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor.copy(alpha = if (isDark) 0.15f else 0.12f),
                    border = BorderStroke(1.dp, badgeColor.copy(alpha = if (isDark) 0.3f else 0.25f))
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = badgeColor.copy(alpha = if (isDark) 0.15f else 0.12f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(Space.xxs)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = textTitle
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = textDesc,
                    lineHeight = 18.sp
                )
            }

            // Tag Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                tags.take(4).forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = tagBg,
                        border = BorderStroke(1.dp, tagBorder)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = tagText,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
