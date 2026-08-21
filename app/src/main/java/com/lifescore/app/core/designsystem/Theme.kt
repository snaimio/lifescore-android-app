package com.lifescore.app.core.designsystem

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 🎨 Complete Premium Material 3 Color System with AMOLED True Black & Dynamic Color
object LifeScoreColors {
    // Primary palette
    val Primary = md_theme_light_primary
    val PrimaryLight = Color(0xFF8B83FF)
    val PrimaryDark = Color(0xFF4834D4)
    val PrimaryContainer = md_theme_light_primaryContainer
    val OnPrimaryContainer = md_theme_light_onPrimaryContainer

    // Secondary palette
    val Secondary = md_theme_light_secondary
    val SecondaryLight = Color(0xFF66FFF0)
    val SecondaryDark = Color(0xFF00A896)
    val SecondaryContainer = md_theme_light_secondaryContainer
    val OnSecondaryContainer = md_theme_light_onSecondaryContainer

    // Tertiary palette
    val Tertiary = md_theme_light_tertiary
    val TertiaryLight = Color(0xFFFFD54F)
    val TertiaryDark = Color(0xFFF57C00)
    val TertiaryContainer = md_theme_light_tertiaryContainer
    val OnTertiaryContainer = md_theme_light_onTertiaryContainer

    // Light Neutral palette
    val Surface = md_theme_light_surface
    val SurfaceVariant = md_theme_light_surfaceVariant
    val Background = md_theme_light_background
    val OnBackground = md_theme_light_onBackground
    val OnSurface = md_theme_light_onSurface
    val Outline = md_theme_light_outline
    val OutlineVariant = md_theme_light_outlineVariant

    val Error = md_theme_light_error
    val Success = Color(0xFF10B981)
    val Warning = Color(0xFFF59E0B)
    val Info = Color(0xFF3B82F6)

    // AMOLED True Black palette
    val DarkBackground = md_theme_dark_background
    val DarkSurface = md_theme_dark_surface
    val DarkSurfaceVariant = md_theme_dark_surfaceVariant
    val DarkOnBackground = md_theme_dark_onBackground
    val DarkOnSurface = md_theme_dark_onSurface
    val DarkOutline = md_theme_dark_outline
    val DarkOutlineVariant = md_theme_dark_outlineVariant

    // Premium Gradients
    val PrimaryGradient = listOf(Primary, Color(0xFF00CEC9))
    val HeroGradient = listOf(Color(0xFF6750A4), Color(0xFF4834D4), Color(0xFF00CEC9))
    val GoldGradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706))
    val EmeraldGradient = listOf(Color(0xFF10B981), Color(0xFF059669))
    val DarkCardGradient = listOf(Color(0xFF1E1B24), Color(0xFF141218))
}

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    onError = md_theme_light_onError,
    errorContainer = md_theme_light_errorContainer,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    outlineVariant = md_theme_light_outlineVariant,
    inverseSurface = md_theme_dark_surface,
    inverseOnSurface = md_theme_dark_onSurface,
    inversePrimary = md_theme_dark_primary
)

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    onError = md_theme_dark_onError,
    errorContainer = md_theme_dark_errorContainer,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    outlineVariant = md_theme_dark_outlineVariant,
    inverseSurface = md_theme_light_surface,
    inverseOnSurface = md_theme_light_onSurface,
    inversePrimary = md_theme_light_primary
)

@Composable
fun LifeScoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Material You dynamic colors for Android 12+
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
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
