package com.barns.app.presentation.myitems

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barns.app.domain.usecase.myitems.AddProductItemUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddItemViewModel(
    private val addProductItemUseCase: AddProductItemUseCase,
) : ViewModel() {
    // MVP source skeleton: a fixed placeholder category until category
    // selection is implemented. Local-only; never sent to a server.
    private val categoryId = "cat-wall-green"

    private val _state = MutableStateFlow(AddItemUiState())
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
    val locationLabel: String = "",
    val notes: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
) {
    val canSave: Boolean
        get() = name.isNotBlank() && !isSaving
}
