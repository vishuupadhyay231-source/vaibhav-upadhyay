package com.example.security

import android.util.Log

/**
 * Centralized safe error handling and information leakage prevention.
 * Prevents stack traces, raw SQL / SQLite errors, internal server paths,
 * or credentials from being shown to users.
 */
object SafeErrorHandler {

    private const val TAG = "VishuSafeLogger"

    /**
     * Map any thrown exception to a generic, friendly user-facing message.
     */
    fun getUserFriendlyMessage(throwable: Throwable?): String {
        if (throwable == null) return "An unexpected issue occurred. Please try again."

        val message = throwable.message ?: ""
        val className = throwable.javaClass.simpleName

        // Redact any database or SQLite internal errors
        if (className.contains("SQLite", ignoreCase = true) ||
            className.contains("Database", ignoreCase = true) ||
            message.contains("SQL", ignoreCase = true) ||
            message.contains("table", ignoreCase = true) ||
            message.contains("column", ignoreCase = true)
        ) {
            return "Unable to save or load data at this moment. Please try again."
        }

        // Network related
        if (className.contains("SocketTimeout", ignoreCase = true) ||
            className.contains("UnknownHost", ignoreCase = true) ||
            className.contains("ConnectException", ignoreCase = true) ||
            message.contains("network", ignoreCase = true)
        ) {
            return "Network connection unavailable. Please check your internet connection."
        }

        // Security / Rate Limit
        if (message.contains("Rate limit", ignoreCase = true) || message.contains("Too many", ignoreCase = true)) {
            return message
        }

        // Storage / IO
        if (className.contains("FileNotFound", ignoreCase = true) ||
            className.contains("IOException", ignoreCase = true) ||
            message.contains("storage", ignoreCase = true)
        ) {
            return "Unable to process file on device. Please ensure sufficient storage is available."
        }

        // Generic fallback for any other error
        return "An unexpected error occurred. Please try again shortly."
    }

    /**
     * Safe internal logger that masks sensitive strings (passwords, tokens, keys)
     * and logs to Android Logcat without exposing info in user UI.
     */
    fun logError(category: String, message: String, throwable: Throwable? = null) {
        val sanitizedMsg = sanitizeMessage(message)
        if (throwable != null) {
            Log.e(TAG, "[$category] $sanitizedMsg", throwable)
        } else {
            Log.e(TAG, "[$category] $sanitizedMsg")
        }
    }

    fun logInfo(category: String, message: String) {
        Log.i(TAG, "[$category] ${sanitizeMessage(message)}")
    }

    private fun sanitizeMessage(msg: String): String {
        return msg
            .replace(Regex("password=[^&\\s,]+", RegexOption.IGNORE_CASE), "password=[REDACTED]")
            .replace(Regex("pass=[^&\\s,]+", RegexOption.IGNORE_CASE), "pass=[REDACTED]")
            .replace(Regex("key=[^&\\s,]+", RegexOption.IGNORE_CASE), "key=[REDACTED]")
            .replace(Regex("secret=[^&\\s,]+", RegexOption.IGNORE_CASE), "secret=[REDACTED]")
            .replace(Regex("token=[^&\\s,]+", RegexOption.IGNORE_CASE), "token=[REDACTED]")
            .replace(Regex("rzp_(?:live|test)_[a-zA-Z0-9]+"), "rzp_***[REDACTED]")
    }
}
