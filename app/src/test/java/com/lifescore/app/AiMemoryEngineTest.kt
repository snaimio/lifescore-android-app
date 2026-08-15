package com.lifescore.app

import com.lifescore.app.core.util.AiMemoryEngine
import com.lifescore.app.core.util.MemoryCategory
import com.lifescore.app.core.util.UserMemoryNode
import com.lifescore.app.data.repository.GeminiCoachRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class AiMemoryEngineTest {

    @Test
    fun testDefaultMemoriesCompleteness() {
        val memories = AiMemoryEngine.getDefaultMemories()
        assertTrue(memories.size >= 5)

        val categories = memories.map { it.category }
        assertTrue(categories.contains(MemoryCategory.HABIT_STRUGGLE))
        assertTrue(categories.contains(MemoryCategory.TEMPORAL_PATTERN))
        assertTrue(categories.contains(MemoryCategory.ACHIEVEMENT))
        assertTrue(categories.contains(MemoryCategory.PSYCHOMETRIC_PROFILE))
        assertTrue(categories.contains(MemoryCategory.JOURNAL_THEME))
    }

    @Test
    fun testBuildSystemContextPromptWithMemories() {
        val memories = listOf(
            UserMemoryNode(category = MemoryCategory.HABIT_STRUGGLE, title = "Morning Friction", detail = "Struggles with 6am workouts"),
            UserMemoryNode(category = MemoryCategory.TEMPORAL_PATTERN, title = "Tuesday Peak", detail = "94% task completion on Tuesdays")
        )

        val prompt = AiMemoryEngine.buildSystemContextPrompt(
            memories = memories,
            archetypeName = "The Architect",
            lifeScore = 820,
            streak = 10
        )

        assertTrue(prompt.contains("The Architect"))
        assertTrue(prompt.contains("820/1000"))
        assertTrue(prompt.contains("10 days"))
        assertTrue(prompt.contains("Morning Friction"))
        assertTrue(prompt.contains("Tuesday Peak"))
    }

    @Test
    fun testBehavioralReflectionsContent() {
        val reflections = AiMemoryEngine.getBehavioralReflections()
        assertTrue(reflections.size >= 3)

        val tuesdayRef = reflections.find { it.headline.contains("Tuesday", ignoreCase = true) }
        assertNotNull(tuesdayRef)
        assertTrue(tuesdayRef!!.metricHighlight.contains("94%"))

        val morningRef = reflections.find { it.headline.contains("Morning", ignoreCase = true) }
        assertNotNull(morningRef)
        assertTrue(morningRef!!.metricHighlight.contains("38%"))
    }

    @Test
    fun testRecommendNextChallengeLogic() {
        val nextAfterFitness = AiMemoryEngine.recommendNextChallenge("30-Day Morning Hydration & 8k Steps")
        assertTrue(nextAfterFitness.contains("Deep Reading Immersion"))
        assertTrue(nextAfterFitness.contains("+600 XP"))

        val nextAfterReading = AiMemoryEngine.recommendNextChallenge("30-Day 20-Min Deep Reading Immersion")
        assertTrue(nextAfterReading.contains("Deep Work Block"))

        val nextDefault = AiMemoryEngine.recommendNextChallenge("Other Quest")
        assertTrue(nextDefault.contains("Zero Impulse Spending"))
    }

    @Test
    fun testAskCoachWithMemoryDeterministicReply() = runBlocking {
        val repo = GeminiCoachRepositoryImpl(apiKey = null) // Offline mode
        val memoryContext = AiMemoryEngine.buildSystemContextPrompt(
            memories = AiMemoryEngine.getDefaultMemories(),
            archetypeName = "The Architect"
        )

        val reply1 = repo.askCoachWithMemory("What challenge should I do next after my 30-day fitness quest?", memoryContext)
        assertTrue(reply1.contains("Next Challenge Recommendation"))
        assertTrue(reply1.contains("Tuesdays"))

        val reply2 = repo.askCoachWithMemory("Why do I struggle with morning tasks?", memoryContext)
        assertTrue(reply2.contains("Morning Friction Strategy"))
        assertTrue(reply2.contains("Architect"))
    }
}
