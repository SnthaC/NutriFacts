package com.nutrifacts.app.ui.details

import com.nutrifacts.app.domain.model.Food

data class FoodDetailsUiState(
    val isLoading: Boolean = true,
    val food: Food? = null,
    val error: String? = null
)