package com.blueduck.annotator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkManager
import com.blueduck.annotator.navigation.Screen
import com.blueduck.annotator.screens.AppDrawer
import com.blueduck.annotator.screens.AppNavigationActions
import com.blueduck.annotator.screens.bottombar.common.SearchScreen
import com.blueduck.annotator.screens.bottombar.home.FileDetailsScreen
import com.blueduck.annotator.screens.bottombar.home.FilesScreen
import com.blueduck.annotator.screens.bottombar.home.CloudProjectScreen
import com.blueduck.annotator.screens.bottombar.home.DriveProjectScreen
import com.blueduck.annotator.screens.bottombar.home.HomeViewModel
import com.blueduck.annotator.screens.bottombar.home.LocalProjectScreen
import com.blueduck.annotator.screens.bottombar.home.ProjectDetailScreen
import com.blueduck.annotator.screens.bottombar.home.SingleFileScreen
import com.blueduck.annotator.screens.bottombar.notification.NotificationScreen
import com.blueduck.annotator.screens.bottombar.shared.SharedScreen
import com.blueduck.annotator.ui.theme.MyAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            MyAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val coroutineScope = rememberCoroutineScope()
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

                    val widthSizeClass = calculateWindowSizeClass(this).widthSizeClass
                    val isExpandedScreen = widthSizeClass == WindowWidthSizeClass.Expanded
                    val isMobile = resources.getBoolean(R.bool.isMobile)
                    val deviceInfo = DeviceInfo(isExpanded = isExpandedScreen, isMobile = isMobile)

                    val viewModel: HomeViewModel = hiltViewModel()

                    AppNavGraph(navController, coroutineScope, drawerState, deviceInfo, viewModel)
                }
            }
        }
    }

    @Composable
    fun AppNavGraph(
        navController: NavHostController,
        coroutineScope: CoroutineScope,
        drawerState: DrawerState,
        deviceInfo: DeviceInfo,
        viewModel: HomeViewModel, ){

        val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute  = currentNavBackStackEntry?.destination?.route ?: Screen.CloudProject.route
        val navigationActions = remember(navController) {
            AppNavigationActions(navController)
        }

        var isDrawerOpen by remember { mutableStateOf(!deviceInfo.isMobile && deviceInfo.isExpanded) }


        Row(modifier = Modifier.fillMaxSize()) {
            println("fsdfdfdfd $currentRoute")
            if (deviceInfo.isMobile){
                ModalNavigationDrawer(
                    drawerContent = {
                    AppDrawer(
                        route = currentRoute,
                        navigateToLocalProject = { route -> navigationActions.navigateToLocalProject(route) },
                        navigateToCloudProject = { route -> navigationActions.navigateToCloudProject(route) },
                        navigateToDriveProject = { route -> navigationActions.navigateToDriveProject(route) },
                        navigateToSettings = { navigationActions.navigateToNotifications() },
                        navigateToShared = { navigationActions.navigateToShared() },
                        closeDrawer = { coroutineScope.launch { drawerState.close() } },
                        viewModel = viewModel
                    )
                }, drawerState = drawerState) {
                    ContentHolder(currentRoute, navController, deviceInfo,viewModel, openDrawer = { coroutineScope.launch { drawerState.open() } })
                }
            }else{
                if (isDrawerOpen){
                    AppDrawer(
                        route = currentRoute,
                        navigateToLocalProject = { route -> navigationActions.navigateToLocalProject(route) },
                        navigateToCloudProject = { route -> navigationActions.navigateToCloudProject(route) },
                        navigateToDriveProject = { route -> navigationActions.navigateToDriveProject(route) },
                        navigateToSettings = { navigationActions.navigateToNotifications() },
                        navigateToShared = { navigationActions.navigateToShared() },
                        closeDrawer = { coroutineScope.launch { drawerState.close() } },
                        viewModel = viewModel
                    )
                }
                ContentHolder(currentRoute, navController, deviceInfo, viewModel, openDrawer = {
                    isDrawerOpen = !isDrawerOpen
                })
            }
        }


     }



 }


@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ContentHolder(
    currentRoute: String,
    navController: NavHostController,
    deviceInfo: DeviceInfo,
    viewModel: HomeViewModel,
    openDrawer: () -> Unit = {}
) {
    Scaffold {
        NavHost(
            navController = navController,
            startDestination = Screen.CloudProject.route,
            modifier = Modifier.padding(it)
        ) {

            composable(Screen.LocalProject.route) {
                LocalProjectScreen(navController = navController, viewModel = viewModel, deviceInfo = deviceInfo){
                    openDrawer()
                }
            }
            composable(Screen.CloudProject.route) {
                CloudProjectScreen(
                    navController = navController,
                    viewModel = viewModel,
                    deviceInfo = deviceInfo
                ){
                    openDrawer()
                }
            }
            composable(Screen.DriveProject.route) {
                DriveProjectScreen(
                    navController = navController,
                    viewModel = viewModel,
                    deviceInfo = deviceInfo
                ){
                    openDrawer()
                }
            }

            composable(Screen.Notification.route) {
                NotificationScreen(navController = navController){
                    openDrawer()
                }
            }

            composable(Screen.Shared.route) {
                SharedScreen(navController = navController){
                    openDrawer()
                }
            }

            composable(Screen.Search.route) {
                SearchScreen(navController = navController)
            }

            composable(Screen.Files.route) {
                FilesScreen(navController = navController, viewModel, deviceInfo)
            }

            composable(Screen.SingleFile.route) {
                SingleFileScreen(navController = navController, viewModel, deviceInfo)
            }

            composable(Screen.ProjectDetail.route) {
                ProjectDetailScreen(navController = navController, viewModel)
            }

            composable(Screen.FileDetail.route) {
                FileDetailsScreen(navController = navController, viewModel)
            }
        }
    }
}

data class DeviceInfo(
    val isExpanded: Boolean,
    val isMobile: Boolean
)