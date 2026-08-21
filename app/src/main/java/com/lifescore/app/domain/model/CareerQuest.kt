package com.lifescore.app.domain.model

data class CareerQuestDay(
    val dayNumber: Int,
    val title: String,
    val description: String,
    val actionPrompt: String,
    val xpReward: Int,
    val isCompleted: Boolean = false
)

data class CareerQuest(
    val id: String,
    val careerTitle: String,
    val riasecCode: String,
    val salaryRange: String,
    val description: String,
    val topSkills: List<String>,
    val days: List<CareerQuestDay>,
    val totalXpReward: Int = 350,
    val isJoined: Boolean = false,
    val completedDaysCount: Int = 0
) {
    val progress: Float
        get() = if (days.isEmpty()) 0f else completedDaysCount.toFloat() / days.size.toFloat()
    
    val isFullyCompleted: Boolean
        get() = completedDaysCount >= days.size
}
