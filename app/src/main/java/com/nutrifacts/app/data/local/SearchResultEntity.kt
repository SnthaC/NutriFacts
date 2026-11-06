package com.nutrifacts.app.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "search_results",
    primaryKeys = ["query", "foodId"],
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
data class SearchResultEntity(
    val query: String,
    val foodId: String,
    val position: Int
)