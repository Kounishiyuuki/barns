package com.barns.app.presentation.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barns.app.domain.usecase.catalog.GetCatalogItemsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Loads the supporting, official read-only catalog list through a use case. */
class CatalogListViewModel(
    private val getCatalogItemsUseCase: GetCatalogItemsUseCase,
) : ViewModel() {
    sealed interface State {
        object Loading : State
        data class Loaded(val items: List<CatalogPresentationItem>) : State
        data class Failed(val message: String) : State
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    fun load() {
        _state.value = State.Loading
        viewModelScope.launch {
            runCatching { getCatalogItemsUseCase.execute() }
                .onSuccess { items ->
                    _state.value = State.Loaded(items.map(CatalogPresentationItem::from))
                }
                .onFailure {
                    _state.value = State.Failed("Unable to load the catalog. Please try again.")
                }
        }
    }
}
