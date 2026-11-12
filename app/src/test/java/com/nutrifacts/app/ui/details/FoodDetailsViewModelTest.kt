package com.nutrifacts.app.ui.details

import androidx.lifecycle.SavedStateHandle
import com.nutrifacts.app.MainDispatcherRule
import com.nutrifacts.app.domain.model.Food
import com.nutrifacts.app.domain.repository.FoodRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FoodDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: FoodRepository = mockk()

    private fun createViewModel() = FoodDetailsViewModel(
        savedStateHandle = SavedStateHandle(mapOf("foodId" to "123")),
        repository = repository
    )

    private fun sampleFood() = Food(
        id = "123",
        name = "Apple",
        brand = "Fresh Farm",
        nutrients = emptyList()
    )

    @Test
    fun loadFood_whenSuccessful_updatesUiState() =
        runTest(mainDispatcherRule.testDispatcher) {
            val food = sampleFood()
            coEvery { repository.getFoodById("123") } returns food

            val viewModel = createViewModel()
            advanceUntilIdle()

            assertEquals(food, viewModel.uiState.value.food)
            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.error)
        }

    @Test
    fun loadFood_whenFoodNotFound_updatesErrorState() =
        runTest(mainDispatcherRule.testDispatcher) {
            coEvery { repository.getFoodById("123") } returns null

            val viewModel = createViewModel()
            advanceUntilIdle()

            assertNull(viewModel.uiState.value.food)
            assertEquals(
                "Food not found in local storage.",
                viewModel.uiState.value.error
            )
            assertFalse(viewModel.uiState.value.isLoading)
        }

    @Test
    fun loadFood_whenRepositoryThrows_updatesErrorState() =
        runTest(mainDispatcherRule.testDispatcher) {
            coEvery {
                repository.getFoodById("123")
            } throws RuntimeException("Database error")

            val viewModel = createViewModel()
            advanceUntilIdle()

            assertNull(viewModel.uiState.value.food)
            assertEquals(
                "Couldn't load food details.",
                viewModel.uiState.value.error
            )
            assertFalse(viewModel.uiState.value.isLoading)
        }

    @Test
    fun loadFood_whenStarted_setsLoadingState() =
        runTest(mainDispatcherRule.testDispatcher) {
            val deferredResult = CompletableDeferred<Food?>()

            coEvery {
                repository.getFoodById("123")
            } coAnswers {
                deferredResult.await()
            }

            val viewModel = createViewModel()
            runCurrent()

            assertTrue(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.food)
            assertNull(viewModel.uiState.value.error)

            deferredResult.complete(sampleFood())
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertEquals(sampleFood(), viewModel.uiState.value.food)
        }
}