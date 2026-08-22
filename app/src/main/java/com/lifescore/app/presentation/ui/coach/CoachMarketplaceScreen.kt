package com.lifescore.app.presentation.ui.coach

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.data.local.entity.CoachEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachMarketplaceScreen(
    viewModel: CoachMarketplaceViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Expert Coach Marketplace", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Coach.me Verified Accountability & 1-on-1 Mentors", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category Filter
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val specialties = listOf("ALL", "Executive Productivity", "Fitness & Nutrition", "Mental Health")
                    items(specialties) { spec ->
                        val isSelected = uiState.selectedSpecialty == spec
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setSpecialty(spec) },
                            label = { Text(spec) }
                        )
                    }
                }
            }

            // Coach Cards
            val filtered = if (uiState.selectedSpecialty == "ALL") {
                uiState.coaches
            } else {
                uiState.coaches.filter { it.specialty.contains(uiState.selectedSpecialty, ignoreCase = true) }
            }

            items(filtered) { coach ->
                CoachCard(
                    coach = coach,
                    onBook = { viewModel.selectCoachForBooking(coach) }
                )
            }

            // Confirmed Bookings
            if (uiState.bookings.isNotEmpty()) {
                item {
                    Text("📅 Your Scheduled Sessions", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                items(uiState.bookings) { booking ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(booking.coachName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Surface(
                                    color = Color(0xFFE8F5E9),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "Confirmed",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("🕒 ${booking.scheduledDateIso}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("🎯 Goal: ${booking.sessionGoal}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }

    // Booking Dialog
    if (uiState.selectedCoachForBooking != null) {
        val coach = uiState.selectedCoachForBooking!!
        AlertDialog(
            onDismissRequest = { viewModel.selectCoachForBooking(null) },
            title = { Text("Book 1-on-1 Session with ${coach.name}") },
            text = {
                Column {
                    Text("Rate: \$${coach.hourlyRateUsd}/hr • 45-min Zoom Video Call", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("What would you like to focus on?", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = uiState.bookingGoalInput,
                        onValueChange = { viewModel.onGoalChange(it) },
                        placeholder = { Text("e.g., Overcoming procrastination in high-stakes projects") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.confirmBooking() }) {
                    Text("Confirm Booking")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.selectCoachForBooking(null) }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CoachCard(
    coach: CoachEntity,
    onBook: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(coach.avatarEmoji, fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(coach.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        if (coach.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = Color(0xFF1E88E5), modifier = Modifier.size(16.dp))
                        }
                    }
                    Text(coach.title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("⭐ ${coach.rating} (${coach.reviewsCount} reviews)", fontSize = 11.sp, color = Color(0xFFFFA000), fontWeight = FontWeight.SemiBold)
                }

                Text(
                    "\$${coach.hourlyRateUsd}/hr",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(coach.bio, fontSize = 13.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurface)

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onBook,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Schedule 1-on-1 Session")
            }
        }
    }
}
