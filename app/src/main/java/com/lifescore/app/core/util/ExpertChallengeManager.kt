package com.lifescore.app.core.util

import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.ExpertMasterclass
import com.lifescore.app.domain.model.MasterclassCertificate
import com.lifescore.app.domain.model.MasterclassDayModule
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

object ExpertChallengeManager {

    val masterclasses: List<ExpertMasterclass> by lazy {
        listOf(
            createNeuroFocusMasterclass(),
            createAtomicHabitMasterclass(),
            createCapitalMasteryMasterclass(),
            createSomaticVitalityMasterclass(),
            createEmotionalEqMasterclass()
        )
    }

    private fun createNeuroFocusMasterclass(): ExpertMasterclass {
        val days = listOf(
            MasterclassDayModule(1, "Dopamine Baselines & Reset", "Understand tonic vs phasic dopamine release.", 300, "Dopamine peaks lead to proportional troughs. Learn how to maintain smooth baseline motivation without sudden crashes.", "Log zero high-friction scrolling for the first 60 minutes after waking."),
            MasterclassDayModule(2, "Circadian Photobiology", "Morning photon exposure and cortisol alignment.", 280, "Viewing sunlight within 30 minutes of waking anchors circadian rhythm, optimizes evening melatonin, and elevates morning alertness.", "Get 10 minutes of direct outdoor natural light before 9:00 AM."),
            MasterclassDayModule(3, "Visual Anchors & Focus Horizon", "Narrowing visual gaze to activate cognitive focus.", 310, "Your visual field directly modulates autonomic alertness. Narrow focal vision primes prefrontal cortex engagement.", "Practice 60 seconds of fixed-point visual gazing before your first work block."),
            MasterclassDayModule(4, "Ultradian 90-Minute Rhythm", "Aligning work with 90-minute biological cycles.", 295, "Cognitive endurance operates in 90-minute ultradian rhythms. Pushing past 90 mins without recovery drops retention by 40%.", "Execute exactly one 90-minute uninterrupted deep work sprint."),
            MasterclassDayModule(5, "Non-Sleep Deep Rest (NSDR)", "Restoring dopamine pools in 20 minutes.", 320, "Yoga Nidra and NSDR protocols accelerate neuroplastic consolidation and replenish striatal dopamine.", "Complete a 15-minute NSDR / body scan session mid-afternoon."),
            MasterclassDayModule(6, "Binaural Beats & 40Hz Gamma", "Acoustic stimulation for active memory encoding.", 270, "40Hz acoustic entrainment increases gamma oscillations in temporal and frontal cortices.", "Listen to 40Hz binaural audio during a 30-minute reading block."),
            MasterclassDayModule(7, "Cold Thermogenesis & Norepinephrine", "Acutely spiking sustained catecholamines.", 330, "Deliberate cold exposure triggers a 250% sustained increase in plasma norepinephrine and dopamine for up to 3 hours.", "Take a 60-second cold shower finish after your morning routine."),
            MasterclassDayModule(8, "Strategic Fasting & Ketones", "Metabolic substrate switching for cognitive clarity.", 290, "Mild ketosis enhances GABA synthesis and reduces glutamate excitotoxicity during critical problem-solving.", "Fast until 12:00 PM with black coffee / water only."),
            MasterclassDayModule(9, "Cognitive Friction & Effort Gating", "Reframing friction as neuroplastic signaling.", 310, "Subjective friction during learning is the physiological trigger for acetylcholine release.", "Spend 20 minutes tackling a subject that feels cognitively difficult."),
            MasterclassDayModule(10, "Digital Sabbath & Sensory Fasting", "Starving background hyper-stimulation.", 340, "Continuous notification switching impairs dorsal attention networks. A digital fast resets sensory receptor density.", "Place phone in another room in Do Not Disturb for 3 consecutive hours."),
            MasterclassDayModule(11, "Physiological Sigh & Autonomic Reset", "Rapid 2-breath down-regulation protocol.", 260, "Double inhale followed by long exhale maximally opens alveoli and triggers vagal parasympathetic tone.", "Execute 5 physiological sighs during a stressful transition."),
            MasterclassDayModule(12, "Sleep Architecture & Glymphatic Cleansing", "Deep slow-wave sleep and brain waste clearance.", 315, "During slow-wave sleep, the glymphatic system expands by 60% to clear metabolic waste and tau proteins.", "Keep bedroom temperature at 67°F (19°C) and pitch dark."),
            MasterclassDayModule(13, "Flow State Trigger Sequences", "Designing ritualized transitions into flow.", 305, "Flow states require clear goals, immediate feedback, and a challenge-to-skill ratio dialed at 4% above comfort.", "Write out your 3-step personal pre-game flow ritual."),
            MasterclassDayModule(14, "Neuro-Architecture Graduation", "Long-term compounding integration.", 360, "Congratulations! You have mastered the biological levers of high-focus execution. Integrate these into your permanent identity.", "Draft your permanent 2026 Daily Neuro-Protocol checklist.")
        )

        return ExpertMasterclass(
            id = "mc_neuro_focus",
            title = "14-Day Neuro-Focus & Dopamine Architecture",
            subtitle = "Master the biological levers of elite cognitive output, focus gating, and circadian mastery.",
            coachName = "Dr. Maya Lin, PhD",
            coachTitle = "Stanford Neurobiology Researcher",
            coachAvatarEmoji = "🧬",
            coachCredentials = "PhD in Cognitive Neuroscience • 12+ Published Papers in Nature & Neuron",
            dimension = DimensionType.LEARNING,
            durationDays = 14,
            priceUsd = 9.99,
            isUnlocked = true, // Unlocked by default for demo
            days = days
        )
    }

