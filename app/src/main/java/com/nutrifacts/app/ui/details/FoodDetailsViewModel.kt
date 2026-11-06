package com.nutrifacts.app.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrifacts.app.domain.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class FoodDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: FoodRepository
) : ViewModel() {


    private val foodId: String =
        checkNotNull(savedStateHandle["foodId"])

    private val _uiState = MutableStateFlow(FoodDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadFood()
    }

    private fun loadFood() {
        viewModelScope.launch {
            _uiState.value = FoodDetailsUiState(isLoading = true)

            try {
                val food = repository.getFoodById(foodId)

                _uiState.value = if (food != null) {
                    FoodDetailsUiState(
                        isLoading = false,
                        food = food
                    )
                } else {
                    FoodDetailsUiState(
                        isLoading = false,
                        error = "Food not found in local storage."
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = FoodDetailsUiState(
                    isLoading = false,
                    error = "Couldn't load food details."
                )
            }
        }
    }
}