package com.edutrack.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
) {
    // Bottom Navigation destinations matching design reference:
    // 1. Home
    // 2. Tasks (Subjects & Session management)
    // 3. Pomodoro (Live Study Focus Timer)
    // 4. Flashcards / History (Recorded study session history & flashcards)
    // 5. Progress (Progress & Insights Analytics)
    data object Home : Screen(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    data object Tasks : Screen(
        route = "tasks",
        title = "Tasks",
        selectedIcon = Icons.Filled.CheckCircleOutline,
        unselectedIcon = Icons.Outlined.CheckCircleOutline
    )

    data object Stopwatch : Screen(
        route = "stopwatch",
        title = "Timer",
        selectedIcon = Icons.Filled.Timer,
        unselectedIcon = Icons.Outlined.Timer
    )

    // 4. Planner / Study Schedule (Monthly calendar & study planner)
    data object Planner : Screen(
        route = "planner",
        title = "Planner",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth
    )

    // 5. Archive / History (Recorded study session history & archive)
    data object History : Screen(
        route = "history",
        title = "Archive",
        selectedIcon = Icons.Filled.Archive,
        unselectedIcon = Icons.Outlined.Archive
    )

    data object Progress : Screen(
        route = "progress",
        title = "Progress",
        selectedIcon = Icons.Filled.Assessment,
        unselectedIcon = Icons.Outlined.Assessment
    )

    // Sub-screens
    data object AddSession : Screen(
        route = "add_session",
        title = "Add Study Session"
    )

    data object AddPlan : Screen(
        route = "add_plan",
        title = "Plan Study Activity"
    )

    data object SessionDetail : Screen(
        route = "session_detail/{sessionId}",
        title = "Session Detail"
    ) {
        fun createRoute(sessionId: Int) = "session_detail/$sessionId"
    }

    data object EditSession : Screen(
        route = "edit_session/{sessionId}",
        title = "Edit Study Session"
    ) {
        fun createRoute(sessionId: Int) = "edit_session/$sessionId"
    }

    companion object {
        val bottomNavItems = listOf(Home, Tasks, Stopwatch, Planner, History, Progress)
    }
}
