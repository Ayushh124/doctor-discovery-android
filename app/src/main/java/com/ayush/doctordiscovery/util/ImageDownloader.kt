package com.ayush.doctordiscovery.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Manual Image Downloader - No third-party libraries!
 * Uses only native Java/Android networking (java.net.URL)
 * 
 * Manager-approved: Pure Kotlin/Android implementation ✅
 */
object ImageDownloader {
    
    /**
     * Downloads an image from a URL and returns it as ImageBitmap
     * 
     * @param urlString The full image URL (e.g., "http://10.0.2.2:3000/uploads/doctors/image.jpg")
     * @return ImageBitmap if successful, null if failed
     * 
     * How it works:
     * 1. Opens HTTP connection to URL
     * 2. Reads image bytes from network stream
     * 3. Decodes bytes into Android Bitmap
     * 4. Converts Bitmap to Compose ImageBitmap
     * 5. Returns result (or null on error)
     */
    suspend fun downloadImage(urlString: String?): ImageBitmap? = withContext(Dispatchers.IO) {
        if (urlString.isNullOrBlank()) {
            return@withContext null
        }
        
        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        
        try {
            // Step 1: Create URL object from string
            val url = URL(urlString)
            
            // Step 2: Open HTTP connection
            connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "GET"
                connectTimeout = 10000  // 10 seconds
                readTimeout = 10000     // 10 seconds
                doInput = true
            }
            
            // Step 3: Connect and check response
            connection.connect()
            
            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                // Server returned error (404, 500, etc.)
                return@withContext null
            }
            
            // Step 4: Get input stream from connection
            inputStream = connection.inputStream
            
            // Step 5: Decode stream into Bitmap
            val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
            
            // Step 6: Convert Android Bitmap to Compose ImageBitmap
            bitmap?.asImageBitmap()
            
        } catch (e: Exception) {
            // Network error, malformed URL, decode error, etc.
            e.printStackTrace()
            null
            
        } finally {
            // Step 7: Clean up resources
            inputStream?.close()
            connection?.disconnect()
        }
    }
    
    /**
     * Downloads image with automatic retry
     * 
     * @param urlString Image URL
     * @param retries Number of retry attempts (default: 2)
     * @return ImageBitmap if successful, null if all attempts failed
     */
    suspend fun downloadImageWithRetry(
        urlString: String?,
        retries: Int = 2
    ): ImageBitmap? {
        repeat(retries + 1) { attempt ->
            val result = downloadImage(urlString)
            if (result != null) {
                return result
            }
            
            // Wait before retry (exponential backoff)
            if (attempt < retries) {
                kotlinx.coroutines.delay(500L * (attempt + 1))
            }
        }
        return null
    }
    
    /**
     * Downloads image with size constraints
     * Useful for reducing memory usage with large images
     * 
     * @param urlString Image URL
     * @param maxWidth Maximum width in pixels
     * @param maxHeight Maximum height in pixels
     * @return Scaled ImageBitmap or null
     */
    suspend fun downloadImageScaled(
        urlString: String?,
        maxWidth: Int = 500,
        maxHeight: Int = 500
    ): ImageBitmap? = withContext(Dispatchers.IO) {
        if (urlString.isNullOrBlank()) {
            return@withContext null
        }
        
        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 10000
                doInput = true
            }
            connection.connect()
            
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext null
            }
            
            inputStream = connection.inputStream
            
            // First, decode image dimensions without loading full image
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            
            // Close and reopen stream (can't reuse after inJustDecodeBounds)
            inputStream.close()
            connection.disconnect()
            
            // Reopen connection
            connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 10000
                doInput = true
            }
            connection.connect()
            inputStream = connection.inputStream
            
            // Calculate sample size to scale down image
            val sampleSize = calculateInSampleSize(
                options.outWidth,
                options.outHeight,
                maxWidth,
                maxHeight
            )
            
            // Decode with sample size
            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
            }
            
            val bitmap = BitmapFactory.decodeStream(inputStream, null, decodeOptions)
            bitmap?.asImageBitmap()
            
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            inputStream?.close()
            connection?.disconnect()
        }
    }
    
    /**
     * Calculates the largest inSampleSize value that is a power of 2
     * and keeps both height and width larger than the requested dimensions
     */
    private fun calculateInSampleSize(
        width: Int,
        height: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        var inSampleSize = 1
        
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            
            while ((halfHeight / inSampleSize) >= reqHeight &&
                (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        
        return inSampleSize
    }
}
