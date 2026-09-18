# 🔍 LifeScore Quality Assurance & Functionality Audit (QA_AUDIT.md)

**Audit Date:** 2026-09-17  
**Lead QA Engineer:** Antigravity Senior QA Lead  
**Scope:** Full Screen Inventory, Back Navigation Affordance, Real Room DB / Firestore Data Verification, and Broken Flow Detection.

---

## 📱 1. Master Screen Inventory & Navigation Matrix

| Screen Composable | Route | Type | TopAppBar Back Arrow? | System Back Works? | Uses Real Data? | QA Status |
|---|---|---|---|---|---|---|
| **TodayScreen** | `today` / `home` | Top-Level Tab | N/A (Root Tab) | ✅ (Exits or stays) | ✅ Real Room + Firestore | ✅ **PASS** |
| **BalanceScreen** | `balance` / `dimensions` | Top-Level Tab | N/A (Root Tab) | ✅ | ⚠️ Mock trend strings | 🛠️ **FIXING** |
| **GrowScreen** | `grow` | Top-Level Tab | N/A (Root Tab) | ✅ | ✅ Real Room DB | ✅ **PASS** |
| **MeScreen** | `me` / `profile` | Top-Level Tab | N/A (Root Tab) | ✅ | ✅ Real Room DB | ✅ **PASS** |
| **ExploreScreen** | `explore` | Top-Level Tab | N/A (Root Tab) | ✅ | ✅ Real Room DB | ✅ **PASS** |
| **HydrationScreen** | `hydration` | Sub-Screen | ✅ (`LifeTopBar`) | ✅ | ✅ Real Room + Firestore | ✅ **PASS** |
| **FocusTimerScreen** | `focus_timer` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real FocusRepository | 🛠️ **FIXING** |
| **SleepStoriesScreen** | `sleep_soundscapes` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Room DB | 🛠️ **FIXING** |
| **MoodTrackerScreen** | `mood_tracker` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real MoodRepository | 🛠️ **FIXING** |
| **AiCoachScreen** | `ai_coach` | Sub-Screen | ✅ (`LifeTopBar`) | ✅ | ⚠️ Mock Weekly Audit Scores | 🛠️ **FIXING** |
| **MeditationLibraryScreen** | `meditation_library` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Audio Engine | 📋 Pending Batch 2 |
| **EnergyScheduleScreen** | `energy_schedule` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Room DB | 📋 Pending Batch 2 |
| **RecoveryDashboardScreen**| `recovery` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Room DB | 📋 Pending Batch 2 |
| **SOSScreen** | `recovery_sos` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Room DB | 📋 Pending Batch 2 |
| **ScreenTimeDashboardScreen**| `screen_time` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real ScreenTimeRepository | 📋 Pending Batch 2 |
| **MinimalistLauncherScreen**| `minimalist_launcher` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real App Launching | 📋 Pending Batch 2 |
| **DailyGrowthScreen** | `daily_growth` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real DailyGrowthRepository | 📋 Pending Batch 2 |
| **BookSummaryLibraryScreen**| `book_library` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real BookRepository | 📋 Pending Batch 2 |
| **BookDetailSummaryScreen** | `book_detail` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real BookRepository | 📋 Pending Batch 2 |
| **ScienceJourneyScreen** | `science_journeys` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real ScienceRepository | 📋 Pending Batch 2 |
| **AtomicHabitsDashboardScreen**| `atomic_habits` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Room DB | 📋 Pending Batch 2 |
| **ActionPlanScreen** | `action_plan` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Room DB | 📋 Pending Batch 2 |
| **ThoughtBreakScreen** | `thought_break` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Room DB | 📋 Pending Batch 2 |
| **NeurodivergentHubScreen**| `neurodivergent_hub`| Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Room DB | 📋 Pending Batch 2 |
| **CharacterSystemScreen** | `character_stats` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Room DB | 📋 Pending Batch 2 |
| **SkillMasteryScreen** | `skill_mastery` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Room DB | 📋 Pending Batch 2 |
| **CombatScreen** | `combat` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Room DB | 📋 Pending Batch 2 |
| **VirtualPetScreen** | `virtual_pet` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Room DB | 📋 Pending Batch 2 |
| **StreakVaultScreen** | `streak_vault` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Room DB | 📋 Pending Batch 2 |
| **JournalScreen** | `journal` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Room DB | 📋 Pending Batch 2 |
| **MemeStudioScreen** | `meme_studio` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Room DB | 📋 Pending Batch 2 |
| **FriendsFeedScreen** | `friends_feed` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Firestore | 📋 Pending Batch 2 |
| **GroupHabitScreen** | `group_habits` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Firestore | 📋 Pending Batch 2 |
| **LeaderboardScreen** | `leaderboard` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Firestore | 📋 Pending Batch 2 |
| **LeagueTiersScreen** | `league_tiers` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Firestore | 📋 Pending Batch 2 |
| **ViralReferralScreen** | `viral_referrals` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Firestore | 📋 Pending Batch 2 |
| **RewardStoreScreen** | `reward_store` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Room DB | 📋 Pending Batch 2 |
| **GemStoreScreen** | `gem_store` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Billing | 📋 Pending Batch 2 |
| **CosmeticsStoreScreen** | `cosmetic_store` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Room DB | 📋 Pending Batch 2 |
| **SubscriptionScreen** | `supporter_subscription`| Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Billing | 📋 Pending Batch 2 |
| **SettingsScreen** | `settings` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real DataStore | 📋 Pending Batch 2 |
| **PrivacyDashboardScreen** | `privacy` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real GDPR Engine | 📋 Pending Batch 2 |
| **EnterpriseDashboardScreen**| `enterprise` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ✅ Real Room DB | 📋 Pending Batch 2 |
| **LoginScreen** | `login` | Sub-Screen | ⚠️ Raw TopAppBar | ✅ | ⚠️ Mock Token Demo | 🛠️ **FIXING** |
| **SplashScreen** | `splash` | Onboarding | N/A | ✅ | ✅ Real Prefs | ✅ **PASS** |
| **WelcomeScreen** | `welcome` | Onboarding | N/A | ✅ | ✅ Real Engine | ✅ **PASS** |
| **QuickAssessmentScreen** | `quick_assessment`| Onboarding | ✅ Back button | ✅ | ✅ Real Engine | ✅ **PASS** |
| **QuickResultsScreen** | `quick_results` | Onboarding | ✅ Back button | ✅ | ✅ Real Engine | ✅ **PASS** |
| **FirstQuestScreen** | `first_quest` | Onboarding | N/A (One-way) | ✅ | ✅ Real Room DB | ✅ **PASS** |

