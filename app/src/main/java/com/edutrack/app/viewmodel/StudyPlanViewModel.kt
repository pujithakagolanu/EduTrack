package com.edutrack.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.edutrack.app.model.StudyPlan
import com.edutrack.app.model.StudyPlanWithSubject
import com.edutrack.app.model.Subject
import com.edutrack.app.repository.StudyPlanRepository
import com.edutrack.app.repository.SubjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class StudyPlanViewModel(
    private val planRepository: StudyPlanRepository,
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    // 1. Current selected calendar day timestamp (start of day)
    private val _selectedDateMillis = MutableStateFlow(getStartOfDay(System.currentTimeMillis()))
    val selectedDateMillis: StateFlow<Long> = _selectedDateMillis.asStateFlow()

    // 2. Current displayed calendar month/year
    private val _displayedMonthYear = MutableStateFlow(getCurrentYearMonth())
    val displayedMonthYear: StateFlow<Pair<Int, Int>> = _displayedMonthYear.asStateFlow() // Pair(year, month 0-indexed)

    // 3. Subjects list for assigning
    val subjects: StateFlow<List<Subject>> = subjectRepository.getAllSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 4. All study plans with subjects
    val allPlans: StateFlow<List<StudyPlanWithSubject>> = planRepository.getAllPlansWithSubject()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 5. Selected Day Plans
    val selectedDayPlans: StateFlow<List<StudyPlanWithSubject>> = combine(allPlans, selectedDateMillis) { plans, selectedDate ->
        val startOfDay = getStartOfDay(selectedDate)
        val endOfDay = getEndOfDay(selectedDate)
        plans.filter { it.dateMillis in startOfDay..endOfDay }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 6. Upcoming incomplete plans (future from start of today)
    val upcomingPlans: StateFlow<List<StudyPlanWithSubject>> = allPlans.combine(
        MutableStateFlow(getStartOfDay(System.currentTimeMillis()))
    ) { plans, startOfToday ->
        plans.filter { it.dateMillis >= startOfToday && !it.isCompleted }
            .sortedBy { it.dateMillis }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 7. Dates with scheduled activities (set of start-of-day millis) for calendar highlighting
    val scheduledDatesWithActivity: StateFlow<Set<Long>> = allPlans.combine(
        MutableStateFlow(Unit)
    ) { plans, _ ->
        plans.map { getStartOfDay(it.dateMillis) }.toSet()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun selectDate(dateMillis: Long) {
        _selectedDateMillis.value = getStartOfDay(dateMillis)
    }

    fun previousMonth() {
        val (year, month) = _displayedMonthYear.value
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, -1)
        }
        _displayedMonthYear.value = Pair(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
    }

    fun nextMonth() {
        val (year, month) = _displayedMonthYear.value
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, 1)
        }
        _displayedMonthYear.value = Pair(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
    }

    fun addStudyPlan(
        title: String,
        subjectId: Int,
        dateMillis: Long,
        targetMinutes: Int,
        notes: String = ""
    ) {
        viewModelScope.launch {
            planRepository.insertPlan(
                StudyPlan(
                    title = title.trim(),
                    subjectId = subjectId,
                    dateMillis = dateMillis,
                    targetMinutes = targetMinutes.coerceAtLeast(5),
                    isCompleted = false,
                    notes = notes.trim()
                )
            )
        }
    }

    fun togglePlanCompletion(plan: StudyPlanWithSubject) {
        viewModelScope.launch {
            planRepository.setPlanCompleted(plan.id, !plan.isCompleted)
        }
    }

    fun deletePlan(planId: Int) {
        viewModelScope.launch {
            planRepository.deletePlanById(planId)
        }
    }

    companion object {
        fun getStartOfDay(timestamp: Long): Long {
            return Calendar.getInstance().apply {
                timeInMillis = timestamp
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }

        fun getEndOfDay(timestamp: Long): Long {
            return Calendar.getInstance().apply {
                timeInMillis = timestamp
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis
        }

        fun getCurrentYearMonth(): Pair<Int, Int> {
            val cal = Calendar.getInstance()
            return Pair(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
        }
    }
}

class StudyPlanViewModelFactory(
    private val planRepository: StudyPlanRepository,
    private val subjectRepository: SubjectRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudyPlanViewModel::class.java)) {
            return StudyPlanViewModel(planRepository, subjectRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
