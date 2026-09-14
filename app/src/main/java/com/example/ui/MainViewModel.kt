package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiAiService
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.remote.FirebaseSyncManager
import com.example.data.repository.VishuRepository
import com.example.security.FileUploadValidator
import com.example.security.InputValidator
import com.example.security.RateLimiter
import com.example.security.SafeErrorHandler
import com.example.security.SecurityUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VishuRepository
    
    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = VishuRepository(database.appDao())

        // Initialize persistent Gemini Key storage
        GeminiAiService.init(application)

        // Initialize Firebase Realtime Cloud Sync
        FirebaseSyncManager.init(application, viewModelScope)

        // Listen for Realtime Cloud App Updates from Cloud
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseSyncManager.remoteAppUpdate.collect { remoteUpdate ->
                if (remoteUpdate != null) {
                    _appUpdateInfo.value = remoteUpdate
                }
            }
        }

        // Listen for Realtime Community Posts (Batch inserted on IO to prevent UI frame skip)
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseSyncManager.remotePosts.collect { remotePostsList ->
                if (remotePostsList.isNotEmpty()) {
                    try {
                        repository.insertCommunityPosts(remotePostsList)
                    } catch (_: Exception) {}
                }
            }
        }

        // Listen for Realtime Study Notes (Batch inserted on IO to prevent UI frame skip)
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseSyncManager.remoteNotes.collect { remoteNotesList ->
                if (remoteNotesList.isNotEmpty()) {
                    try {
                        repository.insertNotes(remoteNotesList)
                    } catch (_: Exception) {}
                }
            }
        }

        // Listen for Realtime Live Classes (Batch inserted on IO to prevent UI frame skip)
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseSyncManager.remoteClasses.collect { remoteClassesList ->
                if (remoteClassesList.isNotEmpty()) {
                    try {
                        repository.insertClasses(remoteClassesList)
                    } catch (_: Exception) {}
                }
            }
        }
    }

    // Repository Flows
    val notes: StateFlow<List<NoteItem>> = repository.allApprovedNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotesAdmin: StateFlow<List<NoteItem>> = repository.allNotesAdmin
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedNotes: StateFlow<List<NoteItem>> = repository.savedNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val codeNotes: StateFlow<List<CodeNoteItem>> = repository.allCodeNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val classes: StateFlow<List<ClassItem>> = repository.allClasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val batches: StateFlow<List<BatchItem>> = repository.allBatches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val courses: StateFlow<List<CourseItem>> = repository.allCourses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projects: StateFlow<List<ProjectItem>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val apps: StateFlow<List<AppItem>> = repository.allApps
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val announcements: StateFlow<List<AnnouncementItem>> = repository.publishedAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAnnouncementsAdmin: StateFlow<List<AnnouncementItem>> = repository.allAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val communityPosts: StateFlow<List<CommunityPostItem>> = repository.allCommunityPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allComments: StateFlow<List<PostCommentItem>> = repository.allComments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val carouselBanners: StateFlow<List<CarouselBannerItem>> = repository.allCarouselBanners
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val auditLogs: StateFlow<List<AuditLog>> = repository.auditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dynamicSections: StateFlow<List<DynamicSection>> = repository.dynamicSections
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserAccount>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminUiSettings: StateFlow<AdminUiSettings?> = repository.adminUiSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminUiSettings())

    // Authentication State & Active User
    private val _isUserLoggedIn = MutableStateFlow(true)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    private val _currentUserAccount = MutableStateFlow<UserAccount?>(
        UserAccount(
            email = "vishuupadhyay231@gmail.com",
            fullName = "Vishu Upadhyay",
            role = "Admin",
            isGoogleAuth = true,
            joinedDate = "10 Jan 2026",
            lastActive = "Online Now",
            deviceModel = "Google Pixel 8 Pro",
            course = "BCA / MCA Specialist",
            semester = 6,
            notesSharedCount = 28
        )
    )
    val currentUserAccount: StateFlow<UserAccount?> = _currentUserAccount.asStateFlow()

    fun signInWithGoogle(email: String, fullName: String, photoUrl: String = "", course: String = "BCA", sem: Int = 1) {
        val emailValidation = InputValidator.validateEmail(email)
        if (!emailValidation.isValid) {
            SafeErrorHandler.logError("Auth", "Invalid email format attempted during sign-in.")
            return
        }

        val rateCheck = RateLimiter.checkAuthAllowed(email)
        if (rateCheck is RateLimiter.RateLimitResult.BackoffRequired) {
            SafeErrorHandler.logError("Auth", "Rate limit backoff active for $email")
            return
        }

        val nameValidation = InputValidator.validateName(fullName, fieldName = "Name", minLen = 2, maxLen = 80)
        val cleanName = if (nameValidation.isValid) fullName.trim() else "Vishu Learner"

        val trimmedEmail = email.trim().lowercase()
        val role = if (trimmedEmail == "vishuupadhyay231@gmail.com" || trimmedEmail.contains("admin")) "Admin" else "Student"
        val newUser = UserAccount(
            email = trimmedEmail,
            fullName = cleanName,
            role = role,
            photoUrl = photoUrl,
            isGoogleAuth = true,
            joinedDate = "Aug 2026",
            lastActive = "Just now",
            course = course,
            semester = sem.coerceIn(1, 8)
        )
        viewModelScope.launch {
            try {
                repository.insertUser(newUser)
                _currentUserAccount.value = newUser
                _isUserLoggedIn.value = true
                repository.saveUserProfile(
                    UserProfile(
                        email = newUser.email,
                        fullName = newUser.fullName,
                        username = newUser.email.substringBefore("@"),
                        course = newUser.course,
                        semester = newUser.semester
                    )
                )
                RateLimiter.recordAuthSuccess(trimmedEmail)
                logAudit("User Signed In", newUser.email, newUser.role)
            } catch (e: Exception) {
                SafeErrorHandler.logError("Auth", "Failed to complete user sign-in", e)
            }
        }
    }

    fun signOutUser() {
        _isUserLoggedIn.value = false
        _currentUserAccount.value = null
        logAudit("User Signed Out", "Auth Session", "Student")
    }

    fun adminUpdateUserRole(email: String, newRole: String) {
        viewModelScope.launch {
            repository.updateUserRole(email, newRole)
            logAudit("Changed User Role", "$email -> $newRole", "vaibhav")
        }
    }

    fun adminToggleUserBlocked(email: String, isBlocked: Boolean) {
        viewModelScope.launch {
            repository.setUserBlocked(email, isBlocked)
            logAudit(if (isBlocked) "Blocked User" else "Unblocked User", email, "vaibhav")
        }
    }

    fun adminDeleteUser(user: UserAccount) {
        viewModelScope.launch {
            repository.deleteUser(user)
            logAudit("Deleted User Account", user.email, "vaibhav")
        }
    }

    fun adminClearFakeUsers() {
        viewModelScope.launch {
            repository.clearNonAdminUsers()
            logAudit("Cleaned Inactive/Fake Users Cache", "Preserved Admin accounts only", "vaibhav")
        }
    }

    fun adminUpdateUiSettings(settings: AdminUiSettings) {
        viewModelScope.launch {
            repository.saveAdminUiSettings(settings)
            _animationIntensity.value = settings.animationSpeed
            logAudit("Updated UI & Animation Settings", "Speed: ${settings.animationSpeed}", "vaibhav")
        }
    }

    // Payment Gateway Configuration Flow
    val paymentGatewayConfig: StateFlow<PaymentGatewayConfig?> = repository.paymentGatewayConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PaymentGatewayConfig())

    // Unlocked Paid Notes
    private val _unlockedNoteIds = MutableStateFlow<Set<Long>>(setOf())
    val unlockedNoteIds: StateFlow<Set<Long>> = _unlockedNoteIds.asStateFlow()

    fun unlockNote(noteId: Long) {
        _unlockedNoteIds.value = _unlockedNoteIds.value + noteId
        logAudit("Note Unlocked via Payment", "Note ID: $noteId", "Student")
    }

    fun isNoteUnlocked(note: NoteItem): Boolean {
        if (!note.isPaid || note.price <= 0.0) return true
        return _unlockedNoteIds.value.contains(note.id)
    }

    // Unlocked Paid Projects
    private val _unlockedProjectIds = MutableStateFlow<Set<Long>>(setOf())
    val unlockedProjectIds: StateFlow<Set<Long>> = _unlockedProjectIds.asStateFlow()

    fun unlockProject(projectId: Long) {
        _unlockedProjectIds.value = _unlockedProjectIds.value + projectId
        logAudit("Project Unlocked via Payment", "Project ID: $projectId", "Student")
    }

    fun isProjectUnlocked(project: ProjectItem): Boolean {
        if (project.isFree || project.price <= 0.0) return true
        return _unlockedProjectIds.value.contains(project.id)
    }

    fun adminUpdatePaymentGateway(config: PaymentGatewayConfig): String? {
        val upiValidation = InputValidator.validateUpiId(config.merchantUpiId)
        if (!upiValidation.isValid) {
            return (upiValidation as InputValidator.ValidationResult.Invalid).error
        }

        val rzpValidation = InputValidator.validateRazorpayKey(config.razorpayKeyId, isOptional = true)
        if (!rzpValidation.isValid) {
            return (rzpValidation as InputValidator.ValidationResult.Invalid).error
        }

        viewModelScope.launch {
            try {
                repository.savePaymentGatewayConfig(config)
                logAudit("Updated Payment Gateway", "${config.provider} (${if (config.isEnabled) "Active" else "Disabled"})", "vaibhav")
            } catch (e: Exception) {
                SafeErrorHandler.logError("Payment", "Failed to update payment gateway config", e)
            }
        }
        return null
    }

    // App Update Info (Remote Control)
    private val _appUpdateInfo = MutableStateFlow(AppUpdateInfo())
    val appUpdateInfo: StateFlow<AppUpdateInfo> = _appUpdateInfo.asStateFlow()

    // Gemini API Key (Admin View & Override)
    private val _customGeminiApiKey = MutableStateFlow("")
    val customGeminiApiKey: StateFlow<String> = _customGeminiApiKey.asStateFlow()

    fun adminSetGeminiApiKey(key: String) {
        _customGeminiApiKey.value = key.trim()
        GeminiAiService.setCustomApiKey(key.trim())
        logAudit("Updated Gemini AI Key", if (key.isNotBlank()) "Key Updated" else "Reset to default", "vaibhav")
    }

    fun getActiveGeminiApiKey(): String {
        return GeminiAiService.getActiveApiKey()
    }

    // Creator Intro Info (Remote Control)
    private val _creatorIntro = MutableStateFlow(CreatorIntroConfig())
    val creatorIntro: StateFlow<CreatorIntroConfig> = _creatorIntro.asStateFlow()

    // Active Screen & Navigation
    private val _currentScreen = MutableStateFlow("home")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    // Search & Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private val _selectedCourseFilter = MutableStateFlow("All")
    val selectedCourseFilter: StateFlow<String> = _selectedCourseFilter.asStateFlow()

    fun setSelectedCourseFilter(course: String) {
        _selectedCourseFilter.value = course
    }

    private val _selectedSemesterFilter = MutableStateFlow(0) // 0 = all
    val selectedSemesterFilter: StateFlow<Int> = _selectedSemesterFilter.asStateFlow()

    fun setSelectedSemesterFilter(sem: Int) {
        _selectedSemesterFilter.value = sem
    }

    private val _selectedCodeLanguage = MutableStateFlow("All")
    val selectedCodeLanguage: StateFlow<String> = _selectedCodeLanguage.asStateFlow()

    fun setSelectedCodeLanguage(lang: String) {
        _selectedCodeLanguage.value = lang
    }

    // Theme & Animation Settings
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleDarkTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
    }

    private val _animationIntensity = MutableStateFlow("Medium")
    val animationIntensity: StateFlow<String> = _animationIntensity.asStateFlow()

    fun setAnimationIntensity(intensity: String) {
        _animationIntensity.value = intensity
    }

    // AI Chat State (Jivan)
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "Namaste! I am **Jivan**, your AI Academic & Technical Mentor on **Vishu Connect**. Ask me any question about BCA/BTech subjects, Data Structures, Code debugging, Mathematics, or Semester exams!",
                isUser = false
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiGenerating = MutableStateFlow(false)
    val isAiGenerating: StateFlow<Boolean> = _isAiGenerating.asStateFlow()

    fun sendAiPrompt(prompt: String) {
        val validation = InputValidator.validateContent(prompt, minLength = 2, maxLength = 3000)
        if (!validation.isValid || _isAiGenerating.value) return

        val rateLimit = RateLimiter.checkPublicRequestAllowed("ai_assistant", _currentUserAccount.value?.email ?: "guest")
        if (rateLimit is RateLimiter.RateLimitResult.TooManyRequests) {
            val rateMsg = ChatMessage(text = "⚠️ ${rateLimit.message}", isUser = false)
            _chatMessages.value = _chatMessages.value + rateMsg
            return
        }

        val userMsg = ChatMessage(text = prompt.trim(), isUser = true)
        _chatMessages.value = _chatMessages.value + userMsg
        _isAiGenerating.value = true

        viewModelScope.launch {
            try {
                val history = _chatMessages.value.map { it.text to it.isUser }
                val reply = GeminiAiService.askJivan(prompt.trim(), history)
                val aiMsg = ChatMessage(text = reply, isUser = false)
                _chatMessages.value = _chatMessages.value + aiMsg
            } catch (e: Exception) {
                val safeErr = SafeErrorHandler.getUserFriendlyMessage(e)
                SafeErrorHandler.logError("GeminiAI", "AI mentor request failed", e)
                _chatMessages.value = _chatMessages.value + ChatMessage(text = "⚠️ $safeErr", isUser = false)
            } finally {
                _isAiGenerating.value = false
            }
        }
    }

    fun clearAiChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                text = "Chat cleared. Ask me anything about BCA, BTech, Programming, or Mathematics!",
                isUser = false
            )
        )
    }

    // Community Posts Actions
    fun togglePostLike(post: CommunityPostItem) {
        val rateLimit = RateLimiter.checkUserActionAllowed("toggle_like", _currentUserAccount.value?.email ?: "user")
        if (rateLimit is RateLimiter.RateLimitResult.TooManyRequests) return

        viewModelScope.launch {
            try {
                val newLiked = !post.isLikedByMe
                val newCount = if (newLiked) post.likesCount + 1 else maxOf(0, post.likesCount - 1)
                repository.updatePostLike(post.id, newCount, newLiked)
            } catch (e: Exception) {
                SafeErrorHandler.logError("Community", "Failed to update like status", e)
            }
        }
    }

    fun addPostComment(postId: Long, authorName: String, authorRole: String, text: String) {
        val textValidation = InputValidator.validateContent(text, minLength = 1, maxLength = 1000)
        if (!textValidation.isValid) return

        val rateLimit = RateLimiter.checkUserActionAllowed("add_comment", authorName)
        if (rateLimit is RateLimiter.RateLimitResult.TooManyRequests) return

        val nameValidation = InputValidator.validateName(authorName, fieldName = "Author", minLen = 2, maxLen = 60)
        val cleanAuthor = if (nameValidation.isValid) authorName.trim() else "Student Learner"

        viewModelScope.launch {
            try {
                val newComment = PostCommentItem(
                    postId = postId,
                    authorName = cleanAuthor,
                    authorRole = authorRole,
                    commentText = text.trim(),
                    timestamp = "Just now",
                    isVerified = authorRole == "Admin"
                )
                repository.insertPostComment(newComment)
                repository.incrementPostCommentCount(postId)
                logAudit("Comment Added", "Post #$postId", cleanAuthor)
            } catch (e: Exception) {
                SafeErrorHandler.logError("Community", "Failed to add post comment", e)
            }
        }
    }

    fun createCommunityPost(title: String, content: String, tag: String, isPinned: Boolean = false, authorName: String = "Student", authorRole: String = "Student") {
        val titleValidation = InputValidator.validateTitle(title, minLength = 3, maxLength = 200)
        val contentValidation = InputValidator.validateContent(content, minLength = 5, maxLength = 5000)
        if (!titleValidation.isValid || !contentValidation.isValid) return

        val rateLimit = RateLimiter.checkUserActionAllowed("create_post", authorName)
        if (rateLimit is RateLimiter.RateLimitResult.TooManyRequests) return

        val nameValidation = InputValidator.validateName(authorName, fieldName = "Author", minLen = 2, maxLen = 60)
        val cleanAuthor = if (nameValidation.isValid) authorName.trim() else "Student"

        viewModelScope.launch {
            try {
                val newPost = CommunityPostItem(
                    authorName = cleanAuthor,
                    authorRole = authorRole,
                    isPinned = isPinned,
                    title = title.trim(),
                    content = content.trim(),
                    tag = tag.trim().ifBlank { "General" },
                    likesCount = 1,
                    commentsCount = 0,
                    timestamp = "Just now",
                    isLikedByMe = true
                )
                repository.insertCommunityPost(newPost)
                FirebaseSyncManager.publishCommunityPost(newPost)
                logAudit("Post Created", title, cleanAuthor)
            } catch (e: Exception) {
                SafeErrorHandler.logError("Community", "Failed to insert community post", e)
            }
        }
    }

    fun adminCreateCommunityPost(title: String, content: String, tag: String = "ANNOUNCEMENT", isPinned: Boolean = true) {
        val titleValidation = InputValidator.validateTitle(title, minLength = 3, maxLength = 200)
        val contentValidation = InputValidator.validateContent(content, minLength = 5, maxLength = 5000)
        if (!titleValidation.isValid || !contentValidation.isValid) return

        viewModelScope.launch {
            try {
                val adminPost = CommunityPostItem(
                    authorName = "Prof. Vishu Upadhyay",
                    authorRole = "Admin",
                    isPinned = isPinned,
                    title = title.trim(),
                    content = content.trim(),
                    tag = tag.trim().ifBlank { "ANNOUNCEMENT" },
                    likesCount = 5,
                    commentsCount = 0,
                    timestamp = "Official Admin Notice",
                    isLikedByMe = true
                )
                repository.insertCommunityPost(adminPost)
                FirebaseSyncManager.publishCommunityPost(adminPost)
                logAudit("Admin Post Published", title, "vaibhav")
            } catch (e: Exception) {
                SafeErrorHandler.logError("Community", "Failed to create admin post", e)
            }
        }
    }

    fun adminUpdateCommunityPost(post: CommunityPostItem) {
        viewModelScope.launch {
            try {
                repository.insertCommunityPost(post)
                logAudit("Admin Post Updated", post.title, "vaibhav")
            } catch (e: Exception) {
                SafeErrorHandler.logError("Community", "Failed to update post", e)
            }
        }
    }

    fun deleteCommunityPost(post: CommunityPostItem) {
        viewModelScope.launch {
            try {
                repository.deleteCommunityPost(post)
                logAudit("Post Deleted", post.title, "vaibhav")
            } catch (e: Exception) {
                SafeErrorHandler.logError("Community", "Failed to delete community post", e)
            }
        }
    }

    fun deletePostComment(comment: PostCommentItem) {
        viewModelScope.launch {
            try {
                repository.deletePostComment(comment)
                logAudit("Comment Deleted", comment.commentText.take(20), "vaibhav")
            } catch (e: Exception) {
                SafeErrorHandler.logError("Community", "Failed to delete comment", e)
            }
        }
    }

    // Notes Actions
    fun toggleSaveNote(note: NoteItem) {
        viewModelScope.launch {
            try {
                repository.toggleSaveNote(note.id, !note.isSaved)
            } catch (e: Exception) {
                SafeErrorHandler.logError("Notes", "Failed to toggle saved note", e)
            }
        }
    }

    fun downloadNote(note: NoteItem, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.incrementDownloadCount(note.id)
            } catch (e: Exception) {
                SafeErrorHandler.logError("Notes", "Failed to increment download count", e)
            }
            onComplete()
        }
    }

    fun uploadUserNote(
        title: String,
        subject: String,
        course: String,
        semester: Int,
        category: String,
        description: String,
        fileName: String,
        isPaid: Boolean = false,
        price: Double = 0.0
    ): String? {
        val userEmail = _currentUserAccount.value?.email ?: "student@vishuconnect.edu"
        val rateLimit = RateLimiter.checkUploadAllowed(userEmail)
        if (rateLimit is RateLimiter.RateLimitResult.TooManyRequests) {
            return rateLimit.message
        }

        val titleValidation = InputValidator.validateTitle(title, minLength = 3, maxLength = 150)
        if (!titleValidation.isValid) return (titleValidation as InputValidator.ValidationResult.Invalid).error

        val descValidation = InputValidator.validateContent(description, minLength = 3, maxLength = 3000)
        if (!descValidation.isValid) return (descValidation as InputValidator.ValidationResult.Invalid).error

        val (cleanFileName, fileResult) = FileUploadValidator.sanitizeAndValidateFileName(fileName)
        if (!fileResult.isValid) return (fileResult as FileUploadValidator.FileValidationResult.Rejected).reason

        val priceValidation = if (isPaid) InputValidator.validatePrice(price) else InputValidator.ValidationResult.Valid
        if (!priceValidation.isValid) return (priceValidation as InputValidator.ValidationResult.Invalid).error

        viewModelScope.launch {
            try {
                val newNote = NoteItem(
                    title = title.trim(),
                    subject = subject.trim().ifBlank { "Computer Science" },
                    course = course.trim().ifBlank { "BCA" },
                    semester = semester.coerceIn(1, 8),
                    category = category.trim().ifBlank { "Exam Notes" },
                    description = description.trim(),
                    fileName = cleanFileName,
                    fileSize = "3.2 MB",
                    isApproved = false,
                    isPaid = isPaid,
                    price = if (isPaid) price else 0.0,
                    uploaderName = _currentUserAccount.value?.fullName ?: "Student",
                    uploaderEmail = userEmail
                )
                repository.insertNote(newNote)
                logAudit("User Note Uploaded", "$title (${if (isPaid) "₹$price" else "Free"})", "Student")
            } catch (e: Exception) {
                SafeErrorHandler.logError("Upload", "Failed to upload student note", e)
            }
        }
        return null
    }

    // User Profile Actions
    fun updateUserProfile(profile: UserProfile) {
        val nameValidation = InputValidator.validateName(profile.fullName, "Full Name", 2, 80)
        if (!nameValidation.isValid) return

        viewModelScope.launch {
            try {
                repository.saveUserProfile(profile)
                logAudit("Profile Updated", profile.fullName, profile.username)
            } catch (e: Exception) {
                SafeErrorHandler.logError("Profile", "Failed to update profile", e)
            }
        }
    }

    // Secret Admin Panel Unlock Logic (Hidden 25 to 30 Clicks on Logo)
    private var logoClickCount = 0
    private var lastClickTime = 0L

    private val _showAdminLoginDialog = MutableStateFlow(false)
    val showAdminLoginDialog: StateFlow<Boolean> = _showAdminLoginDialog.asStateFlow()

    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    private val _adminAuthError = MutableStateFlow<String?>(null)
    val adminAuthError: StateFlow<String?> = _adminAuthError.asStateFlow()

    fun onLogoTapped() {
        val now = System.currentTimeMillis()
        if (now - lastClickTime > 5000) {
            logoClickCount = 1
        } else {
            logoClickCount++
        }
        lastClickTime = now

        if (logoClickCount >= 25) {
            logoClickCount = 0
            if (!_isAdminLoggedIn.value) {
                _showAdminLoginDialog.value = true
            } else {
                navigateTo("admin")
            }
        }
    }

    fun dismissAdminLoginDialog() {
        _showAdminLoginDialog.value = false
        _adminAuthError.value = null
    }

    fun loginAdmin(user: String, pass: String): Boolean {
        val trimmedUser = user.trim()
        val trimmedPass = pass.trim()

        // 1. Strict Input Validation
        val userValidation = InputValidator.validateUsername(trimmedUser)
        val passValidation = InputValidator.validatePassword(trimmedPass, minLength = 4, maxLength = 64)
        if (!userValidation.isValid || !passValidation.isValid) {
            _adminAuthError.value = "Invalid username or password format."
            return false
        }

        // 2. Rate Limiting with Exponential Backoff
        val rateLimitCheck = RateLimiter.checkAuthAllowed(trimmedUser)
        if (rateLimitCheck is RateLimiter.RateLimitResult.BackoffRequired) {
            _adminAuthError.value = rateLimitCheck.message
            SafeErrorHandler.logError("AdminAuth", "Rate limit triggered for user: $trimmedUser")
            return false
        }

        // 3. Constant-time secure verification
        val isUserValid = SecurityUtils.constantTimeEquals(trimmedUser.lowercase(), "vaibhav")
        val isPassValid = SecurityUtils.constantTimeEquals(trimmedPass, "shivam")

        if (isUserValid && isPassValid) {
            RateLimiter.recordAuthSuccess(trimmedUser)
            _isAdminLoggedIn.value = true
            _showAdminLoginDialog.value = false
            _adminAuthError.value = null
            logAudit("Admin Logged In", "Dashboard", "vaibhav")
            navigateTo("admin")
            return true
        } else {
            RateLimiter.recordAuthFailure(trimmedUser)
            _adminAuthError.value = "Invalid credentials. Access restricted."
            SafeErrorHandler.logError("AdminAuth", "Failed login attempt for user: $trimmedUser")
            return false
        }
    }

    fun logoutAdmin() {
        _isAdminLoggedIn.value = false
        logAudit("Admin Logged Out", "Dashboard", "vaibhav")
        navigateTo("home")
    }

    // Admin CRUD Operations
    fun adminApproveNote(note: NoteItem) {
        viewModelScope.launch {
            repository.setNoteApproval(note.id, true)
            logAudit("Approved Note", note.title, "vaibhav")
        }
    }

    fun adminDeleteNote(note: NoteItem) {
        viewModelScope.launch {
            repository.deleteNote(note)
            logAudit("Deleted Note", note.title, "vaibhav")
        }
    }

    fun adminAddNote(note: NoteItem) {
        viewModelScope.launch {
            val approvedNote = note.copy(isApproved = true)
            repository.insertNote(approvedNote)
            FirebaseSyncManager.publishStudyNote(approvedNote)
            logAudit("Admin Created Note", note.title, "vaibhav")
        }
    }

    fun adminAddClass(classItem: ClassItem) {
        viewModelScope.launch {
            repository.insertClass(classItem)
            FirebaseSyncManager.publishLiveClass(classItem)
            logAudit("Created Class", classItem.title, "vaibhav")
        }
    }

    fun adminUpdateClass(classItem: ClassItem) {
        viewModelScope.launch {
            repository.insertClass(classItem)
            FirebaseSyncManager.publishLiveClass(classItem)
            logAudit("Updated Class", classItem.title, "vaibhav")
        }
    }

    fun logoutStudent() {
        viewModelScope.launch {
            // Reset to guest / default student profile
            val defaultStudent = UserProfile(
                fullName = "Guest Student",
                email = "student@vishuconnect.edu",
                course = "BCA",
                semester = 1,
                college = "Department of Computer Science",
                bio = "Exploring BCA & BTech CS courses on Vishu Connect.",
                skills = "C++, Java, Python, HTML/CSS"
            )
            repository.saveUserProfile(defaultStudent)
        }
    }

    fun adminDeleteClass(classItem: ClassItem) {
        viewModelScope.launch {
            repository.deleteClass(classItem)
            logAudit("Deleted Class", classItem.title, "vaibhav")
        }
    }

    fun adminAddCodeNote(codeNote: CodeNoteItem) {
        viewModelScope.launch {
            repository.insertCodeNote(codeNote)
            logAudit("Created Code Note", codeNote.title, "vaibhav")
        }
    }

    fun adminDeleteCodeNote(codeNote: CodeNoteItem) {
        viewModelScope.launch {
            repository.deleteCodeNote(codeNote)
            logAudit("Deleted Code Note", codeNote.title, "vaibhav")
        }
    }

    fun adminAddBatch(batch: BatchItem) {
        viewModelScope.launch {
            repository.insertBatch(batch)
            logAudit("Created Batch", batch.name, "vaibhav")
        }
    }

    fun adminAddCourse(course: CourseItem) {
        viewModelScope.launch {
            repository.insertCourse(course)
            logAudit("Created Course", course.title, "vaibhav")
        }
    }

    fun adminAddProject(project: ProjectItem) {
        viewModelScope.launch {
            repository.insertProject(project)
            logAudit("Created Project", project.title, "vaibhav")
        }
    }

    fun adminDeleteProject(project: ProjectItem) {
        viewModelScope.launch {
            repository.deleteProject(project)
            logAudit("Deleted Project", project.title, "vaibhav")
        }
    }

    fun adminAddApp(appItem: AppItem) {
        viewModelScope.launch {
            repository.insertApp(appItem)
            logAudit("Created App Entry", appItem.name, "vaibhav")
        }
    }

    fun adminAddAnnouncement(announcement: AnnouncementItem) {
        viewModelScope.launch {
            repository.insertAnnouncement(announcement)
            logAudit("Created Announcement", announcement.title, "vaibhav")
        }
    }

    fun adminDeleteAnnouncement(announcement: AnnouncementItem) {
        viewModelScope.launch {
            repository.deleteAnnouncement(announcement)
            logAudit("Deleted Announcement", announcement.title, "vaibhav")
        }
    }

    fun adminToggleSectionVisibility(section: DynamicSection) {
        viewModelScope.launch {
            repository.updateDynamicSection(section.copy(isVisible = !section.isVisible))
            logAudit("Toggled Section", section.title, "vaibhav")
        }
    }

    fun adminToggleSection(section: DynamicSection, isVisible: Boolean) {
        viewModelScope.launch {
            repository.updateDynamicSection(section.copy(isVisible = isVisible))
            logAudit("Toggled Section", "${section.title} -> $isVisible", "vaibhav")
        }
    }

    fun adminUpdateCarousel(banner: CarouselBannerItem) {
        viewModelScope.launch {
            repository.insertCarouselBanner(banner)
            logAudit("Updated Carousel Banner", banner.title, "vaibhav")
        }
    }

    fun adminDeleteCarouselBanner(banner: CarouselBannerItem) {
        viewModelScope.launch {
            repository.deleteCarouselBanner(banner)
            logAudit("Deleted Carousel Banner", banner.title, "vaibhav")
        }
    }

    fun adminUpdateAppUpdateInfo(updateInfo: AppUpdateInfo) {
        _appUpdateInfo.value = updateInfo
        FirebaseSyncManager.broadcastAppUpdate(updateInfo)
        logAudit("Updated App Version Info", "v${updateInfo.latestVersionName}", "vaibhav")
    }

    fun adminUpdateCreatorIntro(creatorInfo: CreatorIntroConfig) {
        _creatorIntro.value = creatorInfo
        logAudit("Updated Creator Profile", creatorInfo.fullName, "vaibhav")
    }

    val isFirebaseAvailable: StateFlow<Boolean> = FirebaseSyncManager.isFirebaseAvailable
    val cloudStatusMessage: StateFlow<String> = FirebaseSyncManager.cloudStatusMessage

    fun getSavedFirebaseConfig(): Triple<String, String, String> {
        return FirebaseSyncManager.getSavedConfig(getApplication())
    }

    fun configureFirebase(projectId: String, apiKey: String, appId: String = "", onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = FirebaseSyncManager.configureFirebase(getApplication(), viewModelScope, projectId, apiKey, appId)
            onResult(result.first, result.second)
            logAudit("Firebase Configured", "Project: $projectId", "vaibhav")
        }
    }

    fun testFirebaseConnection(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = FirebaseSyncManager.testConnection()
            onResult(result.first, result.second)
        }
    }

    fun syncAllLocalDataToCloud(onResult: (Int, String) -> Unit) {
        viewModelScope.launch {
            val currentNotes = notes.value
            val currentClasses = classes.value
            val currentPosts = communityPosts.value
            val currentUpdate = appUpdateInfo.value
            val result = FirebaseSyncManager.syncAllLocalDataToCloud(currentNotes, currentClasses, currentPosts, currentUpdate)
            onResult(result.first, result.second)
            logAudit("Batch Cloud Sync", "${result.first} items", "vaibhav")
        }
    }

    private fun logAudit(action: String, target: String, user: String) {
        viewModelScope.launch {
            val sdf = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault())
            val timestamp = sdf.format(java.util.Date())
            repository.insertAuditLog(
                AuditLog(
                    action = action,
                    target = target,
                    adminUser = user,
                    timestamp = timestamp
                )
            )
        }
    }
}
