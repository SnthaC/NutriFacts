package com.nutrifacts.app.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class UsdaSearchResponseTest {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val adapter = moshi.adapter(UsdaSearchResponse::class.java)

    @Test
    fun fromJson_withCompleteResponse_mapsCorrectly() {
        val json = """
            {
                "foods": [
                    {
                        "fdcId": 12345,
                        "description": "Apple, raw",
                        "brandName": "Example Brand",
                        "brandOwner": "Example Owner",
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

        val result = adapter.fromJson(json)

        assertNotNull(result)
        assertEquals(1, result?.totalHits)
        assertEquals(12345L, result?.foods?.first()?.fdcId)
        assertEquals("Apple, raw", result?.foods?.first()?.description)
        assertEquals("Example Brand", result?.foods?.first()?.brandName)
        assertEquals(
            52.0, result?.foods?.first()
                ?.foodNutrients?.first()?.value
        )
    }

    @Test
    fun fromJson_withMissingOptionalFields_usesDefaults() {
        val json = """
            {
                "foods": [
                    {
                        "fdcId": 12345,
                        "description": "Apple, raw"
                    }
                ]
            }
        """.trimIndent()

        val result = adapter.fromJson(json)

        assertNotNull(result)
        assertEquals(0, result?.totalHits)
        assertEquals(null, result?.foods?.first()?.brandName)
        assertEquals(null, result?.foods?.first()?.brandOwner)
        assertEquals(
            emptyList<UsdaFoodNutrient>(),
            result?.foods?.first()?.foodNutrients
        )
    }
}