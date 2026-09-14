package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    indices = [
        Index(value = ["course", "semester"]),
        Index(value = ["subject"]),
        Index(value = ["isApproved"])
    ]
)
data class NoteItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val subject: String,
    val course: String, // BCA, BTech, CS, Math, General
    val semester: Int, // 1 to 8
    val category: String, // Exam Notes, Hand Written, Question Bank, Cheat Sheet
    val uploaderName: String = "Vishu Upadhyay",
    val uploaderEmail: String = "vishuupadhyay231@gmail.com",
    val uploadDate: String = "Aug 2026",
    val fileName: String,
    val fileType: String = "PDF", // PDF, DOCX, PPTX, ZIP
    val fileSize: String = "3.8 MB",
    val downloadCount: Int = 142,
    val isApproved: Boolean = true,
    val isPaid: Boolean = false,
    val price: Double = 0.0,
    val isSaved: Boolean = false,
    val contentSummary: String = ""
)

@Entity(
    tableName = "code_notes",
    indices = [
        Index(value = ["language"]),
        Index(value = ["category"])
    ]
)
data class CodeNoteItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val language: String, // C, C++, Java, Python, Web/JS, DSA, SQL
    val category: String, // Algorithms, OOP, Database, Systems
    val description: String,
    val codeSnippet: String,
    val explanation: String,
    val output: String = "",
    val difficulty: String = "Intermediate" // Beginner, Intermediate, Advanced
)

@Entity(
    tableName = "classes",
    indices = [
        Index(value = ["subject"]),
        Index(value = ["status"])
    ]
)
data class ClassItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val teacher: String,
    val date: String,
    val time: String,
    val description: String,
    val meetLink: String,
    val status: String = "Upcoming", // Live, Upcoming, Completed
    val batchName: String = "BCA/BTech 2026 Batch",
    val resourcesUrl: String = "",
    val isLive: Boolean = false
)

@Entity(tableName = "batches")
data class BatchItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val course: String,
    val instructor: String,
    val startDate: String,
    val endDate: String,
    val schedule: String,
    val status: String = "Active", // Active, Enrolling, Completed
    val enrolledStudents: Int = 85
)

@Entity(tableName = "courses")
data class CourseItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val instructor: String,
    val duration: String,
    val totalLessons: Int,
    val isFree: Boolean = true,
    val price: Double = 0.0,
    val rating: Float = 4.9f,
    val level: String = "All Levels", // Beginner, Intermediate, Advanced
    val syllabusTopics: String = "Fundamentals, Data Structures, OOP, Web APIs, Database, Deployment"
)

@Entity(tableName = "projects")
data class ProjectItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: String, // Major Project, Minor Project, Full Stack, AI/ML, Android
    val techStack: String, // Kotlin, Jetpack Compose, Room, Firebase, Python
    val version: String = "v1.0.0",
    val githubUrl: String = "https://github.com/vishuupadhyay",
    val demoUrl: String = "",
    val videoUrl: String = "",
    val imageUrl: String = "",
    val downloadFileName: String = "Project_Source_Code.zip",
    val fileSize: String = "14.2 MB",
    val isFeatured: Boolean = true,
    val isFree: Boolean = true,
    val price: Double = 0.0,
    val downloadCount: Int = 85,
    val rating: Float = 4.9f
)

@Entity(tableName = "apps")
data class AppItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val version: String,
    val apkSize: String,
    val releaseDate: String,
    val downloadUrl: String = "https://github.com/vishuupadhyay/vishu-connect/releases",
    val githubUrl: String = "https://github.com/vishuupadhyay",
    val featuresList: String = "Offline Mode, Fast Performance, Material 3 UI, Dark Mode support"
)

@Entity(tableName = "announcements")
data class AnnouncementItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val date: String,
    val priority: String = "Normal", // High, Normal, Urgent
    val isPublished: Boolean = true,
    val author: String = "Vishu Upadhyay"
)

@Entity(
    tableName = "community_posts",
    indices = [
        Index(value = ["tag"]),
        Index(value = ["isPinned"])
    ]
)
data class CommunityPostItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorName: String = "Prof. Vishu Upadhyay",
    val authorRole: String = "Admin", // Admin, Student, Faculty
    val isPinned: Boolean = false,
    val title: String,
    val content: String,
    val imageUrl: String = "",
    val likesCount: Int = 12,
    val commentsCount: Int = 3,
    val timestamp: String = "Just now",
    val tag: String = "ANNOUNCEMENT", // ANNOUNCEMENT, DOUBT, TIPS, NOTES, PROJECT
    val isLikedByMe: Boolean = false
)

