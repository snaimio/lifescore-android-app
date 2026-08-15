package com.lifescore.app.presentation.ui.onboarding

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
import com.lifescore.app.core.util.*
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.HeroArchetype

enum class AssessmentStep {
    MODE_SELECT,
    QUESTIONS,
    ARCHETYPE_REVEAL,
    CAREERS_EXPLORER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingAssessmentScreen(
    onCompleteOnboarding: (HeroArchetype, Map<DimensionType, Float>) -> Unit
) {
    var currentStep by remember { mutableStateOf(AssessmentStep.MODE_SELECT) }
    var isExpressMode by remember { mutableStateOf(false) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedLanguage by remember { mutableStateOf(LanguageManager.getCurrentLanguage()) }
    var showLanguagePicker by remember { mutableStateOf(false) }
    val answers = remember { mutableStateMapOf<Int, Int>() }
    val context = LocalContext.current

    val activeQuestions = remember(isExpressMode) {
        if (isExpressMode) {
            // Pick 4 questions per dimension (24 total)
            PsychometricDimension.values().flatMap { dim ->
                PsychometricAssessmentEngine.questions.filter { it.dimension == dim }.take(4)
            }
        } else {
            PsychometricAssessmentEngine.questions // Full 130 questions
        }
    }

    val assessmentResult = remember(answers.toMap()) {
        if (answers.isNotEmpty()) {
            PsychometricAssessmentEngine.evaluateAssessment(answers)
        } else null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (currentStep) {
                            AssessmentStep.MODE_SELECT -> "LifeScore Assessment"
                            AssessmentStep.QUESTIONS -> "Question ${currentQuestionIndex + 1}/${activeQuestions.size}"
                            AssessmentStep.ARCHETYPE_REVEAL -> "Your Psychometric Profile"
                            AssessmentStep.CAREERS_EXPLORER -> "48 Career Matches"
                        },
                        fontWeight = FontWeight.Black
                    )
                },
                actions = {
                    if (currentStep == AssessmentStep.QUESTIONS) {
                        TextButton(
                            onClick = {
                                // Auto-fill sample responses to expedite review if desired
                                activeQuestions.forEach { q ->
                                    if (!answers.containsKey(q.id)) {
                                        answers[q.id] = (3..5).random()
                                    }
                                }
                                currentStep = AssessmentStep.ARCHETYPE_REVEAL
                            }
                        ) {
                            Text("Fast Complete", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Step Progress Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AssessmentStep.values().forEach { step ->
                    val isDone = currentStep.ordinal >= step.ordinal
                    LinearProgressIndicator(
                        progress = { if (isDone) 1f else 0f },
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            when (currentStep) {
                // STEP 1: MODE SELECTION
                AssessmentStep.MODE_SELECT -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🧬", fontSize = 36.sp)
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Psychometric & RIASEC Engine",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Unlock your true cognitive architecture, 0-200 dimension scores, hero archetype, and 48 high-impact career pathways.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        Spacer(Modifier.height(8.dp))

                        // Language Switcher Chip
                        AssistChip(
                            onClick = { showLanguagePicker = true },
                            label = { Text("${selectedLanguage.flagEmoji} ${selectedLanguage.nativeName} (${selectedLanguage.code.uppercase()})", fontWeight = FontWeight.Bold) },
                            leadingIcon = {
                                Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        )

                        Spacer(Modifier.height(16.dp))

                        // Full 130-Q Deep Dive Card
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    isExpressMode = false
                                    currentQuestionIndex = 0
                                    currentStep = AssessmentStep.QUESTIONS
                                }
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🌟 Comprehensive Deep Dive", fontWeight = FontWeight.Black, fontSize = 16.sp)
                                    Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.primary) {
                                        Text("130 Questions", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "Rigorous 6-dimension psychometric evaluation with maximum precision for career matching and archetype calibration (~10-15 mins).",
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        // Express 24-Q Card
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    isExpressMode = true
                                    currentQuestionIndex = 0
                                    currentStep = AssessmentStep.QUESTIONS
                                }
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("⚡ Express Assessment", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.secondary) {
                                        Text("24 Questions", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "Rapid calibration testing top traits across all 6 core dimensions (~2-3 mins).",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }

                // STEP 2: QUESTIONS STREAM
                AssessmentStep.QUESTIONS -> {
                    val currentQuestion = activeQuestions.getOrNull(currentQuestionIndex)
                    if (currentQuestion != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Dimension Pill
                            Surface(
                                shape = CircleShape,
                                color = Color(currentQuestion.dimension.baseColorHex).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = currentQuestion.dimension.displayName.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(currentQuestion.dimension.baseColorHex),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }

                            Spacer(Modifier.height(20.dp))

                            // Question Prompt Card
                            Card(
                                shape = RoundedCornerShape(22.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    val localizedPrompt = LocalizedAssessmentEngine.getLocalizedQuestion(
                                        currentQuestion.id,
                                        currentQuestion.prompt,
                                        selectedLanguage
                                    )
                                    Text(
                                        text = "\"$localizedPrompt\"",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        lineHeight = 26.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Likert Scale 1 to 5 Buttons
                            val currentVal = answers[currentQuestion.id] ?: 0
                            val scaleLabels = LocalizedAssessmentEngine.getLikertLabels(selectedLanguage)

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                scaleLabels.forEachIndexed { idx, label ->
                                    val ratingValue = idx + 1
                                    val isSelected = currentVal == ratingValue

                                    Surface(
                                        onClick = {
                                            answers[currentQuestion.id] = ratingValue
                                            if (currentQuestionIndex < activeQuestions.size - 1) {
                                                currentQuestionIndex++
                                            } else {
                                                currentStep = AssessmentStep.ARCHETYPE_REVEAL
                                            }
                                        },
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        border = if (isSelected) BorderStroke(2.dp, Color.White) else null,
                                        modifier = Modifier.fillMaxWidth().height(42.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = label,
                                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "$ratingValue",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            // Back / Forward Controls
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TextButton(
                                    onClick = {
                                        if (currentQuestionIndex > 0) currentQuestionIndex--
                                    },
                                    enabled = currentQuestionIndex > 0
                                ) {
                                    Text("← Previous")
                                }

                                if (currentVal > 0 && currentQuestionIndex < activeQuestions.size - 1) {
                                    TextButton(onClick = { currentQuestionIndex++ }) {
                                        Text("Next →")
                                    }
                                }
                            }
                        }
                    }
                }

                // STEP 3: ARCHETYPE REVEAL & 0-200 SCORES
                AssessmentStep.ARCHETYPE_REVEAL -> {
                    val result = assessmentResult
                    if (result != null) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Hero Archetype Reveal
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(22.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(result.archetype.icon, fontSize = 48.sp)
                                        Spacer(Modifier.height(8.dp))
                                        val (locName, locTitle, locDesc) = LocalizedAssessmentEngine.getLocalizedPsychometricArchetype(
                                            result.archetype.id,
                                            result.archetype.name,
                                            result.archetype.title,
                                            result.archetype.description,
                                            selectedLanguage
                                        )
                                        Text(
                                            text = locName,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 24.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = locTitle,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFFD700)
                                        )
                                        Spacer(Modifier.height(10.dp))
                                        Text(
                                            text = locDesc,
                                            fontSize = 12.sp,
                                            lineHeight = 18.sp,
                                            color = Color.White.copy(alpha = 0.85f),
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(Modifier.height(14.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.White.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "RIASEC Signature: ${result.topRiasecCode} • Overall: ${result.overallScore}/1200",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // 0-200 Dimension Breakdown
                            item {
                                Card(
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("📊 0-200 Psychometric Breakdown", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Spacer(Modifier.height(12.dp))

                                        result.dimensionScores.forEach { (dim, score) ->
                                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(dim.displayName, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                                    Text("$score / 200", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(dim.baseColorHex))
                                                }
                                                Spacer(Modifier.height(4.dp))
                                                LinearProgressIndicator(
                                                    progress = { score / 200f },
                                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                                                    color = Color(dim.baseColorHex)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Superpower & Growth Area
                            item {
                                Card(
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("⚡ Core Superpower", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                        Text(result.archetype.superpower, fontSize = 12.sp, modifier = Modifier.padding(vertical = 2.dp))
                                        Spacer(Modifier.height(8.dp))
                                        Text("🌱 Growth Frontier", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFF59E0B))
                                        Text(result.archetype.growthArea, fontSize = 12.sp, modifier = Modifier.padding(vertical = 2.dp))
                                    }
                                }
                            }

                            // Navigation to Careers Explorer
                            item {
                                Button(
                                    onClick = { currentStep = AssessmentStep.CAREERS_EXPLORER },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(Icons.Default.Work, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Explore 48 RIASEC Career Matches", fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.height(30.dp))
                            }
                        }
                    }
                }

                // STEP 4: 48 CAREER MATCHES EXPLORER
                AssessmentStep.CAREERS_EXPLORER -> {
                    val result = assessmentResult
                    if (result != null) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text("🎯 Matched for RIASEC ${result.topRiasecCode}", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(top = 4.dp))
                            }

                            items(result.topCareers) { career ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(career.title, fontWeight = FontWeight.Black, fontSize = 14.sp, modifier = Modifier.weight(1f))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFF4CAF50).copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    "${career.matchPercentage}% Match",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color(0xFF4CAF50),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(Modifier.height(2.dp))
                                        Text(career.salaryRange, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        Spacer(Modifier.height(4.dp))
                                        Text(career.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)

                                        Spacer(Modifier.height(8.dp))
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            items(career.topSkills) { skill ->
                                                Surface(
                                                    shape = CircleShape,
                                                    color = MaterialTheme.colorScheme.surface
                                                ) {
                                                    Text(skill, fontSize = 9.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                Button(
                                    onClick = {
                                        Toast.makeText(context, "Assessment Saved to Cloud Firestore! 🎉", Toast.LENGTH_SHORT).show()
                                        onCompleteOnboarding(HeroArchetype.WARRIOR, emptyMap())
                                    },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(Icons.Default.RocketLaunch, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Complete & Launch LifeScore", fontWeight = FontWeight.Black)
                                }
                                Spacer(Modifier.height(30.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showLanguagePicker) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showLanguagePicker = false }) {
            Card(
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Select Language / Idioma / 语言 / لغة / भाषा", fontWeight = FontWeight.Black, fontSize = 16.sp)
                    Text("Choose language for your psychometric test", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    Spacer(Modifier.height(14.dp))

                    LanguageManager.getSupportedLanguages().forEach { lang ->
                        Surface(
                            onClick = {
                                selectedLanguage = lang
                                LanguageManager.setAppLanguage(lang)
                                showLanguagePicker = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedLanguage == lang) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(lang.flagEmoji, fontSize = 20.sp)
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(lang.nativeName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(lang.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                }
                                if (selectedLanguage == lang) {
                                    Text("✓", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
