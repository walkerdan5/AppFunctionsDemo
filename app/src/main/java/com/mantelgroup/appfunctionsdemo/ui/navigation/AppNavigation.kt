package com.mantelgroup.appfunctionsdemo.ui.navigation

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

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToCart = { navController.navigate(Routes.CART) }
            )
        }
        composable(Routes.CART) {
            CartScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
