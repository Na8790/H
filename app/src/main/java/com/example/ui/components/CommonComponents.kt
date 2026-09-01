package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CourseEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComputerCenterTopBar(
    user: UserEntity?,
    onProfileClick: () -> Unit = {},
    onDemoBadgeClick: () -> Unit = {}
) {
    Surface(
        color = UniNavyPrimary,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .padding(2.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.sanaa_cc_logo),
                            contentDescription = "شعار مركز الحاسب الآلي",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Column {
                        Text(
                            text = "جامعة صنعاء - مركز الحاسب",
                            color = GoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "منصة تكنولوجيا المعلومات",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Demo APK Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GoldAccent,
                        modifier = Modifier
                            .testTag("demo_badge_button")
                            .clickable { onDemoBadgeClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = "نسخة تجريبية",
                                tint = UniNavyDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "نسخة تجريبية",
                                color = UniNavyDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Student Pill / Badge
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = UniNavyLight,
                        modifier = Modifier
                            .testTag("user_profile_pill")
                            .clickable { onProfileClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "طالب",
                                tint = GoldAccent,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = user?.fullName?.split(" ")?.firstOrNull() ?: "رغد",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "ابحث عن الدورات، الدروس أو الأدوات البرمجية..."
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .testTag("search_text_field"),
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 13.sp,
                color = TextSecondaryLight
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "بحث",
                tint = UniNavyLight
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.testTag("clear_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "مسح البحث",
                        tint = TextSecondaryLight
                    )
                }
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = UniNavyPrimary,
            unfocusedBorderColor = BorderLight,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        singleLine = true
    )
}

@Composable
fun CourseCard(
    course: CourseEntity,
    onCourseClick: () -> Unit,
    onEnrollClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("course_card_${course.id}")
            .clickable { onCourseClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Free/Paid Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (course.isFree) EmeraldLight else GoldLight
                ) {
                    Text(
                        text = if (course.isFree) "دورة مجانية مساندة" else "${course.priceYemenRials} ر.ي",
                        color = if (course.isFree) EmeraldDark else GoldDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Level Tag
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = UniNavyPrimary.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = "المستوى: ${course.level}",
                        color = UniNavyPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = course.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryLight,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = course.description,
                fontSize = 12.sp,
                color = TextSecondaryLight,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "المدرب",
                        tint = UniNavyLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = course.instructor,
                        fontSize = 12.sp,
                        color = TextSecondaryLight
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "الدروس",
                        tint = UniNavyLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${course.totalLessons} دروس",
                        fontSize = 12.sp,
                        color = TextSecondaryLight
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "التقييم",
                        tint = AmberWarning,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${course.rating}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                }
            }

            // Progress bar if enrolled
            if (course.isEnrolled) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (course.isCompleted) "مكتملة (صدرت الشهادة)" else "نسبة الإنجاز",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (course.isCompleted) EmeraldDark else UniNavyPrimary
                        )
                        Text(
                            text = "${course.progressPercent}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (course.isCompleted) EmeraldDark else UniNavyPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { course.progressPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (course.isCompleted) EmeraldSuccess else UniNavyPrimary,
                        trackColor = BorderLight
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onEnrollClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .testTag("enroll_button_${course.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (course.isFree) UniNavyPrimary else GoldDark
                    )
                ) {
                    Icon(
                        imageVector = if (course.isFree) Icons.Default.PlayArrow else Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (course.isFree) "بدء المادة مجاناً" else "طلب الالتحاق بالدورة",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentDialog(
    course: CourseEntity,
    paymentMethod: String,
    onPaymentMethodChange: (String) -> Unit,
    receiptCode: String,
    onReceiptCodeChange: (String) -> Unit,
    isSuccess: Boolean,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            if (!isSuccess) {
                Button(
                    onClick = onSubmit,
                    modifier = Modifier.testTag("confirm_payment_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = UniNavyPrimary)
                ) {
                    Text("تأكيد التسجيل والدفع")
                }
            } else {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("done_payment_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                ) {
                    Text("تم وحفظ الجدول")
                }
            }
        },
        dismissButton = {
            if (!isSuccess) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("cancel_payment_button")
                ) {
                    Text("إلغاء", color = TextSecondaryLight)
                }
            }
        },
        title = {
            Text(
                text = if (isSuccess) "تم تأكيد طلب الالتحاق بنجاح!" else "طلب الالتحاق بدورة تدريبية",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (isSuccess) EmeraldDark else UniNavyPrimary
            )
        },
        text = {
            if (isSuccess) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(EmeraldLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "تم قبول تسجيلك في دورة:\n${course.title}",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceLight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "القاعة: ${course.labRoom}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "الجدول: السبت والاثنين والأربعاء (4 - 6 مساءً)", fontSize = 12.sp)
                            Text(text = "المدرب: ${course.instructor}", fontSize = 12.sp)
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "الدورة: ${course.title}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = UniNavyPrimary
                    )
                    Text(
                        text = "رسوم الدورة: ${course.priceYemenRials} ريال يمني",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GoldDark
                    )

                    HorizontalDivider()

                    Text(
                        text = "اختر وسيلة الدفع / التحويل البنكي:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    val paymentOptions = listOf(
                        "بنك الكريمي (حساب المركز: 1204859)",
                        "بنك اليمن الدولي (حساب: 450912)",
                        "محفظة كاش / جوالي (777000000)",
                        "تسديد نقدي في إدارة المركز"
                    )

                    paymentOptions.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPaymentMethodChange(option) }
                                .padding(vertical = 2.dp)
                        ) {
                            RadioButton(
                                selected = (paymentMethod == option),
                                onClick = { onPaymentMethodChange(option) },
                                colors = RadioButtonDefaults.colors(selectedColor = UniNavyPrimary)
                            )
                            Text(
                                text = option,
                                fontSize = 12.sp,
                                color = TextPrimaryLight
                            )
                        }
                    }

                    OutlinedTextField(
                        value = receiptCode,
                        onValueChange = onReceiptCodeChange,
                        label = { Text("رقم الحوالة / الإشعار البنكي") },
                        placeholder = { Text("مثال: TXN-948102") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("payment_receipt_input"),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun DemoControlDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onUnlockAllCoursesAndCertificates: () -> Unit,
    onAutoFillPlacementTest: () -> Unit,
    onResetDatabase: () -> Unit
) {
    if (!isOpen) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = null,
                    tint = GoldDark
                )
                Text(
                    text = "لوحة أدوات النسخة التجريبية (Demo APK)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = UniNavyPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = UniNavyPrimary.copy(alpha = 0.06f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "💡 معلومات النسخة التجريبية (Demo APK)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = UniNavyPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "هذه النسخة معدّة لمعاينة وتجربة كافة وظائف مركز الحاسب الآلي: المسارات، الدورات والدروس، اختبار تحديد المستوى التفاعلي، إصدار الشهادات المعتمدة وحقيبة الطالب.",
                            fontSize = 11.sp,
                            color = TextSecondaryLight,
                            lineHeight = 16.sp
                        )
                    }
                }

                // Quick Action 1: Unlock Everything
                OutlinedButton(
                    onClick = onUnlockAllCoursesAndCertificates,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("demo_unlock_all_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = UniNavyPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = GoldDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "فتح جميع الدورات والشهادات للتجربة",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Quick Action 2: Solve Placement Test
                OutlinedButton(
                    onClick = onAutoFillPlacementTest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("demo_autofill_placement_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = UniNavyPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.FactCheck,
                        contentDescription = null,
                        tint = EmeraldSuccess,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "تجربة حل وتصحيح اختبار المستوى فوراً",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Quick Action 3: Reset database
                OutlinedButton(
                    onClick = onResetDatabase,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("demo_reset_data_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextSecondaryLight
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "إعادة ضبط البيانات التجريبية للافتراضي",
                        fontSize = 12.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldLight.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            tint = EmeraldDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "يمكنك تحميل وتصدير حزمة APK من خلال قائمة الإعدادات في AI Studio.",
                            fontSize = 11.sp,
                            color = EmeraldDark
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = UniNavyPrimary),
                modifier = Modifier.testTag("close_demo_dialog_button")
            ) {
                Text("إغلاق")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
