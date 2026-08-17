package com.lifescore.app.data.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.lifescore.app.BuildConfig
import com.lifescore.app.domain.model.AiQuest
import com.lifescore.app.domain.model.DimensionType
import com.lifescore.app.domain.model.QuestDifficulty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class AiQuestGeneratorService(
    private val apiKey: String? = BuildConfig.GEMINI_API_KEY
) {
    private val generativeModel by lazy {
        if (!apiKey.isNullOrBlank() && apiKey != "DEMO_KEY") {
            try {
                GenerativeModel(
                    modelName = BuildConfig.GEMINI_MODEL,
                    apiKey = apiKey
                )
            } catch (_: Exception) {
                null
            }
        } else null
    }

    suspend fun generatePersonalizedQuests(
        weakestDimension: DimensionType,
        weakestScore: Int,
        totalScore: Int,
        userLevel: Int,
        userStreak: Int
    ): List<AiQuest> = withContext(Dispatchers.IO) {
        if (generativeModel != null) {
            try {
                val prompt = """
                    You are the Solo Leveling / RPG Quest Matrix Engine for LifeScore.
                    User Status:
                    - Level: $userLevel
                    - Overall LifeScore: $totalScore/1000
                    - Weakest Dimension: ${weakestDimension.displayName} ($weakestScore/100)
                    - Active Streak: $userStreak days

                    Generate 3 personalized, gamified real-world quests to level up this user.
                    Return strictly a JSON array of objects with this structure:
                    [
                      {
                        "title": "Clear Morning Brain Fog",
                        "description": "Engage in 15 minutes of uninterrupted morning mobility and sunlight exposure.",
                        "dimension": "${weakestDimension.name}",
                        "difficulty": "C",
                        "pointsReward": 45,
                        "statRewardPoints": 3,
                        "estimatedMinutes": 15,
                        "subObjectives": ["10 min dynamic stretching", "5 min outdoor sunlight walking"]
                      }
                    ]
                    
                    Available difficulties: E, D, C, B, A, S.
                    Return only valid JSON.
                """.trimIndent()

                val response = generativeModel?.generateContent(prompt)
                val text = response?.text?.trim()
                if (!text.isNullOrBlank()) {
                    val cleanedJson = text.removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
                    val jsonArray = JSONArray(cleanedJson)
                    val quests = mutableListOf<AiQuest>()
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val subList = mutableListOf<String>()
                        val subArr = obj.optJSONArray("subObjectives")
                        if (subArr != null) {
                            for (j in 0 until subArr.length()) {
                                subList.add(subArr.getString(j))
                            }
                        }
                        val dim = try {
                            DimensionType.valueOf(obj.getString("dimension"))
                        } catch (_: Exception) {
                            weakestDimension
                        }
                        val diff = QuestDifficulty.fromRankLetter(obj.optString("difficulty", "C"))

                        quests.add(
                            AiQuest(
                                id = UUID.randomUUID().toString(),
                                title = obj.getString("title"),
                                description = obj.getString("description"),
                                dimension = dim,
                                difficulty = diff,
                                pointsReward = (obj.optInt("pointsReward", 30) * diff.xpMultiplier).toInt(),
                                statRewardPoints = diff.statRewardPoints,
                                estimatedMinutes = obj.optInt("estimatedMinutes", 15),
                                subObjectives = subList
                            )
                        )
                    }
                    if (quests.isNotEmpty()) return@withContext quests
                }
            } catch (_: Exception) {
                // Fall back to algorithmic generator
            }
        }
        return@withContext getAlgorithmicQuests(weakestDimension, userLevel)
    }

    private fun getAlgorithmicQuests(weakestDimension: DimensionType, userLevel: Int): List<AiQuest> {
        val list = mutableListOf<AiQuest>()
        
        // Quest 1: Weakness Targeting Quest (Rank C/B)
        val rank1 = if (userLevel > 3) QuestDifficulty.B else QuestDifficulty.C
        list.add(
            AiQuest(
                id = UUID.randomUUID().toString(),
                title = "⚡ ${weakestDimension.displayName} Ascension Protocol",
                description = "Dedicated high-intensity sprint targeting your lowest life pillar.",
                dimension = weakestDimension,
                difficulty = rank1,
                pointsReward = (40 * rank1.xpMultiplier).toInt(),
                statRewardPoints = rank1.statRewardPoints,
                estimatedMinutes = 20,
                subObjectives = listOf(
                    "Identify top friction source in ${weakestDimension.displayName}",
                    "Execute 15 minutes of non-distracted remediation action",
                    "Log retrospective insight in AI journal"
                )
            )
        )

        // Quest 2: High Rank Boss Challenge (Rank A/S)
        val rank2 = if (userLevel >= 5) QuestDifficulty.S else QuestDifficulty.A
        list.add(
            AiQuest(
                id = UUID.randomUUID().toString(),
                title = "⚔️ Monarch's Discipline Barrier",
                description = "Lock into zero-distraction deep work mode for consecutive milestone delivery.",
                dimension = DimensionType.CAREER,
                difficulty = rank2,
                pointsReward = (60 * rank2.xpMultiplier).toInt(),
                statRewardPoints = rank2.statRewardPoints,
                estimatedMinutes = 45,
                subObjectives = listOf(
                    "Enable Do Not Disturb across all hardware",
                    "Complete 45-minute continuous focus block",
                    "Ship one key tangible deliverable"
                )
            )
        )

        // Quest 3: Physical Vitality Core (Rank D)
        list.add(
            AiQuest(
                id = UUID.randomUUID().toString(),
                title = "🏃 Bio-Harmonic Stamina Surge",
                description = "Cardiovascular circulation boost and hydration optimization.",
                dimension = DimensionType.FITNESS,
                difficulty = QuestDifficulty.D,
                pointsReward = (25 * QuestDifficulty.D.xpMultiplier).toInt(),
                statRewardPoints = QuestDifficulty.D.statRewardPoints,
                estimatedMinutes = 15,
                subObjectives = listOf(
                    "Drink 500ml water immediately",
                    "Complete 25 push-ups or 15-min brisk walk",
                    "Record vitality post-exercise state"
                )
            )
        )

        return list
    }
}
