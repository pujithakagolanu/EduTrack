package com.edutrack.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.edutrack.app.model.StudySession
import com.edutrack.app.model.Subject

/**
 * AppDatabase provides the Room database for EduTrack with entities Subject & StudySession.
 * It enforces referential integrity through foreign keys and provides a thread-safe singleton.
 */
@Database(
    entities = [Subject::class, StudySession::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun studySessionDao(): StudySessionDao

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
                    // Enable foreign key constraints in SQLite
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
