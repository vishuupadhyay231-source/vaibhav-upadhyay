# AI BRAIN - Project Architecture & System Design
**Project:** Vishu Connect (Educational Android Platform for BCA, BTech, CS & Mathematics)
**Package:** `com.example` (Application ID: `com.aistudio.vishuconnect.kzpqa`)

---

## 1. System Architecture Overview

Vishu Connect is structured according to **Clean Architecture** and **MVVM (Model-View-ViewModel)** with Jetpack Compose:

```
┌─────────────────────────────────────────────────────────────┐
│                      Jetpack Compose UI                     │
│  - HomeScreen, NotesScreen, CodeNotesScreen, CoursesScreen  │
│  - ClassesAndMeetScreen, ProjectsAndAppsScreen, AdminScreen │
│  - AiAssistantScreen (Jivan), CalculatorsScreen, Profile    │
└──────────────────────────────┬──────────────────────────────┘
                               │ StateFlows & UI Events
┌──────────────────────────────▼──────────────────────────────┐
│                       MainViewModel                         │
│  - StateFlow management for all UI screens                  │
│  - User Authentication, Role Management & Audit Logging     │
│  - Payment Gateway checkout & unlocked content tracking     │
│  - Rate Limiting, Input Validation & Safe Error Dispatch    │
└──────────────┬──────────────────────────────┬───────────────┘
               │                              │
┌──────────────▼──────────────┐┌──────────────▼───────────────┐
│     Security & Validation   ││       AI Service Layer       │
│  - RateLimiter (Auth/Public)││  - GeminiAiService (Jivan)   │
│  - InputValidator (Schemas) ││  - Fallback Academic Engine  │
│  - FileUploadValidator      │└──────────────────────────────┘
│  - SafeErrorHandler & Logger│
└──────────────┬──────────────┘
               │
┌──────────────▼──────────────────────────────────────────────┐
│                     VishuRepository                         │
│  - Central Single Source of Truth for domain models         │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│                    Room Database (AppDatabase)              │
│  - AppDao: Reactive Flow queries with SQLite persistence    │
│  - Entities: Notes, CodeNotes, Classes, Batches, Courses,   │
│    Projects, Apps, Announcements, Posts, Comments, Users,   │
│    AdminUiSettings, PaymentGatewayConfig, AuditLogs         │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. Directory Structure
```
/
├── AI_BRAIN/                  # Permanent memory, decisions, and system specs
│   ├── ARCHITECTURE.md
│   ├── DECISIONS_AND_MEMORY.md
│   └── SECURITY_AND_POLICIES.md
├── app/
│   ├── src/main/java/com/example/
│   │   ├── MainActivity.kt    # Single activity entry point with edge-to-edge
│   │   ├── ai/
│   │   │   └── GeminiAiService.kt  # Jivan AI tutor with Gemini API integration
│   │   ├── data/
│   │   │   ├── local/         # Room Database, DAOs, Registries, Solvers
│   │   │   ├── model/         # Domain Data Entities & State Models
│   │   │   └── repository/    # VishuRepository
│   │   ├── security/          # Rate Limiting, Input Validation, Upload Safety, Error Sanitization
│   │   │   ├── RateLimiter.kt
│   │   │   ├── InputValidator.kt
│   │   │   ├── SafeErrorHandler.kt
│   │   │   ├── FileUploadValidator.kt
│   │   │   └── SecurityUtils.kt
│   │   └── ui/
│   │       ├── MainViewModel.kt
│   │       ├── components/    # Reusable Compose Widgets
│   │       ├── screens/       # All 12 Screen Composables
│   │       └── theme/         # Material 3 Color, Type, Theme
```
