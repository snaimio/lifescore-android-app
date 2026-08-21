package com.lifescore.app.presentation.ui.archetype

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.*
import com.lifescore.app.core.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchetypeProfileScreen(
    initialArchetypeId: String = "architect",
    userScore: Int = 780,
    userLevel: Int = 5,
    onNavigateBack: () -> Unit
) {
    var selectedArchetype by remember {
        mutableStateOf(ArchetypeManager.getArchetypeById(initialArchetypeId))
    }
    var showShareModal by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hero Archetype Profile", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showShareModal = true }) {
                        Icon(Icons.Default.Share, contentDescription = "Share Archetype Card")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Archetype Picker Carousel
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ArchetypeManager.allArchetypes) { arch ->
                        val isSelected = selectedArchetype.id == arch.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedArchetype = arch },
                            label = { Text("${arch.icon} ${arch.name}", fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }
            }

            // 2. Hero Archetype Reveal Card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(selectedArchetype.themeColorHex).copy(alpha = 0.5f),
                                        Color(0xFF0F172A)
                                    )
                                )
                            )
                            .padding(22.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.15f),
                                modifier = Modifier.size(72.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(selectedArchetype.icon, fontSize = 36.sp)
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            Text(
                                text = selectedArchetype.name,
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp,
                                color = Color.White
                            )

                            Text(
                                text = selectedArchetype.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD700),
                                textAlign = TextAlign.Center
                            )

                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = selectedArchetype.overview,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                textAlign = TextAlign.Center
                            )

                            Spacer(Modifier.height(16.dp))

                            // Share Persona Story Card CTA
                            Button(
                                onClick = { showShareModal = true },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(selectedArchetype.themeColorHex)),
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("📸 Share \"I am ${selectedArchetype.name}\" Card", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // 3. Superpower & Growth Area
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⚡", fontSize = 16.sp)
                            Spacer(Modifier.width(6.dp))
                            Text("Core Superpower", fontWeight = FontWeight.Black, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        Text(selectedArchetype.superpower, fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 4.dp, bottom = 10.dp))

                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        Spacer(Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🌱", fontSize = 16.sp)
                            Spacer(Modifier.width(6.dp))
                            Text("Growth Recommendation", fontWeight = FontWeight.Black, fontSize = 13.sp, color = Color(0xFFF59E0B))
                        }
                        Text(selectedArchetype.growthRecommendation, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }

            // 4. Natural Tendencies
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🌟 Natural Tendencies & Cognitive Drivers", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        selectedArchetype.tendencies.forEach { tend ->
                            Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
                                Text("•", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(8.dp))
                                Text(tend, fontSize = 12.sp, lineHeight = 18.sp)
                            }
                        }
                    }
                }
            }

            // 5. Blind Spots & Pitfalls
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("⚠️ Blind Spots & Cognitive Pitfalls", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFE53935))
                        Spacer(Modifier.height(8.dp))
                        selectedArchetype.blindSpots.forEach { spot ->
                            Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
                                Text("⚠", fontSize = 11.sp, color = Color(0xFFE53935))
                                Spacer(Modifier.width(8.dp))
                                Text(spot, fontSize = 12.sp, lineHeight = 18.sp)
                            }
                        }
                    }
                }
            }

            // 6. Work & Leadership Style
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("💼 Work, Leadership & Execution Style", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        selectedArchetype.workStyle.forEach { work ->
                            Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
                                Text("➔", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.secondary)
                                Spacer(Modifier.width(8.dp))
                                Text(work, fontSize = 12.sp, lineHeight = 18.sp)
                            }
                        }
                    }
                }
            }

            // 7. Relationship & Social Dynamics
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🤝 Relationship & Empathy Dynamics", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        selectedArchetype.relationshipStyle.forEach { rel ->
                            Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
                                Text("💖", fontSize = 11.sp)
                                Spacer(Modifier.width(8.dp))
                                Text(rel, fontSize = 12.sp, lineHeight = 18.sp)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(30.dp))
            }
        }
    }

    // 8. Share Archetype Story Card Dialog
    if (showShareModal) {
        var selectedTheme by remember { mutableStateOf(CardTheme.COSMIC_NIGHT) }

        Dialog(onDismissRequest = { showShareModal = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Share Persona Story Card",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "9:16 Instagram & TikTok format with custom theme",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )

                    Spacer(Modifier.height(16.dp))

                    // Theme Selector
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(CardTheme.values()) { theme ->
                            val isSelected = selectedTheme == theme
                            Surface(
                                onClick = { selectedTheme = theme },
                                shape = RoundedCornerShape(12.dp),
                                color = Color(theme.topColor),
                                border = if (isSelected) BorderStroke(2.dp, Color.White) else null,
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val data = ArchetypeShareCardData(
                                userName = "Achiever",
                                score = userScore,
                                level = userLevel,
                                archetype = selectedArchetype
                            )
                            val bitmap = CardGenerator.generateArchetypeCardBitmap(data, selectedTheme)
                            val uri = CardGenerator.saveBitmapToCache(context, bitmap)
                            val caption = ArchetypeManager.generateArchetypeShareCaption(selectedArchetype, userScore, userLevel)
                            val intent = CardGenerator.createShareIntent(context, uri, caption)
                            context.startActivity(Intent.createChooser(intent, "Share Archetype Story Card"))
                            showShareModal = false
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Share to Instagram / WhatsApp", fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(8.dp))

                    TextButton(onClick = { showShareModal = false }) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}
