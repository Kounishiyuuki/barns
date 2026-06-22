package com.barns.app.presentation.myitems

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barns.app.domain.model.ProductItem
import com.barns.app.domain.model.ProductItemStatus
import com.barns.app.domain.model.ProductItemType
import com.barns.app.domain.usecase.myitems.UpdateProductItemUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Form state for editing an existing registered greenery (ProductItem).
 * Local-only: edits are kept in this presentation state until the user
 * explicitly saves; cancelling simply discards this view model.
 */
class EditGreeneryViewModel(
    private val item: ProductItem,
    private val updateProductItemUseCase: UpdateProductItemUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(
        EditGreeneryUiState(
            name = item.name,
            type = item.type,
            locationLabel = item.locationLabel ?: "",
            notes = item.notes ?: "",
            status = item.status,
        ),
    )
    val state: StateFlow<EditGreeneryUiState> = _state.asStateFlow()

    fun onNameChange(value: String) { _state.value = _state.value.copy(name = value) }
    fun onLocationChange(value: String) { _state.value = _state.value.copy(locationLabel = value) }
    fun onNotesChange(value: String) { _state.value = _state.value.copy(notes = value) }
    fun onTypeChange(value: ProductItemType) { _state.value = _state.value.copy(type = value) }
    fun onStatusChange(value: ProductItemStatus) { _state.value = _state.value.copy(status = value) }

    /** Updates the item locally and invokes [onSaved] on success. */
    fun save(onSaved: () -> Unit) {
        val current = _state.value
        if (!current.canSave) return
        _state.value = current.copy(isSaving = true, errorMessage = null)
        viewModelScope.launch {
            runCatching {
                updateProductItemUseCase.execute(
                    original = item,
                    name = current.name.trim(),
                    type = current.type,
                    locationLabel = current.locationLabel.trim().ifBlank { null },
                    notes = current.notes.trim().ifBlank { null },
                    status = current.status,
                )
            }
                .onSuccess {
                    _state.value = _state.value.copy(isSaving = false)
                    onSaved()
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        errorMessage = "Unable to save changes. Please try again.",
                    )
                }
        }
    }
}

data class EditGreeneryUiState(
    val name: String = "",
    val type: ProductItemType = ProductItemType.INSTALLED,
    val locationLabel: String = "",
    val notes: String = "",
    val status: ProductItemStatus = ProductItemStatus.ACTIVE,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
) {
    val canSave: Boolean
        get() = name.isNotBlank() && !isSaving
}
