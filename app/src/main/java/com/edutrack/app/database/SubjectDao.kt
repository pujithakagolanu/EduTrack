package com.edutrack.app.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.edutrack.app.model.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    // 1. Create (Insert)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<Subject>): List<Long>

    // 2. Read
    @Query("SELECT * FROM subjects ORDER BY name ASC")
    fun getAllSubjects(): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE id = :subjectId")
    fun getSubjectById(subjectId: Int): Flow<Subject?>

    @Query("SELECT * FROM subjects WHERE id = :subjectId")
    suspend fun getSubjectByIdOnce(subjectId: Int): Subject?

    @Query("SELECT COUNT(*) FROM subjects")
    fun getSubjectCount(): Flow<Int>

    // 3. Update
    @Update
    suspend fun updateSubject(subject: Subject)

    // 4. Delete
    @Delete
    suspend fun deleteSubject(subject: Subject)

    @Query("DELETE FROM subjects WHERE id = :subjectId")
    suspend fun deleteSubjectById(subjectId: Int)

    @Query("DELETE FROM subjects")
    suspend fun deleteAllSubjects()
}
