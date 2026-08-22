package com.lifescore.app.presentation.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.lifescore.app.core.designsystem.Spacing
import com.lifescore.app.core.designsystem.components.GlassCard
import com.lifescore.app.core.util.QuickAssessmentEngine
import com.lifescore.app.core.util.QuickAssessmentResult
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.HeroArchetype

enum class SimpleOnboardingStep {
    WELCOME,
    ASSESSMENT,
    RESULTS,
    FIRST_QUEST
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimplifiedOnboardingScreen(
    onCompleteOnboarding: (HeroArchetype, Map<DimensionType, Float>, Int, String) -> Unit,
    onOpenFullAssessment: () -> Unit = {}
) {
    var currentStep by remember { mutableStateOf(SimpleOnboardingStep.WELCOME) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    val answers = remember { mutableStateMapOf<Int, Int>() }
    var assessmentResult by remember { mutableStateOf<QuickAssessmentResult?>(null) }
    var isFirstQuestCompleted by remember { mutableStateOf(false) }

    val questions = QuickAssessmentEngine.questions
    val currentQuestion = questions[currentQuestionIndex]

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (currentStep) {
                            SimpleOnboardingStep.WELCOME -> "Welcome to LifeScore"
                            SimpleOnboardingStep.ASSESSMENT -> "Quick Discovery (${currentQuestionIndex + 1}/${questions.size})"
                            SimpleOnboardingStep.RESULTS -> "Your Archetype"
                            SimpleOnboardingStep.FIRST_QUEST -> "First Quest"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    if (currentStep == SimpleOnboardingStep.ASSESSMENT && currentQuestionIndex > 0) {
                        IconButton(onClick = { currentQuestionIndex-- }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    } else if (currentStep != SimpleOnboardingStep.WELCOME && currentStep != SimpleOnboardingStep.FIRST_QUEST) {
                        IconButton(onClick = { currentStep = SimpleOnboardingStep.WELCOME }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (currentStep == SimpleOnboardingStep.ASSESSMENT) {
                        TextButton(onClick = {
                            questions.forEach { q ->
                                if (!answers.containsKey(q.id)) answers[q.id] = 4
                            }
                            assessmentResult = QuickAssessmentEngine.evaluate(answers)
                            currentStep = SimpleOnboardingStep.RESULTS
                        }) {
                            Text("Fast Track", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                targetState = currentStep,
                label = "onboarding_step"
            ) { step ->
                when (step) {
                    SimpleOnboardingStep.WELCOME -> {
                        WelcomeStepContent(
                            onStart = { currentStep = SimpleOnboardingStep.ASSESSMENT },
                            onOpenAdvanced = onOpenFullAssessment
                        )
                    }
                    SimpleOnboardingStep.ASSESSMENT -> {
                        AssessmentStepContent(
                            question = currentQuestion,
                            currentIndex = currentQuestionIndex,
                            totalQuestions = questions.size,
                            selectedScore = answers[currentQuestion.id] ?: 3,
                            onSelectScore = { score ->
                                answers[currentQuestion.id] = score
                                if (currentQuestionIndex < questions.size - 1) {
                                    currentQuestionIndex++
                                } else {
                                    assessmentResult = QuickAssessmentEngine.evaluate(answers)
                                    currentStep = SimpleOnboardingStep.RESULTS
                                }
                            }
                        )
                    }
                    SimpleOnboardingStep.RESULTS -> {
                        assessmentResult?.let { result ->
                            ResultsStepContent(
                                result = result,
                                onContinue = { currentStep = SimpleOnboardingStep.FIRST_QUEST }
                            )
                        }
                    }
                    SimpleOnboardingStep.FIRST_QUEST -> {
                        assessmentResult?.let { result ->
                            FirstQuestStepContent(
                                result = result,
                                isCompleted = isFirstQuestCompleted,
                                onToggleQuest = { isFirstQuestCompleted = !isFirstQuestCompleted },
                                onFinish = {
                                    onCompleteOnboarding(
                                        result.archetype,
                                        result.dimensionScores,
                                        result.startingLifeScore,
                                        result.firstQuestTitle
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomeStepContent(
    onStart: () -> Unit,
    onOpenAdvanced: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(90.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🌟", fontSize = 44.sp)
                }
            }

            Spacer(Modifier.height(Spacing.lg))

            Text(
                text = "Welcome to LifeScore 👋",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(Spacing.sm))

            Text(
                text = "The driver's seat for your life. Simple habits, progressive growth, zero overwhelm.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(Spacing.xl))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(Spacing.md)) {
                    FeaturePill(emoji = "⏱️", title = "10 Simple Questions", subtitle = "Takes under 60 seconds to personalize")
                    Spacer(Modifier.height(Spacing.sm))
                    FeaturePill(emoji = "🛡️", title = "Discover Your Archetype", subtitle = "Uncover your unique growth superpower")
                    Spacer(Modifier.height(Spacing.sm))
                    FeaturePill(emoji = "🎯", title = "1 First Micro-Quest", subtitle = "Begin your streak with immediate momentum")
                }
            }

            Spacer(Modifier.height(Spacing.xl))

            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Get Started (1 min)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }

            Spacer(Modifier.height(Spacing.md))

            TextButton(onClick = onOpenAdvanced) {
                Text("Looking for the full 130-question psychometric audit?", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun FeaturePill(emoji: String, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            modifier = Modifier.size(38.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(emoji, fontSize = 18.sp)
            }
        }
        Spacer(Modifier.width(Spacing.sm))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AssessmentStepContent(
    question: com.lifescore.app.core.util.QuickQuestion,
    currentIndex: Int,
    totalQuestions: Int,
    selectedScore: Int,
    onSelectScore: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / totalQuestions.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )

            Spacer(Modifier.height(Spacing.lg))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(question.dimension.baseColorHex).copy(alpha = 0.15f),
                border = BorderStroke(1.dp, Color(question.dimension.baseColorHex).copy(alpha = 0.4f))
            ) {
                Text(
                    text = "${question.dimension.displayName.uppercase()} DIMENSION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(question.dimension.baseColorHex),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(Modifier.height(Spacing.xl))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "\"${question.text}\"",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp
                    )

                    Spacer(Modifier.height(Spacing.lg))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(question.lowLabel, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(question.highLabel, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(Spacing.md))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        (1..5).forEach { score ->
                            val isSelected = selectedScore == score
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .clickable { onSelectScore(score) }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = score.toString(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Text(
            text = "Tap a rating 1 (Rarely) to 5 (Always) to advance",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = Spacing.md)
        )
    }
}

@Composable
private fun ResultsStepContent(
    result: QuickAssessmentResult,
    onContinue: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(result.archetype.iconEmoji, fontSize = 40.sp)
                }
            }

            Spacer(Modifier.height(Spacing.md))

            Text(
                text = "You are ${result.archetype.title}!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(Spacing.xs))

            Text(
                text = result.archetype.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(Spacing.lg))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(Spacing.md)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🌟 Starting LifeScore", fontWeight = FontWeight.Bold)
                        Text("${result.startingLifeScore} / 1000", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
                    }

                    Spacer(Modifier.height(Spacing.sm))

                    LinearProgressIndicator(
                        progress = { result.startingLifeScore.toFloat() / 1000f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )

                    Spacer(Modifier.height(Spacing.md))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("🛡️ Superpower", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(result.primaryStrength, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("🌱 Focus Area", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(result.growthArea, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(Spacing.xl))

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Claim First Quest →", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FirstQuestStepContent(
    result: QuickAssessmentResult,
    isCompleted: Boolean,
    onToggleQuest: () -> Unit,
    onFinish: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Text(
                text = "Your First Quest Awaits 🎯",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(Spacing.xs))

            Text(
                text = "Small actions create unstoppable momentum. Complete your baseline quest now:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(Spacing.lg))

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleQuest() }
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = { onToggleQuest() }
                    )
                    Spacer(Modifier.width(Spacing.sm))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = result.firstQuestTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "${result.firstQuestDimension.displayName} Dimension • +50 XP",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (isCompleted) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("✨", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }

            if (isCompleted) {
                Spacer(Modifier.height(Spacing.md))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🎉 Quest Completed! +50 XP earned and Day 1 streak ignited.",
                        modifier = Modifier.padding(Spacing.md),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(Spacing.xl))

            Button(
                onClick = onFinish,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (isCompleted) "Enter LifeScore Command Center 🚀" else "Start Journey →",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
