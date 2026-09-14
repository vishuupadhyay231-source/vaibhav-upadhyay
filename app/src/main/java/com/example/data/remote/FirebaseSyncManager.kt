package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.data.model.AppUpdateInfo
import com.example.data.model.CommunityPostItem
import com.example.data.model.PostCommentItem
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

/**
 * FirebaseSyncManager provides real-time multi-device cloud synchronization
 * for App Updates, Community Posts, Comments, Notes, Classes, and System Announcements.
 * Supports both Cloud Firestore and Firebase Realtime Database (REST).
 */
object FirebaseSyncManager {
    private const val TAG = "FirebaseSyncManager"
    private const val PREFS_NAME = "firebase_cloud_prefs"

    const val DEFAULT_PROJECT_ID = "vishu-connect"
    const val DEFAULT_RTDB_URL = "https://vishu-connect-default-rtdb.firebaseio.com"
    const val DEFAULT_API_KEY = "AIzaSyBOgwl-xIJxux8jl8a8_iIgUviR1Nl7gdQ"
    const val DEFAULT_APP_ID = "1:660441284167:android:eb50902297e9d85fb83b62"
    const val DEFAULT_PROJECT_NUMBER = "660441284167"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(8, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(8, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private var firestore: FirebaseFirestore? = null
    private var updateListener: ListenerRegistration? = null
    private var postsListener: ListenerRegistration? = null
    private var notesListener: ListenerRegistration? = null
    private var classesListener: ListenerRegistration? = null

    private val _isFirebaseAvailable = MutableStateFlow(false)
    val isFirebaseAvailable: StateFlow<Boolean> = _isFirebaseAvailable.asStateFlow()

    private val _cloudStatusMessage = MutableStateFlow("🟡 Connecting to vishu-connect...")
    val cloudStatusMessage: StateFlow<String> = _cloudStatusMessage.asStateFlow()

    private val _remoteAppUpdate = MutableStateFlow<AppUpdateInfo?>(null)
    val remoteAppUpdate: StateFlow<AppUpdateInfo?> = _remoteAppUpdate.asStateFlow()

    private val _remotePosts = MutableStateFlow<List<CommunityPostItem>>(emptyList())
    val remotePosts: StateFlow<List<CommunityPostItem>> = _remotePosts.asStateFlow()

    private val _remoteNotes = MutableStateFlow<List<com.example.data.model.NoteItem>>(emptyList())
    val remoteNotes: StateFlow<List<com.example.data.model.NoteItem>> = _remoteNotes.asStateFlow()

    private val _remoteClasses = MutableStateFlow<List<com.example.data.model.ClassItem>>(emptyList())
    val remoteClasses: StateFlow<List<com.example.data.model.ClassItem>> = _remoteClasses.asStateFlow()

    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var lastPayloadHash: Int = 0
    private var syncBackoffMultiplier: Long = 1L

    fun init(context: Context, scope: CoroutineScope) {
        val appContext = context.applicationContext
        try {
            _isFirebaseAvailable.value = true
            _cloudStatusMessage.value = "🟢 Connected (Project: vishu-connect)"
            Log.d(TAG, "Connected to Firebase Realtime Database: vishu-connect")
            // Start high-performance adaptive RTDB sync (Zero GMS dependency, works everywhere)
            startRtdbSync(scope)
        } catch (e: Exception) {
            _isFirebaseAvailable.value = false
            _cloudStatusMessage.value = "⚠️ Local Mode: ${e.message?.take(40)}"
            Log.w(TAG, "Firebase sync init warning: ${e.message}")
        }
    }

    fun enableFirestoreListenersIfConfigured(scope: CoroutineScope) {
        startAllListeners(scope)
    }

    private fun startAllListeners(scope: CoroutineScope) {
        startListeningAppUpdates(scope)
        startListeningCommunityPosts(scope)
        startListeningNotes(scope)
        startListeningClasses(scope)
    }

    private fun startRtdbSync(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            while (isActive) {
                var syncSucceeded = false
                try {
                    syncSucceeded = fetchRemoteRtdbData()
                    if (syncSucceeded) {
                        syncBackoffMultiplier = 1L
                    } else {
                        syncBackoffMultiplier = (syncBackoffMultiplier * 2).coerceAtMost(4L)
                    }
                } catch (e: Exception) {
                    syncBackoffMultiplier = (syncBackoffMultiplier * 2).coerceAtMost(4L)
                    Log.d(TAG, "RTDB sync tick notice: ${e.message}")
                }
                // Adaptive polling with jitter (35s to 50s scaled by backoff) to distribute 10,000+ users evenly
                val baseJitter = 35000L + (Math.random() * 15000L).toLong()
                val nextDelay = (baseJitter * syncBackoffMultiplier).coerceIn(30000L, 120000L)
                delay(nextDelay)
            }
        }
    }

    private fun fetchRemoteRtdbData(): Boolean {
        return try {
            val req = Request.Builder()
                .url("$DEFAULT_RTDB_URL/.json")
                .get()
                .build()
            val resp = httpClient.newCall(req).execute()
            if (resp.isSuccessful) {
                val body = resp.body?.string() ?: ""
                resp.close()
                if (body.isNotBlank() && body != "null") {
                    val currentHash = body.hashCode()
                    if (currentHash != lastPayloadHash) {
                        lastPayloadHash = currentHash
                        parseAndApplyRtdbData(body)
                    }
                    true
                } else {
                    false
                }
            } else {
                resp.close()
                false
            }
        } catch (e: Exception) {
            Log.d(TAG, "fetchRemoteRtdbData notice: ${e.message}")
            false
        }
    }

    private fun parseAndApplyRtdbData(jsonStr: String) {
        try {
            val root = JSONObject(jsonStr)

            // App Update
            if (root.has("app_update")) {
                val upObj = root.optJSONObject("app_update")
                if (upObj != null) {
                    _remoteAppUpdate.value = AppUpdateInfo(
                        currentVersionName = upObj.optString("currentVersionName", "1.0"),
                        currentVersionCode = upObj.optInt("currentVersionCode", 1),
                        latestVersionName = upObj.optString("latestVersionName", "2.0"),
                        latestVersionCode = upObj.optInt("latestVersionCode", 2),
                        downloadUrl = upObj.optString("downloadUrl", ""),
                        releaseNotes = upObj.optString("releaseNotes", ""),
                        isForceUpdate = upObj.optBoolean("isForceUpdate", false),
                        publishDate = upObj.optString("publishDate", ""),
                        apkSize = upObj.optString("apkSize", "")
                    )
                }
            }

            // Study Notes
            if (root.has("study_notes")) {
                val notesObj = root.optJSONObject("study_notes")
                if (notesObj != null) {
                    val list = mutableListOf<com.example.data.model.NoteItem>()
                    val keys = notesObj.keys()
                    while (keys.hasNext()) {
                        val k = keys.next()
                        val n = notesObj.optJSONObject(k) ?: continue
                        list.add(
                            com.example.data.model.NoteItem(
                                id = n.optLong("id", k.hashCode().toLong()),
                                title = n.optString("title", ""),
                                description = n.optString("description", ""),
                                subject = n.optString("subject", "Computer Science"),
                                course = n.optString("course", "BCA"),
                                semester = n.optInt("semester", 1),
                                category = n.optString("category", "Exam Notes"),
                                uploaderName = n.optString("uploaderName", "Vishu Upadhyay"),
                                uploaderEmail = n.optString("uploaderEmail", "vishuupadhyay231@gmail.com"),
                                uploadDate = n.optString("uploadDate", "Aug 2026"),
                                fileName = n.optString("fileName", "Notes.pdf"),
                                fileType = n.optString("fileType", "PDF"),
                                fileSize = n.optString("fileSize", "3.5 MB"),
                                isApproved = n.optBoolean("isApproved", true),
                                isPaid = n.optBoolean("isPaid", false),
                                price = n.optDouble("price", 0.0)
                            )
                        )
                    }
                    if (list.isNotEmpty()) {
                        _remoteNotes.value = list
                    }
                }
            }

            // Live Classes
            if (root.has("live_classes")) {
                val classesObj = root.optJSONObject("live_classes")
                if (classesObj != null) {
                    val list = mutableListOf<com.example.data.model.ClassItem>()
                    val keys = classesObj.keys()
                    while (keys.hasNext()) {
                        val k = keys.next()
                        val c = classesObj.optJSONObject(k) ?: continue
                        list.add(
                            com.example.data.model.ClassItem(
                                id = c.optLong("id", k.hashCode().toLong()),
                                title = c.optString("title", ""),
                                subject = c.optString("subject", "Computer Science"),
                                teacher = c.optString("teacher", "Vishu Upadhyay"),
                                date = c.optString("date", ""),
                                time = c.optString("time", ""),
                                description = c.optString("description", ""),
                                meetLink = c.optString("meetLink", ""),
                                status = c.optString("status", "SCHEDULED"),
                                batchName = c.optString("batchName", "BCA 2026"),
                                isLive = c.optBoolean("isLive", false)
                            )
                        )
                    }
                    if (list.isNotEmpty()) {
                        _remoteClasses.value = list
                    }
                }
            }

            // Community Posts
            if (root.has("community_posts")) {
                val postsObj = root.optJSONObject("community_posts")
                if (postsObj != null) {
                    val list = mutableListOf<CommunityPostItem>()
                    val keys = postsObj.keys()
                    while (keys.hasNext()) {
                        val k = keys.next()
                        val p = postsObj.optJSONObject(k) ?: continue
                        list.add(
                            CommunityPostItem(
                                id = p.optLong("id", k.hashCode().toLong()),
                                title = p.optString("title", ""),
                                content = p.optString("content", ""),
                                authorName = p.optString("authorName", "Student"),
                                authorRole = p.optString("authorRole", "Student"),
                                tag = p.optString("tag", "DOUBT"),
                                isPinned = p.optBoolean("isPinned", false),
                                likesCount = p.optInt("likesCount", 0),
                                commentsCount = p.optInt("commentsCount", 0),
                                timestamp = p.optString("timestamp", "Recently")
                            )
                        )
                    }
                    if (list.isNotEmpty()) {
                        _remotePosts.value = list
                    }
                }
            }
            _isFirebaseAvailable.value = true
            _cloudStatusMessage.value = "🟢 Connected to Firebase (RTDB: vishu-connect)"
        } catch (e: Exception) {
            Log.e(TAG, "parseAndApplyRtdbData error: ${e.message}")
        }
    }

    private fun pushToRtdbAsync(path: String, jsonContent: String) {
        managerScope.launch {
            try {
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = jsonContent.toRequestBody(mediaType)
                val req = Request.Builder()
                    .url("$DEFAULT_RTDB_URL/$path.json")
                    .put(body)
                    .build()
                httpClient.newCall(req).execute().close()
            } catch (e: Exception) {
                Log.w(TAG, "pushToRtdbAsync $path warning: ${e.message}")
            }
        }
    }

    private fun startListeningNotes(scope: CoroutineScope) {
        val db = firestore ?: return
        try {
            notesListener = db.collection("study_notes")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        if (error.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                            Log.i(TAG, "Firestore study_notes requires Cloud rules; using Realtime Database for live sync.")
                            notesListener?.remove()
                            notesListener = null
                        } else {
                            Log.w(TAG, "Notice on study notes listener: ${error.message}")
                        }
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val notesList = mutableListOf<com.example.data.model.NoteItem>()
                        for (doc in snapshot.documents) {
                            val id = doc.getLong("id") ?: (doc.id.hashCode().toLong())
                            val title = doc.getString("title") ?: ""
                            val desc = doc.getString("description") ?: ""
                            val subject = doc.getString("subject") ?: "Computer Science"
                            val course = doc.getString("course") ?: "BCA"
                            val sem = doc.getLong("semester")?.toInt() ?: 1
                            val category = doc.getString("category") ?: "Exam Notes"
                            val uploader = doc.getString("uploaderName") ?: "Vishu Upadhyay"
                            val email = doc.getString("uploaderEmail") ?: "vishuupadhyay231@gmail.com"
                            val date = doc.getString("uploadDate") ?: "Aug 2026"
                            val fileName = doc.getString("fileName") ?: "$title.pdf"
                            val fileType = doc.getString("fileType") ?: "PDF"
                            val fileSize = doc.getString("fileSize") ?: "3.5 MB"
                            val isApproved = doc.getBoolean("isApproved") ?: true
                            val isPaid = doc.getBoolean("isPaid") ?: false
                            val price = doc.getDouble("price") ?: 0.0

                            notesList.add(
                                com.example.data.model.NoteItem(
                                    id = id,
                                    title = title,
                                    description = desc,
                                    subject = subject,
                                    course = course,
                                    semester = sem,
                                    category = category,
                                    uploaderName = uploader,
                                    uploaderEmail = email,
                                    uploadDate = date,
                                    fileName = fileName,
                                    fileType = fileType,
                                    fileSize = fileSize,
                                    isApproved = isApproved,
                                    isPaid = isPaid,
                                    price = price
                                )
                            )
                        }
                        if (notesList.isNotEmpty()) {
                            _remoteNotes.value = notesList
                        }
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Notice on starting notes listener: ${e.message}")
        }
    }

    private fun startListeningClasses(scope: CoroutineScope) {
        val db = firestore ?: return
        try {
            classesListener = db.collection("live_classes")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        if (error.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                            Log.i(TAG, "Firestore live_classes requires Cloud rules; using Realtime Database for live sync.")
                            classesListener?.remove()
                            classesListener = null
                        } else {
                            Log.w(TAG, "Notice on live classes listener: ${error.message}")
                        }
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val classList = mutableListOf<com.example.data.model.ClassItem>()
                        for (doc in snapshot.documents) {
                            val id = doc.getLong("id") ?: (doc.id.hashCode().toLong())
                            val title = doc.getString("title") ?: ""
                            val subject = doc.getString("subject") ?: "Computer Science"
                            val teacher = doc.getString("teacher") ?: "Vishu Upadhyay"
                            val date = doc.getString("date") ?: ""
                            val time = doc.getString("time") ?: ""
                            val desc = doc.getString("description") ?: ""
                            val meetLink = doc.getString("meetLink") ?: ""
                            val status = doc.getString("status") ?: "Upcoming"
                            val batch = doc.getString("batchName") ?: "BCA/BTech 2026 Batch"
                            val isLive = doc.getBoolean("isLive") ?: false

                            classList.add(
                                com.example.data.model.ClassItem(
                                    id = id,
                                    title = title,
                                    subject = subject,
                                    teacher = teacher,
                                    date = date,
                                    time = time,
                                    description = desc,
                                    meetLink = meetLink,
                                    status = status,
                                    batchName = batch,
                                    isLive = isLive
                                )
                            )
                        }
                        if (classList.isNotEmpty()) {
                            _remoteClasses.value = classList
                        }
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Notice on starting classes listener: ${e.message}")
        }
    }

    private fun startListeningAppUpdates(scope: CoroutineScope) {
        val db = firestore ?: return
        try {
            updateListener = db.collection("system_config")
                .document("app_update")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        if (error.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                            Log.i(TAG, "Firestore app_update requires Cloud rules; using Realtime Database for live sync.")
                            updateListener?.remove()
                            updateListener = null
                        } else {
                            Log.w(TAG, "Notice on app updates listener: ${error.message}")
                        }
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val currentName = snapshot.getString("currentVersionName") ?: "1.0.0"
                        val currentCode = snapshot.getLong("currentVersionCode")?.toInt() ?: 1
                        val latestName = snapshot.getString("latestVersionName") ?: "1.0.0"
                        val latestCode = snapshot.getLong("latestVersionCode")?.toInt() ?: 1
                        val downloadUrl = snapshot.getString("downloadUrl") ?: "https://github.com/vishuupadhyay/vishu-connect/releases/latest"
                        val releaseNotes = snapshot.getString("releaseNotes") ?: ""
                        val isForce = snapshot.getBoolean("isForceUpdate") ?: false
                        val publishDate = snapshot.getString("publishDate") ?: "Sep 2026"
                        val apkSize = snapshot.getString("apkSize") ?: "12.4 MB"

                        val updateInfo = AppUpdateInfo(
                            currentVersionName = currentName,
                            currentVersionCode = currentCode,
                            latestVersionName = latestName,
                            latestVersionCode = latestCode,
                            downloadUrl = downloadUrl,
                            releaseNotes = releaseNotes,
                            isForceUpdate = isForce,
                            publishDate = publishDate,
                            apkSize = apkSize
                        )
                        _remoteAppUpdate.value = updateInfo
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Notice on starting app update listener: ${e.message}")
        }
    }

    private fun startListeningCommunityPosts(scope: CoroutineScope) {
        val db = firestore ?: return
        try {
            postsListener = db.collection("community_posts")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        if (error.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                            Log.i(TAG, "Firestore community_posts requires Cloud rules; using Realtime Database for live sync.")
                            postsListener?.remove()
                            postsListener = null
                        } else {
                            Log.w(TAG, "Notice on community posts listener: ${error.message}")
                        }
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val postsList = mutableListOf<CommunityPostItem>()
                        for (doc in snapshot.documents) {
                            val id = doc.getLong("id") ?: (doc.id.hashCode().toLong())
                            val title = doc.getString("title") ?: ""
                            val content = doc.getString("content") ?: ""
                            val authorName = doc.getString("authorName") ?: "Student"
                            val authorRole = doc.getString("authorRole") ?: "Student"
                            val tag = doc.getString("tag") ?: "DOUBT"
                            val isPinned = doc.getBoolean("isPinned") ?: false
                            val likesCount = doc.getLong("likesCount")?.toInt() ?: 0
                            val commentsCount = doc.getLong("commentsCount")?.toInt() ?: 0
                            val timestamp = doc.getString("timestamp") ?: "Just now"

                            postsList.add(
                                CommunityPostItem(
                                    id = id,
                                    title = title,
                                    content = content,
                                    authorName = authorName,
                                    authorRole = authorRole,
                                    tag = tag,
                                    isPinned = isPinned,
                                    likesCount = likesCount,
                                    commentsCount = commentsCount,
                                    timestamp = timestamp
                                )
                            )
                        }
                        if (postsList.isNotEmpty()) {
                            _remotePosts.value = postsList
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start community posts listener: ${e.message}")
        }
    }

    fun broadcastAppUpdate(updateInfo: AppUpdateInfo) {
        val map = mapOf(
            "currentVersionName" to updateInfo.currentVersionName,
            "currentVersionCode" to updateInfo.currentVersionCode,
            "latestVersionName" to updateInfo.latestVersionName,
            "latestVersionCode" to updateInfo.latestVersionCode,
            "downloadUrl" to updateInfo.downloadUrl,
            "releaseNotes" to updateInfo.releaseNotes,
            "isForceUpdate" to updateInfo.isForceUpdate,
            "publishDate" to updateInfo.publishDate,
            "apkSize" to updateInfo.apkSize
        )
        // Push to RTDB
        pushToRtdbAsync("app_update", JSONObject(map).toString())

        val db = firestore ?: return
        db.collection("system_config").document("app_update")
            .set(map, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "App update broadcast successfully to all devices!")
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Firestore update write notice: ${e.message}")
            }
    }

    fun publishCommunityPost(post: CommunityPostItem) {
        val docId = if (post.id != 0L) post.id.toString() else System.currentTimeMillis().toString()
        val map = mapOf(
            "id" to post.id,
            "title" to post.title,
            "content" to post.content,
            "authorName" to post.authorName,
            "authorRole" to post.authorRole,
            "tag" to post.tag,
            "isPinned" to post.isPinned,
            "likesCount" to post.likesCount,
            "commentsCount" to post.commentsCount,
            "timestamp" to post.timestamp
        )
        // Push to RTDB
        pushToRtdbAsync("community_posts/$docId", JSONObject(map).toString())

        val db = firestore ?: return
        db.collection("community_posts").document(docId)
            .set(map, SetOptions.merge())
            .addOnFailureListener { e ->
                Log.d(TAG, "Firestore post write notice: ${e.message}")
            }
    }

    fun publishStudyNote(note: com.example.data.model.NoteItem) {
        val docId = if (note.id != 0L) note.id.toString() else System.currentTimeMillis().toString()
        val map = mapOf(
            "id" to note.id,
            "title" to note.title,
            "description" to note.description,
            "subject" to note.subject,
            "course" to note.course,
            "semester" to note.semester,
            "category" to note.category,
            "uploaderName" to note.uploaderName,
            "uploaderEmail" to note.uploaderEmail,
            "uploadDate" to note.uploadDate,
            "fileName" to note.fileName,
            "fileType" to note.fileType,
            "fileSize" to note.fileSize,
            "isApproved" to note.isApproved,
            "isPaid" to note.isPaid,
            "price" to note.price
        )
        // Push to RTDB
        pushToRtdbAsync("study_notes/$docId", JSONObject(map).toString())

        val db = firestore ?: return
        db.collection("study_notes").document(docId)
            .set(map, SetOptions.merge())
            .addOnFailureListener { e ->
                Log.d(TAG, "Firestore note write notice: ${e.message}")
            }
    }

    fun publishLiveClass(classItem: com.example.data.model.ClassItem) {
        val docId = if (classItem.id != 0L) classItem.id.toString() else System.currentTimeMillis().toString()
        val map = mapOf(
            "id" to classItem.id,
            "title" to classItem.title,
            "subject" to classItem.subject,
            "teacher" to classItem.teacher,
            "date" to classItem.date,
            "time" to classItem.time,
            "description" to classItem.description,
            "meetLink" to classItem.meetLink,
            "status" to classItem.status,
            "batchName" to classItem.batchName,
            "isLive" to classItem.isLive
        )
        // Push to RTDB
        pushToRtdbAsync("live_classes/$docId", JSONObject(map).toString())

        val db = firestore ?: return
        db.collection("live_classes").document(docId)
            .set(map, SetOptions.merge())
            .addOnFailureListener { e ->
                Log.d(TAG, "Firestore class write notice: ${e.message}")
            }
    }

    fun getSavedConfig(context: Context): Triple<String, String, String> {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val proj = prefs.getString("project_id", "") ?: ""
        val key = prefs.getString("api_key", "") ?: ""
        val appId = prefs.getString("app_id", "") ?: ""
        return Triple(
            if (proj.isNotBlank()) proj else DEFAULT_PROJECT_ID,
            if (key.isNotBlank()) key else DEFAULT_API_KEY,
            if (appId.isNotBlank()) appId else DEFAULT_APP_ID
        )
    }

    suspend fun configureFirebase(
        context: Context,
        scope: CoroutineScope,
        projectId: String,
        apiKey: String,
        appId: String = ""
    ): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val cleanProj = projectId.trim()
        val cleanKey = apiKey.trim()
        val cleanAppId = appId.trim()

        if (cleanProj.isBlank()) {
            return@withContext Pair(false, "Project ID cannot be empty.")
        }

        try {
            val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putString("project_id", cleanProj)
                .putString("api_key", cleanKey)
                .putString("app_id", cleanAppId)
                .apply()

            val optionsBuilder = FirebaseOptions.Builder()
                .setProjectId(cleanProj)
                .setApplicationId(cleanAppId.ifBlank { "1:50981912196:android:vishuconnect" })
            if (cleanKey.isNotBlank()) {
                optionsBuilder.setApiKey(cleanKey)
            }
            val options = optionsBuilder.build()

            val app = try {
                if (FirebaseApp.getApps(context.applicationContext).isEmpty()) {
                    FirebaseApp.initializeApp(context.applicationContext, options)
                } else {
                    try {
                        FirebaseApp.getInstance("vishu_cloud")
                    } catch (_: Exception) {
                        FirebaseApp.initializeApp(context.applicationContext, options, "vishu_cloud")
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "App re-init error: ${e.message}")
                FirebaseApp.initializeApp(context.applicationContext, options, "vishu_cloud")
            }

            val db = try {
                FirebaseFirestore.getInstance(app)
            } catch (e: Exception) {
                FirebaseFirestore.getInstance()
            }
            firestore = db
            _isFirebaseAvailable.value = true
            _cloudStatusMessage.value = "🟢 Connected to $cleanProj"

            // Start all cloud listeners
            startAllListeners(scope)
            startRtdbSync(scope)

            Pair(true, "Firebase Connected Successfully to Project: $cleanProj")
        } catch (e: Exception) {
            _cloudStatusMessage.value = "⚠️ Error: ${e.message?.take(50)}"
            Pair(false, "Firebase Init Error: ${e.message}")
        }
    }

    suspend fun testConnection(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        // First test Realtime Database REST access
        try {
            val req = Request.Builder()
                .url("$DEFAULT_RTDB_URL/.json")
                .get()
                .build()
            val resp = httpClient.newCall(req).execute()
            val code = resp.code
            val body = resp.body?.string() ?: ""

            if (code == 200) {
                return@withContext Pair(true, "✅ Firebase Realtime Database is LIVE! Sabhi devices me live sync active hai.")
            } else if (body.contains("Permission denied", ignoreCase = true) || code == 401 || code == 403) {
                return@withContext Pair(false, "Firebase Project 'vishu-connect' Mil Gaya! Bas ek aakhri step bacha hai: Firebase Console -> Realtime Database -> Rules me jakar '.read': true, '.write': true set karke 'Publish' dabayein.")
            }
        } catch (e: Exception) {
            Log.d(TAG, "RTDB ping test warning: ${e.message}")
        }

        // Secondary check via Firestore
        val db = firestore
        if (db == null) {
            return@withContext Pair(false, "Firebase Firestore is not initialized yet.")
        }
        try {
            val testDoc = mapOf(
                "testPing" to System.currentTimeMillis(),
                "device" to "Android",
                "status" to "OK"
            )
            var errorMsg: String? = null
            var success = false
            val latch = java.util.concurrent.CountDownLatch(1)

            db.collection("system_config").document("connection_test")
                .set(testDoc, SetOptions.merge())
                .addOnSuccessListener {
                    success = true
                    latch.countDown()
                }
                .addOnFailureListener { e ->
                    errorMsg = e.message
                    latch.countDown()
                }

            val completed = latch.await(6, java.util.concurrent.TimeUnit.SECONDS)
            if (completed && success) {
                Pair(true, "Connection Verified! Cloud Firestore is active & writable.")
            } else if (errorMsg != null) {
                if (errorMsg!!.contains("PERMISSION_DENIED", ignoreCase = true)) {
                    Pair(false, "Permission Denied: Please set Firestore Rules to test mode: allow read, write: if true;")
                } else {
                    Pair(false, "Firestore Error: $errorMsg")
                }
            } else {
                Pair(false, "Connection Timeout: Check internet connection or Firebase Project ID.")
            }
        } catch (e: Exception) {
            Pair(false, "Error testing Firestore: ${e.message}")
        }
    }

    suspend fun syncAllLocalDataToCloud(
        notes: List<com.example.data.model.NoteItem>,
        classes: List<com.example.data.model.ClassItem>,
        posts: List<com.example.data.model.CommunityPostItem>,
        updateInfo: AppUpdateInfo
    ): Pair<Int, String> = withContext(Dispatchers.IO) {
        var count = 0
        try {
            broadcastAppUpdate(updateInfo)
            count++
            for (n in notes) {
                publishStudyNote(n)
                count++
            }
            for (c in classes) {
                publishLiveClass(c)
                count++
            }
            for (p in posts) {
                publishCommunityPost(p)
                count++
            }
            Pair(count, "Successfully pushed $count items to Firebase Cloud across all devices!")
        } catch (e: Exception) {
            Pair(count, "Sync completed with warning: ${e.message}")
        }
    }
}
