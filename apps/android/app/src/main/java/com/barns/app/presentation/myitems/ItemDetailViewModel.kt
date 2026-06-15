package com.barns.app.presentation.myitems

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barns.app.domain.model.ProductItem
import com.barns.app.domain.usecase.myitems.GetProductItemDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItemDetailViewModel(
    private val itemId: String,
    private val getProductItemDetailUseCase: GetProductItemDetailUseCase,
) : ViewModel() {
    sealed interface State {
        object Loading : State
        data class Loaded(val item: ProductItem) : State
        object NotFound : State
        data class Failed(val message: String) : State
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    fun load() {
        _state.value = State.Loading
        viewModelScope.launch {
            runCatching { getProductItemDetailUseCase.execute(itemId) }
                .onSuccess { item ->
                    _state.value = if (item != null) State.Loaded(item) else State.NotFound
                }
                .onFailure { _state.value = State.Failed("Unable to load this item. Please try again.") }
        }
    }
}
