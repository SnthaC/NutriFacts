# NutriFacts

NutriFacts is an Android app that allows users to search for food products and view their nutritional information.
The app uses the USDA FoodData Central API to retrieve food data, with local caching to improve performance and reduce unnecessary network requests.

## API Migration

The initial version used the Open Food Facts API. Due to issues with API requests and data retrieval, I migrated the app to the USDA FoodData Central API to improve search reliability and nutritional data retrieval.

The current implementation uses USDA FoodData Central as its food data source.


## Data Sources and Attribution

**Current data source: USDA FoodData Central**

Nutrition data is retrieved from [USDA FoodData Central](https://fdc.nal.usda.gov/), provided by the U.S. Department of Agriculture, Agricultural Research Service, and available under CC0 1.0.

**Previous data source: Open Food Facts**

The initial version of NutriFacts used [Open Food Facts](https://world.openfoodfacts.org/) for product information and product images. Product images were provided by Open Food Facts contributors under the Creative Commons Attribution-ShareAlike (CC BY-SA) licence.

## Features

- Search for food products using USDA FoodData Central.
- View nutritional information for individual foods.
- Display nutrient values with their corresponding units.
- Browse recent searches.
- Local persistence: Store food details and search results locally using Room.
- Cache-first behaviour: Display cached search results when available, reducing unnecessary API requests.
- Handle network errors, API rate limits and unsuccessful requests.

## Built With

- Language: Kotlin
- UI: Jetpack Compose
- Architecture: MVVM, Clean Architecture
- Networking: Retrofit, Moshi
- Dependency injection: Hilt
- Asynchronous programming: Kotlin Coroutines and StateFlow
- Local storage: Room

## Architecture

The project follows a layered architecture to separate UI, domain and data responsibilities.
- UI: Jetpack Compose screens and ViewModels manage and display UI state.
- Domain: Contains the core models and application logic.
- Data: Repository implementation coordinates remote API requests and local Room storage.

## Testing

The project includes tests covering:

- Repository behaviour, including API responses, caching and error handling.
- ViewModel state and search behaviour.
- Local data sources and Room database operations.
- API and UI behaviour.
