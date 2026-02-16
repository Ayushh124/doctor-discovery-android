package com.ayush.doctordiscovery.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit singleton instance
 * Provides configured API service for network calls
 */
object RetrofitInstance {
    
    /**
     * Base URL for API calls
     * 
     * Using your Mac's IP address for real device testing
     * If this doesn't work:
     * 1. Run: cd backend && ./get_my_ip.sh
     * 2. Update this IP to match your Mac's current IP
     * 3. Make sure phone and Mac are on same Wi-Fi
     */
    private const val BASE_URL = "http://192.168.1.38:3000/api/"
    
    /**
     * Logging interceptor for debugging
     * Shows request/response details in Logcat
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    /**
     * OkHttp client with logging and timeouts
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()
    
    /**
     * Retrofit instance
     * Lazy initialization - created only when first accessed
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * API service instance
     * Use this to make API calls throughout the app
     * 
     * Example usage:
     * val response = RetrofitInstance.api.getAllDoctors()
     */
    val api: DoctorApiService by lazy {
        retrofit.create(DoctorApiService::class.java)
    }
}
