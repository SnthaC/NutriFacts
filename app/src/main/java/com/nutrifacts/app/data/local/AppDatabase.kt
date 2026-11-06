package com.nutrifacts.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        FoodEntity::class,
        FoodNutrientEntity::class,
        RecentSearchEntity::class,
        SearchResultEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun recentSearchDao(): RecentSearchDao
}