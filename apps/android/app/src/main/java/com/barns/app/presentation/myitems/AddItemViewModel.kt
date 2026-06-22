package com.barns.app.presentation.myitems

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barns.app.domain.model.ProductItemType
import com.barns.app.domain.usecase.myitems.AddProductItemUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddItemViewModel(
    private val addProductItemUseCase: AddProductItemUseCase,
    prefill: RegisterGreeneryPrefill? = null,
) : ViewModel() {
    // MVP source skeleton: defaults to a placeholder category until full
    // category selection exists. A Catalog prefill may override it. Local-only;
    // never sent to a server.
    private var categoryId = prefill?.categoryId ?: "cat-wall-green"

    private val _state = MutableStateFlow(
        prefill?.let { AddItemUiState(name = it.name, type = it.type) } ?: AddItemUiState(),
    )
    val state: StateFlow<AddItemUiState> = _state.asStateFlow()

    fun onNameChange(value: String) {
        _state.value = _state.value.copy(name = value)
    }

    fun onLocationChange(value: String) {
        _state.value = _state.value.copy(locationLabel = value)
    }

    fun onNotesChange(value: String) {
        _state.value = _state.value.copy(notes = value)
    }

    fun onTypeChange(value: ProductItemType) {
        _state.value = _state.value.copy(type = value)
    }

    /** Adds the item locally and invokes [onSaved] on success. */
    fun save(onSaved: () -> Unit) {
        val current = _state.value
        if (!current.canSave) return
        _state.value = current.copy(isSaving = true, errorMessage = null)
        viewModelScope.launch {
            runCatching {
                addProductItemUseCase.execute(
                    name = current.name.trim(),
                    categoryId = categoryId,
                    type = current.type,
                    locationLabel = current.locationLabel.trim().ifBlank { null },
                    notes = current.notes.trim().ifBlank { null },
                )
            }
                .onSuccess {
                    _state.value = _state.value.copy(isSaving = false)
                    onSaved()
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        errorMessage = "Unable to add the item. Please try again.",
                    )
                }
        }
    }
}

data class AddItemUiState(
    val name: String = "",
    // Registering installed greenery is the primary flow, so default to it.
    val type: ProductItemType = ProductItemType.INSTALLED,
    val locationLabel: String = "",
    val notes: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
) {
    val canSave: Boolean
        get() = name.isNotBlank() && !isSaving
}
