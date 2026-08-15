package com.lifescore.app.presentation.ui.share

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lifescore.app.core.util.CardGenerator
import com.lifescore.app.core.util.CardTheme
import com.lifescore.app.core.util.ShareCardData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareScoreCardDialog(
    data: ShareCardData,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedTheme by remember { mutableStateOf(CardTheme.COSMIC_NIGHT) }
    var generatedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isGenerating by remember { mutableStateOf(true) }

    // Re-generate bitmap whenever theme changes
    LaunchedEffect(selectedTheme, data) {
        isGenerating = true
        withContext(Dispatchers.Default) {
            val bmp = CardGenerator.generateCardBitmap(data, selectedTheme)
            generatedBitmap = bmp
            isGenerating = false
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Share Your LifeScore",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Theme Selection Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CardTheme.values().forEach { theme ->
                        val isSelected = selectedTheme == theme
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTheme = theme }
                        ) {
                            Text(
                                text = theme.displayName.take(6),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // 9:16 Card Live Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .aspectRatio(9f / 16f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    if (isGenerating || generatedBitmap == null) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    } else {
                        Image(
                            bitmap = generatedBitmap!!.asImageBitmap(),
                            contentDescription = "Shareable LifeScore 9:16 Card",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Social Sharing Buttons Grid
                Text(
                    text = "SHARE DIRECTLY TO",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    letterSpacing = 1.2.sp
                )

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Instagram Stories
                    SocialShareButton(
                        icon = "📸",
                        label = "Instagram",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            generatedBitmap?.let { bmp ->
                                val uri = CardGenerator.saveBitmapToCache(context, bmp)
                                val caption = CardGenerator.generateCaption(data)
                                shareToApp(context, uri, caption, "com.instagram.android")
                            }
                        }
                    )

                    // WhatsApp
                    SocialShareButton(
                        icon = "💬",
                        label = "WhatsApp",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            generatedBitmap?.let { bmp ->
                                val uri = CardGenerator.saveBitmapToCache(context, bmp)
                                val caption = CardGenerator.generateCaption(data)
                                shareToApp(context, uri, caption, "com.whatsapp")
                            }
                        }
                    )

                    // TikTok / Story
                    SocialShareButton(
                        icon = "🎵",
                        label = "TikTok",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            generatedBitmap?.let { bmp ->
                                val uri = CardGenerator.saveBitmapToCache(context, bmp)
                                val caption = CardGenerator.generateCaption(data)
                                shareToApp(context, uri, caption, "com.zhiliaoapp.musically")
                            }
                        }
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Main System Share Sheet & Copy Caption Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            generatedBitmap?.let { bmp ->
                                val uri = CardGenerator.saveBitmapToCache(context, bmp)
                                val caption = CardGenerator.generateCaption(data)
                                val intent = CardGenerator.createShareIntent(context, uri, caption)
                                context.startActivity(Intent.createChooser(intent, "Share LifeScore Card"))
                            }
                        },
                        modifier = Modifier
                            .weight(1.4f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("More Share Options", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val caption = CardGenerator.generateCaption(data)
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("LifeScore Caption", caption))
                            Toast.makeText(context, "Caption copied to clipboard! ✨", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Copy Caption", fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun SocialShareButton(
    icon: String,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.height(64.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 22.sp)
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun shareToApp(context: Context, uri: android.net.Uri, caption: String, packageName: String) {
    try {
        val intent = CardGenerator.createShareIntent(context, uri, caption, packageName)
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to system chooser if specific social app is not installed
        val fallbackIntent = CardGenerator.createShareIntent(context, uri, caption)
        context.startActivity(Intent.createChooser(fallbackIntent, "Share via"))
    }
}
