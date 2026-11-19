package com.edutrack.app.repository

import com.edutrack.app.database.StudySessionDao
import com.edutrack.app.model.StudySession
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

interface StudySessionRepository {
    fun getAllSessions(): Flow<List<StudySession>>
    fun getSessionById(sessionId: Int): Flow<StudySession?>
    suspend fun getSessionByIdOnce(sessionId: Int): StudySession?
    fun getSessionsBySubject(subjectId: Int): Flow<List<StudySession>>
    fun getSessionsInDateRange(startDate: Long, endDate: Long): Flow<List<StudySession>>
    fun getSessionsBySubjectInDateRange(subjectId: Int, startDate: Long, endDate: Long): Flow<List<StudySession>>

    fun getTotalStudyMinutes(): Flow<Int>
    fun getTotalStudyMinutesBySubject(subjectId: Int): Flow<Int>

    // Today's study time
    fun getTodayStudyMinutes(): Flow<Int>

    // Weekly study time
    fun getWeeklyStudyMinutes(): Flow<Int>

    // Dynamic range study time
    fun getStudyMinutesInDateRange(startDate: Long, endDate: Long): Flow<Int>
    fun getStudyMinutesBySubjectInDateRange(subjectId: Int, startDate: Long, endDate: Long): Flow<Int>

    suspend fun insertSession(session: StudySession): Long
    suspend fun insertSessions(sessions: List<StudySession>): List<Long>
    suspend fun updateSession(session: StudySession)
    suspend fun deleteSession(session: StudySession)
    suspend fun deleteSessionById(sessionId: Int)
    suspend fun deleteSessionsBySubject(subjectId: Int)
    suspend fun deleteAllSessions()
}

class StudySessionRepositoryImpl(
    private val sessionDao: StudySessionDao
) : StudySessionRepository {

    override fun getAllSessions(): Flow<List<StudySession>> = sessionDao.getAllSessions()

    override fun getSessionById(sessionId: Int): Flow<StudySession?> = sessionDao.getSessionById(sessionId)

    override suspend fun getSessionByIdOnce(sessionId: Int): StudySession? = sessionDao.getSessionByIdOnce(sessionId)

    override fun getSessionsBySubject(subjectId: Int): Flow<List<StudySession>> =
        sessionDao.getSessionsBySubject(subjectId)

    override fun getSessionsInDateRange(startDate: Long, endDate: Long): Flow<List<StudySession>> =
        sessionDao.getSessionsInDateRange(startDate, endDate)

    override fun getSessionsBySubjectInDateRange(
        subjectId: Int,
        startDate: Long,
        endDate: Long
    ): Flow<List<StudySession>> =
        sessionDao.getSessionsBySubjectInDateRange(subjectId, startDate, endDate)

    override fun getTotalStudyMinutes(): Flow<Int> = sessionDao.getTotalStudyMinutes()

    override fun getTotalStudyMinutesBySubject(subjectId: Int): Flow<Int> =
        sessionDao.getTotalStudyMinutesBySubject(subjectId)

    override fun getTodayStudyMinutes(): Flow<Int> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis

        return sessionDao.getStudyMinutesInDateRange(startOfDay, endOfDay)
    }

    override fun getWeeklyStudyMinutes(): Flow<Int> {
        val calendar = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfWeek = calendar.timeInMillis
        val endOfWeek = System.currentTimeMillis()

        return sessionDao.getStudyMinutesInDateRange(startOfWeek, endOfWeek)
    }

    override fun getStudyMinutesInDateRange(startDate: Long, endDate: Long): Flow<Int> =
        sessionDao.getStudyMinutesInDateRange(startDate, endDate)

    override fun getStudyMinutesBySubjectInDateRange(
        subjectId: Int,
        startDate: Long,
        endDate: Long
    ): Flow<Int> =
        sessionDao.getStudyMinutesBySubjectInDateRange(subjectId, startDate, endDate)

    override suspend fun insertSession(session: StudySession): Long = sessionDao.insertSession(session)

    override suspend fun insertSessions(sessions: List<StudySession>): List<Long> = sessionDao.insertSessions(sessions)

    override suspend fun updateSession(session: StudySession) = sessionDao.updateSession(session)

    override suspend fun deleteSession(session: StudySession) = sessionDao.deleteSession(session)

    override suspend fun deleteSessionById(sessionId: Int) = sessionDao.deleteSessionById(sessionId)

    override suspend fun deleteSessionsBySubject(subjectId: Int) = sessionDao.deleteSessionsBySubject(subjectId)

    override suspend fun deleteAllSessions() = sessionDao.deleteAllSessions()
}
