package com.ayush.doctordiscovery.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ayush.doctordiscovery.ui.components.*
import com.ayush.doctordiscovery.ui.viewmodel.RegistrationViewModel

/**
 * Doctor Registration Screen - Multi-Step Form with API Integration
 * Step 1: Personal Information
 * Step 2: Professional Details → Submit to Backend
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorRegistrationScreen(
    onBackClick: () -> Unit,
    onRegistrationSuccess: () -> Unit,
    viewModel: RegistrationViewModel = viewModel()
) {
    val formState by viewModel.formState
    val currentStep by viewModel.currentStep
    val uiState by viewModel.uiState
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register as Doctor") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Step indicator
                Text(
                    text = "Step $currentStep of 2",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                
                // Content based on current step
                when (currentStep) {
                    1 -> PersonalInfoStep(
                        formState = formState,
                        onNameChange = viewModel::updateName,
                        onGenderChange = viewModel::updateGender,
                        onAgeChange = viewModel::updateAge,
                        onEmailChange = viewModel::updateEmail,
                        onPhoneChange = viewModel::updatePhone,
                        onCityChange = viewModel::updateCity,
                        onImageSelected = viewModel::updateProfileImage
                    )
                    
                    2 -> ProfessionalDetailsStep(
                        formState = formState,
                        onInstituteChange = viewModel::updateInstitute,
                        onDegreeChange = viewModel::updateDegree,
                        onSpecializationChange = viewModel::updateSpecialization,
                        onExperienceChange = viewModel::updateExperience,
                        onConsultationFeeChange = viewModel::updateConsultationFee,
                        onBioChange = viewModel::updateBio
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Navigation buttons
                NavigationButtons(
                    currentStep = currentStep,
                    isLoading = uiState.isLoading,
                    onBack = {
                        if (currentStep == 1) {
                            onBackClick()
                        } else {
                            viewModel.previousStep()
                        }
                    },
                    onNext = viewModel::nextStep,
                    onSubmit = {
                        viewModel.submitRegistration {
                            onRegistrationSuccess()
                        }
                    }
                )
            }
            
            // Success Dialog
            if (uiState.successMessage != null) {
                AlertDialog(
                    onDismissRequest = {},
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(48.dp)
                        )
                    },
                    title = {
                        Text(
                            text = "Success!",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(
                            text = uiState.successMessage!!,
                            textAlign = TextAlign.Center
                        )
                    },
                    confirmButton = {}
                )
            }
            
            // Error Dialog
            if (uiState.error != null) {
                AlertDialog(
                    onDismissRequest = { viewModel.clearError() },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    title = {
                        Text(
                            text = "Registration Failed",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(text = uiState.error!!)
                    },
                    confirmButton = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}

/**
 * Step 1: Personal Information
 */
@Composable
private fun PersonalInfoStep(
    formState: com.ayush.doctordiscovery.data.model.RegistrationFormState,
    onNameChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onImageSelected: (android.net.Uri?) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Personal Information",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        FormTextField(
            value = formState.name,
            onValueChange = onNameChange,
            label = "Full Name *",
            placeholder = "Dr. John Doe",
            errorMessage = formState.errors["name"],
            leadingIcon = Icons.Default.Person
        )
        
        FormDropdown(
            value = formState.gender,
            onValueChange = onGenderChange,
            label = "Gender *",
            options = listOf("Male", "Female", "Other"),
            errorMessage = formState.errors["gender"],
            leadingIcon = Icons.Default.Person
        )
        
        FormTextField(
            value = formState.age,
            onValueChange = { if (it.length <= 2) onAgeChange(it) },
            label = "Age *",
            placeholder = "35",
            errorMessage = formState.errors["age"],
            keyboardType = KeyboardType.Number,
            leadingIcon = Icons.Default.Person
        )
        
        FormTextField(
            value = formState.email,
            onValueChange = onEmailChange,
            label = "Email Address *",
            placeholder = "doctor@example.com",
            errorMessage = formState.errors["email"],
            keyboardType = KeyboardType.Email,
            leadingIcon = Icons.Default.Email
        )
        
        FormTextField(
            value = formState.phone,
            onValueChange = { if (it.length <= 15) onPhoneChange(it) },
            label = "Phone Number *",
            placeholder = "+91-9876543210",
            errorMessage = formState.errors["phone"],
            keyboardType = KeyboardType.Phone,
            leadingIcon = Icons.Default.Phone
        )
        
        FormDropdown(
            value = formState.city,
            onValueChange = onCityChange,
            label = "City *",
            options = listOf(
                "Mumbai", "Delhi", "Bangalore", "Hyderabad", "Chennai",
                "Pune", "Kolkata", "Ahmedabad", "Jaipur", "Chandigarh"
            ),
            errorMessage = formState.errors["city"],
            leadingIcon = Icons.Default.LocationOn
        )
        
        ImageUploadSection(
            imageUri = formState.profileImageUri,
            onImageSelected = onImageSelected
        )
    }
}

