# PROJECT BRAIN & AUDIT LOG

## Current System Health & Audit Status
- **Target Scale**: 10,000+ Concurrent Active Students & Faculty
- **Target Stability**: 99.99% Crash-free rate, Zero Main-Thread Lag, 60 FPS Compose rendering
- **Database**: Local SQLite Room + Google Firebase Realtime Database (REST Dual-Engine)

---

## 1. Bugs, Root Causes & Fixes Record

### Issue 1: Firestore Permission Denied Logs
- **Error/Bug**: `PERMISSION_DENIED: Missing or insufficient permissions` on `study_notes`, `live_classes`, `app_update`, `community_posts`.
- **Root Cause**: Cloud Firestore security rules were locked while Realtime Database (RTDB) was active with open read/write rules.
- **Fix Done**: Added graceful failover in `FirebaseSyncManager` to automatically detach failing Firestore snapshot listeners on PERMISSION_DENIED and promote Realtime Database as the primary cloud sync engine.
- **Changed Files**: `FirebaseSyncManager.kt`

### Issue 2: 10,000 Users Scale - Thunderous Herd & High Traffic Polling
- **Risk**: Fixed 20s polling with `OkHttpClient` across 10,000 active devices creates 500 req/sec spikes on Firebase and battery drain/lag on low-end client devices.
- **Root Cause**: Fixed `delay(20000L)` without jitter, adaptive interval, or ETag/checksum caching; unnecessary JSON re-parsing when data has not changed.
- **Fix Done**: 
  - Implemented adaptive polling (40s-60s with random jitter ±5s to eliminate synchronized spikes).
  - Added hash comparison on raw RTDB payload before JSON deserialization so unchanged data is not re-parsed.
  - Replaced ad-hoc `CoroutineScope(Dispatchers.IO)` spawns with a single managed, lifecycle-safe `SupervisorJob` CoroutineScope to prevent thread exhaustion.
- **Changed Files**: `FirebaseSyncManager.kt`

### Issue 3: Compose List Recomposition & Scroll Lag with Large Datasets
- **Risk**: When 1,000+ notes or posts are loaded, scrolling without explicit `key` parameters forces Compose to recreate item states and sub-compositions, creating micro-stutter.
- **Fix Done**: Verified and enforced unique key functions `key = { it.id }` across all `LazyColumn` and `LazyRow` items in `NotesScreen`, `CommunityPostsScreen`, `ClassesAndMeetScreen`, and `HomeScreen`.
- **Changed Files**: `NotesScreen.kt`, `CommunityPostsScreen.kt`, `ClassesAndMeetScreen.kt`

### Issue 4: Input Validation & Anti-Crash Sanitization
- **Risk**: Malformed user input, excessively long strings (payload attacks), or invalid URLs could cause UI clipping, JSON parser crashes, or memory spikes.
- **Fix Done**: Added strict schema validator enforcing title lengths (3-120 chars), content boundaries, valid URL regex for Meet/Download links, and rate limiting (max 1 post per 30s per user).
- **Changed Files**: `InputValidator.kt`, `MainViewModel.kt`

### Issue 5: Room Database Indexing for Fast Querying
- **Fix Done**: Added indices on frequently queried foreign columns (`course`, `semester`, `subject`) to ensure sub-millisecond query execution on device SQLite for 10,000 items.
- **Changed Files**: `Models.kt`, `AppDatabase.kt`

### Issue 6: Main Thread Frame Drops & Firestore GMS Broker Conflicts
- **Error/Bug**: `Choreographer: Skipped 62 frames!` and `SecurityException: Unknown calling package com.google.android.gms` on app launch.
- **Root Cause**: 
  1. `MainViewModel` was collecting remote Flow emissions and running single-item database insert loops on `Dispatchers.Main`.
  2. Automatic Firestore snapshot listeners were trying to register with Google Play Services broker when Cloud Firestore rules were disabled.
- **Fix Done**:
  1. Updated all remote data collectors in `MainViewModel` to run strictly on `Dispatchers.IO` with batch methods (`insertNotes`, `insertClasses`, `insertCommunityPosts`).
  2. Made Firebase Realtime Database (RTDB) the zero-overhead primary sync engine on launch, eliminating unwanted GMS service calls and frame skips.
- **Changed Files**: `MainViewModel.kt`, `VishuRepository.kt`, `FirebaseSyncManager.kt`

### Issue 7: HWUI Davey Lag (1406ms) from Synchronous Large JPEG Resource Decoding
- **Error/Bug**: `HWUI: Davey! duration=1406ms;` and `HWUI: Image decoding logging dropped!`.
- **Root Cause**: Top-level UI components (`AppHeader`, `HomeScreen` banner, `AboutVishuScreen`) used `painterResource(id = R.drawable...)` with large 500KB-800KB raw JPEGs, forcing Android to decode high-resolution bitmaps directly on the UI draw thread.
- **Fix Done**: Replaced synchronous `painterResource` with asynchronous, cached, downsampled Coil `AsyncImage` across all headers and screen banners to achieve buttery 60fps rendering.
- **Changed Files**: `CommonComponents.kt`, `HomeScreen.kt`, `AboutVishuScreen.kt`

### Issue 8: Root Recomposition Cascade from Global Flow Subscriptions
- **Error/Bug**: Multiple UI invalidations and frame drops on startup.
- **Root Cause**: `MainActivity` collected all 15+ sub-screen flows at the root `setContent` composable, re-traversing the entire Activity when any database table finished loading.
- **Fix Done**: Scoped Flow collections strictly to the active screen branch inside `MainActivity`.
- **Changed Files**: `MainActivity.kt`

### Creative Enhancement: Custom Animated Live Meet Logo & Join Button
- **User Request**: Make the Live Google Meet Join logo and button highly creative, attractive, and polished.
- **Implementation**: Designed `CreativeLiveMeetLogo` with multi-layered glowing gradient squircle, animated pulsing radar broadcast waves, blinking REC indicator, and a sleek glassmorphism `CreativeLiveJoinButton`.
- **Changed Files**: `CreativeLiveMeetComponents.kt`, `ClassesAndMeetScreen.kt`, `HomeScreen.kt`

---

## 2. Important Decisions (Things NOT to Change)
- **Primary Cloud Backend**: Firebase Realtime Database (`https://vishu-connect-default-rtdb.firebaseio.com/`) with project `vishu-connect` (ID: 660441284167).
- **Dual-Engine Graceful Fallback**: App remains 100% functional offline via Room SQLite if network drops, syncing instantly when connection is restored.
- **Clean Architecture**: No mock data; real entities with clean MVVM state observation.
- **Zero Display Clutter**: APK and system configurations reside cleanly in project files without exposing technical raw links on user screens.

---

## 3. Pending & Verified Tasks
- [x] Rate Limiting & Polling Optimization for 10k users
- [x] Input Validation & Schema Rejection on user creation, notes, and posts
- [x] Room indexing & Lazy list key optimization for smooth 60fps scrolling
- [x] Creative Live Meet Logo & Join Button integrated with animated pulse
- [x] APK generated and copied directly into project folders (`/VishuConnect.apk`, `/AI_BRAIN/VishuConnect.apk`, `/apk/VishuConnect.apk`, `/app/VishuConnect.apk`)
- [x] Build and compilation verification
