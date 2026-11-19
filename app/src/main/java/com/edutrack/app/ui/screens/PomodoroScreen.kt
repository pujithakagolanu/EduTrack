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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
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
import com.edutrack.app.model.Subject
import com.edutrack.app.ui.theme.AccentMint
import com.edutrack.app.ui.theme.AccentMintLight
import com.edutrack.app.ui.theme.BorderSubtle
import com.edutrack.app.ui.theme.ChipInactiveBg
import com.edutrack.app.ui.theme.ChipInactiveText
import com.edutrack.app.ui.theme.CoralRed
import com.edutrack.app.ui.theme.SmartBg
import com.edutrack.app.ui.theme.SmartSurface
import com.edutrack.app.ui.theme.TextDark
import com.edutrack.app.ui.theme.TextMuted
import com.edutrack.app.viewmodel.StudyTrackerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class PomodoroMode(val title: String, val minutes: Int) {
    FOCUS("Focus", 25),
    SHORT_BREAK("Short Break", 5),
    LONG_BREAK("Long Break", 15)
}

@Composable
fun PomodoroScreen(
    viewModel: StudyTrackerViewModel,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.subjects.collectAsState()
    var selectedMode by remember { mutableStateOf(PomodoroMode.FOCUS) }
    var selectedSubjectId by remember { mutableStateOf<Int?>(null) }

    // Initialize selected subject if available
    LaunchedEffect(subjects) {
        if (selectedSubjectId == null && subjects.isNotEmpty()) {
            selectedSubjectId = subjects.first().id
        }
    }

    var timeLeftSeconds by remember { mutableIntStateOf(selectedMode.minutes * 60) }
    var isRunning by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Countdown effect
    LaunchedEffect(isRunning, timeLeftSeconds) {
        if (isRunning && timeLeftSeconds > 0) {
            delay(1000L)
            timeLeftSeconds--
        } else if (isRunning && timeLeftSeconds == 0) {
            isRunning = false
            // If focus session completed, record to Room
            val currentSubject = subjects.find { it.id == selectedSubjectId } ?: subjects.firstOrNull()
            if (selectedMode == PomodoroMode.FOCUS && currentSubject != null) {
                viewModel.addStudySession(
                    subjectId = currentSubject.id,
                    durationMinutes = selectedMode.minutes,
                    notes = "Pomodoro Focus session completed: ${currentSubject.name}",
                    date = System.currentTimeMillis()
                )
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Focus session recorded to your study progress!")
                }
            }
        }
    }

    val minutes = timeLeftSeconds / 60
    val seconds = timeLeftSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Box(modifier = modifier.fillMaxSize().background(SmartBg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Title & Settings gear icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pomodoro Timer",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                )

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SmartSurface)
                        .border(1.dp, BorderSubtle, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Pill Selector: Focus | Short Break | Long Break
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PomodoroMode.entries.forEach { mode ->
                    val isSelected = selectedMode == mode
                    val containerColor = if (isSelected) CoralRed else ChipInactiveBg
                    val contentColor = if (isSelected) Color.White else ChipInactiveText

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(containerColor)
                            .clickable {
                                selectedMode = mode
                                isRunning = false
                                timeLeftSeconds = mode.minutes * 60
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = contentColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Soft White Circular Clock Display (Neumorphic circular card)
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = CircleShape,
                        spotColor = Color(0x18000000),
                        ambientColor = Color(0x0C000000)
                    )
                    .clip(CircleShape)
                    .background(SmartSurface)
                    .border(2.dp, BorderSubtle, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formattedTime,
                    fontSize = 58.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    letterSpacing = (-1).sp
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Play / Pause & Stop Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stop / Reset Button
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(ChipInactiveBg)
                        .clickable {
                            isRunning = false
                            timeLeftSeconds = selectedMode.minutes * 60
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Reset",
                        tint = TextDark,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Play / Pause Button (Mint Green round button)
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(AccentMintLight)
                        .border(1.dp, AccentMint.copy(alpha = 0.2f), CircleShape)
                        .clickable { isRunning = !isRunning },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start",
                        tint = AccentMint,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(44.dp))

            // "Current Study Subject" Card Container
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SmartSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Current Study Subject",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (subjects.isEmpty()) {
                        Text(
                            text = "No subjects found. Create subjects in Tasks tab.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
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

                                val chipBg = if (isSelected) parsedColor.copy(alpha = 0.12f) else ChipInactiveBg
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
                                            color = if (isSelected) parsedColor else ChipInactiveText
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