/**
 * Step 2: Professional Details
 */
@Composable
private fun ProfessionalDetailsStep(
    formState: com.ayush.doctordiscovery.data.model.RegistrationFormState,
    onInstituteChange: (String) -> Unit,
    onDegreeChange: (String) -> Unit,
    onSpecializationChange: (String) -> Unit,
    onExperienceChange: (String) -> Unit,
    onConsultationFeeChange: (String) -> Unit,
    onBioChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Professional Details",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        FormTextField(
            value = formState.institute,
            onValueChange = onInstituteChange,
            label = "Institute Name *",
            placeholder = "AIIMS, Delhi",
            errorMessage = formState.errors["institute"],
            leadingIcon = Icons.Default.Home
        )
        
        FormTextField(
            value = formState.degree,
            onValueChange = onDegreeChange,
            label = "Degree *",
            placeholder = "MBBS, MD (Cardiology)",
            errorMessage = formState.errors["degree"],
            leadingIcon = Icons.Default.Info
        )
        
        FormDropdown(
            value = formState.specialization,
            onValueChange = onSpecializationChange,
            label = "Specialization *",
            options = listOf(
                "General Physician", "Cardiologist", "Dermatologist",
                "Pediatrician", "Orthopedic", "Gynecologist",
                "Neurologist", "Psychiatrist", "ENT Specialist", "Ophthalmologist"
            ),
            errorMessage = formState.errors["specialization"],
            leadingIcon = Icons.Default.Star
        )
        
        FormTextField(
            value = formState.experience,
            onValueChange = { if (it.length <= 2) onExperienceChange(it) },
            label = "Years of Experience (YOE) *",
            placeholder = "15",
            errorMessage = formState.errors["experience"],
            keyboardType = KeyboardType.Number,
            leadingIcon = Icons.Default.Star
        )
        
        FormTextField(
            value = formState.consultationFee,
            onValueChange = { if (it.length <= 5) onConsultationFeeChange(it) },
            label = "Consultation Fee *",
            placeholder = "800",
            prefix = "₹",
            errorMessage = formState.errors["consultationFee"],
            keyboardType = KeyboardType.Number,
            leadingIcon = Icons.Default.Info
        )
        
        FormTextField(
            value = formState.bio,
            onValueChange = onBioChange,
            label = "Bio/About (Optional)",
            placeholder = "Tell patients about yourself...",
            maxLines = 5,
            leadingIcon = Icons.Default.Info
        )
    }
}

/**
 * Navigation buttons with loading state
 */
@Composable
private fun NavigationButtons(
    currentStep: Int,
    isLoading: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Back button
        OutlinedButton(
            onClick = onBack,
            enabled = !isLoading,
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (currentStep == 1) "Cancel" else "Back")
        }
        
        // Next/Submit button
        Button(
            onClick = if (currentStep == 1) onNext else onSubmit,
            enabled = !isLoading,
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submitting...")
            } else {
                if (currentStep == 1) {
                    Text("Next")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next",
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Submit",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submit")
                }
            }
        }
    }
}
