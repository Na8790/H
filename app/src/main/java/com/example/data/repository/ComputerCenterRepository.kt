package com.example.data.repository

import com.example.data.local.ComputerCenterDao
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ComputerCenterRepository(private val dao: ComputerCenterDao) {

    val user: Flow<UserEntity?> = dao.getUser()
    val tracks: Flow<List<ITTrackEntity>> = dao.getAllTracks()
    val courses: Flow<List<CourseEntity>> = dao.getAllCourses()
    val placementQuestions: Flow<List<QuizQuestionEntity>> = dao.getPlacementQuestions()
    val enrollments: Flow<List<EnrollmentEntity>> = dao.getAllEnrollments()
    val certificates: Flow<List<CertificateEntity>> = dao.getAllCertificates()
    val tools: Flow<List<StudentToolEntity>> = dao.getAllTools()

    fun getLessonsForCourse(courseId: String): Flow<List<LessonEntity>> {
        return dao.getLessonsForCourse(courseId)
    }

    fun getQuizzesForTarget(targetId: String): Flow<List<QuizQuestionEntity>> {
        return dao.getQuizzesForTarget(targetId)
    }

    suspend fun markLessonCompleted(courseId: String, lessonId: String) = withContext(Dispatchers.IO) {
        dao.markLessonCompleted(lessonId)
        val course = dao.getCourseById(courseId).firstOrNull()
        if (course != null) {
            val lessons = dao.getLessonsForCourse(courseId).firstOrNull() ?: emptyList()
            val completedCount = lessons.count { it.isCompleted || it.id == lessonId }
            val total = if (course.totalLessons > 0) course.totalLessons else lessons.size.coerceAtLeast(1)
            val newProgress = ((completedCount.toFloat() / total) * 100).toInt().coerceIn(0, 100)
            val isNowCompleted = newProgress >= 100
            dao.updateCourse(
                course.copy(
                    progressPercent = newProgress,
                    isCompleted = isNowCompleted
                )
            )

            // If course completed, auto-issue certificate
            if (isNowCompleted) {
                val certId = "SU-CC-CERT-" + (1000..9999).random()
                val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                val today = dateFormat.format(Date())
                dao.insertCertificate(
                    CertificateEntity(
                        certificateId = certId,
                        studentName = "رغد حمود حسين العصري",
                        courseTitle = course.title,
                        trackName = "تكنولوجيا المعلومات",
                        issueDate = today,
                        finalScore = 95,
                        gradeLevel = "ممتاز (Excellent)",
                        supervisorName = "أ. عبدالله داعر / أ. امتياز الصمصام",
                        centerName = "مركز الحاسب الآلي - جامعة صنعاء"
                    )
                )
            }
        }
    }

    suspend fun registerEnrollment(
        course: CourseEntity,
        paymentMethod: String,
        referenceCode: String,
        amount: Int
    ): Boolean = withContext(Dispatchers.IO) {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        val today = dateFormat.format(Date())
        val enrollment = EnrollmentEntity(
            courseId = course.id,
            courseTitle = course.title,
            studentName = "رغد حمود حسين العصري",
            studentEmail = "raghad.alasri@univ-sanaa.edu.ye",
            enrollmentDate = today,
            paymentMethod = paymentMethod,
            paymentReference = if (referenceCode.isBlank()) "TXN-" + (100000..999999).random() else referenceCode,
            amountPaid = amount,
            status = "معتمد",
            scheduleDetails = "السبت والاثنين والأربعاء (4:00 - 6:00 مساءً)",
            labName = course.labRoom
        )
        dao.insertEnrollment(enrollment)
        dao.updateCourse(course.copy(isEnrolled = true, enrolledCount = course.enrolledCount + 1))
        true
    }

    suspend fun savePlacementResult(score: Int, levelName: String): PlacementQuizResult = withContext(Dispatchers.IO) {
        val feedback: String
        val recommendedTrackId: String
        val recommendedCourseTitle: String
        val levelTitle: String

        when {
            score >= 8 -> {
                levelTitle = "مستوى احترافي ومتقدم (Advanced)"
                feedback = "أداء استثنائي! تمتلك أسساً متينة في الخوارزميات وقواعد البيانات، ننصحك بالمسار المتقدم في تطوير النظم المتكاملة والذكاء الاصطناعي."
                recommendedTrackId = "track_ai"
                recommendedCourseTitle = "تطوير تطبيقات الويب وقواعد البيانات المتقدمة SQL Server"
            }
            score >= 5 -> {
                levelTitle = "مستوى متوسط (Intermediate)"
                feedback = "مستوى جيد جداً! لديك إلمام ممتاز بأساسيات تكنولوجيا المعلومات، ويمكنك الانطلاق مباشرة في مسار هندسة البرمجيات والشبكات."
                recommendedTrackId = "track_dev"
                recommendedCourseTitle = "برمجة تطبيقات الويب والواجهات التفاعلية JavaScript & React"
            }
            else -> {
                levelTitle = "مستوى تأسيسي (Foundational)"
                feedback = "بداية موفقة! ننصحك بالبدء بمسار أساسيات تكنولوجيا المعلومات وبناء المنطق البرمجي وقواعد البيانات التأسيسية."
                recommendedTrackId = "track_db"
                recommendedCourseTitle = "أساسيات تكنولوجيا المعلومات وأنظمة التشغيل وقواعد البيانات"
            }
        }

        // Update user's current level
        val currentUser = dao.getUser().firstOrNull()
        if (currentUser != null) {
            dao.updateUser(
                currentUser.copy(
                    currentLevel = levelTitle,
                    points = currentUser.points + (score * 20)
                )
            )
        }

        PlacementQuizResult(
            score = score,
            totalQuestions = 10,
            levelName = levelName,
            levelTitle = levelTitle,
            feedback = feedback,
            recommendedTrackId = recommendedTrackId,
            recommendedCourseTitle = recommendedCourseTitle
        )
    }

    suspend fun unlockAllDemoContent() = withContext(Dispatchers.IO) {
        // Enroll and complete demo courses and issue certificates
        val allCourses = dao.getAllCourses().firstOrNull() ?: emptyList()
        allCourses.forEach { course ->
            dao.updateCourse(
                course.copy(
                    isEnrolled = true,
                    isCompleted = true,
                    progressPercent = 100
                )
            )
            // Mark its lessons completed
            val lessons = dao.getLessonsForCourse(course.id).firstOrNull() ?: emptyList()
            lessons.forEach { lesson ->
                dao.markLessonCompleted(lesson.id)
            }
        }

        // Add sample demo certificates if not present
        val certs = listOf(
            CertificateEntity(
                certificateId = "SU-CC-IT-2026-089",
                studentName = "رغد حمود حسين العصري",
                courseTitle = "البرمجة بلغة Kotlin وتطوير تطبيقات أندرويد",
                trackName = "تطوير البرمجيات والتطبيقات",
                issueDate = "2026/08/15",
                finalScore = 98,
                gradeLevel = "ممتاز مرتفع (A+)",
                supervisorName = "أ. عبدالله داعر / م. نبيل العديني",
                centerName = "مركز الحاسب الآلي - جامعة صنعاء"
            ),
            CertificateEntity(
                certificateId = "SU-CC-NET-2026-112",
                studentName = "رغد حمود حسين العصري",
                courseTitle = "أساسيات وتصميم شبكات الحاسوب (CCNA)",
                trackName = "الشبكات والبنية التحتية",
                issueDate = "2026/07/20",
                finalScore = 94,
                gradeLevel = "ممتاز (Excellent)",
                supervisorName = "أ. امتياز الصمصام / م. وليد الشرعبي",
                centerName = "مركز الحاسب الآلي - جامعة صنعاء"
            ),
            CertificateEntity(
                certificateId = "SU-CC-SEC-2026-204",
                studentName = "رغد حمود حسين العصري",
                courseTitle = "الأمن السيبراني وحماية النظم والبيانات",
                trackName = "الأمن السيبراني وحماية المعلومات",
                issueDate = "2026/06/10",
                finalScore = 96,
                gradeLevel = "ممتاز (Excellent)",
                supervisorName = "د. علي الحاشدي / أ. عبدالله داعر",
                centerName = "مركز الحاسب الآلي - جامعة صنعاء"
            )
        )
        certs.forEach { dao.insertCertificate(it) }

        // Add sample enrollment
        val demoEnrollment = EnrollmentEntity(
            courseId = "c_ai_1",
            courseTitle = "الذكاء الاصطناعي وهندسة الأوامر (Prompt Engineering)",
            studentName = "رغد حمود حسين العصري",
            studentEmail = "raghad.alasri@univ-sanaa.edu.ye",
            enrollmentDate = "2026/09/01 10:00",
            paymentMethod = "بنك الكريمي (حساب المركز: 1204859)",
            paymentReference = "TXN-DEMO-9988",
            amountPaid = 35000,
            status = "معتمد (نشط حالياً)",
            scheduleDetails = "السبت والأربعاء (4:00 - 6:00 مساءً)",
            labName = "معمل الخوارزمي (مبنى المركز - الدور 2)"
        )
        dao.insertEnrollment(demoEnrollment)
    }

    suspend fun resetToDefaultData() = withContext(Dispatchers.IO) {
        seedInitialDataIfNeeded(forceReset = true)
    }

    suspend fun seedInitialDataIfNeeded(forceReset: Boolean = false) = withContext(Dispatchers.IO) {
        val existingUser = dao.getUser().firstOrNull()
        if (existingUser == null) {
            // Seed user
            dao.insertUser(
                UserEntity(
                    id = "student_raghad",
                    fullName = "رغد حمود حسين العصري",
                    studentId = "IT-2024-8841",
                    email = "raghad.alasri@univ-sanaa.edu.ye",
                    phone = "+967 777 123 456",
                    department = "مركز الحاسب الآلي - جامعة صنعاء (تكنولوجيا المعلومات)",
                    role = "طالبة دبلوم تكنولوجيا معلومات",
                    currentLevel = "مستوى متقدم (Level 4)",
                    points = 1450,
                    completedCoursesCount = 3,
                    enrolledCoursesCount = 2
                )
            )

            // Seed Tracks
            dao.insertTracks(
                listOf(
                    ITTrackEntity(
                        id = "track_dev",
                        title = "تطوير البرمجيات والويب (Software & Web)",
                        description = "مسار شامل لتصميم وبناء المواقع والأنظمة الحديثة باستخدام HTML5, CSS3, JavaScript ومكتبات الواجهات الحديثة.",
                        careerFuture = "مطور واجهات أمامية، مهندس برمجيات، مبرمج أنظمة ويب.",
                        requiredSkills = "المنطق البرمجي، هياكل البيانات، لغات الويب، واجهات RESTful API",
                        iconName = "Code",
                        colorHex = 0xFF1B4965
                    ),
                    ITTrackEntity(
                        id = "track_db",
                        title = "قواعد البيانات ونظم المعلومات (Databases & SQL)",
                        description = "إدارة وهندسة قواعد البيانات العلائقية SQL Server و MySQL وتصميم المخططات وتطوير الإجراءات المخزنة.",
                        careerFuture = "مدير قواعد بيانات (DBA)، محلل نظم، مهندس معمارية بيانات.",
                        requiredSkills = "SQL Queries, ERD Design, Stored Procedures, Normalization, Backup & Recovery",
                        iconName = "Storage",
                        colorHex = 0xFF0F2B48
                    ),
                    ITTrackEntity(
                        id = "track_net",
                        title = "شبكات الحاسوب وهندسة المخدمات (Networks & Servers)",
                        description = "تثبيت وضبط الشبكات المحلية LAN، المخدمات Windows Server، وإعداد أجهزة التوجيه وبروتوكولات التوجيه.",
                        careerFuture = "مهندس شبكات، مدير أنظمة ومخدمات، مسؤول دعم فني متقدم.",
                        requiredSkills = "TCP/IP, Routing & Switching, Subnetting, Server Administration, Packet Tracer",
                        iconName = "Router",
                        colorHex = 0xFF006699
                    ),
                    ITTrackEntity(
                        id = "track_sec",
                        title = "الأمن السيبراني وحماية النظم (Cybersecurity)",
                        description = "تأمين الشبكات والبرمجيات، تحليل الثغرات، التشفير، وحماية قواعد البيانات من الاختراق وهجمات SQL Injection.",
                        careerFuture = "مختبر اختراق أخلاقي، محلل أمني SOC، مسؤول أمن معلومات.",
                        requiredSkills = "Cryptography, Network Security, Pen-testing, Threat Analysis",
                        iconName = "Security",
                        colorHex = 0xFF00796B
                    ),
                    ITTrackEntity(
                        id = "track_ai",
                        title = "الذكاء الاصطناعي وعلوم البيانات (AI & Data Science)",
                        description = "أساسيات لغة Python وتحليل البيانات، بناء النماذج الذكية واستخدام أدوات معالجة اللغات الطبيعية.",
                        careerFuture = "عالم بيانات، مطور ذكاء اصطناعي، محلل ذكاء الأعمال BI.",
                        requiredSkills = "Python, Pandas, NumPy, Machine Learning, Statistical Analysis",
                        iconName = "Psychology",
                        colorHex = 0xFF7B1FA2
                    )
                )
            )

            // Seed Courses
            dao.insertCourses(
                listOf(
                    CourseEntity(
                        id = "course_it_intro",
                        title = "أساسيات تكنولوجيا المعلومات وأنظمة التشغيل",
                        trackId = "track_dev",
                        instructor = "أ. عبدالله داعر",
                        durationHours = 35,
                        level = "مبتدئ",
                        isFree = true,
                        priceYemenRials = 0,
                        rating = 4.9f,
                        totalLessons = 4,
                        enrolledCount = 240,
                        description = "مقدمة شاملة في معمارية الحاسوب، نظم تشغيل Windows و Linux، مبادئ نقل البيانات، وكيفية تنظيم الملفات ومحركات البرمجة.",
                        prerequisites = "لا توجد متطلبات سابقة",
                        labRoom = "مختبر الحاسوب 1 (مبنى المركز)",
                        isEnrolled = true,
                        isCompleted = true,
                        progressPercent = 100
                    ),
                    CourseEntity(
                        id = "course_sql_server",
                        title = "إدارة وتصميم قواعد البيانات SQL Server",
                        trackId = "track_db",
                        instructor = "أ. امتياز الصمصام",
                        durationHours = 50,
                        level = "متوسط",
                        isFree = false,
                        priceYemenRials = 25000,
                        rating = 4.95f,
                        totalLessons = 4,
                        enrolledCount = 185,
                        description = "دورة احترافية تغطي تصميم المخططات العلائقية ERD، كتابة استعلامات SQL متقدمة، إنشاء الجداول والمفاتيح، والفهارس والإجراءات المخزنة Stored Procedures.",
                        prerequisites = "أساسيات تكنولوجيا المعلومات",
                        labRoom = "مختبر قواعد البيانات (القاعة 3)",
                        isEnrolled = true,
                        isCompleted = false,
                        progressPercent = 50
                    ),
                    CourseEntity(
                        id = "course_web_dev",
                        title = "تطوير تطبيقات الويب التفاعلية (HTML5, CSS3, JS)",
                        trackId = "track_dev",
                        instructor = "أ. عبدالله داعر",
                        durationHours = 60,
                        level = "متوسط",
                        isFree = false,
                        priceYemenRials = 30000,
                        rating = 4.88f,
                        totalLessons = 4,
                        enrolledCount = 310,
                        description = "بناء واجهات مستخدم متجاوبة وحديثة باستخدام HTML5 Semantic Elements، تنسيقات CSS3 المتقدمة Flexbox & Grid، والبرمجة بلغة JavaScript لإدارة الأحداث والـ DOM.",
                        prerequisites = "أساسيات الحاسوب والمنطق البرمجي",
                        labRoom = "مختبر البرمجيات 2",
                        isEnrolled = false,
                        isCompleted = false,
                        progressPercent = 0
                    ),
                    CourseEntity(
                        id = "course_networking_ccna",
                        title = "شبكات الحاسوب والربط الشبكي CCNA Basics",
                        trackId = "track_net",
                        instructor = "م. محمد الصنعاني",
                        durationHours = 45,
                        level = "متوسط",
                        isFree = false,
                        priceYemenRials = 28000,
                        rating = 4.75f,
                        totalLessons = 3,
                        enrolledCount = 140,
                        description = "فهم نموذج OSI و TCP/IP، تقسيم الشبكات وتوزيع عناوين IP Subnetting، وإعداد أجهزة السويتش والراوتر على برنامج Cisco Packet Tracer.",
                        prerequisites = "مفاهيم تقنية المعلومات الأساسية",
                        labRoom = "مختبر الشبكات (القاعة 4)",
                        isEnrolled = false,
                        isCompleted = false,
                        progressPercent = 0
                    ),
                    CourseEntity(
                        id = "course_python_prog",
                        title = "البرمجة بلغة بايثون والتحليل المنطقي",
                        trackId = "track_ai",
                        instructor = "د. أنور الحكيمي",
                        durationHours = 40,
                        level = "مبتدئ إلى متوسط",
                        isFree = true,
                        priceYemenRials = 0,
                        rating = 4.92f,
                        totalLessons = 3,
                        enrolledCount = 420,
                        description = "تعلم لغة Python من الصفر: المتغيرات، الدوال، الكائنات OOP، التعامل مع الملفات، وتطبيقات الخوارزميات في حل المشكلات التقنية.",
                        prerequisites = "رغبة في تعلم البرمجة",
                        labRoom = "مختبر الحاسوب 1",
                        isEnrolled = false,
                        isCompleted = false,
                        progressPercent = 0
                    )
                )
            )

            // Seed Lessons
            dao.insertLessons(
                listOf(
                    // IT Intro Lessons
                    LessonEntity(
                        id = "les_it_1",
                        courseId = "course_it_intro",
                        orderIndex = 1,
                        title = "مقدمة في تكنولوجيا المعلومات ومكونات الحاسوب",
                        durationMinutes = 25,
                        summary = "التعرف على البنية العتادية (Hardware) والبرمجية (Software) ووحدات المعالجة المركزية والذواكر.",
                        fullContent = """
                            تُعد تكنولوجيا المعلومات (Information Technology) عصب التطور الرقمي الحديث في الجامعات والمؤسسات.
                            
                            1. مكونات الحاسوب الأساسية:
                            - وحدة المعالجة المركزية (CPU): العقل المدبر لتنفيذ الأوامر والعمليات الحسابية والمنطقية.
                            - الذاكرة العشوائية (RAM): ذاكرة الوصول السريع المؤقتة لتخزين البرامج قيد التشغيل.
                            - وحدة التخزين الدائم (SSD / HDD): للاحتفاظ بنظام التشغيل وملفات المستخدم.
                            - لوحة الأم (Motherboard): حلقة الوصل بين جميع أجزاء الحاسب.
                            
                            2. البرمجيات وأنظمة التشغيل:
                            يعمل نظام التشغيل كطبقة وسيطة بين المستخدم والعتاد (Hardware)، حيث يدير الموارد ويشرف على تشغيل التطبيقات وواجهات المستخدم.
                        """.trimIndent(),
                        keyTerms = "CPU, RAM, Motherboard, Operating System, ROM, Hardware vs Software",
                        codeSnippet = "// تمثيل برمجي لمواصفات جهاز:\nconst systemSpec = {\n  cpu: 'Intel Core i7 12th Gen',\n  ram: '16GB DDR4',\n  storage: '512GB NVMe SSD',\n  os: 'Windows 10 / Linux Ubuntu'\n};",
                        isCompleted = true
                    ),
                    LessonEntity(
                        id = "les_it_2",
                        courseId = "course_it_intro",
                        orderIndex = 2,
                        title = "أنظمة العد والتمثيل الثنائي (Binary System)",
                        durationMinutes = 30,
                        summary = "فهم النظام الثنائي (0 و 1)، النظام الست عشري Hexadecimal، وكيف يخزن الحاسوب البيانات والرموز.",
                        fullContent = """
                            يعتمد الحاسوب الآلي رقمياً على حالتين كهربائيتين: التشغيل (1) والإيقاف (0)، وتسمى أصغر وحدة تخزين (Bit).
                            
                            - البايت (Byte) = 8 بت، ويكفي لتمثيل حرف واحد في جدول ASCII.
                            - الكيلوبايت (KB) = 1024 بايت.
                            - الميجابايت (MB) = 1024 كيلوبايت.
                            - الجيجابايت (GB) = 1024 ميجابايت.
                            
                            التحويل بين الأنظمة:
                            العدد العشري 13 = في النظام الثنائي 1101 (8 + 4 + 0 + 1).
                        """.trimIndent(),
                        keyTerms = "Bit, Byte, Binary, Hexadecimal, ASCII, UTF-8",
                        codeSnippet = "# تحويل رقم إلى ثنائي في بايثون:\nnumber = 13\nbinary_str = bin(number)\nprint(f'العدد {number} بالثنائي هو: {binary_str}')  # 0b1101",
                        isCompleted = true
                    ),
                    LessonEntity(
                        id = "les_it_3",
                        courseId = "course_it_intro",
                        orderIndex = 3,
                        title = "مبادئ أمن المعلومات وحماية البيانات الشخصية",
                        durationMinutes = 20,
                        summary = "أساسيات جدار الحماية، برامج مكافحة الفيروسات، وتأمين كلمات المرور والنسخ الاحتياطي.",
                        fullContent = """
                            يُقصد بأمن المعلومات الحفاظ على سرية البيانات (Confidentiality)، وسلامتها (Integrity)، وتوفرها الدائم (Availability) - ما يُعرف بثالوث CIA.
                            
                            أفضل الممارسات:
                            - تفعيل المصادقة الثنائية (2FA).
                            - تجنب فتح الروابط المشبوهة (Phishing).
                            - الاحتفاظ بنسخ احتياطية دورية (Backup) على وسائط تخزين خارجية أو سحابية مشفرة.
                        """.trimIndent(),
                        keyTerms = "CIA Triad, Phishing, Malware, 2FA, Backup, Firewall",
                        codeSnippet = "// مثال فحص قوة كلمة المرور:\nfunction isPasswordStrong(pwd) {\n  return pwd.length >= 8 && /[A-Z]/.test(pwd) && /[0-9]/.test(pwd) && /[^A-Za-z0-9]/.test(pwd);\n}",
                        isCompleted = true
                    ),
                    LessonEntity(
                        id = "les_it_4",
                        courseId = "course_it_intro",
                        orderIndex = 4,
                        title = "التوجيه المهني والتخصصات في مركز الحاسب الآلي",
                        durationMinutes = 25,
                        summary = "استعراض مسارات الدبلوم والبكالوريوس في جامعة صنعاء، وتحديد المهارات الملائمة لسوق العمل.",
                        fullContent = """
                            يوفر مركز الحاسب الآلي بجامعة صنعاء تخصصات تقنية متميزة تلبي حاجة السوق المحلي والإقليمي:
                            
                            1. تكنولوجيا المعلومات (Information Technology): دمج البرمجيات، قواعد البيانات، والشبكات لبناء حلول مؤسسية متكاملة.
                            2. علوم الحاسوب (Computer Science): التركيز على الخوارزميات، الذكاء الاصطناعي، وهندسة البرمجيات المعقدة.
                            3. نظم المعلومات الإدارية (MIS): حلقة الوصل بين إدارة الأعمال والأنظمة الحاسوبية.
                        """.trimIndent(),
                        keyTerms = "Career Roadmap, IT Specializations, Sana'a University, Industry Skills",
                        codeSnippet = "// مركز الحاسب الآلي - جامعة صنعاء:\nconsole.log('مرحباً بك في تخصص تكنولوجيا المعلومات!');",
                        isCompleted = true
                    ),

                    // SQL Server Lessons
                    LessonEntity(
                        id = "les_sql_1",
                        courseId = "course_sql_server",
                        orderIndex = 1,
                        title = "مقدمة في قواعد البيانات العلائقية ومحرك SQL Server",
                        durationMinutes = 35,
                        summary = "التعرف على بيئة SQL Server Management Studio (SSMS)، الجداول، المفاتيح الأساسية والخارجية.",
                        fullContent = """
                            تعتبر قاعدة البيانات مستودعاً منظماً للبيانات يتيح استرجاعها وتحديثها بسرعة ودقة عالية.
                            
                            المفاهيم الأساسية:
                            - الجدول (Table): يتكون من صفوف (Rows / Records) وأعمدة (Columns / Attributes).
                            - المفتاح الأساسي (Primary Key - PK): قيمة فريدة تميز كل سجل داخل الجدول ولا تقبل التكرار أو القيمة الفارغة (NULL).
                            - المفتاح الخارجي (Foreign Key - FK): حقل يربط جدولاً بجدول آخر لضمان التكامل المرجعي.
                        """.trimIndent(),
                        keyTerms = "SSMS, Table, Primary Key, Foreign Key, Relational Database",
                        codeSnippet = "-- إنشاء قاعدة بيانات وجدول الطلاب:\nCREATE DATABASE SanaaCenterDB;\nGO\nUSE SanaaCenterDB;\n\nCREATE TABLE Students (\n    StudentID INT PRIMARY KEY IDENTITY(1,1),\n    FullName NVARCHAR(100) NOT NULL,\n    Email VARCHAR(100) UNIQUE,\n    LevelName NVARCHAR(50),\n    EnrollmentDate DATETIME DEFAULT GETDATE()\n);",
                        isCompleted = true
                    ),
                    LessonEntity(
                        id = "les_sql_2",
                        courseId = "course_sql_server",
                        orderIndex = 2,
                        title = "استعلامات الاسترجاع والتصفية (SELECT, WHERE, ORDER BY)",
                        durationMinutes = 40,
                        summary = "كتابة استعلامات SQL لاستخراج بيانات محددة مع استخدام الشروط والترتيب والتجميع.",
                        fullContent = """
                            أمر SELECT هو اللبنة الأساسية لاسترجاع البيانات من قاعدة البيانات.
                            
                            الكلمات المفتاحية الأساسية:
                            - WHERE: لتطبيق الشروط والتصفية.
                            - ORDER BY: لترتيب النتائج تصاعدياً (ASC) أو تنازلياً (DESC).
                            - LIKE: للبحث الجزئي في النصوص باستخدام الرمز %.
                            - GROUP BY & HAVING: لتجميع البيانات وتطبيق الشروط على نتائج التجميع.
                        """.trimIndent(),
                        keyTerms = "SELECT, WHERE, ORDER BY, LIKE, GROUP BY, Aggregate Functions",
                        codeSnippet = "-- استرجاع الطلاب المتفوقين مرتبين تنازلياً:\nSELECT FullName, Email, LevelName, Points\nFROM Students\nWHERE Points >= 1000\nORDER BY Points DESC;",
                        isCompleted = true
                    ),
                    LessonEntity(
                        id = "les_sql_3",
                        courseId = "course_sql_server",
                        orderIndex = 3,
                        title = "الربط بين الجداول (INNER JOIN & OUTER JOINS)",
                        durationMinutes = 45,
                        summary = "دمج البيانات من جداول متعددة مرتبطة بعلاقات One-to-Many و Many-to-Many.",
                        fullContent = """
                            في قواعد البيانات العلائقية، تُقسّم البيانات على جداول مترابطة لتجنب التكرار والتكرارية (Redundancy).
                            
                            أنواع الربط:
                            1. INNER JOIN: يعيد فقط السجلات التي تتطابق قيمها في كلا الجدولين.
                            2. LEFT JOIN: يعيد جميع سجلات الجدول الأيسر مع السجلات المطابقة من الجدول الأيمن.
                            3. RIGHT JOIN: يعيد جميع سجلات الجدول الأيمن.
                            4. FULL JOIN: يعيد كافة السجلات عند وجود تطابق في أي من الطرفين.
                        """.trimIndent(),
                        keyTerms = "INNER JOIN, LEFT JOIN, Foreign Key Relationship, Normalization",
                        codeSnippet = "-- استعلام يعرض اسم الطالب مع أسماء الدورات التي التحق بها:\nSELECT S.FullName, C.CourseTitle, E.EnrollmentDate, E.PaymentStatus\nFROM Students S\nINNER JOIN Enrollments E ON S.StudentID = E.StudentID\nINNER JOIN Courses C ON E.CourseID = C.CourseID;",
                        isCompleted = false
                    ),
                    LessonEntity(
                        id = "les_sql_4",
                        courseId = "course_sql_server",
                        orderIndex = 4,
                        title = "الإجراءات المخزنة والمعاملات (Stored Procedures & Transactions)",
                        durationMinutes = 45,
                        summary = "أتمتة العمليات المتكررة، حماية قاعدة البيانات من ثغرات الحقن، وإدارة عمليات ACID.",
                        fullContent = """
                            الإجراء المخزن (Stored Procedure) هو مجموعة أوامر SQL تم تجميعها وتخزينها في السيرفر لزيادة الأداء والأمان.
                            
                            الفوائد:
                            - تحسين الأداء عبر Pre-compiled Execution Plans.
                            - الحماية التامة من ثغرات SQL Injection.
                            - إدارة المعاملات المالية (Transactions) لضمان تنفيذ كل العمليات أو إلغائها معاً (Commit / Rollback).
                        """.trimIndent(),
                        keyTerms = "Stored Procedure, ACID, SQL Injection, COMMIT, ROLLBACK",
                        codeSnippet = "-- إنشاء إجراء مخزن لتسجيل اشتراك طالب جديد:\nCREATE PROCEDURE sp_EnrollStudent\n    @StudentID INT,\n    @CourseID INT,\n    @Amount DECIMAL(10,2)\nAS\nBEGIN\n    SET NOCOUNT ON;\n    BEGIN TRANSACTION;\n    BEGIN TRY\n        INSERT INTO Enrollments (StudentID, CourseID, AmountPaid, Status)\n        VALUES (@StudentID, @CourseID, @Amount, N'معتمد');\n        COMMIT TRANSACTION;\n    END TRY\n    BEGIN CATCH\n        ROLLBACK TRANSACTION;\n        THROW;\n    END CATCH\nEND;",
                        isCompleted = false
                    ),

                    // Web Dev Lessons
                    LessonEntity(
                        id = "les_web_1",
                        courseId = "course_web_dev",
                        orderIndex = 1,
                        title = "هيكلة صفحات الويب الدلالية HTML5 Semantic",
                        durationMinutes = 30,
                        summary = "استخدام عناصر HTML5 الحديثة لبناء بنية نظيفة ومتوافقة مع محركات البحث ومعايير الوصول.",
                        fullContent = """
                            تعتبر HTML5 اللغة الأساسية لهيكلة محتوى صفحات الإنترنت.
                            
                            العناصر الدلالية الحديثة:
                            - <header>: الترويسة الرئيسية واللوجو.
                            - <nav>: روابط التنقل والقوائم.
                            - <main>: المحتوى الفريد والأساسي للصفحة.
                            - <section> و <article>: لتنظيم الأقسام والمقالات المستقلة.
                            - <footer>: التذييل وروابط التواصل وحقوق الملكية.
                        """.trimIndent(),
                        keyTerms = "Semantic HTML, Header, Nav, Main, Footer, Accessibility",
                        codeSnippet = "<!DOCTYPE html>\n<html lang=\"ar\" dir=\"rtl\">\n<head>\n  <meta charset=\"UTF-8\">\n  <title>مركز الحاسب الآلي - جامعة صنعاء</title>\n</head>\n<body>\n  <header>\n    <h1>منصة تكنولوجيا المعلومات</h1>\n  </header>\n</body>\n</html>",
                        isCompleted = false
                    ),
                    LessonEntity(
                        id = "les_web_2",
                        courseId = "course_web_dev",
                        orderIndex = 2,
                        title = "تنسيقات CSS3 الحديثة و Flexbox و CSS Grid",
                        durationMinutes = 40,
                        summary = "تصميم واجهات متجاوبة مع كافة الشاشات والأجهزة الذكية باستخدام النماذج الحديثة.",
                        fullContent = """
                            تتحكم لغة CSS3 في المظهر الجمالي والألوان وتوزيع العناصر على الشاشة.
                            
                            نموذج Flexbox: ممتاز لتوزيع العناصر في بعد واحد (صف أو عمود)، مع التحكم السهل بالمحاذاة والمسافات.
                            نموذج CSS Grid: يوفر شبكة ثنائية الأبعاد (صفوف وأعمدة) للتحكم الدقيق في التخطيطات المعقدة.
                        """.trimIndent(),
                        keyTerms = "CSS3, Flexbox, Grid, Responsive Design, Media Queries",
                        codeSnippet = "/* تنسيق كارت الدورة التعليمية */\n.course-card {\n  display: flex;\n  flex-direction: column;\n  padding: 1.5rem;\n  border-radius: 12px;\n  background: #ffffff;\n  box-shadow: 0 4px 12px rgba(0,0,0,0.08);\n}",
                        isCompleted = false
                    ),
                    LessonEntity(
                        id = "les_web_3",
                        courseId = "course_web_dev",
                        orderIndex = 3,
                        title = "البرمجة التفاعلية وإدارة الأحداث JavaScript DOM",
                        durationMinutes = 45,
                        summary = "التحكم في عناصر الصفحة، التفاعل مع نقرات المستخدم، وإرسال واستقبال بيانات JSON.",
                        fullContent = """
                            لغة JavaScript تمنح الصفحات الحيوية والتفاعل مع المستخدمين دون الحاجة لإعادة تحميل الصفحة.
                            
                            المفاهيم الأساسية:
                            - DOM (Document Object Model): شجرة عناصر الصفحة التي يمكن تعديلها برمجياً.
                            - Event Listeners: الاستماع لنقرات الأزرار، إدخال النصوص، وإرسال النماذج.
                            - Fetch API: طلب البيانات والدروس من السيرفر بصيغة JSON غير متزامنة (Async/Await).
                        """.trimIndent(),
                        keyTerms = "JavaScript, DOM, Event Listener, Fetch API, JSON, Async/Await",
                        codeSnippet = "// إرسال إجابة اختبار الدرس عبر Fetch API:\nasync function submitQuizAnswer(lessonId, selectedOption) {\n  const response = await fetch('/api/submit-quiz', {\n    method: 'POST',\n    headers: { 'Content-Type': 'application/json' },\n    body: JSON.stringify({ lessonId, selectedOption })\n  });\n  const result = await response.json();\n  console.log('نتيجة التقييم:', result);\n}",
                        isCompleted = false
                    ),
                    LessonEntity(
                        id = "les_web_4",
                        courseId = "course_web_dev",
                        orderIndex = 4,
                        title = "ربط الواجهات بقواعد البيانات وإنشاء لوحة التحكم Dashboard",
                        durationMinutes = 50,
                        summary = "بناء لوحة إدارة الدروس والطلاب، وتطبيق معايير الأمان والتحقق من صحة المدخلات.",
                        fullContent = """
                            في هذا الدرس الختامي يتم تجميع كل ما تم تعلمه لإنشاء لوحة تحكم كاملة للمركز التعليمي:
                            - نموذج إدخال وإضافة درس جديد.
                            - عرض جدول الطلاب والدرجات المسجلة في SQL Server.
                            - التحقق من صحة النماذج Form Validation من جهة العميل والخادم.
                        """.trimIndent(),
                        keyTerms = "Dashboard, Full-stack integration, Form Validation, REST API",
                        codeSnippet = "// التحقق من رقم الإشعار البنكي:\nfunction validatePayment(receiptCode, amount) {\n  if (!receiptCode || receiptCode.length < 5) {\n    throw new Error('رقم الإشعار البنكي غير صحيح');\n  }\n  return { valid: true, status: 'قيد المراجعة' };\n}",
                        isCompleted = false
                    )
                )
            )

            // Seed Quizzes for Placement & Lessons
            dao.insertQuizzes(
                listOf(
                    // Placement Test Questions (10 Comprehensive questions)
                    QuizQuestionEntity(
                        id = "place_q1",
                        targetType = "PLACEMENT",
                        targetId = "placement_test",
                        questionText = "ما هي الوحدة المسؤولة عن تنفيذ الأوامر والعمليات الحسابية والمنطقية في جهاز الحاسوب؟",
                        optionA = "الذاكرة العشوائية RAM",
                        optionB = "وحدة المعالجة المركزية CPU",
                        optionC = "القرص الصلب Hard Disk",
                        optionD = "اللوحة الأم Motherboard",
                        correctOptionIndex = 1,
                        explanation = "وحدة المعالجة المركزية (CPU) هي العقل المدبر للحاسب والمسؤولة عن معالجة كافة العمليات."
                    ),
                    QuizQuestionEntity(
                        id = "place_q2",
                        targetType = "PLACEMENT",
                        targetId = "placement_test",
                        questionText = "في قواعد البيانات العلائقية (Relational Databases)، ما هو المفتاح الأساسي (Primary Key)؟",
                        optionA = "حقل يمكن أن تتكرر قيمته في عدة سجلات",
                        optionB = "حقل فريد يميز كل سجل ولا يقبل القيمة الفارغة NULL",
                        optionC = "حقل يستخدم فقط لتخزين كلمات المرور المشفرة",
                        optionD = "جدول كامل يحتوي على نسخ احتياطية",
                        correctOptionIndex = 1,
                        explanation = "المفتاح الأساسي هو معرّف وحيد لكل صف داخل الجدول ولا يقبل التكرار أو القيمة الفارغة."
                    ),
                    QuizQuestionEntity(
                        id = "place_q3",
                        targetType = "PLACEMENT",
                        targetId = "placement_test",
                        questionText = "أي من أوامر SQL التالية يُستخدم لاستخراج واسترجاع البيانات من الجداول؟",
                        optionA = "UPDATE",
                        optionB = "INSERT INTO",
                        optionC = "SELECT",
                        optionD = "DELETE",
                        correctOptionIndex = 2,
                        explanation = "يستخدم الأمر SELECT لاسترجاع وتحديد الأعمدة والصفوف المطلوبة من الجداول."
                    ),
                    QuizQuestionEntity(
                        id = "place_q4",
                        targetType = "PLACEMENT",
                        targetId = "placement_test",
                        questionText = "ما هي لغة البرمجة التي تُستخدم لجعل صفحات الويب تفاعلية وتتعامل مع أحداث المستخدم في المتصفح؟",
                        optionA = "HTML",
                        optionB = "CSS",
                        optionC = "JavaScript",
                        optionD = "SQL",
                        correctOptionIndex = 2,
                        explanation = "لغة JavaScript هي المسؤولة عن البرمجة التفاعلية وإدارة الأحداث في متصفحات الويب."
                    ),
                    QuizQuestionEntity(
                        id = "place_q5",
                        targetType = "PLACEMENT",
                        targetId = "placement_test",
                        questionText = "في شبكات الحاسوب، ما هو البروتوكول المسؤول عن توزيع عناوين IP تلقائياً على الأجهزة؟",
                        optionA = "DNS",
                        optionB = "DHCP",
                        optionC = "HTTP",
                        optionD = "FTP",
                        correctOptionIndex = 1,
                        explanation = "بروتوكول DHCP (Dynamic Host Configuration Protocol) يوزع عناوين IP وإعدادات الشبكة تلقائياً."
                    ),
                    QuizQuestionEntity(
                        id = "place_q6",
                        targetType = "PLACEMENT",
                        targetId = "placement_test",
                        questionText = "ما هو العدد الذي يمثله الرقم الثنائي 1010 في النظام العشري (Decimal)؟",
                        optionA = "8",
                        optionB = "10",
                        optionC = "12",
                        optionD = "14",
                        correctOptionIndex = 1,
                        explanation = "1010 بالثنائي = (1*8) + (0*4) + (1*2) + (0*1) = 10."
                    ),
                    QuizQuestionEntity(
                        id = "place_q7",
                        targetType = "PLACEMENT",
                        targetId = "placement_test",
                        questionText = "ما هو مفهوم ثالوث أمن المعلومات (CIA Triad)؟",
                        optionA = "السرية (Confidentiality)، السلامة (Integrity)، التوفر (Availability)",
                        optionB = "الكاميرا، الإنترنت، الأداء",
                        optionC = "التشفير، النسخ الاحتياطي، الحذف",
                        optionD = "قواعد البيانات، السيرفرات، المختبرات",
                        correctOptionIndex = 0,
                        explanation = "ثالوث أمن المعلومات CIA يمثل الركائز الثلاث الأساسية: السرية، السلامة والنزاهة، والتوفر."
                    ),
                    QuizQuestionEntity(
                        id = "place_q8",
                        targetType = "PLACEMENT",
                        targetId = "placement_test",
                        questionText = "في تصميم قواعد البيانات، ماذا يُقصد بعملية التسوية (Normalization)؟",
                        optionA = "حذف البيانات القديمة تلقائياً",
                        optionB = "تنظيم الجداول لتقليل التكرار ومنع تضارب البيانات",
                        optionC = "زيادة حجم قاعدة البيانات لتسريع البحث",
                        optionD = "تحويل قاعدة البيانات إلى ملف نصي",
                        correctOptionIndex = 1,
                        explanation = "الـ Normalization تهدف إلى تقسيم الجداول وتنظيم العلاقات لمنع تكرار البيانات وضمان اتساقها."
                    ),
                    QuizQuestionEntity(
                        id = "place_q9",
                        targetType = "PLACEMENT",
                        targetId = "placement_test",
                        questionText = "ما هي الوسيلة الأكثر أماناً لحماية تطبيقات الويب وقواعد البيانات من ثغرات حقن SQL (SQL Injection)؟",
                        optionA = "إخفاء شاشات تسجيل الدخول",
                        optionB = "استخدام الاستعلامات المعلمة (Parameterized Queries) والإجراءات المخزنة",
                        optionC = "زيادة عدد المستخدمين",
                        optionD = "استخدام لغة CSS فقط",
                        correctOptionIndex = 1,
                        explanation = "الاستعلامات المعلمة Parameterized Queries تعزل المدخلات عن أوامر SQL مما يمنع ثغرات الحقن."
                    ),
                    QuizQuestionEntity(
                        id = "place_q10",
                        targetType = "PLACEMENT",
                        targetId = "placement_test",
                        questionText = "ما هي منهجية التطوير التكرارية المرنة (Iterative Agile) المعتمدة في بناء هذا النظام؟",
                        optionA = "تطوير النظام كدفعة واحدة في نهاية العام بدون مراجعات",
                        optionB = "تقسيم المشروع لدورات زمنية قصيرة قابلة للاختبار والتحسين المستمر بناءً على الملاحظات",
                        optionC = "الاعتماد الكامل على البرامج الجاهزة دون كتابة كود",
                        optionD = "إلغاء مرحلة التحليل والتصميم",
                        correctOptionIndex = 1,
                        explanation = "منهجية Agile التكرارية تبني البرمجيات عبر دورات متكررة وسريعة مع اختبار وتطوير مستمر."
                    ),

                    // Lesson 1 Quiz
                    QuizQuestionEntity(
                        id = "les_q1_1",
                        targetType = "LESSON",
                        targetId = "les_it_1",
                        questionText = "ما هو المكون الذي يُطلق عليه 'العقل المدبر' للحاسوب ويقوم بتنفيذ التعليمات البرمجية؟",
                        optionA = "الذاكرة RAM",
                        optionB = "المعالج CPU",
                        optionC = "القرص SSD",
                        optionD = "مزود الطاقة",
                        correctOptionIndex = 1,
                        explanation = "وحدة المعالجة المركزية CPU تعالج التعليمات وتقوم بالعمليات الحسابية والمنطقية."
                    ),
                    QuizQuestionEntity(
                        id = "les_q1_2",
                        targetType = "LESSON",
                        targetId = "les_it_1",
                        questionText = "ما الفرق الجوهري بين الذاكرة RAM والقرص الصلب؟",
                        optionA = "RAM ذاكرة مؤقتة تفقد بياناتها بانقطاع الكهرباء، والقرص الصلب دائم",
                        optionB = "RAM لتخزين الفيديوهات والقرص للمعالج",
                        optionC = "لا يوجد فرق بينهما",
                        optionD = "القرص الصلب أسرع بكثير من الـ RAM",
                        correctOptionIndex = 0,
                        explanation = "الذاكرة العشوائية RAM متطايرة ومؤقتة، بينما وسائط التخزين SSD/HDD تحتفظ بالبيانات بصورة دائمة."
                    ),

                    // Lesson SQL 1 Quiz
                    QuizQuestionEntity(
                        id = "les_sql_q1",
                        targetType = "LESSON",
                        targetId = "les_sql_1",
                        questionText = "ما هي الخاصية المميزة للمفتاح الأساسي Primary Key في جداول SQL Server؟",
                        optionA = "يقبل القيم المكررة",
                        optionB = "فريد لكل سجل ولا يقبل القيمة الفارغة NULL",
                        optionC = "يتم توليده دائماً كنص عشوائي",
                        optionD = "يستخدم فقط في التقارير المطبوعة",
                        correctOptionIndex = 1,
                        explanation = "المفتاح الأساسي فريد بطبيعته ولا يمكن أن يحتوي على قيمة فارغة NULL."
                    ),
                    QuizQuestionEntity(
                        id = "les_sql_q2",
                        targetType = "LESSON",
                        targetId = "les_sql_1",
                        questionText = "أي أداة توفر واجهة رسومية لإدارة ومراقبة قواعد بيانات SQL Server؟",
                        optionA = "Photoshop",
                        optionB = "SSMS (SQL Server Management Studio)",
                        optionC = "Packet Tracer",
                        optionD = "MS Word",
                        correctOptionIndex = 1,
                        explanation = "برنامج SSMS هو البيئة الرسمية الشاملة لإدارة مخدمات وقواعد بيانات SQL Server."
                    )
                )
            )

            // Seed Enrollments
            dao.insertEnrollment(
                EnrollmentEntity(
                    courseId = "course_it_intro",
                    courseTitle = "أساسيات تكنولوجيا المعلومات وأنظمة التشغيل",
                    studentName = "رغد حمود حسين العصري",
                    studentEmail = "raghad.alasri@univ-sanaa.edu.ye",
                    enrollmentDate = "2024/02/10 09:30",
                    paymentMethod = "مجاني (منحة مركز الحاسب)",
                    paymentReference = "SCHOLAR-2024-SU",
                    amountPaid = 0,
                    status = "مكتمل",
                    scheduleDetails = "الأحد والثلاثاء (8:00 - 10:00 صباحاً)",
                    labName = "مختبر الحاسوب 1"
                )
            )
            dao.insertEnrollment(
                EnrollmentEntity(
                    courseId = "course_sql_server",
                    courseTitle = "إدارة وتصميم قواعد البيانات SQL Server",
                    studentName = "رغد حمود حسين العصري",
                    studentEmail = "raghad.alasri@univ-sanaa.edu.ye",
                    enrollmentDate = "2024/04/15 11:20",
                    paymentMethod = "بنك الكريمي (حساب المركز)",
                    paymentReference = "KUR-9842105",
                    amountPaid = 25000,
                    status = "معتمد",
                    scheduleDetails = "السبت والاثنين والأربعاء (4:00 - 6:00 مساءً)",
                    labName = "مختبر قواعد البيانات (القاعة 3)"
                )
            )

            // Seed Certificates
            dao.insertCertificate(
                CertificateEntity(
                    certificateId = "SU-CC-2024-8841",
                    studentName = "رغد حمود حسين العصري",
                    courseTitle = "أساسيات تكنولوجيا المعلومات وأنظمة التشغيل",
                    trackName = "تكنولوجيا المعلومات",
                    issueDate = "2024/03/01",
                    finalScore = 98,
                    gradeLevel = "ممتاز مرتفع (High Distinction)",
                    supervisorName = "أ. عبدالله داعر / أ. امتياز الصمصام",
                    centerName = "مركز الحاسب الآلي - جامعة صنعاء"
                )
            )

            // Seed Student Kit Tools
            dao.insertTools(
                listOf(
                    StudentToolEntity(
                        id = "tool_sql",
                        title = "Microsoft SQL Server 2022 Express",
                        category = "قواعد بيانات",
                        version = "v16.0",
                        sizeText = "280 MB",
                        description = "المحرك المعتمد لبيانات المركز لإدارة الجداول والاستعلامات والإجراءات المخزنة.",
                        downloadUrl = "https://www.microsoft.com/sql-server",
                        officialSite = "Microsoft.com"
                    ),
                    StudentToolEntity(
                        id = "tool_ssms",
                        title = "SQL Server Management Studio (SSMS)",
                        category = "قواعد بيانات",
                        version = "v19.3",
                        sizeText = "650 MB",
                        description = "الواجهة الرسومية الشاملة لإدارة وتطوير واستعلام قواعد بيانات SQL Server.",
                        downloadUrl = "https://learn.microsoft.com/sql/ssms",
                        officialSite = "Microsoft.com"
                    ),
                    StudentToolEntity(
                        id = "tool_vscode",
                        title = "Visual Studio Code",
                        category = "بيئات تطوير",
                        version = "v1.92",
                        sizeText = "95 MB",
                        description = "محرر الأكواد الأفضل لتطوير تطبيقات الويب (HTML, CSS, JS, Python) والإضافات الأكاديمية.",
                        downloadUrl = "https://code.visualstudio.com",
                        officialSite = "Visual Studio"
                    ),
                    StudentToolEntity(
                        id = "tool_packet_tracer",
                        title = "Cisco Packet Tracer",
                        category = "بيئات تطوير",
                        version = "v8.2",
                        sizeText = "220 MB",
                        description = "برنامج محاكاة شبكات الحاسوب لإجراء التطبيقات العملية وتوصيل السويتشات والراوترات.",
                        downloadUrl = "https://www.netacad.com",
                        officialSite = "Cisco NetAcad"
                    ),
                    StudentToolEntity(
                        id = "tool_python",
                        title = "Python Runtime Environment",
                        category = "بيئات تطوير",
                        version = "v3.12.4",
                        sizeText = "32 MB",
                        description = "مفسر لغة بايثون لتطوير برامج تحليل البيانات والخوارزميات والتطبيقات الذكية.",
                        downloadUrl = "https://www.python.org",
                        officialSite = "Python.org"
                    ),
                    StudentToolEntity(
                        id = "tool_git",
                        title = "Git Version Control",
                        category = "أكواد ومشاريع",
                        version = "v2.45",
                        sizeText = "55 MB",
                        description = "أداة التحكم بالإصدارات وإدارة مشاريع التخرج البرمجية والمستودعات.",
                        downloadUrl = "https://git-scm.com",
                        officialSite = "Git SCM"
                    ),
                    StudentToolEntity(
                        id = "tool_ref_pdf",
                        title = "حقيبة ومذكرة تكنولوجيا المعلومات الشاملة (PDF)",
                        category = "مذكرات ومراجع",
                        version = "إصدار 2024",
                        sizeText = "14.5 MB",
                        description = "المذكرة الأكاديمية الرسمية المعتمدة من مركز الحاسب الآلي بجامعة صنعاء لمشروع التخرج.",
                        downloadUrl = "https://univ-sanaa.edu.ye/cc/materials",
                        officialSite = "جامعة صنعاء"
                    )
                )
            )
        }
    }
}
