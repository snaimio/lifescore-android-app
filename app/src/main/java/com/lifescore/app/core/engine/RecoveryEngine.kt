package com.lifescore.app.core.engine

import com.lifescore.app.data.local.entity.AddictionType
import com.lifescore.app.data.local.entity.RecoveryMilestone

data class RecoveryStats(
    val totalDays: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val totalSeconds: Long,
    val totalFractionalDays: Double
)

data class CrisisContact(
    val title: String,
    val phone: String?,
    val textNumber: String?,
    val textBody: String?,
    val website: String?,
    val description: String,
    val availability: String = "24/7 • Free & Confidential"
)

data class CbtLesson(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val coreConcept: String,
    val actionableExercise: String
)

class RecoveryEngine {

    fun calculateSobrietyStats(startDate: Long): RecoveryStats {
        val now = System.currentTimeMillis()
        val diffMs = (now - startDate).coerceAtLeast(0L)
        val totalSec = diffMs / 1000
        val diffDays = (totalSec / (24 * 3600)).toInt()
        val diffHours = ((totalSec % (24 * 3600)) / 3600).toInt()
        val diffMinutes = ((totalSec % 3600) / 60).toInt()
        val diffSeconds = (totalSec % 60).toInt()
        val fractionalDays = totalSec.toDouble() / (24.0 * 3600.0)

        return RecoveryStats(
            totalDays = diffDays,
            hours = diffHours,
            minutes = diffMinutes,
            seconds = diffSeconds,
            totalSeconds = totalSec,
            totalFractionalDays = fractionalDays
        )
    }

    fun calculateMoneySaved(fractionalDays: Double, dailyCost: Double): Double {
        return (fractionalDays * dailyCost).coerceAtLeast(0.0)
    }

    fun calculateTimeSavedHours(fractionalDays: Double, dailyMinutes: Int): Double {
        return (fractionalDays * (dailyMinutes.toDouble() / 60.0)).coerceAtLeast(0.0)
    }

    fun calculateItemsAvoided(fractionalDays: Double, dailyItems: Int): Int {
        return (fractionalDays * dailyItems).toInt().coerceAtLeast(0)
    }

