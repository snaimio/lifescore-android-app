package com.lifescore.app.presentation.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lifescore.app.core.designsystem.Space
import com.lifescore.app.domain.model.UserProfile

@Composable
fun LifeScoreDrawerContent(
    userProfile: UserProfile?,
    currentRoute: String?,
    navController: NavController,
    onCloseDrawer: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val drawerBg = if (isDark) Color(0xFF13121C) else Color(0xFFFFFFFF)
    val textPrimary = if (isDark) Color(0xFFFBF8F3) else Color(0xFF19181F)

    ModalDrawerSheet(
        modifier = Modifier.width(320.dp),
        drawerContainerColor = drawerBg,
        drawerContentColor = textPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // 1. Clean User Profile Header
            DrawerUserHeader(
                userProfile = userProfile,
                onCloseDrawer = onCloseDrawer
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = Space.md, vertical = Space.xs),
                color = if (isDark) Color(0x1AD4A24C) else Color(0x22D4A24C)
            )

            // 2. Scrollable Navigation Sections (Non-duplicate Quick Tools, Wellness & Settings)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = Space.sm),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Section 1: Quick Tools
                item {
                    DrawerSectionTitle(
                        title = "QUICK TOOLS",
                        count = "${DrawerNavigationConfig.quickTools.size}"
                    )
                }
                items(DrawerNavigationConfig.quickTools, key = { it.route }) { item ->
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

                // Section 2: Wellness & Lifestyle Trackers
                item {
                    DrawerSectionTitle(
                        title = "WELLNESS & HABITS",
                        count = "${DrawerNavigationConfig.wellnessTrackers.size}"
                    )
                }
                items(DrawerNavigationConfig.wellnessTrackers, key = { it.route }) { item ->
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

                // Section 3: Preferences & System
                item {
                    DrawerSectionTitle(
                        title = "PREFERENCES & SYSTEM",
                        count = "${DrawerNavigationConfig.systemPreferences.size}"
                    )
                }
                items(DrawerNavigationConfig.systemPreferences, key = { it.route }) { item ->
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
                    Spacer(Modifier.height(Space.md))
                }
            }

            // 3. Clean Footer
            DrawerCleanFooter()
        }
    }
}

@Composable
fun DrawerUserHeader(
    userProfile: UserProfile?,
    onCloseDrawer: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val name = userProfile?.name?.ifBlank { "Guest" } ?: "Guest"
    val streak = userProfile?.currentStreakDays ?: 0
    val title = userProfile?.title?.ifBlank { "Member" } ?: "Member"
    val textPrimary = if (isDark) Color(0xFFFBF8F3) else Color(0xFF19181F)
    val textSecondary = if (isDark) Color(0xFF9E958B) else Color(0xFF6B6357)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isDark) {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E1C2E),
                            Color(0xFF13121C)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFBF8F3),
                            Color(0xFFF2ECE1)
                        )
                    )
                }
            )
            .padding(horizontal = Space.md, vertical = Space.md)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    color = Color(0x22D4A24C),
                    border = BorderStroke(1.dp, Color(0x44D4A24C))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Avatar",
                            tint = Color(0xFFD4A24C),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onCloseDrawer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close Menu",
                        tint = textSecondary
                    )
                }
            }

            Spacer(Modifier.height(Space.xs))

            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                ),
                color = textPrimary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = textSecondary
            )

            Spacer(Modifier.height(Space.xs))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isDark) Color(0x1FD4A24C) else Color(0x1AD4A24C),
                border = BorderStroke(0.5.dp, Color(0x33D4A24C))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = Color(0xFFD4A24C),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "$streak Day Consistency",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFD4A24C)
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerSectionTitle(title: String, count: String) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val countBg = if (isDark) Color(0x1AFBF8F3) else Color(0x1A000000)
    val countText = if (isDark) Color(0xFF9E958B) else Color(0xFF6B6357)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Space.sm, top = Space.md, bottom = Space.xxs, end = Space.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD4A24C),
            letterSpacing = 1.2.sp
        )
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = countBg
        ) {
            Text(
                text = count,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = countText,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
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
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val iconTint = if (isSelected) Color(0xFFD4A24C) else if (isDark) Color(0xFF9E958B) else Color(0xFF6B6357)
    val labelColor = if (isSelected) {
        if (isDark) Color(0xFFFBF8F3) else Color(0xFF19181F)
    } else {
        if (isDark) Color(0xFFC4BAB0) else Color(0xFF4A453E)
    }

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        },
        label = {
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = labelColor
            )
        },
        badge = {
            item.badge?.let { b ->
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isSelected) Color(0xFFD4A24C) else Color(0x22D4A24C)
                ) {
                    Text(
                        text = b,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color(0xFF13121C) else Color(0xFFD4A24C),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        },
        selected = isSelected,
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = if (isDark) Color(0x22D4A24C) else Color(0x1AD4A24C),
            unselectedContainerColor = Color.Transparent
        ),
        modifier = Modifier.padding(vertical = 1.dp)
    )
}

@Composable
fun DrawerCleanFooter() {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val footerBg = if (isDark) Color(0xFF1B1A28) else Color(0xFFF4F0EB)
    val footerBorder = if (isDark) Color(0x1AFBF8F3) else Color(0x1A000000)
    val footerText = if (isDark) Color(0xFF7A7269) else Color(0xFF8A8275)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Space.sm),
        shape = RoundedCornerShape(12.dp),
        color = footerBg,
        border = BorderStroke(0.5.dp, footerBorder)
    ) {
        Column(
            modifier = Modifier.padding(Space.sm),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "LifeScore OS • Compounding Growth",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = footerText
            )
        }
    }
}
