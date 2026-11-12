package com.nutrifacts.app.ui.search

import com.nutrifacts.app.MainDispatcherRule
import com.nutrifacts.app.domain.model.Food
import com.nutrifacts.app.domain.model.FoodSearchError
import com.nutrifacts.app.domain.model.FoodSearchResult
import com.nutrifacts.app.domain.repository.FoodRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FoodSearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: FoodRepository = mockk()

    private val viewModel = FoodSearchViewModel(repository)

    @Test
    fun search_whenStarted_setsLoadingState() = runTest(mainDispatcherRule.testDispatcher) {
        val deferredResult = CompletableDeferred<FoodSearchResult>()

        coEvery {
            repository.searchFoods("walnuts")
        } coAnswers {
            deferredResult.await()
        }

        coEvery {
            repository.getRecentSearches()
        } returns emptyList()

        viewModel.search("walnuts")

        runCurrent()

        assertTrue(viewModel.uiState.value.isLoading)

        deferredResult.complete(
            FoodSearchResult.Success(emptyList())
        )

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun search_whenSuccessful_updatesUiState() = runTest(mainDispatcherRule.testDispatcher) {
        val foods = listOf(
            Food(
                id = "123",
                name = "Walnuts",
                brand = "Test Brand",
                nutrients = emptyList()
            )
        )

        coEvery {
            repository.searchFoods("walnuts")
        } returns FoodSearchResult.Success(foods)

        coEvery {
            repository.getRecentSearches()
        } returns listOf("walnuts")

        viewModel.search("walnuts")

        advanceUntilIdle()

        assertEquals(foods, viewModel.uiState.value.foods)
        assertEquals(listOf("walnuts"), viewModel.uiState.value.recentSearches)
        assertEquals("walnuts", viewModel.uiState.value.searchedQuery)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun search_whenNoFoodsFound_updatesUiState() = runTest(mainDispatcherRule.testDispatcher) {
        coEvery {
            repository.searchFoods("unknown food")
        } returns FoodSearchResult.Success(emptyList())

        coEvery {
            repository.getRecentSearches()
        } returns emptyList()

        viewModel.search("unknown food")

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.foods.isEmpty())
        assertEquals("unknown food", viewModel.uiState.value.searchedQuery)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun search_whenNetworkError_updatesErrorState() = runTest(mainDispatcherRule.testDispatcher) {
        coEvery {
            repository.searchFoods("walnuts")
        } returns FoodSearchResult.Failure(FoodSearchError.NETWORK)

        viewModel.search("walnuts")

        advanceUntilIdle()

        assertEquals(
            "Couldn't connect to the food database. Check your connection.",
            viewModel.uiState.value.error
        )
        assertTrue(viewModel.uiState.value.canRetry)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun search_whenRateLimited_updatesErrorState() = runTest(mainDispatcherRule.testDispatcher) {
        coEvery {
            repository.searchFoods("walnuts")
        } returns FoodSearchResult.Failure(FoodSearchError.RATE_LIMITED)

        viewModel.search("walnuts")

        advanceUntilIdle()

        assertEquals(
            "Too many searches right now. Please try again later.",
            viewModel.uiState.value.error
        )
        assertTrue(viewModel.uiState.value.canRetry)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun search_whenUnauthorized_updatesErrorState() = runTest(mainDispatcherRule.testDispatcher) {
        coEvery {
            repository.searchFoods("walnuts")
        } returns FoodSearchResult.Failure(FoodSearchError.UNAUTHORIZED)

        viewModel.search("walnuts")

        advanceUntilIdle()

        assertEquals(
            "The food database couldn't authorize the request. Please try again later.",
            viewModel.uiState.value.error
        )
        assertFalse(viewModel.uiState.value.canRetry)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun search_whenQueryIsBlank_resetsUiStateAndLoadsRecentSearches() =
        runTest(mainDispatcherRule.testDispatcher) {
            coEvery {
                repository.getRecentSearches()
            } returns listOf("walnuts", "almonds")

            viewModel.search("   ")

            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.foods.isEmpty())
            assertEquals(
                listOf("walnuts", "almonds"),
                viewModel.uiState.value.recentSearches
            )
            assertEquals("", viewModel.uiState.value.searchedQuery)
            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.error)
        }

    @Test
    fun onQueryChanged_clearsPreviousResultsAndError() =
        runTest(mainDispatcherRule.testDispatcher) {
            val foods = listOf(
                Food(
                    id = "123",
                    name = "Walnuts",
                    brand = "Test Brand",
                    nutrients = emptyList()
                )
            )

            coEvery {
                repository.searchFoods("walnuts")
            } returns FoodSearchResult.Success(foods)

            coEvery {
                repository.getRecentSearches()
            } returns emptyList()

            viewModel.search("walnuts")
            advanceUntilIdle()

            viewModel.onQueryChanged()

            assertTrue(viewModel.uiState.value.foods.isEmpty())
            assertEquals("", viewModel.uiState.value.searchedQuery)
            assertNull(viewModel.uiState.value.error)
            assertFalse(viewModel.uiState.value.canRetry)
            assertFalse(viewModel.uiState.value.isLoading)
        }

    @Test
    fun searchRecent_whenSuccessful_updatesUiState() = runTest(mainDispatcherRule.testDispatcher) {
        val foods = listOf(
            Food(
                id = "123",
                name = "Walnuts",
                brand = "Test Brand",
                nutrients = emptyList()
            )
        )

        coEvery {
            repository.getCachedSearchResults("walnuts")
        } returns foods

        viewModel.searchRecent("walnuts")

        advanceUntilIdle()

        assertEquals(foods, viewModel.uiState.value.foods)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun searchRecent_whenRepositoryThrows_updatesErrorState() =
        runTest(mainDispatcherRule.testDispatcher) {
            coEvery {
                repository.getCachedSearchResults("walnuts")
            } throws RuntimeException("Database error")

            viewModel.searchRecent("walnuts")

            advanceUntilIdle()

            assertEquals(
                "Couldn't load saved results. Please try again.",
                viewModel.uiState.value.error
            )
            assertFalse(viewModel.uiState.value.isLoading)
        }

    @Test
    fun search_whenNewSearchStarts_cancelsPreviousSearch() =
        runTest(mainDispatcherRule.testDispatcher) {
            val firstSearch = CompletableDeferred<FoodSearchResult>()

            coEvery {
                repository.searchFoods("walnuts")
            } coAnswers {
                firstSearch.await()
            }

            coEvery {
                repository.searchFoods("dates")
            } returns FoodSearchResult.Success(emptyList())

            coEvery {
                repository.getRecentSearches()
            } returns emptyList()

            viewModel.search("walnuts")
            runCurrent()

            viewModel.search("dates")
            advanceUntilIdle()

            assertEquals("dates", viewModel.uiState.value.searchedQuery)
            assertTrue(viewModel.uiState.value.foods.isEmpty())
            assertFalse(viewModel.uiState.value.isLoading)
        }

    @Test
    fun search_whenStarted_cancelsPreviousRecentSearch() =
        runTest(mainDispatcherRule.testDispatcher) {
            val deferredResult = CompletableDeferred<List<Food>>()

            coEvery {
                repository.getCachedSearchResults("walnuts")
            } coAnswers {
                deferredResult.await()
            }

            coEvery {
                repository.searchFoods("dates")
            } returns FoodSearchResult.Success(emptyList())

            coEvery {
                repository.getRecentSearches()
            } returns emptyList()

            viewModel.searchRecent("walnuts")
            runCurrent()

            viewModel.search("dates")
            advanceUntilIdle()

            assertEquals("dates", viewModel.uiState.value.searchedQuery)
            assertTrue(viewModel.uiState.value.foods.isEmpty())
            assertFalse(viewModel.uiState.value.isLoading)
        }

    @Test
    fun search_whenUnexpectedErrorOccurs_updatesErrorState() =
        runTest(mainDispatcherRule.testDispatcher) {
            coEvery {
                repository.searchFoods("walnuts")
            } returns FoodSearchResult.Failure(FoodSearchError.UNKNOWN)

            viewModel.search("walnuts")

            advanceUntilIdle()

            assertEquals(
                "Couldn't load foods. Please try again.",
                viewModel.uiState.value.error
            )
            assertTrue(viewModel.uiState.value.canRetry)
            assertFalse(viewModel.uiState.value.isLoading)
        }
}