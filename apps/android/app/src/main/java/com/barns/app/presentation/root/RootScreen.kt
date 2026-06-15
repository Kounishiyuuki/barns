package com.barns.app.presentation.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.barns.app.app.DependencyContainer
import com.barns.app.presentation.auth.AuthScreen
import com.barns.app.presentation.auth.AuthViewModel
import com.barns.app.presentation.home.HomeScreen

/**
 * Composition root. Gates the source skeleton behind a mock guest sign-in.
 */
@Composable
fun RootScreen(
    authViewModel: AuthViewModel,
    container: DependencyContainer,
) {
    val authState by authViewModel.state.collectAsState()

    LaunchedEffect(authViewModel) {
        authViewModel.loadCurrentUser()
    }

    if (authState is AuthViewModel.State.Authenticated) {
        val homeViewModel = remember(container) { container.makeHomeViewModel() }
        HomeScreen(viewModel = homeViewModel)
    } else {
        AuthScreen(viewModel = authViewModel)
    }
}
