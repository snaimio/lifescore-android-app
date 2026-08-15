package com.lifescore.app

import com.lifescore.app.core.util.CardGenerator
import com.lifescore.app.core.util.CardTheme
import com.lifescore.app.core.util.ShareCardData
import com.lifescore.app.domain.model.DimensionType
import org.junit.Assert.*
import org.junit.Test

class CardGeneratorTest {

    @Test
    fun testGenerateCaptionFormat() {
        val sampleData = ShareCardData(
            userName = "Achiever",
            score = 820,
            level = 4,
            streak = 14,
            title = "Habit Master",
            dimensionScores = DimensionType.values().associateWith { 80 }
        )

        val caption = CardGenerator.generateCaption(sampleData)

        assertTrue(caption.contains("820/1000"))
        assertTrue(caption.contains("14-day streak"))
        assertTrue(caption.contains("https://lifescore.app/dl"))
        assertTrue(caption.contains("#LifeScore"))
    }

    @Test
    fun testCardThemesCompleteness() {
        val themes = CardTheme.values()
        assertEquals(4, themes.size)

        themes.forEach { theme ->
            assertNotNull(theme.displayName)
            assertTrue(theme.topColor != 0)
            assertTrue(theme.bottomColor != 0)
            assertTrue(theme.accentColor != 0)
        }
    }

    @Test
    fun testShareCardDataIntegrity() {
        val scores = mapOf(
            DimensionType.HEALTH to 90,
            DimensionType.WEALTH to 75,
            DimensionType.RELATIONSHIPS to 85,
            DimensionType.CAREER to 95,
            DimensionType.LEARNING to 80,
            DimensionType.FITNESS to 100,
            DimensionType.MENTAL_HEALTH to 70,
            DimensionType.SOCIAL_LIFE to 85
        )

        val cardData = ShareCardData(
            userName = "Champion",
            score = 850,
            level = 5,
            streak = 21,
            title = "Grandmaster",
            dimensionScores = scores,
            yearTag = "2026"
        )

        assertEquals(850, cardData.score)
        assertEquals(8, cardData.dimensionScores.size)
        assertEquals(21, cardData.streak)
        assertEquals("2026", cardData.yearTag)
    }
}
