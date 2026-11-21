package com.edutrack.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edutrack.app.model.StudySession
import com.edutrack.app.model.Subject
import com.edutrack.app.ui.theme.AccentBlue
import com.edutrack.app.ui.theme.AccentMint
import com.edutrack.app.ui.theme.AccentOrange
import com.edutrack.app.ui.theme.AccentPurple
import com.edutrack.app.ui.theme.ProgressPurple
import com.edutrack.app.ui.theme.ProgressTrack
import com.edutrack.app.viewmodel.StudyTrackerViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DailyBarData(
    val dayLabel: String,
    val minutes: Int
)

data class DailyChartPoint(
    val dayLabel: String,
    val dateLabel: String,
    val minutes: Int,
    val isToday: Boolean
)

data class SubjectShare(
    val subject: Subject,
    val totalMinutes: Int,
    val percentage: Float,
    val color: Color
)

@OptIn(ExperimentalLayoutApi::class)
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

    // Daily Line Graph Points: 7 days for Week view, 14 days for Month view
    val chartPoints = remember(sessions, isWeekView) {
        val daysCount = if (isWeekView) 7 else 14
        val cal = Calendar.getInstance()
        val todayYear = cal.get(Calendar.YEAR)
        val todayDoy = cal.get(Calendar.DAY_OF_YEAR)

        (daysCount - 1 downTo 0).map { offset ->
            val targetCal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -offset)
            }
            val targetYear = targetCal.get(Calendar.YEAR)
            val targetDoy = targetCal.get(Calendar.DAY_OF_YEAR)

            val shortDay = targetCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH) ?: ""
            val dayOfMonth = targetCal.get(Calendar.DAY_OF_MONTH)

            val dayMinutes = sessions.filter { session ->
                cal.timeInMillis = session.date
                cal.get(Calendar.DAY_OF_YEAR) == targetDoy && cal.get(Calendar.YEAR) == targetYear
            }.sumOf { it.durationMinutes }

            DailyChartPoint(
                dayLabel = shortDay,
                dateLabel = "$dayOfMonth",
                minutes = dayMinutes,
                isToday = (targetYear == todayYear && targetDoy == todayDoy)
            )
        }
    }

    // Subject Distribution breakdown: Calculate totals, percentage, and colors
    val subjectShares = remember(subjects, sessions) {
        val totalRecordedMinutes = sessions.sumOf { it.durationMinutes }.coerceAtLeast(0)
        val defaultColors = listOf(
            Color(0xFF3B82F6), // Blue
            Color(0xFF10B981), // Emerald
            Color(0xFFF59E0B), // Amber
            Color(0xFF8B5CF6), // Purple
            Color(0xFFEC4899), // Pink
            Color(0xFF06B6D4)  // Cyan
        )

        subjects.mapIndexed { index, subject ->
            val minutes = sessions.filter { it.subjectId == subject.id }.sumOf { it.durationMinutes }
            val pct = if (totalRecordedMinutes > 0) (minutes.toFloat() / totalRecordedMinutes) * 100f else 0f
            val color = runCatching {
                Color(android.graphics.Color.parseColor(subject.colorTag))
            }.getOrElse { defaultColors[index % defaultColors.size] }

            SubjectShare(
                subject = subject,
                totalMinutes = minutes,
                percentage = pct,
                color = color
            )
        }
    }

    val totalDistributionMinutes = subjectShares.sumOf { it.totalMinutes }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Header: Nav Hamburger & "Insights" Title
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

                    Column {
                        Text(
                            text = "Insights",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 26.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Study metrics & distribution overview",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
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
                                    tint = MaterialTheme.colorScheme.primary,
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

            // 3. Study Activity Line Graph with Dates & Connected Dots
            item(key = "study_activity_line_graph") {
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
                        // Header with Title & Week/Month Selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = "Activity",
                                    tint = MaterialTheme.colorScheme.primary,
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

                            // Week / Month Toggle Pill
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
                                            color = if (isWeekView) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
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
                                            color = if (!isWeekView) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Line Graph Canvas with Dates and Connected Dots
                        val maxMinutes = (chartPoints.maxOfOrNull { it.minutes } ?: 0).coerceAtLeast(60)
                        val lineColor = MaterialTheme.colorScheme.primary
                        val dotFillColor = MaterialTheme.colorScheme.surface
                        val gridLineColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                        val activeDotColor = AccentOrange

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val width = size.width
                                val height = size.height - 35.dp.toPx() // Reserve space at bottom for date labels
                                val pointSpacing = if (chartPoints.size > 1) width / (chartPoints.size - 1) else width

                                // Draw horizontal reference grid lines
                                val gridSteps = 3
                                for (i in 0..gridSteps) {
                                    val y = height * (i.toFloat() / gridSteps)
                                    drawLine(
                                        color = gridLineColor,
                                        start = Offset(0f, y),
                                        end = Offset(width, y),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                }

                                // Calculate coordinates for each point
                                val points = chartPoints.mapIndexed { index, point ->
                                    val x = index * pointSpacing
                                    val fraction = (point.minutes.toFloat() / maxMinutes).coerceIn(0f, 1f)
                                    val y = height - (fraction * (height - 15.dp.toPx()))
                                    Offset(x, y)
                                }

                                // Draw soft gradient fill under the curve
                                if (points.isNotEmpty()) {
                                    val fillPath = Path().apply {
                                        moveTo(points.first().x, height)
                                        points.forEach { lineTo(it.x, it.y) }
                                        lineTo(points.last().x, height)
                                        close()
                                    }
                                    drawPath(
                                        path = fillPath,
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                lineColor.copy(alpha = 0.28f),
                                                lineColor.copy(alpha = 0.02f)
                                            ),
                                            startY = 0f,
                                            endY = height
                                        )
                                    )
                                }

                                // Draw continuous connecting line
                                if (points.size > 1) {
                                    val strokePath = Path().apply {
                                        moveTo(points.first().x, points.first().y)
                                        for (i in 1 until points.size) {
                                            lineTo(points[i].x, points[i].y)
                                        }
                                    }
                                    drawPath(
                                        path = strokePath,
                                        color = lineColor,
                                        style = Stroke(
                                            width = 3.dp.toPx(),
                                            cap = StrokeCap.Round,
                                            join = StrokeJoin.Round
                                        )
                                    )
                                }

                                // Draw connected dots and outer halos
                                points.forEachIndexed { index, point ->
                                    val isPointToday = chartPoints[index].isToday
                                    val hasActivity = chartPoints[index].minutes > 0
                                    val currentDotColor = if (isPointToday) activeDotColor else lineColor

                                    // Outer ring
                                    drawCircle(
                                        color = currentDotColor,
                                        radius = if (isPointToday) 6.dp.toPx() else 4.5.dp.toPx(),
                                        center = point
                                    )
                                    // Inner contrasting center
                                    drawCircle(
                                        color = if (hasActivity) dotFillColor else currentDotColor,
                                        radius = if (isPointToday) 3.5.dp.toPx() else 2.5.dp.toPx(),
                                        center = point
                                    )
                                }
                            }

                            // Dates & Day Labels below the line graph
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                chartPoints.forEach { point ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.width(if (chartPoints.size > 7) 20.dp else 36.dp)
                                    ) {
                                        Text(
                                            text = point.dayLabel.take(1),
                                            fontSize = 10.sp,
                                            fontWeight = if (point.isToday) FontWeight.Bold else FontWeight.Medium,
                                            color = if (point.isToday) AccentOrange else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = point.dateLabel,
                                            fontSize = 11.sp,
                                            fontWeight = if (point.isToday) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (point.isToday) AccentOrange else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Subject Distribution: Color Ring / Pie Chart & Color Dots Legend with Percentages
            item(key = "subject_distribution_card") {
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
                        // Header
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PieChart,
                                contentDescription = "Distribution",
                                tint = AccentOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Subject Distribution",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        if (subjects.isEmpty()) {
                            Text(
                                text = "No subjects enrolled yet.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            // Circular Color Ring / Pie Chart
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(22.dp)
                            ) {
                                // Dynamic Pie / Color Ring Canvas
                                Box(
                                    modifier = Modifier.size(130.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.size(120.dp)) {
                                        val strokeWidth = 18.dp.toPx()

                                        if (totalDistributionMinutes <= 0) {
                                            // Empty state track ring
                                            drawArc(
                                                color = Color.LightGray.copy(alpha = 0.35f),
                                                startAngle = -90f,
                                                sweepAngle = 360f,
                                                useCenter = false,
                                                style = Stroke(width = strokeWidth)
                                            )
                                        } else {
                                            var currentStartAngle = -90f
                                            subjectShares.forEach { share ->
                                                val sweepAngle = (share.percentage / 100f) * 360f
                                                if (sweepAngle > 0f) {
                                                    drawArc(
                                                        color = share.color,
                                                        startAngle = currentStartAngle,
                                                        sweepAngle = sweepAngle,
                                                        useCenter = false,
                                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                                                    )
                                                    currentStartAngle += sweepAngle
                                                }
                                            }
                                        }
                                    }

                                    // Center Total Text inside the Ring
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = if (totalDistributionMinutes > 0) "${totalDistributionMinutes}m" else "0m",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                        Text(
                                            text = "Logged",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }

                                // Legend with Subject Name, Color Dot & Percentage
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    subjectShares.forEach { share ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                // Colored Indicator Dot
                                                Box(
                                                    modifier = Modifier
                                                        .size(10.dp)
                                                        .clip(CircleShape)
                                                        .background(share.color)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = share.subject.name,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = FontWeight.Medium,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    ),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }

                                            // Percentage Tag
                                            Text(
                                                text = String.format(Locale.getDefault(), "%.1f%%", share.percentage),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = share.color
                                                )
                                            )
                                        }
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
