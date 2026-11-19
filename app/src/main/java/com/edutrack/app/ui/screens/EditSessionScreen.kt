package com.edutrack.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.edutrack.app.ui.components.EduTrackEmptyState
import com.edutrack.app.ui.components.EduTrackPrimaryButton
import com.edutrack.app.ui.components.EduTrackTextField
import com.edutrack.app.ui.components.EduTrackTopBar
import com.edutrack.app.viewmodel.StudyTrackerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSessionScreen(
    sessionId: Int,
    viewModel: StudyTrackerViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Observe real session and subjects from Room
    val sessions by viewModel.sessions.collectAsState()
    val subjects by viewModel.subjects.collectAsState()

    val session = sessions.find { it.id == sessionId }

    var selectedSubjectId by remember { mutableIntStateOf(session?.subjectId ?: 1) }
    var durationMinutesStr by remember { mutableStateOf(session?.durationMinutes?.toString() ?: "60") }
    var notes by remember { mutableStateOf(session?.notes ?: "") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // Sync state when session record is loaded from Room
    LaunchedEffect(session) {
        session?.let {
            selectedSubjectId = it.subjectId
            durationMinutesStr = it.durationMinutes.toString()
            notes = it.notes
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            EduTrackTopBar(
                title = "Edit Study Session",
                subtitle = "Update details in Room",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        if (session == null) {
            EduTrackEmptyState(
                title = "Session Unavailable",
                description = "Could not locate this study session to edit.",
                actionText = "Go Back",
                onActionClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            val selectedSubjectName = subjects.find { it.id == selectedSubjectId }?.name
                ?: "Subject #$selectedSubjectId"

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Subject Dropdown
                item {
                    Text(
                        text = "Subject",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = isDropdownExpanded,
                        onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedSubjectName,
                            onValueChange = {},
                            readOnly = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Book,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            subjects.forEach { subject ->
                                DropdownMenuItem(
                                    text = { Text(subject.name) },
                                    onClick = {
                                        selectedSubjectId = subject.id
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Duration Input
                item {
                    EduTrackTextField(
                        value = durationMinutesStr,
                        onValueChange = { durationMinutesStr = it },
                        label = "Duration (Minutes)",
                        placeholder = "e.g. 60",
                        leadingIcon = Icons.Default.AccessTime,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                // Notes Input
                item {
                    EduTrackTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = "Session Notes",
                        placeholder = "Update session takeaways, exercises, or questions...",
                        singleLine = false,
                        maxLines = 5
                    )
                }

                // Save Changes Button
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    EduTrackPrimaryButton(
                        text = "Save Changes",
                        icon = Icons.Default.Check,
                        enabled = (durationMinutesStr.toIntOrNull() ?: 0) > 0,
                        onClick = {
                            val duration = durationMinutesStr.toIntOrNull() ?: session.durationMinutes
                            viewModel.updateStudySession(
                                session.copy(
                                    subjectId = selectedSubjectId,
                                    durationMinutes = duration,
                                    notes = notes
                                )
                            )
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Session updated successfully")
                                delay(300)
                                onNavigateBack()
                            }
                        }
                    )
                }
            }
        }
    }
}
