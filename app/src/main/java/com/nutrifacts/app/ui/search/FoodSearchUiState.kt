package com.nutrifacts.app.ui.search


import com.nutrifacts.app.domain.model.Food

data class FoodSearchUiState(
    val foods: List<Food> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)