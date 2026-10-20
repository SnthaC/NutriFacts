package com.nutrifacts.app.ui.details


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nutrifacts.app.domain.model.Food

@Composable
fun FoodDetailsScreen(
    food: Food,
    onBack: () -> Unit
) {
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
            food.imageUrl?.let { imageUrl ->
                if (food.imageUrl.isNullOrBlank()) {
                    Text(
                        text = "No image available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = food.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Text(food.name, style = MaterialTheme.typography.headlineSmall)
            food.brand?.let { Text("Brand: $it") }

            Text("Nutrition per 100 g", style = MaterialTheme.typography.titleLarge)

            NutrientRow("Calories", food.nutrients.calories, "kcal")
            NutrientRow("Protein", food.nutrients.protein, "g")
            NutrientRow("Carbohydrates", food.nutrients.carbohydrates, "g")
            NutrientRow("Fat", food.nutrients.fat, "g")
            NutrientRow("Saturated fat", food.nutrients.saturatedFat, "g")
            NutrientRow("Sugars", food.nutrients.sugars, "g")
            NutrientRow("Fibre", food.nutrients.fiber, "g")
            NutrientRow("Salt", food.nutrients.salt, "g")
            NutrientRow("Sodium", food.nutrients.sodium, "mg")
            NutrientRow("Vitamin A", food.nutrients.vitaminA, "µg")
            NutrientRow("Vitamin C", food.nutrients.vitaminC, "mg")
            NutrientRow("Vitamin D", food.nutrients.vitaminD, "µg")
            NutrientRow("Vitamin B12", food.nutrients.vitaminB12, "µg")
            NutrientRow("Calcium", food.nutrients.calcium, "mg")
            NutrientRow("Iron", food.nutrients.iron, "mg")
            NutrientRow("Potassium", food.nutrients.potassium, "mg")
            NutrientRow("Magnesium", food.nutrients.magnesium, "mg")
            NutrientRow("Zinc", food.nutrients.zinc, "mg")
        }
    }
}

@Composable
private fun NutrientRow(label: String, value: Double?, unit: String) {
    if (value != null) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(label, modifier = Modifier.weight(1f))
            Text("$value $unit")
        }
    }
}