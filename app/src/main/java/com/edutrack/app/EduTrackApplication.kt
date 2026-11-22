package com.edutrack.app

import android.app.Application
import com.edutrack.app.data.SampleData
import com.edutrack.app.data.UserSessionManager
import com.edutrack.app.database.AppDatabase
import com.edutrack.app.repository.AuthRepository
import com.edutrack.app.repository.AuthRepositoryImpl
import com.edutrack.app.repository.StudyPlanRepository
import com.edutrack.app.repository.StudyPlanRepositoryImpl
import com.edutrack.app.repository.StudySessionRepository
import com.edutrack.app.repository.StudySessionRepositoryImpl
import com.edutrack.app.repository.SubjectRepository
import com.edutrack.app.repository.SubjectRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EduTrackApplication : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    val userSessionManager: UserSessionManager by lazy {
        UserSessionManager(this)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(database.userDao(), userSessionManager)
    }

    val subjectRepository: SubjectRepository by lazy {
        SubjectRepositoryImpl(database.subjectDao())
    }

    val studySessionRepository: StudySessionRepository by lazy {
        StudySessionRepositoryImpl(database.studySessionDao())
    }

    val studyPlanRepository: StudyPlanRepository by lazy {
        StudyPlanRepositoryImpl(database.studyPlanDao())
    }

    val examRepository: com.edutrack.app.repository.ExamRepository by lazy {
        com.edutrack.app.repository.ExamRepositoryImpl(database.examDao())
    }

    override fun onCreate() {
        super.onCreate()

        // Seed initial default subjects, study sessions, study plans & exams if database is newly initialized
        val appScope = CoroutineScope(Dispatchers.IO)
        appScope.launch {
            val existing = subjectRepository.getAllSubjects().first()
            if (existing.isEmpty()) {
                val insertedIds = subjectRepository.insertSubjects(SampleData.initialSubjects)
                studySessionRepository.insertSessions(SampleData.createInitialSessions())
                studyPlanRepository.insertPlans(SampleData.createInitialPlans())
                examRepository.insertExams(SampleData.createInitialExams())
            }
        }
    }
}
