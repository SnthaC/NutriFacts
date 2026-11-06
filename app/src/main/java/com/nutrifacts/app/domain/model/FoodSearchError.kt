package com.nutrifacts.app.domain.model

enum class FoodSearchError {
    RATE_LIMITED,
    UNAUTHORIZED,
    NETWORK,
    UNKNOWN
}

sealed interface FoodSearchResult {
    data class Success(val foods: List<Food>) : FoodSearchResult
    data class Failure(val error: FoodSearchError) : FoodSearchResult
}