package com.edutrack.app.repository

import com.edutrack.app.database.ExamDao
import com.edutrack.app.model.Exam
import com.edutrack.app.model.ExamWithSubject
import kotlinx.coroutines.flow.Flow

interface ExamRepository {
    fun getAllExamsWithSubject(): Flow<List<ExamWithSubject>>
    fun getAllExams(): Flow<List<Exam>>
    suspend fun insertExam(exam: Exam): Long
    suspend fun insertExams(exams: List<Exam>): List<Long>
    suspend fun deleteExam(exam: Exam)
    suspend fun deleteExamById(examId: Int)
}

class ExamRepositoryImpl(
    private val examDao: ExamDao
) : ExamRepository {
    override fun getAllExamsWithSubject(): Flow<List<ExamWithSubject>> =
        examDao.getAllExamsWithSubject()

    override fun getAllExams(): Flow<List<Exam>> =
        examDao.getAllExams()

    override suspend fun insertExam(exam: Exam): Long =
        examDao.insertExam(exam)

    override suspend fun insertExams(exams: List<Exam>): List<Long> =
        examDao.insertExams(exams)

    override suspend fun deleteExam(exam: Exam) =
        examDao.deleteExam(exam)

    override suspend fun deleteExamById(examId: Int) =
        examDao.deleteExamById(examId)
}
