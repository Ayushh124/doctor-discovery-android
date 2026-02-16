package com.ayush.doctordiscovery.data.model

import android.net.Uri

/**
 * Data class to hold all registration form data
 * Shared across both steps of the registration process
 */
data class RegistrationFormState(
    // Step 1: Personal Information
    val name: String = "",
    val gender: String = "",
    val age: String = "",
    val email: String = "",
    val phone: String = "",
    val city: String = "",
    val profileImageUri: Uri? = null,
    
    // Step 2: Professional Details
    val institute: String = "",
    val degree: String = "",
    val specialization: String = "",
    val experience: String = "",
    val consultationFee: String = "",
    val bio: String = "",
    
    // Validation errors (field name -> error message)
    val errors: Map<String, String> = emptyMap()
)

/**
 * UI state for registration screen
 */
data class RegistrationUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val tempId: String? = null  // For multi-step backend API
)
