package com.blueduck.dajumgum

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import com.blueduck.dajumgum.onboarding.OnBoardingScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String,
    width: Int,
    bottomBarPadding: PaddingValues,
    bottomBarState: MutableState<Boolean>
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        builder = {
            onBoardingScreen(navController, bottomBarState)
            mainScreenScreen(navController, bottomBarPadding, bottomBarState)
            bookMarkScreen(navController, bottomBarState, bottomBarPadding)
            mealPlanScreen(navController, bottomBarPadding, bottomBarState)
            settingsScreen(navController, width, bottomBarPadding, bottomBarState)
        }
    )
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.onBoardingScreen(
    navController: NavController,
    bottomBarState: MutableState<Boolean>
) {
    composable(
        route = Navigate.Screen.OnBoardingWelcome.route
    ) {
        bottomBarState.value = false
        OnBoardingScreen {
            navController.navigate(Navigate.Screen.Main.route)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.mainScreenScreen(
    navController: NavController,
    bottomBarPadding: PaddingValues,
    bottomBarState: MutableState<Boolean>
) {
    composable(
        route = Navigate.Screen.Main.route
    ) {
        bottomBarState.value = true
        val systemUiController = rememberSystemUiController()
        SideEffect {
            systemUiController.setStatusBarColor(
                color = Color(0xFF2B292B),
                darkIcons = false
            )
            systemUiController.setNavigationBarColor(
                color = Color(0xFF2B292B),
                darkIcons = false
            )
        }
        val activity = (LocalContext.current as? Activity)

        BackHandler(true) {
            activity?.finish()
        }
        Discover(
            bottomBarPadding = bottomBarPadding,
            onDetails = {
                navController.navigate(Navigate.Screen.Details.route + "/$it")
            },
            onIngredients = {
                navController.navigate(Navigate.BottomSheet.Ingredients.route)
            }
        ) {
            navController.navigate(Navigate.Screen.SearchScreen.route + "/$it")
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.bookMarkScreen(
    navController: NavController,
    bottomBarState: MutableState<Boolean>,
    bottomBarPadding: PaddingValues
) {
    composable(
        route = Navigate.Screen.BookMark.route,
        enterTransition = {
            fadeIn(animationSpec = tween(200))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(200))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(200))
        }
    ) {
        bottomBarState.value = true

        BookMarkScreen(
            bottomBarPadding = bottomBarPadding
        ) {
            navController.navigate(Navigate.Screen.Details.route + "/$it")
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.mealPlanScreen(
    navController: NavController,
    bottomBarPadding: PaddingValues,
    bottomBarState: MutableState<Boolean>
) {
    composable(
        route = Navigate.Screen.MealPlan.route
    ) {
        bottomBarState.value = true
        MealPlanScreen(bottomBarPadding)
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.settingsScreen(
    navController: NavController,
    width: Int,
    bottomBarPadding: PaddingValues,
    bottomBarState: MutableState<Boolean>
) {
    composable(
        route = Navigate.Screen.Settings.route,
        exitTransition = {
            if (
                initialState.destination.hierarchy.any {
                    it.route == Navigate.Screen.Language.route || it.route == Navigate.Screen.Notifications.route
                }
            ) {
                slideOutHorizontally(
                    targetOffsetX = { -width },
                    animationSpec = tween(
                        durationMillis = 200,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(animationSpec = tween(200))
            } else {
                null
            }
        },
        popEnterTransition = {
            if (
                initialState.destination.hierarchy.any {
                    it.route == Navigate.Screen.Language.route || it.route == Navigate.Screen.Notifications.route
                }
            ) {
                slideInHorizontally(
                    initialOffsetX = { -width },
                    animationSpec = tween(
                        durationMillis = 200,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(animationSpec = tween(200))
            } else {
                null
            }
        },
    ) {
        bottomBarState.value = true
        SettingsScreen(
            navController= navController,
            bottomBarPadding = bottomBarPadding)
    }
}






@Composable
fun MealPlanScreen(
    bottomBarPadding: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(bottomBarPadding)
    ) {
        Text(text = "In Progress....", modifier = Modifier.align(Alignment.Center))
    }
}
