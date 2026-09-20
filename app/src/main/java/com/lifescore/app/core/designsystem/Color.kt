package com.lifescore.app.core.designsystem

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ============================================
// LIFESCORE — Warm Editorial Palette
// ============================================

// The 8 Dimensions — each with a signature warm, editorial color
object DimensionColors {
    val Health = Color(0xFFE85D5D)          // Warm coral red
    val Wealth = Color(0xFFD4A24C)          // Muted gold
    val Relationships = Color(0xFFD97757)   // Soft terracotta
    val Career = Color(0xFF5B7BA8)          // Dusty navy
    val Learning = Color(0xFF7B6BA8)        // Muted violet
    val Fitness = Color(0xFFE08556)         // Warm amber
    val MentalHealth = Color(0xFF6BA89C)    // Soft teal
    val SocialLife = Color(0xFF9C8A6B)      // Warm taupe

    fun forDimension(dim: com.lifescore.app.domain.model.DimensionType): Color = when (dim) {
        com.lifescore.app.domain.model.DimensionType.HEALTH -> Health
        com.lifescore.app.domain.model.DimensionType.WEALTH -> Wealth
        com.lifescore.app.domain.model.DimensionType.RELATIONSHIPS -> Relationships
        com.lifescore.app.domain.model.DimensionType.CAREER -> Career
        com.lifescore.app.domain.model.DimensionType.LEARNING -> Learning
        com.lifescore.app.domain.model.DimensionType.FITNESS -> Fitness
        com.lifescore.app.domain.model.DimensionType.MENTAL_HEALTH -> MentalHealth
        com.lifescore.app.domain.model.DimensionType.SOCIAL_LIFE -> SocialLife
    }
}

// Neutral foundation — warm, intentional, not cold gray
object Neutrals {
    val Ink_900 = Color(0xFF0F0E0C)         // Near-black, warm
    val Ink_800 = Color(0xFF1C1A17)
    val Ink_700 = Color(0xFF2A2622)
    val Ink_600 = Color(0xFF3D3733)
    val Ink_500 = Color(0xFF57504A)
    val Ink_400 = Color(0xFF7A7269)
    val Ink_300 = Color(0xFF9E958B)
    val Ink_200 = Color(0xFFC4BAB0)
    val Ink_100 = Color(0xFFE5DDD5)
    val Ink_50  = Color(0xFFF5F0EA)
    val Paper   = Color(0xFFFBF8F3)         // Off-white, warm cream
    val White   = Color(0xFFFFFFFF)
}

// Accent — signature gradient & state accents
object Accent {
    val Primary = Color(0xFFD4A24C)          // Champagne gold
    val PrimaryLight = Color(0xFFE5B869)
    val PrimaryDark = Color(0xFFB38230)
    val Secondary = Color(0xFFD97757)        // Warm terracotta
    val Tertiary = Color(0xFF439386)         // Forest emerald
    val Success = Color(0xFF2E7D5B)          // Growth emerald
    val Warning = Color(0xFFD4A24C)
    val Error = Color(0xFFC9553D)
}

// Signature Gradients
object LifeGradients {
    val HeroDark = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0C0B12),      // Obsidian black
            Color(0xFF14241D),      // Deep forest emerald
            Color(0xFF1D3B2F)       // Rich emerald
        )
    )

    val CardMesh = Brush.linearGradient(
        colors = listOf(
            Color(0xFF12111D),
            Color(0xFF18231E),
            Color(0xFF22352B)
        )
    )

    val Gold = Brush.linearGradient(
        colors = listOf(
            Color(0xFFD4A24C),
            Color(0xFFE5B869)
        )
    )

    val GlassWarm = Brush.verticalGradient(
        colors = listOf(
            Neutrals.White.copy(alpha = 0.95f),
            Neutrals.Paper.copy(alpha = 0.85f)
        )
    )
}

// Dimension Pastels for Colorful Light Mode Cards
object DimensionPastels {
    val Health = Color(0xFFFFF2F2)
    val HealthBorder = Color(0xFFFDCACA)
    
    val Wealth = Color(0xFFFFF9EB)
    val WealthBorder = Color(0xFFFDE68A)
    
    val Relationships = Color(0xFFFFF4EE)
    val RelationshipsBorder = Color(0xFFFED7AA)
    
    val Career = Color(0xFFF1F6FD)
    val CareerBorder = Color(0xFFBFDBFE)
    
