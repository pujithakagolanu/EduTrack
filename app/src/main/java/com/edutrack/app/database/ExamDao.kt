package com.edutrack.app.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.edutrack.app.model.Exam
import com.edutrack.app.model.ExamWithSubject
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: Exam): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExams(exams: List<Exam>): List<Long>

    @Delete
    suspend fun deleteExam(exam: Exam)

    @Query("DELETE FROM exams WHERE id = :examId")
    suspend fun deleteExamById(examId: Int)

    @Query("""
        SELECT e.id, e.title, e.subjectId, s.name AS subjectName,
               s.colorTag AS subjectColor, e.examDateMillis, e.location
        FROM exams e
        INNER JOIN subjects s ON e.subjectId = s.id
        ORDER BY e.examDateMillis ASC
    """)
    fun getAllExamsWithSubject(): Flow<List<ExamWithSubject>>

    @Query("SELECT * FROM exams ORDER BY examDateMillis ASC")
    fun getAllExams(): Flow<List<Exam>>
}
