package com.nutrifacts.app.data.repository


import com.nutrifacts.app.data.remote.FoodApi
import com.nutrifacts.app.domain.model.Food
import com.nutrifacts.app.domain.model.Nutrients
import com.nutrifacts.app.domain.repository.FoodRepository
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
    private val foodApi: FoodApi
) : FoodRepository {

    override suspend fun searchFoods(query: String): Result<List<Food>> =
        runCatching {
            val normalizedQuery = query.trim()

            foodApi.searchFoods(query = normalizedQuery)
                .products
                .filter { product ->
                    product.name.orEmpty().contains(
                        normalizedQuery,
                        ignoreCase = true
                    )
                }
                .mapNotNull { product ->
                    val name = product.name
                        ?.takeIf { it.isNotBlank() }
                        ?: return@mapNotNull null

                    Food(
                        id = product.code ?: name,
                        name = name,
                        brand = product.brands,
                        imageUrl = product.imageUrl,
                        nutrients = product.nutriments?.let {
                            Nutrients(
                                calories = it.calories,
                                protein = it.protein,
                                carbohydrates = it.carbohydrates,
                                fat = it.fat,
                                fiber = it.fiber,
                                sugars = it.sugars,
                                saturatedFat = it.saturatedFat,
                                salt = it.salt,
                                sodium = it.sodium,
                                vitaminA = it.vitaminA,
                                vitaminC = it.vitaminC,
                                vitaminD = it.vitaminD,
                                vitaminB12 = it.vitaminB12,
                                calcium = it.calcium,
                                iron = it.iron,
                                potassium = it.potassium,
                                magnesium = it.magnesium,
                                zinc = it.zinc
                            )
                        } ?: Nutrients(
                            calories = null,
                            protein = null,
                            carbohydrates = null,
                            fat = null,
                            fiber = null,
                            sugars = null,
                            saturatedFat = null,
                            salt = null,
                            sodium = null,
                            vitaminA = null,
                            vitaminC = null,
                            vitaminD = null,
                            vitaminB12 = null,
                            calcium = null,
                            iron = null,
                            potassium = null,
                            magnesium = null,
                            zinc = null
                        )
                    )
                }
        }
}