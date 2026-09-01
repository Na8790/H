package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        UserEntity::class,
        ITTrackEntity::class,
        CourseEntity::class,
        LessonEntity::class,
        QuizQuestionEntity::class,
        EnrollmentEntity::class,
        CertificateEntity::class,
        StudentToolEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): ComputerCenterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sanaa_cc_elearn.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
