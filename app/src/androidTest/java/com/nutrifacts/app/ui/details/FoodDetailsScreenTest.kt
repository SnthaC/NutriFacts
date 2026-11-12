package com.nutrifacts.app.ui.details

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.SavedStateHandle
import com.nutrifacts.app.domain.model.Food
import com.nutrifacts.app.domain.model.Nutrient
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FoodDetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun sampleFood() = Food(
        id = "123",
        name = "Apple",
        brand = "Fresh Farm",
        nutrients = listOf(
            Nutrient("Energy", 52.0, "kcal"),
            Nutrient("Protein", 13.0, "G"),
            Nutrient("Total sugars", 10.0, "G"),
            Nutrient("Sodium, Na", 5.0, "MG")
        )
    )

    private fun createViewModel(
        repository: FakeFoodDetailsRepository
    ) = FoodDetailsViewModel(
        savedStateHandle = SavedStateHandle(
            mapOf("foodId" to "123")
        ),
        repository = repository
    )

    @Test
    fun detailsScreen_displaysLoadingIndicator() {
        val gate = CompletableDeferred<Unit>()

        val viewModel = createViewModel(
            FakeFoodDetailsRepository(
                loadFood = {
                    gate.await()
                    sampleFood()
                }
            )
        )

        composeTestRule.setContent {
            FoodDetailsScreen(
                viewModel = viewModel,
                onBack = {}
            )
        }

        composeTestRule
            .onNode(
                hasProgressBarRangeInfo(
                    ProgressBarRangeInfo.Indeterminate
                )
            )
            .assertExists()

        gate.complete(Unit)
        composeTestRule.waitForIdle()

        composeTestRule
            .onNode(
                hasProgressBarRangeInfo(
                    ProgressBarRangeInfo.Indeterminate
                )
            )
            .assertDoesNotExist()
    }

    @Test
    fun detailsScreen_displaysFoodAndNutrition() {
        val viewModel = createViewModel(
            FakeFoodDetailsRepository(food = sampleFood())
        )

        composeTestRule.setContent {
            FoodDetailsScreen(
                viewModel = viewModel,
                onBack = {}
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Apple").assertExists()
        composeTestRule.onNodeWithText("Brand: Fresh Farm").assertExists()
        composeTestRule.onNodeWithText("Nutrition per 100 g").assertExists()

        composeTestRule.onNodeWithText("Energy").assertExists()
        composeTestRule.onNodeWithText("52 kcal").assertExists()

        composeTestRule.onNodeWithText("Protein").assertExists()
        composeTestRule.onNodeWithText("13 g").assertExists()

        composeTestRule.onNodeWithText("Sodium, Na").assertExists()
        composeTestRule.onNodeWithText("5 mg").assertExists()
    }

    @Test
    fun detailsScreen_whenFoodIsMissing_displaysError() {
        val viewModel = createViewModel(
            FakeFoodDetailsRepository(food = null)
        )

        composeTestRule.setContent {
            FoodDetailsScreen(
                viewModel = viewModel,
                onBack = {}
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Food not found in local storage.")
            .assertExists()
    }

    @Test
    fun detailsScreen_whenRepositoryThrows_displaysError() {
        val viewModel = createViewModel(
            FakeFoodDetailsRepository(
                loadFood = {
                    throw IllegalStateException("Database error")
                }
            )
        )

        composeTestRule.setContent {
            FoodDetailsScreen(
                viewModel = viewModel,
                onBack = {}
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Couldn't load food details.")
            .assertExists()
    }

    @Test
    fun detailsScreen_whenBackButtonClicked_callsOnBack() {
        val viewModel = createViewModel(
            FakeFoodDetailsRepository(food = sampleFood())
        )

        var backClicked = false

        composeTestRule.setContent {
            FoodDetailsScreen(
                viewModel = viewModel,
                onBack = { backClicked = true }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        assertTrue(backClicked)
    }
}
