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
        AmbientSoundTrack("rain", "Gentle Rain on Leaves", "Nature", "🌧️", 0.75f, "Soothing rhythmic rainfall through a lush green canopy."),
        AmbientSoundTrack("ocean", "Pacific Ocean Swells", "Water", "🌊", 0.65f, "Deep calming rhythmic waves washing against soft coastal sands."),
        AmbientSoundTrack("fire", "Cabin Hearth Fire", "Warmth", "🔥", 0.70f, "Gentle crackling cedar wood logs in a cozy mountain fireplace."),
        AmbientSoundTrack("forest", "Whispering Pine Forest", "Nature", "🌲", 0.60f, "Crisp mountain breeze through ancient pines and distant birdsong."),
        AmbientSoundTrack("bowls", "Tibetan Singing Bowls", "Zen", "🧘", 0.80f, "Harmonic 432Hz resonant frequency bells for deep meditative state."),
        AmbientSoundTrack("whitenoise", "Deep Space White Noise", "Focus", "🌌", 0.50f, "Smooth pink & brown noise spectrum to block out all room distractions.")
    )

    val sleepStories: List<SleepStory> = listOf(
        SleepStory(
            id = "ancient_forest",
            title = "The Ancient Forest Sanctuary",
            narrator = "Elena Vance (Calm Voice)",
            durationMinutes = 20,
            coverEmoji = "🌲",
            storyIntro = "Journey into a moss-carpeted ancient redwood forest as twilight settles and the quiet stars awaken.",
            storyScript = listOf(
                "Take a deep, slow breath in through your nose... and let it gently release through your mouth.",
                "Imagine stepping onto a soft, emerald moss trail beneath the shelter of towering ancient redwood trees.",
                "The evening air is crisp and fragrant with cedar and damp earth.",
                "A gentle silver mist curls between the grand trunks, softening every sound in the peaceful forest.",
                "With every step, your shoulders drop, tension melts from your brow, and a profound stillness surrounds you.",
                "High above, through the whispering branches, the first evening stars begin to glow with steady, quiet warmth.",
                "You find a smooth mossy clearing beside a crystal clear mountain stream, flowing gently over smooth stones.",
                "As you rest by the water, listen to the gentle rhythm of the stream, carrying away every thought of the day.",
                "You are safe. You are at peace. Drift deeper into sweet, restorative sleep."
            )
        ),
        SleepStory(
            id = "alpine_express",
            title = "Midnight Alpine Express",
            narrator = "Julian Sterling (Deep Baritone)",
            durationMinutes = 25,
            coverEmoji = "🚂",
            storyIntro = "A peaceful overnight train journey climbing through snow-dusted Swiss mountain peaks beneath the Northern lights.",
            storyScript = listOf(
                "Welcome aboard the Midnight Alpine Express. Settle deeply into your warm velvet cabin berth.",
                "The train glides forward with a steady, soothing rhythmic hum along the smooth silver tracks.",
                "Outside your window, moonlit glaciers and snow-covered pines glide softly past in the midnight silence.",
                "The gentle rocking of the carriage lulls your mind into effortless relaxation.",
                "Warm golden light from your reading lamp casts a cozy glow across the wooden cabin walls.",
                "With each passing mile, your body feels heavier, softer, sinking into the plush mattress.",
                "The rhythmic click-clack of the train becomes a quiet lullaby, carrying you across serene valleys of sleep."
            )
        ),
        SleepStory(
            id = "kyoto_starlight",
            title = "Starlight Over Kyoto",
            narrator = "Kaori Takahashi (Serene Zen)",
            durationMinutes = 18,
            coverEmoji = "🏯",
            storyIntro = "Wander through moonlit stone pathways and bamboo groves of an ancient Kyoto garden as lantern lights reflect on koi ponds.",
            storyScript = listOf(
                "Allow your breath to find its natural, effortless rhythm.",
                "Step quietly onto the smooth raked gravel of a tranquil Zen temple garden in Kyoto.",
                "Paper lanterns cast a warm amber glow over calm stone bridges and blooming lotus ponds.",
                "A gentle night breeze rustles the tall green bamboo stalks, sounding like distant ocean waves.",
                "Breathe in the subtle scent of sandalwood incense and rain-washed pine.",
                "Everything here exists in perfect balance and tranquility.",
                "Let go of tomorrow. Rest in the timeless beauty of this peaceful night."
            )
        )
    )
}
