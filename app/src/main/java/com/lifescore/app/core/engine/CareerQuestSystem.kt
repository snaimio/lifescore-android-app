package com.lifescore.app.core.engine

import com.lifescore.app.core.util.CareerMatch
import com.lifescore.app.domain.model.CareerQuest
import com.lifescore.app.domain.model.CareerQuestDay

object CareerQuestSystem {

    /**
     * Generates a 7-day experiential "Try-Before-You-Buy" Career Exploration Quest
     * for a given RIASEC career match.
     */
    fun generateCareerQuest(career: CareerMatch): CareerQuest {
        val days = listOf(
            CareerQuestDay(
                dayNumber = 1,
                title = "Day 1: Foundations & Day-in-the-Life",
                description = "Understand core responsibilities, workplace environments, and tools in ${career.title}.",
                actionPrompt = "Read 1 industry case study or watch a 15-minute 'Day in the Life of a ${career.title}' breakdown.",
                xpReward = 40
            ),
            CareerQuestDay(
                dayNumber = 2,
                title = "Day 2: Core Skill Audit",
                description = "Audit your baseline against required skills: ${career.topSkills.take(3).joinToString(", ")}.",
                actionPrompt = "Complete a self-assessment on the 3 core skills and identify your single biggest leverage gap.",
                xpReward = 45
            ),
            CareerQuestDay(
                dayNumber = 3,
                title = "Day 3: Micro-Project Prototype",
                description = "Execute a miniature deliverable simulating authentic ${career.title} workflows.",
                actionPrompt = "Spend 30 minutes drafting a concept spec, miniature code repo, or strategic slide wireframe.",
                xpReward = 60
            ),
            CareerQuestDay(
                dayNumber = 4,
                title = "Day 4: Industry Trends & Disruption",
                description = "Investigate how AI and market shifts are transforming ${career.title} roles.",
                actionPrompt = "Identify 3 high-growth emerging niches within this sector.",
                xpReward = 45
            ),
            CareerQuestDay(
                dayNumber = 5,
                title = "Day 5: Shadow & Connect",
                description = "Explore real practitioners and portfolio standards in the industry.",
                actionPrompt = "Review 2 senior LinkedIn profiles or public portfolios in ${career.title} to benchmark trajectory.",
                xpReward = 50
            ),
            CareerQuestDay(
                dayNumber = 6,
                title = "Day 6: Portfolio Artifact Polish",
                description = "Refine your sample project or case study notes into a tangible artifact.",
                actionPrompt = "Write a 1-page executive summary or polished README of your Day 3 prototype.",
                xpReward = 55
            ),
            CareerQuestDay(
                dayNumber = 7,
                title = "Day 7: Career Decision Matrix",
                description = "Evaluate energy alignment, compensation trajectory, and personal passion.",
                actionPrompt = "Score ${career.title} (1-10) on Energy, Autonomy, Wealth, and Impact to finalize your direction.",
                xpReward = 75
            )
        )

        return CareerQuest(
            id = "quest_${career.title.lowercase().replace(" ", "_").replace("/", "_")}",
            careerTitle = career.title,
            riasecCode = career.riasecCode,
            salaryRange = career.salaryRange,
            description = career.description,
            topSkills = career.topSkills,
            days = days,
            totalXpReward = days.sumOf { it.xpReward }
        )
    }

    /**
     * Generates a catalogue of career quests based on a list of top career matches.
     */
    fun generateCareerQuestCatalog(careerMatches: List<CareerMatch>): List<CareerQuest> {
        return careerMatches.map { generateCareerQuest(it) }
    }
}
