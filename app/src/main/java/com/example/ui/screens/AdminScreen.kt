package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.*
import com.example.ui.MainViewModel
import com.example.ui.components.openExternalUrl
import com.example.ui.theme.*

@Composable
fun AdminScreen(
    viewModel: MainViewModel,
    notes: List<NoteItem>,
    classes: List<ClassItem>,
    announcements: List<AnnouncementItem>,
    auditLogs: List<AuditLog>,
    sections: List<DynamicSection>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedAdminTab by remember { mutableIntStateOf(0) }
    val adminTabs = listOf(
        "Users & Directory",
        "UI & Animations",
        "Notes Moderation",
        "Classes & Meet",
        "Community Feed",
        "Front Posters",
        "Creator ID & Contact",
        "Payment Gateway",
        "App Update & APK",
        "Announcements",
        "Dynamic Sections",
        "Audit Logs"
    )

    val allUsers by viewModel.allUsers.collectAsState()
    val uiSettings by viewModel.adminUiSettings.collectAsState()
    val posts by viewModel.communityPosts.collectAsState()
    val comments by viewModel.allComments.collectAsState()
    val updateInfo by viewModel.appUpdateInfo.collectAsState()
    val banners by viewModel.carouselBanners.collectAsState()
    val paymentConfig by viewModel.paymentGatewayConfig.collectAsState()
    val creatorIntro by viewModel.creatorIntro.collectAsState()

    var showAddUserDialog by remember { mutableStateOf(false) }
    var showAddClassDialog by remember { mutableStateOf(false) }
    var classToEdit by remember { mutableStateOf<ClassItem?>(null) }
    var showAddAnnouncementDialog by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var showAddBannerDialog by remember { mutableStateOf(false) }
    var bannerToEdit by remember { mutableStateOf<CarouselBannerItem?>(null) }
    var postToEdit by remember { mutableStateOf<CommunityPostItem?>(null) }
    var showCreateAdminPostDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_screen_panel")
    ) {
        // Admin Top Bar
        Surface(
            color = Color(0xFF0F172A),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = BrandCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Admin Control Suite",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Total Registered: ${allUsers.size} Users",
                            color = BrandCyan,
                            fontSize = 11.sp
                        )
                    }
                }

                Button(
                    onClick = { viewModel.logoutAdmin() },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandError),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("admin_logout_button")
                ) {
                    Icon(Icons.Default.Logout, contentDescription = "Logout", modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Logout Admin", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedAdminTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            adminTabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedAdminTab == index,
                    onClick = { selectedAdminTab = index },
                    text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }
        }

        // Admin Tab Content
        when (selectedAdminTab) {
            0 -> {
                // Tab 0: Registered Users & Directory
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "User Directory (${allUsers.size} Users)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Manage registered student accounts, Google IDs & admin privileges",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.adminClearFakeUsers()
                                            Toast.makeText(context, "Cleaned fake & test accounts. Only real admin and verified users kept.", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandError),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Clean Fake Users", fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = { showAddUserDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Add User", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    items(allUsers, key = { it.email }) { u ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (u.isBlocked) BrandError.copy(alpha = 0.1f)
                                else if (u.role == "Admin") BrandAmber.copy(alpha = 0.12f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(if (u.role == "Admin") BrandAmber else BrandBlue),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = u.fullName.take(1).uppercase(),
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(text = u.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text(text = u.email, fontSize = 12.sp, color = BrandBlue)
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (u.role == "Admin") BrandAmber.copy(alpha = 0.2f) else BrandSuccess.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = u.role.uppercase(),
                                            color = if (u.role == "Admin") BrandAmber else BrandSuccess,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Course: ${u.course} • Sem ${u.semester}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Device: ${u.deviceModel}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Joined: ${u.joinedDate}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Status: ${u.lastActive}",
                                        fontSize = 11.sp,
                                        color = if (u.isBlocked) BrandError else BrandSuccess
                                    )
                                }

                                Divider(modifier = Modifier.padding(vertical = 8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Toggle Role
                                    OutlinedButton(
                                        onClick = {
                                            val newRole = if (u.role == "Admin") "Student" else "Admin"
                                            viewModel.adminUpdateUserRole(u.email, newRole)
                                            Toast.makeText(context, "${u.fullName} role changed to $newRole", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text(if (u.role == "Admin") "Make Student" else "Make Admin", fontSize = 11.sp)
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Toggle Block
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.adminToggleUserBlocked(u.email, !u.isBlocked)
                                            Toast.makeText(context, if (u.isBlocked) "User unblocked" else "User blocked", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = if (u.isBlocked) BrandSuccess else BrandAmber
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text(if (u.isBlocked) "Unblock" else "Block", fontSize = 11.sp)
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Delete User
                                    IconButton(
                                        onClick = {
                                            viewModel.adminDeleteUser(u)
                                            Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BrandError, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Tab 1: Global UI, Colors & Animation Speed Control
                val currentSettings = uiSettings ?: AdminUiSettings()
                var animSpeed by remember(currentSettings) { mutableStateOf(currentSettings.animationSpeed) }
                var buttonStyle by remember(currentSettings) { mutableStateOf(currentSettings.buttonStyle) }
                var adminColor by remember(currentSettings) { mutableStateOf(currentSettings.adminPostColorHex) }
                var userColor by remember(currentSettings) { mutableStateOf(currentSettings.userPostColorHex) }
                var allowPost by remember(currentSettings) { mutableStateOf(currentSettings.allowStudentPosting) }
                var allowNotes by remember(currentSettings) { mutableStateOf(currentSettings.allowStudentNoteSharing) }
                var showCarousel by remember(currentSettings) { mutableStateOf(currentSettings.showHeroCarousel) }
                var bannerAnnouncement by remember(currentSettings) { mutableStateOf(currentSettings.activeAnnouncementBanner) }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Global Animation Speed Control",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Centrally control transition and animation speeds across all user devices",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf("Instant", "Fast", "Normal", "Smooth", "Disabled").forEach { speed ->
                                        FilterChip(
                                            selected = animSpeed == speed,
                                            onClick = { animSpeed = speed },
                                            label = { Text(speed, fontSize = 11.sp) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                Divider()

                                Text(
                                    text = "Community Post & Badge Colors",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = adminColor,
                                        onValueChange = { adminColor = it },
                                        label = { Text("Admin Post Color (Hex)") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = userColor,
                                        onValueChange = { userColor = it },
                                        label = { Text("Student Post Color (Hex)") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Divider()

                                Text(
                                    text = "Button Styling Preference",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf("Modern Rounded", "Pill Shape", "Elevated Glass").forEach { style ->
                                        FilterChip(
                                            selected = buttonStyle == style,
                                            onClick = { buttonStyle = style },
                                            label = { Text(style, fontSize = 11.sp) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                Divider()

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Allow Students to Create Community Posts", fontSize = 13.sp)
                                    Switch(checked = allowPost, onCheckedChange = { allowPost = it })
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Allow Students to Upload Live Notes", fontSize = 13.sp)
                                    Switch(checked = allowNotes, onCheckedChange = { allowNotes = it })
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Show Dynamic Rotating Hero Carousel", fontSize = 13.sp)
                                    Switch(checked = showCarousel, onCheckedChange = { showCarousel = it })
                                }

                                OutlinedTextField(
                                    value = bannerAnnouncement,
                                    onValueChange = { bannerAnnouncement = it },
                                    label = { Text("Global Marquee Announcement Banner") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Button(
                                    onClick = {
                                        val newSettings = currentSettings.copy(
                                            animationSpeed = animSpeed,
                                            buttonStyle = buttonStyle,
                                            adminPostColorHex = adminColor,
                                            userPostColorHex = userColor,
                                            allowStudentPosting = allowPost,
                                            allowStudentNoteSharing = allowNotes,
                                            showHeroCarousel = showCarousel,
                                            activeAnnouncementBanner = bannerAnnouncement
                                        )
                                        viewModel.adminUpdateUiSettings(newSettings)
                                        Toast.makeText(context, "UI and Animation settings updated live!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Save UI & Animation Configuration")
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Tab 2: Notes Moderation & Live Sharing
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Button(
                            onClick = { showAddNoteDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Publish Official Note")
                        }
                    }

                    items(notes, key = { it.id }) { note ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (note.isApproved)
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                else
                                    BrandAmber.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (note.isApproved) BrandSuccess.copy(alpha = 0.2f) else BrandAmber.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = if (note.isApproved) "APPROVED" else "PENDING REVIEW",
                                            color = if (note.isApproved) BrandSuccess else BrandAmber,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Text(
                                        text = "${note.course} • Sem ${note.semester}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = note.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "Uploader: ${note.uploaderName} (${note.uploaderEmail})", fontSize = 11.sp, color = BrandBlue)
                                Text(text = note.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (!note.isApproved) {
                                        Button(
                                            onClick = {
                                                viewModel.adminApproveNote(note)
                                                Toast.makeText(context, "Note approved!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = BrandSuccess),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text("Approve", fontSize = 11.sp)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            viewModel.adminDeleteNote(note)
                                            Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandError),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Delete", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // Tab 3: Classes & Meet
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Button(
                            onClick = { showAddClassDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Schedule Live Class / Meet")
                        }
                    }

                    items(classes, key = { it.id }) { c ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = c.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        if (c.isLive) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = BrandError
                                            ) {
                                                Text(
                                                    text = "LIVE",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.sp,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(text = "${c.teacher} • ${c.date} (${c.time})", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = c.meetLink, fontSize = 11.sp, color = BrandBlue, maxLines = 1)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { classToEdit = c }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Class", tint = BrandBlue)
                                    }
                                    IconButton(onClick = { viewModel.adminDeleteClass(c) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BrandError)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            4 -> {
                // Tab 4: Community Feed Moderation
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = BrandBlue.copy(alpha = 0.12f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Official Community Feeds & Posts",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Publish faculty notices, tips & announcements directly from Admin Dashboard",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { showCreateAdminPostDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Icon(Icons.Default.AddComment, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("New Post", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "All Community Posts (${posts.size} Posts)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(posts, key = { it.id }) { p ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${p.authorName} (${p.authorRole})",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        if (p.isPinned) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(Icons.Default.PushPin, contentDescription = "Pinned", tint = BrandAmber, modifier = Modifier.size(16.dp))
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = { postToEdit = p },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BrandBlue, modifier = Modifier.size(16.dp))
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = { viewModel.deleteCommunityPost(p) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BrandError, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                                Text(text = p.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = p.content, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 3)
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Recent Comments (${comments.size} Comments)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(comments, key = { it.id }) { comm ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "${comm.authorName}: ${comm.commentText}", fontSize = 12.sp)
                                    Text(text = "Post #${comm.postId} • ${comm.timestamp}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(
                                    onClick = { viewModel.deletePostComment(comm) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = BrandError, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            5 -> {
                // Tab 5: Front Posters & Carousel Banners
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Button(
                            onClick = { showAddBannerDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Front Poster / Banner")
                        }
                    }

                    items(banners, key = { it.id }) { b ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = b.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = b.subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "Target: ${b.actionTarget} • Badge: ${b.badgeText}", fontSize = 11.sp, color = BrandCyan)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { bannerToEdit = b }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BrandBlue)
                                    }
                                    IconButton(onClick = { viewModel.adminDeleteCarouselBanner(b) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BrandError)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            6 -> {
                // Tab 6: Creator ID & Contact Info
                var avatarUrl by remember(creatorIntro) { mutableStateOf(creatorIntro?.avatarUrl ?: creatorIntro?.photoUrl ?: "") }
                var fullName by remember(creatorIntro) { mutableStateOf(creatorIntro?.fullName ?: creatorIntro?.name ?: "Vishu Upadhyay") }
                var roleTitle by remember(creatorIntro) { mutableStateOf(creatorIntro?.title ?: creatorIntro?.roleTitle ?: "Founder & Lead Developer • Vishu Connect") }
                var bio by remember(creatorIntro) { mutableStateOf(creatorIntro?.bio ?: "Building modern, accessible educational tools for BCA, BTech, and Computer Science students across the globe.") }
                var igUsername by remember(creatorIntro) { mutableStateOf(creatorIntro?.instagramUsername ?: "@vishu_upadhyay_") }
                var igUrl by remember(creatorIntro) { mutableStateOf(creatorIntro?.instagramUrl ?: "https://instagram.com/vishu_upadhyay_") }
                var waNumber by remember(creatorIntro) { mutableStateOf(creatorIntro?.whatsappNumber ?: "+91 9876543210") }
                var waUrl by remember(creatorIntro) { mutableStateOf(creatorIntro?.whatsappUrl ?: "https://wa.me/919876543210") }
                var waMsg by remember(creatorIntro) { mutableStateOf(creatorIntro?.whatsappMessage ?: "Hello Vishu Sir, I have a doubt/note on Vishu Connect") }
                var githubUrl by remember(creatorIntro) { mutableStateOf(creatorIntro?.githubUrl ?: "https://github.com/vishuupadhyay") }
                var youtubeUrl by remember(creatorIntro) { mutableStateOf(creatorIntro?.youtubeUrl ?: "https://youtube.com/@vishu_connect") }
                var linkedinUrl by remember(creatorIntro) { mutableStateOf(creatorIntro?.linkedinUrl ?: "https://linkedin.com/in/vishuupadhyay") }
                var telegramUrl by remember(creatorIntro) { mutableStateOf(creatorIntro?.telegramUrl ?: "https://t.me/vishu_connect") }
                var emailAddress by remember(creatorIntro) { mutableStateOf(creatorIntro?.email ?: "vishuupadhyay231@gmail.com") }
                var websiteUrl by remember(creatorIntro) { mutableStateOf(creatorIntro?.websiteUrl ?: "https://vishuconnect.edu") }
                var additionalInfo by remember(creatorIntro) { mutableStateOf(creatorIntro?.additionalInfo ?: "Available for academic guidance, project reviews & BCA mentoring.") }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Creator ID Card & Social Links Manager",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Manage your photo, bio, Instagram, WhatsApp, YouTube, GitHub, Telegram & LinkedIn live across the app",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                OutlinedTextField(
                                    value = fullName,
                                    onValueChange = { fullName = it },
                                    label = { Text("Full Name") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = roleTitle,
                                    onValueChange = { roleTitle = it },
                                    label = { Text("Designation / Title") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = avatarUrl,
                                    onValueChange = { avatarUrl = it },
                                    label = { Text("Profile Photo / Avatar Image URL") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = bio,
                                    onValueChange = { bio = it },
                                    label = { Text("Creator Bio") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 2
                                )

                                Divider(modifier = Modifier.padding(vertical = 4.dp))
                                Text("🌐 Social & Communication Channels", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandBlue)

                                OutlinedTextField(
                                    value = igUsername,
                                    onValueChange = { igUsername = it },
                                    label = { Text("Instagram Handle (e.g. @vishu_upadhyay_)") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = igUrl,
                                    onValueChange = { igUrl = it },
                                    label = { Text("Instagram Profile URL") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = waNumber,
                                    onValueChange = { waNumber = it },
                                    label = { Text("WhatsApp Phone Number") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = waUrl,
                                    onValueChange = { waUrl = it },
                                    label = { Text("Direct WhatsApp Chat Link") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = waMsg,
                                    onValueChange = { waMsg = it },
                                    label = { Text("Default WhatsApp Message Template") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = youtubeUrl,
                                    onValueChange = { youtubeUrl = it },
                                    label = { Text("YouTube Channel Link") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = githubUrl,
                                    onValueChange = { githubUrl = it },
                                    label = { Text("GitHub Profile Link") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = telegramUrl,
                                    onValueChange = { telegramUrl = it },
                                    label = { Text("Telegram Channel / Group Link") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = linkedinUrl,
                                    onValueChange = { linkedinUrl = it },
                                    label = { Text("LinkedIn Profile Link") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = emailAddress,
                                    onValueChange = { emailAddress = it },
                                    label = { Text("Creator Email Address") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = websiteUrl,
                                    onValueChange = { websiteUrl = it },
                                    label = { Text("Portfolio / Official Website Link") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Button(
                                    onClick = {
                                        val updatedConfig = (creatorIntro ?: CreatorIntroConfig()).copy(
                                            name = fullName,
                                            fullName = fullName,
                                            roleTitle = roleTitle,
                                            title = roleTitle,
                                            bio = bio,
                                            avatarUrl = avatarUrl,
                                            photoUrl = avatarUrl,
                                            instagramUsername = igUsername,
                                            instagramUrl = igUrl,
                                            whatsappUrl = waUrl,
                                            whatsappNumber = waNumber,
                                            whatsappMessage = waMsg,
                                            githubUrl = githubUrl,
                                            github = githubUrl,
                                            youtubeUrl = youtubeUrl,
                                            youtube = youtubeUrl,
                                            linkedinUrl = linkedinUrl,
                                            linkedin = linkedinUrl,
                                            telegramUrl = telegramUrl,
                                            email = emailAddress,
                                            websiteUrl = websiteUrl,
                                            portfolio = websiteUrl,
                                            additionalInfo = additionalInfo
                                        )
                                        viewModel.adminUpdateCreatorIntro(updatedConfig)
                                        Toast.makeText(context, "Creator Profile & all Social Links updated live!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Save ID Card & Social Links Live")
                                }
                            }
                        }
                    }
                }
            }

            7 -> {
                // Tab 7: Payment Gateway Configuration Tab
                var provider by remember(paymentConfig) { mutableStateOf(paymentConfig?.provider ?: "UPI Direct & Razorpay") }
                var merchantUpiId by remember(paymentConfig) { mutableStateOf(paymentConfig?.merchantUpiId ?: "vishuupadhyay231@okaxis") }
                var razorpayKeyId by remember(paymentConfig) { mutableStateOf(paymentConfig?.razorpayKeyId ?: paymentConfig?.apiKey ?: "rzp_live_vishu2026_key") }
                var razorpayKeySecret by remember(paymentConfig) { mutableStateOf(paymentConfig?.razorpayKeySecret ?: paymentConfig?.secretKey ?: "") }
                var merchantName by remember(paymentConfig) { mutableStateOf(paymentConfig?.merchantName ?: "Vishu Upadhyay • Vishu Connect") }
                var qrCodeUrl by remember(paymentConfig) { mutableStateOf(paymentConfig?.qrCodeUrl ?: "https://api.qrserver.com/v1/create-qr-code/?size=280x280&data=upi://pay?pa=vishuupadhyay231@okaxis&pn=Vishu+Connect+Notes") }
                var isEnabled by remember(paymentConfig) { mutableStateOf(paymentConfig?.isEnabled ?: true) }
                var isTestMode by remember(paymentConfig) { mutableStateOf(paymentConfig?.isTestMode ?: false) }
                var instructions by remember(paymentConfig) { mutableStateOf(paymentConfig?.instructions ?: "Pay securely via any UPI App (GPay, PhonePe, Paytm, BHIM) or Credit/Debit Cards. Your download unlocks instantly upon verification.") }
                var supportWA by remember(paymentConfig) { mutableStateOf(paymentConfig?.supportWhatsApp ?: "+919876543210") }
                var supportEmail by remember(paymentConfig) { mutableStateOf(paymentConfig?.supportEmail ?: "vishuupadhyay231@gmail.com") }

                val providerOptions = listOf("UPI Direct & Razorpay", "Razorpay Payment Gateway", "UPI FastPay QR", "Custom Gateway")

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Payment Gateway & Merchant Key Manager",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Configure your Razorpay API Keys, UPI ID & QR Code. Paid notes & projects will seamlessly route transactions through these credentials.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Enable Payment System", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Text("Allow paid notes & project unlocks", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Switch(checked = isEnabled, onCheckedChange = { isEnabled = it })
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Test / Sandbox Mode", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Text(if (isTestMode) "Using Test API Keys" else "Live Production Mode", fontSize = 11.sp, color = if (isTestMode) BrandAmber else BrandSuccess)
                                    }
                                    Switch(checked = isTestMode, onCheckedChange = { isTestMode = it })
                                }

                                Divider()

                                Text("Select Active Gateway Provider:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(providerOptions) { opt ->
                                        FilterChip(
                                            selected = provider == opt,
                                            onClick = { provider = opt },
                                            label = { Text(opt, fontSize = 11.sp) }
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = merchantUpiId,
                                    onValueChange = { merchantUpiId = it },
                                    label = { Text("Primary Merchant UPI ID (GPay / PhonePe / Paytm)") },
                                    placeholder = { Text("vishuupadhyay231@okaxis") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = razorpayKeyId,
                                    onValueChange = { razorpayKeyId = it },
                                    label = { Text("Razorpay Key ID (rzp_live_... / rzp_test_...)") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = razorpayKeySecret,
                                    onValueChange = { razorpayKeySecret = it },
                                    label = { Text("Razorpay Key Secret (Optional / Private)") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = merchantName,
                                    onValueChange = { merchantName = it },
                                    label = { Text("Merchant / Store Display Name") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = qrCodeUrl,
                                    onValueChange = { qrCodeUrl = it },
                                    label = { Text("Custom UPI QR Code Generator Link") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = instructions,
                                    onValueChange = { instructions = it },
                                    label = { Text("Student Checkout Instructions") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 2
                                )

                                OutlinedTextField(
                                    value = supportWA,
                                    onValueChange = { supportWA = it },
                                    label = { Text("Payment Support WhatsApp Number") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = supportEmail,
                                    onValueChange = { supportEmail = it },
                                    label = { Text("Payment Support Email Address") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Button(
                                    onClick = {
                                        val newConfig = (paymentConfig ?: PaymentGatewayConfig()).copy(
                                            provider = provider,
                                            providerName = provider,
                                            merchantUpiId = merchantUpiId,
                                            upiId = merchantUpiId,
                                            razorpayKeyId = razorpayKeyId,
                                            razorpayKeySecret = razorpayKeySecret,
                                            apiKey = razorpayKeyId,
                                            secretKey = razorpayKeySecret,
                                            apiKeyOrSecret = razorpayKeyId,
                                            merchantName = merchantName,
                                            qrCodeUrl = qrCodeUrl,
                                            isEnabled = isEnabled,
                                            isTestMode = isTestMode,
                                            instructions = instructions,
                                            supportWhatsApp = supportWA,
                                            supportEmail = supportEmail
                                        )
                                        val error = viewModel.adminUpdatePaymentGateway(newConfig)
                                        if (error != null) {
                                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                        } else {
                                            Toast.makeText(context, "Payment Gateway & API Keys saved successfully!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandSuccess),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Save Payment Gateway Configuration")
                                }
                            }
                        }
                    }
                }
            }

            8 -> {
                // Tab 8: App Update & Remote APK (Admin Only)
                var verName by remember { mutableStateOf(updateInfo.latestVersionName) }
                var verCode by remember { mutableStateOf(updateInfo.latestVersionCode.toString()) }
                var apkUrl by remember { mutableStateOf(updateInfo.downloadUrl) }
                var notesText by remember { mutableStateOf(updateInfo.releaseNotes) }
                var apkSizeText by remember { mutableStateOf(updateInfo.apkSize) }
                var isForce by remember { mutableStateOf(updateInfo.isForceUpdate) }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "Remote APK Distribution & Update Control",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "When you publish a new update here, all user devices receive live update alerts automatically.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = BrandBlue.copy(alpha = 0.12f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandBlue.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("Broadcast Update to All Devices", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandBlue)
                                            Text("When you click 'Publish & Broadcast', all installed devices will immediately show the new version update prompt.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }

                                Divider(modifier = Modifier.padding(vertical = 4.dp))

                                OutlinedTextField(value = verName, onValueChange = { verName = it }, label = { Text("Version Name (e.g. 2.0.1)") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = verCode, onValueChange = { verCode = it }, label = { Text("Version Code") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = apkUrl, onValueChange = { apkUrl = it }, label = { Text("Direct APK Download URL") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = apkSizeText, onValueChange = { apkSizeText = it }, label = { Text("APK File Size") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = notesText, onValueChange = { notesText = it }, label = { Text("Release Notes / Changelog") }, minLines = 3, modifier = Modifier.fillMaxWidth())

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = isForce, onCheckedChange = { isForce = it })
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Force Update (Mandatory update for all devices)")
                                }

                                Button(
                                    onClick = {
                                        val newConfig = AppUpdateInfo(
                                            latestVersionCode = verCode.toIntOrNull() ?: 1,
                                            latestVersionName = verName,
                                            apkSize = apkSizeText,
                                            downloadUrl = apkUrl,
                                            releaseNotes = notesText,
                                            isForceUpdate = isForce
                                        )
                                        viewModel.adminUpdateAppUpdateInfo(newConfig)
                                        Toast.makeText(context, "App update broadcasted to all user devices!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Publish Live Update Broadcast")
                                }
                            }
                        }
                    }

                    item {
                        var currentApiKey by remember { mutableStateOf(viewModel.getActiveGeminiApiKey()) }
                        var apiKeyInput by remember { mutableStateOf(currentApiKey) }
                        var showKeyVisible by remember { mutableStateOf(false) }

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Psychology, contentDescription = null, tint = BrandPurple, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Jivan AI Mentor — Gemini API Key Manager",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    text = "View or update the Gemini API key used by Jivan AI Mentor in real-time across the app.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (currentApiKey.isNotBlank() && !currentApiKey.contains("MY_GEMINI")) BrandGreen.copy(alpha = 0.12f) else BrandAmber.copy(alpha = 0.15f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            if (currentApiKey.isNotBlank() && !currentApiKey.contains("MY_GEMINI")) Icons.Default.CheckCircle else Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = if (currentApiKey.isNotBlank() && !currentApiKey.contains("MY_GEMINI")) BrandGreen else BrandAmber,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = if (currentApiKey.isNotBlank() && !currentApiKey.contains("MY_GEMINI")) "Status: Active & Connected" else "Status: Key Not Set / Offline Fallback Active",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = if (currentApiKey.isNotBlank() && !currentApiKey.contains("MY_GEMINI")) BrandGreen else BrandAmber
                                            )
                                            Text(
                                                text = if (showKeyVisible) currentApiKey.ifBlank { "No key set" } else if (currentApiKey.isNotBlank()) "••••••••••••" + currentApiKey.takeLast(6) else "No key configured",
                                                fontSize = 11.sp,
                                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                            )
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = apiKeyInput,
                                    onValueChange = { apiKeyInput = it },
                                    label = { Text("Gemini API Key (AIzaSy...)") },
                                    placeholder = { Text("Enter Google Gemini API Key") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    trailingIcon = {
                                        IconButton(onClick = { showKeyVisible = !showKeyVisible }) {
                                            Icon(
                                                if (showKeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = "Toggle Visibility"
                                            )
                                        }
                                    }
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            viewModel.adminSetGeminiApiKey(apiKeyInput)
                                            currentApiKey = viewModel.getActiveGeminiApiKey()
                                            Toast.makeText(context, "Gemini API key updated for Jivan AI!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Save & Apply Key")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            apiKeyInput = ""
                                            viewModel.adminSetGeminiApiKey("")
                                            currentApiKey = viewModel.getActiveGeminiApiKey()
                                            Toast.makeText(context, "API Key reset to default config", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Reset")
                                    }
                                }
                            }
                        }
                    }

                    item {
                        val cloudStatus by viewModel.cloudStatusMessage.collectAsState()
                        val isCloudAvailable by viewModel.isFirebaseAvailable.collectAsState()
                        val savedFirebaseConfig = remember { viewModel.getSavedFirebaseConfig() }
                        var fbProjectId by remember { mutableStateOf(savedFirebaseConfig.first) }
                        var fbApiKey by remember { mutableStateOf(savedFirebaseConfig.second) }
                        var fbAppId by remember { mutableStateOf(savedFirebaseConfig.third) }
                        var isTestingFb by remember { mutableStateOf(false) }
                        var fbTestResult by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
                        var isSyncingAll by remember { mutableStateOf(false) }

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CloudSync, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(26.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Firebase Cloud Sync (Multi-Device)",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Sync all Notes, Classes, Posts & Updates across all phones live.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                // Status Banner
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isCloudAvailable) BrandGreen.copy(alpha = 0.12f) else BrandAmber.copy(alpha = 0.12f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isCloudAvailable) BrandGreen.copy(alpha = 0.3f) else BrandAmber.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            if (isCloudAvailable) Icons.Default.CheckCircle else Icons.Default.CloudOff,
                                            contentDescription = null,
                                            tint = if (isCloudAvailable) BrandGreen else BrandAmber,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = if (isCloudAvailable) "Real-Time Cloud Sync: Active" else "Real-Time Cloud Sync: Standby / Local Mode",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = if (isCloudAvailable) BrandGreen else BrandAmber
                                            )
                                            Text(
                                                text = cloudStatus,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = fbProjectId,
                                    onValueChange = { 
                                        fbProjectId = it 
                                        fbTestResult = null
                                    },
                                    label = { Text("Firebase Project ID (e.g. vishuconnect-app)") },
                                    placeholder = { Text("Enter Project ID from Firebase Console") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = fbApiKey,
                                    onValueChange = { 
                                        fbApiKey = it 
                                        fbTestResult = null
                                    },
                                    label = { Text("Firebase Web API Key (AIzaSy...)") },
                                    placeholder = { Text("Enter Web API Key") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = fbAppId,
                                    onValueChange = { 
                                        fbAppId = it 
                                        fbTestResult = null
                                    },
                                    label = { Text("Firebase App ID (Optional)") },
                                    placeholder = { Text("1:123456789:android:abc") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Action Buttons Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            if (fbProjectId.isBlank()) {
                                                Toast.makeText(context, "Please enter your Firebase Project ID", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            viewModel.configureFirebase(fbProjectId, fbApiKey, fbAppId) { success, msg ->
                                                fbTestResult = Pair(success, msg)
                                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.CloudDone, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Save & Connect")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            isTestingFb = true
                                            fbTestResult = null
                                            viewModel.testFirebaseConnection { success, msg ->
                                                isTestingFb = false
                                                fbTestResult = Pair(success, msg)
                                            }
                                        },
                                        enabled = !isTestingFb,
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        if (isTestingFb) {
                                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                        } else {
                                            Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Test Ping")
                                        }
                                    }
                                }

                                // Batch Cloud Upload Button
                                Button(
                                    onClick = {
                                        isSyncingAll = true
                                        viewModel.syncAllLocalDataToCloud { count, msg ->
                                            isSyncingAll = false
                                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                        }
                                    },
                                    enabled = !isSyncingAll,
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (isSyncingAll) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Uploading to Cloud...")
                                    } else {
                                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Push All Local Notes & Classes to Cloud")
                                    }
                                }

                                // Test Result Feedback
                                fbTestResult?.let { (success, msg) ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (success) BrandGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = (if (success) "✅ " else "❌ ") + msg,
                                            fontSize = 12.sp,
                                            color = if (success) BrandGreen else MaterialTheme.colorScheme.error,
                                            lineHeight = 16.sp,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }

                                // Quick Guide Card
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("📌 Quick 1-Minute Firebase Setup Guide:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandBlue)
                                        Text("1. console.firebase.google.com par naya project banayein (e.g. 'vishuconnect').", fontSize = 11.sp)
                                        Text("2. 'Cloud Firestore' open karke 'Create database' par click karein aur 'Test Mode' select karein.", fontSize = 11.sp)
                                        Text("3. Project Settings (⚙️ icon) se Project ID aur Web API Key copy karein.", fontSize = 11.sp)
                                        Text("4. Upar paste karke 'Save & Connect' aur 'Push All Local Notes' dabayein. Sabhi phones par live sync activate ho jayega!", fontSize = 11.sp, color = BrandGreen)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            9 -> {
                // Tab 9: Announcements
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Button(
                            onClick = { showAddAnnouncementDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandAmber),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AddAlert, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Post Broadcast Announcement")
                        }
                    }

                    items(announcements, key = { it.id }) { ann ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = ann.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = ann.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { viewModel.adminDeleteAnnouncement(ann) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BrandError)
                                }
                            }
                        }
                    }
                }
            }

            10 -> {
                // Tab 10: Dynamic Sections
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "Configure Feature Modules & Screen Visibility",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    items(sections, key = { it.key }) { sec ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = sec.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = sec.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = sec.isVisible,
                                    onCheckedChange = { viewModel.adminToggleSection(sec, it) }
                                )
                            }
                        }
                    }
                }
            }

            11 -> {
                // Tab 11: Audit Logs
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "System Security & Action Logs",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    items(auditLogs, key = { it.id }) { log ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = log.action, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = "Target: ${log.target}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "User: ${log.adminUser} • ${log.timestamp}", fontSize = 10.sp, color = BrandBlue)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showAddUserDialog) {
        AddUserAdminDialog(
            onDismiss = { showAddUserDialog = false },
            onAdd = { email, name, role, course, sem ->
                viewModel.signInWithGoogle(email, name, course = course, sem = sem)
                showAddUserDialog = false
                Toast.makeText(context, "User $name registered!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showAddClassDialog) {
        AddClassAdminDialog(
            onDismiss = { showAddClassDialog = false },
            onAdd = { title, teacher, date, time, meetLink, desc ->
                viewModel.adminAddClass(
                    ClassItem(
                        title = title,
                        subject = "Computer Science",
                        teacher = teacher,
                        date = date,
                        time = time,
                        meetLink = meetLink,
                        description = desc
                    )
                )
                showAddClassDialog = false
                Toast.makeText(context, "Class scheduled!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (classToEdit != null) {
        EditClassAdminDialog(
            item = classToEdit!!,
            onDismiss = { classToEdit = null },
            onSave = { updated ->
                viewModel.adminUpdateClass(updated)
                classToEdit = null
                Toast.makeText(context, "Class updated!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showAddAnnouncementDialog) {
        AddAnnouncementAdminDialog(
            onDismiss = { showAddAnnouncementDialog = false },
            onAdd = { title, desc, priority ->
                viewModel.adminAddAnnouncement(
                    AnnouncementItem(
                        title = title,
                        description = desc,
                        date = "Today",
                        priority = priority
                    )
                )
                showAddAnnouncementDialog = false
                Toast.makeText(context, "Announcement posted!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showAddNoteDialog) {
        AddOfficialNoteAdminDialog(
            onDismiss = { showAddNoteDialog = false },
            onAdd = { title, subject, course, sem, desc, fileName ->
                viewModel.adminAddNote(
                    NoteItem(
                        title = title,
                        subject = subject,
                        course = course,
                        semester = sem,
                        description = desc,
                        fileName = fileName,
                        category = "Official Notes",
                        fileSize = "4.2 MB",
                        isApproved = true
                    )
                )
                showAddNoteDialog = false
                Toast.makeText(context, "Official note published!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showAddBannerDialog) {
        AddBannerAdminDialog(
            onDismiss = { showAddBannerDialog = false },
            onAdd = { title, sub, badge, target ->
                viewModel.adminUpdateCarousel(
                    CarouselBannerItem(
                        title = title,
                        subtitle = sub,
                        badgeText = badge,
                        actionType = "navigate",
                        actionTarget = target
                    )
                )
                showAddBannerDialog = false
                Toast.makeText(context, "Banner added!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (bannerToEdit != null) {
        EditBannerAdminDialog(
            item = bannerToEdit!!,
            onDismiss = { bannerToEdit = null },
            onSave = { updated ->
                viewModel.adminUpdateCarousel(updated)
                bannerToEdit = null
                Toast.makeText(context, "Banner updated!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (postToEdit != null) {
        val currentPost = postToEdit!!
        EditPostAdminDialog(
            post = currentPost,
            onDismiss = { postToEdit = null },
            onSave = { updated: CommunityPostItem ->
                viewModel.adminUpdateCommunityPost(updated)
                postToEdit = null
                Toast.makeText(context, "Post updated!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showCreateAdminPostDialog) {
        CreateAdminPostDialog(
            onDismiss = { showCreateAdminPostDialog = false },
            onCreate = { title, content, tag, isPinned ->
                viewModel.adminCreateCommunityPost(title, content, tag, isPinned)
                showCreateAdminPostDialog = false
                Toast.makeText(context, "Official Faculty Post published to Community!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun AddUserAdminDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, Int) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Student") }
    var course by remember { mutableStateOf("BCA") }
    var sem by remember { mutableIntStateOf(1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register New User / Google Account") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Google Email ID") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = course, onValueChange = { course = it }, label = { Text("Course") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = sem.toString(), onValueChange = { sem = it.toIntOrNull() ?: 1 }, label = { Text("Sem") }, modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (email.isNotBlank() && name.isNotBlank()) onAdd(email, name, role, course, sem) }) {
                Text("Register")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddBannerAdminDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var badge by remember { mutableStateOf("⚡ NEW POSTER") }
    var target by remember { mutableStateOf("calculators") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Carousel Front Poster") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Banner Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = subtitle, onValueChange = { subtitle = it }, label = { Text("Subtitle") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = badge, onValueChange = { badge = it }, label = { Text("Badge Text (e.g. 🤖 JIVAN TUTOR)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = target, onValueChange = { target = it }, label = { Text("Target Screen (calculators, posts, notes, ai_assistant)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { if (title.isNotBlank()) onAdd(title, subtitle, badge, target) }) {
                Text("Add Poster")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun EditBannerAdminDialog(
    item: CarouselBannerItem,
    onDismiss: () -> Unit,
    onSave: (CarouselBannerItem) -> Unit
) {
    var title by remember { mutableStateOf(item.title) }
    var subtitle by remember { mutableStateOf(item.subtitle) }
    var badge by remember { mutableStateOf(item.badgeText) }
    var target by remember { mutableStateOf(item.actionTarget) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Poster Banner") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Banner Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = subtitle, onValueChange = { subtitle = it }, label = { Text("Subtitle") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = badge, onValueChange = { badge = it }, label = { Text("Badge Text") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = target, onValueChange = { target = it }, label = { Text("Target Screen") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { onSave(item.copy(title = title, subtitle = subtitle, badgeText = badge, actionTarget = target)) }) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun EditPostAdminDialog(
    post: CommunityPostItem,
    onDismiss: () -> Unit,
    onSave: (CommunityPostItem) -> Unit
) {
    var title by remember { mutableStateOf(post.title) }
    var content by remember { mutableStateOf(post.content) }
    var tag by remember { mutableStateOf(post.tag) }
    var isPinned by remember { mutableStateOf(post.isPinned) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Community Post") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Post Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Post Content") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                OutlinedTextField(value = tag, onValueChange = { tag = it }, label = { Text("Tag (ANNOUNCEMENT, DOUBT, DISCUSS)") }, modifier = Modifier.fillMaxWidth())
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isPinned, onCheckedChange = { isPinned = it })
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pin to Top of Community Feed")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(post.copy(title = title, content = content, tag = tag, isPinned = isPinned)) }) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddClassAdminDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var teacher by remember { mutableStateOf("Er. Vishu Upadhyay") }
    var date by remember { mutableStateOf("Today") }
    var time by remember { mutableStateOf("07:00 PM") }
    var meetLink by remember { mutableStateOf("https://meet.google.com/new") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Schedule Class & Google Meet") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Class Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = teacher, onValueChange = { teacher = it }, label = { Text("Instructor") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time") }, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = meetLink, onValueChange = { meetLink = it }, label = { Text("Google Meet Link") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { if (title.isNotBlank()) onAdd(title, teacher, date, time, meetLink, desc) }) {
                Text("Schedule")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddAnnouncementAdminDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Urgent") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Broadcast Announcement") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Message") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
            }
        },
        confirmButton = {
            Button(onClick = { if (title.isNotBlank()) onAdd(title, desc, priority) }) {
                Text("Broadcast")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddOfficialNoteAdminDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, Int, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("BCA") }
    var semester by remember { mutableIntStateOf(1) }
    var desc by remember { mutableStateOf("") }
    var fileName by remember { mutableStateOf("Official_Notes.pdf") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Publish Official Note") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = course, onValueChange = { course = it }, label = { Text("Course") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = semester.toString(), onValueChange = { semester = it.toIntOrNull() ?: 1 }, label = { Text("Semester") }, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = fileName, onValueChange = { fileName = it }, label = { Text("File Name") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { if (title.isNotBlank()) onAdd(title, subject, course, semester, desc, fileName) }) {
                Text("Publish")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun EditClassAdminDialog(
    item: ClassItem,
    onDismiss: () -> Unit,
    onSave: (ClassItem) -> Unit
) {
    var title by remember { mutableStateOf(item.title) }
    var teacher by remember { mutableStateOf(item.teacher) }
    var date by remember { mutableStateOf(item.date) }
    var time by remember { mutableStateOf(item.time) }
    var meetLink by remember { mutableStateOf(item.meetLink) }
    var desc by remember { mutableStateOf(item.description) }
    var isLive by remember { mutableStateOf(item.isLive) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Class & Meet Link") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Class Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = teacher, onValueChange = { teacher = it }, label = { Text("Instructor") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time") }, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = meetLink, onValueChange = { meetLink = it }, label = { Text("Google Meet Link") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show as LIVE Now on Home:")
                    Switch(checked = isLive, onCheckedChange = { isLive = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        item.copy(
                            title = title,
                            teacher = teacher,
                            date = date,
                            time = time,
                            meetLink = meetLink,
                            description = desc,
                            isLive = isLive,
                            status = if (isLive) "Live" else "Upcoming"
                        )
                    )
                }
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun CreateAdminPostDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, content: String, tag: String, isPinned: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("ANNOUNCEMENT") }
    var isPinned by remember { mutableStateOf(true) }

    val tags = listOf("ANNOUNCEMENT", "EXAM_ALERT", "TIPS", "DISCUSS")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Campaign, contentDescription = null, tint = BrandBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Official Faculty Post", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Post Title / Topic") },
                    placeholder = { Text("e.g. End Semester Exam Guidelines 2026") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Post Content / Detailed Notice") },
                    placeholder = { Text("Write the full message, guidelines, or study materials note...") },
                    minLines = 3,
                    maxLines = 6,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Category Tag:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    tags.forEach { tag ->
                        val isSelected = selectedTag == tag
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTag = tag },
                            label = { Text(tag, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(checked = isPinned, onCheckedChange = { isPinned = it })
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pin this notice to the top of Community Feed", style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onCreate(title, content, selectedTag, isPinned)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
            ) {
                Text("Publish to Feed")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

