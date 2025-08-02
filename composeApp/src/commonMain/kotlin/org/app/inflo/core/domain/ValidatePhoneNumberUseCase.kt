package org.app.inflo.core.domain

class ValidatePhoneNumberUseCase {
    
    operator fun invoke(phoneNumber: String): ValidationResult {
        val trimmedNumber = phoneNumber.trim()
        
        return when {
            trimmedNumber.length > 10 -> ValidationResult.Error("Phone number cannot exceed 10 digits")
            trimmedNumber.any { !it.isDigit() } -> ValidationResult.Error("Phone number must contain only digits")
            else -> ValidationResult.Success
        }
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
} 