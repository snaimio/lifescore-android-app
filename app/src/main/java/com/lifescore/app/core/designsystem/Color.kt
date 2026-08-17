package com.lifescore.app.core.designsystem

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ──────────────────────────────────────────────
// Brand & Core Tokens (Vibrant Palette)
// ──────────────────────────────────────────────
val md_theme_light_primary = Color(0xFF6C63FF)
val md_theme_light_secondary = Color(0xFF03DAC6)
val md_theme_light_tertiary = Color(0xFFBB86FC)
val md_theme_light_surface = Color(0xFFF8F9FE)
val md_theme_light_background = Color(0xFFFFFFFF)

val md_theme_dark_primary = Color(0xFF7C73FF)
val md_theme_dark_secondary = Color(0xFF03DAC6)
val md_theme_dark_tertiary = Color(0xFFBB86FC)
val md_theme_dark_surface = Color(0xFF1C1B1E)
val md_theme_dark_background = Color(0xFF141316)

// Supporting Brand Tokens
val Indigo50 = Color(0xFF6C63FF)
val Indigo80 = Color(0xFF7C73FF)
val IndigoContainer = Color(0xFFECEBFF)
val OnIndigoContainer = Color(0xFF1E1464)

val Teal40 = Color(0xFF03DAC6)
val Teal80 = Color(0xFF66FFF9)
val TealContainer = Color(0xFFD0F8F6)
val OnTealContainer = Color(0xFF003834)

val Amber40 = Color(0xFFFF9800)
val Amber80 = Color(0xFFFFB74D)
val AmberContainer = Color(0xFFFFF3E0)
val OnAmberContainer = Color(0xFFE65100)

val GoldAccent = Color(0xFFFFD700)
val Success = Color(0xFF10B981)
val ErrorLight = Color(0xFFEF4444)
val ErrorDark = Color(0xFFF87171)

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

// ──────────────────────────────────────────────
// Custom Gradients (Gamified Visuals)
// ──────────────────────────────────────────────
val scoreGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF6C63FF),
        Color(0xFF03DAC6)
    )
)

val heroCardGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF6C63FF),
        Color(0xFF4834D4),
        Color(0xFF03DAC6)
    )
)

val streakFlameGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFF5722),
        Color(0xFFFF9800),
        Color(0xFFFFEB3B)
    )
)

val dimensionGradients = mapOf(
    "Health" to Brush.linearGradient(listOf(Color(0xFF4CAF50), Color(0xFF8BC34A))),
    "Wealth" to Brush.linearGradient(listOf(Color(0xFFFFA726), Color(0xFFFF9800))),
    "Relationships" to Brush.linearGradient(listOf(Color(0xFFEC4899), Color(0xFFF43F5E))),
    "Career" to Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF0284C7))),
    "Learning" to Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6366F1))),
    "Fitness" to Brush.linearGradient(listOf(Color(0xFFF97316), Color(0xFFFB923C))),
    "Mental Health" to Brush.linearGradient(listOf(Color(0xFF06B6D4), Color(0xFF14B8A6))),
    "Social Life" to Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF34D399)))
)

// ──────────────────────────────────────────────
// Glassmorphism Brushes
// ──────────────────────────────────────────────
val GlassFillLight = Brush.verticalGradient(
    colors = listOf(
        Color.White.copy(alpha = 0.85f),
        Color.White.copy(alpha = 0.65f)
    )
)

val GlassFillDark = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF2D2B33).copy(alpha = 0.80f),
        Color(0xFF1F1E24).copy(alpha = 0.65f)
    )
)

val GlassBorderLight = Color.White.copy(alpha = 0.60f)
val GlassBorderDark = Color.White.copy(alpha = 0.12f)
