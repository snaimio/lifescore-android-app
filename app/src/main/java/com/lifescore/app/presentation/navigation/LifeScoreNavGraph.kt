package com.lifescore.app.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.lifescore.app.LifeScoreApp
import com.lifescore.app.presentation.challenges.ChallengesScreen
import com.lifescore.app.presentation.challenges.ChallengesViewModel
import com.lifescore.app.presentation.coach.AiCoachScreen
import com.lifescore.app.presentation.coach.AiCoachViewModel
import com.lifescore.app.presentation.paywall.PaywallBottomSheet
import com.lifescore.app.presentation.ui.dimensions.DimensionsScreen
import com.lifescore.app.presentation.ui.dimensions.DimensionsViewModel
import com.lifescore.app.presentation.ui.home.HomeScreen
import com.lifescore.app.presentation.ui.home.HomeViewModel
import com.lifescore.app.presentation.ui.leaderboard.LeaderboardScreen
import com.lifescore.app.presentation.ui.leaderboard.LeaderboardViewModel
import com.lifescore.app.presentation.ui.settings.SettingsScreen
import com.lifescore.app.presentation.ui.tasks.TasksScreen
import com.lifescore.app.presentation.ui.tasks.TasksViewModel
import com.lifescore.app.presentation.vlogs.MicroVlogsScreen
import com.lifescore.app.presentation.vlogs.MicroVlogsViewModel

import kotlinx.coroutines.launch

