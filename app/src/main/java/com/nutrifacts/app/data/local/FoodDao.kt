package com.nutrifacts.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface FoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: FoodEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutrients(nutrients: List<FoodNutrientEntity>)

    @Transaction
    @Query("SELECT * FROM foods WHERE id = :foodId")
    suspend fun getFoodWithNutrients(foodId: String): FoodWithNutrients?

    @Transaction
    @Query("SELECT * FROM foods WHERE id = :foodId")
    suspend fun getFoodWithNutrientsById(
        foodId: String
    ): FoodWithNutrients?

    @Query("SELECT * FROM foods WHERE name LIKE '%' || :query || '%' ORDER BY name LIMIT 5")
    suspend fun getFoodsMatching(query: String): List<FoodEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchResults(results: List<SearchResultEntity>)

    @Query("SELECT * FROM search_results WHERE `query` = :query ORDER BY position ASC")
    suspend fun getSearchResultsForQuery(query: String): List<SearchResultEntity>

    @Query("SELECT * FROM foods WHERE id = :foodId")
    suspend fun getFoodById(foodId: String): FoodEntity?

    @Transaction
    suspend fun saveFoodWithNutrients(
        food: FoodEntity,
        nutrients: List<FoodNutrientEntity>
    ) {
        insertFood(food)
        insertNutrients(nutrients)
    }

    @Query("DELETE FROM search_results WHERE `query` = :query")
    suspend fun deleteSearchResultsForQuery(query: String)

    @Transaction
    suspend fun replaceSearchResults(
        query: String,
        results: List<SearchResultEntity>
    ) {
        deleteSearchResultsForQuery(query)
        insertSearchResults(results)
    }
}
