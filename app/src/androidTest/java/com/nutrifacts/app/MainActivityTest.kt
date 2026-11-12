package com.nutrifacts.app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun mainActivity_launchesAndDisplaysSearchScreen() {
        composeTestRule
            .onNodeWithText("Search foods")
            .assertExists()
    }
}
