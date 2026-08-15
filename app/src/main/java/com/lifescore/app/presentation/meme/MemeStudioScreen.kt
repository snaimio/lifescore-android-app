package com.lifescore.app.presentation.meme

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
import com.lifescore.app.core.util.MemeGeneratorEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemeStudioScreen(
    viewModel: MemeViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.coinsEarnedToast) {
        uiState.coinsEarnedToast?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Meme Studio", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = { viewModel.remixWithAi() },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text("🎲 AI Remix", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Hero 9:16 Meme Canvas Preview Card
            item {
                Card(
                    shape = RoundedCornerShape(26.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .height(380.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = uiState.selectedTemplate.backgroundGradientColors.map { Color(it) }
                                )
                            )
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Top Text
                            Text(
                                text = uiState.topCaption,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                lineHeight = 22.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Center Emoji Art
                            Surface(
                                shape = CircleShape,
                                color = Color.Black.copy(alpha = 0.3f),
                                modifier = Modifier.size(110.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(uiState.selectedTemplate.emojiArt, fontSize = 42.sp)
                                }
                            }

                            // Bottom Text
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = uiState.bottomCaption,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    lineHeight = 22.sp,
                                    color = Color(0xFFFFD700),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(Modifier.height(14.dp))

                                // Watermark Badge
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color.Black.copy(alpha = 0.45f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("⚡ LifeScore", fontWeight = FontWeight.Black, fontSize = 10.sp, color = Color.White)
                                        Text(" • ${uiState.userStreak}d Streak 🔥", fontSize = 10.sp, color = Color(0xFFFFD700))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. Template Selector Carousel
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Select Meme Format", fontWeight = FontWeight.Black, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(uiState.templates) { template ->
                            val isSelected = uiState.selectedTemplate.id == template.id
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                                modifier = Modifier.clickable { viewModel.selectTemplate(template) }
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(template.category.icon, fontSize = 20.sp)
                                    Spacer(Modifier.height(4.dp))
                                    Text(template.title, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // 3. Caption Customizers
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Customize Meme Captions", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = uiState.topCaption,
                            onValueChange = { viewModel.updateTopCaption(it) },
                            label = { Text("Top Header Text") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = false,
                            maxLines = 2
                        )
                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = uiState.bottomCaption,
                            onValueChange = { viewModel.updateBottomCaption(it) },
                            label = { Text("Punchline Text") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = false,
                            maxLines = 2
                        )
                    }
                }
            }

            // 4. Action Buttons (Share & Save)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.saveMemeToGallery() },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Icon(Icons.Default.SaveAlt, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Save (+50 🪙)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val shareCaption = MemeGeneratorEngine.generateShareCaption(uiState.currentMeme)
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareCaption)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share LifeScore Meme"))
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1.3f).height(48.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Share to Story 🚀", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(30.dp))
            }
        }
    }
}
