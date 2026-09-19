package com.lifescore.app

import com.lifescore.app.core.audio.NatureSoundEngine
import com.lifescore.app.domain.model.selfimprovement.SoundscapeCatalog
import com.lifescore.app.presentation.ui.sleep.SleepUiState
import org.junit.Assert.*
import org.junit.Test

class NatureSoundEngineTest {

    @Test
    fun soundscapeCatalog_containsOnlyPureNatureSounds_noInstruments() {
        val forbiddenTerms = listOf("bowl", "bell", "instrument", "singing", "train", "express", "synthesizer", "white noise")
        
        SoundscapeCatalog.ambientTracks.forEach { track ->
            val lowerName = track.name.lowercase()
            val lowerDesc = track.description.lowercase()
            
            forbiddenTerms.forEach { term ->
                assertFalse(
                    "Track ${track.id} contains forbidden non-nature term: $term",
                    lowerName.contains(term) || lowerDesc.contains(term)
                )
            }
        }
    }

    @Test
    fun sleepStories_containOnlyNatureJourneys_noVehiclesOrInstruments() {
        val forbiddenStoryPatterns = listOf(
            Regex("\\btrain\\b", RegexOption.IGNORE_CASE),
            Regex("\\bexpress\\b", RegexOption.IGNORE_CASE),
            Regex("\\bcar\\b", RegexOption.IGNORE_CASE),
            Regex("\\bautomobile\\b", RegexOption.IGNORE_CASE),
            Regex("\\bengine\\b", RegexOption.IGNORE_CASE),
            Regex("\\binstrument\\b", RegexOption.IGNORE_CASE),
            Regex("\\bairplane\\b", RegexOption.IGNORE_CASE)
        )

        SoundscapeCatalog.sleepStories.forEach { story ->
            val text = "${story.title} ${story.storyIntro}"

            forbiddenStoryPatterns.forEach { pattern ->
                assertFalse(
                    "Story ${story.id} contains non-nature vehicle/instrument pattern: ${pattern.pattern}",
                    pattern.containsMatchIn(text)
                )
            }
        }
    }

    @Test
    fun natureSoundEngine_managesTrackVolumesAndStateCleanly() {
        val engine = NatureSoundEngine()

        engine.setTrackVolume("rain", 0.8f)
        assertEquals(0.8f, engine.getActiveVolumes()["rain"] ?: 0f, 0.01f)

        engine.setTrackVolume("ocean", 0.5f)
        assertEquals(2, engine.getActiveVolumes().size)

        engine.removeTrack("rain")
        assertNull(engine.getActiveVolumes()["rain"])
        assertEquals(1, engine.getActiveVolumes().size)

        engine.stopAll()
        assertTrue(engine.getActiveVolumes().isEmpty())
        assertFalse(engine.isPlaying())

        engine.release()
    }

    @Test
    fun sleepUiState_initializesWithNatureTracksAndNoXp() {
        val state = SleepUiState()

        assertTrue(state.ambientTracks.isNotEmpty())
        assertTrue(state.sleepStories.isNotEmpty())
        assertTrue(state.activeTrackVolumes.isEmpty())
        assertFalse(state.isStoryPlaying)
        assertFalse(state.isTimerActive)
        assertEquals(30, state.sleepTimerMinutes)

        // Verify all 8 ambient tracks are valid nature tracks
        val trackIds = state.ambientTracks.map { it.id }.toSet()
        assertTrue(trackIds.contains("rain"))
        assertTrue(trackIds.contains("ocean"))
        assertTrue(trackIds.contains("stream"))
        assertTrue(trackIds.contains("forest"))
        assertTrue(trackIds.contains("crickets"))
        assertTrue(trackIds.contains("thunder"))
        assertTrue(trackIds.contains("waterfall"))
        assertTrue(trackIds.contains("fire"))

        // Verify zero XP references
        assertFalse(state.snackbarMessage?.contains("XP") ?: false)
    }
}
