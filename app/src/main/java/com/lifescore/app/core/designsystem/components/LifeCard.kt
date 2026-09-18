package com.lifescore.app.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lifescore.app.core.designsystem.LifeScoreShapes
import com.lifescore.app.core.designsystem.Neutrals
import com.lifescore.app.core.designsystem.Space

enum class CardVariant {
    Default,
    Cream,
    Outlined,
    Primary
}

@Composable
fun LifeCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    variant: CardVariant = CardVariant.Default,
    contentPadding: PaddingValues = PaddingValues(horizontal = Space.cardH, vertical = Space.cardV),
    content: @Composable ColumnScope.() -> Unit
) {
    val bg = when (variant) {
        CardVariant.Default -> MaterialTheme.colorScheme.surface
        CardVariant.Cream -> MaterialTheme.colorScheme.surfaceVariant
        CardVariant.Outlined -> Color.Transparent
        CardVariant.Primary -> MaterialTheme.colorScheme.primaryContainer
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            ),
        shape = LifeScoreShapes.card,
        color = bg,
        border = if (variant == CardVariant.Outlined) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        } else {
            BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        },
        shadowElevation = if (variant == CardVariant.Default) 0.5.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}
