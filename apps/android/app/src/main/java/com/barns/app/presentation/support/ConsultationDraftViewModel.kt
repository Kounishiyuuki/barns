package com.barns.app.presentation.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barns.app.domain.model.ConsultationCategory
import com.barns.app.domain.model.ConsultationDraft
import com.barns.app.domain.model.ConsultationUrgency
import com.barns.app.domain.usecase.support.GetConsultationDraftUseCase
import com.barns.app.domain.usecase.support.SaveConsultationDraftUseCase
import java.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConsultationDraftViewModel(
    private val getConsultationDraftUseCase: GetConsultationDraftUseCase,
    private val saveConsultationDraftUseCase: SaveConsultationDraftUseCase,
) : ViewModel() {
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

    /** Saves the draft locally only. It is never sent to a server. */
    fun save() {
        val current = _state.value
        if (!current.canSave) return
        _state.value = current.copy(isSaving = true)
        viewModelScope.launch {
            runCatching {
                saveConsultationDraftUseCase.execute(
                    existing = existing,
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
