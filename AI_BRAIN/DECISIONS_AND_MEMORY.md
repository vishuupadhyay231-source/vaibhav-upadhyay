# AI BRAIN - Key Decisions, Features & Memory Log

---

## 1. Core Feature Specifications & User Intent
- **Application Purpose**: Educational hub for BCA, BTech, Computer Science, and Mathematics students with curated notes, code notes, live classes, batches, online calculators, project repositories, downloadable apps, and the Jivan AI mentor.
- **Admin Panel Access**: Secret gesture (multi-tap counter on header branding) + credential authentication.
- **Real Users Only**: Dummy and fake users purged from database; only verified admin (`vishuupadhyay231@gmail.com`) and genuine registered student accounts are stored. Added one-click purge tool in Admin panel.
- **Notes & Projects Monetization**:
  - Most notes and projects are completely **FREE**.
  - Paid notes & projects do not distract users with price tags in the primary feed, displaying clean `FREE` / `PREMIUM` badges.
  - Clicking Download on premium items opens a payment checkout with UPI (GPay/PhonePe/Paytm), QR Code, or Razorpay integration.
- **Dynamic Projects Tab**:
  - Promoted to bottom navigation next to Community.
  - Displays College Projects with live demo links, YouTube video walkthroughs, GitHub links, and ZIP source code downloads.
- **Jivan AI Mentor**:
  - Multi-model Gemini fallback (`gemini-2.0-flash`, `gemini-1.5-flash`, `gemini-2.5-flash`, `gemini-2.0-flash-lite`, `gemini-1.5-pro`, `gemini-3.5-flash`).
  - Real-time intelligent knowledge reasoning engine responding to BCA/BTech CS topics, DSA, OOPs, DBMS, OS, Networks, Math, Programming (C, C++, Java, Python, Kotlin), and Hindi/Hinglish student queries instantly.
- **In-App Auto Update System**:
  - Automatically triggers on app launch if `latestVersionCode > currentVersionCode` or `isForceUpdate` is active.
  - Remote-controlled from Admin Dashboard with custom download URL, release notes, and version code.
  - Direct 1-tap download & web portal fallback.
- **Admin vs Student Post Separation**:
  - Community screen is simplified with a single student "Ask Doubt" action.
  - Official faculty posts, notices, guidelines, and pinned announcements are published directly from the Admin Dashboard ("Community Feed Moderation" tab).

---

## 2. Technical Decisions & Rules
- **No Mock or Dummy Data**: Real Room persistence for all items.
- **Security-First Architecture**:
  - Rate limiting with exponential backoff on sensitive auth and public endpoints.
  - Strict input validation schemas (reject invalid inputs instead of only escaping).
  - All secrets isolated in `BuildConfig` / `.env` (never hardcoded in source).
  - Safe file upload verification with magic byte inspection and sandboxed storage.
  - Sanitized, generic error messages for end users; internal safe logger for debugging.
