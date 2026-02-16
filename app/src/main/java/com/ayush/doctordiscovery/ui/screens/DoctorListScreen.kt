package com.ayush.doctordiscovery.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ayush.doctordiscovery.ui.components.*
import com.ayush.doctordiscovery.ui.viewmodel.DoctorListViewModel

/**
 * Main Doctor List Screen with infinite scroll
 * Features:
 * - Search by doctor name
 * - Filter by specialization and city
 * - Infinite scroll (load more on scroll to bottom)
 * - Pull to refresh
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorListScreen(
    viewModel: DoctorListViewModel = viewModel(),
    onDoctorClick: (Int) -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState
    val searchQuery by viewModel.searchQuery
    val selectedSpecialization by viewModel.selectedSpecialization
    val selectedCity by viewModel.selectedCity
    val cities by viewModel.cities
    val specializations by viewModel.specializations
    
    var showFilterSheet by remember { mutableStateOf(false) }
    
    val listState = rememberLazyListState()
    
    // Auto-refresh when screen comes into focus
    // This ensures the list is updated after doctor registration
    LaunchedEffect(Unit) {
        viewModel.refreshDoctors()
    }
    
    // Detect when user scrolls to bottom for infinite scroll
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && 
                    lastVisibleIndex >= uiState.doctors.size - 2 && 
                    !uiState.isLoadingMore && 
                    !uiState.isLoading
                ) {
                    viewModel.loadMoreDoctors()
                }
            }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find Doctors") },
                actions = {
                    // Register button
                    IconButton(onClick = onRegisterClick) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Register Doctor"
                        )
                    }
                    
                    // Filter button
                    BadgedBox(
                        badge = {
                            if (selectedSpecialization != null || selectedCity != null) {
                                Badge(containerColor = MaterialTheme.colorScheme.error)
                            }
                        }
                    ) {
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Filter"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                onSearch = { viewModel.executeSearch() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            
            // Active filters chips
            if (selectedSpecialization != null || selectedCity != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedSpecialization?.let { spec ->
                        FilterChip(
                            selected = true,
                            onClick = { viewModel.onSpecializationSelected(null) },
                            label = { Text(spec) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove filter",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                    
                    selectedCity?.let { city ->
                        FilterChip(
                            selected = true,
                            onClick = { viewModel.onCitySelected(null) },
                            label = { Text(city) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove filter",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Content
            when {
                uiState.isLoading && uiState.doctors.isEmpty() -> {
                    LoadingIndicator()
                }
                
                uiState.error != null && uiState.doctors.isEmpty() -> {
                    ErrorView(
                        message = uiState.error!!,
                        onRetry = viewModel::loadDoctors
                    )
                }
                
                uiState.doctors.isEmpty() -> {
                    EmptyStateView(
                        message = "No doctors found\nTry adjusting your filters"
                    )
                }
                
                else -> {
                    DoctorList(
                        topDoctors = uiState.topDoctors,
                        doctors = uiState.doctors,
                        isLoadingMore = uiState.isLoadingMore,
                        onDoctorClick = onDoctorClick,
                        listState = listState
                    )
                }
            }
        }
        
        // Filter Bottom Sheet
        if (showFilterSheet) {
            FilterBottomSheet(
                specializations = specializations,
                cities = cities,
                selectedSpecialization = selectedSpecialization,
                selectedCity = selectedCity,
                onSpecializationSelected = viewModel::onSpecializationSelected,
                onCitySelected = viewModel::onCitySelected,
                onClearFilters = viewModel::clearFilters,
                onDismiss = { showFilterSheet = false }
            )
        }
    }
}

/**
 * Doctor list with top doctors section and infinite scroll
 */
@Composable
private fun DoctorList(
    topDoctors: List<com.ayush.doctordiscovery.data.model.Doctor>,
    doctors: List<com.ayush.doctordiscovery.data.model.Doctor>,
    isLoadingMore: Boolean,
    onDoctorClick: (Int) -> Unit,
    listState: LazyListState
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top 4 Most Searched Doctors Section
        if (topDoctors.isNotEmpty()) {
            item {
                TopDoctorsSection(
                    topDoctors = topDoctors,
                    onDoctorClick = onDoctorClick
                )
            }
            
            // Spacer between sections
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        
        // Regular doctor list
        items(
            items = doctors,
            key = { it.id }
        ) { doctor ->
            DoctorCard(
                doctor = doctor,
                onClick = { onDoctorClick(doctor.id) }
            )
        }
        
        // Loading more indicator at bottom
        if (isLoadingMore) {
            item {
                SmallLoadingIndicator()
            }
        }
    }
}

/**
 * Search bar component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search doctors by name...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search")
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear")
                }
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
                keyboardController?.hide()
            }
        )
    )
}

/**
 * Filter bottom sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheet(
    specializations: List<String>,
    cities: List<String>,
    selectedSpecialization: String?,
    selectedCity: String?,
    onSpecializationSelected: (String?) -> Unit,
    onCitySelected: (String?) -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                TextButton(onClick = {
                    onClearFilters()
                    onDismiss()
                }) {
                    Text("Clear All")
                }
            }
            
            HorizontalDivider()
            
            // Specialization filter
            Text(
                text = "Specialization",
                style = MaterialTheme.typography.titleMedium
            )
            
            DropdownFilterMenu(
                label = "Select Specialization",
                options = specializations,
                selectedOption = selectedSpecialization,
                onOptionSelected = {
                    onSpecializationSelected(it)
                    onDismiss()
                }
            )
            
            // City filter
            Text(
                text = "City",
                style = MaterialTheme.typography.titleMedium
            )
            
            DropdownFilterMenu(
                label = "Select City",
                options = cities,
                selectedOption = selectedCity,
                onOptionSelected = {
                    onCitySelected(it)
                    onDismiss()
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Dropdown filter menu
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownFilterMenu(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            value = selectedOption ?: "",
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Top Doctors Section - Shows 4 most searched doctors in horizontal scroll
 */
@Composable
private fun TopDoctorsSection(
    topDoctors: List<com.ayush.doctordiscovery.data.model.Doctor>,
    onDoctorClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🔥 Most Searched Doctors",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Top ${topDoctors.size}",
                style = MaterialTheme.typography.bodySmall,
                color = androidx.compose.ui.graphics.Color.Gray
            )
        }
        
        // Horizontal scrollable row of top doctors
        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = topDoctors,
                key = { it.id }
            ) { doctor ->
                TopDoctorCard(
                    doctor = doctor,
                    onClick = { onDoctorClick(doctor.id) }
                )
            }
        }
    }
}

/**
 * Compact card for top doctors (horizontal layout)
 */
@Composable
private fun TopDoctorCard(
    doctor: com.ayush.doctordiscovery.data.model.Doctor,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(160.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Doctor Image
            NetworkImage(
                imageUrl = doctor.imageUrl,
                contentDescription = "Dr. ${doctor.name}",
                modifier = Modifier
                    .size(60.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(androidx.compose.ui.graphics.Color(0xFFE0E0E0)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            
            // Doctor Name
            Text(
                text = doctor.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                maxLines = 2,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Specialization
            Text(
                text = doctor.specialization,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "⭐",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = doctor.rating,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                    )
                }
                
                // Views
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "👁️",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "${doctor.searchCount}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                    )
                }
            }
        }
    }
}
