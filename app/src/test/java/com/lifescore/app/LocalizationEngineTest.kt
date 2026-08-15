package com.lifescore.app

import com.lifescore.app.core.util.AppLanguage
import com.lifescore.app.core.util.LanguageManager
import com.lifescore.app.core.util.LocalizedAssessmentEngine
import com.lifescore.app.domain.model.HeroArchetype
import org.junit.Assert.*
import org.junit.Test

class LocalizationEngineTest {

    @Test
    fun testAll5SupportedLanguagesIntegrity() {
        val langs = LanguageManager.getSupportedLanguages()
        assertEquals(5, langs.size)

        val codes = langs.map { it.code }
        assertTrue(codes.contains("en"))
        assertTrue(codes.contains("es"))
        assertTrue(codes.contains("zh"))
        assertTrue(codes.contains("ar"))
        assertTrue(codes.contains("hi"))
    }

    @Test
    fun testRtlSupportForArabic() {
        val arabic = AppLanguage.fromCode("ar")
        assertEquals(AppLanguage.ARABIC, arabic)
        assertTrue(arabic.isRtl)

        val english = AppLanguage.fromCode("en")
        assertFalse(english.isRtl)

        val spanish = AppLanguage.fromCode("es")
        assertFalse(spanish.isRtl)
    }

    @Test
    fun testLikertScale5OptionsInAllLanguages() {
        AppLanguage.values().forEach { lang ->
            val labels = LocalizedAssessmentEngine.getLikertLabels(lang)
            assertEquals(5, labels.size)
            labels.forEach { label ->
                assertTrue(label.isNotBlank())
            }
        }
    }

    @Test
    fun testAllHeroArchetypesLocalizedIn5Languages() {
        AppLanguage.values().forEach { lang ->
            HeroArchetype.values().forEach { archetype ->
                val (locTitle, locDesc) = LocalizedAssessmentEngine.getLocalizedHeroArchetype(archetype, lang)
                assertTrue("Title should not be blank for ${archetype.name} in ${lang.code}", locTitle.isNotBlank())
                assertTrue("Description should not be blank for ${archetype.name} in ${lang.code}", locDesc.isNotBlank())
            }
        }
    }

    @Test
    fun testPsychometricArchetypesLocalizedIn5Languages() {
        val archetypes = listOf("architect", "sage", "warrior", "healer", "visionary", "alchemist", "mystic", "diplomat", "sovereign", "outlier")
        AppLanguage.values().forEach { lang ->
            archetypes.forEach { id ->
                val (name, title, desc) = LocalizedAssessmentEngine.getLocalizedPsychometricArchetype(id, "Default", "Title", "Desc", lang)
                assertTrue(name.isNotBlank())
                assertTrue(title.isNotBlank())
                assertTrue(desc.isNotBlank())
            }
        }
    }

    @Test
    fun testLocalizedQuestionPromptGeneration() {
        val basePrompt = "I consistently wake up energized and ready to tackle deep focus work."
        val questionId = 12

        val enText = LocalizedAssessmentEngine.getLocalizedQuestion(questionId, basePrompt, AppLanguage.ENGLISH)
        assertEquals(basePrompt, enText)

        val esText = LocalizedAssessmentEngine.getLocalizedQuestion(questionId, basePrompt, AppLanguage.SPANISH)
        assertTrue(esText.contains("¿En qué medida"))

        val zhText = LocalizedAssessmentEngine.getLocalizedQuestion(questionId, basePrompt, AppLanguage.CHINESE)
        assertTrue(zhText.contains("请评估你对以下陈述的赞同程度"))

        val arText = LocalizedAssessmentEngine.getLocalizedQuestion(questionId, basePrompt, AppLanguage.ARABIC)
        assertTrue(arText.contains("إلى أي مدى توافق"))

        val hiText = LocalizedAssessmentEngine.getLocalizedQuestion(questionId, basePrompt, AppLanguage.HINDI)
        assertTrue(hiText.contains("आप इस कथन से कितना सहमत हैं"))
    }
}
