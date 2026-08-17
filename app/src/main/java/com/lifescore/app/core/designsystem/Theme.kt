package com.lifescore.app.core.designsystem

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// 🎨 Complete Premium Color System with AMOLED & Material You Support
object LifeScoreColors {
    // Primary palette (Vibrant Indigo/Purple)
    val Primary = Color(0xFF6C5CE7)
    val PrimaryLight = Color(0xFF8C7CFF)
    val PrimaryDark = Color(0xFF4834D4)
    val PrimaryContainer = Color(0xFFEDE9FE)
    val OnPrimaryContainer = Color(0xFF2E1065)

    // Secondary palette (Electric Emerald/Teal)
    val Secondary = Color(0xFF00CEC9)
    val SecondaryLight = Color(0xFF55EFC4)
    val SecondaryDark = Color(0xFF00B894)
    val SecondaryContainer = Color(0xFFCCFBF1)
    val OnSecondaryContainer = Color(0xFF042F2E)

    // Tertiary palette (Warm Amber / Rose)
    val Tertiary = Color(0xFFFF9F43)
    val TertiaryLight = Color(0xFFFECA57)
    val TertiaryDark = Color(0xFFEE5253)
    val TertiaryContainer = Color(0xFFFEF3C7)
    val OnTertiaryContainer = Color(0xFF78350F)

    // Light Neutral palette (Clean, Crisp, Elevated)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF1F3F9)
    val Background = Color(0xFFF7F8FC)
    val OnBackground = Color(0xFF0F172A)
    val OnSurface = Color(0xFF0F172A)
    val Outline = Color(0xFFCBD5E1)
    val OutlineVariant = Color(0xFFE2E8F0)

    val Error = Color(0xFFEF4444)
    val Success = Color(0xFF10B981)
    val Warning = Color(0xFFF59E0B)
    val Info = Color(0xFF3B82F6)

    // AMOLED Dark mode palette (Deep Obsidian & High-Contrast Cards)
    val DarkBackground = Color(0xFF0B0D14)
    val DarkSurface = Color(0xFF121520)
    val DarkSurfaceVariant = Color(0xFF1B2032)
    val DarkOnBackground = Color(0xFFF8FAFC)
    val DarkOnSurface = Color(0xFFF8FAFC)
    val DarkOutline = Color(0xFF334155)
    val DarkOutlineVariant = Color(0xFF1E293B)

    // Premium Gradients
    val PrimaryGradient = listOf(Primary, Secondary)
    val HeroGradient = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6), Color(0xFFEC4899))
    val GoldGradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706))
    val EmeraldGradient = listOf(Color(0xFF10B981), Color(0xFF059669))
    val DarkCardGradient = listOf(Color(0xFF161A29), Color(0xFF0F121C))
}

// 🔤 Compact & Scalable Typography System for Samsung S24 & Multi-Density
object LifeScoreTypography {
    fun getTypography(): Typography {
        return Typography(
            displayLarge = TextStyle(
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.25).sp,
                lineHeight = 54.sp
            ),
            displayMedium = TextStyle(
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                lineHeight = 44.sp
            ),
            displaySmall = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                lineHeight = 36.sp
            ),
            headlineLarge = TextStyle(
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                lineHeight = 32.sp
            ),
            headlineMedium = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                lineHeight = 28.sp
            ),
            headlineSmall = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.sp,
                lineHeight = 24.sp
            ),
            titleLarge = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                lineHeight = 24.sp
            ),
            titleMedium = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.1.sp,
                lineHeight = 20.sp
            ),
            titleSmall = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.1.sp,
                lineHeight = 18.sp
            ),
            bodyLarge = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.2.sp,
                lineHeight = 20.sp
            ),
            bodyMedium = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.2.sp,
                lineHeight = 18.sp
            ),
            bodySmall = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.3.sp,
                lineHeight = 15.sp
            ),
            labelLarge = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.1.sp,
                lineHeight = 18.sp
            ),
            labelMedium = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.3.sp,
                lineHeight = 15.sp
            ),
            labelSmall = TextStyle(
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.4.sp,
                lineHeight = 14.sp
            )
        )
    }
}

