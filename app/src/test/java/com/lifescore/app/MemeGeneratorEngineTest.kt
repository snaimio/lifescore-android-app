package com.lifescore.app

import com.lifescore.app.core.util.MemeGeneratorEngine
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.MemeCategory
import org.junit.Assert.*
import org.junit.Test

class MemeGeneratorEngineTest {

    @Test
    fun testAll8MemeTemplatesIntegrity() {
        val templates = MemeGeneratorEngine.getAllTemplates()
        assertEquals(8, templates.size)

        templates.forEach { template ->
            assertTrue("Template ID should not be blank", template.id.isNotBlank())
            assertTrue("Title should not be blank", template.title.isNotBlank())
            assertTrue("Top text should not be blank", template.topTextDefault.isNotBlank())
            assertTrue("Bottom text should not be blank", template.bottomTextDefault.isNotBlank())
            assertTrue("Emoji art should not be blank", template.emojiArt.isNotBlank())
            assertTrue("Background gradient must have at least 2 colors", template.backgroundGradientColors.size >= 2)
        }

        val categories = templates.map { it.category }.toSet()
        assertTrue(categories.contains(MemeCategory.DIMENSION_STRUGGLE))
        assertTrue(categories.contains(MemeCategory.STREAK_VICTORY))
        assertTrue(categories.contains(MemeCategory.EXPECTATION_VS_REALITY))
        assertTrue(categories.contains(MemeCategory.DEEP_WORK_GRIND))
        assertTrue(categories.contains(MemeCategory.MEDITATION_ZEN))
    }

    @Test
    fun testContextualMemeGeneration() {
        val meme = MemeGeneratorEngine.generateContextualMeme(
            strongestDimension = DimensionType.CAREER,
            weakestDimension = DimensionType.MENTAL_HEALTH,
            streak = 21,
            score = 840
        )

        assertEquals(840, meme.userScore)
        assertEquals(21, meme.userStreak)
        assertEquals(DimensionType.MENTAL_HEALTH, meme.targetDimension)
        assertTrue(meme.topText.contains("CAREER"))
        assertTrue(meme.bottomText.contains("MENTAL HEALTH"))
    }

    @Test
    fun testRemixCaptionsForVariousTemplates() {
        val (top1, bottom1) = MemeGeneratorEngine.remixCaptions("meme_dimension_gap", DimensionType.LEARNING, DimensionType.FITNESS)
        assertTrue(top1.contains("LEARNING"))
        assertTrue(bottom1.contains("FITNESS"))

        val (top2, bottom2) = MemeGeneratorEngine.remixCaptions("meme_sigma_streak")
        assertTrue(top2.contains("DISCIPLINE"))
        assertTrue(bottom2.contains("EXCUSES"))
    }

    @Test
    fun testShareCaptionFormatWithHashtags() {
        val meme = MemeGeneratorEngine.generateContextualMeme(
            strongestDimension = DimensionType.FITNESS,
            weakestDimension = DimensionType.LEARNING,
            streak = 14,
            score = 780
        )

        val shareText = MemeGeneratorEngine.generateShareCaption(meme)
        assertTrue(shareText.contains("LifeScore: 780 • 14d Streak 🔥"))
        assertTrue(shareText.contains("#LifeScore"))
        assertTrue(shareText.contains("#HabitTracker"))
        assertTrue(shareText.contains("#ViralMemes"))
    }
}
