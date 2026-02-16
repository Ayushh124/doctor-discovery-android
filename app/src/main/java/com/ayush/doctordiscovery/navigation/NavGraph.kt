package com.ayush.doctordiscovery.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ayush.doctordiscovery.ui.screens.DoctorDetailScreen
import com.ayush.doctordiscovery.ui.screens.DoctorListScreen
import com.ayush.doctordiscovery.ui.screens.DoctorRegistrationScreen

/**
 * Navigation routes for the app
 */
sealed class Screen(val route: String) {
    object DoctorList : Screen("doctor_list")
    object DoctorDetail : Screen("doctor_detail/{doctorId}") {
        fun createRoute(doctorId: Int) = "doctor_detail/$doctorId"
    }
    object DoctorRegistration : Screen("doctor_registration")
}

/**
 * Navigation graph
 * Defines all screens and navigation between them
 */
@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.DoctorList.route
    ) {
        // Doctor List Screen (Home)
        composable(route = Screen.DoctorList.route) {
            DoctorListScreen(
                onDoctorClick = { doctorId ->
                    // Navigate to detail screen with doctor ID
                    navController.navigate(Screen.DoctorDetail.createRoute(doctorId))
                },
                onRegisterClick = {
                    // Navigate to registration screen
                    navController.navigate(Screen.DoctorRegistration.route)
                }
            )
        }
        
        // Doctor Detail Screen
        composable(
            route = Screen.DoctorDetail.route,
            arguments = listOf(
                navArgument("doctorId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            // Extract doctorId from navigation arguments
            val doctorId = backStackEntry.arguments?.getInt("doctorId") ?: 0
            
            DoctorDetailScreen(
                doctorId = doctorId,
                onBackClick = {
                    // Go back to list screen
                    navController.popBackStack()
                }
            )
        }
        
        // Doctor Registration Screen
        composable(route = Screen.DoctorRegistration.route) {
            DoctorRegistrationScreen(
                onBackClick = {
                    // Go back to list screen
                    navController.popBackStack()
                },
                onRegistrationSuccess = {
                    // After successful registration, go back to list
                    navController.popBackStack()
                }
            )
        }
    }
}
