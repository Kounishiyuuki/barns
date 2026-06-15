package com.barns.app.data.repository

import com.barns.app.domain.model.User
import com.barns.app.domain.repository.AuthRepository

/**
 * In-memory mock auth. No persistence, no network, no real credentials.
 */
class MockAuthRepository : AuthRepository {
    private val lock = Any()
    private var signedInUser: User? = null
    private val mockUser = User(id = "mock-user-001", displayName = "Guest")

    override suspend fun currentUser(): User? =
        synchronized(lock) { signedInUser }

    override suspend fun signInWithMockAccount(): User =
        synchronized(lock) {
            signedInUser = mockUser
            mockUser
        }

    override suspend fun signOut() {
        synchronized(lock) {
            signedInUser = null
        }
    }
}
