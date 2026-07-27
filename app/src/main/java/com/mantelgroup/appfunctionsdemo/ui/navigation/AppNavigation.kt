package com.mantelgroup.appfunctionsdemo.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mantelgroup.appfunctionsdemo.ui.HomeScreen
import com.mantelgroup.appfunctionsdemo.ui.CartScreen

object Routes {
    const val HOME = "home"
    const val CART = "cart"
}

private const val DURATION = 350

private fun enterFromRight(): EnterTransition =
    fadeIn(tween(DURATION)) + slideInHorizontally(tween(DURATION)) { it / 4 }

private fun exitToLeft(): ExitTransition =
    fadeOut(tween(DURATION)) + slideOutHorizontally(tween(DURATION)) { -it / 4 }

private fun enterFromLeft(): EnterTransition =
    fadeIn(tween(DURATION)) + slideInHorizontally(tween(DURATION)) { -it / 4 }

private fun exitToRight(): ExitTransition =
    fadeOut(tween(DURATION)) + slideOutHorizontally(tween(DURATION)) { it / 4 }

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(
            route = Routes.HOME,
            enterTransition = { fadeIn(tween(DURATION)) },
            exitTransition = { exitToLeft() },
            popEnterTransition = { enterFromLeft() },
            popExitTransition = { exitToRight() },
        ) {
            HomeScreen(
                onNavigateToCart = { navController.navigate(Routes.CART) }
            )
        }
        composable(
            route = Routes.CART,
            enterTransition = { enterFromRight() },
            exitTransition = { exitToRight() },
            popEnterTransition = { enterFromLeft() },
            popExitTransition = { exitToRight() },
        ) {
            CartScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
