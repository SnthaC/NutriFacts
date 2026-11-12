package com.nutrifacts.app.data.local

import com.nutrifacts.app.domain.model.Food
import com.nutrifacts.app.domain.model.Nutrient
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FoodLocalDataSourceTest {

    private val foodDao = mockk<FoodDao>()
    private val recentSearchDao = mockk<RecentSearchDao>()

    private lateinit var dataSource: FoodLocalDataSource

    @Before
    fun setUp() {
        dataSource = FoodLocalDataSource(foodDao, recentSearchDao)
    }

    @Test
    fun saveSearchResults_whenCalled_savesResultsInCorrectOrder() = runTest {
        val expectedResults = listOf(
            SearchResultEntity("apple", "1", 0),
            SearchResultEntity("apple", "2", 1)
        )

        coEvery {
            foodDao.replaceSearchResults("apple", expectedResults)
        } returns Unit

        dataSource.saveSearchResults("apple", listOf("1", "2"))

        coVerify(exactly = 1) {
            foodDao.replaceSearchResults("apple", expectedResults)
        }
    }

    @Test
    fun getCachedSearchResults_whenResultsExist_mapsFoodsAndSkipsMissingFoods() =
        runTest {
            val cachedFood = FoodWithNutrients(
                food = FoodEntity("1", "Apple", "Brand A"),
                nutrients = listOf(
                    FoodNutrientEntity(
                        foodId = "1",
                        name = "Energy",
                        amount = 52.0,
                        unit = "kcal"
                    )
                )
            )

            coEvery {
                foodDao.getSearchResultsForQuery("apple")
            } returns listOf(
                SearchResultEntity("apple", "1", 0),
                SearchResultEntity("apple", "2", 1)
            )

            coEvery {
                foodDao.getFoodWithNutrients("1")
            } returns cachedFood

            coEvery {
                foodDao.getFoodWithNutrients("2")
            } returns null

            val result = dataSource.getCachedSearchResults("apple")

            assertEquals(
                listOf(
                    Food(
                        id = "1",
                        name = "Apple",
                        brand = "Brand A",
                        nutrients = listOf(
                            Nutrient("Energy", 52.0, "kcal")
                        )
                    )
                ),
                result
            )
        }

    @Test
    fun saveRecentSearch_trimsQueryAndDeletesOlderSearches() = runTest {
        val searchSlot = slot<RecentSearchEntity>()

        coEvery {
            recentSearchDao.insertSearch(capture(searchSlot))
        } returns Unit

        coEvery {
            recentSearchDao.deleteOlderSearches()
        } returns Unit

        val before = System.currentTimeMillis()

        dataSource.saveRecentSearch("  apple  ")

        val after = System.currentTimeMillis()

        assertEquals("apple", searchSlot.captured.query)
        assertTrue(searchSlot.captured.searchedAt in before..after)

        coVerify(exactly = 1) {
            recentSearchDao.deleteOlderSearches()
        }
    }

    @Test
    fun getFoodById_whenFoodExists_mapsFoodAndNutrients() = runTest {
        val cachedFood = FoodWithNutrients(
            food = FoodEntity("1", "Apple", "Brand A"),
            nutrients = listOf(
                FoodNutrientEntity(
                    foodId = "1",
                    name = "Protein",
                    amount = 0.3,
                    unit = "g"
                )
            )
        )

        coEvery {
            foodDao.getFoodWithNutrients("1")
        } returns cachedFood

        val result = dataSource.getFoodById("1")

        assertEquals(
            Food(
                id = "1",
                name = "Apple",
                brand = "Brand A",
                nutrients = listOf(
                    Nutrient("Protein", 0.3, "g")
                )
            ),
            result
        )
    }

    @Test
    fun getFoodById_whenFoodDoesNotExist_returnsNull() = runTest {
        coEvery {
            foodDao.getFoodWithNutrients("missing")
        } returns null

        val result = dataSource.getFoodById("missing")

        assertNull(result)
    }
}
