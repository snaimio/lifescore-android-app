package com.lifescore.app.presentation.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lifescore.app.core.designsystem.Spacing

@Composable
fun QuickActionsRow(
    onTasks: () -> Unit,
    onAI: () -> Unit,
    onLeaderboard: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        QuickActionButton(
            icon = Icons.Default.Checklist,
            label = "Tasks",
            onClick = onTasks,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            icon = Icons.Default.SmartToy,
            label = "AI Coach",
            onClick = onAI,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            icon = Icons.Default.Leaderboard,
            label = "Rankings",
            onClick = onLeaderboard,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
