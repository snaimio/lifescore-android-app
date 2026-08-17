package com.lifescore.app.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.lifescore.app.BuildConfig
import com.lifescore.app.data.local.dao.JournalDao
import com.lifescore.app.data.local.entity.JournalEntity
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.JournalEntry
import com.lifescore.app.domain.model.JournalMood
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

interface JournalRepository {
    fun getAllEntries(): Flow<List<JournalEntry>>
    suspend fun saveEntry(
        mood: JournalMood,
        text: String,
        dimension: DimensionType
    ): JournalEntry
    suspend fun deleteEntry(entry: JournalEntry)
}

class JournalRepositoryImpl(
    private val dao: JournalDao,
    private val apiKey: String? = BuildConfig.GEMINI_API_KEY
) : JournalRepository {

    private val generativeModel by lazy {
        if (!apiKey.isNullOrBlank() && apiKey != "DEMO_KEY") {
            try {
                GenerativeModel(
                    modelName = BuildConfig.GEMINI_MODEL,
                    apiKey = apiKey
                )
            } catch (_: Exception) {
                null
            }
        } else null
    }

    override fun getAllEntries(): Flow<List<JournalEntry>> {
        return dao.getAllJournalEntries()
            .onStart { seedInitialEntryIfEmpty() }
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun saveEntry(
        mood: JournalMood,
        text: String,
        dimension: DimensionType
    ): JournalEntry = withContext(Dispatchers.IO) {
        val todayIso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val aiReflection = generateAiReflection(mood, text, dimension)

        val entry = JournalEntry(
            id = UUID.randomUUID().toString(),
            dateIso = todayIso,
            mood = mood,
            textContent = text,
            dimensionTag = dimension,
            aiReflection = aiReflection,
            audioDurationSeconds = 0,
            createdAt = System.currentTimeMillis()
        )

        dao.insertJournalEntry(entry.toEntity())
        return@withContext entry
    }

    override suspend fun deleteEntry(entry: JournalEntry) {
        dao.deleteJournalEntry(entry.toEntity())
    }

    private suspend fun generateAiReflection(mood: JournalMood, text: String, dimension: DimensionType): String {
        if (generativeModel != null) {
            try {
                val prompt = """
                    You are the cognitive psychologist & high-performance coach AI inside LifeScore.
                    User logged this journal entry:
                    Mood: ${mood.title} (${mood.emoji})
                    Target Pillar: ${dimension.displayName}
                    Reflection Text: "$text"

                    Provide a concise, profound 2-sentence psychological reflection and actionable framing:
                    Sentence 1: Validate their emotional baseline with empathetic reframing.
                    Sentence 2: One concrete micro-action for tomorrow.
                """.trimIndent()
                val response = generativeModel?.generateContent(prompt)
                val responseText = response?.text?.trim()
                if (!responseText.isNullOrBlank()) return responseText
            } catch (_: Exception) {}
        }
        return getOfflineReflection(mood, dimension)
    }

    private fun getOfflineReflection(mood: JournalMood, dimension: DimensionType): String {
        return when (mood) {
            JournalMood.EXCITED -> "High momentum detected in ${dimension.displayName}. Harness this peak state by outlining your most audacious weekly milestone tonight."
            JournalMood.HAPPY -> "Gratitude and steady execution create compounding returns in ${dimension.displayName}. Anchor this positive emotion to solidify the habit loop."
            JournalMood.NEUTRAL -> "Consistency during neutral days is the true hallmark of mastery. Protect your uninterrupted focus block tomorrow."
            JournalMood.STRESSED -> "Friction is the catalyst for growth in ${dimension.displayName}. Take 5 deep breaths, disconnect from screens for 15 minutes, and prioritize one single high-leverage micro-win."
            JournalMood.TIRED -> "Rest is an active recovery strategy, not wasted time. Prioritize 8 hours of sleep and high hydration to restore baseline energy."
        }
    }

    private suspend fun seedInitialEntryIfEmpty() {
        val todayIso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        if (dao.getEntryByDate(todayIso) == null) {
            dao.insertJournalEntry(
                JournalEntity(
                    id = "initial_entry",
                    dateIso = todayIso,
                    mood = JournalMood.HAPPY,
                    textContent = "Completed my first AI Quest protocol today! Feeling focused and ready to level up my 8 life dimensions.",
                    dimensionTag = DimensionType.LEARNING,
                    aiReflection = "Strong early activation. Logging daily thoughts creates a powerful cognitive mirror for exponential self-mastery.",
                    createdAt = System.currentTimeMillis() - 3600000
                )
            )
        }
    }

    private fun JournalEntity.toDomain() = JournalEntry(
        id = id,
        dateIso = dateIso,
        mood = mood,
        textContent = textContent,
        dimensionTag = dimensionTag,
        aiReflection = aiReflection,
        audioDurationSeconds = audioDurationSeconds,
        createdAt = createdAt
    )

    private fun JournalEntry.toEntity() = JournalEntity(
        id = id,
        dateIso = dateIso,
        mood = mood,
        textContent = textContent,
        dimensionTag = dimensionTag,
        aiReflection = aiReflection,
        audioDurationSeconds = audioDurationSeconds,
        createdAt = createdAt
    )
}
