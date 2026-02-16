package com.ayush.doctordiscovery.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ayush.doctordiscovery.util.ImageDownloader
import com.ayush.doctordiscovery.util.ImageUrlHelper

/**
 * NetworkImage - Manual image loading composable
 * NO THIRD-PARTY LIBRARIES! Manager-approved! ✅
 * 
 * Uses only:
 * - Kotlin coroutines (built-in)
 * - java.net.URL (Java standard library)
 * - Android Bitmap (Android framework)
 * - Jetpack Compose (Android UI framework)
 * 
 * @param imageUrl The image URL (can be relative or full)
 * @param contentDescription Description for accessibility
 * @param modifier Modifier for styling
 * @param contentScale How to scale the image
 * @param showLoadingText Whether to show "Loading..." text (default: false)
 */
@Composable
fun NetworkImage(
    imageUrl: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    showLoadingText: Boolean = false
) {
    // Convert relative path to full URL
    val fullUrl = ImageUrlHelper.getFullImageUrl(imageUrl)
    
    // produceState: Compose-friendly way to launch coroutines
    // Returns a State<ImageLoadState> that automatically triggers recomposition
    val imageState = produceState<ImageLoadState>(
        initialValue = ImageLoadState.Loading,
        key1 = fullUrl  // Reload if URL changes
    ) {
        // This block runs in a coroutine when the composable enters composition
        value = ImageLoadState.Loading
        
        if (fullUrl.isNullOrBlank()) {
            // No URL provided, show placeholder immediately
            value = ImageLoadState.Error
            return@produceState
        }
        
        try {
            // Download image using our custom downloader
            val imageBitmap = ImageDownloader.downloadImage(fullUrl)
            
            if (imageBitmap != null) {
                value = ImageLoadState.Success(imageBitmap)
            } else {
                value = ImageLoadState.Error
            }
        } catch (e: Exception) {
            // Any error during download
            e.printStackTrace()
            value = ImageLoadState.Error
        }
    }
    
    // Render based on current state
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (val state = imageState.value) {
            is ImageLoadState.Loading -> {
                // Show loading indicator
                LoadingPlaceholder(showLoadingText)
            }
            
            is ImageLoadState.Success -> {
                // Show the actual image
                Image(
                    bitmap = state.image,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale
                )
            }
            
            is ImageLoadState.Error -> {
                // Show error placeholder
                ErrorPlaceholder()
            }
        }
    }
}

/**
 * Loading state placeholder
 */
@Composable
private fun LoadingPlaceholder(showText: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (showText) {
            Text(
                text = "Loading...",
                fontSize = 12.sp,
                color = Color.Gray
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        }
    }
}

/**
 * Error state placeholder (default person icon)
 */
@Composable
private fun ErrorPlaceholder() {
    Icon(
        imageVector = Icons.Default.Person,
        contentDescription = "Placeholder",
        modifier = Modifier.size(40.dp),
        tint = Color.Gray
    )
}

/**
 * Sealed class representing image loading states
 * This is a common pattern in Android development for state management
 */
sealed class ImageLoadState {
    object Loading : ImageLoadState()
    data class Success(val image: ImageBitmap) : ImageLoadState()
    object Error : ImageLoadState()
}

/**
 * Alternative NetworkImage with custom placeholders
 * Allows you to provide custom composables for loading and error states
 */
@Composable
fun NetworkImageCustom(
    imageUrl: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    loadingContent: @Composable () -> Unit = { LoadingPlaceholder(false) },
    errorContent: @Composable () -> Unit = { ErrorPlaceholder() }
) {
    val fullUrl = ImageUrlHelper.getFullImageUrl(imageUrl)
    
    val imageState = produceState<ImageLoadState>(
        initialValue = ImageLoadState.Loading,
        key1 = fullUrl
    ) {
        value = ImageLoadState.Loading
        
        if (fullUrl.isNullOrBlank()) {
            value = ImageLoadState.Error
            return@produceState
        }
        
        try {
            val imageBitmap = ImageDownloader.downloadImage(fullUrl)
            value = if (imageBitmap != null) {
                ImageLoadState.Success(imageBitmap)
            } else {
                ImageLoadState.Error
            }
        } catch (e: Exception) {
            e.printStackTrace()
            value = ImageLoadState.Error
        }
    }
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (val state = imageState.value) {
            is ImageLoadState.Loading -> loadingContent()
            is ImageLoadState.Success -> {
                Image(
                    bitmap = state.image,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale
                )
            }
            is ImageLoadState.Error -> errorContent()
        }
    }
}

/**
 * NetworkImage with retry capability
 * Useful for unreliable network connections
 */
@Composable
fun NetworkImageWithRetry(
    imageUrl: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    retries: Int = 2
) {
    val fullUrl = ImageUrlHelper.getFullImageUrl(imageUrl)
    
    val imageState = produceState<ImageLoadState>(
        initialValue = ImageLoadState.Loading,
        key1 = fullUrl
    ) {
        value = ImageLoadState.Loading
        
        if (fullUrl.isNullOrBlank()) {
            value = ImageLoadState.Error
            return@produceState
        }
        
        try {
            // Use retry version of downloader
            val imageBitmap = ImageDownloader.downloadImageWithRetry(fullUrl, retries)
            value = if (imageBitmap != null) {
                ImageLoadState.Success(imageBitmap)
            } else {
                ImageLoadState.Error
            }
        } catch (e: Exception) {
            e.printStackTrace()
            value = ImageLoadState.Error
        }
    }
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (val state = imageState.value) {
            is ImageLoadState.Loading -> LoadingPlaceholder(false)
            is ImageLoadState.Success -> {
                Image(
                    bitmap = state.image,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale
                )
            }
            is ImageLoadState.Error -> ErrorPlaceholder()
        }
    }
}
