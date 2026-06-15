package com.barns.app.domain.usecase.auth

import com.barns.app.domain.model.User
import com.barns.app.domain.repository.AuthRepository

/**
 * Signs in with the mock guest account.
 */
class LoginAsGuestUseCase(
    private val authRepository: AuthRepository,
) {
    suspend fun execute(): User = authRepository.signInWithMockAccount()
}
