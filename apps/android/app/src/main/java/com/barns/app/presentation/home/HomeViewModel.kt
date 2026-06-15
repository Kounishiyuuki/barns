package com.barns.app.presentation.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Home screen view model. Source skeleton: exposes a placeholder UI state.
 * Use cases (home summary, current user) are wired as features land,
 * mirroring the iOS HomeViewModel.
 */
class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState.placeholder)
    val state: StateFlow<HomeUiState> = _state.asStateFlow()
}

data class HomeUiState(
    val title: String,
    val message: String,
) {
    companion object {
        val placeholder = HomeUiState(
            title = "After-sales care starts here",
            message = "Your registered greenery, care reminders, and phone consultation guidance will appear here.",
        )
    }
}
