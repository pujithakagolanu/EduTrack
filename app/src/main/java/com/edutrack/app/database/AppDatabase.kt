package com.edutrack.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.edutrack.app.model.Exam
import com.edutrack.app.model.StudyPlan
import com.edutrack.app.model.StudySession
import com.edutrack.app.model.Subject
import com.edutrack.app.model.User

/**
 * AppDatabase provides the Room database for EduTrack with entities Subject, StudySession, StudyPlan, User & Exam.
 */
@Database(
    entities = [Subject::class, StudySession::class, StudyPlan::class, User::class, Exam::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun studyPlanDao(): StudyPlanDao
    abstract fun userDao(): UserDao
    abstract fun examDao(): ExamDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val DATABASE_NAME = "edutrack_coursework.db"

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
