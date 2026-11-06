package com.nutrifacts.app.ui.details

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FoodDetailsScreen(
    viewModel: FoodDetailsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val food = uiState.food

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (food == null) {
        Text(uiState.error ?: "Food not found")
        return
    }

    BackHandler(onBack = onBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(food.name, style = MaterialTheme.typography.headlineSmall)
            food.brand?.let { Text("Brand: $it") }
            Text("Nutrition per 100 g",
                style = MaterialTheme.typography.titleLarge)

            val priorityOrder = listOf(
                "Energy",
                "Energy",
                "Total lipid (fat)",
                "Fatty acids, total saturated",
                "Fatty acids, total monounsaturated",
                "Fatty acids, total polyunsaturated",
                "Fatty acids, total trans",
                "Carbohydrate, by difference",
                "Total sugars",
                "Fiber, total dietary",
                "Protein",
                "Sodium, Na"
            )


            food.nutrients.sortedBy { nutrient ->
                val index = priorityOrder.indexOfFirst { priority ->
                    nutrient.name.contains(priority, ignoreCase = true)
                }
                if (index == -1) Int.MAX_VALUE else index
            }
                .forEach { nutrient ->
                    NutrientRow(
                        label = nutrient.name,
                        value = nutrient.amount,
                        unit = nutrient.unit
                    )
                }
        }
    }
}

@Composable
private fun NutrientRow(
    label: String,
    value: Double?,
    unit: String
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        val isBold = label.equals("Energy", ignoreCase = true) ||
                label.equals("total lipid (fat)", ignoreCase = true) ||
                label.equals("carbohydrate, by difference", ignoreCase = true) ||
                label.equals("Protein", ignoreCase = true) ||
                label.equals("fiber, total dietary", ignoreCase = true)

        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )

        val displayUnit = when (unit) {
            "G" -> "g"
            "MG" -> "mg"
            "UG" -> "µg"
            else -> unit
        }

        Text(
            "${
                value?.let {
                    if (it % 1.0 == 0.0) it.toLong().toString() else it.toString()
                } ?: "N/A"
            } $displayUnit")
    }
}
