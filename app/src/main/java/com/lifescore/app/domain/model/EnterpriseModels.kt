package com.lifescore.app.domain.model

import java.util.UUID

enum class EnterpriseRole(val displayName: String, val badgeEmoji: String) {
    ADMIN("Enterprise Admin", "👑"),
    TEAM_LEAD("Department Lead", "🎖️"),
    MEMBER("Team Member", "👤")
}

enum class DepartmentType(val displayName: String, val iconEmoji: String) {
    ENGINEERING("Engineering & Architecture", "💻"),
    PRODUCT_DESIGN("Product & UX Design", "🎨"),
    SALES_GROWTH("Sales & Enterprise Growth", "📈"),
    OPERATIONS_HR("Operations & People", "⚡")
}

enum class B2BPlanTier(
    val title: String,
    val monthlyPerSeatCost: Double,
    val annualPerSeatCost: Double,
    val maxSeats: Int,
    val badgeLabel: String
) {
    STARTUP("Startup Tier", 4.99, 3.99, 25, "STARTER"),
    GROWTH("Scale & Growth Tier", 8.99, 6.99, 100, "MOST POPULAR"),
    ENTERPRISE_UNLIMITED("Enterprise Unlimited", 12.99, 9.99, 10000, "ENTERPRISE")
}

data class EnterpriseOrg(
    val id: String = "org_acme_01",
    val companyName: String = "Acme Technologies Inc.",
    val domain: String = "acme.com",
    val planTier: B2BPlanTier = B2BPlanTier.GROWTH,
    val totalSeats: Int = 100,
    val adminEmail: String = "tanjin@acme.com",
    val createdAt: Long = System.currentTimeMillis() - 86400000L * 60
)

data class EnterpriseMember(
    val uid: String = UUID.randomUUID().toString(),
    val displayName: String,
    val email: String,
    val role: EnterpriseRole = EnterpriseRole.MEMBER,
    val department: DepartmentType,
    val lifeScore: Int,
    val currentStreak: Int,
    val weeklyQuestsCompleted: Int,
    val isBurnoutRisk: Boolean = false,
    val avatarEmoji: String = "👤"
)

data class EnterpriseChallenge(
    val id: String,
    val title: String,
    val description: String,
    val targetDimension: DimensionType,
    val currentProgress: Long,
    val targetGoal: Long,
    val unit: String,
    val rewardXpPerMember: Int = 1000,
    val daysRemaining: Int = 18,
    val participantsCount: Int = 78
) {
    val progressFraction: Float get() = (currentProgress.toFloat() / targetGoal).coerceIn(0f, 1f)
}

data class DepartmentLeaderboardItem(
    val department: DepartmentType,
    val memberCount: Int,
    val averageLifeScore: Float,
    val averageStreak: Float,
    val totalQuestsCompleted: Int,
    val rank: Int
)

data class BurnoutRiskMetric(
    val department: DepartmentType,
    val riskLevel: String, // "LOW", "MODERATE", "HIGH"
    val riskScorePercent: Int,
    val recommendations: String
)