    fun getHealthMilestones(addictionType: AddictionType): List<RecoveryMilestone> {
        return when (addictionType) {
            AddictionType.SMOKING -> listOf(
                RecoveryMilestone(
                    milestoneDays = 1,
                    title = "24 Hours Smoke-Free",
                    description = "Carbon monoxide levels in your blood normalize. Oxygen levels surge.",
                    healthBenefit = "❤️ Heart attack risk drops",
                    medallionEmoji = "🥉"
                ),
                RecoveryMilestone(
                    milestoneDays = 2,
                    title = "48 Hours: Nerve Regeneration",
                    description = "Damaged nerve endings start regrowing. Senses of smell and taste dramatically heighten.",
                    healthBenefit = "👃 Taste & smell return",
                    medallionEmoji = "🥉"
                ),
                RecoveryMilestone(
                    milestoneDays = 3,
                    title = "72 Hours: 100% Nicotine Cleanse",
                    description = "Nicotine is completely flushed out of your body. Bronchial tubes relax, breathing becomes easier.",
                    healthBenefit = "🌬️ Nicotine-free bloodstream",
                    medallionEmoji = "🥉"
                ),
                RecoveryMilestone(
                    milestoneDays = 7,
                    title = "1 Week: Cilia Recovery",
                    description = "Lungs begin clearing built-up mucus. Circulation improves, risk of infection drops.",
                    healthBenefit = "💪 Stronger immune defense",
                    medallionEmoji = "🥈"
                ),
                RecoveryMilestone(
                    milestoneDays = 14,
                    title = "2 Weeks: Peak Physical Energy",
                    description = "Blood flow improves dramatically. Walking, running, and physical workouts feel effortless.",
                    healthBenefit = "🏃 +30% Cardiovascular stamina",
                    medallionEmoji = "🥈"
                ),
                RecoveryMilestone(
                    milestoneDays = 30,
                    title = "1 Month: Deep Lung Healing",
                    description = "Coughing, sinus congestion, and shortness of breath decrease significantly.",
                    healthBenefit = "🫁 Lungs actively regenerating",
                    medallionEmoji = "🥇"
                ),
                RecoveryMilestone(
                    milestoneDays = 90,
                    title = "3 Months: Brain Neurochemistry Reset",
                    description = "Dopamine receptors in your brain have normalized. Psychological cravings are rare and weak.",
                    healthBenefit = "🧠 Balanced dopamine baseline",
                    medallionEmoji = "💎"
                ),
                RecoveryMilestone(
                    milestoneDays = 365,
                    title = "1 Year: Total Life Transformation",
                    description = "Your excess risk of coronary heart disease is half that of a continuing smoker.",
                    healthBenefit = "👑 50% lower heart disease risk",
                    medallionEmoji = "👑"
                )
            )

            AddictionType.ALCOHOL -> listOf(
                RecoveryMilestone(
                    milestoneDays = 1,
                    title = "24 Hours Alcohol-Free",
                    description = "Blood sugar stabilizes. Your body has fully metabolized residual ethanol.",
                    healthBenefit = "💧 Cellular rehydration",
                    medallionEmoji = "🥉"
                ),
                RecoveryMilestone(
                    milestoneDays = 3,
                    title = "72 Hours: Physical Detox Complete",
                    description = "Acute physical withdrawal symptoms subside. Sleep quality begins to restore deep REM stages.",
                    healthBenefit = "😴 Deep restorative sleep",
                    medallionEmoji = "🥉"
                ),
                RecoveryMilestone(
                    milestoneDays = 7,
                    title = "1 Week: Liver Fat Reduction",
                    description = "Liver enzymes begin normalizing. Inflammation across vital organs subsides.",
                    healthBenefit = "🧪 Liver detoxification",
                    medallionEmoji = "🥈"
                ),
                RecoveryMilestone(
                    milestoneDays = 14,
                    title = "2 Weeks: Gut & Stomach Relief",
                    description = "Stomach acid production normalizes. Acid reflux disappears, gut microbiome rebalances.",
                    healthBenefit = "✨ Clear skin & digestion",
                    medallionEmoji = "🥈"
                ),
                RecoveryMilestone(
                    milestoneDays = 30,
                    title = "1 Month: 15% Liver Fat Drop",
                    description = "Liver fat drops by up to 15%. Blood pressure normalizes and mental fog evaporates.",
                    healthBenefit = "🧠 Razor-sharp mental clarity",
                    medallionEmoji = "🥇"
                ),
                RecoveryMilestone(
                    milestoneDays = 90,
                    title = "3 Months: Cellular Rejuvenation",
                    description = "Red blood cells and white immune cells are fully renewed. Mood stability is high.",
                    healthBenefit = "🛡️ Full immune recovery",
                    medallionEmoji = "💎"
                ),
                RecoveryMilestone(
                    milestoneDays = 365,
                    title = "1 Year: Master of Sobriety",
                    description = "Liver disease risk plummeted. Brain matter recovery and monumental life achievement.",
                    healthBenefit = "👑 Complete physical renewal",
                    medallionEmoji = "👑"
                )
            )

            AddictionType.NICOTINE_VAPING -> listOf(
                RecoveryMilestone(
                    milestoneDays = 1,
                    title = "24 Hours Vapor-Free",
                    description = "Pulse rate and blood pressure drop to healthy baselines.",
                    healthBenefit = "❤️ Normalized blood pressure",
                    medallionEmoji = "🥉"
                ),
                RecoveryMilestone(
                    milestoneDays = 3,
                    title = "3 Days: Clean Airways",
                    description = "Chemical residue cleared from air passages. Airway constriction relaxes.",
                    healthBenefit = "🌬️ Clear easy breathing",
                    medallionEmoji = "🥉"
                ),
                RecoveryMilestone(
                    milestoneDays = 7,
                    title = "1 Week: Dopamine Recalibration",
                    description = "Nicotine chemical dependence broken. Brain starts naturally producing dopamine.",
                    healthBenefit = "🧠 Natural mood elevation",
                    medallionEmoji = "🥈"
                ),
                RecoveryMilestone(
                    milestoneDays = 30,
                    title = "1 Month: Lung Tissue Recovery",
                    description = "Vape-induced lung inflammation vanishes. Immune system fully active in throat/bronchi.",
                    healthBenefit = "🫁 Fully healed airways",
                    medallionEmoji = "🥇"
                ),
                RecoveryMilestone(
                    milestoneDays = 90,
                    title = "3 Months: Habit Freedom",
                    description = "Hand-to-mouth subconscious trigger permanently extinct. Complete autonomy.",
                    healthBenefit = "💎 Zero chemical reliance",
                    medallionEmoji = "💎"
                )
            )

            AddictionType.SUGAR_ADDICTION -> listOf(
                RecoveryMilestone(
                    milestoneDays = 1,
                    title = "24h Sugar Stabilized",
                    description = "Insulin spikes subside. Blood glucose begins balancing throughout the day.",
                    healthBenefit = "⚡ Steady all-day energy",
                    medallionEmoji = "🥉"
                ),
                RecoveryMilestone(
                    milestoneDays = 7,
                    title = "1 Week: Taste Buds Reset",
                    description = "Natural foods (fruits, veggies) taste rich and sweet. Systemic inflammation drops.",
                    healthBenefit = "🍓 Heightened natural taste",
                    medallionEmoji = "🥈"
                ),
                RecoveryMilestone(
                    milestoneDays = 21,
                    title = "3 Weeks: Metabolic Switch",
                    description = "Body transitions into efficient clean metabolic burning. Energy crashes vanish.",
                    healthBenefit = "🔥 Metabolic flexibility",
                    medallionEmoji = "🥇"
                ),
                RecoveryMilestone(
                    milestoneDays = 60,
                    title = "2 Months: Insulin Sensitivity",
                    description = "Insulin resistance drastically reversed. Skin glows, gut health restored.",
                    healthBenefit = "💎 Optimal metabolic health",
                    medallionEmoji = "💎"
                )
            )

            AddictionType.SOCIAL_MEDIA_ADDICTION -> listOf(
                RecoveryMilestone(
                    milestoneDays = 1,
                    title = "24h Screen Detox",
                    description = "Phantom phone vibration urge drops. Cortisol and sensory overwhelm reduce.",
                    healthBenefit = "🧘 Calm nervous system",
                    medallionEmoji = "🥉"
                ),
                RecoveryMilestone(
                    milestoneDays = 3,
                    title = "3 Days: Reclaimed Deep Focus",
                    description = "Sustained attention span increases by 40%. Ability to read books effortlessly.",
                    healthBenefit = "📚 45+ min deep focus",
                    medallionEmoji = "🥉"
                ),
                RecoveryMilestone(
                    milestoneDays = 7,
                    title = "1 Week: Present Living",
                    description = "Reclaimed 20+ hours of life. Social comparison stress completely vanishes.",
                    healthBenefit = "⏳ 20+ hours reclaimed",
                    medallionEmoji = "🥈"
                ),
                RecoveryMilestone(
                    milestoneDays = 30,
                    title = "1 Month: Digital Sovereign",
                    description = "Phones become pure utilitarian tools, not dopamine slots. Deep inner peace.",
                    healthBenefit = "👑 Unshakeable mental clarity",
                    medallionEmoji = "🥇"
                )
            )

            else -> listOf(
                RecoveryMilestone(
                    milestoneDays = 1,
                    title = "24 Hours Victorious",
                    description = "You took the courageous first step and held the line.",
                    healthBenefit = "🌱 Fresh new chapter",
                    medallionEmoji = "🥉"
                ),
                RecoveryMilestone(
                    milestoneDays = 7,
                    title = "1 Week of Discipline",
                    description = "New neural pathways are forming. Self-respect and momentum compounding.",
                    healthBenefit = "💪 Building iron willpower",
                    medallionEmoji = "🥈"
                ),
                RecoveryMilestone(
                    milestoneDays = 30,
                    title = "1 Month Milestone",
                    description = "A full month of freedom. You have proven you can thrive without old crutches.",
                    healthBenefit = "🥇 30 Days of Freedom",
                    medallionEmoji = "🥇"
                ),
                RecoveryMilestone(
                    milestoneDays = 90,
                    title = "90 Days Transformed",
                    description = "Complete lifestyle and identity shift. You are free.",
                    healthBenefit = "💎 Master of Your Destiny",
                    medallionEmoji = "💎"
                )
            )
        }
    }