    private fun createAtomicHabitMasterclass(): ExpertMasterclass {
        val days = (1..14).map { day ->
            MasterclassDayModule(
                dayNumber = day,
                title = "Atomic Compound Day $day: Identity Architecture",
                summary = "Systematic daily iteration on identity shifts and environment design.",
                audioDurationSeconds = 300,
                transcriptSummary = "Every action is a vote for the type of person you wish to become.",
                dailyTaskTitle = "Apply Day $day Habit Cue Optimization",
                dailyTaskPoints = 50
            )
        }
        return ExpertMasterclass(
            id = "mc_atomic_habits",
            title = "14-Day Atomic Habit Compounding System",
            subtitle = "Build frictionless habit loops, identity-based consistency, and environmental default design.",
            coachName = "James Vance",
            coachTitle = "Executive Performance Architect",
            coachAvatarEmoji = "⚡",
            coachCredentials = "Advisor to Fortune 500 CEOs & Olympic Gold Medalists",
            dimension = DimensionType.CAREER,
            durationDays = 14,
            priceUsd = 9.99,
            isUnlocked = false,
            days = days
        )
    }

    private fun createCapitalMasteryMasterclass(): ExpertMasterclass {
        val days = (1..14).map { day ->
            MasterclassDayModule(
                dayNumber = day,
                title = "Capital Allocation Day $day: Asymmetric Edge",
                summary = "Macro cash-flow modeling, tax minimization, and asymmetric portfolio allocation.",
                audioDurationSeconds = 300,
                transcriptSummary = "Play iterated games with asymmetric risk/reward profiles.",
                dailyTaskTitle = "Complete Day $day Balance Sheet Audit",
                dailyTaskPoints = 50
            )
        }
        return ExpertMasterclass(
            id = "mc_capital_mastery",
            title = "14-Day Capital Mastery & Asymmetric Investing",
            subtitle = "Think like an institutional allocator: Fortress balance sheets, cash-flow flywheels, and equity moats.",
            coachName = "Marcus Sterling",
            coachTitle = "Multi-Asset Portfolio Partner",
            coachAvatarEmoji = "💰",
            coachCredentials = "Former Managing Director • Allocating $1.2B+ in Private Equity",
            dimension = DimensionType.WEALTH,
            durationDays = 14,
            priceUsd = 9.99,
            isUnlocked = false,
            days = days
        )
    }

