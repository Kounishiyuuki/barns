package com.barns.app.presentation.care

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barns.app.domain.model.CareLog
import com.barns.app.domain.model.CareTask
import com.barns.app.domain.model.CareTaskStatus
import com.barns.app.domain.usecase.care.GetCareLogsUseCase
import com.barns.app.domain.usecase.care.GetCareTasksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CareViewModel(
    private val getCareTasksUseCase: GetCareTasksUseCase,
    private val getCareLogsUseCase: GetCareLogsUseCase,
) : ViewModel() {
    sealed interface State {
        object Loading : State
        data class Loaded(val content: CareContent) : State
        data class Failed(val message: String) : State
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    fun load() {
        _state.value = State.Loading
        viewModelScope.launch {
            runCatching {
                val tasks = getCareTasksUseCase.execute()
                val logs = getCareLogsUseCase.execute()
                CareContent(
                    upcoming = tasks.filter { it.status == CareTaskStatus.PENDING },
                    recentLogs = logs,
                )
            }
                .onSuccess { content -> _state.value = State.Loaded(content) }
                .onFailure { _state.value = State.Failed("Unable to load care. Please try again.") }
        }
    }
}

data class CareContent(
    val upcoming: List<CareTask>,
    val recentLogs: List<CareLog>,
)
