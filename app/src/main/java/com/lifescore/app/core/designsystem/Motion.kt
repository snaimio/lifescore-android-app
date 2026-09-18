package com.lifescore.app.core.designsystem

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

// ============================================
// LIFESCORE — Motion & Spring Physics
// ============================================

object Motion {
    val Snappy = spring<Float>(
        dampingRatio = 0.75f,
        stiffness = 400f
    )
    val Gentle = spring<Float>(
        dampingRatio = 0.85f,
        stiffness = 200f
    )
    val Bouncy = spring<Float>(
        dampingRatio = 0.55f,
        stiffness = 300f
    )

    const val Fast = 150
    const val Medium = 250
    const val Slow = 400
}
