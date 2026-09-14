package com.example.data.repository

import com.example.data.local.AppDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class VishuRepository(private val dao: AppDao) {

    // Notes
    val allApprovedNotes: Flow<List<NoteItem>> = dao.getAllApprovedNotes()
    val allNotesAdmin: Flow<List<NoteItem>> = dao.getAllNotesAdmin()
    val savedNotes: Flow<List<NoteItem>> = dao.getSavedNotes()

    suspend fun insertNote(note: NoteItem): Long = dao.insertNote(note)
    suspend fun insertNotes(notes: List<NoteItem>) = dao.insertNotes(notes)
    suspend fun updateNote(note: NoteItem) = dao.updateNote(note)
    suspend fun deleteNote(note: NoteItem) = dao.deleteNote(note)
    suspend fun toggleSaveNote(id: Long, isSaved: Boolean) = dao.updateNoteSavedStatus(id, isSaved)
    suspend fun incrementDownloadCount(id: Long) = dao.incrementDownloadCount(id)
    suspend fun setNoteApproval(id: Long, approved: Boolean) = dao.setNoteApproval(id, approved)

    // Code Notes
    val allCodeNotes: Flow<List<CodeNoteItem>> = dao.getAllCodeNotes()
    suspend fun insertCodeNote(note: CodeNoteItem): Long = dao.insertCodeNote(note)
    suspend fun deleteCodeNote(note: CodeNoteItem) = dao.deleteCodeNote(note)

    // Classes
    val allClasses: Flow<List<ClassItem>> = dao.getAllClasses()
    suspend fun insertClass(classItem: ClassItem): Long = dao.insertClass(classItem)
    suspend fun insertClasses(classes: List<ClassItem>) = dao.insertClasses(classes)
    suspend fun deleteClass(classItem: ClassItem) = dao.deleteClass(classItem)

    // Batches
    val allBatches: Flow<List<BatchItem>> = dao.getAllBatches()
    suspend fun insertBatch(batch: BatchItem): Long = dao.insertBatch(batch)

    // Courses
    val allCourses: Flow<List<CourseItem>> = dao.getAllCourses()
    suspend fun insertCourse(course: CourseItem): Long = dao.insertCourse(course)

    // Projects
    val allProjects: Flow<List<ProjectItem>> = dao.getAllProjects()
    suspend fun insertProject(project: ProjectItem): Long = dao.insertProject(project)
    suspend fun deleteProject(project: ProjectItem) = dao.deleteProject(project)

    // Apps
    val allApps: Flow<List<AppItem>> = dao.getAllApps()
    suspend fun insertApp(app: AppItem): Long = dao.insertApp(app)

    // Announcements
    val publishedAnnouncements: Flow<List<AnnouncementItem>> = dao.getPublishedAnnouncements()
    val allAnnouncements: Flow<List<AnnouncementItem>> = dao.getAllAnnouncements()
    suspend fun insertAnnouncement(announcement: AnnouncementItem): Long = dao.insertAnnouncement(announcement)
    suspend fun deleteAnnouncement(announcement: AnnouncementItem) = dao.deleteAnnouncement(announcement)

    // Community Posts
    val allCommunityPosts: Flow<List<CommunityPostItem>> = dao.getAllCommunityPosts()
    suspend fun insertCommunityPost(post: CommunityPostItem): Long = dao.insertCommunityPost(post)
    suspend fun insertCommunityPosts(posts: List<CommunityPostItem>) = dao.insertCommunityPosts(posts)
    suspend fun deleteCommunityPost(post: CommunityPostItem) = dao.deleteCommunityPost(post)
    suspend fun updatePostLike(postId: Long, newLikes: Int, isLiked: Boolean) = dao.updatePostLike(postId, newLikes, isLiked)
    suspend fun incrementPostCommentCount(postId: Long) = dao.incrementPostCommentCount(postId)

    // Post Comments
    fun getCommentsForPost(postId: Long): Flow<List<PostCommentItem>> = dao.getCommentsForPost(postId)
    val allComments: Flow<List<PostCommentItem>> = dao.getAllComments()
    suspend fun insertPostComment(comment: PostCommentItem): Long = dao.insertPostComment(comment)
    suspend fun deletePostComment(comment: PostCommentItem) = dao.deletePostComment(comment)

    // Carousel Banners
    val allCarouselBanners: Flow<List<CarouselBannerItem>> = dao.getAllCarouselBanners()
    suspend fun insertCarouselBanner(banner: CarouselBannerItem): Long = dao.insertCarouselBanner(banner)
    suspend fun deleteCarouselBanner(banner: CarouselBannerItem) = dao.deleteCarouselBanner(banner)

    // User Profile
    val userProfile: Flow<UserProfile?> = dao.getUserProfile()
    suspend fun saveUserProfile(profile: UserProfile) = dao.saveUserProfile(profile)

    // Audit Logs
    val auditLogs: Flow<List<AuditLog>> = dao.getAuditLogs()
    suspend fun insertAuditLog(log: AuditLog) = dao.insertAuditLog(log)

    // Dynamic Sections
    val dynamicSections: Flow<List<DynamicSection>> = dao.getDynamicSections()
    suspend fun updateDynamicSection(section: DynamicSection) = dao.updateDynamicSection(section)

    // Payment Gateway Configuration
    val paymentGatewayConfig: Flow<PaymentGatewayConfig?> = dao.getPaymentGatewayConfig()
    suspend fun savePaymentGatewayConfig(config: PaymentGatewayConfig) = dao.savePaymentGatewayConfig(config)

    // User Directory (Users in App)
    val allUsers: Flow<List<UserAccount>> = dao.getAllUsers()
    suspend fun getUserAccount(email: String): UserAccount? = dao.getUserAccount(email)
    suspend fun insertUser(user: UserAccount) = dao.insertUser(user)
    suspend fun updateUser(user: UserAccount) = dao.updateUser(user)
    suspend fun deleteUser(user: UserAccount) = dao.deleteUser(user)
    suspend fun clearNonAdminUsers() = dao.clearNonAdminUsers()
    suspend fun updateUserRole(email: String, newRole: String) = dao.updateUserRole(email, newRole)
    suspend fun setUserBlocked(email: String, isBlocked: Boolean) = dao.setUserBlocked(email, isBlocked)

    // Admin UI Settings (Global Controls)
    val adminUiSettings: Flow<AdminUiSettings?> = dao.getAdminUiSettings()
    suspend fun saveAdminUiSettings(settings: AdminUiSettings) = dao.saveAdminUiSettings(settings)
}