    val Learning = Color(0xFFF6F3FA)
    val LearningBorder = Color(0xFFDDD6FE)
    
    val Fitness = Color(0xFFFFF6ED)
    val FitnessBorder = Color(0xFFFED7AA)
    
    val MentalHealth = Color(0xFFF0FAF7)
    val MentalHealthBorder = Color(0xFFA7F3D0)
    
    val SocialLife = Color(0xFFF9F7F2)
    val SocialLifeBorder = Color(0xFFE5DDD5)
    
    fun backgroundFor(dim: com.lifescore.app.domain.model.DimensionType): Color = when (dim) {
        com.lifescore.app.domain.model.DimensionType.HEALTH -> Health
        com.lifescore.app.domain.model.DimensionType.WEALTH -> Wealth
        com.lifescore.app.domain.model.DimensionType.RELATIONSHIPS -> Relationships
        com.lifescore.app.domain.model.DimensionType.CAREER -> Career
        com.lifescore.app.domain.model.DimensionType.LEARNING -> Learning
        com.lifescore.app.domain.model.DimensionType.FITNESS -> Fitness
        com.lifescore.app.domain.model.DimensionType.MENTAL_HEALTH -> MentalHealth
        com.lifescore.app.domain.model.DimensionType.SOCIAL_LIFE -> SocialLife
    }
    
    fun borderFor(dim: com.lifescore.app.domain.model.DimensionType): Color = when (dim) {
        com.lifescore.app.domain.model.DimensionType.HEALTH -> HealthBorder
        com.lifescore.app.domain.model.DimensionType.WEALTH -> WealthBorder
        com.lifescore.app.domain.model.DimensionType.RELATIONSHIPS -> RelationshipsBorder
        com.lifescore.app.domain.model.DimensionType.CAREER -> CareerBorder
        com.lifescore.app.domain.model.DimensionType.LEARNING -> LearningBorder
        com.lifescore.app.domain.model.DimensionType.FITNESS -> FitnessBorder
        com.lifescore.app.domain.model.DimensionType.MENTAL_HEALTH -> MentalHealthBorder
        com.lifescore.app.domain.model.DimensionType.SOCIAL_LIFE -> SocialLifeBorder
    }
}

// Light theme — Vibrant Champagne Gold & Colorful Editorial Cards
val LightColorScheme = lightColorScheme(
    primary = Color(0xFFD4A24C),
    onPrimary = Color(0xFF1B1408),
    primaryContainer = Color(0xFFFFF3DB),
    onPrimaryContainer = Color(0xFF7A510A),
    
    secondary = Color(0xFFD97757),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFEECE4),
    onSecondaryContainer = Color(0xFF7C2D12),
    
    tertiary = Color(0xFF439386),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE2F7F2),
    onTertiaryContainer = Color(0xFF0D5347),
    
    background = Color(0xFFF9F8F5),
    onBackground = Color(0xFF19181F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF19181F),
    surfaceVariant = Color(0xFFF2EFE9),
    onSurfaceVariant = Color(0xFF6B6678),
    
    outline = Color(0xFFE2DDD5),
    outlineVariant = Color(0xFFEBE6DD),
    
    error = Color(0xFFE85D5D),
    onError = Color(0xFFFFFFFF)
)

// Dark theme — Luxury Obsidian & Champagne Gold
val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD4A24C),             // Champagne Gold
    onPrimary = Color(0xFF1B1408),
    primaryContainer = Color(0xFF2E2413),
    onPrimaryContainer = Color(0xFFFDE68A),
    
    secondary = Color(0xFFD97757),           // Warm terracotta
    onSecondary = Color(0xFF2C130B),
    secondaryContainer = Color(0xFF38231E),
    onSecondaryContainer = Color(0xFFF0B49C),
    
    tertiary = Color(0xFF6BA89C),            // Sage teal
    onTertiary = Color(0xFF0F2622),
    tertiaryContainer = Color(0xFF1E3531),
    onTertiaryContainer = Color(0xFFA8D5CB),
    
    background = Color(0xFF0C0B12),          // Obsidian Luxury Deep Black
    onBackground = Color(0xFFF6F4F0),        // Crisp Oyster White
    surface = Color(0xFF14131E),             // Frosted Card Surface
    onSurface = Color(0xFFF6F4F0),
    surfaceVariant = Color(0xFF1D1B2B),      // Elevated Card Surface
    onSurfaceVariant = Color(0xFF9E9AA8),    // Soft Muted Lavender-Gray
    
    outline = Color(0xFF2E2B3E),
    outlineVariant = Color(0xFF222030),
    
    error = Color(0xFFE85D5D),
    onError = Color(0xFF3B0F0F)
)

