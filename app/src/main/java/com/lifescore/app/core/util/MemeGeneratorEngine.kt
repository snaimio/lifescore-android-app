package com.lifescore.app.core.util

import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.GeneratedMeme
import com.lifescore.app.domain.model.MemeCategory
import com.lifescore.app.domain.model.MemeTemplate

object MemeGeneratorEngine {

    fun getAllTemplates(): List<MemeTemplate> {
        return listOf(
            MemeTemplate(
                id = "meme_dimension_gap",
                title = "The Dimension Gap",
                category = MemeCategory.DIMENSION_STRUGGLE,
                topTextDefault = "MY CAREER DIMENSION: 950/1000",
                bottomTextDefault = "MY SLEEP & MENTAL HEALTH: 320/1000 💀",
                emojiArt = "📈 ➡️ 📉",
                viralTagline = "Perfectly balanced, as all things should not be.",
                backgroundGradientColors = listOf(0xFF1E1B4B, 0xFF4338CA)
            ),
            MemeTemplate(
                id = "meme_sigma_streak",
                title = "Sigma Streak Ascendance",
                category = MemeCategory.STREAK_VICTORY,
                topTextDefault = "WHEN YOU HIT A 14-DAY STREAK",
                bottomTextDefault = "AND SUDDENLY BECOME THE PROTAGONIST 👑",
                emojiArt = "🔥 ⚔️ 🛡️",
                viralTagline = "Discipline is the only cheat code.",
                backgroundGradientColors = listOf(0xFF831843, 0xFFBE185D)
            ),
            MemeTemplate(
                id = "meme_3am_empire",
                title = "3 AM Empire Architect",
                category = MemeCategory.EXPECTATION_VS_REALITY,
                topTextDefault = "ME RE-STRUCTURING MY LIFE AT 3 AM",
                bottomTextDefault = "ME STRUGGLING TO DRINK WATER AT 8 AM 🤡",
                emojiArt = "🧠 ➡️ 🫥",
                viralTagline = "The visionary mind works in mysterious hours.",
                backgroundGradientColors = listOf(0xFF064E3B, 0xFF047857)
            ),
            MemeTemplate(
                id = "meme_zen_master",
                title = "Instant Zen Guru",
                category = MemeCategory.MEDITATION_ZEN,
                topTextDefault = "ME AFTER 2 MINUTES OF BOX BREATHING:",
                bottomTextDefault = "\"I AM ONE WITH THE COSMOS\" 🧘✨",
                emojiArt = "🌿 🧘 🌌",
                viralTagline = "Inner peace unlocked in 120 seconds.",
                backgroundGradientColors = listOf(0xFF14532D, 0xFF16A34A)
            ),
            MemeTemplate(
                id = "meme_deep_work_flow",
                title = "Deep Work Tunnel",
                category = MemeCategory.DEEP_WORK_GRIND,
                topTextDefault = "ENTERING 4 HOURS OF DEEP CODE FOCUS",
                bottomTextDefault = "TIME HAS NO MEANING IN THE FLOW STATE ⚡",
                emojiArt = "💻 ⚡ ⏳",
                viralTagline = "10,000 hours begins with one deep session.",
                backgroundGradientColors = listOf(0xFF0F172A, 0xFF334155)
            ),
            MemeTemplate(
                id = "meme_coin_splurge",
                title = "Reward Store Splurge",
                category = MemeCategory.STREAK_VICTORY,
                topTextDefault = "FINISHED 5 HABIT QUESTS (+75 COINS)",
                bottomTextDefault = "REDEEMS 'WATCH NETFLIX GUILT-FREE' 🎁",
                emojiArt = "🪙 🍿 🛋️",
                viralTagline = "Earn your dopamine, don't borrow it.",
                backgroundGradientColors = listOf(0xFF701A75, 0xFFA21CAF)
            ),
            MemeTemplate(
                id = "meme_gym_vs_stretch",
                title = "Gym vs Mobility",
                category = MemeCategory.DIMENSION_STRUGGLE,
                topTextDefault = "CRUSHES 100KG SQUATS FOR 1 HOUR",
                bottomTextDefault = "CANNOT DO 30 SECONDS OF STRETCHING 🦵",
                emojiArt = "🏋️ ➡️ 🥨",
                viralTagline = "Mobility is the real final boss.",
                backgroundGradientColors = listOf(0xFF7F1D1D, 0xFFB91C1C)
            ),
            MemeTemplate(
                id = "meme_diamond_league",
                title = "League Promotion",
                category = MemeCategory.STREAK_VICTORY,
                topTextDefault = "PROMOTED TO DIAMOND LEAGUE #1 💎",
                bottomTextDefault = "THE COMPETITIVENESS HAS CONSUMED ME 🏆",
                emojiArt = "👑 💎 🚀",
                viralTagline = "Sunday reset brings zero mercy.",
                backgroundGradientColors = listOf(0xFF1E3A8A, 0xFF2563EB)
            )
        )
    }

    fun generateContextualMeme(
        strongestDimension: DimensionType = DimensionType.CAREER,
        weakestDimension: DimensionType = DimensionType.HEALTH,
        streak: Int = 14,
        score: Int = 780
    ): GeneratedMeme {
        val template = getAllTemplates().first()
        return GeneratedMeme(
            templateId = template.id,
            topText = "MY ${strongestDimension.displayName.uppercase()} SCORE: $score",
            bottomText = "MY ${weakestDimension.displayName.uppercase()} HABITS: SEND HELP 💀",
            emojiArt = "${strongestDimension.iconName} ➡️ 🫥",
            userScore = score,
            userStreak = streak,
            targetDimension = weakestDimension
        )
    }

    fun remixCaptions(
        templateId: String,
        strongest: DimensionType = DimensionType.CAREER,
        weakest: DimensionType = DimensionType.HEALTH
    ): Pair<String, String> {
        return when (templateId) {
            "meme_dimension_gap" -> Pair(
                "ME MAXING OUT ${strongest.displayName.uppercase()}",
                "WHILE MY ${weakest.displayName.uppercase()} IS HANGING BY A THREAD 🧵"
            )
            "meme_sigma_streak" -> Pair(
                "DAY 21 OF RELENTLESS DISCIPLINE",
                "MY OLD EXCUSES ARE NOW EXTINCT 🦕🔥"
            )
            "meme_3am_empire" -> Pair(
                "MIND AT 3 AM: \"I WILL DOMINATE ALL 8 DIMENSIONS\"",
                "BODY AT 7 AM: \"CANNOT FIND THE SOCKS\" 🧦"
            )
            "meme_zen_master" -> Pair(
                "ONE 5-MINUTE MEDITATION SESSION",
                "BUDDHA HAS BEEN REAL QUIET SINCE THIS DROPPED 🌿🧘"
            )
            else -> Pair(
                "CONSISTENCY IS MY ONLY SUPERPOWER",
                "LIFESCORE LEVELING UP ONE QUEST AT A TIME 🚀"
            )
        }
    }

    fun generateShareCaption(meme: GeneratedMeme): String {
        return """
            ${meme.topText}
            ${meme.bottomText}
            
            LifeScore: ${meme.userScore} • ${meme.userStreak}d Streak 🔥
            Level up your 8 dimensions of life on LifeScore App! 🚀
            
            #LifeScore #HabitTracker #Discipline #ViralMemes #GamifyYourLife
        """.trimIndent()
    }
}
