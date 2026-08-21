package com.lifescore.app.presentation.ui.home.components

import com.lifescore.app.domain.model.DimensionType

fun getDimensionEmoji(dimension: DimensionType): String {
    return when (dimension) {
        DimensionType.HEALTH -> "🏃"
        DimensionType.WEALTH -> "💰"
        DimensionType.RELATIONSHIPS -> "💖"
        DimensionType.CAREER -> "💼"
        DimensionType.LEARNING -> "🧠"
        DimensionType.FITNESS -> "⚡"
        DimensionType.MENTAL_HEALTH -> "🧘"
        DimensionType.SOCIAL_LIFE -> "🤝"
    }
}
