package com.lifescore.app.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

object LifeScoreShapes {
    val None = RoundedCornerShape(0.dp)
    val ExtraSmall = RoundedCornerShape(4.dp)
    val Small = RoundedCornerShape(8.dp)
    val Medium = RoundedCornerShape(12.dp)
    val Large = RoundedCornerShape(16.dp)
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
