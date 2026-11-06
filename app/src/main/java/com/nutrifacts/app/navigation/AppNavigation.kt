package com.nutrifacts.app.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nutrifacts.app.ui.details.FoodDetailsScreen
import com.nutrifacts.app.ui.details.FoodDetailsViewModel
import com.nutrifacts.app.ui.search.FoodSearchScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SEARCH
    ) {
        composable(Routes.SEARCH) {
            FoodSearchScreen(
                onFoodClick = { food ->
                    navController.navigate("${Routes.DETAILS}/${food.id}")
                }
            )
        }

        composable(
            route = "${Routes.DETAILS}/{foodId}",
            arguments = listOf(
                navArgument("foodId") {
                    type = NavType.StringType
                }
            )
        ) {
            val viewModel: FoodDetailsViewModel = hiltViewModel()

            FoodDetailsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}