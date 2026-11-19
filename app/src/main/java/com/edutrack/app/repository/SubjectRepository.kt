package com.edutrack.app.repository

import com.edutrack.app.database.SubjectDao
import com.edutrack.app.model.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {
    fun getAllSubjects(): Flow<List<Subject>>
    fun getSubjectById(subjectId: Int): Flow<Subject?>
    suspend fun getSubjectByIdOnce(subjectId: Int): Subject?
    fun getSubjectCount(): Flow<Int>
    suspend fun insertSubject(subject: Subject): Long
    suspend fun insertSubjects(subjects: List<Subject>): List<Long>
    suspend fun updateSubject(subject: Subject)
    suspend fun deleteSubject(subject: Subject)
    suspend fun deleteSubjectById(subjectId: Int)
    suspend fun deleteAllSubjects()
}

class SubjectRepositoryImpl(
    private val subjectDao: SubjectDao
) : SubjectRepository {

    override fun getAllSubjects(): Flow<List<Subject>> = subjectDao.getAllSubjects()

    override fun getSubjectById(subjectId: Int): Flow<Subject?> = subjectDao.getSubjectById(subjectId)

    override suspend fun getSubjectByIdOnce(subjectId: Int): Subject? = subjectDao.getSubjectByIdOnce(subjectId)

    override fun getSubjectCount(): Flow<Int> = subjectDao.getSubjectCount()

    override suspend fun insertSubject(subject: Subject): Long = subjectDao.insertSubject(subject)

    override suspend fun insertSubjects(subjects: List<Subject>): List<Long> = subjectDao.insertSubjects(subjects)

    override suspend fun updateSubject(subject: Subject) = subjectDao.updateSubject(subject)

    override suspend fun deleteSubject(subject: Subject) = subjectDao.deleteSubject(subject)

    override suspend fun deleteSubjectById(subjectId: Int) = subjectDao.deleteSubjectById(subjectId)

    override suspend fun deleteAllSubjects() = subjectDao.deleteAllSubjects()
}
