package com.nutrifacts.app.data.local

import androidx.room.Relation

data class FoodWithNutrients(
    @androidx.room.Embedded
    val food: FoodEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "foodId"
    )
    val nutrients: List<FoodNutrientEntity>
)
