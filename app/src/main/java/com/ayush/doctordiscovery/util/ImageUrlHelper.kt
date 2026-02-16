package com.ayush.doctordiscovery.util

/**
 * Helper object for handling image URLs
 * 
 * The backend stores image paths like: "uploads/doctors/1234567890_image.jpg"
 * We need to convert this to a full URL: "http://10.5.50.85:3000/uploads/doctors/1234567890_image.jpg"
 */
object ImageUrlHelper {
    
    /**
     * Base URL for the backend server
     * 
     * Using your Mac's IP address for real device testing
     * Must match the IP in RetrofitInstance.kt
     */
    private const val BASE_URL = "http://192.168.1.38:3000"
    
    /**
     * Converts a relative image path to a full URL
     * 
     * Examples:
     * - Input: "uploads/doctors/image.jpg"
     * - Output: "http://10.5.50.85:3000/uploads/doctors/image.jpg"
     * 
     * - Input: "http://example.com/image.jpg" (already full URL)
     * - Output: "http://example.com/image.jpg" (unchanged)
     * 
     * - Input: null or empty
     * - Output: null (will trigger placeholder in UI)
     */
    fun getFullImageUrl(imagePath: String?): String? {
        if (imagePath.isNullOrBlank()) {
            return null
        }
        
        // If already a full URL (starts with http:// or https://), return as is
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return imagePath
        }
        
        // Remove leading slash if present
        val cleanPath = imagePath.trimStart('/')
        
        // Build full URL
        return "$BASE_URL/$cleanPath"
    }
    
    /**
     * Alternative: Use emulator's special IP (10.0.2.2)
     * Only use this if the above doesn't work
     */
    fun getFullImageUrlForEmulator(imagePath: String?): String? {
        if (imagePath.isNullOrBlank()) {
            return null
        }
        
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return imagePath
        }
        
        val cleanPath = imagePath.trimStart('/')
        return "http://10.0.2.2:3000/$cleanPath"
    }
}
