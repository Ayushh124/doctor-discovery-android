package com.ayush.doctordiscovery.data.repository

import android.content.Context
import android.net.Uri
import com.ayush.doctordiscovery.data.model.*
import com.ayush.doctordiscovery.data.remote.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

/**
 * Repository pattern for data operations
 * Handles all API calls and provides clean interface for ViewModels
 */
class DoctorRepository {
    
    private val api = RetrofitInstance.api
    
    /**
     * Get all doctors without filters
     */
    suspend fun getAllDoctors(): Result<List<Doctor>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getAllDoctors()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Failed to fetch doctors: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Search doctors with filters and pagination
     * Returns SearchResponse which includes pagination metadata
     */
    suspend fun searchDoctors(
        name: String? = null,
        specialization: String? = null,
        location: String? = null,
        minRating: Float? = null,
        maxFee: Int? = null,
        minExperience: Int? = null,
        sortBy: String? = null,
        order: String? = null,
        page: Int = 1,
        limit: Int = 10
    ): Result<SearchResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.searchDoctors(
                name = name,
                specialization = specialization,
                location = location,
                minRating = minRating,
                maxFee = maxFee,
                minExperience = minExperience,
                sortBy = sortBy,
                order = order,
                page = page,
                limit = limit
            )
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Search failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get single doctor by ID
     * IMPORTANT: This automatically increments search_count on the backend!
     * No need to call a separate endpoint - it happens when you view the doctor.
     */
    suspend fun getDoctorById(id: Int): Result<Doctor> = withContext(Dispatchers.IO) {
        try {
            val response = api.getDoctorById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Failed to fetch doctor: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get top most searched doctors with full information
     * @param limit - Number of top doctors to fetch (default: 4)
     * Used for displaying "Most Searched" section on home screen
     */
    suspend fun getTopDoctors(limit: Int = 4): Result<List<Doctor>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getTopDoctors(limit)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Failed to fetch top doctors: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get all available cities
     */
    suspend fun getCities(): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getCities()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Failed to fetch cities: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get all available specializations
     */
    suspend fun getSpecializations(): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getSpecializations()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Failed to fetch specializations: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Register new doctor (multi-step process)
     * 
     * Step 1: Submit personal information → Get tempId
     * Step 2: Upload image (if provided) → Attach to tempId
     * Step 3: Submit professional details → Complete registration
     * 
     * @param formState - Complete form data
     * @param context - Android context for reading image file
     * @return Result with Doctor object on success
     */
    suspend fun registerDoctor(
        formState: RegistrationFormState,
        context: Context
    ): Result<Doctor> = withContext(Dispatchers.IO) {
        try {
            // Step 1: Submit personal information
            val step1Response = api.registerStep1(
                name = formState.name,
                gender = formState.gender,
                age = formState.age.toInt(),
                email = formState.email,
                phone = formState.phone,
                location = formState.city
            )
            
            if (!step1Response.isSuccessful || step1Response.body() == null) {
                // Get detailed error message from backend
                val errorBody = try {
                    val errorString = step1Response.errorBody()?.string()
                    if (errorString != null) {
                        // Try to parse JSON error
                        val jsonObject = org.json.JSONObject(errorString)
                        val errorsArray = jsonObject.optJSONArray("errors")
                        if (errorsArray != null) {
                            val errorList = mutableListOf<String>()
                            for (i in 0 until errorsArray.length()) {
                                errorList.add(errorsArray.getString(i))
                            }
                            errorList.joinToString("\n• ", "• ")
                        } else {
                            jsonObject.optString("message", errorString)
                        }
                    } else {
                        step1Response.message()
                    }
                } catch (e: Exception) {
                    step1Response.message()
                }
                
                return@withContext Result.failure(
                    Exception("Step 1 validation failed:\n$errorBody")
                )
            }
            
            val tempId = step1Response.body()!!.tempId
            
            // Step 2: Upload image (if selected)
            var imagePath: String? = null
            if (formState.profileImageUri != null) {
                try {
                    val imagePart = prepareImagePart(context, formState.profileImageUri)
                    val tempIdBody = tempId.toRequestBody("text/plain".toMediaTypeOrNull())
                    
                    val imageResponse = api.uploadProfileImage(tempIdBody, imagePart)
                    
                    if (imageResponse.isSuccessful && imageResponse.body() != null) {
                        // Store the image path to pass to Step 2
                        imagePath = imageResponse.body()!!.imagePath
                    }
                } catch (e: Exception) {
                    // Image upload error, but continue (it's optional)
                }
            }
            
            // Step 3: Submit professional details (with image path if uploaded)
            val step2Response = api.registerStep2(
                tempId = tempId,
                specialization = formState.specialization,
                institute = formState.institute,
                degree = formState.degree,
                experienceYears = formState.experience.toInt(),
                consultationFee = formState.consultationFee.toInt(),
                bio = formState.bio.ifBlank { null },
                imagePath = imagePath
            )
            
            if (step2Response.isSuccessful && step2Response.body() != null) {
                Result.success(step2Response.body()!!.data)
            } else {
                Result.failure(Exception("Registration failed: ${step2Response.message()}"))
            }
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Helper function to prepare image for multipart upload
     */
    private fun prepareImagePart(context: Context, uri: Uri): MultipartBody.Part {
        // Read image from URI and create a temporary file
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("Cannot open image file")
        
        val tempFile = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(tempFile)
        
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        
        // Create RequestBody
        val requestBody = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        
        // Create MultipartBody.Part
        return MultipartBody.Part.createFormData("image", tempFile.name, requestBody)
    }
}
