package com.nutrifacts.app.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrifacts.app.domain.model.FoodSearchError
import com.nutrifacts.app.domain.model.FoodSearchResult
import com.nutrifacts.app.domain.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class FoodSearchViewModel @Inject constructor(
    private val repository: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodSearchUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChanged() {
        searchJob?.cancel()

        _uiState.value = _uiState.value.copy(
            foods = emptyList(),
            searchedQuery = "",
            error = null,
            canRetry = false,
            isLoading = false
        )
    }

    fun search(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            _uiState.value = FoodSearchUiState()
            loadRecentSearches()
            return
        }

        searchJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                searchedQuery = query,
                isLoading = true,
                error = null,
                canRetry = false
            )

            try {
                when (val result = repository.searchFoods(query)) {

                    is FoodSearchResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            foods = result.foods,
                            error = null
                        )
                        loadRecentSearches()
                    }

                    is FoodSearchResult.Failure -> {
                        val message = when (result.error) {
                            FoodSearchError.RATE_LIMITED ->
                                "Too many searches right now. Please try again later."

                            FoodSearchError.UNAUTHORIZED ->
                                "The food database couldn't authorize the request. Please try again later."

                            FoodSearchError.NETWORK ->
                                "Couldn't connect to the food database. Check your connection."

                            FoodSearchError.UNKNOWN ->
                                "Couldn't load foods. Please try again."
                        }

                        val canRetry = when (result.error) {
                            FoodSearchError.NETWORK,
                            FoodSearchError.UNKNOWN,
                            FoodSearchError.RATE_LIMITED -> true

                            FoodSearchError.UNAUTHORIZED -> false
                        }

                        _uiState.value = FoodSearchUiState(
                            foods = emptyList(),
                            error = message,
                            canRetry = canRetry
                        )
                    }
                }
            } finally {
                if (currentCoroutineContext().isActive) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }

    fun loadRecentSearches() {
        viewModelScope.launch {
            try {
                val recentSearches = repository.getRecentSearches()
                _uiState.value = _uiState.value.copy(
                    recentSearches = recentSearches
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                android.util.Log.e(
                    "FoodSearchViewModel",
                    "Failed to load recent searches",
                    error
                )
            }
        }
    }

    fun searchRecent(query: String) {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                canRetry = false
            )

            try {
                val foods = repository.getCachedSearchResults(query)

                _uiState.value = _uiState.value.copy(
                    foods = foods,
                    error = null
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Couldn't load saved results. Please try again.",
                    canRetry = true
                )
            } finally {
                if (currentCoroutineContext().isActive) {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }
}