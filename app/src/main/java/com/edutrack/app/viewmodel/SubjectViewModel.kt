package com.edutrack.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.edutrack.app.model.StudySession
import com.edutrack.app.model.Subject
import com.edutrack.app.repository.StudySessionRepository
import com.edutrack.app.repository.SubjectRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class SubjectValidationResult {
    data object Success : SubjectValidationResult()
    data class Error(val message: String) : SubjectValidationResult()
}

class SubjectViewModel(
    private val subjectRepository: SubjectRepository,
    private val sessionRepository: StudySessionRepository
) : ViewModel() {

    // Observable subjects list from Room
    val subjects: StateFlow<List<Subject>> = subjectRepository.getAllSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Observable sessions list to compute study hours per subject
    val sessions: StateFlow<List<StudySession>> = sessionRepository.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow: SharedFlow<String> = _eventFlow.asSharedFlow()

    fun validateSubject(name: String, weeklyGoalHoursStr: String, excludeSubjectId: Int? = null): SubjectValidationResult {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            return SubjectValidationResult.Error("Subject name cannot be empty")
        }

        val goalHours = weeklyGoalHoursStr.toIntOrNull()
        if (goalHours == null || goalHours <= 0) {
            return SubjectValidationResult.Error("Weekly goal hours must be a positive number greater than 0")
        }

        val isDuplicate = subjects.value.any {
            it.name.equals(trimmedName, ignoreCase = true) && (excludeSubjectId == null || it.id != excludeSubjectId)
        }
        if (isDuplicate) {
            return SubjectValidationResult.Error("A subject with the name \"$trimmedName\" already exists")
        }

        return SubjectValidationResult.Success
    }

    fun addSubject(name: String, colorTag: String, weeklyGoalHours: Int, onComplete: () -> Unit) {
        viewModelScope.launch {
            val subject = Subject(
                name = name.trim(),
                colorTag = colorTag,
                weeklyGoalHours = weeklyGoalHours
            )
            subjectRepository.insertSubject(subject)
            _eventFlow.emit("Subject \"${subject.name}\" created successfully")
            onComplete()
        }
    }

    fun updateSubject(subject: Subject, onComplete: () -> Unit) {
        viewModelScope.launch {
            subjectRepository.updateSubject(subject)
            _eventFlow.emit("Subject \"${subject.name}\" updated successfully")
            onComplete()
        }
    }

    fun deleteSubject(subject: Subject, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            // Room Foreign Key CASCADE removes all associated study sessions automatically
            subjectRepository.deleteSubject(subject)
            _eventFlow.emit("Subject \"${subject.name}\" and associated sessions deleted")
            onComplete()
        }
    }
}

class SubjectViewModelFactory(
    private val subjectRepository: SubjectRepository,
    private val sessionRepository: StudySessionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubjectViewModel::class.java)) {
            return SubjectViewModel(subjectRepository, sessionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
