package com.barns.app.presentation.patterns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barns.app.domain.model.WallGreenPattern
import com.barns.app.domain.usecase.patterns.GetPatternsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PatternListViewModel(
    private val getPatternsUseCase: GetPatternsUseCase,
) : ViewModel() {
    sealed interface State {
        object Loading : State
        data class Loaded(val patterns: List<WallGreenPattern>) : State
        data class Failed(val message: String) : State
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    fun load() {
        _state.value = State.Loading
        viewModelScope.launch {
            runCatching { getPatternsUseCase.execute() }
                .onSuccess { patterns -> _state.value = State.Loaded(patterns) }
                .onFailure { _state.value = State.Failed("Unable to load patterns. Please try again.") }
        }
    }
}
