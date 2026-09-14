package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.AppHeader
import com.example.ui.components.AppUpdateDialog
import com.example.ui.components.ContactInfoDialog
import com.example.ui.components.SecretAdminAuthDialog
import com.example.ui.screens.*
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.VishuConnectTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()

            val appUpdateInfo by viewModel.appUpdateInfo.collectAsState()
            val creatorIntro by viewModel.creatorIntro.collectAsState()

            val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()
            val currentUserAccount by viewModel.currentUserAccount.collectAsState()

            val showAdminAuthDialog by viewModel.showAdminLoginDialog.collectAsState()
            val adminAuthError by viewModel.adminAuthError.collectAsState()
            var showAppUpdateDialog by remember { mutableStateOf(false) }
            var showContactDialog by remember { mutableStateOf(false) }

            // Automatic In-App Update Popup Check on launch only if strictly forced by admin
            LaunchedEffect(appUpdateInfo) {
                if (appUpdateInfo.isForceUpdate) {
                    showAppUpdateDialog = true
                }
            }

            VishuConnectTheme(darkTheme = isDarkTheme) {
                // Back button handler
                BackHandler(enabled = currentScreen != "home") {
                    viewModel.navigateTo("home")
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        val headerTitle = when (currentScreen) {
                            "home" -> "Vishu Connect"
                            "notes" -> "Notes Hub"
                            "classes" -> "Live Classes & Meet"
                            "code_notes" -> "Code & DSA Notes"
                            "calculators" -> "240 STEM Solvers"
                            "posts" -> "Community Hub"
                            "courses" -> "Academic Courses"
                            "projects_apps" -> "College Projects"
                            "ai_assistant" -> "Jivan"
                            "about_vishu" -> "Creator Profile"
                            "profile" -> "Profile & Account"
                            "admin" -> "Control Suite"
                            else -> "Vishu Connect"
                        }

                        val headerSubtitle = when (currentScreen) {
                            "home" -> "BCA • BTech • CS • Mathematics"
                            "notes" -> "Semester PDF Guides"
                            "classes" -> "Google Meet Classrooms"
                            "code_notes" -> "C++, Java, Python, SQL"
                            "calculators" -> "240 Complete Solvers List"
                            "posts" -> "Doubts & Announcements"
                            "courses" -> "Curated Syllabus"
                            "projects_apps" -> "Source Code & Repositories"
                            "ai_assistant" -> "Intelligent Study Mentor"
                            "about_vishu" -> "Vishu Upadhyay"
                            "profile" -> currentUserAccount?.fullName ?: "Active Account"
                            "admin" -> "System Administration"
                            else -> ""
                        }

                        AppHeader(
                            title = headerTitle,
                            subtitle = headerSubtitle,
                            onLogoTapped = { viewModel.onLogoTapped() },
                            showBackButton = currentScreen != "home",
                            onBackClicked = { viewModel.navigateTo("home") },
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { viewModel.toggleDarkTheme(!isDarkTheme) },
                            isAdminLoggedIn = isAdminLoggedIn,
                            onProfileClick = { viewModel.navigateTo("profile") },
                            onContactClick = { showContactDialog = true },
                            onUpdateClick = { showAppUpdateDialog = true },
                            hasPendingUpdate = appUpdateInfo.latestVersionCode > appUpdateInfo.currentVersionCode
                        )
                    },
                    bottomBar = {
                        if (currentScreen != "admin") {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 3.dp
                            ) {
                                NavigationBarItem(
                                    selected = currentScreen == "home",
                                    onClick = { viewModel.navigateTo("home") },
                                    icon = {
                                        Icon(
                                            if (currentScreen == "home") Icons.Filled.Home else Icons.Outlined.Home,
                                            contentDescription = "Home"
                                        )
                                    },
                                    label = { Text("Home") },
                                    modifier = Modifier.testTag("nav_home")
                                )
                                NavigationBarItem(
                                    selected = currentScreen == "notes",
                                    onClick = { viewModel.navigateTo("notes") },
                                    icon = {
                                        Icon(
                                            if (currentScreen == "notes") Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                                            contentDescription = "Notes"
                                        )
                                    },
                                    label = { Text("Notes") },
                                    modifier = Modifier.testTag("nav_notes")
                                )
                                NavigationBarItem(
                                    selected = currentScreen == "calculators",
                                    onClick = { viewModel.navigateTo("calculators") },
                                    icon = {
                                        Icon(
                                            if (currentScreen == "calculators") Icons.Filled.Calculate else Icons.Outlined.Calculate,
                                            contentDescription = "Solvers"
                                        )
                                    },
                                    label = { Text("Solvers") },
                                    modifier = Modifier.testTag("nav_calculators")
                                )
                                NavigationBarItem(
                                    selected = currentScreen == "posts",
                                    onClick = { viewModel.navigateTo("posts") },
                                    icon = {
                                        Icon(
                                            if (currentScreen == "posts") Icons.Filled.Forum else Icons.Outlined.Forum,
                                            contentDescription = "Community"
                                        )
                                    },
                                    label = { Text("Community") },
                                    modifier = Modifier.testTag("nav_community")
                                )
                                NavigationBarItem(
                                    selected = currentScreen == "projects_apps" || currentScreen == "projects",
                                    onClick = { viewModel.navigateTo("projects_apps") },
                                    icon = {
                                        Icon(
                                            if (currentScreen == "projects_apps" || currentScreen == "projects") Icons.Filled.Code else Icons.Outlined.Code,
                                            contentDescription = "Projects"
                                        )
                                    },
                                    label = { Text("Projects") },
                                    modifier = Modifier.testTag("nav_projects")
                                )
                                NavigationBarItem(
                                    selected = currentScreen == "ai_assistant",
                                    onClick = { viewModel.navigateTo("ai_assistant") },
                                    icon = {
                                        Icon(
                                            if (currentScreen == "ai_assistant") Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                                            contentDescription = "Jivan",
                                            tint = if (currentScreen == "ai_assistant") BrandCyan else LocalContentColor.current,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    label = { Text("Jivan") },
                                    modifier = Modifier.testTag("nav_jivan")
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
                        when (currentScreen) {
                            "home" -> {
                                val notes by viewModel.notes.collectAsState()
                                val classes by viewModel.classes.collectAsState()
                                val announcements by viewModel.announcements.collectAsState()
                                HomeScreen(
                                    viewModel = viewModel,
                                    notes = notes,
                                    classes = classes,
                                    announcements = announcements,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                            "notes" -> {
                                val notes by viewModel.notes.collectAsState()
                                NotesScreen(
                                    viewModel = viewModel,
                                    notes = notes
                                )
                            }
                            "classes" -> {
                                val classes by viewModel.classes.collectAsState()
                                val batches by viewModel.batches.collectAsState()
                                ClassesAndMeetScreen(
                                    viewModel = viewModel,
                                    classes = classes,
                                    batches = batches
                                )
                            }
                            "code_notes" -> {
                                val codeNotes by viewModel.codeNotes.collectAsState()
                                CodeNotesScreen(
                                    viewModel = viewModel,
                                    codeNotes = codeNotes
                                )
                            }
                            "calculators" -> CalculatorsScreen(
                                viewModel = viewModel,
                                onNavigateBack = { viewModel.navigateTo("home") }
                            )
                            "posts" -> CommunityPostsScreen(
                                viewModel = viewModel,
                                onNavigateBack = { viewModel.navigateTo("home") }
                            )
                            "courses" -> {
                                val courses by viewModel.courses.collectAsState()
                                CoursesScreen(
                                    viewModel = viewModel,
                                    courses = courses
                                )
                            }
                            "projects_apps" -> {
                                val projects by viewModel.projects.collectAsState()
                                val apps by viewModel.apps.collectAsState()
                                ProjectsAndAppsScreen(
                                    viewModel = viewModel,
                                    projects = projects,
                                    apps = apps
                                )
                            }
                            "ai_assistant" -> AiAssistantScreen(
                                viewModel = viewModel
                            )
                            "about_vishu" -> AboutVishuScreen(
                                viewModel = viewModel
                            )
                            "profile" -> {
                                val userProfile by viewModel.userProfile.collectAsState()
                                val savedNotes by viewModel.savedNotes.collectAsState()
                                val animationIntensity by viewModel.animationIntensity.collectAsState()
                                ProfileScreen(
                                    viewModel = viewModel,
                                    profile = userProfile,
                                    savedNotes = savedNotes,
                                    isDarkTheme = isDarkTheme,
                                    animationIntensity = animationIntensity
                                )
                            }
                            "admin" -> {
                                val allNotesAdmin by viewModel.allNotesAdmin.collectAsState()
                                val classes by viewModel.classes.collectAsState()
                                val announcements by viewModel.announcements.collectAsState()
                                val auditLogs by viewModel.auditLogs.collectAsState()
                                val dynamicSections by viewModel.dynamicSections.collectAsState()
                                AdminScreen(
                                    viewModel = viewModel,
                                    notes = allNotesAdmin,
                                    classes = classes,
                                    announcements = announcements,
                                    auditLogs = auditLogs,
                                    sections = dynamicSections
                                )
                            }
                        }
                    }
                }

                // Secret Admin Authentication Dialog (Triggered on 15 Logo Clicks)
                SecretAdminAuthDialog(
                    isOpen = showAdminAuthDialog,
                    errorMessage = adminAuthError,
                    onDismiss = { viewModel.dismissAdminLoginDialog() },
                    onLogin = { username, password ->
                        viewModel.loginAdmin(username, password)
                    }
                )

                // App Update Dialog
                AppUpdateDialog(
                    isOpen = showAppUpdateDialog,
                    updateInfo = appUpdateInfo,
                    onDismiss = { showAppUpdateDialog = false }
                )

                // 3-Dot Contact & Creator Information Dialog
                ContactInfoDialog(
                    isOpen = showContactDialog,
                    creator = creatorIntro,
                    onDismiss = { showContactDialog = false }
                )

                // First-Time & Ongoing Google Sign In Gate
                if (!isUserLoggedIn) {
                    GoogleSignInModal(
                        onSignIn = { email, name, course, sem ->
                            viewModel.signInWithGoogle(email, name, course = course, sem = sem)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GoogleSignInModal(
    onSignIn: (String, String, String, Int) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var email by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("BCA") }
    var sem by remember { mutableIntStateOf(1) }

    AlertDialog(
        onDismissRequest = { /* Require sign in to proceed */ },
        icon = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF0B1120))
                    .border(
                        1.5.dp,
                        Brush.linearGradient(listOf(BrandCyan, Color(0xFF818CF8), BrandCyan)),
                        RoundedCornerShape(18.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.vishu_vi_cyber_neon_logo_1787479124539),
                    contentDescription = "Vishu Connect 3D Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Welcome to Vishu Connect",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text(
                    text = "Sign in with your Google Account to unlock live notes, classes, community & 240 BCA solvers",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Quick 1-Tap Google Sign In
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onSignIn("vishu.learner@vishuconnect.edu", "Vishu Learner", "BCA", 4)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandBlue.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("G", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFFEA4335))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("1-Tap Google Quick Sign-In", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Divider(modifier = Modifier.weight(1f))
                    Text("  or enter your Google ID  ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Divider(modifier = Modifier.weight(1f))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Google Email (e.g. user@gmail.com)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Your Full Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = course,
                        onValueChange = { course = it },
                        label = { Text("Course (BCA/BTech)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = sem.toString(),
                        onValueChange = { sem = it.toIntOrNull() ?: 1 },
                        label = { Text("Sem (1-8)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(0.7f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    val finalEmail = if (email.isNotBlank()) email.trim() else "learner@vishuconnect.edu"
                    val finalName = if (fullName.isNotBlank()) fullName.trim() else "Vishu Student"
                    onSignIn(finalEmail, finalName, course, sem)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Sign In & Start Learning", fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(22.dp)
    )
}
