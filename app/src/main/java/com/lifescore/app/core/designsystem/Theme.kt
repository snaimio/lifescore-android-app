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

object LifeScoreColors {
    val Primary = Color(0xFFD4A24C)
    val PrimaryLight = Color(0xFFE5B869)
    val PrimaryDark = Color(0xFFB38230)
    val PrimaryContainer = Color(0xFF2E2413)
    val OnPrimaryContainer = Color(0xFFFDE68A)

    val Secondary = Accent.Secondary
    val SecondaryLight = Color(0xFFF0B49C)
    val SecondaryDark = Color(0xFF4A2618)
    val SecondaryContainer = Color(0xFF38231E)
    val OnSecondaryContainer = Color(0xFFF0B49C)

    val Tertiary = Accent.Tertiary
    val TertiaryLight = Color(0xFFA8D5CB)
    val TertiaryDark = Color(0xFF1A3D37)
    val TertiaryContainer = Color(0xFF1E3531)
    val OnTertiaryContainer = Color(0xFFA8D5CB)

    val Surface = Color(0xFF14131E)
    val SurfaceVariant = Color(0xFF1D1B2B)
    val Background = Color(0xFF0C0B12)
    val OnBackground = Color(0xFFF6F4F0)
    val OnSurface = Color(0xFFF6F4F0)
    val Outline = Color(0xFF2E2B3E)
    val OutlineVariant = Color(0xFF222030)

    val Error = Accent.Error
    val Success = Accent.Success
    val Warning = Accent.Warning
    val Info = DimensionColors.Career

    val DarkBackground = Color(0xFF0C0B12)
    val DarkSurface = Color(0xFF14131E)
    val DarkSurfaceVariant = Color(0xFF1D1B2B)
    val DarkOnBackground = Color(0xFFF6F4F0)
    val DarkOnSurface = Color(0xFFF6F4F0)
    val DarkOutline = Color(0xFF2E2B3E)
    val DarkOutlineVariant = Color(0xFF222030)

    val PrimaryGradient = listOf(Color(0xFFD4A24C), Color(0xFFE5B869))
    val HeroGradientLight = listOf(Color(0xFF181524), Color(0xFF221E33), Color(0xFF2A243D))
    val HeroGradientDark = listOf(Color(0xFF14121E), Color(0xFF1D1A2A), Color(0xFF262238))
    val GoldGradient = listOf(Color(0xFFD4A24C), Color(0xFFE5B869))
    val EmeraldGradient = listOf(Color(0xFF6BA89C), Color(0xFF52877D))
}

@Composable
fun LifeScoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = android.graphics.Color.TRANSPARENT
                window.navigationBarColor = android.graphics.Color.TRANSPARENT
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = LifeScoreTypography,
        shapes = LifeScoreShapes.toMaterialShapes(),
        content = content
    )
}
