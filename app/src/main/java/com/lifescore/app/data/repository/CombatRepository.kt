package com.lifescore.app.data.repository

import com.lifescore.app.data.local.dao.BossDao
import com.lifescore.app.data.local.dao.CharacterStatsDao
import com.lifescore.app.data.local.entity.BossEntity
import com.lifescore.app.domain.model.BossRoster
import com.lifescore.app.domain.model.CombatBoss
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlin.random.Random

data class AttackResult(
    val damageDealt: Int,
    val isCritical: Boolean,
    val remainingHp: Int,
    val isDefeated: Boolean,
    val battleLog: String
)

interface CombatRepository {
    fun getAllBosses(): Flow<List<CombatBoss>>
    suspend fun executeAttack(bossId: String, playerCombatPower: Int): AttackResult
}

class CombatRepositoryImpl(
    private val bossDao: BossDao,
    private val statsDao: CharacterStatsDao
) : CombatRepository {

    override fun getAllBosses(): Flow<List<CombatBoss>> {
        return bossDao.getAllBosses()
            .onStart { seedBossesIfEmpty() }
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun executeAttack(bossId: String, playerCombatPower: Int): AttackResult {
        val bossEntity = bossDao.getBossById(bossId) ?: return AttackResult(0, false, 0, false, "Boss not found")
        
        val isCrit = Random.nextFloat() < 0.25f // 25% crit chance
        val baseDamage = (playerCombatPower * (if (isCrit) 1.8f else 1.0f) * (0.85f + Random.nextFloat() * 0.3f)).toInt()
        val actualDamage = baseDamage.coerceAtLeast(15)

        val newHp = (bossEntity.currentHp - actualDamage).coerceAtLeast(0)
        val isDefeated = newHp == 0

        bossDao.updateBossHp(bossId, newHp)

        if (isDefeated) {
            statsDao.grantAvailablePoints(bossEntity.rewardStatPoints)
        }

        val log = if (isDefeated) {
            "💥 CRITICAL VICTORY! You dealt $actualDamage damage and defeated ${bossEntity.name}! (+${bossEntity.rewardStatPoints} Stat Points, +${bossEntity.rewardXp} XP)"
        } else if (isCrit) {
            "⚡ CRITICAL STRIKE! You struck ${bossEntity.name} for $actualDamage massive damage!"
        } else {
            "⚔️ Strike landed on ${bossEntity.name} dealing $actualDamage damage."
        }

        return AttackResult(
            damageDealt = actualDamage,
            isCritical = isCrit,
            remainingHp = newHp,
            isDefeated = isDefeated,
            battleLog = log
        )
    }

    private suspend fun seedBossesIfEmpty() {
        val bosses = BossRoster.allBosses.map { it.toEntity() }
        bossDao.insertBosses(bosses)
    }

    private fun BossEntity.toDomain() = CombatBoss(
        id = id,
        name = name,
        title = title,
        dimension = dimension,
        maxHp = maxHp,
        currentHp = currentHp,
        attackPower = attackPower,
        avatarEmoji = avatarEmoji,
        rewardXp = rewardXp,
        rewardStatPoints = rewardStatPoints,
        isDefeated = isDefeated
    )

    private fun CombatBoss.toEntity() = BossEntity(
        id = id,
        name = name,
        title = title,
        dimension = dimension,
        maxHp = maxHp,
        currentHp = currentHp,
        attackPower = attackPower,
        avatarEmoji = avatarEmoji,
        rewardXp = rewardXp,
        rewardStatPoints = rewardStatPoints,
        isDefeated = isDefeated
    )
}
