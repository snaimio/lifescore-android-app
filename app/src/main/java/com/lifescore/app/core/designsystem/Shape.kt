package com.lifescore.app.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ============================================
// LIFESCORE — Custom Intentional Shapes
// ============================================

object LifeScoreShapes {
    val extraSmall = RoundedCornerShape(6.dp)
    val small = RoundedCornerShape(10.dp)
    val medium = RoundedCornerShape(14.dp)
    val large = RoundedCornerShape(20.dp)
    val extraLarge = RoundedCornerShape(28.dp)
    val pill = RoundedCornerShape(999.dp)
    val full = RoundedCornerShape(9999.dp)
    val none = RoundedCornerShape(0.dp)

    // Semantic tokens
    val card = RoundedCornerShape(20.dp)
    val cardSmall = RoundedCornerShape(14.dp)
    val button = RoundedCornerShape(12.dp)
    val input = RoundedCornerShape(12.dp)
    val chip = RoundedCornerShape(10.dp)
    val tag = RoundedCornerShape(6.dp)
    val fab = RoundedCornerShape(16.dp)
    val sheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val dialog = RoundedCornerShape(24.dp)
    val modal = dialog

    fun toMaterialShapes(): Shapes {
        return Shapes(
            extraSmall = extraSmall,
            small = small,
            medium = medium,
            large = large,
            extraLarge = extraLarge
        )
    }
}
