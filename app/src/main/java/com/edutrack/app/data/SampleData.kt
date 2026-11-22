package com.edutrack.app.data

import com.edutrack.app.model.StudyPlan
import com.edutrack.app.model.StudySession
import com.edutrack.app.model.Subject
import java.util.Calendar

object SampleData {
    val initialSubjects = listOf(
        Subject(
            id = 1,
            name = "Mobile App Architecture",
            colorTag = "#6750A4",
            weeklyGoalHours = 12
        ),
        Subject(
            id = 2,
            name = "Algorithms & Data Structures",
            colorTag = "#3B82F6",
            weeklyGoalHours = 10
        ),
        Subject(
            id = 3,
            name = "Database Systems & SQL",
            colorTag = "#2E7D5B",
            weeklyGoalHours = 8
        ),
        Subject(
            id = 4,
            name = "Human-Computer Interaction",
            colorTag = "#D97706",
            weeklyGoalHours = 6
        )
    )

    fun createInitialSessions(currentTimeMs: Long = System.currentTimeMillis()): List<StudySession> = listOf(
        StudySession(
            id = 1,
            subjectId = 1,
            durationMinutes = 90,
            date = currentTimeMs - 3600000L * 2,
            notes = "Completed Room database entities, DAOs, and Foreign Key CASCADE constraints."
        ),
        StudySession(
            id = 2,
            subjectId = 2,
            durationMinutes = 120,
            date = currentTimeMs - 86400000L * 1,
            notes = "Solved 4 graph traversal problems and analyzed priority queue complexities."
        ),
        StudySession(
            id = 3,
            subjectId = 3,
            durationMinutes = 75,
            date = currentTimeMs - 86400000L * 2,
            notes = "Configured index profiling and compared sequential scan vs bitmap heap scan."
        ),
        StudySession(
            id = 4,
            subjectId = 1,
            durationMinutes = 60,
            date = currentTimeMs - 86400000L * 3,
            notes = "Implemented DAO methods with reactive Flows and Repository abstraction layer."
        )
    )

    fun createInitialPlans(currentTimeMs: Long = System.currentTimeMillis()): List<StudyPlan> {
        val cal = Calendar.getInstance().apply { timeInMillis = currentTimeMs }

        fun getTimeForDayOffset(offsetDays: Int, hour: Int = 10): Long {
            val c = Calendar.getInstance().apply {
                timeInMillis = currentTimeMs
                add(Calendar.DAY_OF_YEAR, offsetDays)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return c.timeInMillis
        }

        return listOf(
            StudyPlan(
                id = 1,
                title = "Compose Navigation & Animations Deep Dive",
                subjectId = 1,
                dateMillis = getTimeForDayOffset(0, 14),
                targetMinutes = 60,
                isCompleted = false,
                notes = "Implement transition specs and test backstack retention.",
                priority = "High"
            ),
            StudyPlan(
                id = 2,
                title = "Dynamic Programming & Memoization Exercises",
                subjectId = 2,
                dateMillis = getTimeForDayOffset(1, 10),
                targetMinutes = 90,
                isCompleted = false,
                notes = "Solve 0/1 Knapsack and Longest Common Subsequence.",
                priority = "High"
            ),
            StudyPlan(
                id = 3,
                title = "SQL Index Optimization & Query Profiling",
                subjectId = 3,
                dateMillis = getTimeForDayOffset(2, 16),
                targetMinutes = 45,
                isCompleted = false,
                notes = "Analyze B-tree vs hash index trade-offs.",
                priority = "Medium"
            ),
            StudyPlan(
                id = 4,
                title = "Usability Heuristics & Material 3 Review",
                subjectId = 4,
                dateMillis = getTimeForDayOffset(4, 11),
                targetMinutes = 50,
                isCompleted = false,
                notes = "Review Nielsen Norman group heuristics and color contrast.",
                priority = "Low"
            )
        )
    }

    fun createInitialExams(currentTimeMs: Long = System.currentTimeMillis()): List<com.edutrack.app.model.Exam> {
        fun getExamTime(daysAhead: Int): Long {
            val c = Calendar.getInstance().apply {
                timeInMillis = currentTimeMs
                add(Calendar.DAY_OF_YEAR, daysAhead)
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return c.timeInMillis
        }

        return listOf(
            com.edutrack.app.model.Exam(
                id = 1,
                title = "Mobile Architecture Final Exam",
                subjectId = 1,
                examDateMillis = getExamTime(12),
                location = "Lecture Hall 4A"
            ),
            com.edutrack.app.model.Exam(
                id = 2,
                title = "Algorithms Midterm Assessment",
                subjectId = 2,
                examDateMillis = getExamTime(5),
                location = "Lab Complex 2"
            ),
            com.edutrack.app.model.Exam(
                id = 3,
                title = "Database Systems Practical",
                subjectId = 3,
                examDateMillis = getExamTime(18),
                location = "Main Campus Hall"
            )
        )
    }
}
