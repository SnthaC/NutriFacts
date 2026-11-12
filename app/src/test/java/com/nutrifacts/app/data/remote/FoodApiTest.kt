package com.nutrifacts.app.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class FoodApiTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var foodApi: FoodApi

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        foodApi = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(FoodApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun searchFoods_sendsCorrectRequestAndParsesResponse() {
        val json = """
            {
                "foods": [
                    {
                        "fdcId": 12345,
                        "description": "Apple, raw",
                        "brandName": "Example Brand",
                        "foodNutrients": [
                            {
                                "nutrientId": 1008,
                                "nutrientName": "Energy",
                                "unitName": "KCAL",
                                "value": 52.0
                            }
                        ]
                    }
                ],
                "totalHits": 1
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(json)
        )

        val result = kotlinx.coroutines.runBlocking {
            foodApi.searchFoods(
                query = "apple",
                apiKey = "test-api-key"
            )
        }

        val request = mockWebServer.takeRequest()

        assertEquals("GET", request.method)
        assertEquals("/foods/search", request.requestUrl?.encodedPath)
        assertEquals("apple", request.requestUrl?.queryParameter("query"))
        assertEquals("20", request.requestUrl?.queryParameter("pageSize"))
        assertEquals("test-api-key", request.requestUrl?.queryParameter("api_key"))

        assertEquals(1, result.totalHits)
        assertEquals("Apple, raw", result.foods.first().description)
        assertEquals(52.0, result.foods.first().foodNutrients.first().value!!, 0.001)
    }
}
