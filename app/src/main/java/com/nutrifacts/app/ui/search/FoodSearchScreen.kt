package com.nutrifacts.app.ui.search


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.nutrifacts.app.domain.model.Food
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color

@Composable
fun FoodSearchScreen(
    viewModel: FoodSearchViewModel = hiltViewModel(),
    onFoodClick: (Food) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
               // viewModel.search(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search foods") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    viewModel.search(query)
                }
            ),
            trailingIcon = {
                IconButton(
                    onClick = { viewModel.search(query) },
                    enabled = query.isNotBlank(),
                    modifier = Modifier
                        .height(56.dp)
                        .width(56.dp)
                        .background(
                            Color.LightGray.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search food",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        )

        Spacer(Modifier.height(16.dp))

        when {
            uiState.isLoading -> CircularProgressIndicator()

            uiState.error != null -> Text(uiState.error!!)

            uiState.foods.isEmpty() && query.isNotBlank() ->
                Text("No foods found.")

            else -> LazyColumn {
                items(uiState.foods, key = { it.id }) { food ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()

                            .clickable {
                                onFoodClick(food)
                            }
                            .padding(vertical = 8.dp

                            )) {
                        Text(food.name, style = MaterialTheme.typography.titleMedium)
                        food.brand?.let { Text(it) }
                        Text("Calories: ${food.nutrients.calories ?: "N/A"} kcal / 100 g")
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}