package com.nutrifacts.app.ui.search

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.nutrifacts.app.domain.model.Food
import com.nutrifacts.app.domain.model.FoodSearchError
import com.nutrifacts.app.domain.model.FoodSearchResult
import com.nutrifacts.app.domain.model.Nutrient
import kotlinx.coroutines.CompletableDeferred
import org.junit.Rule
import org.junit.Test

class FoodSearchScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchScreen_displaysSearchField() {
        val viewModel = FoodSearchViewModel(FakeFoodRepository())

        composeTestRule.setContent {
            FoodSearchScreen(
                viewModel = viewModel,
                onFoodClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("Search foods")
            .assertExists()
    }

    @Test
    fun searchScreen_displaysRecentSearches() {
        val viewModel = FoodSearchViewModel(FakeFoodRepository())

        composeTestRule.setContent {
            FoodSearchScreen(
                viewModel = viewModel,
                onFoodClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("Recent searches")
            .assertExists()
    }

    @Test
    fun search_displaysNoResultsMessage() {
        val viewModel = FoodSearchViewModel(FakeFoodRepository())

        composeTestRule.setContent {
            FoodSearchScreen(
                viewModel = viewModel,
                onFoodClick = {}
            )
        }

        composeTestRule
            .onNode(hasSetTextAction())
            .performTextInput("apple")

        composeTestRule
            .onNodeWithContentDescription("Search food")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("No foods found.")
            .assertExists()
    }

    @Test
    fun search_displaysFoodResults() {
        val food = Food(
            id = "123",
            name = "Apple",
            brand = "Fresh Farm",
            nutrients = listOf(
                Nutrient("Energy", 52.0, "kcal")
            )
        )

        val viewModel = FoodSearchViewModel(
            FakeFoodRepository(listOf(food))
        )

        composeTestRule.setContent {
            FoodSearchScreen(
                viewModel = viewModel,
                onFoodClick = {}
            )
        }

        composeTestRule
            .onNode(hasSetTextAction())
            .performTextInput("apple")

        composeTestRule
            .onNodeWithContentDescription("Search food")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Apple")
            .assertExists()

        composeTestRule
            .onNodeWithText("Fresh Farm")
            .assertExists()
    }

    @Test
    fun clickingFoodResult_callsOnFoodClick() {
        val food = Food(
            id = "123",
            name = "Apple",
            brand = "Fresh Farm",
            nutrients = listOf(
                Nutrient("Energy", 52.0, "kcal")
            )
        )

        var selectedFood: Food? = null

        val viewModel = FoodSearchViewModel(
            FakeFoodRepository(listOf(food))
        )

        composeTestRule.setContent {
            FoodSearchScreen(
                viewModel = viewModel,
                onFoodClick = { selectedFood = it }
            )
        }

        composeTestRule
            .onNode(hasSetTextAction())
            .performTextInput("apple")

        composeTestRule
            .onNodeWithContentDescription("Search food")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNode(hasText("Apple") and hasClickAction())
            .performClick()

        assert(selectedFood == food)
    }

    @Test
    fun search_displaysLoadingIndicator() {
        val searchGate = CompletableDeferred<Unit>()

        val viewModel = FoodSearchViewModel(
            FakeFoodRepository(searchGate = searchGate)
        )

        composeTestRule.setContent {
            FoodSearchScreen(
                viewModel = viewModel,
                onFoodClick = {}
            )
        }

        composeTestRule
            .onNode(hasSetTextAction())
            .performTextInput("apple")

        composeTestRule
            .onNodeWithContentDescription("Search food")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertExists()

        searchGate.complete(Unit)

        composeTestRule.waitForIdle()

        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertDoesNotExist()
    }

    @Test
    fun search_whenNetworkErrorOccurs_retryLoadsResults() {
        val food = Food(
            id = "123",
            name = "Apple",
            brand = "Fresh Farm",
            nutrients = listOf(
                Nutrient("Energy", 52.0, "kcal")
            )
        )

        val viewModel = FoodSearchViewModel(
            FakeFoodRepository(
                searchResults = listOf(
                    FoodSearchResult.Failure(FoodSearchError.NETWORK),
                    FoodSearchResult.Success(listOf(food))
                )
            )
        )

        composeTestRule.setContent {
            FoodSearchScreen(
                viewModel = viewModel,
                onFoodClick = {}
            )
        }

        composeTestRule
            .onNode(hasSetTextAction())
            .performTextInput("apple")

        composeTestRule
            .onNodeWithContentDescription("Search food")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText(
                "Couldn't connect to the food database. Check your connection."
            )
            .assertExists()

        composeTestRule
            .onNodeWithText("Try again")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Apple")
            .assertExists()
    }
}