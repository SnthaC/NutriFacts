package com.nutrifacts.app.domain.model

data class Food(
    val id: String,
    val name: String,
    val brand: String?,
    val imageUrl: String?,
    val nutrients: Nutrients
)

data class Nutrients(
    val calories: Double?,
    val protein: Double?,
    val carbohydrates: Double?,
    val fat: Double?,
    val fiber: Double?,
    val sugars: Double?,
    val saturatedFat: Double?,
    val salt: Double?,
    val sodium: Double?,
    val vitaminA: Double?,
    val vitaminC: Double?,
    val vitaminD: Double?,
    val vitaminB12: Double?,
    val calcium: Double?,
    val iron: Double?,
    val potassium: Double?,
    val magnesium: Double?,
    val zinc: Double?
)
