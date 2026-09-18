package com.lifescore.app.presentation.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.R
import com.lifescore.app.core.designsystem.LifeGradients
import com.lifescore.app.core.designsystem.LifeScoreShapes
import com.lifescore.app.core.designsystem.Space
import com.lifescore.app.core.designsystem.components.CardVariant
import com.lifescore.app.core.designsystem.components.LifeCard
import com.lifescore.app.core.designsystem.components.LifeIcon
import com.lifescore.app.core.designsystem.components.LifeIcons

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onSignIn: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LifeGradients.HeroDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Space.screenH, vertical = Space.lg)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(Space.sm))

            // Brand Mark Hero
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = CircleShape,
                    color = Color(0x15D4A24C),
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(R.drawable.ic_launcher_foreground),
                            contentDescription = "LifeScore Brand Mark",
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                Spacer(Modifier.height(Space.md))

                Text(
                    "Discover the life you're meant to live.",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFFFBF8F3),
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp
                )

                Spacer(Modifier.height(Space.xs))

                Text(
                    "Uncover your archetype, balance 8 core dimensions, and build compounding daily momentum.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFFBF8F3).copy(alpha = 0.75f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }

            // 3 Pillar Highlights Card
            LifeCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Space.md),
                variant = CardVariant.Default
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Space.md)
                ) {
                    ValuePropRow(
                        icon = LifeIcons.Star,
                        title = "Archetype Identity",
                        desc = "Discover your core operating strengths, blind spots, and natural superpowers."
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    ValuePropRow(
                        icon = LifeIcons.Analytics,
                        title = "360° Life Matrix",
                        desc = "Track holistic balance across health, career, wealth, and mental peace."
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    ValuePropRow(
                        icon = LifeIcons.Streak,
                        title = "Compounding Daily Habits",
                        desc = "Turn micro-intentions into streak momentum and character progression."
                    )
                }
            }

            // Bottom Actions: Single Primary Button
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Space.xs)
            ) {
                Button(
                    onClick = onGetStarted,
                    shape = LifeScoreShapes.button,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        "Get Started (60 Seconds) →",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = onSignIn,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Already have an account? Sign In",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFFBF8F3).copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ValuePropRow(
    icon: LifeIcons,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = LifeScoreShapes.button,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                LifeIcon(icon = icon, size = 22.dp)
            }
        }
        Spacer(Modifier.width(Space.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}
