package com.lifescore.app.core.engine

enum class VerificationType(val title: String, val iconEmoji: String, val description: String) {
    NONE("Standard Check-Off", "⚡", "Instant 1-tap completion"),
    TIMER("Focus Timer Proof", "⏱️", "Requires completing timed countdown session"),
    PHOTO("Visual Snapshot Proof", "📸", "Upload or snap a quick proof photo"),
    REFLECTION("Micro-Reflection Proof", "✍️", "Write a 1-sentence lesson or insight")
}

data class TaskVerificationRecord(
    val taskId: Long,
    val type: VerificationType,
    val verifiedAt: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,
    val photoUri: String? = null,
    val reflectionNote: String? = null
)

object TaskVerificationEngine {

    fun validateTimerCompletion(targetMinutes: Int, elapsedMinutes: Int): Boolean {
        return elapsedMinutes >= (targetMinutes * 0.8f).toInt() // At least 80% of timer completed
    }

    fun validateReflection(note: String): Boolean {
        return note.trim().split("\\s+".toRegex()).size >= 3 // At least 3 words
    }

    fun calculateBonusIntegrityXp(type: VerificationType, basePoints: Int): Int {
        return when (type) {
            VerificationType.TIMER -> (basePoints * 0.35f).toInt()      // +35% bonus for timed proof
            VerificationType.PHOTO -> (basePoints * 0.25f).toInt()      // +25% bonus for photo proof
            VerificationType.REFLECTION -> (basePoints * 0.20f).toInt() // +20% bonus for reflection
            VerificationType.NONE -> 0
        }
    }
}
