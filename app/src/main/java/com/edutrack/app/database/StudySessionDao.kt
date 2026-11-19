package com.edutrack.app.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.edutrack.app.model.StudySession
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {

    // 1. Create (Insert)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySession): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<StudySession>): List<Long>

    // 2. Read (All sessions)
    @Query("SELECT * FROM study_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<StudySession>>

    @Query("SELECT * FROM study_sessions WHERE id = :sessionId")
    fun getSessionById(sessionId: Int): Flow<StudySession?>

    @Query("SELECT * FROM study_sessions WHERE id = :sessionId")
    suspend fun getSessionByIdOnce(sessionId: Int): StudySession?

    // 3. Sessions by Subject
    @Query("SELECT * FROM study_sessions WHERE subjectId = :subjectId ORDER BY date DESC")
    fun getSessionsBySubject(subjectId: Int): Flow<List<StudySession>>

    // 4. Date-Range Filtering Queries
    @Query("SELECT * FROM study_sessions WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getSessionsInDateRange(startDate: Long, endDate: Long): Flow<List<StudySession>>

    @Query("SELECT * FROM study_sessions WHERE subjectId = :subjectId AND date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getSessionsBySubjectInDateRange(subjectId: Int, startDate: Long, endDate: Long): Flow<List<StudySession>>

    // 5. Aggregate Calculations: Total Study Minutes
    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM study_sessions")
    fun getTotalStudyMinutes(): Flow<Int>

    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM study_sessions WHERE subjectId = :subjectId")
    fun getTotalStudyMinutesBySubject(subjectId: Int): Flow<Int>

    // 6. Aggregate Calculations: Study Time in Date Range (Today's / Weekly)
    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM study_sessions WHERE date >= :startDate AND date <= :endDate")
    fun getStudyMinutesInDateRange(startDate: Long, endDate: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM study_sessions WHERE subjectId = :subjectId AND date >= :startDate AND date <= :endDate")
    fun getStudyMinutesBySubjectInDateRange(subjectId: Int, startDate: Long, endDate: Long): Flow<Int>

    // 7. Update
    @Update
    suspend fun updateSession(session: StudySession)

    // 8. Delete
    @Delete
    suspend fun deleteSession(session: StudySession)

    @Query("DELETE FROM study_sessions WHERE id = :sessionId")
    suspend fun deleteSessionById(sessionId: Int)

    @Query("DELETE FROM study_sessions WHERE subjectId = :subjectId")
    suspend fun deleteSessionsBySubject(subjectId: Int)

    @Query("DELETE FROM study_sessions")
    suspend fun deleteAllSessions()
}