---

## 🎯 2. Identified High-Priority Issues (Batch 1: First 5 Broken Screens)

1. **`AiCoachScreen` (Mock Data & Navigation)**:
   - *Issue*: `generateWeeklyAudit()` passed a hardcoded `mockScores = DimensionType.values().associateWith { 75 }` and `tasksCompleted = 24` instead of reading live completed tasks and dimension percentages from `LifeScoreRepository`.
   - *Fix*: Connect `generateWeeklyAudit()` to query real tasks and dimension scores from `LifeScoreRepository`.

2. **`BalanceScreen` (Mock Trend Strings)**:
   - *Issue*: Dimension card trends used `val mockTrend = remember(dimension) { ... }` with hardcoded `"+8% this week"` strings.
   - *Fix*: Calculate real weekly score deltas from `ScoreEngine` and `DimensionHistoryEntity`.

3. **`FocusTimerScreen` (Navigation & Emoji Header)**:
   - *Issue*: Used raw `TopAppBar` with emoji header and legacy 24dp back box instead of `LifeTopBar` with standard 48dp back navigation affordance.
   - *Fix*: Refactor to `LifeTopBar(title = "Mindful Focus", subtitle = "Deep Work & Habit Trees", onBack = onBack)`.

4. **`SleepStoriesScreen` (Navigation & Emoji Header)**:
   - *Issue*: Used raw `TopAppBar` with emoji header instead of `LifeTopBar` with standard 48dp back navigation affordance.
   - *Fix*: Refactor to `LifeTopBar(title = "Sleep & Soundscapes", subtitle = "Restorative Rest & Ambient Audio", onBack = onBack)`.

5. **`MoodTrackerScreen` (Navigation & Emoji Header)**:
   - *Issue*: Used raw `TopAppBar` with emoji header instead of `LifeTopBar` with standard 48dp back navigation affordance.
   - *Fix*: Refactor to `LifeTopBar(title = "Mood Tracker", subtitle = "Emotional Telemetry & Well-Being", onBack = onBack)`.
