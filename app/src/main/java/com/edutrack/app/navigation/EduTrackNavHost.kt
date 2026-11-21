package com.edutrack.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.edutrack.app.EduTrackApplication
import com.edutrack.app.ui.components.EduTrackBottomBar
import com.edutrack.app.ui.screens.AddSessionScreen
import com.edutrack.app.ui.screens.EditSessionScreen
import com.edutrack.app.ui.screens.HistoryScreen
import com.edutrack.app.ui.screens.HomeScreen
import com.edutrack.app.ui.screens.PlannerScreen
import com.edutrack.app.ui.screens.ProgressScreen
import com.edutrack.app.ui.screens.SessionDetailScreen
import com.edutrack.app.ui.screens.StopwatchScreen
import com.edutrack.app.ui.screens.SubjectsScreen
import com.edutrack.app.ui.theme.AccentBlue
import com.edutrack.app.ui.theme.AccentBlueLight
import com.edutrack.app.ui.theme.SmartBg
import com.edutrack.app.ui.theme.SmartSurface
import com.edutrack.app.ui.theme.TextDark
import com.edutrack.app.ui.theme.TextMuted
import com.edutrack.app.viewmodel.StudyPlanViewModel
import com.edutrack.app.viewmodel.StudyPlanViewModelFactory
import com.edutrack.app.viewmodel.StudyTrackerViewModel
import com.edutrack.app.viewmodel.SubjectViewModel
import com.edutrack.app.viewmodel.SubjectViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun EduTrackAppContent(
    navController: NavHostController,
    studyTrackerViewModel: StudyTrackerViewModel,
    application: EduTrackApplication,
    isDarkTheme: Boolean = false,
    onToggleDarkTheme: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Dedicated SubjectViewModel with factory for Subject Manager
    val subjectViewModel: SubjectViewModel = viewModel(
        factory = SubjectViewModelFactory(
            subjectRepository = application.subjectRepository,
            sessionRepository = application.studySessionRepository
        )
    )

    // Dedicated StudyPlanViewModel with factory for Study Planner
    val studyPlanViewModel: StudyPlanViewModel = viewModel(
        factory = StudyPlanViewModelFactory(
            planRepository = application.studyPlanRepository,
            subjectRepository = application.subjectRepository
        )
    )

    // Show bottom bar only on root top-level destinations
    val showBottomBar = currentRoute in Screen.bottomNavItems.map { it.route }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showBottomBar,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                modifier = Modifier.width(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    // Drawer Header with App Logo and Title
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "Logo",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "EduTrack",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "Study Progress Tracker",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "MAIN NAVIGATION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                    )

                    // Navigation items for drawer
                    Screen.bottomNavItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationDrawerItem(
                            label = {
                                Text(
                                    text = screen.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            icon = {
                                val icon = if (isSelected) screen.selectedIcon else screen.unselectedIcon
                                if (icon != null) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = screen.title
                                    )
                                }
                            },
                            selected = isSelected,
                            onClick = {
                                coroutineScope.launch { drawerState.close() }
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Home.route) {
                                            saveState = (screen.route != Screen.Home.route)
                                            inclusive = (screen.route == Screen.Home.route)
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.secondary,
                                selectedTextColor = MaterialTheme.colorScheme.secondary,
                                unselectedContainerColor = Color.Transparent,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "QUICK ACTIONS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMuted,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                    )

                    NavigationDrawerItem(
                        label = { Text("Log New Study Session") },
                        icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            navController.navigate(Screen.AddSession.route)
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    NavigationDrawerItem(
                        label = { Text("Plan Study Activity") },
                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Plan") },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            navController.navigate(Screen.Planner.route)
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "THEME",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                    )

                    NavigationDrawerItem(
                        label = { Text(if (isDarkTheme) "Light Mode" else "Dark Mode") },
                        icon = {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Theme"
                            )
                        },
                        selected = false,
                        onClick = {
                            onToggleDarkTheme()
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    EduTrackBottomBar(
                        currentRoute = currentRoute,
                        onNavigateToRoute = { targetRoute ->
                            if (targetRoute != currentRoute) {
                                navController.navigate(targetRoute) {
                                    popUpTo(Screen.Home.route) {
                                        saveState = (targetRoute != Screen.Home.route)
                                        inclusive = (targetRoute == Screen.Home.route)
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            },
            modifier = modifier
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding),
                enterTransition = {
                    fadeIn(animationSpec = tween(280))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(240))
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(280))
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(240))
                }
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = studyTrackerViewModel,
                        onNavigateToAddSession = { navController.navigate(Screen.AddSession.route) },
                        onNavigateToSubjects = { navController.navigate(Screen.Tasks.route) },
                        onNavigateToPlanner = { navController.navigate(Screen.Planner.route) },
                        onNavigateToHistory = { navController.navigate(Screen.History.route) },
                        onNavigateToSessionDetail = { sessionId ->
                            navController.navigate(Screen.SessionDetail.createRoute(sessionId))
                        },
                        onOpenDrawer = {
                            coroutineScope.launch { drawerState.open() }
                        },
                        isDarkTheme = isDarkTheme,
                        onToggleDarkTheme = onToggleDarkTheme
                    )
                }

                composable(Screen.Tasks.route) {
                    SubjectsScreen(
                        viewModel = subjectViewModel,
                        onOpenDrawer = {
                            coroutineScope.launch { drawerState.open() }
                        }
                    )
                }

                composable(Screen.Stopwatch.route) {
                    StopwatchScreen(
                        viewModel = studyTrackerViewModel,
                        onOpenDrawer = {
                            coroutineScope.launch { drawerState.open() }
                        }
                    )
                }

                composable(Screen.Planner.route) {
                    PlannerScreen(
                        viewModel = studyPlanViewModel,
                        onOpenDrawer = {
                            coroutineScope.launch { drawerState.open() }
                        }
                    )
                }

                composable(Screen.History.route) {
                    HistoryScreen(
                        viewModel = studyTrackerViewModel,
                        onNavigateToSessionDetail = { sessionId ->
                            navController.navigate(Screen.SessionDetail.createRoute(sessionId))
                        },
                        onNavigateToAddSession = { navController.navigate(Screen.AddSession.route) },
                        onOpenDrawer = {
                            coroutineScope.launch { drawerState.open() }
                        }
                    )
                }

                composable(Screen.Progress.route) {
                    ProgressScreen(
                        viewModel = studyTrackerViewModel,
                        onOpenDrawer = {
                            coroutineScope.launch { drawerState.open() }
                        }
                    )
                }

                composable(
                    route = Screen.AddSession.route,
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Up,
                            animationSpec = tween(320)
                        ) + fadeIn(animationSpec = tween(320))
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(280)
                        ) + fadeOut(animationSpec = tween(280))
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(280)
                        ) + fadeOut(animationSpec = tween(280))
                    }
                ) {
                    AddSessionScreen(
                        viewModel = studyTrackerViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Screen.SessionDetail.route,
                    arguments = listOf(navArgument("sessionId") { type = NavType.IntType }),
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(300))
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(280)
                        ) + fadeOut(animationSpec = tween(280))
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(300))
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(280)
                        ) + fadeOut(animationSpec = tween(280))
                    }
                ) { backStackEntry ->
                    val sessionId = backStackEntry.arguments?.getInt("sessionId") ?: 0
                    SessionDetailScreen(
                        sessionId = sessionId,
                        viewModel = studyTrackerViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToEditSession = { id ->
                            navController.navigate(Screen.EditSession.createRoute(id))
                        }
                    )
                }

                composable(
                    route = Screen.EditSession.route,
                    arguments = listOf(navArgument("sessionId") { type = NavType.IntType }),
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(300))
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(280)
                        ) + fadeOut(animationSpec = tween(280))
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(280)
                        ) + fadeOut(animationSpec = tween(280))
                    }
                ) { backStackEntry ->
                    val sessionId = backStackEntry.arguments?.getInt("sessionId") ?: 0
                    EditSessionScreen(
                        sessionId = sessionId,
                        viewModel = studyTrackerViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
