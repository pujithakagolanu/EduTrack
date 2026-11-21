package com.edutrack.app.repository

import com.edutrack.app.database.StudyPlanDao
import com.edutrack.app.model.StudyPlan
import com.edutrack.app.model.StudyPlanWithSubject
import kotlinx.coroutines.flow.Flow

interface StudyPlanRepository {
    fun getAllPlans(): Flow<List<StudyPlan>>
    fun getAllPlansWithSubject(): Flow<List<StudyPlanWithSubject>>
    fun getPlanById(planId: Int): Flow<StudyPlan?>
    fun getPlansForDateRange(startOfDay: Long, endOfDay: Long): Flow<List<StudyPlanWithSubject>>
    fun getUpcomingPlans(fromTimestamp: Long): Flow<List<StudyPlanWithSubject>>
    suspend fun insertPlan(plan: StudyPlan): Long
    suspend fun insertPlans(plans: List<StudyPlan>): List<Long>
    suspend fun updatePlan(plan: StudyPlan)
    suspend fun setPlanCompleted(planId: Int, isCompleted: Boolean)
    suspend fun deletePlan(plan: StudyPlan)
    suspend fun deletePlanById(planId: Int)
    suspend fun deleteAllPlans()
}

class StudyPlanRepositoryImpl(
    private val studyPlanDao: StudyPlanDao
) : StudyPlanRepository {

    override fun getAllPlans(): Flow<List<StudyPlan>> =
        studyPlanDao.getAllPlans()

    override fun getAllPlansWithSubject(): Flow<List<StudyPlanWithSubject>> =
        studyPlanDao.getAllPlansWithSubject()

    override fun getPlanById(planId: Int): Flow<StudyPlan?> =
        studyPlanDao.getPlanById(planId)

    override fun getPlansForDateRange(startOfDay: Long, endOfDay: Long): Flow<List<StudyPlanWithSubject>> =
        studyPlanDao.getPlansForDateRange(startOfDay, endOfDay)

    override fun getUpcomingPlans(fromTimestamp: Long): Flow<List<StudyPlanWithSubject>> =
        studyPlanDao.getUpcomingPlans(fromTimestamp)

    override suspend fun insertPlan(plan: StudyPlan): Long =
        studyPlanDao.insertPlan(plan)

    override suspend fun insertPlans(plans: List<StudyPlan>): List<Long> =
        studyPlanDao.insertPlans(plans)

    override suspend fun updatePlan(plan: StudyPlan) =
        studyPlanDao.updatePlan(plan)

    override suspend fun setPlanCompleted(planId: Int, isCompleted: Boolean) =
        studyPlanDao.setPlanCompleted(planId, isCompleted)

    override suspend fun deletePlan(plan: StudyPlan) =
        studyPlanDao.deletePlan(plan)

    override suspend fun deletePlanById(planId: Int) =
        studyPlanDao.deletePlanById(planId)

    override suspend fun deleteAllPlans() =
        studyPlanDao.deleteAllPlans()
}
