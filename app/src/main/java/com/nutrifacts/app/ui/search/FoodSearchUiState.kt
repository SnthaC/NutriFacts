package com.nutrifacts.app.ui.search

import com.nutrifacts.app.domain.model.Food

data class FoodSearchUiState(
    val foods: List<Food> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val searchedQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val canRetry: Boolean = false
)