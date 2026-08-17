package com.lifescore.app.domain.model

data class AiQuest(
    val id: String,
    val title: String,
    val description: String,
    val dimension: DimensionType,
    val difficulty: QuestDifficulty = QuestDifficulty.C,
    val pointsReward: Int = 30,
    val statRewardPoints: Int = 2,
    val estimatedMinutes: Int = 15,
    val subObjectives: List<String> = emptyList(),
    val isAccepted: Boolean = false,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
