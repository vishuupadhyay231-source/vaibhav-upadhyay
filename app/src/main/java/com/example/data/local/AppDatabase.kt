package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        NoteItem::class,
        CodeNoteItem::class,
        ClassItem::class,
        BatchItem::class,
        CourseItem::class,
        ProjectItem::class,
        AppItem::class,
        AnnouncementItem::class,
        CommunityPostItem::class,
        PostCommentItem::class,
        CarouselBannerItem::class,
        UserProfile::class,
        PaymentGatewayConfig::class,
        AuditLog::class,
        DynamicSection::class,
        UserAccount::class,
        AdminUiSettings::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vishu_connect_db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.appDao())
                }
            }
        }

        private suspend fun populateInitialData(dao: AppDao) {
            // Seed Notes
            val initialNotes = listOf(
                NoteItem(
                    title = "Data Structures & Algorithms in C++ Complete Guide",
                    description = "Comprehensive notes covering Arrays, Linked Lists, Stacks, Queues, Trees, Binary Search Trees, AVL Trees, Graphs, and Dynamic Programming.",
                    subject = "Data Structures",
                    course = "BCA",
                    semester = 3,
                    category = "Exam Notes",
                    fileName = "DSA_CPP_Complete_Notes.pdf",
                    fileSize = "4.6 MB",
                    downloadCount = 389,
                    contentSummary = "Unit 1: Time & Space Complexity Big-O.\nUnit 2: Linear Data Structures.\nUnit 3: Non-Linear Trees & Graphs.\nUnit 4: Sorting & Searching Algorithms.\nUnit 5: DP and Greedy Methods."
                ),
                NoteItem(
                    title = "Database Management System (DBMS) & SQL Handbook",
                    description = "ER Modeling, Relational Algebra, SQL Queries, Normalization (1NF, 2NF, 3NF, BCNF, 4NF), ACID properties, and Transaction Concurrency Control.",
                    subject = "DBMS",
                    course = "BCA",
                    semester = 4,
                    category = "Hand Written",
                    fileName = "DBMS_SQL_Master_Guide.pdf",
                    fileSize = "5.2 MB",
                    downloadCount = 512,
                    contentSummary = "Normalization proofs with step-by-step table decomposition. SQL DDL, DML, DCL, and complex Subqueries with Joins."
                ),
                NoteItem(
                    title = "Operating System Concepts & Shell Scripting",
                    description = "Process Management, CPU Scheduling (FCFS, SJF, Round Robin), Deadlock Prevention & Bank's Algorithm, Virtual Memory, and Paging.",
                    subject = "Operating Systems",
                    course = "BTech",
                    semester = 4,
                    category = "Question Bank",
                    fileName = "Operating_Systems_Core_Concepts.pdf",
                    fileSize = "3.9 MB",
                    downloadCount = 278,
                    contentSummary = "Deadlock Banker's Algorithm solved numericals, Page Replacement algorithms (FIFO, LRU, Optimal), and Semaphore Synchronization."
                ),
                NoteItem(
                    title = "Discrete Mathematics & Graph Theory",
                    description = "Set Theory, Relations, Propositional Logic, Truth Tables, Permutations, Combinations, Recurrence Relations, and Tree Traversals.",
                    subject = "Mathematics",
                    course = "Math",
                    semester = 2,
                    category = "Exam Notes",
                    fileName = "Discrete_Mathematics_Formulas_Proofs.pdf",
                    fileSize = "3.1 MB",
                    downloadCount = 415,
                    contentSummary = "Propositional equivalence laws, Mathematical Induction steps, Pigeonhole principle with university examination questions."
                ),
                NoteItem(
                    title = "Computer Networks & TCP/IP Protocols",
                    description = "OSI 7-Layer Architecture, Subnetting, IPv4 vs IPv6, Routing Algorithms (Dijkstra, Bellman-Ford), TCP 3-way Handshake, and DNS/HTTP protocols.",
                    subject = "Computer Networks",
                    course = "BCA",
                    semester = 5,
                    category = "Exam Notes",
                    fileName = "Computer_Networks_OSI_TCPIP.pdf",
                    fileSize = "4.1 MB",
                    downloadCount = 330,
                    contentSummary = "Subnet masking calculations with CIDR slash notation, IP addressing classes, CRC error detection, and CSMA/CD."
                ),
                NoteItem(
                    title = "Object Oriented Programming with Java",
                    description = "Classes, Polymorphism, Inheritance, Encapsulation, Abstract Classes, Interfaces, Exception Handling, Collections Framework, and Multithreading.",
                    subject = "Java Programming",
                    course = "BTech",
                    semester = 3,
                    category = "Cheat Sheet",
                    fileName = "Java_OOP_CheatSheet_Programs.pdf",
                    fileSize = "2.8 MB",
                    downloadCount = 620,
                    contentSummary = "Complete OOP cheat sheet with Java syntax examples, Collection list/map differences, and Lambda expressions."
                )
            )
            dao.insertNotes(initialNotes)

            // Seed Code Notes
            val initialCodeNotes = listOf(
                CodeNoteItem(
                    title = "Dijkstra's Shortest Path Algorithm in C++",
                    language = "C++",
                    category = "Algorithms",
                    description = "Single-source shortest path algorithm using priority queue (min-heap) with O((V + E) log V) time complexity.",
                    codeSnippet = """#include <iostream>
#include <vector>
#include <queue>
using namespace std;

typedef pair<int, int> pii; // (weight, node)

void dijkstra(int src, int V, vector<vector<pii>>& adj) {
    priority_queue<pii, vector<pii>, greater<pii>> pq;
    vector<int> dist(V, 1e9);

    dist[src] = 0;
    pq.push({0, src});

    while (!pq.empty()) {
        int u = pq.top().second;
        int d = pq.top().first;
        pq.pop();

        if (d > dist[u]) continue;

        for (auto& edge : adj[u]) {
            int v = edge.second;
            int weight = edge.first;
            if (dist[u] + weight < dist[v]) {
                dist[v] = dist[u] + weight;
                pq.push({dist[v], v});
            }
        }
    }

    cout << "Shortest distances from source " << src << ":\n";
    for (int i = 0; i < V; ++i)
        cout << "Node " << i << " -> " << dist[i] << "\n";
}""",
                    explanation = "Dijkstra's algorithm greedily extracts the node with minimum tentative distance using a priority queue. Essential for routing and shortest path graphs.",
                    output = "Shortest distances from source 0:\nNode 0 -> 0\nNode 1 -> 4\nNode 2 -> 12\nNode 3 -> 19",
                    difficulty = "Advanced"
                ),
                CodeNoteItem(
                    title = "Binary Search Tree (BST) Operations in Java",
                    language = "Java",
                    category = "Data Structures",
                    description = "Node insertion, recursive search, and Inorder traversal (yielding sorted output).",
                    codeSnippet = """class BST {
    static class Node {
        int val;
        Node left, right;
        Node(int val) { this.val = val; }
    }

    Node root;

    Node insert(Node root, int val) {
        if (root == null) return new Node(val);
        if (val < root.val) root.left = insert(root.left, val);
        else if (val > root.val) root.right = insert(root.right, val);
        return root;
    }

    void inorder(Node root) {
        if (root == null) return;
        inorder(root.left);
        System.out.print(root.val + " ");
        inorder(root.right);
    }
}""",
                    explanation = "Left subtrees contain smaller elements; right subtrees contain larger elements. Average search and insertion time is O(log N).",
                    output = "Inorder Traversal: 10 20 30 40 50 60 70",
                    difficulty = "Intermediate"
                )
            )
            dao.insertCodeNotes(initialCodeNotes)

            // Seed Classes
            val initialClasses = listOf(
                ClassItem(
                    title = "Advanced Data Structures & Algorithms",
                    subject = "DSA (BCA/BTech)",
                    teacher = "Prof. Vishu Upadhyay",
                    date = "Today",
                    time = "05:00 PM - 06:30 PM",
                    description = "Live interactive session on AVL tree balancing, Graph traversals (BFS/DFS), and Top university numericals.",
                    meetLink = "https://meet.google.com/abc-vishu-live",
                    status = "Live",
                    batchName = "BCA 2026 Alpha Batch"
                ),
                ClassItem(
                    title = "Computer Networks & Subnetting Live",
                    subject = "Computer Networks",
                    teacher = "Prof. Vishu Upadhyay",
                    date = "Tomorrow",
                    time = "06:30 PM - 08:00 PM",
                    description = "CIDR subnet calculation, Classless IP addressing, and Dijkstra routing packet simulation.",
                    meetLink = "https://meet.google.com/net-vishu-live",
                    status = "Upcoming",
                    batchName = "BTech CS 2026 Batch"
                )
            )
            dao.insertClasses(initialClasses)

            // Seed Batches
            val initialBatches = listOf(
                BatchItem(
                    name = "BCA Mastermind 2026 Batch",
                    description = "All-inclusive batch covering Sem 1 to 6 subjects, live coding classes, lab exams, and viva preparation.",
                    course = "BCA (All Semesters)",
                    instructor = "Vishu Upadhyay & Team",
                    startDate = "15 Aug 2026",
                    endDate = "30 Dec 2026",
                    schedule = "Mon, Wed, Fri (5:00 PM)",
                    status = "Active",
                    enrolledStudents = 142
                ),
                BatchItem(
                    name = "BTech CS Core & DSA Cohort",
                    description = "Operating Systems, Networks, DBMS, System Design, and Modern Web/Mobile development.",
                    course = "BTech Computer Science",
                    instructor = "Vishu Upadhyay",
                    startDate = "01 Sep 2026",
                    endDate = "15 Jan 2027",
                    schedule = "Tue, Thu, Sat (7:00 PM)",
                    status = "Enrolling",
                    enrolledStudents = 96
                )
            )
            dao.insertBatches(initialBatches)

            // Seed Courses
            val initialCourses = listOf(
                CourseItem(
                    title = "BCA & BTech Complete DSA in Modern C++",
                    description = "Master all linear and non-linear data structures, Big-O analysis, LeetCode patterns, and university exam questions.",
                    instructor = "Vishu Upadhyay",
                    duration = "36 Hours (48 Lessons)",
                    totalLessons = 48,
                    isFree = true,
                    rating = 4.9f,
                    level = "Beginner to Advanced",
                    syllabusTopics = "Time Complexity, Arrays, Pointers, Linked Lists, Stacks, Queues, Binary Trees, BST, Graphs, Dynamic Programming"
                ),
                CourseItem(
                    title = "Android App Development with Jetpack Compose",
                    description = "Learn modern Android development from scratch using Kotlin, Material 3, Coroutines, Room DB, Retrofit, and Firebase.",
                    instructor = "Vishu Upadhyay",
                    duration = "28 Hours (35 Lessons)",
                    totalLessons = 35,
                    isFree = true,
                    rating = 4.95f,
                    level = "Intermediate",
                    syllabusTopics = "Kotlin Basics, Jetpack Compose UI, State Management, MVVM Architecture, Room Database, REST APIs, Google Play Publishing"
                )
            )
            dao.insertCourses(initialCourses)

            // Seed Projects (2 Free college projects + 2 Premium production projects)
            val initialProjects = listOf(
                ProjectItem(
                    id = 1,
                    title = "Student Academic Portal & Notes Hub",
                    description = "Complete college semester portal with offline PDF reader, Unit solvers, and Google Meet integration. Ideal for BCA/BTech minor project.",
                    category = "Minor Project",
                    techStack = "Kotlin, Jetpack Compose, Room DB, Material 3",
                    version = "v1.2.0",
                    githubUrl = "https://github.com/vishuupadhyay/student-portal",
                    demoUrl = "https://vishuconnect.edu/demo/student-portal",
                    videoUrl = "https://youtube.com/@vishu_connect",
                    downloadFileName = "Student_Portal_Full_Source.zip",
                    fileSize = "16.8 MB",
                    isFeatured = true,
                    isFree = true,
                    price = 0.0,
                    downloadCount = 192,
                    rating = 4.9f
                ),
                ProjectItem(
                    id = 2,
                    title = "AI Smart Math & Big-O Solver Engine",
                    description = "Python & FastAPI backend integrated with Gemini AI to solve calculus, subnet calculations, and algorithm time complexity step-by-step.",
                    category = "Major Project",
                    techStack = "Python, FastAPI, Gemini API, React, SQLite",
                    version = "v2.0.1",
                    githubUrl = "https://github.com/vishuupadhyay/ai-solver-engine",
                    demoUrl = "https://vishuconnect.edu/demo/ai-solver",
                    videoUrl = "https://youtube.com/@vishu_connect",
                    downloadFileName = "AI_Math_Solver_Source.zip",
                    fileSize = "22.4 MB",
                    isFeatured = true,
                    isFree = true,
                    price = 0.0,
                    downloadCount = 245,
                    rating = 4.95f
                ),
                ProjectItem(
                    id = 3,
                    title = "Full Stack Multi-Vendor Campus Store & Canteen",
                    description = "Production ready multi-vendor ordering system with real-time order tracking, UPI payment gateway, inventory management, and admin dashboard.",
                    category = "Major Project",
                    techStack = "Kotlin, Jetpack Compose, Node.js, MongoDB, Razorpay",
                    version = "v3.1.0",
                    githubUrl = "https://github.com/vishuupadhyay/campus-store",
                    demoUrl = "https://vishuconnect.edu/demo/campus-store",
                    videoUrl = "https://youtube.com/@vishu_connect",
                    downloadFileName = "Campus_Store_FullStack.zip",
                    fileSize = "34.5 MB",
                    isFeatured = false,
                    isFree = false,
                    price = 199.0,
                    downloadCount = 74,
                    rating = 4.85f
                ),
                ProjectItem(
                    id = 4,
                    title = "IoT Smart College Automation & Attendance System",
                    description = "RFID and Biometric student attendance system with ESP32 microcontroller, Firebase real-time sync, SMS alert trigger, and Android companion app.",
                    category = "IoT & Hardware",
                    techStack = "Embedded C, ESP32, Firebase Cloud, Android Kotlin",
                    version = "v2.5.0",
                    githubUrl = "https://github.com/vishuupadhyay/iot-smart-campus",
                    demoUrl = "https://vishuconnect.edu/demo/iot-campus",
                    videoUrl = "https://youtube.com/@vishu_connect",
                    downloadFileName = "IoT_Smart_Campus_Firmware_App.zip",
                    fileSize = "28.2 MB",
                    isFeatured = false,
                    isFree = false,
                    price = 249.0,
                    downloadCount = 58,
                    rating = 4.9f
                )
            )
            dao.insertProjects(initialProjects)

            // Seed Apps
            val initialApps = listOf(
                AppItem(
                    name = "Vishu Connect Student App",
                    description = "The official Vishu Connect Android APK with offline notes, live classes, study calculators, and AI mentor.",
                    version = "v1.0.0",
                    apkSize = "15.4 MB",
                    releaseDate = "21 Aug 2026",
                    githubUrl = "https://github.com/vishuupadhyay",
                    featuresList = "Offline Notes Cache, Scientific & CS Calculators, Jivan AI Study Mentor, Dark Theme, Google Meet Hub"
                )
            )
            dao.insertApps(initialApps)

            // Seed Announcements
            val initialAnnouncements = listOf(
                AnnouncementItem(
                    title = "🎉 Welcome to Vishu Connect Platform!",
                    description = "All semester notes for BCA, BTech, and Mathematics are now uploaded and free to download. Check the 200+ Calculators and Jivan AI Assistant for instant study help!",
                    date = "Today",
                    priority = "Urgent",
                    isPublished = true,
                    author = "Prof. Vishu Upadhyay"
                ),
                AnnouncementItem(
                    title = "🔴 Live Google Meet Class Scheduled",
                    description = "Special interactive session on Data Structures & Algorithms this evening at 05:00 PM. Join via the Classes section.",
                    date = "Today",
                    priority = "High",
                    isPublished = true,
                    author = "Prof. Vishu Upadhyay"
                )
            )
            dao.insertAnnouncements(initialAnnouncements)

            // Seed Community Posts
            val initialPosts = listOf(
                CommunityPostItem(
                    id = 1,
                    authorName = "Prof. Vishu Upadhyay",
                    authorRole = "Admin",
                    isPinned = true,
                    title = "📢 BCA Semester 4 & 6 Model Examination Papers Uploaded!",
                    content = "Dear students, all solved model test papers for DBMS, Computer Networks, Java OOP, and Operating Systems have been published in the Notes Hub. Please download them and practice the numericals thoroughly.",
                    likesCount = 38,
                    commentsCount = 4,
                    timestamp = "2 hours ago",
                    tag = "ANNOUNCEMENT",
                    isLikedByMe = true
                ),
                CommunityPostItem(
                    id = 2,
                    authorName = "Aman Sharma (BCA Sem 3)",
                    authorRole = "Student",
                    isPinned = false,
                    title = "❓ Doubt in AVL Tree Left-Right (LR) Double Rotation",
                    content = "Can someone explain how the node balancing is restored during an LR rotation? I am getting confused when the child node has its own subtrees.",
                    likesCount = 14,
                    commentsCount = 2,
                    timestamp = "4 hours ago",
                    tag = "DOUBT",
                    isLikedByMe = false
                ),
                CommunityPostItem(
                    id = 3,
                    authorName = "Prof. Vishu Upadhyay",
                    authorRole = "Admin",
                    isPinned = false,
                    title = "💡 Tip: 200+ STEM & BCA Solvers are now live!",
                    content = "We have launched the 200 BCA & STEM Solvers suite. You can solve arithmetic, binary logic, subnet masks, matrix eigenvalues, Big-O complexity, and SGPA calculations with step-by-step proofs directly inside the app.",
                    likesCount = 56,
                    commentsCount = 5,
                    timestamp = "Yesterday",
                    tag = "TIPS",
                    isLikedByMe = true
                )
            )
            dao.insertCommunityPosts(initialPosts)

            // Seed Comments
            val initialComments = listOf(
                PostCommentItem(
                    postId = 1,
                    authorName = "Priya Patel",
                    authorRole = "Student",
                    commentText = "Thank you sir! The DBMS normalization notes are crystal clear.",
                    timestamp = "1 hour ago",
                    isVerified = true
                ),
                PostCommentItem(
                    postId = 1,
                    authorName = "Prof. Vishu Upadhyay",
                    authorRole = "Admin",
                    commentText = "Glad to hear that Priya! Keep practicing the SQL subquery questions as well.",
                    timestamp = "45 mins ago",
                    isVerified = true
                ),
                PostCommentItem(
                    postId = 2,
                    authorName = "Prof. Vishu Upadhyay",
                    authorRole = "Admin",
                    commentText = "Hi Aman! LR rotation is essentially a Left rotation on the left child, followed by a Right rotation on the root. Check solver #143 (AVL Tree Balance Factor) in the Calculators tab for an interactive visual demo!",
                    timestamp = "3 hours ago",
                    isVerified = true
                ),
                PostCommentItem(
                    postId = 2,
                    authorName = "Aman Sharma",
                    authorRole = "Student",
                    commentText = "Understood sir! Just checked solver #143, that made it so easy.",
                    timestamp = "2 hours ago",
                    isVerified = false
                )
            )
            dao.insertPostComments(initialComments)

            // Seed Carousel Banners (6 interactive posters for fast carousel animation)
            val initialBanners = listOf(
                CarouselBannerItem(
                    id = 1,
                    title = "JIVAN STUDY COMPANION",
                    subtitle = "Smart AI Tutor — Ask doubts, generate code & solve complex math",
                    badgeText = "Smart AI Tutor",
                    actionType = "navigate",
                    actionTarget = "ai_assistant"
                ),
                CarouselBannerItem(
                    id = 2,
                    title = "240 BCA & STEM SOLVERS",
                    subtitle = "Step-by-step calculators for Math, Logic, Networks, AI & Big-O",
                    badgeText = "240 Tools",
                    actionType = "navigate",
                    actionTarget = "calculators"
                ),
                CarouselBannerItem(
                    id = 3,
                    title = "LIVE GOOGLE MEET CLASSES",
                    subtitle = "Join daily live lectures with screen sharing & real-time Q&A",
                    badgeText = "Live Batches",
                    actionType = "navigate",
                    actionTarget = "classes"
                ),
                CarouselBannerItem(
                    id = 4,
                    title = "SEMESTER NOTES & QUESTION BANK",
                    subtitle = "Free & premium handwritten notes with instant PDF download",
                    badgeText = "Semester Guides",
                    actionType = "navigate",
                    actionTarget = "notes"
                ),
                CarouselBannerItem(
                    id = 5,
                    title = "CAMPUS COMMUNITY & DOUBT DESK",
                    subtitle = "Ask your programming doubts & get verified answers from mentors",
                    badgeText = "Ask Doubts",
                    actionType = "navigate",
                    actionTarget = "posts"
                ),
                CarouselBannerItem(
                    id = 6,
                    title = "MAJOR & MINOR PROJECT VAULT",
                    subtitle = "Full-stack, Kotlin Android & Python project source code & guides",
                    badgeText = "Source Code",
                    actionType = "navigate",
                    actionTarget = "projects_apps"
                )
            )
            dao.insertCarouselBanners(initialBanners)

            // Seed Admin UI Settings
            val initialUiSettings = AdminUiSettings()
            dao.saveAdminUiSettings(initialUiSettings)

            // Seed Registered App Users (Only verified Administrator - no fake users)
            val initialUsers = listOf(
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
            dao.insertUsers(initialUsers)

            // Seed Payment Gateway Configuration
            val initialGateway = PaymentGatewayConfig()
            dao.savePaymentGatewayConfig(initialGateway)

            // Seed User Profile
            val initialProfile = UserProfile()
            dao.saveUserProfile(initialProfile)

            // Seed Dynamic Sections
            val initialSections = listOf(
                DynamicSection(key = "notes", title = "Notes Hub", iconName = "MenuBook", orderIndex = 1, isVisible = true, description = "Semester wise PDF notes & guides"),
                DynamicSection(key = "classes", title = "Live Classes", iconName = "School", orderIndex = 2, isVisible = true, description = "Lecture schedule & resources"),
                DynamicSection(key = "posts", title = "Community & Posts", iconName = "Forum", orderIndex = 3, isVisible = true, description = "Doubts, announcements & discussions"),
                DynamicSection(key = "calculators", title = "240 BCA Solvers", iconName = "Calculate", orderIndex = 4, isVisible = true, description = "STEM, Math, CS & Networking calculators"),
                DynamicSection(key = "code_notes", title = "Code Notes", iconName = "Code", orderIndex = 5, isVisible = true, description = "Programming snippets & DSA solutions"),
                DynamicSection(key = "courses", title = "Courses", iconName = "WorkspacePremium", orderIndex = 6, isVisible = true, description = "Structured video courses & modules"),
                DynamicSection(key = "projects", title = "Projects Hub", iconName = "FolderSpecial", orderIndex = 7, isVisible = true, description = "Major & minor project source codes"),
                DynamicSection(key = "apps", title = "Apps & APKs", iconName = "Android", orderIndex = 8, isVisible = true, description = "Educational tools & APK downloads"),
                DynamicSection(key = "ai_assistant", title = "Jivan AI Assistant", iconName = "AutoAwesome", orderIndex = 9, isVisible = true, description = "Intelligent BCA/BTech study mentor"),
                DynamicSection(key = "about_vishu", title = "Creator Profile", iconName = "PersonPin", orderIndex = 10, isVisible = true, description = "About Vishu Upadhyay & Portfolio"),
                DynamicSection(key = "profile", title = "My Profile", iconName = "AccountCircle", orderIndex = 11, isVisible = true, description = "Account & personal details")
            )
            dao.insertDynamicSections(initialSections)

            // Initial Audit Log
            dao.insertAuditLog(
                AuditLog(
                    action = "System Initialized",
                    target = "Vishu Connect v2.0 Production Suite",
                    adminUser = "system",
                    timestamp = "21 Aug 2026, 01:45 AM"
                )
            )
        }
    }
}
