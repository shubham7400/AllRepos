package com.blueduck.annotator.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.blueduck.annotator.screens.auth.SignInScreen


@Composable
fun AuthNavGraph(navController: NavHostController, isExpandedScreen: Boolean){
    NavHost(navController = navController, startDestination = Screen.Login.route){
        composable(route = Screen.Login.route){
            SignInScreen(isExpandedScreen)
        }
    }
}

