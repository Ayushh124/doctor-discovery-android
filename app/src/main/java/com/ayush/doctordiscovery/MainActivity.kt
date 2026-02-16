package com.ayush.doctordiscovery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.ayush.doctordiscovery.navigation.NavGraph
import com.ayush.doctordiscovery.ui.theme.DoctorDiscoveryTheme

/**
 * Main Activity - Entry point of the app
 * 
 * Now includes navigation between:
 * - Doctor List Screen (shows all doctors)
 * - Doctor Detail Screen (shows full details + auto-increments search_count)
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoctorDiscoveryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Navigation Controller
                    val navController = rememberNavController()
                    
                    // Navigation Graph (handles screen transitions)
                    NavGraph(navController = navController)
                }
            }
        }
    }
}