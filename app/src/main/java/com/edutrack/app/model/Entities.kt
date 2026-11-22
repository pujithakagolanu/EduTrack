package com.edutrack.app.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Subject entity for coursework requirements:
 * - id: Int (Primary Key, auto-generated)
 * - name: String
 * - colorTag: String
 * - weeklyGoalHours: Int
 */
@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val colorTag: String = "#6750A4",
    val weeklyGoalHours: Int = 10
)

/**
 * StudySession entity for coursework requirements:
 * - id: Int (Primary Key, auto-generated)
 * - subjectId: Int (Foreign Key referencing Subject with CASCADE delete)
 * - durationMinutes: Int
 * - date: Long (timestamp epoch ms)
 * - notes: String
 */
@Entity(
    tableName = "study_sessions",
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["subjectId"]),
        Index(value = ["date"])
    ]
)
data class StudySession(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val subjectId: Int,
    val durationMinutes: Int,
    val date: Long = System.currentTimeMillis(),
    val notes: String = ""
)

/**
 * Subject with its associated sessions, useful for relational queries.
 */
data class SubjectWithStudyTime(
    val id: Int,
    val name: String,
    val colorTag: String,
    val weeklyGoalHours: Int,
    val totalMinutes: Int,
    val sessionCount: Int
)

/**
 * StudyPlan entity for scheduling and planning upcoming study activities / tasks:
 * - id: Int (Primary Key, auto-generated)
 * - title: String (e.g. "Review Chapter 4 & Quiz")
 * - subjectId: Int (Foreign Key referencing Subject with CASCADE delete)
 * - dateMillis: Long (Epoch ms representing the scheduled calendar date)
 * - targetMinutes: Int (Planned study time duration)
 * - isCompleted: Boolean (Status of completion)
 * - notes: String (Optional details or checklist)
 * - priority: String ("High", "Medium", "Low")
 */
@Entity(
    tableName = "study_plans",
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["subjectId"]),
        Index(value = ["dateMillis"])
    ]
)
data class StudyPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val subjectId: Int,
    val dateMillis: Long,
    val targetMinutes: Int = 45,
    val isCompleted: Boolean = false,
    val notes: String = "",
    val priority: String = "Medium"
)

/**
 * StudyPlan with its joined Subject data for rich UI rendering
 */
data class StudyPlanWithSubject(
    val id: Int,
    val title: String,
    val subjectId: Int,
    val subjectName: String,
    val subjectColor: String,
    val dateMillis: Long,
    val targetMinutes: Int,
    val isCompleted: Boolean,
    val notes: String,
    val priority: String = "Medium"
)

/**
 * Exam entity for Exam Countdown feature:
 * - id: Int (Primary Key, auto-generated)
 * - title: String (e.g. "Final Examination")
 * - subjectId: Int (Foreign Key referencing Subject with CASCADE delete)
 * - examDateMillis: Long (Epoch ms for scheduled exam date)
 * - location: String (e.g. "Hall B" or "Online")
 */
@Entity(
    tableName = "exams",
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["subjectId"]),
        Index(value = ["examDateMillis"])
    ]
)
data class Exam(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val subjectId: Int,
    val examDateMillis: Long,
    val location: String = ""
)

/**
 * Exam with associated Subject details for UI presentation
 */
data class ExamWithSubject(
    val id: Int,
    val title: String,
    val subjectId: Int,
    val subjectName: String,
    val subjectColor: String,
    val examDateMillis: Long,
    val location: String
)
