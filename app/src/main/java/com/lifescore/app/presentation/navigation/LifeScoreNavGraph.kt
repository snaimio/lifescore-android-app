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

@Composable
fun LifeScoreNavGraph(
    navController: NavHostController,
    app: LifeScoreApp
) {
    var showAuthModal by remember { mutableStateOf(false) }
    var showPaywall by remember { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarItems = listOf(
        Screen.Home,
        Screen.Dimensions,
        Screen.Tasks,
        Screen.AICoach,
        Screen.Leaderboard
    )

    val showBottomBar = bottomBarItems.any { it.route == currentRoute }

    // ViewModels
    val homeViewModel = remember {
        HomeViewModel(
            repository = app.lifeScoreRepository,
            firebaseRepository = app.firebaseRepository,
            authRepository = app.authRepository
        )
    }
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

    val startDestination = remember {
        if (app.authRepository.currentUser != null) Screen.Home.route else Screen.Login.route
    }

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
                                    text = screen.title,
                                    fontSize = 10.sp,
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
                    onOpenPaywall = { showPaywall = true }
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
            composable(Screen.Onboarding.route) {
                com.lifescore.app.presentation.ui.onboarding.OnboardingAssessmentScreen(
                    onCompleteOnboarding = { archetype, ratings ->
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
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
