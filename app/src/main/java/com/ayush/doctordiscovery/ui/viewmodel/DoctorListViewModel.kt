package com.ayush.doctordiscovery.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayush.doctordiscovery.data.model.Doctor
import com.ayush.doctordiscovery.data.repository.DoctorRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for Doctor List Screen
 * Manages doctor list state, pagination, and infinite scroll
 */
class DoctorListViewModel : ViewModel() {
    
    private val repository = DoctorRepository()
    
    // UI State
    private val _uiState = mutableStateOf(DoctorListUiState())
    val uiState: State<DoctorListUiState> = _uiState
    
    // Pagination
    private var currentPage = 1
    private var hasNextPage = true
    private var isLoadingMore = false
    
    // Filters
    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery
    
    private val _selectedSpecialization = mutableStateOf<String?>(null)
    val selectedSpecialization: State<String?> = _selectedSpecialization
    
    private val _selectedCity = mutableStateOf<String?>(null)
    val selectedCity: State<String?> = _selectedCity
    
    // Dropdown data
    private val _cities = mutableStateOf<List<String>>(emptyList())
    val cities: State<List<String>> = _cities
    
    private val _specializations = mutableStateOf<List<String>>(emptyList())
    val specializations: State<List<String>> = _specializations
    
    init {
        loadTopDoctors()  // Load top 4 first!
        loadDoctors()
        loadCities()
        loadSpecializations()
    }
    
    /**
     * Load top 4 most searched doctors
     * Displayed in special "Most Searched" section at top of screen
     */
    private fun loadTopDoctors() {
        viewModelScope.launch {
            repository.getTopDoctors(limit = 4).onSuccess { topDoctorsList ->
                _uiState.value = _uiState.value.copy(
                    topDoctors = topDoctorsList
                )
            }
        }
    }
    
    /**
     * Load initial doctors list
     */
    fun loadDoctors() {
        if (_uiState.value.isLoading) return
        
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        currentPage = 1
        hasNextPage = true
        
        viewModelScope.launch {
            val result = repository.searchDoctors(
                name = _searchQuery.value.takeIf { it.isNotBlank() },
                specialization = _selectedSpecialization.value,
                location = _selectedCity.value,
                page = currentPage,
                limit = 10
            )
            
            result.onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    doctors = response.data,
                    isLoading = false,
                    error = null
                )
                hasNextPage = response.pagination.hasNextPage
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    /**
     * Refresh doctors list and top doctors
     * Called when returning to home screen (e.g., after registration)
     */
    fun refreshDoctors() {
        viewModelScope.launch {
            // Refresh top doctors (top 4)
            repository.getTopDoctors(limit = 4).onSuccess { topDoctorsList ->
                _uiState.value = _uiState.value.copy(
                    topDoctors = topDoctorsList
                )
            }
            
            // Refresh main list
            loadDoctors()
        }
    }
    
    /**
     * Load more doctors for infinite scroll
     * Called when user scrolls to the end of the list
     */
    fun loadMoreDoctors() {
        if (!hasNextPage || isLoadingMore || _uiState.value.isLoading) return
        
        isLoadingMore = true
        _uiState.value = _uiState.value.copy(isLoadingMore = true)
        
        viewModelScope.launch {
            val nextPage = currentPage + 1
            val result = repository.searchDoctors(
                name = _searchQuery.value.takeIf { it.isNotBlank() },
                specialization = _selectedSpecialization.value,
                location = _selectedCity.value,
                page = nextPage,
                limit = 10
            )
            
            result.onSuccess { response ->
                val updatedList = _uiState.value.doctors + response.data
                _uiState.value = _uiState.value.copy(
                    doctors = updatedList,
                    isLoadingMore = false
                )
                currentPage = nextPage
                hasNextPage = response.pagination.hasNextPage
                isLoadingMore = false
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoadingMore = false)
                isLoadingMore = false
            }
        }
    }
    
    /**
     * Update search query and reload
     */
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        // Trigger search immediately when query changes
        loadDoctors()
    }
    
    /**
     * Execute search with current query
     */
    fun executeSearch() {
        loadDoctors()
    }
    
    /**
     * Update specialization filter and reload
     */
    fun onSpecializationSelected(specialization: String?) {
        _selectedSpecialization.value = specialization
        loadDoctors()
    }
    
    /**
     * Update city filter and reload
     */
    fun onCitySelected(city: String?) {
        _selectedCity.value = city
        loadDoctors()
    }
    
    /**
     * Clear all filters
     */
    fun clearFilters() {
        _searchQuery.value = ""
        _selectedSpecialization.value = null
        _selectedCity.value = null
        loadDoctors()
    }
    
    /**
     * Refresh data (pull to refresh)
     */
    fun refresh() {
        loadDoctors()
    }
    
    /**
     * Load cities for dropdown
     */
    private fun loadCities() {
        viewModelScope.launch {
            repository.getCities().onSuccess { citiesList ->
                _cities.value = citiesList
            }
        }
    }
    
    /**
     * Load specializations for dropdown
     */
    private fun loadSpecializations() {
        viewModelScope.launch {
            repository.getSpecializations().onSuccess { specList ->
                _specializations.value = specList
            }
        }
    }
}

/**
 * UI State data class
 */
data class DoctorListUiState(
    val topDoctors: List<Doctor> = emptyList(),  // Top 4 most searched
    val doctors: List<Doctor> = emptyList(),      // Regular doctor list
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null
)
