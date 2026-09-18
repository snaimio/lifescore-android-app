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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0E14))
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
                        color = Color(0xFFFBF8F3)
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
                                if (isSelected) Color(0xFFD4A24C) else Color(0x33FBF8F3)
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
                color = Color(0xFF161522),
                border = BorderStroke(1.dp, Color(0x1FD4A24C)),
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
                        color = Color(0xFFFBF8F3)
                    )
                    Text(
                        text = "Select your primary focus to personalize your dashboard:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9E958B)
                    )

                    Spacer(Modifier.height(Space.xxs))

                    // 4 Grid Choices
                    Column(verticalArrangement = Arrangement.spacedBy(Space.xs)) {
                        focusOptions.forEachIndexed { index, option ->
                            val isSelected = selectedFocusIndex == index
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) option.accentColor.copy(alpha = 0.18f) else Color(0xFF1F1E2E),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) option.accentColor else Color(0x1FFFFFFF)
                                ),
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
                                        color = option.accentColor.copy(alpha = 0.22f),
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
                                            color = if (isSelected) option.accentColor else Color(0xFFFBF8F3)
                                        )
                                        Text(
                                            text = option.subtitle,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isSelected) Color(0xFFFBF8F3).copy(alpha = 0.85f) else Color(0xFF9E958B)
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
                    border = BorderStroke(1.dp, Color(0x33D4A24C)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFBF8F3)
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
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF161522),
        border = BorderStroke(1.dp, Color(0x1FD4A24C)),
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
                    color = badgeColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.3f))
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
                    color = badgeColor.copy(alpha = 0.15f),
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
                    color = Color(0xFFFBF8F3)
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFBF8F3).copy(alpha = 0.75f),
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
                        color = Color(0x0DFBF8F3),
                        border = BorderStroke(1.dp, Color(0x14FBF8F3))
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFBF8F3).copy(alpha = 0.8f),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
