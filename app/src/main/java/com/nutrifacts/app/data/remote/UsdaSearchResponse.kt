package com.nutrifacts.app.data.remote

data class UsdaSearchResponse(
    val foods: List<UsdaFoodProduct> = emptyList(),
    val totalHits: Int = 0
)

data class UsdaFoodProduct(
    val fdcId: Long,
    val description: String,
    val brandName: String? = null,
    val brandOwner: String? = null,
    val foodNutrients: List<UsdaFoodNutrient> = emptyList()
)

data class UsdaFoodNutrient(
    val nutrientId: Int? = null,
    val nutrientName: String? = null,
    val unitName: String? = null,
    val value: Double? = null
)
