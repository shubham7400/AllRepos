package com.blueduck.dajumgum

 import android.app.Activity
 import android.content.Context
 import android.content.Intent
 import android.content.res.Configuration
 import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
 import androidx.compose.foundation.layout.fillMaxSize
 import androidx.compose.material3.*
 import androidx.compose.runtime.*
 import androidx.compose.ui.Modifier
 import androidx.compose.ui.platform.LocalContext
 import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
 import androidx.core.view.WindowCompat
 import androidx.navigation.compose.rememberNavController
 import com.blueduck.dajumgum.enums.Screen
 import com.blueduck.dajumgum.preferences.DataStoreManager
 import com.blueduck.dajumgum.ui.bottombar.HomeActivity
 import com.blueduck.dajumgum.ui.theme.DajumgumTheme
 import dagger.hilt.android.AndroidEntryPoint
 import kotlinx.coroutines.*
 import java.util.*


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        installSplashScreen().setKeepOnScreenCondition {
            keepSplashScreen
        }

        setContent {
            DajumgumTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val context = LocalContext.current
                    setDefaultLanguage(context)
                    StartApp {
                        keepSplashScreen = false
                    }
                }
            }
        }
    }


    // Sets the default language of the app to Korean.
    private fun setDefaultLanguage(context: Context) {
        val locale = Locale("ko")
        Locale.setDefault(locale)
        val res = context.resources
        val config = Configuration(res.configuration)
        config.locale = locale
        res.updateConfiguration(config, res.displayMetrics)
    }

}


@Composable
fun StartApp( onLauncherFinished : () -> Unit){

    val navController = rememberNavController()

    val activity = LocalContext.current as Activity

    // Accessing the login status
    val isLoggedIn by DataStoreManager.isLoggedIn(activity).collectAsState(initial = false)

    if (isLoggedIn) {
        activity.startActivity(Intent(activity, HomeActivity::class.java))
        activity.finish()
    } else {
        AuthNavGraph(navController, Screen.LoginScreen.route)
    }


    LaunchedEffect(Unit) {
        launch {
            delay(1000)
            onLauncherFinished()
        }
    }
}

