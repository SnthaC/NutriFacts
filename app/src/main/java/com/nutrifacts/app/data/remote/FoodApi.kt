package com.nutrifacts.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface FoodApi {

    @GET("cgi/search.pl")
    suspend fun searchFoods(
        @Query("search_terms") query: String,
        @Query("search_simple") searchSimple: Int = 1,
        @Query("action") action: String = "process",
        @Query("json") json: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("fields")
        fields: String =
            "code,product_name,brands,image_front_small_url,nutriments"
    ): FoodSearchResponse
}