    fun getCravingTriggers(): List<String> {
        return listOf(
            "😰 Stress & High Pressure",
            "👥 Social Environment / Friends",
            "🍺 Alcohol / Other Triggers",
            "🍽️ Immediately After Meals",
            "☕ Morning Coffee Routine",
            "🚗 Driving / Commute",
            "😢 Sadness or Emotional Pain",
            "🎉 Celebration & Excitement",
            "🥱 Boredom / Idle Time",
            "📱 Late Night Scrolling"
        )
    }

    fun getDistractionActivities(): List<String> {
        return listOf(
            "💧 Drink a large glass of ice-cold water slowly",
            "🌬️ Do 3 cycles of 4-7-8 Box Breathing",
            "🚶 Step outside and take a brisk 10-minute walk",
            "💪 Do 15 push-ups or 20 air squats right now",
            "📝 Write down 3 things you will lose if you give in",
            "🎵 Put on your favorite high-energy upbeat track",
            "🧊 Hold an ice cube in your hand until it melts",
            "📞 Call or text a supportive friend or family member",
            "🧩 Play a 5-minute puzzle or brain challenge in LifeScore",
            "🧹 Organize your desk, drawer, or clean your room",
            "🚿 Take a quick 30-second cold water splash to face",
            "📖 Read 2 pages of a growth mindset book"
        )
    }

