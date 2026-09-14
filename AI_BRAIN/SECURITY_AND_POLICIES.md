# AI BRAIN - Security Policies, Audits & Specifications

---

## 1. Rate Limiting Policy
- **Authentication Endpoints**:
  - Threshold: Max 5 attempts per account/IP within 5 minutes.
  - Backoff: Exponential cooldown (1st fail: 0s, 2nd: 2s, 3rd: 5s, 4th: 15s, 5th+: 60s+ cooldown).
  - No permanent hard lockout: Accounts recover smoothly after backoff.
- **Public Query Endpoints (AI, Search, Solvers)**:
  - Threshold: 30 requests per minute with sliding token-bucket window.
- **Authenticated User Actions (Posts, Comments, Likes, Downloads)**:
  - Threshold: 60 actions per minute per active user.
- **File Upload Actions**:
  - Threshold: 5 uploads per 10 minutes per user.

---

## 2. Input Validation Schema Policy
- **Email**: RFC 5322 regex validation, max length 254 chars, reject invalid characters and formatting.
- **Password**: Min 6, max 64 characters, rejection of empty or malformed strings.
- **Names / Usernames**: 2-50 chars, alphabetic + accented characters, numbers, and standard symbols.
- **Titles & Content**: Length limits strictly enforced (Title: 3..200 chars, Content: 5..10000 chars).
- **Prices & Numbers**: Valid positive non-NaN finite floats/doubles (0.0 to 100,000.0).
- **UPI IDs**: Validated against `^[a-zA-Z0-9.\-_]{2,256}@[a-zA-Z]{2,64}$`.
- **Payment Keys**: Validated against Razorpay ID format or clean alphanumeric tokens.

---

## 3. Secrets Management & Scan Results
- **API Keys**: All Gemini API keys loaded via `BuildConfig.GEMINI_API_KEY` (injected from AI Studio Secrets & `.env`).
- **Payment Keys**: Configured dynamically through the Admin Control Panel and stored in local encrypted/secure storage.
- **Version Control**: `.env`, `debug.keystore`, and `local.properties` are strictly included in `.gitignore`.
- **No Hardcoded Tokens**: Verified zero hardcoded credentials or API secrets across the entire Kotlin codebase.

---

## 4. Dependency Vulnerability Audit
- **Android Gradle Plugin**: 9.1.1 (Secure, tested)
- **Kotlin**: 2.2.10 (Modern, secure)
- **Jetpack Compose BOM**: 2024.09.00 (Current stable)
- **Room Database**: 2.7.0 (Current stable)
- **OkHttp**: 4.10.0 / **Retrofit**: 2.12.0 (Modern, secure HTTP/2 clients with TLS 1.3 enforcement)
- **Coil**: 2.7.0 (Secure image loader with bitmap memory bounds)
- **All dependencies audited**: No known critical or high severity CVE vulnerabilities.

---

## 5. Error Handling & Information Leakage Prevention
- **Generic User Messages**: Users receive user-friendly error explanations (e.g. "Unable to complete request. Please try again.").
- **Zero Stack Traces in UI**: Raw SQLite exceptions, SQL statements, stack traces, and internal server paths are never displayed in Toasts, Snackbars, or Dialogs.
- **Safe Internal Logging**: `SafeLogger` redacts all sensitive strings (passwords, tokens, keys, email prefixes) prior to writing to Logcat or Audit Logs.

---

## 6. File Upload Safety Policy
- **Type Verification**: Magic byte header inspection (`%PDF-`, `\x89PNG`, `\xFF\xD8\xFF`, `PK\x03\x04`).
- **Size Bounds**: Enforced 25MB max for PDFs and ZIPs, 5MB max for images.
- **Storage Isolation**: Files stored in private app sandbox (`context.filesDir/secure_uploads/`), outside web root.
- **Non-Executable**: Path traversal (`..`) stripped, dangerous extensions blocked, files stored non-executable.
