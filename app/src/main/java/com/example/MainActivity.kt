package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ComputerCenterTopBar
import com.example.ui.components.DemoControlDialog
import com.example.ui.components.PaymentDialog
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.UniNavyDark
import com.example.ui.theme.UniNavyPrimary
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.ComputerCenterViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ComputerCenterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Ensure Arabic Right-to-Left (RTL) Layout Direction
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    MainAppContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: ComputerCenterViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val user by viewModel.user.collectAsStateWithLifecycle()
    val tracks by viewModel.tracks.collectAsStateWithLifecycle()
    val courses by viewModel.courses.collectAsStateWithLifecycle()
    val placementQuestions by viewModel.placementQuestions.collectAsStateWithLifecycle()
    val enrollments by viewModel.enrollments.collectAsStateWithLifecycle()
    val certificates by viewModel.certificates.collectAsStateWithLifecycle()
    val tools by viewModel.tools.collectAsStateWithLifecycle()

    val activeCourseLessons by viewModel.activeCourseLessons.collectAsStateWithLifecycle()
    val activeLessonQuizzes by viewModel.activeLessonQuizzes.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            if (uiState.activeCourseDetail == null && uiState.activeLesson == null) {
                ComputerCenterTopBar(
                    user = user,
                    onProfileClick = { viewModel.selectTab(AppTab.REPORT) },
                    onDemoBadgeClick = { viewModel.openDemoControlDialog() }
                )
            }
        },
        bottomBar = {
            if (uiState.activeLesson == null) {
                NavigationBar(
                    containerColor = UniNavyPrimary,
                    contentColor = Color.White,
                    modifier = Modifier
                        .testTag("bottom_nav_bar")
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    NavigationBarItem(
                        selected = uiState.selectedTab == AppTab.HOME,
                        onClick = { viewModel.selectTab(AppTab.HOME) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.selectedTab == AppTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "الرئيسية"
                            )
                        },
                        label = { Text("الرئيسية", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = UniNavyDark,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = Color.White.copy(alpha = 0.7f),
                            indicatorColor = com.example.ui.theme.GoldAccent
                        ),
                        modifier = Modifier.testTag("nav_home")
                    )

                    NavigationBarItem(
                        selected = uiState.selectedTab == AppTab.COURSES,
                        onClick = { viewModel.selectTab(AppTab.COURSES) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.selectedTab == AppTab.COURSES) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                                contentDescription = "الدورات"
                            )
                        },
                        label = { Text("الدورات", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = UniNavyDark,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = Color.White.copy(alpha = 0.7f),
                            indicatorColor = com.example.ui.theme.GoldAccent
                        ),
                        modifier = Modifier.testTag("nav_courses")
                    )

                    NavigationBarItem(
                        selected = uiState.selectedTab == AppTab.PLACEMENT,
                        onClick = { viewModel.selectTab(AppTab.PLACEMENT) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.selectedTab == AppTab.PLACEMENT) Icons.Filled.Assessment else Icons.Outlined.Assessment,
                                contentDescription = "تحديد المستوى"
                            )
                        },
                        label = { Text("تحديد المستوى", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = UniNavyDark,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = Color.White.copy(alpha = 0.7f),
                            indicatorColor = com.example.ui.theme.GoldAccent
                        ),
                        modifier = Modifier.testTag("nav_placement")
                    )

                    NavigationBarItem(
                        selected = uiState.selectedTab == AppTab.STUDENT_KIT,
                        onClick = { viewModel.selectTab(AppTab.STUDENT_KIT) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.selectedTab == AppTab.STUDENT_KIT) Icons.Filled.Workspaces else Icons.Outlined.Workspaces,
                                contentDescription = "حقيبة الطالب"
                            )
                        },
                        label = { Text("الحقيبة", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = UniNavyDark,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = Color.White.copy(alpha = 0.7f),
                            indicatorColor = com.example.ui.theme.GoldAccent
                        ),
                        modifier = Modifier.testTag("nav_student_kit")
                    )

                    NavigationBarItem(
                        selected = uiState.selectedTab == AppTab.REPORT,
                        onClick = { viewModel.selectTab(AppTab.REPORT) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.selectedTab == AppTab.REPORT) Icons.Filled.Article else Icons.Outlined.Article,
                                contentDescription = "الشهادات والتقرير"
                            )
                        },
                        label = { Text("الشهادات والتقرير", fontSize = 9.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = UniNavyDark,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = Color.White.copy(alpha = 0.7f),
                            indicatorColor = com.example.ui.theme.GoldAccent
                        ),
                        modifier = Modifier.testTag("nav_report")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.selectedTab) {
                AppTab.HOME -> {
                    HomeScreen(
                        user = user,
                        tracks = tracks,
                        featuredCourses = courses,
                        onNavigateTab = { tab -> viewModel.selectTab(tab) },
                        onCourseClick = { course ->
                            viewModel.selectTab(AppTab.COURSES)
                            viewModel.openCourseDetails(course)
                        },
                        onEnrollClick = { course ->
                            if (course.isFree) {
                                viewModel.selectTab(AppTab.COURSES)
                                viewModel.openCourseDetails(course)
                            } else {
                                viewModel.openPaymentDialog(course)
                            }
                        }
                    )
                }

                AppTab.COURSES -> {
                    CoursesScreen(
                        courses = courses,
                        tracks = tracks,
                        searchQuery = uiState.searchQuery,
                        selectedTrackId = uiState.selectedTrackFilter,
                        activeCourse = uiState.activeCourseDetail,
                        activeCourseLessons = activeCourseLessons,
                        activeLesson = uiState.activeLesson,
                        activeLessonQuizzes = activeLessonQuizzes,
                        isShowingLessonQuiz = uiState.isShowingLessonQuiz,
                        lessonQuizAnswers = uiState.lessonQuizAnswers,
                        lessonQuizSubmitted = uiState.lessonQuizSubmitted,
                        lessonQuizScore = uiState.lessonQuizScore,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onTrackSelect = { viewModel.setTrackFilter(it) },
                        onCourseClick = { viewModel.openCourseDetails(it) },
                        onCloseCourse = { viewModel.closeCourseDetails() },
                        onEnrollClick = { course ->
                            if (course.isFree) {
                                viewModel.openCourseDetails(course)
                            } else {
                                viewModel.openPaymentDialog(course)
                            }
                        },
                        onLessonClick = { viewModel.openLesson(it) },
                        onCloseLesson = { viewModel.closeLesson() },
                        onToggleLessonQuiz = { viewModel.toggleLessonQuiz() },
                        onSelectLessonQuizAnswer = { qId, ans -> viewModel.selectLessonQuizAnswer(qId, ans) },
                        onSubmitLessonQuiz = { viewModel.submitLessonQuiz() }
                    )
                }

                AppTab.PLACEMENT -> {
                    PlacementTestScreen(
                        questions = placementQuestions,
                        currentIndex = uiState.placementCurrentIndex,
                        answers = uiState.placementAnswers,
                        isCompleted = uiState.isPlacementCompleted,
                        result = uiState.placementResult,
                        onSelectAnswer = { qId, ans -> viewModel.selectPlacementAnswer(qId, ans) },
                        onNextQuestion = { viewModel.nextPlacementQuestion() },
                        onPrevQuestion = { viewModel.prevPlacementQuestion() },
                        onSubmitTest = { viewModel.submitPlacementTest() },
                        onRestartTest = { viewModel.restartPlacementTest() },
                        onNavigateTab = { tab -> viewModel.selectTab(tab) }
                    )
                }

                AppTab.STUDENT_KIT -> {
                    StudentKitScreen(
                        tools = tools,
                        enrollments = enrollments,
                        onCourseClickById = { courseId ->
                            val course = courses.find { it.id == courseId }
                            if (course != null) {
                                viewModel.selectTab(AppTab.COURSES)
                                viewModel.openCourseDetails(course)
                            }
                        }
                    )
                }

                AppTab.REPORT -> {
                    CertificatesAndReportScreen(
                        user = user,
                        certificates = certificates
                    )
                }
            }

            // Payment / Enrollment Dialog
            uiState.paymentCourse?.let { course ->
                PaymentDialog(
                    course = course,
                    paymentMethod = uiState.paymentMethod,
                    onPaymentMethodChange = { viewModel.setPaymentMethod(it) },
                    receiptCode = uiState.paymentReceiptCode,
                    onReceiptCodeChange = { viewModel.setPaymentReceiptCode(it) },
                    isSuccess = uiState.isPaymentSuccess,
                    onSubmit = { viewModel.submitPayment() },
                    onDismiss = { viewModel.closePaymentDialog() }
                )
            }

            // Demo Control Dialog
            DemoControlDialog(
                isOpen = uiState.isDemoControlDialogOpen,
                onDismiss = { viewModel.closeDemoControlDialog() },
                onUnlockAllCoursesAndCertificates = { viewModel.quickUnlockAllDemoData() },
                onAutoFillPlacementTest = { viewModel.quickAutoFillPlacement() },
                onResetDatabase = { viewModel.resetDemoData() }
            )
        }
    }
}
