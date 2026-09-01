package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.ComputerCenterRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppTab(val titleAr: String) {
    HOME("الرئيسية"),
    COURSES("الدورات"),
    PLACEMENT("تحديد المستوى"),
    STUDENT_KIT("حقيبة الطالب"),
    REPORT("الشهادات والتقرير")
}

data class UiState(
    val selectedTab: AppTab = AppTab.HOME,
    val selectedTrackFilter: String? = null,
    val searchQuery: String = "",
    val activeCourseDetail: CourseEntity? = null,
    val activeLesson: LessonEntity? = null,
    val isShowingLessonQuiz: Boolean = false,
    val lessonQuizAnswers: Map<String, Int> = emptyMap(),
    val lessonQuizSubmitted: Boolean = false,
    val lessonQuizScore: Int = 0,
    
    // Placement test state
    val placementCurrentIndex: Int = 0,
    val placementAnswers: Map<String, Int> = emptyMap(),
    val placementResult: PlacementQuizResult? = null,
    val isPlacementCompleted: Boolean = false,
    
    // Payment Dialog state
    val paymentCourse: CourseEntity? = null,
    val paymentMethod: String = "بنك الكريمي (حساب المركز)",
    val paymentReceiptCode: String = "",
    val isPaymentSuccess: Boolean = false,
    val isDemoControlDialogOpen: Boolean = false,
    val snackbarMessage: String? = null
)

class ComputerCenterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ComputerCenterRepository

    val user: StateFlow<UserEntity?>
    val tracks: StateFlow<List<ITTrackEntity>>
    val courses: StateFlow<List<CourseEntity>>
    val placementQuestions: StateFlow<List<QuizQuestionEntity>>
    val enrollments: StateFlow<List<EnrollmentEntity>>
    val certificates: StateFlow<List<CertificateEntity>>
    val tools: StateFlow<List<StudentToolEntity>>

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _activeCourseLessons = MutableStateFlow<List<LessonEntity>>(emptyList())
    val activeCourseLessons: StateFlow<List<LessonEntity>> = _activeCourseLessons.asStateFlow()

    private val _activeLessonQuizzes = MutableStateFlow<List<QuizQuestionEntity>>(emptyList())
    val activeLessonQuizzes: StateFlow<List<QuizQuestionEntity>> = _activeLessonQuizzes.asStateFlow()

    init {
        val database = AppDatabase.getInstance(application)
        repository = ComputerCenterRepository(database.dao())

        user = repository.user.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        tracks = repository.tracks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        courses = repository.courses.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        placementQuestions = repository.placementQuestions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        enrollments = repository.enrollments.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        certificates = repository.certificates.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        tools = repository.tools.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }
    }

    fun selectTab(tab: AppTab) {
        _uiState.update { it.copy(selectedTab = tab, activeCourseDetail = null, activeLesson = null) }
    }

    fun setTrackFilter(trackId: String?) {
        _uiState.update { it.copy(selectedTrackFilter = trackId) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun openCourseDetails(course: CourseEntity) {
        _uiState.update { it.copy(activeCourseDetail = course, activeLesson = null, isShowingLessonQuiz = false) }
        viewModelScope.launch {
            repository.getLessonsForCourse(course.id).collect { lessons ->
                _activeCourseLessons.value = lessons
            }
        }
    }

    fun closeCourseDetails() {
        _uiState.update { it.copy(activeCourseDetail = null, activeLesson = null) }
    }

    fun openLesson(lesson: LessonEntity) {
        _uiState.update {
            it.copy(
                activeLesson = lesson,
                isShowingLessonQuiz = false,
                lessonQuizAnswers = emptyMap(),
                lessonQuizSubmitted = false,
                lessonQuizScore = 0
            )
        }
        viewModelScope.launch {
            repository.getQuizzesForTarget(lesson.id).collect { quizzes ->
                _activeLessonQuizzes.value = quizzes
            }
        }
    }

    fun closeLesson() {
        _uiState.update { it.copy(activeLesson = null, isShowingLessonQuiz = false) }
    }

    fun toggleLessonQuiz() {
        _uiState.update { it.copy(isShowingLessonQuiz = !it.isShowingLessonQuiz) }
    }

    fun selectLessonQuizAnswer(questionId: String, optionIndex: Int) {
        val current = _uiState.value.lessonQuizAnswers.toMutableMap()
        current[questionId] = optionIndex
        _uiState.update { it.copy(lessonQuizAnswers = current) }
    }

    fun submitLessonQuiz() {
        val questions = _activeLessonQuizzes.value
        val answers = _uiState.value.lessonQuizAnswers
        var correct = 0
        questions.forEach { q ->
            if (answers[q.id] == q.correctOptionIndex) {
                correct++
            }
        }
        val score = if (questions.isNotEmpty()) ((correct.toFloat() / questions.size) * 100).toInt() else 100
        _uiState.update { it.copy(lessonQuizSubmitted = true, lessonQuizScore = score) }

        val activeCourse = _uiState.value.activeCourseDetail
        val activeLesson = _uiState.value.activeLesson
        if (activeCourse != null && activeLesson != null) {
            viewModelScope.launch {
                repository.markLessonCompleted(activeCourse.id, activeLesson.id)
                showSnackbar("تم تسجيل إتمام الدرس بنجاح والحصول على $score%")
            }
        }
    }

    // Placement test logic
    fun selectPlacementAnswer(questionId: String, optionIndex: Int) {
        val current = _uiState.value.placementAnswers.toMutableMap()
        current[questionId] = optionIndex
        _uiState.update { it.copy(placementAnswers = current) }
    }

    fun nextPlacementQuestion() {
        val maxIndex = placementQuestions.value.size - 1
        if (_uiState.value.placementCurrentIndex < maxIndex) {
            _uiState.update { it.copy(placementCurrentIndex = it.placementCurrentIndex + 1) }
        }
    }

    fun prevPlacementQuestion() {
        if (_uiState.value.placementCurrentIndex > 0) {
            _uiState.update { it.copy(placementCurrentIndex = it.placementCurrentIndex - 1) }
        }
    }

    fun submitPlacementTest() {
        val questions = placementQuestions.value
        val answers = _uiState.value.placementAnswers
        var correctCount = 0
        questions.forEach { q ->
            if (answers[q.id] == q.correctOptionIndex) {
                correctCount++
            }
        }

        viewModelScope.launch {
            val levelName = when {
                correctCount >= 8 -> "متقدم"
                correctCount >= 5 -> "متوسط"
                else -> "مبتدئ"
            }
            val result = repository.savePlacementResult(correctCount, levelName)
            _uiState.update {
                it.copy(
                    isPlacementCompleted = true,
                    placementResult = result
                )
            }
            showSnackbar("تهانينا! نتيجتك: $correctCount / ${questions.size} - تم تحديد مستواك: $levelName")
        }
    }

    fun restartPlacementTest() {
        _uiState.update {
            it.copy(
                placementCurrentIndex = 0,
                placementAnswers = emptyMap(),
                placementResult = null,
                isPlacementCompleted = false
            )
        }
    }

    // Payment & Enrollment
    fun openPaymentDialog(course: CourseEntity) {
        _uiState.update {
            it.copy(
                paymentCourse = course,
                paymentReceiptCode = "",
                isPaymentSuccess = false
            )
        }
    }

    fun setPaymentMethod(method: String) {
        _uiState.update { it.copy(paymentMethod = method) }
    }

    fun setPaymentReceiptCode(code: String) {
        _uiState.update { it.copy(paymentReceiptCode = code) }
    }

    fun submitPayment() {
        val course = _uiState.value.paymentCourse ?: return
        val method = _uiState.value.paymentMethod
        val code = _uiState.value.paymentReceiptCode
        viewModelScope.launch {
            val success = repository.registerEnrollment(course, method, code, course.priceYemenRials)
            if (success) {
                _uiState.update { it.copy(isPaymentSuccess = true) }
                showSnackbar("تم تسجيل طلب الالتحاق وإصدار تفاصيل القاعة والجدول الدراسي بنجاح!")
            }
        }
    }

    fun closePaymentDialog() {
        _uiState.update { it.copy(paymentCourse = null, isPaymentSuccess = false) }
    }

    fun openDemoControlDialog() {
        _uiState.update { it.copy(isDemoControlDialogOpen = true) }
    }

    fun closeDemoControlDialog() {
        _uiState.update { it.copy(isDemoControlDialogOpen = false) }
    }

    fun quickUnlockAllDemoData() {
        viewModelScope.launch {
            repository.unlockAllDemoContent()
            _uiState.update { it.copy(isDemoControlDialogOpen = false) }
            showSnackbar("تم فتح جميع الشهادات والدورات التجريبية بنجاح للتقييم والتجربة!")
        }
    }

    fun quickAutoFillPlacement() {
        val questions = placementQuestions.value
        val autoAnswers = mutableMapOf<String, Int>()
        questions.forEachIndexed { idx, q ->
            // Fill high-scoring answers
            autoAnswers[q.id] = q.correctOptionIndex
        }
        _uiState.update {
            it.copy(
                selectedTab = AppTab.PLACEMENT,
                placementAnswers = autoAnswers,
                isDemoControlDialogOpen = false
            )
        }
        submitPlacementTest()
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetToDefaultData()
            _uiState.update {
                it.copy(
                    isDemoControlDialogOpen = false,
                    placementCurrentIndex = 0,
                    placementAnswers = emptyMap(),
                    placementResult = null,
                    isPlacementCompleted = false
                )
            }
            showSnackbar("تمت إعادة تعيين البيانات التجريبية إلى حالتها الأصلية.")
        }
    }

    fun showSnackbar(message: String) {
        _uiState.update { it.copy(snackbarMessage = message) }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
