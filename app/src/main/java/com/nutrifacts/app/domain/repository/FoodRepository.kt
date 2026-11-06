package com.nutrifacts.app.domain.repository

import com.nutrifacts.app.domain.model.Food
import com.nutrifacts.app.domain.model.FoodSearchResult

interface FoodRepository {

    suspend fun searchFoods(query: String): FoodSearchResult

    suspend fun getCachedSearchResults(query: String): List<Food>

    suspend fun getRecentSearches(): List<String>

    suspend fun getFoodById(id: String): Food?
}