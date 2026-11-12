package com.nutrifacts.app.data.local

import com.nutrifacts.app.domain.model.Food
import com.nutrifacts.app.domain.model.Nutrient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodLocalDataSource @Inject constructor(
    private val foodDao: FoodDao,
    private val recentSearchDao: RecentSearchDao
) {

    suspend fun getRecentSearches(): List<RecentSearchEntity> =
        recentSearchDao.getRecentSearches()

    suspend fun saveFoodWithNutrients(
        food: FoodEntity,
        nutrients: List<FoodNutrientEntity>
    ) {
        foodDao.saveFoodWithNutrients(food, nutrients)
    }

    suspend fun saveSearchResults(
        query: String,
        foodIds: List<String>
    ) {
        foodDao.replaceSearchResults(
            query = query,
            results = foodIds.mapIndexed { index, foodId ->
                SearchResultEntity(
                    query = query,
                    foodId = foodId,
                    position = index
                )
            }
        )
    }


    suspend fun getCachedSearchResults(
        query: String
    ): List<Food> {
        return foodDao.getSearchResultsForQuery(query).mapNotNull { result ->
            foodDao.getFoodWithNutrients(result.foodId)?.let { cached ->
                Food(
                    id = cached.food.id,
                    name = cached.food.name,
                    brand = cached.food.brand,
                    nutrients = cached.nutrients.map { nutrient ->
                        Nutrient(
                            name = nutrient.name,
                            amount = nutrient.amount,
                            unit = nutrient.unit
                        )
                    }
                )
            }
        }
    }

    suspend fun saveRecentSearch(query: String) {
        recentSearchDao.insertSearch(
            RecentSearchEntity(
                query = query.trim(),
                searchedAt = System.currentTimeMillis()
            )
        )
        recentSearchDao.deleteOlderSearches()
    }

    suspend fun getFoodById(id: String): Food? {
        val cached = foodDao.getFoodWithNutrients(id) ?: return null

        return Food(
            id = cached.food.id,
            name = cached.food.name,
            brand = cached.food.brand,
            nutrients = cached.nutrients.map { nutrient ->
                Nutrient(
                    name = nutrient.name,
                    amount = nutrient.amount,
                    unit = nutrient.unit
                )
            }
        )
    }
}