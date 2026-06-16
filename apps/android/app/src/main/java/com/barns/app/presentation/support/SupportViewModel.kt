package com.barns.app.presentation.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barns.app.domain.model.CompanyInfo
import com.barns.app.domain.usecase.support.GetSupportInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SupportViewModel(
    private val getSupportInfoUseCase: GetSupportInfoUseCase,
) : ViewModel() {
    sealed interface State {
        object Loading : State
        data class Loaded(val info: CompanyInfo) : State
        data class Failed(val message: String) : State
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    fun load() {
        _state.value = State.Loading
        viewModelScope.launch {
            runCatching { getSupportInfoUseCase.execute() }
                .onSuccess { info -> _state.value = State.Loaded(info) }
                .onFailure { _state.value = State.Failed("Unable to load support info. Please try again.") }
        }
    }
}
