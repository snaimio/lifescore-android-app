package com.lifescore.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lifescore.app.domain.model.CollectibleCard
import com.lifescore.app.domain.model.HeroArchetype
import com.lifescore.app.domain.model.UserProfile

@Composable
fun CharacterSheetDialog(
    userProfile: UserProfile,
    archetype: HeroArchetype = HeroArchetype.WARRIOR,
    onDismiss: () -> Unit
) {
    val collectibleCards = remember(userProfile.currentLevel) {
        listOf(
            CollectibleCard("c1", "Unbreakable Will", "Discipline", "Discipline is choosing between what you want now and what you want most.", 1, isUnlocked = true, colorHex = 0xFFFF5722),
            CollectibleCard("c2", "Clarity of Mind", "Wisdom", "Peace comes from within. Do not seek it without.", 3, isUnlocked = userProfile.currentLevel >= 3, colorHex = 0xFF9C27B0),
            CollectibleCard("c3", "Laser Focus", "Focus", "Starve your distractions, feed your focus.", 5, isUnlocked = userProfile.currentLevel >= 5, colorHex = 0xFF2196F3),
            CollectibleCard("c4", "Titan's Grit", "Strength", "The pain you feel today will be the strength you feel tomorrow.", 10, isUnlocked = userProfile.currentLevel >= 10, colorHex = 0xFF4CAF50),
            CollectibleCard("c5", "Radiant Sovereign", "Confidence", "Mastery over oneself is the ultimate sovereignty.", 15, isUnlocked = userProfile.currentLevel >= 15, colorHex = 0xFFFFD700)
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 640.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Hero Character Sheet", fontWeight = FontWeight.Black, fontSize = 18.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Avatar / Archetype Card
                Surface(
                    shape = CircleShape,
                    color = Color(archetype.baseColorHex).copy(alpha = 0.2f),
                    modifier = Modifier.size(68.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(archetype.iconEmoji, fontSize = 36.sp)
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(userProfile.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Level ${userProfile.currentLevel} • ${userProfile.title}", color = Color(archetype.baseColorHex), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text("Total XP: ${userProfile.currentXp} • 🔥 ${userProfile.currentStreakDays}d Streak", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)

                Spacer(Modifier.height(16.dp))

                Text(
                    "Collectible Milestone Cards",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(collectibleCards) { card ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (card.isUnlocked) Color(card.colorHex).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (card.isUnlocked) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(card.colorHex), modifier = Modifier.size(24.dp))
                                    Spacer(Modifier.height(4.dp))
                                    Text(card.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
                                    Text(card.category, fontSize = 10.sp, color = Color(card.colorHex), fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(4.dp))
                                    Text("\"${card.quote}\"", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, lineHeight = 13.sp)
                                } else {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(24.dp))
                                    Spacer(Modifier.height(4.dp))
                                    Text("Locked", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                                    Text("Unlocks at Lvl ${card.unlockedAtLevel}", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
