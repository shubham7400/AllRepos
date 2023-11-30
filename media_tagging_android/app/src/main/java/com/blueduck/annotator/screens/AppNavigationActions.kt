package com.blueduck.annotator.screens

import androidx.navigation.NavHostController
import com.blueduck.annotator.navigation.Screen

class AppNavigationActions(private val navController: NavHostController) {

    fun navigateToLocalProject(route: String) {
        navController.navigate(route) {
            popUpTo(0){
                inclusive = true
            }
        }
    }

    fun navigateToCloudProject(route: String) {
        navController.navigate(route) {
            popUpTo(0){
                inclusive = true
            }
        }
    }

    fun navigateToDriveProject(route: String) {
        navController.navigate(route) {
            popUpTo(0){
                inclusive = true
            }
        }
    }

    fun navigateToNotifications() {
        navController.navigate(Screen.Notification.route) {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToShared() {
        navController.navigate(Screen.Shared.route) {
            launchSingleTop = true
            restoreState = true
        }
    }


}