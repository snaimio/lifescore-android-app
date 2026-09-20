package com.lifescore.app.presentation.ui.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.R
import com.lifescore.app.core.designsystem.LifeGradients
import com.lifescore.app.core.designsystem.Space
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onComplete: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    var startAnimation by remember { mutableStateOf(false) }
    val entryScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "entryScale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2200)
        onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LifeGradients.HeroDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(Space.xl)
        ) {
            // Sunrise Arc Brand Mark Halo
            Surface(
                shape = CircleShape,
                color = Color(0x15D4A24C),
                modifier = Modifier
                    .size(130.dp)
                    .scale(pulseScale * entryScale)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0x303D3A8C),
                        modifier = Modifier.size(96.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(R.drawable.lifescore_logo),
                                contentDescription = "LifeScore Brand Logo",
                                modifier = Modifier.size(72.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(Space.xl))

            Text(
                "LifeScore",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFFFBF8F3),
                letterSpacing = 1.5.sp
            )

            Spacer(Modifier.height(Space.xs))

            Text(
                "Architect the life you're meant to live.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFD4A24C),
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(Space.xxxl))

            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color(0xFFE08556),
                strokeWidth = 2.dp
            )
        }
    }
}
