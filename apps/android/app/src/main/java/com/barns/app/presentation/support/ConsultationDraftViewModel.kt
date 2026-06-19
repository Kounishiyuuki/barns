package com.barns.app.presentation.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barns.app.domain.model.ConsultationCategory
import com.barns.app.domain.model.ConsultationDraft
import com.barns.app.domain.model.ConsultationUrgency
import com.barns.app.domain.model.ProductItem
import com.barns.app.domain.usecase.support.GetConsultationDraftUseCase
import com.barns.app.domain.usecase.support.SaveConsultationDraftUseCase
import com.barns.app.presentation.myitems.ProductItemPresentation
import java.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConsultationDraftViewModel(
    private val getConsultationDraftUseCase: GetConsultationDraftUseCase,
    private val saveConsultationDraftUseCase: SaveConsultationDraftUseCase,
    // When the draft is started from a registered greenery item detail, this
    // holds the item context so the note can be prefilled. Optional so the
    // general Support draft flow keeps working unchanged.
    private val item: ProductItem? = null,
) : ViewModel() {
    /** Human-readable name of the registered greenery this note is about. */
    val itemContextName: String? = item?.name

    // Selectable options for the local-only draft form.
    val categories: List<ConsultationCategory> = listOf(
        ConsultationCategory.MAINTENANCE,
        ConsultationCategory.CARE,
        ConsultationCategory.REPLACEMENT,
        ConsultationCategory.OTHER,
    )
    val urgencies: List<ConsultationUrgency> = listOf(
        ConsultationUrgency.LOW,
        ConsultationUrgency.NORMAL,
        ConsultationUrgency.HIGH,
    )

    private var existing: ConsultationDraft? = null

    private val _state = MutableStateFlow(ConsultationDraftUiState())
    val state: StateFlow<ConsultationDraftUiState> = _state.asStateFlow()

    fun onTopicChange(value: String) {
        _state.value = _state.value.copy(topic = value)
    }

    fun onBodyChange(value: String) {
        _state.value = _state.value.copy(body = value)
    }

    fun onCategoryChange(value: ConsultationCategory) {
        _state.value = _state.value.copy(category = value)
    }

    fun onUrgencyChange(value: ConsultationUrgency) {
        _state.value = _state.value.copy(urgency = value)
    }

    fun load() {
        // Item-specific note: prefill from the registered greenery context.
        // Local-only; nothing is submitted anywhere.
        item?.let { current ->
            val state = _state.value
            _state.value = state.copy(
                topic = if (state.topic.isBlank()) "Consultation: ${current.name}" else state.topic,
                body = if (state.body.isBlank()) contextSummary(current) else state.body,
            )
            return
        }

        viewModelScope.launch {
            val draft = runCatching { getConsultationDraftUseCase.execute() }.getOrNull() ?: return@launch
            existing = draft
            _state.value = _state.value.copy(
                topic = draft.topic,
                body = draft.body,
                category = draft.category,
                urgency = draft.urgency,
            )
        }
    }

    /**
     * Builds a local-only context summary from existing item fields so the
     * user can add their concern below it before contacting support.
     */
    private fun contextSummary(item: ProductItem): String {
        val display = ProductItemPresentation.from(item)
        return buildString {
            appendLine("Item: ${display.name}")
            appendLine("Type: ${display.typeLabel}")
            appendLine("Category: ${display.categoryLabel}")
            appendLine("Location: ${display.locationLabel}")
            appendLine("Status: ${display.statusLabel}")
            appendLine()
            append("Concern: ")
        }
    }

    /** Saves the draft locally only. It is never sent to a server. */
    fun save() {
        val current = _state.value
        if (!current.canSave) return
        _state.value = current.copy(isSaving = true)
        viewModelScope.launch {
            runCatching {
                saveConsultationDraftUseCase.execute(
                    existing = existing,
                    productItemId = item?.id,
                    topic = current.topic.trim(),
                    category = current.category,
                    urgency = current.urgency,
                    body = current.body.trim(),
                )
            }
                .onSuccess { saved ->
                    existing = saved
                    _state.value = _state.value.copy(isSaving = false, savedAt = saved.updatedAt)
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        errorMessage = "Unable to save the draft. Please try again.",
                    )
                }
        }
    }
}

data class ConsultationDraftUiState(
    val topic: String = "",
    val body: String = "",
    val category: ConsultationCategory = ConsultationCategory.MAINTENANCE,
    val urgency: ConsultationUrgency = ConsultationUrgency.NORMAL,
    val isSaving: Boolean = false,
    val savedAt: Instant? = null,
    val errorMessage: String? = null,
) {
    val canSave: Boolean
        get() = topic.isNotBlank() && !isSaving
}
