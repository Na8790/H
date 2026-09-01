package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data models and Room entities for Sana'a University Computer Center Platform
 */

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = "student_raghad",
    val fullName: String = "رغد حمود حسين العصري",
    val studentId: String = "IT-2024-8841",
    val email: String = "raghad.alasri@univ-sanaa.edu.ye",
    val phone: String = "+967 777 000 000",
    val department: String = "قسم تكنولوجيا المعلومات - مركز الحاسب الآلي",
    val role: String = "طالب دبلوم تكنولوجيا معلومات",
    val currentLevel: String = "متقدم (Advanced)",
    val points: Int = 1250,
    val completedCoursesCount: Int = 4,
    val enrolledCoursesCount: Int = 2
)

@Entity(tableName = "it_tracks")
data class ITTrackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val careerFuture: String,
    val requiredSkills: String,
    val iconName: String,
    val colorHex: Long
)

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: String,
    val title: String,
    val trackId: String,
    val instructor: String,
    val durationHours: Int,
    val level: String, // مبتدئ / متوسط / متقدم
    val isFree: Boolean,
    val priceYemenRials: Int,
    val rating: Float,
    val totalLessons: Int,
    val enrolledCount: Int,
    val description: String,
    val prerequisites: String,
    val labRoom: String,
    val isEnrolled: Boolean = false,
    val isCompleted: Boolean = false,
    val progressPercent: Int = 0
)

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val orderIndex: Int,
    val title: String,
    val durationMinutes: Int,
    val summary: String,
    val fullContent: String,
    val keyTerms: String,
    val codeSnippet: String = "",
    val isCompleted: Boolean = false
)

@Entity(tableName = "quizzes")
data class QuizQuestionEntity(
    @PrimaryKey val id: String,
    val targetType: String, // "LESSON" or "PLACEMENT"
    val targetId: String, // lessonId or trackId
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOptionIndex: Int, // 0..3
    val explanation: String
)

@Entity(tableName = "enrollments")
data class EnrollmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courseId: String,
    val courseTitle: String,
    val studentName: String,
    val studentEmail: String,
    val enrollmentDate: String,
    val paymentMethod: String, // الكريمي / بنك اليمن الدولي / محفظة إلكترونية / مجاني
    val paymentReference: String,
    val amountPaid: Int,
    val status: String, // "معتمد" / "قيد المراجعة" / "مكتمل"
    val scheduleDetails: String,
    val labName: String
)

@Entity(tableName = "certificates")
data class CertificateEntity(
    @PrimaryKey val certificateId: String,
    val studentName: String,
    val courseTitle: String,
    val trackName: String,
    val issueDate: String,
    val finalScore: Int,
    val gradeLevel: String, // ممتاز / جيد جداً
    val supervisorName: String = "أ. عبدالله داعر / أ. امتياز الصمصام",
    val centerName: String = "مركز الحاسب الآلي - جامعة صنعاء"
)

@Entity(tableName = "student_tools")
data class StudentToolEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // "قواعد بيانات" / "بيئات تطوير" / "مذكرات ومراجع" / "أكواد ومشاريع"
    val version: String,
    val sizeText: String,
    val description: String,
    val downloadUrl: String,
    val officialSite: String
)

data class PlacementQuizResult(
    val score: Int,
    val totalQuestions: Int,
    val levelName: String,
    val levelTitle: String,
    val feedback: String,
    val recommendedTrackId: String,
    val recommendedCourseTitle: String
)
