package com.example.jpsampledrawer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.rememberNavController
import com.example.jpsampledrawer.ui.theme.SampleDrawerTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleDrawerTheme {
                val navController = rememberNavController()
                val coroutineScope = rememberCoroutineScope()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

                val widthSizeClass = calculateWindowSizeClass(this).widthSizeClass
                val isExpandedScreen = widthSizeClass == WindowWidthSizeClass.Expanded
                val isMobile = resources.getBoolean(R.bool.isMobile)
                val deviceInfo = DeviceInfo(isExpanded = isExpandedScreen, isMobile = isMobile)

                SampleAppNavGraph(navController, coroutineScope, drawerState, deviceInfo)
            }
        }
    }
}

