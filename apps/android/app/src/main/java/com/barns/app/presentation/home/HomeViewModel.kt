package com.barns.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barns.app.domain.model.HomeSummary
import com.barns.app.domain.model.User
import com.barns.app.domain.usecase.auth.GetCurrentUserUseCase
import com.barns.app.domain.usecase.home.GetHomeSummaryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Home screen view model. Mirrors the iOS HomeViewModel shape.
 */
class HomeViewModel(
    private val getHomeSummaryUseCase: GetHomeSummaryUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : ViewModel() {
    sealed interface State {
        object Loading : State
        data class Loaded(val content: HomeContent) : State
        data class Failed(val message: String) : State
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    fun load() {
        _state.value = State.Loading
        viewModelScope.launch {
            runCatching {
                val summary = getHomeSummaryUseCase.execute()
                val user = getCurrentUserUseCase.execute()
                HomeContent(greeting = greetingFor(user), summary = summary)
            }
                .onSuccess { content -> _state.value = State.Loaded(content) }
                .onFailure { _state.value = State.Failed("Unable to load home. Please try again.") }
        }
    }

    private fun greetingFor(user: User?): String =
        if (user == null) {
            "Welcome to barns"
        } else {
            "Welcome back, ${user.displayName}"
        }
}

data class HomeContent(
    val greeting: String,
    val summary: HomeSummary,
)
