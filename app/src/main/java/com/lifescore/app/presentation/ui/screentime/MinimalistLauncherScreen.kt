package com.lifescore.app.presentation.ui.screentime

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinimalistLauncherScreen(
    onBack: () -> Unit = {},
    onLaunchApp: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val currentTime = remember {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        sdf.format(Date())
    }
    val currentDate = remember {
        val sdf = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        sdf.format(Date())
    }

    val minimalistApps = listOf(
        "Phone", "Messages", "LifeScore", "Camera", "Maps",
        "Calendar", "Calculator", "Clock", "Notes", "Kindle",
        "Spotify", "Settings", "Browser"
    )

    val filteredApps = remember(searchQuery) {
        if (searchQuery.isBlank()) minimalistApps
        else minimalistApps.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {
            // Top Exit Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Exit Minimalist Mode", tint = Color.White.copy(alpha = 0.7f))
                }
                Text(
                    "MINIMALIST FOCUS MODE",
                    color = Color.White.copy(alpha = 0.4f),
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 2.sp
                )
            }

            Spacer(Modifier.height(36.dp))

            // Time & Date Display
            Text(
                text = currentTime,
                fontSize = 54.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                letterSpacing = (-1).sp
            )
            Text(
                text = currentDate,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.6f),
                fontWeight = FontWeight.Normal
            )

            Spacer(Modifier.height(24.dp))

            // Daily Mindful Intention Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1E1E1E),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💡", fontSize = 18.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Attention is your most valuable currency. Spend it consciously.",
                        color = Color.White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search apps...", color = Color.White.copy(alpha = 0.4f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.4f)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White.copy(alpha = 0.3f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                    focusedContainerColor = Color(0xFF1E1E1E),
                    unfocusedContainerColor = Color(0xFF1E1E1E)
                ),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            // Text-Only App List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredApps) { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLaunchApp(app) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = app,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.3f))
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Bottom Exit Pill
            Button(
                onClick = onBack,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2C)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Exit Minimalist Launcher", color = Color.White.copy(alpha = 0.8f))
            }
        }
    }
}
