package com.blueduck.dajumgum

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.blueduck.dajumgum.enums.BottomBarScreen
import com.blueduck.dajumgum.enums.Screen
import com.blueduck.dajumgum.ui.auth.AuthViewModel
import com.blueduck.dajumgum.ui.auth.LoginScreen
import com.blueduck.dajumgum.ui.auth.SignupScreen
import com.blueduck.dajumgum.ui.bottombar.HomeViewModel
import com.blueduck.dajumgum.ui.bottombar.chat.ChatScreen
import com.blueduck.dajumgum.ui.bottombar.home.HomeScreen
import com.blueduck.dajumgum.ui.bottombar.inspection.AddKeywordScreen
import com.blueduck.dajumgum.ui.bottombar.inspection.CreateAirConditionerDefectScreen
import com.blueduck.dajumgum.ui.bottombar.inspection.CreateCustomerScreen
import com.blueduck.dajumgum.ui.bottombar.inspection.CreateInspectionDefectScreen
import com.blueduck.dajumgum.ui.bottombar.inspection.CreateTemperatureDefectScreen
import com.blueduck.dajumgum.ui.bottombar.inspection.DefectListScreen
import com.blueduck.dajumgum.ui.bottombar.inspection.InspectionReportScreen
import com.blueduck.dajumgum.ui.bottombar.inspection.InspectionScreen
import com.blueduck.dajumgum.ui.bottombar.inspection.PdfPreviewScreen
import com.blueduck.dajumgum.ui.bottombar.schedule.ScheduleScreen

/**
 * Composable function that sets up the navigation for the app using Jetpack Navigation.
 * @param navController The NavHostController responsible for handling the navigation.
 * @param startDestination The route of the starting destination.
 */

@Composable
fun AuthNavGraph(navController: NavHostController, startDestination: String,) {
    // Retrieve the view model using Hilt dependency injection
    val viewModel: AuthViewModel = hiltViewModel()

    // Set up the navigation routes and associated composables using NavHost
    NavHost(navController = navController, startDestination = startDestination) {

        // Define composable for each screen using the associated route
        composable( Screen.LoginScreen.route) { LoginScreen(navController, viewModel) }
        composable( Screen.SignupScreen.route) { SignupScreen(navController, viewModel) }
    }
}

@Composable
fun BottomBarNavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues
){
    // Retrieve the view model using Hilt dependency injection
    val viewModel: HomeViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = BottomBarScreen.Inspection.route, modifier = Modifier.padding(paddingValues)) {
        composable(route = BottomBarScreen.Home.route){
            HomeScreen(navController , viewModel)
        }
        composable(route = BottomBarScreen.Inspection.route){
            InspectionScreen(navController, viewModel )
        }
        composable(route = BottomBarScreen.Schedule.route){
            ScheduleScreen(navController, viewModel)
        }
        composable(route = BottomBarScreen.Chat.route){
            ChatScreen(navController, viewModel)
        }

        composable(route = Screen.CreateCustomer.route){
            CreateCustomerScreen(navController, viewModel)
        }
        composable(route = Screen.AddKeyword.route){
            AddKeywordScreen(navController, viewModel)
        }
        composable(route = Screen.DefectList.route){
            DefectListScreen(navController, viewModel)
        }
        composable(route = Screen.CreateInspectionDefect.route){
            CreateInspectionDefectScreen(navController, viewModel)
        }
        composable(route = Screen.CreateTemperatureDefect.route){
            CreateTemperatureDefectScreen(navController, viewModel)
        }
        composable(route = Screen.CreateAirConditionerDefect.route){
            CreateAirConditionerDefectScreen(navController, viewModel)
        }
        composable(route = Screen.InspectionReport.route){
            InspectionReportScreen(navController, viewModel)
        }
        composable(route = Screen.PdfView.route){
            PdfPreviewScreen(navController, viewModel)
        }
    }
}