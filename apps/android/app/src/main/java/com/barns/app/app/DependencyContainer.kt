package com.barns.app.app

import com.barns.app.data.repository.MockAuthRepository
import com.barns.app.data.repository.MockHomeRepository
import com.barns.app.domain.repository.AuthRepository
import com.barns.app.domain.repository.HomeRepository
import com.barns.app.domain.usecase.auth.GetCurrentUserUseCase
import com.barns.app.domain.usecase.auth.LoginAsGuestUseCase
import com.barns.app.domain.usecase.home.GetHomeSummaryUseCase
import com.barns.app.presentation.auth.AuthViewModel
import com.barns.app.presentation.home.HomeViewModel

/**
 * Minimal manual dependency container for the source skeleton. Mirrors the
 * iOS DependencyContainer. Repository wiring and use cases are added as
 * features are implemented; a DI framework may later live in the di package.
 */
class DependencyContainer(
    private val authRepository: AuthRepository = MockAuthRepository(),
    private val homeRepository: HomeRepository = MockHomeRepository(),
) {
    fun makeAuthViewModel(): AuthViewModel =
        AuthViewModel(
            loginAsGuestUseCase = LoginAsGuestUseCase(authRepository),
            getCurrentUserUseCase = GetCurrentUserUseCase(authRepository),
        )

    fun makeHomeViewModel(): HomeViewModel =
        HomeViewModel(
            getHomeSummaryUseCase = GetHomeSummaryUseCase(homeRepository),
            getCurrentUserUseCase = GetCurrentUserUseCase(authRepository),
        )
}
