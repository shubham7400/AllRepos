package com.example.jpsampledrawer

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.jpsampledrawer.home.HomeScreen
import com.example.jpsampledrawer.settings.SettingsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleAppNavGraph(
    navController: NavHostController,
    coroutineScope: CoroutineScope,
    drawerState: DrawerState,
    deviceInfo: DeviceInfo,
) {

    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: AllDestinations.HOME
    val navigationActions = remember(navController) {
        AppNavigationActions(navController)
    }

    var isDrawerOpen by rememberSaveable { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxSize()) {
        if (deviceInfo.isMobile){
            ModalNavigationDrawer(drawerContent = {
                AppDrawer(
                    route = currentRoute,
                    navigateToHome = { navigationActions.navigateToHome() },
                    navigateToSettings = { navigationActions.navigateToSettings() },
                    closeDrawer = { coroutineScope.launch { drawerState.close() } },
                    modifier = Modifier
                )
            }, drawerState = drawerState) {
                ContentHolder(currentRoute, navController, deviceInfo, openDrawer = { coroutineScope.launch { drawerState.open() } })
            }
        }else{
            if (deviceInfo.isExpanded){
                AppDrawer(
                    route = currentRoute,
                    navigateToHome = { navigationActions.navigateToHome() },
                    navigateToSettings = { navigationActions.navigateToSettings() },
                    closeDrawer = { coroutineScope.launch { drawerState.close() } },
                    modifier = Modifier
                )
            }else{
                if (isDrawerOpen){
                    AppDrawer(
                        route = currentRoute,
                        navigateToHome = { navigationActions.navigateToHome() },
                        navigateToSettings = { navigationActions.navigateToSettings() },
                        closeDrawer = { coroutineScope.launch { drawerState.close() } },
                        modifier = Modifier
                    )
                }
            }
            ContentHolder(currentRoute, navController, deviceInfo, openDrawer = {
                isDrawerOpen = !isDrawerOpen
            })
        }
    }
 }

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ContentHolder(
    currentRoute: String,
    navController: NavHostController,
    deviceInfo: DeviceInfo,
    openDrawer: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = currentRoute) },
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                navigationIcon = {
                    if (!deviceInfo.isExpanded || deviceInfo.isMobile){
                        IconButton(
                            onClick = { openDrawer() },
                            content = { Icon(imageVector = Icons.Default.Menu, contentDescription = null) }
                        )
                    }
                }
            )
        }, modifier = Modifier
    ) {
        NavHost(
            navController = navController,
            startDestination = AllDestinations.HOME,
            modifier = Modifier.padding(it)
        ) {

            composable(AllDestinations.HOME) {
                HomeScreen()
            }

            composable(AllDestinations.SETTINGS) {
                SettingsScreen()
            }
        }
    }
}

data class DeviceInfo(
    val isExpanded: Boolean,
    val isMobile: Boolean
)