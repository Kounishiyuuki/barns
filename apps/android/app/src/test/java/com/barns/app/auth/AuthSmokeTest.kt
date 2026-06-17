package com.barns.app.auth

import com.barns.app.data.repository.MockAuthRepository
import com.barns.app.domain.usecase.auth.GetCurrentUserUseCase
import com.barns.app.domain.usecase.auth.LoginAsGuestUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Mock auth smoke tests: no current user until guest login, then the mock
 * guest user is exposed. No real credentials, no network.
 */
class AuthSmokeTest {

    @Test
    fun noCurrentUserBeforeLogin() = runTest {
        val repository = MockAuthRepository()
        val getCurrentUser = GetCurrentUserUseCase(repository)

        assertNull(getCurrentUser.execute())
    }

    @Test
    fun guestLoginExposesMockUser() = runTest {
        val repository = MockAuthRepository()
        val loginAsGuest = LoginAsGuestUseCase(repository)
        val getCurrentUser = GetCurrentUserUseCase(repository)

        val signedIn = loginAsGuest.execute()
        val current = getCurrentUser.execute()

        assertNotNull(current)
        assertEquals(signedIn.id, current?.id)
    }
}
