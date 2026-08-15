package com.lifescore.app.core.util

import com.lifescore.app.domain.model.*

object EnterpriseManager {

    fun getDefaultOrg(): EnterpriseOrg {
        return EnterpriseOrg(
            id = "org_acme_01",
            companyName = "Acme Technologies Inc.",
            domain = "acme.com",
            planTier = B2BPlanTier.GROWTH,
            totalSeats = 100,
            adminEmail = "tanjin@acme.com"
        )
    }

    fun getDefaultMembers(): List<EnterpriseMember> {
        return listOf(
            EnterpriseMember(
                displayName = "Alex Rivera",
                email = "alex@acme.com",
                role = EnterpriseRole.TEAM_LEAD,
                department = DepartmentType.ENGINEERING,
                lifeScore = 890,
                currentStreak = 24,
                weeklyQuestsCompleted = 28,
                avatarEmoji = "👨‍💻"
            ),
            EnterpriseMember(
                displayName = "Sophia Moreau",
                email = "sophia@acme.com",
                role = EnterpriseRole.ADMIN,
                department = DepartmentType.OPERATIONS_HR,
                lifeScore = 910,
                currentStreak = 30,
                weeklyQuestsCompleted = 32,
                avatarEmoji = "👩‍💼"
            ),
            EnterpriseMember(
                displayName = "Elena Rostova",
                email = "elena@acme.com",
                role = EnterpriseRole.TEAM_LEAD,
                department = DepartmentType.PRODUCT_DESIGN,
                lifeScore = 860,
                currentStreak = 21,
                weeklyQuestsCompleted = 26,
                avatarEmoji = "👩‍🎨"
            ),
            EnterpriseMember(
                displayName = "Marcus Sterling",
                email = "marcus@acme.com",
                role = EnterpriseRole.TEAM_LEAD,
                department = DepartmentType.SALES_GROWTH,
                lifeScore = 780,
                currentStreak = 12,
                weeklyQuestsCompleted = 20,
                avatarEmoji = "👨‍💼"
            ),
            EnterpriseMember(
                displayName = "Priya Sharma",
                email = "priya@acme.com",
                role = EnterpriseRole.MEMBER,
                department = DepartmentType.ENGINEERING,
                lifeScore = 820,
                currentStreak = 18,
                weeklyQuestsCompleted = 24,
                avatarEmoji = "👩‍💻"
            ),
            EnterpriseMember(
                displayName = "Jordan Hayes",
                email = "jordan@acme.com",
                role = EnterpriseRole.MEMBER,
                department = DepartmentType.PRODUCT_DESIGN,
                lifeScore = 790,
                currentStreak = 14,
                weeklyQuestsCompleted = 22,
                avatarEmoji = "🧑‍💻"
            ),
            EnterpriseMember(
                displayName = "Samira Khan",
                email = "samira@acme.com",
                role = EnterpriseRole.MEMBER,
                department = DepartmentType.ENGINEERING,
                lifeScore = 730,
                currentStreak = 9,
                weeklyQuestsCompleted = 18,
                avatarEmoji = "👩‍🔧"
            ),
            EnterpriseMember(
                displayName = "David Kim",
                email = "david@acme.com",
                role = EnterpriseRole.MEMBER,
                department = DepartmentType.SALES_GROWTH,
                lifeScore = 680,
                currentStreak = 5,
                weeklyQuestsCompleted = 12,
                isBurnoutRisk = true,
                avatarEmoji = "👨‍💼"
            )
        )
    }

    fun getDefaultChallenges(): List<EnterpriseChallenge> {
        return listOf(
            EnterpriseChallenge(
                id = "ent_ch_deep_work",
                title = "🎯 Q3 Enterprise Deep Work Sprint",
                description = "Aggregate 2,500 uninterrupted hours of deep flow focus across all engineering and product teams.",
                targetDimension = DimensionType.CAREER,
                currentProgress = 1850,
                targetGoal = 2500,
                unit = "hours",
                rewardXpPerMember = 1200,
                daysRemaining = 12,
                participantsCount = 78
            ),
            EnterpriseChallenge(
                id = "ent_ch_million_steps",
                title = "🏃 Company 1,000,000 Step Odyssey",
                description = "Promote physical health and cardiovascular vitality with a joint 1M steps target.",
                targetDimension = DimensionType.FITNESS,
                currentProgress = 740000,
                targetGoal = 1000000,
                unit = "steps",
                rewardXpPerMember = 1000,
                daysRemaining = 16,
                participantsCount = 84
            ),
            EnterpriseChallenge(
                id = "ent_ch_burnout_shield",
                title = "🧘 Circadian Reset & Burnout Prevention",
                description = "Complete 100 wind-down breathwork sessions and 8-hour sleep streaks across all departments.",
                targetDimension = DimensionType.MENTAL_HEALTH,
                currentProgress = 89,
                targetGoal = 100,
                unit = "routines",
                rewardXpPerMember = 800,
                daysRemaining = 6,
                participantsCount = 65
            )
        )
    }

    fun calculateDepartmentLeaderboard(members: List<EnterpriseMember>): List<DepartmentLeaderboardItem> {
        val grouped = members.groupBy { it.department }

        val items = grouped.map { (dept, deptMembers) ->
            val count = deptMembers.size
            val avgScore = if (count > 0) deptMembers.map { it.lifeScore }.average().toFloat() else 0f
            val avgStreak = if (count > 0) deptMembers.map { it.currentStreak }.average().toFloat() else 0f
            val totalQuests = deptMembers.sumOf { it.weeklyQuestsCompleted }

            DepartmentLeaderboardItem(
                department = dept,
                memberCount = count,
                averageLifeScore = avgScore,
                averageStreak = avgStreak,
                totalQuestsCompleted = totalQuests,
                rank = 1
            )
        }.sortedByDescending { it.averageLifeScore }

        return items.mapIndexed { index, item -> item.copy(rank = index + 1) }
    }

    fun calculateCompanyVitalityIndex(members: List<EnterpriseMember>): Float {
        if (members.isEmpty()) return 80.0f
        val avg = members.map { it.lifeScore }.average().toFloat()
        // Normalized 0 to 100 (where 1000 is 100)
        return (avg / 10f).coerceIn(0f, 100f)
    }

    fun calculateBurnoutMetrics(members: List<EnterpriseMember>): List<BurnoutRiskMetric> {
        return DepartmentType.values().map { dept ->
            val deptMembers = members.filter { it.department == dept }
            val burnoutCount = deptMembers.count { it.isBurnoutRisk }
            val riskPercent = if (deptMembers.isNotEmpty()) (burnoutCount.toFloat() / deptMembers.size * 100).toInt() else 0

            val (level, rec) = when {
                riskPercent >= 40 -> Pair("HIGH", "⚠️ Recommend mandatory no-meeting afternoons and deep focus blocks.")
                riskPercent >= 20 -> Pair("MODERATE", "⚡ Watch workload closely; encourage evening circadian disconnect.")
                else -> Pair("LOW", "✅ Optimum flow state and balanced sustainable habits.")
            }

            BurnoutRiskMetric(
                department = dept,
                riskLevel = level,
                riskScorePercent = riskPercent,
                recommendations = rec
            )
        }
    }

    fun calculateBillingQuote(
        plan: B2BPlanTier,
        seatCount: Int,
        isAnnual: Boolean
    ): Double {
        val perSeatRate = if (isAnnual) plan.annualPerSeatCost else plan.monthlyPerSeatCost
        val monthlyTotal = seatCount * perSeatRate
        return if (isAnnual) monthlyTotal * 12 else monthlyTotal
    }
}
