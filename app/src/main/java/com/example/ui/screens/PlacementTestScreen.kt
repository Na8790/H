package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlacementQuizResult
import com.example.data.model.QuizQuestionEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab

@Composable
fun PlacementTestScreen(
    questions: List<QuizQuestionEntity>,
    currentIndex: Int,
    answers: Map<String, Int>,
    isCompleted: Boolean,
    result: PlacementQuizResult?,
    onSelectAnswer: (String, Int) -> Unit,
    onNextQuestion: () -> Unit,
    onPrevQuestion: () -> Unit,
    onSubmitTest: () -> Unit,
    onRestartTest: () -> Unit,
    onNavigateTab: (AppTab) -> Unit
) {
    if (isCompleted && result != null) {
        // Result Screen
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .testTag("placement_result_view"),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = UniNavyPrimary)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(GoldAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = UniNavyDark,
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "تقرير نتيجة اختبار تحديد المستوى",
                            color = GoldLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "${result.score} / ${result.totalQuestions}",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldSuccess
                        ) {
                            Text(
                                text = result.levelTitle,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Feedback and Recommendations
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = GoldDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "التوجيه الأكاديمي والمهني المقترح:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniNavyPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = result.feedback,
                            fontSize = 13.sp,
                            color = TextPrimaryLight,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "المادة المقترحة للبدء بها مباشرة:",
                            fontSize = 12.sp,
                            color = TextSecondaryLight
                        )
                        Text(
                            text = result.recommendedCourseTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = UniNavyPrimary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { onNavigateTab(AppTab.COURSES) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("explore_recommended_courses_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = UniNavyPrimary)
                        ) {
                            Text("استعراض الدورات الموصى بها", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = onRestartTest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("restart_placement_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إعادة إجراء الاختبار من جديد")
                }
            }
        }
        return
    }

    // Interactive Placement Questions flow
    if (questions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = UniNavyPrimary)
        }
        return
    }

    val currentQ = questions.getOrNull(currentIndex) ?: return
    val selectedOption = answers[currentQ.id]
    val progress = (currentIndex + 1).toFloat() / questions.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("placement_test_flow"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Test Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = UniNavyPrimary)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "اختبار تحديد المستوى الأكاديمي",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "السؤال ${currentIndex + 1} من ${questions.size}",
                            color = GoldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = GoldAccent,
                        trackColor = UniNavyLight
                    )
                }
            }
        }

        // Question Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = currentQ.questionText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val options = listOf(currentQ.optionA, currentQ.optionB, currentQ.optionC, currentQ.optionD)
                    options.forEachIndexed { index, optionText ->
                        val isSelected = selectedOption == index
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) UniNavyPrimary.copy(alpha = 0.08f) else SurfaceLight,
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        if (isSelected) UniNavyPrimary else BorderLight,
                                        if (isSelected) UniNavyPrimary else BorderLight
                                    )
                                )
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onSelectAnswer(currentQ.id, index) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onSelectAnswer(currentQ.id, index) },
                                    colors = RadioButtonDefaults.colors(selectedColor = UniNavyPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = optionText,
                                    fontSize = 13.sp,
                                    color = TextPrimaryLight
                                )
                            }
                        }
                    }
                }
            }
        }

        // Navigation Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onPrevQuestion,
                    enabled = currentIndex > 0,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("prev_placement_button")
                ) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("السابق")
                }

                if (currentIndex < questions.size - 1) {
                    Button(
                        onClick = onNextQuestion,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = UniNavyPrimary),
                        modifier = Modifier.testTag("next_placement_button")
                    ) {
                        Text("التالي")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                } else {
                    Button(
                        onClick = onSubmitTest,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                        modifier = Modifier.testTag("submit_placement_test_button")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إنهاء الاختبار ورصد المستوى", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
