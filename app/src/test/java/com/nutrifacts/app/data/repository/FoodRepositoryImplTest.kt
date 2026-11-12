package com.nutrifacts.app.data.repository


import com.nutrifacts.app.data.local.FoodLocalDataSource
import com.nutrifacts.app.data.local.RecentSearchEntity
import com.nutrifacts.app.data.remote.FoodApi
import com.nutrifacts.app.data.remote.UsdaFoodNutrient
import com.nutrifacts.app.data.remote.UsdaFoodProduct
import com.nutrifacts.app.data.remote.UsdaSearchResponse
import com.nutrifacts.app.domain.model.Food
import com.nutrifacts.app.domain.model.FoodSearchError
import com.nutrifacts.app.domain.model.FoodSearchResult
import com.nutrifacts.app.domain.model.Nutrient
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class FoodRepositoryImplTest {

    private val foodApi = mockk<FoodApi>()
    private val localDataSource = mockk<FoodLocalDataSource>()

    private val repository = FoodRepositoryImpl(
        foodApi = foodApi,
        localDataSource = localDataSource
    )

    @Test
    fun searchFoods_whenCachedResultsExist_returnsCachedFoods() = runTest {
        val cachedFoods = listOf(
            Food(
                id = "123",
                name = "Walnuts",
                brand = null,
                nutrients = listOf(
                    Nutrient(
                        name = "Protein",
                        amount = 15.2,
                        unit = "g"
                    )
                )
            )
        )

        coEvery {
            localDataSource.getCachedSearchResults("walnuts")
        } returns cachedFoods

        coEvery {
            localDataSource.saveRecentSearch("walnuts")
        } returns Unit

        val result = repository.searchFoods("walnuts")

        assertEquals(
            FoodSearchResult.Success(cachedFoods),
            result
        )

        coVerify(exactly = 1) {
            localDataSource.getCachedSearchResults("walnuts")
        }

        coVerify(exactly = 1) {
            localDataSource.saveRecentSearch("walnuts")
        }

        coVerify(exactly = 0) {
            foodApi.searchFoods(
                query = any(),
                apiKey = any()
            )
        }
    }

    @Test
    fun searchFoods_trimsQueryBeforeCheckingCache() = runTest {

        coEvery {
            localDataSource.getCachedSearchResults("walnuts")
        } returns emptyList()

        coEvery {
            foodApi.searchFoods(
                query = "walnuts",
                apiKey = any()
            )
        } returns mockk {
            every { foods } returns emptyList()
        }

        coEvery {
            localDataSource.saveRecentSearch("walnuts")
        } returns Unit

        coEvery {
            localDataSource.saveSearchResults("walnuts", emptyList())
        } returns Unit

        repository.searchFoods(" walnuts ")

        coVerify(exactly = 1) {
            localDataSource.getCachedSearchResults("walnuts")
        }
    }

    @Test
    fun searchFoods_whenNetworkErrorOccurs_returnsNetworkFailure() = runTest {
        coEvery {
            localDataSource.getCachedSearchResults("walnuts")
        } returns emptyList()

        coEvery {
            foodApi.searchFoods(query = "walnuts", apiKey = any())
        } throws IOException("Network error")

        val result = repository.searchFoods("walnuts")

        assertEquals(
            FoodSearchResult.Failure(FoodSearchError.NETWORK),
            result
        )
    }

    @Test
    fun searchFoods_whenRateLimited_returnsRateLimitedFailure() = runTest {
        coEvery {
            localDataSource.getCachedSearchResults("walnuts")
        } returns emptyList()

        val response = Response.error<UsdaSearchResponse>(
            429,
            "Too Many Requests".toResponseBody()
        )

        coEvery {
            foodApi.searchFoods(query = "walnuts", apiKey = any())
        } throws HttpException(response)

        val result = repository.searchFoods("walnuts")

        assertEquals(
            FoodSearchResult.Failure(FoodSearchError.RATE_LIMITED),
            result
        )
    }

    @Test
    fun searchFoods_whenUnauthorized_returnsUnauthorizedFailure() = runTest {
        coEvery {
            localDataSource.getCachedSearchResults("walnuts")
        } returns emptyList()

        val response = Response.error<UsdaSearchResponse>(
            401,
            "Unauthorized".toResponseBody()
        )

        coEvery {
            foodApi.searchFoods(query = "walnuts", apiKey = any())
        } throws HttpException(response)

        val result = repository.searchFoods("walnuts")

        assertEquals(
            FoodSearchResult.Failure(FoodSearchError.UNAUTHORIZED),
            result
        )
    }

    @Test
    fun searchFoods_whenForbidden_returnsUnauthorizedFailure() = runTest {
        coEvery {
            localDataSource.getCachedSearchResults("walnuts")
        } returns emptyList()

        val response = Response.error<UsdaSearchResponse>(
            403,
            "Forbidden".toResponseBody()
        )

        coEvery {
            foodApi.searchFoods(query = "walnuts", apiKey = any())
        } throws HttpException(response)

        val result = repository.searchFoods("walnuts")

        assertEquals(
            FoodSearchResult.Failure(FoodSearchError.UNAUTHORIZED),
            result
        )
    }

    @Test
    fun searchFoods_whenUnexpectedErrorOccurs_returnsUnknownFailure() = runTest {
        coEvery {
            localDataSource.getCachedSearchResults("walnuts")
        } returns emptyList()

        coEvery {
            foodApi.searchFoods(query = "walnuts", apiKey = any())
        } throws IllegalStateException("Unexpected error")

        val result = repository.searchFoods("walnuts")

        assertEquals(
            FoodSearchResult.Failure(FoodSearchError.UNKNOWN),
            result
        )
    }

    @Test
    fun searchFoods_whenCancelled_rethrowsCancellationException() = runTest {
        coEvery {
            localDataSource.getCachedSearchResults("walnuts")
        } returns emptyList()

        coEvery {
            foodApi.searchFoods(query = "walnuts", apiKey = any())
        } throws CancellationException("Search cancelled")

        try {
            repository.searchFoods("walnuts")
            fail("Expected CancellationException to be rethrown")
        } catch (error: CancellationException) {
            assertEquals("Search cancelled", error.message)
        }
    }

    @Test
    fun searchFoods_whenCachingFails_stillReturnsSuccess() = runTest {
        coEvery {
            localDataSource.getCachedSearchResults("walnuts")
        } returns emptyList()

        val apiResponse = mockk<UsdaSearchResponse>()
        every { apiResponse.foods } returns emptyList()

        coEvery {
            foodApi.searchFoods(query = "walnuts", apiKey = any())
        } returns apiResponse

        coEvery {
            localDataSource.saveSearchResults("walnuts", emptyList())
        } throws IllegalStateException("Database error")

        val result = repository.searchFoods("walnuts")

        assertEquals(
            FoodSearchResult.Success(emptyList()),
            result
        )
    }

    @Test
    fun searchFoods_whenApiSucceeds_mapsAndSavesFoods() = runTest {
        coEvery {
            localDataSource.getCachedSearchResults("walnuts")
        } returns emptyList()

        val apiResponse = mockk<UsdaSearchResponse>()

        every { apiResponse.foods } returns listOf(
            UsdaFoodProduct(
                fdcId = 123L,
                description = "Walnuts",
                brandName = "Test Brand",
                brandOwner = null,
                foodNutrients = listOf(
                    UsdaFoodNutrient(
                        nutrientId = 100,
                        nutrientName = "Protein",
                        unitName = "g",
                        value = 15.2
                    )
                )
            )
        )

        coEvery {
            foodApi.searchFoods(query = "walnuts", apiKey = any())
        } returns apiResponse

        coEvery {
            localDataSource.saveFoodWithNutrients(any(), any())
        } just Runs

        coEvery {
            localDataSource.saveSearchResults("walnuts", listOf("123"))
        } just Runs

        coEvery {
            localDataSource.saveRecentSearch("walnuts")
        } just Runs

        val result = repository.searchFoods("walnuts")

        assertEquals(
            FoodSearchResult.Success(
                listOf(
                    Food(
                        id = "123",
                        name = "Walnuts",
                        brand = "Test Brand",
                        nutrients = listOf(
                            Nutrient(
                                name = "Protein",
                                amount = 15.2,
                                unit = "g"
                            )
                        )
                    )
                )
            ),
            result
        )

        coVerify(exactly = 1) {
            localDataSource.saveFoodWithNutrients(any(), any())
        }

        coVerify(exactly = 1) {
            localDataSource.saveSearchResults("walnuts", listOf("123"))
        }

        coVerify(exactly = 1) {
            localDataSource.saveRecentSearch("walnuts")
        }
    }

    @Test
    fun getCachedSearchResults_returnsResultsFromLocalDataSource() = runTest {
        val cachedFoods = listOf(
            Food(
                id = "123",
                name = "Walnuts",
                brand = null,
                nutrients = emptyList()
            )
        )

        coEvery {
            localDataSource.getCachedSearchResults("walnuts")
        } returns cachedFoods

        val result = repository.getCachedSearchResults("walnuts")

        assertEquals(cachedFoods, result)

        coVerify(exactly = 1) {
            localDataSource.getCachedSearchResults("walnuts")
        }
    }

    @Test
    fun getRecentSearches_returnsSearchQueriesFromLocalDataSource() = runTest {
        val recentSearches = listOf(
            RecentSearchEntity(query = "walnuts", searchedAt = 1000L),
            RecentSearchEntity(query = "almonds", searchedAt = 2000L),
            RecentSearchEntity(query = "oats", searchedAt = 3000L)
        )

        coEvery {
            localDataSource.getRecentSearches()
        } returns recentSearches

        val result = repository.getRecentSearches()

        assertEquals(
            listOf("walnuts", "almonds", "oats"),
            result
        )

        coVerify(exactly = 1) {
            localDataSource.getRecentSearches()
        }
    }

    @Test
    fun getFoodById_returnsFoodFromLocalDataSource() = runTest {
        val food = Food(
            id = "123",
            name = "Walnuts",
            brand = null,
            nutrients = emptyList()
        )

        coEvery {
            localDataSource.getFoodById("123")
        } returns food

        val result = repository.getFoodById("123")

        assertEquals(food, result)

        coVerify(exactly = 1) {
            localDataSource.getFoodById("123")
        }
    }
}