// ──────────────────────────────────────────────
// Compatibility Aliases for Existing Design Tokens
// ──────────────────────────────────────────────
val ColorHealth = DimensionColors.Health
val ColorWealth = DimensionColors.Wealth
val ColorRelationships = DimensionColors.Relationships
val ColorCareer = DimensionColors.Career
val ColorLearning = DimensionColors.Learning
val ColorFitness = DimensionColors.Fitness
val ColorMentalHealth = DimensionColors.MentalHealth
val ColorSocialLife = DimensionColors.SocialLife

val GoldAccent = DimensionColors.Wealth
val Success = Accent.Success
val Warning = Accent.Warning
val Info = DimensionColors.Career

val GlassFillLight = Brush.verticalGradient(
    colors = listOf(
        Neutrals.White.copy(alpha = 0.95f),
        Neutrals.Paper.copy(alpha = 0.88f)
    )
)

val GlassBorderLight = Neutrals.Ink_100.copy(alpha = 0.8f)

val GlassFillDark = Brush.verticalGradient(
    colors = listOf(
        Color.White.copy(alpha = 0.08f),
        Color.White.copy(alpha = 0.03f)
    )
)

val GlassBorderDark = Color.White.copy(alpha = 0.1f)

val md_theme_light_primary = Accent.Primary
val md_theme_light_onPrimary = Neutrals.White
val md_theme_light_primaryContainer = Color(0xFFFFF3DB)
val md_theme_light_onPrimaryContainer = Color(0xFF7A510A)
val md_theme_light_secondary = Accent.Secondary
val md_theme_light_onSecondary = Neutrals.White
val md_theme_light_secondaryContainer = Color(0xFFFEECE4)
val md_theme_light_onSecondaryContainer = Color(0xFF7C2D12)
val md_theme_light_tertiary = Accent.Tertiary
val md_theme_light_onTertiary = Neutrals.White
val md_theme_light_tertiaryContainer = Color(0xFFE2F7F2)
val md_theme_light_onTertiaryContainer = Color(0xFF0D5347)
val md_theme_light_error = Accent.Error
val md_theme_light_onError = Neutrals.White
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Neutrals.Paper
val md_theme_light_onBackground = Neutrals.Ink_900
val md_theme_light_surface = Neutrals.White
val md_theme_light_onSurface = Neutrals.Ink_900
val md_theme_light_surfaceVariant = Neutrals.Ink_50
val md_theme_light_onSurfaceVariant = Neutrals.Ink_500
val md_theme_light_outline = Neutrals.Ink_200
val md_theme_light_outlineVariant = Neutrals.Ink_100

val md_theme_dark_primary = Color(0xFFD4A24C)
val md_theme_dark_onPrimary = Color(0xFF1B1408)
val md_theme_dark_primaryContainer = Color(0xFF2E2413)
val md_theme_dark_onPrimaryContainer = Color(0xFFFDE68A)
val md_theme_dark_secondary = Color(0xFFF0B49C)
val md_theme_dark_onSecondary = Color(0xFF4A2618)
val md_theme_dark_secondaryContainer = Color(0xFF6B3E28)
val md_theme_dark_onSecondaryContainer = Color(0xFFF5E5DC)
val md_theme_dark_tertiary = Color(0xFFA8D5CB)
val md_theme_dark_onTertiary = Color(0xFF1A3D37)
val md_theme_dark_tertiaryContainer = Color(0xFF355E58)
val md_theme_dark_onTertiaryContainer = Color(0xFFDCF0EC)
val md_theme_dark_error = Color(0xFFE89B8A)
val md_theme_dark_onError = Color(0xFF5A1F12)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = Color(0xFF0E0D0C)
val md_theme_dark_onBackground = Color(0xFFEDE7DF)
val md_theme_dark_surface = Color(0xFF171614)
val md_theme_dark_onSurface = Color(0xFFEDE7DF)
val md_theme_dark_surfaceVariant = Color(0xFF262421)
val md_theme_dark_onSurfaceVariant = Color(0xFFB8B0A7)
val md_theme_dark_outline = Color(0xFF3A3632)
val md_theme_dark_outlineVariant = Color(0xFF2A2724)
