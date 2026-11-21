package com.edutrack.app.model

import com.edutrack.app.repository.StudyPlanRepository
import com.edutrack.app.repository.SubjectRepository
import com.edutrack.app.ui.screens.calculateStudyStreakDays
import com.edutrack.app.ui.screens.calculateWeeklyDayHours
import com.edutrack.app.viewmodel.StudyPlanViewModel
import com.edutrack.app.viewmodel.SubjectValidationResult
import com.edutrack.app.viewmodel.SubjectViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class EduTrackIntegrationUnitTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

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
    fun testStudyPlanViewModelSchedulingAndCompletion() = runTest {
        val sampleSubject = Subject(id = 1, name = "Computer Science", colorTag = "#6750A4", weeklyGoalHours = 10)
        val today = StudyPlanViewModel.getStartOfDay(System.currentTimeMillis())

        val planList = mutableListOf(
            StudyPlan(id = 1, title = "Data Structures Review", subjectId = 1, dateMillis = today, targetMinutes = 45, isCompleted = false)
        )

        val fakePlanRepo = object : StudyPlanRepository {
            override fun getAllPlans(): Flow<List<StudyPlan>> = flowOf(planList)
            override fun getAllPlansWithSubject(): Flow<List<StudyPlanWithSubject>> = flowOf(
                planList.map {
                    StudyPlanWithSubject(
                        id = it.id,
                        title = it.title,
                        subjectId = it.subjectId,
                        subjectName = sampleSubject.name,
                        subjectColor = sampleSubject.colorTag,
                        dateMillis = it.dateMillis,
                        targetMinutes = it.targetMinutes,
                        isCompleted = it.isCompleted,
                        notes = it.notes
                    )
                }
            )
            override fun getPlanById(planId: Int): Flow<StudyPlan?> = flowOf(planList.find { it.id == planId })
            override fun getPlansForDateRange(startOfDay: Long, endOfDay: Long): Flow<List<StudyPlanWithSubject>> =
                getAllPlansWithSubject()
            override fun getUpcomingPlans(fromTimestamp: Long): Flow<List<StudyPlanWithSubject>> =
                getAllPlansWithSubject()
            override suspend fun insertPlan(plan: StudyPlan): Long {
                planList.add(plan)
                return plan.id.toLong()
            }
            override suspend fun insertPlans(plans: List<StudyPlan>): List<Long> {
                planList.addAll(plans)
                return plans.map { it.id.toLong() }
            }
            override suspend fun updatePlan(plan: StudyPlan) {}
            override suspend fun setPlanCompleted(planId: Int, isCompleted: Boolean) {
                val index = planList.indexOfFirst { it.id == planId }
                if (index != -1) {
                    planList[index] = planList[index].copy(isCompleted = isCompleted)
                }
            }
            override suspend fun deletePlan(plan: StudyPlan) {
                planList.removeIf { it.id == plan.id }
            }
            override suspend fun deletePlanById(planId: Int) {
                planList.removeIf { it.id == planId }
            }
            override suspend fun deleteAllPlans() {
                planList.clear()
            }
        }

        val fakeSubjectRepo = object : SubjectRepository {
            override fun getAllSubjects(): Flow<List<Subject>> = flowOf(listOf(sampleSubject))
            override fun getSubjectById(subjectId: Int): Flow<Subject?> = flowOf(sampleSubject)
            override suspend fun getSubjectByIdOnce(subjectId: Int): Subject? = sampleSubject
            override fun getSubjectCount(): Flow<Int> = flowOf(1)
            override suspend fun insertSubject(subject: Subject): Long = 1L
            override suspend fun insertSubjects(subjects: List<Subject>): List<Long> = listOf(1L)
            override suspend fun updateSubject(subject: Subject) {}
            override suspend fun deleteSubject(subject: Subject) {}
            override suspend fun deleteSubjectById(subjectId: Int) {}
            override suspend fun deleteAllSubjects() {}
        }

        val viewModel = StudyPlanViewModel(fakePlanRepo, fakeSubjectRepo)

        // 1. Verify date selection & start of day
        viewModel.selectDate(today)
        assertEquals(today, viewModel.selectedDateMillis.value)

        // 2. Verify allPlans stream from repository
        val loadedPlans = fakePlanRepo.getAllPlansWithSubject().first()
        assertEquals(1, loadedPlans.size)
        assertEquals("Data Structures Review", loadedPlans[0].title)
        assertEquals(sampleSubject.name, loadedPlans[0].subjectName)
        assertFalse(loadedPlans[0].isCompleted)

        // 3. Verify date highlighted activity set contains today
        val scheduledDates = planList.map { StudyPlanViewModel.getStartOfDay(it.dateMillis) }.toSet()
        assertTrue(scheduledDates.contains(today))
    }
}
