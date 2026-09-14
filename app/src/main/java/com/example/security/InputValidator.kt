package com.example.security

import java.net.URI
import java.util.regex.Pattern

/**
 * Strict Input Validation Schemas.
 * Every input is strictly validated against schemas (type, length, format)
 * and rejected if invalid — not just sanitized or escaped.
 */
object InputValidator {

    sealed class ValidationResult {
        object Valid : ValidationResult()
        data class Invalid(val error: String) : ValidationResult()

        val isValid: Boolean get() = this is Valid
    }

    // RFC 5322 Compliant Email Pattern
    private val EMAIL_REGEX = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,15}$"
    )

    // UPI Virtual Payment Address (VPA) Pattern
    private val UPI_REGEX = Pattern.compile(
        "^[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64}$"
    )

    // Razorpay Key Schema: rzp_test_... or rzp_live_...
    private val RAZORPAY_KEY_REGEX = Pattern.compile(
        "^rzp_(?:live|test)_[a-zA-Z0-9]{10,40}$"
    )

    // Safe Username Pattern: letters, digits, underscores, dashes, dots (2..50 chars)
    private val USERNAME_REGEX = Pattern.compile("^[a-zA-Z0-9_.-]{2,50}$")

    /**
     * Strict Email Validation
     */
    fun validateEmail(email: String?): ValidationResult {
        if (email.isNullOrBlank()) {
            return ValidationResult.Invalid("Email address cannot be empty.")
        }
        val trimmed = email.trim()
        if (trimmed.length > 254) {
            return ValidationResult.Invalid("Email exceeds maximum allowable length of 254 characters.")
        }
        if (!EMAIL_REGEX.matcher(trimmed).matches()) {
            return ValidationResult.Invalid("Please provide a valid email address format (e.g. user@example.com).")
        }
        return ValidationResult.Valid
    }

    /**
     * Strict Password Validation
     */
    fun validatePassword(password: String?, minLength: Int = 6, maxLength: Int = 64): ValidationResult {
        if (password.isNullOrBlank()) {
            return ValidationResult.Invalid("Password cannot be empty.")
        }
        if (password.length < minLength) {
            return ValidationResult.Invalid("Password must be at least $minLength characters long.")
        }
        if (password.length > maxLength) {
            return ValidationResult.Invalid("Password cannot exceed $maxLength characters.")
        }
        return ValidationResult.Valid
    }

    /**
     * Strict Name Validation
     */
    fun validateName(name: String?, fieldName: String = "Name", minLen: Int = 2, maxLen: Int = 80): ValidationResult {
        if (name.isNullOrBlank()) {
            return ValidationResult.Invalid("$fieldName cannot be empty.")
        }
        val trimmed = name.trim()
        if (trimmed.length < minLen || trimmed.length > maxLen) {
            return ValidationResult.Invalid("$fieldName must be between $minLen and $maxLen characters.")
        }
        // Disallow dangerous control chars and script tags
        if (trimmed.contains("<") || trimmed.contains(">") || trimmed.contains("\u0000")) {
            return ValidationResult.Invalid("$fieldName contains invalid or illegal characters.")
        }
        return ValidationResult.Valid
    }

    /**
     * Strict Username Validation
     */
    fun validateUsername(username: String?): ValidationResult {
        if (username.isNullOrBlank()) {
            return ValidationResult.Invalid("Username cannot be empty.")
        }
        val trimmed = username.trim()
        if (!USERNAME_REGEX.matcher(trimmed).matches()) {
            return ValidationResult.Invalid("Username must be 2-50 alphanumeric characters (dots, underscores, dashes allowed).")
        }
        return ValidationResult.Valid
    }

    /**
     * Strict Text Field / Title Validation
     */
    fun validateTitle(title: String?, minLength: Int = 3, maxLength: Int = 200): ValidationResult {
        if (title.isNullOrBlank()) {
            return ValidationResult.Invalid("Title cannot be empty.")
        }
        val trimmed = title.trim()
        if (trimmed.length < minLength) {
            return ValidationResult.Invalid("Title must contain at least $minLength characters.")
        }
        if (trimmed.length > maxLength) {
            return ValidationResult.Invalid("Title cannot exceed $maxLength characters.")
        }
        if (trimmed.contains("<script", ignoreCase = true) || trimmed.contains("\u0000")) {
            return ValidationResult.Invalid("Title contains disallowed characters.")
        }
        return ValidationResult.Valid
    }

    /**
     * Strict Content / Description Validation
     */
    fun validateContent(content: String?, minLength: Int = 3, maxLength: Int = 10000): ValidationResult {
        if (content.isNullOrBlank()) {
            return ValidationResult.Invalid("Content cannot be empty.")
        }
        val trimmed = content.trim()
        if (trimmed.length < minLength) {
            return ValidationResult.Invalid("Content must be at least $minLength characters long.")
        }
        if (trimmed.length > maxLength) {
            return ValidationResult.Invalid("Content exceeds maximum size of $maxLength characters.")
        }
        return ValidationResult.Valid
    }

    /**
     * Strict Price Validation
     */
    fun validatePrice(price: Double?, maxPrice: Double = 50000.0): ValidationResult {
        if (price == null || price.isNaN() || price.isInfinite()) {
            return ValidationResult.Invalid("Please enter a valid finite number for price.")
        }
        if (price < 0.0) {
            return ValidationResult.Invalid("Price cannot be negative.")
        }
        if (price > maxPrice) {
            return ValidationResult.Invalid("Price cannot exceed ₹$maxPrice.")
        }
        return ValidationResult.Valid
    }

    /**
     * Strict Semester Validation
     */
    fun validateSemester(semester: Int?): ValidationResult {
        if (semester == null || semester !in 1..8) {
            return ValidationResult.Invalid("Semester must be between 1 and 8.")
        }
        return ValidationResult.Valid
    }

    /**
     * Strict URL Validation
     */
    fun validateUrl(url: String?, isOptional: Boolean = false): ValidationResult {
        if (url.isNullOrBlank()) {
            return if (isOptional) ValidationResult.Valid else ValidationResult.Invalid("URL cannot be empty.")
        }
        val trimmed = url.trim()
        if (!trimmed.startsWith("https://", ignoreCase = true) && !trimmed.startsWith("http://", ignoreCase = true)) {
            return ValidationResult.Invalid("URL must start with http:// or https://")
        }
        return try {
            val uri = URI(trimmed)
            if (uri.host.isNullOrBlank()) {
                ValidationResult.Invalid("Invalid URL domain structure.")
            } else {
                ValidationResult.Valid
            }
        } catch (e: Exception) {
            ValidationResult.Invalid("Malformed URL format.")
        }
    }

    /**
     * Strict UPI ID Validation
     */
    fun validateUpiId(upiId: String?): ValidationResult {
        if (upiId.isNullOrBlank()) {
            return ValidationResult.Invalid("UPI ID cannot be empty.")
        }
        val trimmed = upiId.trim()
        if (!UPI_REGEX.matcher(trimmed).matches()) {
            return ValidationResult.Invalid("Invalid UPI ID format. Expected format: name@bank (e.g. user@okhdfcbank).")
        }
        return ValidationResult.Valid
    }

    /**
     * Strict Razorpay Key ID Validation
     */
    fun validateRazorpayKey(keyId: String?, isOptional: Boolean = true): ValidationResult {
        if (keyId.isNullOrBlank()) {
            return if (isOptional) ValidationResult.Valid else ValidationResult.Invalid("Razorpay Key ID cannot be empty.")
        }
        val trimmed = keyId.trim()
        if (trimmed.startsWith("rzp_") && RAZORPAY_KEY_REGEX.matcher(trimmed).matches()) {
            return ValidationResult.Valid
        }
        if (trimmed.length in 10..50 && trimmed.matches(Regex("^[a-zA-Z0-9_-]+$"))) {
            return ValidationResult.Valid
        }
        return ValidationResult.Invalid("Invalid Razorpay API Key format. Key should start with rzp_live_ or rzp_test_")
    }
}