    private fun createSomaticVitalityMasterclass(): ExpertMasterclass {
        val days = (1..14).map { day ->
            MasterclassDayModule(
                dayNumber = day,
                title = "Somatic Vitality Day $day: Mitochondrial Protocol",
                summary = "Zone 2 base building, VO2 max intervals, and joint longevity.",
                audioDurationSeconds = 300,
                transcriptSummary = "Mitochondrial density dictates cellular energy and metabolic longevity.",
                dailyTaskTitle = "Complete Day $day Zone 2 Movement Sprint",
                dailyTaskPoints = 50
            )
        }
        return ExpertMasterclass(
            id = "mc_somatic_vitality",
            title = "14-Day Somatic Protocol & Mitochondrial Vitality",
            subtitle = "Re-engineer your physical vessel with Zone 2 conditioning, joint mechanics, and longevity protocols.",
            coachName = "Elena Vance",
            coachTitle = "Olympic Biomechanics Specialist",
            coachAvatarEmoji = "🏃",
            coachCredentials = "Head Physiologist for Olympic Track & Triathlon Teams",
            dimension = DimensionType.FITNESS,
            durationDays = 14,
            priceUsd = 9.99,
            isUnlocked = false,
            days = days
        )
    }

    private fun createEmotionalEqMasterclass(): ExpertMasterclass {
        val days = (1..14).map { day ->
            MasterclassDayModule(
                dayNumber = day,
                title = "Relational EQ Day $day: Radical Vulnerability",
                summary = "Non-violent communication, boundary holding, and high-trust intimacy.",
                audioDurationSeconds = 300,
                transcriptSummary = "Vulnerability is the foundational currency of high-trust relationships.",
                dailyTaskTitle = "Execute Day $day Deep Empathy Dialogue",
                dailyTaskPoints = 50
            )
        }
        return ExpertMasterclass(
            id = "mc_emotional_eq",
            title = "14-Day Radical Vulnerability & High-Trust EQ",
            subtitle = "Master the art of deep listening, courageous conversations, and unshakeable relationship bonds.",
            coachName = "Dr. Sophia Moreau",
            coachTitle = "Clinical Relationship Psychologist",
            coachAvatarEmoji = "💖",
            coachCredentials = "PhD Clinical Psychology • Author of 'The Vulnerability Bridge'",
            dimension = DimensionType.RELATIONSHIPS,
            durationDays = 14,
            priceUsd = 9.99,
            isUnlocked = false,
            days = days
        )
    }

    fun checkInDay(masterclass: ExpertMasterclass, dayNumber: Int): ExpertMasterclass {
        val updatedDays = masterclass.days.map { day ->
            if (day.dayNumber == dayNumber) {
                day.copy(isCompleted = true, completedAt = System.currentTimeMillis())
            } else day
        }
        val isAllDone = updatedDays.all { it.isCompleted }
        val nextDay = if (isAllDone) 14 else (dayNumber + 1).coerceAtMost(14)

        return masterclass.copy(
            days = updatedDays,
            currentDay = nextDay,
            isCompleted = isAllDone,
            graduationDate = if (isAllDone) System.currentTimeMillis() else null
        )
    }

    fun generateCertificate(masterclass: ExpertMasterclass, userName: String): MasterclassCertificate {
        val dateStr = SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(Date())
        val rawInput = "$userName-${masterclass.id}-$dateStr-LIFESCORE_VERIFIED"
        val hash = MessageDigest.getInstance("SHA-256")
            .digest(rawInput.toByteArray())
            .joinToString("") { "%02x".format(it) }
            .take(16)
            .uppercase()

        val certId = "CERT-" + masterclass.id.takeLast(4).uppercase() + "-" + hash.take(6)

        return MasterclassCertificate(
            certificateId = certId,
            userName = userName,
            masterclassTitle = masterclass.title,
            coachName = masterclass.coachName,
            coachTitle = masterclass.coachTitle,
            dimension = masterclass.dimension,
            completionDate = dateStr,
            verificationHash = hash,
            xpEarnedTotal = (masterclass.durationDays * 50) + masterclass.graduationXpBonus
        )
    }

    fun generateCertificateShareCaption(certificate: MasterclassCertificate): String {
        return "🎓 Certified in ${certificate.masterclassTitle}!\nInstructed by ${certificate.coachName} • Official LifeScore Masterclass Graduate 🏆\nVerification ID: ${certificate.certificateId}\nDiscover world-class coach masterclasses: https://lifescore.app/masterclasses #LifeScore #Masterclass #ContinuousLearning #GrowthMindset"
    }
}
