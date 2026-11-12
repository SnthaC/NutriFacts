package com.nutrifacts.app.ui.search

import com.nutrifacts.app.domain.model.Food
import com.nutrifacts.app.domain.model.FoodSearchResult
import com.nutrifacts.app.domain.repository.FoodRepository
import kotlinx.coroutines.CompletableDeferred

class FakeFoodRepository(
    private val foods: List<Food> = emptyList(),
    private val searchResults: List<FoodSearchResult>? = null,
    private val searchGate: CompletableDeferred<Unit>? = null
) : FoodRepository {

    private var searchCallCount = 0

    override suspend fun searchFoods(query: String): FoodSearchResult {
        searchGate?.await()

        return searchResults?.getOrNull(searchCallCount++)
            ?: FoodSearchResult.Success(foods)
    }

    override suspend fun getCachedSearchResults(query: String): List<Food> =
        foods

    override suspend fun getRecentSearches(): List<String> =
        emptyList()

    override suspend fun getFoodById(id: String): Food? =
        foods.find { it.id == id }
}