package com.lifescore.app.presentation.vlogs

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.MicroVlog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraXCaptureDialog(
    clip: MicroVlog,
    onDismiss: () -> Unit,
    onSaveClip: (dayOfWeek: String, dimension: DimensionType, caption: String) -> Unit
) {
    var selectedDimension by remember { mutableStateOf(clip.dimension) }
    var captionText by remember { mutableStateOf(clip.caption) }
    var isRecording by remember { mutableStateOf(false) }
    var recordProgress by remember { mutableStateOf(0f) }
    var isFrontCamera by remember { mutableStateOf(false) }
    var isFlashOn by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = { if (!isRecording) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Simulated Camera Preview Viewport with Grid Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF1E293B),
                                Color(0xFF0F172A),
                                Color.Black
                            )
                        )
                    )
            ) {
                // Top Camera Controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Red.copy(alpha = if (isRecording) 0.9f else 0.4f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color.White, CircleShape)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (isRecording) "2.0s RECORDING" else "2.0s QUICK SNAP",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { isFlashOn = !isFlashOn },
                            modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                contentDescription = "Flash",
                                tint = if (isFlashOn) Color.Yellow else Color.White
                            )
                        }
                        IconButton(
                            onClick = { isFrontCamera = !isFrontCamera },
                            modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.FlipCameraAndroid, contentDescription = "Flip", tint = Color.White)
                        }
                    }
                }

                // Central Camera Reticle / Watermark
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(selectedDimension.baseColorHex).copy(alpha = 0.25f),
                        border = BorderStroke(2.dp, Color(selectedDimension.baseColorHex)),
                        modifier = Modifier.size(100.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("📹", fontSize = 42.sp)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Hold Shutter for 2 Seconds",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "CameraX • 60 FPS • Habit Calibration",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp
                    )
                }

                // Bottom Controls & Shutter Ring
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Dimension Selector
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(DimensionType.values()) { dim ->
                            val isSelected = selectedDimension == dim
                            Surface(
                                onClick = { selectedDimension = dim },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(dim.baseColorHex) else Color.White.copy(alpha = 0.15f),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text(
                                    text = dim.displayName,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Caption Input
                    OutlinedTextField(
                        value = captionText,
                        onValueChange = { captionText = it },
                        placeholder = { Text("What habit did you crush? (e.g. 5km morning run)", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(selectedDimension.baseColorHex),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedContainerColor = Color.Black.copy(alpha = 0.4f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.4f)
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(20.dp))

                    // 2.0s Countdown Shutter Ring
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(90.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { recordProgress },
                            strokeWidth = 5.dp,
                            color = Color.Red,
                            trackColor = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.fillMaxSize()
                        )

                        Surface(
                            onClick = {
                                if (!isRecording) {
                                    isRecording = true
                                    coroutineScope.launch {
                                        val totalMs = 2000L
                                        val interval = 20L
                                        val steps = totalMs / interval
                                        for (i in 0..steps) {
                                            recordProgress = i.toFloat() / steps.toFloat()
                                            delay(interval)
                                        }
                                        isRecording = false
                                        val finalCaption = captionText.ifBlank { "Crushed ${selectedDimension.displayName} habit!" }
                                        onSaveClip(clip.dayOfWeek, selectedDimension, finalCaption)
                                    }
                                }
                            },
                            shape = CircleShape,
                            color = if (isRecording) Color.Red else Color.White,
                            modifier = Modifier.size(70.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (isRecording) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(Color.White, RoundedCornerShape(4.dp))
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .background(Color.White, CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
