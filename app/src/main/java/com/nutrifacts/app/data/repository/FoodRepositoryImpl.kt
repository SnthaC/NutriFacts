package com.nutrifacts.app.data.repository

import com.nutrifacts.app.BuildConfig
import com.nutrifacts.app.data.local.FoodEntity
import com.nutrifacts.app.data.local.FoodLocalDataSource
import com.nutrifacts.app.data.local.FoodNutrientEntity
import com.nutrifacts.app.data.remote.FoodApi
import com.nutrifacts.app.domain.model.Food
import com.nutrifacts.app.domain.model.FoodSearchError
import com.nutrifacts.app.domain.model.FoodSearchResult
import com.nutrifacts.app.domain.model.Nutrient
import com.nutrifacts.app.domain.repository.FoodRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
    private val foodApi: FoodApi,
    private val localDataSource: FoodLocalDataSource
) : FoodRepository {

    override suspend fun searchFoods(query: String): FoodSearchResult {
        val normalizedQuery = query.trim()

        val cachedFoods = localDataSource.getCachedSearchResults(normalizedQuery)

        if (cachedFoods.isNotEmpty()) {
            localDataSource.saveRecentSearch(normalizedQuery)
            return FoodSearchResult.Success(cachedFoods)
        }

        return try {
            val response = foodApi.searchFoods(
                query = normalizedQuery,
                apiKey = BuildConfig.USDA_API_KEY
            )

            val foods = response.foods
                .filter {
                    it.description
                        .contains(normalizedQuery, ignoreCase = true)
                }
                .mapNotNull { food ->
                    val name = food.description.takeIf { it.isNotBlank() }
                        ?: return@mapNotNull null

                    Food(
                        id = food.fdcId.toString(),
                        name = name,
                        brand = food.brandName ?: food.brandOwner,
                        nutrients = food.foodNutrients.map { nutrient ->
                            Nutrient(
                                name = nutrient.nutrientName.orEmpty(),
                                amount = nutrient.value,
                                unit = nutrient.unitName.orEmpty()
                            )
                        }
                    )
                }

            try {
                foods.forEach { food ->
                    localDataSource.saveFoodWithNutrients(
                        food = FoodEntity(
                            id = food.id,
                            name = food.name,
                            brand = food.brand
                        ),
                        nutrients = food.nutrients.map { nutrient ->
                            FoodNutrientEntity(
                                foodId = food.id,
                                name = nutrient.name,
                                amount = nutrient.amount,
                                unit = nutrient.unit
                            )
                        }
                    )
                }

                localDataSource.saveSearchResults(
                    query = normalizedQuery,
                    foodIds = foods.map { it.id }
                )

                localDataSource.saveRecentSearch(normalizedQuery)

            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                // Cache failures should not prevent returning successful API results.
            }

            FoodSearchResult.Success(foods)

        } catch (error: CancellationException) {
            throw error
        } catch (error: retrofit2.HttpException) {

            val searchError = when (error.code()) {
                429 -> FoodSearchError.RATE_LIMITED
                401, 403 -> FoodSearchError.UNAUTHORIZED
                else -> FoodSearchError.UNKNOWN
            }

            FoodSearchResult.Failure(searchError)
        } catch (error: java.io.IOException) {
            FoodSearchResult.Failure(FoodSearchError.NETWORK)
        } catch (error: Exception) {

            FoodSearchResult.Failure(FoodSearchError.UNKNOWN)
        }
    }

    override suspend fun getCachedSearchResults(query: String): List<Food> {
        return localDataSource.getCachedSearchResults(query.trim())
    }

    override suspend fun getRecentSearches(): List<String> {
        return localDataSource.getRecentSearches().map { it.query }
    }

    override suspend fun getFoodById(id: String): Food? {
        return localDataSource.getFoodById(id)
    }
}
