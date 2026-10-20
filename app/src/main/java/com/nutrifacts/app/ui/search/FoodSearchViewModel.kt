package com.nutrifacts.app.ui.search


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrifacts.app.domain.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodSearchViewModel @Inject constructor(
    private val repository: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodSearchUiState())
    val uiState = _uiState.asStateFlow()


    private var searchJob: Job? = null

    fun search(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            _uiState.value = FoodSearchUiState()
            return
        }


        searchJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                repository.searchFoods(query)
                    .onSuccess { foods ->
                        _uiState.value = FoodSearchUiState(foods = foods)
                    }
                    .onFailure {
                        _uiState.value = FoodSearchUiState(
                            error = "Couldn't load foods. Check your connection and try again."
                        )
                    }
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}