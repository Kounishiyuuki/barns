package com.barns.app.presentation.myitems

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barns.app.domain.model.ProductItem
import com.barns.app.domain.model.ProductItemStatus
import com.barns.app.domain.usecase.myitems.GetProductItemsUseCase
import com.barns.app.domain.usecase.myitems.RestoreProductItemUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Drives the local-only Archived Greenery list. It loads the customer's
 * archived [ProductItem]s (read-only here, except restore) and can restore an
 * item back to the active My Greenery list. All work is local: no hard delete,
 * no re-create/re-insert, no API, no sync.
 */
class ArchivedGreeneryViewModel(
    private val getProductItemsUseCase: GetProductItemsUseCase,
    private val restoreProductItemUseCase: RestoreProductItemUseCase,
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
                // This list shows only archived items; they remain in the local
                // store (no hard delete).
                .onSuccess { items ->
                    _state.value = State.Loaded(items.filter { it.status == ProductItemStatus.ARCHIVED })
                }
                .onFailure {
                    _state.value = State.Failed("Unable to load archived items. Please try again.")
                }
        }
    }

    /**
     * Restores an archived item back to active locally (soft action; no hard
     * delete). Reloads so the restored item leaves this archived list.
     */
    fun restore(item: ProductItem) {
        viewModelScope.launch {
            runCatching { restoreProductItemUseCase.execute(item) }
                .onSuccess { load() }
                .onFailure {
                    _state.value = State.Failed("Unable to restore this item. Please try again.")
                }
        }
    }
}
