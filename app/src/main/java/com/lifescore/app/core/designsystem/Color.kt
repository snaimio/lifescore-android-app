package com.lifescore.app.core.designsystem

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ──────────────────────────────────────────────
// VIBRANT LIGHT COLORS (DEFAULT)
// ──────────────────────────────────────────────
val md_theme_light_primary = Color(0xFF6750A4)          // Deep Purple
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFF0E6FF)   // Soft purple bg
val md_theme_light_onPrimaryContainer = Color(0xFF21005D)

val md_theme_light_secondary = Color(0xFF006A6A)        // Vibrant Teal
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFE0F7F4) // Soft teal bg
val md_theme_light_onSecondaryContainer = Color(0xFF004D40)

val md_theme_light_tertiary = Color(0xFFD45A2E)         // Warm Orange
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFFFE8D6)
val md_theme_light_onTertiaryContainer = Color(0xFF3E1A00)

val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onErrorContainer = Color(0xFF410002)

val md_theme_light_background = Color(0xFFF5F5FA)       // Light lavender-white
val md_theme_light_onBackground = Color(0xFF1C1B1F)     // Dark text
val md_theme_light_surface = Color(0xFFFDFDFF)          // Crisp white surface
val md_theme_light_onSurface = Color(0xFF1C1B1F)
val md_theme_light_surfaceVariant = Color(0xFFF0EEF5)   // Subtle light gray-purple
val md_theme_light_onSurfaceVariant = Color(0xFF49454F)

val md_theme_light_outline = Color(0xFFC8C4D0)
val md_theme_light_outlineVariant = Color(0xFFE2DDE8)

// ──────────────────────────────────────────────
// SAMSUNG AMOLED TRUE BLACK DARK COLORS
// ──────────────────────────────────────────────
val md_theme_dark_primary = Color(0xFFD0BCFF)
val md_theme_dark_onPrimary = Color(0xFF381E72)
val md_theme_dark_primaryContainer = Color(0xFF4F378B)
val md_theme_dark_onPrimaryContainer = Color(0xFFEADDFF)

val md_theme_dark_secondary = Color(0xFFCCC2DC)
val md_theme_dark_onSecondary = Color(0xFF332D41)
val md_theme_dark_secondaryContainer = Color(0xFF4A4458)
val md_theme_dark_onSecondaryContainer = Color(0xFFE8DEF8)

val md_theme_dark_tertiary = Color(0xFFEFB8C8)
val md_theme_dark_onTertiary = Color(0xFF492532)
val md_theme_dark_tertiaryContainer = Color(0xFF633B48)
val md_theme_dark_onTertiaryContainer = Color(0xFFFFD8E4)

val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)

// True AMOLED Black
val md_theme_dark_background = Color(0xFF000000)
val md_theme_dark_onBackground = Color(0xFFE6E1E5)
val md_theme_dark_surface = Color(0xFF141218)
val md_theme_dark_onSurface = Color(0xFFE6E1E5)
val md_theme_dark_surfaceVariant = Color(0xFF2B2930)
val md_theme_dark_onSurfaceVariant = Color(0xFFCAC4D0)
val md_theme_dark_outline = Color(0xFF938F99)
val md_theme_dark_outlineVariant = Color(0xFF49454F)

// ──────────────────────────────────────────────
// Dimension Base Colors
// ──────────────────────────────────────────────
val ColorHealth = Color(0xFF4CAF50)
val ColorWealth = Color(0xFFFFA726)
val ColorRelationships = Color(0xFFEC4899)
val ColorCareer = Color(0xFF3B82F6)
val ColorLearning = Color(0xFF8B5CF6)
val ColorFitness = Color(0xFFF97316)
val ColorMentalHealth = Color(0xFF06B6D4)
val ColorSocialLife = Color(0xFF10B981)

val GoldAccent = Color(0xFFFFD700)
val Success = Color(0xFF10B981)
val Warning = Color(0xFFF59E0B)
val Info = Color(0xFF3B82F6)

// ──────────────────────────────────────────────
// Glassmorphism Brushes
// ──────────────────────────────────────────────
val GlassFillLight = Brush.verticalGradient(
    colors = listOf(
        Color.White.copy(alpha = 0.90f),
        Color.White.copy(alpha = 0.75f)
    )
)

val GlassBorderLight = Color(0xFFE8E5F0).copy(alpha = 0.8f)

val GlassFillDark = Brush.verticalGradient(
    colors = listOf(
        Color.White.copy(alpha = 0.08f),
        Color.White.copy(alpha = 0.02f)
    )
)

val GlassBorderDark = Color.White.copy(alpha = 0.1f)
