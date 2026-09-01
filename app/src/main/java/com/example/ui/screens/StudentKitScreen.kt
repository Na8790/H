package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CourseEntity
import com.example.data.model.EnrollmentEntity
import com.example.data.model.StudentToolEntity
import com.example.ui.components.AppSearchBar
import com.example.ui.theme.*

@Composable
fun StudentKitScreen(
    tools: List<StudentToolEntity>,
    enrollments: List<EnrollmentEntity>,
    onCourseClickById: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf("قواعد بيانات", "بيئات تطوير", "مذكرات ومراجع", "أكواد ومشاريع")

    val filteredTools = tools.filter { tool ->
        val matchesCat = selectedCategory == null || tool.category == selectedCategory
        val matchesSearch = searchQuery.isBlank() ||
                tool.title.contains(searchQuery, ignoreCase = true) ||
                tool.description.contains(searchQuery, ignoreCase = true)
        matchesCat && matchesSearch
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("student_kit_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Header Card: Student Kit Introduction
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = UniNavyPrimary)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(GoldAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Workspaces,
                            contentDescription = null,
                            tint = UniNavyDark,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "حقيبة الطالب والبرمجيات الأكاديمية",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "تحميل حزم البرامج وقواعد البيانات والمذكرات المعتمدة لمركز الحاسب",
                            color = GoldLight,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Section: Active Enrollments & Classes
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                Text(
                    text = "سجل التسجيل والالتحاق بالقاعات (${enrollments.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = UniNavyPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        if (enrollments.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight)
                ) {
                    Text(
                        text = "لا توجد دورات مسجلة حالياً. يمكنك تصفح قسم الدورات والتسجيل في المجموعات.",
                        fontSize = 12.sp,
                        color = TextSecondaryLight,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        } else {
            items(enrollments) { enrollment ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .testTag("enrollment_card_${enrollment.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = enrollment.courseTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniNavyPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (enrollment.status == "معتمد" || enrollment.status == "مكتمل") EmeraldLight else AmberWarning.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = enrollment.status,
                                    color = if (enrollment.status == "معتمد" || enrollment.status == "مكتمل") EmeraldDark else AmberWarning,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("القاعة:", fontSize = 10.sp, color = TextSecondaryLight)
                                Text(enrollment.labName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Column {
                                Text("الموعد:", fontSize = 10.sp, color = TextSecondaryLight)
                                Text(enrollment.scheduleDetails, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                            Column {
                                Text("رقم الإشعار:", fontSize = 10.sp, color = TextSecondaryLight)
                                Text(enrollment.paymentReference, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }

        // Section: Software & Materials Downloads
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "الأدوات والمراجع القابلة للتحميل",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = UniNavyPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                AppSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "ابحث عن أدوات SQL، المحررات أو المراجع..."
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Category filter chips
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("الكل", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = UniNavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = UniNavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Tools List
        items(filteredTools) { tool ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("tool_card_${tool.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(UniNavyPrimary.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (tool.category) {
                                "قواعد بيانات" -> Icons.Default.Storage
                                "بيئات تطوير" -> Icons.Default.Code
                                "مذكرات ومراجع" -> Icons.Default.MenuBook
                                else -> Icons.Default.DataObject
                            },
                            contentDescription = null,
                            tint = UniNavyPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = tool.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SurfaceLight
                            ) {
                                Text(
                                    text = tool.version,
                                    fontSize = 9.sp,
                                    color = TextSecondaryLight,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = tool.description,
                            fontSize = 11.sp,
                            color = TextSecondaryLight,
                            maxLines = 2,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "الحجم: ${tool.sizeText}",
                                fontSize = 10.sp,
                                color = UniNavyLight,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "المصدر: ${tool.officialSite}",
                                fontSize = 10.sp,
                                color = TextSecondaryLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    FilledTonalIconButton(
                        onClick = {
                            Toast.makeText(context, "جاري تحضير رابط تحميل ${tool.title} من ${tool.officialSite}", Toast.LENGTH_SHORT).show()
                            try {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(tool.downloadUrl))
                                context.startActivity(browserIntent)
                            } catch (e: Exception) {
                                // Handled safely
                            }
                        },
                        modifier = Modifier.testTag("download_tool_${tool.id}"),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = UniNavyPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "تحميل",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
