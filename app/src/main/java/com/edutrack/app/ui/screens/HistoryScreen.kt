package com.edutrack.app.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.edutrack.app.ui.components.EduTrackEmptyState
import com.edutrack.app.ui.components.EduTrackTopBar
import com.edutrack.app.ui.components.StudySessionCard
import com.edutrack.app.viewmodel.StudyTrackerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: StudyTrackerViewModel,
    onNavigateToSessionDetail: (Int) -> Unit,
    onNavigateToAddSession: () -> Unit,
    onOpenDrawer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val sessions by viewModel.sessions.collectAsState()
    val subjects by viewModel.subjects.collectAsState()

    // 1. Filtering State: Subject Filter (null = all)
    var selectedSubjectId by remember { mutableStateOf<Int?>(null) }

    // 2. Filtering State: Date Range Filter (null = all)
    var startDateEpochMs by remember { mutableStateOf<Long?>(null) }
    var endDateEpochMs by remember { mutableStateOf<Long?>(null) }
    var showDateRangePicker by remember { mutableStateOf(false) }

    val dateRangePickerState = rememberDateRangePickerState()

    // 3. Compute filtered sessions list
    val filteredSessions = remember(sessions, selectedSubjectId, startDateEpochMs, endDateEpochMs) {
        sessions.filter { session ->
            val matchesSubject = selectedSubjectId == null || session.subjectId == selectedSubjectId
            val matchesStartDate = startDateEpochMs == null || session.date >= startDateEpochMs!!
            val matchesEndDate = endDateEpochMs == null || session.date <= (endDateEpochMs!! + 86400000L - 1)
            matchesSubject && matchesStartDate && matchesEndDate
        }
    }

    val isFilterActive = selectedSubjectId != null || startDateEpochMs != null

    Scaffold(
        topBar = {
            EduTrackTopBar(
                title = "Session History",
                subtitle = "${filteredSessions.size} of ${sessions.size} sessions",
                actions = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open Drawer"
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ==========================================
            // Filter Bar (Subject Chips + Date Range + Clear)
            // ==========================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Filters",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (isFilterActive) {
                            TextButton(
                                onClick = {
                                    selectedSubjectId = null
                                    startDateEpochMs = null
                                    endDateEpochMs = null
                                    dateRangePickerState.setSelection(null, null)
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear Filters",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Clear", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Horizontal Scrollable Subject Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // All Subjects chip
                        FilterChip(
                            selected = selectedSubjectId == null,
                            onClick = { selectedSubjectId = null },
                            label = { Text("All Subjects") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        // Per-Subject chips
                        subjects.forEach { subject ->
                            FilterChip(
                                selected = selectedSubjectId == subject.id,
                                onClick = {
                                    selectedSubjectId = if (selectedSubjectId == subject.id) null else subject.id
                                },
                                label = { Text(subject.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }

                        // Date Range Filter Chip
                        val dateRangeLabel = if (startDateEpochMs != null && endDateEpochMs != null) {
                            val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
                            "${formatter.format(Date(startDateEpochMs!!))} - ${formatter.format(Date(endDateEpochMs!!))}"
                        } else {
                            "Date Range"
                        }

                        FilterChip(
                            selected = startDateEpochMs != null,
                            onClick = { showDateRangePicker = true },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = { Text(dateRangeLabel) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }

            // ==========================================
            // Scrollable LazyColumn of Filtered Sessions
            // ==========================================
            if (filteredSessions.isEmpty()) {
                EduTrackEmptyState(
                    title = if (isFilterActive) "No Matching Sessions" else "No Study History",
                    description = if (isFilterActive) {
                        "No sessions match the selected filter criteria. Try clearing filters."
                    } else {
                        "All completed study intervals will appear here in chronological order."
                    },
                    icon = Icons.Default.History,
                    actionText = if (isFilterActive) "Clear Filters" else "Log First Session",
                    onActionClick = {
                        if (isFilterActive) {
                            selectedSubjectId = null
                            startDateEpochMs = null
                            endDateEpochMs = null
                        } else {
                            onNavigateToAddSession()
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    items(filteredSessions, key = { it.id }) { session ->
                        val subject = subjects.find { it.id == session.subjectId }
                        val subjectName = subject?.name ?: "Subject #${session.subjectId}"
                        val subjectColorTag = subject?.colorTag ?: "#6750A4"

                        StudySessionCard(
                            session = session,
                            subjectName = subjectName,
                            subjectColorTag = subjectColorTag,
                            onClick = { onNavigateToSessionDetail(session.id) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    // Date Range Picker Modal
    if (showDateRangePicker) {
        DatePickerDialog(
            onDismissRequest = { showDateRangePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        startDateEpochMs = dateRangePickerState.selectedStartDateMillis
                        endDateEpochMs = dateRangePickerState.selectedEndDateMillis
                        showDateRangePicker = false
                    }
                ) {
                    Text("Apply Range")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        startDateEpochMs = null
                        endDateEpochMs = null
                        showDateRangePicker = false
                    }
                ) {
                    Text("Clear")
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                title = {
                    Text(
                        text = "Select Date Range",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
