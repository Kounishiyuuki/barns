package com.barns.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barns.app.domain.model.User
import com.barns.app.domain.usecase.auth.GetCurrentUserUseCase
import com.barns.app.domain.usecase.auth.LoginAsGuestUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginAsGuestUseCase: LoginAsGuestUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : ViewModel() {
    sealed interface State {
        object Idle : State
        object Loading : State
        data class Authenticated(val user: User) : State
        data class Failed(val message: String) : State
    }

    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state.asStateFlow()

    val isAuthenticated: Boolean
        get() = _state.value is State.Authenticated

    fun loginAsGuest() {
        _state.value = State.Loading
        viewModelScope.launch {
            runCatching { loginAsGuestUseCase.execute() }
                .onSuccess { user -> _state.value = State.Authenticated(user) }
                .onFailure { _state.value = State.Failed("Sign-in failed. Please try again.") }
        }
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            runCatching { getCurrentUserUseCase.execute() }
                .onSuccess { user -> if (user != null) _state.value = State.Authenticated(user) }
                .onFailure { _state.value = State.Failed("Sign-in failed. Please try again.") }
        }
    }
}
