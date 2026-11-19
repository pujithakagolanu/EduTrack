package com.edutrack.app.model

import com.edutrack.app.repository.SubjectRepository
import com.edutrack.app.ui.screens.calculateStudyStreakDays
import com.edutrack.app.ui.screens.calculateWeeklyDayHours
import com.edutrack.app.viewmodel.SubjectValidationResult
import com.edutrack.app.viewmodel.SubjectViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class EduTrackIntegrationUnitTest {

    @Test
    fun testSubjectValidationEmptyName() {
        val fakeSubjectRepo = object : SubjectRepository {
            override fun getAllSubjects(): Flow<List<Subject>> = flowOf(emptyList())
            override fun getSubjectById(subjectId: Int): Flow<Subject?> = flowOf(null)
            override suspend fun getSubjectByIdOnce(subjectId: Int): Subject? = null
            override fun getSubjectCount(): Flow<Int> = flowOf(0)
            override suspend fun insertSubject(subject: Subject): Long = 1L
            override suspend fun insertSubjects(subjects: List<Subject>): List<Long> = listOf(1L)
            override suspend fun updateSubject(subject: Subject) {}
            override suspend fun deleteSubject(subject: Subject) {}
            override suspend fun deleteSubjectById(subjectId: Int) {}
            override suspend fun deleteAllSubjects() {}
        }

        val viewModel = SubjectViewModel(fakeSubjectRepo, object : com.edutrack.app.repository.StudySessionRepository {
            override fun getAllSessions(): Flow<List<StudySession>> = flowOf(emptyList())
            override fun getSessionById(sessionId: Int): Flow<StudySession?> = flowOf(null)
            override suspend fun getSessionByIdOnce(sessionId: Int): StudySession? = null
            override fun getSessionsBySubject(subjectId: Int): Flow<List<StudySession>> = flowOf(emptyList())
            override fun getSessionsInDateRange(startDate: Long, endDate: Long): Flow<List<StudySession>> = flowOf(emptyList())
            override fun getSessionsBySubjectInDateRange(subjectId: Int, startDate: Long, endDate: Long): Flow<List<StudySession>> = flowOf(emptyList())
            override fun getTotalStudyMinutes(): Flow<Int> = flowOf(0)
            override fun getTotalStudyMinutesBySubject(subjectId: Int): Flow<Int> = flowOf(0)
            override fun getTodayStudyMinutes(): Flow<Int> = flowOf(0)
            override fun getWeeklyStudyMinutes(): Flow<Int> = flowOf(0)
            override fun getStudyMinutesInDateRange(startDate: Long, endDate: Long): Flow<Int> = flowOf(0)
            override fun getStudyMinutesBySubjectInDateRange(subjectId: Int, startDate: Long, endDate: Long): Flow<Int> = flowOf(0)
            override suspend fun insertSession(session: StudySession): Long = 1L
            override suspend fun insertSessions(sessions: List<StudySession>): List<Long> = listOf(1L)
            override suspend fun updateSession(session: StudySession) {}
            override suspend fun deleteSession(session: StudySession) {}
            override suspend fun deleteSessionById(sessionId: Int) {}
            override suspend fun deleteSessionsBySubject(subjectId: Int) {}
            override suspend fun deleteAllSessions() {}
        })

        // 1. Empty name validation test
        val emptyResult = viewModel.validateSubject("", "10")
        assertTrue(emptyResult is SubjectValidationResult.Error)
        assertEquals("Subject name cannot be empty", (emptyResult as SubjectValidationResult.Error).message)

        // 2. Invalid zero or negative hours validation test
        val zeroGoalResult = viewModel.validateSubject("Math", "0")
        assertTrue(zeroGoalResult is SubjectValidationResult.Error)
        assertEquals("Weekly goal hours must be a positive number greater than 0", (zeroGoalResult as SubjectValidationResult.Error).message)

        val negativeGoalResult = viewModel.validateSubject("Math", "-5")
        assertTrue(negativeGoalResult is SubjectValidationResult.Error)

        val nonNumberGoalResult = viewModel.validateSubject("Math", "abc")
        assertTrue(nonNumberGoalResult is SubjectValidationResult.Error)

        // 3. Valid input
        val validResult = viewModel.validateSubject("Calculus", "8")
        assertTrue(validResult is SubjectValidationResult.Success)
    }

    @Test
    fun testStreakCalculation() {
        val calendar = Calendar.getInstance()
        val today = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val twoDaysAgo = calendar.timeInMillis

        // Consecutive 3 days
        val sessions = listOf(
            StudySession(id = 1, subjectId = 1, durationMinutes = 60, date = today),
            StudySession(id = 2, subjectId = 1, durationMinutes = 45, date = yesterday),
            StudySession(id = 3, subjectId = 1, durationMinutes = 90, date = twoDaysAgo)
        )

        val streak = calculateStudyStreakDays(sessions)
        assertEquals(3, streak)

        // Empty sessions gives 0
        assertEquals(0, calculateStudyStreakDays(emptyList()))
    }

    @Test
    fun testWeeklyDayHoursCalculation() {
        val calendar = Calendar.getInstance()
        val sessions = listOf(
            StudySession(id = 1, subjectId = 1, durationMinutes = 120, date = calendar.timeInMillis)
        )
        val weeklyData = calculateWeeklyDayHours(sessions)
        assertEquals(7, weeklyData.size)
        val todayData = weeklyData.find { it.isToday }
        assertEquals(2.0f, todayData?.hours ?: 0f, 0.01f)
    }
}
