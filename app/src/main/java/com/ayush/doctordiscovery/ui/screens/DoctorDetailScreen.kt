package com.ayush.doctordiscovery.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ayush.doctordiscovery.data.model.Doctor
import com.ayush.doctordiscovery.ui.components.ErrorView
import com.ayush.doctordiscovery.ui.components.LoadingIndicator
import com.ayush.doctordiscovery.ui.components.NetworkImage
import com.ayush.doctordiscovery.ui.viewmodel.DoctorDetailViewModel

/**
 * Doctor Detail Screen
 * 
 * AUTOMATIC POPULARITY TRACKING:
 * When this screen loads, it calls viewModel.loadDoctorDetails(doctorId)
 * This triggers:
 * 1. Android → Retrofit → GET /api/doctors/:id
 * 2. Backend → UPDATE doctors SET search_count = search_count + 1
 * 3. Backend → SELECT and return doctor with updated count
 * 4. Android → Display doctor with new search_count
 * 
 * The user doesn't click anything - it happens automatically!
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDetailScreen(
    doctorId: Int,
    onBackClick: () -> Unit,
    viewModel: DoctorDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState
    val isInTopTen = viewModel.isInTopTen()
    
    // Load doctor details when screen opens
    // THIS IS WHERE THE MAGIC HAPPENS - search_count increments here!
    LaunchedEffect(doctorId) {
        viewModel.loadDoctorDetails(doctorId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Doctor Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingIndicator(message = "Loading doctor details...")
            }
            
            uiState.error != null -> {
                ErrorView(
                    message = uiState.error!!,
                    onRetry = { viewModel.retry(doctorId) }
                )
            }
            
            uiState.doctor != null -> {
                DoctorDetailContent(
                    doctor = uiState.doctor!!,
                    isInTopTen = isInTopTen,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

/**
 * Doctor detail content with all information
 */
@Composable
private fun DoctorDetailContent(
    doctor: Doctor,
    isInTopTen: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header section with image and basic info
        DoctorHeaderSection(doctor = doctor, isInTopTen = isInTopTen)
        
        // Main information cards
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // About section (only show if bio exists)
            if (!doctor.bio.isNullOrBlank()) {
                InfoCard(title = "About") {
                    Text(
                        text = doctor.bio,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color.Gray
                    )
                }
            }
            
            // Professional Information
            InfoCard(title = "Professional Information") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    InfoRow(
                        icon = Icons.Default.Info,
                        label = "Degree",
                        value = doctor.degree ?: "Not specified"
                    )
                    InfoRow(
                        icon = Icons.Default.Home,
                        label = "Institute",
                        value = doctor.institute ?: "Not specified"
                    )
                    InfoRow(
                        icon = Icons.Default.Star,
                        label = "Experience",
                        value = "${doctor.experienceYears} years"
                    )
                    InfoRow(
                        icon = Icons.Default.Star,
                        label = "Specialization",
                        value = doctor.specialization
                    )
                }
            }
            
            // Personal Information
            InfoCard(title = "Personal Information") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    doctor.gender?.let { gender ->
                        InfoRow(
                            icon = Icons.Default.Person,
                            label = "Gender",
                            value = gender
                        )
                    }
                    doctor.age?.let { age ->
                        InfoRow(
                            icon = Icons.Default.Person,
                            label = "Age",
                            value = "$age years"
                        )
                    }
                    InfoRow(
                        icon = Icons.Default.LocationOn,
                        label = "Location",
                        value = doctor.location
                    )
                }
            }
            
            // Contact Information
            InfoCard(title = "Contact Information") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    InfoRow(
                        icon = Icons.Default.Phone,
                        label = "Phone",
                        value = doctor.phone
                    )
                    InfoRow(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = doctor.email
                    )
                }
            }
            
            // Rating & Fee
            InfoCard(title = "Consultation Details") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    InfoRow(
                        icon = Icons.Default.Star,
                        label = "Rating",
                        value = "${doctor.rating} ⭐"
                    )
                    InfoRow(
                        icon = Icons.Default.Info,
                        label = "Consultation Fee",
                        value = doctor.formattedFee
                    )
                }
            }
            
            // Book Appointment Button
            Button(
                onClick = { /* TODO: Implement booking in future */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Book Appointment", fontSize = 16.sp)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Header section with doctor image, name, and badges
 */
@Composable
private fun DoctorHeaderSection(
    doctor: Doctor,
    isInTopTen: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Doctor Image (Manual loading - NO third-party libraries!)
            NetworkImage(
                imageUrl = doctor.imageUrl,
                contentDescription = "Dr. ${doctor.name}",
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Doctor Name
            Text(
                text = doctor.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            // Specialization
            Text(
                text = doctor.specialization,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Badges Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rating Badge
                Badge(
                    modifier = Modifier.height(28.dp),
                    containerColor = Color(0xFFFFA000)
                ) {
                    Text(
                        text = "⭐ ${doctor.rating}",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = Color.White
                    )
                }
                
                // Most Searched Badge (only if in Top 10)
                if (isInTopTen) {
                    Badge(
                        modifier = Modifier.height(28.dp),
                        containerColor = Color(0xFFE91E63) // Pink/Red
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text("🔥", fontSize = 14.sp)
                            Text(
                                text = "Most Searched",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                // Search Count Badge
                Badge(
                    modifier = Modifier.height(28.dp),
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Text(
                        text = "${doctor.searchCount} views",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

/**
 * Reusable info card component
 */
@Composable
private fun InfoCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            content()
        }
    }
}

/**
 * Info row with icon, label, and value
 */
@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        // Label and Value
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}