@Composable
fun LifeScoreNavGraph(
    navController: NavHostController,
    app: LifeScoreApp
) {
    var showAuthModal by remember { mutableStateOf(false) }
    var showPaywall by remember { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val userProfile by app.lifeScoreRepository.getUserProfile().collectAsState(initial = null)

    // ViewModels
    val homeViewModel = remember {
        HomeViewModel(
            repository = app.lifeScoreRepository,
            firebaseRepository = app.firebaseRepository,
            authRepository = app.authRepository
        )
    }
    val homeState by homeViewModel.uiState.collectAsState()
    val userPhase = homeState.userPhase

    val bottomBarItems: List<Screen> = remember {
        listOf(
            Screen.Home,
            Screen.Tasks,
            Screen.Dimensions,
            Screen.Profile
        )
    }

    val showBottomBar = bottomBarItems.any { it.route == currentRoute }
    val dimensionsViewModel = remember { DimensionsViewModel(app.lifeScoreRepository) }
    val tasksViewModel = remember { TasksViewModel(app.lifeScoreRepository) }
    val coachViewModel = remember {
        AiCoachViewModel(
            coachRepository = app.coachRepository,
            lifeScoreRepository = app.lifeScoreRepository
        )
    }
    val challengesViewModel = remember {
        ChallengesViewModel(
            repository = app.lifeScoreRepository,
            firebaseRepository = app.firebaseRepository,
            authRepository = app.authRepository
        )
    }
    val leaderboardViewModel = remember {
        LeaderboardViewModel(
            repository = app.lifeScoreRepository,
            firebaseRepository = app.firebaseRepository,
            authRepository = app.authRepository
        )
    }
    val microVlogsViewModel = remember { MicroVlogsViewModel(app.lifeScoreRepository) }
    var quickAssessmentResult by remember { mutableStateOf<com.lifescore.app.core.util.QuickAssessmentResult?>(null) }

    val startDestination = Screen.Splash.route

    // Transition specs for smooth page navigation
    val enterTransition: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
        fadeIn(animationSpec = tween(300)) + slideInHorizontally(
            initialOffsetX = { fullWidth -> (fullWidth * 0.08f).toInt() },
            animationSpec = tween(300)
        )
    }
    val exitTransition: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
        fadeOut(animationSpec = tween(250)) + slideOutHorizontally(
            targetOffsetX = { fullWidth -> -(fullWidth * 0.08f).toInt() },
            animationSpec = tween(250)
        )
    }
    val popEnterTransition: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
        fadeIn(animationSpec = tween(300)) + slideInHorizontally(
            initialOffsetX = { fullWidth -> -(fullWidth * 0.08f).toInt() },
            animationSpec = tween(300)
        )
    }
    val popExitTransition: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
        fadeOut(animationSpec = tween(250)) + slideOutHorizontally(
            targetOffsetX = { fullWidth -> (fullWidth * 0.08f).toInt() },
            animationSpec = tween(250)
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showBottomBar,
        drawerContent = {
            LifeScoreDrawerContent(
                userProfile = userProfile,
                currentRoute = currentRoute,
                navController = navController,
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        tonalElevation = 3.dp,
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        bottomBarItems.forEach { screen ->
                            NavigationBarItem(
                                selected = currentRoute == screen.route,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        screen.icon,
                                        contentDescription = screen.title,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = when (screen) {
                                            Screen.Tasks -> "Quests"
                                            Screen.Dimensions -> "Stats"
                                            Screen.Profile -> "Me"
                                            else -> screen.title
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = if (currentRoute == screen.route) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding),
                enterTransition = { enterTransition() },
                exitTransition = { exitTransition() },
                popEnterTransition = { popEnterTransition() },
                popExitTransition = { popExitTransition() }
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        navController = navController,
                        viewModel = homeViewModel,
                        onOpenPaywall = { showPaywall = true },
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
            composable(Screen.Dimensions.route) {
                DimensionsScreen(
                    navController = navController,
                    viewModel = dimensionsViewModel
                )
            }
            composable(Screen.Tasks.route) {
                TasksScreen(
                    navController = navController,
                    viewModel = tasksViewModel
                )
            }
            composable(Screen.Challenges.route) {
                ChallengesScreen(
                    viewModel = challengesViewModel,
                    onOpenPaywall = { showPaywall = true }
                )
            }
            composable(Screen.AICoach.route) {
                AiCoachScreen(viewModel = coachViewModel)
            }
            composable(Screen.Leaderboard.route) {
                LeaderboardScreen(
                    navController = navController,
                    viewModel = leaderboardViewModel
                )
            }
            composable(Screen.MicroVlogs.route) {
                MicroVlogsScreen(
                    viewModel = microVlogsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.ArchetypeProfile.route) {
                com.lifescore.app.presentation.ui.archetype.ArchetypeProfileScreen(
                    initialArchetypeId = "architect",
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.SkillMastery.route) {
                val skillViewModel = androidx.lifecycle.viewmodel.compose.viewModel<com.lifescore.app.presentation.skills.SkillMasteryViewModel>()
                com.lifescore.app.presentation.skills.SkillMasteryScreen(
                    viewModel = skillViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.RewardStore.route) {
                val storeViewModel = remember { com.lifescore.app.presentation.store.RewardStoreViewModel(app.lifeScoreRepository) }
                com.lifescore.app.presentation.store.RewardStoreScreen(
                    viewModel = storeViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Enterprise.route) {
                val enterpriseViewModel = androidx.lifecycle.viewmodel.compose.viewModel<com.lifescore.app.presentation.enterprise.EnterpriseViewModel>()
                com.lifescore.app.presentation.enterprise.EnterpriseDashboardScreen(
                    viewModel = enterpriseViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Profile.route) {
                val profileViewModel = remember { com.lifescore.app.presentation.ui.profile.ProfileViewModel(app.lifeScoreRepository) }
                com.lifescore.app.presentation.ui.profile.ProfileScreen(
                    viewModel = profileViewModel,
                    navController = navController
                )
            }
            composable(Screen.MemeStudio.route) {
                val memeViewModel = androidx.lifecycle.viewmodel.compose.viewModel<com.lifescore.app.presentation.meme.MemeViewModel>()
                com.lifescore.app.presentation.meme.MemeStudioScreen(
                    viewModel = memeViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Consent.route) {
                com.lifescore.app.presentation.ui.consent.ConsentScreen(
                    onConsentResolved = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Consent.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    navController = navController,
                    onOpenPaywall = { showPaywall = true },
                    onOpenAuth = { showAuthModal = true }
                )
            }
            composable(Screen.Splash.route) {
                com.lifescore.app.presentation.ui.onboarding.SplashScreen(
                    onComplete = {
                        if (app.authRepository.currentUser != null) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Welcome.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    }
                )
            }
            composable(Screen.Welcome.route) {
                com.lifescore.app.presentation.ui.onboarding.WelcomeScreen(
                    onGetStarted = {
                        navController.navigate(Screen.QuickAssessment.route)
                    },
                    onSignIn = {
                        navController.navigate(Screen.Login.route)
                    }
                )
            }
            composable(Screen.QuickAssessment.route) {
                com.lifescore.app.presentation.ui.onboarding.QuickAssessmentScreen(
                    onComplete = { answers ->
                        val res = com.lifescore.app.core.util.QuickAssessmentEngine.evaluate(answers)
                        quickAssessmentResult = res
                        scope.launch {
                            app.lifeScoreRepository.updateUserProfile(
                                com.lifescore.app.domain.model.UserProfile(
                                    name = "Hero",
                                    title = res.archetype.displayName,
                                    currentLevel = 1,
                                    currentXp = 0
                                )
                            )
                        }
                        navController.navigate(Screen.QuickResults.route)
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.QuickResults.route) {
                val fallbackRes = remember {
                    com.lifescore.app.core.util.QuickAssessmentEngine.evaluate(emptyMap())
                }
                com.lifescore.app.presentation.ui.onboarding.QuickResultsScreen(
                    result = quickAssessmentResult ?: fallbackRes,
                    onContinue = {
                        navController.navigate(Screen.FirstQuest.route)
                    }
                )
            }
            composable(Screen.FirstQuest.route) {
                val activeResult = quickAssessmentResult ?: remember {
                    com.lifescore.app.core.util.QuickAssessmentEngine.evaluate(emptyMap())
                }
                com.lifescore.app.presentation.ui.onboarding.FirstQuestScreen(
                    questTitle = activeResult.firstQuestTitle,
                    dimension = activeResult.firstQuestDimension,
                    onCompleteQuest = {
                        scope.launch {
                            app.lifeScoreRepository.addTask(
                                title = activeResult.firstQuestTitle,
                                dimension = activeResult.firstQuestDimension,
                                points = 50
                            )
                            app.lifeScoreRepository.updateUserProfile(
                                com.lifescore.app.domain.model.UserProfile(
                                    name = "Hero",
                                    title = activeResult.archetype.displayName,
                                    currentLevel = 1,
                                    currentXp = 50,
                                    currentStreakDays = 1
                                )
                            )
                        }
                    },
                    onSkip = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Onboarding.route) {
                com.lifescore.app.presentation.ui.onboarding.SimplifiedOnboardingScreen(
                    onCompleteOnboarding = { archetype, ratings, startingScore, firstQuestTitle ->
                        scope.launch {
                            app.lifeScoreRepository.addTask(
                                title = firstQuestTitle,
                                dimension = com.lifescore.app.domain.model.DimensionType.HEALTH,
                                points = 50
                            )
                        }
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    },
                    onOpenFullAssessment = {
                        navController.navigate(Screen.FullAssessment.route)
                    }
                )
            }
            composable(Screen.FullAssessment.route) {
                com.lifescore.app.presentation.ui.onboarding.OnboardingAssessmentScreen(
                    onCompleteOnboarding = { archetype, ratings ->
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.FullAssessment.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Explore.route) {
                com.lifescore.app.presentation.ui.explore.ExploreSectionScreen(
                    currentPhase = userPhase,
                    onNavigateToRoute = { route -> navController.navigate(route) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Login.route) {
                val loginViewModel = remember { com.lifescore.app.presentation.ui.auth.LoginViewModel(app.authRepository) }
                com.lifescore.app.presentation.ui.auth.LoginScreen(
                    viewModel = loginViewModel,
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.AiQuests.route) {
                val aiQuestViewModel = remember {
                    com.lifescore.app.presentation.quest.AiQuestViewModel(
                        questRepository = app.aiQuestRepository,
                        lifeScoreRepository = app.lifeScoreRepository
                    )
                }
                com.lifescore.app.presentation.quest.AiQuestScreen(
                    viewModel = aiQuestViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.CharacterStats.route) {
                val characterViewModel = remember {
                    com.lifescore.app.presentation.character.CharacterViewModel(
                        characterRepository = app.characterStatsRepository,
                        lifeScoreRepository = app.lifeScoreRepository
                    )
                }
                com.lifescore.app.presentation.character.CharacterSystemScreen(
                    viewModel = characterViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.GroupHabits.route) {
                val groupHabitViewModel = remember {
                    com.lifescore.app.presentation.group.GroupHabitViewModel(
                        repository = app.groupHabitRepository
                    )
                }
                com.lifescore.app.presentation.group.GroupHabitScreen(
                    viewModel = groupHabitViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Journal.route) {
                val journalViewModel = remember {
                    com.lifescore.app.presentation.journal.JournalViewModel(
                        repository = app.journalRepository
                    )
                }
                com.lifescore.app.presentation.journal.JournalScreen(
                    viewModel = journalViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Combat.route) {
                val combatViewModel = remember {
                    com.lifescore.app.presentation.combat.CombatViewModel(
                        combatRepository = app.combatRepository,
                        statsRepository = app.characterStatsRepository
                    )
                }
                com.lifescore.app.presentation.combat.CombatScreen(
                    viewModel = combatViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Analytics.route) {
                val analyticsViewModel = remember {
                    com.lifescore.app.presentation.analytics.AnalyticsViewModel(
                        analyticsRepository = app.analyticsRepository,
                        lifeScoreRepository = app.lifeScoreRepository
                    )
                }
                com.lifescore.app.presentation.analytics.AnalyticsDashboardScreen(
                    viewModel = analyticsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Privacy.route) {
                val privacyViewModel = remember {
                    com.lifescore.app.presentation.privacy.PrivacyViewModel(
                        database = app.database
                    )
                }
                com.lifescore.app.presentation.privacy.PrivacyDashboardScreen(
                    viewModel = privacyViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.HabitLibrary.route) {
                com.lifescore.app.presentation.ui.habits.HabitLibraryScreen(
                    navController = navController
                )
            }
            composable(Screen.ActionPlan.route) {
                com.lifescore.app.presentation.ui.actionplan.ActionPlanScreen(
                    navController = navController
                )
            }
            composable(Screen.Hydration.route) {
                val hydrationViewModel = remember {
                    com.lifescore.app.presentation.ui.hydration.HydrationViewModel(
                        hydrationRepository = app.hydrationRepository,
                        lifeScoreRepository = app.lifeScoreRepository
                    )
                }
                com.lifescore.app.presentation.ui.hydration.HydrationScreen(
                    viewModel = hydrationViewModel,
                    navController = navController
                )
            }
            composable(Screen.TrackerHub.route) {
                val trackerHubViewModel = remember {
                    com.lifescore.app.presentation.ui.trackers.TrackerHubViewModel(
                        lifeTrackersRepository = app.lifeTrackersRepository,
                        lifeScoreRepository = app.lifeScoreRepository
                    )
                }
                com.lifescore.app.presentation.ui.trackers.TrackerHubScreen(
                    viewModel = trackerHubViewModel,
                    navController = navController
                )
            }
            composable(Screen.AtomicHabits.route) {
                val atomicHabitsViewModel = remember {
                    com.lifescore.app.presentation.ui.atomichabits.AtomicHabitsViewModel(
                        repository = app.atomicHabitsRepository,
                        lifeScoreRepository = app.lifeScoreRepository
                    )
                }
                com.lifescore.app.presentation.ui.atomichabits.AtomicHabitsDashboardScreen(
                    viewModel = atomicHabitsViewModel,
                    navController = navController
                )
            }
            composable(Screen.Recovery.route) {
                val recoveryViewModel = remember {
                    com.lifescore.app.presentation.ui.recovery.RecoveryViewModel(
                        repository = app.recoveryRepository
                    )
                }
                com.lifescore.app.presentation.ui.recovery.RecoveryDashboardScreen(
                    viewModel = recoveryViewModel,
                    navController = navController,
                    onOpenSOS = { navController.navigate(Screen.RecoverySos.route) }
                )
            }
            composable(Screen.RecoverySos.route) {
                com.lifescore.app.presentation.ui.recovery.SOSScreen(
                    onBack = { navController.popBackStack() },
                    onOpenAICoach = { navController.navigate(Screen.AICoach.route) }
                )
            }
            composable(Screen.BookLibrary.route) {
                val bookViewModel = remember {
                    com.lifescore.app.presentation.ui.books.BookSummaryViewModel(
                        repository = app.bookSummaryRepository
                    )
                }
                com.lifescore.app.presentation.ui.books.BookSummaryLibraryScreen(
                    viewModel = bookViewModel,
                    navController = navController
                )
            }
            composable(
                route = "book_detail/{bookId}",
                arguments = listOf(androidx.navigation.navArgument("bookId") { type = androidx.navigation.NavType.StringType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId") ?: "atomic_habits"
                val bookViewModel = remember {
                    com.lifescore.app.presentation.ui.books.BookSummaryViewModel(
                        repository = app.bookSummaryRepository
                    )
                }
                com.lifescore.app.presentation.ui.books.BookDetailSummaryScreen(
                    bookId = bookId,
                    viewModel = bookViewModel,
                    navController = navController
                )
            }
            composable(Screen.DailyGrowth.route) {
                val dailyGrowthViewModel = remember {
                    com.lifescore.app.presentation.ui.growth.DailyGrowthViewModel(
                        repository = app.dailyGrowthRepository
                    )
                }
                com.lifescore.app.presentation.ui.growth.DailyGrowthScreen(
                    viewModel = dailyGrowthViewModel,
                    navController = navController
                )
            }
            composable(Screen.FocusTimer.route) {
                val focusViewModel = remember {
                    com.lifescore.app.presentation.ui.focus.FocusViewModel(
                        repository = app.focusRepository
                    )
                }
                com.lifescore.app.presentation.ui.focus.FocusTimerScreen(
                    viewModel = focusViewModel,
                    navController = navController
                )
            }
            composable(Screen.MoodTracker.route) {
                val moodViewModel = remember {
                    com.lifescore.app.presentation.ui.mood.MoodViewModel(
                        repository = app.moodRepository
                    )
                }
                com.lifescore.app.presentation.ui.mood.MoodTrackerScreen(
                    viewModel = moodViewModel,
                    navController = navController
                )
            }
            composable(Screen.SleepSoundscapes.route) {
                val sleepViewModel = remember {
                    com.lifescore.app.presentation.ui.sleep.SleepViewModel(
                        lifeScoreRepository = app.lifeScoreRepository
                    )
                }
                com.lifescore.app.presentation.ui.sleep.SleepStoriesScreen(
                    viewModel = sleepViewModel,
                    navController = navController
                )
            }
            composable(Screen.ScreenTime.route) {
                val screenTimeViewModel = remember {
                    com.lifescore.app.presentation.ui.screentime.ScreenTimeViewModel(
                        repository = app.screenTimeRepository
                    )
                }
                com.lifescore.app.presentation.ui.screentime.ScreenTimeDashboardScreen(
                    viewModel = screenTimeViewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToMinimalist = { navController.navigate(Screen.MinimalistLauncher.route) },
                    onNavigateToFocusTimer = { navController.navigate(Screen.FocusTimer.route) }
                )
            }
            composable(Screen.MinimalistLauncher.route) {
                com.lifescore.app.presentation.ui.screentime.MinimalistLauncherScreen(
                    onBack = { navController.popBackStack() },
                    onLaunchApp = { appName ->
                        if (appName == "LifeScore") navController.navigate(Screen.Home.route)
                        else navController.popBackStack()
                    }
                )
            }
            composable(Screen.TaskBreakthrough.route) {
                com.lifescore.app.presentation.ui.wellness.TaskBreakthroughScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.ThoughtBreak.route) {
                val screenTimeViewModel = remember {
                    com.lifescore.app.presentation.ui.screentime.ScreenTimeViewModel(
                        repository = app.screenTimeRepository
                    )
                }
                com.lifescore.app.presentation.ui.wellness.ThoughtBreakScreen(
                    viewModel = screenTimeViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.BookFlashcards.route) {
                val bookLearningViewModel = remember {
                    com.lifescore.app.presentation.ui.books.BookLearningViewModel(
                        repository = app.bookLearningRepository
                    )
                }
                com.lifescore.app.presentation.ui.books.BookDiscoveryAndFlashcardsScreen(
                    viewModel = bookLearningViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.EnergySchedule.route) {
                val energyViewModel = remember {
                    com.lifescore.app.presentation.ui.energy.EnergyScheduleViewModel(
                        repository = app.energyScheduleRepository
                    )
                }
                com.lifescore.app.presentation.ui.energy.EnergyScheduleScreen(
                    viewModel = energyViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.VirtualPet.route) {
                val petViewModel = remember {
                    com.lifescore.app.presentation.ui.pet.VirtualPetViewModel(
                        repository = app.virtualPetRepository
                    )
                }
                com.lifescore.app.presentation.ui.pet.PetDashboardScreen(
                    viewModel = petViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.MeditationLibrary.route) {
                val meditationViewModel = remember {
                    com.lifescore.app.presentation.ui.meditation.MeditationViewModel(
                        repository = app.meditationLibraryRepository
                    )
                }
                com.lifescore.app.presentation.ui.meditation.MeditationLibraryScreen(
                    viewModel = meditationViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.PartySystem.route) {
                val partyViewModel = remember {
                    com.lifescore.app.presentation.ui.party.PartyViewModel(
                        repository = app.partySystemRepository
                    )
                }
                com.lifescore.app.presentation.ui.party.PartyDashboardScreen(
                    viewModel = partyViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.CoachMarketplace.route) {
                val coachViewModel = remember {
                    com.lifescore.app.presentation.ui.coach.CoachMarketplaceViewModel(
                        repository = app.coachMarketplaceRepository
                    )
                }
                com.lifescore.app.presentation.ui.coach.CoachMarketplaceScreen(
                    viewModel = coachViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.NeurodivergentHub.route) {
                val neuroViewModel = remember {
                    com.lifescore.app.presentation.ui.neuro.NeurodivergentViewModel()
                }
                com.lifescore.app.presentation.ui.neuro.NeurodivergentHubScreen(
                    viewModel = neuroViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.ScienceJourneys.route) {
                val journeyViewModel = remember {
                    com.lifescore.app.presentation.ui.journeys.ScienceJourneyViewModel(
                        repository = app.scienceJourneyRepository
                    )
                }
                com.lifescore.app.presentation.ui.journeys.ScienceJourneyScreen(
                    viewModel = journeyViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.ViralReferrals.route) {
                val viralViewModel = remember {
                    com.lifescore.app.presentation.ui.viral.ViralReferralViewModel(
                        repository = app.viralGrowthRepository
                    )
                }
                com.lifescore.app.presentation.ui.viral.ViralReferralScreen(
                    viewModel = viralViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.LeagueTiers.route) {
                val leagueViewModel = remember {
                    com.lifescore.app.presentation.ui.leagues.LeagueTiersViewModel(
                        repository = app.viralGrowthRepository
                    )
                }
                com.lifescore.app.presentation.ui.leagues.LeagueTiersScreen(
                    viewModel = leagueViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.StreakVault.route) {
                val streakViewModel = remember {
                    com.lifescore.app.presentation.ui.streaks.StreakVaultViewModel(
                        repository = app.viralGrowthRepository
                    )
                }
                com.lifescore.app.presentation.ui.streaks.StreakVaultScreen(
                    viewModel = streakViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.CustomRewards.route) {
                val rewardsViewModel = remember {
                    com.lifescore.app.presentation.ui.rewards.CustomRewardsViewModel(
                        repository = app.viralGrowthRepository
                    )
                }
                com.lifescore.app.presentation.ui.rewards.CustomRewardsScreen(
                    viewModel = rewardsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.FriendsFeed.route) {
                val feedViewModel = remember {
                    com.lifescore.app.presentation.ui.social.FriendsFeedViewModel(
                        repository = app.viralGrowthRepository
                    )
                }
                com.lifescore.app.presentation.ui.social.FriendsFeedScreen(
                    viewModel = feedViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.AiMemoryInspector.route) {
                val memoryViewModel = remember {
                    com.lifescore.app.presentation.ui.ai.AiMemoryInspectorViewModel(
                        repository = app.viralGrowthRepository
                    )
                }
                com.lifescore.app.presentation.ui.ai.AiMemoryInspectorScreen(
                    viewModel = memoryViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "tracker_detail/{trackerId}",
                arguments = listOf(androidx.navigation.navArgument("trackerId") { type = androidx.navigation.NavType.StringType })
            ) { backStackEntry ->
                val trackerId = backStackEntry.arguments?.getString("trackerId") ?: "steps"
                val trackerType = com.lifescore.app.core.trackers.TrackerType.values().find { it.id == trackerId }
                    ?: com.lifescore.app.core.trackers.TrackerType.STEPS

                val dedicatedViewModel = remember(trackerType) {
                    com.lifescore.app.presentation.ui.trackers.DedicatedTrackerViewModel(
                        trackerType = trackerType,
                        lifeTrackersRepository = app.lifeTrackersRepository,
                        lifeScoreRepository = app.lifeScoreRepository
                    )
                }

                com.lifescore.app.presentation.ui.trackers.DedicatedTrackerScreen(
                    viewModel = dedicatedViewModel,
                    navController = navController
                )
            }
        }

        if (showPaywall) {
            PaywallBottomSheet(
                billingRepository = app.billingRepository,
                onDismiss = { showPaywall = false }
            )
        }

        if (showAuthModal) {
            com.lifescore.app.presentation.ui.auth.AuthBottomSheet(
                authRepository = app.authRepository,
                onAuthSuccess = { showAuthModal = false },
                onDismiss = { showAuthModal = false }
            )
        }
    }
}
}


