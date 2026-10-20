package com.nutrifacts.app.data.remote

import com.squareup.moshi.Json

data class FoodSearchResponse(
    val count: Int = 0,
    val products: List<FoodProduct> = emptyList()
)

data class FoodProduct(
    val code: String? = null,

    @Json(name = "product_name")
    val name: String? = null,

    val brands: String? = null,

    @Json(name = "image_front_small_url")
    val imageUrl: String? = null,

    val nutriments: FoodNutrients? = null
)

data class FoodNutrients(
    @Json(name = "energy-kcal_100g")
    val calories: Double? = null,

    @Json(name = "proteins_100g")
    val protein: Double? = null,

    @Json(name = "carbohydrates_100g")
    val carbohydrates: Double? = null,

    @Json(name = "fat_100g")
    val fat: Double? = null,

    @Json(name = "fiber_100g")
    val fiber: Double? = null,

    @Json(name = "sugars_100g")
    val sugars: Double? = null,

    @Json(name = "saturated-fat_100g")
    val saturatedFat: Double? = null,

    @Json(name = "salt_100g")
    val salt: Double? = null,

    @Json(name = "sodium_100g")
    val sodium: Double? = null,

    @Json(name = "vitamin-a_100g")
    val vitaminA: Double? = null,

    @Json(name = "vitamin-c_100g")
    val vitaminC: Double? = null,

    @Json(name = "vitamin-d_100g")
    val vitaminD: Double? = null,

    @Json(name = "vitamin-b12_100g")
    val vitaminB12: Double? = null,

    @Json(name = "calcium_100g")
    val calcium: Double? = null,

    @Json(name = "iron_100g")
    val iron: Double? = null,

    @Json(name = "potassium_100g")
    val potassium: Double? = null,

    @Json(name = "magnesium_100g")
    val magnesium: Double? = null,

    @Json(name = "zinc_100g")
    val zinc: Double? = null
)