package com.barns.app.presentation.care

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barns.app.domain.model.CareTask
import com.barns.app.domain.model.CareTaskStatus
import com.barns.app.domain.usecase.care.CompleteCareTaskUseCase
import com.barns.app.domain.usecase.care.GetCareTaskDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CareTaskDetailViewModel(
    private val taskId: String,
    private val getCareTaskDetailUseCase: GetCareTaskDetailUseCase,
    private val completeCareTaskUseCase: CompleteCareTaskUseCase,
) : ViewModel() {
    sealed interface State {
        object Loading : State
        data class Loaded(val task: CareTask) : State
        object NotFound : State
        data class Failed(val message: String) : State
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _isCompleting = MutableStateFlow(false)
    val isCompleting: StateFlow<Boolean> = _isCompleting.asStateFlow()

    fun load() {
        _state.value = State.Loading
        viewModelScope.launch {
            runCatching { getCareTaskDetailUseCase.execute(taskId) }
                .onSuccess { task ->
                    _state.value = if (task != null) State.Loaded(task) else State.NotFound
                }
                .onFailure { _state.value = State.Failed("Unable to load this task. Please try again.") }
        }
    }

    fun complete() {
        val current = _state.value
        if (current !is State.Loaded) return
        if (current.task.status == CareTaskStatus.COMPLETED || _isCompleting.value) return
        _isCompleting.value = true
        viewModelScope.launch {
            runCatching {
                completeCareTaskUseCase.execute(current.task.id)
                getCareTaskDetailUseCase.execute(taskId)
            }
                .onSuccess { task ->
                    _state.value = if (task != null) State.Loaded(task) else State.NotFound
                }
                .onFailure { _state.value = State.Failed("Unable to complete this task. Please try again.") }
            _isCompleting.value = false
        }
    }
}
