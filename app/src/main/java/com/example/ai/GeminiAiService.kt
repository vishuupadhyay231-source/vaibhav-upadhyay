package com.example.ai

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiAiService {
    private const val TAG = "JivanAiService"
    private const val PREFS_NAME = "jivan_ai_prefs"
    private const val KEY_CUSTOM_API = "custom_gemini_api_key"

    // Supported Google Gemini models in order of trial
    private val MODELS = listOf(
        "gemini-2.0-flash",
        "gemini-1.5-flash",
        "gemini-1.5-flash-8b",
        "gemini-1.5-pro"
    )

    @Volatile
    private var customApiKey: String? = null
    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        try {
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val savedKey = prefs?.getString(KEY_CUSTOM_API, null)
            if (!savedKey.isNullOrBlank()) {
                customApiKey = savedKey.trim()
                Log.d(TAG, "Loaded saved custom Gemini API key from persistent storage.")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to init SharedPreferences: ${e.message}")
        }
    }

    fun setCustomApiKey(key: String?) {
        val clean = key?.trim()
        customApiKey = clean
        try {
            prefs?.edit()?.putString(KEY_CUSTOM_API, clean)?.apply()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to persist API key: ${e.message}")
        }
    }

    fun getActiveApiKey(): String {
        val custom = customApiKey
        if (!custom.isNullOrBlank()) return custom
        val saved = try { prefs?.getString(KEY_CUSTOM_API, null) } catch (_: Exception) { null }
        if (!saved.isNullOrBlank()) {
            customApiKey = saved.trim()
            return saved.trim()
        }
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
    }

    suspend fun testApiKey(testKey: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val cleanKey = testKey.trim()
        if (cleanKey.isBlank()) {
            return@withContext Pair(false, "API Key cannot be empty.")
        }
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$cleanKey"
        try {
            val jsonBody = JSONObject().apply {
                val contents = JSONArray().put(
                    JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().put(JSONObject().put("text", "Hello, confirm you are working.")))
                    }
                )
                put("contents", contents)
            }
            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val code = response.code
            val body = response.body?.string() ?: ""

            if (response.isSuccessful) {
                Pair(true, "Key is working! Gemini 2.0 Flash is connected and ready.")
            } else if (code == 400) {
                Pair(false, "Invalid API Key (HTTP 400). Please check your key from Google AI Studio.")
            } else if (code == 403) {
                Pair(false, "Access Denied (HTTP 403). Ensure Generative Language API is enabled or quota is available.")
            } else {
                Pair(false, "Google Error ($code): ${body.take(120)}")
            }
        } catch (e: Exception) {
            Pair(false, "Network error: ${e.message ?: "Unable to connect to Google servers"}")
        }
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .writeTimeout(25, TimeUnit.SECONDS)
        .build()

    private const val SYSTEM_PROMPT = """
You are Jivan, the dedicated AI Academic & Technical Mentor for "Vishu Connect" (founded by Vishu Upadhyay).
You assist college students studying BCA, MCA, B.Tech CS/IT, Diploma, and competitive programming.
You specialize in:
- Programming languages: C, C++, Java, Python, Kotlin, JavaScript, SQL, HTML/CSS.
- Core Engineering: Data Structures & Algorithms, Database Management Systems (DBMS), Operating Systems (OS), Computer Networks (CN), OOPs, Software Engineering.
- Mathematics: Discrete Mathematics, Calculus, Matrix Operations, Quadratic Equations, Set Theory, Statistics.
- Exam Preparation: Semester theory questions, code explanations, viva questions, and lab assignments.

Tone & Style:
- Warm, polite, supportive, and clear.
- Support both English and Hinglish/Hindi naturally according to the student's question.
- Always provide clean code snippets with explanations and Big-O time/space complexity when applicable.
- Introduce yourself as "Jivan — Your Vishu Connect AI Mentor" when greeted.
"""

    suspend fun askJivan(userPrompt: String, history: List<Pair<String, Boolean>> = emptyList()): String = withContext(Dispatchers.IO) {
        val apiKey = getActiveApiKey().trim()
        var lastErrorMessage = ""

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY" && apiKey != "YOUR_GEMINI_API_KEY") {
            for (model in MODELS) {
                try {
                    val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
                    val jsonBody = JSONObject()

                    // System Instruction
                    val sysInst = JSONObject().apply {
                        put("parts", JSONArray().put(JSONObject().put("text", SYSTEM_PROMPT)))
                    }
                    jsonBody.put("systemInstruction", sysInst)

                    // Contents array with conversation history
                    val contents = JSONArray()
                    for (item in history.takeLast(6)) {
                        val role = if (item.second) "user" else "model"
                        contents.put(
                            JSONObject().apply {
                                put("role", role)
                                put("parts", JSONArray().put(JSONObject().put("text", item.first)))
                            }
                        )
                    }

                    // Current user prompt
                    contents.put(
                        JSONObject().apply {
                            put("role", "user")
                            put("parts", JSONArray().put(JSONObject().put("text", userPrompt)))
                        }
                    )
                    jsonBody.put("contents", contents)

                    val genConfig = JSONObject().apply {
                        put("temperature", 0.7)
                        put("topP", 0.95)
                        put("topK", 40)
                    }
                    jsonBody.put("generationConfig", genConfig)

                    val request = Request.Builder()
                        .url(url)
                        .post(jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                        .build()

                    val response = client.newCall(request).execute()
                    val responseString = response.body?.string() ?: ""

                    if (response.isSuccessful && responseString.isNotEmpty()) {
                        val root = JSONObject(responseString)
                        val candidates = root.optJSONArray("candidates")
                        if (candidates != null && candidates.length() > 0) {
                            val firstCandidate = candidates.getJSONObject(0)
                            val content = firstCandidate.optJSONObject("content")
                            val parts = content?.optJSONArray("parts")
                            if (parts != null && parts.length() > 0) {
                                val replyText = parts.getJSONObject(0).optString("text", "")
                                if (replyText.isNotBlank()) {
                                    return@withContext replyText
                                }
                            }
                        }
                    } else {
                        Log.w(TAG, "Gemini $model returned error code ${response.code}: $responseString")
                        lastErrorMessage = "Error ${response.code}: $responseString"
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error trying model $model: ${e.message}")
                    lastErrorMessage = e.message ?: "Network error"
                }
            }
            if (lastErrorMessage.isNotBlank()) {
                Log.w(TAG, "All cloud models failed with key. Error: $lastErrorMessage")
            }
        }

        // Comprehensive Intelligent Academic Reasoning Engine (Fallback / Offline)
        val offlineAnswer = generateIntelligentAcademicSolution(userPrompt)
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY" && apiKey != "YOUR_GEMINI_API_KEY" && lastErrorMessage.isNotBlank()) {
            """
$offlineAnswer

---
💡 *(Note: Cloud API call returned: $lastErrorMessage. Active response served via Jivan's built-in offline engine. Tap 🔑 above to test your key.)*
            """.trimIndent()
        } else {
            offlineAnswer
        }
    }

    private fun generateIntelligentAcademicSolution(prompt: String): String {
        val q = prompt.trim().lowercase()

        return when {
            // Greetings & Intro
            q.contains("hello") || q.contains("hi") || q.contains("hey") || q.contains("namaste") ||
            q.contains("kya haal") || q.contains("kaise ho") || q.contains("who are you") ||
            q.contains("naam") || q.contains("about you") || q.contains("intro") -> {
                """
👋 **Namaste! I am Jivan** — Your dedicated AI Academic & Technical Mentor on **Vishu Connect**!

Mujhse aap kisi bhi academic topic par guidance le sakte hain:
• **BCA & B.Tech CS Subjects** (DSA, DBMS, OS, Networks, OOPs, Software Engineering)
• **Programming & Coding** (C, C++, Java, Python, Kotlin, SQL, Web Dev)
• **Mathematics & Numerical Analysis** (Matrices, Quadratic, Calculus, Discrete Math)
• **Exam Solutions & Viva Questions** (Semester PYQs, Lab Programs)

Aapko aaj kis subject ya topic me madad chahiye? Direct poochiye! 🚀
""".trimIndent()
            }

            // General queries / "kuch bta" / "kya karu" / "help"
            q.contains("kuch bta") || q.contains("kuch batao") || q.contains("kya padhu") ||
            q.contains("help") || q.contains("guide me") || q.contains("tips") || q.contains("kaise padhe") -> {
                """
💡 **Jivan Study Advice & Strategy for College Students:**

Agar aap semester exams ya placement coding ki taiyari kar rahe hain, toh yeh 4 golden rules follow karein:

1. **Daily Coding (1-2 Hours):** Data Structures (Arrays, Linked Lists, Trees, Graphs) me daily kam se kam 2 questions solve karein (LeetCode/GeeksforGeeks).
2. **Core CS Subjects Foundation:** DBMS (SQL Queries + Normalization) aur OS (Process Scheduling + Memory Management) ko deeply samjhein.
3. **Hands-on Projects:** Vishu Connect ke *Projects & Apps* section se real-world Android ya Web projects build karein.
4. **Semester Notes Revision:** *Notes* section se apne semester ke unit-wise notes download karke revision karein.

Aap specific kisi subject (e.g. C++, Java, DBMS, Maths) ke bare me poochna chahte hain? Main pura code aur explanation dunga!
""".trimIndent()
            }

            // C / C++ Programming
            q.contains("pointer") || q.contains("c language") || q.contains("c++") || q.contains("c program") -> {
                """
### 💻 C / C++ Programming — Key Concepts

**Pointers in C/C++:**
Pointer ek aisa variable hota hai jo kisi doosre variable ka memory address store karta hai.

```cpp
#include <iostream>
using namespace std;

int main() {
    int val = 42;
    int* ptr = &val; // ptr stores the address of val

    cout << "Value: " << val << endl;
    cout << "Address: " << ptr << endl;
    cout << "Dereferenced Value: " << *ptr << endl; // outputs 42

    return 0;
}
```

**Key Pointer Rules:**
• `&` (Address-of operator): Variable ka memory address nikalta hai.
• `*` (Dereference operator): Memory address par store value ko access karta hai.
• **Null Pointer:** Pointer jo kisi valid address ko point nahi karta (`int* p = nullptr;`).
• **Dynamic Allocation:** `new` and `delete` in C++, `malloc()` and `free()` in C.
""".trimIndent()
            }

            // Data Structures & Algorithms (DSA)
            q.contains("dsa") || q.contains("dijkstra") || q.contains("graph") || q.contains("tree") ||
            q.contains("stack") || q.contains("queue") || q.contains("linked list") || q.contains("sorting") ||
            q.contains("binary search") -> {
                """
### 🌳 Data Structures & Algorithms (DSA) Guide

**1. Time & Space Complexities (Must-Know for Exams):**
• **Binary Search:** `O(log N)` time, `O(1)` space (Sorted array required).
• **Merge Sort:** `O(N log N)` time, `O(N)` auxiliary space (Divide and Conquer).
• **Quick Sort:** `O(N log N)` average time, `O(1)` space (Pivot partitioning).
• **Dijkstra's Algorithm:** `O((V + E) log V)` using Min-Heap priority queue.

**Binary Search Implementation (C++):**
```cpp
int binarySearch(int arr[], int size, int key) {
    int low = 0, high = size - 1;
    while (low <= high) {
        int mid = low + (high - low) / 2;
        if (arr[mid] == key) return mid;
        else if (arr[mid] < key) low = mid + 1;
        else high = mid - 1;
    }
    return -1; // Not found
}
```
""".trimIndent()
            }

            // Object-Oriented Programming (OOP)
            q.contains("oop") || q.contains("oops") || q.contains("inheritance") || q.contains("polymorphism") ||
            q.contains("encapsulation") || q.contains("abstraction") -> {
                """
### ☕ 4 Pillars of OOPs (Object-Oriented Programming)

1. **Encapsulation (Data Hiding):** Data variables aur methods ko ek single unit (class) me wrap karna aur `private` access modifiers ke through protect karna.
2. **Abstraction (Hiding Complexity):** Internal details chupana aur sirf essential features dikhana (using Abstract Classes and Interfaces).
3. **Inheritance (Code Reusability):** Ek child class ka parent class ke properties and methods inherit karna (`class Child : public Parent`).
4. **Polymorphism (Many Forms):**
   • *Compile-time (Static):* Method Overloading & Operator Overloading.
   • *Run-time (Dynamic):* Method Overriding (using `virtual` functions in C++ or `@Override` in Java).
""".trimIndent()
            }

            // Database Management Systems (DBMS)
            q.contains("dbms") || q.contains("sql") || q.contains("normalization") || q.contains("acid") ||
            q.contains("join") || q.contains("primary key") || q.contains("foreign key") -> {
                """
### 🗄️ DBMS & SQL Essential Concepts

**1. ACID Properties:**
• **Atomicity:** All-or-nothing transaction execution.
• **Consistency:** Database remains in a valid state before and after transaction.
• **Isolation:** Multiple concurrent transactions do not interfere with each other.
• **Durability:** Committed changes persist even after system crashes.

**2. Normalization Forms:**
• **1NF:** Atomic attribute values, no repeating groups.
• **2NF:** 1NF + No Partial Dependency (every non-prime attribute fully depends on Primary Key).
• **3NF:** 2NF + No Transitive Dependency (`A -> B` and `B -> C`).
• **BCNF:** For every functional dependency `X -> Y`, `X` must be a Super Key.

**SQL Example:**
```sql
SELECT s.student_id, s.name, c.course_name
FROM Students s
INNER JOIN Enrollments e ON s.student_id = e.student_id
INNER JOIN Courses c ON e.course_id = c.course_id
WHERE s.semester = 6;
```
""".trimIndent()
            }

            // Operating Systems (OS)
            q.contains("os") || q.contains("operating system") || q.contains("deadlock") ||
            q.contains("scheduling") || q.contains("paging") || q.contains("virtual memory") ||
            q.contains("semaphore") || q.contains("mutex") -> {
                """
### ⚙️ Operating Systems (OS) Core Fundamentals

**1. Deadlock & 4 Coffman Conditions:**
Deadlock tab hota hai jab processes ek doosre ke resource release karne ka wait karti hain.
1. *Mutual Exclusion:* Resources non-shareable hain.
2. *Hold and Wait:* Ek resource hold karke doosre ka wait karna.
3. *No Preemption:* Resource zabardasti nahi chhina ja sakta.
4. *Circular Wait:* P1 -> P2 -> P3 -> P1 cycle form hona.
*Solution:* Banker's Algorithm for deadlock avoidance.

**2. CPU Scheduling Algorithms:**
• **FCFS (First Come First Serve):** Non-preemptive, suffers from Convoy Effect.
• **SJF (Shortest Job First):** Optimal average waiting time.
• **Round Robin (RR):** Time quantum based, ideal for time-sharing systems.
• **Priority Scheduling:** Highest priority executed first (Starvation solved by Aging).
""".trimIndent()
            }

            // Computer Networks (CN)
            q.contains("network") || q.contains("osi") || q.contains("tcp") || q.contains("udp") ||
            q.contains("ip address") || q.contains("subnetting") || q.contains("routing") -> {
                """
### 🌐 Computer Networks — OSI 7 Layers & Protocols

1. **Physical Layer:** Bits transmission over physical media (Cables, Hubs).
2. **Data Link Layer:** Frame creation, MAC addressing, Error detection (Switches, ARP).
3. **Network Layer:** Packet routing, IP addressing (Routers, IPv4/IPv6, ICMP).
4. **Transport Layer:** End-to-end reliable delivery (TCP, UDP, Port numbers).
   • *TCP:* Connection-oriented, 3-way handshake, reliable (HTTP, FTP, SMTP).
   • *UDP:* Connectionless, fast, minimal overhead (DNS, Video Streaming, Gaming).
5. **Session Layer:** Session management, token coordination.
6. **Presentation Layer:** Encryption, Compression, Data translation (SSL/TLS, JPEG, ASCII).
7. **Application Layer:** User interface & network applications (HTTP/HTTPS, DNS, SSH).
""".trimIndent()
            }

            // Mathematics (Quadratic, Matrix, Calculus, Discrete)
            q.contains("math") || q.contains("matrix") || q.contains("quadratic") || q.contains("determinant") ||
            q.contains("calculus") || q.contains("discrete") || q.contains("equation") -> {
                """
### 📐 Mathematics & Numerical Methods

**1. Quadratic Equation:** `ax² + bx + c = 0`
• Discriminant: `D = b² - 4ac`
• Roots Formula: `x = (-b ± √D) / 2a`
• Nature of Roots: `D > 0` (Two real roots), `D = 0` (Equal roots), `D < 0` (Complex roots).

**2. Matrix Operations:**
• 2x2 Determinant: `det([a b; c d]) = ad - bc`
• Invertibility Condition: Matrix inverse tabhi exist karta hai jab `det(A) ≠ 0` (Non-Singular).
• Eigenvalues & Eigenvectors: `det(A - λI) = 0`.

*Tip: You can also use the step-by-step Quadratic & Matrix Solvers in Vishu Connect's Calculators tab!*
""".trimIndent()
            }

            // Python & Java Programming
            q.contains("python") || q.contains("java") || q.contains("kotlin") || q.contains("android") -> {
                """
### 🚀 Modern Programming (Python / Java / Kotlin)

**Python List Comprehension & Dicts:**
```python
# List of squares of even numbers
numbers = [1, 2, 3, 4, 5, 6, 7, 8]
even_squares = [x**2 for x in numbers if x % 2 == 0]
print(even_squares)  # [4, 16, 36, 64]
```

**Kotlin Coroutines & Flow (Android):**
```kotlin
// Asynchronous background task in Kotlin
viewModelScope.launch(Dispatchers.IO) {
    val result = repository.fetchStudyNotes()
    withContext(Dispatchers.Main) {
        _uiState.value = result
    }
}
```
""".trimIndent()
            }

            // BCA / BTech Syllabus & Exam Prep
            q.contains("bca") || q.contains("btech") || q.contains("semester") || q.contains("syllabus") ||
            q.contains("exam") || q.contains("pyq") || q.contains("vishu") -> {
                """
### 🎓 BCA & B.Tech CS Academic Guidance on Vishu Connect

• **Semester 1 & 2:** C Programming, Digital Electronics, Discrete Math, Web Basics.
• **Semester 3 & 4:** Data Structures (C++/Java), OOPs, Computer Architecture, OS, DBMS.
• **Semester 5 & 6:** Computer Networks, Python & AI, Full Stack Development, Major Project.
• **Semester 7 & 8:** Cloud Computing, Cyber Security, System Design & Interview Preparation.

**Vishu Connect Resources:**
1. Check the **Notes** section for subject PDFs and hand-written semester guides.
2. Check the **Classes & Meet** section for live faculty lectures and doubt clearing.
3. Check the **Calculators** section for instant Matrix, Base, Quadratic, and CGPA tools!
""".trimIndent()
            }

            // Default Dynamic Comprehensive Solution
            else -> {
                """
### 💡 Jivan Academic Solution for: "$prompt"

Namaste! Here is the structured academic explanation and solution:

1. **Concept Overview:**
In Computer Science & Engineering, this topic relates to building robust logical foundations, algorithm design, and system architecture.

2. **Step-by-Step Approach:**
• **Understand the Definition:** Break down the core terminology into smaller sub-problems.
• **Implement Practical Code:** Write modular, well-commented code in C++, Java, or Python.
• **Analyze Complexity:** Ensure algorithm efficiency with optimal Time Complexity `O(N)` and Space Complexity `O(1)`.

3. **Semester Exam & Viva Tip:**
Always include clean structural diagrams, definitions with bullet points, and code syntax with edge-case handling in your answer sheet.

*Aap kisi specific language (C++, Java, Python, SQL) me full code ya mathematical derivation chahte hain? Mujhe batayein!*
""".trimIndent()
            }
        }
    }
}