// 📐 Shape System
object LifeScoreShapes {
    val None = RoundedCornerShape(0.dp)
    val ExtraSmall = RoundedCornerShape(6.dp)
    val Small = RoundedCornerShape(10.dp)
    val Medium = RoundedCornerShape(14.dp)
    val Large = RoundedCornerShape(18.dp)
    val ExtraLarge = RoundedCornerShape(24.dp)
    val Full = RoundedCornerShape(9999.dp)

    val Card = RoundedCornerShape(18.dp)
    val Button = RoundedCornerShape(14.dp)
    val Chip = RoundedCornerShape(10.dp)
    val Dialog = RoundedCornerShape(24.dp)
    val BottomSheet = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

    fun toMaterialShapes(): Shapes {
        return Shapes(
            extraSmall = ExtraSmall,
            small = Small,
            medium = Medium,
            large = Large,
            extraLarge = ExtraLarge
        )
    }
}

// 🌓 Complete Theme
@Composable
fun LifeScoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep high contrast brand palette by default
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> {
            darkColorScheme(
                primary = LifeScoreColors.Primary,
                onPrimary = Color.White,
                primaryContainer = LifeScoreColors.PrimaryDark,
                onPrimaryContainer = Color.White,
                secondary = LifeScoreColors.Secondary,
                onSecondary = Color(0xFF042F2E),
                secondaryContainer = Color(0xFF064E3B),
                onSecondaryContainer = Color.White,
                tertiary = LifeScoreColors.Tertiary,
                onTertiary = Color.White,
                tertiaryContainer = LifeScoreColors.TertiaryDark,
                onTertiaryContainer = Color.White,
                error = LifeScoreColors.Error,
                onError = Color.White,
                background = LifeScoreColors.DarkBackground,
                onBackground = LifeScoreColors.DarkOnBackground,
                surface = LifeScoreColors.DarkSurface,
                onSurface = LifeScoreColors.DarkOnSurface,
                surfaceVariant = LifeScoreColors.DarkSurfaceVariant,
                onSurfaceVariant = Color(0xFF94A3B8),
                outline = LifeScoreColors.DarkOutline,
                outlineVariant = LifeScoreColors.DarkOutlineVariant,
                inverseSurface = Color.White,
                inverseOnSurface = Color(0xFF0F172A),
                inversePrimary = LifeScoreColors.PrimaryLight
            )
        }
        else -> {
            lightColorScheme(
                primary = LifeScoreColors.Primary,
                onPrimary = Color.White,
                primaryContainer = LifeScoreColors.PrimaryContainer,
                onPrimaryContainer = LifeScoreColors.OnPrimaryContainer,
                secondary = LifeScoreColors.SecondaryDark,
                onSecondary = Color.White,
                secondaryContainer = LifeScoreColors.SecondaryContainer,
                onSecondaryContainer = LifeScoreColors.OnSecondaryContainer,
                tertiary = LifeScoreColors.Tertiary,
                onTertiary = Color.White,
                tertiaryContainer = LifeScoreColors.TertiaryContainer,
                onTertiaryContainer = LifeScoreColors.OnTertiaryContainer,
                error = LifeScoreColors.Error,
                onError = Color.White,
                background = LifeScoreColors.Background,
                onBackground = LifeScoreColors.OnBackground,
                surface = LifeScoreColors.Surface,
                onSurface = LifeScoreColors.OnSurface,
                surfaceVariant = LifeScoreColors.SurfaceVariant,
                onSurfaceVariant = Color(0xFF64748B),
                outline = LifeScoreColors.Outline,
                outlineVariant = LifeScoreColors.OutlineVariant,
                inverseSurface = LifeScoreColors.DarkSurface,
                inverseOnSurface = Color.White,
                inversePrimary = LifeScoreColors.PrimaryLight
            )
        }
    }

    val typography = LifeScoreTypography.getTypography()
    val shapes = LifeScoreShapes.toMaterialShapes()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
