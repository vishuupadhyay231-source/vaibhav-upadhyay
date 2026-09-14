package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // Notes
    @Query("SELECT * FROM notes WHERE isApproved = 1 ORDER BY id DESC")
    fun getAllApprovedNotes(): Flow<List<NoteItem>>

    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotesAdmin(): Flow<List<NoteItem>>

    @Query("SELECT * FROM notes WHERE isSaved = 1 ORDER BY id DESC")
    fun getSavedNotes(): Flow<List<NoteItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteItem>)

    @Update
    suspend fun updateNote(note: NoteItem)

    @Delete
    suspend fun deleteNote(note: NoteItem)

    @Query("UPDATE notes SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateNoteSavedStatus(id: Long, isSaved: Boolean)

    @Query("UPDATE notes SET downloadCount = downloadCount + 1 WHERE id = :id")
    suspend fun incrementDownloadCount(id: Long)

    @Query("UPDATE notes SET isApproved = :approved WHERE id = :id")
    suspend fun setNoteApproval(id: Long, approved: Boolean)

    // Code Notes
    @Query("SELECT * FROM code_notes ORDER BY id ASC")
    fun getAllCodeNotes(): Flow<List<CodeNoteItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCodeNotes(notes: List<CodeNoteItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCodeNote(note: CodeNoteItem): Long

    @Delete
    suspend fun deleteCodeNote(note: CodeNoteItem)

    // Classes
    @Query("SELECT * FROM classes ORDER BY id DESC")
    fun getAllClasses(): Flow<List<ClassItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClasses(classes: List<ClassItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(classItem: ClassItem): Long

    @Delete
    suspend fun deleteClass(classItem: ClassItem)

    // Batches
    @Query("SELECT * FROM batches ORDER BY id DESC")
    fun getAllBatches(): Flow<List<BatchItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatches(batches: List<BatchItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(batch: BatchItem): Long

    // Courses
    @Query("SELECT * FROM courses ORDER BY id ASC")
    fun getAllCourses(): Flow<List<CourseItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseItem): Long

    // Projects
    @Query("SELECT * FROM projects ORDER BY id DESC")
    fun getAllProjects(): Flow<List<ProjectItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjects(projects: List<ProjectItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectItem): Long

    @Delete
    suspend fun deleteProject(project: ProjectItem)

    // Apps
    @Query("SELECT * FROM apps ORDER BY id DESC")
    fun getAllApps(): Flow<List<AppItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(apps: List<AppItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: AppItem): Long

    // Announcements
    @Query("SELECT * FROM announcements WHERE isPublished = 1 ORDER BY id DESC")
    fun getPublishedAnnouncements(): Flow<List<AnnouncementItem>>

    @Query("SELECT * FROM announcements ORDER BY id DESC")
    fun getAllAnnouncements(): Flow<List<AnnouncementItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncements(announcements: List<AnnouncementItem>)

    @Delete
    suspend fun deleteAnnouncement(announcement: AnnouncementItem)

    // Community Posts
    @Query("SELECT * FROM community_posts ORDER BY isPinned DESC, id DESC")
    fun getAllCommunityPosts(): Flow<List<CommunityPostItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunityPost(post: CommunityPostItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunityPosts(posts: List<CommunityPostItem>)

    @Delete
    suspend fun deleteCommunityPost(post: CommunityPostItem)

    @Query("UPDATE community_posts SET likesCount = :newLikes, isLikedByMe = :isLiked WHERE id = :postId")
    suspend fun updatePostLike(postId: Long, newLikes: Int, isLiked: Boolean)

    @Query("UPDATE community_posts SET commentsCount = commentsCount + 1 WHERE id = :postId")
    suspend fun incrementPostCommentCount(postId: Long)

    // Comments
    @Query("SELECT * FROM post_comments WHERE postId = :postId ORDER BY id ASC")
    fun getCommentsForPost(postId: Long): Flow<List<PostCommentItem>>

    @Query("SELECT * FROM post_comments ORDER BY id DESC")
    fun getAllComments(): Flow<List<PostCommentItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostComment(comment: PostCommentItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostComments(comments: List<PostCommentItem>)

    @Delete
    suspend fun deletePostComment(comment: PostCommentItem)

    // Carousel Banners
    @Query("SELECT * FROM carousel_banners ORDER BY id ASC")
    fun getAllCarouselBanners(): Flow<List<CarouselBannerItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCarouselBanners(banners: List<CarouselBannerItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCarouselBanner(banner: CarouselBannerItem): Long

    @Delete
    suspend fun deleteCarouselBanner(banner: CarouselBannerItem)

    // User Profile
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY id DESC LIMIT 50")
    fun getAuditLogs(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLog)

    // Dynamic Sections
    @Query("SELECT * FROM dynamic_sections ORDER BY orderIndex ASC")
    fun getDynamicSections(): Flow<List<DynamicSection>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDynamicSections(sections: List<DynamicSection>)

    @Update
    suspend fun updateDynamicSection(section: DynamicSection)

    // Payment Gateway Configuration
    @Query("SELECT * FROM payment_gateway WHERE id = 1 LIMIT 1")
    fun getPaymentGatewayConfig(): Flow<PaymentGatewayConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePaymentGatewayConfig(config: PaymentGatewayConfig)

    // User Directory (Multi-user Management)
    @Query("SELECT * FROM app_users ORDER BY role DESC, email ASC")
    fun getAllUsers(): Flow<List<UserAccount>>

    @Query("SELECT * FROM app_users WHERE email = :email LIMIT 1")
    suspend fun getUserAccount(email: String): UserAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserAccount>)

    @Update
    suspend fun updateUser(user: UserAccount)

    @Delete
    suspend fun deleteUser(user: UserAccount)

    @Query("DELETE FROM app_users WHERE role != 'Admin'")
    suspend fun clearNonAdminUsers()

    @Query("UPDATE app_users SET role = :newRole WHERE email = :email")
    suspend fun updateUserRole(email: String, newRole: String)

    @Query("UPDATE app_users SET isBlocked = :isBlocked WHERE email = :email")
    suspend fun setUserBlocked(email: String, isBlocked: Boolean)

    // Admin UI Global Settings
    @Query("SELECT * FROM admin_ui_settings WHERE id = 1 LIMIT 1")
    fun getAdminUiSettings(): Flow<AdminUiSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAdminUiSettings(settings: AdminUiSettings)
}
