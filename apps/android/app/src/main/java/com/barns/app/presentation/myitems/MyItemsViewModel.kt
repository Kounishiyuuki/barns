package com.barns.app.presentation.myitems

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barns.app.domain.model.ProductItem
import com.barns.app.domain.usecase.myitems.GetProductItemsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyItemsViewModel(
    private val getProductItemsUseCase: GetProductItemsUseCase,
) : ViewModel() {
    sealed interface State {
        object Loading : State
        data class Loaded(val items: List<ProductItem>) : State
        data class Failed(val message: String) : State
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    fun load() {
        _state.value = State.Loading
        viewModelScope.launch {
            runCatching { getProductItemsUseCase.execute() }
                .onSuccess { items -> _state.value = State.Loaded(items) }
                .onFailure { _state.value = State.Failed("Unable to load items. Please try again.") }
        }
    }
}
