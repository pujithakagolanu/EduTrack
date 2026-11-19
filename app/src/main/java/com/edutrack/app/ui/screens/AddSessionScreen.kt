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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.edutrack.app.ui.components.EduTrackPrimaryButton
import com.edutrack.app.ui.components.EduTrackTextField
import com.edutrack.app.ui.components.EduTrackTopBar
import com.edutrack.app.viewmodel.StudyTrackerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSessionScreen(
    viewModel: StudyTrackerViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.subjects.collectAsState()

    var selectedSubjectId by remember(subjects) {
        mutableIntStateOf(subjects.firstOrNull()?.id ?: 1)
    }
    var selectedSubjectName by remember(subjects) {
        mutableStateOf(subjects.firstOrNull()?.name ?: "Select Subject")
    }
    var durationMinutesStr by remember { mutableStateOf("60") }
    var notes by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            EduTrackTopBar(
                title = "Log Study Session",
                subtitle = "Track time and notes",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Subject Selection Dropdown
            item {
                Text(
                    text = "Select Subject",
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
                                    selectedSubjectName = subject.name
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
                    placeholder = "What topics, chapters or exercises did you complete?",
                    singleLine = false,
                    maxLines = 4
                )
            }

            // Save Action Button
            item {
                Spacer(modifier = Modifier.height(12.dp))
                EduTrackPrimaryButton(
                    text = "Save Study Session",
                    icon = Icons.Default.Check,
                    enabled = (durationMinutesStr.toIntOrNull() ?: 0) > 0,
                    onClick = {
                        val duration = durationMinutesStr.toIntOrNull() ?: 60
                        viewModel.addStudySession(
                            subjectId = selectedSubjectId,
                            durationMinutes = duration,
                            notes = notes,
                            date = System.currentTimeMillis()
                        )
                        onNavigateBack()
                    }
                )
            }
        }
    }
}
