package com.blueduck.annotator.screens.auth


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.blueduck.annotator.HomeActivity
import com.blueduck.annotator.navigation.AuthNavGraph
import com.blueduck.annotator.preferences.getUser
import com.blueduck.annotator.ui.theme.MyAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MyAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val widthSizeClass = calculateWindowSizeClass(this).widthSizeClass
                    val isExpandedScreen = widthSizeClass == WindowWidthSizeClass.Expanded

                    val navController = rememberNavController()
                    val context = LocalContext.current
                    if (context.getUser() != null) {
                        context.startActivity(Intent(context, HomeActivity::class.java))
                        (context as MainActivity).finishAffinity()
                    }else{
                        AuthNavGraph(navController = navController, isExpandedScreen)
                    }
                }
            }
        }

      }
}