    fun getCrisisResources(): List<CrisisContact> {
        return listOf(
            CrisisContact(
                title = "988 Suicide & Crisis Lifeline",
                phone = "988",
                textNumber = "988",
                textBody = null,
                website = "https://988lifeline.org",
                description = "Free, confidential 24/7 support for anyone experiencing substance distress or emotional crisis."
            ),
            CrisisContact(
                title = "SAMHSA National Helpline",
                phone = "1-800-662-4357",
                textNumber = null,
                textBody = null,
                website = "https://www.samhsa.gov/find-help/national-helpline",
                description = "Substance Abuse and Mental Health Services 24/7, 365-day treatment referral & info service."
            ),
            CrisisContact(
                title = "Crisis Text Line",
                phone = null,
                textNumber = "741741",
                textBody = "HOME",
                website = "https://www.crisistextline.org",
                description = "Text HOME to 741741 to connect with a compassionate trained Crisis Counselor 24/7."
            ),
            CrisisContact(
                title = "National SmokeFree Quitline",
                phone = "1-800-784-8669",
                textNumber = null,
                textBody = null,
                website = "https://smokefree.gov",
                description = "Expert smoking & vaping cessation coaches, quit plans, and immediate urge intervention."
            ),
            CrisisContact(
                title = "Alcoholics Anonymous (AA) Online",
                phone = null,
                textNumber = null,
                textBody = null,
                website = "https://aa-intergroup.org/meetings",
                description = "Find 24/7 online video and chat AA support meetings happening right this minute globally."
            )
        )
    }

    fun getCbtLessons(): List<CbtLesson> {
        return listOf(
            CbtLesson(
                id = "urge_surfing",
                title = "Urge Surfing (Alan Marlatt)",
                subtitle = "Ride the wave instead of fighting it",
                emoji = "🌊",
                coreConcept = "Cravings are like ocean waves: they rise, peak at 10-15 minutes, and naturally break and dissolve. Fighting an urge amplifies tension. Surfing involves acknowledging the craving with mindful curiosity, breathing through the peak without giving in.",
                actionableExercise = "When you feel an urge, close your eyes. Locate where in your body the sensation is (chest, stomach, throat). Rate it 1-10. Breathe into that physical sensation for 3 minutes without reacting."
            ),
            CbtLesson(
                id = "halt_system",
                title = "The H.A.L.T. Check",
                subtitle = "Identify the true underlying vulnerability",
                emoji = "🛑",
                coreConcept = "Over 80% of relapses occur when the body is in one of four states: Hungry, Angry, Lonely, or Tired. The addiction crutch is just an attempt to self-medicate one of these core biological needs.",
                actionableExercise = "Before reaching for any substance or vice, ask: Am I Hungry (eat a meal)? Angry (express emotion)? Lonely (reach out)? Tired (take a rest)?"
            ),
            CbtLesson(
                id = "decatastrophize",
                title = "Cognitive Decatastrophizing",
                subtitle = "Break the 'I can't stand this' illusion",
                emoji = "🧠",
                coreConcept = "Your brain creates cognitive distortions: 'I NEED this to survive right now' or 'I will die if I don't give in.' In reality, cravings are just neurological chemical whispers that physically cannot harm you.",
                actionableExercise = "Reframe the thought: 'This craving is uncomfortable, but discomfort is NOT dangerous. My body is literally healing right now.'"
            ),
            CbtLesson(
                id = "dopamine_reset",
                title = "The 30-Day Dopamine Reset",
                subtitle = "Rewiring your baseline pleasure receptors",
                emoji = "⚡",
                coreConcept = "Addictive vices flood the brain with unnatural supranormal dopamine, downregulating your D2 receptors. When you quit, there is a temporary 'dopamine deficit state' before your receptors naturally multiply back.",
                actionableExercise = "Embrace the low energy of days 1-14 as the physical sign that your receptors are actively healing. Joy in simple things will return."
            )
        )
    }
}
