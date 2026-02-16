package com.ayush.doctordiscovery.data.model

import com.google.gson.annotations.SerializedName

/**
 * Doctor data model matching backend API response
 * All fields match the MySQL database schema
 */
data class Doctor(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("specialization")
    val specialization: String,
    
    @SerializedName("experience_years")
    val experienceYears: Int,
    
    @SerializedName("location")
    val location: String,
    
    @SerializedName("rating")
    val rating: String, // Backend sends as string "4.8"
    
    @SerializedName("consultation_fee")
    val consultationFee: Int,
    
    @SerializedName("phone")
    val phone: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("bio")
    val bio: String? = null,
    
    @SerializedName("gender")
    val gender: String? = null,
    
    @SerializedName("age")
    val age: Int? = null,
    
    @SerializedName("institute")
    val institute: String? = null,
    
    @SerializedName("degree")
    val degree: String? = null,
    
    @SerializedName("image_url")
    val imageUrl: String?,
    
    @SerializedName("search_count")
    val searchCount: Int,
    
    @SerializedName("created_at")
    val createdAt: String,
    
    @SerializedName("updated_at")
    val updatedAt: String
) {
    /**
     * Helper property to get rating as Float for display
     */
    val ratingFloat: Float
        get() = rating.toFloatOrNull() ?: 0f
    
    /**
     * Helper property for formatted consultation fee
     */
    val formattedFee: String
        get() = "₹$consultationFee"
    
    /**
     * Helper property for experience display
     */
    val experienceText: String
        get() = "$experienceYears ${if (experienceYears == 1) "year" else "years"} exp."
}
