package com.ayush.doctordiscovery.data.remote

import com.ayush.doctordiscovery.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API service interface
 * Defines all endpoints for Doctor Discovery backend
 */
interface DoctorApiService {
    
    /**
     * Get all doctors without filters
     * Endpoint: GET /api/doctors
     */
    @GET("doctors")
    suspend fun getAllDoctors(): Response<DoctorListResponse>
    
    /**
     * Get single doctor by ID
     * Endpoint: GET /api/doctors/{id}
     * IMPORTANT: This automatically increments search_count on backend!
     */
    @GET("doctors/{id}")
    suspend fun getDoctorById(
        @Path("id") id: Int
    ): Response<DoctorResponse>
    
    /**
     * Get top most searched doctors with full information
     * Endpoint: GET /api/doctors/top
     * @param limit - Number of top doctors to return (default: 4, max: 10)
     */
    @GET("doctors/top")
    suspend fun getTopDoctors(
        @Query("limit") limit: Int = 4
    ): Response<DoctorListResponse>
    
    /**
     * Get all available cities
     * Endpoint: GET /api/doctors/cities
     */
    @GET("doctors/cities")
    suspend fun getCities(): Response<CitiesResponse>
    
    /**
     * Get all available specializations
     * Endpoint: GET /api/doctors/specializations
     */
    @GET("doctors/specializations")
    suspend fun getSpecializations(): Response<SpecializationsResponse>
    
    /**
     * Search doctors with filters and pagination
     * Endpoint: GET /api/doctors/search
     * 
     * All parameters are optional:
     * @param name - Partial name search
     * @param specialization - Exact specialization match
     * @param location - Exact location match
     * @param minRating - Minimum rating (0-5)
     * @param maxFee - Maximum consultation fee
     * @param minExperience - Minimum years of experience
     * @param sortBy - Field to sort by (rating, experience_years, consultation_fee, search_count, name)
     * @param order - Sort order (asc or desc)
     * @param page - Page number (default: 1)
     * @param limit - Results per page (default: 10)
     */
    @GET("doctors/search")
    suspend fun searchDoctors(
        @Query("name") name: String? = null,
        @Query("specialization") specialization: String? = null,
        @Query("location") location: String? = null,
        @Query("minRating") minRating: Float? = null,
        @Query("maxFee") maxFee: Int? = null,
        @Query("minExperience") minExperience: Int? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("order") order: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<SearchResponse>
    
    /**
     * REGISTRATION ENDPOINTS
     */
    
    /**
     * Step 1: Submit personal information
     * Endpoint: POST /api/register/step1
     */
    @FormUrlEncoded
    @POST("register/step1")
    suspend fun registerStep1(
        @Field("name") name: String,
        @Field("gender") gender: String,
        @Field("age") age: Int,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("location") location: String
    ): Response<RegistrationStep1Response>
    
    /**
     * Step 2: Upload profile image (optional)
     * Endpoint: POST /api/register/upload-image
     */
    @Multipart
    @POST("register/upload-image")
    suspend fun uploadProfileImage(
        @Part("tempId") tempId: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<ImageUploadResponse>
    
    /**
     * Step 3: Submit professional details and complete registration
     * Endpoint: POST /api/register/step2
     */
    @FormUrlEncoded
    @POST("register/step2")
    suspend fun registerStep2(
        @Field("tempId") tempId: String,
        @Field("specialization") specialization: String,
        @Field("institute") institute: String,
        @Field("degree") degree: String,
        @Field("experience_years") experienceYears: Int,
        @Field("consultation_fee") consultationFee: Int,
        @Field("bio") bio: String?,
        @Field("image_url") imagePath: String?
    ): Response<RegistrationCompleteResponse>
}
