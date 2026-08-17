package com.lifescore.app.domain.model

data class CombatBoss(
    val id: String,
    val name: String,
    val title: String,
    val dimension: DimensionType,
    val maxHp: Int,
    val currentHp: Int,
    val attackPower: Int,
    val avatarEmoji: String,
    val rewardXp: Int,
    val rewardStatPoints: Int,
    val isDefeated: Boolean = false
) {
    val hpPercentage: Float
        get() = (currentHp.toFloat() / maxHp.toFloat()).coerceIn(0f, 1f)
}

data class BattleLogEntry(
    val id: String,
    val message: String,
    val isCritical: Boolean = false,
    val isPlayerTurn: Boolean = true,
    val damageDealt: Int = 0
)

object BossRoster {
    val allBosses = listOf(
        CombatBoss(
            id = "boss_procrastination",
            name = "Sloth Demon Malakor",
            title = "Lord of Tomorrow",
            dimension = DimensionType.CAREER,
            maxHp = 500,
            currentHp = 350,
            attackPower = 35,
            avatarEmoji = "👹",
            rewardXp = 800,
            rewardStatPoints = 5
        ),
        CombatBoss(
            id = "boss_burnout",
            name = "Ashen Titan Ignis",
            title = "Devourer of Energy",
            dimension = DimensionType.HEALTH,
            maxHp = 750,
            currentHp = 750,
            attackPower = 45,
            avatarEmoji = "🌋",
            rewardXp = 1200,
            rewardStatPoints = 8
        ),
        CombatBoss(
            id = "boss_isolation",
            name = "Shadow Wraith Vex",
            title = "Weaver of Doubts",
            dimension = DimensionType.MENTAL_HEALTH,
            maxHp = 1000,
            currentHp = 1000,
            attackPower = 60,
            avatarEmoji = "🌑",
            rewardXp = 2000,
            rewardStatPoints = 12
        )
    )
}
