package com.example.security

import java.util.concurrent.ConcurrentHashMap
import kotlin.math.min
import kotlin.math.pow

/**
 * Tiered, configurable Rate Limiter supporting:
 * 1. Authentication Routes (Stricter, per-account & per-IP/device, exponential backoff)
 * 2. Public Endpoints (Moderate limit, sliding window)
 * 3. Authenticated User Actions (Looser limit, sliding window)
 * 4. File Uploads (Strict limits)
 */
object RateLimiter {

    // Configurable Thresholds
    data class RateLimitConfig(
        val maxAuthFailuresBeforeBackoff: Int = 3,
        val authBaseBackoffSeconds: Long = 2L,
        val authMaxBackoffSeconds: Long = 60L,
        val authFailureWindowMs: Long = 5 * 60 * 1000L, // 5 minutes
        val publicMaxRequestsPerMinute: Int = 30,
        val authenticatedMaxActionsPerMinute: Int = 60,
        val maxUploadsPerTenMinutes: Int = 5
    )

    var config: RateLimitConfig = RateLimitConfig()

    // Auth Attempt Tracking: Key -> List of failed timestamp millis
    private val authFailures = ConcurrentHashMap<String, MutableList<Long>>()

    // Sliding Window Tracking: Key -> List of action timestamp millis
    private val actionTimestamps = ConcurrentHashMap<String, MutableList<Long>>()

    sealed class RateLimitResult {
        object Allowed : RateLimitResult()
        data class BackoffRequired(val waitSeconds: Long, val message: String) : RateLimitResult()
        data class TooManyRequests(val waitSeconds: Long, val message: String) : RateLimitResult()
    }

    /**
     * Checks if an authentication attempt is allowed for a given identifier (email or IP/device key).
     */
    fun checkAuthAllowed(identifier: String): RateLimitResult {
        val key = "auth:${identifier.lowercase().trim()}"
        val now = System.currentTimeMillis()
        val failures = authFailures[key] ?: return RateLimitResult.Allowed

        synchronized(failures) {
            // Remove failures older than the failure window
            failures.removeAll { now - it > config.authFailureWindowMs }
            val failureCount = failures.size

            if (failureCount >= config.maxAuthFailuresBeforeBackoff) {
                val excess = failureCount - config.maxAuthFailuresBeforeBackoff + 1
                val backoffSeconds = min(
                    config.authMaxBackoffSeconds,
                    (config.authBaseBackoffSeconds.toDouble().pow(excess.toDouble())).toLong()
                )
                val lastFailure = failures.lastOrNull() ?: now
                val elapsedSeconds = (now - lastFailure) / 1000
                val remainingWait = backoffSeconds - elapsedSeconds

                if (remainingWait > 0) {
                    return RateLimitResult.BackoffRequired(
                        waitSeconds = remainingWait,
                        message = "Too many failed attempts. Please wait $remainingWait seconds before trying again."
                    )
                }
            }
        }
        return RateLimitResult.Allowed
    }

    /**
     * Record a failed authentication attempt to trigger exponential backoff.
     */
    fun recordAuthFailure(identifier: String) {
        val key = "auth:${identifier.lowercase().trim()}"
        val now = System.currentTimeMillis()
        val list = authFailures.computeIfAbsent(key) { mutableListOf() }
        synchronized(list) {
            list.removeAll { now - it > config.authFailureWindowMs }
            list.add(now)
        }
    }

    /**
     * Reset auth failure record upon successful authentication.
     */
    fun recordAuthSuccess(identifier: String) {
        val key = "auth:${identifier.lowercase().trim()}"
        authFailures.remove(key)
    }

    /**
     * Rate limiter for Public endpoints (e.g. AI queries, search, solvers).
     */
    fun checkPublicRequestAllowed(endpoint: String, clientId: String = "client"): RateLimitResult {
        val key = "pub:$endpoint:$clientId"
        return checkSlidingWindow(
            key = key,
            maxRequests = config.publicMaxRequestsPerMinute,
            windowMs = 60 * 1000L,
            limitName = "requests"
        )
    }

    /**
     * Rate limiter for Authenticated user actions (e.g. posting, comments, likes).
     */
    fun checkUserActionAllowed(action: String, userId: String): RateLimitResult {
        val key = "user:$action:$userId"
        return checkSlidingWindow(
            key = key,
            maxRequests = config.authenticatedMaxActionsPerMinute,
            windowMs = 60 * 1000L,
            limitName = "actions"
        )
    }

    /**
     * Rate limiter for File Uploads.
     */
    fun checkUploadAllowed(userId: String): RateLimitResult {
        val key = "upload:$userId"
        return checkSlidingWindow(
            key = key,
            maxRequests = config.maxUploadsPerTenMinutes,
            windowMs = 10 * 60 * 1000L,
            limitName = "uploads"
        )
    }

    private fun checkSlidingWindow(
        key: String,
        maxRequests: Int,
        windowMs: Long,
        limitName: String
    ): RateLimitResult {
        val now = System.currentTimeMillis()
        val list = actionTimestamps.computeIfAbsent(key) { mutableListOf() }

        synchronized(list) {
            list.removeAll { now - it > windowMs }
            if (list.size >= maxRequests) {
                val oldest = list.firstOrNull() ?: now
                val waitSeconds = maxOf(1L, (windowMs - (now - oldest)) / 1000)
                return RateLimitResult.TooManyRequests(
                    waitSeconds = waitSeconds,
                    message = "Rate limit reached ($maxRequests $limitName per window). Please wait $waitSeconds seconds."
                )
            }
            list.add(now)
        }
        return RateLimitResult.Allowed
    }

    fun clearAll() {
        authFailures.clear()
        actionTimestamps.clear()
    }
}
