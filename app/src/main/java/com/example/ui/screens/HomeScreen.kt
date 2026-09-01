package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CourseEntity
import com.example.data.model.ITTrackEntity
import com.example.data.model.UserEntity
import com.example.ui.components.CourseCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab

@Composable
fun HomeScreen(
    user: UserEntity?,
    tracks: List<ITTrackEntity>,
    featuredCourses: List<CourseEntity>,
    onNavigateTab: (AppTab) -> Unit,
    onCourseClick: (CourseEntity) -> Unit,
    onEnrollClick: (CourseEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Academic Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_banner),
                        contentDescription = "بانر مركز الحاسب الآلي بجامعة صنعاء",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        UniNavyDark.copy(alpha = 0.85f),
                                        UniNavyDark
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GoldAccent
                        ) {
                            Text(
                                text = "مشروع تخرج تكنولوجيا المعلومات",
                                color = UniNavyDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "منصة التعليم والتدريب الإلكتروني الذكية",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "مركز الحاسب الآلي - جامعة صنعاء | إدارة وتنسيق الدروس والاختبارات",
                            color = GoldLight,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Quranic Verse & Graduation Dedication Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = UniNavyPrimary)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "﴿ اقْرَأْ بِاسْمِ رَبِّكَ الَّذِي خَلَقَ ۝ خَلَقَ الْإِنسَانَ مِنْ عَلَقٍ ۝ اقْرَأْ وَرَبُّكَ الْأَكْرَمُ ۝ الَّذِي عَلَّمَ بِالْقَلَمِ ۝ عَلَّمَ الْإِنسَانَ مَا لَمْ يَعْلَمْ ﴾",
                        color = GoldLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = UniNavyLight)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "إعداد الطالبة: رغد حمود حسين العصري",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "إشراف: أ. عبدالله داعر & أ. امتياز الصمصام",
                            color = GoldAccent,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Career Guidance / Placement Test Callout
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("placement_callout_card")
                    .clickable { onNavigateTab(AppTab.PLACEMENT) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldLight),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(EmeraldSuccess, EmeraldDark)))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(EmeraldSuccess),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "اختبار تحديد المستوى والتوجيه المهني",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                        Text(
                            text = "أجب عن 10 أسئلة لمعرفة مستواك الأكاديمي والمسار الأنسب لميولك",
                            fontSize = 11.sp,
                            color = TextPrimaryLight,
                            lineHeight = 16.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "ابدأ",
                        tint = EmeraldDark
                    )
                }
            }
        }

        // Section Title: IT Tracks (المسارات والتخصصات)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "المسارات والتخصصات التقنية",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = UniNavyPrimary
                )
                TextButton(onClick = { onNavigateTab(AppTab.COURSES) }) {
                    Text("عرض كل الدورات", fontSize = 12.sp, color = UniNavyLight)
                }
            }
        }

        // Tracks Horizontal Slider
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tracks) { track ->
                    Card(
                        modifier = Modifier
                            .width(220.dp)
                            .testTag("track_card_${track.id}")
                            .clickable { onNavigateTab(AppTab.COURSES) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(track.colorHex).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (track.id) {
                                        "track_dev" -> Icons.Default.Code
                                        "track_db" -> Icons.Default.Storage
                                        "track_net" -> Icons.Default.Router
                                        "track_sec" -> Icons.Default.Security
                                        else -> Icons.Default.Psychology
                                    },
                                    contentDescription = null,
                                    tint = Color(track.colorHex),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = track.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = track.description,
                                fontSize = 11.sp,
                                color = TextSecondaryLight,
                                maxLines = 3,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Featured Academic & Training Courses
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "المناهج والدورات التدريبية المعتمدة",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = UniNavyPrimary
                )
                Text(
                    text = "${featuredCourses.size} مادة",
                    fontSize = 12.sp,
                    color = TextSecondaryLight
                )
            }
        }

        items(featuredCourses) { course ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                CourseCard(
                    course = course,
                    onCourseClick = { onCourseClick(course) },
                    onEnrollClick = { onEnrollClick(course) }
                )
            }
        }
    }
}
