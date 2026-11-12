package com.nutrifacts.app.ui.details

import com.nutrifacts.app.domain.model.Food
import com.nutrifacts.app.domain.model.FoodSearchResult
import com.nutrifacts.app.domain.repository.FoodRepository

class FakeFoodDetailsRepository(
    private val food: Food? = null,
    private val loadFood: (suspend (String) -> Food?)? = null
) : FoodRepository {

    override suspend fun searchFoods(query: String): FoodSearchResult =
        FoodSearchResult.Success(emptyList())

    override suspend fun getCachedSearchResults(query: String): List<Food> =
        emptyList()

    override suspend fun getRecentSearches(): List<String> =
        emptyList()

    override suspend fun getFoodById(id: String): Food? =
        loadFood?.invoke(id) ?: food
}