package com.lifescore.app

import com.lifescore.app.core.util.EnterpriseManager
import com.lifescore.app.domain.model.*
import org.junit.Assert.*
import org.junit.Test

class EnterpriseEngineTest {

    @Test
    fun testDefaultOrganizationDataIntegrity() {
        val org = EnterpriseManager.getDefaultOrg()
        assertEquals("Acme Technologies Inc.", org.companyName)
        assertEquals("acme.com", org.domain)
        assertEquals(B2BPlanTier.GROWTH, org.planTier)
        assertEquals(100, org.totalSeats)
    }

    @Test
    fun testDefaultMembersRosterAcrossDepartments() {
        val members = EnterpriseManager.getDefaultMembers()
        assertTrue(members.size >= 8)

        val depts = members.map { it.department }.toSet()
        assertTrue(depts.contains(DepartmentType.ENGINEERING))
        assertTrue(depts.contains(DepartmentType.PRODUCT_DESIGN))
        assertTrue(depts.contains(DepartmentType.SALES_GROWTH))
        assertTrue(depts.contains(DepartmentType.OPERATIONS_HR))

        val roles = members.map { it.role }.toSet()
        assertTrue(roles.contains(EnterpriseRole.ADMIN))
        assertTrue(roles.contains(EnterpriseRole.TEAM_LEAD))
        assertTrue(roles.contains(EnterpriseRole.MEMBER))
    }

    @Test
    fun testDepartmentLeaderboardRanking() {
        val members = EnterpriseManager.getDefaultMembers()
        val leaderboard = EnterpriseManager.calculateDepartmentLeaderboard(members)

        assertEquals(4, leaderboard.size)
        assertEquals(1, leaderboard[0].rank)
        assertEquals(2, leaderboard[1].rank)
        assertEquals(3, leaderboard[2].rank)
        assertEquals(4, leaderboard[3].rank)

        // Verify descending average LifeScore order
        assertTrue(leaderboard[0].averageLifeScore >= leaderboard[1].averageLifeScore)
        assertTrue(leaderboard[1].averageLifeScore >= leaderboard[2].averageLifeScore)
    }

    @Test
    fun testCompanyVitalityIndexCalculation() {
        val members = EnterpriseManager.getDefaultMembers()
        val vitalityIndex = EnterpriseManager.calculateCompanyVitalityIndex(members)

        assertTrue(vitalityIndex in 50.0f..100.0f)
        val expectedAvg = members.map { it.lifeScore }.average().toFloat() / 10f
        assertEquals(expectedAvg, vitalityIndex, 0.01f)
    }

    @Test
    fun testBurnoutRiskDetectionForOverworkedTeams() {
        val members = EnterpriseManager.getDefaultMembers()
        val metrics = EnterpriseManager.calculateBurnoutMetrics(members)

        assertEquals(4, metrics.size)
        val salesMetric = metrics.firstOrNull { it.department == DepartmentType.SALES_GROWTH }
        assertNotNull(salesMetric)
        assertTrue(salesMetric!!.riskScorePercent > 0)
    }

    @Test
    fun testB2BBillingPricingCalculations() {
        // Startup tier: 20 seats @ $4.99/mo monthly = $99.80/mo
        val startupMonthly = EnterpriseManager.calculateBillingQuote(B2BPlanTier.STARTUP, 20, isAnnual = false)
        assertEquals(20 * 4.99, startupMonthly, 0.01)

        // Growth tier: 100 seats @ $6.99/mo annual = $699.00/mo * 12 = $8,388.00/yr
        val growthAnnual = EnterpriseManager.calculateBillingQuote(B2BPlanTier.GROWTH, 100, isAnnual = true)
        assertEquals(100 * 6.99 * 12, growthAnnual, 0.01)

        // Enterprise tier: 50 seats @ $9.99/mo annual
        val enterpriseAnnual = EnterpriseManager.calculateBillingQuote(B2BPlanTier.ENTERPRISE_UNLIMITED, 50, isAnnual = true)
        assertEquals(50 * 9.99 * 12, enterpriseAnnual, 0.01)
    }

    @Test
    fun testSharedCompanyChallengesProgressFraction() {
        val challenges = EnterpriseManager.getDefaultChallenges()
        assertTrue(challenges.size >= 3)

        challenges.forEach { ch ->
            assertTrue(ch.progressFraction in 0f..1f)
            assertTrue(ch.rewardXpPerMember >= 500)
            assertTrue(ch.participantsCount > 0)
        }
    }
}
