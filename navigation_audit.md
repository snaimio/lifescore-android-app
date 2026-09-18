# Navigation Audit: LifeScore Android App

Comprehensive audit of all top-level destinations, sub-screens, and back navigation affordances.

---

## 🧭 1. Top-Level Tabs (NO Back Button in TopAppBar)
These 5 tabs represent the primary root navigation destinations. Pressing the system back button from these destinations either switches to the `Today` tab or exits the app cleanly.

- [x] **TodayScreen** (`today` / `home`) — Tab 1: Daily Focus & Ritual Loop
- [x] **BalanceScreen** (`balance` / `dimensions`) — Tab 2: 360° Life Matrix Radar
- [x] **GrowScreen** (`grow`) — Tab 3: Active Goals, Stacks & Quests
- [x] **MeScreen** (`me` / `profile`) — Tab 4: Character Sheet & Identity
- [x] **ExploreScreen** (`explore`) — Tab 5: 15 Trackers, Summaries & Miniapps

---

## 🔙 2. Sub-Screens (MUST HAVE Back Arrow Navigation in TopAppBar)
All sub-screens must provide:
1. Visible `Icons.AutoMirrored.Rounded.ArrowBack` with minimum 48dp touch target.
2. `contentDescription = "Navigate back"` for accessibility and TalkBack.
3. Explicit `onBack = { navController.popBackStack() }` wiring.
4. Support for Android Predictive Back and edge gestures.

### Onboarding & Auth Flow
- [ ] `SplashScreen` (`splash`) — Root launch splash
- [ ] `WelcomeScreen` (`welcome`) — Zero-friction welcome
- [ ] `QuickAssessmentScreen` (`quick_assessment`) — 10-Question assessment
- [ ] `QuickResultsScreen` (`quick_results`) — Archetype reveal
- [ ] `FirstQuestScreen` (`first_quest`) — First habit completion
- [ ] `LoginScreen` (`login`) — Auth & Cloud sync
- [ ] `ConsentScreen` (`consent`) — Privacy & data consent

### Health & Vitality Trackers
- [ ] `HydrationScreen` (`hydration`) — Hydration & water logs
- [ ] `SleepSoundscapesScreen` (`sleep_soundscapes`) — Sleep timer & ambient soundscapes
- [ ] `FocusTimerScreen` (`focus_timer`) — Pomodoro & mindful trees
- [ ] `MoodTrackerScreen` (`mood_tracker`) — Emotional log & triggers
- [ ] `MeditationLibraryScreen` (`meditation_library`) — Guided sessions & breathwork
- [ ] `EnergyScheduleScreen` (`energy_schedule`) — Chronotype & energy curves
- [ ] `RecoveryDashboardScreen` (`recovery`) — Habit cessation & sober counter
- [ ] `SOSScreen` (`recovery_sos`) — Craving intervention & emergency contacts
- [ ] `ScreenTimeDashboardScreen` (`screen_time`) — App limits & digital detox
- [ ] `MinimalistLauncherScreen` (`minimalist_launcher`) — High-focus monochrome launcher

### Growth, Learning & Habits
- [ ] `HabitLibraryScreen` (`habit_library`) — 100+ Science-backed habit catalog
- [ ] `DailyGrowthScreen` (`daily_growth`) — 15-Minute daily micro-learning
- [ ] `BookSummaryLibraryScreen` (`book_library`) — Core book takeaways
- [ ] `BookDetailSummaryScreen` (`book_detail`) — Detailed chapter breakdowns
- [ ] `BookDiscoveryAndFlashcardsScreen` (`book_flashcards`) — Active recall flashcards
- [ ] `ScienceJourneyScreen` (`science_journeys`) — 14-Day evidence protocols
- [ ] `AtomicHabitsDashboardScreen` (`atomic_habits`) — 4 Laws implementation
- [ ] `FourLawsScreen` (`four_laws`) — Friction reduction tools
- [ ] `HabitIdentityScreen` (`habit_identity`) — Identity voting tracker
- [ ] `HabitScorecardScreen` (`habit_scorecard`) — Habit awareness grid
- [ ] `SystemDesignJournalScreen` (`system_design_journal`) — Environment architect
- [ ] `ActionPlanScreen` (`action_plan`) — Milestone execution plans
- [ ] `ThoughtBreakScreen` (`thought_break`) — Cognitive reframing & CBT
- [ ] `NeurodivergentHubScreen` (`neurodivergent_hub`) — ADHD soft-focus workspace

### Gamification, Character & AI
- [ ] `AiCoachScreen` (`ai_coach`) — Conversational AI Coach
- [ ] `AiMemoryInspectorScreen` (`ai_memory_inspector`) — AI context inspector
- [ ] `AiQuestScreen` (`ai_quests`) — Procedural daily quest tree
- [ ] `CharacterSystemScreen` (`character_stats`) — RPG stats, gear & attributes
- [ ] `SkillMasteryScreen` (`skill_mastery`) — 10,000-Hour skill progressions
- [ ] `CombatScreen` (`combat`) — Boss raids & party battles
- [ ] `VirtualPetScreen` (`virtual_pet`) — Companion evolving with habits
- [ ] `StreakVaultScreen` (`streak_vault`) — Streak protection & insurance
- [ ] `JournalScreen` (`journal`) — Encrypted daily reflections
- [ ] `MemeStudioScreen` (`meme_studio`) — Habit meme generator

### Social, Viral & Monetization
- [ ] `FriendsFeedScreen` (`friends_feed`) — Squad activity & cheers
- [ ] `GroupHabitScreen` (`group_habits`) — Shared accountability pods
- [ ] `LeaderboardScreen` (`leaderboard`) — Weekly XP & tier standings
- [ ] `LeagueTiersScreen` (`league_tiers`) — 10-Tier promotion brackets
- [ ] `ViralReferralScreen` (`viral_referrals`) — Referral codes & free Pro
- [ ] `RewardStoreScreen` (`reward_store` / `custom_rewards`) — Gold rewards shop
- [ ] `GemStoreScreen` (`gem_store`) — Gem packs
- [ ] `CosmeticsStoreScreen` (`cosmetic_store`) — Themes & avatar cosmetics
- [ ] `SupporterSubscriptionScreen` (`supporter_subscription`) — Pro subscriptions
- [ ] `SettingsScreen` (`settings`) — App preferences & data export
- [ ] `PrivacyDashboardScreen` (`privacy`) — Data deletion & permissions
- [ ] `EnterpriseDashboardScreen` (`enterprise`) — Team wellness metrics
