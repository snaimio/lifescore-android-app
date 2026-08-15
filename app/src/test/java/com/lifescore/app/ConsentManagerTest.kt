package com.lifescore.app

import org.junit.Assert.*
import org.junit.Test

class ConsentManagerTest {

    @Test
    fun testJsonExportDataSchema() {
        val jsonExportString = """
            {
              "app": "LifeScore",
              "version": "1.0.0",
              "exportedAt": "1786753000000",
              "userProfile": {
                "name": "Champion Hero",
                "level": 5,
                "lifeScore": 780,
                "streakDays": 14,
                "archetype": "The Warrior"
              },
              "dimensions": {
                "fitness": 85,
                "career": 90,
                "learning": 80,
                "health": 75,
                "mentalHealth": 80,
                "wealth": 85,
                "relationships": 75,
                "socialLife": 70
              }
            }
        """.trimIndent()

        assertTrue(jsonExportString.contains("\"app\": \"LifeScore\""))
        assertTrue(jsonExportString.contains("\"version\": \"1.0.0\""))
        assertTrue(jsonExportString.contains("\"name\": \"Champion Hero\""))
        assertTrue(jsonExportString.contains("\"level\": 5"))
        assertTrue(jsonExportString.contains("\"lifeScore\": 780"))
        assertTrue(jsonExportString.contains("\"streakDays\": 14"))
        assertTrue(jsonExportString.contains("\"archetype\": \"The Warrior\""))
        assertTrue(jsonExportString.contains("\"fitness\": 85"))
        assertTrue(jsonExportString.contains("\"career\": 90"))
        assertTrue(jsonExportString.contains("\"mentalHealth\": 80"))
    }
}
