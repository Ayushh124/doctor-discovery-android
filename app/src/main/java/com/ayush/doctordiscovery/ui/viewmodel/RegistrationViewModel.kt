package com.ayush.doctordiscovery.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ayush.doctordiscovery.data.model.RegistrationFormState
import com.ayush.doctordiscovery.data.model.RegistrationUiState
import com.ayush.doctordiscovery.data.repository.DoctorRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for Doctor Registration Screen
 * Handles form state, validation, and API submission
 */
class RegistrationViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = DoctorRepository()
    
    // Form data
    private val _formState = mutableStateOf(RegistrationFormState())
    val formState: State<RegistrationFormState> = _formState
    
    // Current step (1 or 2)
    private val _currentStep = mutableStateOf(1)
    val currentStep: State<Int> = _currentStep
    
    // UI state (loading, error, success)
    private val _uiState = mutableStateOf(RegistrationUiState())
    val uiState: State<RegistrationUiState> = _uiState
    
    // Update form fields
    fun updateName(value: String) {
        _formState.value = _formState.value.copy(name = value)
    }
    
    fun updateGender(value: String) {
        _formState.value = _formState.value.copy(gender = value)
    }
    
    fun updateAge(value: String) {
        _formState.value = _formState.value.copy(age = value)
    }
    
    fun updateEmail(value: String) {
        _formState.value = _formState.value.copy(email = value)
    }
    
    fun updatePhone(value: String) {
        _formState.value = _formState.value.copy(phone = value)
    }
    
    fun updateCity(value: String) {
        _formState.value = _formState.value.copy(city = value)
    }
    
    fun updateProfileImage(uri: Uri?) {
        _formState.value = _formState.value.copy(profileImageUri = uri)
    }
    
    fun updateInstitute(value: String) {
        _formState.value = _formState.value.copy(institute = value)
    }
    
    fun updateDegree(value: String) {
        _formState.value = _formState.value.copy(degree = value)
    }
    
    fun updateSpecialization(value: String) {
        _formState.value = _formState.value.copy(specialization = value)
    }
    
    fun updateExperience(value: String) {
        _formState.value = _formState.value.copy(experience = value)
    }
    
    fun updateConsultationFee(value: String) {
        _formState.value = _formState.value.copy(consultationFee = value)
    }
    
    fun updateBio(value: String) {
        _formState.value = _formState.value.copy(bio = value)
    }
    
    /**
     * Move to next step (Step 1 → Step 2)
     */
    fun nextStep() {
        if (_currentStep.value == 1 && validateStep1()) {
            _currentStep.value = 2
            _formState.value = _formState.value.copy(errors = emptyMap())
        }
    }
    
    /**
     * Move to previous step (Step 2 → Step 1)
     */
    fun previousStep() {
        if (_currentStep.value > 1) {
            _currentStep.value -= 1
            _formState.value = _formState.value.copy(errors = emptyMap())
        }
    }
    
    /**
     * Validate Step 1 - Check if all required fields are filled
     */
    private fun validateStep1(): Boolean {
        val errors = mutableMapOf<String, String>()
        val form = _formState.value
        
        if (form.name.isBlank()) errors["name"] = "Required"
        if (form.gender.isEmpty()) errors["gender"] = "Required"
        if (form.age.isBlank()) errors["age"] = "Required"
        if (form.email.isBlank()) errors["email"] = "Required"
        if (form.phone.isBlank()) errors["phone"] = "Required"
        if (form.city.isEmpty()) errors["city"] = "Required"
        
        _formState.value = _formState.value.copy(errors = errors)
        return errors.isEmpty()
    }
    
    /**
     * Validate Step 2 - Check if all required fields are filled
     */
    private fun validateStep2(): Boolean {
        val errors = mutableMapOf<String, String>()
        val form = _formState.value
        
        if (form.institute.isBlank()) errors["institute"] = "Required"
        if (form.degree.isBlank()) errors["degree"] = "Required"
        if (form.specialization.isEmpty()) errors["specialization"] = "Required"
        if (form.experience.isBlank()) errors["experience"] = "Required"
        if (form.consultationFee.isBlank()) errors["consultationFee"] = "Required"
        
        _formState.value = _formState.value.copy(errors = errors)
        return errors.isEmpty()
    }
    
    /**
     * Submit registration to backend
     * Multi-step process:
     * 1. Validate Step 2
     * 2. Call backend API (Step 1 → Upload Image → Step 2)
     * 3. Show success message
     * 4. Navigate back to home
     */
    fun submitRegistration(onSuccess: () -> Unit) {
        // Validate Step 2 first
        if (!validateStep2()) {
            return
        }
        
        // Clear any previous errors/success
        _uiState.value = RegistrationUiState(isLoading = true)
        
        viewModelScope.launch {
            try {
                // Call repository to handle multi-step registration
                val result = repository.registerDoctor(
                    formState = _formState.value,
                    context = getApplication()
                )
                
                result.fold(
                    onSuccess = { doctor ->
                        // Success! Show message for 2 seconds, then navigate
                        _uiState.value = RegistrationUiState(
                            isLoading = false,
                            successMessage = "Doctor registered successfully! 🎉"
                        )
                        
                        // Wait 2 seconds to show message, then navigate
                        kotlinx.coroutines.delay(2000)
                        onSuccess()
                    },
                    onFailure = { exception ->
                        // Error - show error message
                        _uiState.value = RegistrationUiState(
                            isLoading = false,
                            error = exception.message ?: "Registration failed"
                        )
                    }
                )
                
            } catch (e: Exception) {
                _uiState.value = RegistrationUiState(
                    isLoading = false,
                    error = "Network error: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * Reset form (for testing or after successful registration)
     */
    fun resetForm() {
        _formState.value = RegistrationFormState()
        _currentStep.value = 1
        _uiState.value = RegistrationUiState()
    }
}
