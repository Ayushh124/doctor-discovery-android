package com.ayush.doctordiscovery.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayush.doctordiscovery.data.model.Doctor
import com.ayush.doctordiscovery.data.repository.DoctorRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for Doctor Detail Screen
 * 
 * IMPORTANT: When loadDoctorDetails() is called, it:
 * 1. Fetches doctor details from backend
 * 2. Backend AUTOMATICALLY increments search_count in database
 * 3. Returns updated doctor data with new search_count
 * 4. Checks if doctor is in Top 10 for badge display
 * 
 * No separate "increment" button needed - happens automatically on view!
 */
class DoctorDetailViewModel : ViewModel() {
    
    private val repository = DoctorRepository()
    
    // UI State
    private val _uiState = mutableStateOf(DoctorDetailUiState())
    val uiState: State<DoctorDetailUiState> = _uiState
    
    // Top 10 doctor IDs (for badge logic)
    private val _topDoctorIds = mutableStateOf<Set<Int>>(emptySet())
    val topDoctorIds: State<Set<Int>> = _topDoctorIds
    
    /**
     * Load doctor details by ID
     * 
     * HOW IT WORKS:
     * 1. Android calls: repository.getDoctorById(id)
     * 2. Retrofit sends: GET http://10.5.50.85:3000/api/doctors/5
     * 3. Backend receives request
     * 4. Backend runs: UPDATE doctors SET search_count = search_count + 1 WHERE id = 5
     * 5. Backend fetches: SELECT * FROM doctors WHERE id = 5
     * 6. Backend returns updated doctor with new search_count
     * 7. Android displays doctor with incremented count
     * 
     * ALL AUTOMATIC - NO USER ACTION REQUIRED!
     */
    fun loadDoctorDetails(doctorId: Int) {
        if (_uiState.value.isLoading) return
        
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            // Fetch doctor details (this automatically increments search_count on backend!)
            val result = repository.getDoctorById(doctorId)
            
            result.onSuccess { doctor ->
                _uiState.value = _uiState.value.copy(
                    doctor = doctor,
                    isLoading = false,
                    error = null
                )
                
                // Load top 10 to check if this doctor should show badge
                loadTopDoctors()
                
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Failed to load doctor details"
                )
            }
        }
    }
    
    /**
     * Load top 10 doctors by search_count
     * Used to determine if current doctor should show "Most Searched" badge
     */
    private fun loadTopDoctors() {
        viewModelScope.launch {
            repository.getTopDoctors(limit = 10).onSuccess { topDoctors ->
                // Extract IDs of top 10 doctors
                _topDoctorIds.value = topDoctors.map { it.id }.toSet()
            }
        }
    }
    
    /**
     * Check if current doctor is in Top 10
     */
    fun isInTopTen(): Boolean {
        val currentDoctorId = _uiState.value.doctor?.id ?: return false
        return _topDoctorIds.value.contains(currentDoctorId)
    }
    
    /**
     * Retry loading doctor details
     */
    fun retry(doctorId: Int) {
        loadDoctorDetails(doctorId)
    }
}

/**
 * UI State for Doctor Detail Screen
 */
data class DoctorDetailUiState(
    val doctor: Doctor? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
