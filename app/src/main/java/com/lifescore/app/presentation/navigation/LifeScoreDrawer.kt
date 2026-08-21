package com.lifescore.app.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.domain.model.UserProfile

@Composable
fun LifeScoreDrawerContent(
    userProfile: UserProfile?,
    currentRoute: String?,
    navController: NavController,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(330.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // 1. User Profile Header
            DrawerUserHeader(
                userProfile = userProfile,
                onCloseDrawer = onCloseDrawer
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )

            // 2. Scrollable Navigation Sections
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Section 1: Main Core
                item {
                    DrawerSectionTitle(title = "CORE LIFE OS", count = "${DrawerNavigationConfig.mainItems.size}")
                }
                items(DrawerNavigationConfig.mainItems, key = { it.route }) { item ->
                    DrawerNavRow(
                        item = item,
                        isSelected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                            }
                            onCloseDrawer()
                        }
                    )
                }

                // Section 2: 15 Modular Tracker Mini-Apps
                item {
                    DrawerSectionTitle(title = "15 TRACKER MINI-APPS", count = "15")
                }
                items(DrawerNavigationConfig.trackerMiniApps, key = { it.route }) { item ->
                    DrawerNavRow(
                        item = item,
                        isSelected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                            }
                            onCloseDrawer()
                        }
                    )
                }

                // Section 3: Growth & Science Systems
                item {
                    DrawerSectionTitle(title = "GROWTH & SCIENCE", count = "${DrawerNavigationConfig.growthItems.size}")
                }
                items(DrawerNavigationConfig.growthItems, key = { it.route }) { item ->
                    DrawerNavRow(
                        item = item,
                        isSelected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                            }
                            onCloseDrawer()
                        }
                    )
                }

                // Section 4: Community & Settings
                item {
                    DrawerSectionTitle(title = "COMMUNITY & SETTINGS", count = "${DrawerNavigationConfig.communityItems.size}")
                }
                items(DrawerNavigationConfig.communityItems, key = { it.route }) { item ->
                    DrawerNavRow(
                        item = item,
                        isSelected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                            }
                            onCloseDrawer()
                        }
                    )
                }

                item {
                    Spacer(Modifier.height(Spacing.md))
                }
            }

            // 3. Footer
            DrawerDailyTipFooter()
        }
    }
}

@Composable
fun DrawerUserHeader(
    userProfile: UserProfile?,
    onCloseDrawer: () -> Unit
) {
    val name = userProfile?.name ?: "Guest Hero"
    val level = userProfile?.currentLevel ?: 1
    val streak = userProfile?.currentStreakDays ?: 0
    val title = userProfile?.title ?: "Novice Seeker"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        Color.Transparent
                    )
                )
            )
            .padding(horizontal = Spacing.md, vertical = Spacing.sm)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("👤", fontSize = 28.sp)
                    }
                }

                IconButton(
                    onClick = onCloseDrawer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close Menu",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(Spacing.xs))

            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "$title • Level $level",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(Spacing.xs))

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF8B5CF6).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "⭐ Lvl $level Hero",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B5CF6),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF59E0B).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "🔥 ${streak}d streak",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerSectionTitle(title: String, count: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Spacing.sm, top = Spacing.md, bottom = Spacing.xs, end = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = count,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
            )
        }
    }
}

@Composable
fun DrawerNavRow(
    item: DrawerItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = {
            if (item.emoji != null) {
                Text(item.emoji, fontSize = 18.sp)
            } else {
                Icon(item.icon, contentDescription = null, modifier = Modifier.size(20.dp))
            }
        },
        label = {
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        },
        badge = {
            item.badge?.let { b ->
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = b,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        },
        selected = isSelected,
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.padding(vertical = 1.dp)
    )
}

@Composable
fun DrawerDailyTipFooter() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.sm),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(Spacing.sm)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💡", fontSize = 14.sp)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Daily Growth Law",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Small 1% daily compounding habits create monumental transformation over time.",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "LifeScore OS • v1.0.0 Pro",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
