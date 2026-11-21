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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.edutrack.app.model.Subject
import com.edutrack.app.ui.components.EduTrackEmptyState
import com.edutrack.app.ui.components.EduTrackTextField
import com.edutrack.app.ui.components.EduTrackTopBar
import com.edutrack.app.viewmodel.SubjectValidationResult
import com.edutrack.app.viewmodel.SubjectViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

// Pre-curated student productivity color palette tags
val PresetSubjectColors = listOf(
    "#6750A4", // Lavender (Primary)
    "#3B82F6", // Royal Blue
    "#2E7D5B", // Forest Mint
    "#D97706", // Amber Bronze
    "#EC4899", // Berry Pink
    "#8B5CF6", // Violet
    "#06B6D4", // Cyan
    "#EF4444"  // Coral Red
)

@Composable
fun SubjectsScreen(
    viewModel: SubjectViewModel,
    onOpenDrawer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.subjects.collectAsState()
    val sessions by viewModel.sessions.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Dialog States
    var showAddDialog by remember { mutableStateOf(false) }
    var subjectToEdit by remember { mutableStateOf<Subject?>(null) }
    var subjectToDelete by remember { mutableStateOf<Subject?>(null) }

    // Listen for Snackbar notifications from ViewModel events
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            EduTrackTopBar(
                title = "Subject Manager",
                subtitle = "${subjects.size} study subjects",
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Subject")
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        if (subjects.isEmpty()) {
            EduTrackEmptyState(
                title = "No Subjects Found",
                description = "Create your study subjects to set weekly goals and organize session logs.",
                icon = Icons.Default.Book,
                actionText = "Add First Subject",
                onActionClick = { showAddDialog = true },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Text(
                        text = "Enrolled Study Subjects",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                items(subjects, key = { it.id }) { subject ->
                    val subjectSessions = sessions.filter { it.subjectId == subject.id }
                    val totalMinutesLogged = subjectSessions.sumOf { it.durationMinutes }
                    val totalHoursLogged = totalMinutesLogged / 60f

                    SubjectManagerCard(
                        subject = subject,
                        totalHoursLogged = totalHoursLogged,
                        sessionCount = subjectSessions.size,
                        onEditClick = { subjectToEdit = subject },
                        onDeleteClick = { subjectToDelete = subject }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(72.dp)) // Clearance for FAB
                }
            }
        }
    }

    // ==========================================
    // ADD SUBJECT DIALOG
    // ==========================================
    if (showAddDialog) {
        SubjectFormDialog(
            title = "Add Subject",
            confirmButtonText = "Save Subject",
            initialName = "",
            initialColorTag = PresetSubjectColors[0],
            initialWeeklyGoalHours = "10",
            onDismiss = { showAddDialog = false },
            onValidate = { name, goalStr -> viewModel.validateSubject(name, goalStr) },
            onConfirm = { name, colorTag, goalHours ->
                viewModel.addSubject(name, colorTag, goalHours) {
                    showAddDialog = false
                }
            }
        )
    }

    // ==========================================
    // EDIT SUBJECT DIALOG
    // ==========================================
    if (subjectToEdit != null) {
        val target = subjectToEdit!!
        SubjectFormDialog(
            title = "Edit Subject",
            confirmButtonText = "Save Changes",
            initialName = target.name,
            initialColorTag = target.colorTag,
            initialWeeklyGoalHours = target.weeklyGoalHours.toString(),
            onDismiss = { subjectToEdit = null },
            onValidate = { name, goalStr -> viewModel.validateSubject(name, goalStr, excludeSubjectId = target.id) },
            onConfirm = { name, colorTag, goalHours ->
                viewModel.updateSubject(
                    target.copy(
                        name = name,
                        colorTag = colorTag,
                        weeklyGoalHours = goalHours
                    )
                ) {
                    subjectToEdit = null
                }
            }
        )
    }

    // ==========================================
    // DELETE CONFIRMATION DIALOG
    // ==========================================
    if (subjectToDelete != null) {
        val target = subjectToDelete!!
        val associatedSessionsCount = sessions.count { it.subjectId == target.id }

        AlertDialog(
            onDismissRequest = { subjectToDelete = null },
            title = {
                Text(
                    text = "Delete \"${target.name}\"?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Are you sure you want to remove this subject from Room?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (associatedSessionsCount > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "⚠️ Notice: $associatedSessionsCount recorded study session(s) linked to this subject will also be deleted to maintain database referential integrity.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val s = target
                        subjectToDelete = null
                        viewModel.deleteSubject(s)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Subject")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { subjectToDelete = null }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

// ==========================================
// Subject Manager Item Card
// ==========================================
@Composable
private fun SubjectManagerCard(
    subject: Subject,
    totalHoursLogged: Float,
    sessionCount: Int,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = runCatching { Color(android.graphics.Color.parseColor(subject.colorTag)) }
        .getOrDefault(MaterialTheme.colorScheme.primary)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Color Tag Indicator Avatar
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = subject.name.take(2).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = accentColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Weekly Goal: ${subject.weeklyGoalHours} hrs",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Edit Button
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Subject",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Delete Button
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Subject",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stats footer bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Recorded: ${String.format(Locale.getDefault(), "%.1f", totalHoursLogged)} hrs",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "$sessionCount sessions",
                    style = MaterialTheme.typography.bodySmall,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ==========================================
// Reusable Add / Edit Subject Dialog with Validation
// ==========================================
@Composable
private fun SubjectFormDialog(
    title: String,
    confirmButtonText: String,
    initialName: String,
    initialColorTag: String,
    initialWeeklyGoalHours: String,
    onDismiss: () -> Unit,
    onValidate: (name: String, goalHoursStr: String) -> SubjectValidationResult,
    onConfirm: (name: String, colorTag: String, weeklyGoalHours: Int) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var selectedColor by remember { mutableStateOf(initialColorTag) }
    var weeklyGoalHoursStr by remember { mutableStateOf(initialWeeklyGoalHours) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Subject Name Field
                EduTrackTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    },
                    label = "Subject Name",
                    placeholder = "e.g. Distributed Systems"
                )

                // Weekly Goal Hours Field
                EduTrackTextField(
                    value = weeklyGoalHoursStr,
                    onValueChange = {
                        weeklyGoalHoursStr = it
                        errorMessage = null
                    },
                    label = "Weekly Goal (Hours)",
                    placeholder = "e.g. 10",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // Colour Tag Picker
                Column {
                    Text(
                        text = "Colour Tag",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PresetSubjectColors.forEach { hex ->
                            val color = Color(android.graphics.Color.parseColor(hex))
                            val isSelected = selectedColor.equals(hex, ignoreCase = true)

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable { selectedColor = hex }
                                    .then(
                                        if (isSelected) Modifier.border(
                                            2.dp,
                                            MaterialTheme.colorScheme.onSurface,
                                            CircleShape
                                        ) else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Inline Validation Error Display
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when (val result = onValidate(name, weeklyGoalHoursStr)) {
                        is SubjectValidationResult.Success -> {
                            val goal = weeklyGoalHoursStr.trim().toInt()
                            onConfirm(name.trim(), selectedColor, goal)
                        }
                        is SubjectValidationResult.Error -> {
                            errorMessage = result.message
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
