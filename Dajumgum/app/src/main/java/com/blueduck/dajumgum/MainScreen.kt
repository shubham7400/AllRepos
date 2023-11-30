package com.blueduck.dajumgum

 import androidx.compose.animation.ExperimentalAnimationApi
 import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
 import androidx.compose.runtime.collectAsState
 import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
 import com.blueduck.dajumgum.onboarding.OnBoardingViewModel
 import com.google.accompanist.navigation.animation.rememberAnimatedNavController
 import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
 import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
 import kotlinx.coroutines.CoroutineScope
 import kotlinx.coroutines.Dispatchers
 import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
@Composable
internal fun MainScreen( viewModel: OnBoardingViewModel, onLauncherFinished : () -> Unit) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberAnimatedNavController(bottomSheetNavigator)
    val bottomBarState = rememberSaveable { (mutableStateOf(false)) }

    val isOnBoardingDone = viewModel.isOnBoardingDone.collectAsState().value

    val waitUntilWeFetchIsOnBoardingDone = viewModel.waitUntilWeFetchIsOnBoardingDone.collectAsState().value

    if (waitUntilWeFetchIsOnBoardingDone){
        val screen = if (isOnBoardingDone){
            Navigate.Screen.Main.route
        }else{
            Navigate.Screen.OnBoardingWelcome.route
        }

        ModalBottomSheetLayout(
            bottomSheetNavigator = bottomSheetNavigator,
            sheetShape = BottomSheetShape,
            sheetBackgroundColor = Color.Transparent
        ) {
            Scaffold(
                backgroundColor = MaterialTheme.colors.surface,
                bottomBar = {
                    BottomBar(navController, bottomBarState)
                }
            ) {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()

                ) {
                    AppNavigation(
                        navController = navController,
                        startDestination = screen,
                        width = constraints.maxWidth / 2,
                        bottomBarPadding = it,
                        bottomBarState = bottomBarState
                    )
                }
            }
        }

        onLauncherFinished()
    }
}