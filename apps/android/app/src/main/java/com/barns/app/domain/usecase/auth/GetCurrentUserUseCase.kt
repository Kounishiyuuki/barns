package com.barns.app.domain.usecase.auth

import com.barns.app.domain.model.User
import com.barns.app.domain.repository.AuthRepository

/**
 * Returns the currently signed-in user, if any.
 */
class GetCurrentUserUseCase(
    private val authRepository: AuthRepository,
) {
    suspend fun execute(): User? = authRepository.currentUser()
}
