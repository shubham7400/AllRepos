package com.app.spacexapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.app.spacexapp.navigation.AppNavigation
import com.app.core.designsystem.theme.SpaceXTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        renderUi()
    }

    private fun renderUi() = setContent {
        SpaceXTheme {
            val navController = rememberNavController()
            AppNavigation(navController)
        }
    }
}