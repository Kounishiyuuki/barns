package com.barns.app.presentation.myitems

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barns.app.domain.model.ProductItem
import com.barns.app.domain.usecase.myitems.ArchiveProductItemUseCase
import com.barns.app.domain.usecase.myitems.GetProductItemDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItemDetailViewModel(
    private val itemId: String,
    private val getProductItemDetailUseCase: GetProductItemDetailUseCase,
    private val officialContentResolver: ItemOfficialContentResolver,
    private val archiveProductItemUseCase: ArchiveProductItemUseCase,
) : ViewModel() {
    sealed interface State {
        object Loading : State
        data class Loaded(val item: ProductItem) : State
        object NotFound : State
        data class Failed(val message: String) : State
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    /**
     * Official read-only reference content (basic info + care guides) for the
     * loaded item. `null` until resolved; stays `null` if nothing matches.
     */
    private val _officialContent = MutableStateFlow<ItemOfficialContent?>(null)
    val officialContent: StateFlow<ItemOfficialContent?> = _officialContent.asStateFlow()

    fun load() {
        _state.value = State.Loading
        _officialContent.value = null
        viewModelScope.launch {
            runCatching { getProductItemDetailUseCase.execute(itemId) }
                .onSuccess { item ->
                    if (item != null) {
                        _state.value = State.Loaded(item)
                        _officialContent.value = officialContentResolver.resolve(item)
                    } else {
                        _state.value = State.NotFound
                    }
                }
                .onFailure { _state.value = State.Failed("Unable to load this item. Please try again.") }
        }
    }

    /**
     * Archives the loaded greenery locally (soft action; no hard delete) and
     * invokes [onArchived] on success so the screen can return to the list.
     */
    fun archive(onArchived: () -> Unit) {
        val current = _state.value as? State.Loaded ?: return
        viewModelScope.launch {
            runCatching { archiveProductItemUseCase.execute(current.item) }
                .onSuccess { onArchived() }
                .onFailure {
                    _state.value = State.Failed("Unable to archive this item. Please try again.")
                }
        }
    }
}
