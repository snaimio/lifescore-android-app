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

// 🎨 Complete Premium Editorial Material 3 Color System
object LifeScoreColors {
    val Primary = Accent.Primary
    val PrimaryLight = Accent.PrimaryLight
    val PrimaryDark = Accent.PrimaryDark
    val PrimaryContainer = Color(0xFFE6E5F5)
    val OnPrimaryContainer = Accent.PrimaryDark

    val Secondary = Accent.Secondary
    val SecondaryLight = Color(0xFFF0B49C)
    val SecondaryDark = Color(0xFF4A2618)
    val SecondaryContainer = Color(0xFFF5E5DC)
    val OnSecondaryContainer = Color(0xFF4A2618)

    val Tertiary = Accent.Tertiary
    val TertiaryLight = Color(0xFFA8D5CB)
    val TertiaryDark = Color(0xFF1A3D37)
    val TertiaryContainer = Color(0xFFDCF0EC)
    val OnTertiaryContainer = Color(0xFF1A3D37)

    val Surface = Neutrals.White
    val SurfaceVariant = Neutrals.Ink_50
    val Background = Neutrals.Paper
    val OnBackground = Neutrals.Ink_900
    val OnSurface = Neutrals.Ink_900
    val Outline = Neutrals.Ink_200
    val OutlineVariant = Neutrals.Ink_100

    val Error = Accent.Error
    val Success = Accent.Success
    val Warning = Accent.Warning
    val Info = DimensionColors.Career

    val DarkBackground = Color(0xFF0E0D0C)
    val DarkSurface = Color(0xFF171614)
    val DarkSurfaceVariant = Color(0xFF262421)
    val DarkOnBackground = Color(0xFFEDE7DF)
    val DarkOnSurface = Color(0xFFEDE7DF)
    val DarkOutline = Color(0xFF3A3632)
    val DarkOutlineVariant = Color(0xFF2A2724)

    val PrimaryGradient = listOf(Accent.Primary, Accent.PrimaryLight, Accent.Secondary)
    val HeroGradientLight = listOf(Color(0xFF2A2750), Color(0xFF3D3A8C), Color(0xFF6B4B8C))
    val HeroGradientDark = listOf(Color(0xFF1E1C38), Color(0xFF2D295C), Color(0xFF4A3B69))
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
