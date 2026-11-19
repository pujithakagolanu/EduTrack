package com.edutrack.app.data

import com.edutrack.app.model.StudySession
import com.edutrack.app.model.Subject

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
}
