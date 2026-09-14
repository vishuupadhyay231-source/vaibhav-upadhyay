package com.example.security

import java.security.MessageDigest
import java.security.SecureRandom

object SecurityUtils {

    /**
     * Constant-time string equality comparison to prevent timing attacks.
     */
    fun constantTimeEquals(a: String, b: String): Boolean {
        return MessageDigest.isEqual(a.toByteArray(Charsets.UTF_8), b.toByteArray(Charsets.UTF_8))
    }

    /**
     * Compute SHA-256 hash of a string with optional salt.
     */
    fun sha256(input: String, salt: String = "vishu_connect_secure_salt_2026"): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest((input + salt).toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Generate secure random token string for session or transaction verification.
     */
    fun generateSecureToken(byteLength: Int = 16): String {
        val random = SecureRandom()
        val bytes = ByteArray(byteLength)
        random.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
