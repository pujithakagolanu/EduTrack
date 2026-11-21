package com.edutrack.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edutrack.app.ui.theme.AccentMint
import com.edutrack.app.ui.theme.AccentOrange
import com.edutrack.app.ui.theme.CoralRed
import com.edutrack.app.viewmodel.StudyTrackerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class LapRecord(
    val lapNumber: Int,
    val timeFormatted: String
)

enum class TimerMode {
    FOCUS,
    BREAK
}

@Composable
fun StopwatchScreen(
    viewModel: StudyTrackerViewModel,
    onOpenDrawer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.subjects.collectAsState()
    var selectedSubjectId by remember { mutableStateOf<Int?>(null) }
    var timerMode by remember { mutableStateOf(TimerMode.FOCUS) }

    LaunchedEffect(subjects) {
        if (selectedSubjectId == null && subjects.isNotEmpty()) {
            selectedSubjectId = subjects.first().id
        }
    }

    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    val laps = remember { mutableStateListOf<LapRecord>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Timer counter: increments in Focus mode, or functions as break stopwatch
    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            elapsedSeconds++
        }
    }

    val hours = elapsedSeconds / 3600
    val minutes = (elapsedSeconds % 3600) / 60
    val seconds = elapsedSeconds % 60
    val formattedTime = if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Nav Hamburger & Title
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
                        text = "Timer",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                // Status pill (Running vs Idle)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isRunning) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isRunning) {
                            if (timerMode == TimerMode.FOCUS) "Focusing" else "On Break"
                        } else "Ready",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isRunning) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Focus & Start Break Mode Toggle Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Focus Mode Button
                val isFocusActive = timerMode == TimerMode.FOCUS
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isFocusActive) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .then(
                            if (isFocusActive) Modifier.shadow(2.dp, RoundedCornerShape(16.dp))
                            else Modifier
                        )
                        .clickable {
                            if (timerMode != TimerMode.FOCUS) {
                                timerMode = TimerMode.FOCUS
                                isRunning = false
                                elapsedSeconds = 0
                                laps.clear()
                            }
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = "Focus",
                            tint = if (isFocusActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Focus",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isFocusActive) FontWeight.Bold else FontWeight.Medium,
                                color = if (isFocusActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                // Start Break Button
                val isBreakActive = timerMode == TimerMode.BREAK
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isBreakActive) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .then(
                            if (isBreakActive) Modifier.shadow(2.dp, RoundedCornerShape(16.dp))
                            else Modifier
                        )
                        .clickable {
                            if (timerMode != TimerMode.BREAK) {
                                timerMode = TimerMode.BREAK
                                isRunning = true // automatically starts the break
                                elapsedSeconds = 0
                                laps.clear()
                            }
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Coffee,
                            contentDescription = "Break",
                            tint = if (isBreakActive) AccentMint else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Start Break",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isBreakActive) FontWeight.Bold else FontWeight.Medium,
                                color = if (isBreakActive) AccentMint else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Large Circular Timer Display (Adaptive Surface)
            val ringBorderColor = if (timerMode == TimerMode.FOCUS) {
                MaterialTheme.colorScheme.outlineVariant
            } else {
                AccentMint.copy(alpha = 0.45f)
            }

            Box(
                modifier = Modifier
                    .size(280.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        spotColor = Color(0x22000000),
                        ambientColor = Color(0x11000000)
                    )
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(2.dp, ringBorderColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formattedTime,
                        fontSize = if (hours > 0) 44.sp else 56.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-1).sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (timerMode == TimerMode.FOCUS) "FOCUS TIMER" else "BREAK TIMER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (timerMode == TimerMode.FOCUS) MaterialTheme.colorScheme.onSurfaceVariant else AccentMint,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Controls: Reset/Save | Play/Pause | Lap
            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reset & Save to Room session
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            if (elapsedSeconds > 0 && timerMode == TimerMode.FOCUS) {
                                val currentSubject = subjects.find { it.id == selectedSubjectId } ?: subjects.firstOrNull()
                                if (currentSubject != null) {
                                    val sessionMinutes = (elapsedSeconds / 60).coerceAtLeast(1)
                                    viewModel.addStudySession(
                                        subjectId = currentSubject.id,
                                        durationMinutes = sessionMinutes,
                                        notes = "Focus timer session: ${currentSubject.name} (${formattedTime})",
                                        date = System.currentTimeMillis()
                                    )
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Saved $sessionMinutes min to ${currentSubject.name}!")
                                    }
                                }
                            } else if (elapsedSeconds > 0 && timerMode == TimerMode.BREAK) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Break finished: ${formattedTime}")
                                }
                            }
                            isRunning = false
                            elapsedSeconds = 0
                            laps.clear()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop and Save",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Play / Pause Button
                val playButtonBg = if (timerMode == TimerMode.FOCUS) {
                    MaterialTheme.colorScheme.tertiaryContainer
                } else {
                    AccentMint.copy(alpha = 0.18f)
                }
                val playButtonTint = if (timerMode == TimerMode.FOCUS) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    AccentMint
                }

                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(playButtonBg)
                        .border(1.5.dp, playButtonTint.copy(alpha = 0.35f), CircleShape)
                        .clickable { isRunning = !isRunning },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start",
                        tint = playButtonTint,
                        modifier = Modifier.size(34.dp)
                    )
                }

                // Lap Button
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            if (isRunning && elapsedSeconds > 0) {
                                laps.add(0, LapRecord(lapNumber = laps.size + 1, timeFormatted = formattedTime))
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = "Lap",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Current Study Subject Selector (Relevant during Focus)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = if (timerMode == TimerMode.FOCUS) "Current Study Subject" else "Study Subject (Focus Target)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (subjects.isEmpty()) {
                        Text(
                            text = "No subjects found. Create subjects in Tasks tab.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            subjects.forEach { subject ->
                                val isSelected = subject.id == selectedSubjectId
                                val parsedColor = runCatching {
                                    Color(android.graphics.Color.parseColor(subject.colorTag))
                                }.getOrDefault(CoralRed)

                                val chipBg = if (isSelected) parsedColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                                val borderModifier = if (isSelected) {
                                    Modifier.border(1.5.dp, parsedColor, RoundedCornerShape(16.dp))
                                } else {
                                    Modifier
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .then(borderModifier)
                                        .background(chipBg)
                                        .clickable { selectedSubjectId = subject.id }
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = subject.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) parsedColor else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Recorded Laps Section (if any recorded)
            if (laps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "Laps & Splits",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        laps.forEach { lap ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Lap ${lap.lapNumber}",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = lap.timeFormatted,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
