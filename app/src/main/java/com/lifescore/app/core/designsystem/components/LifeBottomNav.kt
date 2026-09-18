package com.lifescore.app.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.LifeScoreShapes
import com.lifescore.app.core.designsystem.Motion
import com.lifescore.app.core.designsystem.Space
import com.lifescore.app.presentation.navigation.Screen

@Composable
fun LifeBottomNav(
    selectedRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = Space.sm, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LifeNavItem("today", "Today", Icons.Outlined.WbSunny, selectedRoute, onNavigate)
            LifeNavItem("balance", "Balance", Icons.Outlined.PieChart, selectedRoute, onNavigate)
            LifeNavItem("grow", "Grow", Icons.AutoMirrored.Filled.TrendingUp, selectedRoute, onNavigate)
            LifeNavItem("me", "Me", Icons.Outlined.Person, selectedRoute, onNavigate)
            LifeNavItem("explore", "Explore", Icons.Outlined.Explore, selectedRoute, onNavigate)
        }
    }
}

@Composable
private fun LifeNavItem(
    route: String,
    label: String,
    icon: ImageVector,
    selectedRoute: String?,
    onNavigate: (String) -> Unit
) {
    val isSelected = route == selectedRoute ||
        (route == "today" && selectedRoute == "home") ||
        (route == "balance" && selectedRoute == "dimensions") ||
        (route == "me" && selectedRoute == "profile")

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        animationSpec = tween(Motion.Medium),
        label = "navColor"
    )

    Column(
        modifier = Modifier
            .clip(LifeScoreShapes.medium)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onNavigate(route) }
            .padding(horizontal = Space.sm, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 10.sp,
            color = iconColor
        )
        Spacer(Modifier.height(2.dp))
        // Subtle dot indicator under selected
        Box(
            modifier = Modifier
                .size(3.5.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else Color.Transparent
                )
        )
    }
}
