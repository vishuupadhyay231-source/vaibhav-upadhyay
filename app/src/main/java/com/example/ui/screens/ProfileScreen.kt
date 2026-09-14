package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppUpdateInfo
import com.example.data.model.CreatorIntroConfig
import com.example.data.model.NoteItem
import com.example.data.model.UserProfile
import com.example.ui.MainViewModel
import com.example.ui.components.AppUpdateDialog
import com.example.ui.components.openExternalUrl
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    profile: UserProfile?,
    savedNotes: List<NoteItem>,
    isDarkTheme: Boolean,
    animationIntensity: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val user = profile ?: UserProfile()
    val creatorIntro by viewModel.creatorIntro.collectAsState()
    val appUpdateInfo by viewModel.appUpdateInfo.collectAsState()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf(false) }
    var showAppUpdateDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Overview", "Saved Notes (${savedNotes.size})", "Preferences")

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen")
    ) {
        // Top Action Bar with 3-dot Menu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Profile & Account",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu Options")
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Check for Updates", fontWeight = FontWeight.Bold, color = BrandGreen) },
                        leadingIcon = { Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = BrandGreen) },
                        onClick = {
                            showMenu = false
                            showAppUpdateDialog = true
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Contact & ID Info") },
                        leadingIcon = { Icon(Icons.Default.ContactMail, contentDescription = null, tint = BrandBlue) },
                        onClick = {
                            showMenu = false
                            showContactDialog = true
                        }
                    )
                    Divider()
                    DropdownMenuItem(
                        text = { Text("Sign Out / Switch Account", color = BrandError) },
                        leadingIcon = { Icon(Icons.Default.Logout, contentDescription = null, tint = BrandError) },
                        onClick = {
                            showMenu = false
                            viewModel.signOutUser()
                            if (isAdminLoggedIn) {
                                viewModel.logoutAdmin()
                            }
                            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // Profile Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Profile Header Card
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF0F172A),
                                        Color(0xFF1E293B),
                                        Color(0xFF334155)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Avatar circle with initials
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(BrandCyan)
                                        .padding(2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(Color(0xFF0F172A)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = user.fullName.take(2).uppercase(),
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Black,
                                            color = BrandCyan
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = user.fullName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.Verified,
                                            contentDescription = "Verified",
                                            tint = Color(0xFF38BDF8),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = "@${user.fullName.lowercase().replace(" ", "_")}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isAdminLoggedIn) Color(0xFF9333EA) else Color(0xFF2563EB)
                                        ) {
                                            Text(
                                                text = if (isAdminLoggedIn) "ADMIN" else "STUDENT",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color.White.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "${user.course} • Sem ${user.semester}",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                IconButton(
                                    onClick = { showEditProfileDialog = true },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.15f))
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "🏢 ${user.college}",
                                fontSize = 12.sp,
                                color = Color(0xFFCBD5E1),
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = user.bio,
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            when (selectedTab) {
                0 -> {
                    // Overview: Skills + Connected Social Links
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "💡 Academic & Tech Proficiencies",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val skillsList = user.skills.split(",").map { it.trim() }.filter { it.isNotBlank() }
                                    skillsList.forEach { skill ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = BrandBlue.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = skill,
                                                color = BrandBlue,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🌐 Connect & Channels",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    TextButton(onClick = { showContactDialog = true }) {
                                        Text("View Creator", fontSize = 12.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))

                                ContactHandleRow(
                                    icon = Icons.Default.CameraAlt,
                                    label = "Instagram Profile",
                                    value = "@vishu_upadhyay_",
                                    color = Color(0xFFE1306C),
                                    onClick = { openExternalUrl(context, creatorIntro?.instagramUrl ?: "https://instagram.com/vishu_upadhyay_") }
                                )

                                ContactHandleRow(
                                    icon = Icons.Default.Code,
                                    label = "GitHub Repository",
                                    value = "github.com/vishuupadhyay",
                                    color = Color(0xFF24292E),
                                    onClick = { openExternalUrl(context, creatorIntro?.githubUrl ?: "https://github.com/vishuupadhyay") }
                                )

                                ContactHandleRow(
                                    icon = Icons.Default.Work,
                                    label = "LinkedIn Profile",
                                    value = "linkedin.com/in/vishuupadhyay",
                                    color = Color(0xFF0A66C2),
                                    onClick = { openExternalUrl(context, creatorIntro?.linkedinUrl ?: "https://linkedin.com/in/vishuupadhyay") }
                                )

                                ContactHandleRow(
                                    icon = Icons.Default.Chat,
                                    label = "WhatsApp Chat & Community",
                                    value = "Direct Chat & Group Support",
                                    color = Color(0xFF25D366),
                                    onClick = {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(creatorIntro?.whatsappUrl ?: "https://wa.me/919876543210"))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            openExternalUrl(context, creatorIntro?.whatsappUrl ?: "https://wa.me/919876543210")
                                        }
                                    }
                                )

                                ContactHandleRow(
                                    icon = Icons.Default.Email,
                                    label = "Email Support",
                                    value = creatorIntro?.email ?: "vishuupadhyay231@gmail.com",
                                    color = Color(0xFFEA4335),
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                                            data = Uri.parse("mailto:${creatorIntro?.email ?: "vishuupadhyay231@gmail.com"}")
                                            putExtra(Intent.EXTRA_SUBJECT, "Vishu Connect Help")
                                        }
                                        try { context.startActivity(intent) } catch (e: Exception) {}
                                    }
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedButton(
                                    onClick = {
                                        viewModel.logoutStudent()
                                        if (isAdminLoggedIn) viewModel.logoutAdmin()
                                        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandError),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Logout Student Account", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Saved Notes Tab
                    if (savedNotes.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 30.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No saved notes yet. Bookmark notes in Notes Hub to access them offline here!",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(savedNotes, key = { it.id }) { note ->
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
                                        Text(text = note.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(
                                            text = "${note.course} • Sem ${note.semester} • ${note.fileSize}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(onClick = { viewModel.toggleSaveNote(note) }) {
                                        Icon(
                                            Icons.Default.BookmarkRemove,
                                            contentDescription = "Remove",
                                            tint = BrandAmber
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Preferences Tab
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "⚙️ App Customization & Styling",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                            contentDescription = null,
                                            tint = BrandCyan,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(text = "Dark Mode Theme", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    }
                                    Switch(
                                        checked = isDarkTheme,
                                        onCheckedChange = { viewModel.toggleDarkTheme(it) }
                                    )
                                }

                                Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.surface)

                                Text(
                                    text = "ℹ️ Animation speed, theme accents and poster customization are managed centrally by Admin Control.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = {
                                        viewModel.signOutUser()
                                        if (isAdminLoggedIn) {
                                            viewModel.logoutAdmin()
                                        }
                                        Toast.makeText(context, "Signed out from Google Account", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandError.copy(alpha = 0.15f), contentColor = BrandError),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sign Out / Switch Google Account", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            currentProfile = user,
            onDismiss = { showEditProfileDialog = false },
            onSave = { updated ->
                viewModel.updateUserProfile(updated)
                showEditProfileDialog = false
                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showContactDialog) {
        ContactCreatorDialog(
            creator = creatorIntro,
            onDismiss = { showContactDialog = false }
        )
    }

    if (showAppUpdateDialog) {
        AppUpdateDialog(
            isOpen = showAppUpdateDialog,
            updateInfo = appUpdateInfo,
            onDismiss = { showAppUpdateDialog = false }
        )
    }
}

@Composable
fun ContactHandleRow(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = value, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Default.OpenInNew, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContactCreatorDialog(
    creator: CreatorIntroConfig?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val vishuSkills = listOf(
        "AI / ML & GenAI", "Web Dev (Full Stack)", "App Dev (Android/Kotlin)",
        "Cybersecurity", "UI / UX Design", "DSA & Algorithms", "Cloud Architecture"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PersonPin, contentDescription = null, tint = BrandBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Creator & Skills Profile", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = creator?.fullName ?: "Vishu Upadhyay",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = creator?.title ?: "Founder & Developer • Vishu Connect",
                    fontSize = 12.sp,
                    color = BrandBlue,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = creator?.bio ?: "Building modern, accessible educational tools for BCA, BTech, and Computer Science students.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Skills & Expertise:", fontWeight = FontWeight.Bold, fontSize = 12.sp)

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    vishuSkills.forEach { skill ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BrandPurple.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = skill,
                                color = BrandPurple,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 4.dp))
                Text(text = "Connect Links:", fontWeight = FontWeight.Bold, fontSize = 12.sp)

                Button(
                    onClick = { openExternalUrl(context, creator?.instagramUrl ?: "https://instagram.com/vishu_upadhyay_") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1306C)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Instagram (@vishu_upadhyay_)", fontSize = 12.sp)
                }

                Button(
                    onClick = { openExternalUrl(context, creator?.githubUrl ?: "https://github.com/vishuupadhyay") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24292E)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                ) {
                    Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("GitHub Profile", fontSize = 12.sp)
                }

                Button(
                    onClick = { openExternalUrl(context, creator?.linkedinUrl ?: "https://linkedin.com/in/vishuupadhyay") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A66C2)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                ) {
                    Icon(Icons.Default.Work, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("LinkedIn Profile", fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(creator?.whatsappUrl ?: "https://wa.me/919876543210"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            openExternalUrl(context, creator?.whatsappUrl ?: "https://wa.me/919876543210")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("WhatsApp Chat & Group", fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close") }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun EditProfileDialog(
    currentProfile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (UserProfile) -> Unit
) {
    var fullName by remember { mutableStateOf(currentProfile.fullName) }
    var course by remember { mutableStateOf(currentProfile.course) }
    var semester by remember { mutableIntStateOf(currentProfile.semester) }
    var college by remember { mutableStateOf(currentProfile.college) }
    var bio by remember { mutableStateOf(currentProfile.bio) }
    var skills by remember { mutableStateOf(currentProfile.skills) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Student Profile", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = college,
                        onValueChange = { college = it },
                        label = { Text("College / University") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = course,
                            onValueChange = { course = it },
                            label = { Text("Course (e.g. BCA)") },
                            modifier = Modifier.weight(2f)
                        )
                        OutlinedTextField(
                            value = semester.toString(),
                            onValueChange = { semester = it.toIntOrNull() ?: 1 },
                            label = { Text("Semester") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Bio") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
                item {
                    OutlinedTextField(
                        value = skills,
                        onValueChange = { skills = it },
                        label = { Text("Skills (comma separated)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        currentProfile.copy(
                            fullName = fullName,
                            course = course,
                            semester = semester,
                            college = college,
                            bio = bio,
                            skills = skills
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

