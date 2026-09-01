package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CertificateEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.*

enum class ReportSection {
    CERTIFICATES,
    SUMMARY,
    DEDICATION,
    SUPERVISOR,
    FEASIBILITY,
    SYSTEM_ANALYSIS
}

@Composable
fun CertificatesAndReportScreen(
    user: UserEntity?,
    certificates: List<CertificateEntity>
) {
    val context = LocalContext.current
    var activeSection by remember { mutableStateOf(ReportSection.CERTIFICATES) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("certificates_report_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Student Profile Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = UniNavyPrimary)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(GoldAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ر",
                                color = UniNavyDark,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user?.fullName ?: "رغد حمود حسين العصري",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "الرقم الأكاديمي: ${user?.studentId ?: "IT-2024-8841"}",
                                color = GoldLight,
                                fontSize = 11.sp
                            )
                            Text(
                                text = user?.department ?: "مركز الحاسب الآلي - جامعة صنعاء",
                                color = UniCyanSoft,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = UniNavyLight)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("المستوى الحالي", fontSize = 10.sp, color = GoldLight)
                            Text(user?.currentLevel ?: "متقدم", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("الشهادات المكتسبة", fontSize = 10.sp, color = GoldLight)
                            Text("${certificates.size} شهادات", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("النقاط التراكمية", fontSize = 10.sp, color = GoldLight)
                            Text("${user?.points ?: 1450} XP", fontSize = 12.sp, color = EmeraldSuccess, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section Selector Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = activeSection.ordinal,
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                Tab(
                    selected = activeSection == ReportSection.CERTIFICATES,
                    onClick = { activeSection = ReportSection.CERTIFICATES },
                    text = { Text("الشهادات المعتمدة", fontSize = 12.sp) }
                )
                Tab(
                    selected = activeSection == ReportSection.SUMMARY,
                    onClick = { activeSection = ReportSection.SUMMARY },
                    text = { Text("ملخص المشروع", fontSize = 12.sp) }
                )
                Tab(
                    selected = activeSection == ReportSection.DEDICATION,
                    onClick = { activeSection = ReportSection.DEDICATION },
                    text = { Text("الإهداء والشكر", fontSize = 12.sp) }
                )
                Tab(
                    selected = activeSection == ReportSection.SUPERVISOR,
                    onClick = { activeSection = ReportSection.SUPERVISOR },
                    text = { Text("لجنة الإشراف", fontSize = 12.sp) }
                )
                Tab(
                    selected = activeSection == ReportSection.FEASIBILITY,
                    onClick = { activeSection = ReportSection.FEASIBILITY },
                    text = { Text("دراسة الجدوى", fontSize = 12.sp) }
                )
                Tab(
                    selected = activeSection == ReportSection.SYSTEM_ANALYSIS,
                    onClick = { activeSection = ReportSection.SYSTEM_ANALYSIS },
                    text = { Text("التحليل والنمذجة", fontSize = 12.sp) }
                )
            }
        }

        // Active Section Content
        when (activeSection) {
            ReportSection.CERTIFICATES -> {
                if (certificates.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Text(
                                text = "أكمل الدورات واجتز اختبارات الدروس للحصول على شهادات معتمدة صادرة من مركز الحاسب الآلي.",
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center,
                                color = TextSecondaryLight
                            )
                        }
                    }
                } else {
                    items(certificates) { cert ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("certificate_card_${cert.certificateId}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(listOf(GoldAccent, GoldDark, GoldLight))
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "الجمهورية اليمنية\nجامعة صنعاء - مركز الحاسب الآلي",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UniNavyPrimary
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(GoldLight),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = GoldDark,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "شهادة إتمام معتمدة",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UniNavyPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "يشهد مركز الحاسب الآلي بجامعة صنعاء بأن الطالبة:",
                                    fontSize = 12.sp,
                                    color = TextSecondaryLight
                                )
                                Text(
                                    text = cert.studentName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UniNavyDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "قد أتمت بنجاح متطلبات الدورة التدريبية المقررة:",
                                    fontSize = 12.sp,
                                    color = TextSecondaryLight
                                )
                                Text(
                                    text = cert.courseTitle,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UniNavyPrimary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "بتقدير: ${cert.gradeLevel} (الدرجة: ${cert.finalScore}%)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = BorderLight)
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("رقم الشهادة:", fontSize = 10.sp, color = TextSecondaryLight)
                                        Text(cert.certificateId, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("تاريخ الإصدار:", fontSize = 10.sp, color = TextSecondaryLight)
                                        Text(cert.issueDate, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "المشرفون: ${cert.supervisorName}",
                                    fontSize = 11.sp,
                                    color = TextSecondaryLight,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedButton(
                                    onClick = {
                                        Toast.makeText(context, "تم حفظ ومشاركة وثيقة الشهادة بصيغة رقمية معتمدة", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("مشاركة أو طباعة الشهادة الرسمية", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            ReportSection.SUMMARY -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "ملخص مشروع التخرج",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniNavyPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = """
                                    المشروع عبارة عن منصة تعليمية إلكترونية لمركز الحاسب الآلي بجامعة صنعاء تهدف إلى توفير جميع الدروس والمواد التعليمية الخاصة بتخصص تكنولوجيا المعلومات.
                                    
                                    يقوم النظام بمعالجة مشكلة نقص الدروس وتنظيمها بشكل أفضل مع تسهيل وصول الطالب إلى مواده التعليمية في أي وقت ومن أي جهاز، كما يسعى إلى تحسين مستوى الطالب عبر اختبارات قصيرة بعد كل درس لضمان الفهم.
                                    
                                    التقنيات المعتمدة:
                                    - قواعد البيانات: SQL Server لتخزين البيانات بكفاءة وأمان.
                                    - واجهات وتطبيقات المستخدم: HTML5, CSS3, JavaScript, وتطبيق الهاتف الذكي بلغة Kotlin و Jetpack Compose.
                                    - نظام التشغيل والسيرفر: Windows 10 وسيرفر متوسط القدرة لتشغيل المنصة بمرونة.
                                """.trimIndent(),
                                fontSize = 13.sp,
                                color = TextPrimaryLight,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }

            ReportSection.DEDICATION -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "الإهداء والشكر والتقدير",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniNavyPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = """
                                    الحمد لله أولاً وآخراً الذي بنعمته تتم الصالحات وبتوفيقه تحقق هذا الإنجاز.
                                    
                                    أهدي ثمرة هذا الجهد:
                                    • إلى أهلي الكرام الذين كانوا السند والداعم الأول في مسيرتي العلمية والعملية.
                                    • إلى أصدقائي وزملائي الذين شاركوني الطريق ووقفوا بجانبي في لحظات التحدي والنجاح.
                                    • إلى دكاترتي في الجامعة الذين منحوني من علمهم وتوجيهاتهم وكانوا المنارة التي أضاءت لي دروب المعرفة.
                                    • شكر خاص وتقدير للأستاذ عبدالله داعر والأستاذة امتياز الصمصام على ما قدموه من جهد وتعب وصبر معنا، فقد كان لهم دور بارز في نجاحنا.
                                """.trimIndent(),
                                fontSize = 13.sp,
                                color = TextPrimaryLight,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }

            ReportSection.SUPERVISOR -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "شهادة المشرف ولجنة المناقشة",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniNavyPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "أقر أنا المشرف بأن مشروع التخرج المعنون بـ (منصة التعليم الإلكتروني لمركز الحاسب الآلي بجامعة صنعاء) والذي أعدته الطالبة: رغد حمود حسين العصري، قد تم تحت إشرافي في قسم تكنولوجيا المعلومات استكمالاً لمتطلبات الحصول على شهادة الدبلوم.",
                                fontSize = 13.sp,
                                color = TextPrimaryLight,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("لجنة المناقشة والإشراف:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = UniNavyPrimary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("1. الأستاذ / عبدالله داعر", fontSize = 12.sp)
                                    Text("2. الأستاذة / امتياز الصمصام", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            ReportSection.FEASIBILITY -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "دراسة الجدوى ومنهجية المشروع",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniNavyPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = """
                                    1. الجدوى التشغيلية: تلبي حاجة المركز لأتمتة تنظيم المناهج وإجراء اختبارات تحديد المستوى إلكترونياً وتخفيف العبء الإداري.
                                    2. الجدوى التقنية: توفر البنية التحتية من مخدمات وقواعد بيانات SQL Server وشبكة محلية LAN وأجهزة الحاسب للمختبرات.
                                    3. الجدوى المالية: توفير تكاليف الطباعة الورقية للملازم وتوليد عوائد عبر بوابة حجز الدورات الاحترافية المدفوعة.
                                    4. الجدوى الزمنية: اعتماد منهجية التطوير التكرارية المرنة (Iterative Agile) لتقسيم المشروع لدورات سريعة وضمان التسليم بجودة عالية.
                                """.trimIndent(),
                                fontSize = 13.sp,
                                color = TextPrimaryLight,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }

            ReportSection.SYSTEM_ANALYSIS -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "تحليل المتطلبات وسيناريو النظام",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniNavyPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = """
                                    السيناريو التشغيلي:
                                    يقوم الطالب بالدخول إلى منصة مركز الحاسب الآلي، ثم يجري اختبار تحديد المستوى. يقوم النظام تلقائياً بتحديد مستواه وعرض المسارات التعليمية المناسبة، ثم يطلب الطالب الالتحاق بالدورة ويسدد الرسوم عبر الحساب البنكي، ليحصل فوراً على القبول وموعد القاعة والجدول الدراسي.
                                    
                                    المتطلبات الوظيفية:
                                    • تصفح المسارات والدورات ومحتوى الدروس.
                                    • نظام تحديد المستوى الآلي ورصد الدرجات.
                                    • إدارة الدروس والاختبارات القصيرة بعد كل درس.
                                    • إصدار الشهادات الرقمية المعتمدة.
                                    • حقيبة الطالب لتحميل الأدوات ومحرك SQL Server.
                                """.trimIndent(),
                                fontSize = 13.sp,
                                color = TextPrimaryLight,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
