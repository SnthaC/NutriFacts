package com.nutrifacts.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface FoodApi {

    @GET("foods/search")
    suspend fun searchFoods(
        @Query("query") query: String,
        @Query("pageSize") pageSize: Int = 20,
        @Query("api_key") apiKey: String
    ): UsdaSearchResponse
}