package com.ayush.doctordiscovery.data.model

import com.google.gson.annotations.SerializedName

/**
 * Generic API response wrapper
 * Used for simple endpoints that return data array
 */
data class DoctorListResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("count")
    val count: Int,
    
    @SerializedName("data")
    val data: List<Doctor>
)

/**
 * Search response with pagination
 * Used for /api/doctors/search endpoint
 */

data class SearchResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("count")
    val count: Int,
    
    @SerializedName("pagination")
    val pagination: Pagination,
    
    @SerializedName("filters")
    val filters: Filters,
    
    @SerializedName("sorting")
    val sorting: Sorting,
    
    @SerializedName("data")
    val data: List<Doctor>
)

/**
 * Pagination metadata
 */
data class Pagination(
    @SerializedName("currentPage")
    val currentPage: Int,
    
    @SerializedName("totalPages")
    val totalPages: Int,
    
    @SerializedName("totalResults")
    val totalResults: Int,
    
    @SerializedName("resultsPerPage")
    val resultsPerPage: Int,
    
    @SerializedName("hasNextPage")
    val hasNextPage: Boolean,
    
    @SerializedName("hasPreviousPage")
    val hasPreviousPage: Boolean
)

/**
 * Applied filters
 */
data class Filters(
    @SerializedName("name")
    val name: String?,
    
    @SerializedName("specialization")
    val specialization: String?,
    
    @SerializedName("location")
    val location: String?,
    
    @SerializedName("minRating")
    val minRating: Float?,
    
    @SerializedName("maxFee")
    val maxFee: Int?,
    
    @SerializedName("minExperience")
    val minExperience: Int?
)

/**
 * Sorting configuration
 */
data class Sorting(
    @SerializedName("sortBy")
    val sortBy: String,
    
    @SerializedName("order")
    val order: String
)

/**
 * Single doctor response
 */
data class DoctorResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("data")
    val data: Doctor
)

/**
 * Cities list response
 */
data class CitiesResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("count")
    val count: Int,
    
    @SerializedName("data")
    val data: List<String>
)

/**
 * Specializations list response
 */
data class SpecializationsResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("count")
    val count: Int,
    
    @SerializedName("data")
    val data: List<String>
)

/**
 * REGISTRATION RESPONSES
 */

/**
 * Step 1 response - Returns temporary ID
 */
data class RegistrationStep1Response(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("tempId")
    val tempId: String
)

/**
 * Image upload response
 */
data class ImageUploadResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("imagePath")
    val imagePath: String?
)

/**
 * Final registration response - Returns complete doctor object
 */
data class RegistrationCompleteResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: Doctor
)
