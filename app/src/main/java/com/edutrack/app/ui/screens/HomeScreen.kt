package com.edutrack.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edutrack.app.ui.components.SubjectCard
import com.edutrack.app.ui.theme.AccentBlue
import com.edutrack.app.ui.theme.AccentBlueLight
import com.edutrack.app.ui.theme.AccentMint
import com.edutrack.app.ui.theme.AccentOrange
import com.edutrack.app.ui.theme.AccentPurple
import com.edutrack.app.ui.theme.BorderSubtle
import com.edutrack.app.ui.theme.ProgressPurple
import com.edutrack.app.ui.theme.ProgressTrack
import com.edutrack.app.ui.theme.SmartBg
import com.edutrack.app.ui.theme.SmartSurface
import com.edutrack.app.ui.theme.TextDark
import com.edutrack.app.ui.theme.TextLight
import com.edutrack.app.ui.theme.TextMuted
import com.edutrack.app.viewmodel.StudyTrackerViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: StudyTrackerViewModel,
    onNavigateToAddSession: () -> Unit,
    onNavigateToSubjects: () -> Unit,
    onNavigateToPlanner: () -> Unit = {},
    onNavigateToHistory: () -> Unit,
    onNavigateToSessionDetail: (Int) -> Unit,
    onOpenDrawer: () -> Unit = {},
    isDarkTheme: Boolean = false,
    onToggleDarkTheme: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.subjects.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val todayMinutes by viewModel.todayStudyMinutes.collectAsState()
    val weeklyMinutes by viewModel.weeklyStudyMinutes.collectAsState()

    // Formatted current date e.g. "Friday, June 27"
    val dateString = remember {
        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    }

    val totalWeeklyHours = weeklyMinutes / 60f
    val totalWeeklyGoalHours = subjects.sumOf { it.weeklyGoalHours }.coerceAtLeast(0)
    val overallProgressPercent = if (totalWeeklyGoalHours > 0) {
        ((totalWeeklyHours / totalWeeklyGoalHours) * 100).toInt().coerceIn(0, 100)
    } else 0
    val overallProgressFraction = (overallProgressPercent / 100f).coerceIn(0f, 1f)

    val currentStreak = remember(sessions) {
        calculateStudyStreakDays(sessions)
    }

    // Day-wise last 7 days history for fire streak
    val dayWiseStreakHistory = remember(sessions) {
        val cal = Calendar.getInstance()
        val sessionDays = sessions.map { session ->
            val sCal = Calendar.getInstance().apply { timeInMillis = session.date }
            "${sCal.get(Calendar.YEAR)}_${sCal.get(Calendar.DAY_OF_YEAR)}"
        }.toSet()

        // 7 days ending today
        (6 downTo 0).map { dayOffset ->
            val dayCal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -dayOffset)
            }
            val key = "${dayCal.get(Calendar.YEAR)}_${dayCal.get(Calendar.DAY_OF_YEAR)}"
            val dayName = dayCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) ?: ""
            val hasStudied = sessionDays.contains(key)
            val isToday = dayOffset == 0
            StreakDayStatus(dayLabel = dayName, hasStudied = hasStudied, isToday = isToday)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddSession,
                containerColor = AccentBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Study Session",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Header: App Title, Nav Hamburger & Current Date
            item(key = "header_title") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onOpenDrawer,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open Navigation Menu",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "EduTrack",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 26.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = dateString,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    // Light / Dark Mode Toggle Icon Button
                    IconButton(
                        onClick = onToggleDarkTheme,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
                            tint = if (isDarkTheme) AccentOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 2. Motivational Quote Card
            item(key = "quote_card") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFF1E2838) else AccentBlueLight
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AccentBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = "Quote",
                                tint = AccentBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Success is not final, failure is not fatal: It is the courage to continue that counts.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDarkTheme) Color(0xFFF1F4F9) else MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 20.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "— Winston Churchill",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isDarkTheme) Color(0xFF9CA3AF) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }

            // 3. Dual Metric Tiles: Today's Tasks & Study Time
            item(key = "metric_tiles") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Left Tile: Today's Tasks
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToSubjects() },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Today's Tasks",
                                    tint = AccentBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Today's\nTasks",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.SemiBold,
                                        lineHeight = 14.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "${subjects.size}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "tasks active",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    // Right Tile: Study Time
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = "Study Time",
                                    tint = AccentMint,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Study\nTime",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.SemiBold,
                                        lineHeight = 14.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "$todayMinutes",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "minutes today",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }

            // 4. Overall Progress Section
            item(key = "overall_progress") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Overall Progress",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Progress",
                                tint = ProgressPurple,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$overallProgressPercent %",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ProgressPurple
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { overallProgressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = ProgressPurple,
                        trackColor = ProgressTrack,
                        strokeCap = StrokeCap.Round
                    )
                }
            }

            // 5. Clean & Aesthetic Study Streak Card
            item(key = "streak_summary_card") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Header with Title & Fire Pill
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Study Streak",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (currentStreak > 0) "$currentStreak consecutive days active" else "Build your daily momentum",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AccentOrange.copy(alpha = 0.12f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = AccentOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$currentStreak Days",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AccentOrange
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Day-Wise 7-Day Flame Track (Minimalist, elegant)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            dayWiseStreakHistory.forEach { dayStatus ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    dayStatus.hasStudied -> AccentOrange.copy(alpha = 0.16f)
                                                    dayStatus.isToday -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                                                    else -> Color.Transparent
                                                }
                                            )
                                            .border(
                                                width = if (dayStatus.isToday) 1.5.dp else 0.dp,
                                                color = if (dayStatus.isToday) AccentOrange else Color.Transparent,
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (dayStatus.hasStudied) {
                                            Icon(
                                                imageVector = Icons.Default.LocalFireDepartment,
                                                contentDescription = "Active day",
                                                tint = AccentOrange,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(5.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f))
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = dayStatus.dayLabel,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = if (dayStatus.isToday) FontWeight.Bold else FontWeight.Medium,
                                            color = if (dayStatus.isToday) AccentOrange else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Clean Aesthetic Target Milestones (3-Day, 7-Day, 30-Day)
                        val milestones = listOf(
                            Pair("3-Day", 3),
                            Pair("7-Day", 7),
                            Pair("30-Day", 30)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            milestones.forEach { (label, target) ->
                                val isReached = currentStreak >= target
                                val fraction = (currentStreak.toFloat() / target).coerceIn(0f, 1f)
                                val milestoneAccent = when (target) {
                                    3 -> Color(0xFFF59E0B)
                                    7 -> Color(0xFF10B981)
                                    else -> Color(0xFF6366F1)
                                }

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            if (isReached) milestoneAccent.copy(alpha = 0.08f)
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                        )
                                        .padding(vertical = 10.dp, horizontal = 12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isReached) milestoneAccent else MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                        Text(
                                            text = if (isReached) "✓" else "$currentStreak/$target",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isReached) milestoneAccent else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    LinearProgressIndicator(
                                        progress = { fraction },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(3.dp)
                                            .clip(RoundedCornerShape(2.dp)),
                                        color = if (isReached) milestoneAccent else milestoneAccent.copy(alpha = 0.7f),
                                        trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                        strokeCap = StrokeCap.Round
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 6. Subject Progress Section
            item(key = "subject_progress_heading") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Subject Progress",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )

                    Text(
                        text = "View All (${subjects.size})",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = AccentBlue,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.clickable { onNavigateToSubjects() }
                    )
                }
            }

            // Real Subjects mapped from Room
            items(subjects, key = { "subject_${it.id}" }) { subject ->
                val subjectSessions = sessions.filter { it.subjectId == subject.id }
                val loggedMinutes = subjectSessions.sumOf { it.durationMinutes }
                SubjectCard(
                    subject = subject,
                    totalMinutesLogged = loggedMinutes,
                    sessionCount = subjectSessions.size,
                    onClick = onNavigateToSubjects
                )
            }

            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

data class StreakDayStatus(
    val dayLabel: String,
    val hasStudied: Boolean,
    val isToday: Boolean
)
