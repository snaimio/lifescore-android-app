package com.lifescore.app.domain.model.selfimprovement

data class AmbientSoundTrack(
    val id: String,
    val name: String,
    val category: String,
    val iconEmoji: String,
    val defaultVolume: Float = 0.7f,
    val description: String
)

data class SleepStory(
    val id: String,
    val title: String,
    val narrator: String,
    val durationMinutes: Int,
    val coverEmoji: String,
    val storyIntro: String,
    val storyScript: List<String>
)

object SoundscapeCatalog {
    val ambientTracks: List<AmbientSoundTrack> = listOf(
        AmbientSoundTrack("rain", "Canopy Rain on Leaves", "Nature", "🌧️", 0.75f, "Soothing rhythmic rainfall through a lush green canopy."),
        AmbientSoundTrack("ocean", "Pacific Ocean Waves", "Water", "🌊", 0.65f, "Deep calming rhythmic waves washing against soft coastal sands."),
        AmbientSoundTrack("stream", "Mountain Brook & Stream", "Water", "🏞️", 0.70f, "Crystal clear water trickling gently over smooth river stones."),
        AmbientSoundTrack("forest", "Whispering Pine Forest", "Forest", "🌲", 0.60f, "Crisp mountain breeze rustling through ancient pines and foliage."),
        AmbientSoundTrack("crickets", "Night Meadow & Crickets", "Night", "🌙", 0.65f, "Peaceful nocturnal crickets and gentle summer meadow ambiance."),
        AmbientSoundTrack("thunder", "Distant Rolling Thunder", "Storm", "⛈️", 0.55f, "Subtle rolling thunder resonance amidst soft rainfall."),
        AmbientSoundTrack("waterfall", "Woodland Waterfall", "Water", "💦", 0.60f, "Cascading fresh mountain waterfall and peaceful mist."),
        AmbientSoundTrack("fire", "Wilderness Campfire", "Warmth", "🔥", 0.65f, "Gentle crackling cedar wood logs under a starry night sky.")
    )

    val sleepStories: List<SleepStory> = listOf(
        SleepStory(
            id = "ancient_forest",
            title = "The Ancient Redwood Sanctuary",
            narrator = "Elena Vance (Calm Nature Guide)",
            durationMinutes = 20,
            coverEmoji = "🌲",
            storyIntro = "Journey into a moss-carpeted ancient redwood forest as twilight settles and the quiet stars awaken.",
            storyScript = listOf(
                "Take a deep, slow breath in through your nose, and let it gently release through your mouth.",
                "Imagine stepping onto a soft, emerald moss trail beneath the shelter of towering ancient redwood trees.",
                "The evening air is crisp and fragrant with cedar, pine, and damp earth.",
                "A gentle silver mist curls between the grand trunks, softening every sound in the peaceful forest.",
                "With every step, your shoulders drop, tension melts from your brow, and a profound stillness surrounds you.",
                "High above, through the whispering branches, the first evening stars begin to glow with steady, quiet warmth.",
                "You find a smooth mossy clearing beside a crystal clear mountain stream, flowing gently over smooth stones.",
                "As you rest by the water, listen to the gentle rhythm of the stream, carrying away every thought of the day.",
                "You are safe. You are at peace. Drift deeper into sweet, restorative sleep."
            )
        ),
        SleepStory(
            id = "ocean_cove",
            title = "Starlit Coastal Cove",
            narrator = "Marcus Thorne (Ocean Naturalist)",
            durationMinutes = 22,
            coverEmoji = "🌊",
            storyIntro = "Rest on warm white sand beside a peaceful ocean cove as rhythmic turquoise swells wash the shore beneath the Milky Way.",
            storyScript = listOf(
                "Close your eyes and listen to the eternal rhythm of the ocean waves.",
                "You are standing on a secluded beach of soft, cool white sand, surrounded by gentle sea breezes.",
                "The rhythmic surge of each wave gently reaches the shore, then glides softly back into the deep ocean.",
                "Above you, an endless canopy of brilliant stars and constellations shines with calm clarity.",
                "With every ocean breath, a wave of warm relaxation washes through your body from your head to your toes.",
                "The gentle murmur of the water lulls your thoughts into quiet tranquility.",
                "You sink comfortably into the soft sand, completely supported by the earth.",
                "Feel the cool night ocean breeze brush your skin as you surrender to restorative rest."
            )
        ),
        SleepStory(
            id = "mountain_valley",
            title = "Moonlit Alpine Valley",
            narrator = "Julian Sterling (Mountain Guide)",
            durationMinutes = 25,
            coverEmoji = "🏔️",
            storyIntro = "A peaceful twilight walk through an alpine meadow surrounded by silent snow-capped peaks and wildflower breezes.",
            storyScript = listOf(
                "Inhale the clean, crisp fragrance of mountain wildflowers and wild thyme.",
                "You walk gently along a winding mountain trail overlooking a serene alpine valley.",
                "The majestic peaks around you glow in soft shades of indigo, violet, and silver beneath the rising moon.",
                "A crystal brook flows through the green meadow, singing a quiet lullaby as it weaves between stones.",
                "Every breath of fresh mountain air cleanses your mind and brings deep stillness to your spirit.",
                "Find a peaceful resting place among the soft grasses beneath the open, moonlit sky.",
                "The night is calm, the mountain is silent, and sleep welcomes you with open arms."
            )
        )
    )
}
