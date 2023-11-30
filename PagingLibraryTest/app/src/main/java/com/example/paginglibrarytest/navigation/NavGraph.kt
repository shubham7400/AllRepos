package com.example.paginglibrarytest.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.paginglibrarytest.screens.home.HomeScreen
import com.example.paginglibrarytest.screens.search.SearchScreen

@Composable
fun SetupNavGraph( navController: NavHostController){
    NavHost(navController = navController, startDestination = Screen.Home.route){
        composable( route = Screen.Home.route ){
            HomeScreen( navController = navController)
        }
        composable( route = Screen.Search.route){
            SearchScreen( navController = navController)
        }
    }
}