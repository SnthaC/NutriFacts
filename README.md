# NutriFacts

An Android app for searching food products and viewing their nutritional information.

NutriFacts allows users to search for foods and explore their nutritional values through a simple interface.

## Features

- Search for food products.
- View nutritional information for individual foods.
- Display nutrient values with their corresponding units.
- Browse recent searches.
- Access cached search results when available.

## Built With

- Kotlin
- Jetpack Compose
- MVVM
- Retrofit
- Moshi
- Hilt
- Kotlin Coroutines and StateFlow
- Room

## API Migration

The initial version used the Open Food Facts API. Due to issues with API requests and data retrieval, I migrated the app to the USDA FoodData Central API to improve search reliability and nutritional data retrieval.

The current implementation uses USDA FoodData Central as its food data source.
