package com.nutrifacts.app.navigation


import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nutrifacts.app.domain.model.Food
import com.nutrifacts.app.ui.details.FoodDetailsScreen
import com.nutrifacts.app.ui.search.FoodSearchScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var selectedFood by remember { mutableStateOf<Food?>(null) }

    NavHost(
        navController = navController,
        startDestination = Routes.SEARCH
    ) {
        composable( Routes.SEARCH) {
            FoodSearchScreen(
                onFoodClick = { food ->
                    selectedFood = food
                    navController.navigate(Routes.DETAILS)
                }
            )
        }

        composable(Routes.DETAILS) {
            selectedFood?.let { food ->
                FoodDetailsScreen(
                    food = food,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}