package com.edutrack.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.edutrack.app.model.StudySession
import com.edutrack.app.model.Subject
import com.edutrack.app.repository.StudySessionRepository
import com.edutrack.app.repository.SubjectRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudyTrackerViewModel(
    private val subjectRepository: SubjectRepository,
    private val sessionRepository: StudySessionRepository
) : ViewModel() {

    // Observable Flow streams for UI
    val subjects: StateFlow<List<Subject>> = subjectRepository.getAllSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sessions: StateFlow<List<StudySession>> = sessionRepository.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalStudyMinutes: StateFlow<Int> = sessionRepository.getTotalStudyMinutes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayStudyMinutes: StateFlow<Int> = sessionRepository.getTodayStudyMinutes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val weeklyStudyMinutes: StateFlow<Int> = sessionRepository.getWeeklyStudyMinutes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // CRUD - Subject
    fun addSubject(name: String, colorTag: String, weeklyGoalHours: Int) {
        viewModelScope.launch {
            subjectRepository.insertSubject(
                Subject(
                    name = name.trim(),
                    colorTag = colorTag,
                    weeklyGoalHours = weeklyGoalHours
                )
            )
        }
    }

    fun updateSubject(subject: Subject) {
        viewModelScope.launch {
            subjectRepository.updateSubject(subject)
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch {
            // Because of ForeignKey CASCADE in StudySession, all child sessions
            // are automatically removed by Room/SQLite, preserving referential integrity.
            subjectRepository.deleteSubject(subject)
        }
    }

    fun deleteSubjectById(subjectId: Int) {
        viewModelScope.launch {
            subjectRepository.deleteSubjectById(subjectId)
        }
    }

    // CRUD - StudySession
    fun addStudySession(
        subjectId: Int,
        durationMinutes: Int,
        notes: String = "",
        date: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch {
            sessionRepository.insertSession(
                StudySession(
                    subjectId = subjectId,
                    durationMinutes = durationMinutes,
                    notes = notes.trim(),
                    date = date
                )
            )
        }
    }

    fun updateStudySession(session: StudySession) {
        viewModelScope.launch {
            sessionRepository.updateSession(session)
        }
    }

    fun deleteSession(session: StudySession) {
        viewModelScope.launch {
            sessionRepository.deleteSession(session)
        }
    }

    fun deleteSessionById(sessionId: Int) {
        viewModelScope.launch {
            sessionRepository.deleteSessionById(sessionId)
        }
    }
}

class ViewModelFactory(
    private val subjectRepository: SubjectRepository,
    private val sessionRepository: StudySessionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudyTrackerViewModel::class.java)) {
            return StudyTrackerViewModel(subjectRepository, sessionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
