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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.IconButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edutrack.app.model.StudySession
import com.edutrack.app.model.Subject
import com.edutrack.app.ui.theme.AccentBlue
import com.edutrack.app.ui.theme.AccentBlueLight
import com.edutrack.app.ui.theme.AccentMint
import com.edutrack.app.ui.theme.AccentOrange
import com.edutrack.app.ui.theme.AccentPurple
import com.edutrack.app.ui.theme.CoralRed
import com.edutrack.app.ui.theme.ProgressPurple
import com.edutrack.app.ui.theme.ProgressTrack
import com.edutrack.app.viewmodel.StudyTrackerViewModel
import java.util.Calendar
import java.util.Locale

data class DailyBarData(
    val dayLabel: String,
    val minutes: Int
)

@Composable
fun ProgressScreen(
    viewModel: StudyTrackerViewModel,
    onOpenDrawer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.subjects.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val totalStudyMinutes by viewModel.totalStudyMinutes.collectAsState()
    val weeklyStudyMinutes by viewModel.weeklyStudyMinutes.collectAsState()

    var isWeekView by remember { mutableStateOf(true) }

    // Total time in hours & minutes format e.g. "3h 30m"
    val totalHours = totalStudyMinutes / 60
    val totalMinutesRemainder = totalStudyMinutes % 60
    val totalTimeString = "${totalHours}h ${totalMinutesRemainder}m"

    // Task Completion Rate calculation
    val totalGoalHours = subjects.sumOf { it.weeklyGoalHours }.coerceAtLeast(1)
    val completedWeeklyHours = weeklyStudyMinutes / 60f
    val taskCompletionPercent = ((completedWeeklyHours / totalGoalHours) * 100).toInt().coerceIn(0, 100)

    // Daily breakdown for Sat, Sun, Mon, Tue, Wed, Thu, Fri (matching screenshot labels)
    val dailyBars = remember(sessions) {
        calculateDailyBars(sessions)
    }

    val maxDailyMinutes = (dailyBars.maxOfOrNull { it.minutes } ?: 0).coerceAtLeast(80)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Header: Nav Hamburger & Progress & Insights
            item(key = "header_title") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open Navigation Menu",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = "Progress & Insights",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 26.sp
                        )
                    )
                }
            }

            // 2. Dual Top Metric Cards: Task Completion & Total Study Time
            item(key = "top_metrics") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Left Metric: Task Completion
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
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Completion",
                                    tint = AccentMint,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Task Completion",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "$taskCompletionPercent%",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${sessions.size} sessions recorded",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    // Right Metric: Total Study Time
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
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = "Study Time",
                                    tint = AccentBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Total Study Time",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = totalTimeString,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "across all subjects",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }

            // 3. Study Activity Chart Card (with Week / Month Toggle Pill)
            item(key = "study_activity_card") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Header with Week / Month Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = "Activity",
                                    tint = AccentPurple,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Study Activity",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }

                            // Week / Month Pill Selector
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(3.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isWeekView) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
                                        .clickable { isWeekView = true }
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Week",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isWeekView) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (!isWeekView) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
                                        .clickable { isWeekView = false }
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Month",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (!isWeekView) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Bar Chart Display: Left Y-Axis labels (80m, 60m, 40m, 20m, 0m) + Rounded Bars
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            // Y-Axis
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(end = 12.dp),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.End
                            ) {
                                Text("80m", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                                Text("60m", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                                Text("40m", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                                Text("20m", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                                Text("0m", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                            }

                            // Bars for Sat Sun Mon Tue Wed Thu Fri
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                dailyBars.forEach { day ->
                                    val barFraction = if (maxDailyMinutes > 0) {
                                        (day.minutes.toFloat() / maxDailyMinutes).coerceIn(0.04f, 1f)
                                    } else 0.04f

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Bottom,
                                        modifier = Modifier.fillMaxHeight()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .width(22.dp)
                                                .fillMaxHeight(barFraction)
                                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                                .background(AccentPurple)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = day.dayLabel,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Subject Breakdown Card
            item(key = "subject_breakdown_card") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Breakdown",
                                tint = AccentOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Subject Breakdown",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        if (subjects.isEmpty()) {
                            Text(
                                text = "No subjects found.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                subjects.forEach { subject ->
                                    val subjectMinutes = sessions
                                        .filter { it.subjectId == subject.id }
                                        .sumOf { it.durationMinutes }
                                    val subHours = subjectMinutes / 60
                                    val subMin = subjectMinutes % 60
                                    val timeFormatted = "${subHours}h ${subMin}m"

                                    val goalMinutes = (subject.weeklyGoalHours * 60).coerceAtLeast(1)
                                    val progressFraction = (subjectMinutes.toFloat() / goalMinutes).coerceIn(0f, 1f)

                                    val subjectColor = runCatching {
                                        Color(android.graphics.Color.parseColor(subject.colorTag))
                                    }.getOrDefault(AccentBlue)

                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Subject Name in pill
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(subjectColor.copy(alpha = 0.15f))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = subject.name,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = subjectColor,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }

                                            Text(
                                                text = timeFormatted,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Horizontal bar
                                        LinearProgressIndicator(
                                            progress = { progressFraction },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = subjectColor,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                            strokeCap = StrokeCap.Round
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

private fun calculateDailyBars(sessions: List<StudySession>): List<DailyBarData> {
    val labels = listOf("Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri")
    val cal = Calendar.getInstance()
    
    // Calculate minutes for each day of current week
    val result = mutableListOf<DailyBarData>()
    for (i in 6 downTo 0) {
        val targetCal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -i)
        }
        val targetDayOfYear = targetCal.get(Calendar.DAY_OF_YEAR)
        val targetYear = targetCal.get(Calendar.YEAR)
        val shortName = targetCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH) ?: ""

        val dayMinutes = sessions.filter { session ->
            cal.timeInMillis = session.date
            cal.get(Calendar.DAY_OF_YEAR) == targetDayOfYear && cal.get(Calendar.YEAR) == targetYear
        }.sumOf { it.durationMinutes }

        result.add(DailyBarData(dayLabel = shortName, minutes = dayMinutes))
    }

    return if (result.size == 7) result else labels.map { DailyBarData(it, 0) }
}

data class DailyStudyHours(
    val dayLabel: String,
    val dateKey: String,
    val hours: Float,
    val isToday: Boolean
)

fun calculateStudyStreakDays(sessions: List<StudySession>): Int {
    if (sessions.isEmpty()) return 0
    val calendar = Calendar.getInstance()
    val sessionDays = sessions.map { session ->
        calendar.timeInMillis = session.date
        "${calendar.get(Calendar.YEAR)}_${calendar.get(Calendar.DAY_OF_YEAR)}"
    }.toSet()

    var streak = 0
    val checkCal = Calendar.getInstance()
    var key = "${checkCal.get(Calendar.YEAR)}_${checkCal.get(Calendar.DAY_OF_YEAR)}"
    if (!sessionDays.contains(key)) {
        checkCal.add(Calendar.DAY_OF_YEAR, -1)
        key = "${checkCal.get(Calendar.YEAR)}_${checkCal.get(Calendar.DAY_OF_YEAR)}"
        if (!sessionDays.contains(key)) return 0
    }
    while (sessionDays.contains(key)) {
        streak++
        checkCal.add(Calendar.DAY_OF_YEAR, -1)
        key = "${checkCal.get(Calendar.YEAR)}_${checkCal.get(Calendar.DAY_OF_YEAR)}"
    }
    return streak
}

fun calculateWeeklyDayHours(sessions: List<StudySession>): List<DailyStudyHours> {
    val labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val cal = Calendar.getInstance()
    val todayDayOfYear = cal.get(Calendar.DAY_OF_YEAR)
    val todayYear = cal.get(Calendar.YEAR)

    cal.firstDayOfWeek = Calendar.MONDAY
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

    val list = mutableListOf<DailyStudyHours>()
    for (i in 0 until 7) {
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
        val year = cal.get(Calendar.YEAR)
        val isToday = (dayOfYear == todayDayOfYear && year == todayYear)
        val dateKey = "${year}_$dayOfYear"

        val minutesForDay = sessions.filter { session ->
            val sCal = Calendar.getInstance().apply { timeInMillis = session.date }
            sCal.get(Calendar.DAY_OF_YEAR) == dayOfYear && sCal.get(Calendar.YEAR) == year
        }.sumOf { it.durationMinutes }

        list.add(
            DailyStudyHours(
                dayLabel = labels[i],
                dateKey = dateKey,
                hours = minutesForDay / 60f,
                isToday = isToday
            )
        )
        cal.add(Calendar.DAY_OF_YEAR, 1)
    }
    return list
}
