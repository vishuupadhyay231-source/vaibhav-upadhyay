package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BatchItem
import com.example.data.model.ClassItem
import com.example.ui.MainViewModel
import com.example.ui.components.CreativeLiveJoinButton
import com.example.ui.components.CreativeLiveMeetLogo
import com.example.ui.components.openExternalUrl
import com.example.ui.theme.*

@Composable
fun ClassesAndMeetScreen(
    viewModel: MainViewModel,
    classes: List<ClassItem>,
    batches: List<BatchItem>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("🔴 Live & Scheduled", "👥 Active Batches", "📅 Timetable")

    // Find if there is an active live class
    val liveClass = classes.firstOrNull { it.status.equals("Live", ignoreCase = true) } ?: classes.firstOrNull()

    var showHandRaiseDialog by remember { mutableStateOf(false) }
    var handRaiseCount by remember { mutableIntStateOf(14) }
    var hasRaisedHand by remember { mutableStateOf(false) }

    var selectedSemesterFilter by remember { mutableStateOf("All Semesters") }
    val semesterFilters = listOf("All Semesters", "BCA Sem 1", "BCA Sem 2", "BCA Sem 3", "BCA Sem 4", "BCA Sem 5", "BCA Sem 6")

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("classes_meet_screen")
    ) {
        // Modern Pill Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            edgePadding = 16.dp,
            divider = {}
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                // Live Classes & Google Meet Hub
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Creative Live Studio Hero Card (if a class exists)
                    if (liveClass != null) {
                        item {
                            CreativeLiveHeroCard(
                                liveClass = liveClass,
                                onJoinMeet = {
                                    openExternalUrl(context, liveClass.meetLink)
                                },
                                onCopyLink = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Meet Link", liveClass.meetLink)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Google Meet link copied!", Toast.LENGTH_SHORT).show()
                                },
                                onRaiseHand = {
                                    if (!hasRaisedHand) {
                                        hasRaisedHand = true
                                        handRaiseCount++
                                        Toast.makeText(context, "✋ Hand raised in live class! Instructor notified.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        hasRaisedHand = false
                                        handRaiseCount--
                                        Toast.makeText(context, "Hand lowered.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                hasRaisedHand = hasRaisedHand,
                                handRaiseCount = handRaiseCount
                            )
                        }
                    }

                    // Classroom Readiness & Studio Features Row
                    item {
                        ClassroomReadinessCard()
                    }

                    // Filter Semester chips
                    item {
                        Column {
                            Text(
                                text = "Upcoming Class Sessions",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(semesterFilters) { sem ->
                                    val isSelected = selectedSemesterFilter == sem
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedSemesterFilter = sem },
                                        label = { Text(sem, fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = BrandBlue.copy(alpha = 0.2f),
                                            selectedLabelColor = BrandBlue
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Scheduled Class Items
                    items(classes, key = { it.id }) { classItem ->
                        CreativeClassScheduleCard(
                            classItem = classItem,
                            onJoinMeet = {
                                openExternalUrl(context, classItem.meetLink)
                            },
                            onCopyLink = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Meet Link", classItem.meetLink)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Link copied!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
            1 -> {
                // Active Batches Tab
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = BrandCyan.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = null,
                                    tint = BrandCyan,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Official BCA & Tech Batches",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Live enrolled cohorts with mentorship by Vishu Sir.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    items(batches, key = { it.id }) { batch ->
                        CreativeBatchCard(batch = batch)
                    }
                }
            }
            2 -> {
                // Weekly Timetable
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "Weekly Live Lecture Schedule",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    val days = listOf(
                        Triple("Monday", "Data Structures & Algorithms (BCA 3rd)", "06:00 PM - 07:30 PM"),
                        Triple("Tuesday", "Java OOPs & GUI Development", "06:00 PM - 07:30 PM"),
                        Triple("Wednesday", "Database Management & SQL Hands-on", "07:00 PM - 08:30 PM"),
                        Triple("Thursday", "Web Tech (HTML, CSS, JS & PHP)", "06:00 PM - 07:30 PM"),
                        Triple("Friday", "Python & Data Science Workshop", "06:30 PM - 08:00 PM"),
                        Triple("Saturday", "Live Code Doubt Clearing & Project Review", "05:00 PM - 07:00 PM"),
                        Triple("Sunday", "Special Coding Marathon & Mock Tests", "11:00 AM - 01:00 PM")
                    )

                    items(days, key = { it.first }) { (day, subject, time) ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = day, fontWeight = FontWeight.Bold, color = BrandBlue, fontSize = 14.sp)
                                    Text(text = subject, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(text = "⏰ $time", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = BrandSuccess.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "Google Meet",
                                        color = BrandSuccess,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreativeLiveHeroCard(
    liveClass: ClassItem,
    onJoinMeet: () -> Unit,
    onCopyLink: () -> Unit,
    onRaiseHand: () -> Unit,
    hasRaisedHand: Boolean,
    handRaiseCount: Int
) {
    val isLive = liveClass.status.equals("Live", ignoreCase = true)

    // Pulse animation for LIVE badge
    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F172A)
        ),
        border = BorderStroke(
            1.5.dp,
            Brush.linearGradient(
                colors = if (isLive) listOf(Color(0xFFEF4444), Color(0xFF8B5CF6), Color(0xFF3B82F6))
                else listOf(Color(0xFF3B82F6), Color(0xFF06B6D4))
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("creative_live_hero_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Status + Broadcast Mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isLive) Color(0xFFEF4444).copy(alpha = 0.25f) else BrandBlue.copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, if (isLive) Color(0xFFEF4444) else BrandBlue)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .scale(if (isLive) pulseScale else 1f)
                                .clip(CircleShape)
                                .background(if (isLive) Color(0xFFEF4444) else BrandBlue)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isLive) "🔴 LIVE STREAMING" else "SCHEDULED CLASS",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "128 Students in Room",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Class Title
            Text(
                text = liveClass.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = liveClass.description,
                fontSize = 13.sp,
                color = Color(0xFF94A3B8),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Instructor & Time Metadata Pill
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1E293B),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(BrandBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "V",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = liveClass.teacher,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Lead Educator • BCA Mentor",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Text(
                        text = "⏰ ${liveClass.time}",
                        color = BrandCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: Creative Live Join Meet Button + Raise Hand + Copy Link
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Creative Hero Join Button with animated logo & broadcast pulse
                CreativeLiveJoinButton(
                    onClick = onJoinMeet,
                    isLive = isLive
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Raise Hand interactive button
                    OutlinedButton(
                        onClick = onRaiseHand,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (hasRaisedHand) Color(0xFFFEF3C7) else Color(0xFF1E293B),
                            contentColor = if (hasRaisedHand) Color(0xFFD97706) else Color.White
                        ),
                        border = BorderStroke(1.dp, if (hasRaisedHand) Color(0xFFD97706) else Color(0xFF334155)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("raise_hand_button")
                    ) {
                        Text(
                            text = if (hasRaisedHand) "✋ Hand Raised ($handRaiseCount)" else "✋ Raise Hand ($handRaiseCount)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Copy Link Icon Button
                    OutlinedButton(
                        onClick = onCopyLink,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFF1E293B),
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Meet Link",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Copy Link",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClassroomReadinessCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "⚡ Classroom Live Readiness Check",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ReadinessPill(icon = Icons.Default.Mic, title = "Mic: Ready", color = BrandSuccess)
                ReadinessPill(icon = Icons.Default.Videocam, title = "Cam: HD", color = BrandBlue)
                ReadinessPill(icon = Icons.Default.ScreenShare, title = "Screen: Active", color = BrandCyan)
                ReadinessPill(icon = Icons.Default.Description, title = "Notes: Sync", color = Color(0xFF8B5CF6))
            }
        }
    }
}

@Composable
fun ReadinessPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = color)
        }
    }
}

@Composable
fun CreativeClassScheduleCard(
    classItem: ClassItem,
    onJoinMeet: () -> Unit,
    onCopyLink: () -> Unit
) {
    val isLive = classItem.status.equals("Live", ignoreCase = true)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        border = if (isLive) BorderStroke(1.dp, BrandError.copy(alpha = 0.5f)) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isLive) BrandError.copy(alpha = 0.15f) else BrandBlue.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isLive) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(BrandError)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = if (isLive) "LIVE NOW" else classItem.status.uppercase(),
                            color = if (isLive) BrandError else BrandBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                Text(
                    text = "${classItem.date} • ${classItem.time}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = classItem.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = classItem.description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = classItem.teacher,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = onCopyLink,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Copy Meet Link",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    CreativeLiveJoinButton(
                        onClick = onJoinMeet,
                        isLive = isLive,
                        isCompact = true,
                        text = if (isLive) "Join Meet" else "Open Meet"
                    )
                }
            }
        }
    }
}

@Composable
fun CreativeBatchCard(batch: BatchItem) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = batch.course,
                    color = BrandBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BrandSuccess.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = batch.status,
                        color = BrandSuccess,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = batch.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = batch.description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🗓 ${batch.schedule}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "👥 ${batch.enrolledStudents} Enrolled",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

