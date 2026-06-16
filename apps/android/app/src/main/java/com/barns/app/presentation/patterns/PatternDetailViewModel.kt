package com.barns.app.presentation.patterns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barns.app.domain.model.WallGreenPattern
import com.barns.app.domain.usecase.patterns.GetPatternDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PatternDetailViewModel(
    private val patternId: String,
    private val getPatternDetailUseCase: GetPatternDetailUseCase,
) : ViewModel() {
    sealed interface State {
        object Loading : State
        data class Loaded(val pattern: WallGreenPattern) : State
        object NotFound : State
        data class Failed(val message: String) : State
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    fun load() {
        _state.value = State.Loading
        viewModelScope.launch {
            runCatching { getPatternDetailUseCase.execute(patternId) }
                .onSuccess { pattern ->
                    _state.value = if (pattern != null) State.Loaded(pattern) else State.NotFound
                }
                .onFailure { _state.value = State.Failed("Unable to load this pattern. Please try again.") }
        }
    }
}
