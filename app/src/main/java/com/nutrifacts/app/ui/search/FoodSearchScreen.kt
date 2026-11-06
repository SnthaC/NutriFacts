package com.nutrifacts.app.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nutrifacts.app.domain.model.Food

@Composable
fun FoodSearchScreen(
    viewModel: FoodSearchViewModel = hiltViewModel(),
    onFoodClick: (Food) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadRecentSearches()
    }

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
                            Color.LightGray.copy(alpha = 0.4f)
                        )
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
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(uiState.error ?: "Something went wrong")

                    Button(
                        onClick = { viewModel.search(query) },
                        enabled = query.isNotBlank()
                    ) {
                        Text("Try again")
                    }
                }
            }

            query.isBlank() && uiState.foods.isEmpty() -> {
                LazyColumn {
                    item {
                        Text(
                            text = "Recent searches",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(uiState.recentSearches) { recentQuery ->
                        Text(
                            text = recentQuery,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    query = recentQuery
                                    viewModel.searchRecent(recentQuery)
                                }
                                .padding(vertical = 12.dp)
                        )
                        HorizontalDivider()
                    }
                }
            }

            uiState.foods.isEmpty() &&
                    uiState.searchedQuery == query &&
                    !uiState.isLoading &&
                    uiState.error == null &&
                    query.isNotBlank() -> {
                Text("No foods found.")
            }

            else -> LazyColumn {
                items(uiState.foods, key = { it.id }) { food ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onFoodClick(food)
                            }
                            .padding(
                                vertical = 8.dp

                            )) {
                        Text(
                            food.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        food.brand?.let { Text(it) }

                        Text(
                            food.nutrients
                                .firstOrNull {
                                    it.name.equals("Energy", ignoreCase = true)
                                }
                                ?.let {
                                    "Energy: ${it.amount ?: "N/A"} ${it.unit}"
                                } ?: "Energy: N/A"
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}