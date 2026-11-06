package com.nutrifacts.app.domain.model

data class Food(
    val id: String,
    val name: String,
    val brand: String?,
    val imageUrl: String?,
    val nutrients: List<Nutrient>
)

data class Nutrient(
    val name: String,
    val amount: Double?,
    val unit: String
)
