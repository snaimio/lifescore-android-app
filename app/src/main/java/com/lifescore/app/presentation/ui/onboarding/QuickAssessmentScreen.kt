package com.lifescore.app.presentation.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.core.util.QuickAssessmentEngine
import com.lifescore.app.core.util.QuickQuestion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAssessmentScreen(
    onComplete: (answers: Map<Int, Int>) -> Unit,
    onBack: () -> Unit
) {
    val questions = QuickAssessmentEngine.questions
    var currentQuestionIndex by remember { mutableStateOf(0) }
    val answers = remember { mutableStateMapOf<Int, Int>() }

    val currentQ = questions[currentQuestionIndex]
    val progress by animateFloatAsState(
        targetValue = (currentQuestionIndex + 1).toFloat() / questions.size,
        label = "assessmentProgress"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (currentQuestionIndex > 0) {
                        IconButton(onClick = { currentQuestionIndex-- }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Question")
                        }
                    } else {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                title = {
                    Column {
                        Text(
                            "Discover Your Type",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Question ${currentQuestionIndex + 1} of ${questions.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    TextButton(onClick = {
                        // Fast track with neutral defaults
                        questions.forEach { q ->
                            if (!answers.containsKey(q.id)) answers[q.id] = 4
                        }
                        onComplete(answers)
                    }) {
                        Text("Fast Track →", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.lg, vertical = Spacing.md)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Progress Bar
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )

                Spacer(Modifier.height(Spacing.xl))

                // Question Card
                AnimatedContent(
                    targetState = currentQ,
                    transitionSpec = {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut())
                    },
                    label = "questionCard"
                ) { q ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(Spacing.lg)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.padding(bottom = Spacing.sm)
                            ) {
                                Text(
                                    text = "${q.dimension.displayName}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = q.text,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 28.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(Spacing.xl))

                // 5 Interactive Options
                val options = listOf(
                    1 to "Strongly Disagree",
                    2 to "Disagree",
                    3 to "Neutral / Sometimes",
                    4 to "Agree",
                    5 to "Strongly Agree"
                )

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    options.forEach { (value, label) ->
                        val isSelected = answers[currentQ.id] == value
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            ),
                            onClick = {
                                answers[currentQ.id] = value
                                if (currentQuestionIndex < questions.size - 1) {
                                    currentQuestionIndex++
                                } else {
                                    onComplete(answers)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = Spacing.md),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = when (value) {
                                        1 -> "🔴"
                                        2 -> "🟠"
                                        3 -> "🟡"
                                        4 -> "🟢"
                                        else -> "🌟"
                                    },
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(Spacing.xl))
        }
    }
}
