package com.nutrifacts.app.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "food_nutrients",
    foreignKeys = [
        ForeignKey(
            entity = FoodEntity::class,
            parentColumns = ["id"],
            childColumns = ["foodId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("foodId")]
)
data class FoodNutrientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val foodId: String,
    val name: String,
    val amount: Double?,
    val unit: String
)