@Entity(tableName = "post_comments")
data class PostCommentItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Long,
    val authorName: String,
    val authorRole: String = "Student", // Admin, Student
    val commentText: String,
    val timestamp: String = "Just now",
    val isVerified: Boolean = false
)

@Entity(tableName = "carousel_banners")
data class CarouselBannerItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subtitle: String,
    val badgeText: String = "Smart AI Tutor",
    val imageUrl: String = "",
    val actionType: String = "navigate", // navigate, url
    val actionTarget: String = "ai_assistant"
)

@Entity(tableName = "app_users")
data class UserAccount(
    @PrimaryKey val email: String,
    val fullName: String,
    val role: String = "Student", // Admin, Student, Faculty
    val photoUrl: String = "",
    val isGoogleAuth: Boolean = true,
    val joinedDate: String = "Aug 2026",
    val lastActive: String = "Active now",
    val isBlocked: Boolean = false,
    val deviceModel: String = "Android Device",
    val course: String = "BCA / CS",
    val semester: Int = 4,
    val notesSharedCount: Int = 0
)

@Entity(tableName = "admin_ui_settings")
data class AdminUiSettings(
    @PrimaryKey val id: Int = 1,
    val animationSpeed: String = "Fast", // Instant, Fast, Normal, Smooth, Disabled
    val buttonStyle: String = "Modern Rounded", // Modern Rounded, Pill Shape, Elevated Glass, Sharp Tech
    val adminPostColorHex: String = "#F59E0B", // Amber Gold VIP accent
    val userPostColorHex: String = "#3B82F6", // Royal Blue accent
    val allowStudentPosting: Boolean = true,
    val allowStudentNoteSharing: Boolean = true,
    val showHeroCarousel: Boolean = true,
    val activeAnnouncementBanner: String = "Welcome to Vishu Connect - Live Semester Notes & 240 Solvers Suite!"
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val email: String = "vishu.learner@vishuconnect.edu",
    val fullName: String = "Vishu Upadhyay",
    val username: String = "vishu_learner",
    val bio: String = "BCA / BTech Student & Tech Enthusiast | Exploring Computer Science",
    val course: String = "BCA (Bachelor of Computer Applications)",
    val college: String = "Institute of Technology & Computer Science",
    val semester: Int = 4,
    val skills: String = "Kotlin, C++, Java, Python, SQL, DSA, AI Integration",
    val github: String = "https://github.com/vishuupadhyay",
    val instagram: String = "https://instagram.com/vishu_connect",
    val linkedin: String = "https://linkedin.com/in/vishuupadhyay",
    val whatsapp: String = "+919876543210",
    val portfolio: String = "https://vishuconnect.edu",
    val isEmailVerified: Boolean = true
)

@Entity(tableName = "payment_gateway")
data class PaymentGatewayConfig(
    @PrimaryKey val id: Int = 1,
    val provider: String = "UPI Direct & Razorpay",
    val providerName: String = "UPI FastPay / Razorpay Gateway",
    val merchantUpiId: String = "vishuupadhyay231@okaxis",
    val upiId: String = "vishuupadhyay231@okaxis",
    val razorpayKeyId: String = "rzp_live_vishu2026_key",
    val razorpayKeySecret: String = "",
    val merchantName: String = "Vishu Upadhyay • Vishu Connect",
    val qrCodeUrl: String = "https://api.qrserver.com/v1/create-qr-code/?size=280x280&data=upi://pay?pa=vishuupadhyay231@okaxis&pn=Vishu+Connect+Notes",
    val apiKey: String = "rzp_live_vishu2026_key",
    val secretKey: String = "",
    val apiKeyOrSecret: String = "rzp_live_vishu2026_key",
    val isEnabled: Boolean = true,
    val isTestMode: Boolean = false,
    val currency: String = "INR",
    val instructions: String = "Pay securely via any UPI App (GPay, PhonePe, Paytm, BHIM) or Credit/Debit Cards. Your download unlocks instantly upon verification.",
    val supportContact: String = "vishuupadhyay231@gmail.com | WhatsApp: +91 9876543210",
    val supportWhatsApp: String = "+919876543210",
    val supportEmail: String = "vishuupadhyay231@gmail.com"
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val action: String,
    val target: String,
    val adminUser: String = "vaibhav",
    val timestamp: String
)

