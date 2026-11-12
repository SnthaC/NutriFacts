package com.nutrifacts.app.data.local

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FoodDaoTest : DatabaseTest() {

    private fun createFood(
        id: String,
        name: String
    ) = FoodEntity(
        id = id,
        name = name,
        brand = "Test Brand"
    )

    @Test
    fun insertFood_whenCalled_foodCanBeRetrievedById() = runTest {
        val food = createFood("1", "Apple")

        foodDao.insertFood(food)

        assertEquals(food, foodDao.getFoodById("1"))
    }

    @Test
    fun getFoodById_whenFoodDoesNotExist_returnsNull() = runTest {
        assertNull(foodDao.getFoodById("unknown"))
    }

    @Test
    fun getFoodsMatching_returnsMatchingFoodsAlphabeticallyWithLimitOfFive() = runTest {
        val foods = listOf(
            createFood("1", "Apple Juice"),
            createFood("2", "Apple Pie"),
            createFood("3", "Apple Sauce"),
            createFood("4", "Apple Tart"),
            createFood("5", "Apple Cake"),
            createFood("6", "Apple Crumble"),
            createFood("7", "Banana")
        )

        foods.forEach { foodDao.insertFood(it) }

        val result = foodDao.getFoodsMatching("Apple")

        assertEquals(5, result.size)
        assertEquals(
            listOf("Apple Cake", "Apple Crumble", "Apple Juice", "Apple Pie", "Apple Sauce"),
            result.map { it.name }
        )
    }

    @Test
    fun getSearchResultsForQuery_returnsResultsInPositionOrder() = runTest {
        val foods = listOf(
            createFood("1", "Apple"),
            createFood("2", "Banana"),
            createFood("3", "Orange")
        )

        foods.forEach { foodDao.insertFood(it) }

        foodDao.insertSearchResults(
            listOf(
                SearchResultEntity("fruit", "3", 2),
                SearchResultEntity("fruit", "1", 0),
                SearchResultEntity("fruit", "2", 1)
            )
        )

        val result = foodDao.getSearchResultsForQuery("fruit")

        assertEquals(
            listOf("1", "2", "3"),
            result.map { it.foodId }
        )
    }

    @Test
    fun replaceSearchResults_replacesPreviousResults() = runTest {
        val foods = listOf(
            createFood("1", "Apple"),
            createFood("2", "Banana"),
            createFood("3", "Orange")
        )

        foods.forEach { foodDao.insertFood(it) }

        foodDao.insertSearchResults(
            listOf(
                SearchResultEntity("fruit", "1", 0),
                SearchResultEntity("fruit", "2", 1)
            )
        )

        foodDao.replaceSearchResults(
            query = "fruit",
            results = listOf(
                SearchResultEntity("fruit", "3", 0)
            )
        )

        val result = foodDao.getSearchResultsForQuery("fruit")

        assertEquals(1, result.size)
        assertEquals("3", result.first().foodId)
    }

    @Test
    fun saveFoodWithNutrients_savesFoodAndNutrientsTogether() = runTest {
        val food = createFood("1", "Apple")
        val nutrients = listOf(
            FoodNutrientEntity(
                foodId = "1",
                name = "Protein",
                amount = 0.3,
                unit = "g"
            ),
            FoodNutrientEntity(
                foodId = "1",
                name = "Carbohydrates",
                amount = 14.0,
                unit = "g"
            )
        )

        foodDao.saveFoodWithNutrients(food, nutrients)

        val result = foodDao.getFoodWithNutrients("1")

        assertEquals(food, result?.food)
        assertEquals(2, result?.nutrients?.size)
        assertEquals(
            setOf("Protein", "Carbohydrates"),
            result?.nutrients?.map { it.name }?.toSet()
        )
    }

    @Test
    fun getFoodWithNutrients_whenFoodDoesNotExist_returnsNull() = runTest {
        val result = foodDao.getFoodWithNutrients("unknown")

        assertNull(result)
    }

    @Test
    fun deleteFood_deletesAssociatedNutrientsAndSearchResults() = runTest {
        val food = createFood("1", "Apple")

        foodDao.insertFood(food)

        foodDao.insertNutrients(
            listOf(
                FoodNutrientEntity(
                    foodId = "1",
                    name = "Protein",
                    amount = 0.3,
                    unit = "g"
                )
            )
        )

        foodDao.insertSearchResults(
            listOf(SearchResultEntity("fruit", "1", 0))
        )

        database.openHelper.writableDatabase.execSQL(
            "DELETE FROM foods WHERE id = '1'"
        )

        assertNull(foodDao.getFoodById("1"))
        assertTrue(foodDao.getFoodWithNutrients("1") == null)
        assertTrue(foodDao.getSearchResultsForQuery("fruit").isEmpty())
    }
}
