package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ComputerCenterDao {

    // User Operations
    @Query("SELECT * FROM users LIMIT 1")
    fun getUser(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    // Tracks Operations
    @Query("SELECT * FROM it_tracks")
    fun getAllTracks(): Flow<List<ITTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<ITTrackEntity>)

    // Courses Operations
    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE trackId = :trackId")
    fun getCoursesByTrack(trackId: String): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE id = :courseId")
    fun getCourseById(courseId: String): Flow<CourseEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseEntity>)

    @Update
    suspend fun updateCourse(course: CourseEntity)

    // Lessons Operations
    @Query("SELECT * FROM lessons WHERE courseId = :courseId ORDER BY orderIndex ASC")
    fun getLessonsForCourse(courseId: String): Flow<List<LessonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Query("UPDATE lessons SET isCompleted = 1 WHERE id = :lessonId")
    suspend fun markLessonCompleted(lessonId: String)

    // Quizzes Operations
    @Query("SELECT * FROM quizzes WHERE targetType = 'PLACEMENT'")
    fun getPlacementQuestions(): Flow<List<QuizQuestionEntity>>

    @Query("SELECT * FROM quizzes WHERE targetId = :targetId")
    fun getQuizzesForTarget(targetId: String): Flow<List<QuizQuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizzes(quizzes: List<QuizQuestionEntity>)

    // Enrollments Operations
    @Query("SELECT * FROM enrollments ORDER BY id DESC")
    fun getAllEnrollments(): Flow<List<EnrollmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnrollment(enrollment: EnrollmentEntity): Long

    // Certificates Operations
    @Query("SELECT * FROM certificates")
    fun getAllCertificates(): Flow<List<CertificateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCertificate(certificate: CertificateEntity)

    // Tools Operations
    @Query("SELECT * FROM student_tools")
    fun getAllTools(): Flow<List<StudentToolEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTools(tools: List<StudentToolEntity>)
}