@Entity(tableName = "dynamic_sections")
data class DynamicSection(
    @PrimaryKey val key: String,
    val title: String,
    val iconName: String,
    val orderIndex: Int,
    val isVisible: Boolean = true,
    val description: String = ""
)

data class AppUpdateInfo(
    val currentVersionName: String = "1.0.0",
    val currentVersionCode: Int = 1,
    val latestVersionName: String = "1.0.0",
    val latestVersionCode: Int = 1,
    val minSupportedVersionCode: Int = 1,
    val downloadUrl: String = "https://github.com/vishuupadhyay/vishu-connect/releases/latest",
    val updateUrl: String = "https://github.com/vishuupadhyay/vishu-connect/releases/latest",
    val apkSize: String = "12.4 MB",
    val releaseNotes: String = "• Real-time Firebase Firestore Sync\n• Live Community Doubt Solving\n• 240+ BCA Solvers & Jivan AI",
    val isForceUpdate: Boolean = false,
    val isMandatory: Boolean = false,
    val publishDate: String = "Sep 2026"
)

data class CreatorIntroConfig(
    val fullName: String = "Vishu Upadhyay",
    val name: String = "Vishu Upadhyay",
    val title: String = "Founder & Lead Developer • Vishu Connect",
    val roleTitle: String = "Founder, Lead Developer & Instructor",
    val bio: String = "Passionate educator and software engineer dedicated to building modern tools for BCA, BTech, and Mathematics students worldwide.",
    val avatarUrl: String = "",
    val photoUrl: String = "",
    val instagramUsername: String = "@vishu_upadhyay_",
    val instagramUrl: String = "https://instagram.com/vishu_upadhyay_",
    val whatsappUrl: String = "https://wa.me/919876543210",
    val whatsappNumber: String = "+919876543210",
    val whatsappMessage: String = "Hello Vishu Sir, I have a question about Vishu Connect notes/classes.",
    val education: String = "Bachelor of Computer Applications (BCA) & CS Specialist",
    val skills: String = "Android (Jetpack Compose, Kotlin), Full Stack (Node.js, React), AI Integration (Gemini API), Data Structures & Algorithms, Database Engineering (SQL, Firebase, Room)",
    val githubUrl: String = "https://github.com/vishuupadhyay",
    val linkedinUrl: String = "https://linkedin.com/in/vishuupadhyay",
    val youtubeUrl: String = "https://youtube.com/@vishu_connect",
    val telegramUrl: String = "https://t.me/vishu_connect",
    val websiteUrl: String = "https://vishuconnect.edu",
    val github: String = "https://github.com/vishuupadhyay",
    val whatsapp: String = "+919876543210",
    val instagram: String = "https://instagram.com/vishu_upadhyay_",
    val linkedin: String = "https://linkedin.com/in/vishuupadhyay",
    val youtube: String = "https://youtube.com/@vishu_connect",
    val email: String = "vishuupadhyay231@gmail.com",
    val portfolio: String = "https://vishuconnect.edu",
    val location: String = "India",
    val additionalInfo: String = "Feel free to reach out for doubts, notes inquiries, or collaboration.",
    val isVisible: Boolean = true
)

data class SolverItem(
    val id: Int,
    val number: Int,
    val name: String,
    val semester: String, // Sem 1 to Sem 6, All
    val category: String, // Basic & Scientific, Trigonometry & Geometry, Combinatorics & Probability, Calculus & Matrices, Number Systems & Logic, Networking & Subnetting, Data Structures & Big-O, SGPA & Academic, Physics & Electronics, Financial & Algorithms
    val description: String,
    val formula: String,
    val labelA: String = "Value A (x)",
    val labelB: String = "Value B (y)",
    val labelC: String = "Value C (z)",
    val defaultA: String = "10",
    val defaultB: String = "2",
    val defaultC: String = "1",
    val inputCount: Int = 2, // 1, 2, or 3
    val inputType: String = "number" // number, text, boolean
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val codeBlock: String? = null,
    val language: String? = null
)
