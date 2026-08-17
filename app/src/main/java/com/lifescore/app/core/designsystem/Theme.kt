package com.lifescore.app.core.designsystem

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// 🎨 Complete Color System
object LifeScoreColors {
    // Primary palette (Purple)
    val Primary = Color(0xFF6C63FF)
    val PrimaryLight = Color(0xFF8B83FF)
    val PrimaryDark = Color(0xFF4A42D9)
    val PrimaryContainer = Color(0xFFE8E6FF)
    val OnPrimaryContainer = Color(0xFF2A1F7A)
    
    // Secondary palette (Teal)
    val Secondary = Color(0xFF03DAC6)
    val SecondaryLight = Color(0xFF66FFF0)
    val SecondaryDark = Color(0xFF00A896)
    val SecondaryContainer = Color(0xFFE0F7F4)
    val OnSecondaryContainer = Color(0xFF004D40)
    
    // Tertiary palette (Amber)
    val Tertiary = Color(0xFFFFB74D)
    val TertiaryLight = Color(0xFFFFD54F)
    val TertiaryDark = Color(0xFFF57C00)
    val TertiaryContainer = Color(0xFFFFF3E0)
    val OnTertiaryContainer = Color(0xFF4E2E00)
    
    // Neutral palette
    val Surface = Color(0xFFF8F9FA)
    val SurfaceVariant = Color(0xFFE9ECEF)
    val Background = Color(0xFFFFFFFF)
    val Error = Color(0xFFE53935)
    val Success = Color(0xFF43A047)
    val Warning = Color(0xFFFFB300)
    val Info = Color(0xFF1E88E5)
    
    // Dark mode neutral palette
    val DarkSurface = Color(0xFF1C1B1E)
    val DarkSurfaceVariant = Color(0xFF2D2B30)
    val DarkBackground = Color(0xFF141316)
    
    // Gradients
    val PrimaryGradient = listOf(Primary, PrimaryLight)
    val SecondaryGradient = listOf(Secondary, SecondaryLight)
    val HeroGradient = listOf(Primary, Secondary)
    val WarmGradient = listOf(Tertiary, TertiaryLight)
    val CoolGradient = listOf(Primary, Secondary)
    val DarkGradient = listOf(Color(0xFF2D2B30), Color(0xFF1C1B1E))
}

// 🔤 Typography System
object LifeScoreTypography {
    fun getTypography(): Typography {
        return Typography(
            displayLarge = TextStyle(
                fontSize = 57.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.25).sp,
                lineHeight = 64.sp
            ),
            displayMedium = TextStyle(
                fontSize = 45.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                lineHeight = 52.sp
            ),
            displaySmall = TextStyle(
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                lineHeight = 44.sp
            ),
            headlineLarge = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.sp,
                lineHeight = 40.sp
            ),
            headlineMedium = TextStyle(
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.sp,
                lineHeight = 36.sp
            ),
            headlineSmall = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.sp,
                lineHeight = 32.sp
            ),
            titleLarge = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.sp,
                lineHeight = 28.sp
            ),
            titleMedium = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.15.sp,
                lineHeight = 24.sp
            ),
            titleSmall = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.1.sp,
                lineHeight = 20.sp
            ),
            bodyLarge = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.5.sp,
                lineHeight = 24.sp
            ),
            bodyMedium = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.25.sp,
                lineHeight = 20.sp
            ),
            bodySmall = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.4.sp,
                lineHeight = 16.sp
            ),
            labelLarge = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.1.sp,
                lineHeight = 20.sp
            ),
            labelMedium = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp,
                lineHeight = 16.sp
            ),
            labelSmall = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp,
                lineHeight = 16.sp
            )
        )
    }
}

// 📐 Shape System
object LifeScoreShapes {
    val None = RoundedCornerShape(0.dp)
    val ExtraSmall = RoundedCornerShape(4.dp)
    val Small = RoundedCornerShape(8.dp)
    val Medium = RoundedCornerShape(12.dp)
    val Large = RoundedCornerShape(16.dp)
    val ExtraLarge = RoundedCornerShape(24.dp)
    val Full = RoundedCornerShape(9999.dp)
    
    val Card = RoundedCornerShape(16.dp)
    val Button = RoundedCornerShape(12.dp)
    val Chip = RoundedCornerShape(8.dp)
    val Dialog = RoundedCornerShape(28.dp)
    val BottomSheet = RoundedCornerShape(24.dp)

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
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = LifeScoreColors.Primary,
            onPrimary = Color.White,
            primaryContainer = LifeScoreColors.PrimaryContainer,
            onPrimaryContainer = LifeScoreColors.OnPrimaryContainer,
            secondary = LifeScoreColors.Secondary,
            onSecondary = Color.White,
            secondaryContainer = LifeScoreColors.SecondaryContainer,
            onSecondaryContainer = LifeScoreColors.OnSecondaryContainer,
            tertiary = LifeScoreColors.Tertiary,
            onTertiary = Color.White,
            tertiaryContainer = LifeScoreColors.TertiaryContainer,
            onTertiaryContainer = LifeScoreColors.OnTertiaryContainer,
            error = LifeScoreColors.Error,
            onError = Color.White,
            background = LifeScoreColors.DarkBackground,
            onBackground = Color.White,
            surface = LifeScoreColors.DarkSurface,
            onSurface = Color.White,
            surfaceVariant = LifeScoreColors.DarkSurfaceVariant,
            onSurfaceVariant = Color(0xFFCAC4D0),
            outline = Color(0xFF938F99),
            inverseSurface = Color.White,
            inverseOnSurface = Color.Black,
            inversePrimary = LifeScoreColors.Primary
        )
    } else {
        lightColorScheme(
            primary = LifeScoreColors.Primary,
            onPrimary = Color.White,
            primaryContainer = LifeScoreColors.PrimaryContainer,
            onPrimaryContainer = LifeScoreColors.OnPrimaryContainer,
            secondary = LifeScoreColors.Secondary,
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
            onBackground = Color.Black,
            surface = LifeScoreColors.Surface,
            onSurface = Color.Black,
            surfaceVariant = LifeScoreColors.SurfaceVariant,
            onSurfaceVariant = Color(0xFF49454F),
            outline = Color(0xFF79747E),
            inverseSurface = Color.Black,
            inverseOnSurface = Color.White,
            inversePrimary = LifeScoreColors.Primary
        )
    }
    
    val typography = LifeScoreTypography.getTypography()
    val shapes = LifeScoreShapes.toMaterialShapes()
    
    // Set edge-to-edge display
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
