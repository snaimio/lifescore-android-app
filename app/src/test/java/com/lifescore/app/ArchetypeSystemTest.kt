package com.lifescore.app

import com.lifescore.app.core.util.ArchetypeManager
import com.lifescore.app.core.util.CardGenerator
import com.lifescore.app.core.util.CardTheme
import com.lifescore.app.core.util.ArchetypeShareCardData
import com.lifescore.app.domain.model.DimensionType
import org.junit.Assert.*
import org.junit.Test

class ArchetypeSystemTest {

    @Test
    fun testAll10ArchetypesDataIntegrity() {
        val archetypes = ArchetypeManager.allArchetypes
        assertEquals(10, archetypes.size)

        archetypes.forEach { arch ->
            assertTrue("Archetype ${arch.name} id must not be empty", arch.id.isNotBlank())
            assertTrue("Archetype ${arch.name} icon must not be empty", arch.icon.isNotBlank())
            assertTrue("Archetype ${arch.name} title must not be empty", arch.title.isNotBlank())
            assertTrue("Archetype ${arch.name} overview must not be empty", arch.overview.isNotBlank())
            assertTrue("Archetype ${arch.name} superpower must not be empty", arch.superpower.isNotBlank())
            assertTrue("Archetype ${arch.name} growth must not be empty", arch.growthRecommendation.isNotBlank())

            assertTrue("Archetype ${arch.name} tendencies must have >= 3 items", arch.tendencies.size >= 3)
            assertTrue("Archetype ${arch.name} blind spots must have >= 3 items", arch.blindSpots.size >= 3)
            assertTrue("Archetype ${arch.name} work style must have >= 3 items", arch.workStyle.size >= 3)
            assertTrue("Archetype ${arch.name} relationship style must have >= 3 items", arch.relationshipStyle.size >= 3)
            assertTrue("Archetype ${arch.name} primary dimensions must have >= 2 items", arch.primaryDimensions.size >= 2)
        }
    }

    @Test
    fun testDimensionScoreToArchetypeMapping() {
        // High Career + Wealth -> Architect
        val architectScores = mapOf(
            DimensionType.CAREER to 90,
            DimensionType.WEALTH to 85,
            DimensionType.FITNESS to 50
        )
        val arch1 = ArchetypeManager.mapScoresToArchetype(architectScores)
        assertEquals("The Architect", arch1.name)

        // High Fitness + Health -> Warrior
        val warriorScores = mapOf(
            DimensionType.FITNESS to 95,
            DimensionType.HEALTH to 85,
            DimensionType.CAREER to 50
        )
        val arch2 = ArchetypeManager.mapScoresToArchetype(warriorScores)
        assertEquals("The Warrior", arch2.name)

        // High Health + Relationships -> Healer
        val healerScores = mapOf(
            DimensionType.HEALTH to 90,
            DimensionType.RELATIONSHIPS to 85,
            DimensionType.CAREER to 40
        )
        val arch3 = ArchetypeManager.mapScoresToArchetype(healerScores)
        assertEquals("The Healer", arch3.name)
    }

    @Test
    fun testArchetypeShareCaptionGeneration() {
        val arch = ArchetypeManager.getArchetypeById("architect")
        val caption = ArchetypeManager.generateArchetypeShareCaption(arch, score = 820, level = 6)

        assertTrue(caption.contains("I am The Architect"))
        assertTrue(caption.contains("🏛️"))
        assertTrue(caption.contains("Superpower:"))
        assertTrue(caption.contains("Level 6"))
        assertTrue(caption.contains("820 LifeScore"))
        assertTrue(caption.contains("https://lifescore.app/archetype"))
    }

    @Test
    fun testArchetypeShareCardData() {
        val arch = ArchetypeManager.getArchetypeById("visionary")
        val data = ArchetypeShareCardData(
            userName = "Champion Hero",
            score = 780,
            level = 4,
            archetype = arch
        )

        assertEquals("Champion Hero", data.userName)
        assertEquals(780, data.score)
        assertEquals(4, data.level)
        assertEquals("The Visionary", data.archetype.name)
    }
}
