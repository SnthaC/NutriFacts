package com.nutrifacts.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val brand: String?
)