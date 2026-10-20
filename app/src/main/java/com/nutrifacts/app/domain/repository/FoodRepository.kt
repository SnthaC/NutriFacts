package com.nutrifacts.app.domain.repository

import com.nutrifacts.app.domain.model.Food

interface FoodRepository {
    suspend fun searchFoods(query: String): Result<List<Food>>
}