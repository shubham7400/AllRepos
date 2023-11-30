package com.blueduck.dajumgum.ui.bottombar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.blueduck.dajumgum.BottomBarNavGraph
import com.blueduck.dajumgum.R
import com.blueduck.dajumgum.enums.BottomBarScreen
import com.blueduck.dajumgum.ui.theme.DajumgumTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DajumgumTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ConfigureUi()
                }
            }
        }
    }
}


@Composable
fun ConfigureUi( ){

    val navController = rememberNavController()

    var bottomBarState by rememberSaveable { (mutableStateOf(true)) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    bottomBarState = navBackStackEntry?.destination?.route in BottomBarScreen.values().map { it.route }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (bottomBarState ){
                AppNavigationBar(navController)
            }
        }
    ){paddingValues ->
        BottomBarNavGraph(navController = navController, paddingValues)
    }

}


@Composable
fun AppNavigationBar(navController: NavHostController) {

    val tabs = listOf(NavigationBarScreen.Home, NavigationBarScreen.Inspection, NavigationBarScreen.Schedule, NavigationBarScreen.Chat)

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route


    NavigationBar(containerColor = MaterialTheme.colorScheme.primary, modifier = Modifier.height(60.dp)) {
        tabs.forEachIndexed { index, tab ->
            NavigationBarItem(
                selected = currentRoute == tab.route,
                onClick = {
                    navController.navigate(tab.route) {
                        launchSingleTop = true
                        popUpTo(navController.graph.findStartDestination().id){
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.White,
                    indicatorColor =  colorResource(id = R.color.orange),
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.White
                ),
                icon = {
                    when(tab.route){
                        tabs[0].route -> {
                             Icon(painter = painterResource(id = tab.iconResourceId), contentDescription = stringResource(id = tab.tabTextResourceId))
                        }
                        tabs[1].route -> {
                             Icon(painter = painterResource(id = tab.iconResourceId), contentDescription = stringResource(id = tab.tabTextResourceId))
                        }
                        tabs[2].route -> {
                             Icon(painter = painterResource(id = tab.iconResourceId), contentDescription = stringResource(id = tab.tabTextResourceId))
                        }
                        tabs[3].route -> {
                             Icon(painter = painterResource(id = tab.iconResourceId), contentDescription = stringResource(id = tab.tabTextResourceId))
                        }
                    }
                }
            )
        }
    }

}

