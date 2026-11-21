package com.edutrack.app.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.edutrack.app.model.StudyPlan
import com.edutrack.app.model.StudyPlanWithSubject
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyPlanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: StudyPlan): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlans(plans: List<StudyPlan>): List<Long>

    @Update
    suspend fun updatePlan(plan: StudyPlan)

    @Delete
    suspend fun deletePlan(plan: StudyPlan)

    @Query("DELETE FROM study_plans WHERE id = :planId")
    suspend fun deletePlanById(planId: Int)

    @Query("SELECT * FROM study_plans WHERE id = :planId")
    fun getPlanById(planId: Int): Flow<StudyPlan?>

    @Query("SELECT * FROM study_plans ORDER BY dateMillis ASC")
    fun getAllPlans(): Flow<List<StudyPlan>>

    @Query("""
        SELECT 
            p.id, p.title, p.subjectId, s.name AS subjectName, 
            s.colorTag AS subjectColor, p.dateMillis, p.targetMinutes, 
            p.isCompleted, p.notes
        FROM study_plans p
        INNER JOIN subjects s ON p.subjectId = s.id
        ORDER BY p.dateMillis ASC, p.id ASC
    """)
    fun getAllPlansWithSubject(): Flow<List<StudyPlanWithSubject>>

    @Query("""
        SELECT 
            p.id, p.title, p.subjectId, s.name AS subjectName, 
            s.colorTag AS subjectColor, p.dateMillis, p.targetMinutes, 
            p.isCompleted, p.notes
        FROM study_plans p
        INNER JOIN subjects s ON p.subjectId = s.id
        WHERE p.dateMillis >= :startOfDay AND p.dateMillis <= :endOfDay
        ORDER BY p.isCompleted ASC, p.id ASC
    """)
    fun getPlansForDateRange(startOfDay: Long, endOfDay: Long): Flow<List<StudyPlanWithSubject>>

    @Query("""
        SELECT 
            p.id, p.title, p.subjectId, s.name AS subjectName, 
            s.colorTag AS subjectColor, p.dateMillis, p.targetMinutes, 
            p.isCompleted, p.notes
        FROM study_plans p
        INNER JOIN subjects s ON p.subjectId = s.id
        WHERE p.dateMillis >= :fromTimestamp
        ORDER BY p.isCompleted ASC, p.dateMillis ASC
    """)
    fun getUpcomingPlans(fromTimestamp: Long): Flow<List<StudyPlanWithSubject>>

    @Query("UPDATE study_plans SET isCompleted = :isCompleted WHERE id = :planId")
    suspend fun setPlanCompleted(planId: Int, isCompleted: Boolean)

    @Query("DELETE FROM study_plans")
    suspend fun deleteAllPlans()
}
