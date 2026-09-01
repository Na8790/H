package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CourseEntity
import com.example.data.model.ITTrackEntity
import com.example.data.model.LessonEntity
import com.example.data.model.QuizQuestionEntity
import com.example.ui.components.AppSearchBar
import com.example.ui.components.CourseCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(
    courses: List<CourseEntity>,
    tracks: List<ITTrackEntity>,
    searchQuery: String,
    selectedTrackId: String?,
    activeCourse: CourseEntity?,
    activeCourseLessons: List<LessonEntity>,
    activeLesson: LessonEntity?,
    activeLessonQuizzes: List<QuizQuestionEntity>,
    isShowingLessonQuiz: Boolean,
    lessonQuizAnswers: Map<String, Int>,
    lessonQuizSubmitted: Boolean,
    lessonQuizScore: Int,
    onSearchChange: (String) -> Unit,
    onTrackSelect: (String?) -> Unit,
    onCourseClick: (CourseEntity) -> Unit,
    onCloseCourse: () -> Unit,
    onEnrollClick: (CourseEntity) -> Unit,
    onLessonClick: (LessonEntity) -> Unit,
    onCloseLesson: () -> Unit,
    onToggleLessonQuiz: () -> Unit,
    onSelectLessonQuizAnswer: (String, Int) -> Unit,
    onSubmitLessonQuiz: () -> Unit
) {
    // 1. If active lesson is opened, show Lesson Reader / Quiz
    if (activeLesson != null) {
        LessonReaderView(
            lesson = activeLesson,
            quizzes = activeLessonQuizzes,
            isShowingQuiz = isShowingLessonQuiz,
            quizAnswers = lessonQuizAnswers,
            isSubmitted = lessonQuizSubmitted,
            score = lessonQuizScore,
            onBack = onCloseLesson,
            onToggleQuiz = onToggleLessonQuiz,
            onSelectAnswer = onSelectLessonQuizAnswer,
            onSubmitQuiz = onSubmitLessonQuiz
        )
        return
    }

    // 2. If course details is opened, show Course Detail & Syllabus
    if (activeCourse != null) {
        CourseDetailView(
            course = activeCourse,
            lessons = activeCourseLessons,
            onBack = onCloseCourse,
            onEnrollClick = { onEnrollClick(activeCourse) },
            onLessonClick = onLessonClick
        )
        return
    }

    // 3. Otherwise show Course List with Filters
    val filteredCourses = courses.filter { course ->
        val matchesTrack = selectedTrackId == null || course.trackId == selectedTrackId
        val matchesSearch = searchQuery.isBlank() ||
                course.title.contains(searchQuery, ignoreCase = true) ||
                course.description.contains(searchQuery, ignoreCase = true) ||
                course.instructor.contains(searchQuery, ignoreCase = true)
        matchesTrack && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("courses_screen_container")
    ) {
        // Search bar
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            AppSearchBar(
                query = searchQuery,
                onQueryChange = onSearchChange,
                placeholder = "ابحث في المناهج، لغات البرمجة، أو المدربين..."
            )
        }

        // Track Filter chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedTrackId == null,
                    onClick = { onTrackSelect(null) },
                    label = { Text("جميع التخصصات", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = UniNavyPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
            items(tracks) { track ->
                FilterChip(
                    selected = selectedTrackId == track.id,
                    onClick = { onTrackSelect(track.id) },
                    label = { Text(track.title.split(" ").firstOrNull() ?: track.title, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = UniNavyPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Courses List
        if (filteredCourses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = TextSecondaryLight,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "لا توجد مواد مطابقة لخيارات البحث",
                        color = TextSecondaryLight,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredCourses) { course ->
                    CourseCard(
                        course = course,
                        onCourseClick = { onCourseClick(course) },
                        onEnrollClick = { onEnrollClick(course) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailView(
    course: CourseEntity,
    lessons: List<LessonEntity>,
    onBack: () -> Unit,
    onEnrollClick: () -> Unit,
    onLessonClick: (LessonEntity) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = course.title,
                        maxLines = 1,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_from_course_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = UniNavyPrimary)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Course Info Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (course.isFree) EmeraldLight else GoldLight
                            ) {
                                Text(
                                    text = if (course.isFree) "مجاني مساند" else "${course.priceYemenRials} ر.ي",
                                    color = if (course.isFree) EmeraldDark else GoldDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = "المستوى: ${course.level}",
                                fontSize = 12.sp,
                                color = UniNavyPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = course.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryLight
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = course.description,
                            fontSize = 13.sp,
                            color = TextSecondaryLight,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("المدرب المشرف:", fontSize = 11.sp, color = TextSecondaryLight)
                                Text(course.instructor, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = UniNavyPrimary)
                            }
                            Column {
                                Text("مكان التطبيق:", fontSize = 11.sp, color = TextSecondaryLight)
                                Text(course.labRoom, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = UniNavyPrimary)
                            }
                            Column {
                                Text("المدة التدريبية:", fontSize = 11.sp, color = TextSecondaryLight)
                                Text("${course.durationHours} ساعة", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = UniNavyPrimary)
                            }
                        }

                        if (!course.isEnrolled) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onEnrollClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("detail_enroll_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (course.isFree) UniNavyPrimary else GoldDark
                                )
                            ) {
                                Text(
                                    text = if (course.isFree) "بدء دراسة المادة" else "حجز مقعد والتسجيل",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Lessons Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "المنهج والدروس التفاعلية (${lessons.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = UniNavyPrimary
                    )
                    Text(
                        text = "اضغط على الدرس للقراءة والاختبار",
                        fontSize = 11.sp,
                        color = TextSecondaryLight
                    )
                }
            }

            // Lessons List
            items(lessons) { lesson ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("lesson_item_${lesson.id}")
                        .clickable { onLessonClick(lesson) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (lesson.isCompleted) EmeraldLight.copy(alpha = 0.5f) else Color.White
                    ),
                    border = if (lesson.isCompleted) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(EmeraldSuccess, EmeraldDark))) else null
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (lesson.isCompleted) EmeraldSuccess else UniNavyPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            if (lesson.isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "مكتمل",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(
                                    text = "${lesson.orderIndex}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = lesson.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = lesson.summary,
                                fontSize = 11.sp,
                                color = TextSecondaryLight,
                                maxLines = 1
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SurfaceLight
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = TextSecondaryLight,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "${lesson.durationMinutes} د",
                                    fontSize = 11.sp,
                                    color = TextSecondaryLight
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonReaderView(
    lesson: LessonEntity,
    quizzes: List<QuizQuestionEntity>,
    isShowingQuiz: Boolean,
    quizAnswers: Map<String, Int>,
    isSubmitted: Boolean,
    score: Int,
    onBack: () -> Unit,
    onToggleQuiz: () -> Unit,
    onSelectAnswer: (String, Int) -> Unit,
    onSubmitQuiz: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "الدرس ${lesson.orderIndex}: ${lesson.title}",
                        maxLines = 1,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_from_lesson_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = onToggleQuiz,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("toggle_quiz_button"),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (isShowingQuiz) GoldAccent else UniNavyLight,
                            contentColor = if (isShowingQuiz) UniNavyDark else Color.White
                        )
                    ) {
                        Icon(
                            imageVector = if (isShowingQuiz) Icons.Default.MenuBook else Icons.Default.Quiz,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isShowingQuiz) "محتوى الدرس" else "اختبار الفهم",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = UniNavyPrimary)
            )
        }
    ) { innerPadding ->
        if (isShowingQuiz) {
            // Lesson Quiz Mode
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = UniNavyLight)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "اختبار قياس مستوى الفهم بعد الدرس",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "أجب عن الأسئلة للتأكد من استيعاب المفاهيم والحصول على الدرجات",
                                    color = GoldLight,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                if (quizzes.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Text(
                                text = "تم تسجيل إتمام هذا الدرس بنجاح بدون اختبارات إضافية.",
                                modifier = Modifier.padding(20.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(quizzes) { q ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = q.questionText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryLight,
                                    lineHeight = 20.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                val options = listOf(q.optionA, q.optionB, q.optionC, q.optionD)
                                options.forEachIndexed { index, opt ->
                                    val isSelected = quizAnswers[q.id] == index
                                    val isCorrect = q.correctOptionIndex == index

                                    val optionBg = when {
                                        !isSubmitted && isSelected -> UniNavyPrimary.copy(alpha = 0.1f)
                                        isSubmitted && isCorrect -> EmeraldLight
                                        isSubmitted && isSelected && !isCorrect -> CoralAlert.copy(alpha = 0.15f)
                                        else -> SurfaceLight
                                    }

                                    val optionBorder = when {
                                        !isSubmitted && isSelected -> UniNavyPrimary
                                        isSubmitted && isCorrect -> EmeraldSuccess
                                        isSubmitted && isSelected && !isCorrect -> CoralAlert
                                        else -> BorderLight
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = optionBg,
                                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(optionBorder, optionBorder))),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clickable(enabled = !isSubmitted) {
                                                onSelectAnswer(q.id, index)
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = { if (!isSubmitted) onSelectAnswer(q.id, index) },
                                                colors = RadioButtonDefaults.colors(selectedColor = UniNavyPrimary)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = opt,
                                                fontSize = 13.sp,
                                                color = TextPrimaryLight
                                            )
                                        }
                                    }
                                }

                                if (isSubmitted) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = SurfaceLight,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(
                                                text = "الشرح والتوضيح:",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = UniNavyPrimary
                                            )
                                            Text(
                                                text = q.explanation,
                                                fontSize = 12.sp,
                                                color = TextSecondaryLight
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        if (!isSubmitted) {
                            Button(
                                onClick = onSubmitQuiz,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("submit_lesson_quiz_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = UniNavyPrimary)
                            ) {
                                Text("تسجيل الإجابات وإنهاء الدرس", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = EmeraldLight)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "الدرجة المستحقة: $score%",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldDark
                                    )
                                    Text(
                                        text = "تم تحديث سجلك الأكاديمي وإتمام الدرس بنجاح!",
                                        fontSize = 12.sp,
                                        color = TextPrimaryLight
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Theory & Practical Code Content View
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = lesson.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniNavyPrimary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = lesson.fullContent,
                                fontSize = 14.sp,
                                color = TextPrimaryLight,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }

                // Key Terms Card
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceLight)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = null,
                                    tint = GoldDark,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "المصطلحات والمفاهيم الجوهرية:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UniNavyPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = lesson.keyTerms,
                                fontSize = 12.sp,
                                color = TextSecondaryLight
                            )
                        }
                    }
                }

                // Code Snippet Card
                if (lesson.codeSnippet.isNotBlank()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = UniNavyDark)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "التطبيق العملي والشيفرة البرمجية (Code):",
                                        color = GoldLight,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Terminal,
                                        contentDescription = null,
                                        tint = UniCyanSoft,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF070F1E),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = lesson.codeSnippet,
                                        color = Color(0xFF80FFDB),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(12.dp),
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Button to Start Quiz
                item {
                    Button(
                        onClick = onToggleQuiz,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("goto_quiz_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = UniNavyPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Quiz, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "الانتقال إلى اختبار الدرس القصير (Quiz)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
